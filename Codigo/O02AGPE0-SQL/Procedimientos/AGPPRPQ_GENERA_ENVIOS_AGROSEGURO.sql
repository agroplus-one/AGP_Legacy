SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.pq_genera_envios_agroseguro IS

	-- Author  : U029803
	-- Created : 28/12/2010 12:33:20
	--- Purpose : Paquete de generacion envios hacia Agroseguro
	
	type t_array is TABLE OF VARCHAR2(50)INDEX BY BINARY_INTEGER;

  ---Procedimiento para actualizar el numero de polizas que se han pasado a definitiva
  PROCEDURE actualizaPolizasDefinitivas;
  --Procedimiento para actualizar el numero de anexos de modificación que se han pasado a definitivo
  PROCEDURE actualizaModifDefinitivos;
  --Procedimiento para actualizar el numero de anexos de reducción que se han pasado a definitivo
  PROCEDURE actualizaReduccDefinitivas(p_fecha IN VARCHAR2);
  --Procedimiento para actualizar el numero de siniestros que se han pasado a definitivo
  PROCEDURE actualizaSiniestrosDefinitivos;
  --Procedimiento para actualizar un dato para saber qué siniestros en definitivo no se han enviado
  --a Agroseguro.
  PROCEDURE actualizaSiniestrosNoEnviados;
  -- Procedimiento para:
   --1. Comprobar los anexos en estado definitivo del año menor al actual
   --2. Cambio de estado del anexo a estado recibido error (4)
  PROCEDURE actualizaAnexosDefNoEnviados;
	
  -- Crea los ficheros para el envío
  PROCEDURE crea_ficheros_envio(codplan IN VARCHAR2);

  FUNCTION genera_fichero_polizas(codplan IN VARCHAR2, tipopoliza IN VARCHAR2, grupoSeguro IN VARCHAR2) RETURN VARCHAR2;

  FUNCTION genera_fichero_siniestros(anosiniestros IN VARCHAR2) RETURN VARCHAR2;

  FUNCTION genera_fichero_stros_manual(P_IDENVIO IN VARCHAR2, P_NOMBRE_FICHERO IN VARCHAR2, anosiniestros IN VARCHAR2) RETURN VARCHAR2;

  FUNCTION genera_fichero_anexosrc(anoanexos IN VARCHAR2, p_fecha IN VARCHAR2) RETURN VARCHAR2;

  FUNCTION genera_fichero_anexosmp(anoanexos IN VARCHAR2) RETURN VARCHAR2;

  FUNCTION file_exists(p_fname IN VARCHAR2) RETURN BOOLEAN;

  FUNCTION get_next_name_file(codplan IN VARCHAR2, grupoSeguro IN VARCHAR2) RETURN VARCHAR2;	
													
  FUNCTION restaurar_anexos RETURN NUMBER;
	
  FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array;   
	
