SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.PQ_RECEPCION_POLIZAS_SBP IS
/******************************************************************************
   NAME:       	PQ_RECEPCION_POLIZAS_SBP
   PURPOSE:    	Se procesa el fichero recibido de OMEGA y se actualizan los datos
				de las polizas de sobreprecio dependiendo del codigo recibido.

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        09/03/2012  Moises Perez    1. Created this package.

*******************************************************************************/

-------------------------------------------------------------------------------
-- PROCEDIMIENTOS PUBLICOS
-------------------------------------------------------------------------------

-- Carga las polizas de sobreprecio
FUNCTION recepcion_polizas_sobreprecio(p_fecha IN varchar2) RETURN VARCHAR2;

-- Comprueba si la poliza ya ha sido tratada anteriormente
FUNCTION isPolizaTratada (referencia   in varchar2, tipoEnvio in varchar2) RETURN BOOLEAN;

-- Devuelve el id de la poliza de sobreprecio asociado a la referencia y tipo de envio pasado como parametro
FUNCTION getIdPolizaSbp (v_ref_poliza   in varchar2, v_tipo_envio_num in number) RETURN NUMBER;

END PQ_RECEPCION_POLIZAS_SBP;

END PQ_RECEPCION_POLIZAS_SBP;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_RECEPCION_POLIZAS_SBP AS

---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------
-- FUNCTION recepcion_polizas_sobreprecio
--
-- Fichero de Polizas de Sobreprecio
--
-- Lee el fichero de respuesta enviado por OMEGA y actuliza las polizas de sobreprecio dependiendo del codigo devuelto
--
--
-----------------------------------------------------------------------------------------------------------------------
----- | NOMBRE						  | TIPO				| CAMPO
----- |-------------------------------|---------------------|----------------------------------------------------------
-- 01 | N? de poliza de Agroweb		  | AF(8)				| TB_SBP_POLIZAS.REFERENCIA || TB_REFERENCIAS_AGRICOLAS
-- 02 | Fecha de efecto				  | N(8)				| TO_CHAR(SYSDATE, ''DDMMYYYY'')
-- 03 | Fecha de vencimiento		  | N(8)				| ???
-- 04 | Cuenta de domiciliacion		  | N(20)				| TB_DATOS_ASEGURADOS.CCC
-- 05 | Codigo de la entidad		  | N(4)				| TB_COLECTIVOS.CODENTIDAD
-- 06 | Codigo de la oficina		  | N(4)				| TB_POLIZAS.OFICINA
-- 07 | NIF del asegurado		      | AF(9)				| TB_ASEGURADOS.NIFCIF (a?adir 0 por la izquierda si no llega a longitud 9)
-- 08 | Nombre y apellidos			  | AF(100)				| TB_ASEGURADOS.NOMBRE, APELLIDO1, APELLIDO2 (a?adir espacios en blanco por la derecha hasta llegar a 100)
-- 09 | Colectivo					  | N(8)				| TB_COLECTIVOS.IDCOLECTIVO (en BD tama?o 7, a?adir 0 a la derecha o izquierda????)
-- 10 | Linea						  | N(3)				| TB_LINEAS.CODLINEA
-- 11 | Plan						  | N(4)				| TB_LINEAS.CODPLAN
-- 12 | Cultivo						  | N(3)				| TB_SBP_PARCELAS.CODCULTIVO
-- 13 | Nombre del cultivo			  | A(25)				| TB_SC_C_CULTIVOS
-- 14 | Total produccion por cultivo  | N(10)				| TB_SBP_PARCELAS.TOTAL_PRODUCCION
-- 15 | Sobreprecio					  | N(5,4)				| TB_SBP_SOBREPRECIO.SOBREPRECIO
-- 16 | Suma asegurada por cultivo    | N(10,2)				| Total de produccion POR CULTIVO * Sobreprecio
-- 17 | Tasa de garantia de pedrisco  | N(3,2)				| TB_SBP_PARCELAS.TASA_PEDRISCO
-- 18 | Prima de garantia de pedrisco | N(10,2)				| TB_SBP_PARCELAS.PRIMA_NETA_PEDRISCO
-- 19 | Tasa de garantia de incendio  | N(3,2)				| TB_SBP_PARCELAS.TASA_INCENDIO
-- 20 | Prima de garantia de incendio | N(10,2)				| TB_SBP_PARCELAS.PRIMA_NETA_INCENDIO
-- 21 | Codigo de error               |AF(8)				  | TB_SBP_ERRORES_POLIZA.IDERROR
-- 22 | Descripcion del error         |AF(110)				| TB_SBP_ERRORES_POLIZA.DESC_ERROR
-----------------------------------------------------------------------------------------------------------------------
-- |
---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------



