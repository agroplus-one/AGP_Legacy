SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.PQ_RECEP_POLIZAS_AGRO_IRIS IS
/******************************************************************************
   NAME:       	PQ_RECEP_POLIZAS_AGRO_IRIS
   PURPOSE:    	Se procesa el fichero recibido de IRIS y se actualizan los datos
				        de las pólizas dependiendo del código recibido.

   REVISIONS:
   Ver        Date        Author             Description
   ---------  ----------  ---------------    ------------------------------------
   1.0        10/01/2019  Ruben Lopez		  1. Created this package.
                            (T-Systems)
   1.1        06/02/2020  T-Systemes.    2. Modificación Petición 55722-PTC6273
   1.2        06-04-2020  T-SYSTEMS      3. Subida a PROD Pet.55722 (PTC-6273)

******************************************************************************/

--------------------------------------------------------------------------------
-- PROCEDIMIENTOS PUBLICOS
-------------------------------------------------------------------------------

-- Carga las polizas recibidas de IRIS
FUNCTION recepcion_polizas_agro_iris RETURN VARCHAR2;

END PQ_RECEP_POLIZAS_AGRO_IRIS;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_RECEP_POLIZAS_AGRO_IRIS AS


	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION recepcion_polizas_agro_iris
	--
	-- Fichero de recepción de pólizas de IRIS
	--
	-- Obtenemos un fichero con el estado de las polizas integradas en IRIS
  -- Desde IRIS nos enviarán tres tipos de Estado de pólizas (Código de ERROR)
  -- Codigo de ERROR '00000' (Valor parametrizado en Tabla TB_CONFIG_AGP) -> Póliza Integrada CORRECTAMENTE EN IRIS
  -- Código de ERRROR > = '01000' --> Error de tipo No resuelto por Entidad, nosotros lo trataremos como si hubiera sido un alta
  --                                  correcta y actualizaremos con valor '00000', para no tener que volver a enviarla a IRIS
  -- Código de ERROR - Resto de Valores -> Se trataran como errores, se actualizarán los valores de error, con el código devuelto 
  --                                       por IRIS y se volverán a tratar al día siguiente. 
	-----------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------

-- Declaración de la tabla de estados de póliza
lc VARCHAR2(55) := 'pq_recep_polizas_agro_iris.recepcion_polizas_agro_iris';-- Variable que almacena el nombre del paquete y de la función

---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- FUNCTION getValNumerico
--
-- Devuelve el valor númerico de un dato.
--
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
FUNCTION getValNumerico (l_line VARCHAR2, ini NUMBER, fin NUMBER) RETURN NUMBER IS

v_str VARCHAR(100) := '';

BEGIN
  -- Se parte la línea entre las posiciones indicadas
  v_str := SUBSTR (l_line, ini, fin);

  -- Se devuelve el valor numérico de la cadena
  RETURN TO_NUMBER (v_str);

  -- Si ocurre cualquier error
  EXCEPTION
  WHEN OTHERS THEN
      -- Se escribe en el log el error
      PQ_Utl.LOG(lc,'Error al pasar a numero el valor ''' || v_str || '', 1);
      PQ_Utl.LOG(lc, SQLCODE || ' - ' || SQLERRM, 1);
      RETURN 0;
END;


---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- FUNCTION recepcion_polizas_agro_iris
--
-- Lee el fichero devuelto por IRIS y lo procesa
--
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- Inicio de declaración de función
FUNCTION recepcion_polizas_agro_iris RETURN VARCHAR2 IS

-- Variables generales

-- MODIF TAM PARA PRUEBAS DESARROLLO EN TEST Y PR MODIFICAR Y RESTABLECER LOS VALORES
nom_var_config    VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- TEST y PROD :Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
nom_var_config_copia    VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
--nom_var_config    VARCHAR2 (30) := 'DIR_IN_AGP'; -- DESA: Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
--nom_var_config_copia    VARCHAR2 (30) := 'DIR_IN_AGP'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY

