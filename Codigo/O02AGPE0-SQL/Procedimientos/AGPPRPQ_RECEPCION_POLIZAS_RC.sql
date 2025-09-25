SET DEFINE OFF;
SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE o02agpe0.PQ_RECEPCION_POLIZAS_RC IS
/*******************************************************************************
   NAME:       	PQ_RECEPCION_POLIZAS_RC
   PURPOSE:    	Se procesa el fichero recibido de OMEGA y se actualizan los datos
				        de las polizas de R.C Ganado dependiendo del codigo recibido.

   REVISIONS:
   Ver        Date        Author             Description
   ---------  ----------  ---------------    ------------------------------------
   1.0        22/01/2018  Tatiana Albaladejo  1. Created this package.
                            (T-Systems)

******************************************************************************/

-------------------------------------------------------------------------------
-- PROCEDIMIENTOS PUBLICOS
-------------------------------------------------------------------------------

-- Carga las polizas de R.C Ganado
FUNCTION recepcion_polizas_rc_ganado(p_fecha IN VARCHAR2) RETURN VARCHAR2;

-- Comprueba si la poliza ya ha sido tratada anteriormente
FUNCTION isPolizaTratada (referencia   in varchar2, tipoEnvio in varchar2) RETURN BOOLEAN;

-- Devuelve el id de la poliza de R.C Ganado asociado a la referencia y tipo de envio pasado como parametro
FUNCTION getIdPolizaRc (v_ref_poliza   in varchar2, v_dc  in varchar2) RETURN NUMBER;

END PQ_RECEPCION_POLIZAS_RC;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_RECEPCION_POLIZAS_RC AS
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- FUNCTION recepcion_polizas_rc
--
-- Fichero de Polizas de R.C Ganado
--
-- Lee el fichero de respuesta enviado por OMEGA y actuliza las polizas de R.C Ganado dependiendo del codigo devuelto
--
--
-----------------------------------------------------------------------------------------------------------------------
----- | NOMBRE						  | TIPO				| CAMPO
----- |-------------------------------|---------------------|----------------------------------------------------------
-- 01 | N? de poliza de Agroplus		  | AF(8)				| TB_RC_POLIZAS.IDPOLIZA
-- 02 | Fecha de efecto				      | N(8)				| TO_CHAR(SYSDATE, ''DDMMYYYY'')
-- 03 | Fecha de vencimiento		    | N(8)				| ???
-- 04 | Forma de Pago               | AF(1)       |
-- 04 | Cuenta de domiciliacion		  | N(20)				| TB_DATOS_ASEGURADOS.CCC
-- 05 | Codigo de la entidad		    | N(4)				| TB_COLECTIVOS.CODENTIDAD
-- 06 | Codigo de la oficina		    | N(4)				| TB_POLIZAS.OFICINA
-- 07 | NIF del asegurado		        | AF(9)				| TB_ASEGURADOS.NIFCIF (a?adir 0 por la izquierda si no llega a longitud 9)
-- 08 | Nombre y apellidos			    | AF(100)			| TB_ASEGURADOS.NOMBRE, APELLIDO1, APELLIDO2 (a?adir espacios en blanco por la derecha hasta llegar a 100)
-- 09 | Codigo de riesgo            | AF(10)      |
-- 10 | Tipo de Via                 | AF(2)	      |
-- 11 | Nombre via                  | AF(50)      |
-- 12 | N? de la Via                | AF(10)      |
-- 13 | Poblacion                   | AF(30)      |
-- 14 | Codigo de Provincia         | N(2)        |
-- 15 | Codigo Postal               | N(5)        |
-- 16 | Codigo de Pais              | N(3)        |
-- 17 | Suma Asegurada R.C          | N(10,2)     |
-- 18 | Tipo de Tasa                | AF(2)       |
-- 19 | Criterios de Tarificacion   | AF(4)       |
-- 20 | Base Calculo                | D(10,2)     |
-- 21 | Importe R.C                 | D(10,2)     |
-- 22 | Prima Minima de R.C         | D(10,2)     |
-- 23 | Limite maximo por victima   | D(10,2)     |
-- 24 | Prima Neta Garantia         | D(10,2)     |
-- 25 | Tipo franquicia             | AF(1)       | Siempre valor 'A'-Absoluta
-- 26 | Valor Franquicia            | N(5)        |
-- 27 | Desc. del Riesgo            | AF(762)     |RESPONSABILIDAD CIVIL QUE LE PUEDA SER EXIGIDA AL ASEGURADO DE ACUERDO CON
--				                                        | LAS CONDICIONES ADJUNTAS EN SU CALIDAD DE PROPIETARIO DE xxx CABEZAS DE GANADO yyy
--				                                        |CON TIPO DE MANEJO zzz", donde xxx es el numero de animales, yyy la especie y zzz el regimen.
-- 28 | Codigo de error             |AF(6)			  | TB_RC_ERRORES.ID
-- 29 | Descripcion del error       |AF(50)		    | TB_RC_ERRORES.DESC_ERROR
-- 30 | Producto Tecnico            |N(4)         |Vendra siempre 4001
-- 31 | N? Poliza Omega             |N(9)         |Referencia de la RC de Ganado en Omega
-----------------------------------------------------------------------------------------------------------------------
-- |
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------



