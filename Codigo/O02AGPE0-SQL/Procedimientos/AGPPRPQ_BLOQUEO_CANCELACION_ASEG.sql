SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_BLOQUEO_CANCELACION_ASEG is

  TYPE t_array IS TABLE OF VARCHAR2(1000) INDEX BY BINARY_INTEGER;

  TYPE t_cod_ent_subent IS RECORD(
		 entidad   NUMBER
		,subentidad NUMBER);

	TYPE t_array_cod_ent_subent IS TABLE OF t_cod_ent_subent INDEX BY BINARY_INTEGER;
  FUNCTION bloqueo RETURN VARCHAR2;
  FUNCTION fn_split (cadena IN VARCHAR2, separador IN CHAR) RETURN t_array;
  PROCEDURE carga_asegurado (nifAseg IN VARCHAR2, idAseg IN VARCHAR2, operacion IN VARCHAR2);
  PROCEDURE actualizar_bloqueo (nifAseg IN VARCHAR2, idAseg IN VARCHAR2, operacion IN VARCHAR2);
  FUNCTION comprobar_polizas_vigor (v_nifAsegurado IN VARCHAR2) RETURN BOOLEAN;
  FUNCTION comprobar_vencimiento (v_fecha_envio in DATE, v_cod_linea in VARCHAR2, v_codmodulo in VARCHAR2) RETURN BOOLEAN;
