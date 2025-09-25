SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.PQ_ENVIO_POLIZAS_AGRO_IRIS IS

type t_array is TABLE OF VARCHAR2(10)INDEX BY BINARY_INTEGER;

  /******************************************************************************
     NAME:        PQ_ENVIO_POLIZAS_AGRO_IRIS
     PURPOSE:

     REVISIONS:
     Ver        Date        Author           Description
     ---------  ----------  ---------------  ------------------------------------
     1.0        24-12-2018  Ruben Lopez    1. Created this package.
                            T-Systems.
     1.1        03-02-2020  T-Systemes.    2. Modificación Petición 55722-PTC6273
                            
                            
  ******************************************************************************/

  -------------------------------------------------------------------------------
  -- PROCEDIMIENTOS PUBLICOS
  -------------------------------------------------------------------------------

  -- Genera el fichero para IRIS
  FUNCTION generacion_fichero_envio_iris RETURN VARCHAR2;
  
  
  -- Pinta la cadena recibida en el log independientemente de su tama?o
  FUNCTION print_log_completo(str IN VARCHAR2) RETURN VARCHAR2;

   -- Devuelve la consulta que obtiene formateados todos los campos de las polizas con estado 'Enviada correcta' y 'Emitida'
  FUNCTION crearQuery (v_tipo VARCHAR2) RETURN VARCHAR2;

  FUNCTION crearWhere RETURN VARCHAR2;

  -- Pet. 55722 ** MODIF TAM (04.02.2020) ** Inicio ** 
  FUNCTION crearWhereAnul RETURN VARCHAR2;
  
  FUNCTION crearWhereVenc RETURN VARCHAR2;
  FUNCTION comprobar_vencimiento (v_fecha_envio in DATE, v_cod_linea in VARCHAR2, v_codmodulo in VARCHAR2, v_id_poliza in VARCHAR2) RETURN BOOLEAN;
  -- Pet. 55722 ** MODIF TAM (04.02.2020) ** Fin **  
  
  PROCEDURE actualizaVarConfig;
  
  FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array;
  
 
