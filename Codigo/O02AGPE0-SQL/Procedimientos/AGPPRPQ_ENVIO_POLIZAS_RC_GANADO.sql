SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.PQ_ENVIO_POLIZAS_RC_GANADO IS

  /******************************************************************************
     NAME:        PQ_ENVIO_POLIZAS_RC_GANADO
     PURPOSE:     Se enviaran las polizas de R.C Ganado en estado 'Grabada Definitiva' (estado 2)
          Se enviaran un ?nico registro por poliza
          Solo se admite una p?liza de R.C Ganado sobre una misma
          referencia de poliza.
          Las polizas de R.C Ganado en este momento pasan al estado
          'Enviada Pendiente Aceptacion' (estado 3) hasta que se procese el fichero
          devuelto por Omega.

     REVISIONS:
     Ver        Date        Author           Description
     ---------  ----------  ---------------  ------------------------------------
     1.0        16-01-2018  Tatiana Alb.    1. Created this package.
                            T-Systems
  ******************************************************************************/

  -------------------------------------------------------------------------------
  -- PROCEDIMIENTOS PUBLICOS
  -------------------------------------------------------------------------------

  -- Carga las polizas de R.C Ganado
  FUNCTION load_polizas_rc_ganado(p_fecha IN VARCHAR2) RETURN VARCHAR2;

  -- Pinta la cadena recibida en el log independientemente de su tama?o
  FUNCTION print_log_completo(str IN VARCHAR2) RETURN VARCHAR2;

   -- Devuelve la consulta que obtiene formateados todos los campos del sobreprecio que hay que incluir en el fichero
  FUNCTION crearQuery RETURN VARCHAR2;

  -- Actualiza las variables de configuracion correspondientes con el numero de polizas
  -- de sobreprecio pasadas a definitiva desde la ultima ejecucion del batch de envio
  PROCEDURE actualizaVarConfig(p_fecha IN VARCHAR2);

  FUNCTION getFechaEfecto  RETURN DATE;

  -- Genera el fichero TXT Vacio
  FUNCTION load_polizas_rc_ganado_vacio RETURN VARCHAR2;