-- Declaracion de la tabla de estados de poliza
-- Para almacenar la referencia (GXXXXXX) + tipo de envio (A o S)
TYPE TABLA_ESTADO_POLIZA IS TABLE OF VARCHAR(8) INDEX BY BINARY_INTEGER;
t_est_plz         TABLA_ESTADO_POLIZA;        -- Tabla que contiene el estado de cada poliza procesada
lc VARCHAR2(55) := 'pq_recepcion_polizas_rc.recepcion_polizas_rc_ganado';-- Variable que almacena el nombre del paquete y de la funcion

---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- FUNCTION getValNumerico
--
-- Devuelve el valor numerico de un dato.
--
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
FUNCTION getValNumerico (l_line VARCHAR2, ini NUMBER, fin NUMBER) RETURN NUMBER IS

v_str VARCHAR(100) := '';

BEGIN
  -- Se parte la linea entre las posiciones indicadas
  v_str := SUBSTR (l_line, ini, fin);

  -- Se devuelve el valor numerico de la cadena
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
-- FUNCTION getValNumerico
--
-- Devuelve el valor numerico de un dato.
--
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- Inicio de declaracion de funcion
FUNCTION recepcion_polizas_rc_ganado(p_fecha IN VARCHAR2) RETURN VARCHAR2 IS

-- Variables generales
estado_correcto   VARCHAR2(1) := '5';         -- Codigo de estado correspondiente a 'Enviada correcta'
estado_erroneo    VARCHAR2(1) := '4';         -- Codigo de estado correspondiente a 'Enviada erronea'
-- MODIF TAM PARA PRUEBAS DESARROLLO EN TEST Y PR MODIFICAR Y RESTABLECER LOS VALORES
nom_var_config    VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- TEST y PROD :Nombre de la variable de ocnfiguracion de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
nom_var_config_copia    VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguracion de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
--nom_var_config    VARCHAR2 (30) := 'DIR_IN_AGP'; -- DESA: Nombre de la variable de ocnfiguracion de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
--nom_var_config_copia    VARCHAR2 (30) := 'DIR_IN_AGP'; -- Nombre de la variable de ocnfiguracion de TB_CONFIG_AGP que contiene el nombre del DIRECTORY

l_dir         		TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
l_dir_name    		VARCHAR2(1000);					-- Ruta fisica donde se ubican los ficheros que se reciben

