SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.PQ_ENVIO_POLIZAS_RENOVABLES
IS
type t_array is TABLE OF VARCHAR2(50)INDEX BY BINARY_INTEGER;
TYPE array_cuentaAseg  is TABLE OF VARCHAR2(50)INDEX BY BINARY_INTEGER;

FUNCTION load_polizas_renovables(p_fecha IN VARCHAR2) RETURN VARCHAR2;
FUNCTION getCuentaAsegurado( nifAsegurado IN VARCHAR2, p_codentidad IN varchar2, l_codlineaPoliza in varchar2,
                             l_ent_med in o02agpe0.tb_colectivos.entmediadora%TYPE,
                             l_subent_med in o02agpe0.tb_colectivos.subentmediadora%TYPE) RETURN array_cuentaAseg;

FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array;

END PQ_ENVIO_POLIZAS_RENOVABLES;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_ENVIO_POLIZAS_RENOVABLES AS

  ----------------------------------------------------------------------------------------------------------------------------
  ----------------------------------------------------------------------------------------------------------------------------
  -- FUNCTION load_polizas_definitivas
  --
  -- Fichero de Pólizas Renovables
  --
  -- Recoge las pólizas que han entrado en el día y contiene los siguientes campos:
  --
  -- 1.- Código de entidad                                 PIC X(04)
  -- 2.- Código de oficina                                 PIC X(04)
  -- 3.- N.I.F. del asegurado                              PIC X(09)
  -- 5.- Plan de la póliza                                 PIC X(04)
  -- 6.- Línea de la póliza                                PIC X(03)
  -- 8.- Número de póliza.                                 PIC X(07)
  -- X.- Tipo de poliza: principal/complementaria          PIC X(01)
  -- 11.- Importe total de la póliza                       PIC 9(11)
  -- 19.- Cuenta.  Dividido en los campos:
  --                                              ENTCCC   PIC X(4)
  --                                              OFICCC   PIC X(4)
  --                                              DIGCCC   PIC X(02)
  --                                              MODCCC   PIC X(02)
  --                                              CTACCC   PIC X(06)
  --                                              DIGCTA   PIC X(02)
  ---------------------------------------------------------------------------------------------------------------------------
  ---------------------------------------------------------------------------------------------------------------------------
  FUNCTION load_polizas_renovables(p_fecha IN VARCHAR2) RETURN VARCHAR2 IS

    lc VARCHAR2(80) := 'PQ_ENVIO_POLIZAS_RENOVABLES.load_polizas_renovables';

    TYPE TpCursor IS REF CURSOR;

    f_fichero     UTL_FILE.FILE_TYPE;
    f_fichero_cra UTL_FILE.FILE_TYPE; -- Fichero para CR Almendralejo
    l_dir         TB_CONFIG_AGP.AGP_valor%TYPE; -- Valor del parametro de configuracion
    l_line        VARCHAR2(1000);
    l_error       VARCHAR2(1000);
    l_nombre      VARCHAR2(1000);
    l_dir_name    VARCHAR2(1000);
    l_query       VARCHAR2(32000);
    l_tp_cursor   TpCursor;
    l_idpoliza    TB_POLIZAS_RENOVABLES.ID%TYPE;

    -- Código de estado de pago 'Pendiente confirmación'
    ESTADO_AGP_PEND_CONF CONSTANT NUMBER(1) := 2;
    ESTADO_AGP_PAGADO    CONSTANT NUMBER(1) := 1;

    -- **** Composicion de la linea ****
    l_CODIGO_ENTIDAD      TB_COLECTIVOS.CODENTIDAD%TYPE;
    l_CODIGO_OFICINA      varchar2(4);
    l_NIF_TOMADOR         varchar2(9);
    l_CTA_ABONO_TOMADOR   varchar2(20);
    l_FECHA_ABONO         varchar2(10);
    l_NUMERO_POLIZA       VARCHAR2(8); -- REF_DC_POLIZA   ,
    l_NETO_TOMADOR        VARCHAR2(11);
    l_NIF_ASEGURADO       TB_ASEGURADOS.NIFCIF%TYPE;
    l_DISC_ASEGURADO      VARCHAR2(3);
    l_LINEA_POLIZA        TB_LINEAS.CODLINEA%TYPE;
    l_PLAN_POLIZA         TB_LINEAS.CODPLAN%TYPE;
    l_CTA_CARGO_ASEGURADO Varchar2(20);
    tamAseg               NUMBER;
    tamIban               NUMBER;
    l_FEC_CARGO           VARCHAR2(10);
    l_IMPORTE_CARGO       VARCHAR2(11);
    l_REF_POLIZA          VARCHAR2(8);
    l_COSTE_TOTAL_TOMADOR TB_POLIZAS_RENOVABLES.COSTE_TOTAL_TOMADOR%TYPE;
    l_IMPORTE_ABONAR      TB_POLIZAS_RENOVABLES.IMPORTE_DOMICILIAR%TYPE;
    l_DEST_DOMICILIACION  TB_POLIZAS_RENOVABLES.DESTINO_DOMICILIACION%TYPE;
    l_ENVIO_A_PAGOS       VARCHAR2(1);
    l_FECHA_RENOVACION    TB_POLIZAS_RENOVABLES.FECHA_RENOVACION%TYPE;
	l_FECHA_DOMICILIACION TB_POLIZAS_RENOVABLES.FECHA_DOMICILIACION%TYPE;
    l_IBAN                VARCHAR2(34);
    l_IBAN_TOM            VARCHAR2(34);
    l_NOMBRE_ASEG         VARCHAR2(20);
    l_APELLIDO_ASEG       VARCHAR2(80);
    l_RAZON_SOCIAL        VARCHAR2(100);
    l_TIPO_IDENTIFICACION VARCHAR2(3);
    l_num_cuentaAsegurado array_cuentaAseg;
    v_num_cuentaAsegurado array_cuentaAseg;
    l_num_polizas         NUMBER := 0; -- Número de pólizas enviadas a pagos
    l_num_polizas_cra     NUMBER := 0; -- Número de pólizas DE CR Almendralejo enviadas a pagos
    str_num_polizas      CONSTANT VARCHAR2(80) := 'PAGOS_POL_REN_ENV'; -- PAGOS_POLIZAS_RENOVA_ENVIADAS
    
    ASEGURADO_SIN_CUENTA CONSTANT VARCHAR(24) := 'ASEGURADO SIN CUENTA';
    l_num_polizas_a_pagos NUMBER := 0; -- Número de pólizas no pagadas que están en algún estado en el cual se pueden enviar a pagos
    str_num_polizas_a_pagos CONSTANT VARCHAR2(80) := 'PAGOS_POL_REN_A_ENV'; -- PAGOS_POLIZAS_RENOVA_A_ENVIAR
    l_num_polizas_frac NUMBER := 0; -- Número de pólizas de destinatario distinto de tomador
    l_num_pol_pag_tom NUMBER := 0;  -- Número de pólizas Pagadas, pero no enviadas.
    l_num_pol_fichero NUMBER := 0;  -- Número de pólizas incluidas en el fichero 
    l_num_polizas_clie NUMBER := 0; -- Numero de polizas de Asegurado sin cuenta.
    /* ESC-7877 */
    str_num_pol_tom  VARCHAR2(80):= 'POL_PAGADAS_TOMADOR';
    /* ESC-7877 */
    
    v_msg_error_aux VARCHAR2(100);
    -- *********** Excepciones **********************************
    no_polizas_found EXCEPTION;
    no_pagos_found EXCEPTION;

    llenarErrores   boolean default true;    
    
    /* PTC-5729 */
    longitudErrores1 number := 0;
    longitudErrores2 number := 0;
    
    --v_plz_error_msg VARCHAR2(2000) := NULL; -- Listado de referencias con su descripción, erroneas
    v_plz_error_msg1 VARCHAR2(2000) := NULL; -- Listado de referencias con descripción 1, erroneas
    v_plz_error_msg2 VARCHAR2(2000) := NULL; -- Listado de referencias con descripción 2, erroneas
    
    str_plz_def_e_msg1    CONSTANT VARCHAR(24) := 'PAGOS_POL_DEF_MSG_ER1'; --PAGOS_POL_DEF_MSG_ERROR1
    str_plz_def_e_msg2    CONSTANT VARCHAR(24) := 'PAGOS_POL_DEF_MSG_ER2'; --PAGOS_POL_DEF_MSG_ERROR2
    /* PTC-5729 Fin */
    
    -- MPM - Variables para obtener el mediador
    L_ENT_MED    o02agpe0.tb_colectivos.entmediadora%TYPE;
    L_SUBENT_MED o02agpe0.tb_colectivos.subentmediadora%TYPE;

    -- P20610 - MPM - Variables para almacenar el número de días antes y después de la fecha actual
    -- que se buscarán por fecha de renovación las pólizas renovables para enviar a pagos
    V_NUM_DIAS_DESDE NUMBER(3) := 7; -- Valor por defecto
    V_NUM_DIAS_HASTA NUMBER(3) := 4; -- Valor por defecto
    V_ESTADOS_ENVIO  o02agpe0.tb_parametros.estado_plz_renov_pago%TYPE := '3,4'; -- Valor por defecto
    REG_PARAMETROS   o02agpe0.tb_parametros%ROWTYPE;
	v_fecha_planif DATE;
  BEGIN

    PQ_Utl.LOG(lc, '## INI ##', 1);

	pq_utl.log(lc, 'Fecha de planificacion recibida por parametro - ' || p_fecha);
	
	v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');
    -- PQ_Utl.getcfg esta en el paquete de utilidades
    -- accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).
    l_dir := PQ_Utl.getcfg('DIR_EXPORT_ENVIOS_RENOVABLES');
    -- Se guarda el path fisico del directorio
    
    SELECT DIRECTORY_PATH
      into l_dir_name
      FROM ALL_DIRECTORIES
     WHERE DIRECTORY_NAME = l_dir;

    l_nombre := 'POLIZAS_RENOVA';
    -- ********************************************************
    -- Abrimos el fichero de salida con extension .txt
    -- ********************************************************
    f_fichero := UTL_FILE.FOPEN(LOCATION     => l_dir,
                                filename     => l_nombre || '.TXT',
                                open_mode    => 'w',
                                max_linesize => PQ_Typ.MAX_LINEFILESIZEWRITE);

    f_fichero_cra := UTL_FILE.FOPEN(LOCATION     => l_dir,
                                    filename     => l_nombre || '_CRA.TXT',
                                    open_mode    => 'w',
                                    max_linesize => PQ_Typ.MAX_LINEFILESIZEWRITE);

    BEGIN

      -- Reseteamos todos los contadores de envío y recepción de Pólizas renovables a Pagos
      --PQ_Utl.setcfg(str_num_polizas_a_pagos, 0);
      --PQ_Utl.setcfg(str_num_polizas, 0);
      /* ESC-7877 */
      --PQ_Utl.setcfg(str_num_pol_tom, 0);
      
      --PQ_Utl.setcfg('PAGOS_POLIZAS_RENOVA_CRA', 0);
      --PQ_Utl.setcfg('PAGOS_POLIZAS_RENOVA_OK', 0);
      --PQ_Utl.setcfg('PAGOS_POLIZAS_RENOVA_ERROR', 0);
      --PQ_Utl.setcfg('PAGOS_POLIZAS_RENOVA_MSG_ERROR', 0);
      --PQ_Utl.setcfg('PAGOS_POLIZA_RENOVA_EJECUCION', ' ');
      --PQ_Utl.setcfg('PAGOS_POLIZAS_RENOVA_FRAC', 0);
      --P20610 - MPM - Obtiene el número de días antes y después de la fecha actual y los estados en los que se envían
      BEGIN
        SELECT *
          INTO REG_PARAMETROS
          FROM o02agpe0.tb_parametros
         WHERE ROWNUM = 1;

        IF (REG_PARAMETROS.NUM_DIAS_DESDE_PAGO_RENOV IS NOT NULL) THEN
          V_NUM_DIAS_DESDE := REG_PARAMETROS.NUM_DIAS_DESDE_PAGO_RENOV;
        END IF;

        IF (REG_PARAMETROS.NUM_DIAS_HASTA_PAGO_RENOV IS NOT NULL) THEN
          V_NUM_DIAS_HASTA := REG_PARAMETROS.NUM_DIAS_HASTA_PAGO_RENOV;
        END IF;

        IF (REG_PARAMETROS.ESTADO_PLZ_RENOV_PAGO IS NOT NULL) THEN
          -- Elimina la última coma de la cadena si existe
          V_ESTADOS_ENVIO := TRIM(TRAILING ',' FROM
                                  REG_PARAMETROS.ESTADO_PLZ_RENOV_PAGO);
        END IF;
        -- Fin P20610

      EXCEPTION
        WHEN OTHERS THEN
          PQ_Utl.LOG(lc,
                     'Error al obtener los parámetros de la tabla TB_PARAMETROS, se utilizarán los valores por defecto',
                     2);
      END;

      PQ_Utl.LOG(lc,
                 'Se buscarán renovables con fecha de renovación desde hoy - ' ||
                 V_NUM_DIAS_DESDE || ' días',
                 2);
      PQ_Utl.LOG(lc,
                 'Se buscarán renovables con fecha de renovación hasta hoy + ' ||
                 V_NUM_DIAS_HASTA || ' días',
                 2);
      PQ_Utl.LOG(lc,
                 'Se buscarán renovables en estados (' || V_ESTADOS_ENVIO || ')',
                 2);

      PQ_Utl.LOG(lc, 'Recuperando las polizas definitivas ', 2);
      l_error := 'Recuperando en la tabla de polizas las polizas definitivas ';

	  -- IGT 16/01/2019 POLIZAS EN ESTADOS PARAMETRIZADOS ENTRE LAS FECHAS INDICADAS UNION CON TODAS LAS POLIZAS EN ESTADO 4
      l_query := 'SELECT ren.ID,
           col.CODENTIDAD,
           nvl(lpad(pol.OFICINA, 4, 0), ''9999''),
           LPAD(ren.NIF_TOMADOR, 9, ''0''),
           col.IBAN,
           CONCAT(CONCAT(CONCAT(col.ccc_entidad, col.ccc_oficina),
                         col. ccc_dc),
                  col.ccc_cuenta),
           TO_CHAR(to_date(''' || v_fecha_planif || '''), ''DD.MM.YYYY''),
           (ren.REFERENCIA || ren.DC),
           LPAD(TRIM(TO_CHAR(NVL (ren.IMPORTE_DOMICILIAR, 0), ''999999999V99'')), 11, ''0''),
           LPAD(ren.NIF_ASEGURADO, 9, ''0''),
           LPAD(ase.DISCRIMINANTE, 3, ''0''),
           ren.LINEA,
           ren.PLAN,
           TO_CHAR(to_date(''' || v_fecha_planif || '''), ''DD.MM.YYYY''),
           LPAD(TRIM(TO_CHAR(NVL (ren.IMPORTE_DOMICILIAR, 0), ''999999999V99'')), 11, ''0''),
           ren.REFERENCIA,
           col.entmediadora,
           col.subentmediadora,
		       ren.coste_total_tomador,
           ren.importe_domiciliar,
           ren.destino_domiciliacion,
           tom.envioAPagos,
           ren.FECHA_RENOVACION,
		   ren.FECHA_DOMICILIACION,
           RPAD(ase.NOMBRE, 20, '' ''),
           RPAD(ase.APELLIDO1 || '' '' || ase.APELLIDO2, 80, '' ''),
           RPAD(LTRIM(ase.RAZONSOCIAL), 100, '' ''),
           ase.TIPOIDENTIFICACION
  	  FROM TB_POLIZAS_RENOVABLES ren,
           TB_ASEGURADOS         ase,
           TB_COLECTIVOS         col,
           TB_POLIZAS            pol,
           TB_TOMADORES          tom,
           tb_lineas             lin
      WHERE ren.ESTADO_AGROSEGURO in (' || V_ESTADOS_ENVIO || ')
           AND col.ID = pol.idcolectivo
           AND ase.ID = pol.IDASEGURADO
           AND ren.nif_tomador = tom.ciftomador
           AND col.codentidad = tom.codentidad
           AND ren.FECHA_RENOVACION BETWEEN to_date(''' || to_date(v_fecha_planif - V_NUM_DIAS_DESDE) || ''') and to_date(''' || to_date(v_fecha_planif + V_NUM_DIAS_HASTA) || ''')
           AND POL.REFERENCIA=REN.REFERENCIA
           AND pol.dc=ren.dc
           AND ren.PAGADA =0
           AND lin.lineaseguroid = pol.lineaseguroid
           AND lin.codlinea = ren.linea
           AND lin.codplan = ren.plan
       UNION
       SELECT ren.ID,
           col.CODENTIDAD,
           nvl(lpad(pol.OFICINA, 4, 0), ''9999''),
           LPAD(ren.NIF_TOMADOR, 9, ''0''),
           col.IBAN,
           CONCAT(CONCAT(CONCAT(col.ccc_entidad, col.ccc_oficina),
                         col. ccc_dc),
                  col.ccc_cuenta),
           TO_CHAR(to_date(''' || v_fecha_planif || '''), ''DD.MM.YYYY''),
           (ren.REFERENCIA || ren.DC),
           LPAD(TRIM(TO_CHAR(NVL (pol.COSTE_TOMADOR_AGRO, 0), ''999999999V99'')), 11, ''0''),
           LPAD(ren.NIF_ASEGURADO, 9, ''0''),
           LPAD(ase.DISCRIMINANTE, 3, ''0''),
           ren.LINEA,
           ren.PLAN,
           TO_CHAR(to_date(''' || v_fecha_planif || '''), ''DD.MM.YYYY''),
           LPAD(TRIM(TO_CHAR(NVL (pol.COSTE_TOMADOR_AGRO, 0), ''999999999V99'')), 11, ''0''),
           ren.REFERENCIA,
           col.entmediadora,
           col.subentmediadora,
		       ren.coste_total_tomador,
           ren.importe_domiciliar,
           ren.destino_domiciliacion,
           tom.envioAPagos,
           ren.FECHA_RENOVACION,
		   ren.FECHA_DOMICILIACION,
           RPAD(ase.NOMBRE, 20, '' ''),
           RPAD(ase.APELLIDO1 || '' '' || ase.APELLIDO2, 80, '' ''),
           RPAD(LTRIM(ase.RAZONSOCIAL), 100, '' ''),
           ase.TIPOIDENTIFICACION
  	  FROM TB_POLIZAS_RENOVABLES ren,
           TB_ASEGURADOS         ase,
           TB_COLECTIVOS         col,
           TB_POLIZAS            pol,
           TB_TOMADORES          tom,
           tb_lineas             lin
      WHERE ren.ESTADO_AGROSEGURO = 4
           AND col.ID = pol.idcolectivo
           AND ase.ID = pol.IDASEGURADO
           AND ren.nif_tomador = tom.ciftomador
           AND col.codentidad = tom.codentidad
           AND POL.REFERENCIA=REN.REFERENCIA
           AND pol.dc=ren.dc  
           AND ren.PAGADA =0
           AND lin.lineaseguroid = pol.lineaseguroid
           AND lin.codlinea = ren.linea
           AND lin.codplan = ren.plan';

      PQ_Utl.LOG(lc, l_query, 2);

      OPEN l_tp_cursor FOR l_query;
      loop
        FETCH l_tp_cursor
          INTO l_idpoliza, l_CODIGO_ENTIDAD, l_CODIGO_OFICINA, l_NIF_TOMADOR, l_IBAN_TOM, l_CTA_ABONO_TOMADOR, l_FECHA_ABONO, l_NUMERO_POLIZA, l_NETO_TOMADOR, l_NIF_ASEGURADO, l_DISC_ASEGURADO, l_LINEA_POLIZA, l_PLAN_POLIZA, l_FEC_CARGO, l_IMPORTE_CARGO, l_REF_POLIZA, L_ENT_MED, L_SUBENT_MED, l_COSTE_TOTAL_TOMADOR, l_IMPORTE_ABONAR, l_DEST_DOMICILIACION, l_ENVIO_A_PAGOS, l_FECHA_RENOVACION, l_FECHA_DOMICILIACION, l_NOMBRE_ASEG, l_APELLIDO_ASEG, l_RAZON_SOCIAL, l_TIPO_IDENTIFICACION;

        EXIT WHEN l_tp_cursor%NOTFOUND;

        PQ_Utl.LOG(lc,
                   '-- Comienza el proceso para la renovable ' ||
                   l_idpoliza,
                   2);

        PQ_Utl.LOG(lc, '--- Valor de COSTE_TOTAL_TOMADOR recuperado: ' || l_COSTE_TOTAL_TOMADOR || ' de la póliza: ' || l_idpoliza, 2);
        PQ_Utl.LOG(lc, '--- Valor de IMPORTE_ABONAR recuperado: ' || l_IMPORTE_ABONAR || ' de la póliza: ' || l_idpoliza, 2);
        PQ_Utl.LOG(lc, '--- Valor de DEST_DOMICILIACION recuperado: ' || l_DEST_DOMICILIACION || ' de la póliza: ' || l_idpoliza, 2);
        
        IF l_ENVIO_A_PAGOS = 'N' THEN
        
          l_num_pol_pag_tom := l_num_pol_pag_tom + 1;
          -- Las pólizas que tienen cuyo tomador tiene como ENVIO A PAGOS A NO,
          -- se marcan como pagadas y fecha de pago la fecha de renovación de la póliza

          PQ_Utl.LOG(lc,
                     'El tomador está marcado como ENVIO A PAGOS A NO',
                     2);

          -- Actualizo tb_polizas_renovables
          UPDATE TB_POLIZAS_RENOVABLES P
             SET P.PAGADA = ESTADO_AGP_PAGADO
           WHERE P.ID = l_idpoliza;

          -- Actualizo tb_polizas
          UPDATE TB_POLIZAS P
             SET P.PAGADA     = ESTADO_AGP_PAGADO,
                 P.FECHA_PAGO = l_FECHA_RENOVACION
           WHERE P.IDPOLIZA IN
                 (select p.idpoliza
                    from o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
                   where p.referencia = l_REF_POLIZA
                     and p.lineaseguroid = l.lineaseguroid
                     and l.codplan = l_PLAN_POLIZA
                     and l.codlinea = l_LINEA_POLIZA);

          PQ_Utl.LOG(lc,
                     'REF POLIZA con envíoAPagos a NO: ' || l_PLAN_POLIZA || '/' ||
                     l_LINEA_POLIZA || ' - ' || l_REF_POLIZA);

        ELSIF l_DEST_DOMICILIACION = 'T' THEN
			
			IF l_IMPORTE_ABONAR IS NOT NULL AND l_IMPORTE_ABONAR > 0 THEN
			
			  PQ_Utl.LOG(lc,
						 'El destinatario de la domiciliacion es el tomador, se incluye en el fichero de pagos',
						 2);

			  l_CTA_CARGO_ASEGURADO := '';
			  l_IBAN                := '';
			  l_num_cuentaAsegurado := getCuentaAsegurado(l_NIF_ASEGURADO,
														  l_CODIGO_ENTIDAD,
														  l_LINEA_POLIZA,
														  L_ENT_MED,
														  L_SUBENT_MED);
			  if (l_num_cuentaAsegurado is not null AND
				  l_num_cuentaAsegurado(0) is not null AND
				  l_num_cuentaAsegurado(1) is not null) then
				l_IBAN                := l_num_cuentaAsegurado(0);
				l_CTA_CARGO_ASEGURADO := l_num_cuentaAsegurado(1);
			  end if;

			  PQ_Utl.LOG(lc,
						 'ASEGURADO:' || trim(l_CTA_CARGO_ASEGURADO) || '##' ||
						 l_IBAN,
						 2);

			  tamAseg := LENGTH(l_CTA_CARGO_ASEGURADO);
			  tamIban := LENGTH(l_IBAN);

			  IF tamAseg IS NULL or tamAseg <> 20 or tamIban IS NULL or
				 tamIban <> 4 THEN
				
				/* ESC-7877 */ 
				l_num_polizas_clie := l_num_polizas_clie + 1; 
				
				-- guardamos la ref y la descripción
				-- Obtiene la descripción del error asociada al código
				v_msg_error_aux := ASEGURADO_SIN_CUENTA;
				PQ_Utl.LOG(lc,
						   '-- Pago de póliza a enviar ' || l_NUMERO_POLIZA ||
						   ' erróneo - ' || v_msg_error_aux || '   ',
						   2);

				if llenarErrores then
				  /*PTC-5729 - Inicio */
				  longitudErrores1 := LENGTH(v_plz_error_msg1);
				  
				  if (longitudErrores1 = 0 or longitudErrores1 IS NULL) THEN
					 v_plz_error_msg1 := v_msg_error_aux || ': ' ||
									  l_NUMERO_POLIZA ; 
				  ELSE                                        
					if (longitudErrores1 < 1980) then
						v_plz_error_msg1 := v_plz_error_msg1 || ', ' ||
											l_NUMERO_POLIZA ;
					else
					  v_plz_error_msg1 := v_plz_error_msg1 || ', [...]';
					  llenarErrores   := false;
					end if;
				 END IF;   
				  /* PTC-5729 - Fin */
				end if;

			  ELSE
				IF L_CODIGO_ENTIDAD = 3001 THEN
				  -- CRA
				  IF (l_TIPO_IDENTIFICACION = 'CIF') THEN
					l_line := l_CODIGO_ENTIDAD || l_CODIGO_OFICINA ||
							  l_NIF_TOMADOR || l_CTA_ABONO_TOMADOR ||
							  l_FECHA_ABONO || l_NUMERO_POLIZA ||
							  l_NETO_TOMADOR || l_NIF_ASEGURADO ||
							  l_DISC_ASEGURADO || l_LINEA_POLIZA ||
							  l_PLAN_POLIZA || l_CTA_CARGO_ASEGURADO ||
							  l_FEC_CARGO || l_IMPORTE_CARGO || l_RAZON_SOCIAL;
				  ELSE
					l_line := l_CODIGO_ENTIDAD || l_CODIGO_OFICINA ||
							  l_NIF_TOMADOR || l_CTA_ABONO_TOMADOR ||
							  l_FECHA_ABONO || l_NUMERO_POLIZA ||
							  l_NETO_TOMADOR || l_NIF_ASEGURADO ||
							  l_DISC_ASEGURADO || l_LINEA_POLIZA ||
							  l_PLAN_POLIZA || l_CTA_CARGO_ASEGURADO ||
							  l_FEC_CARGO || l_IMPORTE_CARGO || l_NOMBRE_ASEG ||
							  l_APELLIDO_ASEG;
				  END IF;

				  -- Si la entidad es la de la CR Almendralejo, lo añado a otro fichero
				  UTL_FILE.PUT_LINE(f_fichero_cra, l_line || chr(13));
				  l_num_polizas_cra := l_num_polizas_cra + 1;
				ELSE
				  IF (l_TIPO_IDENTIFICACION = 'CIF') THEN
					l_line := l_CODIGO_ENTIDAD || l_CODIGO_OFICINA ||
							  l_NIF_TOMADOR || RPAD(LTRIM(l_IBAN_TOM), 34, ' ') ||
							  l_CTA_ABONO_TOMADOR || l_FECHA_ABONO ||
							  l_NUMERO_POLIZA || l_NETO_TOMADOR ||
							  l_NIF_ASEGURADO || l_DISC_ASEGURADO ||
							  l_LINEA_POLIZA || l_PLAN_POLIZA ||
							  RPAD(LTRIM(l_IBAN), 34, ' ') ||
							  l_CTA_CARGO_ASEGURADO || l_FEC_CARGO ||
							  l_IMPORTE_CARGO || l_RAZON_SOCIAL;
				  ELSE
					l_line := l_CODIGO_ENTIDAD || l_CODIGO_OFICINA ||
							  l_NIF_TOMADOR || RPAD(LTRIM(l_IBAN_TOM), 34, ' ') ||
							  l_CTA_ABONO_TOMADOR || l_FECHA_ABONO ||
							  l_NUMERO_POLIZA || l_NETO_TOMADOR ||
							  l_NIF_ASEGURADO || l_DISC_ASEGURADO ||
							  l_LINEA_POLIZA || l_PLAN_POLIZA ||
							  RPAD(LTRIM(l_IBAN), 34, ' ') ||
							  l_CTA_CARGO_ASEGURADO || l_FEC_CARGO ||
							  l_IMPORTE_CARGO || l_NOMBRE_ASEG ||
							  l_APELLIDO_ASEG;
				  END IF;
				  UTL_FILE.PUT_LINE(f_fichero, l_line || chr(13));
				  l_num_polizas := l_num_polizas + 1;
				END IF;

				-- MPM 04/10/13 -
				-- Se informa la fecha de envío a pago y se actualiza el estado del pago de la póliza
				-- a 'Pendiente confirmación'
				UPDATE TB_POLIZAS_RENOVABLES P
				   SET P.PAGADA = ESTADO_AGP_PEND_CONF
				 WHERE P.ID = l_idpoliza;
				
				-- Actualizo también tb_polizas
				UPDATE TB_POLIZAS P
				   SET P.PAGADA = ESTADO_AGP_PEND_CONF
				 WHERE P.IDPOLIZA IN
					   (select p.idpoliza
						  from o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
						 where p.referencia = l_REF_POLIZA
						   and p.lineaseguroid = l.lineaseguroid
						   and l.codplan = l_PLAN_POLIZA
						   and l.codlinea = l_LINEA_POLIZA);

			  END IF;
			END IF;
        ELSE
        
         PQ_Utl.LOG(lc, 'La Póliza: ' || l_idpoliza || ' tiene destinatario distinto de tomador', 2);
          l_num_polizas_frac := l_num_polizas_frac + 1;

          UPDATE TB_POLIZAS_RENOVABLES P
             SET P.PAGADA = ESTADO_AGP_PAGADO
           WHERE P.ID = l_idpoliza;
          
          -- Actualizo también tb_polizas
          UPDATE TB_POLIZAS P
             SET P.PAGADA = ESTADO_AGP_PAGADO,
				         P.FECHA_PAGO = l_FECHA_DOMICILIACION
           WHERE P.IDPOLIZA IN
                 (select p.idpoliza
                    from o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
                   where p.referencia = l_REF_POLIZA
                     and p.lineaseguroid = l.lineaseguroid
                     and l.codplan = l_PLAN_POLIZA
                     and l.codlinea = l_LINEA_POLIZA);

          --Las de destinatario distinto de tomador se meten como si fueran erróneas para que aparezcan luego en el correo

          if llenarErrores then
            /* PTC - 5729 - Inicio */   
            longitudErrores2 := length(v_plz_error_msg2);
            
            if (longitudErrores2 = 0 or longitudErrores2 is NULL) THEN
               v_plz_error_msg2 := 'DESTINATARIO DIFERENTE A TOMADOR: ' || l_NUMERO_POLIZA;
            ELSE 
               if (longitudErrores2 < 1980) then
                  v_plz_error_msg2 := v_plz_error_msg2 || ', ' || l_NUMERO_POLIZA ;
               else
                  v_plz_error_msg2 := v_plz_error_msg2 || ', [...]';
                  /* ESC-7443 ** MODIF TAM (05.11.2019) */
                  llenarErrores   := false;
               end if;
              /* PTC - 5729 - FIN  */
            END IF;
          END IF;

        END IF;

        IF l_DEST_DOMICILIACION = 'T' THEN
          PQ_Utl.LOG(lc,
                     'Actualizamos IBAN de pólizas con destinatario de domiciliación = al tomador ' ||
                     l_REF_POLIZA);
          PQ_Utl.LOG(lc, 'Busca la cuenta del asegurado:');
          PQ_Utl.LOG(lc, 'l_NIF_ASEGURADO: ' || l_NIF_ASEGURADO);
          PQ_Utl.LOG(lc, 'l_CODIGO_ENTIDAD: ' || l_CODIGO_ENTIDAD);
          PQ_Utl.LOG(lc, 'l_LINEA_POLIZA: ' || l_LINEA_POLIZA);
          PQ_Utl.LOG(lc, 'L_ENT_MED: ' || L_ENT_MED);
          PQ_Utl.LOG(lc, 'L_SUBENT_MED: ' || L_SUBENT_MED);

          v_num_cuentaAsegurado := getCuentaAsegurado(l_NIF_ASEGURADO,
                                                      l_CODIGO_ENTIDAD,
                                                      l_LINEA_POLIZA,
                                                      L_ENT_MED,
                                                      L_SUBENT_MED);

          PQ_Utl.LOG(lc, 'Búsqueda finalizada');

          if (v_num_cuentaAsegurado is not null AND
             v_num_cuentaAsegurado(0) is not null AND
             v_num_cuentaAsegurado(1) is not null) then
            PQ_Utl.LOG(lc, 'Registro encontrado');
            PQ_Utl.LOG(lc,
                       v_num_cuentaAsegurado(0) || v_num_cuentaAsegurado(1));
            l_IBAN := CONCAT(v_num_cuentaAsegurado(0),
                             v_num_cuentaAsegurado(1));

            UPDATE TB_POLIZAS_RENOVABLES P
               SET P.IBAN = l_IBAN
             WHERE P.ID = l_idpoliza;
          ELSE
            PQ_Utl.LOG(lc,
                       'IBAN de pólizas con destinatario de domiciliación <> al asegurado. IBAN no encontrado. Póliza: ' ||
                       l_REF_POLIZA);
          end if;
        END IF;

		l_num_polizas_a_pagos := l_num_polizas_a_pagos + 1;

      END LOOP;

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        l_error := 'No se han encontrado polizas definitivas';
        PQ_Utl.LOG(lc, l_error, 2);
        PQ_Utl.LOG(lc,
                   'Error al generar los ficheros de load_polizas_renovables [' ||
                   sqlcode || ' - ' || SQLERRM || '] ***',
                   1);
        RAISE no_polizas_found;
    END;

    -- Actualiza en la tabla de configuración el número de pólizas enviadas a pagos
	  PQ_Utl.LOG(lc,
                     'Número de pólizas que se enviarán a pagos = ' ||
                     l_num_polizas_a_pagos,
                     2);
                     
              	  PQ_Utl.LOG(lc,
                     'PQ_Utl.setcfg(' ||
                     str_num_polizas_a_pagos || p_fecha || ',' ||  TO_CHAR(l_num_polizas_a_pagos),
                     2);       
	  PQ_Utl.setcfg(str_num_polizas_a_pagos || p_fecha, TO_CHAR(l_num_polizas_a_pagos));
  
    /* ESC-7753 ** Incluimos en el contador de polizas Enviadas a Pagos las que se han actualizado a Pagadas, cuyo tomador tiene como ENVIO A PAGOS A NO*/
    l_num_pol_fichero := l_num_polizas;
    
    l_num_polizas := l_num_polizas + l_num_pol_pag_tom;
    /* FIN ESC-7753 */
    PQ_Utl.LOG(lc,
               'Número de pólizas enviadas a pagos = ' || l_num_polizas ||
               '   ',
               2);
    PQ_Utl.setcfg(str_num_polizas || p_fecha, l_num_polizas);
    
    /* ESC-7877 ** MODIF TAM (11.12.2019) ** Inicio */
    /* Guardamos en BBDD el contador de polizas pagadas pero no enviadas a pagos por Tomador */
    /* Para recuperarlo luego en el correo Resumen */

    PQ_Utl.setcfg(str_num_pol_tom || p_fecha, l_num_pol_pag_tom);
    /* ESC-7877 */
    
    PQ_Utl.LOG(lc,
               'Número de pólizas CR Almendralejo enviadas a pagos = ' ||
               l_num_polizas_cra || '   ',
               2);
    PQ_Utl.setcfg('PAGOS_POL_RENOVA_CRA' || p_fecha, l_num_polizas_cra);
    -- ESC-23068: Se quita la fecha del contador PAGOS_POL_RENOVA_FRAC dado que no se utiliza en el correo de resumen
    PQ_Utl.setcfg('PAGOS_POL_RENOVA_FRAC', l_num_polizas_frac);

    -- Detalle de las para enviar a pagos erroneas
    /* PTC - 5729 * Inicio */
    /* Grabamos en 3 filas distintas los 3 tipos de errores */
    /* ASEGURADO SIN CUENTA */
    IF (v_plz_error_msg1 IS NOT NULL) THEN
      PQ_UTL.setcfg(str_plz_def_e_msg1 || p_fecha, v_plz_error_msg1);
    ELSE
      PQ_UTL.setcfg(str_plz_def_e_msg1 || p_fecha, ' ');
    END IF;

    /*DESTINATARIO DIFERENTE A TOMADOR*/
    IF (v_plz_error_msg2 IS NOT NULL) THEN
      PQ_UTL.setcfg(str_plz_def_e_msg2 || p_fecha, v_plz_error_msg2);
    ELSE
      PQ_UTL.setcfg(str_plz_def_e_msg2 || p_fecha, ' ');
    END IF;

    -- ********************************************************
    -- Cerramos el fichero.
    -- ********************************************************
    COMMIT;
    CLOSE l_tp_cursor;
    UTL_FILE.FCLOSE(f_fichero);
    UTL_FILE.FCLOSE(f_fichero_cra);

    -- Realizamos una copia del fichero generado para guardarlo a modo de histórico
    -- ya que el fichero 'POLIZAS_RENOVA.TXT' se generará cada vez.
    UTL_FILE.fcopy(l_dir,
                   l_nombre || '.TXT',
                   l_dir,
                   l_nombre || to_char(v_fecha_planif, 'YYMMDDHH24MISS') || '.TXT');
    UTL_FILE.fcopy(l_dir,
                   l_nombre || '_CRA.TXT',
                   l_dir,
                   l_nombre || to_char(v_fecha_planif, 'YYMMDDHH24MISS') ||
                   '_CRA.TXT');

    -- Pinta en el log la información de las pólizas enviadas a pagos
    PQ_Utl.LOG(lc, '');
    PQ_Utl.LOG(lc,
               '*********************************************************************************',
               2);
    PQ_Utl.LOG(lc,
               'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' ||
               TO_CHAR(v_fecha_planif, 'DD/MM/YY HH24:MI:SS'),
               2);
    PQ_Utl.LOG(lc,
               '*********************************************************************************',
               2);
    PQ_Utl.LOG(lc, 'POLIZAS TRATADAS                := ' || l_num_polizas_a_pagos, 2);
    PQ_Utl.LOG(lc, 'POLIZAS TOTALES ENVIADAS        := ' || l_num_polizas, 2);
    PQ_Utl.LOG(lc, 'POLIZAS INCLUIDAS EN FICHERO    := ' || l_num_pol_fichero, 2);
    PQ_Utl.LOG(lc, 'POLIZAS PAGADA TOM. NO ENVIADA  := ' || l_num_pol_pag_tom, 2);
    PQ_Utl.LOG(lc, 'POLIZAS DEST DIST TOMADOR       := ' || l_num_polizas_frac, 2);
    PQ_Utl.LOG(lc, 'POLIZAS PAGO SIN CUENTA         := ' || l_num_polizas_clie,2);
    PQ_Utl.LOG(lc,
               '*********************************************************************************',
               2);               
    PQ_Utl.LOG(lc, '', 2);
    
    PQ_Utl.LOG(lc, 'Fin del proceso ' || TO_CHAR(SYSDATE, 'HH24:MI:SS'), 2);
    PQ_Utl.LOG(lc, '## FIN ##', 1);

    RETURN 'Se han encontrado ' || l_num_polizas || ' definitivas.';

  EXCEPTION
    WHEN no_pagos_found THEN
      PQ_Utl.LOG(lc, '*** no_pagos_found ***', 1);
      ROLLBACK;
      PQ_Err.raiser(sqlcode, l_error || ' [' || SQLERRM || ']');
      raise no_pagos_found;
      RETURN l_error || ' [' || SQLERRM || ']';
    WHEN no_polizas_found THEN
      PQ_Utl.LOG(lc, '*** no_polizas_found ***', 1);
      ROLLBACK;
      raise no_polizas_found;
      PQ_Err.raiser(sqlcode, l_error || ' [' || SQLERRM || ']');
      RETURN l_error || ' [' || SQLERRM || ']';
    WHEN OTHERS THEN
      PQ_Utl.LOG(lc,
                 'Error al generar los ficheros de load_polizas_renovables [' ||
                 sqlcode || ' - ' || SQLERRM || '] ***',
                 1);
      ROLLBACK;
      raise;
      PQ_Err.raiser(sqlcode,
                    'Error al generar los ficheros de load_polizas_definitivas' || ' [' ||
                    SQLERRM || ']');
      RETURN 'Error al generar los ficheros de load_polizas_renovables' || ' [' || SQLERRM || ']';

  END;

  FUNCTION getCuentaAsegurado(nifAsegurado     IN VARCHAR2,
                              p_codentidad     IN varchar2,
                              l_codlineaPoliza in varchar2,
                              l_ent_med        in o02agpe0.tb_colectivos.entmediadora%TYPE,
                              l_subent_med     in o02agpe0.tb_colectivos.subentmediadora%TYPE)
    RETURN array_cuentaAseg IS

    lc                 VARCHAR2(100) := 'pq_envio_Polizas_Renovables.getCuentaAsegurado';
    l_array_cuentaAseg array_cuentaAseg;

  BEGIN

    BEGIN

      PQ_Utl.LOG(lc,
                 'Buscamos por cuenta Especifica:Se busca la cuenta asociada a NIF ' ||
                 nifAsegurado || ', ENT ' || p_codentidad || ', LIN ' ||
                 l_codlineaPoliza || ', MEDIADORA ' || l_ent_med || '-' ||
                 l_subent_med,
                 2);

      select dase.iban, dase.ccc
        into l_array_cuentaAseg(0), l_array_cuentaAseg(1)
        from o02agpe0.tb_datos_asegurados dase,
             o02agpe0.tb_asegurados       aseg,
             o02agpe0.tb_usuarios         usu
       where aseg.id = dase.idasegurado
         and aseg.nifcif = nifAsegurado
         and aseg.codentidad = p_codentidad
         and aseg.codusuario = usu.codusuario
         and usu.entmediadora = l_ent_med
         and usu.subentmediadora = l_subent_med
         and dase.codlinea = l_codlineaPoliza;

    EXCEPTION
      WHEN OTHERS THEN

        BEGIN

          PQ_Utl.LOG(lc,
                     'NO Hay especifica.Buscamos por cuenta generica :Se busca la cuenta asociada a NIF ' ||
                     nifAsegurado || ', ENT ' || p_codentidad ||
                     ', LIN 999 , MEDIADORA ' || l_ent_med || '-' ||
                     l_subent_med,
                     2);

          select dase.iban, dase.ccc
            into l_array_cuentaAseg(0), l_array_cuentaAseg(1)
            from o02agpe0.tb_datos_asegurados dase,
                 o02agpe0.tb_asegurados       aseg,
                 o02agpe0.tb_usuarios         usu
           where aseg.id = dase.idasegurado
             and aseg.nifcif = nifAsegurado
             and aseg.codentidad = p_codentidad
             and aseg.codusuario = usu.codusuario
             and usu.entmediadora = l_ent_med
             and usu.subentmediadora = l_subent_med
             and dase.codlinea = 999;

        EXCEPTION
          WHEN OTHERS THEN
            PQ_Utl.LOG(lc,
                       'El asegurado no tiene cuenta asignada [' || sqlcode ||
                       ' - ' || SQLERRM || '] ***',
                       2);
            l_array_cuentaAseg(0) := NULL;
            l_array_cuentaAseg(1) := NULL;
            RETURN l_array_cuentaAseg;
        END;

    END;
    RETURN l_array_cuentaAseg;
  END getCuentaAsegurado;

  FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array IS
    i       number := 0;
    pos     number := 0;
    lv_str  varchar2(100) := in_string;
    strings t_array;
  BEGIN

    pos := instr(lv_str, delim, 1, 1);
    WHILE (pos != 0) LOOP
      i := i + 1;
      strings(i) := substr(lv_str, 1, pos - 1);
      lv_str := substr(lv_str, pos + 1, length(lv_str));
      pos := instr(lv_str, delim, 1, 1);
      If pos = 0 THEN
        strings(i + 1) := lv_str;
      END IF;
    END LOOP;

    RETURN strings;
  END SPLIT;

END pq_envio_Polizas_Renovables;
/
SHOW ERRORS;