END PQ_ENVIO_POLIZAS_AGRO_IRIS;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_ENVIO_POLIZAS_AGRO_IRIS AS

	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION generacion_fichero_envio_iris
	--
	-- Fichero de envio de pólizas iris
	--
	-- Recoge las polizas 'Enviada correcta' y 'Emitida' y genera un fichero para enviar a Iris con el siguiente formato:
  -- "	Tipo '00': un registro por fichero.      --> REGISTRO DE CABECERA 
	-- "	Tipo '01': un registro por póliza.       --> REGISTRO DE DATOS DEL ACUERDO Y PRIMER TITULAR 
  -- "	Tipo '03': dos registros por póliza:     --> REGISTRO DE DATOS DEL RESTO DE INTERVINIENTES 
  --       * Uno con datos del Asegurado
  --       * Otro con datos del Tomador 
  -- "	Tipo '07': un registro por póliza.       --> REGISTRO DE INFORMACIONES ADICIONALES/DERIVADAS 
  -- "	Tipo '51': un registro por póliza.       --> REGISTRO DE DETALLE DE DATOS DEL TITULAR 
  -- "	Tipo '53': un registro por póliza.       --> REGISTRO DE DETALLE DE DATOS DEL RESTO DE INTERVINNIENTES 
  -- "	Tipo '99': un registro por fichero.      --> REGISTRO DE TOTALES.
  --
  -- Pet. 55722-PTC-6273
  -- Recoge las polizas 'Anuladas' y 'Vencidas' y se añaden al fichero (al final) para enviar a RSI las cancelaciones
  -- "	Tipo '01': un registro por póliza.       --> REGISTRO DE DATOS DEL ACUERDO Y PRIMER TITULAR con estado del acuerdo '7' (Cancelado)
    -- Pet. 67752 ** MODIF T-Systems (26/08/2021)  --> Se obtiene la fecha de vencimiento de la tabla de pólizas, en caso de tenerla informada.
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------

	-- Inicio de declaracion de funcion
	FUNCTION generacion_fichero_envio_iris RETURN VARCHAR2 IS

		-- Variables generales
		lc VARCHAR2(60) := 'pq_envio_polizas_agro_iris.generacion_fichero_envio_iris'; -- Variable que almacena el nombre del paquete y de la funcion
		TYPE tpcursor IS REF CURSOR; -- Tipo cursor
		f_fichero           utl_file.file_type; -- Variable que almacena la referencia al fichero de envio
		l_dir               tb_config_agp.agp_valor%TYPE; -- Valor del parametro de configuracion para el DIRECTORY donde se dejan los ficheros a enviar
		l_line00            VARCHAR2(256); -- Variables que almacena la linea 00 que se volcara en el fichero
		l_line01            VARCHAR2(256); -- Variables que almacena la linea 01 que se volcara en el fichero
		l_line03            VARCHAR2(256); -- Variables que almacena la linea 03 que se volcara en el fichero
		l_line07            VARCHAR2(256); -- Variables que almacena la linea 07 que se volcara en el fichero
		l_line51            VARCHAR2(256); -- Variables que almacena la linea 51 que se volcara en el fichero
		l_line53            VARCHAR2(256); -- Variables que almacena la linea 53 que se volcara en el fichero
		l_line99            VARCHAR2(256); -- Variables que almacena la linea 99 que se volcara en el fichero
		l_nombre            VARCHAR2(15) := 'PLZS_AGRO_IRIS'; -- Nombre del fichero de envio
		l_dir_name          VARCHAR2(1000); -- Ruta fisica donde se dejan los ficheros que se van a enviar
		l_tp_cursor         tpcursor; -- Cursor para la consulta de polizas ('Altas')
    
    
		l_num_polizas       NUMBER := 0; -- Contador para el numero de polizas enviadas
		l_num_lineas	      NUMBER := 0; -- Contador para el numero de lineas escritasç
		no_polizas_found    EXCEPTION; -- Excepcion indicadora de que no hay polizas para enviar
    crlf                VARCHAR2(1) := chr(13);
    


		-- Variables para componer la consulta para obtener las polizas
		l_query          VARCHAR2(12500);

		l_max_linefilesize_aux NUMBER := 32000;
    
    -- Variables necesarias para el cursor
    v_id_poliza                  VARCHAR2(10);
    v_cod_entidad                VARCHAR2(4);
		v_f_envio                    VARCHAR2(8);                 
		v_acuerdo_origen             VARCHAR2(20);
		v_cod_oficina                VARCHAR2(4);
		v_nifcif                     VARCHAR2(10);
		v_tipo_identificacion        VARCHAR2(4);       
		v_cod_linea                  VARCHAR2(4);
		v_nom_linea                  VARCHAR2(50);
		v_nom_apellidos              VARCHAR2(100);
		v_direccion                  VARCHAR2(100);
		v_nom_localidad              VARCHAR2(50);
    v_cod_agricola_ganado        VARCHAR2(4);
    v_tipo_identificacion_codigo VARCHAR2(2);
    v_telefono_fijo_asegurado    VARCHAR2(15);
		v_telefono_movil_asegurado   VARCHAR2(15);  
    v_nifcif_tomador             VARCHAR2(14);
	  v_razon_social_tomador       VARCHAR2(50);
		v_domicilio_tomador          VARCHAR2(100);
		v_telefono_fijo_tomador      VARCHAR2(15);
		v_telefono_movil_tomador     VARCHAR2(15);  
    v_codmodulo                  VARCHAR2(5);
    v_f_envio_aux                DATE;
    --por lo general es len.7 y el max(len()) en pro a 19/06/24 es de 8
    v_cod_usuario                VARCHAR2(8);
    
    -- Pet. 55722 
    l_num_cancel                NUMBER := 0; -- Contador para el numero de Cancelaciones enviadas.
    l_query_anul                VARCHAR2(12500);
    l_query_venc                VARCHAR2(12500);
    l_num_anul                  NUMBER := 0; -- Contador para el numero de pólizas Anuladas
    l_num_venc                  NUMBER := 0; -- Contador para el numero de pólizas Vencidas
    v_vencida                   BOOLEAN := false;
    nom_agp_cancel_env          VARCHAR2(16) := 'CANCEL_IRIS_AGRO';
    
		-- Inicio del cuerpo de la funcion
		BEGIN

			-- Comienzo de escritura en log
			pq_utl.log(lc,'## INICIO PROCEDIMIENTO PQ_ENVIO_POLIZAS_AGRO_IRIS ##', 1);


			-- Obtiene de la tabla TB_CONFIG_AGP el nombre del DIRECTORY que contiene la ruta donde se depositan los ficheros a enviar
			pq_utl.log(lc, 'Obtiene el nombre del DIRECTORY que contiene la ruta donde se depositaran los ficheros de envio.', 2);

			-- Directorio de desarrollo TEST y PR
			l_dir := pq_utl.getcfg('DIR_EXPORT_ENVIOS');
      

			-- Se guarda el path fisico del directorio
			pq_utl.log(lc, 'Obtiene la ruta fisica del DIRECTORY.', 2);

			SELECT directory_path
					INTO l_dir_name
					FROM all_directories
				 WHERE directory_name = l_dir;

			-- Se abre el fichero de texto de salida en la ruta indicada
			pq_utl.log(lc, 'Abre el fichero de texto de salida en la ruta indicada.', 2);

      -- TAM: Se abre el fichero txt con modo 'wb' por que necesitamos que no se envíe en la ultima fila un salto de carro y no se añada al final 
      -- una línea vacía
			f_fichero := utl_file.fopen(location => l_dir, filename => l_nombre || '.TXT', open_mode => 'wb', max_linesize =>l_max_linefilesize_aux);
      

      -- Antes de generar el fichero consultamos las polizas.
      actualizaVarConfig();
			---------------------------------------------
			-- Escribimos regitros tipo '01' '03' '07' '51' '99'
			---------------------------------------------

			-- Se compone la consulta para recuperar las polizas con estado 'Enviada correcta' y 'Emitida'
			l_query:= crearQuery('P');

			-- Crea el cursor para la consulta
			pq_utl.log(lc, 'Abre el cursor para la query');

			OPEN l_tp_cursor FOR l_query;

			-- Bucle para recorrer todos los registros del cursor
			LOOP

				-- Vuelca los datos del cursor en las variables indicadas
		
				FETCH l_tp_cursor
					INTO
						v_id_poliza,
						v_cod_entidad,
						v_f_envio,
						v_acuerdo_origen,
						v_cod_oficina,
						v_nifcif,
						v_tipo_identificacion,
						v_cod_linea,
						v_nom_linea,
						v_nom_apellidos,
						v_direccion,
						v_nom_localidad,
            v_telefono_fijo_asegurado,
		        v_telefono_movil_asegurado,
            v_nifcif_tomador,
            v_razon_social_tomador,
            v_domicilio_tomador,
            v_telefono_fijo_tomador,
            v_telefono_movil_tomador,
            v_cod_usuario;

				-- Pinto los datos recogidos
				--pq_utl.log(lc, 'v_id_poliza = ' || v_id_poliza);
				--pq_utl.log(lc, 'v_f_envio = ' || v_f_envio);
				--pq_utl.log(lc, 'v_acuerdo_origen = ' || v_acuerdo_origen);
				--pq_utl.log(lc, 'v_cod_oficina = ' || v_cod_oficina);
				--pq_utl.log(lc, 'v_nifcif = ' || v_nifcif);
				--pq_utl.log(lc, 'v_tipo_identificacion = ' || v_tipo_identificacion);
				--pq_utl.log(lc, 'v_cod_linea = ' || v_cod_linea);
				--pq_utl.log(lc, 'v_nom_linea = ' || v_nom_linea);
				--pq_utl.log(lc, 'v_nom_apellidos = ' || v_nom_apellidos);
				--pq_utl.log(lc, 'v_direccion = ' || v_direccion);
				--pq_utl.log(lc, 'v_nom_localidad = ' || v_nom_localidad);

				IF (l_tp_cursor%NOTFOUND) THEN

					-- Si el numero de polizas tratadas es cero se lanza la excepcion correspondiente
					IF (l_num_polizas = 0) THEN
						pq_utl.log(lc, 'No se han encontrado polizas');
						RAISE no_polizas_found;
					ELSE
						pq_utl.log(lc, 'Valor de fecha envio - ' || v_f_envio);
					END IF;
          
					pq_utl.log(lc, 'No hay mas registros que enviar.');

					-- Sale del bucle
					EXIT;
				END IF;
        
      ---------------------------------------------
			-- Escribimos primer registro, tipo '00' -> Si se han encontrado datos. Sino no se escribe ni el primer registro
			---------------------------------------------
      if l_num_lineas = 0 THEN 
   			l_line00 := '00' ||
	   						'XXXXXXX' ||
		   					'9996' ||
			   				TO_CHAR(sysdate, 'DDMMYYYY') ||
				   			RPAD(' ', 229);

   			l_num_lineas := l_num_lineas + 1;
      
         -- Inserta en el fichero de envio la linea '00'
	   		--pq_utl.log(lc, 'Escribe la linea "00" en el fichero de envio.');

        -- TAM: En vez de añadir la línea en el fichero con el put_line, lo hacemos con el put_raw para que no se añada al 
        -- final del fichero el salto de carro y por tanto una línea vacía
   			/*utl_file.put_line(f_fichero, l_line00 || chr(13));*/
        utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));
        
      END IF;   
      -- Actualiza el contador de polizas, cada vez que entramos al bucle es que tenemos una poliza mas
		  l_num_polizas := l_num_polizas + 1;

      
			-- Damos valor a variables dependientes------
			---------------------------------------------
			IF (v_cod_linea >= 300 AND v_cod_linea < 400)	THEN
			  v_cod_agricola_ganado := '1001';
      END IF;

			IF (v_cod_linea >= 400 AND v_cod_linea < 500)	THEN
			  v_cod_agricola_ganado := '1002';
      END IF;

			IF (v_tipo_identificacion = 'NIF') THEN
				v_tipo_identificacion_codigo := '51';
      END IF;

			IF (v_tipo_identificacion = 'CIF') THEN
				v_tipo_identificacion_codigo := '01';
      END IF;

			IF (v_tipo_identificacion = 'NIE') THEN
				v_tipo_identificacion_codigo := '53';
      END IF;
      
      v_cod_oficina := lpad(v_cod_oficina, 4, '0');

      
      -- Formamos los campos del asegurado y tomador
      v_nifcif_tomador := rpad(v_nifcif_tomador,14);
			v_razon_social_tomador := rpad(v_razon_social_tomador,50);
			v_domicilio_tomador := rpad(v_domicilio_tomador,100);
      
			IF v_telefono_fijo_tomador IS NULL THEN
         v_telefono_fijo_tomador := rpad(' ',15);
      ELSE
          v_telefono_fijo_tomador := rpad(v_telefono_fijo_tomador,15);
      END IF;
      
      IF v_telefono_movil_tomador IS NULL THEN
         v_telefono_movil_tomador := rpad(' ',15);
      ELSE
          v_telefono_movil_tomador := rpad(v_telefono_movil_tomador,15);
      END IF;
      
      IF v_telefono_fijo_asegurado IS NULL THEN
         v_telefono_fijo_asegurado := rpad(' ',15);
      ELSE
          v_telefono_fijo_asegurado := rpad(v_telefono_fijo_asegurado,15);
      END IF;
      
      IF v_telefono_movil_asegurado IS NULL THEN
         v_telefono_movil_asegurado := rpad(' ',15);
      ELSE
          v_telefono_movil_asegurado := rpad(v_telefono_movil_asegurado,15);
      END IF;
      
      IF v_direccion IS NULL THEN
         v_direccion := rpad(' ',100);
      ELSE
          v_direccion := rpad(v_direccion,100);
      END IF;
      
      IF v_nom_apellidos IS NULL THEN
         v_nom_apellidos := rpad(' ',50);
      ELSE
          v_nom_apellidos := rpad(v_nom_apellidos,50);
      END IF;
      
      IF v_f_envio IS NULL THEN
         v_f_envio := rpad(' ',8);
      END IF;   
      
      IF v_f_envio = '        ' THEN
         v_f_envio := TO_CHAR(sysdate, 'YYYYMMDD');
      END IF;   

			---------------------------------------------
			-- Escribimos regitros tipo '01'
			---------------------------------------------

				l_line01 := 	'01' ||
							v_cod_entidad ||
							rpad(v_f_envio, 8) ||
							rpad(v_acuerdo_origen, 20) || -- Son 20 de longitud en total, de momento el resto blancos
							rpad('0', 10, '0') ||
							v_cod_oficina ||
							'05' ||
							'83' ||
							v_cod_agricola_ganado || -- 1001 o 1002
							'001' ||
							'000000000' ||
							rpad(v_nifcif, 14) ||
							v_tipo_identificacion_codigo || --01, 51 o 53
							'978' ||
							rpad(' ', 3) ||
							'4' || 
							'A' || 
							'USUARIO:' || -- Literal usuario
							rpad(v_cod_usuario, 8) || -- Codigo usuario
							rpad(' ',142);

          -- Inserta en el fichero de envio la linea '00'
          --pq_utl.log(lc, 'Escribe la linea "01" en el fichero de envio.');
        
   			/*utl_file.put_line(f_fichero, l_line01 || chr(13));*/
        utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line01||utl_Tcp.crlf));

				l_num_lineas := l_num_lineas + 1;

			---------------------------------------------
			-- Escribimos regitros tipo '03'
			---------------------------------------------

       -- Escribe el registo '03' de asegurado                                        
				l_line03 := 	'03' ||
								v_cod_entidad ||
								rpad(v_acuerdo_origen, 20) || -- Son 20 de longitud en total, de momento el resto blancos
								rpad('0', 10, '0') ||
								v_cod_oficina ||
								'05' ||
								'83' ||
								v_cod_agricola_ganado || -- 1001 o 1002
								'001' ||
								rpad('0', 9, '0') ||
								rpad(v_nifcif, 14) || 
								v_tipo_identificacion_codigo || --01, 51 o 53
        -- Modif TAM (06.08.2019) ** Solicitado por RGA modificar el tipo de relación para el asegurado y en vez de
        -- enviar el valor '01', desde RSI solicitan modificarlo y poner el valor '37' para el asegurado.        
				--				'01' || --Si es titular '01', si es tomador '80'
        				'37' || --Si es asegurado '37', si es tomador '80'
								rpad('0', 15, '0') ||
								rpad('0', 2, '0') ||
								rpad(v_f_envio, 8) ||
								rpad(' ',8) || -- Fecha inactivo (Valor de la fecha de seguimiento en TB_POLIZAS)
								'A' || -- Correspondiente a Alta (pendiente por confirmar como sera la baja)
								rpad(' ',138);

				l_num_lineas := l_num_lineas + 1;
        
        -- Inserta en el fichero de envio la linea '03'
			  --pq_utl.log(lc, 'Escribe la linea "03" en el fichero de envio.');

        
   			/*utl_file.put_line(f_fichero, l_line03 || chr(13));*/
        utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line03||utl_Tcp.crlf));


        -- Escribe el registo '03' de tomador                                       
				l_line03 := 	'03' ||
								v_cod_entidad ||
								rpad(v_acuerdo_origen, 20) || -- Son 20 de longitud en total, de momento el resto blancos
								rpad('0', 10, '0') ||
								v_cod_oficina ||
								'05' ||
								'83' ||
								v_cod_agricola_ganado || -- 1001 o 1002
								'001' ||
								rpad('0', 9, '0') ||
                rpad(v_nifcif_tomador,14) ||
								'01' || --01, 51 o 53 -> Para el tomador siempre 'CIF'
								'80' || --Si es Asegurado '37', si es tomador '80'
								rpad('0', 15, '0') ||
								rpad('0', 2, '0') ||
								rpad(v_f_envio, 8) ||
								rpad(' ',8) || -- Fecha inactivo (DE MOMENTO VACIO)
								'A' || -- Correspondiente a Alta
								rpad(' ',138);

				l_num_lineas := l_num_lineas + 1;
        
        -- Inserta en el fichero de envio la linea '03'
			  --pq_utl.log(lc, 'Escribe la linea "03" en el fichero de envio.');

        
        /*utl_file.put_line(f_fichero, l_line03 || chr(13));*/
        utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line03||utl_Tcp.crlf));

        
        
			---------------------------------------------
			-- Escribimos registros, tipo '07'
			---------------------------------------------
				l_line07 := 	'07' ||
								v_cod_entidad ||
								rpad(v_acuerdo_origen, 20) || -- Son 20 de longitud en total, de momento el resto blancos
								rpad('0', 10, '0') ||
								v_cod_oficina ||
								'05' ||
								'83' ||
								v_cod_agricola_ganado || -- 1001 o 1002
								'001' ||
								'1' ||
								'84' ||
								rpad(v_nom_linea, 50) ||
								'A' || -- Correspondiente a Alta
								rpad(' ',145);


        -- Inserta en el fichero de envio la linea '07'
			  --pq_utl.log(lc, 'Escribe la linea "07" en el fichero de envio.');


        /*utl_file.put_line(f_fichero, l_line07 || chr(13));*/
        utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line07||utl_Tcp.crlf));


				l_num_lineas := l_num_lineas + 1;

			---------------------------------------------
			-- Escribimos registros, tipo '51'
			---------------------------------------------

				l_line51 := 	'51' ||
								v_cod_entidad ||
								rpad(v_acuerdo_origen, 20) || -- Son 20 de longitud en total, de momento el resto blancos
								rpad('0', 10, '0') ||
								v_cod_oficina ||
								'05' ||
								'83' ||
								v_cod_agricola_ganado || -- 1001 o 1002
								'001' ||
								rpad(v_nifcif, 14) ||
                v_tipo_identificacion_codigo ||
                rpad(v_nom_apellidos, 50) ||
                rpad(v_direccion, 100) ||
                rpad(v_telefono_fijo_asegurado, 15) ||
                rpad(v_telefono_movil_asegurado, 15) ||
                rpad(' ',3);


        -- Inserta en el fichero de envio la linea '51'
			  --pq_utl.log(lc, 'Escribe la linea "51" en el fichero de envio.');
        
			  
        /*utl_file.put_line(f_fichero, l_line51 || chr(13));*/
        utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line51||utl_Tcp.crlf));

				l_num_lineas := l_num_lineas + 1;

			---------------------------------------------
			-- Escribimos registros, tipo '53'
			---------------------------------------------
     
				l_line53 := 	'53' ||
								v_cod_entidad ||
								rpad(v_acuerdo_origen, 20) || -- Son 20 de longitud en total, de momento el resto blancos
								rpad('0', 10, '0') ||
								v_cod_oficina ||
								'05' ||
								'83' ||
								v_cod_agricola_ganado || -- 1001 o 1002
								'001' ||
								v_nifcif_tomador || -- nifcif del tomador
                '01' ||
								v_razon_social_tomador || -- razon social del tomador
								v_domicilio_tomador || -- domicilio completo del tomador
								rpad(v_telefono_fijo_tomador, 15) ||
                rpad(v_telefono_movil_tomador, 15) ||
								rpad(' ',3);


        -- Inserta en el fichero de envio la linea '53'
			  --pq_utl.log(lc, 'Escribe la linea "53" en el fichero de envio.');

			  
        /*utl_file.put_line(f_fichero, l_line53 || chr(13));*/
        utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line53||utl_Tcp.crlf));

				l_num_lineas := l_num_lineas + 1;

				-- Actualizamos el campo fecha_envio_iris con la fecha actual de la poliza que estamos tratando
				UPDATE TB_POLIZAS
					 SET FECHA_ENVIO_IRIS = sysdate
				WHERE idpoliza = v_id_poliza;


			END LOOP;

      -- Cierra el cursor
		  pq_utl.log(lc, 'Cierra el cursor.');
		  CLOSE l_tp_cursor;
      
      -- ========================================================================================
      -- Pet. 55722 ** MODIF TAM (03.02.2020) ** INICIO **
      -- -------------------------------------------------------------------------
      -- Realizamos la consulta de las pólizas Anuladas y las añadimos al fichero.
      pq_utl.log(lc, 'Entramos a recuperar las Cancelaciones (Anuladas)');
      
      -- Crea el cursor para la consulta
			pq_utl.log(lc, 'Abre el cursor para la query de Cancelaciones (Anuladas)',2);
      
      l_query_anul:= crearQuery('A');

			OPEN l_tp_cursor FOR l_query_anul;

			-- Bucle para recorrer todos los registros del cursor
			LOOP

				-- Vuelca los datos del cursor en las variables indicadas
		
				FETCH l_tp_cursor
					INTO
						v_id_poliza,
						v_cod_entidad,
						v_f_envio,
						v_acuerdo_origen,
						v_cod_oficina,
						v_nifcif,
						v_tipo_identificacion,
						v_cod_linea,
						v_nom_linea,
						v_nom_apellidos,
						v_direccion,
						v_nom_localidad,
            v_telefono_fijo_asegurado,
		        v_telefono_movil_asegurado,
            v_nifcif_tomador,
            v_razon_social_tomador,
            v_domicilio_tomador,
            v_telefono_fijo_tomador,
            v_telefono_movil_tomador,
            v_cod_usuario;

				IF (l_tp_cursor%NOTFOUND) THEN

					-- Si el numero de polizas tratadas es cero se lanza la excepcion correspondiente
					IF (l_num_cancel = 0) THEN
						pq_utl.log(lc, 'No se han encontrado polizas de Anuladas');
						--RAISE no_polizas_found;
					ELSE
						pq_utl.log(lc, 'Valor de fecha envio - ' || v_f_envio);
					END IF;
          
					pq_utl.log(lc, 'No hay mas Anulaciones que enviar.');

					-- Sale del bucle
					EXIT;
				END IF;
        
				---------------------------------------------
			  -- Escribimos primer registro, tipo '00' -> Si se han encontrado datos. Sino no se escribe ni el primer registro
			  ---------------------------------------------
        if l_num_lineas = 0 THEN 
   		  	l_line00 := '00' ||
	   			 			      'XXXXXXX' ||
		   					      '9996' ||
			   				      TO_CHAR(sysdate, 'DDMMYYYY') ||
				   			      RPAD(' ', 229);

   			  l_num_lineas := l_num_lineas + 1;

          utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));
        
        END IF;   
        
        -- Actualiza el contador de polizas, cada vez que entramos al bucle es que tenemos una poliza mas
        l_num_cancel := l_num_cancel + 1;
        l_num_anul := l_num_anul + 1;
      
			  -- Damos valor a variables dependientes------
			  ---------------------------------------------

			  IF (v_tipo_identificacion = 'NIF') THEN
				   v_tipo_identificacion_codigo := '51';
        END IF;

			  IF (v_tipo_identificacion = 'CIF') THEN
				   v_tipo_identificacion_codigo := '01';
        END IF;

			  IF (v_tipo_identificacion = 'NIE') THEN
				   v_tipo_identificacion_codigo := '53';
        END IF;
      
 			  IF (v_cod_linea >= 300 AND v_cod_linea < 400)	THEN
			     v_cod_agricola_ganado := '1001';
        END IF;

			  IF (v_cod_linea >= 400 AND v_cod_linea < 500)	THEN
			     v_cod_agricola_ganado := '1002';
        END IF;

        IF v_f_envio IS NULL THEN
           v_f_envio := rpad(' ',8);
        END IF;   
      
        IF v_f_envio = '        ' THEN
           v_f_envio := TO_CHAR(sysdate, 'YYYYMMDD');
        END IF;
      
        v_cod_oficina := lpad(v_cod_oficina, 4, '0');

			  ---------------------------------------------
			  -- Escribimos regitros tipo '01'
			  ---------------------------------------------

				l_line01 := 	'01' ||
							v_cod_entidad ||
							rpad(v_f_envio, 8) ||
							rpad(v_acuerdo_origen, 20) || -- Son 20 de longitud en total, de momento el resto blancos
							rpad('0', 10, '0') ||
							v_cod_oficina ||
							'05' ||
							'83' ||
							v_cod_agricola_ganado || -- 1001 o 1002
							'001' ||
							'000000000' ||
							rpad(v_nifcif, 14) ||
							v_tipo_identificacion_codigo || --01, 51 o 53
							'978' ||
							rpad(' ', 3) ||
							'7' || -- Valor 7 para las CANCELACIONES
							'B' || -- Correspondiente a Bajas 
							rpad(' ', 8) || -- Literal usuario
							rpad(' ', 8) || -- Codigo usuario
							rpad(' ',142);

        utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line01||utl_Tcp.crlf));

				l_num_lineas := l_num_lineas + 1;
        
    	  -- Actualizamos el campo fecha_envio_iris con la fecha actual de la poliza que estamos tratando
				UPDATE TB_POLIZAS
					 SET FECHA_ENV_CANC_IRIS = sysdate
				WHERE idpoliza = v_id_poliza;


			END LOOP;
      
      -- Realizamos la consulta de las pólizas Vencidas y las añadimos al fichero
      -- -------------------------------------------------------------------------
      pq_utl.log(lc, 'Entramos a recuperar las Cancelaciones (Vencimientos)');
      
      -- Crea el cursor para la consulta
			pq_utl.log(lc, 'Abre el cursor para la query de Cancelaciones (Vencimientos)',2);
      
      l_query_venc:= crearQuery('V');

			OPEN l_tp_cursor FOR l_query_venc;

			-- Bucle para recorrer todos los registros del cursor
      pq_utl.log(lc, 'Antes de leer el cursor');
			LOOP

				-- Vuelca los datos del cursor en las variables indicadas
		
				FETCH l_tp_cursor
					INTO
						v_id_poliza,
						v_cod_entidad,
						v_f_envio,
						v_acuerdo_origen,
						v_cod_oficina,
						v_nifcif,
						v_tipo_identificacion,
						v_cod_linea,
						v_nom_linea,
						v_nom_apellidos,
						v_direccion,
						v_nom_localidad,
            v_telefono_fijo_asegurado,
		        v_telefono_movil_asegurado,
            v_nifcif_tomador,
            v_razon_social_tomador,
            v_domicilio_tomador,
            v_telefono_fijo_tomador,
            v_telefono_movil_tomador,
            v_codmodulo;
            
				IF (l_tp_cursor%NOTFOUND) THEN

					-- Si el numero de polizas tratadas es cero se lanza la excepcion correspondiente
					IF (l_num_cancel = 0) THEN
						pq_utl.log(lc, 'No se han encontrado polizas Vencidas');
						RAISE no_polizas_found;
					ELSE
						pq_utl.log(lc, 'Valor de fecha envio - ' || v_f_envio);
					END IF;
          
					pq_utl.log(lc, 'No hay mas Anulaciones que enviar.');

					-- Sale del bucle
					EXIT;
				END IF;
        
				---------------------------------------------
			  -- Escribimos primer registro, tipo '00' -> Si se han encontrado datos. Sino no se escribe ni el primer registro
			  ---------------------------------------------
        if l_num_lineas = 0 THEN 
   		  	l_line00 := '00' ||
	   			 			      'XXXXXXX' ||
		   					      '9996' ||
			   				      TO_CHAR(sysdate, 'DDMMYYYY') ||
				   			      RPAD(' ', 229);

   			  l_num_lineas := l_num_lineas + 1;

          utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line00||utl_Tcp.crlf));
        
        END IF;   
        
        -- Por cada registro recuperamos la fecha de Vencimiento conforme a los valores que recuperemos de la 
        -- tabla de parametrización de meses por línea/codmodulo
        
        -- MODIF (27/03/2020) : Inicio
        -- RGA indica que la fecha de vencimiento se calcula en base a la fecha envio a Agroseguro
        -- y no a la fecha envío a IRIS. Lo cambiamos.
        --v_vencida := comprobar_vencimiento(v_fecha_env_iris, v_cod_linea, v_codmodulo );
        
        v_f_envio_aux := TO_DATE (v_f_envio, 'DDMMYYYY');
        v_vencida := comprobar_vencimiento(v_f_envio_aux, v_cod_linea, v_codmodulo, v_id_poliza );
        -- MODIF (27/03/2020) ** Fin 
        
        IF (v_vencida = true) THEN
        
           -- Actualiza el contador de polizas, cada vez que entramos al bucle es que tenemos una poliza mas
           l_num_cancel := l_num_cancel + 1;
           l_num_venc := l_num_venc + 1;

           -- Damos valor a variables dependientes------
			     ---------------------------------------------
			     IF (v_tipo_identificacion = 'NIF') THEN
				      v_tipo_identificacion_codigo := '51';
           END IF;

			     IF (v_tipo_identificacion = 'CIF') THEN
				      v_tipo_identificacion_codigo := '01';
           END IF;

			     IF (v_tipo_identificacion = 'NIE') THEN
				      v_tipo_identificacion_codigo := '53';
           END IF;
      
 			     IF (v_cod_linea >= 300 AND v_cod_linea < 400)	THEN
			        v_cod_agricola_ganado := '1001';
           END IF;

			     IF (v_cod_linea >= 400 AND v_cod_linea < 500)	THEN
			        v_cod_agricola_ganado := '1002';
           END IF;

           IF v_f_envio IS NULL THEN
              v_f_envio := rpad(' ',8);
           END IF;   
      
           IF v_f_envio = '        ' THEN
              v_f_envio := TO_CHAR(sysdate, 'YYYYMMDD');
           END IF;
      
           v_cod_oficina := lpad(v_cod_oficina, 4, '0');

			     ---------------------------------------------
			     -- Escribimos regitros tipo '01'
			     ---------------------------------------------

				   l_line01 := 	'01' ||
							v_cod_entidad ||
							rpad(v_f_envio, 8) ||
							rpad(v_acuerdo_origen, 20) || -- Son 20 de longitud en total, de momento el resto blancos
							rpad('0', 10, '0') ||
							v_cod_oficina ||
							'05' ||
							'83' ||
							v_cod_agricola_ganado || -- 1001 o 1002
							'001' ||
							'000000000' ||
							rpad(v_nifcif, 14) ||
							v_tipo_identificacion_codigo || --01, 51 o 53
							'978' ||
							rpad(' ', 3) ||
							'7' || -- Valor 7 para las CANCELACIONES
							'B' || -- Correspondiente a Bajas 
							rpad(' ', 8) || -- Literal usuario
							rpad(' ', 8) || -- Codigo usuario
							rpad(' ',142);

           utl_file.put_raw(f_fichero, utl_raw.cast_to_raw(l_line01||utl_Tcp.crlf));

				   l_num_lineas := l_num_lineas + 1;
        
    	     -- Actualizamos el campo fecha_envio_iris con la fecha actual de la poliza que estamos tratando0
				   UPDATE TB_POLIZAS
					    SET FECHA_ENV_CANC_IRIS = sysdate
				   WHERE idpoliza = v_id_poliza; 
        --ELSE
           --pq_utl.log(lc, 'La póliza ' || v_id_poliza || ' no está vencida.'); 
        END IF;
        
				END LOOP;
        
      -- Guardamos el nº de cancelaciones enviadas
      UPDATE TB_CONFIG_AGP C
         SET C.AGP_VALOR = l_num_cancel
      WHERE C.AGP_NEMO = nom_agp_cancel_env;
      
      
      -- Pet. 55722 ** MODIF TAM (03.02.2020) ** FIN **     
      
      COMMIT; 
      
 			---------------------------------------------
			-- Escribimos ultimo registro, tipo '99'
			---------------------------------------------
      l_num_lineas := l_num_lineas + 1;
      
			l_line99 := 	'99' ||
								lpad(l_num_lineas, 7, '0') ||
								rpad(' ',241);
                
      -- Inserta en el fichero de envio la linea '99'
			--pq_utl.log(lc, 'Escribe la linea "99" en el fichero de envio.');
      
      /****/
      /*utl_file.put(f_fichero, l_line99);*/
      utl_file.put_raw(f_fichero,utl_raw.cast_to_raw(l_line99));
      /*****/
      
      -- Cierra el cursor
		  pq_utl.log(lc, 'Cierra el cursor.');
		  CLOSE l_tp_cursor;
      
      -- Cierra el fichero
      pq_utl.log(lc, 'Cierra el fichero de envio.');

		  utl_file.fclose(f_fichero);
      
      -- Realizamos una copia del fichero generado para guardarlo a modo de historico
		  -- ya que el fichero 'POLIZAS_RC.TXT' se generara cada vez.
		  pq_utl.log(lc, 'Se copia el fichero generado en :' || l_dir_name || ' con nombre: ' || l_nombre || to_char(SYSDATE, 'YYMMDDHH24MISS') || '.');
		  utl_file.fcopy(l_dir, l_nombre || '.TXT', l_dir, l_nombre || to_char(SYSDATE, 'YYMMDDHH24MISS') || '.TXT');
      
      PQ_Utl.LOG(lc, '');
	    PQ_Utl.LOG(lc, '*********************************************************************************', 2);
 	    PQ_Utl.LOG(lc, 'FICHERO DE ENVIO PÓLIZAS AGRO PARA INTEGRAR EN IRIS', 2);
      PQ_Utl.LOG(lc, '*********************************************************************************', 2);
	    PQ_Utl.LOG(lc, 'ESTADISTICAS DEL FICHERO EN FECHA ' || TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI:SS'), 2);
	    PQ_Utl.LOG(lc, '*********************************************************************************', 2);
	    PQ_Utl.LOG(lc, 'Registros procesados                              := ' || l_num_polizas, 2);   
      PQ_Utl.LOG(lc, 'Cancelaciones procesadas                          := ' || l_num_cancel, 2);   
      PQ_Utl.LOG(lc, '      - Anulaciones procesadas                    := ' || l_num_anul, 2);   
      PQ_Utl.LOG(lc, '      - Vencimientos procesados                   := ' || l_num_venc, 2);   
	    PQ_Utl.LOG(lc, '*********************************************************************************', 2);
	    PQ_Utl.LOG(lc, '', 2);

		  pq_utl.log(lc, 'El proceso ha finalizado correctamente a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
		  pq_utl.log(lc, '## FIN PROCEDIMIENTO PQ_ENVIO_POLIZAS_AGRO_IRIS ##', 1);
      
      RETURN 'Se han encontrado ' || l_num_polizas || ' polizas.';
     
		-- Control de excepciones
	EXCEPTION
		WHEN no_polizas_found THEN
			-- Rollback por si se ha hecho algun update
			ROLLBACK;
			-- Se cierra el cursor y el fichero de envio
			CLOSE l_tp_cursor;
			utl_file.fclose(f_fichero);
			-- Se escribe en el log el error

		RETURN 'Se han encontrado ' || l_num_polizas || ' polizas definitivas.';

		WHEN OTHERS THEN
			ROLLBACK;
      DBMS_OUTPUT.PUT_LINE(SQLERRM);
      pq_utl.log(lc,SQLERRM);
			-- Se escribe en el log el error
			pq_utl.log(lc, 'El proceso ha finalizado CON ERRORES a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			pq_utl.log(lc, '## FIN PROCEDIMIENTO PQ_ENVIO_POLIZAS_AGRO_IRIS ##', 1);
			pq_err.raiser(SQLCODE, 'Error al generar los ficheros de generacion_fichero_envio_iris' || ' [' || SQLERRM || ']');
			-- Se vuelve a lanzar la excepcion para parar la cadena
			--RAISE;
      RETURN NULL;
      
	END;
	-- Fin del cuerpo de la funcion
  
    
  ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION print_log_completo
	--
	-- Escribe en el log la cadena pasa por parametro independientemente de la longitud de esta
	--
	---------------------------------------------------------------------------------------------------------------------------
	--
	-- Inicio de declaracion de funcion
	FUNCTION print_log_completo(str IN VARCHAR2) RETURN VARCHAR2 IS
		-- Variables
		str_aux VARCHAR2(10000); -- Auxiliar usada para pintar cadena en el log
	BEGIN

		str_aux := str;

		LOOP
			-- Si el tama?o de la cadena es mayor que el que permite el metodo de log, se parte y se escribe por trozos
			IF (str_aux IS NOT NULL AND length(str_aux) > 994) THEN
				pq_utl.log(null, substr(str_aux, 1, 994), 2);
				str_aux := substr(str_aux, 994);
				-- Si el tama?o de la cadena es menor, se pinta y se sale del bucle
			ELSE
				pq_utl.log(null, str_aux, 2);
				EXIT;
			END IF;
		END LOOP;

		RETURN 'OK';

		-- Control de excepciones
	EXCEPTION
		-- Si ocurre cualquier excepcion se pinta la cadena en el log directamente
		WHEN OTHERS THEN
			pq_utl.log(null, str, 2);
			RETURN 'KO';
	END;
	-- Fin del cuerpo de la funcion


	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION crearQuery
	--
	-- Devuelve la consulta que obtiene formateados todos los campos del R.C Ganado que hay que incluir en el fichero
	-- Obtenemos como parametro el valor tipo, que nos indicará si la where se tiene que crear para recuperar las 
  -- Polizas normales o las Cancelaciones (Anulaciones o Vencimientos).
  ---- Tipo = 'P' -> Alta de pólizas en estado emitidas o enviadas correctas
  ---- Tipo = 'A' -> Polizas Ánuladas (en estado Anulada), con fecha de anulación posterior a la fecha de envío a IRIS y aceptadas
  --                 por IRIS
  ---- Tipo = 'V' -> Polizas Vencidas(en estado Emitida o Enviada Correcta pero con vencimiento posterior al envio a IRIS

  ---------------------------------------------------------------------------------------------------------------------------
	FUNCTION crearQuery (v_tipo in VARCHAR2) RETURN VARCHAR2 IS

		lc VARCHAR2(50) := 'pq_envio_polizas_agro_iris.crearQuery'; -- Variable que almacena el nombre del paquete y de la funcion

		-- Variables para componer la consulta para obtener las polizas de RC Ganado a enviar
		l_query             VARCHAR2(12500);
		l_query_select      VARCHAR2(4000);
		l_query_from        VARCHAR2(4000);
    l_query_where              VARCHAR2(6000);
		l_query_order       VARCHAR2(3000);
		l_query_groupby     VARCHAR2(5000);
    
		BEGIN

			--pq_utl.log(lc, 'Monta la query para recuperar las polizas enviadas y emitidas ', 2);

      IF (v_tipo = 'V') THEN
         l_query_select := 	'SELECT 
                   P.IDPOLIZA,
                   AG.CODENTIDAD,
								   TO_CHAR(P.FECHAENVIO, ''DDMMYYYY''),
								   LI.CODPLAN || ''/'' || P.TIPOREF || ''/'' || P.REFERENCIA  || P.DC,
								   P.OFICINA,
								   AG.NIFCIF,
								   AG.TIPOIDENTIFICACION,
								   LI.CODLINEA,
								   LI.NOMLINEA,
								   AG.NOMBRE || '' '' || AG.APELLIDO1 || '' '' || AG.APELLIDO2,
								   AG.CLAVEVIA || '' '' || AG.DIRECCION || '', '' || AG.NUMVIA,
								   LO.NOMLOCALIDAD,
                   AG.TELEFONO,
                   AG.MOVIL,
                   TM.CIFTOMADOR,
                   TM.RAZONSOCIAL,
                   TM.CLAVEVIA || '' '' || TM.DOMICILIO || '', '' || TM.NUMVIA,
                   TM.TELEFONO,
                   TM.MOVIL,
                   P.CODMODULO';

                   
         l_query_groupby := 'group by  
                   P.IDPOLIZA,
                   AG.CODENTIDAD,
								   TO_CHAR(P.FECHAENVIO, ''DDMMYYYY''),
								   LI.CODPLAN || ''/'' || P.TIPOREF || ''/'' || P.REFERENCIA  || P.DC,
								   P.OFICINA,
								   AG.NIFCIF,
								   AG.TIPOIDENTIFICACION,
								   LI.CODLINEA,
								   LI.NOMLINEA,
								   AG.NOMBRE || '' '' || AG.APELLIDO1 || '' '' || AG.APELLIDO2,
								   AG.CLAVEVIA || '' '' || AG.DIRECCION || '', '' || AG.NUMVIA,
								   LO.NOMLOCALIDAD,
                   AG.TELEFONO,
                   AG.MOVIL,
                   TM.CIFTOMADOR,
                   TM.RAZONSOCIAL,
                   TM.CLAVEVIA || '' '' || TM.DOMICILIO || '', '' || TM.NUMVIA,
                   TM.TELEFONO,
                   TM.MOVIL, 
                   P.CODMODULO';
                   
      ELSE
			   l_query_select := 	'SELECT 
                   P.IDPOLIZA,
                   AG.CODENTIDAD,
								   TO_CHAR(P.FECHAENVIO, ''DDMMYYYY''),
								   LI.CODPLAN || ''/'' || P.TIPOREF || ''/'' || P.REFERENCIA  || P.DC,
								   P.OFICINA,
								   AG.NIFCIF,
								   AG.TIPOIDENTIFICACION,
								   LI.CODLINEA,
								   LI.NOMLINEA,
								   AG.NOMBRE || '' '' || AG.APELLIDO1 || '' '' || AG.APELLIDO2,
								   AG.CLAVEVIA || '' '' || AG.DIRECCION || '', '' || AG.NUMVIA,
								   LO.NOMLOCALIDAD,
                   AG.TELEFONO,
                   AG.MOVIL,
                   TM.CIFTOMADOR,
                   TM.RAZONSOCIAL,
                   TM.CLAVEVIA || '' '' || TM.DOMICILIO || '', '' || TM.NUMVIA,
                   TM.TELEFONO,
                   TM.MOVIL,
				   P.CODUSUARIO';
                   
      l_query_groupby := 'group by  
                   P.IDPOLIZA,
                   AG.CODENTIDAD,
								   TO_CHAR(P.FECHAENVIO, ''DDMMYYYY''),
								   LI.CODPLAN || ''/'' || P.TIPOREF || ''/'' || P.REFERENCIA  || P.DC,
								   P.OFICINA,
								   AG.NIFCIF,
								   AG.TIPOIDENTIFICACION,
								   LI.CODLINEA,
								   LI.NOMLINEA,
								   AG.NOMBRE || '' '' || AG.APELLIDO1 || '' '' || AG.APELLIDO2,
								   AG.CLAVEVIA || '' '' || AG.DIRECCION || '', '' || AG.NUMVIA,
								   LO.NOMLOCALIDAD,
                   AG.TELEFONO,
                   AG.MOVIL,
                   TM.CIFTOMADOR,
                   TM.RAZONSOCIAL,
                   TM.CLAVEVIA || '' '' || TM.DOMICILIO || '', '' || TM.NUMVIA,
                   TM.TELEFONO,
                   TM.MOVIL, 
				   P.CODUSUARIO';
      END IF;
      
      -- Pet. 55722 ** MODIF TAM (03.02.2020) 
      IF (v_tipo = 'A') THEN  
         l_query_from := ' 	FROM 	o02agpe0.TB_POLIZAS      P,
                                  o02agpe0.TB_POLIZAS_HISTORICO_ESTADOS HI,
				  						            o02agpe0.TB_ASEGURADOS   AG,
					  					            o02agpe0.TB_LINEAS  	 LI,
						  				            o02agpe0.TB_LOCALIDADES  LO,
                                  o02agpe0.TB_COLECTIVOS  CO,
                                  o02agpe0.TB_TOMADORES  TM	';
      ELSE
       
			   l_query_from := ' 	FROM 	o02agpe0.TB_POLIZAS      P,
				  						            o02agpe0.TB_ASEGURADOS   AG,
					  					            o02agpe0.TB_LINEAS  	 LI,
						  				            o02agpe0.TB_LOCALIDADES  LO,
                                  o02agpe0.TB_COLECTIVOS  CO,
                                  o02agpe0.TB_TOMADORES  TM	';
      END IF;                            

			l_query_order := ' ORDER BY IDPOLIZA ';
                  
 			-- Alta de Pólizas 
      IF (v_tipo = 'P') THEN
 			   l_query_where:= crearWhere;     
      END IF;
      
      -- Pólizas Anulaas
      IF (v_tipo = 'A') THEN   
         l_query_where := crearWhereAnul;
      END IF;
      
      -- Pólizas Vencidas
      IF (v_tipo ='V') THEN
         l_query_where := crearWhereVenc;
      END IF;          
      
			l_query:= -- Select para polizas principales
				l_query_select ||
				-- From para polizas principales
				l_query_from ||
				-- Where 
        l_query_where ||
				 -- Group by para polizas principales
				l_query_groupby ||
				 -- Order general
				l_query_order;

				-- Si la query es mas larga que lo que permite el metodo de log, se mete en bucle para escribirla completamente
				pq_utl.log(lc, 'Query completa en log -> ' || (print_log_completo(l_query)));

				RETURN l_query;

		END;
    
  --------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION crearWhere
	--
	-- Devuelve el string correspondiente al Where que se monta
	--
    ---------------------------------------------------------------------------------------------------------------------------
	FUNCTION crearWhere RETURN VARCHAR2 IS

			-- Variables para componer la consulta para obtener las polizas de RC Ganado a enviar
		l_where             VARCHAR2(6000);
	  l_where_entidades   VARCHAR2(5000);
   	
    l_codigo_iris_ok    VARCHAR(5); -- Variable que almacena el codigo de OK de envio a IRIS
    l_iris_agro_entidad VARCHAR2(2000);
    
   
    --l_where_pruebas     VARCHAR2(1000);
    l_entidad_med       VARCHAR(4);
    l_subent_med        VARCHAR(4);
    v_array_ent_sub     t_array;
    v_array_entidad     t_array;
    l_linea_array       VARCHAR(9);
    --v_array_entidad     t_array;    

		BEGIN

      -- Obtenemos el valor de codigo de ok para IRIS de TB_COFIG_AGP
      SELECT con.agp_valor 
        into l_codigo_iris_ok
        FROM o02agpe0.TB_CONFIG_AGP con
       WHERE con.agp_nemo = 'ESTADO_IRIS_OK';
      
      -- Obtenmos las Entidades 
      -- Obtenemos el valor de codigo de ok para IRIS de TB_COFIG_AGP
      SELECT conf.agp_valor 
        into l_iris_agro_entidad
        FROM o02agpe0.TB_CONFIG_AGP conf
       WHERE conf.agp_nemo = 'AGRO_IRIS_ENTIDAD';
       
      -- Obtenemos las E-S Mediadoras parametrizadas por tablas
       v_array_ent_sub := SPLIT(l_iris_agro_entidad,';');
       
       IF (v_array_ent_sub IS NOT NULL AND v_array_ent_sub.count > 0) THEN
         FOR i IN v_array_ent_sub.FIRST..v_array_ent_sub.LAST
         LOOP
             l_linea_array := v_array_ent_sub(i);
 
             v_array_entidad := SPLIT(l_linea_array,'-');
             l_entidad_med :=v_array_entidad(1);
             l_subent_med :=v_array_entidad(2);
             IF (l_entidad_med IS NOT NULL AND l_subent_med IS NOT NULL) THEN
                 IF i = 1 THEN
                    l_where_entidades := l_where_entidades || ' AND ( (CO.ENTMEDIADORA = ' || l_entidad_med || ' AND CO.SUBENTMEDIADORA = ' || l_subent_med || ') ';
                 ELSE
                    l_where_entidades := l_where_entidades || 'OR (CO.ENTMEDIADORA = ' || l_entidad_med || ' AND CO.SUBENTMEDIADORA = ' || l_subent_med || ') ';
                 END IF;   
             END IF;
         END LOOP;
         
         l_where_entidades := l_where_entidades || ') ';
      END IF;
       
			l_where := 	' WHERE P.IDASEGURADO = AG.ID
										  AND P.LINEASEGUROID = LI.LINEASEGUROID
										  AND	LO.CODLOCALIDAD = AG.CODLOCALIDAD
										  AND AG.CODPROVINCIA = LO.CODPROVINCIA
										  AND AG.SUBLOCALIDAD = LO.SUBLOCALIDAD
                      AND P.IDCOLECTIVO = CO.ID
                      AND CO.CIFTOMADOR = TM.CIFTOMADOR' ||
                   l_where_entidades ||
                   ' AND P.IDESTADO IN  (8, 14) ' ||
                   ' AND ( P.ESTADO_IRIS != ''' || l_codigo_iris_ok || ''' OR P.FECHA_ENVIO_IRIS IS NULL ) ' ;
                   
                   
			RETURN l_where;

		END;
    
    -- **** INICIO ** PETICIÓN 55722 - PTC-6273 ** MODIF TAM (03.02.2020) ****
    ---------------------------------------------------------------------------------------------------------------------------
    ---------------------------------------------------------------------------------------------------------------------------
  	-- FUNCTION crearWhereAnul
	  --
  	-- Devuelve el string correspondiente al Where que se monta para obtener las pólizas Anuladas que previamente se han enviado
    -- a IRIS y fueron aceptadas, o cuya anulación ya ha sido anulada y no se ha aceptado.
  	--
    ---------------------------------------------------------------------------------------------------------------------------
	  FUNCTION crearWhereAnul RETURN VARCHAR2 IS

   		 -- Variables para componer la consulta para obtener las polizas de RC Ganado a enviar
		   l_where             VARCHAR2(6000);
	     l_where_entidades   VARCHAR2(5000);
   	
       l_codigo_iris_ok    VARCHAR(5); -- Variable que almacena el codigo de OK de envio a IRIS
       l_iris_agro_entidad VARCHAR2(2000);
    
   
       --l_where_pruebas     VARCHAR2(1000);
       l_entidad_med       VARCHAR(4);
       l_subent_med        VARCHAR(4);
       v_array_ent_sub     t_array;
       v_array_entidad     t_array;
       l_linea_array       VARCHAR(9);
       --v_array_entidad     t_array;    

		BEGIN

      -- Obtenemos el valor de codigo de ok para IRIS de TB_COFIG_AGP
      SELECT con.agp_valor 
        into l_codigo_iris_ok
        FROM o02agpe0.TB_CONFIG_AGP con
       WHERE con.agp_nemo = 'ESTADO_IRIS_OK';
      
      -- Obtenmos las Entidades 
      -- Obtenemos el valor de codigo de ok para IRIS de TB_COFIG_AGP
      SELECT conf.agp_valor 
        into l_iris_agro_entidad
        FROM o02agpe0.TB_CONFIG_AGP conf
       WHERE conf.agp_nemo = 'AGRO_IRIS_ENTIDAD';
       
      -- Obtenemos las E-S Mediadoras parametrizadas por tablas
       v_array_ent_sub := SPLIT(l_iris_agro_entidad,';');
       
       IF (v_array_ent_sub IS NOT NULL AND v_array_ent_sub.count > 0) THEN
         FOR i IN v_array_ent_sub.FIRST..v_array_ent_sub.LAST
         LOOP
             l_linea_array := v_array_ent_sub(i);
 
             v_array_entidad := SPLIT(l_linea_array,'-');
             l_entidad_med :=v_array_entidad(1);
             l_subent_med :=v_array_entidad(2);
             IF (l_entidad_med IS NOT NULL AND l_subent_med IS NOT NULL) THEN
                 IF i = 1 THEN
                    l_where_entidades := l_where_entidades || ' AND ( (CO.ENTMEDIADORA = ' || l_entidad_med || ' AND CO.SUBENTMEDIADORA = ' || l_subent_med || ') ';
                 ELSE
                    l_where_entidades := l_where_entidades || 'OR (CO.ENTMEDIADORA = ' || l_entidad_med || ' AND CO.SUBENTMEDIADORA = ' || l_subent_med || ') ';
                 END IF;   
             END IF;
         END LOOP;
         
         l_where_entidades := l_where_entidades || ') ';
      END IF;
       
			l_where := 	' WHERE P.IDASEGURADO = AG.ID
										  AND P.LINEASEGUROID = LI.LINEASEGUROID
										  AND	LO.CODLOCALIDAD = AG.CODLOCALIDAD
										  AND AG.CODPROVINCIA = LO.CODPROVINCIA
										  AND AG.SUBLOCALIDAD = LO.SUBLOCALIDAD
                      AND P.IDCOLECTIVO = CO.ID
                      AND CO.CIFTOMADOR = TM.CIFTOMADOR
                      AND P.IDPOLIZA = HI.IDPOLIZA' ||
                   l_where_entidades ||
                   ' AND P.IDESTADO IN  (16) ' ||
                   ' AND ( (P.ESTADO_IRIS = ''' || l_codigo_iris_ok || ''' AND P.FECHA_ENVIO_IRIS IS NOT NULL )
                          OR (P.ESTADO_CANC_IRIS != ''' || l_codigo_iris_ok || ''' AND P.FECHA_ENV_CANC_IRIS IS NOT NULL) ) 
                     AND HI.ESTADO IN (16) 
                     AND HI.FECHA > P.FECHA_ENVIO_IRIS ' ;
                   
                   
			RETURN l_where;

		END;
    
       ---------------------------------------------------------------------------------------------------------------------------
    ---------------------------------------------------------------------------------------------------------------------------
  	-- FUNCTION crearWhereVenc
	  --
  	-- Devuelve el string correspondiente al Where que se monta para obtener las pólizas Vencidas con posterioridad al envío a
    -- a IRIS y fueron aceptadas, o cuyo Vencimiento ya ha sido anulada y no se ha aceptado.
  	--
    ---------------------------------------------------------------------------------------------------------------------------
	  FUNCTION crearWhereVenc RETURN VARCHAR2 IS

		lc VARCHAR2(50) := 'pq_envio_polizas_agro_iris.crearWhereVenc'; -- Variable que almacena el nombre del paquete y de la funcion

		-- Variables para componer la consulta para obtener las polizas de RC Ganado a enviar
		l_where             VARCHAR2(6000);
	  l_where_entidades   VARCHAR2(5000);
   	
    l_codigo_iris_ok    VARCHAR(5); -- Variable que almacena el codigo de OK de envio a IRIS
    l_iris_agro_entidad VARCHAR2(2000);
    
   
    --l_where_pruebas     VARCHAR2(1000);
    l_entidad_med       VARCHAR(4);
    l_subent_med        VARCHAR(4);
    v_array_ent_sub     t_array;
    v_array_entidad     t_array;
    l_linea_array       VARCHAR(9);
    --v_array_entidad     t_array;    

		BEGIN

      -- Obtenemos el valor de codigo de ok para IRIS de TB_COFIG_AGP
      SELECT con.agp_valor 
        into l_codigo_iris_ok
        FROM o02agpe0.TB_CONFIG_AGP con
       WHERE con.agp_nemo = 'ESTADO_IRIS_OK';
      
      -- Obtenmos las Entidades 
      -- Obtenemos el valor de codigo de ok para IRIS de TB_COFIG_AGP
      SELECT conf.agp_valor 
        into l_iris_agro_entidad
        FROM o02agpe0.TB_CONFIG_AGP conf
       WHERE conf.agp_nemo = 'AGRO_IRIS_ENTIDAD';
       
      -- Obtenemos las E-S Mediadoras parametrizadas por tablas
       v_array_ent_sub := SPLIT(l_iris_agro_entidad,';');
       
       IF (v_array_ent_sub IS NOT NULL AND v_array_ent_sub.count > 0) THEN
         FOR i IN v_array_ent_sub.FIRST..v_array_ent_sub.LAST
         LOOP
             l_linea_array := v_array_ent_sub(i);
 
             v_array_entidad := SPLIT(l_linea_array,'-');
             l_entidad_med :=v_array_entidad(1);
             l_subent_med :=v_array_entidad(2);
             IF (l_entidad_med IS NOT NULL AND l_subent_med IS NOT NULL) THEN
                 IF i = 1 THEN
                    l_where_entidades := l_where_entidades || ' AND ( (CO.ENTMEDIADORA = ' || l_entidad_med || ' AND CO.SUBENTMEDIADORA = ' || l_subent_med || ') ';
                 ELSE
                    l_where_entidades := l_where_entidades || 'OR (CO.ENTMEDIADORA = ' || l_entidad_med || ' AND CO.SUBENTMEDIADORA = ' || l_subent_med || ') ';
                 END IF;   
             END IF;
         END LOOP;
         
         l_where_entidades := l_where_entidades || ') ';
      END IF;
       
			l_where := 	' WHERE P.IDASEGURADO = AG.ID
										  AND P.LINEASEGUROID = LI.LINEASEGUROID
										  AND	LO.CODLOCALIDAD = AG.CODLOCALIDAD
										  AND AG.CODPROVINCIA = LO.CODPROVINCIA
										  AND AG.SUBLOCALIDAD = LO.SUBLOCALIDAD
                      AND P.IDCOLECTIVO = CO.ID
                      AND CO.CIFTOMADOR = TM.CIFTOMADOR' ||
                   l_where_entidades ||
                   ' AND P.IDESTADO IN  (8, 14) ' ||
                   ' AND  (P.ESTADO_IRIS = ''' || l_codigo_iris_ok || ''' AND P.FECHA_ENVIO_IRIS IS NOT NULL )'||
                   ' AND ( (P.FECHA_ENV_CANC_IRIS is null) OR (P.ESTADO_CANC_IRIS != ''' || l_codigo_iris_ok || ''' AND P.FECHA_ENV_CANC_IRIS IS NOT NULL) ' ||
                   ' OR (P.ESTADO_CANC_IRIS is null AND P.FECHA_ENV_CANC_IRIS IS NOT NULL) ) ' ;
                   
                   
                   
			RETURN l_where;

		END;
    
    ---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION comprobar_vencimiento
	--
	--  ---------------------------------------------------------------------------------------------------------------------------
	FUNCTION comprobar_vencimiento (v_fecha_envio in DATE, v_cod_linea in VARCHAR2, v_codmodulo in VARCHAR2, v_id_poliza in VARCHAR2) RETURN BOOLEAN IS
  
      v_vencimiento          BOOLEAN := false;
      v_meses                NUMBER(5);
      v_fecha_vencida        VARCHAR2(10) := '';
      d_fecha_vencida        DATE;
      lc VARCHAR2(50) := 'pq_envio_polizas_agro_iris.comprobar_vencimiento'; -- Variable que almacena el nombre del paquete y de la funcion
      
   BEGIN
   
      /* Pet. 67752 ** MODIF TAM (26/08/2021) ** Inicio */
      /* Primero se comprueba si la fecha de Vencimiento está correctamente informada en el nuevo campo de la tabla de polizas*/
      BEGIN 
         select TO_CHAR(PO.FECHA_VTO, 'YYYY/MM/DD')
           into v_fecha_vencida
           from o02agpe0.tb_polizas PO
          where PO.IDPOLIZA = v_id_poliza;
            
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
      
      /* Si recuperamos la fecha de vencimiento de la póliza con valor nulo, obtenemos la fecha de vencimiento como hasta ahora */
      IF (v_fecha_vencida = '' or v_fecha_vencida is NULL) THEN
      /* Pet. 67752 ** MODIF TAM (26/08/2021) ** Fin */

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
        
         IF (TO_CHAR(sysdate,'YYYY/MM/DD') > v_fecha_vencida ) THEN
           v_vencimiento := true;
         ELSE
           v_vencimiento := false;   
         END IF;   
 
     /* Sino comprobamos el vencimiento con el valor obtenido en la tabla de pólizas */        
     ELSE
       IF (TO_CHAR(sysdate,'YYYY/MM/DD') > v_fecha_vencida ) THEN
          v_vencimiento := true;
       ELSE
          v_vencimiento := false;   
       END IF; 
     END IF; 
     
     RETURN v_vencimiento;

    END;
    -- **** FIN PETICIÓN 55722 - PTC-6273 ** MODIF TAM (03.02.2020) ****
    

    ---------------------------------------------------------------------------------------------------------------------------
  	---------------------------------------------------------------------------------------------------------------------------
  	-- PROCEDURE actualizaVarConfig
  	--
  	-- Actualiza las variables de configuracion correspondientes con el numero de polizas
    -- de envio IRIS procesadas desde la ultima ejecucion del batch de envio
    --
    PROCEDURE actualizaVarConfig AS
  
    lc                           VARCHAR2(50) := 'pq_envio_polizas_agro_iris.actualizaVarConfig'; -- Variable que almacena el nombre del paquete y de la funcion
    query                        VARCHAR2(7000); 
    -- Almacena el numero de polizas IRIS que se enviaran
    v_num_plz_iris               NUMBER(8);
    -- Nombre de la variable de configuracion que almacena el n? de polizas IRIS
    nom_var_config	             VARCHAR2(18) := 'POLIZAS_IRIS_AGRO';
    l_where                      VARCHAR2(6000);
    
    BEGIN
    
     l_where := crearWhere;                   
    
      query := 'SELECT count(*) 
                FROM 	o02agpe0.TB_POLIZAS                P,
										            o02agpe0.TB_ASEGURADOS   AG,
										            o02agpe0.TB_LINEAS  	   LI,
										            o02agpe0.TB_LOCALIDADES  LO,
                                o02agpe0.TB_COLECTIVOS   CO,
                                o02agpe0.TB_TOMADORES    TM	';
      query := query || l_where;
      
     EXECUTE IMMEDIATE query INTO v_num_plz_iris;
     
     PQ_Utl.LOG(lc, 'Valor TOTAL DE Nº de POLIZAS ' || v_num_plz_iris, 2);
             
     --************************
    
      -- Actualiza la variable de configuracion correspondiente al numero de polizas Iris procesadas
      UPDATE tb_config_agp
         SET AGP_VALOR = v_num_plz_iris 
       WHERE agp_nemo = nom_var_config;
      COMMIT;
    pq_utl.log(lc, 'Actualizada la variable de configuracion ' || nom_var_config || ' a ' || v_num_plz_iris, 2);

  END;
  
  -- Nueva función para obtener las E-S Mediadoras parametrizables en tablas
  
  FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array IS
    i       number := 0;
    pos     number := 0;
    lv_str  varchar2(2000) := in_string;
    strings t_array;
  BEGIN

    pos := instr(lv_str, delim, 1, 1);
    WHILE (pos != 0) LOOP
      i := i + 1;
      strings(i) := substr(lv_str, 1, pos-1);
      lv_str := substr(lv_str, pos + 1, length(lv_str));
      pos := instr(lv_str, delim, 1, 1);
      If pos = 0 THEN
        strings(i + 1) := lv_str;
      END IF;
    END LOOP;

    RETURN strings;
  END SPLIT;

 END PQ_ENVIO_POLIZAS_AGRO_IRIS;
/
SHOW ERRORS;