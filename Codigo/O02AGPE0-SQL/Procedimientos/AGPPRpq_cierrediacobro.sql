SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.pq_cierrediacobro IS
/******************************************************************************
   NAME:       	pk_cierrediacobro
   PURPOSE:    	Se recogerán aquellas pólizas en estado de .definitivas., y
		aquellas que hayan sido financiadas y corresponda la fecha de
		segundo pago, y aquellas que consten como fraccionadas (según línea).
		Se generará un recibo (CSB19) a adeudar en la cuenta del cliente
		y a abonar a la cuenta remesadora de la oficina correspondiente.
 		El importe a cobrar será: la totalidad en caso de no financiada
		ni fraccionada, o la parte que corresponda en caso de financiadas
		o fraccionadas. En la medida de lo posible, se reutilizará el
		proceso de la aplicación actual para evitar discrepancias.
		Se retrasará hasta el máximo posible siempre y cuando se asigne
		fecha del día (provisionalmente hasta las 20:00 y no como actualmente
		a las 17:00).


   REVISIONS:
   Ver        Date        Author           Description
   ----------  ----------  ---------------  ------------------------------------
   1.0        08/09/2010  Sergio Castro    1. Created this package.
   2.0        30/06/2021  T-Systems        GDLD-63702 

******************************************************************************/

-------------------------------------------------------------------------------
-- PROCEDIMIENTOS PUBLICOS
-------------------------------------------------------------------------------

-- Carga las polizas definitivas
FUNCTION load_polizas_definitivas(p_fecha IN VARCHAR2) RETURN VARCHAR2;

FUNCTION getNumPlzAPagar(fecha_planif IN DATE) RETURN NUMBER;

FUNCTION getNumPolizasDomiciliadas(fecha_planif IN DATE) RETURN NUMBER;

END pq_cierrediacobro;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.pq_cierrediacobro AS



---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- FUNCTION load_polizas_definitivas
--
-- Fichero de Pólizas
--
-- Recoge las pólizas que han entrado en el día y contiene los siguientes campos:
--
-- 1.- Código de entidad                                 PIC X(04)
-- 2.- Código de oficina                                 PIC X(04)
-- 3.- N.I.F. del asegurado                              PIC X(09)
-- 5.- Plan de la póliza                                 PIC X(04)
-- 6.- Línea de la póliza                                PIC X(03)
-- 8.- Número de póliza.                                 PIC X(07)
-- X.- Módulo                                            PIC X(05)
-- X.- Tipo de poliza: principal/complementaria          PIC X(01)
-- 11.- Importe total de la póliza                       PIC 9(11)
-- 12.- Porcentaje de primer vencimiento                 PIC 9(03)V99
-- 13.- Fecha de primer vencimiento                      PIC 9(08)
-- 14.- Importe de primer vencimiento                    PIC 9(11)
-- 15.- Porcentaje de segundo vto                        PIC 9(03)V99
-- 16.- Fecha de segundo vto.                            PIC 9(08)
-- 17.- Importe del segundo vto                          PIC 9(11)
-- 19.- Cuenta.  Dividido en los campos:
--                                              ENTCCC   PIC X(4)
--                                              OFICCC   PIC X(4)
--                                              DIGCCC   PIC X(02)
--                                              MODCCC   PIC X(02)
--                                              CTACCC   PIC X(06)
--                                              DIGCTA   PIC X(02)
-- 20.- Nombre del asegurado                    NOMASEG  PIC X(20)
-- 21.- Apellido del asegurado                  APEASEG  PIC X(80)
-- 22.- Colectivo.  Dividido en número y dígito de colectivo
--                                              NUMCOLEC PIC X(7)
--                                              DIGCOLEC PIC 9.
-- 23.- Fecha de inicio de póliza (día de recepción de la póliza))
--                                            RN-FECINIC PIC 9(08)
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
FUNCTION load_polizas_definitivas(p_fecha IN VARCHAR2)
RETURN VARCHAR2 IS

lc   VARCHAR2(50) := 'pq_cierrediacobro.load_polizas_definitivas';

TYPE TpCursor 		IS REF CURSOR;

