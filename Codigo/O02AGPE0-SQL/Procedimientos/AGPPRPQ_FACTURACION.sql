CREATE OR REPLACE PACKAGE O02AGPE0.pq_facturacion IS

	-- Author  : Antonio Martín
	-- Created : 24/5/2012 11:07:19
	-- Purpose : Facturación de la aplicación de Agroplus.

	PROCEDURE pr_facturacion(p_codentidad VARCHAR2
													,p_codoficina VARCHAR2
													,p_codusuario VARCHAR2
													,p_tipo       VARCHAR2);

	PROCEDURE pr_generar_fichero;
	PROCEDURE pr_generar_fichero(v_fecha VARCHAR2);
	PROCEDURE p(p VARCHAR2);


END pq_facturacion;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.pq_facturacion IS

	PROCEDURE pr_facturacion(p_codentidad VARCHAR2
													,p_codoficina VARCHAR2
													,p_codusuario VARCHAR2
													,p_tipo       VARCHAR2) IS

		v_numact tb_facturacion.numact%TYPE := 0;
		v_numcon tb_facturacion.numcon%TYPE := 0;
		v_numimp tb_facturacion.numimp%TYPE := 0;
	BEGIN

		BEGIN
			SELECT numact, numcon, numimp
				INTO v_numact, v_numcon, v_numimp
				FROM tb_facturacion f
			 WHERE codentidad = p_codentidad
				 AND codoficina = p_codoficina
				 AND codusuario = p_codusuario
				 AND to_char(fecha, 'YYYYMMDD') = to_char(SYSDATE, 'YYYYMMDD');
		EXCEPTION
			WHEN no_data_found THEN
				v_numact := 0;
				v_numcon := 0;
				v_numimp := 0;
			WHEN OTHERS THEN
				htp.p(SQLERRM || ' ERROR EN ESTADISTICAS');
		END;
		IF p_tipo = 'C' THEN
			BEGIN
				INSERT INTO tb_facturacion
					(codentidad, codoficina, codusuario, fecha, numact, numcon, numimp)
				VALUES
					(p_codentidad, p_codoficina, p_codusuario, trunc(SYSDATE), 0, 1, 0);
			EXCEPTION
				WHEN dup_val_on_index THEN
					UPDATE tb_facturacion
						 SET numcon = v_numcon + 1
					 WHERE codentidad = p_codentidad
						 AND codoficina = p_codoficina
						 AND codusuario = p_codusuario
						 AND to_char(fecha, 'yyyymmdd') = to_char(SYSDATE, 'YYYYMMDD');
			END;
		ELSIF p_tipo = 'A' THEN
			BEGIN
				INSERT INTO tb_facturacion
					(codentidad, codoficina, codusuario, fecha, numact, numcon, numimp)
				VALUES
					(p_codentidad, p_codoficina, p_codusuario, trunc(SYSDATE), 1, 0, 0);
			EXCEPTION
				WHEN dup_val_on_index THEN
					UPDATE tb_facturacion
						 SET numact = v_numact + 1
					 WHERE codentidad = p_codentidad
						 AND codoficina = p_codoficina
						 AND codusuario = p_codusuario
						 AND to_char(fecha, 'yyyymmdd') = to_char(SYSDATE, 'YYYYMMDD');
			END;
		ELSIF p_tipo = 'I' THEN
			BEGIN
				INSERT INTO tb_facturacion
					(codentidad, codoficina, codusuario, fecha, numact, numcon, numimp)
				VALUES
					(p_codentidad, p_codoficina, p_codusuario, trunc(SYSDATE), 0, 0, 1);
			EXCEPTION
				WHEN dup_val_on_index THEN
					UPDATE tb_facturacion
						 SET numimp = v_numimp + 1
					 WHERE codentidad = p_codentidad
						 AND codoficina = p_codoficina
						 AND codusuario = p_codusuario
						 AND to_char(fecha, 'yyyymmdd') = to_char(SYSDATE, 'YYYYMMDD');
			END;
		END IF;

	END pr_facturacion;



	PROCEDURE pr_generar_fichero AS
	BEGIN
		pr_generar_fichero(to_char(SYSDATE, 'YYYYMMDD'));
    --pr_generar_fichero('20131231');
	END pr_generar_fichero;


	PROCEDURE pr_generar_fichero(v_fecha VARCHAR2) AS
		fichero_txt utl_file.file_type;
		fichero_log utl_file.file_type;
		dir_txt     VARCHAR2(15) := 'AGP_BATCH';
		dir_cop     VARCHAR2(15) := 'AGP_BATCH';
		nomfich_txt VARCHAR2(12) := 'FACAGROW.DAT';
		nomfich_cop VARCHAR2(21); --  FGAYYYYMMDDHH24MISS.txt
		nomfich_log VARCHAR2(22); -- IGANYYYYMMDDHH24MISS.txt
		dt_fecha    DATE;
		hora        VARCHAR2(6) := to_char(SYSDATE, 'HH24MISS');
		cuenta      NUMBER := 0;
		v_error     VARCHAR(32000) := NULL;

		-- CURSOR CON CONSULTA PRINCIPAL PARA ESCRIBIR A FICHERO TXT
		CURSOR c_facturas(v_fecha VARCHAR2) IS
			SELECT etiqueta || usuario || suma || entidad || oficina || v_fecha datos
				FROM (SELECT *
								 FROM (SELECT 'AGPLUIMP' etiqueta, rpad(a.codusuario, 8, ' ') usuario, lpad(SUM(numimp), 10, 0) suma, lpad(a.codentidad, 4, 0) entidad, lpad(a.codoficina, 4, 0) oficina, 1 orden
													FROM o02agpe0.tb_facturacion a
												 WHERE a.codusuario NOT LIKE 'U02%'
													 AND a.codentidad NOT IN (8017, 9998)
													 AND to_char(a.fecha, 'YYYYMM') = substr(v_fecha, 0, 6)
													 AND a.numimp > 0
												 GROUP BY a.codentidad, a.codoficina, a.codusuario
												 ORDER BY a.codentidad, a.codoficina, a.codusuario) imp
							 UNION
							 SELECT *
								 FROM (SELECT 'AGPLUENV' etiqueta, rpad(b.codusuario, 8, ' ') usuario, lpad(SUM(b.numcon), 10, 0) suma, lpad(b.codentidad, 4, 0) entidad, lpad(b.codoficina, 4, 0) oficina, 2 orden
													FROM o02agpe0.tb_facturacion b
												 WHERE b.codusuario NOT LIKE 'U02%'
													 AND b.codentidad NOT IN (8017, 9998)
													 AND to_char(b.fecha, 'YYYYMM') = substr(v_fecha, 0, 6)
													 AND b.numcon > 0
												 GROUP BY b.codentidad, b.codoficina, b.codusuario
												 ORDER BY b.codentidad, b.codoficina, b.codusuario) env

							 UNION
							 SELECT *
								 FROM (SELECT 'AGPLUTRA' etiqueta, rpad(c.codusuario, 8, ' ') usuario, lpad(SUM(c.numact), 10, 0) suma, lpad(c.codentidad, 4, 0) entidad, lpad(c.codoficina, 4, 0) oficina, 3 orden
													FROM o02agpe0.tb_facturacion c
												 WHERE c.codusuario NOT LIKE 'U02%'
													 AND c.codentidad NOT IN (8017, 9998)
													 AND to_char(c.fecha, 'yyyymm') = substr(v_fecha, 0, 6)
													 AND numact > 0
												 GROUP BY c.codentidad, c.codoficina, c.codusuario
												 ORDER BY c.codentidad, c.codoficina, c.codusuario) tra)
			 ORDER BY entidad, oficina, usuario, orden;

		-- INICIO DE PROCEDIMIENTO PRINCIPAL PR_FACTURAS_AW(V_FECHA VARCHAR2)
	BEGIN
		-- VALIDA FECHA
		p('ENTRAMOS EN EL PROCEDIMIENTO......');
		dt_fecha := to_date(v_fecha, 'YYYYMMDD');
		IF to_number(to_char(dt_fecha, 'DD')) < 10 THEN
			dt_fecha := dt_fecha - to_number(to_char(dt_fecha, 'DD'));
		END IF;
		p('dt_fecha ' || dt_fecha);


		p('Abrimos los ficheros ......');
		-- ASIGNACION Y APERTURA DE FICHEROS LOG Y TXT
		nomfich_log := 'IAGR' || to_char(dt_fecha, 'YYYYMMDD') || hora || '.txt';
		nomfich_cop := 'FAG' || to_char(dt_fecha, 'YYYYMMDD') || hora || '.txt';
		p('Creamos el nombre de los ficheros ......');
		fichero_txt := utl_file.fopen(dir_txt, nomfich_txt, 'W');
		fichero_log := utl_file.fopen(dir_txt, nomfich_log, 'W');
		p('Ficheros Abiertos......');

		-- ABRE CURSOR CON FECHA VALIDADA Y ESCRIBE DATOS EN FICHERO TXT
		p('Escribimos en el fichero......');
		FOR s IN c_facturas(to_char(dt_fecha, 'YYYYMMDD')) LOOP
			utl_file.put_line(fichero_txt, s.datos);
			cuenta := cuenta + 1;
		END LOOP;
		p('Salimos de la escritura del fichero......');

		-- ESCRIBE INFORMACION EN FICHERO LOG, CIERRA LOS FICHEROS Y REALIZA COPIA.
		utl_file.put_line(fichero_log, 'Total de movimientos de FACTURAS de AgroPlus: ' || cuenta);
		utl_file.put_line(fichero_log, 'Fin de los datos de FACTURAS de AgroPlus.');
		p('Escribimos en el fichero de log......');
		utl_file.fclose(fichero_txt);
		utl_file.fclose(fichero_log);
		p('Cerramos los ficheros......');
		utl_file.fcopy(dir_txt, nomfich_txt, dir_cop, nomfich_cop);
		p('Creamos copia del fichero generado......');

	EXCEPTION
		WHEN OTHERS THEN
			v_error := SQLERRM;
			dbms_output.put_line('ERROR ........' || substr(v_error, 1, 200));

			utl_file.put_line(fichero_log, 'CODIGO DE ERROR: ' || SQLCODE);
			utl_file.put_line(fichero_log, 'ERROR: ' || SQLERRM);
			utl_file.fclose(fichero_txt);
			utl_file.fclose(fichero_log);
	END pr_generar_fichero;

	PROCEDURE p(p VARCHAR2) IS
	BEGIN
		dbms_output.put_line(p);
	END p;

END pq_facturacion;
/