-- Declaracion de la tabla de estados de poliza
-- Para almacenar la referencia (GXXXXXX) + tipo de envio (A o S)
TYPE TABLA_ESTADO_POLIZA IS TABLE OF VARCHAR(8) INDEX BY BINARY_INTEGER;
t_est_plz         TABLA_ESTADO_POLIZA;        -- Tabla que contiene el estado de cada poliza procesada
lc   VARCHAR2(54) := 'pq_recepcion_polizas_sbp.recepcion_polizas_sobreprecio'; -- Variable que almacena el nombre del paquete y de la funcion





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





-- Inicio de declaracion de funcion
FUNCTION recepcion_polizas_sobreprecio(p_fecha IN varchar2) RETURN VARCHAR2 IS

-- Variables generales
estado_correcto   VARCHAR2(1) := '5';         -- Codigo de estado correspondiente a 'Enviada correcta'
estado_erroneo    VARCHAR2(1) := '4';         -- Codigo de estado correspondiente a 'Enviada erronea'
nom_var_config    VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguracion de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
l_dir         		TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
l_dir_name    		VARCHAR2(1000);					-- Ruta fisica donde se ubican los ficheros que se reciben
nom_var_config_copia    VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguracion de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
l_dir_copia    		TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan las copias de los ficheros recibidos
l_dir_name_copia	VARCHAR2(1000);					-- Ruta fisica donde se ubican las copias de los ficheros que se reciben
l_nombre      		VARCHAR2(8) := 'P_SB_REC' ;-- Nombre del fichero de recepcion
f_fichero    		  UTL_FILE.FILE_TYPE;     -- Variable que almacena la referencia al fichero de envio
l_num_polizas     NUMBER := 0;            -- Contador de polizas actualizadas
l_num_registros   NUMBER := 0;            -- Contador de registros procesados
l_line        		VARCHAR2(1000);         -- Variables que almacena la linea que se leera del fichero
v_ref_poliza  		VARCHAR2(7);            -- Variable auxiliar para almacenar la referencia de la poliza que se procesa
v_cod_error  		  VARCHAR2(6);            -- Variable auxiliar para almacenar el codigo de error recibido
v_id_plz_sb       NUMBER(15);             -- Variable auxiliar para almacenar el ID del registro correspondiente a la poliza de sobreprecio
v_tipo_exc        BOOLEAN := false;       -- Variable que indica que ha producido la excepcion generica 'no_data_found'
cod_error_ok  		VARCHAR2(6) := 'PRP556';            -- Codigo de error correspondiente a 'Poliza recibida correctamente'
v_aux_error       NUMBER := 0;            -- Variable que almacena el n? de registros que hay en TB_SBP_POLIZAS_ERRORES para una poliza
v_tipo_envio      VARCHAR2(1);            -- Almacena el tipo de envio de la poliza (principal o suplemento)
v_tipo_envio_num  NUMBER := 0;            -- Almacena el tipo de envio de la poliza (principal o suplemento) en formato numerico
v_ref_desconocidas VARCHAR2(4000) := '';  -- Almacena las referencia de poliza que no se encuentran en las tablas