f_fichero    		  UTL_FILE.FILE_TYPE;
f_fichero_crm    	UTL_FILE.FILE_TYPE; -- Fichero para CRM
f_fichero_cra    	UTL_FILE.FILE_TYPE; -- Fichero para CR Almendralejo
l_dir         		TB_CONFIG_AGP.AGP_valor%TYPE; -- Valor del parametro de configuracion
l_line        		VARCHAR2(1000);
l_error       		VARCHAR2(1000);
l_nombre      		VARCHAR2(1000);
l_dir_name    		VARCHAR2(1000);
l_query	      		VARCHAR2(32000);
l_fecha_planif	    VARCHAR2(100); 
l_tp_cursor   		TpCursor;
l_idpoliza		TB_POLIZAS.IDPOLIZA%TYPE;
-- Código de estado de pago 'Pendiente confirmación'
ESTADO_AGP_PEND_CONF CONSTANT NUMBER(1) := 2;
ESTADO_PAGO_PEND_CONF CONSTANT NUMBER(1) := -2;

ESTADO_AGP_PAGO_PAGADA CONSTANT NUMBER(1) := 1;
ESTADO_PAGO_CORRECTO CONSTANT NUMBER(1) := 0;
-- **** Composicion de la linea ****
l_CODIGO_ENTIDAD        TB_COLECTIVOS.CODENTIDAD%TYPE;
l_CODIGO_OFICINA        varchar2(4);
l_NIF_ASEGURADO         TB_ASEGURADOS.NIFCIF%TYPE;
l_PLAN_POLIZA		        TB_LINEAS.CODPLAN%TYPE;
l_LINEA_POLIZA		      TB_LINEAS.CODLINEA%TYPE;
l_NUMERO_POLIZA         VARCHAR2(8);
l_MODULO		            VARCHAR2(5);
l_TIPOREF		            VARCHAR2(1);
l_IMPORTE_TOTAL		      VARCHAR2(11);
l_PCT_PRIMER_VTO 	      VARCHAR2(5);
l_FEC_PRIMER_VTO        VARCHAR2(8);
l_IMP_PRIMER_VTO	      VARCHAR2(11);
l_PCT_SEGUNDO_VTO 	    VARCHAR2(5);
l_FEC_SEGUNDO_VTO	      VARCHAR2(8);
l_IMP_SEGUNDO_VTO	      VARCHAR2(11);
l_ENTIDAD_CCC  		      VARCHAR2(4);
l_OFICINA_CCC  		      VARCHAR2(4);
l_DIGITO_CCC   		      VARCHAR2(2);
l_MODALIDAD_CCC		      VARCHAR2(2);
l_NUMERO_CTA 		        VARCHAR2(6);
l_DIGITO_CTA   		      VARCHAR2(2);
l_NOMBRE_ASEG		        VARCHAR2(20);
l_APELLIDO_ASEG		      VARCHAR2(80);
l_RAZON_SOCIAL          VARCHAR2(100);
l_NUMERO_COLECTIVO	    VARCHAR2(7);
l_DIGITO_COLECTIVO	    VARCHAR2(1);
l_RECEPCION_POLIZA	    VARCHAR2(8);
l_TIPO_IDENTIFICACION   VARCHAR2(3);
l_count_ent_crm         NUMBER;
l_IBAN                  VARCHAR2(34);
l_TIPO_PAGO		          TB_PAGOS_POLIZA.TIPO_PAGO%TYPE;
l_FECHA   		          TB_PAGOS_POLIZA.FECHA%TYPE;

--l_count_ent_cra         NUMBER;

-- *********** Contadores ***********************************
l_num_polizas NUMBER := 0;         -- Número de pólizas enviadas a pagos
l_num_polizas_crm NUMBER := 0;         -- Número de pólizas DE CRM enviadas a pagos
l_num_polizas_cra NUMBER := 0;         -- Número de pólizas DE CR Almendralejo enviadas a pagos
l_num_polizas_pagoManual NUMBER := 0;  -- Número de pólizas con pago manual
str_num_polizas CONSTANT VARCHAR2(30) := 'PAGOS_POLIZAS_ENVIADAS';

l_num_polizas_a_pagos NUMBER := 0;     -- Número de pólizas no pagadas que están en algún estado en el cual se pueden enviar a pagos
str_num_polizas_a_pagos CONSTANT VARCHAR2(30) := 'PAGOS_POLIZAS_A_ENVIAR';

l_num_polizas_domiciliadas NUMBER := 0; -- Número de pólizas domiciliadas con Agroseguro.
str_num_polizas_domiciliadas CONSTANT VARCHAR2(30) := 'PAGOS_POLIZAS_DOM';

-- *********** Excepciones **********************************
no_polizas_found	EXCEPTION;
no_pagos_found		EXCEPTION;
fecha_planif_vacia    EXCEPTION;

