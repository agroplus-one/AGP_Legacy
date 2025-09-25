SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.pq_brujula IS

	--- Author  :  Antonio Martín Aznar
	-- Created : 18/5/2012 13:00:25
	-- Purpose : Cuadros de Mandos.
	PROCEDURE pr_acuerdos_agroplus;
	PROCEDURE pr_cuadros_de_mando;

END pq_brujula;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.pq_brujula IS

	PROCEDURE pr_acuerdos_agroplus IS
		CURSOR c_polizas_general IS
			SELECT c.codentidad, COUNT(*) polizas
				FROM tb_polizas p, tb_colectivos c
			 WHERE p.idcolectivo = c.id
				 AND c.codentidad <> 9998
				 AND p.idestado = 8
				 AND to_char(p.fechaenvio, 'YYYYMMDD') <= to_char(last_day(add_months(SYSDATE, -1)), 'yyyymmdd')
			 GROUP BY c.codentidad
			 ORDER BY c.codentidad;
	
		/*  
        CURSOR g_polizas_general IS
          SELECT codent, COUNT(*) polizas
            FROM ganaweb.polizas_general
           WHERE codent <> 9998
             AND flgest IN (2, 3)
             AND fecenv <= to_char(last_day(add_months(SYSDATE, -1)), 'yyyymmdd')
           GROUP BY codent
           ORDER BY codent;
    */
	
		v_fecha VARCHAR2(25) := NULL;
	BEGIN
		FOR a IN c_polizas_general LOOP
			v_fecha := to_char(last_day(add_months(SYSDATE, -1)), 'YYYY-MM-DD');
			BEGIN
				IF a.codentidad IN (3001, 3045, 3094, 3118, 3186) THEN
					EXECUTE IMMEDIATE 'INSERT INTO GD_ACUERDOS_SER VALUES (''' || v_fecha || ''', ''' || a.codentidad || ''', ''AGROWEB'', '' '', ''EXT'', ' ||
														a.polizas || ')';
					COMMIT;
				ELSE
					EXECUTE IMMEDIATE 'INSERT INTO GD_ACUERDOS_SER VALUES (''' || v_fecha || ''', ''' || a.codentidad || ''', ''AGROWEB'', '' '', ''IRIS'', ' ||
														a.polizas || ')';
					COMMIT;
				END IF;
			EXCEPTION
				WHEN dup_val_on_index THEN
					EXECUTE IMMEDIATE 'UPDATE GD_ACUERDOS_SER
					    SET NUMELEAC = ' || a.polizas || '
					  WHERE FECMES = ''' || v_fecha || '''
					    AND CODENTID = ''' || a.codentidad || '''
					    AND CODAGRUP = ''AGROWEB''
					    AND CODSUBAG = '' '' ';
					COMMIT;
				
				WHEN OTHERS THEN
					dbms_output.put_line('ERROR:' || SQLERRM);
			END;
		END LOOP;
	
	
		/*    FOR g IN g_polizas_general LOOP
      v_fecha := to_char(last_day(add_months(SYSDATE, -1)), 'YYYY-MM-DD');
      BEGIN
        IF g.codent IN (3001, 3045, 3094, 3118, 3186) THEN
          \*
                      INSERT INTO GD_ACUERDOS_SER (FECMES, CODENTID, CODAGRUP,CODSUBAG, TIPENTID, NUMELEAC)
                             VALUES (V_FECHA, A.CODENT, 'AGROWEB', ' ', 'EXT', a.polizas);
          *\
        
          EXECUTE IMMEDIATE 'INSERT INTO GD_ACUERDOS_SER VALUES (''' || v_fecha || ''', ''' || g.codent || ''', ''GANAWEB'', '' '', ''EXT'', ' ||
                            g.polizas || ')';
          COMMIT;
        ELSE
          EXECUTE IMMEDIATE 'INSERT INTO GD_ACUERDOS_SER VALUES (''' || v_fecha || ''', ''' || g.codent || ''', ''GANAWEB'', '' '', ''IRIS'', ' ||
                            g.polizas || ')';
          COMMIT;
        END IF;
      EXCEPTION
        WHEN dup_val_on_index THEN
          EXECUTE IMMEDIATE 'UPDATE GD_ACUERDOS_SER
              SET NUMELEAC = ' || g.polizas || '
            WHERE FECMES = ''' || v_fecha || '''
              AND CODENTID = ''' || g.codent || '''
              AND CODAGRUP = ''GANAWEB''
              AND CODSUBAG = '' '' ';
          COMMIT;
        
        WHEN OTHERS THEN
          dbms_output.put_line('ERROR:' || SQLERRM);
      END;
    END LOOP;*/
	END pr_acuerdos_agroplus;


	PROCEDURE pr_cuadros_de_mando IS
	
		v_fecha DATE := SYSDATE;
	
		v_f_inicio  VARCHAR2(8) := NULL;
		v_f_periodo VARCHAR2(8) := NULL;
	
		v_total NUMBER := 0;
	
		c_indicacor    CONSTANT VARCHAR2(8) := 'PRDES003';
		c_subindicacor CONSTANT VARCHAR2(20) := '                    ';
	
		c_entid_total CONSTANT VARCHAR2(4) := 'AAAA';
	
		v_fichero     utl_file2.file_type;
		v_fichero_log utl_file2.file_type;
	
		v_n_fichero     CONSTANT VARCHAR2(25) := 'Brujula.txt';
		v_n_fichero_log CONSTANT VARCHAR2(35) := 'Brujula' || to_char(SYSDATE, 'yyyymmddhh24miss') || '.log';
	
		v_linea     VARCHAR2(32000) := NULL;
		v_linea_log VARCHAR2(32000) := NULL;
	
	
	
	
		CURSOR c_polizas(v_inicio VARCHAR2, v_fin VARCHAR2) IS
			SELECT c.codentidad, COUNT(*) valor
				FROM tb_polizas p, tb_colectivos c
			 WHERE p.idcolectivo = c.id
				 AND to_char(p.fechaenvio, 'YYYYMMDD') BETWEEN v_inicio AND v_fin
			 GROUP BY c.codentidad
			 ORDER BY c.codentidad;
	
		PROCEDURE pintar(p VARCHAR2) IS
		BEGIN
			--dbms_output.put_line(p);
			NULL;
		END;
	
	BEGIN
	
		v_fichero := utl_file2.fopen(location => 'AGP_BATCH', filename => v_n_fichero, open_mode => 'w');
	
		v_fichero_log := utl_file2.fopen(location => 'AGP_BATCH', filename => v_n_fichero_log, open_mode => 'w');
	
		v_linea_log := 'Inicio del proceso ' || to_char(SYSDATE, 'dd/mm/yyyy hh24:mi:ss');
		utl_file2.put_line(file => v_fichero_log, buffer => v_linea_log);
	
		-- Hayamos el día ultimo dia del mes para el que se solicitan los datos.
		v_f_periodo := to_char(last_day(add_months(v_fecha, -1)), 'YYYYMMDD');
		pintar('v_f_periodo => ' || v_f_periodo);
	
		-- Hayamos el día 1 de Enero.
		/*
        v_f_inicio := substr(v_f_periodo,
                             1,
                             4) || '01' || '01';
    */
	
		v_f_inicio := to_char(add_months(SYSDATE, -1), 'YYYYMM') || '01';
		pintar('v_f_inicio => ' || v_f_inicio);
	
	
	
		FOR i IN c_polizas(v_f_inicio, v_f_periodo) LOOP
			v_linea := substr(v_f_periodo, 1, 6) || lpad(i.codentidad, 4, '0') || c_indicacor || c_subindicacor || TRIM(to_char(i.valor, '0000000000D00'));
		
			utl_file2.put_line(file => v_fichero, buffer => v_linea);
		
			pintar(v_linea);
		
			v_total := v_total + i.valor;
		
		END LOOP;
		-- Creamos la linea de totales.
		v_linea := substr(v_f_periodo, 1, 6) || c_entid_total || c_indicacor || c_subindicacor || TRIM(to_char(v_total, '0000000000D00'));
	
		utl_file2.put_line(file => v_fichero, buffer => v_linea);
	
		pintar(v_linea);
	
	
	
		v_linea_log := 'Fin del proceso ' || to_char(SYSDATE, 'dd/mm/yyyy hh24:mi:ss');
		utl_file2.put_line(file => v_fichero_log, buffer => v_linea_log);
	
		utl_file2.fclose(v_fichero);
		utl_file2.fclose(v_fichero_log);
	
	
	END pr_cuadros_de_mando;

END pq_brujula;
/
SHOW ERRORS;