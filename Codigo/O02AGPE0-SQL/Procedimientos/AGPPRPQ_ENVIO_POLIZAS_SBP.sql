SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.pq_envio_polizas_sbp IS
/*******************************************************************************
     NAME:        PQ_ENVIO_POLIZAS_SBP
     PURPOSE:     Se enviaran las polizas de sobreprecio en estado 'Grabada Definitiva' (estado 2)
          Se enviaran tantos registros por poliza como cultivos contenga
          hasta un maximo de 7.
          Solo se admite una poliza de sobreprecio sobre una misma
          referencia de poliza.
          Las polizas de sobreprecio en este momento pasan al estado
          'Enviada Pendiente Aceptacion' (estado 3) hasta que se procese el fichero
          devuelto por Omega.

     REVISIONS:
     Ver        Date        Author           Description
     ---------  ----------  ---------------  ------------------------------------
     1.0        06/03/2012  Moises Perez    1. Created this package.
     2.0        28/07/2021  T-Systems	    2. P0063697 (PROD)

  ******************************************************************************/

	-------------------------------------------------------------------------------
	-- PROCEDIMIENTOS PUBLICOS
	-------------------------------------------------------------------------------

	-- Carga las polizas de sobreprecio
	FUNCTION load_polizas_sobreprecio(p_fecha IN VARCHAR2) RETURN VARCHAR2;
	-- Pinta la cadena recibida en el log independientemente de su tamanho
	FUNCTION print_log_completo(str IN VARCHAR2) RETURN VARCHAR2;
  -- Indica si la fecha actual esta dentro del intervalo comprendida entre la minima
  -- y la maxima fecha de contratacion para sobreprecio
  FUNCTION dentroFechasContratacion(p_fecha IN varchar2) RETURN VARCHAR2;

  -- Recorre todas las polizas de sobreprecio dadas de alta y actualiza los campos de tasas enviadas a Omega,
  -- comprobando si se llega a la tasa minima o no
  PROCEDURE actualizarFechasDeEnvio;
  -- Comprueba si la suma de las primas de todas las parcelas de sobreprecio de la poliza llega al minimo indicado; si no llega,
  -- aumenta proporcionalmente las primas para que lo haga
  PROCEDURE comprobarPrimaMinima (v_id_plz_sbp NUMBER, v_lineaseguroid NUMBER, v_prima_ped IN OUT VARCHAR2,
                                  v_prima_inc IN OUT VARCHAR2, v_cultivo VARCHAR2, parcial_prima IN OUT NUMBER,
                                  num_parcela_aumento_prima IN OUT NUMBER);
  -- Devuelve la consulta que obtiene formateados todos los campos del sobreprecio que hay que incluir en el fichero
  FUNCTION crearQuery (listaIdSuplemento LONG) RETURN VARCHAR2;
 	-- Compara la suma de las primas del suplemento con la de la principal asociada para comprobar si hay que
  -- pagar por el suplemento o ya se pago de mas en la principal para llegara la prima minima
  PROCEDURE comprobarPrimaPrincipal (v_ref_poliza VARCHAR2, v_id_plz_sbp NUMBER, v_prima_ped IN OUT VARCHAR2,
                                  v_prima_inc IN OUT VARCHAR2, v_lineaseguroid NUMBER,
                                  v_codcultivo o02agpe0.tb_sbp_parcelas.codcultivo%TYPE,
                                  v_escribir_linea IN OUT BOOLEAN);
  -- Actualiza las variables de configuracion correspondientes con el numero de polizas
  -- de sobreprecio pasadas a definitiva desde la ultima ejecucion del batch de envio
  PROCEDURE actualizaVarConfig(p_fecha IN varchar2);
	-- Si existe el fichero 'suplementos.txt' y no esta vacio, lee el contenido y lo devuelve
  -- l_dir es el directorio donde se encuentra el fichero 'suplementos.txt'
  FUNCTION leerFicheroSupl (l_dir tb_config_agp.agp_valor%TYPE) RETURN LONG;
  FUNCTION getFechaEfecto  RETURN DATE;
  FUNCTION getFechaEfectoSup (v_ref_poliza VARCHAR2) RETURN DATE;
  FUNCTION getFechaEfectoSupAnexoCpl (v_ref_poliza VARCHAR2) RETURN DATE;
  FUNCTION isUsuarioCorreduriaExt (v_cod_entMed o02agpe0.tb_colectivos.entmediadora%TYPE, 
  									v_cod_SubentMed o02agpe0.tb_colectivos.subentmediadora%TYPE) return NUMBER;
   
  

  FUNCTION dentroFechasEnvioSupl(p_fecha IN varchar2) RETURN VARCHAR2;
  FUNCTION dentroFechasEnvioSuplLin ( v_lineaseguroid NUMBER, p_fecha IN VARCHAR2)  RETURN BOOLEAN;

