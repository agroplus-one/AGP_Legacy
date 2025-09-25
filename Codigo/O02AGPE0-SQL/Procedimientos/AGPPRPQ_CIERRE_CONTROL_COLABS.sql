SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_CIERRE_CONTROL_COLABS is

  
  TYPE t_cod_ent_subent IS RECORD(
		 entidad   NUMBER
		,subentidad NUMBER);

	TYPE t_array_cod_ent_subent IS TABLE OF t_cod_ent_subent INDEX BY BINARY_INTEGER;
   FUNCTION actualizar_colaboradores RETURN VARCHAR2;

end PQ_CIERRE_CONTROL_COLABS;
/
create or replace package body o02agpe0.PQ_CIERRE_CONTROL_COLABS is
  -- *******************************************************************
  -- ** Author  : U028975 (T-Systems)                                 ** 
  -- ** Created : 21.01.2020                                          **
  -- ** Recuperar fichero de formación y actualizar aquellos          **
  -- ** colaboradores que no hayan realizaro la formación obligatoria ** 
  -- ******************************************************************* 

  -- Array para almacenar los registros de la tabla TB_PAGO_ESTADOS_PAGO


 FUNCTION actualizar_colaboradores RETURN VARCHAR2 IS

       v_cif_colaborador     VARCHAR2(14);
       v_formacion           VARCHAR2(1);
       
       l_dir         		     TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
       l_dir_name    		     VARCHAR2(1000);				      	-- Ruta física donde se ubican los ficheros que se reciben
       
       l_dir_copia    	     TB_CONFIG_AGP.AGP_valor%TYPE; 	        -- Valor del parametro de configuracion para el DIRECTORY donde se dejan las copias de los ficheros recibidos
       l_dir_name_copia	     VARCHAR2(1000);					              -- Ruta física donde se ubican las copias de los ficheros que se reciben
       l_nombre      		     VARCHAR2(23) := 'formacion_colaboradores' ;    -- Nombre del fichero de recepción
       f_fichero    		     UTL_FILE.FILE_TYPE;                    -- Variable que almacena la referencia al fichero de envío
       lc                    VARCHAR2(50) := 'PQ_CIERRE_CONTROL_COLABS.actualizar_colaboradores'; -- Variable que almacena el nombre del paquete y de la función
       
       nom_var_config        VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       nom_var_config_copia  VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       nom_var_config_desa	 VARCHAR2 (30) := 'DIR_IN_AGP';        -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY en DESARROLLO
       
       nombre_fichero_colabs VARCHAR2(30):= 'NOMB_FIC_COLABS';

       l_line        		     VARCHAR2(25);         -- Variables que almacena la linea que se leerá del fichero
       v_valor               VARCHAR2(25);
       l_num_registros       NUMBER := 0;
       l_num_colab_ok        NUMBER := 0;          -- Contador de colaboradores con la formación obligatoria realizada
       l_num_colab_ko        NUMBER := 0;          -- Contador de colaboradores con la formación obligatoria NO REALIZADA
       l_num_entidades_ko    NUMBER := 0;          -- Contador de Entidades 3xxx-0 no informadas en el fichero y por tanto se realiza baja lógica
       l_form_realizada      VARCHAR2(1) :='1';
       l_form_no_realzda     VARCHAR2(1) :='0';
       v_fechabaja           VARCHAR2(10) :='';
       v_codEntidad          NUMBER :=0;
       v_codSubentidad       NUMBER :=0;
   		 v_consulta            VARCHAR2(2000) := '';
       v_cons_entidades      VARCHAR2(2500) := '';
       
    	 TYPE cur_typ IS REF CURSOR;
	     v_cursor    cur_typ;
 	     v_cursor2    cur_typ;
       l_ind       NUMBER := 0;
       
       l_array_ent_fich    t_array_cod_ent_subent;
       
       v_ent_inicial        NUMBER := 3000;
       v_ent_final          NUMBER := 3999;
       v_encontrado         BOOLEAN := false;
       l_ind_ent            NUMBER := 1;
       
       existeElArchivo  BOOLEAN;
       longitudEnBytes  NUMBER;
       numeroDeBloques  NUMBER;


  BEGIN

     -- Comienzo de escritura en log
     PQ_Utl.LOG(lc,'## INI CIERRE COLABORADORES SIN FORMACIÓN ##',1);

     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositan el fichero recibidos.   ', 2);
     

     -- TEST y PROD:
     l_dir  := PQ_Utl.getcfg(nom_var_config);
     l_dir_copia  := PQ_Utl.getcfg(nom_var_config_copia);
     

     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME = l_dir;

     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido.   ', 2);

     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name_copia FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir_copia;
     
     
     /**** ****/
     l_nombre :=PQ_Utl.getcfg(nombre_fichero_colabs);
     /* Antes de abrir el fichero verifico si existe */
     UTL_FILE.FGETATTR( l_dir, l_nombre || '.csv', existeElArchivo, longitudEnBytes, numeroDeBloques);

     IF existeElArchivo THEN
        PQ_Utl.LOG(lc, 'El fichero '|| l_nombre  || '.csv' ||' existe, continuamos.' );
     ELSE
        PQ_Utl.LOG(lc, 'El fichero '|| l_nombre || '.csv' ||' NO existe en la ruta: ' || l_dir );
        /* retornamos OK pero no continuamos */
        RETURN 'OK';
     END IF;

     /***  ****/

     -- Se abre el fichero de texto de salida en la ruta indicada
     PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.  ', 2);
     f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.csv',	open_mode    => 'r');
     
     
     -- Guarda una copia del fichero recibido en la ruta indicada
     --PQ_Utl.LOG(lc, 'UTL_FILE.fcopy('||l_dir||', '||l_nombre || '.csv, '||l_dir_copia||', '|| l_nombre || to_char(sysdate,'_YYMMDDHH24MISS') || '.csv)   ', 2);
     --UTL_FILE.fcopy(l_dir, l_nombre || '.csv', l_dir_copia, l_nombre  || to_char(sysdate,'_YYMMDDHH24MISS') || '.csv');

     -- Comienza el bucle para leer todas las líneas del fichero
     LOOP
        -- Lee la línea actual
        -- Si al leer se ha llegado al final del fichero, se lanzará la excepción 'no_data_found' y el flag
        -- v_tipo_exc a true indicará que la excepción la ha producido este método
        BEGIN
            UTL_FILE.get_line (f_fichero,l_line);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                PQ_Utl.LOG(lc, 'No hay mas registros en el fichero.  ', 2);
                EXIT;
        END;
        l_num_registros := l_num_registros + 1;

        -- Si la línea leída está vacía salta a la siguiente iteración para leer otra línea
        IF (l_line is null OR (LENGTH(l_line)<=1)) THEN
           PQ_Utl.LOG(lc, 'La línea está vacía, se lee la siguiente.', 2);
           CONTINUE;
        END IF;

        BEGIN
        
           /* recuperamos los valores de la línea leida */
           v_valor:= l_line;
 
           v_cif_colaborador := substr(v_valor, 1, instr(v_valor, ';') - 1);
           v_formacion := substr(v_valor, instr(v_valor, ';') + 1, 1);
           
           /* Construimos cursor de lectura */
           
           /* 24.01.2020 -> Se incluye validación para que solo se tengan en cuenta los CIF de las 
           /* subentidades distinta de 0 y cuya entidad Mediadora sea 3xxx */
           v_consulta := 'select fechabaja, codentidad, codsubentidad from o02agpe0.tb_subentidades_mediadoras sub';
        	 v_consulta := v_consulta || ' where nifcif =  ''' || v_cif_colaborador || ''' ';
           v_consulta := v_consulta || ' and sub.codentidad >= ' || v_ent_inicial || ' and sub.codentidad <= ' || v_ent_final;
           v_consulta := v_consulta || ' and sub.codsubentidad <> 0';
           
           /* Abrimos un cursor para obtener las polizas del CIF colaborador */
           OPEN v_cursor 
            FOR v_consulta;

           LOOP
              FETCH v_cursor
               INTO v_fechabaja, 
                    v_codEntidad, 
                    v_codSubentidad;
                      
              EXIT WHEN v_cursor%NOTFOUND;      
              
              /* Guardamos la Entidad del colaborador leido */
              l_ind := l_ind + 1;    
              l_array_ent_fich(l_ind).entidad := v_codEntidad;
              l_array_ent_fich(l_ind).subentidad := v_codSubentidad;
                      
              IF (v_formacion = l_form_no_realzda) THEN --> Formación No Realizada
                 /* Si la fecha de baja no está informada la actualizamos */
                 IF (v_fechabaja is null) THEN
                    update o02agpe0.tb_subentidades_mediadoras sub
                       set sub.fechabaja = sysdate
                     where nifcif = v_cif_colaborador
                       and sub.codentidad >= v_ent_inicial and sub.codentidad <= v_ent_final
                       and sub.codsubentidad <> 0;
                       
                    PQ_Utl.LOG(lc, 'Formación No Realizada, baja lógica del colaborador: ' || v_cif_colaborador, 2);
                      
                    l_num_colab_ko := l_num_colab_ko + 1;
                 ELSE
                    PQ_Utl.LOG(lc, 'Formación No Realizada, pero colaborador ya dado de baja: ' || v_cif_colaborador, 2);
                 END IF;
                     
              END IF; 
              
              IF (v_formacion = l_form_realizada) THEN
                 IF (v_fechabaja is not null) THEN
                    /* Eliminamos la baja lógica */
                    update o02agpe0.tb_subentidades_mediadoras sub
                       set sub.fechabaja = null
                     where nifcif = v_cif_colaborador
                       and sub.codentidad >= v_ent_inicial and sub.codentidad <= v_ent_final
                       and sub.codsubentidad <> 0;
                        
                    PQ_Utl.LOG(lc, 'Formación Realizada, eliminamos baja lógica del colaborador: ' ||v_cif_colaborador, 2);
                      
                    l_num_colab_ok := l_num_colab_ok + 1;
                 ELSE
                    PQ_Utl.LOG(lc, 'Formación Realizada, y colaborador activo: ' || v_cif_colaborador, 2);
                 END IF;
              END IF; 
                      
           END LOOP;
           
		       CLOSE v_cursor;
           
        EXCEPTION
           WHEN NO_DATA_FOUND THEN
              PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DEL FICHERO ' || v_cif_colaborador || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
        END;
       
     END LOOP;
     
     COMMIT;
      
     /* Una vez tengamos cargado el array con las entidades de los colaboradores leidos
     /* recuperamos las Entidades 3xxx-0, que no estén dadas de baja */

     /* Todas aquellas entidades Entidad 3xxx-0  que no se hayan tratado en el fichero 
     /* y por tanto no tengan la formación realizada se darán de baja */
      
     BEGIN
      
        v_cons_entidades := 'select sub.codentidad, sub.codsubentidad from o02agpe0.tb_subentidades_mediadoras sub ';
        v_cons_entidades := v_cons_entidades || ' where sub.codentidad >= ' || v_ent_inicial || ' and sub.codentidad <= ' || v_ent_final;
        v_cons_entidades := v_cons_entidades || ' and sub.codsubentidad <> 0 ';
        v_cons_entidades := v_cons_entidades || ' and sub.fechabaja is null';
        
        v_codEntidad := '';
        v_codSubentidad := '';  
      
        /* Abrimos un cursor para obtener las polizas del CIF colaborador */
        OPEN v_cursor2 
         FOR v_cons_entidades;

           LOOP
               v_encontrado := false;
         	     FETCH v_cursor2
                INTO v_codEntidad, 
                     v_codSubentidad;
                  
               BEGIN     
                  WHILE (l_array_ent_fich(l_ind_ent).entidad) IS NOT NULL LOOP
                     IF ((v_codEntidad = l_array_ent_fich(l_ind_ent).entidad) AND (v_codSubentidad = l_array_ent_fich(l_ind_ent).subentidad)) THEN
                        v_encontrado := true;
                     END IF;
            
                     l_ind_ent := l_ind_ent + 1;
                 
                  END LOOP;    
               EXCEPTION
                  WHEN NO_DATA_FOUND THEN
                     l_ind_ent := 1;
               END;    
              
               /* Si no se ha encotrado el registro, lo damos de baja */
               IF (v_encontrado = false) THEN
               
                  update o02agpe0.tb_subentidades_mediadoras sub
                     set sub.fechabaja = sysdate
                   where sub.codentidad = v_codEntidad
                     and sub.codsubentidad = v_codSubentidad;
                     
                   l_num_entidades_ko := l_num_entidades_ko + 1;
      
                  PQ_Utl.LOG(lc, 'Realizamos la baja lógica de la Entidad: ' || v_codEntidad || ' y SubEntidad: ' || v_codSubentidad, 2);
               END IF;
                    
               EXIT WHEN v_cursor2%NOTFOUND;
           END LOOP;
		       
           CLOSE v_cursor2;           
      
     EXCEPTION
        WHEN NO_DATA_FOUND THEN
           PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DEL FICHERO ' || v_cif_colaborador || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
     END;
   
     -- Guardamos los resultados en el fichero de log
     PQ_Utl.LOG(lc, '');
     PQ_Utl.LOG(lc, '*********************************************************************************', 2);
     PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI:SS'), 2);
     PQ_Utl.LOG(lc, '*********************************************************************************', 2);
     PQ_Utl.LOG(lc, 'Registros procesados              := ' || l_num_registros, 2);
     PQ_Utl.LOG(lc, 'Colaboradores Formación Realizada := ' || l_num_colab_ok, 2);
     PQ_Utl.LOG(lc, 'Colaboradores Formación No Realizada := ' || l_num_colab_ko, 2);
     PQ_Utl.LOG(lc, 'Entidades 3xxx-y Formación No Realizada := ' || l_num_entidades_ko, 2);
     PQ_Utl.LOG(lc, '*********************************************************************************', 2);
     PQ_Utl.LOG(lc, '', 2);

     -- Fin de proceso
     PQ_Utl.LOG(lc,'El proceso ha finalizado correctamente a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
     PQ_Utl.LOG(lc,'## FIN ##', 1);

     COMMIT;
     
     UTL_FILE.FCLOSE( f_fichero );
     
     /* 21/05/2020 - Por petición de RGA, se elimina el paso de renombrar el fichero y se deja tal cual */
     /*BEGIN
        UTL_FILE.FRENAME(l_dir, l_nombre || '.csv', l_dir_copia, l_nombre  || to_char(sysdate,'_YYMMDDHH24MISS') || '.csv', false);
     END;*/
     
    
     RETURN 'OK';

     EXCEPTION
        WHEN NO_DATA_FOUND THEN
           -- Hemos llegado al final del fichero
           COMMIT;
           
           return 'OK';
			  WHEN others THEN
           PQ_Utl.log(lc, 'CODIGO DE ERROR: ' || SQLCODE);
           PQ_Utl.log(lc, 'ERROR: ' || SQLERRM);
           ROLLBACK;
           
           -- Se indica que el proceso ha sido erróneo
    			 -- Se escribe en el log el error
           PQ_Utl.LOG(lc,'ERROR AL PROCESAR EL FICHERO DE CIERRE DE COLABORADORES ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
           PQ_Err.raiser(sqlcode,'Error al generar los ficheros de recepcion_fichero_formacion' || ' [' || SQLERRM || ']');
           
           -- Fin de proceso
           PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
           PQ_Utl.LOG(lc,'## FIN ##', 1);
           return 'KO';

     END;

end PQ_CIERRE_CONTROL_COLABS;
/
SHOW ERRORS;