--(DESARROLLO)nom_var_config_copia    VARCHAR2 (30) := 'DIR_IN_AGP'; -- Nombre de la variable de ocnfiguracion de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
l_dir_copia    		TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan las copias de los ficheros recibidos
l_dir_name_copia	VARCHAR2(1000);					-- Ruta fisica donde se ubican las copias de los ficheros que se reciben
l_nombre      		VARCHAR2(8) := 'P_RC_REC' ;-- Nombre del fichero de recepcion
f_fichero    		  UTL_FILE.FILE_TYPE;     -- Variable que almacena la referencia al fichero de envio

l_num_polizas     NUMBER := 0;            -- Contador de polizas actualizadas (cod error e idestado)
l_num_polizas_ok  NUMBER := 0;            -- Contador de polizas Alta en Omega (cod error e idestado y N? Referencia de Omega, es decir las que se han dado de alta en Omega)
l_num_registros   NUMBER := 0;            -- Contador de registros procesados
l_num_ref_desc    NUMBER := 0;            -- Contador de registros no encontrados en RC_POLIZAS.

l_line        		VARCHAR2(1500);         -- Variables que almacena la linea que se leera del fichero
-- MODIF TAM (15.02.2018) ** Inicio --
--v_ref_poliza  		VARCHAR2(8);          -- Variable auxiliar para almacenar la referencia de la poliza que se procesa
v_ref_poliza   		VARCHAR2(7);            -- Variable auxiliar para almacenar la referencia de la poliza que se procesa
v_dc              VARCHAR2(1);            -- Variable auxiliar para almacenar la el D.C de la referencia de la poliza.
-- MODIF TAM (15.02.2018) ** Fin --
v_cod_error  		  VARCHAR2(6);            -- Variable auxiliar para almacenar el codigo de error recibido
v_desc_error      VARCHAR2(50);           -- Variable auxiliar para almacenar la descripcion del codigo de error recibido.
v_id_plz_rc       NUMBER(15);             -- Variable auxiliar para almacenar el ID del registro correspondiente a la poliza de R.C Ganado
v_tipo_exc        BOOLEAN := false;       -- Variable que indica que ha producido la excepcion generica 'no_data_found'
cod_error_ok  		VARCHAR2(6) := 'PRP565';     -- Codigo de error correspondiente a 'Poliza recibida correctamente'
v_aux_error       VARCHAR2(6);            -- Variable que almacena el ERROR que hay en TB_RC_ERRORES para saber si existe.
v_tipo_envio      VARCHAR2(1);            -- Almacena el tipo de envio de la poliza (principal o suplemento)
v_ref_desconocidas VARCHAR2(4000) := '';  -- Almacena las referencia de poliza que no se encuentran en las tablas

--v_prodTec TB_RC_POLIZAS.PROD_TECNICO%TYPE; --> En la tabla de RC_POLIZAS no hay campo del producto tecnico, siempre sera 4001
v_prodTec NUMBER(4);

v_refOmega TB_RC_POLIZAS.REFERENCIA_OMEGA%TYPE;
v_codusuario    VARCHAR2(8) :='Batch Re';

v_fecha_planif	DATE;