l_dir         		TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
l_dir_name    		VARCHAR2(1000);					-- Ruta física donde se ubican los ficheros que se reciben

--(DESARROLLO)nom_var_config_copia    VARCHAR2 (30) := 'DIR_IN_AGP'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
l_dir_copia    		TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan las copias de los ficheros recibidos
l_dir_name_copia	VARCHAR2(1000);					-- Ruta física donde se ubican las copias de los ficheros que se reciben
l_nombre      		VARCHAR2(19) := 'P_POL_AGRO_IRIS_REC' ;-- Nombre del fichero de recepción
f_fichero    		  UTL_FILE.FILE_TYPE;     -- Variable que almacena la referencia al fichero de envío

l_num_polizas            NUMBER := 0;            -- Contador de pólizas actualizadas (cod error e idestado)
l_num_polizas_ok         NUMBER := 0;            -- Contador de pólizas Alta en Omega (cod error e idestado y Nº Referencia de Omega, es decir las que se han dado de alta en Omega)
l_num_registros          NUMBER := 0;            -- Contador de registros procesados
l_pol_not_found          NUMBER := 0;            -- Contador de registros no updateados por que no se encuentran en BBDD 
l_cod_error_iris         NUMBER := 0;
l_pol_error_noresuelto   NUMBER :=0;             -- Contador de registros con "Error No resuelto por Entidad" tratado como alta correcta.
l_num_cancel_error       NUMBER :=0;             -- Contador de cancelaciones recibidas con error.

l_line        		VARCHAR2(500);         -- Variables que almacena la linea que se leerá del fichero
v_ref_poliza   		VARCHAR2(8);            -- Variable auxiliar para almacenar la referencia de la póliza que se procesa
v_cod_error  		  VARCHAR2(6);            -- Variable auxiliar para almacenar el código de error recibido
v_plan_poliza     VARCHAR2(4);            -- Variable auxiliar para almacenar el código del plan de la póliza
v_tipo_referencia VARCHAR(1);

v_tipo_exc        BOOLEAN := false;       -- Variable que indica qué ha producido la excepción genérica 'no_data_found'
v_ref_desconocidas VARCHAR2(4000) := '';  -- Almacena las referencia de póliza que no se encuentran en las tablas

v_polizas_error	VARCHAR(4000) := '   '; -- Almacena las referencia de las pólizas que tiene error

nom_agp_nemo	VARCHAR(4000) := 'POLIZAS_IRIS_RECHAZADAS';

l_codigo_iris_ok     VARCHAR(5); 

-- ** Pet. 55722 ** MODIF TAM (07.02.2020) ** 
v_cancel_error    VARCHAR(4000) := '   '; -- Almacena las referencia de las cancelaciones que tiene error y que por tanto no han sido aceptadas por IRIS
l_num_cancel      NUMBER := 0;            -- Contador de Cancelaciones tratadas en el fichero de recepción de IRIS
l_num_cancel_ok   NUMBER := 0;            -- Contador de Cancelaciones aceptadas por IRIS
v_fecha_env_canc_iris DATE;
nom_agp_ref_cancel_nok  VARCHAR(40) := 'CANCEL_IRIS_AGRO_RECHZ';
nom_agp_cancel_ok       VARCHAR(40) := 'CANCEL_IRIS_AGRO_OK';
 