fecha_planif	DATE;

BEGIN
   PQ_Utl.LOG(lc,'## INI ##',1);

   pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);

   IF p_fecha = '' THEN
   	l_error := 'Fecha de planificacion vacia';
   		       PQ_Utl.LOG(lc, l_error, 2);
    		     RAISE fecha_planif_vacia;
   END IF;

   -- Convertimos a DATE la fecha de planificacion recibida por parametro desde el sh
   fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');


   -- PQ_Utl.getcfg esta en el paquete de utilidades
   -- accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).

   -- PARA TEST Y PROD:
   l_dir  := PQ_Utl.getcfg('DIR_EXPORT_ENVIOS');

	pq_utl.log(lc, 'Directorio exportacion envios:' || l_dir);
	
   -- Se guarda el path fisico del directorio
   SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir;

   pq_utl.log(lc, 'Path fisico del directorio:' || l_dir_name);
   
   l_nombre := 'POLIZAS_SC';

   -- ********************************************************
   -- Abrimos el fichero de salida con extension .txt
   -- ********************************************************
   f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre || '.TXT',
	 			open_mode    => 'w',
	 			max_linesize => PQ_Typ.MAX_LINEFILESIZEWRITE);

   f_fichero_crm := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre || '_CRM.TXT',
	 			open_mode    => 'w',
	 			max_linesize => PQ_Typ.MAX_LINEFILESIZEWRITE);

   f_fichero_cra := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre || '_CRA.TXT',
	 			open_mode    => 'w',
	 			max_linesize => PQ_Typ.MAX_LINEFILESIZEWRITE);

   BEGIN

   -- Obtiene el número de pólizas que se van a enviar a pagos
   l_num_polizas_a_pagos := getNumPlzAPagar(fecha_planif);
   PQ_Utl.LOG(lc, 'Número de pólizas que se enviarán a pagos = ' ||l_num_polizas_a_pagos, 2);
   -- Actualiza la tabla de configuración con este número
   PQ_Utl.setcfg (str_num_polizas_a_pagos || p_fecha, l_num_polizas_a_pagos);

   -- Obtiene el número de pólizas domicialiadas con Agroseguro
   l_num_polizas_domiciliadas := getNumPolizasDomiciliadas(fecha_planif);
   PQ_Utl.LOG(lc, 'Número de pólizas domiciliadas con Agroseguro = ' ||l_num_polizas_domiciliadas, 2);
   -- Actualiza la tabla de configuración con este número
   PQ_Utl.setcfg (str_num_polizas_domiciliadas || p_fecha, l_num_polizas_domiciliadas);

   PQ_Utl.LOG(lc, 'Recuperando las polizas definitivas ', 2);
   l_error := 'Recuperando en la tabla de polizas las polizas definitivas ';


	  -- Ver TB_ESTADOS_POLIZA. Estado 8 es Definitiva
    -- MPM 04/10/13 -
    -- Se obtiene el módulo con 5 posiciones y el tipo de referencia para indicar si es principal o complementaria
    -- ¿¿¿¿¿ Para pagos de crm también hay que enviar 5 posiciones en el módulo ?????
    --

   l_fecha_planif := TO_CHAR(fecha_planif, 'DD/MM/YYYY');

   	 	  l_query := '
		SELECT
		      A.IDPOLIZA,
          F.CODENTIDAD,
		      nvl(lpad(A.OFICINA, 4, 0), ''9999''),
          LPAD(D.NIFCIF, 9, ''0''),
		      LPAD(trim(TO_CHAR(E.CODPLAN, ''9999'')), 4, '' ''),
		      LPAD(trim(TO_CHAR(E.CODLINEA, ''999'')), 3, '' ''),
		      RPAD(A.REFERENCIA || A.DC, 8, '' ''),
		      RPAD(A.CODMODULO, 5, '' ''),
          RPAD(A.TIPOREF, 1, '' ''),
		      LPAD(TRIM(TO_CHAR(NVL (C.IMPORTE, 0), ''999999999V99'')), 11, ''0''),
		      lpad(trim(TO_CHAR(C.PCTPRIMERPAGO, ''900V00'')), 5, ''0''),
		      nvl(TO_CHAR(C.FECHA, ''YYYYMMDD''), ''' || p_fecha ||'''),
		      LPAD(TRIM(TO_CHAR (((C.IMPORTE * C.PCTPRIMERPAGO) /100), ''999999999V99'')), 11, ''0''),
          lpad(trim(nvl(TO_CHAR(C.PCTSEGUNDOPAGO, ''900V00''), ''00000'')), 5, ''0''),
		      nvl(TO_CHAR(C.FECHASEGUNDOPAGO, ''YYYYMMDD''), ''00000000''),
		      nvl(LPAD(TRIM(TO_CHAR(((C.IMPORTE * C.PCTSEGUNDOPAGO)/100), ''999999999V99'')), 11, ''0''), ''00000000000''),
		      C.IBAN,
          SUBSTR(C.CCCBANCO,0,4),
		      SUBSTR(C.CCCBANCO,5,4),
		      SUBSTR(C.CCCBANCO,9,2),
		      SUBSTR(C.CCCBANCO,11,2),
		      SUBSTR(C.CCCBANCO,13,6),
		      SUBSTR(C.CCCBANCO,19,2),
		      RPAD(D.NOMBRE, 20, '' ''),
		      RPAD(D.APELLIDO1 || '' '' || D.APELLIDO2, 80, '' ''),
          RPAD(D.RAZONSOCIAL, 100, '' ''),
		      RPAD(F.IDCOLECTIVO, 7, '' ''),
	        F.DC, D.TIPOIDENTIFICACION,
          nvl(TO_CHAR(A.fechaenvio, ''YYYYMMDD''), '''|| p_fecha || ''' ),
          C.TIPO_PAGO, C.FECHA
		FROM  TB_POLIZAS A,
		      TB_PAGOS_POLIZA C,
		      TB_ASEGURADOS D,
		      TB_LINEAS E,
		      TB_COLECTIVOS F,
			  TB_USUARIOS U
		WHERE A.IDASEGURADO = D.ID
		AND   A.IDPOLIZA = C.IDPOLIZA
		AND   A.LINEASEGUROID = E.LINEASEGUROID
		AND   U.CODUSUARIO = A.CODUSUARIO
		AND   A.IDESTADO in (3, 5, 7, 8)
		AND   C.TIPO_PAGO <> 2
		AND   A.PAGADA = 0
		AND   A.IDCOLECTIVO = F.ID
		AND (select ph.FECHA
           from o02agpe0.VW_POLIZAS_HIST_ESTADOS_DESC ph
           where ph.IDPOLIZA = A.IDPOLIZA and ph.ESTADO = 8 and rownum=1)
           <= to_date (''' || l_fecha_planif || ' 16:40:00'', ''DD/MM/YYYY HH24:MI:SS'')';


    dbms_output.put_line(l_query);
	  PQ_Utl.LOG(lc, l_query, 2);

    OPEN l_tp_cursor FOR l_query;
    loop
	  FETCH l_tp_cursor INTO l_idpoliza,
				 l_CODIGO_ENTIDAD,  l_CODIGO_OFICINA,       l_NIF_ASEGURADO,    l_PLAN_POLIZA,        l_LINEA_POLIZA,     l_NUMERO_POLIZA,
				 l_MODULO,          l_TIPOREF,				      l_IMPORTE_TOTAL,		l_PCT_PRIMER_VTO,		  l_FEC_PRIMER_VTO,		l_IMP_PRIMER_VTO,
				 l_PCT_SEGUNDO_VTO,	l_FEC_SEGUNDO_VTO,			l_IMP_SEGUNDO_VTO,  l_IBAN,				        l_ENTIDAD_CCC,			l_OFICINA_CCC,
				 l_DIGITO_CCC,			l_MODALIDAD_CCC,				l_NUMERO_CTA,				l_DIGITO_CTA,			    l_NOMBRE_ASEG,			l_APELLIDO_ASEG,
         l_RAZON_SOCIAL,		l_NUMERO_COLECTIVO,			l_DIGITO_COLECTIVO, l_TIPO_IDENTIFICACION,l_RECEPCION_POLIZA, l_TIPO_PAGO,
         l_FECHA;

    EXIT WHEN l_tp_cursor%NOTFOUND;
      IF(l_TIPO_PAGO = 1) THEN -- PAGO MANUAL
         PQ_Utl.LOG(lc, 'Pago Manual para la poliza: '|| l_NUMERO_POLIZA || ' con CODMODULO: ' ||l_MODULO || ' e idpoliza: ' ||l_idpoliza, 2);


          UPDATE TB_POLIZAS P SET P.PAGADA = ESTADO_AGP_PAGO_PAGADA, FECHA_PAGO = fecha_planif WHERE P.IDPOLIZA = l_idpoliza;
          PQ_Utl.LOG(lc, 'Despues de updatear la poliza a pagada ', 2);


          -- Introduce el registro correspondiente en la tabla de histórico de pagos
          INSERT INTO TB_PAGO_HISTORICO_PLZ (ID, IDPOLIZA, IDAGP, IDPAGO, CODUSUARIO, FECHA)
          VALUES (SQ_PAGO_HISTORICO_PLZ.NEXTVAL, l_idpoliza, ESTADO_AGP_PAGO_PAGADA, ESTADO_PAGO_CORRECTO, NULL, fecha_planif);
          PQ_Utl.LOG(lc, 'Despues de insertar el registro en el historico: '||l_idpoliza, 2);

          l_num_polizas_pagoManual  := l_num_polizas_pagoManual + 1;
      ELSE
           IF (l_PCT_SEGUNDO_VTO = 0) THEN
              l_FEC_SEGUNDO_VTO := '00000000';
           END IF;
           -- AMG 11122012 se comprueba que la suma del 1er importe + el 2º sea igual que el importe total.
           IF (l_IMP_PRIMER_VTO + l_IMP_SEGUNDO_VTO != l_IMPORTE_TOTAL) THEN
              l_IMP_PRIMER_VTO := LPAD(TRIM(TO_CHAR(l_IMPORTE_TOTAL - l_IMP_SEGUNDO_VTO, '99999999999')), 11, '0');
           END IF;

          -- Comprueba si la entidad pertenece al grupo CRM
          select count(*) into l_count_ent_crm
            from tb_entidades e, tb_grupo_entidades g
             where e.idgrupo = g.id
               and g.descripcion like '%CRM%'
               and e.codentidad = l_CODIGO_ENTIDAD;

          IF (l_count_ent_crm = 1 OR L_CODIGO_ENTIDAD = 3001) THEN -- CRM ó CRA
             IF (l_TIPO_IDENTIFICACION = 'CIF') THEN
                   l_line :=
                    l_CODIGO_ENTIDAD        || l_CODIGO_OFICINA       || l_NIF_ASEGURADO      || l_PLAN_POLIZA      ||
                    l_LINEA_POLIZA          || l_NUMERO_POLIZA        || l_MODULO             || l_TIPOREF          ||
                    l_IMPORTE_TOTAL         || l_PCT_PRIMER_VTO       || l_FEC_PRIMER_VTO     || l_IMP_PRIMER_VTO   ||
                    l_PCT_SEGUNDO_VTO       || l_FEC_SEGUNDO_VTO      || l_IMP_SEGUNDO_VTO    || l_ENTIDAD_CCC      ||
                    l_OFICINA_CCC           || l_DIGITO_CCC           || l_MODALIDAD_CCC      || l_NUMERO_CTA       ||
                    l_DIGITO_CTA            || l_RAZON_SOCIAL         || l_NUMERO_COLECTIVO   || l_DIGITO_COLECTIVO ||
                    l_RECEPCION_POLIZA;

               ELSE
                   l_line :=
                    l_CODIGO_ENTIDAD        || l_CODIGO_OFICINA       || l_NIF_ASEGURADO      || l_PLAN_POLIZA      ||
                    l_LINEA_POLIZA          || l_NUMERO_POLIZA        || l_MODULO             || l_TIPOREF          ||
                    l_IMPORTE_TOTAL         || l_PCT_PRIMER_VTO       || l_FEC_PRIMER_VTO     || l_IMP_PRIMER_VTO   ||
                    l_PCT_SEGUNDO_VTO       || l_FEC_SEGUNDO_VTO      || l_IMP_SEGUNDO_VTO    || l_ENTIDAD_CCC      ||
                    l_OFICINA_CCC           || l_DIGITO_CCC           || l_MODALIDAD_CCC      || l_NUMERO_CTA       ||
                    l_DIGITO_CTA            || l_NOMBRE_ASEG          || l_APELLIDO_ASEG      || l_NUMERO_COLECTIVO ||
                    l_DIGITO_COLECTIVO      || l_RECEPCION_POLIZA;
               END IF;
          ELSE
              IF (l_TIPO_IDENTIFICACION = 'CIF') THEN
                   l_line :=
                    l_CODIGO_ENTIDAD        || l_CODIGO_OFICINA       || l_NIF_ASEGURADO      || l_PLAN_POLIZA      ||
                    l_LINEA_POLIZA          || l_NUMERO_POLIZA        || l_MODULO             || l_TIPOREF          ||
                    l_IMPORTE_TOTAL         || l_PCT_PRIMER_VTO       || l_FEC_PRIMER_VTO     || l_IMP_PRIMER_VTO   ||
                    l_PCT_SEGUNDO_VTO       || l_FEC_SEGUNDO_VTO      || l_IMP_SEGUNDO_VTO    || RPAD(LTRIM(l_IBAN), 34,' ') ||
                    l_ENTIDAD_CCC           || l_OFICINA_CCC          || l_DIGITO_CCC         || l_MODALIDAD_CCC    ||
                    l_NUMERO_CTA            || l_DIGITO_CTA           || l_RAZON_SOCIAL       || l_NUMERO_COLECTIVO ||
                    l_DIGITO_COLECTIVO      || l_RECEPCION_POLIZA;
               ELSE
                   l_line :=
                    l_CODIGO_ENTIDAD        || l_CODIGO_OFICINA       || l_NIF_ASEGURADO      || l_PLAN_POLIZA      ||
                    l_LINEA_POLIZA          || l_NUMERO_POLIZA        || l_MODULO             || l_TIPOREF          ||
                    l_IMPORTE_TOTAL         || l_PCT_PRIMER_VTO       || l_FEC_PRIMER_VTO     || l_IMP_PRIMER_VTO   ||
                    l_PCT_SEGUNDO_VTO       || l_FEC_SEGUNDO_VTO      || l_IMP_SEGUNDO_VTO    || RPAD(LTRIM(l_IBAN), 34,' ') ||
                    l_ENTIDAD_CCC           || l_OFICINA_CCC          || l_DIGITO_CCC         || l_MODALIDAD_CCC    ||
                    l_NUMERO_CTA            || l_DIGITO_CTA           || l_NOMBRE_ASEG        || l_APELLIDO_ASEG    ||
                    l_NUMERO_COLECTIVO      || l_DIGITO_COLECTIVO     || l_RECEPCION_POLIZA;
               END IF;
          END IF;



          IF l_count_ent_crm = 1 THEN
              UTL_FILE.PUT_LINE (f_fichero_crm, l_line || chr(13));
              l_num_polizas_crm := l_num_polizas_crm + 1;
          ELSIF L_CODIGO_ENTIDAD = 3001 THEN
              -- Si la entidad es la de la CR Almendralejo, lo añado a otro fichero
              UTL_FILE.PUT_LINE (f_fichero_cra, l_line || chr(13));
              l_num_polizas_cra := l_num_polizas_cra + 1;
          ELSE
              UTL_FILE.PUT_LINE (f_fichero, l_line || chr(13));
              l_num_polizas := l_num_polizas + 1;
          END IF;

          -- MPM 04/10/13 -
          -- Se informa la fecha de envío a pago y se actualiza el estado del pago de la póliza
          -- a 'Pendiente confirmación'
          UPDATE TB_POLIZAS P SET P.PAGADA = ESTADO_AGP_PEND_CONF, FECHA_PAGO = fecha_planif WHERE P.IDPOLIZA = l_idpoliza;

          -- Introduce el registro correspondiente en la tabla de histórico de pagos
          INSERT INTO TB_PAGO_HISTORICO_PLZ (ID, IDPOLIZA, IDAGP, IDPAGO, CODUSUARIO, FECHA)
          VALUES (SQ_PAGO_HISTORICO_PLZ.NEXTVAL, l_idpoliza, ESTADO_AGP_PEND_CONF, ESTADO_PAGO_PEND_CONF, NULL, fecha_planif);

      END IF;
    --

	  END LOOP;

   	  EXCEPTION
   	     WHEN NO_DATA_FOUND THEN
   	  	     l_error := 'No se han encontrado polizas definitivas';
   		       PQ_Utl.LOG(lc, l_error, 2);
    		     RAISE no_polizas_found;
   END;

   -- Actualiza en la tabla de configuración el número de pólizas enviadas a pagos
   PQ_Utl.LOG(lc, 'Número de pólizas enviadas a pagos = ' || l_num_polizas || '   ', 2);
   PQ_Utl.setcfg (str_num_polizas || p_fecha, l_num_polizas);
   PQ_Utl.LOG(lc, 'Número de pólizas CRM enviadas a pagos = ' || l_num_polizas_crm || '   ', 2);
   -- ESC-23068: Se quita la fecha del contador PAGOS_POLIZAS_CRM dado que no se utiliza en el correo de resumen
   PQ_Utl.setcfg ('PAGOS_POLIZAS_CRM', l_num_polizas_crm);
   PQ_Utl.LOG(lc, 'Número de pólizas CR Almendralejo enviadas a pagos = ' || l_num_polizas_cra || '   ', 2);
   PQ_Utl.setcfg ('PAGOS_POLIZAS_CRA' || p_fecha, l_num_polizas_cra);

   PQ_Utl.LOG(lc, 'Número de pólizas manuales = ' || l_num_polizas_pagoManual || '   ', 2);
   PQ_Utl.setcfg ('PAGOS_POLIZAS_MANUALES' || p_fecha, l_num_polizas_pagoManual);

   pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || TO_CHAR(fecha_planif, 'DDMMYYY'));
   PQ_Utl.setcfg ('FECHA_PLANIFICACION', TO_CHAR(fecha_planif, 'DDMMYYY'));


   -- ********************************************************
   -- Cerramos el fichero.
   -- ********************************************************
   COMMIT;
   CLOSE l_tp_cursor;
   UTL_FILE.FCLOSE( f_fichero );
   UTL_FILE.FCLOSE( f_fichero_crm );
   UTL_FILE.FCLOSE( f_fichero_cra );

   -- Realizamos una copia del fichero generado para guardarlo a modo de histórico
   -- ya que el fichero 'POLIZAS_SC.TXT' se generará cada vez.
   UTL_FILE.fcopy(l_dir, l_nombre || '.TXT', l_dir, l_nombre || to_char(sysdate,'YYMMDDHH24MISS') || '.TXT');
   UTL_FILE.fcopy(l_dir, l_nombre || '_CRM.TXT', l_dir, l_nombre || to_char(sysdate,'YYMMDDHH24MISS') || '_CRM.TXT');
   UTL_FILE.fcopy(l_dir, l_nombre || '_CRA.TXT', l_dir, l_nombre || to_char(sysdate,'YYMMDDHH24MISS') || '_CRA.TXT');

   -- Pinta en el log la información de las pólizas enviadas a pagos
   PQ_Utl.LOG(lc, '');
   PQ_Utl.LOG(lc, '*********************************************************************************', 2);
   PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI:SS'), 2);
   PQ_Utl.LOG(lc, '*********************************************************************************', 2);
   PQ_Utl.LOG(lc, 'Polizas definitivas   := ' || l_num_polizas, 2);
   PQ_Utl.LOG(lc, '*********************************************************************************', 2);
   PQ_Utl.LOG(lc, '', 2);

   PQ_Utl.LOG(lc,'Fin del proceso ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
   PQ_Utl.LOG(lc,'## FIN ##', 1);

   RETURN 'Se han encontrado ' || l_num_polizas || ' definitivas.';


EXCEPTION
   WHEN no_pagos_found THEN
      PQ_Utl.LOG(lc,'*** no_pagos_found ***', 1);
      ROLLBACK;
      PQ_Err.raiser(sqlcode, l_error || ' [' || SQLERRM || ']');
      raise no_pagos_found;
      RETURN l_error || ' [' || SQLERRM || ']';
   WHEN no_polizas_found THEN
      PQ_Utl.LOG(lc,'*** no_polizas_found ***', 1);
      ROLLBACK;
      raise no_polizas_found;
      PQ_Err.raiser(sqlcode, l_error || ' [' || SQLERRM || ']');
      RETURN l_error || ' [' || SQLERRM || ']';
   WHEN fecha_planif_vacia THEN
   	  PQ_Utl.LOG(lc,'*** fecha_planif_vacia ***', 1);
      PQ_Err.raiser(sqlcode, l_error || ' [' || SQLERRM || ']');
      raise fecha_planif_vacia;
      RETURN l_error || ' [' || SQLERRM || ']';
   WHEN OTHERS THEN
      PQ_Utl.LOG(lc,'Error al generar los ficheros de load_polizas_definitivas [' || sqlcode || ' - ' || SQLERRM || '] ***', 1);
      ROLLBACK;
      raise;
      PQ_Err.raiser(sqlcode,'Error al generar los ficheros de load_polizas_definitivas' || ' [' || SQLERRM || ']');
      RETURN 'Error al generar los ficheros de load_polizas_definitivas' || ' [' || SQLERRM || ']';

END;


-- ---------------------------------------------------------------------------------------------------- --
-- Obtiene el número de pólizas no pagadas y que estén en un estado en el cual se puedan enviar a pagos --
-- ---------------------------------------------------------------------------------------------------- --
FUNCTION getNumPlzAPagar(fecha_planif IN DATE) RETURN NUMBER IS

    lc   VARCHAR2(50) := 'pq_cierrediacobro.getNumPlzAPagar';
    -- Número de pólizas no pagadas que están en algún estado en el cual se pueden enviar a pagos
    l_num_polizas_a_pagos NUMBER := 0;

BEGIN
-- Obtiene el número de pólizas que hay que enviar a pagos
    BEGIN
           /* ESC-7836 ** MODIF TAM (02.12.2019) **/
           /* Igualamos la Where a la de la Query principal que obtiene los datos */
          /*  SELECT COUNT(*) INTO l_num_polizas_a_pagos
              FROM o02agpe0.tb_polizas P, o02agpe0.Tb_Pagos_Poliza pg
              WHERE p.PAGADA = 0
              AND p.idpoliza = pg.idpoliza
              AND pg.tipo_pago <> 2
              AND ((p.IDESTADO in (5, 7, 8))
                OR
                (p.IDESTADO=3
                  AND (select ph.FECHA
               from o02agpe0.VW_POLIZAS_HIST_ESTADOS_DESC ph
               where ph.IDPOLIZA = P.IDPOLIZA and ph.ESTADO=8 and rownum=1)
                <= to_date (TO_CHAR(SYSDATE, 'DD/MM/YYYY') || '16:40:00', 'DD/MM/YYYY HH24:MI:SS') ));*/
              SELECT COUNT(*) INTO l_num_polizas_a_pagos
                FROM o02agpe0.tb_polizas P, o02agpe0.Tb_Pagos_Poliza pg
              WHERE p.PAGADA = 0
                AND p.idpoliza = pg.idpoliza
                AND pg.tipo_pago <> 2
            	  AND p.IDESTADO in (3, 5, 7, 8)
                AND (select ph.FECHA
                      from o02agpe0.VW_POLIZAS_HIST_ESTADOS_DESC ph
                     where ph.IDPOLIZA = P.IDPOLIZA and ph.ESTADO=8 and rownum=1)
                    <= to_date (TO_CHAR(fecha_planif, 'DD/MM/YYYY') || '16:40:00', 'DD/MM/YYYY HH24:MI:SS');

           /* ESC-7836 ** MODIF TAM (02.12.2019) **/

    EXCEPTION
        WHEN OTHERS THEN
            PQ_Utl.LOG(lc, 'Error al obtener el número de pólizas que se enviarán a pagos ', 2);
    END;

    RETURN l_num_polizas_a_pagos;

END getNumPlzAPagar;

-- --------------------------------------------------------- --
-- Obtiene el número de pólizas domicialiadas con Agroseguro --
-- --------------------------------------------------------- --
FUNCTION getNumPolizasDomiciliadas(fecha_planif IN DATE) RETURN NUMBER IS

    lc  VARCHAR2(50) := 'pq_cierrediacobro.getNumPolizasDomiciliadas';
    -- Número de pólizas domicialiadas con Agroseguro
    l_num_polizas_domiciliadas NUMBER := 0;

BEGIN
-- Obtiene el número de pólizas domicialiadas con Agroseguro
    BEGIN

		SELECT COUNT(*) INTO l_num_polizas_domiciliadas
			FROM o02agpe0.tb_polizas P, o02agpe0.Tb_Pagos_Poliza pg
				WHERE p.PAGADA = 1
                AND p.idpoliza = pg.idpoliza
                AND pg.tipo_pago = 2
				AND p.IDESTADO = 8
                AND (
					SELECT ph.FECHA
						FROM o02agpe0.VW_POLIZAS_HIST_ESTADOS_DESC ph
							WHERE ph.IDPOLIZA = P.IDPOLIZA
							AND ph.ESTADO=8
							AND ROWNUM=1
					) BETWEEN
						TO_DATE (TO_CHAR(fecha_planif-1, 'DD/MM/YYYY') || '16:40:00', 'DD/MM/YYYY HH24:MI:SS')
						AND
						TO_DATE (TO_CHAR(fecha_planif, 'DD/MM/YYYY') || '16:40:00', 'DD/MM/YYYY HH24:MI:SS');

    EXCEPTION
        WHEN OTHERS THEN
            PQ_Utl.LOG(lc, 'Error al obtener el número de pólizas domicialiadas con Agroseguro ', 2);
    END;

    RETURN l_num_polizas_domiciliadas;

END getNumPolizasDomiciliadas;

END pq_cierrediacobro;
/
SHOW ERRORS;