END pq_genera_envios_agroseguro;
/
CREATE OR REPLACE PACKAGE BODY pq_genera_envios_agroseguro IS

  gsAgricola CONSTANT VARCHAR2(3) := 'A01'; -- Grupo de seguro para p?lizas agr?colas
  gsGanado CONSTANT VARCHAR2(3) := 'G01'; -- Grupo de seguro para p?lizas ganaderas


  PROCEDURE guardar_anexos_temp(v_anexos IN VARCHAR2) AS

		lc VARCHAR2(50) := 'PQ_GENERA_ENVIOS_AGROSEGURO.guardar_anexos_temp';

	BEGIN

         pq_utl.log(lc, 'Se van a guardar los siguientes anexos:  ' || v_anexos);

         IF v_anexos IS NOT NULL THEN
         	UPDATE TB_CONFIG_AGP SET AGP_VALOR = v_anexos WHERE AGP_NEMO = 'ID_ANX_RC_PROCESADOS';
         	COMMIT;
         END IF;

	END guardar_anexos_temp;

	FUNCTION restaurar_anexos RETURN NUMBER IS
		v_array_anexos t_array;
		lc VARCHAR2(50) := 'PQ_GENERA_ENVIOS_AGROSEGURO.restaurar_anexos';
		v_anexos VARCHAR2(10000);
		
	BEGIN
		SELECT AGP_VALOR
		INTO v_anexos
		FROM TB_CONFIG_AGP
		WHERE AGP_NEMO = 'ID_ANX_RC_PROCESADOS';

		v_array_anexos := SPLIT(v_anexos, ';');

		for it in 1..v_array_anexos.Count LOOP
            	pq_utl.log(lc, 'IdAnexo = ' || TO_CHAR(v_array_anexos(it)));
                -- Atualizar la columna idstado de TB_ANEXO_RED con valor 5
                UPDATE TB_ANEXO_RED SET idestado = '5' WHERE id = v_array_anexos(it);
                -- Eliminar el ?ltimo registro de tb_anexo_red_historico_estados con idestado 2
                DELETE FROM tb_anexo_red_historico_estados WHERE ROWID IN (SELECT ROWID FROM tb_anexo_red_historico_estados where idanexo = v_array_anexos(it) AND estado = '2' ORDER BY ID DESC FETCH FIRST 1 ROWS ONLY);
        end LOOP;
        COMMIT;

         pq_utl.log(lc, 'Se han recuperado los anexos:  ' || v_anexos);

         RETURN 1;

        EXCEPTION
	    	WHEN OTHERS THEN
	        pq_utl.log(lc, 'Se ha producido un error en la recuperacion de los anexos ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
        	RETURN -1;


	END restaurar_anexos;

	FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array IS
		    i       number := 0;
		    pos     number := 0;
		    lv_str  varchar2(100) := in_string;
		    strings t_array;
  	BEGIN
  	
  	
  		-- en caso de que no contenga ningun ';'
        IF (instr(lv_str, delim) = 0) THEN
            -- verifica que el string no está vacio y contiene un mínimo de carácteres
            if (length(lv_str) > 1) THEN
                strings(1) := lv_str;
            END IF;
            
        END IF;
        
	    pos := instr(lv_str, delim, 1, 1);
	    WHILE (pos != 0) LOOP
	      i := i + 1;
	      strings(i) := substr(lv_str, 1, pos-1);
	      lv_str := substr(lv_str, pos + 1, length(lv_str));
	      pos := instr(lv_str, delim, 1, 1);
	      If pos = 0 THEN
	        strings(i + 1) := lv_str;
	      END IF;
	    END LOOP;

	    RETURN strings;

  	END SPLIT;

  --Procedimiento para actualizar el numero de polizas que se han pasado a definitiva
  PROCEDURE actualizaPolizasDefinitivas AS

    aux_cnt  number := 0;

	BEGIN

		pq_utl.log('## INICIO actualizaPolizasDefinitivas ##', 2);

    select count(*) into aux_cnt from tb_polizas p where p.idestado = 3 AND (select ph.FECHA
         from o02agpe0.VW_POLIZAS_HIST_ESTADOS_DESC ph
         where ph.IDPOLIZA = P.IDPOLIZA and ph.ESTADO=3 and rownum=1)
         <= to_date (TO_CHAR(SYSDATE, 'DD/MM/YYYY') || ' 16:40:00', 'DD/MM/YYYY HH24:MI:SS');

    pq_utl.log('Polizas definitivas: ' || aux_cnt || ' ', 2);
    update tb_config_agp c set c.agp_valor = aux_cnt where c.agp_nemo = 'POLIZAS_DEF';
    commit;

    pq_utl.log('## FIN actualizaPolizasDefinitivas ##', 2);

	END actualizaPolizasDefinitivas;

  --Procedimiento para actualizar el numero de anexos de modificaci?n que se han pasado a definitivo
  PROCEDURE actualizaModifDefinitivos AS

    aux_cnt  number := 0;

	BEGIN

		pq_utl.log('## INICIO actualizaModifDefinitivos ##', 2);

    select count(*) into aux_cnt from tb_anexo_mod m where m.estado = 5;
    pq_utl.log('Anexos de modificacion definitivos: ' || aux_cnt || ' ', 2);
    update tb_config_agp c set c.agp_valor = aux_cnt where c.agp_nemo = 'ANEXO_MOD_DEF';
    commit;

		pq_utl.log('## FIN actualizaModifDefinitivos ##', 2);

	END actualizaModifDefinitivos;

  --Procedimiento para actualizar el numero de anexos de reducci?n que se han pasado a definitivo
  PROCEDURE actualizaReduccDefinitivas(p_fecha IN VARCHAR2) AS

    aux_cnt  number := 0;

	BEGIN

		pq_utl.log('## INICIO actualizaReduccDefinitivas ##', 2);

    select count(*) into aux_cnt from tb_anexo_red r where r.idestado = 5;
    pq_utl.log('Anexos de reduccion definitivos: ' || aux_cnt || ' ', 2);
    o02agpe0.PQ_UTL.setCfg('ANEXO_RED_DEF' || p_fecha, aux_cnt);
    commit;

		pq_utl.log('## FIN actualizaReduccDefinitivas ##', 2);

	END actualizaReduccDefinitivas;

  --Procedimiento para actualizar el numero de siniestros que se han pasado a definitivo
  PROCEDURE actualizaSiniestrosDefinitivos AS

    aux_cnt  number := 0;

	BEGIN

		pq_utl.log('## INICIO actualizaSiniestrosDefinitivos ##', 2);

    select count(*) into aux_cnt from tb_siniestros s where s.estado = 5;
    pq_utl.log('Siniestros definitivos: ' || aux_cnt || ' ', 2);
    update tb_config_agp c set c.agp_valor = aux_cnt where c.agp_nemo = 'SINIESTROS_DEF';
    commit;

		pq_utl.log('## FIN actualizaSiniestrosDefinitivos ##', 2);

	END actualizaSiniestrosDefinitivos;

  --Procedimiento para actualizar un dato para saber qu? siniestros en definitivo no se han enviado
  --a Agroseguro.
  PROCEDURE actualizaSiniestrosNoEnviados AS

    TYPE            TpCursor	IS REF CURSOR;

    aux_ref         TB_POLIZAS.REFERENCIA%TYPE;
    aux_ref_tot     varchar2(2000) := '';
    num_sin_no_env  number(5) := 0;
    aux_cur_ref	    TpCursor;

	BEGIN

		pq_utl.log('## INICIO actualizaSiniestrosNoEnviados ##', 2);

    select count(*) into num_sin_no_env from o02agpe0.tb_siniestros s where s.estado = 5;
    IF num_sin_no_env > 0 THEN
        OPEN aux_cur_ref FOR select referencia from o02agpe0.tb_siniestros s, o02agpe0.tb_polizas p where s.idpoliza = p.idpoliza and s.estado = 5;
	      LOOP
            FETCH aux_cur_ref INTO aux_ref;
	          EXIT WHEN aux_cur_ref%NOTFOUND;
            aux_ref_tot := aux_ref_tot || '   - ' || aux_ref || '  ' || CHR(10);
        END LOOP;
        CLOSE aux_cur_ref;

        update tb_config_agp c set c.agp_valor = aux_ref_tot where c.agp_nemo = 'SINIESTROS_DEF_NO_ENV';
        commit;
    ELSE
        update tb_config_agp c set c.agp_valor = ' ' where c.agp_nemo = 'SINIESTROS_DEF_NO_ENV';
        commit;
    END IF;



		pq_utl.log('## FIN actualizaSiniestrosNoEnviados ##', 2);

	END actualizaSiniestrosNoEnviados;

   -- Procedimiento para:
   --1. Comprobar los anexos en estado definitivo del a?o menor al actual
   --2. Cambio de estado del anexo a estado recibido error (4)
   PROCEDURE actualizaAnexosDefNoEnviados AS

      TYPE TpCursor    IS REF CURSOR;
      l_tp_cursor      TpCursor;
      l_sql            VARCHAR2(2000);
      anoAnterior      VARCHAR2(4);
      idAnexo          NUMBER(15);
      cont             NUMBER(5) := 0;
      referencia       VARCHAR2(7);
      listReferencias  VARCHAR2(2000);
  BEGIN
    pq_utl.log('## INICIO actualizaAnexosDefinivos ##', 2);

    select to_char(sysdate, 'YYYY')-1 into anoAnterior from dual;

    l_sql :='select a.id, p.referencia from o02agpe0.tb_anexo_mod a, o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
            where a.idpoliza = p.idpoliza
             and p.lineaseguroid = l.lineaseguroid
             and a.estado = 5
             and l.codplan < '||anoAnterior;

  OPEN l_tp_cursor FOR l_sql;
       LOOP FETCH l_tp_cursor INTO idanexo,referencia;
            EXIT WHEN l_tp_cursor%NOTFOUND;

             BEGIN
                  update tb_anexo_mod a set a.estado=4  where a.id=idanexo;
                  cont:=cont+1;
                  listReferencias := listReferencias || ',' || referencia;
             END;

       END LOOP;
    IF listReferencias is null THEN
       listReferencias :=' ';
    END IF;

    update o02agpe0.tb_config_agp c set c.agp_valor = cont where c.agp_nemo = 'ANEXO_MOD_DEF_NO_ENVIADOS';
    update o02agpe0.tb_config_agp c set c.agp_valor = listReferencias where c.agp_nemo = 'ANEXO_MOD_DEF_NO_ENVIADOS_REF';
    COMMIT;

    pq_utl.log('## FIN actualizaAnexosDefinivos ##', 2);

  END actualizaAnexosDefNoEnviados;
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- PROCEDURE crea_ficheros_envio
	--
	-- Genera los ficheros de envios hacia Agroseguro
	-- * Envio de polizas
	-- * Envio de Siniestros
	-- * Envio de Anexos de Reduccion Capital
	-- * Envio de Anexos de Modificacion Poliza
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	PROCEDURE crea_ficheros_envio(codplan IN VARCHAR2) AS

		lc        VARCHAR2(50) := 'PQ_GENERA_ENVIOS_AGROSEGURO.crea_ficheros_envio';
		l_fichero VARCHAR2(8) := '';

	BEGIN

		pq_utl.log(lc, '## INI ##', 1);
		pq_utl.log(lc, '## CREA FICHERO POLIZAS ##', 1);

		l_fichero := pq_genera_envios_agroseguro.genera_fichero_polizas(codplan, 'P', 'A01');

		pq_utl.log(lc, '## FICHERO POLIZAS CREADO:  ##' + l_fichero, 1);
		pq_utl.log(lc, '## CREA FICHERO SINIESTROS ##', 1);

		l_fichero := pq_genera_envios_agroseguro.genera_fichero_siniestros(codplan);

		pq_utl.log(lc, '## FICHERO CREADO:  ##' + l_fichero, 1);
		pq_utl.log(lc, '## CREA FICHERO ANEXOS RC ##', 1);

		l_fichero := pq_genera_envios_agroseguro.genera_fichero_anexosrc(codplan, '');

		pq_utl.log(lc, '## FICHERO CREADO:  ##' + l_fichero, 1);
		pq_utl.log(lc, '## CREA FICHERO ANEXOS MP ##', 1);

		l_fichero := pq_genera_envios_agroseguro.genera_fichero_anexosmp(codplan);

		pq_utl.log(lc, '## FICHERO CREADO:  ##' + l_fichero, 1);


	END crea_ficheros_envio;

	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION genera_fichero_polizas
	--
	-- El nombre de los ficheros ser? de 8 caracteres, entre los rangos 0..9 y A..Z,
	-- excluyendo aquellos que se consideran especiales (?, ?, vocales acentuadas)
	--
	-- Ejemplo: SC912291.TXT
	-- SA => S - Seguro + A - Si el Plan es de este a?o, o + B si el plan es el del a?o pasado
	--  9 => A?o
	-- 12 => Mes
	-- 29 => Dia
	--  1 => Primer env?o. Este numero lo vamos cambiando en funci?n de si tenemos que realizar reenvios
	--
	-- El fichero .TXT contendr? una linea formada por los siquientes caracteres:
	--
	-- -C?digo de la Oficina: 5 caracteres alfanum?ricos asignados por Agroseguro.
	-- -Tipo de Documento: 2 caracteres alfanum?ricos, indicando el c?digo del tipo de
	--  informaci?n enviada.
	--  Los valores posibles se indicar?n en cada uno de los manuales, que describen el
	--  procedimiento espec?fico para su tratamiento.
	-- -Formato Texto: 1 car?cter alfanum?rico con el siguiente significado:
	--  I - ASCII
	--  N - ANSI
	-- -Campa?a *: 4 caracteres num?ricos sin signo.
	--  * El significado de este campo depender? del Tipo de Documento.
	-- -No de Documentos: 9 caracteres num?ricos sin signo. Contendr? el n?mero total
	--  de documentos (aplicaciones, remesas o siniestros) que contiene el env?o.
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	FUNCTION genera_fichero_polizas(codplan IN VARCHAR2, tipopoliza IN VARCHAR2, grupoSeguro IN VARCHAR2) RETURN VARCHAR2 IS

		lc VARCHAR2(50) := 'PQ_GENERA_ENVIOS_AGROSEGURO.genera_fichero_polizas';
		TYPE tpcursor IS REF CURSOR;

		f_fichero utl_file.file_type;
		--f_log             UTL_FILE.FILE_TYPE;
		--l_output          UTL_FILE.FILE_TYPE;
		--l_file_err        VARCHAR2(256);  -- nombre del fichero de err
		--l_file_log        VARCHAR2(256);  -- Nombre del fichero de log
		l_dir tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion
		--l_line            VARCHAR2(1000);
		l_error VARCHAR2(1000);

		l_nombre          VARCHAR2(1000);
		l_nombre_xml      VARCHAR2(1000);
		l_dir_name        VARCHAR2(1000);
		l_query           VARCHAR2(2000);
		l_tp_cursor       tpcursor;
		l_idpoliza        tb_polizas.idpoliza%TYPE;
		l_referencia      tb_polizas.referencia%TYPE;
		l_dc              tb_polizas.dc%TYPE;
    l_tiporef         tb_polizas.tiporef%TYPE;
		l_lineasegid      tb_polizas.lineaseguroid%TYPE;
		l_codmodulo       tb_polizas.codmodulo%TYPE;
		l_filamodulo      tb_comparativas_poliza.filamodulo%TYPE;
		l_filacomparativa tb_comparativas_poliza.filacomparativa%TYPE;
		l_cod_plan        tb_lineas.codplan%TYPE;
		l_cod_linea       tb_lineas.codlinea%TYPE;
		l_idenvio         tb_polizas.idenvio%TYPE;

		l_tipo_doc_sc   VARCHAR2(2) := 'SC'; -- P?lizas Seguro Creciente (Agr?cola)
    l_tipo_doc_comp VARCHAR2(2) := 'SD'; -- P?lizas Seguro Creciente Complementarias (Agr?cola)
    l_tipo_doc_gan  VARCHAR2(2) := 'SG'; -- P?lizas Seguro Creciente (Ganado)
		--l_TIPO_DOC_RD     VARCHAR2(2) := 'RD'; -- Reducci?n Capital (Seguro Creciente)
		--l_TIPO_DOC_MS     VARCHAR2(2) := 'MS'; -- Modificaci?n Seguro Agrario (Seguro Creciente)

		l_formato_texto_xml VARCHAR2(1) := 'U'; -- XML con encoding UTF-8
		--l_FORMATO_TEXTO_ASCII VARCHAR2(1) := 'I'; -- ASCII
		--l_FORMATO_TEXTO_ANSI  VARCHAR2(1) := 'N'; -- ANSI



		l_codigo_oficina VARCHAR2(5) := 'RURAL';

		l_xml_clob     CLOB;
		l_xml_clob_ref CLOB;
		l_length_clob  NUMBER;
		aux            NUMBER;
		hayenvios      BOOLEAN := FALSE;


		-- *********** Contadores ***********************************
		l_num_polizas NUMBER := 0;

		-- *********** Excepciones **********************************
		no_polizas_found EXCEPTION;
		no_envios_found EXCEPTION;

	BEGIN
		pq_utl.log(lc, '## INI ##', 1);

		-- PQ_Utl.getcfg esta en el paquete de utilidades
		-- accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).
		l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');
		-- Se guarda el path fisico del directorio
		SELECT directory_path
			INTO l_dir_name
			FROM all_directories
		 WHERE directory_name = l_dir;

		-- Se obtiene el nombre del posible env?
		l_nombre := get_next_name_file(codplan, grupoSeguro);

		BEGIN
			pq_utl.log(lc, 'Recuperando las polizas en Grabacion Definitiva ', 2);
			l_error := 'Recuperando en la tabla de polizas las polizas Grabadas Definitivas ';

			-- Ver TB_ESTADO_POLIZAS. El estado 3 es GRABADO DEFINITIVO.
      -- MPM - 05/01/2016 - Se a?ade el filtro para que s?lo se env?en las p?lizas
      --                    pagadas o con el pago pendiente de confirmaci?n (P18297)
			l_query := '
		SELECT
		      A.IDPOLIZA,
					A.LINEASEGUROID,
		      B.CODPLAN,
		      B.CODLINEA,
          A.REFERENCIA,
          A.TIPOREF
		FROM  TB_POLIZAS A,
		      TB_LINEAS B,
          TB_SC_C_LINEAS L
		WHERE A.IDESTADO =''3''
		AND   A.LINEASEGUROID = B.LINEASEGUROID
    AND   A.PAGADA IN (1,2)
		AND   B.CODPLAN = ''' || codplan || '''
    AND   A.TIPOREF = ''' || tipopoliza || '''
    AND   L.CODLINEA = B.CODLINEA
    AND   L.CODGRUPOSEGURO = ''' || grupoSeguro || '''
    AND (select ph.FECHA
         from o02agpe0.VW_POLIZAS_HIST_ESTADOS_DESC ph
         where ph.IDPOLIZA = A.IDPOLIZA and ph.ESTADO=3 and rownum=1)
         <= to_date (TO_CHAR(SYSDATE, ''DD/MM/YYYY'') || '' 16:40:00'', ''DD/MM/YYYY HH24:MI:SS'')';

			-- AND   TO_CHAR(A.FECHADEFINITIVA, ''YYYYMMDD'') = TO_CHAR(SYSDATE, ''YYYYMMDD'')';

			pq_utl.log(lc, l_query, 2);

			OPEN l_tp_cursor FOR l_query;

			LOOP
				FETCH l_tp_cursor
					INTO l_idpoliza, l_lineasegid, l_cod_plan, l_cod_linea, l_referencia, l_tiporef;
				EXIT WHEN l_tp_cursor%NOTFOUND; -- No hay mas registros


				-- Para una poliza, se selecciona el XML de tipo C?lculo (CL), y se ordenan por fecha,
				-- para recoger el ?ltimo intento.

				BEGIN

					hayenvios := TRUE;

					IF (l_num_polizas = 0) THEN
						--Se inserta el movimiento en la tabla de comunicaciones
						SELECT sq_comunicaciones.NEXTVAL
							INTO l_idenvio
							FROM dual;

						INSERT INTO tb_comunicaciones
							(idenvio, fecha_envio, fichero_envio, tipo_mov, resultado, fichero_tipo)
						VALUES
							(l_idenvio, SYSDATE, l_nombre, 'E', NULL, 'P');

						--Abrimos el ZIP para a?adirle los ficheros a enviar
						pq_utlzip.abrirzip(l_dir_name || '/' || l_nombre || '.ZIP');
					END IF;

          IF (l_tiporef = 'P') THEN

    					SELECT cp.filamodulo, cp.filacomparativa, cp.codmodulo
    						INTO l_filamodulo, l_filacomparativa, l_codmodulo
    						FROM tb_comparativas_poliza cp
    					 WHERE cp.idpoliza = l_idpoliza
    						 AND rownum < 2;

    					UPDATE tb_polizas pol
    							   --SET referencia = l_referencia, dc = l_dc,
                     SET codmodulo = l_codmodulo
    						     WHERE idpoliza = l_idpoliza;

          END IF;

					COMMIT;

					--Obtener el xml de la p?liza
					SELECT xmlacusecontratacion, dbms_lob.getlength(xmlacusecontratacion)
						INTO l_xml_clob, l_length_clob
						FROM tb_polizas
					 WHERE idpoliza = l_idpoliza;

          IF (l_tiporef = 'P') THEN

              l_xml_clob_ref := l_xml_clob;

              --Comprobamos que el namespace del xml es el correcto
              IF (instr(l_xml_clob_ref, 'http://www.agroseguro.es/SeguroAgrario/CalculoSeguroAgrario') > 0) THEN
                 l_xml_clob_ref := REPLACE(l_xml_clob_ref, 'http://www.agroseguro.es/SeguroAgrario/CalculoSeguroAgrario',
    																			'http://www.agroseguro.es/SeguroAgrario/Contratacion');

					       --Si hemos cambiado el namespace, actualizamos en base de datos el xml de la p?liza
					       UPDATE tb_polizas
						            SET xmlacusecontratacion = l_xml_clob_ref--, FECHAENVIO = sysdate
					              WHERE idpoliza = l_idpoliza;

              END IF;
          ELSE
              l_xml_clob_ref := l_xml_clob;
          END IF;

					pq_utl.log(lc, aux);

				EXCEPTION
					WHEN no_data_found THEN
						l_error := 'No se han encontrado envios para la poliza ' || l_idpoliza;
						RAISE no_envios_found;
				END;

				l_nombre_xml := l_referencia || l_cod_plan || l_cod_linea;

				-- *******************************+++++++++++++++++++++*************************
				-- Creacion del XML de la poliza. Abrimos el fichero de salida con extension .XML
				-- ****************************************************+++++++++++++++++++++****
				DBMS_LOB.CLOB2FILE(l_xml_clob_ref, l_dir, l_nombre_xml || '.XML', 0, 'wb');

				--Se comprime el XML dentro del .ZIP. El .ZIP tiene el mismo nombre que el TXT
				pq_utl.log(lc,
									 'Comprimiendo el archivo ' || l_dir_name || '/' || l_nombre_xml || '.XML en el archivo ' || l_dir_name || '/' || l_nombre ||
									 '.ZIP');
				pq_utlzip.compressfile(l_dir_name || '/' || l_nombre_xml || '.XML',
															 l_dir_name || '/' || l_nombre || '.ZIP');

				--Se borra el XML
				pq_utl.log(lc, 'Eliminando el archivo ' || l_dir_name || '/' || l_nombre_xml || '.XML');
				pq_utlzip.borraxmls(l_dir_name || '/' || l_nombre_xml || '.XML');

				--Se actualiza el id_envio de la poliza y su estado a Enviada Pendiente de Confirmar (TB_ESTADOS_POLIZA)
				UPDATE tb_polizas
					     SET idenvio = l_idenvio, idestado = 5, fechaenvio = sysdate
				       WHERE idpoliza = l_idpoliza;
        -- TMR Insertamos en el historico de estados de poliza
        INSERT INTO TB_POLIZAS_HISTORICO_ESTADOS (id,idpoliza,codusuario,fecha,estado)
                 values (SQ_POLIZAS_HISTORICO_ESTADOS.nextval,l_idpoliza,null,sysdate,5);


				COMMIT;

				l_num_polizas := l_num_polizas + 1;

			END LOOP;


			IF (hayenvios = FALSE) THEN
				l_error := 'No se han encontrado polizas definitivas para el plan '
                   || codplan || ', tipo ' || tipopoliza || ' y grupo de seguro ' || grupoSeguro;
				RAISE no_polizas_found;
			END IF;



			--Cerramos el ZIP
			pq_utlzip.cerrarzip;

			-- ********************************************************
			-- Generacion TXT
			-- ********************************************************
			f_fichero := utl_file.fopen(location     => l_dir,
																	filename     => l_nombre || '.TXT',
																	open_mode    => 'w',
																	max_linesize => pq_typ.max_linefilesizewrite);

      -- Se guarda el contenido del TXT (Ver arriba formato)
      -- Si las p?lizas son agr?colas
      IF (grupoSeguro = gsAgricola) THEN
         IF (l_tiporef = 'P') THEN
         			utl_file.put_line(f_fichero,
  												l_codigo_oficina || l_tipo_doc_sc || l_formato_texto_xml || codplan ||
  												lpad(l_num_polizas, 9, '0'));
         ELSE
         			utl_file.put_line(f_fichero,
  												l_codigo_oficina || l_tipo_doc_comp || l_formato_texto_xml || codplan ||
  												lpad(l_num_polizas, 9, '0'));
         END IF;
      -- Si las p?lizas son de ganado
      ELSE
          utl_file.put_line(f_fichero,
  												l_codigo_oficina || l_tipo_doc_gan || l_formato_texto_xml || codplan ||
  												lpad(l_num_polizas, 9, '0'));
      END IF;

			UPDATE tb_comunicaciones
				 SET resultado = 'CORRECTO'
			 WHERE idenvio = l_idenvio;

		EXCEPTION
			WHEN no_data_found THEN
				l_error := 'No se han encontrado polizas definitivas';
				RAISE no_polizas_found;
		END;


		-- ********************************************************
		-- Cerramos el fichero.
		-- ********************************************************
		COMMIT;
		CLOSE l_tp_cursor;
		utl_file.fclose(f_fichero);

		-- ********************************************************
		-- Guardamos los resultados en el fichero de log
		-- ********************************************************
		pq_utl.log(lc, ' ', 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || to_char(SYSDATE, 'DD/MM/YY HH24:MI:SS'), 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'Polizas definitivas   := ' || l_num_polizas, 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, ' ', 2);

		pq_utl.log(lc, 'Fin del proceso ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
		pq_utl.log(lc, '## FIN ##', 1);

		-- Se devuelve el nombre del fichero generado
		RETURN l_nombre;


	EXCEPTION
		WHEN no_envios_found THEN
			ROLLBACK;
      pq_utl.log(lc, 'no_envios_found ' || SQLCODE || l_error || ' [' || SQLERRM || '] ***', 2);
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN no_polizas_found THEN
			ROLLBACK;
      pq_utl.log(lc, 'no_polizas_found ' || SQLCODE || l_error || ' [' || SQLERRM || '] ***', 2);
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN OTHERS THEN
			ROLLBACK;
      pq_utl.log(lc, 'others ' || SQLCODE || l_error || ' [' || SQLERRM || '] ***', 2);
			pq_err.raiser(SQLCODE, 'Error al generar los ficheros de genera_fichero_polizas' || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;

	END genera_fichero_polizas;



	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION genera_fichero_siniestros
	--
	-- El nombre de los ficheros ser? de 8 caracteres, entre los rangos 0..9 y A..Z,
	-- excluyendo aquellos que se consideran especiales (?, ?, vocales acentuadas)
	--
	-- Ejemplo: SC912291.TXT
	-- SA => S - Seguro + A - Si el Plan es de este a?o, o + B si el plan es el del a?o pasado
	--  9 => A?o
	-- 12 => Mes
	-- 29 => Dia
	--  1 => Primer env?o. Este numero lo vamos cambiando en funci?n de si tenemos que realizar reenvios
	--
	-- El fichero .TXT contendr? una linea formada por los siquientes caracteres:
	--
	-- -C?digo de la Oficina: 5 caracteres alfanum?ricos asignados por Agroseguro.
	-- -Tipo de Documento: 2 caracteres alfanum?ricos, indicando el c?digo del tipo de
	--  informaci?n enviada.
	--  Los valores posibles se indicar?n en cada uno de los manuales, que describen el
	--  procedimiento espec?fico para su tratamiento.
	-- -Formato Texto: 1 car?cter alfanum?rico con el siguiente significado:
	--  I - ASCII
	--  N - ANSI
	-- -Campa?a *: 4 caracteres num?ricos sin signo.
	--  * El significado de este campo depender? del Tipo de Documento.
	-- -No de Documentos: 9 caracteres num?ricos sin signo. Contendr? el n?mero total
	--  de documentos (aplicaciones, remesas o siniestros) que contiene el env?o.
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	FUNCTION genera_fichero_siniestros(anosiniestros IN VARCHAR2) RETURN VARCHAR2 IS

		lc VARCHAR2(60) := 'PQ_GENERA_ENVIOS_AGROSEGURO.genera_fichero_siniestros';
		TYPE tpcursor IS REF CURSOR;

		f_fichero utl_file.file_type;
		l_dir     tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion
		l_error   VARCHAR2(1000);

		l_nombre      VARCHAR2(1000);
		l_nombre_xml  VARCHAR2(1000);
		l_dir_name    VARCHAR2(1000);
		l_query       VARCHAR2(2000);
		l_tp_cursor   tpcursor;
		l_idsiniestro tb_polizas.idpoliza%TYPE;
		l_idenvio     tb_polizas.idenvio%TYPE;

		l_tipo_doc_ss VARCHAR2(2) := 'SS'; -- Siniestros (Seguro Creciente)

		l_formato_texto_xml VARCHAR2(1) := 'U'; -- XML con encoding UTF-8

		l_codigo_oficina VARCHAR2(5) := 'RURAL';

		l_xml_clob    CLOB;
		l_length_clob NUMBER;
		aux           NUMBER;


		-- *********** Contadores ***********************************
		l_num_siniestros NUMBER := 0;

		-- *********** Excepciones **********************************
		no_siniestros_found EXCEPTION;
		no_envios_found EXCEPTION;

	BEGIN
		pq_utl.log(lc, '## INI ##', 1);

		-- PQ_Utl.getcfg esta en el paquete de utilidades
		-- accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).
		l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');
		-- Se guarda el path fisico del directorio
		SELECT directory_path
			INTO l_dir_name
			FROM all_directories
		 WHERE directory_name = l_dir;

		-- Se obtiene el nombre del posible env?
		l_nombre := get_next_name_file(anosiniestros, NULL);

		BEGIN
			pq_utl.log(lc, 'Recuperando los Siniestros Provisionales ', 2);
			l_error := 'Recuperando en la tabla de Siniestros los Provisionales ';

			-- Ver TB_SINIESTRO. El estado 1 es Provisional.
			l_query := '
		SELECT
		     DISTINCT(S.ID)
		FROM TB_SINIESTROS S,
				 TB_SINIESTRO_PARCELAS SP
		WHERE S.ESTADO =''5''
		AND SP.IDSINIESTRO = S.ID
		AND SP.ALTAENSINIESTRO = ''S''
    AND to_char(S.fechaocurrencia, '' yyyy '') = ' || anosiniestros;

			OPEN l_tp_cursor FOR l_query;

			pq_utl.log(lc, l_query, 2);

			IF l_tp_cursor%NOTFOUND THEN
				l_error := 'No se han encontrado siniestros definitivos para el a?o' || anosiniestros;
				RAISE no_siniestros_found;
			END IF;

			--Se inserta el movimiento en la tabla de comunicaciones
			SELECT sq_comunicaciones.NEXTVAL
				INTO l_idenvio
				FROM dual;

			INSERT INTO tb_comunicaciones
				(idenvio, fecha_envio, fichero_envio, tipo_mov, resultado, fichero_tipo)
			VALUES
				(l_idenvio, SYSDATE, l_nombre, 'E', NULL, 'S');

			--Abrimos el ZIP para a?adirle los ficheros a enviar
			pq_utlzip.abrirzip(l_dir_name || '/' || l_nombre || '.ZIP');

			FETCH l_tp_cursor
				INTO l_idsiniestro;

			LOOP
				-- Para cada siniesstro se genera el XML, actualizo antes el numero interno dentro del envio ya que va en el XML
				BEGIN

					l_nombre_xml := lpad(l_num_siniestros + 1, 6, '0');

					UPDATE tb_siniestros s
						 SET s.NUMINTERNOENVIO = l_nombre_xml
					 WHERE id = l_idsiniestro;

					COMMIT;

					aux := pq_creaxml_siniestros.generaxmlsiniestro(l_idsiniestro);

					SELECT s.xml, dbms_lob.getlength(s.xml)
						INTO l_xml_clob, l_length_clob
						FROM tb_siniestros s
					 WHERE s.id = l_idsiniestro;

				EXCEPTION
					WHEN no_data_found THEN
						l_error := 'No se han encontrado XML Siniestro ' || l_idsiniestro;
						RAISE no_envios_found;
				END;



				-- *******************************+++++++++++++++++++++*************************
				-- Creacion del XML del Siniestro. Abrimos el fichero de salida con extension .XML
				-- ****************************************************+++++++++++++++++++++****
				DBMS_LOB.CLOB2FILE(l_xml_clob, l_dir, l_nombre_xml || '.XML', 0, 'wb');

				--Se comprime el XML dentro del .ZIP. El .ZIP tiene el mismo nombre que el TXT
				pq_utl.log(lc,
									 'Comprimiendo el archivo ' || l_dir_name || '/' || l_nombre_xml || '.XML en el archivo ' || l_dir_name || '/' || l_nombre ||
									 '.ZIP');
				pq_utlzip.compressfile(l_dir_name || '/' || l_nombre_xml || '.XML', l_dir_name || '/' || l_nombre || '.ZIP');

				--Se borra el XML
				pq_utl.log(lc, 'Eliminando el archivo ' || l_dir_name || '/' || l_nombre_xml || '.XML');
				pq_utlzip.borraxmls(l_dir_name || '/' || l_nombre_xml || '.XML');

				--Se actualiza el Estado del Siniestro a Enviada con el envio
				UPDATE tb_siniestros s
					 SET s.idenvio = l_idenvio, s.estado = 2
				 WHERE id = l_idsiniestro;

        -- ASF Insertamos en el historico de estados de siniestro
        INSERT INTO TB_SINIESTRO_HISTORICO_ESTADOS (id,idsiniestro,codusuario,fecha,estado)
           values (SQ_SINIESTRO_HISTORICO_ESTADOS.nextval, l_idsiniestro, null, sysdate, 2);

				COMMIT;

				l_num_siniestros := l_num_siniestros + 1;

				FETCH l_tp_cursor
					INTO l_idsiniestro;
				EXIT WHEN l_tp_cursor%NOTFOUND; -- No hay mas registros
			END LOOP;

			--Cerramos el ZIP
			pq_utlzip.cerrarzip;

			-- ********************************************************
			-- Generacion TXT
			-- ********************************************************
			f_fichero := utl_file.fopen(location     => l_dir,
																	filename     => l_nombre || '.TXT',
																	open_mode    => 'w',
																	max_linesize => pq_typ.max_linefilesizewrite);
			-- Se guarda el contenido del TXT (Ver arriba formato)
			utl_file.put_line(f_fichero,
												l_codigo_oficina || l_tipo_doc_ss || l_formato_texto_xml || anosiniestros ||
												lpad(l_num_siniestros, 9, '0'));

			UPDATE tb_comunicaciones
				 SET resultado = 'CORRECTO'
			 WHERE idenvio = l_idenvio;

		EXCEPTION
			WHEN no_data_found THEN
				l_error := 'No se han encontrado siniestros a enviar';
				RAISE no_siniestros_found;
		END;


		-- ********************************************************
		-- Cerramos el fichero.
		-- ********************************************************
		COMMIT;
		CLOSE l_tp_cursor;
		utl_file.fclose(f_fichero);

		-- ********************************************************
		-- Guardamos los resultados en el fichero de log
		-- ********************************************************
		pq_utl.log(lc, ' ', 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || to_char(SYSDATE, 'DD/MM/YY HH24:MI:SS'), 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'Siniestros definitivas   := ' || l_num_siniestros, 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, ' ', 2);

		pq_utl.log(lc, 'Fin del proceso ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
		pq_utl.log(lc, '## FIN ##', 1);

		-- Se devuelve el nombre del fichero generado
		RETURN l_nombre;


	EXCEPTION
		WHEN no_envios_found THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN no_siniestros_found THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN OTHERS THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, 'Error al generar los ficheros de genera_fichero_siniestros' || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;

	END genera_fichero_siniestros;

  --Funci?n para regenerar un fichero de siniestros cuando el batch ha fallado.
  --Recibe el identificador del envio y el nombre del fichero a enviar
  FUNCTION genera_fichero_stros_manual(P_IDENVIO IN VARCHAR2, P_NOMBRE_FICHERO IN VARCHAR2, anosiniestros IN VARCHAR2) RETURN VARCHAR2 IS

		lc VARCHAR2(60) := 'PQ_GENERA_ENVIOS_AGROSEGURO.genera_fichero_siniestros';
		TYPE tpcursor IS REF CURSOR;

		f_fichero utl_file.file_type;
		l_dir     tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion
		l_error   VARCHAR2(1000);

		l_nombre      VARCHAR2(1000);
		l_nombre_xml  VARCHAR2(1000);
		l_dir_name    VARCHAR2(1000);
		l_query       VARCHAR2(2000);
		l_tp_cursor   tpcursor;
		v_nombre_xml varchar2(15);
    v_fichero    CLOB;

		l_tipo_doc_ss VARCHAR2(2) := 'SS'; -- Siniestros (Seguro Creciente)

		l_formato_texto_xml VARCHAR2(1) := 'U'; -- XML con encoding UTF-8

		l_codigo_oficina VARCHAR2(5) := 'RURAL';

		l_length_clob NUMBER;

		-- *********** Contadores ***********************************
		l_num_siniestros NUMBER := 0;

		-- *********** Excepciones **********************************
		no_siniestros_found EXCEPTION;
		no_envios_found EXCEPTION;

	BEGIN
		pq_utl.log(lc, '## INI ##', 1);

		-- PQ_Utl.getcfg esta en el paquete de utilidades
		-- accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).
		l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');
		-- Se guarda el path fisico del directorio
		SELECT directory_path
			INTO l_dir_name
			FROM all_directories
		 WHERE directory_name = l_dir;

		BEGIN
			pq_utl.log(lc, 'Recuperando los Siniestros Provisionales ', 2);
			l_error := 'Recuperando en la tabla de Siniestros los Provisionales ';

			-- Ver TB_SINIESTRO. El estado 1 es Provisional.
			l_query := '
		SELECT
		     lpad(s.numinternoenvio, 6, ''0'') as fichero, s.xml, dbms_lob.getlength(s.xml)
		FROM TB_SINIESTROS S
		WHERE S.IDENVIO = ' || P_IDENVIO;

			OPEN l_tp_cursor FOR l_query;

			pq_utl.log(lc, l_query, 2);

			IF l_tp_cursor%NOTFOUND THEN
				l_error := 'No se han encontrado siniestros para el envio ' || P_IDENVIO;
				RAISE no_siniestros_found;
			END IF;

			--Se inserta el movimiento en la tabla de comunicaciones
			UPDATE tb_comunicaciones SET fecha_envio = sysdate, fichero_envio = P_NOMBRE_FICHERO
         WHERE IDENVIO = P_IDENVIO;

			--Abrimos el ZIP para a?adirle los ficheros a enviar
			pq_utlzip.abrirzip(l_dir_name || '/' || P_NOMBRE_FICHERO || '.ZIP');

			FETCH l_tp_cursor
				INTO v_nombre_xml, v_fichero, l_length_clob;

			LOOP
  			-- *******************************+++++++++++++++++++++*************************
				-- Creacion del XML del Siniestro. Abrimos el fichero de salida con extension .XML
				-- ****************************************************+++++++++++++++++++++****
				DBMS_LOB.CLOB2FILE(v_fichero, l_dir, v_nombre_xml || '.XML', 0, 'wb');

				--Se comprime el XML dentro del .ZIP. El .ZIP tiene el mismo nombre que el TXT
				pq_utl.log(lc,
									 'Comprimiendo el archivo ' || l_dir_name || '/' || v_nombre_xml || '.XML en el archivo ' || l_dir_name || '/' || l_nombre ||
									 '.ZIP');
				pq_utlzip.compressfile(l_dir_name || '/' || v_nombre_xml || '.XML', l_dir_name || '/' || l_nombre || '.ZIP');

				--Se borra el XML
				pq_utl.log(lc, 'Eliminando el archivo ' || l_dir_name || '/' || v_nombre_xml || '.XML');
				pq_utlzip.borraxmls(l_dir_name || '/' || v_nombre_xml || '.XML');

        --Actualizo el numero de siniestros para ponerlo en el txt
				l_num_siniestros := l_num_siniestros + 1;

				FETCH l_tp_cursor
					INTO v_nombre_xml, v_fichero, l_length_clob;
				EXIT WHEN l_tp_cursor%NOTFOUND; -- No hay mas registros
			END LOOP;

			--Cerramos el ZIP
			pq_utlzip.cerrarzip;

			-- ********************************************************
			-- Generacion TXT
			-- ********************************************************
			f_fichero := utl_file.fopen(location     => l_dir,
																	filename     => P_NOMBRE_FICHERO || '.TXT',
																	open_mode    => 'w',
																	max_linesize => pq_typ.max_linefilesizewrite);
			-- Se guarda el contenido del TXT (Ver arriba formato)
			utl_file.put_line(f_fichero,
												l_codigo_oficina || l_tipo_doc_ss || l_formato_texto_xml || anosiniestros ||
												lpad(l_num_siniestros, 9, '0'));

		EXCEPTION
			WHEN no_data_found THEN
				l_error := 'No se han encontrado siniestros a enviar';
				RAISE no_siniestros_found;
		END;


		-- ********************************************************
		-- Cerramos el fichero.
		-- ********************************************************
		COMMIT;
		CLOSE l_tp_cursor;
		utl_file.fclose(f_fichero);

		-- ********************************************************
		-- Guardamos los resultados en el fichero de log
		-- ********************************************************
		pq_utl.log(lc, ' ', 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'ESTADISTICAS DEL FICHERO ' || P_NOMBRE_FICHERO || ' FECHA ' || to_char(SYSDATE, 'DD/MM/YY HH24:MI:SS'), 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'Siniestros definitivas   := ' || l_num_siniestros, 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, ' ', 2);

		pq_utl.log(lc, 'Fin del proceso ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
		pq_utl.log(lc, '## FIN ##', 1);

		-- Se devuelve el nombre del fichero generado
		RETURN P_NOMBRE_FICHERO;


	EXCEPTION
		WHEN no_envios_found THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN no_siniestros_found THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN OTHERS THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, 'Error al generar los ficheros de genera_fichero_siniestros' || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;

	END genera_fichero_stros_manual;

	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION genera_fichero_anexosRC
	--
	-- El nombre de los ficheros ser? de 8 caracteres, entre los rangos 0..9 y A..Z,
	-- excluyendo aquellos que se consideran especiales (?, ?, vocales acentuadas)
	--
	-- Ejemplo: SC912291.TXT
	-- SA => S - Seguro + A - Si el Plan es de este a?o, o + B si el plan es el del a?o pasado
	--  9 => A?o
	-- 12 => Mes
	-- 29 => Dia
	--  1 => Primer env?o. Este numero lo vamos cambiando en funci?n de si tenemos que realizar reenvios
	--
	-- El fichero .TXT contendr? una linea formada por los siquientes caracteres:
	--
	-- -C?digo de la Oficina: 5 caracteres alfanum?ricos asignados por Agroseguro.
	-- -Tipo de Documento: 2 caracteres alfanum?ricos, indicando el c?digo del tipo de
	--  informaci?n enviada.
	--  Los valores posibles se indicar?n en cada uno de los manuales, que describen el
	--  procedimiento espec?fico para su tratamiento.
	-- -Formato Texto: 1 car?cter alfanum?rico con el siguiente significado:
	--  I - ASCII
	--  N - ANSI
	-- -Campa?a *: 4 caracteres num?ricos sin signo.
	--  * El significado de este campo depender? del Tipo de Documento.
	-- -No de Documentos: 9 caracteres num?ricos sin signo. Contendr? el n?mero total
	--  de documentos (aplicaciones, remesas o siniestros) que contiene el env?o.
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	FUNCTION genera_fichero_anexosrc(anoanexos IN VARCHAR2, p_fecha IN VARCHAR2) RETURN VARCHAR2 IS

	        -- REQ.05
        type tparrstr IS VARRAY(100000) of VARCHAR2(20);
        v_ids_anexos tparrstr := tparrstr();

        v_anexos VARCHAR2(10000);
		v_restaura NUMBER; -- variable de resultado

		lc VARCHAR2(60) := 'PQ_GENERA_ENVIOS_AGROSEGURO.genera_fichero_anexosRC';
		TYPE tpcursor IS REF CURSOR;

		f_fichero utl_file.file_type;
		l_dir     tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion
		l_error   VARCHAR2(1000);

		l_nombre     VARCHAR2(1000);
		l_nombre_xml VARCHAR2(1000);
		l_dir_name   VARCHAR2(1000);
		l_query      VARCHAR2(2000);
		l_tp_cursor  tpcursor;
		l_idanexo    tb_polizas.idpoliza%TYPE;
		l_refpoliza  tb_polizas.referencia%TYPE;
		l_cod_plan   tb_lineas.codplan%TYPE;
		l_cod_linea  tb_lineas.codlinea%TYPE;
		l_idenvio    tb_polizas.idenvio%TYPE;

		l_tipo_doc_rd VARCHAR2(2) := 'RD'; -- Reducci?n Capital (Seguro Creciente)

		l_formato_texto_xml VARCHAR2(1) := 'U'; -- XML con encoding UTF-8

		l_codigo_oficina VARCHAR2(5) := 'RURAL';

		l_xml_clob    CLOB;
		l_length_clob NUMBER;
		aux           NUMBER;

		-- *********** Contadores ***********************************
		l_num_anexos NUMBER := 0;

		-- *********** Excepciones **********************************
		no_anexos_found EXCEPTION;
		no_envios_found EXCEPTION;
		
		v_fecha_planif DATE;

	BEGIN
		pq_utl.log(lc, '## INI ##', 1);

		pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);
	
		if p_fecha = '' then
			v_fecha_planif := sysdate;
		else
			-- Convertimos a DATE la fecha de planificacion recibida por parametro
			v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');
		end if;

		-- PQ_Utl.getcfg esta en el paquete de utilidades
		-- accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).
		l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');
		-- Se guarda el path fisico del directorio
		SELECT directory_path
			INTO l_dir_name
			FROM all_directories
		 WHERE directory_name = l_dir;

		-- Se obtiene el nombre del posible env?
		l_nombre := get_next_name_file(anoanexos, NULL);

		BEGIN
			pq_utl.log(lc, 'Recuperando los Anexos RC Provisionales ', 2);
			l_error := 'Recuperando en la tabla de Anexos RC los Provisionales ';

			-- Ver TB_ANEXO_RED. El estado 1 es Provisional.
			l_query := '
		SELECT
		      A.ID,
					P.REFERENCIA,
					L.CODLINEA,
					L.CODPLAN
		FROM TB_ANEXO_RED A,
					TB_POLIZAS P,
					TB_LINEAS L
		WHERE A.IDESTADO =''5''
			AND A.IDPOLIZA = P.IDPOLIZA
			AND P.LINEASEGUROID = L.LINEASEGUROID AND L.CODPLAN = ' || anoanexos;


			OPEN l_tp_cursor FOR l_query;

			pq_utl.log(lc, l_query, 2);

			IF l_tp_cursor%NOTFOUND THEN
				l_error := 'No se han encontrado anexos definitivas para el Plan ' || anoanexos;
				RAISE no_anexos_found;
			END IF;

			--Se inserta el movimiento en la tabla de comunicaciones
			SELECT sq_comunicaciones.NEXTVAL
				INTO l_idenvio
				FROM dual;

			INSERT INTO tb_comunicaciones
				(idenvio, fecha_envio, fichero_envio, tipo_mov, resultado, fichero_tipo)
			VALUES
				(l_idenvio, v_fecha_planif, l_nombre, 'E', NULL, 'R');

			--Abrimos el ZIP para a?adirle los ficheros a enviar
			pq_utlzip.abrirzip(l_dir_name || '/' || l_nombre || '.ZIP');

			FETCH l_tp_cursor
				INTO l_idanexo, l_refpoliza, l_cod_linea, l_cod_plan;

			LOOP
				-- Para cada anexo se genera el XML de anexo
				BEGIN

					aux := pq_creaxml_anexo_rc.generaxmlanexorc(l_idanexo);

					SELECT ar.xml, dbms_lob.getlength(ar.xml)
						INTO l_xml_clob, l_length_clob
						FROM tb_anexo_red ar
					 WHERE ar.id = l_idanexo;

				EXCEPTION
					WHEN no_data_found THEN
						l_error := 'No se han encontrado XML Anexos ' || l_idanexo;
						RAISE no_envios_found;
				END;

				l_nombre_xml := l_refpoliza || l_cod_plan || l_cod_linea;

				-- *******************************+++++++++++++++++++++*************************
				-- Creacion del XML del anexo RC. Abrimos el fichero de salida con extension .XML
				-- ****************************************************+++++++++++++++++++++****
				DBMS_LOB.CLOB2FILE(l_xml_clob, l_dir, l_nombre_xml || '.XML', 0, 'wb');

				--Se comprime el XML dentro del .ZIP. El .ZIP tiene el mismo nombre que el TXT
				pq_utl.log(lc,
									 'Comprimiendo el archivo ' || l_dir_name || '/' || l_nombre_xml || '.XML en el archivo ' || l_dir_name || '/' || l_nombre ||
									 '.ZIP');
				pq_utlzip.compressfile(l_dir_name || '/' || l_nombre_xml || '.XML',
															 l_dir_name || '/' || l_nombre || '.ZIP');

				--Se borra el XML
				pq_utl.log(lc, 'Eliminando el archivo ' || l_dir_name || '/' || l_nombre_xml || '.XML');
				pq_utlzip.borraxmls(l_dir_name || '/' || l_nombre_xml || '.XML');

				--Se actualiza el Estado del Anexo a Enviada con el envio
				UPDATE tb_anexo_red
					 SET idenvio = l_idenvio, idestado = 2, FECHAENVIO = v_fecha_planif
				 WHERE id = l_idanexo;

        -- ASF Insertamos en el historico de estados del anexo de reduccion
        INSERT INTO TB_ANEXO_RED_HISTORICO_ESTADOS (id,idanexo,codusuario,fecha,estado)
           values (SQ_ANEXO_RED_HISTORICO_ESTADOS.nextval, l_idanexo, null, v_fecha_planif, 2);

				COMMIT;


                -- REQ.05 - Almacenar el identificador del anexo
                v_ids_anexos.extend;
                v_ids_anexos(v_ids_anexos.Count):= l_idanexo;

                pq_utl.log(lc, 'IdAnexo = ' || TO_CHAR(l_idanexo));

                IF v_anexos IS NULL THEN
                	v_anexos := l_idanexo;
                ELSE
                	v_anexos := v_anexos || ';' || l_idanexo;
                END IF;
                --

				l_num_anexos := l_num_anexos + 1;

				FETCH l_tp_cursor
					INTO l_idanexo, l_refpoliza, l_cod_linea, l_cod_plan;
				EXIT WHEN l_tp_cursor%NOTFOUND; -- No hay mas registros
			END LOOP;

			--Cerramos el ZIP
			pq_utlzip.cerrarzip;

			-- ********************************************************
			-- Generacion TXT
			-- ********************************************************
			f_fichero := utl_file.fopen(location     => l_dir,
																	filename     => l_nombre || '.TXT',
																	open_mode    => 'w',
																	max_linesize => pq_typ.max_linefilesizewrite);
			-- Se guarda el contenido del TXT (Ver arriba formato)
			utl_file.put_line(f_fichero,
												l_codigo_oficina || l_tipo_doc_rd || l_formato_texto_xml || anoanexos ||
												lpad(l_num_anexos, 9, '0'));

			UPDATE tb_comunicaciones
				 SET resultado = 'CORRECTO'
			 WHERE idenvio = l_idenvio;

		EXCEPTION
			WHEN no_data_found THEN
				l_error := 'No se han encontrado anexos a enviar';
				RAISE no_anexos_found;
		END;


		-- ********************************************************
		-- Cerramos el fichero.
		-- ********************************************************
		COMMIT;
		CLOSE l_tp_cursor;
		utl_file.fclose(f_fichero);

		-- ********************************************************
		-- Guardamos los resultados en el fichero de log
		-- ********************************************************
		pq_utl.log(lc, ' ', 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || to_char(SYSDATE, 'DD/MM/YY HH24:MI:SS'), 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'Anexos definitivas   := ' || l_num_anexos, 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, ' ', 2);

		pq_utl.log(lc, 'Fin del proceso ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
		pq_utl.log(lc, '## FIN ##', 1);

		-- Guardamos los ids de los anexos en una tabla temporal para restaurarlos en caso de error
		guardar_anexos_temp(v_anexos);

		-- Se devuelve el nombre del fichero generado
		RETURN l_nombre;


	EXCEPTION
		WHEN no_envios_found THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN no_anexos_found THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN OTHERS THEN
			pq_utl.log(lc, 'Error al generar los ficheros de ' || to_char(v_ids_anexos.Count) || ' anexos');
			v_restaura := restaurar_anexos;

            --
			pq_err.raiser(SQLCODE, 'Error al generar los ficheros de genera_fichero_anexosRC' || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;

	END genera_fichero_anexosrc;


	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION genera_fichero_anexosMP
	--
	-- El nombre de los ficheros ser? de 8 caracteres, entre los rangos 0..9 y A..Z,
	-- excluyendo aquellos que se consideran especiales (?, ?, vocales acentuadas)
	--
	-- Ejemplo: SC912291.TXT
	-- SA => S - Seguro + A - Si el Plan es de este a?o, o + B si el plan es el del a?o pasado
	--  9 => A?o
	-- 12 => Mes
	-- 29 => Dia
	--  1 => Primer env?o. Este numero lo vamos cambiando en funci?n de si tenemos que realizar reenvios
	--
	-- El fichero .TXT contendr? una linea formada por los siquientes caracteres:
	--
	-- -C?digo de la Oficina: 5 caracteres alfanum?ricos asignados por Agroseguro.
	-- -Tipo de Documento: 2 caracteres alfanum?ricos, indicando el c?digo del tipo de
	--  informaci?n enviada.
	--  Los valores posibles se indicar?n en cada uno de los manuales, que describen el
	--  procedimiento espec?fico para su tratamiento.
	-- -Formato Texto: 1 car?cter alfanum?rico con el siguiente significado:
	--  I - ASCII
	--  N - ANSI
	-- -Campa?a *: 4 caracteres num?ricos sin signo.
	--  * El significado de este campo depender? del Tipo de Documento.
	-- -No de Documentos: 9 caracteres num?ricos sin signo. Contendr? el n?mero total
	--  de documentos (aplicaciones, remesas o siniestros) que contiene el env?o.
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	FUNCTION genera_fichero_anexosmp(anoanexos IN VARCHAR2) RETURN VARCHAR2 IS

		lc VARCHAR2(60) := 'PQ_GENERA_ENVIOS_AGROSEGURO.genera_fichero_anexosMP';
		TYPE tpcursor IS REF CURSOR;

		f_fichero utl_file.file_type;
		l_dir     tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion
		l_error   VARCHAR2(1000);

		l_nombre     VARCHAR2(1000);
		l_nombre_xml VARCHAR2(1000);
		l_dir_name   VARCHAR2(1000);
		l_query      VARCHAR2(2000);
		l_tp_cursor  tpcursor;
		l_idanexo    tb_polizas.idpoliza%TYPE;
		l_refpoliza  tb_polizas.referencia%TYPE;
		l_cod_plan   tb_lineas.codplan%TYPE;
		l_cod_linea  tb_lineas.codlinea%TYPE;
		l_idenvio    tb_polizas.idenvio%TYPE;

		l_tipo_doc_ms VARCHAR2(2) := 'MS'; -- Modificacion Poliza (Seguro Creciente)

		l_formato_texto_xml VARCHAR2(1) := 'U'; -- XML con encoding UTF-8

		l_codigo_oficina VARCHAR2(5) := 'RURAL';

		l_xml_clob    CLOB;
		l_length_clob NUMBER;

		-- *********** Contadores ***********************************
		l_num_anexos NUMBER := 0;

		-- *********** Excepciones **********************************
		no_anexos_found EXCEPTION;
		no_envios_found EXCEPTION;

	BEGIN
		pq_utl.log(lc, '## INI ##', 1);

		-- PQ_Utl.getcfg esta en el paquete de utilidades
		-- accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).
		l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');
		-- Se guarda el path fisico del directorio
		SELECT directory_path
			INTO l_dir_name
			FROM all_directories
		 WHERE directory_name = l_dir;

		-- Se obtiene el nombre del posible env?
		l_nombre := get_next_name_file(anoanexos, NULL);

		BEGIN
			pq_utl.log(lc, 'Recuperando los anexos MP Provisionales ', 2);
			l_error := 'Recuperando en la tabla de anexos MP los anexos Provisionales ';

			-- Ver TB_ANEXO_MOD. El estado 1 es Provisional.
			l_query := '
		SELECT
		      A.ID,
					P.REFERENCIA,
					L.CODLINEA,
					L.CODPLAN
		FROM TB_ANEXO_MOD A,
					TB_POLIZAS P,
					TB_LINEAS L
		WHERE A.ESTADO =''5''
			AND A.IDPOLIZA = P.IDPOLIZA
			AND P.LINEASEGUROID = L.LINEASEGUROID
      AND L.CODPLAN = ' || anoanexos;


			OPEN l_tp_cursor FOR l_query;

			pq_utl.log(lc, l_query, 2);

			IF l_tp_cursor%NOTFOUND THEN
				l_error := 'No se han encontrado anexos MP a enviar para el Plan ' || anoanexos;
				RAISE no_anexos_found;
			END IF;

			--Se inserta el movimiento en la tabla de comunicaciones
			SELECT sq_comunicaciones.NEXTVAL
				INTO l_idenvio
				FROM dual;

			INSERT INTO tb_comunicaciones (idenvio, fecha_envio, fichero_envio, tipo_mov, resultado, fichero_tipo)
			VALUES (l_idenvio, SYSDATE, l_nombre, 'E', NULL, 'M');

      --Abrimos el ZIP para a?adirle los ficheros a enviar
			pq_utlzip.abrirzip(l_dir_name || '/' || l_nombre || '.ZIP');

			FETCH l_tp_cursor
				INTO l_idanexo, l_refpoliza, l_cod_linea, l_cod_plan;

			LOOP
				-- Para cada anexo se genera el XML de anexo
				BEGIN

					--aux := pq_creaxml_anexo_mp.generaxmlanexomp(l_idanexo);

					SELECT am.xml, dbms_lob.getlength(am.xml)
						INTO l_xml_clob, l_length_clob
						FROM tb_anexo_mod am
					 WHERE am.id = l_idanexo;

				EXCEPTION
					WHEN no_data_found THEN
						l_error := 'No se han encontrado XML Anexos MP' || l_idanexo;
						RAISE no_envios_found;
				END;

				l_nombre_xml := l_refpoliza || l_cod_plan || l_cod_linea;

				-- *******************************+++++++++++++++++++++*************************
				-- Creacion del XML del anexo RC. Abrimos el fichero de salida con extension .XML
				-- ****************************************************+++++++++++++++++++++****
				DBMS_LOB.CLOB2FILE(l_xml_clob, l_dir, l_nombre_xml || '.XML', 0, 'wb');

				--Se comprime el XML dentro del .ZIP. El .ZIP tiene el mismo nombre que el TXT
				pq_utl.log(lc,
									 'Comprimiendo el archivo ' || l_dir_name || '/' || l_nombre_xml || '.XML en el archivo ' || l_dir_name || '/' || l_nombre ||
									 '.ZIP');
				pq_utlzip.compressfile(l_dir_name || '/' || l_nombre_xml || '.XML',
															 l_dir_name || '/' || l_nombre || '.ZIP');

				--Se borra el XML
				pq_utl.log(lc, 'Eliminando el archivo ' || l_dir_name || '/' || l_nombre_xml || '.XML');
				pq_utlzip.borraxmls(l_dir_name || '/' || l_nombre_xml || '.XML');

				--Se actualiza el Estado del Anexo a Enviada con el envio
				UPDATE tb_anexo_mod
					 SET idenvio = l_idenvio, estado = 2
				WHERE id = l_idanexo;

        -- ASF Insertamos en el historico de estados de siniestro
        INSERT INTO TB_ANEXO_MOD_HISTORICO_ESTADOS (id,idanexo,codusuario,fecha,estado)
           values (SQ_ANEXO_MOD_HISTORICO_ESTADOS.nextval, l_idanexo, null, sysdate, 2);

				COMMIT;

				l_num_anexos := l_num_anexos + 1;

				FETCH l_tp_cursor
					INTO l_idanexo, l_refpoliza, l_cod_linea, l_cod_plan;
				EXIT WHEN l_tp_cursor%NOTFOUND; -- No hay mas registros
			END LOOP;

      --Cerramos el ZIP
			pq_utlzip.cerrarzip;

			-- ********************************************************
			-- Generacion TXT
			-- ********************************************************
			f_fichero := utl_file.fopen(location     => l_dir,
																	filename     => l_nombre || '.TXT',
																	open_mode    => 'w',
																	max_linesize => pq_typ.max_linefilesizewrite);
			-- Se guarda el contenido del TXT (Ver arriba formato)
			utl_file.put_line(f_fichero,
												l_codigo_oficina || l_tipo_doc_ms || l_formato_texto_xml || anoanexos ||
												lpad(l_num_anexos, 9, '0'));

			UPDATE tb_comunicaciones
				 SET resultado = 'CORRECTO'
			 WHERE idenvio = l_idenvio;

		EXCEPTION
			WHEN no_data_found THEN
				l_error := 'No se han encontrado anexos a enviar';
				RAISE no_anexos_found;
		END;


		-- ********************************************************
		-- Cerramos el fichero.
		-- ********************************************************
		COMMIT;
		CLOSE l_tp_cursor;
		utl_file.fclose(f_fichero);

		-- ********************************************************
		-- Guardamos los resultados en el fichero de log
		-- ********************************************************
		pq_utl.log(lc, ' ', 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || to_char(SYSDATE, 'DD/MM/YY HH24:MI:SS'), 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, 'Anexos MP definitivas   := ' || l_num_anexos, 2);
		pq_utl.log(lc, '*********************************************************************************', 2);
		pq_utl.log(lc, ' ', 2);

		pq_utl.log(lc, 'Fin del proceso ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
		pq_utl.log(lc, '## FIN ##', 1);

		-- Se devuelve el nombre del fichero generado
		RETURN l_nombre;


	EXCEPTION
		WHEN no_envios_found THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN no_anexos_found THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, l_error || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;
		WHEN OTHERS THEN
			ROLLBACK;
			pq_err.raiser(SQLCODE, 'Error al generar los ficheros de genera_fichero_anexosRC' || ' [' || SQLERRM || ']');
			RETURN 'Error ' || pq_typ.ko;


	END genera_fichero_anexosmp;


	--Funcion que comprueba si un fichero ya existe
	FUNCTION file_exists(p_fname IN VARCHAR2) RETURN BOOLEAN IS
		l_file      utl_file.file_type;
    l_dir       tb_config_agp.agp_valor%TYPE;
    existe      boolean := false;
    cuenta      number(10);
	BEGIN
    BEGIN
      l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');
      l_file := utl_file.fopen(l_dir,
														 p_fname || '.ZIP', 'r');
		  utl_file.fclose(l_file);
		  existe := TRUE;

	  EXCEPTION
		  WHEN utl_file.invalid_path THEN
			  existe := FALSE;
		  WHEN utl_file.invalid_operation THEN
			  existe := FALSE;
      WHEN OTHERS THEN
        existe := FALSE;
    END;

    IF (existe = false) THEN
      select count(*) into cuenta from tb_comunicaciones c where c.fichero_envio = p_fname;
      IF (cuenta > 0) THEN
        existe := true;
      END IF;
    END IF;

    return existe;

	END file_exists;


	FUNCTION get_next_name_file(codplan IN VARCHAR2, grupoSeguro IN VARCHAR2) RETURN VARCHAR2 IS
		l_num_envio        NUMBER := 1; --Por defecto, primer envio
    l_character        NUMBER := 64; --Para enviar la 'A' en el nombre (chr(65) 0 'A')
		l_nombre           VARCHAR2(8);
		l_name_prefix_cy   VARCHAR2(2) := 'SC'; -- Prefijo para el fichero del a?o actual
		--l_name_prefix_py   VARCHAR2(2) := 'SD'; -- Prefijo para el fichero del a?o anterior
		l_year_last_number NUMBER;

	BEGIN
   pq_utl.log('## DATOS DE ENTRADA ##'||codplan||'#'||grupoSeguro||'##', 2);
  -- Se determina el prefijo a utilizar
    IF (grupoSeguro IS NOT NULL AND grupoSeguro = 'GS') THEN
       l_name_prefix_cy := 'GS';
    ELSIF (grupoSeguro IS NOT NULL AND grupoSeguro = gsGanado) THEN
       l_name_prefix_cy := 'SG';
    END IF;

		-- Se busca el numero de envio
		SELECT COUNT(DISTINCT c.idenvio)
			INTO l_num_envio
			FROM tb_comunicaciones c
		 WHERE extract(DAY FROM c.fecha_envio) = extract(DAY FROM SYSDATE)
           AND extract(MONTH FROM c.fecha_envio) = extract(MONTH FROM SYSDATE)
           AND extract(YEAR FROM c.fecha_envio) = extract(YEAR FROM SYSDATE);
     pq_utl.log('## NUM ENVIO ##'||l_character||'#'||l_num_envio||'##', 2);
		-- Se recoge el ?ltimo digito del plan en l_YEAR_LAST_NUMBER
		-- Y se conforma el nombre del fichero
		l_year_last_number := substr(codplan, 4, 1);
		l_nombre           := l_name_prefix_cy || l_year_last_number || lpad(extract(MONTH FROM SYSDATE), 2, '0') ||
                                                                    lpad(extract(DAY FROM SYSDATE), 2, '0') ||
                                                                    (chr(l_character + l_num_envio + 1));

		--Si ya existe el nombre de fichero con l_NAME_PREFIX_CY, se crea con l_NAME_PREFIX_PY
		WHILE file_exists(l_nombre) LOOP
      l_num_envio := l_num_envio + 1;
			l_nombre := l_name_prefix_cy || l_year_last_number || lpad(extract(MONTH FROM SYSDATE), 2, '0') ||
                                                            lpad(extract(DAY FROM SYSDATE), 2, '0') ||
                                                            (chr(l_character + l_num_envio + 1));
		END LOOP;
		RETURN l_nombre;
	END get_next_name_file;

END pq_genera_envios_agroseguro;
/
SHOW ERRORS;