-- Inicio del cuerpo de la funcion
BEGIN
   -- Comienzo de escritura en log
   PQ_Utl.LOG(lc,'## INI ##',1);

    PQ_Utl.LOG(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);

    -- Convertimos a DATE la fecha de planificacion recibida por parametro desde el sh
	v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');


   -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos de OMEGA
   PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos.', 2);
   l_dir  := PQ_Utl.getcfg(nom_var_config);

   -- Se guarda el path fisico del directorio
   PQ_Utl.LOG(lc, 'Obtiene la ruta fisica del DIRECTORY.', 2);
   SELECT DIRECTORY_PATH
     into l_dir_name
   FROM ALL_DIRECTORIES
   WHERE DIRECTORY_NAME=l_dir;

   -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositara la copia del fichero recibido de OMEGA
   PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositara la copia del fichero recibido.', 2);
   l_dir_copia  := PQ_Utl.getcfg(nom_var_config_copia);

   -- Se guarda el path fisico del directorio
   PQ_Utl.LOG(lc, 'Obtiene la ruta fisica del DIRECTORY de copia.', 2);
   SELECT DIRECTORY_PATH
     into l_dir_name_copia
   FROM ALL_DIRECTORIES
   WHERE DIRECTORY_NAME=l_dir_copia;

   -- Guarda una copia del fichero recibido en la ruta indicada
   PQ_Utl.LOG(lc, 'Se copia el fichero recibido en :' || l_dir_name_copia || ' con nombre: ' || l_nombre || to_char(v_fecha_planif,'YYMMDDHH24MISS') || '.');
   UTL_FILE.fcopy(l_dir, l_nombre || '.TXT', l_dir_copia, l_nombre  || to_char(v_fecha_planif,'YYMMDDHH24MISS') || '.TXT');


   -- Se abre el fichero de texto de salida en la ruta indicada
   PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.', 2);

   -- TAM (30.01.2017) * Inicio --
   -- Al leer el fichero me retorna un error y es posible que sea por la longitud de la linea, en este caso la aumentamos
   --f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.TXT',	open_mode    => 'r');

   f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.TXT',	open_mode    => 'r', max_linesize => 1500);
   -- TAM (30.01.2017) * fin --


   -- Comienza el bucle para leer todas las lineas del fichero
   LOOP
       -- Cambia el flag v_tipo_exc
       v_tipo_exc := true;

       -- Lee la linea actual
       -- Si al leer se ha llegado al final del fichero, se lanzara la excepcion 'no_data_found' y el flag
       -- v_tipo_exc a true indicara que la excepcion la ha producido este metodo
       UTL_FILE.get_line (f_fichero,l_line);
       l_num_registros := l_num_registros + 1;
       PQ_Utl.LOG(lc, '---', 2);
       PQ_Utl.LOG(lc, 'Leida la linea ' || l_num_registros, 2);

       -- Si la linea leida esta vacia salta a la siguiente iteracion para leer otra linea
       IF (l_line is null OR (LENGTH(l_line)<=1)) THEN
          PQ_Utl.LOG(lc, 'La linea esta vacia, se lee la siguiente.', 2);
          CONTINUE;
       END IF;

       -- Cambia el flag v_tipo_exc
       v_tipo_exc := false;

       -- Obtiene de la linea el numero de referencia de la poliza Agroplus.
       -- MODIF TAM (15.02.2018) -- Por una incidencia detectada, se ha modificado el valor de la referencia
       -- de envio y de Recepcion. Ahora viene informada con el campo referencia y D.C de la tabla TB_POLIZAS
       v_ref_poliza := SUBSTR (l_line, 0, 7);
       v_dc :=SUBSTR (l_line, 8, 1);

       -- Se modifica el formato de recepcion para incluir el producto tecnico y la referencia de la poliza
       -- en Omega antes del codigo y descripcion del error
       v_cod_error := SUBSTR (l_line, 1125, 6);
       v_desc_error := SUBSTR (l_line, 1131, 50);
       v_prodTec := getValNumerico (l_line, 1181, 4);
       v_refOmega := getValNumerico (l_line, 1185, 9);

       -- Escribe en el log los datos del registro recibido
       PQ_Utl.LOG(lc, 'Poliza de R.C Ganado recibida con datos: ', 2);
       PQ_Utl.LOG(lc, '- Referencia Agroplus: ' || v_ref_poliza, 2);
       PQ_Utl.LOG(lc, '- D.C Agroplus:' || v_dc, 2);
       PQ_Utl.LOG(lc, '- Id Poliza Agroplus: ' || v_id_plz_rc, 2);
       PQ_Utl.LOG(lc, '- Codigo: ' || v_cod_error, 2);
       PQ_Utl.LOG(lc, '- Producto tecnico: ' || v_prodTec, 2);
       PQ_Utl.LOG(lc, '- Referencia Omega: ' || v_refOmega, 2);

       -- Si la referencia de la poliza no consta como actualizada,
       -- se inserta en la tabla TB_RC_ERRORES el registro que asocia el ID de la poliza de
       -- R.C Ganado con el codigo de error
       IF (isPolizaTratada (referencia => v_ref_poliza, tipoEnvio => v_tipo_envio) = false) THEN

           v_id_plz_rc := getIdPolizaRc (v_ref_poliza => v_ref_poliza, v_dc => v_dc);

           PQ_Utl.LOG(lc, 'recuperamos el id de la poliza: ' || v_id_plz_rc);
           -- Si el id es NULL se a?ade la referencia a la variable y se va a la siguiente iteracion
           IF (v_id_plz_rc) IS NULL THEN

             -- Actualizamos el contador de numero de Polizas con referencia desconocida.
             l_num_ref_desc := l_num_ref_desc + 1;

              IF (v_ref_desconocidas IS NULL) THEN
                 v_ref_desconocidas := v_ref_poliza;
              ELSE
                 v_ref_desconocidas := v_ref_desconocidas || ',' || v_ref_poliza;
              END IF;

              CONTINUE;
           END IF;

           -- Comprueba si ya existe registro de codigo de error
           -- para la actualizacion en la tabla TB_RC_ERRORES
           SELECT COUNT(*)
             into v_aux_error
           FROM TB_RC_ERRORES
           WHERE ID = v_cod_error;

           -- Si NO hay registro se hace el INSERT.
           IF (v_aux_error = 0) THEN

               INSERT INTO TB_RC_ERRORES(id, descripcion)
               VALUES (v_cod_error, v_desc_error);
               PQ_Utl.LOG(lc, 'Insertamos Registro de error Nuevo: '
                         || v_cod_error || ' y descripcion: ' || v_desc_error , 2);
           END IF;


           -- Se hace update del registro de la poliza para actualizar el cod_error recibido.
           UPDATE TB_RC_POLIZAS
             SET IDERROR = v_cod_error
           WHERE ID = v_id_plz_rc;

           PQ_Utl.LOG(lc, 'Registro de error de la poliza de R.C Ganado con id: ' || v_id_plz_rc ||
                          ' y idPoliza:' || v_ref_poliza || ' actualizado a: ' || v_cod_error
                         || ', referencia de Omega: ' || v_refOmega || ' y producto tecnico: ' || v_prodTec  , 2);

           -- Se actualiza el estado de la poliza en la tabla TB_RC_POLIZAS dependiendo del codigo de error obtenido
           IF (v_cod_error = cod_error_ok) THEN
              -- Actualiza tambien la referencia de Omega
              UPDATE TB_RC_POLIZAS S
                 SET IDESTADO = estado_correcto,
                     REFERENCIA_OMEGA = v_refOmega
              WHERE ID = v_id_plz_rc;

              l_num_polizas_ok := l_num_polizas_ok + 1;

              PQ_Utl.LOG(lc, 'Poliza de R.C Ganado con con id: ' || v_id_plz_rc || ' y Ref. Poliza:' || v_ref_poliza
                          || ' actualizada a estado: ' || estado_correcto
                          || ', referencia de Omega: ' || v_refOmega || ' y producto tecnico: ' || v_prodTec, 2);



              -- Insertamos en el historico de estados de poliza de R.C
              INSERT INTO TB_RC_POLIZAS_HIST_ESTADOS (id,idpoliza_rc,codusuario,fecha,idestado)
                 values (SQ_POLIZAS_HIST_ESTADOS_RC.nextval,v_id_plz_rc,v_codusuario,v_fecha_planif,estado_correcto);
           ELSE

              -- Actualiza tambien la referencia de Omega
              UPDATE TB_RC_POLIZAS
                 SET IDESTADO = estado_erroneo,
                     REFERENCIA_OMEGA = v_refOmega
              WHERE ID = v_id_plz_rc;

              PQ_Utl.LOG(lc, 'Poliza de R.C Ganado con con id: ' || v_id_plz_rc || ' y Ref. Poliza' || v_ref_poliza
                             || 'actualizada a estado: ' || estado_erroneo , 2);

              -- Insertamos en el historico de estados de poliza de rc
              INSERT INTO TB_RC_POLIZAS_HIST_ESTADOS (id,idpoliza_rc,codusuario,fecha,idestado)
                 values (SQ_POLIZAS_HIST_ESTADOS_RC.nextval,v_id_plz_rc,v_codusuario,v_fecha_planif,estado_erroneo);
           END IF;


           l_num_polizas := l_num_polizas + 1;
       ELSE
           PQ_Utl.LOG(lc, 'Registro correspondiente a referencia de poliza ya actualizada', 2);
       END IF;

   END LOOP;

   -- Control de excepciones
   EXCEPTION
   -- Si no hay mas lineas en el fichero recibido
   WHEN no_data_found THEN

      -- Si v_tipo_exc es false indicara que la excepcion ha sido producida porque no existe ID asociada
      -- a una referencia indicada en el fichero de recepcion
      IF (v_tipo_exc = false) THEN
         -- Rollback de los updates que se hayan lanzado anteriormente
         ROLLBACK;
         -- Se escribe en el log el error
         PQ_Err.raiser(sqlcode,'No se ha encontrado ID en la tabla TB_RC_POLIZAS asociado a esa referencia.' || ' [' || SQLERRM || ']');
         UTL_FILE.FCLOSE( f_fichero );
         -- Fin de proceso
         PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(sysdate,'HH24:MI:SS'), 2);
         PQ_Utl.LOG(lc,'## FIN ##', 1);
         -- Se vuelve a lanzar la excepcion para parar la cadena
         raise;
      END IF;

      -- Si no, se ha llegado al final del fichero de recepcion
      PQ_Utl.LOG(lc, 'Se ha llegado al final del fichero.', 2);
      -- Se cierra el fichero de envio
      PQ_Utl.LOG(lc, 'Se cierra el fichero.', 2);
      UTL_FILE.FCLOSE( f_fichero );
      -- Se hace commit de las transacciones
      PQ_Utl.LOG(lc, 'Se hace commit de las transacciones.', 2);
      COMMIT;

      -- Se actualiza el registro 'REF_RC_NO_TRATADAS' para el correo
      IF (v_ref_desconocidas IS NOT NULL) THEN
        PQ_Utl.LOG(lc, 'Lista de referencias no tratadas.', 2);
        PQ_Utl.LOG(null, v_ref_desconocidas, 2);
        UPDATE o02agpe0.tb_config_agp ca
           set ca.agp_valor = SUBSTR(v_ref_desconocidas,0,2000)
           where ca.agp_nemo='REF_RC_NO_TRATADAS';
        COMMIT;
      END IF;

      -- Guardamos los resultados en el fichero de log
      PQ_Utl.LOG(lc, '');
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || TO_CHAR(sysdate,'DD/MM/YY HH24:MI:SS'), 2);
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, 'Registros procesados                  := ' || l_num_registros, 2);
      PQ_Utl.LOG(lc, 'Polizas de R.C Ganado alta en Omega   := ' || l_num_polizas_ok, 2);
      PQ_Utl.LOG(lc, 'Polizas de R.C Ganado actualizadas    := ' || l_num_polizas, 2);
      PQ_Utl.LOG(lc, 'Polizas de R.C Ganado no encontraddas := ' || l_num_ref_desc, 2);
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, '', 2);

      -- Fin de proceso
      PQ_Utl.LOG(lc,'El proceso ha finalizado correctamente a las ' || TO_CHAR(sysdate,'HH24:MI:SS'), 2);
      PQ_Utl.LOG(lc,'## FIN ##', 1);

      RETURN 'OK';

  -- Si ocurre cualquier otro error
  WHEN OTHERS THEN
      -- Rollback de los updates que se hayan lanzado anteriormente
      ROLLBACK;
      -- Se escribe en el log el error
      PQ_Err.raiser(sqlcode,'Error al generar los ficheros de recepcion_polizas_rc_ganado' || ' [' || SQLERRM || ']');
      -- Fin de proceso
      PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(sysdate,'HH24:MI:SS'), 2);
      PQ_Utl.LOG(lc,'## FIN ##', 1);
      -- Se vuelve a lanzar la excepcion para parar la cadena
      raise;

   END;