end PQ_BLOQUEO_CANCELACION_ASEG;
/
create or replace package body o02agpe0.PQ_BLOQUEO_CANCELACION_ASEG is
  -- *******************************************************************
  -- ** Author  : U028975 (T-Systems)                                 **
  -- ** Created : 23.12.2020                                          **
  -- ** Recuperar fichero enviado por RGA por Uccomand, leer dicho    **
  -- ** fichero y realizar el bloqueo/desbloqueo de los ASegurados    **
  -- ** que corresponda..                                             **
  -- *******************************************************************

  FUNCTION bloqueo RETURN VARCHAR2 IS

       l_dir         		     TB_CONFIG_AGP.AGP_valor%TYPE; 	        -- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
       l_dir_name    		     VARCHAR2(1000);				      	        -- Ruta física donde se ubican los ficheros que se reciben

       l_nombre      		     VARCHAR2(30) := '' ;                   -- Nombre del fichero de recepción
       f_fichero    		     UTL_FILE.FILE_TYPE;                    -- Variable que almacena la referencia al fichero de envío
       f_fich_error 		     UTL_FILE.FILE_TYPE;                    -- Variable que almacena la referencia al fichero de envío
       lc                    VARCHAR2(50) := 'PQ_BLOQUEO_CANCELACION_ASEG.bloqueo'; -- Variable que almacena el nombre del paquete y de la función

       nom_var_config        VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       nom_var_config_desa	 VARCHAR2 (30) := 'DIR_IN_AGP';        -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY en DESARROLLO

       nombre_fichero_bloqueos VARCHAR2(30):= 'NOMB_FIC_BLOQUEO';

       l_nombre_fich_errores  VARCHAR2(35) := 'ERRORES_MARCAS_BLOQ_CANC';

       l_line        		     VARCHAR2(1000);       -- Variables que almacena la linea que se leerá del fichero
       v_valor               VARCHAR2(1000);
       l_num_registros       NUMBER := 0;
   		 v_cons_bloq           VARCHAR2(2000) := '';

       v_compania           VARCHAR2(1);
       v_nif_asegurado      VARCHAR2(14);
       v_id_asegurado       VARCHAR2(50);
       v_nombre_asegurado   VARCHAR2(100);
       v_operacion          VARCHAR2(2);

       v_idEmpresa_Agro     VARCHAR2(1) := '1';  -- Valor fijo para identificador de Agro

       l_estado_asegurado   VARCHAR2(1);
       v_est_bloq           VARCHAR2(1) := 'B'; --> ESTADO 'BLOQUEADO'
       v_est_desbloq        VARCHAR2(1) := 'D'; --> ESTADO 'DESBLOQUEADO'

    	 TYPE cur_typ IS REF CURSOR;
	     v_cursor    cur_typ;

       existeElArchivo  BOOLEAN;
       longitudEnBytes  NUMBER;
       numeroDeBloques  NUMBER;

       valorestabla             t_array;
       l_num_lin_error          NUMBER := 0; -- Contador para el numero de errores retornados
    	 l_max_linefilesize_aux   NUMBER := 32000;
 		   l_line00                 VARCHAR2(500); -- Variables que almacena la linea 00 que se volcara en el fichero de errores
       l_num_pol                NUMBER := 0;  --> Variable donde se informará el nº de pólizas en vigor que tiene el usuario
       l_num_aseg               NUMBER := 0;  --> Variable donde se informará el nº de asegurados recuperadas para ese id
       l_num_aseg_bloq          NUMBER := 0;  --> Contador para el numero de Asegurados bloqueados en el proceso.
       l_aseg_bloqueados        NUMBER := 0;  --> Contador para saber si el asegurado ya existe en la tabla de bloqueos con estado 'BLOQUEADO'.
       l_aseg_desbloqueados     NUMBER := 0;  --> Contador para saber si el asegurado ya existe en la tabla de bloqueos con estado 'DESBLOQUEADO'
       l_num_aseg_desbloq       NUMBER := 0;  --> Contador para el numero de Asegurados desbloqueados en el proceso.
       l_num_reg_noAgro         NUMBER := 0;  --> Contador para el numero de Registros que no pertenecen a Agroseguro.
       l_num_reg_noExiste       NUMBER := 0;  --> Contador de registros cuyo NIF no eiste en la tabla de Asegurados

       l_length                 NUMBER := 0;
       tienePolenVigor          BOOLEAN := true;

  BEGIN

     -- Comienzo de escritura en log
     PQ_Utl.LOG(lc,'## INI BLOQUEO / CANCELACION DE ASEGURADOS##',1);

     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta de donde se reciben los ficheros.   ', 2);

     -- Desarolo
     --l_dir  := PQ_Utl.getcfg(nom_var_config_desa);
     --PQ_Utl.log (lc, 'Valor de l_dir:' || l_dir,2);

     -- TEST y PROD:
     l_dir  := PQ_Utl.getcfg(nom_var_config);
     PQ_Utl.log (lc, 'Valor de l_dir TEST y PROD:' || l_dir ,2);

     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name
       FROM ALL_DIRECTORIES
      WHERE DIRECTORY_NAME = l_dir;

     PQ_Utl.log (lc, 'Valor de l_dir_name:' || l_dir_name,2);

     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido.   ', 2);

     l_nombre :=PQ_Utl.getcfg(nombre_fichero_bloqueos);

     l_nombre := l_nombre  || to_char(sysdate,'_YYYYMMDD');

     PQ_Utl.log (lc, 'Valor de l_nombre:' || l_nombre, 2);

     /* Antes de abrir el fichero verifico si existe */
     UTL_FILE.FGETATTR( l_dir, l_nombre || '.dat', existeElArchivo, longitudEnBytes, numeroDeBloques);

     IF existeElArchivo THEN
        PQ_Utl.LOG(lc, 'El fichero '|| l_nombre  || '.dat' ||' existe, continuamos.' );
     ELSE
        PQ_Utl.LOG(lc, 'El fichero '|| l_nombre || '.dat' ||' NO existe en la ruta: ' || l_dir );
        /* retornamos OK pero no continuamos */
        RETURN 'OK';
     END IF;

     -- Se abre el fichero de texto de salida en la ruta indicada
     PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.  ', 2);
     f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.dat',	open_mode    => 'r');

      -- Abrimos el fichero de errores
      -- Se abre el fichero dat con modo 'wb' por que necesitamos que no se envíe en la ultima fila un salto de carro y no se añada al final
      -- una línea vacía
      l_nombre_fich_errores := l_nombre_fich_errores || to_char(sysdate,'_YYYYMMDD');
			f_fich_error := utl_file.fopen(location => l_dir, filename => l_nombre_fich_errores || '.dat', open_mode => 'wb', max_linesize =>l_max_linefilesize_aux);

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

          valorestabla :=  fn_split (v_valor, '|');
          l_estado_asegurado :='';

          /* Guardamos los datos leidos de la línea del fichero */
          v_compania := TRIM(valorestabla(1));
          v_nif_asegurado := TRIM(valorestabla(2));
          v_id_asegurado := TRIM(valorestabla(3));
          v_nombre_asegurado := TRIM(valorestabla(4));
          v_operacion := TRIM(substr(valorestabla(5), 1, 1));

          IF (TRIM(v_compania) = TRIM(v_idEmpresa_Agro)) THEN --> Registro de compañía Agro
            PQ_Utl.LOG(lc, 'Registro que pertenece a Agro, con NIF: '|| v_nif_asegurado, 2);

            -- Por solicitud de RGA, se updatean los asegurados por únicamente por el valor de NIF
            --execute immediate 'select count(*) from o02agpe0.tb_asegurados aseg
            --                    where aseg.id = ''' || v_id_asegurado || '''
            --                      and aseg.nifcif = ''' || v_nif_asegurado || ''' ' into l_num_aseg;

            execute immediate 'select count(*) from o02agpe0.tb_asegurados aseg
                                where aseg.nifcif = ''' || v_nif_asegurado || ''' ' into l_num_aseg;


            IF (l_num_aseg > 0) THEN
              /* 1º: Identificamos la operación a realizar sobre el asegurado (Bloqueo/Desbloqueo) */
              IF (TRIM(v_operacion) = TRIM(v_est_bloq)) THEN

                PQ_Utl.LOG(lc, 'BLOQUEO DE ASEGURADO : '|| v_nif_asegurado, 2);

       /************************/
       /* BLOQUEO DE ASEGURADO */
       /************************/
                 IF  (v_nif_asegurado IS NOT NULL) THEN
                    /* 2º: Verificamos que el asegurado no se encuentre bloqueado ya
                           - Si se encuentra ya bloqueado retornar en fichero de errores */
                    execute immediate 'select count(*) from o02agpe0.tb_bloqueos_asegurados bloq
                                where bloq.nifcif = ''' || v_nif_asegurado || '''
                                and bloq.idestado_aseg =''' || v_est_bloq || ''' ' into l_aseg_bloqueados;


                    IF (l_aseg_bloqueados > 0) THEN
                       PQ_Utl.LOG(lc, '-- Asegurado ya bloqueado, incluimos en fichero de errores', 2);

                       /* Añadimos registro en el fichero de salida de errores */
                       l_length := length(v_valor);
                       l_line00 := substr(v_valor,1, l_length-1) || '|B'
                                   || '|0002'
                                   || '|NIF YA BLOQUEADO';

                       l_num_lin_error := l_num_lin_error + 1;
                       utl_file.put_raw(f_fich_error, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));
                    ELSE
                         /* Comprobamos si el NIF se encuentra ya grabado en BBDD pero con estado 'Desbloqueado', en cuyo
                         /* caso se actualiza de nuevo el NIF a Bloqueado y fechas de auditorias */
                         execute immediate 'select count(*) from o02agpe0.tb_bloqueos_asegurados bloq
                                where bloq.nifcif = ''' || v_nif_asegurado || '''
                                and bloq.idestado_aseg =''' || v_est_desbloq || ''' ' into l_aseg_desbloqueados;

                         IF (l_aseg_desbloqueados > 0) THEN
                             pq_bloqueo_cancelacion_aseg.actualizar_bloqueo(v_nif_asegurado, v_id_asegurado, v_operacion);
                         ELSE
                             PQ_Utl.LOG(lc, 'Asegurado todavía no bloqueado', 2);

                             /* Comprobamos si el NIF tiene alguna póliza en Vigor */
                             PQ_Utl.log(lc, 'Comprobar polizas en Vigor para el NIF: ' || v_nif_asegurado);
                             tienePolenVigor := pq_bloqueo_cancelacion_aseg.comprobar_polizas_vigor(v_nif_asegurado);
                             PQ_Utl.log(lc, 'Despues de comprobar si tiene polizas en vigor');
                             IF (tienePolenVigor = true) THEN
                               /*  Si existen polizas en vigor, retornar en fichero de errores */
                               PQ_Utl.LOG(lc, '-- Asegurado tiene pólizas en Vigor, incluimos en fichero de errores', 2);

                               /* Añadimos registro en el fichero de salida de errores */
                               l_length := length(v_valor);
                               l_line00 := substr(v_valor,1, l_length-1) || '|B'
                                           || '|0001'
                                           || '|EXISTEN CONTRATOS/SINIESTROS EN VIGOR';

                               l_num_lin_error := l_num_lin_error + 1;

                               utl_file.put_raw(f_fich_error, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));
                             ELSE
                                /* Continuamos con el proceso*/
                                PQ_Utl.LOG(lc, 'Asegurado NO tiene pólizas en Vigor', 2);
                                l_num_aseg_bloq := l_num_aseg_bloq + 1;

                                /* 4º: Se inserta registro nuevo en la tabla de bloqueos asegurados */
                                pq_bloqueo_cancelacion_aseg.carga_asegurado(v_nif_asegurado, v_id_asegurado, v_operacion);
                             END IF;

                         END IF;  /* Fin del if de tiene asegurado desbloqueado */
                     END IF; /*  */
                 END IF;

              ELSE
                PQ_Utl.LOG(lc, 'DESBLOQUEO DE ASEGURADO : '|| v_nif_asegurado, 2);
       /***************************/
       /* DESBLOQUEO DE ASEGURADO */
       /***************************/
                 /* 2º: Verificamos que el asegurado Se encuentre bloqueado */

                 IF  (v_nif_asegurado IS NOT NULL) THEN
                   /* 2º: Verificamos que el asegurado no se encuentre bloqueado ya
                          - Si se encuentra ya bloqueado retornar en fichero de errores */

                    execute immediate 'select count(*) from o02agpe0.tb_bloqueos_asegurados bloq
                                where bloq.nifcif = ''' || v_nif_asegurado || '''
                                and bloq.idestado_aseg =''' || v_est_bloq || ''' ' into l_aseg_bloqueados;

                    IF (l_aseg_bloqueados > 0) THEN
                       l_num_aseg_desbloq := l_num_aseg_desbloq + 1;

                       /* 4º: Se inserta un nuevo registro en la tabla de bloqueos para indicar que el asegurado ha sido desbloequeado */
                       pq_bloqueo_cancelacion_aseg.actualizar_bloqueo(v_nif_asegurado, v_id_asegurado, v_operacion);
                    ELSE
                       PQ_Utl.LOG(lc, ' -- Asegurado ya desbloqueado, incluimos en fichero de errores', 2);

                       /*- Si se encuentra ya bloqueado retornar en fichero de errores */
                       /* Añadimos registro en el fichero de salida de errores */
                       l_length := length(v_valor);
                       l_line00 := substr(v_valor,1, l_length-1) || '|' || l_estado_asegurado
                                   || '|0003'
                                   || '|NIF YA DESBLOQUEADO';

                       l_num_lin_error := l_num_lin_error + 1;
                       utl_file.put_raw(f_fich_error, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));

                    END IF;

                 END IF;
              END IF;
            ELSE
               /* El asegurado no existe en nuestra BBDD */
              PQ_Utl.LOG(lc, 'Asegurado con id : '|| v_id_asegurado || ' y con NIF:' || v_nif_asegurado , 2);

              l_length := length(v_valor);

              l_estado_asegurado := '-';
              l_line00 := substr(v_valor,1, l_length-1) || '|' || l_estado_asegurado
                          || '|0004'
                          || '|NIF INEXISTENTE';

              l_num_lin_error := l_num_lin_error + 1;
              l_num_reg_noExiste := l_num_reg_noExiste + 1;

              utl_file.put_raw(f_fich_error, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));
            END IF;
          ELSE
            PQ_Utl.LOG(lc, 'Registro que NO pertenece a compañia Agro, con NIF: '|| v_nif_asegurado, 2);
            l_num_reg_noAgro := l_num_reg_noAgro + 1;
          END IF;

       EXCEPTION
           WHEN NO_DATA_FOUND THEN
              PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DEL FICHERO ' || v_nif_asegurado || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
        END;

     END LOOP;

     COMMIT;

     -- Guardamos los resultados en el fichero de log
     PQ_Utl.LOG(lc, '');
     PQ_Utl.LOG(lc, '*********************************************************************************', 2);
     PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO DE BLOQUEOS ' || l_nombre || ' FECHA ' || TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI:SS'), 2);
     PQ_Utl.LOG(lc, '*********************************************************************************', 2);
     PQ_Utl.LOG(lc, 'Registros procesados              := ' || l_num_registros, 2);
     PQ_Utl.LOG(lc, 'Registros con errores    := ' || l_num_lin_error, 2);
     PQ_Utl.LOG(lc, 'Registros No Agroseguro  := ' || l_num_reg_noAgro, 2);
     PQ_Utl.LOG(lc, 'Asegurados Bloqueados    := ' || l_num_aseg_bloq, 2);
     PQ_Utl.LOG(lc, 'Asegurados Desbloqueados := ' || l_num_aseg_desbloq, 2);
     PQ_Utl.LOG(lc, 'Asegurado Inexistente    := ' || l_num_reg_noExiste, 2);
     PQ_Utl.LOG(lc, '*********************************************************************************', 2);
     PQ_Utl.LOG(lc, '', 2);

     -- Fin de proceso
     PQ_Utl.LOG(lc,'El proceso ha finalizado correctamente a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
     PQ_Utl.LOG(lc,'## FIN  BLOQUEO / CANCELACION DE ASEGURADOS##',1);

     COMMIT;

        UTL_FILE.FCLOSE( f_fichero );
        UTL_FILE.FCLOSE( f_fich_error );

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
           PQ_Utl.LOG(lc,'ERROR AL PROCESAR EL FICHERO DE BLOQUE DE ASEGURADOS ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
           PQ_Err.raiser(sqlcode,'Error al generar los ficheros de MARCAS_BLOQ_CANC' || ' [' || SQLERRM || ']');

           -- Fin de proceso
           PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
           PQ_Utl.LOG(lc,'## FIN  BLOQUEO / CANCELACION DE ASEGURADOS##',1);
           return 'KO';

     END;

     FUNCTION fn_split (cadena IN VARCHAR2, separador IN CHAR)  RETURN t_array
      IS
         i         NUMBER          := 0;
         pos       NUMBER          := 0;
         lv_str    VARCHAR2 (2000) := cadena;
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
                  DBMS_OUTPUT.put_line (   'Linea '
                                        || (i + 1)
                                        || ', valor: '
                                        || strings (i + 1)
                                      );
               END IF;
            END LOOP;
         END IF;

      -- return array
      RETURN strings;
   END fn_split;

/************************************/
/**** PROCEDURE: carga_asegurado ****/
/************************************/
   PROCEDURE carga_asegurado (nifAseg IN VARCHAR2, idAseg IN VARCHAR2, operacion IN VARCHAR2) IS

        v_id             TB_ASEGURADOS.ID%TYPE;
        v_nifAseg        TB_ASEGURADOS.NIFCIF%TYPE;
        v_nombre         TB_ASEGURADOS.NOMBRE%TYPE;
        v_apellido1      TB_ASEGURADOS.APELLIDO1%TYPE;
        v_apellido2      TB_ASEGURADOS.APELLIDO2%TYPE;

        v_fecha_bloqueo    TB_BLOQUEOS_ASEGURADOS.FECHA_BLOQUEO%TYPE;
        v_usuario_bloqueo  TB_BLOQUEOS_ASEGURADOS.USUARIO_BLOQ%TYPE;
        v_fecha_desbloq    TB_BLOQUEOS_ASEGURADOS.FECHA_DESBLOQUE%TYPE;
        v_usu_desbloq      TB_BLOQUEOS_ASEGURADOS.USUARIO_DESBLOQ%TYPE;

        l_usuario          VARCHAR2(8) := 'BATCH';
        b_bloquear         BOOLEAN := false;

       v_consulta_aseg  varchar2(2000) := 'select aseg.id, aseg.nifcif, aseg.nombre, aseg.apellido1, aseg.apellido2 ' ||
                                'from o02agpe0.tb_asegurados aseg ' ||
                                'where aseg.nifcif = ''' || nifAseg || ''' ';

        TYPE cur_typ IS REF CURSOR;
        C_ASEGURADOS cur_typ;

    BEGIN

         IF (operacion = 'B') THEN
           v_fecha_bloqueo := sysdate;
           v_usuario_bloqueo := l_usuario;
        ELSE
           v_fecha_desbloq := sysdate;
           v_usu_desbloq := l_usuario;
        END IF;

        --LUEGO INSERTAMOS TODOS LOS REGISTROS PARA LOS ASEGURADOS QUE TIENEN PÓLIZA
        OPEN C_ASEGURADOS FOR v_consulta_aseg;
        LOOP
            FETCH C_ASEGURADOS INTO
            v_id,
            v_nifAseg,
            v_nombre,
            v_apellido1,
            v_apellido2;

           EXIT WHEN C_ASEGURADOS%NOTFOUND;

            /* por cada asegurado encontrado para ese nif se inserta el correspondiente dato en la tabla */
            INSERT into o02agpe0.tb_bloqueos_asegurados VALUES(
               o02agpe0.SQ_BLOQUEOS_ASEGURADOS.nextval,
               v_id,
               v_nifAseg,
               v_nombre,
               v_apellido1,
               v_apellido2,
               v_fecha_bloqueo,
               v_usuario_bloqueo,
               v_fecha_desbloq,
               v_usu_desbloq,
               operacion,
               sysdate);
               commit;

        END LOOP;


    EXCEPTION
         when others then
            pq_utl.log('ERROR EN Carga Bloqueos Asegurados ' || SQLERRM || '*********');
            rollback;
    END carga_asegurado;



    /***************************************/
    /**** PROCEDURE: actualizar_bloqueo ****/
    /***************************************/
    PROCEDURE actualizar_bloqueo (nifAseg IN VARCHAR2, idAseg IN VARCHAR2, operacion IN VARCHAR2) IS

        l_usuario       VARCHAR2(8) := 'BATCH';

    BEGIN

        IF (operacion = 'B') THEN

           UPDATE o02agpe0.tb_bloqueos_asegurados bloq
              SET bloq.fecha_bloqueo = sysdate,
                  bloq.usuario_bloq  = l_usuario,
                  bloq.idestado_aseg = operacion,
                  bloq.fecha_audit = sysdate
            WHERE bloq.nifcif = nifAseg;

        ELSE
           UPDATE o02agpe0.tb_bloqueos_asegurados bloq
              SET bloq.fecha_desbloque = sysdate,
                  bloq.usuario_desbloq = l_usuario,
                  bloq.idestado_aseg = operacion,
                  bloq.fecha_audit = sysdate
            WHERE bloq.nifcif = nifAseg;

        END IF;

        commit;

    EXCEPTION
         when others then
            pq_utl.log('* ERROR EN la Actualización del registro de tb_bloqueos:  ' || nifAseg || ' con el error:' || SQLERRM || '*');
            rollback;
    END actualizar_bloqueo;
    ---------------------------------------------------------------------------------------------------------------------------

 ---------------------------------------------------------------------------------------------------------------------------
 -- FUNCTION devolver si hay pólizas en vigor
 -----------------------------------------------------------------------------------------------------------------------------
 FUNCTION comprobar_polizas_vigor (v_nifAsegurado IN VARCHAR2) RETURN BOOLEAN IS

      v_vencimiento          BOOLEAN := false;
      v_meses                NUMBER(5);
      v_fecha_vencida        VARCHAR2(10);
      l_fecha_prueba         VARCHAR2(20);
      l_FECHAFIN_CONTRATO    VARCHAR2(8):= to_char(sysdate, 'YYYYMMDD');
      lc VARCHAR2(60) := 'pq_genera_ficheros_bd_unif.obtener_fechafin_contrato'; -- Variable que almacena el nombre del paquete y de la funcion
      l_query                 VARCHAR2(2000);

      v_fechaEnvio            VARCHAR2(8);
      v_codlinea              VARCHAR2(4);
      v_codplan               VARCHAR2(4);
      v_codmodulo             VARCHAR2(5);

      polizasEnVigor        BOOLEAN := false;
      b_bloquear            BOOLEAN := false;
      v_f_envio_aux         DATE;

      TYPE cur_typ IS REF CURSOR;
        C_POLIZAS cur_typ;

   BEGIN

      l_query := ' SELECT TO_CHAR(PO.FECHAENVIO, ''DDMMYYYY''), po.codmodulo, lin.codlinea, lin.codplan ' ||
                  ' FROM O02agpe0.TB_POLIZAS po ' ||
                  ' INNER JOIN o02agpe0.TB_ASEGURADOS ase on ase.id = po.idasegurado ' ||
                  ' INNER JOIN o02agpe0.TB_LINEAS lin on lin.lineaseguroid = po.lineaseguroid ' ||
                  ' WHERE po.IDESTADO IN  (8, 14) ' ||
                  ' and ase.nifcif = ''' || v_nifAsegurado || ''' ';
       PQ_UTL.log (lc, 'Valor de l_query: ' || l_query);

        --LUEGO INSERTAMOS TODOS LOS REGISTROS PARA LOS ASEGURADOS QUE TIENEN PÓLIZA
        OPEN C_POLIZAS FOR l_query;
        LOOP
            FETCH C_POLIZAS INTO
            v_fechaenvio,
            v_codmodulo,
            v_codplan,
            v_codlinea;

			    EXIT WHEN C_POLIZAS%NOTFOUND;
          /* Si estamos bloqueando por cada id de ASegurado tendremos que
          /* comprobar si tiene pólizas en vigor */
          pq_utl.log(lc, 'Comprobamos polizas en vigor para el NIF: ' || v_nifAsegurado);
          v_f_envio_aux := TO_DATE (v_fechaenvio, 'DDMMYYYY');
          b_bloquear := comprobar_vencimiento(v_f_envio_aux, v_codlinea, v_codmodulo);

         /* Si encontramos al menos una póliza en vigor, salimos con true */
         IF (b_bloquear = true) THEN
            return b_bloquear;
          END IF;
        END LOOP;
        close C_POLIZAS;
        return b_bloquear;

    EXCEPTION
         WHEN others THEN
            pq_utl.log('ERROR EN Carga Bloqueos Asegurados ' || SQLERRM || '*********');
            rollback;

    return b_bloquear;

 END comprobar_polizas_vigor;

 ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION comprobar_vencimiento
	--
	--  ---------------------------------------------------------------------------------------------------------------------------
	FUNCTION comprobar_vencimiento (v_fecha_envio in DATE, v_cod_linea in VARCHAR2, v_codmodulo in VARCHAR2) RETURN BOOLEAN IS

      v_vencimiento          BOOLEAN := false;
      v_meses                NUMBER(5);
      v_fecha_vencida        VARCHAR2(10);
      d_fecha_vencida        DATE;
      lc VARCHAR2(50) := 'pq_envio_polizas_agro_iris.comprobar_vencimiento'; -- Variable que almacena el nombre del paquete y de la funcion

   BEGIN

      -- 1º buscamos el valor del mes venicmiento de forma mas especifica (por codlinea y codmodulo)
      BEGIN
         select ME.NUM_MESES
           into v_meses
           from o02agpe0.tb_meses_venc ME
          where ME.CODLINEA = v_cod_linea
            and ME.CODMODULO = v_codmodulo;

      EXCEPTION
         WHEN NO_DATA_FOUND THEN
           v_meses := 0;
         WHEN OTHERS THEN
           DBMS_OUTPUT.PUT_LINE(SQLERRM);
           pq_utl.log(lc,SQLERRM);
			     -- Se escribe en el log el error
			     pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			     pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(1)' || ' [' || SQLERRM || ']');
           return false;
      END;


      IF (v_meses = 0) THEN
         BEGIN
           -- 2º buscamos el valor del mes venicmiento de forma mas genérica (por codlinea y codmodulo genérico)
           select ME.NUM_MESES
             into v_meses
             from o02agpe0.tb_meses_venc ME
            where ME.CODLINEA = v_cod_linea
              and ME.CODMODULO = '99999';
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_meses := 0;
            WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE(SQLERRM);
              pq_utl.log(lc,SQLERRM);
			        -- Se escribe en el log el error
			        pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			        pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(2)' || ' [' || SQLERRM || ']');
              return false;
         END;
      END IF;

      -- 3º buscamos el valor del mes venicmiento de forma genérica (por codlinea genérica y codmodulo genérico)
      IF (v_meses = 0) THEN
         BEGIN
           -- 2º buscamos el valor del mes venicmiento de forma mas genérica (por codlinea y codmodulo genérico)
           select ME.NUM_MESES
             into v_meses
             from o02agpe0.tb_meses_venc ME
            where ME.CODLINEA = 999
              and ME.CODMODULO = '99999';
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_meses := 0;
            WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE(SQLERRM);
              pq_utl.log(lc,SQLERRM);
			        -- Se escribe en el log el error
			        pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			        pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(3)' || ' [' || SQLERRM || ']');
              return false;
         END;
      END IF;

      -- Una vez obtenido los meses para la línea/codmodulo, calculamos la fehca de vencimiento
      v_fecha_vencida := TO_CHAR(add_months(v_fecha_envio, +v_meses), 'YYYY/MM/DD');

      /* Si la fecha vencimiento es menor que la del día es que la póliza está vencida */
      if (TO_CHAR(sysdate,'YYYY/MM/DD') > v_fecha_vencida ) THEN
         v_vencimiento := false;
      ELSE
         v_vencimiento := true;
      END IF;

      /* Retornamos true si la póliza está todavía en vigor*/
      RETURN v_vencimiento;

    END;
    -- **** FIN PETICIÓN 55722 - PTC-6273 ** MODIF TAM (03.02.2020) ****


end PQ_BLOQUEO_CANCELACION_ASEG;
/
SHOW ERRORS;