END PQ_ENVIO_POLIZAS_RC_GANADO;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_ENVIO_POLIZAS_RC_GANADO AS

	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION load_polizas_rc_ganado
	--
	-- Fichero de Polizas de R.C Ganado
	--
	-- Recoge las polizas de R.C Ganado que han entrado en el dia y contiene los siguientes campos:
	--
	--
	-----------------------------------------------------------------------------------------------------------------------
	----- | NOMBRE              | TIPO        | CAMPO
	----- |---------------------|------- -----|----------------------------------------------------------------------------
	-- 01 | Poliza de agroplus  | AF(8)       | TB_RC_POLIZAS.IDPOLIZA || TB_RC_POLIZAS
	-- 02 | Fecha de efecto     | AF(8)       | TO_CHAR(SYSDATE, ''YYYYMMDD'')
	-- 03 | Fecha de vencimiento| AF(8)       | TO_CHAR(DATE, "YYYYMMDD")
	-- 04 | Forma de Pago       | AF(1)       | (U-Unica, A-Anual, S-Semestral, T-Trimestral). Se enviara siempre 'A'
	-- 05 | Cuenta de domiciliacion | N(24)   | TB_DATOS_ASEGURADOS.CCC
	-- 06 | Codigo de la entidad| N(4)        | TB_COLECTIVOS.CODENTIDAD
	-- 07 | Codigo de la oficina| N(4)        | TB_POLIZAS.OFICINA
	-- 08 | NIF del asegurado   | AF(9)       | TB_ASEGURADOS.NIFCIF (a?adir 0 por la izquierda si no llega a longitud 9)
	-- 09 | Nombre y apellidos  | AF(100)     | TB_ASEGURADOS.NOMBRE, APELLIDO1, APELLIDO2 (a?adir espacios en blanco por la derecha hasta llegar a 100)
	-- 10 | Codigo de riesgo    | AF(10)      |
	-- 11 | Tipo de Via         | AF(2)	  |
	-- 12 | Nombre via          | AF(50)      |
	-- 13 | N? de la Via        | AF(10)      |
	-- 14 | Poblacion           | AF(30)      |
	-- 15 | Codigo de Provincia | N(2)        |
	-- 16 | Codigo Postal       | N(5)        |
	-- 17 | Codigo de Pais      | N(3)        | Se enviara siempre `11- Espa?a'.
	-- 18 | Suma Asegurada R.C  | N(10,2)     |
	-- 19 | Tipo de Tasa        | AF(2)       | Se enviara siempre 'U- Unidades'
	-- 20 | Criterios de Tarificacion | AF(4) | Se enviara siempre 'ANIM- N? de Animales'
	-- 21 | Base Calculo        | D(10,2)     |
	-- 22 | Importe R.C         | D(10,2)     |
	-- 23 | Prima Minima de R.C | D(10,2)     |
	-- 24 | Limite maximo por victima | D(10,2)| Se enviara siempre '150.000,00'
	-- 25 | Prima Neta Garantia | D(10,2)     |
	-- 26 | Tipo franquicia     | AF(1)       | Siempre valor 'A'-Absoluta
	-- 27 | Valor Franquicia    | N(5)        |
	-- 28 | Desc. del Riesgo    | AF(762)     |RESPONSABILIDAD CIVIL QUE LE PUEDA SER EXIGIDA AL ASEGURADO DE ACUERDO CON
	--				          |LAS CONDICIONES ADJUNTAS EN SU CALIDAD DE PROPIETARIO DE xxx CABEZAS DE GANADO yyy
	--				          |CON TIPO DE MANEJO zzz", donde xxx es el numero de animales, yyy la especie y zzz el regimen.
	-----------------------------------------------------------------------------------------------------------------------
	-- |
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------

	-- Inicio de declaracion de funcion
	FUNCTION load_polizas_rc_ganado(p_fecha IN VARCHAR2) RETURN VARCHAR2 IS

		-- Variables generales
		lc VARCHAR2(50) := 'pq_envio_polizas_rc_ganado.load_polizas_rc_ganado'; -- Variable que almacena el nombre del paquete y de la funcion
		TYPE tpcursor IS REF CURSOR; -- Tipo cursor
		f_fichero         utl_file.file_type; -- Variable que almacena la referencia al fichero de envio
		l_dir             tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros a enviar
		l_line            VARCHAR2(1200); -- Variables que almacena la linea que se volcara en el fichero
		l_nombre          VARCHAR2(8) := 'PLZS_RC'; -- Nombre del fichero de envio
		l_dir_name        VARCHAR2(1000); -- Ruta fisica donde se dejan los ficheros que se van a enviar
		l_tp_cursor       tpcursor; -- Cursor para la consulta de polizas de R.C Ganado
		l_num_polizas     NUMBER := 0; -- Contador para el numero de polizas enviadas
		l_num_polizas_upd NUMBER := 0; -- Contador para el numero de polizas a las que se ha cambiado el estado
		v_id_plz_rc_ant  VARCHAR2(15); -- Variable para almacenar el id de poliza tratado en la iteracion anterior
		no_polizas_rc_found EXCEPTION; -- Excepcion indicadora de que no hay polizas de R.C Ganado para enviar
		fichero_tipo VARCHAR2(1) := 'B'; -- Letra que indica el tipo de fichero para la tabla TB_COMUNICACIONES
		tipo_mov     VARCHAR2(10) := 'ENVIO'; -- Tipo de movimiento para el fichero para la tabla TB_COMUNICACIONES

    -- Variables para componer la consulta para obtener las polizas de R.C Ganado a enviar
    l_query          VARCHAR2(12500);

		-- Variables donde se volcaran los datos de los campos
		v_id_plz_rc     VARCHAR2(15);

		v_ref_poliza    VARCHAR2(8);
    v_f_envio       VARCHAR2(8);
		v_f_efecto      VARCHAR2(8);
    v_cod_fpago     VARCHAR2(1) :='A';
		v_f_vencimiento VARCHAR2(8);
		v_ccc           VARCHAR2(24);
		v_cod_entidad   VARCHAR2(4);
		v_cod_oficina   VARCHAR2(4);
		v_nif           VARCHAR2(9);
		v_nom_apellidos VARCHAR2(100);
		v_cod_riesgo    VARCHAR2(10);
		v_tipo_via      VARCHAR2(2);
		v_nomb_via      VARCHAR2(50);
		v_num_via       VARCHAR2(10);
		v_poblacion     VARCHAR2(30);
		v_cod_prov      VARCHAR2(2);
		v_cod_postal    VARCHAR2(5);
		v_cod_pais      VARCHAR2(3) :='011';
		v_sum_aseg      VARCHAR2(12);
		v_tipo_tasa     VARCHAR2(2) :='U ';
		v_crit_tarf     VARCHAR2(4) :='ANIM';
		v_base_calc     VARCHAR2(12);   --Numero de unidades de animales
		v_imp_rc        VARCHAR2(12);   --Tasa de la RC
		v_prima_min_rc  VARCHAR2(12);   -- Se enviara la prima minima asociada a la especie, regimen y suma asegurada elegida
    -- MODIF TAM (08.02.2018) ** Inicio **
    -- Se cambia el formateo del importe de sublimite maximo por victima para que no este
    -- editado y mantenga el formato del resto de campos
		v_lim_max_vic   VARCHAR2(12):='000015000000';
		v_prim_net_gar  VARCHAR2(12);   --Precio de la RC sin impuestos
		v_tip_franq     VARCHAR2(1) :='A';
		v_val_franq     VARCHAR2(5);
    v_num_anim      VARCHAR2(9);
		v_desc_esp      VARCHAR2(50);
		v_desc_reg      VARCHAR2(50);
		v_desc_riesg    VARCHAR2(762);
    v_codusuario    VARCHAR2(8) :='Batch   ';
    	v_fecha_planif	DATE;

    -- He tenido que crearme una variable auxiliar para el tama?o
    -- maximo de la linea del fichero, ya que estaba utilizando una
    -- constante que tenia como tama?o maximo 1050 y nuestra linea
    -- tiene una longitud de 1124.
    l_max_linefilesize_aux NUMBER := 1150;


    f_fichero_con utl_file.file_type;
		v_linea_con   VARCHAR2(32000) := NULL;

		         -- RQ.03
        type tpstrarr is varray(100000) of varchar(15);
        v_ids_polizas tpstrarr := tpstrarr();

		-- Inicio del cuerpo de la funcion
	BEGIN

		-- Comienzo de escritura en log
		pq_utl.log(lc, '## INI ##', 1);

		pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);

		-- Convertimos a DATE la fecha de planificacion recibida por parametro desde el sh
		v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');

		-- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros a enviar
		pq_utl.log(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositaran los ficheros de envio.', 2);

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

    -- Cambiamos para utilizar una variable auxiliar con un tama?o de fichero mas grande.
		--f_fichero := utl_file.fopen(location => l_dir, filename => l_nombre || '.TXT', open_mode => 'w', max_linesize => pq_typ.max_linefilesizewrite_aux);
     f_fichero := utl_file.fopen(location => l_dir, filename => l_nombre || '.TXT', open_mode => 'w', max_linesize =>l_max_linefilesize_aux);


    -- Actualiza las variables de configuracion correspondientes con el numero de polizas
    -- de R.C Ganado pasadas a definitiva desde la ultima ejecucion del batch de envio
    actualizaVarConfig(p_fecha);

		-- Se compone la consulta para recuperar las polizas de R.c Ganado en estado 'Grabada definitiva'
    -- Si la funcion 'leerFicheroSupl' devuelve ids, se compone la query para obtener los datos correspondientes a esos ids,
    -- si devuelve null, la compone para obtener los R.c Ganado y suplementos en definitiva
		l_query:= crearQuery;

		-- Crea el cursor para la consulta
		pq_utl.log(lc, 'Abre el cursor para la query');

    OPEN l_tp_cursor FOR l_query;

		-- Bucle para recorrer todos los registros del cursor
		LOOP

			-- Vuelca los datos del cursor en las variables indicadas
			pq_utl.log(lc, 'Volcado de datos del cursor en variables');

			FETCH l_tp_cursor
				INTO v_id_plz_rc,
             v_ref_poliza,
             v_f_efecto,
             v_f_vencimiento,
             v_ccc,
             v_cod_entidad,
             v_cod_oficina,
             v_nif,
             v_nom_apellidos,
             v_cod_riesgo,
             v_tipo_via,
             v_nomb_via,
             v_num_via,
             v_poblacion,
             v_cod_prov,
             v_cod_postal,
             v_sum_aseg,
             v_base_calc,
             v_imp_rc,
             v_prima_min_rc,
             v_prim_net_gar,
             v_val_franq,
             v_num_anim,
             v_desc_esp,
             v_desc_reg;

      -- Pinto la entidad y oficina
      pq_utl.log(lc, 'v_id_plz_rc = ' || v_id_plz_rc);
      pq_utl.log(lc, 'v_ref_poliza = ' || v_ref_poliza);
      pq_utl.log(lc, 'v_f_efecto = ' || v_f_efecto);
      pq_utl.log(lc, 'v_f_vencimiento = ' || v_f_vencimiento);
      pq_utl.log(lc, 'v_ccc = ' || v_ccc);
      pq_utl.log(lc, 'v_cod_entidad = ' || v_cod_entidad);
      pq_utl.log(lc, 'v_cod_oficina = ' || v_cod_oficina);
      pq_utl.log(lc, 'v_nif = ' || v_nif);
      pq_utl.log(lc, 'v_nom_apellidos = ' || v_nom_apellidos);
      pq_utl.log(lc, 'v_cod_riesgo = ' || v_cod_riesgo);
      pq_utl.log(lc, 'v_tipo_via = ' || v_tipo_via);
      pq_utl.log(lc, 'v_nomb_via = ' || v_nomb_via);
      pq_utl.log(lc, 'v_num_via = ' || v_num_via);
      pq_utl.log(lc, 'v_poblacion = ' || v_poblacion);
      pq_utl.log(lc, 'v_cod_prov = ' || v_cod_prov);
      pq_utl.log(lc, 'v_cod_postal = ' || v_cod_postal);
      pq_utl.log(lc, 'v_sum_aseg = ' || v_sum_aseg);
      pq_utl.log(lc, 'v_base_calc = ' || v_base_calc);
      pq_utl.log(lc, 'v_imp_rc = ' || v_imp_rc);
      pq_utl.log(lc, 'v_prima_min_rc = ' || v_prima_min_rc);
      pq_utl.log(lc, 'v_prim_net_gar = ' || v_prim_net_gar);
--      pq_utl.log(lc, 'v_val_franq = ' || v_val_franq);
      pq_utl.log(lc, 'v_num_anim = ' || v_num_anim);
      pq_utl.log(lc, 'v_desc_esp = ' || v_desc_esp);
      pq_utl.log(lc, 'v_desc_reg = ' || v_desc_reg);


			-- Si no ha mas registros
			IF (l_tp_cursor%NOTFOUND) THEN

				-- Si el numero de polizas tratadas es cero se lanza la excepcion correspondiente
				IF (l_num_polizas = 0) THEN
					pq_utl.log(lc, 'No se han encontrado polizas de R.C Ganado en estado definitiva');
					RAISE no_polizas_rc_found;
				ELSE

				-- Si llega hasta aqui, se han tratado polizas y hay que actualizar el estado de la ultima
        -- Solo se actualiza el estado de la poliza de RC Ganadosi es una ejecucion normal
  				pq_utl.log(lc, 'Actualizado el estado de la poliza de R.C Ganado con id - ' || v_id_plz_rc_ant);

        -- MODIF TAM (02.02.2018) ** Inicio --
        -- La fecha de envio hay que actualizarla, al actualizar el estado
        -- esa misma fecha sera la que enviemos en el fichero y con esa fecha
        -- obtendremos al fecha de vencimiento
        --(28.02.2018) - MODIF TAM
        -- v_f_efecto := TO_CHAR(sysdate, 'YYYYMMDD');
        	v_f_envio := p_fecha;
          pq_utl.log(lc, 'Valor de fecha envio - ' || v_f_envio);

          --v_f_vencimiento := TO_CHAR(add_months(sysdate, +12), 'YYYYMMDD');

          --pq_utl.log(lc, 'Valor de fecha vencimiento - ' || v_f_vencimiento);

          pq_utl.log(lc, 'Informamos la fecha de envio y vencimiento antes de modificar la tabla TB_RC_POLIZAS');

  				UPDATE tb_rc_polizas
  					 SET idestado = 3,
                 fecha_envio = v_fecha_planif
  				 WHERE id = v_id_plz_rc_ant;
           -- MODIF TAM (02.02.2018) ** Inicio --

           -- TMR Insertamos en el historico de estados de poliza de R.C Ganado
           INSERT INTO TB_RC_POLIZAS_HIST_ESTADOS (id,idpoliza_rc,codusuario,fecha,idestado)
                 values (SQ_POLIZAS_HIST_ESTADOS_RC.nextval,v_id_plz_rc_ant,v_codusuario,v_fecha_planif,3);
          l_num_polizas_upd := l_num_polizas_upd + 1;
        END IF;

				pq_utl.log(lc, 'No hay mas registros que enviar.');

				-- Sale del bucle
				EXIT;
			END IF;

			pq_utl.log(lc, 'Se compone la linea para la poliza con id - ' || v_id_plz_rc );

      v_desc_riesg := 'RESPONSABILIDAD CIVIL QUE LE PUEDA SER EXIGIDA AL ASEGURADO DE ACUERDO CON LAS CONDICIONES ADJUNTAS EN' ||
                      ' SU CALIDAD DE PROPIETARIO DE ' || TRIM(v_num_anim) || ' CABEZAS DE GANADO ' || TRIM(v_desc_esp) ||
                      ' CON TIPO DE MANEJO ' || v_desc_reg;

      --

      --(28.02.2018) - MODIF TAM
      v_f_envio := TO_CHAR(sysdate, 'YYYYMMDD');
      pq_utl.log(lc, 'Valor de fecha envio - ' || v_f_envio);

      --v_f_vencimiento := TO_CHAR(add_months(sysdate, +12), 'YYYYMMDD');
      --pq_utl.log(lc, 'Valor de fecha vencimiento - ' || v_f_vencimiento);


      -- Compone la linea que se volcara en el fichero correspondiente a un cultivo y poliza de R.C Ganado
			l_line := v_ref_poliza ||
			          v_f_efecto ||
			          v_f_vencimiento ||
			          v_cod_fpago ||
			          v_ccc ||
			          v_cod_entidad ||
			          v_cod_oficina ||
			          v_nif ||
			          v_nom_apellidos ||
			          v_cod_riesgo ||
			          v_tipo_via ||
			          v_nomb_via ||
			          v_num_via ||
			          v_poblacion ||
			          v_cod_prov ||
			          v_cod_postal ||
			          v_cod_pais ||
			          v_sum_aseg ||
			          v_tipo_tasa ||
			          v_crit_tarf ||
			          v_base_calc ||
			          v_imp_rc ||
			          v_prima_min_rc ||
			          v_lim_max_vic ||
			          v_prim_net_gar ||
			          v_tip_franq ||
			          v_val_franq ||
			          rpad(trim(v_desc_riesg), 762);


			-- Inserta en el fichero de envio la linea compuesta y el salto
			pq_utl.log(lc, 'Escribe la linea en el fichero de envio.');

			utl_file.put_line(f_fichero, l_line || chr(13));

			-- Actualiza el contador de polizas enviadas
			l_num_polizas := l_num_polizas + 1;

			-- Comprobar si hay que hacer el update de la poliza de R.C Ganado
			-- Si es la primera poliza que se trata, se actualiza el id de poliza anterior con el actual
			IF (v_id_plz_rc_ant IS NULL) THEN
				v_id_plz_rc_ant := v_id_plz_rc;
				-- Si no es la primera poliza que se trata
			ELSE
				-- Si el id de poliza anterior es diferente que el actual -> Update del id anterior
				IF (v_id_plz_rc_ant <> v_id_plz_rc) THEN
					pq_utl.log(lc, 'Actualizado el estado de la poliza de R.C Ganado con id - ' || v_id_plz_rc_ant);

          -- Solo se actualiza el estado de la poliza de R.C Ganado si es una ejecucion normal
 					UPDATE tb_rc_polizas
 						 SET idestado = 3,
             fecha_envio = v_fecha_planif
 					 WHERE id = v_id_plz_rc_ant;

          -- Insertamos en el historico de estados de poliza de R.C Ganado
          INSERT INTO TB_RC_POLIZAS_HIST_ESTADOS (id,idpoliza_rc,codusuario,fecha,idestado)
                 values (SQ_POLIZAS_HIST_ESTADOS_RC.nextval,v_id_plz_rc_ant,v_codusuario,v_fecha_planif,3);

					l_num_polizas_upd := l_num_polizas_upd + 1;
					v_id_plz_rc_ant  := v_id_plz_rc;

					 -- RQ.03
                    v_ids_polizas.extend;
                    v_ids_polizas(v_ids_polizas.Count) := v_id_plz_rc;

				END IF;
			END IF;

		END LOOP;

		-- Hace commit de los updates solo si es una ejecucion normal
    pq_utl.log(lc, 'Hace commit para los updates.');
    COMMIT;

    -- Cierra el cursor y el fichero de envio
		pq_utl.log(lc, 'Cierra el cursor.');
		CLOSE l_tp_cursor;

		-- Inicio de la concatenacion de ficheros
    -- TAM (29.05.2018) ** Inicio -> ESTO NO ES NECESARIO PARA R.C
		--BEGIN
		--	f_fichero_con := utl_file.fopen(location => l_dir, filename => 'MANOLITO.TXT', open_mode => 'r', max_linesize => pq_typ.max_linefilesizewrite);
		--
		--LOOP
		--	BEGIN
		--		-- Leo del fichero a concatenar.
		--					utl_file.get_line(file => f_fichero_con, buffer => v_linea_con);
		--
		--					-- Escribo en el fichero abierto.
		--					utl_file.put_line(file => f_fichero, buffer => v_linea_con);
		--
		--				EXCEPTION
		--					WHEN no_data_found THEN
		--						EXIT;
		--				END;
		--			END LOOP;

		--		EXCEPTION
		--			WHEN OTHERS THEN
		--      null;
		--				pq_utl.log(lc, 'Error en el proceso de concatenacion de ficheros ' || SQLERRM);
    --END;
		-- Fin concatenacion de ficheros
    -- TAM (29.05.2018) ** Inicio -> ESTO NO ES NECESARIO PARA R.C


		pq_utl.log(lc, 'Cierra el fichero de envio.');
		utl_file.fclose(f_fichero);

		pq_utl.log(lc, 'Cierra el fichero a Concatenar.');
		utl_file.fclose(f_fichero);


		-- Realizamos una copia del fichero generado para guardarlo a modo de historico
		-- ya que el fichero 'POLIZAS_RC.TXT' se generara cada vez.
		pq_utl.log(lc, 'Se copia el fichero generado en :' || l_dir_name || ' con nombre: ' || l_nombre || to_char(v_fecha_planif, 'YYMMDDHH24MISS') || '.');
		utl_file.fcopy(l_dir, l_nombre || '.TXT', l_dir, l_nombre || to_char(v_fecha_planif, 'YYMMDDHH24MISS') || '.TXT');

		-- Inserta en la tabla de comunicaciones un registro indicando el envio del fichero
		pq_utl.log(lc, 'Inserta en la tabla de comunicaciones un registro indicando el envio del fichero');
		INSERT INTO tb_comunicaciones
		VALUES
			(sq_comunicaciones.NEXTVAL, v_fecha_planif, l_nombre, tipo_mov, NULL, NULL, NULL, fichero_tipo, NULL);
		COMMIT;

		-- Guardamos los resultados en el fichero de log

		pq_utl.log(lc, '');
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || to_char(sysdate, 'DD/MM/YY HH24:MI:SS'), 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'Polizas de R.C Ganado definitivas enviadas   := ' || l_num_polizas, 2);
		pq_utl.log(lc, 'Polizas de R.C Ganado actualizadas   := ' || l_num_polizas_upd, 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, '', 2);

		pq_utl.log(lc, 'El proceso ha finalizado correctamente a las ' || to_char(sysdate, 'HH24:MI:SS'), 2);
		pq_utl.log(lc, '## FIN ##', 1);

		RETURN 'Se han encontrado ' || l_num_polizas || ' definitivas.';

		-- Control de excepciones
	EXCEPTION
		WHEN no_polizas_rc_found THEN
			-- Rollback por si se ha hecho algun update
			ROLLBACK;
			-- Se cierra el cursor y el fichero de envio
			CLOSE l_tp_cursor;
			utl_file.fclose(f_fichero);
			-- Se escribe en el log el error
			pq_utl.log(lc, 'El proceso ha finalizado correctamente a las ' || to_char(sysdate, 'HH24:MI:SS'), 2);
			pq_utl.log(lc, '## FIN ##', 1);

			RETURN 'Se han encontrado ' || l_num_polizas || ' definitivas.';

		WHEN OTHERS THEN
            -- RQ.03
            pq_utl.log(lc, 'Error al generar los ficheros de ' || to_char(v_ids_polizas.Count) || ' polizas');
            for it in 1..v_ids_polizas.Count LOOP
                pq_utl.log(lc, 'IdPoliza = ' || to_char(v_ids_polizas(it)));
                -- Actualizar columna idstado de tb_rc_polizas con valor "2"
                UPDATE tb_rc_polizas SET idestado = '2' WHERE idpoliza = v_ids_polizas(it);
                -- Eliminar el ultimo registro de TB_RC_POLIZAS_HIST_ESTADOS con idestado "3".
                DELETE FROM TB_RC_POLIZAS_HIST_ESTADOS WHERE ROWID IN (SELECT ROWID FROM TB_RC_POLIZAS_HIST_ESTADOS where IDPOLIZA_RC = v_ids_polizas(it) AND idestado = '3' ORDER BY ID DESC FETCH FIRST 1 ROWS ONLY);
            end LOOP;
            COMMIT;

            -- END
      DBMS_OUTPUT.PUT_LINE(SQLERRM);
      pq_utl.log(lc,SQLERRM);
			-- Se escribe en el log el error
			pq_utl.log(lc, 'El proceso ha finalizado CON ERRORES a las ' || to_char(v_fecha_planif, 'HH24:MI:SS'), 2);
			pq_utl.log(lc, '## FIN ##', 1);
			pq_err.raiser(SQLCODE, 'Error al generar los ficheros de load_polizas_rc_ganado' || ' [' || SQLERRM || ']');
			-- Se vuelve a lanzar la excepcion para parar la cadena
			--RAISE;
      RETURN NULL;
	END;
	-- Fin del cuerpo de la funcion



	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION print_log_completo
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
			-- Si el tama?o de la cadena es mayor que el que permite el metodo de log, se parte y se escribe por trozos
			IF (str_aux IS NOT NULL AND length(str_aux) > 994) THEN
				pq_utl.log(null, substr(str_aux, 1, 994), 2);
				str_aux := substr(str_aux, 994);
				-- Si el tama?o de la cadena es menor, se pinta y se sale del bucle
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






  -------------------------------------------------------------------------------------------------------------------------
  -- FUNCTION getFechaEfecto
	--
	-- Devuelve la fecha de efecto configurada en la tabla TB_CONFIG_AGP o la fecha actual si esta no existe o es no valida
  --
    -------------------------------------------------------------------------------------------------------------------------
  FUNCTION getFechaEfecto  RETURN DATE IS

  v_fecEfecto DATE;
  lc VARCHAR2(50) := 'pq_envio_polizas_rc_ganado.getFechaEfecto';

  BEGIN

  select TO_DATE (c.agp_valor, 'DD/MM/YYYY')
    into v_fecEfecto
  from o02agpe0.tb_config_agp c
  where c.agp_nemo like 'ENVIO_RC_FECHA_EFECTO';

  pq_utl.log(lc, 'La fecha de efecto es ' || v_fecEfecto, 2);
  return v_fecEfecto;

  EXCEPTION

           WHEN OTHERS THEN
                pq_utl.log(lc, 'La fecha de efecto no esta configurada, se usa la fecha actual.', 2);
                RETURN SYSDATE;

  END;

  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION crearQuery
	--
	-- Devuelve la consulta que obtiene formateados todos los campos del R.C Ganado que hay que incluir en el fichero
  --
    ---------------------------------------------------------------------------------------------------------------------------
  FUNCTION crearQuery RETURN VARCHAR2 IS

  lc VARCHAR2(50) := 'pq_envio_polizas_rc_ganado.crearQuery'; -- Variable que almacena el nombre del paquete y de la funcion

  -- Variables para componer la consulta para obtener las polizas de RC Ganado a enviar
  l_query           VARCHAR2(12500);
  l_query_select    VARCHAR2(4000);
  l_query_from      VARCHAR2(4000);
  l_query_order     VARCHAR2(3000);
  l_query_groupby   VARCHAR2(5000);

  BEGIN

  -- Obtiene la fecha de efecto configurada

  pq_utl.log(lc, 'Monta la query para recuperar las polizas de RC Ganado definitivas ', 2);

  -- MODIF TAM (14.02.2018) ** Incidencias Detectadas por RGA --
  -- Cambiamos el formateo de numanimales los que se envian en la descr. del Riesgo
  -- y el primer campo que enviamos el erroneo, hay que enviar la Referencia de la poliza y el
  -- D.C

	l_query_select := 'SELECT TO_CHAR(PO.ID) id,
                            P.REFERENCIA || P.DC,
                            TO_CHAR(PO.FECHA_EFECTO, ''YYYYMMDD''),
                            TO_CHAR(add_months(PO.FECHA_EFECTO, +12), ''YYYYMMDD''),
                            RPAD(TO_CHAR(nvl(PAG.IBAN, '' '') || nvl(PAG.CCCBANCO, '' '')), 24),
                            LPAD(TRIM(TO_CHAR(C.CODENTIDAD, 9999)), 4, 0),
                            LPAD(TRIM(TO_CHAR(nvl(P.OFICINA, ''0''), 9999)), 4, 0),
                            LPAD(TRIM(AG.NIFCIF), 9, 0),
                            RPAD(TRIM (DECODE (NOMBRE, NULL,RAZONSOCIAL, nvl(AG.NOMBRE, '' '') || '' '' || nvl(AG.APELLIDO1, '' '') || '' '' || nvl(AG.APELLIDO2, '' ''))), 100, '' ''),
                            RPAD(TRIM(PO.CODESPECIE_RC), 10, '' ''),
                            RPAD(nvl(AG.CLAVEVIA, '' ''),2),
                            RPAD(nvl(AG.DIRECCION, '' ''), 50),
                            RPAD(nvl(AG.NUMVIA, '' ''), 10),
                            RPAD(TRIM(LO.NOMLOCALIDAD), 30),
                            LPAD(TO_CHAR(nvl(AG.CODPROVINCIA, ''0'')),2, ''0''),
                            LPAD(TO_CHAR(nvl(AG.CODPOSTAL, ''0'')),5, ''0''),
                            LPAD(TRIM(TO_CHAR(nvl(PO.SUMA_ASEGURADA, ''0''), ''9999999999V99'')), 12, ''0''),
                            LPAD(TRIM(TO_CHAR(nvl(PO.NUMANIMALES, ''0''), ''9999999999V99'')), 12, ''0''),
                            LPAD(TRIM(TO_CHAR(nvl(PO.TASA, ''0''), ''9999999999V99'')), 12, ''0''),
                            LPAD(TRIM(TO_CHAR(nvl(PO.PRIMA_MINIMA, ''0''), ''9999999999V99'')), 12, ''0''),
                            LPAD(TRIM(TO_CHAR(nvl(PO.PRIMA_NETA, ''0''), ''9999999999V99'')), 12, ''0''),
                            LPAD(TRIM(TO_CHAR(nvl(PO.FRANQUICIA, ''0''), ''99999'')), 5, 0),
                            LPAD(TO_CHAR(nvl(PO.NUMANIMALES, ''0'')),9),
                            ES.DESCRIPCION,
                            RE.DESCRIPCION';

  l_query_from := ' FROM o02agpe0.TB_RC_POLIZAS   PO,
                         o02agpe0.TB_ASEGURADOS   AG,
                         o02agpe0.TB_POLIZAS      P,
                         o02agpe0.TB_COLECTIVOS   C,
                         o02agpe0.TB_PAGOS_POLIZA PAG,
                         o02agpe0.TB_RC_LINEAS    LR,
                         o02agpe0.TB_LOCALIDADES  LO,
                         o02agpe0.TB_RC_ESPECIES  ES,
                         o02agpe0.TB_RC_REGIMEN   RE
                    WHERE PO.IDPOLIZA = P.IDPOLIZA
                       AND P.IDASEGURADO = AG.ID
                       AND PAG.IDPOLIZA = PO.IDPOLIZA
                       AND P.IDCOLECTIVO = C.ID
                       AND AG.CODLOCALIDAD = LO.CODLOCALIDAD
                       AND AG.CODPROVINCIA = LO.CODPROVINCIA
                       AND AG.SUBLOCALIDAD = LO.SUBLOCALIDAD
                       AND PO.CODESPECIE_RC = ES.CODESPECIE
                       AND PO.CODREGIMEN_RC = RE.CODREGIMEN';

    l_query_order := ' ORDER BY ID ';

    l_query_groupby := 'group by  TO_CHAR(PO.ID),
                                  P.REFERENCIA || P.DC,
                                  TO_CHAR(PO.FECHA_EFECTO, ''YYYYMMDD''),
                                  TO_CHAR(add_months(PO.FECHA_EFECTO, +12), ''YYYYMMDD''),
                                  RPAD(TO_CHAR(nvl(PAG.IBAN, '' '') || nvl(PAG.CCCBANCO, '' '')), 24),
                                  LPAD(TRIM(TO_CHAR(C.CODENTIDAD, 9999)), 4, 0),
                                  LPAD(TRIM(TO_CHAR(nvl(P.OFICINA, ''0''), 9999)), 4, 0),
                                  LPAD(TRIM(AG.NIFCIF), 9, 0),
                                  RPAD(TRIM (DECODE (NOMBRE, NULL,RAZONSOCIAL, nvl(AG.NOMBRE, '' '') || '' '' || nvl(AG.APELLIDO1, '' '') || '' '' || nvl(AG.APELLIDO2, '' ''))), 100, '' ''),
                                  RPAD(TRIM(PO.CODESPECIE_RC), 10, '' ''),
                                  RPAD(nvl(AG.CLAVEVIA, '' ''),2),
                                  RPAD(nvl(AG.DIRECCION, '' ''), 50),
                                  RPAD(nvl(AG.NUMVIA, '' ''), 10),
                                  RPAD(TRIM(LO.NOMLOCALIDAD), 30),
                                  LPAD(TO_CHAR(nvl(AG.CODPROVINCIA, ''0'')),2, ''0''),
                                  LPAD(TO_CHAR(nvl(AG.CODPOSTAL, ''0'')),5, ''0''),
                                  LPAD(TRIM(TO_CHAR(nvl(PO.SUMA_ASEGURADA, ''0''), ''9999999999V99'')), 12, ''0''),
                                  LPAD(TRIM(TO_CHAR(nvl(PO.NUMANIMALES, ''0''), ''9999999999V99'')), 12, ''0''),
                                  LPAD(TRIM(TO_CHAR(nvl(PO.TASA, ''0''), ''9999999999V99'')), 12, ''0''),
                                  LPAD(TRIM(TO_CHAR(nvl(PO.PRIMA_MINIMA, ''0''), ''9999999999V99'')), 12, ''0''),
                                  LPAD(TRIM(TO_CHAR(nvl(PO.PRIMA_NETA, ''0''), ''9999999999V99'')), 12, ''0''),
                                  LPAD(TRIM(TO_CHAR(nvl(PO.FRANQUICIA, ''0''), ''99999'')), 5, 0),
                                  LPAD(TO_CHAR(nvl(PO.NUMANIMALES, ''0'')),9),
                                  ES.DESCRIPCION,
                                  RE.DESCRIPCION';


        l_query:= -- Select para polizas principales
             l_query_select ||
             -- From para polizas principales
             l_query_from ||
             -- Where para polizas principales
             ' AND PO.IDESTADO = ''2'' ' || ' and p.idestado in (3,5,8) ' ||
             --' AND PO.IDESTADO = ''2'' '  ||
             -- Group by para polizas principales
             l_query_groupby ||
             -- Order general
             l_query_order;

		-- Si la query es mas larga que lo que permite el metodo de log, se mete en bucle para escribirla completamente
		pq_utl.log(lc, 'Query completa en log -> ' || (print_log_completo(l_query)));

    RETURN l_query;

  END;

  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- PROCEDURE actualizaVarConfig
	--
	-- Actualiza las variables de configuracion correspondientes con el numero de polizas
  -- de R.C Ganado pasadas a definitiva desde la ultima ejecucion del batch de envio
  --
  PROCEDURE actualizaVarConfig(p_fecha IN VARCHAR2) AS

  lc VARCHAR2(50) := 'pq_envio_polizas_rc.actualizaVarConfig'; -- Variable que almacena el nombre del paquete y de la funcion

  -- Almacena el numero de polizas de R.C Ganado que se enviaran
  v_num_plz_def   NUMBER(5);
  -- Nombre de la variable de configuracion que almacena el n? de polizas de R.C Ganado pasadas a definitiva
  nom_var_config	    varchar2(15) := 'POLIZAS_RC_DEF';

  BEGIN

  pq_utl.log(lc, 'Numero de polizas de R.C Ganado pasadas a definitiva desde la ultima ejecucion del batch de envio', 2);

    -- R.C Ganado principales que esten en definitiva y la poliza principal asociada este enviada correcta, enviada pendiente de confirmar o en definitiva
    SELECT count(*) into v_num_plz_def
    FROM o02agpe0.TB_RC_POLIZAS PO, o02agpe0.tb_polizas p
    WHERE PO.IDESTADO = 2
    and po.idpoliza = p.idpoliza
    and p.idestado in (3,5,8);


  pq_utl.log(lc, 'Numero de polizas de R.C Ganado principales en definitiva: ' || v_num_plz_def, 2);

    -- Actualiza la variable de configuracion correspondiente al numero de polizas de R.C Ganado pasadas a definitiva
    o02agpe0.PQ_UTL.setCfg(nom_var_config || p_fecha, v_num_plz_def);

  pq_utl.log(lc, 'Actualizada la variable de configuracion ' || nom_var_config || ' a ' || v_num_plz_def, 2);

  END;



  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- PROCEDURE load_polizas_rc_ganado_vacio
	--
	-- (05.02.2018)- Tatiana A.
  -- Nos creamos una funcion que lo unico que realizara es generar el fichero en la ruta definida, para que la cadena de
  -- envio se pueda subir antes a produccion, generando el fichero en vacio.
  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
  FUNCTION load_polizas_rc_ganado_vacio RETURN VARCHAR2 IS

		-- Variables generales
		lc VARCHAR2(60) := 'pq_envio_polizas_rc_ganado.load_polizas_rc_ganado_vacio'; -- Variable que almacena el nombre del paquete y de la funcion
		f_fichero         utl_file.file_type; -- Variable que almacena la referencia al fichero de envio
		l_dir             tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros a enviar
		l_nombre          VARCHAR2(8) := 'PLZS_RC'; -- Nombre del fichero de envio
		l_dir_name        VARCHAR2(1000); -- Ruta fisica donde se dejan los ficheros que se van a enviar
		l_num_polizas     NUMBER := 0; -- Contador para el numero de polizas enviadas
		l_num_polizas_upd NUMBER := 0; -- Contador para el numero de polizas a las que se ha cambiado el estado
		no_polizas_rc_found EXCEPTION; -- Excepcion indicadora de que no hay polizas de R.C Ganado para enviar
		fichero_tipo VARCHAR2(1) := 'B'; -- Letra que indica el tipo de fichero para la tabla TB_COMUNICACIONES
		tipo_mov     VARCHAR2(10) := 'ENVIO'; -- Tipo de movimiento para el fichero para la tabla TB_COMUNICACIONES

    -- He tenido que crearme una variable auxiliar para el tama?o
    -- maximo de la linea del fichero, ya que estaba utilizando una
    -- constante que tenia como tama?o maximo 1050 y nuestra linea
    -- tiene una longitud de 1124.
    l_max_linefilesize_aux NUMBER := 1150;

--    f_fichero_con utl_file.file_type;

		-- Inicio del cuerpo de la funcion
	BEGIN

		--f_fichero_con := utl_file.fopen(location => l_dir, filename => 'MANOLITO.TXT', open_mode => 'r', max_linesize => pq_typ.max_linefilesizewrite);

		-- Comienzo de escritura en log
		pq_utl.log(lc, '## INI ##', 1);


		-- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros a enviar
		pq_utl.log(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositaran los ficheros de envio.', 2);

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

    -- Cambiamos para utilizar una variable auxiliar con un tama?o de fichero mas grande.
		--f_fichero := utl_file.fopen(location => l_dir, filename => l_nombre || '.TXT', open_mode => 'w', max_linesize => pq_typ.max_linefilesizewrite_aux);
     f_fichero := utl_file.fopen(location => l_dir, filename => l_nombre || '.TXT', open_mode => 'w', max_linesize =>l_max_linefilesize_aux);


    -- Actualiza las variables de configuracion correspondientes con el numero de polizas
    -- de R.C Ganado pasadas a definitiva desde la ultima ejecucion del batch de envio
    --actualizaVarConfig();


		-- Si el numero de polizas tratadas es cero se lanza la excepcion correspondiente
		IF (l_num_polizas = 0) THEN
				pq_utl.log(lc, 'No se han encontrado polizas de R.C Ganado en estado definitiva');
				--RAISE no_polizas_rc_found;

				pq_utl.log(lc, 'No hay registros que enviar.');

  	END IF;


		pq_utl.log(lc, 'Cierra el fichero de envio.');
		utl_file.fclose(f_fichero);

		pq_utl.log(lc, 'Cierra el fichero a Concatenar.');
		utl_file.fclose(f_fichero);


		-- Realizamos una copia del fichero generado para guardarlo a modo de historico
		-- ya que el fichero 'POLIZAS_RC.TXT' se generara cada vez.
		pq_utl.log(lc, 'Se copia el fichero generado en :' || l_dir_name || ' con nombre: ' || l_nombre || to_char(SYSDATE, 'YYMMDDHH24MISS') || '.');
		utl_file.fcopy(l_dir, l_nombre || '.TXT', l_dir, l_nombre || to_char(SYSDATE, 'YYMMDDHH24MISS') || '.TXT');

		-- Inserta en la tabla de comunicaciones un registro indicando el envio del fichero
		pq_utl.log(lc, 'Inserta en la tabla de comunicaciones un registro indicando el envio del fichero');
		INSERT INTO tb_comunicaciones
		VALUES
			(sq_comunicaciones.NEXTVAL, SYSDATE, l_nombre, tipo_mov, NULL, NULL, NULL, fichero_tipo, NULL);
		COMMIT;

		-- Log de estadisticas --
		pq_utl.log(lc, '');
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || to_char(SYSDATE, 'DD/MM/YY HH24:MI:SS'), 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'Polizas de R.C Ganado definitivas enviadas   := ' || l_num_polizas, 2);
		pq_utl.log(lc, 'Polizas de R.C Ganado actualizadas   := ' || l_num_polizas_upd, 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, '', 2);

		pq_utl.log(lc, 'El proceso ha finalizado correctamente a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
		pq_utl.log(lc, '## FIN ##', 1);

		RETURN 'Se han encontrado ' || l_num_polizas || ' definitivas.';

  END;
	-- Fin del cuerpo de la funcion

END PQ_ENVIO_POLIZAS_RC_GANADO;
-- Fin de declaracion de paquete
/
SHOW ERRORS;