-- MPM[2014]
v_prodTec TB_SBP_POLIZAS.PROD_TECNICO%TYPE;
v_refOmega TB_SBP_POLIZAS.REF_PLZ_OMEGA%TYPE;

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
   SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir;

   -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositara la copia del fichero recibido de OMEGA
   PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositara la copia del fichero recibido.', 2);
   l_dir_copia  := PQ_Utl.getcfg(nom_var_config_copia);
   -- Se guarda el path fisico del directorio
   PQ_Utl.LOG(lc, 'Obtiene la ruta fisica del DIRECTORY de copia.', 2);
   SELECT DIRECTORY_PATH into l_dir_name_copia FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir_copia;

   -- Guarda una copia del fichero recibido en la ruta indicada
   PQ_Utl.LOG(lc, 'Se copia el fichero recibido en :' || l_dir_name_copia || ' con nombre: ' || l_nombre || to_char(v_fecha_planif,'YYMMDDHH24MISS') || '.');
   UTL_FILE.fcopy(l_dir, l_nombre || '.TXT', l_dir_copia, l_nombre  || to_char(v_fecha_planif,'YYMMDDHH24MISS') || '.TXT');


   -- Se abre el fichero de texto de salida en la ruta indicada
   PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.', 2);
   f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.TXT',	open_mode    => 'r');

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

       -- Obtiene de la linea el numero de referencia de la poliza, el codigo de error enviado y el tipo de envio
       v_ref_poliza := SUBSTR (l_line, 0, 7);
       v_tipo_envio := SUBSTR (l_line, 270, 1);
       -- MPM[2014] - INI
       -- Se modifica el formato de recepcion para incluir el producto tecnico y la referencia de la poliza
       -- en Omega antes del codigo y descripcion del error
       v_cod_error := SUBSTR (l_line, 284, 6);
       v_prodTec := getValNumerico (l_line, 271, 4);
       v_refOmega := getValNumerico (l_line, 275, 9);
       -- MPM[2014] - FIN


       -- Obtiene el valor numerico del tipo de envio
       IF (v_tipo_envio = 'A') THEN
          v_tipo_envio_num := 1;
       ELSE
          v_tipo_envio_num := 2;
       END IF;

       -- Escribe en el log los datos del registro recibido
       PQ_Utl.LOG(lc, 'Poliza de sobreprecio recibida con datos: ', 2);
       PQ_Utl.LOG(lc, '- Referencia Agroplus: ' || v_ref_poliza, 2);
       PQ_Utl.LOG(lc, '- Tipo de envio: ' || v_tipo_envio, 2);
       PQ_Utl.LOG(lc, '- Codigo: ' || v_cod_error, 2);
       PQ_Utl.LOG(lc, '- Producto tecnico: ' || v_prodTec, 2);
       PQ_Utl.LOG(lc, '- Referencia Omega: ' || v_refOmega, 2);

       -- Si la referencia de la poliza no consta como actualizada,
       -- se inserta en la tabla TB_SBP_POLIZAS_ERRORES el registro que asocia el ID de la poliza de
       -- sobreprecio con el codigo de error
       IF (isPolizaTratada (referencia => v_ref_poliza, tipoEnvio => v_tipo_envio) = false) THEN
           -- Se obtiene el ID de la tabla TB_SBP_POLIZAS correspondiente a la referencia, tipo de envio
           -- y estado 'Enviada pendiente de aceptacion' o 'Enviada erronea'
           -- Si no hay ID relacionada, se lanzara la excepcion 'no_data_found' y el flag
           -- v_tipo_exc a false indicara que la excepcion la ha producido este metodo

           v_id_plz_sb := getIdPolizaSbp (v_ref_poliza => v_ref_poliza, v_tipo_envio_num => v_tipo_envio_num);
           -- Si el id es NULL se a?ade la referencia a la variable y se va a la siguiente iteracion
           IF (v_id_plz_sb) IS NULL THEN
              IF (v_ref_desconocidas IS NULL) THEN
                 v_ref_desconocidas := v_ref_poliza;
              ELSE
                 v_ref_desconocidas := v_ref_desconocidas || ',' || v_ref_poliza;
              END IF;

              CONTINUE;
           END IF;


           -- Comprueba si ya existe registro de codigo de error para ese id de poliza
           -- para la actualizacion en la tabla TB_SBP_POLIZAS_ERRORES
           SELECT COUNT(*) into v_aux_error FROM TB_SBP_POLIZAS_ERRORES WHERE ID = v_id_plz_sb;
           -- Si ya hay registro se hace el UPDATE
           IF (v_aux_error > 0) THEN
              UPDATE TB_SBP_POLIZAS_ERRORES SET IDERROR = v_cod_error WHERE ID = v_id_plz_sb;
              PQ_Utl.LOG(lc, 'Registro de error de la poliza de sobreprecio con referencia: '
                         || v_ref_poliza || ' y tipo de envio: ' || v_tipo_envio_num || ' actualizado a: ' || v_cod_error
                         || ', referencia de Omega: ' || v_refOmega || ' y producto tecnico: ' || v_prodTec  , 2);
           -- Si no hay registro se hace el INSERT
           ELSE
               INSERT INTO TB_SBP_POLIZAS_ERRORES VALUES (v_id_plz_sb, v_cod_error);
               PQ_Utl.LOG(lc, 'Registro de error de la poliza de sobreprecio con referencia: '
                         || v_ref_poliza || ' y tipo de envio: ' || v_tipo_envio_num ||' insertado a: ' || v_cod_error , 2);
           END IF;

           -- Se actualiza el estado de la poliza en la tabla TB_SBP_POLIZAS dependiendo del codigo de error obtenido
           IF (v_cod_error = cod_error_ok) THEN
              -- MPM[2014] - INI
              -- Actualiza tambien la referencia de Omega y el producto tecnico
              UPDATE TB_SBP_POLIZAS S
              SET IDESTADO = estado_correcto,
              PROD_TECNICO = v_prodTec,
              REF_PLZ_OMEGA = v_refOmega
              WHERE ID = v_id_plz_sb;
              -- MPM[2014] - FIN
              PQ_Utl.LOG(lc, 'Poliza de sobreprecio con referencia: ' || v_ref_poliza || ' y tipo de envio: ' || v_tipo_envio_num || ' actualizada a estado: ' || estado_correcto
                          || ', referencia de Omega: ' || v_refOmega || ' y producto tecnico: ' || v_prodTec, 2);

              -- Insertamos en el historico de estados de poliza de sbp
              INSERT INTO TB_SBP_HISTORICO_ESTADOS (id,idpoliza_sbp,codusuario,fecha,estado)
                 values (SQ_SBP_HISTORICO_ESTADOS.nextval,v_id_plz_sb,null,v_fecha_planif,estado_correcto);
           ELSE
              -- MPM[2014] - INI
              -- Actualiza tambien la referencia de Omega y el producto tecnico
              UPDATE TB_SBP_POLIZAS
              SET IDESTADO = estado_erroneo,
              PROD_TECNICO = v_prodTec,
              REF_PLZ_OMEGA = v_refOmega
              WHERE ID = v_id_plz_sb;
              -- MPM[2014] - FIN
              PQ_Utl.LOG(lc, 'Poliza de sobreprecio con referencia: ' || v_ref_poliza || ' y tipo de envio: ' || v_tipo_envio_num || ' actualizada a estado: ' || estado_erroneo , 2);

              -- Insertamos en el historico de estados de poliza de sbp
              INSERT INTO TB_SBP_HISTORICO_ESTADOS (id,idpoliza_sbp,codusuario,fecha,estado)
                 values (SQ_SBP_HISTORICO_ESTADOS.nextval,v_id_plz_sb,null,v_fecha_planif,estado_erroneo);
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
         PQ_Err.raiser(sqlcode,'No se ha encontrado ID en la tabla TB_SBP_POLIZAS asociado a esa referencia.' || ' [' || SQLERRM || ']');
         UTL_FILE.FCLOSE( f_fichero );
         -- Fin de proceso
         PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(v_fecha_planif,'HH24:MI:SS'), 2);
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

      -- Se actualiza el registro 'REF_SBP_NO_TRATADAS' para el correo
      IF (v_ref_desconocidas IS NULL) THEN
		o02agpe0.PQ_UTL.setCfg('REF_SBP_NO_TRATADAS' || p_fecha, ' ');
	  ELSE
        PQ_Utl.LOG(lc, 'Lista de referencias no tratadas.', 2);
        PQ_Utl.LOG(null, v_ref_desconocidas, 2);
        o02agpe0.PQ_UTL.setCfg('REF_SBP_NO_TRATADAS' || p_fecha, SUBSTR(v_ref_desconocidas,0,2000));
      END IF;
	  COMMIT;

      -- Guardamos los resultados en el fichero de log

      PQ_Utl.LOG(lc, '');
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || TO_CHAR(v_fecha_planif,'DD/MM/YY HH24:MI:SS'), 2);
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, 'Registros procesados   := ' || l_num_registros, 2);
      PQ_Utl.LOG(lc, 'Polizas de sobreprecio actualizadas   := ' || l_num_polizas, 2);
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
      PQ_Err.raiser(sqlcode,'Error al generar los ficheros de recepcion_polizas_sobreprecio' || ' [' || SQLERRM || ']');
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
--
-- Inicio de declaracion de funcion
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
-- FUNCTION getIdPolizaSbp
--
-- Devuelve el id de la poliza de sobreprecio asociado a la referencia y tipo de envio pasado como parametro.
-- Si no encuentra ningun registro devolvera NULL
--
---------------------------------------------------------------------------------------------------------------------------
--
-- Inicio de declaracion de funcion
FUNCTION getIdPolizaSbp (v_ref_poliza   in varchar2, v_tipo_envio_num in number) RETURN NUMBER IS

