#!/bin/ksh

#########################################################
## Recepción de Acuses de Recibo de Agroseguro         ##
##                                                     ##
## Si se ha realizado el envío de pólizas por          ##
## comunicaciones, se recibirá el acuse de recibo,     ##
## según el documento 'DES-N-0042'.                    ##
## Según los datos recibidos en el acuse de recibo,    ##
## se actualizará el estado de póliza, bien a 'enviada ##
## correcta' o a 'enviada errónea'. Además se          ##
## actualizará la entidad comunicaciones de póliza     ##
## con el movimiento de acuse de recibo y el resultado ##
## (correcto o error).                                 ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       02/10/2010 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

## Función para tratar los acuses de recibo. 
## Recibe los siguientes parámetros:
## 	- El nombre del fichero TXT a tratar 
##	- El nombre del fichero ZIP a tratar
## El tratamiento consiste en lo siguiente:
## 	1. Se lee el txt indicado como parámetro para saber el envío al que se refiere y el estado del mismo
##	2. Se consulta en TB_COMUNICACIONES si tenemos un envío para el acuse de recibo
##	3. En caso de que exista, lo clasificamos para hacer el JOIN con la 
##	   tabla adecuada (Polizas, Modificaciones, Siniestros o Reducciones de capital)
##	4. Actualizar el estado del envío, el fichero (si viniera), la fecha de
##	   recepción y el estado de la tabla que corresponda.
function TratarAcuseRecibo
{
	acuseReciboTXT=${1}
	acuseReciboZIP=${2}
	
	## El Formato del contenido del fichero de texto será una línea, terminada con los
	## caracteres <CR><LF>, que contendrá los siguientes campos:
	## 
	## Nombre del Envío: 8 caracteres alfanuméricos con el nombre del lote de
	## 		     información (nombre del fichero TXT/ZIP) en el que se
	##		     envió la información
	## Código de Respuesta: 1 carácter alfanumérico, indicando el resultado del 
	## 			envío. La codificación seguida es la siguiente:
	##			A - Envío Aceptado (Se aceptan todos los documentos)
	##			R - Envío Rechazado (Se rechazan todos los documentos)
	##			X - Envío Aceptado parcialmente (Se aceptan algunos
	##			    Documentos y se rechazan otros)
	## Descriptivo del error: 71 caractéres alfanuméricos. Vendrá relleno si el 
	##			  envío es rechazado.

	## Comprobamos la última letra del contenido del fichero de texto
	codigoRespuesta=`cat "$destDir/$acuseReciboTXT"|awk '{ print substr($0,9,1) }'`
	## Obtenemos el nombre del envío
	nombreEnvio=`cat "$destDir/$acuseReciboTXT"|awk '{ print substr($0,1,8) }'`
	
	ficherorecibido=`echo "$acuseReciboTXT"|awk '{ print substr($0,1,8) }'`
	log "FICHERO_RECIBIDO: '$ficherorecibido'"
	
	log "Codigo de Respuesta: '$codigoRespuesta'"
	log "Nombre del envio: $nombreEnvio"
	
	envio=`sqlplus -s /@$oracleSIDProp << EOF
        	whenever sqlerror exit failure;
		set heading off;
		set feedback off;
		set serveroutput on;
		set termout off;
		DECLARE
			TYPE TpCursor IS REF CURSOR;
			l_tp_cursor  TpCursor;
			
			l_query  VARCHAR2(1000);
			
			l_idenvio NUMBER(15);
			l_ficherotipo VARCHAR2(1);

		BEGIN
			l_query := 'SELECT IDENVIO, FICHERO_TIPO FROM o02agpe0.TB_COMUNICACIONES WHERE TIPO_MOV = ''E'' AND FICHERO_ENVIO = ''$nombreEnvio''';
			OPEN l_tp_cursor FOR l_query;
			FETCH l_tp_cursor INTO l_idenvio, l_ficherotipo;
			IF l_tp_cursor%FOUND THEN
				DBMS_OUTPUT.PUT_LINE(l_idenvio || '==' || l_ficherotipo || '**');
                        END IF;
                        
                        --Cierro los cursores
                        CLOSE l_tp_cursor;
		END;
		/
	exit;
	EOF`
	
	ret_val=$?
	
	if [ "$ret_val" -gt 0 ]; then
		echo "Error al seleccionar los nombres de ficheros enviados. Error $ret_val"
		log $filename
		exit 1
	fi
	
	## Tratamos el envio para actualizar el estado de la poliza/anexo/siniestro
	## envio viene con el formato 'idenvio==ficherotipo**'
	
	log "Envío obtenido de la base de datos: '$envio'"
	
	if [ "$envio" = "" ]; then
		log "El fichero no corresponde a un envío de Agroplus"
	else
		
		## Se extrae el id del envio
		idenvio=`echo $envio| cut -d'==' -f1`
		echo "Identificador del envio obtenido de BD: '$idenvio'"
		if [ "$idenvio" -gt 0 ]; then
			##El tipo de fichero tiene 1 caracter de longitud. Nos quedamos con la parte de despues del '==' y quitamos los '**'
			tipofichero=`echo $envio| cut -d'==' -f3| cut -d'**' -f1`
			echo "Tipo de fichero obtenido de BD: '$tipofichero'"
			
			log "Llamada a java: GeneraSqlActualizacion '$tipofichero' '$destDir' '$idenvio' '$acuseReciboZIP' $codigoRespuesta"  | tee -a $LOG_NAME_1
			resultActualizar=$(GeneraSqlActualizacion $tipofichero $destDir $idenvio $acuseReciboZIP $codigoRespuesta)
			
			echo "Resultado de la acutalización de estados: $resultActualizar"
			
			## Para terminar la recepción, si ha habido pólizas rechazadas, tenemos que enviar correos
			if [ "$codigoRespuesta" = "R" ] || [ "$codigoRespuesta" = "X" ]; then
				resultMail=`sqlplus -s /@$oracleSIDProp << EOF
					whenever sqlerror exit failure;
					set heading off;
					set feedback off;
					set serveroutput on;
					set termout off;
					BEGIN
						--Llamada al pl que genera los emails
						select o02agpe0.PQ_TRATAR_POLIZAS_RECHAZADAS.GENERA_CORREOS_RECHAZO($idenvio) FROM DUAL;
					END;
					/
				exit
				EOF`
				
				log $resultMail
			fi
			
		fi
	fi
	
}