-- Fin del cuerpo de la funcion principal


---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- FUNCTION isPolizaTratada
--
-- Devuelve un boolean dependiendo de si la referencia y el tipo de envio estan ya registrados en la tabla y si no lo estan
-- los inserta.
--
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
FUNCTION isPolizaTratada (referencia in varchar2, tipoEnvio in varchar2) RETURN BOOLEAN IS

encontrada BOOLEAN := false;

BEGIN
     -- Si la tabla esta vacia, la referencia y el tipo de envio no se han podido guardar antes
     IF (t_est_plz.LAST IS NULL) THEN
        -- Se insertan la referencia y el tipo de envio en la tabla y se devuelve false
        t_est_plz (1) := referencia || tipoEnvio;
        RETURN FALSE;

     -- Si la tabla tiene datos
     ELSE
         -- Recorre la tabla hasta encontrar la referencia o llegar al final
         FOR i IN t_est_plz.FIRST..t_est_plz.LAST
         LOOP
             IF (t_est_plz(i) = (referencia || tipoEnvio)) THEN
             -- La referencia ya estaba en la tabla
                encontrada := true;
                EXIT;
             END IF;
         END LOOP;

     END IF;

     -- Si no se ha encontrado, se inserta la referencia en la tabla
     IF (encontrada = false) THEN
        t_est_plz (t_est_plz.LAST) := (referencia || tipoEnvio);
     END IF;

     RETURN encontrada;