v_id_plz_sbp_aux       NUMBER(15) := NULL;

BEGIN

-- Se obtiene el ID de la tabla TB_SBP_POLIZAS correspondiente a la referencia, tipo de envio
-- y estado 'Enviada pendiente de aceptacion' o 'Enviada erronea'
-- Si no hay ID relacionada, se lanzara la excepcion 'no_data_found' y se devolvera NULL
SELECT SP.ID into v_id_plz_sbp_aux FROM TB_POLIZAS P, TB_SBP_POLIZAS SP WHERE P.REFERENCIA = v_ref_poliza
AND P.IDPOLIZA=SP.IDPOLIZA AND SP.TIPOENVIO = v_tipo_envio_num and sp.idestado in (3,4);

RETURN v_id_plz_sbp_aux;

-- Control de excepciones
EXCEPTION
-- Si no hay poliza de sbp asociada a los datos indicados
WHEN no_data_found THEN
-- Se escribe en el log el error
PQ_Utl.LOG (lc,'No se ha encontrado ID en la tabla TB_SBP_POLIZAS asociado a esa referencia.' || ' [' || SQLERRM || ']');
RETURN NULL;

END;
-- Fin de declaracion de la funcion

END PQ_RECEPCION_POLIZAS_SBP;
/

SHOW ERRORS;