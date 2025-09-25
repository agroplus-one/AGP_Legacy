SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_DESCONEXION_EUROCAJA is

  /* Declaraciones*/
  TYPE t_usuario_ent IS RECORD(
		 codusuario VARCHAR(10)
		,codentidad NUMBER);

	TYPE t_array_usuario_ent IS TABLE OF t_usuario_ent INDEX BY BINARY_INTEGER;
  TYPE t_array IS TABLE OF VARCHAR2(30000) INDEX BY BINARY_INTEGER;

  /* Functions*/
  FUNCTION desconexion_eurocaja RETURN VARCHAR2;
  FUNCTION fn_split (cadena IN VARCHAR2, separador IN CHAR) RETURN t_array;


end PQ_DESCONEXION_EUROCAJA;
/
create or replace package body o02agpe0.PQ_DESCONEXION_EUROCAJA is
  -- *******************************************************************
  -- ** Author  : U028975 (T-Systems)                                 **
  -- ** Created : 07.04.2021                                          **
  -- ** Lectura de un fichero depositado por rga, del cual se leeran  **
  -- ** los registros para realizar el updateo o alta de los usuarios.**
  -- *******************************************************************

  -- Array para almacenar los registros de la tabla TB_PAGO_ESTADOS_PAGO


 FUNCTION desconexion_eurocaja RETURN VARCHAR2 IS

       l_dir         		     TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
       l_dir_name    		     VARCHAR2(1000);				      	-- Ruta física donde se ubican los ficheros que se reciben

       l_dir_copia    	     TB_CONFIG_AGP.AGP_valor%TYPE; 	        -- Valor del parametro de configuracion para el DIRECTORY donde se dejan las copias de los ficheros recibidos
       l_dir_name_copia	     VARCHAR2(1000);					              -- Ruta física donde se ubican las copias de los ficheros que se reciben
       f_fichero    		     UTL_FILE.FILE_TYPE;                    -- Variable que almacena la referencia al fichero de envío
       f_fich_error 		     UTL_FILE.FILE_TYPE;                    -- Variable que almacena la referencia al fichero de error
       lc                    VARCHAR2(50) := 'PQ_DESCONEXION_EUROCAJA.desconexion_eurocaja'; -- Variable que almacena el nombre del paquete y de la función
       l_max_linefilesize_aux   NUMBER := 32000;

       nom_var_config        VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       nom_var_config_copia  VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       nom_var_config_desa	 VARCHAR2 (30) := 'DIR_IN_AGP';        -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY en DESARROLLO

       l_nombre_fichero      VARCHAR2(30):= 'FIC_DESCONEXION';
       l_nombre              VARCHAR(50):='';

       l_line        		     VARCHAR2(2500);       -- Variables que almacena la linea que se leerá del fichero
       l_line00      		     VARCHAR2(1000);       -- Variables que almacena la linea del fichero de errores.
       v_valor               VARCHAR2(2500);

       l_num_registros       NUMBER := 0;          -- contador de registros totales leidos
       l_num_usu_modif       NUMBER := 0;          -- Contador de usuarios modificados
       l_num_usu_alta        NUMBER := 0;          -- Contador de usuarios dados de alta
       l_num_usu_baja        NUMBER := 0;          -- Contador de usuarios dados de baja
       l_num_usu_error       NUMBER := 0;          -- Contador de usuarios con errores

       v_cod_usuario         VARCHAR2(10) := '';
       v_nombre              VARCHAR2(60) := '';
       v_nombre_fich         VARCHAR2(60) := '';
       v_apellido1           VARCHAR2(60) := '';
       l_caja                NUMBER  := 0;
       v_nombre_caja         VARCHAR2(50) := '';
       v_perfil              VARCHAR2(2)  := '';
       v_email               VARCHAR2(60) := '';
       l_oficina             NUMBER :=0;
       v_identificador       VARCHAR2(30) := '';
       v_nif                 VARCHAR2(14)  :='';

       l_num_usu             NUMBER :=0;
       l_num_caja            NUMBER :=0;
       l_num_oficina         NUMBER :=0;
       l_externo             NUMBER :=0;
       l_tipousu             NUMBER :=0;
       l_ent_med             NUMBER := 0;
       l_sub_ent_med         NUMBER := 0;
       l_carga_pac           NUMBER := 0;
       l_financiar           NUMBER := 0;
       v_nombre_usu          VARCHAR2(80);
       l_codentidad_fich     NUMBER := 0;
       v_codEntidad_fich     VARCHAR2(4) := '';
       v_cons_usuario        VARCHAR2(1000) := '';
       v_cons_entidades_desc VARCHAR2(500) := '';
       v_codUsuario          VARCHAR2(10):= '';
       l_codEntidad          NUMBER := 0;
       l_ind_usu             NUMBER := 1   ;

       valoresUsuario        t_array;

    	 TYPE cur_typ IS REF CURSOR;
 	     v_cursor1    cur_typ;
 	     v_cursor2    cur_typ;
       l_ind       NUMBER := 0;

       l_array_usu_fich    t_array_usuario_ent;

       v_encontrado         BOOLEAN := false;

       existeElArchivo  BOOLEAN;
       longitudEnBytes  NUMBER;
       numeroDeBloques  NUMBER;

  BEGIN

     -- Comienzo de escritura en log
     PQ_Utl.LOG(lc,'## [INI] DESCONEXION DE EUROCAJA RURAL ##',1);

     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositan el fichero recibidos.   ', 2);

     -- Desarolo
     --l_dir  := PQ_Utl.getcfg(nom_var_config_desa);
     --l_dir_copia  := PQ_Utl.getcfg(nom_var_config_desa);

     -- TEST y PROD:
     l_dir  := PQ_Utl.getcfg(nom_var_config);
     l_dir_copia  := PQ_Utl.getcfg(nom_var_config_copia);


     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME = l_dir;

     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido.   ', 2);

     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name_copia FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir_copia;

     /*************************************************************************/
     /* OBTENEMOS LA LISTA DE CAJAS QUE HAY QUE DESCONECTAR */
      BEGIN

        v_cons_entidades_desc := 'select desx.codentidad, desx.codentidad_med, desx.codsubentidad_med';
        v_cons_entidades_desc := v_cons_entidades_desc || ' from o02agpe0.tb_desconexion_entidad desx';

        v_codUsuario := '';
        l_codEntidad := 0;

        /* Abrimos un cursor para obtener las cajas a desconectar*/
        OPEN v_cursor1
         FOR v_cons_entidades_desc;

           LOOP

         	     FETCH v_cursor1
                INTO l_codentidad_fich,
                     l_ent_med,
                     l_sub_ent_med;

          EXIT WHEN v_cursor1%NOTFOUND;


              IF (l_codentidad_fich = '') THEN
                PQ_Utl.LOG(lc, 'No se han recuperado mas cajas para Desconectar: ' );
                RETURN 'OK';
              ELSE
                PQ_Utl.LOG(lc, '**** SE PROCEDE A DESCONECTAR LA CAJA : '|| l_codentidad_fich || '****' );
              END IF;

              /* Inicializamos de nuevo las variables */
              l_num_registros := 0;
              l_num_usu_modif := 0;
              l_num_usu_alta  := 0;
              l_num_usu_baja  := 0;
              l_num_usu_error := 0;

              l_nombre :=PQ_Utl.getcfg(l_nombre_fichero);
              v_codEntidad_fich := TO_CHAR (l_codentidad_fich);

              /* MODIF 14.04.2021*/
              /*v_nombre_fich := TRIM(l_nombre || '_' || v_codEntidad_fich) ;*/
              v_nombre_fich := TRIM(l_nombre || to_char(sysdate,'_YYYYMMDD') || '_' || v_codEntidad_fich) ;

              /* Antes de abrir el fichero verifico si existe */
              UTL_FILE.FGETATTR( l_dir, v_nombre_fich || '.txt', existeElArchivo, longitudEnBytes, numeroDeBloques);

              IF existeElArchivo THEN
                 PQ_Utl.LOG(lc, 'El fichero '|| v_nombre_fich || '.txt' ||' existe, continuamos.' );
              ELSE
                 PQ_Utl.LOG(lc, 'El fichero '|| v_nombre_fich || '.txt' ||' NO existe en la ruta: ' || l_dir );
                /* continuamos con el bucle de entidades */
                CONTINUE;
              END IF;

              -- Se abre el fichero de texto de salida en la ruta indicada
              PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.  ', 2);
              f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => v_nombre_fich || '.txt',	open_mode    => 'r');

              f_fich_error := utl_file.fopen(location => l_dir, filename => v_nombre_fich || '_ERR' || '.txt', open_mode => 'wb', max_linesize =>l_max_linefilesize_aux);

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

                   valoresUsuario :=  fn_split (v_valor, chr(9));

                   /* Obtenemos lso campos de la línea del fichero leída*/
                   v_cod_usuario     := TRIM(valoresUsuario(1));
                   v_nombre          := TRIM(valoresUsuario(2));
                   v_apellido1       := TRIM(valoresUsuario(3));
                   l_caja            := TO_NUMBER (TRIM(valoresUsuario(4)));
                   v_nombre_caja     := TRIM(valoresUsuario(5));
                   v_perfil          := TRIM(valoresUsuario(6));
                   v_email           := TRIM(valoresUsuario(7));
                   l_oficina         := TO_NUMBER (TRIM(valoresUsuario(8)));
                   v_identificador   := TRIM(valoresUsuario(9));
                   v_nif             := TRIM(valoresUsuario(10));


                   /* 1º Validamos que el valor de la caja de la línea leída, exista en nuestra BBDD */
                   IF (l_caja > 0) THEN
                      execute immediate 'select count(*) from o02agpe0.tb_entidades ent
                                         where ent.codentidad = ' || l_caja || ' ' INTO  l_num_caja;
                     IF (l_num_caja = 0) THEN
                       /* Si la caja no existe insertamos registro en el fichero de errores y pasamos al siguiente registro */
                       l_line00 := v_cod_usuario || chr(9)
                                   || 'ERROR, CAJA INEXISTENTE: ' || l_caja || '.';
                       utl_file.put_raw(f_fich_error, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));

                       l_num_usu_error := l_num_usu_error + 1;
                       PQ_Utl.LOG(lc, 'Valor de entidad incorrecta, leemos siguiente registro', 2);
                       CONTINUE;
                     END IF;

                   END IF;

                   /* 2º Validamos que el valor de la oficina exista en nuestra BBDD */
                   IF (l_oficina > 0) THEN
                      execute immediate 'select count(*) from o02agpe0.tb_oficinas ofi
                                        where ofi.codentidad = ' || l_caja || ' and ofi.codoficina = ' || l_oficina || ' ' INTO  l_num_oficina;
                     IF (l_num_oficina = 0) THEN
                       /* Si la oficina no existe insertamos registro en el fichero de errores y pasamos al siguiente registro */
                       l_line00 := v_cod_usuario || chr(9)
                                   || 'ERROR, OFICINA: ' || l_oficina || ' DE LA CAJA: ' || l_caja || ' INEXISTENTE.';
                       utl_file.put_raw(f_fich_error, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));

                       l_num_usu_error := l_num_usu_error + 1;
                       PQ_Utl.LOG(lc, 'Valor de oficina incorrecta, leemos siguiente registro', 2);
                       CONTINUE;
                     END IF;

                   END IF;

                   /*PQ_Utl.LOG(lc, 'Leemos registro del usuario - v_cod_usuario:' || v_cod_usuario);*/

                   IF  (v_cod_usuario IS NOT NULL) THEN

                   /* Guardamos la Entidad del colaborador leido */
                      l_ind := l_ind + 1;
                      l_array_usu_fich(l_ind).codusuario := v_cod_usuario;
                      l_array_usu_fich(l_ind).codentidad := l_caja;

                      v_nombre_usu := TRIM(SUBSTR (v_nombre, 1, 25)) || ' ' ||  TRIM(SUBSTR (v_apellido1, 1, 30));

                      IF (v_identificador = 'SERVICIOS CENTRALES') THEN
                         l_tipousu := '1'; --(Perfil 1)
                      ELSE
                         l_tipousu := '3'; --(Perfil 1)
                      END IF;

                      /* 1º: Comprobamos si existe el usuario en nuestra BBDD */

                      execute immediate 'select count(*) from o02agpe0.tb_usuarios usu
                                        where usu.codusuario = ''' || v_cod_usuario || ''' ' into l_num_usu;

                     IF (l_num_usu > 0) THEN
                        /*PQ_Utl.LOG(lc, 'Usuario ' || v_cod_usuario || ' Modificado' );*/
                        /* Si el usuario existe lo modificadmos con los datos del fichero */
                        BEGIN
                            UPDATE o02agpe0.tb_usuarios usu
                               SET usu.nombreusu = v_nombre_usu,
                                   usu.email = v_email,
                                   usu.tipousuario = l_tipousu,
                                   usu.externo = l_externo,
                                   usu.codoficina = l_oficina,
                                   usu.delegacion = l_oficina,
                                   usu.fechabaja = null
                               where usu.codusuario = v_cod_usuario ;

                        EXCEPTION
        					        WHEN OTHERS THEN
                            PQ_Utl.LOG(lc, 'Error al updatear el usuario:' || v_cod_usuario || ', con error:' || '. ' || sqlcode || ' [' || SQLERRM || ']' ,2);
                            l_line00 := v_cod_usuario || chr(9)
                                   || 'ERROR AL REALIZAR EL UPDATE DEL USUARIO';
                            utl_file.put_raw(f_fich_error, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));
                            l_num_usu_error := l_num_usu_error + 1;
                            /* Aunque de error continuamos con el siguiente registro */
        						        CONTINUE;
        				        END;
                        l_num_usu_modif := l_num_usu_modif + 1;
                     ELSE
                        /*PQ_Utl.LOG(lc, 'Usuario ' || v_cod_usuario || ' dado de Alta' );*/
                        /* Si el usuario no existe damos de alta un nuevo usuario */
                        BEGIN
                           INSERT into o02agpe0.tb_usuarios
                              VALUES( v_cod_usuario, l_caja,
                                      null, l_oficina,
                                      l_tipousu, v_nombre_usu,
                                      null, null,
                                      null, null,
                                      v_email, l_ent_med,
                                      l_sub_ent_med, l_oficina,
                                      l_externo, l_carga_pac,
                                      l_financiar, null,
                                      null, null);
                        EXCEPTION
        					        WHEN OTHERS THEN
                            PQ_Utl.LOG(lc, 'Error al Insertar el usuario:' || v_cod_usuario || ', con error:' || '. ' || sqlcode || ' [' || SQLERRM || ']' ,2);
                            l_line00 := v_cod_usuario || chr(9)
                                   || 'ERROR AL REALIZAR EL INSERT DEL USUARIO';
                            utl_file.put_raw(f_fich_error, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));
                            l_num_usu_error := l_num_usu_error + 1;

        						        /* Aunque de error continuamos con el siguiente registro */
        						        CONTINUE;
        				        END;
                        l_num_usu_alta := l_num_usu_alta + 1;
                     END IF;

                  ELSE
                     PQ_Utl.LOG(lc, 'Codigo de Usuario vacio, en línea '||l_num_registros, 2);
                  END IF;

                EXCEPTION
                   WHEN NO_DATA_FOUND THEN
                      PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DEL FICHERO ' || v_nombre_fich || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
                END;

             END LOOP;

             COMMIT;


             /*******************************************************************************/
             /* 2º: Establecemos la fecha de baja con la del día de ejecución , para aquellos
                    usuarios de BBDD de la entidad correspondiente que no hayan
                    sido tratados en el fichero recibido */

             BEGIN
                PQ_Utl.LOG(lc, '** Procedemos a dar de baja los usuarios de la entidad que no vengan informados en el fichero', 2);

                v_cons_usuario := 'select usua.codusuario, usua.codentidad from o02agpe0.tb_usuarios usua ';
                v_cons_usuario := v_cons_usuario || ' where usua.fechabaja is null';
                v_cons_usuario := v_cons_usuario || '  and usua.codentidad = ' || l_codentidad_fich ;

                v_codUsuario := '';
                l_codEntidad := 0;

                /* Abrimos un cursor para obtener las polizas del CIF colaborador */
                OPEN v_cursor2
                 FOR v_cons_usuario;

                   LOOP
                       v_encontrado := false;
                 	     FETCH v_cursor2
                        INTO v_codUsuario,
                             l_codEntidad;

                       BEGIN
                          WHILE (l_array_usu_fich(l_ind_usu).codusuario) IS NOT NULL LOOP
                             IF ((v_codUsuario = l_array_usu_fich(l_ind_usu).codusuario) AND (l_codEntidad = l_array_usu_fich(l_ind_usu).codentidad)) THEN
                                v_encontrado := true;
                             END IF;

                             l_ind_usu := l_ind_usu + 1;

                          END LOOP;
                       EXCEPTION
                          WHEN NO_DATA_FOUND THEN
                             l_ind_usu := 1;
                       END;

                       /* Si no se ha encotrado el registro, lo damos de baja */
                       IF (v_encontrado = false) THEN

                          update o02agpe0.tb_usuarios usu
                             set usu.fechabaja = sysdate
                           where usu.codusuario = v_codUsuario
                             and usu.codentidad = l_codEntidad;

                            l_num_usu_baja :=  l_num_usu_baja + 1;

                          /*PQ_Utl.LOG(lc, 'Realizamos la baja lógica del Usuario : ' || v_codUsuario || ' y Entidad: ' || l_codEntidad, 2);*/
                       END IF;

                       EXIT WHEN v_cursor2%NOTFOUND;
                   END LOOP;

                   CLOSE v_cursor2;

             EXCEPTION
                WHEN NO_DATA_FOUND THEN
                   PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DEL FICHERO ' || v_cod_usuario || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
             END;

             PQ_Utl.LOG(lc, '** Usuarios dados de baja no informados en el fichero: '|| l_num_usu_baja, 2);


           -- Guardamos los resultados en el fichero de log
           PQ_Utl.LOG(lc, '');
           PQ_Utl.LOG(lc, '*********************************************************************************', 2);
           PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || v_nombre_fich || '. FECHA ' || TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI:SS'), 2);
           PQ_Utl.LOG(lc, '*********************************************************************************', 2);
           PQ_Utl.LOG(lc, 'Registros procesados    := ' || l_num_registros, 2);
           PQ_Utl.LOG(lc, 'Usuarios Modificados    := ' || l_num_usu_modif, 2);
           PQ_Utl.LOG(lc, 'Usuarios Dados de Alta  := ' || l_num_usu_alta, 2);
           PQ_Utl.LOG(lc, 'Usuarios Dados de Baja  := ' || l_num_usu_baja, 2);
           PQ_Utl.LOG(lc, 'Usuarios con error      := ' || l_num_usu_error, 2);
           PQ_Utl.LOG(lc, '*********************************************************************************', 2);
           PQ_Utl.LOG(lc, '', 2);

           -- Fin de proceso
           PQ_Utl.LOG(lc,'El proceso ha finalizado correctamente a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
           PQ_Utl.LOG(lc,'## FIN ##', 1);

           COMMIT;

           UTL_FILE.FCLOSE( f_fichero );
           UTL_FILE.FCLOSE( f_fich_error );

           /* MODIF 14.04.2021 */
           /* Como tendremos un fichero con la fecha no hace falta renombrar
           BEGIN
            UTL_FILE.FRENAME(l_dir, v_nombre_fich || '.txt', l_dir_copia, v_nombre_fich  || to_char(sysdate,'_YYMMDDHH24MISS') || '.txt', false);
           END;*/

         EXIT WHEN v_cursor1%NOTFOUND;
         END LOOP;

         CLOSE v_cursor1;

         EXCEPTION
            WHEN NO_DATA_FOUND THEN
               PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DEL FICHERO ' || v_cod_usuario || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
         END;

     PQ_Utl.LOG(lc,'## [END] DESCONEXION DE EUROCAJA RURAL ##',1);
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
           PQ_Utl.LOG(lc,'ERROR AL PROCESAR EL FICHERO DE DESCONEXION DE EUROCAJA ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
           PQ_Err.raiser(sqlcode,'Error al generar los ficheros de desconexion' || ' [' || SQLERRM || ']');

           -- Fin de proceso
           PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
           PQ_Utl.LOG(lc,'## FIN ##', 1);
           return 'KO';

     END;

     FUNCTION fn_split (cadena IN VARCHAR2, separador IN CHAR)  RETURN t_array
      IS
         i         NUMBER          := 0;
         pos       NUMBER          := 0;
         lv_str    VARCHAR2 (3000) := cadena;
         strings   t_array;
      BEGIN
         -- determine first chuck of string
         pos := INSTR (lv_str, separador, 1, 1);

         -- while there are chunks left, loop
         IF (pos = 0)
         THEN
            strings (1) := cadena;
         ELSE
            WHILE (pos != 0)
            LOOP
               -- increment counter
               i := i + 1;
               -- create array element for chuck of string
               strings (i) := SUBSTR (lv_str, 1, pos - 1);
               -- remove chunk from string
               lv_str := SUBSTR (lv_str, pos + 1, LENGTH (lv_str));
               -- determine next chunk
               pos := INSTR (lv_str, separador, 1, 1);

               -- no last chunk, add to array
               IF pos = 0 THEN
                  strings (i + 1) := lv_str;
                  /*DBMS_OUTPUT.put_line (   'Linea '
                                        || (i + 1)
                                        || ', valor: '
                                        || strings (i + 1)
                                      );*/
               END IF;
            END LOOP;
         END IF;

      -- return array
      RETURN strings;
   END fn_split;

end PQ_DESCONEXION_EUROCAJA;
/
SHOW ERRORS;