END pq_envio_polizas_sbp;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.pq_envio_polizas_sbp AS

	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION load_polizas_sobreprecio
	--
	-- Fichero de Polizas de Sobreprecio
	--
	-- Recoge las polizas de sobreprecio que han entrado en el dia y contiene los siguientes campos:
	--
	--
	-----------------------------------------------------------------------------------------------------------------------
	----- | NOMBRE              | TIPO        | CAMPO
	----- |-------------------------------|---------------------|----------------------------------------------------------
	-- 01 | No de poliza de Agroweb     | AF(8)       | TB_SBP_POLIZAS.REFERENCIA || TB_REFERENCIAS_AGRICOLAS
	-- 02 | Fecha de efecto         | N(8)        | TO_CHAR(SYSDATE, ''DDMMYYYY'')
	-- 03 | Fecha de vencimiento      | N(8)        | ???
	-- 04 | Cuenta de domiciliacion     | N(20)       | TB_DATOS_ASEGURADOS.CCC
	-- 05 | Codigo de la entidad      | N(4)        | TB_COLECTIVOS.CODENTIDAD
	-- 06 | Codigo de la oficina      | N(4)        | TB_POLIZAS.OFICINA
	-- 07 | NIF del asegurado         | AF(9)       | TB_ASEGURADOS.NIFCIF (anhadir 0 por la izquierda si no llega a longitud 9)
	-- 08 | Nombre y apellidos        | AF(100)       | TB_ASEGURADOS.NOMBRE, APELLIDO1, APELLIDO2 (anhadir espacios en blanco por la derecha hasta llegar a 100)
	-- 09 | Colectivo           | N(8)        | TB_COLECTIVOS.IDCOLECTIVO (en BD tamanho 7, anhadir 0 a la derecha o izquierda????)
	-- 10 | Linea             | N(3)        | TB_LINEAS.CODLINEA
	-- 11 | Plan              | N(4)        | TB_LINEAS.CODPLAN
	-- 12 | Cultivo             | N(3)        | TB_SBP_PARCELAS.CODCULTIVO
	-- 13 | Nombre del cultivo        | A(25)       | TB_SC_C_CULTIVOS
	-- 14 | Total produccion por cultivo  | N(10)       | TB_SBP_PARCELAS.TOTAL_PRODUCCION (en BD tamanho 12.2) ???
	-- 15 | Sobreprecio           | N(5,4)        | TB_SBP_SOBREPRECIO.SOBREPRECIO (en BD tamanho 8.2) ???
	-- 16 | Suma asegurada por cultivo    | N(10,2)       | Total de produccion POR CULTIVO * Sobreprecio
	-- 17 | Tasa de garantia de pedrisco  | N(3,2)        | TB_SBP_PARCELAS.TASA_PEDRISCO (en BD tamanho 5.2) ???
	-- 18 | Prima de garantia de pedrisco | N(10,2)       | TB_SBP_PARCELAS.PRIMA_NETA_PEDRISCO (en BD tamanho 12.2) ???
	-- 19 | Tasa de garantia de incendio  | N(3,2)        | TB_SBP_PARCELAS.TASA_INCENDIO (en BD tamanho 5.2) ???
	-- 20 | Prima de garantia de incendio | N(10,2)       | TB_SBP_PARCELAS.PRIMA_NETA_INCENDIO (en BD tamanho 12.2) ???
	-----------------------------------------------------------------------------------------------------------------------
	-- |
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------

	-- Inicio de declaracion de funcion
	FUNCTION load_polizas_sobreprecio(p_fecha IN VARCHAR2) RETURN VARCHAR2 IS

	        -- RQ.06
        type tpstrarr is varray(100000) of varchar(8);
        v_ids_polizas tpstrarr := tpstrarr();

		-- Variables generales
		lc VARCHAR2(50) := 'pq_envio_polizas_sbp.load_polizas_sobreprecio'; -- Variable que almacena el nombre del paquete y de la funcion
		TYPE tpcursor IS REF CURSOR; -- Tipo cursor
		f_fichero         utl_file.file_type; -- Variable que almacena la referencia al fichero de envio
		l_dir             tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros a enviar
		l_line            VARCHAR2(1000); -- Variables que almacena la linea que se volcara en el fichero
		l_nombre          VARCHAR2(8) := 'PLZS_SBP'; -- Nombre del fichero de envio
		l_dir_name        VARCHAR2(1000); -- Ruta fisica donde se dejan los ficheros que se van a enviar
		l_tp_cursor       tpcursor; -- Cursor para la consulta de polizas de sobreprecio
		l_num_polizas     NUMBER := 0; -- Contador para el numero de polizas enviadas
		l_num_polizas_upd NUMBER := 0; -- Contador para el numero de polizas a las que se ha cambiado el estado
		v_id_plz_sbp_ant  VARCHAR2(8); -- Variable para almacenar el id de poliza tratado en la iteracion anterior
		no_polizas_sbp_found EXCEPTION; -- Excepcion indicadora de que no hay polizas de sobreprecio para enviar
		fichero_tipo VARCHAR2(1) := 'B'; -- Letra que indica el tipo de fichero para la tabla TB_COMUNICACIONES
		tipo_mov     VARCHAR2(10) := 'ENVIO'; -- Tipo de movimiento para el fichero para la tabla TB_COMUNICACIONES

    -- Variables para componer la consulta para obtener las polizas de sobreprecio a enviar
    l_query          VARCHAR2(16500);

		-- Variables donde se volcaran los datos de los campos
		v_id_plz_sbp    VARCHAR2(8);
		v_lineaseguroid tb_lineas.lineaseguroid%TYPE;
		v_ref_poliza    VARCHAR2(8);
		v_f_efecto      VARCHAR2(8);
		v_f_vencimiento VARCHAR2(8);
		v_ccc           VARCHAR2(20);
		v_cod_entidad   VARCHAR2(4);
		v_cod_oficina   VARCHAR2(4);
		v_nif           VARCHAR2(9);
		v_nom_apellidos VARCHAR2(100);
		v_colectivo     VARCHAR2(8);
		v_linea         VARCHAR2(3);
		v_plan          VARCHAR2(4);
		v_cultivo       VARCHAR2(3);
		v_nom_cult      VARCHAR2(25);
		v_tot_prod      VARCHAR2(10);
		v_sobreprecio   VARCHAR2(9);
		v_suma_aseg     VARCHAR2(12);
		v_tasa_ped      VARCHAR2(5);
		v_prima_ped     VARCHAR2(12);
		v_tasa_inc      VARCHAR2(5);
		v_prima_inc     VARCHAR2(12);
		v_tipo_envio    NUMBER(1);
		v_tipo_envio_str    VARCHAR2(1);
		v_codUsuario    tb_polizas.codusuario%TYPE;
		v_countUsuExt       NUMBER := 0;
		-- MPM 24/05 -- Envio de correos
		f_fichero_con utl_file.file_type;
		v_linea_con   VARCHAR2(32000) := NULL;

    
		v_cod_entMed    o02agpe0.tb_colectivos.entmediadora%TYPE;
		v_cod_SubentMed o02agpe0.tb_colectivos.subentmediadora%TYPE;
		v_num_cuentaAsegurado PQ_ENVIO_POLIZAS_RENOVABLES.array_cuentaAseg;
		v_externa       o02agpe0.tb_polizas.externa%TYPE;
		v_escribir_linea BOOLEAN;
		v_txt_cod_SubentMed VARCHAR2(4);-- Para aÃ±adir al fichero con espacios


		-- Listado de ids de sobreprecios para volver a enviar como suplementos
		listaIdSuplemento LONG := NULL;
	
		parcial_prima             NUMBER(8, 2) := 0; -- Almacena la suma parcial de las tasas de todas las parcelas de una poliza de sobreprecio
		num_parcela_aumento_prima NUMBER := 0; -- Indica la parcela actual de una poliza de sobreprecio que requiere incremento en las tasas

		v_estado    o02agpe0.TB_SBP_POLIZAS.IDESTADO%TYPE;
		
		v_fecha_planif	DATE;

	BEGIN

		-- Comienzo de escritura en log
		pq_utl.log(lc, '## INI ##', 1);

		pq_utl.log(lc, 'Fecha de planificacion recibida por parametro - ' || p_fecha);

		v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');

		-- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros a enviar
		pq_utl.log(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositaran los ficheros de envio.', 2);


--!! ATENCION !!!
		--l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');


    -- Directorio de desarrollo TEST y PR
		l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');

    -- Directorio de DESARROLLO
    --l_dir := pq_utl.getcfg('DIR_IN_AGP');

		-- Se guarda el path fisico del directorio
		pq_utl.log(lc, 'Obtiene la ruta fisica del DIRECTORY.', 2);

    SELECT directory_path
			INTO l_dir_name
			FROM all_directories
		 WHERE directory_name = l_dir;


		-- Se abre el fichero de texto de salida en la ruta indicada

    pq_utl.log(lc, 'Abre el fichero de texto de salida en la ruta indicada.', 2);
		f_fichero := utl_file.fopen(location => l_dir, filename => l_nombre || '.TXT', open_mode => 'w', max_linesize => pq_typ.max_linefilesizewrite);

    -- Comprueba si se van a reenviar sobreprecios como suplementos o es una ejecucion normal
    listaIdSuplemento := leerFicheroSupl (l_dir);

    -- Actualiza las variables de configuracion correspondientes con el numero de polizas
    -- de sobreprecio pasadas a definitiva desde la ultima ejecucion del batch de envio
    -- Solo si es una ejecucion normal
    IF (listaIdSuplemento IS NULL) THEN
        actualizaVarConfig(p_fecha);
    END IF;


		-- Se compone la consulta para recuperar las polizas de sobreprecio en estado 'Grabada definitiva'
    -- Si la funcion 'leerFicheroSupl' devuelve ids, se compone la query para obtener los datos correspondientes a esos ids,
    -- si devuelve null, la compone para obtener los sobreprecios y suplementos en definitiva
		l_query:= crearQuery(listaIdSuplemento);

		-- Crea el cursor para la consulta
		pq_utl.log(lc, 'Abre el cursor para la query');

    OPEN l_tp_cursor FOR l_query;

		-- Bucle para recorrer todos los registros del cursor
		LOOP

			-- Vuelca los datos del cursor en las variables indicadas
			pq_utl.log(lc, 'Volcado de datos del cursor en variables');
			FETCH l_tp_cursor
				INTO v_id_plz_sbp, v_lineaseguroid, v_ref_poliza, v_f_efecto, v_f_vencimiento, v_ccc, v_cod_entidad,
        v_cod_oficina, v_nif, v_nom_apellidos, v_colectivo, v_linea, v_plan, v_cultivo, v_nom_cult, v_tot_prod,
        v_sobreprecio, v_suma_aseg, v_tasa_ped, v_prima_ped, v_tasa_inc, v_prima_inc, v_codUsuario, v_estado, v_tipo_envio,
        v_cod_entMed, v_cod_SubentMed, v_externa;

      -- Pinto la entidad y oficina

      pq_utl.log(lc, 'v_nom_cult = ' || v_nom_cult);
      pq_utl.log(lc, 'v_tot_prod = ' || v_tot_prod);
      pq_utl.log(lc, 'v_sobreprecio = ' || v_sobreprecio);
      pq_utl.log(lc, 'v_suma_aseg = ' || v_suma_aseg);
      pq_utl.log(lc, 'v_tasa_ped = ' || v_tasa_ped);
      pq_utl.log(lc, 'v_prima_ped = ' || v_prima_ped);
      pq_utl.log(lc, 'v_tasa_inc = ' || v_tasa_inc);
      pq_utl.log(lc, 'v_prima_inc = ' || v_prima_inc);
      pq_utl.log(lc, 'v_tipo_envio = ' || v_tipo_envio);
      pq_utl.log(lc, 'v_estado = ' || v_estado);
      pq_utl.log(lc, 'v_codUsuario = ' || v_codUsuario);
      pq_utl.log(lc, 'v_externa = ' || v_externa);


			-- Si no ha mas registros
			IF (l_tp_cursor%NOTFOUND) THEN
				-- Si el numero de polizas tratadas es cero se lanza la excepcion correspondiente
				IF (l_num_polizas = 0) THEN
					pq_utl.log(lc, 'No se han encontrado polizas de sobreprecio en estado definitiva');
					RAISE no_polizas_sbp_found;
				END IF;

				-- Si llega hasta aqui, se han tratado polizas y hay que actualizar el estado de la ultima
        -- Solo se actualiza el estado de la poliza de sobreprecio si es una ejecucion normal
        IF (listaIdSuplemento IS NULL) THEN
  				pq_utl.log(lc, 'Actualizado el estado de la poliza de sobreprecio con id - ' || v_id_plz_sbp_ant);
  				UPDATE tb_sbp_polizas
  					 SET idestado = 3
  				 WHERE id = v_id_plz_sbp_ant;

           -- TMR Insertamos en el historico de estados de poliza de sbp
           INSERT INTO TB_SBP_HISTORICO_ESTADOS (id,idpoliza_sbp,codusuario,fecha,estado)
                 values (SQ_SBP_HISTORICO_ESTADOS.nextval,v_id_plz_sbp_ant,null,v_fecha_planif,3);

                                -- RQ.06
                    v_ids_polizas.extend;
                    v_ids_polizas(v_ids_polizas.Count) := v_id_plz_sbp_ant;

          l_num_polizas_upd := l_num_polizas_upd + 1;
        END IF;

				pq_utl.log(lc, 'No hay mas registros que enviar.');

				-- Sale del bucle
				EXIT;
			END IF;

			-- Si el sobreprecio es principal necesitamos el total de prima de la poliza por si no se llega a la prima minima
			-- para que "lo que falta" hasta el minimo se distribuya uniformemente entre las parcelas del sobreprecio.
      -- Dependiendo de si es poliza principal o suplemento se enviara un caracter u otro en la linea
      IF (v_tipo_envio = 1) THEN
         comprobarPrimaMinima (v_id_plz_sbp, v_lineaseguroid, v_prima_ped, v_prima_inc , v_cultivo, parcial_prima, num_parcela_aumento_prima);
         v_tipo_envio_str := 'A';
      -- Si el sobreprecio es complementario hay que comprobar que prima se envio en el principal asociado
      -- para saber que prima tiene que enviar el suplemento
      ELSE
         -- Se comprueba si hay que modificar las primas del suplemento por no llegar a la prima minima
         v_escribir_linea := TRUE;

		 IF (v_estado = 2) THEN
            comprobarPrimaPrincipal (v_ref_poliza, v_id_plz_sbp, v_prima_ped, v_prima_inc, v_lineaseguroid, v_cultivo, v_escribir_linea);
         END IF;

         /* ESC-5816 - Se debe comprobar que el suplemento este en periodo de contratacion, sino no se envia el suplemento */

         IF (dentroFechasEnvioSuplLin(v_lineaseguroid, p_fecha) = FALSE) THEN
            pq_utl.log(lc, 'No se envia el suplemento de la poliza:' || v_ref_poliza || ' con id: ' || v_id_plz_sbp ||
                           ' y lineaSeguro: ' || v_lineaseguroid || ' por no estar dentro de la fecha de contratacion.');
            CONTINUE;
         END IF;

         IF (v_escribir_linea = FALSE) THEN
            CONTINUE;
         END IF;
		 
         v_tipo_envio_str := 'S';
      END IF;

      --  AMG 8216 las cuentas en las polizas de ACM que lo obtengan de los datos del asegurado.
      --IF ((v_ccc IS NULL) OR (longitudCCC < 20)) THEN
      PQ_Utl.LOG(lc, 'v_externa: ' || v_externa);

      IF (v_externa = 1) THEN
           DBMS_OUTPUT.PUT_LINE('Busca la cuenta del asegurado:');
           DBMS_OUTPUT.PUT_LINE('v_nif: ' || v_nif);
           DBMS_OUTPUT.PUT_LINE('v_cod_entidad: ' || v_cod_entidad);
           DBMS_OUTPUT.PUT_LINE('v_linea: ' || v_linea);
           DBMS_OUTPUT.PUT_LINE('v_cod_entMed: ' || v_cod_entMed);
           DBMS_OUTPUT.PUT_LINE('v_cod_SubentMed: ' || v_cod_SubentMed);

           PQ_Utl.LOG(lc, 'Busca la cuenta del asegurado:');
           PQ_Utl.LOG(lc, 'v_nif: ' || v_nif);
           PQ_Utl.LOG(lc, 'v_cod_entidad: ' || v_cod_entidad);
           PQ_Utl.LOG(lc, 'v_linea: ' || v_linea);
           PQ_Utl.LOG(lc, 'v_cod_entMed: ' || v_cod_entMed);
           PQ_Utl.LOG(lc, 'v_cod_SubentMed: ' || v_cod_SubentMed);

           v_num_cuentaAsegurado := PQ_ENVIO_POLIZAS_RENOVABLES.getCuentaAsegurado(v_nif,v_cod_entidad,v_linea,v_cod_entMed,v_cod_SubentMed);
           IF (v_num_cuentaAsegurado is not null AND v_num_cuentaAsegurado(1) is not null) then
              DBMS_OUTPUT.PUT_LINE('CCC encontrada: ' || v_num_cuentaAsegurado(1));
              pq_utl.log(lc, 'CCC encontrada: ' || v_num_cuentaAsegurado(1));
              v_ccc := v_num_cuentaAsegurado(1);
          ELSE
              pq_utl.log(lc, 'CUENTA NO ENCONTRADA');
          END IF;

      END IF;
      -- FIN 8216

      -- SIGPE 8014 cuando la poliza de sobreprecio este asociada a un usuario registrado como propietario en la
      -- tabla TB_CORREDURIAS_EXTERNAS, se envien en el fichero de contratacion la entidad 9996  y oficina 9903
      pq_utl.log(lc, 'INI Comprobamos si la póliza pertenece a una correduria externa');
      v_countUsuExt := isUsuarioCorreduriaExt (v_cod_entMed, v_cod_SubentMed);
      pq_utl.log(lc, 'v_countUsuExt ' || v_countUsuExt);

      IF v_countUsuExt > 0 THEN      
        pq_utl.log(lc, 'Cambiamos entidad y oficina');
        v_cod_entidad:= '9996';
        v_cod_oficina:= '9903';
      END IF;


      pq_utl.log(lc, 'FIN Comprobacion si el usuario pertenece a una correduria externa');
			pq_utl.log(lc, 'Se compone la linea para la poliza con id - ' || v_id_plz_sbp || ' y codcultivo - ' || v_cultivo);

			v_txt_cod_SubentMed := RPAD(v_cod_SubentMed, 4, ' ');
			
			-- Compone la linea que se volcara en el fichero correspondiente a un cultivo y poliza de sobreprecio
			l_line := v_ref_poliza || v_f_efecto || v_f_vencimiento || v_ccc || v_cod_entidad || v_cod_oficina || v_nif || v_nom_apellidos || v_colectivo ||
			 				v_linea || v_plan || v_cultivo || v_nom_cult || v_tot_prod || v_sobreprecio || v_suma_aseg || v_tasa_ped || v_prima_ped || v_tasa_inc ||
			  			v_prima_inc || v_tipo_envio_str || v_txt_cod_SubentMed;

   		-- Inserta en el fichero de envio la linea compuesta y el salto
	   	pq_utl.log(lc, 'Escribe la linea en el fichero de envio.');
		  utl_file.put_line(f_fichero, l_line || chr(13));

			-- Actualiza el contador de polizas enviadas
			l_num_polizas := l_num_polizas + 1;

			-- Comprobar si hay que hacer el update de la poliza de sobreprecio
			-- Si es la primera poliza que se trata, se actualiza el id de poliza anterior con el actual
			IF (v_id_plz_sbp_ant IS NULL) THEN
			  	v_id_plz_sbp_ant := v_id_plz_sbp;
			-- Si no es la primera poliza que se trata
			ELSE
			  -- Si el id de poliza anterior es diferente que el actual -> Update del id anterior
			  IF (v_id_plz_sbp_ant <> v_id_plz_sbp) THEN
			  	 pq_utl.log(lc, 'Actualizado el estado de la poliza de sobreprecio con id - ' || v_id_plz_sbp_ant);

           -- Solo se actualiza el estado de la poliza de sobreprecio si es una ejecucion normal
           IF (listaIdSuplemento IS NULL) THEN
  					  UPDATE tb_sbp_polizas
  						   SET idestado = 3
  					  WHERE id = v_id_plz_sbp_ant;

               -- Insertamos en el historico de estados de poliza de sbp
              INSERT INTO TB_SBP_HISTORICO_ESTADOS (id,idpoliza_sbp,codusuario,fecha,estado)
                  values (SQ_SBP_HISTORICO_ESTADOS.nextval,v_id_plz_sbp_ant,null,v_fecha_planif,3);
           END IF;
 -- RQ.06
                    v_ids_polizas.extend;
                    v_ids_polizas(v_ids_polizas.Count) := v_id_plz_sbp_ant;

					 l_num_polizas_upd := l_num_polizas_upd + 1;
					 v_id_plz_sbp_ant  := v_id_plz_sbp;
				END IF;
			END IF;


		END LOOP;

		-- Hace commit de los updates solo si es una ejecucion normal
    IF (listaIdSuplemento IS NULL) THEN
       pq_utl.log(lc, 'Hace commit para los updates.');
		   COMMIT;
    END IF;

    -- Cierra el cursor y el fichero de envio
		pq_utl.log(lc, 'Cierra el cursor.');
		CLOSE l_tp_cursor;


		-- Antonio Martin. Inicio de la concatenacion de ficheros

		BEGIN
			f_fichero_con := utl_file.fopen(location => l_dir, filename => 'MANOLITO.TXT', open_mode => 'r', max_linesize => pq_typ.max_linefilesizewrite);

			LOOP
				BEGIN
					-- Leo del fichero a concatenar.
					utl_file.get_line(file => f_fichero_con, buffer => v_linea_con);
					-- Escribo en el fichero abierto.
					utl_file.put_line(file => f_fichero, buffer => v_linea_con);

				EXCEPTION
					WHEN no_data_found THEN
						EXIT;
				END;
			END LOOP;

		EXCEPTION
			WHEN OTHERS THEN
      null;
				pq_utl.log(lc, 'Error en el proceso de concatenacion de ficheros ' || SQLERRM);
		END;

		-- Antonio Martin Fin concatenacion de ficheros

		pq_utl.log(lc, 'Cierra el fichero de envio.');
		utl_file.fclose(f_fichero);

		pq_utl.log(lc, 'Cierra el fichero a Concatenar.');
		utl_file.fclose(f_fichero);


		-- Realizamos una copia del fichero generado para guardarlo a modo de historico
		-- ya que el fichero 'POLIZAS_SBP.TXT' se generara cada vez.
		pq_utl.log(lc, 'Se copia el fichero generado en :' || l_dir_name || ' con nombre: ' || l_nombre || to_char(SYSDATE, 'YYMMDDHH24MISS') || '.');
		utl_file.fcopy(l_dir, l_nombre || '.TXT', l_dir, l_nombre || to_char(SYSDATE, 'YYMMDDHH24MISS') || '.TXT');

		-- Inserta en la tabla de comunicaciones un registro indicando el envio del fichero
		pq_utl.log(lc, 'Inserta en la tabla de comunicaciones un registro indicando el envio del fichero');
		INSERT INTO tb_comunicaciones
		VALUES
			(sq_comunicaciones.NEXTVAL, v_fecha_planif, l_nombre, tipo_mov, NULL, NULL, NULL, fichero_tipo, NULL);
		COMMIT;

		-- Guardamos los resultados en el fichero de log

		pq_utl.log(lc, '');
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || to_char(v_fecha_planif, 'DD/MM/YY HH24:MI:SS'), 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'Polizas de sobreprecio definitivas enviadas   := ' || l_num_polizas, 2);
		pq_utl.log(lc, 'Polizas de sobreprecio actualizadas   := ' || l_num_polizas_upd, 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, '', 2);

		pq_utl.log(lc, 'El proceso ha finalizado correctamente a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
		pq_utl.log(lc, '## FIN ##', 1);

		RETURN 'Se han encontrado ' || l_num_polizas || ' definitivas.';

		-- Control de excepciones
	EXCEPTION
		WHEN no_polizas_sbp_found THEN
			-- Rollback por si se ha hecho algun update
			ROLLBACK;
			-- Se cierra el cursor y el fichero de envio
			CLOSE l_tp_cursor;
			utl_file.fclose(f_fichero);
			-- Se escribe en el log el error
			pq_utl.log(lc, 'El proceso ha finalizado correctamente a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			pq_utl.log(lc, '## FIN ##', 1);

			RETURN 'Se han encontrado ' || l_num_polizas || ' definitivas.';

		WHEN OTHERS THEN
            -- RQ.06
            pq_utl.log(lc, 'Error al generar los ficheros de ' || to_char(v_ids_polizas.Count) || ' polizas');
            for it in 1..v_ids_polizas.Count LOOP
            	pq_utl.log(lc, 'IdPoliza = ' || to_char(v_ids_polizas(it)));
                -- Actualizar columna idstado de tb_sbp_polizas con valor "2"
                UPDATE tb_sbp_polizas SET idestado = '2' WHERE idpoliza = v_ids_polizas(it);
                -- Eliminar el ultimo registro de tb_sbp_historico_estados con idestado "3".
                DELETE FROM tb_sbp_historico_estados WHERE ROWID IN (SELECT ROWID FROM tb_sbp_historico_estados where IDPOLIZA_SBP = v_ids_polizas(it) AND estado = '3' ORDER BY ID DESC FETCH FIRST 1 ROWS ONLY);
            end LOOP;
            COMMIT;

      DBMS_OUTPUT.PUT_LINE(SQLERRM);
			-- Se escribe en el log el error
			pq_utl.log(lc, 'El proceso ha finalizado CON ERRORES a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			pq_utl.log(lc, '## FIN ##', 1);
			pq_err.raiser(SQLCODE, 'Error al generar los ficheros de load_polizas_sobreprecio' || ' [' || SQLERRM || ']');
			-- Se vuelve a lanzar la excepcion para parar la cadena
			--RAISE;
      RETURN NULL;
	END;
	-- Fin del cuerpo de la funcion



	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION load_polizas_sobreprecio
	--
	-- Escribe en el log la cadena pasa por parametro independientemente de la longitud de esta
	--
	---------------------------------------------------------------------------------------------------------------------------
	--
	-- Inicio de declaracion de funcion
	FUNCTION print_log_completo(str IN VARCHAR2) RETURN VARCHAR2 IS
		-- Variables
		str_aux VARCHAR2(10000); -- Auxiliar usada para pintar cadena en el log
	BEGIN

		str_aux := str;

		LOOP
			-- Si el tamanho de la cadena es mayor que el que permite el metodo de log, se parte y se escribe por trozos
			IF (str_aux IS NOT NULL AND length(str_aux) > 994) THEN
				pq_utl.log(null, substr(str_aux, 1, 994), 2);
				str_aux := substr(str_aux, 994);
				-- Si el tamanho de la cadena es menor, se pinta y se sale del bucle
			ELSE
				pq_utl.log(null, str_aux, 2);
				EXIT;
			END IF;
		END LOOP;

		RETURN 'OK';

		-- Control de excepciones
	EXCEPTION
		-- Si ocurre cualquier excepcion se pinta la cadena en el log directamente
		WHEN OTHERS THEN
			pq_utl.log(null, str, 2);
			RETURN 'KO';
	END;
	-- Fin del cuerpo de la funcion



  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION dentroFechasContratacion
	--
	-- Indica si la fecha actual esta dentro del intervalo comprendida entre la minima
  -- y la maxima fecha de contratacion para sobreprecio
  --
		-- Inicio de declaracion de funcion
  FUNCTION dentroFechasContratacion(p_fecha IN varchar2) RETURN VARCHAR2 IS

  l_dentro_fechas     NUMBER := 0;
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.dentroFechasContratacion'; -- Variable que almacena el nombre del paquete y de la funcion
  ultPlan o02agpe0.tb_lineas.codplan%TYPE; -- Ultimo plan dado de alta en fechas de contratacion de sobreprecio

  v_fecha_planif	DATE;

  BEGIN

	  pq_utl.log(lc, 'Fecha de planificacion recibida por parametro: ' || p_fecha);

	  v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');

  -- MPM[2014] - INI
  -- Se comprueba si estamos en fechas de contratacion para el ultimo plan dado de alta en el mto correspondiente
  -- sin tener en cuenta los cultivos
  -- Obtener el ultimo plan dado de alta en fechas de contratacion
  select max(l.codplan) into ultPlan from tb_sbp_fechas_contratacion f, tb_lineas l where f.lineaseguroid = l.lineaseguroid;

  -- Ejecuta la consulta que devuelve un 1 si la fecha actual esta dentro de la minima
  -- y la maxima fecha de contratacion
  select count(*) into l_dentro_fechas
  from (select min(fechainicio) fini, max(fechafin) ffin
          from tb_sbp_fechas_contratacion fc, tb_lineas l
          where fc.lineaseguroid = l.lineaseguroid and l.codplan = ultPlan)
  where fini <= v_fecha_planif
  and ffin >= v_fecha_planif;
  -- MPM[2014] - FIN

  pq_utl.log(lc, 'Resultado comprobacion de fechas de contratacion = ' || l_dentro_fechas, 2);

  IF (l_dentro_fechas = 1) THEN
     RETURN ('true');
  END IF;

  RETURN ('false');


  EXCEPTION
		-- Si ocurre cualquier excepcion se indica que no se esta en fechas de contratacion
		WHEN OTHERS THEN
			pq_utl.log(lc, 'Error al comprobar si estamos en fechas de contratacion', 2);
      pq_utl.log(lc, SQLERRM, 2);
			RETURN 'false';


  END;
  -- Fin del cuerpo de la funcion

  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION dentroFechasEnvioSupl
	--
	-- Indica si la linea del suplemento esta dentro del intervalo comprendida entre la minima
  -- y la maxima fecha de contratacion para sobreprecio
  --
  -- Inicio de declaracion de funcion

  FUNCTION dentroFechasEnvioSupl(p_fecha IN varchar2) RETURN VARCHAR2 IS

  l_dentro_fechas     NUMBER := 0;
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.dentroFechasEnvioSupl'; -- Variable que almacena el nombre del paquete y de la funcion
  ultPlan o02agpe0.tb_lineas.codplan%TYPE; -- Ultimo plan dado de alta en fechas de contratacion de sobreprecio

  --pq_utl.log(lc, '[INICIO] - dentro de Fechas Envio Suplementos', 2);

  v_fecha_planif	DATE;

  BEGIN

  pq_utl.log(lc, '[INICIO] DentroFechasEnvioSupl ');

  pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);

  v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');

  -- MPM[2014] - INI
  -- Se comprueba si estamos en fechas de contratacion para el ultimo plan dado de alta en el mto correspondiente
  -- sin tener en cuenta los cultivos
  -- Obtener el ultimo plan dado de alta en fechas de contratacion
  select max(l.codplan) into ultPlan
    from tb_sbp_fechas_contratacion f, tb_lineas l
  where f.lineaseguroid = l.lineaseguroid;

  pq_utl.log(lc, 'Despues de obtener el ultimo plan' || ultPlan);

  -- Ejecuta la consulta que devuelve un 1 si la fecha actual esta dentro de la minima
  -- y la maxima fecha de contratacion
  select count(*) into l_dentro_fechas
  from (select min(fechainicio) fini, max(fecha_fin_supl) ffin
          from tb_sbp_fechas_contratacion fc, tb_lineas l
         where fc.lineaseguroid = l.lineaseguroid and l.codplan = ultPlan)
  where fini <= v_fecha_planif
  and ffin >= v_fecha_planif;
  -- MPM[2014] - FIN

  pq_utl.log(lc, 'Dentro de Fechas Envio Suplementos', 2);

  pq_utl.log(lc, 'Resultado comprobacion de fechas de contratacion del Suplemento = ' || l_dentro_fechas, 2);

  IF (l_dentro_fechas = 1) THEN
    RETURN ('true');
  END IF;

  RETURN ('false');


  EXCEPTION
		-- Si ocurre cualquier excepcion se indica que no se esta en fechas de contratacion
		WHEN OTHERS THEN
			pq_utl.log(lc, 'Error al comprobar si estamos en fechas de contratacion del Suplemento', 2);
      pq_utl.log(lc, SQLERRM, 2);
			RETURN ('false');


  END;
  -- Fin del cuerpo de la funcion

  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION dentroFechasEnvioSupl
	--
	-- Indica si la linea del suplemento esta dentro del intervalo comprendida entre la minima
  -- y la maxima fecha de contratacion para sobreprecio
  --
  -- Inicio de declaracion de funcion

  FUNCTION dentroFechasEnvioSuplLin ( v_lineaseguroid NUMBER, p_fecha IN VARCHAR2)  RETURN BOOLEAN IS

  l_dentro_fechas     NUMBER := 0;
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.dentroFechasEnvioSupl'; -- Variable que almacena el nombre del paquete y de la funcion
  ultPlan o02agpe0.tb_lineas.codplan%TYPE; -- Ultimo plan dado de alta en fechas de contratacion de sobreprecio

  v_fecha_planif DATE;


  BEGIN

	 pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);

	 v_fecha_planif := TO_DATE(p_fecha, 'YYYYMMDD');

  -- MPM[2014] - INI
  -- Se comprueba si estamos en fechas de contratacion para el ultimo plan dado de alta en el mto correspondiente
  -- sin tener en cuenta los cultivos
  -- Obtener el ultimo plan dado de alta en fechas de contratacion
  select max(l.codplan) into ultPlan
    from tb_sbp_fechas_contratacion f, tb_lineas l
  where f.lineaseguroid = l.lineaseguroid;

  -- Ejecuta la consulta que devuelve un 1 si la fecha actual esta dentro de la minima
  -- y la maxima fecha de contratacion         
  select count(*) into l_dentro_fechas
  from (select min(fechainicio) fini, max(fecha_fin_supl) ffin
          from tb_sbp_fechas_contratacion fc, tb_lineas l
          where fc.lineaseguroid = v_lineaseguroid and l.codplan = ultPlan)
  where fini <= v_fecha_planif
  and ffin >= v_fecha_planif;
  -- MPM[2014] - FIN

  pq_utl.log(lc, 'Resultado comprobacion de fechas de contratacion del Suplemento = ' || l_dentro_fechas, 2);

  IF (l_dentro_fechas = 1) THEN
     RETURN TRUE;
  END IF;

  RETURN FALSE;


  EXCEPTION
		-- Si ocurre cualquier excepcion se indica que no se esta en fechas de contratacion
		WHEN OTHERS THEN
			pq_utl.log(lc, 'Error al comprobar si estamos en fechas de contratacion del Suplemento', 2);
      pq_utl.log(lc, SQLERRM, 2);
			RETURN FALSE;


  END;
  -- Fin del cuerpo de la funcion

  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- PROCEDURE actualizarFechasDeEnvio
	--
	-- Recorre todas las polizas de sobreprecio dadas de alta y actualiza los campos de tasas enviadas a Omega,
  -- comprobando si se llega a la tasa minima o no
  --
  PROCEDURE actualizarFechasDeEnvio  AS

  TYPE tpcursor IS REF CURSOR; -- Tipo cursor
  l_tp_cursor       tpcursor; -- Cursor para la consulta de polizas de sobreprecio
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.actualizarFechasDeEnvio'; -- Variable que almacena el nombre del paquete y de la funcion
  l_query VARCHAR2(1000) := 'select par.id, par.idpoliza_sbp,
                             LPAD (TRIM (TO_CHAR (PAR.PRIMA_NETA_INCENDIO, ''9999999999V99'')), 12, ''0''),
                             LPAD (TRIM (TO_CHAR (PAR.PRIMA_NETA_PEDRISCO, ''9999999999V99'')), 12, ''0''),
                             pm.prima_minima
                             from tb_sbp_parcelas par, tb_sbp_prima_minima pm, tb_sbp_polizas p
                             where par.lineaseguroid = pm.lineaseguroid and par.idpoliza_sbp = p.id and p.idestado >2
                             and par.prima_neta_incendio_env is null and par.prima_neta_pedrisco_env is null
                             order by par.idpoliza_sbp, par.id';

  -- Variables para el fetch
  v_id o02agpe0.tb_sbp_parcelas.id%type;
  v_idpoliza tb_sbp_parcelas.idpoliza_sbp%type;
  v_tasa_incendio VARCHAR2(12);
  v_tasa_pedrisco VARCHAR2(12);
  v_prima_minima o02agpe0.tb_sbp_prima_minima.prima_minima%type;

  v_prima_tot_p  NUMBER(8, 2);
  v_prima_tot_i  NUMBER(8, 2);
  v_prima_total  NUMBER(8, 2);

  v_prima_ped_numerico NUMBER(8, 2);
  v_prima_inc_numerico NUMBER(8, 2);

  num_parcelas_de_plz_sbp   NUMBER := 0;
  num_parcela_aumento_prima NUMBER := 0; -- Indica la parcela actual de una poliza de sobreprecio que requiere incremento en las tasas
  token_inc_prima           VARCHAR2(11) := '#INC_PRIMA#';

  v_pol_sbp tb_sbp_parcelas.idpoliza_sbp%type;
  aumentarPrima BOOLEAN := FALSE;

  BEGIN

  -- Crea el cursor para la consulta
	pq_utl.log(lc, 'Recorre todas las parcelas de sobreprecio');
	OPEN l_tp_cursor FOR l_query;

  LOOP

      FETCH l_tp_cursor INTO v_id, v_idpoliza, v_tasa_incendio, v_tasa_pedrisco, v_prima_minima;

      -- Si no ha mas registros
			IF (l_tp_cursor%NOTFOUND) THEN
       pq_utl.log(lc, 'Finaliza el proceso de actualizacion');
         EXIT;
      END IF;

      -- Si la poliza de sobreprecio actual hay que tratarla por ser diferente poliza que la de la
      -- iteracion anterior
      IF (v_idpoliza <> v_pol_sbp OR v_pol_sbp IS NULL) THEN

        -- Actualiza el id de poliza de sobreprecio que se esta tratando
        v_pol_sbp := v_idpoliza;

        -- Necesitamos el total de prima de la poliza por si no se llega a la prima minima
  			-- para que "lo que falta" hasta el minimo se distribuya uniformemente entre las parcelas del sobreprecio.
  			pq_utl.log(lc, 'Obtenemos el total de prima de la poliza ' || v_idpoliza || ' y el numero de parcelas que tiene.');
  			SELECT SUM(par.prima_neta_incendio), SUM(par.prima_neta_pedrisco), COUNT(*)
  				INTO v_prima_tot_i, v_prima_tot_p, num_parcelas_de_plz_sbp
  				FROM tb_sbp_parcelas par
  			 WHERE par.idpoliza_sbp = v_idpoliza;

         -- Se muestra la prima total de la poliza de sobreprecio
        v_prima_total := v_prima_tot_i + v_prima_tot_p;
  			pq_utl.log(lc, 'Prima total=' || v_prima_total || ', prima_incendio_total=' || v_prima_tot_i || ', prima_pedrisco_total=' || v_prima_tot_p ||	' ---');

        -- Si la prima total no alcanza a la prima minima configurada para el plan/linea de la poliza
        IF (v_prima_minima > v_prima_total) THEN

          -- Se muestra el mensaje informativo en el log cuando se trate la primera parcela de la poliza
          aumentarPrima := TRUE;
          num_parcela_aumento_prima := 0;

        pq_utl.log(lc, token_inc_prima || ' - INICIO -');
        pq_utl.log(lc, token_inc_prima || ' La prima total de las ' || num_parcelas_de_plz_sbp || ' parcelas de la poliza no llega a ' || v_prima_minima);           
        pq_utl.log(lc, token_inc_prima || ' Se incrementaran las primas de todas las parcelas para llegar al minimo.');

        ELSE
        	 aumentarPrima := FALSE;
        END IF;

      END IF;

     -- Tratamiento de las parcelas
     IF (aumentarPrima = FALSE) THEN
              -- Si no hay que aumentar las primas se actualizan directamente las tasas enviadas
              UPDATE TB_SBP_PARCELAS SPAR
              SET SPAR.PRIMA_NETA_INCENDIO_ENV= SPAR.PRIMA_NETA_INCENDIO,
              SPAR.PRIMA_NETA_PEDRISCO_ENV= SPAR.PRIMA_NETA_PEDRISCO
              WHERE SPAR.ID = v_id;
     ELSE
         -- Se incrementan las tasas de la parcela actual
         -- Se actualiza la variable que indica que parcela de la poliza se esta tratando
        num_parcela_aumento_prima := num_parcela_aumento_prima + 1;
      pq_utl.log(lc, token_inc_prima || ' Parcela numero: ' || num_parcela_aumento_prima);

      pq_utl.log(lc, token_inc_prima || ' Prima de pedrisco anterior al incremento: ' || v_tasa_pedrisco);
      pq_utl.log(lc, token_inc_prima || ' Prima de incendio anterior al incremento: ' || v_tasa_incendio);

				--actualizamos el valor para las primas de pedrisco e incendio para que el total sea el minimo
        v_prima_ped_numerico := round((((to_number(v_tasa_pedrisco) / 100) * v_prima_minima) / v_prima_total), 2);
				v_prima_inc_numerico := round((((to_number(v_tasa_incendio) / 100) * v_prima_minima) / v_prima_total), 2);

				pq_utl.log(lc, token_inc_prima || ' Prima de pedrisco posterior al incremento: ' || v_prima_ped_numerico);
      pq_utl.log(lc, token_inc_prima || ' Prima de incendio posterior al incremento: ' || v_prima_inc_numerico);

        -- Se actualiza el registro de parcela indicando las primas de incendio y pedrisco que se envian
        UPDATE o02agpe0.TB_SBP_PARCELAS PAR
        SET PAR.PRIMA_NETA_INCENDIO_ENV=v_prima_inc_numerico, PAR.PRIMA_NETA_PEDRISCO_ENV=v_prima_ped_numerico
        WHERE PAR.ID=v_id;

     END IF;

     COMMIT;


  END LOOP;


  CLOSE l_tp_cursor;


  END;


  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- PROCEDURE comprobarPrimaMinima
	--
	-- Comprueba si la suma de las primas de todas las parcelas de sobreprecio de la poliza llega al minimo indicado; si no llega,
  -- aumenta proporcionalmente las primas para que lo haga
  --
  procedure comprobarPrimaMinima (v_id_plz_sbp NUMBER, v_lineaseguroid NUMBER, v_prima_ped IN OUT VARCHAR2,
                                  v_prima_inc IN OUT VARCHAR2, v_cultivo VARCHAR2, parcial_prima IN OUT NUMBER,
                                  num_parcela_aumento_prima IN OUT NUMBER) as

  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.comprobarPrimaMinima'; -- Variable que almacena el nombre del paquete y de la funcion
	v_prima_tot_p  NUMBER(8, 2);
	v_prima_tot_i  NUMBER(8, 2);
  v_prima_total  NUMBER(8, 2);
  v_prima_minima NUMBER(8, 2);

  /* ESC-5816 */
  v_dif_primas          NUMBER(8, 2);
  v_max_prima_inc       NUMBER(8, 2);
  v_cult_max_prima_inc  VARCHAR2(3);
  v_max_prima_ped       NUMBER(8, 2);
  v_cult_max_prima_ped  VARCHAR2(3);
  v_prima_final         NUMBER(8, 2);
  /* ESC-5816 */

  num_parcelas_de_plz_sbp   NUMBER := 0; -- Indica el numero de parcelas que tiene asociada una poliza de sobreprecio

  token_inc_prima           VARCHAR2(11) := '#INC_PRIMA#';  -- Token que se incluye al pintar las lineas en el log en el apartado de incremeto de primas
  token_inc_prima_final     VARCHAR2(7) := '#FINAL#';

  v_prima_ped_numerico      NUMBER(8, 2) := 0; -- Almacena la tasa de pedrisco incrementada de la parcela en formato numerico
  v_prima_inc_numerico      NUMBER(8, 2) := 0; -- Almacena la tasa de incendio incrementada de la parcela en formato numerico

  begin

      -- Necesitamos el total de prima de la poliza por si no se llega a la prima minima
			-- para que "lo que falta" hasta el minimo se distribuya uniformemente entre las parcelas del sobreprecio.
			pq_utl.log(lc, 'Obtenemos el total de prima de la poliza ' || v_id_plz_sbp || '.');
			SELECT SUM(par.prima_neta_incendio), SUM(par.prima_neta_pedrisco)
				INTO v_prima_tot_i, v_prima_tot_p
				FROM tb_sbp_parcelas par
			 WHERE par.idpoliza_sbp = v_id_plz_sbp;

			v_prima_total := v_prima_tot_i + v_prima_tot_p;
			pq_utl.log(lc, 'Prima total=' || v_prima_total || ', prima_incendio_total=' || v_prima_tot_i || ', prima_pedrisco_total=' || v_prima_tot_p ||	' ---');

      -- Obtenemos la prima minima configurada para esta linea
			SELECT pm.prima_minima
				INTO v_prima_minima
				FROM tb_sbp_prima_minima pm
			 WHERE pm.lineaseguroid = v_lineaseguroid;

      -- Si la suma de primas de todas las parcelas de una poliza no supera la prima minima
			IF (v_prima_total < v_prima_minima) THEN

        -- Obtiene el numero de lineas que se incluiran en el fichero de envio para esta poliza
        SELECT COUNT(distinct(par.codcultivo))
        INTO num_parcelas_de_plz_sbp
        FROM tb_sbp_parcelas par
			  WHERE par.idpoliza_sbp = v_id_plz_sbp;

		-- Se muestra el mensaje informativo en el log cuando se trate la primera parcela de la poliza
        IF (num_parcela_aumento_prima = 0) THEN
           pq_utl.log(lc, token_inc_prima || ' - INICIO -');
           pq_utl.log(lc, token_inc_prima || ' La prima total de las ' || num_parcelas_de_plz_sbp || ' lineas de envio de la poliza no llega a ' || v_prima_minima);
           pq_utl.log(lc, token_inc_prima || ' Se incrementaran las primas de todas las parcelas para llegar al minimo.');
        END IF;

        -- MPM 14/06
        -- Se actualiza la variable que indica que parcela de la poliza se esta tratando
        num_parcela_aumento_prima := num_parcela_aumento_prima + 1;
        pq_utl.log(lc, token_inc_prima || ' Linea de envio numero: ' || num_parcela_aumento_prima);

        pq_utl.log(lc, token_inc_prima || ' Prima de pedrisco anterior al incremento: ' || v_prima_ped);
        pq_utl.log(lc, token_inc_prima || ' Prima de incendio anterior al incremento: ' || v_prima_inc);

				--actualizamos el valor para las primas de pedrisco e incendio para que el total sea el minimo
        v_prima_ped_numerico := round((((to_number(v_prima_ped) / 100) * v_prima_minima) / v_prima_total), 2);
		v_prima_inc_numerico := round((((to_number(v_prima_inc) / 100) * v_prima_minima) / v_prima_total), 2);

		pq_utl.log(lc, token_inc_prima || ' Prima de pedrisco posterior al incremento: ' || v_prima_ped_numerico);
        pq_utl.log(lc, token_inc_prima || ' Prima de incendio posterior al incremento: ' || v_prima_inc_numerico);

        -- Se actualizan los registros de todas las parcelas de la poliza que tengan el mismo cultivo
        -- indicando las primas de incendio y pedrisco que se envian
        UPDATE o02agpe0.TB_SBP_PARCELAS PAR
        SET PAR.PRIMA_NETA_INCENDIO_ENV=v_prima_inc_numerico, PAR.PRIMA_NETA_PEDRISCO_ENV=v_prima_ped_numerico
        WHERE PAR.IDPOLIZA_SBP=v_id_plz_sbp and par.codcultivo=v_cultivo;

        COMMIT;


        -- Se actualiza la variable que almacena la suma parcial de todas las primas de las parcelas de la poliza
        parcial_prima := parcial_prima + v_prima_ped_numerico + v_prima_inc_numerico;
        -- Si la actual es la ultima parcela a tratar de la poliza, se comprueba que la suma parcial de todas las primas es igual
        -- o mayor que la prima minima. Si no, se suma la diferencia en la tasa de incendio de la ultima parcela.
        IF (num_parcela_aumento_prima = num_parcelas_de_plz_sbp) THEN
           IF ((v_prima_minima - parcial_prima)>0) THEN
              pq_utl.log(lc, token_inc_prima || token_inc_prima_final || ' Se ha procesado la ultima linea de envio y la suma de las primas no llega a ' || v_prima_minima);
              pq_utl.log(lc, token_inc_prima || token_inc_prima_final || ' Se incrementa la prima de pedrisco de la ultima linea de envio en ' || (v_prima_minima - parcial_prima));

              v_prima_ped_numerico := v_prima_ped_numerico + (v_prima_minima - parcial_prima);
             /*ESC-5816 */
             /* Calculamos la diferencia, y si la diferencia es menor a 1, se asignara la propia diferencia a la prima de mayor valor */
             v_dif_primas := v_prima_minima - parcial_prima;

             IF (v_dif_primas < 1) THEN

                 SELECT distinct par.prima_neta_incendio_env, par.codcultivo
                   INTO v_max_prima_inc, v_cult_max_prima_inc
                   FROM o02agpe0.tb_sbp_parcelas par
                  WHERE rownum = 1 and par.idpoliza_sbp = v_id_plz_sbp
                    AND par.prima_neta_incendio_env in (select max(par.prima_neta_incendio_env)
                                                          FROM o02agpe0.tb_sbp_parcelas par
                                                         WHERE par.idpoliza_sbp = v_id_plz_sbp);

                 SELECT distinct par.prima_neta_pedrisco_env, par.codcultivo
                   INTO v_max_prima_ped, v_cult_max_prima_ped
                   FROM o02agpe0.tb_sbp_parcelas par
                  WHERE rownum = 1 and par.idpoliza_sbp = v_id_plz_sbp
                    AND par.prima_neta_pedrisco_env in (select max(par.prima_neta_pedrisco_env)
                                                          FROM o02agpe0.tb_sbp_parcelas par
                                                         WHERE par.idpoliza_sbp = v_id_plz_sbp);

                 IF (v_max_prima_inc > v_max_prima_ped) THEN
                    v_prima_final := v_max_prima_inc + v_dif_primas;

                    UPDATE o02agpe0.TB_SBP_PARCELAS PAR
                       SET PAR.PRIMA_NETA_INCENDIO_ENV=v_prima_final
                     WHERE PAR.IDPOLIZA_SBP=v_id_plz_sbp
                       AND PAR.codcultivo=v_cult_max_prima_inc;
                 ELSE
                    v_prima_final := v_max_prima_ped + v_dif_primas;

                    UPDATE o02agpe0.TB_SBP_PARCELAS PAR
                       SET PAR.PRIMA_NETA_PEDRISCO_ENV=v_prima_final
                     WHERE PAR.IDPOLIZA_SBP=v_id_plz_sbp
                       AND PAR.codcultivo=v_cult_max_prima_ped;

                 END IF;
              END IF;
           END IF;
           /* FIN ESC-5816 */

           -- Como se han tratado todas las parcelas de la poliza, se resetea el indicador de parcela y la prima parcial          
           num_parcela_aumento_prima := 0;
           parcial_prima := 0;

           pq_utl.log(lc, token_inc_prima || ' - FIN -');
        END IF;

        -- Formatea los valores de las primas
        v_prima_ped := lpad(TRIM(to_char(v_prima_ped_numerico, '9999999999V99')), 12, '0');
        v_prima_inc := lpad(TRIM(to_char(v_prima_inc_numerico, '9999999999V99')), 12, '0');


      -- Si la suma de primas sobrepasa la prima minima
      ELSE

        -- Se actualizan los registros de todas las parcelas de la poliza que tengan el mismo cultivo
        -- indicando las primas de incendio y pedrisco que se envian
        UPDATE o02agpe0.TB_SBP_PARCELAS PAR
        SET PAR.PRIMA_NETA_INCENDIO_ENV=(to_number(v_prima_inc)/100), PAR.PRIMA_NETA_PEDRISCO_ENV=(to_number(v_prima_ped)/100)
        WHERE PAR.IDPOLIZA_SBP=v_id_plz_sbp and par.codcultivo=v_cultivo;

        COMMIT;


			END IF;



  end; -- Fin comprobacion de prima minima


  -- FUNCTION getFechaEfecto
	--
	-- Devuelve la fecha de efecto configurada en la tabla TB_CONFIG_AGP o la fecha actual si esta no existe o es no valida
  --
  FUNCTION getFechaEfecto  RETURN DATE IS

  v_fecEfecto DATE;
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.getFechaEfecto';

  BEGIN

  select TO_DATE (c.agp_valor, 'DD/MM/YYYY') into v_fecEfecto from o02agpe0.tb_config_agp c where c.agp_nemo like 'ENVIO_SBP_FECHA_EFECTO';
  pq_utl.log(lc, 'La fecha de efecto es ' || v_fecEfecto, 2);
  return v_fecEfecto;

  EXCEPTION

           WHEN OTHERS THEN
                pq_utl.log(lc, 'La fecha de efecto no esta configurada, se usa la fecha actual.', 2);
                RETURN SYSDATE;

  END;

  -- MPM - Obtiene la fecha correspondiente al envio de la poliza de sobreprecio principal asociada al suplemento que se esta tratando
  FUNCTION getFechaEfectoSup (v_ref_poliza VARCHAR2) RETURN DATE IS
  id_sbp number;
  v_fecEfecto DATE;
  v_ref_polizaF varchar2(10);
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.getFechaEfecto';

  BEGIN

        BEGIN
             v_ref_polizaF := SUBSTR (v_ref_poliza, 1, 7);
             pq_utl.log(lc, v_ref_polizaF, 2);
             select polsbp.id into id_sbp from o02agpe0.tb_sbp_polizas polsbp where polsbp.referencia = v_ref_polizaF and polsbp.idestado = 5 and polsbp.tipoenvio=1;

        EXCEPTION
        WHEN OTHERS THEN
             pq_utl.log(lc, 'Error al sacar el id de la poliza de sobreprecio ', 2);
             RETURN SYSDATE;

        END;
        BEGIN
             select max(fecha)into v_fecEfecto from o02agpe0.tb_sbp_historico_estados hist where idpoliza_sbp = id_sbp and hist.estado=5;
        EXCEPTION
        WHEN OTHERS THEN
           pq_utl.log(lc, 'Error al sacar la fecha de efecto la poliza de sobreprecio ', 2);
             RETURN SYSDATE;

        END;
pq_utl.log(lc, 'La fecha de efecto es ' || v_fecEfecto, 2);
  return v_fecEfecto;

  EXCEPTION

           WHEN OTHERS THEN
              pq_utl.log(lc, 'La fecha de efecto no esta configurada, se usa la fecha actual.', 2);
                RETURN SYSDATE;

  END;


  -- MPM - Sigpe 8252
  -- Obtiene la fecha de envio a Agroseguro de la poliza complementaria o del anexo de modificacion que
  -- ha provocado la generacion del suplemento que se esta tratando
  FUNCTION getFechaEfectoSupAnexoCpl (v_ref_poliza VARCHAR2) RETURN DATE IS

  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.getFechaEfectoSupAnexoCpl';
  v_ref_polizaF varchar2(10);
  v_fecEfecto DATE := NULL;

  BEGIN

  v_ref_polizaF := SUBSTR (v_ref_poliza, 1, 7);

pq_utl.log(lc, 'Obtener la fecha de efecto para el suplemento de la poliza ' || v_ref_polizaF, 2);

  -- Comprobacion de fecha por contratacion de complementaria
pq_utl.log(lc, 'Comprueba si el suplemento se ha generado por contratacion de complementaria.', 2);

  BEGIN
     select max (p.fechaenvio) into v_fecEfecto
      from o02agpe0.tb_polizas p
     where p.tiporef = 'C'
       and p.idestado = 8
       and p.referencia = v_ref_polizaF
     group by p.referencia;

  EXCEPTION WHEN NO_DATA_FOUND THEN
     null;
     pq_utl.log(lc, 'No se ha encontrado fecha por contratacion de complementaria.', 2);
  END;

  IF (v_fecEfecto IS NOT NULL) THEN RETURN v_fecEfecto; END IF;
  
  -- Comprobacion de fecha por contratacion de anexo de modificacion
  pq_utl.log(lc, 'Comprueba si el suplemento se ha generado por contratacion de anexo de modificacion.', 2);

  BEGIN
    select max (a.fecha) into v_fecEfecto
     from o02agpe0.tb_anexo_mod_cupon a
    where a.estado in (6,7)
     and a.referencia= v_ref_polizaF
    group by a.referencia;

  EXCEPTION WHEN NO_DATA_FOUND THEN
     null;
     pq_utl.log(lc, 'No se ha encontrado fecha por contratacion de anexo de modificacion.', 2);
  END;

  IF (v_fecEfecto IS NOT NULL) THEN RETURN v_fecEfecto; END IF;

  -- Si no se ha encontrado fecha se devuelve la actual
  IF (v_fecEfecto IS NULL) THEN RETURN SYSDATE; END IF;


  EXCEPTION

     WHEN OTHERS THEN
          dbms_output.put_line (SQLCODE);
        pq_utl.log(lc, 'Ha ocurrido algun error al obtener la fecha de envio de cpl o am asociado al anexo, se usa la fecha actual', 2);
          RETURN SYSDATE;
  END;
 ----------------------------------------------------------------------------------------------------------


 FUNCTION isUsuarioCorreduriaExt (v_cod_entMed IN o02agpe0.tb_colectivos.entmediadora%TYPE, 
  									v_cod_SubentMed IN o02agpe0.tb_colectivos.subentmediadora%TYPE) return NUMBER is

  v_count  NUMBER := 0;
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.isUsuarioCorreduriaExt';
  v_codInterno VARCHAR2(12) := v_cod_entMed || '000' || v_cod_SubentMed;

  BEGIN
  pq_utl.log(lc, 'INI isUsuarioCorreduriaExt ');
  
   
   select count(*) into v_count
   from TB_CORREDURIAS_EXTERNAS e
   where e.cod_interno = v_codInterno;

   pq_utl.log(lc, 'FIN isUsuarioCorreduriaExt ', v_count);

   RETURN v_count;
    EXCEPTION
         WHEN NO_DATA_FOUND THEN
              RETURN 0;
   END isUsuarioCorreduriaExt;





  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION crearQuery
	--
	-- Devuelve la consulta que obtiene formateados todos los campos del sobreprecio que hay que incluir en el fichero
  --
  FUNCTION crearQuery (listaIdSuplemento LONG) RETURN VARCHAR2 IS

  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.crearQuery'; -- Variable que almacena el nombre del paquete y de la funcion

  -- Variables para componer la consulta para obtener las polizas de sobreprecio a enviar
  l_query           VARCHAR2(16500);
  l_query_select    VARCHAR2(6000);
   --ESC-32546
  l_query_select_sup VARCHAR2(6000);
   --ESC-32546
  l_query_from      VARCHAR2(4000);
  l_query_order     VARCHAR2(3000);
  l_query_groupby   VARCHAR2(3500);

  BEGIN

  pq_utl.log(lc, 'Monta la query para recuperar las polizas de sobreprecio definitivas ', 2);

		l_query_select := 'SELECT TO_CHAR(PO.ID) id,
                       P.LINEASEGUROID,
                       P.REFERENCIA || P.DC,
					   (select TO_CHAR(max(hist.fecha) + 1, ''DDMMYYYY'') from o02agpe0.tb_sbp_historico_estados hist where hist.idpoliza_sbp = TO_CHAR(PO.ID) and hist.estado=2),
					   TO_CHAR(FC.FECHA_FIN_GARANTIA, ''DDMMYYYY''),
                       PAG.CCCBANCO,
                       LPAD(TRIM(TO_CHAR(C.CODENTIDAD, 9999)), 4, 0),
                       LPAD(TRIM(TO_CHAR(P.OFICINA, 9999)), 4, 0),
                       LPAD(AG.NIFCIF, 9, ''0''),
                       RPAD(TRIM (DECODE (NOMBRE, NULL,RAZONSOCIAL, AG.NOMBRE || '' '' || AG.APELLIDO1 || '' '' || AG.APELLIDO2)), 100, '' ''),
                       RPAD(C.IDCOLECTIVO, 7, '' '') || C.DC,
                       LPAD(trim(TO_CHAR(L.CODLINEA, ''999'')), 3, '' ''),
                       LPAD(trim(TO_CHAR(L.CODPLAN, ''9999'')), 4, '' ''),
                       LPAD(TRIM(TO_CHAR(PA.CODCULTIVO, 999)), 3, 0),
                       RPAD(CU.DESCULTIVO, 25, '' ''),
                       LPAD(TRIM(TO_CHAR( SUM (PA.TOTAL_PRODUCCION), ''9999999999'')), 10, ''0''),
                       LPAD(TRIM(TO_CHAR(PA.SOBREPRECIO, ''99999V9999'')), 9, ''0''),
                       LPAD(TRIM(TO_CHAR(SUM(PA.TOTAL_PRODUCCION * PA.SOBREPRECIO), ''9999999999V99'')), 12, ''0''),
                       ''00001'',
                       LPAD(TRIM(TO_CHAR(SUM (PA.PRIMA_NETA_PEDRISCO), ''9999999999V99'')), 12, ''0''),
                       ''00001'',
                       LPAD(TRIM(TO_CHAR(SUM (PA.PRIMA_NETA_INCENDIO), ''9999999999V99'')), 12, ''0''),
                       P.CODUSUARIO,
                       PO.IDESTADO ';
     --ESC-32546
     l_query_select_sup := 'SELECT TO_CHAR(PO.ID) id,
                       P.LINEASEGUROID,
                       P.REFERENCIA || P.DC,
					   (select TO_CHAR(max(hist.fecha), ''DDMMYYYY'') from o02agpe0.tb_sbp_historico_estados hist where hist.idpoliza_sbp = TO_CHAR(PO.ID) and hist.estado=2),
					   TO_CHAR(FC.FECHA_FIN_GARANTIA, ''DDMMYYYY''),
                       PAG.CCCBANCO,
                       LPAD(TRIM(TO_CHAR(C.CODENTIDAD, 9999)), 4, 0),
                       LPAD(TRIM(TO_CHAR(P.OFICINA, 9999)), 4, 0),
                       LPAD(AG.NIFCIF, 9, ''0''),
                       RPAD(TRIM (DECODE (NOMBRE, NULL,RAZONSOCIAL, AG.NOMBRE || '' '' || AG.APELLIDO1 || '' '' || AG.APELLIDO2)), 100, '' ''),
                       RPAD(C.IDCOLECTIVO, 7, '' '') || C.DC,
                       LPAD(trim(TO_CHAR(L.CODLINEA, ''999'')), 3, '' ''),
                       LPAD(trim(TO_CHAR(L.CODPLAN, ''9999'')), 4, '' ''),
                       LPAD(TRIM(TO_CHAR(PA.CODCULTIVO, 999)), 3, 0),
                       RPAD(CU.DESCULTIVO, 25, '' ''),
                       LPAD(TRIM(TO_CHAR( SUM (PA.TOTAL_PRODUCCION), ''9999999999'')), 10, ''0''),
                       LPAD(TRIM(TO_CHAR(PA.SOBREPRECIO, ''99999V9999'')), 9, ''0''),
                       LPAD(TRIM(TO_CHAR(SUM(PA.TOTAL_PRODUCCION * PA.SOBREPRECIO), ''9999999999V99'')), 12, ''0''),
                       ''00001'',
                       LPAD(TRIM(TO_CHAR(SUM (PA.PRIMA_NETA_PEDRISCO), ''9999999999V99'')), 12, ''0''),
                       ''00001'',
                       LPAD(TRIM(TO_CHAR(SUM (PA.PRIMA_NETA_INCENDIO), ''9999999999V99'')), 12, ''0''),
                       P.CODUSUARIO,
                       PO.IDESTADO ';
     --ESC-32546                 

     l_query_from := ' FROM o02agpe0.TB_SBP_POLIZAS        PO,
                       o02agpe0.TB_SBP_PARCELAS            PA,
                       o02agpe0.TB_POLIZAS                 P,
                       o02agpe0.TB_ASEGURADOS              AG,
                       o02agpe0.TB_LINEAS                  L,
                       o02agpe0.TB_PAGOS_POLIZA            PAG,
                       o02agpe0.TB_COLECTIVOS              C,                   
                       o02agpe0.TB_SC_C_CULTIVOS           CU,
                       o02agpe0.TB_SBP_FECHAS_CONTRATACION FC
                 WHERE PO.ID = PA.IDPOLIZA_SBP
				   AND P.IDASEGURADO = AG.ID
                   AND PA.LINEASEGUROID = L.LINEASEGUROID
                   AND PAG.IDPOLIZA = PO.IDPOLIZA
                   AND P.IDCOLECTIVO = C.ID
                   AND PA.CODCULTIVO = CU.CODCULTIVO
                   AND PA.LINEASEGUROID = CU.LINEASEGUROID
                   AND L.LINEASEGUROID = FC.LINEASEGUROID
                   AND PA.CODCULTIVO = FC.CODCULTIVO ';

    l_query_order := ' ORDER BY ID ';

    l_query_groupby := ' group by TO_CHAR(PO.ID), P.LINEASEGUROID, (P.REFERENCIA || P.DC),FC.FECHA_FIN_GARANTIA,PAG.CCCBANCO,C.CODENTIDAD,P.OFICINA,
   AG.NIFCIF, TRIM (DECODE (NOMBRE, NULL,RAZONSOCIAL, AG.NOMBRE || '' '' || AG.APELLIDO1 || '' '' || AG.APELLIDO2)), RPAD(C.IDCOLECTIVO, 7, '' '') || C.DC,
   L.CODLINEA,L.CODPLAN,PA.CODCULTIVO,CU.DESCULTIVO,PA.SOBREPRECIO,PO.TIPOENVIO,P.CODUSUARIO,C.ENTMEDIADORA,C.SUBENTMEDIADORA,P.EXTERNA,PO.IDESTADO ';

    -- Si 'listaIdSuplemento' es nulo, obtienen los sobreprecios y suplementos en definitiva
     IF (listaIdSuplemento IS NULL) THEN
        l_query:= -- Select para polizas principales
             l_query_select || ' , PO.TIPOENVIO ' || ' , C.ENTMEDIADORA ' || ' , C.SUBENTMEDIADORA ' || ' , P.EXTERNA ' ||
             -- From para polizas principales
             l_query_from ||
             -- Where para polizas principales
             ' AND PO.IDESTADO = 2 AND PO.IDPOLIZA = P.IDPOLIZA AND PO.TIPOENVIO= 1 and p.idestado in (3,5,8) ' ||
             -- Group by para polizas principales
             l_query_groupby ||
             -- Select para suplementos
             ' UNION ' ||
             --ESC-32546
             l_query_select_sup || ' , PO.TIPOENVIO ' || ' , C.ENTMEDIADORA ' || ' , C.SUBENTMEDIADORA ' || ' , P.EXTERNA ' ||
             --ESC-32546
             -- From para suplementos
             l_query_from ||
             -- Where para suplementos
             -- MPM[2014] - INI
               -- Se envian los suplementos en definitiva y erroneos
               -- Ahora los suplementos se pueden haber generado por complementaria o por anexo de modificacion,
               -- por lo que se cambia el join con la tabla de polizas del campo de id de cpl al de id de poliza
               ' AND (PO.IDESTADO = 2 OR PO.IDESTADO = 4) AND po.idpoliza = p.idpoliza AND PO.TIPOENVIO= 2 AND P.IDESTADO=8 ' ||
             -- MPM[2014] - INI
             -- Group by para polizas principales
             l_query_groupby ||
             -- Order general
             l_query_order;
     -- Si 'listaIdSuplemento' viene informado se fija el tipo de envio a 2 (suplementos)
     ELSE
         l_query:= -- Select para polizas principales
                   l_query_select || ', 2 ' || ' , C.ENTMEDIADORA ' || ' , C.SUBENTMEDIADORA ' || ' , P.EXTERNA ' ||
                   -- From para polizas principales
                   l_query_from ||
                   ' AND PO.ID IN (' || listaIdSuplemento || ') AND PO.IDPOLIZA = P.IDPOLIZA ' ||
                   l_query_groupby ||
                   -- Order general
                   l_query_order;
     END IF;

		-- Si la query es mas larga que lo que permite el metodo de log, se mete en bucle para escribirla completamente
		pq_utl.log(lc, 'Query completa en log -> ' || (print_log_completo(l_query)));

    RETURN l_query;

  END;


  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION comprobarPrimaPrincipal
	--
	-- Compara la suma de las primas del suplemento con la de la principal asociada para comprobar si hay que
  -- pagar por el suplemento o ya se pago de mas en la principal para llegara la prima minima
  --
  procedure comprobarPrimaPrincipal (v_ref_poliza VARCHAR2, v_id_plz_sbp NUMBER, v_prima_ped IN OUT VARCHAR2,
                                  v_prima_inc IN OUT VARCHAR2, v_lineaseguroid NUMBER,
                                  v_codcultivo o02agpe0.tb_sbp_parcelas.codcultivo%TYPE,
                                  v_escribir_linea IN OUT BOOLEAN) as

  v_prima_total_suplemento NUMBER(10, 2);
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.comprobarPrimaPrincipal'; -- Variable que almacena el nombre del paquete y de la funcion

  v_prima_ped_aux NUMBER(12, 2);
  v_prima_inc_aux NUMBER(12, 2);
  v_prima_minima_aux NUMBER(12, 4);

  v_num_parc_supl NUMBER(2);

  v_prima_minima o02agpe0.tb_sbp_prima_minima.prima_minima%TYPE;

  v_prima_enviada_supl o02agpe0.tb_sbp_parcelas.prima_neta_incendio_env%TYPE;

  BEGIN

  -- SIGPE 8252 - MPM - Obtiene la prima minima configurada para la linea a la que pertenece el suplemento
  SELECT pm.prima_minima
		INTO v_prima_minima
		FROM tb_sbp_prima_minima pm
		WHERE pm.lineaseguroid = v_lineaseguroid;

  -- Obtiene la prima total que se va a enviar en el suplemento
  select sum(suma), count(*)
  into v_prima_total_suplemento, v_num_parc_supl
  from (
  select sum(spar.prima_neta_incendio) + sum(spar.prima_neta_pedrisco) suma
   from o02agpe0.tb_sbp_parcelas spar
  where spar.idpoliza_sbp = v_id_plz_sbp
  group by spar.codcultivo);

  -- SIGPE 8252 - MPM - Comprueba si esta parcela del suplemento ya se ha tratado en iteraciones anteriores
  select sum (spar.prima_neta_incendio_env)
   into v_prima_enviada_supl
  from o02agpe0.tb_sbp_parcelas spar
   where spar.idpoliza_sbp = v_id_plz_sbp
   and spar.codcultivo=v_codcultivo;

   pq_utl.log(lc, 'Prima del suplemento enviada anteriormente ' || v_prima_enviada_supl);

   IF (v_prima_enviada_supl IS NOT NULL) THEN
      v_escribir_linea := FALSE;
      RETURN;
   END IF;

  -- Si la diferencia entre lo que se envio en la principal y lo calculado en el suplemento es positiva
  -- significa que lo que se pago de mas en la principal cubre lo incluido en el suplemento. Hay que enviar como
  -- prima del suplemento lo mismo que se envio en la principal para que al contratarlo se reste la prima del suplemento
  -- de la principal y sea 0 para que no se cobre nada al cliente.
  --
  -- Si la prima total del suplemento no llega a la prima minima configurada para la linea
  -- hay que incrementar las primas de las parcelas del suplemento para que llegue a la minima
  -- y asi no se cobre nada o se genere la devolucion correspondiente, dependiendo si la
  -- poliza ya era de prima minima o no 
  IF (v_prima_minima is not null and (v_prima_minima > v_prima_total_suplemento)) THEN

	  pq_utl.log(lc, 'La prima del suplemento no llega al minimo');
	  pq_utl.log(lc, 'Prima minima = ' || v_prima_minima);
	  pq_utl.log(lc, 'Prima del suplemento = ' || v_prima_total_suplemento);
	  pq_utl.log(lc, 'Numero de parcelas del suplemento = ' || v_num_parc_supl);
	  pq_utl.log(lc, 'v_ref_poliza = ' || v_ref_poliza);
	  pq_utl.log(lc, 'codcultivo = ' || v_codcultivo);
	  
	  -- calculamos cuanto hay que anhadir a la prima de cada parcela
	  v_prima_minima_aux := ((v_prima_minima - v_prima_total_suplemento) / v_num_parc_supl) / 2;	  
	  pq_utl.log(lc, 'v_prima_minima_aux = ' || v_prima_minima_aux);
	  
	  -- ANHADIMOS EL IMPORTE DE RELLENO DE PRIMA MINIMA A CADA UNA DE LAS PRIMAS
	  -- DIVIDIMOS ENTRE 100 AL CAMBIAR A NUMBER YA QUE EL DATO VIENE EN FORMATO
	  -- VARCHAR SIN SEPARADOR DE DECIMALES
	  v_prima_inc_aux := (TO_NUMBER(v_prima_inc) / 100) + v_prima_minima_aux;
	  v_prima_ped_aux := (TO_NUMBER(v_prima_ped) / 100) + v_prima_minima_aux;
	  
	  -- SI POR REDONDEOS SE PIERDEN IMPORTES, AUMENTAMOS UN CENTIMO EN UNA PRIMA
	  -- PARA EVITAR RECHAZOS DE OMEGA
	  IF (v_prima_minima_aux - round(v_prima_minima_aux, 2)) > 0 THEN
	  	pq_utl.log(lc, 'Realizado ajuste por redondeo');
	  	v_prima_inc_aux := v_prima_inc_aux + 0.01;
	  END IF;

	  -- SIGPE 8252 - MPM - Actualizar los campos de primas enviadas de los registros de parcelas con la misma provincia
      -- y cultivo del suplemento que se esta tratando para que no se traten al procesar las siguientes parcelas
      UPDATE o02agpe0.tb_sbp_parcelas spar
      set spar.prima_neta_incendio_env = v_prima_inc_aux,
          spar.prima_neta_pedrisco_env = v_prima_ped_aux
      where spar.idpoliza_sbp = v_id_plz_sbp
          and spar.codcultivo=v_codcultivo;
      commit;

      v_prima_ped := LPAD(TRIM(TO_CHAR(v_prima_ped_aux, '9999999999V99')), 12, '0');
      v_prima_inc := LPAD(TRIM(TO_CHAR(v_prima_inc_aux, '9999999999V99')), 12, '0');
      pq_utl.log(lc, 'Se actualizan las primas del suplemento');
      pq_utl.log(lc, 'v_prima_inc -> ' || v_prima_inc);
      pq_utl.log(lc, 'v_prima_ped -> ' || v_prima_ped);

  END IF;
  -- Si la diferencia es negativa, la suma de tasas de la principal y el suplemento supera lo pagado en la principal, por
  -- lo que hay que pagar para contratar el suplemento. Se envian las tasas del suplemento como estan

 
  END;

    ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- PROCEDURE actualizaVarConfig
	--
	-- Actualiza las variables de configuracion correspondientes con el numero de polizas
  -- de sobreprecio pasadas a definitiva desde la ultima ejecucion del batch de envio
  --
  PROCEDURE actualizaVarConfig(p_fecha IN varchar2) AS

  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.actualizaVarConfig'; -- Variable que almacena el nombre del paquete y de la funcion

  -- Almacena el numero de polizas de sobreprecio de tipo principal en estado 'Grabacion definitiva' y cuya
  -- poliza principal asociada este en estado 'Enviada correcta', 'Enviada pendiente de confirmacion' o 'Definitiva'
  v_num_plz_def_1   NUMBER(5);
  -- Almacena el numero de polizas de sobreprecio de tipo suplemento en estado 'Grabacion definitiva' y cuya
  -- poliza complementaria asociada este en estado 'Enviada correcta'
  v_num_plz_def_2   NUMBER(5);
  -- Almacena el numero de polizas de sobreprecio que se enviaran
  v_num_plz_def   NUMBER(5);
  -- Nombre de la variable de configuracion que almacena el no de polizas de sobreprecio pasadas a definitiva
  nom_var_config	    varchar2(15) := 'POLIZAS_SBP_DEF';

  BEGIN

    pq_utl.log(lc,
               'Numero de polizas de sobreprecio pasadas a definitiva desde la ultima ejecucion del batch de envio',
               2);

    -- Sbp principales que esten en definitiva y la poliza principal asociada este enviada correcta, enviada pendiente de confirmar o en definitiva
    SELECT count(*)
      into v_num_plz_def_1
      FROM o02agpe0.TB_SBP_POLIZAS PO,
           o02agpe0.tb_polizas p,
           (select fc.lineaseguroid,
                   min(fechainicio) as fini,
                   max(fechafin) as ffin
              from o02agpe0.tb_sbp_fechas_contratacion fc
             group by fc.lineaseguroid) q
     WHERE PO.IDESTADO = 2
       AND po.tipoenvio = 1
       and p.idestado in (3, 5, 8)
       and q.fini <= TO_DATE(sysdate, 'DD/MM/YY')
       and ffin >= TO_DATE(sysdate, 'DD/MM/YY')
       and q.lineaseguroid = p.lineaseguroid
       and po.idpoliza = p.idpoliza;

    pq_utl.log(lc,
               'Numero de polizas de sobreprecio principales en definitiva: ' ||
               v_num_plz_def_1,
               2);

    -- Suplementos que esten en definitiva y la poliza complementaria asociada este enviada correcta
    SELECT count(*)
      into v_num_plz_def_2
      FROM o02agpe0.TB_SBP_POLIZAS PO,
           o02agpe0.tb_polizas p,
           (select fc.lineaseguroid,
                   min(fechainicio) as fini,
                   max(fechafin) as ffin
              from o02agpe0.tb_sbp_fechas_contratacion fc
             group by fc.lineaseguroid) q
     WHERE PO.IDESTADO = 2
       AND po.tipoenvio = 2
       and q.fini <= TO_DATE(sysdate, 'DD/MM/YY')
       and ffin >= TO_DATE(sysdate, 'DD/MM/YY')
       and q.lineaseguroid = p.lineaseguroid
       and po.idpoliza = p.idpoliza;

    pq_utl.log(lc,
               'Numero de polizas de suplementos de sobreprecio en definitiva: ' ||
               v_num_plz_def_2,
               2);

    v_num_plz_def := v_num_plz_def_1 + v_num_plz_def_2;

    pq_utl.log(lc,
               'Total de polizas de sobreprecio en estado definitivo: ' ||
               v_num_plz_def,
               2);

    -- Actualiza la variable de configuracion correspondiente al numero de polizas de sobreprecio pasadas a definitiva
    o02agpe0.PQ_UTL.setCfg(nom_var_config || p_fecha, v_num_plz_def);
  END;


  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION leerFicheroSupl
	--
	-- Si existe el fichero 'suplementos.txt' y no esta vacio, lee el contenido y lo devuelve
  -- l_dir es el directorio donde se encuentra el fichero 'suplementos.txt'
  --
  FUNCTION leerFicheroSupl (l_dir tb_config_agp.agp_valor%TYPE) RETURN LONG IS

  listaIds  LONG;
  f_supl         utl_file.file_type; -- Variable que almacena la referencia al fichero de suplementos
  lc VARCHAR2(50) := 'pq_envio_polizas_sbp.leerFicheroSupl'; -- Variable que almacena el nombre del paquete y de la funcion
  v_linea_con   VARCHAR2(32000) := NULL;

  BEGIN
    -- Intenta abrir el fichero en modo lectura
		pq_utl.log(lc, 'Abre el fichero de suplementos.', 2);
		f_supl := utl_file.fopen(location => l_dir, filename => 'suplementos.txt', open_mode => 'r', max_linesize => pq_typ.max_linefilesizewrite);

    -- Lee el contenido el fichero y guarda en la variable Long
  pq_utl.log(lc, 'Lee el fichero de suplementos.', 2);
    LOOP
				BEGIN
					-- Leo del fichero a concatenar.
					utl_file.get_line(file => f_supl, buffer => v_linea_con);
					-- Anhade a la variable que se va a devolver
				  listaIds := listaIds || v_linea_con;
				EXCEPTION
					WHEN no_data_found THEN
          				pq_utl.log(lc, 'Llega al final del fichero de suplementos.', 2);
         				pq_utl.log(lc, 'Cierra el fichero de suplementos.');
		       			utl_file.fclose(f_supl);
						RETURN listaIds;
				END;
		END LOOP;

    EXCEPTION
			WHEN OTHERS THEN
				pq_utl.log(lc, 'El fichero de suplementos no existe.');
        RETURN NULL;
  END;

END pq_envio_polizas_sbp;
-- Fin de declaraciï¿½n de paquete
/
SHOW ERRORS;