-- Inicio del cuerpo de la función
BEGIN

   -- Comienzo de escritura en log
   PQ_Utl.LOG(lc,'## INICIO PROCEDIMIENTO PQ_RECEP_POLIZAS_AGRO_IRIS ##',1);
   
   -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos de IRIS
   PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos.', 2);
   l_dir  := PQ_Utl.getcfg(nom_var_config);

   -- Se guarda el path fisico del directorio
   PQ_Utl.LOG(lc, 'Obtiene la ruta física del DIRECTORY.', 2);
   SELECT DIRECTORY_PATH 
	   into l_dir_name 
     FROM ALL_DIRECTORIES 
     WHERE DIRECTORY_NAME=l_dir;

   -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido de IRIS
   Pq_Utl.LOG(lc, 'Valor de nombre del fichero:' || l_nombre);
   PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido.', 2);
   l_dir_copia  := PQ_Utl.getcfg(nom_var_config_copia);
   
   -- Se guarda el path fisico del directorio
   PQ_Utl.LOG(lc, 'Obtiene la ruta física del DIRECTORY de copia.', 2);
   SELECT DIRECTORY_PATH 
	    into l_dir_name_copia 
     FROM ALL_DIRECTORIES 
     WHERE DIRECTORY_NAME=l_dir_copia;
     
   -- Obtenemos el valor de codigo de ok para IRIS de TB_COFIG_AGP
   SELECT con.agp_valor 
     into l_codigo_iris_ok
     FROM o02agpe0.TB_CONFIG_AGP con
   WHERE con.agp_nemo = 'ESTADO_IRIS_OK';  

   -- Guarda una copia del fichero recibido en la ruta indicada
   PQ_Utl.LOG(lc, 'Valor de l_nombre:'||l_nombre);
   PQ_Utl.LOG(lc, 'Se copia el fichero recibido en :' || l_dir_name_copia || ' con nombre: ' || l_nombre || to_char(sysdate,'YYMMDDHH24MISS') || '.');
   UTL_FILE.fcopy(l_dir, l_nombre || '.TXT', l_dir_copia, l_nombre  || to_char(sysdate,'YYMMDDHH24MISS') || '.TXT');


   -- Se abre el fichero de texto de salida en la ruta indicada
   PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.', 2);
   

   --f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.TXT',	open_mode    => 'r', max_linesize => 150000);
   f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.TXT',	open_mode    => 'r');

   
   -- Comienza el bucle para leer todas las líneas del fichero
   LOOP
	   -- Cambia el flag v_tipo_exc
	   v_tipo_exc := true;

	   -- Lee la línea actual
	   -- Si al leer se ha llegado al final del fichero, se lanzará la excepción 'no_data_found' y el flag
	   -- v_tipo_exc a true indicará que la excepción la ha producido este método
	   UTL_FILE.get_line (f_fichero,l_line);
	   l_num_registros := l_num_registros + 1;
	   --PQ_Utl.LOG(lc, '---', 2);
	   --PQ_Utl.LOG(lc, 'Leida la línea ' || l_num_registros, 2);

	   -- Si la línea leída está vacía salta a la siguiente iteración para leer otra línea
	   IF (l_line is null OR (LENGTH(l_line)<=1)) THEN
		  PQ_Utl.LOG(lc, 'La línea está vacía, se lee la siguiente.', 2);
		  CONTINUE;
	   END IF;

	   -- Cambia el flag v_tipo_exc
	   v_tipo_exc := false;

	   
	   -- Obtiene los valores de la referencia de la poliza y el codigo de error
	   v_ref_poliza := SUBSTR (l_line, 8, 7);
	   v_tipo_referencia := SUBSTR (l_line, 6, 1);
     v_plan_poliza := SUBSTR (l_line, 1, 4);
	   v_cod_error := SUBSTR (l_line, 64, 5);
     
     
     -- MODIF TAM (21.03.2019)
     -- Si la poliza retorna un error del tipo "No resuelto por la Entidad" tendrá un valor superior o igual al 01000
     -- En este caso por especificación de RGA, trataremos la poliza como si hubiera sido OK en IRIS y la guardamos como tal
     -- para no volverla e enviar en el proceso 
     l_cod_error_iris := SUBSTR (l_line, 64, 5); 
     
     IF (l_cod_error_iris >= 1000) THEN
       v_cod_error := l_codigo_iris_ok;
        PQ_Utl.LOG(lc, 'La póliza: ' || v_ref_poliza || ' del tipo: ' || v_tipo_referencia || 
                        ' en el plan: ' || v_plan_poliza || ' Viene con error No resuelto por la Entidad' , 2);
                        
       l_pol_error_noresuelto := l_pol_error_noresuelto + 1;                 
       
     END IF;  
     
     
   	 -- Escribe en el log los datos del registro recibido
	   --PQ_Utl.LOG(lc, '- Referencia Agroplus: ' || v_ref_poliza, 2);
   	 --PQ_Utl.LOG(lc, '- Tipo de referencia: ' || v_tipo_referencia, 2);
     --PQ_Utl.LOG(lc, '- Plan Póliza: ' || v_plan_poliza, 2);
	   --PQ_Utl.LOG(lc, '- Código: ' || v_cod_error, 2);
     
     -- Pet. 55722 ** MODIF TAM (07.02.2020) ** Inicio
     -- Una vez leemos el registro comprobamos si lo que tenemos que actualizar es el código de Cancelación o el del envío.
     
     -- Buscamos si la póliza se ha enviado y aceptado y la cancelación tiene fecha de envío informada.
     BEGIN
        SELECT pol.Fecha_Env_Canc_Iris
          into v_fecha_env_canc_iris
          FROM o02agpe0.Tb_polizas pol
         WHERE pol.REFERENCIA = v_ref_poliza 
          AND pol.TIPOREF = v_tipo_referencia
          AND pol.LINEASEGUROID in (SELECT L.LINEASEGUROID
                                    FROM o02agpe0.TB_LINEAS L
                                   WHERE L.CODPLAN = v_plan_poliza) 
          AND pol.ESTADO_IRIS = l_codigo_iris_ok AND pol.FECHA_ENVIO_IRIS IS NOT NULL
          AND pol.FECHA_ENV_CANC_IRIS IS NOT NULL; 
          
          -- Se hace update del registro de la poliza para actualizar la fecha de recepcion IRIS y el estado
        UPDATE TB_POLIZAS P
           SET P.ESTADO_CANC_IRIS = v_cod_error
         WHERE P.REFERENCIA = v_ref_poliza 
           AND P.TIPOREF = v_tipo_referencia
           AND P.LINEASEGUROID in (SELECT L.LINEASEGUROID
                                    FROM o02agpe0.TB_LINEAS L
                                   WHERE L.CODPLAN = v_plan_poliza); 
       
        IF (sql%rowcount = 0) THEN 
           l_pol_not_found := l_pol_not_found + 1;
           PQ_Utl.LOG(lc, 'No se actualiza la cancelación: ' || v_ref_poliza || ' del tipo: ' || v_tipo_referencia || 
                          ' en el plan: ' || v_plan_poliza || ' por que no se encuentra en BBDD' , 2);

        END IF;      
    
		    IF (v_cod_error <> l_codigo_iris_ok) THEN
          IF (v_cancel_error is null OR (LENGTH(v_cancel_error)<=1990)) THEN
		 	       v_cancel_error := v_cancel_error ||  v_ref_poliza || ', ';
          END IF;   
          l_num_cancel_error := l_num_cancel_error + 1;
		    ELSE
			    l_num_cancel_ok := l_num_cancel_ok + 1;
		    END IF;
	   
		   l_num_cancel := l_num_cancel + 1;
                                         
     EXCEPTION
       WHEN no_data_found THEN
          -- Si no se ha encontrado nada, es que no es cancelación y por tanto se trata la póliza como hasta ahora.
          
          -- Se hace update del registro de la poliza para actualizar la fecha de recepcion IRIS y el estado
          UPDATE TB_POLIZAS P
             SET P.FECHA_RECEPCION_IRIS = sysdate,
                 P.ESTADO_IRIS = v_cod_error
           WHERE P.REFERENCIA = v_ref_poliza 
             AND P.TIPOREF = v_tipo_referencia
             AND P.LINEASEGUROID in (SELECT L.LINEASEGUROID
                                    FROM o02agpe0.TB_LINEAS L
                                   WHERE L.CODPLAN = v_plan_poliza); 
       
           IF (sql%rowcount = 0) THEN 
              l_pol_not_found := l_pol_not_found + 1;
              PQ_Utl.LOG(lc, 'No se actualiza la póliza: ' || v_ref_poliza || ' del tipo: ' || v_tipo_referencia || 
                             ' en el plan: ' || v_plan_poliza || ' por que no se encuentra en BBDD' , 2);

           END IF;      
    
		       IF (v_cod_error <> l_codigo_iris_ok) THEN
             IF (v_polizas_error is null OR (LENGTH(v_polizas_error)<=1990)) THEN
		 	          v_polizas_error := v_polizas_error ||  v_ref_poliza || ', ';
             END IF;   
		       ELSE
			       l_num_polizas_ok := l_num_polizas_ok + 1;
		       END IF;
	   
		       l_num_polizas := l_num_polizas + 1;
           
       WHEN OTHERS THEN
	        -- Rollback de los updates que se hayan lanzado anteriormente
	        ROLLBACK;
	        -- Se escribe en el log el error
	        PQ_Err.raiser(sqlcode,'Error al generar los ficheros de recepcion_polizas_agro_iris' || ' [' || SQLERRM || ']');
	        -- Fin de proceso
	        PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
	        PQ_Utl.LOG(lc,'## FIN ##', 1);
	        -- Se vuelve a lanzar la excepción para parar la cadena
	        raise;
     END;
                               
   END LOOP;
   
   -- Control de excepciones
   EXCEPTION
   -- Si no hay más líneas en el fichero recibido
   WHEN no_data_found THEN

	  -- Si v_tipo_exc es false indicará que la excepción ha sido producida porque no existe ID asociada
	  -- a una referencia indicada en el fichero de recepción
	  IF (v_tipo_exc = false) THEN
		   -- Rollback de los updates que se hayan lanzado anteriormente
  		 ROLLBACK;
	
    	 -- Se escribe en el log el error
		   PQ_Err.raiser(sqlcode,'No se ha encontrado ID en la tabla TB_RC_POLIZAS asociado a esa referencia.' || ' [' || SQLERRM || ']');
		   UTL_FILE.FCLOSE( f_fichero );
		 
       -- Fin de proceso
		   PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
		   PQ_Utl.LOG(lc,'## FIN ##', 1);
		 
       -- Se vuelve a lanzar la excepción para parar la cadena
		   raise;
	  END IF;

    
	  -- Si no, se ha llegado al final del fichero de recepción
	  PQ_Utl.LOG(lc, 'Se ha llegado al final del fichero.', 2);
    
    
    /*** Pet. 55722 ** MODIF TAM(02.04.2020) ** Inicio ***/
   /* Actualizamos todas aquellas Cancelaciones que se han enviado y no se han recibido
   /* en el fichero de recepción con estado correcto */
   
   PQ_Utl.LOG(lc, 'Actualizamos las cancelaciones que no están en el fichero a correctas', 2);
   UPDATE TB_POLIZAS P
         SET P.ESTADO_CANC_IRIS = l_codigo_iris_ok         
       WHERE TO_DATE(P.Fecha_Env_Canc_Iris) = TO_DATE(sysdate) 
         AND P.Estado_Canc_Iris IS NULL;
         
      IF (sql%rowcount > 0) THEN 
           l_num_cancel_ok := sql%rowcount;
      ELSE
           PQ_Utl.LOG(lc, 'No se encuentran cancelaciones correctas: ' ||sql%rowcount , 2);  
      END IF;      
   
     /*** Pet. 55722 ** MODIF TAM(02.04.2020) ** Inicio ***/
    
    -- Eliminamos la ultima coma de la cadena de polizas rechazadas
    v_polizas_error := SUBSTR (v_polizas_error, 1, LENGTH (v_polizas_error) - 2);
    v_cancel_error := SUBSTR (v_cancel_error, 1, LENGTH (v_cancel_error) - 2);
    
    PQ_Utl.LOG(lc, 'Actualizamos los indicadores de cancelaciones y polizas', 2);
     
    -- Actualizamos el valor de la tabla de configuraciones con la cadena de polizas rechazas en la ejecucion
    UPDATE TB_CONFIG_AGP C
      SET C.AGP_VALOR = v_polizas_error
    WHERE C.AGP_NEMO = nom_agp_nemo;
    
    -- ** Pet. 55722 ** MODIF TAM (07.02.2020) **
    
    -- Actualizamos los valores de la tabla de configuraciones con la cadena de polizas rechazadas en la ejecución
     
    UPDATE TB_CONFIG_AGP C
      SET C.AGP_VALOR = v_cancel_error
    WHERE C.AGP_NEMO = nom_agp_ref_cancel_nok;
    
    UPDATE TB_CONFIG_AGP C
      SET C.AGP_VALOR = l_num_cancel_ok
    WHERE C.AGP_NEMO = nom_agp_cancel_ok;
    
    PQ_Utl.LOG(lc, 'Finalizamos con las actualizaciones', 2);

    
    -- ** Pet. 55722 ** MODIF TAM (07.02.2020) **
   
    
	  -- Se cierra el fichero de envío
	  PQ_Utl.LOG(lc, 'Se cierra el fichero.', 2);
	  UTL_FILE.FCLOSE( f_fichero );
	  -- Se hace commit de las transacciones
	  PQ_Utl.LOG(lc, 'Se hace commit de las transacciones.', 2);
	  COMMIT;

	  -- Guardamos los resultados en el fichero de log
	  PQ_Utl.LOG(lc, '');
	  PQ_Utl.LOG(lc, '*********************************************************************************', 2);
 	  PQ_Utl.LOG(lc, 'FICHERO DE RECEPCIÓN PÓLIZAS AGRO INTEGRADAS EN IRIS', 2);
    PQ_Utl.LOG(lc, '*********************************************************************************', 2);
	  PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI:SS'), 2);
	  PQ_Utl.LOG(lc, '*********************************************************************************', 2);
	  PQ_Utl.LOG(lc, 'Registros procesados                              := ' || l_num_registros, 2);   
	  PQ_Utl.LOG(lc, 'Polizas alta en Iris                              := ' || l_num_polizas_ok, 2);
    PQ_Utl.LOG(lc, 'Polizas alta en Iris (error no resulto Entidad)   := ' ||l_pol_error_noresuelto, 2);
	  PQ_Utl.LOG(lc, 'Polizas actualizadas                              := ' || l_num_polizas, 2);
    PQ_Utl.LOG(lc, 'Polizas No Actualizadas (not_found)               := ' || l_pol_not_found, 2);
    PQ_Utl.LOG(lc, '*********************************************************************************', 2);
    PQ_Utl.LOG(lc, 'Cancelaciones actualizadas con error              := ' || l_num_cancel_error        , 2);
    PQ_Utl.LOG(lc, 'Cancelaciones actualizadas OK                     := ' || l_num_cancel_ok, 2); 
	  PQ_Utl.LOG(lc, '*********************************************************************************', 2);
	  PQ_Utl.LOG(lc, '', 2);

	  -- Fin de proceso
	  PQ_Utl.LOG(lc,'El proceso ha finalizado correctamente a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
	  PQ_Utl.LOG(lc,'## FIN ##', 1);

	  RETURN 'OK';

  -- Si ocurre cualquier otro error
  WHEN OTHERS THEN
	  -- Rollback de los updates que se hayan lanzado anteriormente
	  ROLLBACK;
	  -- Se escribe en el log el error
	  PQ_Err.raiser(sqlcode,'Error al generar los ficheros de recepcion_polizas_agro_iris' || ' [' || SQLERRM || ']');
	  -- Fin de proceso
	  PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
	  PQ_Utl.LOG(lc,'## FIN ##', 1);
	  -- Se vuelve a lanzar la excepción para parar la cadena
	  raise;

   END;
-- Fin del cuerpo de la función principal


END PQ_RECEP_POLIZAS_AGRO_IRIS;
/
SHOW ERRORS;