## Función que se ejecuta cuando el código de respuesta es X : Aceptado parcialmente
## En este caso es necesario leer el archivo AcuseRecibo.xml contenido en el .ZIP asociado
## y comprobar para cada poliza/anexo/siniestro si ha sido aceptada o rechazada 
function GeneraSqlActualizacion
{

	## Se llama a la clase Java GeneraSqlEstadoPoliza
	## usage: GeneradorSqlEstadoPoliza
	## -tipofichero <arg>     Tipo de fichero: poliza/anexo/siniestro (obligatorio)
	## -dirAcuseRecibo <arg>  Directorio en el que se ha descargado el ZIP con el acuse de recibo de la póliza (obligatorio)
	## -nomAcuseRecibo <arg>  Nombre del ZIP que contiene el acuse de recibo de la póliza (obligatorio)
	
	log "$LD_LIBRARY_PATH"  | tee -a $LOG_NAME_1;
	log "$ORACLE_HOME/bin"  | tee -a $LOG_NAME_1;

	resultadoAceptacionPoliza=`$javaPathProp/java -jar -Djava.awt.headless=true -Djava.library.path=".;$ORACLE_HOME/bin;$LD_LIBRARY_PATH" $fileManagerProp/GeneradorSqlEstadoPoliza.jar \
					-tipofichero $tipofichero \
	     				-dirAcuseRecibo $destDir \
	     				-idenvio $idenvio \
	     				-nomAcuseRecibo $acuseReciboZIP \
	     				-codigoRespuesta $codigoRespuesta`
	log $resultadoAceptacionPoliza  | tee -a $LOG_NAME_1;
}

