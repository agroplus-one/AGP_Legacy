SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.PQ_INTEGRACION_GED IS

  lc VARCHAR2(25) := 'PQ_INTEGRACION_GED.'; -- Variable que almacena el nombre del paquete y de la función
  TYPE t_array IS TABLE OF VARCHAR2(30000) INDEX BY BINARY_INTEGER;
  
  PROCEDURE actualizar_firma_diferida(p_fecha IN VARCHAR2);
  FUNCTION fn_split (cadena IN VARCHAR2, separador IN CHAR) RETURN t_array;
   
END PQ_INTEGRACION_GED;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_INTEGRACION_GED AS

	PROCEDURE actualizar_firma_diferida(p_fecha IN VARCHAR2) IS
		
	
		lc VARCHAR2(25) := 'PQ_INTEGRACION_GED.';       -- Variable que almacena el nombre del paquete y de la funcion
	    nom_var_config	VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS';       -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
	    
	    l_dir         		     TB_CONFIG_AGP.AGP_VALOR%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
	    l_dir_name    		     VARCHAR2(1000);				      	-- Ruta física donde se ubican los ficheros que se recibe
	       
	    l_nombre_fichero	VARCHAR2(30):= 'FIC_FIRMA_DIFERIDA';
	    l_nombre	VARCHAR(50):='';
	    v_nombre_fich VARCHAR2(32);
	       
	    f_fichero	UTL_FILE.FILE_TYPE;     -- Variable que almacena la referencia al fichero de envio
	    l_line	VARCHAR2(2500);             -- Variables que almacena la linea que se leera del fichero
	    v_valor               VARCHAR2(2500);
  	   	valoresUsuario        t_array;
	    
  	   	
	    existeElArchivo  BOOLEAN;
	    longitudEnBytes  NUMBER;
	    numeroDeBloques  NUMBER;
	    
	    num_registros       NUMBER := 0;    -- contador de registros totales leidos
	    num_pol_modif       NUMBER := 0;    -- contador de polizas modificadas
	    num_pol_error     	NUMBER := 0;	-- contador de polizas con errores
	      	    	    
	   	v_tipo_poliza VARCHAR2(2500);
		v_cod_barras VARCHAR2(2500);
		v_id_archiv VARCHAR2(2500);
		v_id_poliza NUMBER;
	    
	    BEGIN
		    
			-- Comienzo de escritura en log
		    PQ_Utl.LOG(lc,'PQ_INTEGRACION_GED - actualizar_firma_diferida - init', 1);
		
		    -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos de OMEGA
		    PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositan el fichero recibidos.   ', 2);
		    
		    l_nombre := PQ_Utl.getcfg(l_nombre_fichero);
		   	v_nombre_fich := TRIM(l_nombre || '_' || p_fecha) ;
		   	 
		   	l_dir  := PQ_Utl.getcfg(nom_var_config);
		   	 
		 	 -- Se guarda el path fisico del directorio
		    SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME = l_dir;
		
		
		     -- Se abre el fichero de texto de salida en la ruta indicada
		    PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.  ', 2);
		     
		     -- Antes de abrir el fichero se verifica si existe
		    UTL_FILE.FGETATTR( l_dir, v_nombre_fich || '.txt', existeElArchivo, longitudEnBytes, numeroDeBloques);
		              
		    IF existeElArchivo THEN
		     	PQ_Utl.LOG(lc, 'El fichero '|| v_nombre_fich || '.txt' ||' existe, continuamos.' );
		    ELSE
		        PQ_Utl.LOG(lc, 'El fichero '|| v_nombre_fich || '.txt' ||' NO existe en la ruta: ' || l_dir );
		    END IF;
	              
	    	f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => v_nombre_fich || '.txt',	open_mode    => 'r');          
		    
	 		-- Para cada linea del fichero, obtenemos la poliza de Agroplus y actualizamos la informacion de TB_GED_DOC_POLIZA o TB_GED_DOC_POLIZA_SBP para la poliza.
	 	 	-- Comienza el bucle para leer todas las líneas del fichero
	        LOOP
	        	-- Lee la linea actual
	            -- Si al leer se ha llegado al final del fichero, se lanzara la excepcion 'no_data_found'
	            BEGIN
	            	UTL_FILE.get_line (f_fichero,l_line);
	            EXCEPTION
	                WHEN NO_DATA_FOUND THEN
	                PQ_Utl.LOG(lc, 'No hay mas registros en el fichero.  ', 2);
	                EXIT;
	            END;
	                
	            num_registros := num_registros + 1;
	                
	           	-- Si la linea leida esta vacia salta a la siguiente iteracion para leer otra línea
	            IF (l_line is null OR (LENGTH(l_line)<=1)) THEN
	            	PQ_Utl.LOG(lc, 'La linea esta vacia, se lee la siguiente.', 2);
	                CONTINUE;
	            END IF;
	                
	            BEGIN
		            
	            	/* Recuperamos los valores de la linea leida */
	                v_valor:= l_line;
	                valoresUsuario :=  fn_split (v_valor, chr(32));
	                /* Obtenemos los campos de la linea del fichero leida*/
		            v_tipo_poliza:=TRIM(valoresUsuario(1));
  	                PQ_Utl.LOG(lc, 'TIPO POLIZA: ' || v_tipo_poliza , 2);
  	                v_cod_barras:=TRIM(valoresUsuario(2));
   	                PQ_Utl.LOG(lc, 'COD BARRAS: ' || v_cod_barras , 2);
	                v_id_archiv:= TRIM(valoresUsuario(3));
   	                PQ_Utl.LOG(lc, 'IDARCHIVO: ' || v_id_archiv , 2);
	                   
					IF (v_valor IS NOT NULL AND v_tipo_poliza IS NOT NULL) THEN
					
						PQ_Utl.LOG(lc, 'Registro valido.', 2);
						
						-- Segun el tipo de poliza procesada guardamos un nombre de tabla u otro en funcion de si es normal o de sobreprecio
	                   	IF (v_tipo_poliza = 'AGR') THEN
	                   			
								PQ_Utl.LOG(lc, 'SELECT idpoliza INTO v_id_poliza FROM o02agpe0.TB_GED_DOC_POLIZA WHERE cod_barras = ' || v_cod_barras || ' AND cod_canal_firma = 4 AND IND_DOC_FIRMADA = ''N'';', 2);

	                   			select idpoliza into v_id_poliza from o02agpe0.TB_GED_DOC_POLIZA where cod_barras = v_cod_barras AND cod_canal_firma = 4 AND IND_DOC_FIRMADA = 'N';
	                   			-- Comprobamos que exista registro en dicha tabla
		                    	PQ_Utl.LOG(lc, 'ID_POLIZA: ' || v_id_poliza, 2);
		                    	
		                    	IF (v_id_poliza IS NOT NULL) THEN	
		                    	 	
		                    		PQ_Utl.LOG(lc, 'Existe registro con idpoliza: ' || v_id_poliza , 2);
		                    	 	num_registros := num_registros + 1;
		                    	 	
		                    		BEGIN
			                    	 	-- Hacemos el update de la poliza para que conste la firma diferida de su documentacion asociada
	   	 		                    	 PQ_Utl.LOG(lc, 'Se procede a actualizar TB_GED_DOC_POLIZA', 2);						
		
			                    	 	update o02agpe0.TB_GED_DOC_POLIZA 
			                    	 	set IDDOCUMENTUM = v_id_archiv, IND_DOC_FIRMADA = 'S', FECHA_FIRMA = TO_DATE(p_fecha,'YYYYMMDD'), CODUSUARIO = '@BATCH' 
			                    	 	WHERE IDPOLIZA = v_id_poliza;		
			                    	 	num_pol_modif := num_pol_modif + 1;
										PQ_Utl.LOG(lc, 'Actualizacion correcta.', 2);

										
									EXCEPTION
	        					    	WHEN OTHERS THEN
	                            			PQ_Utl.LOG(lc, 'Error al updatear la poliza:' || v_id_poliza || ', con error:' || '. ' || sqlcode || ' [' || SQLERRM || ']' ,2);
	                            			num_pol_error := num_pol_error + 1;
	                            			-- Aunque la actualizacion falle continuamos con el siguiente registro
	        								CONTINUE;
	        				    	END;
		                    	 
	        				    END IF;
	                   	ELSE
	                   			
								PQ_Utl.LOG(lc, 'SELECT idpoliza_sbp INTO v_id_poliza FROM o02agpe0.TB_GED_DOC_POLIZA_SBP WHERE cod_barras = ' || v_cod_barras || ' AND cod_canal_firma = 4 AND IND_DOC_FIRMADA = ''N'';', 2);

	                   			select idpoliza_sbp into v_id_poliza from o02agpe0.TB_GED_DOC_POLIZA_SBP where cod_barras = v_cod_barras AND cod_canal_firma = 4 AND IND_DOC_FIRMADA = 'N';
	                   			-- Comprobamos que exista registro en dicha tabla
		                    	PQ_Utl.LOG(lc, 'ID_POLIZA: ' || v_id_poliza, 2);
		                    	
		                    	IF (v_id_poliza IS NOT NULL) THEN	
		                    	 	
		                    		PQ_Utl.LOG(lc, 'Existe registro con idpoliza: ' || v_id_poliza , 2);
		                    	 	num_registros := num_registros + 1;
		                    	 	
		                    		BEGIN
			                    	 	-- Hacemos el update de la poliza para que conste la firma diferida de su documentacion asociada
	   	 		                    	 PQ_Utl.LOG(lc, 'Se procede a actualizar TB_GED_DOC_POLIZA_SBP', 2);						
		
			                    	 	update o02agpe0.TB_GED_DOC_POLIZA_SBP 
			                    	 	set IDDOCUMENTUM = v_id_archiv, IND_DOC_FIRMADA = 'S', FECHA_FIRMA = TO_DATE(p_fecha,'YYYYMMDD'), CODUSUARIO = '@BATCH' 
			                    	 	WHERE IDPOLIZA_SBP = v_id_poliza;		
			                    	 	num_pol_modif := num_pol_modif + 1;
										PQ_Utl.LOG(lc, 'Actualizacion correcta.', 2);

									EXCEPTION
	        					    	WHEN OTHERS THEN
	                            			PQ_Utl.LOG(lc, 'Error al updatear la poliza:' || v_id_poliza || ', con error:' || '. ' || sqlcode || ' [' || SQLERRM || ']' ,2);
	                            			num_pol_error := num_pol_error + 1;
	                            			-- Aunque la actualizacion falle continuamos con el siguiente registro
	        								CONTINUE;
	        				    	END;
		                    	 
	        				    END IF;
	                   	
	                   	END IF;
					
					END IF;
	                   
				EXCEPTION
	            	WHEN NO_DATA_FOUND THEN
					PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DEL FICHERO ' || v_nombre_fich || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
	        	END;
	                
			END LOOP;
				
			-- Guardamos los resultados en el fichero de log
			PQ_Utl.LOG(lc, '');
			PQ_Utl.LOG(lc, '*********************************************************************************', 2);
			PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || v_nombre_fich || '. FECHA ' || TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI:SS'), 2);
			PQ_Utl.LOG(lc, '*********************************************************************************', 2);
			PQ_Utl.LOG(lc, 'Registros procesados   := ' || num_registros, 2);
			PQ_Utl.LOG(lc, 'Polizas Modificadas    := ' || num_pol_modif, 2);
			PQ_Utl.LOG(lc, 'Polizas con error    := ' || num_pol_error, 2);
			PQ_Utl.LOG(lc, '*********************************************************************************', 2);
			PQ_Utl.LOG(lc, '', 2);
	
			-- Fin de proceso
			PQ_Utl.LOG(lc,'El proceso ha finalizado correctamente a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
			PQ_Utl.LOG(lc,'## FIN ##', 1);
	           	
			COMMIT;
			
			UTL_FILE.FCLOSE( f_fichero );
			
			PQ_Utl.LOG(lc,'PQ_INTEGRACION_GED - actualizar_firma_diferida - end', 1);
	    
	END actualizar_firma_diferida;
   
	FUNCTION fn_split (cadena IN VARCHAR2, separador IN CHAR) RETURN t_array IS
	  i NUMBER := 0;
	  pos NUMBER := 0;
	  lv_str VARCHAR2 (3000) := cadena;
	  strings t_array;
	BEGIN
	  -- remove leading and trailing spaces
	  lv_str := TRIM(lv_str);
	  -- replace multiple spaces with single space
	  WHILE INSTR(lv_str, '  ') > 0 LOOP
	    lv_str := REPLACE(lv_str, '  ', ' ');
	  END LOOP;
	  -- determine first chunk of string
	  pos := INSTR (lv_str, separador, 1, 1);
	  -- while there are chunks left, loop
	  IF (pos = 0) THEN
	    strings (1) := cadena;
	  ELSE
	    WHILE (pos != 0) LOOP
	      -- increment counter
	      i := i + 1;
	      -- trim the substring before adding to the array
	      strings (i) := TRIM(SUBSTR (lv_str, 1, pos - 1));
	      -- remove chunk from string
	      lv_str := SUBSTR (lv_str, pos + 1, LENGTH (lv_str));
	      -- determine next chunk
	      pos := INSTR (lv_str, separador, 1, 1);
	      -- no last chunk, add to array
	      IF pos = 0 THEN
	        strings (i + 1) := TRIM(lv_str);
	      END IF;
	    END LOOP;
	  END IF;
	  -- return array
	  RETURN strings;
END;

 
END PQ_INTEGRACION_GED;
/
SHOW ERRORS;