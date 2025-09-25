CREATE OR REPLACE PACKAGE pq_cultivoslineas IS

	/******************************************************************************
     NAME:       
     PURPOSE:    Genera un fichero con todos los cultivos lineas
  
     REVISIONS:
     Ver        Date        Author           Description
     ---------  ----------  ---------------  ------------------------------------
     1.0        07/03/2011  Antonio Martín        
  
     NOTES:
  
  ******************************************************************************/

	PROCEDURE pr_generar_fichero;

END pq_cultivoslineas;
/
CREATE OR REPLACE PACKAGE BODY pq_cultivoslineas IS

	PROCEDURE pr_generar_fichero IS
		v_file    utl_file.file_type;
		v_filelog utl_file.file_type;
	
		v_linea    VARCHAR2(32000) := NULL;
		v_linealog VARCHAR2(32000) := NULL;
	
		v_nom_log   VARCHAR2(32000) := NULL;
		v_nom_fic   VARCHAR2(32000) := NULL;
		v_nom_copia VARCHAR2(32000) := NULL;
	
		v_contador NUMBER := 0;
	
	
	
		v_dir CONSTANT VARCHAR2(32000) := 'AGP_BATCH';
	
		CURSOR c_cultivos IS
			SELECT l.codplan, l.codlinea, l.nomlinea
				FROM tb_lineas l
			 WHERE l.codplan IN (SELECT MAX(l1.codplan)
														 FROM tb_lineas l1
														WHERE l1.codlinea = l.codlinea)
			 ORDER BY l.codlinea;
	
	BEGIN
		v_linealog := 'Inicio el proceso ' || to_char(SYSDATE,
																									'dd/mm/yyyy hh24:mi:ss');
	
		v_nom_log := 'LOG_CULTIVOS_' || to_char(SYSDATE,
																						'yyyymmddhh24miss') || '.log';
	
		v_filelog := utl_file.fopen(location  => v_dir,
																filename  => v_nom_log,
																open_mode => 'w');
	
		utl_file.put_line(file   => v_filelog,
											buffer => v_linealog || chr(13));
	
	
		v_nom_fic := 'cultivos.txt';
	
		v_file := utl_file.fopen(location  => v_dir,
														 filename  => v_nom_fic,
														 open_mode => 'w');
	
		FOR a IN c_cultivos LOOP
			v_linea := lpad('01',
											2,
											0) || lpad(a.codlinea,
																 3,
																 0) || rpad(substr(a.nomlinea,
																									 1,
																									 30),
																						30,
																						' ');
			utl_file.put_line(v_file,
												v_linea || chr(13));
		
			v_contador := v_contador + 1;
		
		
		
		END LOOP;
	
		v_linealog := 'lineas Creadas ' || v_contador;
	
		utl_file.put_line(file   => v_filelog,
											buffer => v_linealog || chr(13));
	
	
		v_linealog := 'Fin del proceso ' || to_char(SYSDATE,
																								'dd/mm/yyyy hh24:mi:ss');
	
		utl_file.put_line(file   => v_filelog,
											buffer => v_linealog || chr(13));
	
	
	
		utl_file.fclose(file => v_filelog);
		utl_file.fclose(file => v_file);
	
	EXCEPTION
		WHEN OTHERS THEN
			RAISE;
		
	
	END;

END pq_cultivoslineas;
/
