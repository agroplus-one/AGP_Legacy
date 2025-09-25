SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_RECEPCION_PAGOS_POLIZAS is

  -- Author  :U029769
  -- Created : 09/08/2013 11:35:32
   FUNCTION recepcion_pagos_polizas(p_fecha IN VARCHAR2) RETURN VARCHAR2;

   FUNCTION getMsgEstadoPago (codError IN VARCHAR2) RETURN VARCHAR2;

   FUNCTION recepcion_pagos_polizas_renova(p_fecha IN VARCHAR2) RETURN VARCHAR2;

end PQ_RECEPCION_PAGOS_POLIZAS;
/
create or replace package body o02agpe0.PQ_RECEPCION_PAGOS_POLIZAS is

  -- Author  : U029769
  -- Created : 09/08/2013 11:35:32
  --El formato del fichero de resultados de los pagos será el siguiente
  --Ind. abono/cargo	PIC X(1) (A/C)
  --Entidad 		PIC X(4)
  --Oficina			PIC X(4)
  ---Plan			PIC X(4)
  --Línea			PIC X(3)
  --NIF Asegurado		PIC X(9)
  --Póliza 			PIC X(8)
  --Módulo			PIC X(1)
  --Importe abono/cargo	PIC 9(11)
  --Fecha de vencimiento	PIC X(10)
  --Estado  		PIC X(2)
  --Descripción		PIC X(30)
  --Ind. Póliza financiada	PIC X(1)
  --N.Pago			PIC X(1)

  -- Array para almacenar los registros de la tabla TB_PAGO_ESTADOS_PAGO
   TYPE arrayEstadosPagoInt IS VARRAY(2) OF VARCHAR2(30);
   TYPE arrayEstadosPago IS VARRAY(20) OF arrayEstadosPagoInt;
   listaEstados arrayEstadosPago := arrayEstadosPago();


 FUNCTION recepcion_pagos_polizas(p_fecha IN VARCHAR2) RETURN VARCHAR2 IS

       v_ind_abono_cargo     VARCHAR(1);
       v_entidad             VARCHAR(4);
       v_oficina             VARCHAR(4);
       v_plan                VARCHAR(4);
       v_linea               VARCHAR(3);
       v_nifAseg             VARCHAR(9);
       v_ref_poliza          VARCHAR(7);
       v_dc_poliza           varchar2(1);
       v_modulo              VARCHAR(5);
       v_importe             VARCHAR(11);
       v_fechaVenc           VARCHAR(10);
       v_estado              VARCHAR(2);
       v_descipcion          VARCHAR(30);
       v_polFinanc           VARCHAR(1);
       v_NPago               VARCHAR(1);
       l_dir         		     TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
       l_dir_name    		     VARCHAR2(1000);				      	-- Ruta física donde se ubican los ficheros que se reciben
       l_dir_copia    	     TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan las copias de los ficheros recibidos
       l_dir_name_copia	     VARCHAR2(1000);					      -- Ruta física donde se ubican las copias de los ficheros que se reciben
       l_nombre      		     VARCHAR2(15) := 'pagos_agroplus' ;    -- Nombre del fichero de recepción
       f_fichero    		     UTL_FILE.FILE_TYPE;            -- Variable que almacena la referencia al fichero de envío
       lc                    VARCHAR2(50) := 'PQ_RECEPCION_PAGOS_POLIZAS.recepcion_pagos_polizas'; -- Variable que almacena el nombre del paquete y de la función
       nom_var_config        VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       nom_var_config_copia  VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       l_line        		     VARCHAR2(1000);         -- Variables que almacena la linea que se leerá del fichero
       l_num_registros       NUMBER := 0;            -- Contador de registros procesados
       v_idpoliza            NUMBER := 4;
       v_idagp               NUMBER := 2;

       -- Contadores --
       v_num_plz_ok     NUMBER := 0; -- Número de pagos recibidos correctos
       str_num_plz_ok   CONSTANT VARCHAR(20) := 'PAGOS_POLIZAS_OK';
       v_num_plz_error  NUMBER := 0; -- Número de pagos recibidos incorrectos
       str_num_plz_error  CONSTANT VARCHAR(20) := 'PAGOS_POLIZAS_ERROR';
       v_plz_error_msg  VARCHAR2(2000) := NULL; -- Listado de referencias con pagos erróneos
       str_plz_error_msg CONSTANT VARCHAR (24) := 'PAGOS_POL_MSG_ERROR';
       v_plz_ejecucion CONSTANT VARCHAR (22) := 'PAGOS_POL_EJECUCION'; -- Resultado de la ejecución del proceso
       v_msg_error_aux VARCHAR2(100);
       v_fecha_planif DATE;
  BEGIN

     -- Comienzo de escritura en log
     PQ_Utl.LOG(lc,'## INI ##',1);
     
     pq_utl.log(lc, 'Fecha de planificacion recibida por parametro - ' || p_fecha);
        
     v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');


     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos.   ', 2);
     l_dir  := PQ_Utl.getcfg(nom_var_config);
     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir;

     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido.   ', 2);
     l_dir_copia  := PQ_Utl.getcfg(nom_var_config_copia);
     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name_copia FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir_copia;

     -- Guarda una copia del fichero recibido en la ruta indicada
     PQ_Utl.LOG(lc, 'UTL_FILE.fcopy('||l_dir||', '||l_nombre || '.txt, '||l_dir_copia||', '|| l_nombre || to_char(v_fecha_planif,'YYMMDDHH24MISS') || '.TXT)   ', 2);
     UTL_FILE.fcopy(l_dir, l_nombre || '.txt', l_dir_copia, l_nombre  || to_char(v_fecha_planif,'YYMMDDHH24MISS') || '.TXT');

     -- Se abre el fichero de texto de salida en la ruta indicada
     PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.  ', 2);
     f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.txt',	open_mode    => 'r');


     -- Comienza el bucle para leer todas las líneas del fichero
     LOOP
        -- Lee la línea actual
        -- Si al leer se ha llegado al final del fichero, se lanzará la excepción 'no_data_found' y el flag
        -- v_tipo_exc a true indicará que la excepción la ha producido este método
        BEGIN
            UTL_FILE.get_line (f_fichero,l_line);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                -- Hemos llegado al final del fichero => SALIMOS DEL BUCLE
                EXIT;
        END;
        l_num_registros := l_num_registros + 1;
        --PQ_Utl.LOG(lc, 'Leida la línea ' || l_num_registros, 2);

        -- Si la línea leída está vacía salta a la siguiente iteración para leer otra línea
        IF (l_line is null OR (LENGTH(l_line)<=1)) THEN
           PQ_Utl.LOG(lc, 'La línea está vacía, se lee la siguiente.', 2);
           CONTINUE;
        END IF;

        BEGIN
           v_ind_abono_cargo := SUBSTR (l_line, 0, 1);
           v_entidad    := SUBSTR (l_line, 2, 4);
           v_oficina    := SUBSTR (l_line, 6, 4);
           v_plan       := SUBSTR (l_line, 10, 4);
           v_linea      := SUBSTR (l_line, 14, 3);
           v_nifAseg    := SUBSTR (l_line, 17, 9);
           v_ref_poliza := SUBSTR (l_line, 26, 7);
           v_dc_poliza  := SUBSTR (l_line, 33, 1);
           -- El módulo viene formateado a 5 caracteres, se hace trim para poder usarlo en las consultas posteriores
           v_modulo     := TRIM(SUBSTR (l_line, 34, 5));
           v_importe    := SUBSTR (l_line, 39, 11);
           v_fechaVenc  := SUBSTR (l_line, 50, 10);
           -- El estado del pago viene formateado a 2 caracteres, pero hay errores de 1 y de 2 dígitos
           v_estado     := TRIM (SUBSTR (l_line, 60, 2));
           v_descipcion := SUBSTR (l_line, 62, 30);
           v_polFinanc  := SUBSTR (l_line, 92, 1);
           v_NPago      := SUBSTR (l_line, 93, 1);

           -- Obtiene el idpoliza asociado a la referencia y al módulo indicado en el fichero
           select idpoliza into v_idpoliza
               from tb_polizas p
               where p.referencia = v_ref_poliza
               and  p.codmodulo = v_modulo
               and p.idestado in (3, 5, 7, 8);

           -- Obtiene el estado que tendrá la póliza en Agroplus a partir del estado
           -- recibido en el fichero
           select idagp into v_idagp
               from TB_PAGO_REL_AGP_PAGO
               where idpago=v_estado;

           IF (v_ind_abono_cargo = 'A') THEN
               -- Si se trata del abono a Agroseguro => marcamos la póliza con el estado correspondiente

               -- Actualizamos TB_PAGO_HISTORICO_PLZ
               Pq_Historico_Estados.pr_insertar_pago_poliza (v_idpoliza, v_idagp, v_estado, ' ', to_char(v_fecha_planif, 'DD/MM/YYYY HH24:MI:SS'));

               -- Actualiza el campo PAGADA de la póliza con el estado del pago
               update o02agpe0.tb_polizas set pagada=v_idagp where idpoliza = v_idpoliza;

               -- Actualiza los contadores dependiendo si el pago ha sido correcto o no
               IF (v_estado = '0') THEN
                   v_num_plz_ok := v_num_plz_ok + 1;
                   PQ_Utl.LOG(lc, '-- Pago de póliza ' || v_ref_poliza || ' con módulo ' || v_modulo || ' realizado correctamente   ', 2);
               ELSE
                   v_num_plz_error := v_num_plz_error + 1;
                   -- Obtiene la descripción del error asociada al código
                   v_msg_error_aux := getMsgEstadoPago (v_estado);
                   PQ_Utl.LOG(lc, '-- Pago de póliza ' || v_ref_poliza || ' con módulo ' || v_modulo || ' erróneo - ' || v_msg_error_aux || '   ', 2);
                   v_plz_error_msg := v_plz_error_msg || '#' || '- ' || v_ref_poliza || ' Módulo ' ||
                       v_modulo || ': ' || v_msg_error_aux;
               END IF;

           END IF;

           -- Insertamos en TB_PAGO_HISTORICO_BATCH todos los datos del fichero
           insert into tb_pago_historico_batch values (SQ_pago_historico_batch.Nextval,
               v_ind_abono_cargo, v_entidad, v_oficina, v_plan, v_linea, v_nifAseg,
               v_ref_poliza || v_dc_poliza, v_modulo, v_importe, v_fechaVenc, v_estado,
               v_descipcion, v_polFinanc, v_NPago);

       EXCEPTION
          WHEN NO_DATA_FOUND THEN
             PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DE LA POLIZA ' || v_ref_poliza || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
       END;

      END LOOP;

      -- Actualiza en la tabla de configuración los valores de los pagos procesados
      PQ_UTL.setcfg (str_num_plz_ok || p_fecha, v_num_plz_ok); -- Pagos correctos
      PQ_UTL.setcfg (str_num_plz_error || p_fecha, v_num_plz_error); -- Pagos erróneos
      -- Detalle de los pagos erróneos
      IF (v_plz_error_msg IS NOT NULL) THEN
    	  -- ESC-23068: Se quita la fecha del nemo PAGOS_POL_MSG_ERROR dado que no se utiliza en el correo de resumen
          PQ_UTL.setcfg (str_plz_error_msg, v_plz_error_msg);
      ELSE
          PQ_UTL.setcfg (str_plz_error_msg, ' ');
      END IF;
      -- ESC-23068: Se quita la fecha del nemo PAGOS_POL_EJECUCION dado que no se utiliza en el correo de resumen
      PQ_UTL.setcfg (v_plz_ejecucion, 'CORRECTO'); -- Se indica que el proceso ha sido correcto

      -- Guardamos los resultados en el fichero de log
      PQ_Utl.LOG(lc, '');
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || TO_CHAR(v_fecha_planif,'DD/MM/YY HH24:MI:SS'), 2);
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, 'Registros procesados   := ' || l_num_registros, 2);
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, '', 2);

      -- Fin de proceso
      PQ_Utl.LOG(lc,'El proceso ha finalizado correctamente a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
      PQ_Utl.LOG(lc,'## FIN ##', 1);

      COMMIT;

      RETURN 'OK';

      EXCEPTION
          WHEN NO_DATA_FOUND THEN
             -- Hemos llegado al final del fichero
             return 'OK';
					WHEN others THEN
             ROLLBACK;
             -- Se indica que el proceso ha sido erróneo
             PQ_UTL.setcfg (v_plz_ejecucion, 'ERRONEO');
    			   -- Se escribe en el log el error
             PQ_Utl.LOG(lc,'ERROR AL PROCESAR EL FICHERO DE RECEPCION DE PAGOS ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
             PQ_Err.raiser(sqlcode,'Error al generar los ficheros de recepcion_pagos_polizas' || ' [' || SQLERRM || ']');
             -- Fin de proceso
             PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
             PQ_Utl.LOG(lc,'## FIN ##', 1);
             return 'KO';

 END;


 -- --------------------------------------------------------------------------------------- --
 -- Devuelve el mensaje correspondiente al código de error del pago recibido como parámetro --
 -- --------------------------------------------------------------------------------------- --
 FUNCTION getMsgEstadoPago (codError IN VARCHAR2) RETURN VARCHAR2 IS

 TYPE cur_typ IS REF CURSOR;
 cur_est_pago cur_typ;

 v_estado o02agpe0.Tb_Pago_Estados_Pago.id%TYPE;
 v_desc   o02agpe0.Tb_Pago_Estados_Pago.descripcion%TYPE;
 v_msg    o02agpe0.Tb_Pago_Estados_Pago.descripcion%TYPE;

 BEGIN

      -- Comprueba si la lista de registros de TB_PAGO_ESTADOS_PAGO está vacía para rellenarla
      IF (listaEstados IS NULL OR listaEstados.COUNT = 0) THEN

         OPEN cur_est_pago FOR SELECT id, descripcion FROM o02agpe0.Tb_Pago_Estados_Pago;

         LOOP
             FETCH cur_est_pago INTO v_estado, v_desc;
             EXIT WHEN cur_est_pago%NOTFOUND;

             IF (codError = v_estado) THEN
                v_msg := v_desc;
             END IF;

             listaEstados.EXTEND;
             listaEstados(listaEstados.COUNT) := arrayEstadosPagoInt (v_estado, v_desc);

         END LOOP;

         RETURN v_msg;

      -- Si la lista ya está llena se busca en ella la descripción asociada al código de error recibido
      ELSE

          FOR i IN listaEstados.FIRST..listaEstados.LAST LOOP
              IF (listaEstados(i)(1) = codError) THEN
                 RETURN listaEstados(i)(2);
              END IF;
          END LOOP;


      END IF;

 END;




-----------------------------------------------------------------------------
--Función polizas renovables
------------------------------------------------------------------

FUNCTION recepcion_pagos_polizas_renova(p_fecha IN VARCHAR2) RETURN VARCHAR2 IS
 v_ind_abono_cargo     VARCHAR(1);
       v_entidad             VARCHAR(4):=NULL;
       v_oficina             VARCHAR(4):=NULL;
       v_plan                VARCHAR(4):=NULL;
       v_linea               VARCHAR(3):=NULL;
       v_nifAseg             VARCHAR(9):=NULL;
       v_ref_poliza          VARCHAR(7):=NULL;
       v_dc_poliza           varchar2(1):=NULL;
     	 v_disasg               VARCHAR2(3)  := NULL;
       --v_modulo              VARCHAR(5);
       v_importe             VARCHAR(11):=NULL;
       v_fechaVenc           VARCHAR(10):=NULL;
       v_estado              NUMBER(2);
       v_descripcion          VARCHAR(30):=NULL;
     --  v_polFinanc           VARCHAR(1);
     --  v_NPago               VARCHAR(1);
       l_dir         		     TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros recibidos
       l_dir_name    		     VARCHAR2(1000);				      	-- Ruta física donde se ubican los ficheros que se reciben
       l_dir_copia    	     TB_CONFIG_AGP.AGP_valor%TYPE; 	-- Valor del parametro de configuracion para el DIRECTORY donde se dejan las copias de los ficheros recibidos
       l_dir_name_copia	     VARCHAR2(1000);					      -- Ruta física donde se ubican las copias de los ficheros que se reciben
       l_nombre      		     VARCHAR2(25) := 'renovablesplus' ;    -- Nombre del fichero de recepción
       f_fichero    		     UTL_FILE.FILE_TYPE;            -- Variable que almacena la referencia al fichero de envío
       lc                    VARCHAR2(80) := 'PQ_RECEPCION_PAGOS_POLIZAS.recepcion_pagos_polizas_renova'; -- Variable que almacena el nombre del paquete y de la función
       nom_var_config        VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS_RENOVABLES'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       nom_var_config_copia  VARCHAR2 (30) := 'DIR_EXPORT_ENVIOS_RENOVABLES'; -- Nombre de la variable de ocnfiguración de TB_CONFIG_AGP que contiene el nombre del DIRECTORY
       l_line        		     VARCHAR2(1000):=NULL;         -- Variables que almacena la linea que se leerá del fichero
       l_num_registros       NUMBER := 0;            -- Contador de registros procesados
       v_idpoliza            NUMBER := 4;
       v_idagp               NUMBER := 2;
       contError               Boolean := true;
       -- Contadores --
       v_num_plz_ok     NUMBER := 0; -- Número de pagos recibidos correctos
       str_num_plz_ok   CONSTANT VARCHAR(40) := 'PAGOS_POL_RENOVA_OK';
       v_num_plz_error  NUMBER := 0; -- Número de pagos recibidos incorrectos
       str_num_plz_error  CONSTANT VARCHAR(40) := 'PAGOS_POL_RENOVA_ERR';
       v_plz_error_msg  VARCHAR2(2000) := NULL; -- Listado de referencias con pagos erróneos
       str_plz_error_msg CONSTANT VARCHAR (40) := 'PAGOS_POL_REN_MSG_ERR';
       v_plz_ejecucion CONSTANT VARCHAR (40) := 'PAGOS_POL_RENOVA_EJE'; -- Resultado de la ejecución del proceso
       v_msg_error_aux VARCHAR2(100);

        v_fecha_planif DATE;
  BEGIN

     -- Comienzo de escritura en log
     PQ_Utl.LOG(lc,'## INI ##',1);
    pq_utl.log(lc, 'Fecha de planificacion recibida por parametro - ' || p_fecha);
	
	v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');
    
     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros recibidos.   ', 2);
     l_dir  := PQ_Utl.getcfg(nom_var_config);
     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir;

     -- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido de OMEGA
     PQ_Utl.LOG(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositará la copia del fichero recibido.   ', 2);
     l_dir_copia  := PQ_Utl.getcfg(nom_var_config_copia);
     -- Se guarda el path fisico del directorio
     SELECT DIRECTORY_PATH into l_dir_name_copia FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir_copia;

     -- Guarda una copia del fichero recibido en la ruta indicada
     PQ_Utl.LOG(lc, 'UTL_FILE.fcopy('||l_dir||', '||l_nombre || '.txt, '||l_dir_copia||', '|| l_nombre || to_char(v_fecha_planif,'YYMMDDHH24MISS') || '.TXT)   ', 2);
     UTL_FILE.fcopy(l_dir, l_nombre || '.txt', l_dir_copia, l_nombre  || to_char(v_fecha_planif,'YYMMDDHH24MISS') || '.TXT');

     -- Se abre el fichero de texto de salida en la ruta indicada
     PQ_Utl.LOG(lc, 'Abre el fichero en la ruta indicada.  ', 2);
     f_fichero := UTL_FILE.FOPEN (LOCATION     => l_dir,	filename     => l_nombre || '.txt',	open_mode    => 'r');


     -- Comienza el bucle para leer todas las líneas del fichero
     LOOP
        -- Lee la línea actual
        -- Si al leer se ha llegado al final del fichero, se lanzará la excepción 'no_data_found' y el flag
        -- v_tipo_exc a true indicará que la excepción la ha producido este método
        BEGIN
            UTL_FILE.get_line (f_fichero,l_line);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                -- Hemos llegado al final del fichero => SALIMOS DEL BUCLE
                EXIT;
        END;
        l_num_registros := l_num_registros + 1;
        --PQ_Utl.LOG(lc, 'Leida la línea ' || l_num_registros, 2);

        -- Si la línea leída está vacía salta a la siguiente iteración para leer otra línea
        IF (l_line is null OR (LENGTH(l_line)<=1)) THEN
           PQ_Utl.LOG(lc, 'La línea está vacía, se lee la siguiente.', 2);
           CONTINUE;
        END IF;

        BEGIN

          PQ_Utl.LOG(lc, 'Comienza el procesamiento de la linea.', 2);

          -- Obtiene los datos de la línea del fichero
          v_entidad  := substr(l_line,  0,  4);
					v_oficina  := substr(l_line,  5,  4);
					v_plan  := substr(l_line,  9,  4);
					v_linea  := substr(l_line, 13,  3);
					v_nifAseg  := substr(l_line, 16,  9);
					v_disasg  := substr(l_line, 25,  3);
					v_ref_poliza  := substr(l_line, 28,  7);
          v_dc_poliza := substr(l_line,35,  1);
					v_importe  := substr(l_line, 36, 11);
					v_fechaVenc  := substr(l_line, 47, 10);
					v_estado  := TO_NUMBER (substr(l_line, 57,  2));
				  v_descripcion  := substr(l_line, 59, 30);
          v_ind_abono_cargo := substr(l_line, 89,  1);

          -- Pinta los datos en el log
          PQ_Utl.LOG(lc, 'Entidad = ' || v_entidad, 2);
          PQ_Utl.LOG(lc, 'Oficina = ' || v_oficina, 2);
          PQ_Utl.LOG(lc, 'Plan = ' || v_plan, 2);
          PQ_Utl.LOG(lc, 'Linea = ' || v_linea, 2);
          PQ_Utl.LOG(lc, 'NIF Aseg = ' || v_nifAseg, 2);
          PQ_Utl.LOG(lc, 'Dis Aseg = ' || v_disasg, 2);
          PQ_Utl.LOG(lc, 'Ref. plz = ' || v_ref_poliza, 2);
          PQ_Utl.LOG(lc, 'Dc. plz = ' || v_dc_poliza, 2);
          PQ_Utl.LOG(lc, 'Importe = ' || v_importe, 2);
          PQ_Utl.LOG(lc, 'Fecha Venc. = ' || v_fechaVenc, 2);
          PQ_Utl.LOG(lc, 'Estado = ' || v_estado, 2);
          PQ_Utl.LOG(lc, 'Descripcion = ' || v_descripcion, 2);
          PQ_Utl.LOG(lc, 'Abono/Cargo = ' || v_ind_abono_cargo, 2);



          IF v_disasg IS NULL OR v_disasg = '   ' THEN
					v_disasg := '0';
          END IF;

           -- Obtiene el idpoliza asociado a la referencia y al módulo indicado en el fichero
           --Apartir de aqui tenemos que verificar de acuerdo con la poliza renovable
           --- en poliza renovable no hay codmodulo

               select pol.id into v_idpoliza
                      from tb_polizas_renovables pol
                      where pol.referencia = v_ref_poliza and pol.plan=v_plan and pol.linea=v_linea;


             --  and  pol.codmodulo = v_modulo
               --and pol.idestado in (3, 5, 7, 8); -- se tendria que indicar solo para de estado=3? p.estadoAgroseguro

           -- Obtiene el estado que tendrá la póliza en Agroplus a partir del estado
           -- recibido en el fichero
           BEGIN
           select idagp into v_idagp
               from TB_PAGO_REL_AGP_PAGO
               where idpago=v_estado;
             EXCEPTION
            WHEN NO_DATA_FOUND THEN
               PQ_Utl.LOG(lc, 'ERROR AL BUSCAR LA POLIZA ' || v_ref_poliza || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
            END;
           IF (v_ind_abono_cargo = 'A') THEN
               -- Si se trata del abono a Agroseguro => marcamos la póliza con el estado correspondiente

               -- Actualizamos TB_PAGO_HISTORICO_PLZ
              -- Pq_Historico_Estados.pr_insertar_pago_poliza (v_idpoliza, v_idagp, v_estado, ' ', to_char(sysdate, 'DD/MM/YYYY HH24:MI:SS'));

               -- Actualiza el campo PAGADA de la póliza renovable con el estado del pago
               update o02agpe0.tb_polizas_renovables set pagada=v_idagp where id = v_idpoliza;

               -- Actualiza los contadores dependiendo si el pago ha sido correcto o no
               IF (v_estado = 0) THEN
                   -- Actualiza el campo PAGADA Y FECHA DE PAGO de la póliza asociada a la renovable
                   -- teniendo en cuenta el plan y la línea, además de la referencia
                   /*update o02agpe0.tb_polizas poll set poll.pagada=v_idagp,poll.fecha_pago=sysdate
                   where poll.referencia = v_ref_poliza;*/
                   update o02agpe0.tb_polizas poll
                     set poll.pagada = v_idagp, poll.fecha_pago = sysdate
                   where poll.idpoliza in
                         (select p.idpoliza
                            from o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
                           where p.referencia = v_ref_poliza
                             and p.lineaseguroid = l.lineaseguroid
                             and l.codplan = v_plan
                             and l.codlinea = v_linea);

                   v_num_plz_ok := v_num_plz_ok + 1;
                   PQ_Utl.LOG(lc, '-- Pago de póliza ' || v_plan || '/' || v_linea || ' - ' || v_ref_poliza || ' realizado correctamente   ', 2);
               ELSE
                   -- Actualiza solo el campo PAGADA de la póliza asociada a la renovable
                   -- teniendo en cuenta el plan y la línea, además de la referencia                   
                   /*update o02agpe0.tb_polizas poll set poll.pagada=v_idagp
                   where poll.referencia = v_ref_poliza;*/
                   
                   update o02agpe0.tb_polizas poll set poll.pagada=v_idagp
                   where poll.idpoliza in
                         (select p.idpoliza
                            from o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
                           where p.referencia = v_ref_poliza
                             and p.lineaseguroid = l.lineaseguroid
                             and l.codplan = v_plan
                             and l.codlinea = v_linea);

                   -- AMG Tratar dicho estado como un error para el correo resumen
                   v_num_plz_error := v_num_plz_error + 1;
                   -- Obtiene la descripción del error asociada al código
                   v_msg_error_aux := getMsgEstadoPago (v_estado);
                   PQ_Utl.LOG(lc, '-- Abono de póliza ' || v_plan || '/' || v_linea || ' - ' || v_ref_poliza || ' erróneo - ' || v_msg_error_aux || '   ', 2);
                   v_plz_error_msg := v_plz_error_msg || '#' || v_ref_poliza || ': ' || v_msg_error_aux;
                   -- fin AMG
               /*
               ELSE

                   v_num_plz_error := v_num_plz_error + 1;
                   -- Obtiene la descripción del error asociada al código
                   v_msg_error_aux := getMsgEstadoPago (v_estado);
                   PQ_Utl.LOG(lc, '-- Pago de póliza ' || v_ref_poliza || ' erróneo - ' || v_msg_error_aux || '   ', 2);
                   v_plz_error_msg := v_plz_error_msg || '#' || '- ' || v_ref_poliza || ': ' || v_msg_error_aux;
               */
               END IF;

           ELSIF (v_ind_abono_cargo = ' ' OR v_ind_abono_cargo = '') THEN
                  v_num_plz_error := v_num_plz_error + 1;
                   -- Obtiene la descripción del error asociada al código
                   v_msg_error_aux := getMsgEstadoPago (v_estado);
                   PQ_Utl.LOG(lc, '-- Pago de póliza ' || v_ref_poliza || ' erróneo - ' || v_msg_error_aux || '   ', 2);
                   --IF contError THEN
                   --    v_plz_error_msg := v_plz_error_msg || '' || v_ref_poliza || ': ' || v_msg_error_aux;
                   --ELSE
                       v_plz_error_msg := v_plz_error_msg || '#' || v_ref_poliza || ': ' || v_msg_error_aux;
                   --END IF;
                   --contError := false;
           END IF;

           -- Insertamos en TB_PAGO_HISTORICO_BATCH todos los datos del fichero
         --  insert into tb_pago_historico_batch values (SQ_pago_historico_batch.Nextval,
           --    v_ind_abono_cargo, v_entidad, v_oficina, v_plan, v_linea, v_nifAseg,
            --   v_ref_poliza || v_dc_poliza, v_modulo, v_importe, v_fechaVenc, v_estado,
             --  v_descipcion, v_polFinanc, v_NPago);
           INSERT INTO TB_POLIZAS_PAGOS_RENOVABLES
           (ID,IDPOLIZA, CODENTIDAD, OFICINA, PLAN, LINEA, NIFASEGURADO,DISCR_ASEG,REFERENCIA,DC,
           IMPORTE, FECHA_VTO, ESTADO, DESCRIPCION,IND_ABONO_CARGO)
           VALUES
           (SQ_POLIZAS_PAGOS_RENOVABLES.NEXTVAL,v_idpoliza,v_entidad,v_oficina,v_plan,v_linea,v_nifAseg,
           v_dc_poliza,v_ref_poliza,v_dc_poliza,v_importe,v_fechaVenc,v_estado,v_descripcion,v_ind_abono_cargo);

       EXCEPTION
          WHEN NO_DATA_FOUND THEN
             PQ_Utl.LOG(lc, 'ERROR AL PROCESAR LOS DATOS DE LA POLIZA ' || v_ref_poliza || '. ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
       END;

      END LOOP;

      -- Actualiza en la tabla de configuración los valores de los pagos procesados
      PQ_UTL.setcfg (str_num_plz_ok || p_fecha, v_num_plz_ok); -- Pagos correctos
      PQ_UTL.setcfg (str_num_plz_error || p_fecha, v_num_plz_error); -- Pagos erróneos
      -- Detalle de los pagos erróneos
      -- ESC-23068: Se quita la fecha del nemo PAGOS_POL_REN_MSG_ERR dado que no se utiliza en el correo de resumen
      IF (v_plz_error_msg IS NOT NULL) THEN
          PQ_UTL.setcfg (str_plz_error_msg, v_plz_error_msg);
      ELSE
          PQ_UTL.setcfg (str_plz_error_msg, ' ');
      END IF;
      -- ESC-23068: Se quita la fecha del nemo PAGOS_POL_RENOVA_EJE dado que no se utiliza en el correo de resumen
      PQ_UTL.setcfg (v_plz_ejecucion, 'CORRECTO'); -- Se indica que el proceso ha sido correcto

      -- Guardamos los resultados en el fichero de log
      PQ_Utl.LOG(lc, '');
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO ' || l_nombre || ' FECHA ' || TO_CHAR(v_fecha_planif,'DD/MM/YY HH24:MI:SS'), 2);
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, 'Registros procesados   := ' || l_num_registros, 2);
      PQ_Utl.LOG(lc, 'Registros correctos   := ' || v_num_plz_ok, 2);
      PQ_Utl.LOG(lc, 'Registros erróneos   := ' || v_num_plz_error, 2);
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
      PQ_Utl.LOG(lc, '', 2);

      -- Fin de proceso
      PQ_Utl.LOG(lc,'El proceso ha finalizado correctamente a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
      PQ_Utl.LOG(lc,'## FIN ##', 1);

      COMMIT;

      RETURN 'OK';

      EXCEPTION
          WHEN NO_DATA_FOUND THEN
             -- Hemos llegado al final del fichero
             return 'OK';
					WHEN others THEN
             ROLLBACK;
             -- Se indica que el proceso ha sido erróneo
             PQ_UTL.setcfg (v_plz_ejecucion, 'ERRONEO');
    			   -- Se escribe en el log el error
             PQ_Utl.LOG(lc,'ERROR AL PROCESAR EL FICHERO DE RECEPCION DE PAGOS ' || sqlcode || ' [' || SQLERRM || ']    ', 2);
             PQ_Err.raiser(sqlcode,'Error al generar los ficheros de recepcion_pagos_polizas' || ' [' || SQLERRM || ']');
             -- Fin de proceso
             PQ_Utl.LOG(lc,'El proceso ha finalizado CON ERRORES a las ' || TO_CHAR(SYSDATE,'HH24:MI:SS'), 2);
             PQ_Utl.LOG(lc,'## FIN ##', 1);
             return 'KO';

 END;

end PQ_RECEPCION_PAGOS_POLIZAS;
/
SHOW ERRORS;