END;
-- Fin de declaracion de la funcion

---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- FUNCTION getIdPolizaRc
--
-- Devuelve el id de la poliza de RC Ganado asociado a la referencia
-- Si no encuentra ningun registro devolvera NULL
-- MODIF TAM (15.02.2018) Por una incidencia se ha modificado la referencia de envio y de Recepcion de la poliza
-- ya que se enviaba el idpoliza y lo que necesitan es la referencia de la poliza y el digito de Control
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
FUNCTION getIdPolizaRc (v_ref_poliza   in varchar2, v_dc in varchar2) RETURN NUMBER IS
-- Inicio de declaracion de funcion

v_id_plz_rc_aux       NUMBER(15) := NULL;

--- MODIF TAM (15.02.2018) ** Inicio --
BEGIN

-- Se obtiene el ID de la tabla TB_RC_POLIZAS correspondiente a la referencia, tipo de envio
-- y estado 'Enviada pendiente de aceptacion' o 'Enviada erronea'
-- Si no hay ID relacionada, se lanzara la excepcion 'no_data_found' y se devolvera NULL


-- Se obtiene el ID de la tabla TB_RC_POLIZAS correspondiente a la referencia, tipo de envio
-- y estado 'Enviada pendiente de aceptacion' o 'Enviada erronea'
-- Si no hay ID relacionada, se lanzara la excepcion 'no_data_found' y se devolvera NULL
SELECT RC.ID
  into v_id_plz_rc_aux
  FROM TB_POLIZAS P, TB_RC_POLIZAS RC
WHERE P.REFERENCIA = v_ref_poliza
  AND P.Dc = v_dc
  AND P.IDPOLIZA = RC.IDPOLIZA
  AND RC.IDESTADO in (3,4);

RETURN v_id_plz_rc_aux;

-- Control de excepciones
EXCEPTION
-- Si no hay poliza de R.C Ganado asociada a los datos indicados
WHEN no_data_found THEN

  -- Se escribe en el log el error
  PQ_Utl.LOG (lc,'No se ha encontrado ID en la tabla TB_RC_POLIZAS asociado a esa referencia.' || ' [' || SQLERRM || ']');
  RETURN NULL;

END;
-- Fin de declaracion de la funcion

END PQ_RECEPCION_POLIZAS_RC;
/
SHOW ERRORS;