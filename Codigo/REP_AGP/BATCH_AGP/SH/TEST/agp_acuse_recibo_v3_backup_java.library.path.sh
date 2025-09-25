#!/bin/ksh

#########################################################
## Recepci�n de Acuses de Recibo de Agroseguro         ##
##                                                     ##
## Si se ha realizado el env�o de p�lizas por          ##
## comunicaciones, se recibir� el acuse de recibo,     ##
## seg�n el documento 'DES-N-0042'.                    ##
## Seg�n los datos recibidos en el acuse de recibo,    ##
## se actualizar� el estado de p�liza, bien a 'enviada ##
## correcta' o a 'enviada err�nea'. Adem�s se          ##
## actualizar� la entidad comunicaciones de p�liza     ##
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

## Funci�n para tratar los acuses de recibo. 
## Recibe los siguientes par�metros:
## 	- El nombre del fichero TXT a tratar 
##	- El nombre del fichero ZIP a tratar
## El tratamiento consiste en lo siguiente:
## 	1. Se lee el txt indicado como par�metro para saber el env�o al que se refiere y el estado del mismo
##	2. Se consulta en TB_COMUNICACIONES si tenemos un env�o para el acuse de recibo
##	3. En caso de que exista, lo clasificamos para hacer el JOIN con la 
##	   tabla adecuada (Polizas, Modificaciones, Siniestros o Reducciones de capital)
##	4. Actualizar el estado del env�o, el fichero (si viniera), la fecha de
##	   recepci�n y el estado de la tabla que corresponda.
function TratarAcuseRecibo
{
	acuseReciboTXT=${1}
	acuseReciboZIP=${2}
	
	## El Formato del contenido del fichero de texto ser� una l�nea, terminada con los
	## caracteres <CR><LF>, que contendr� los siguientes campos:
	## 
	## Nombre del Env�o: 8 caracteres alfanum�ricos con el nombre del lote de
	## 		     informaci�n (nombre del fichero TXT/ZIP) en el que se
	##		     envi� la informaci�n
	## C�digo de Respuesta: 1 car�cter alfanum�rico, indicando el resultado del 
	## 			env�o. La codificaci�n seguida es la siguiente:
	##			A - Env�o Aceptado (Se aceptan todos los documentos)
	##			R - Env�o Rechazado (Se rechazan todos los documentos)
	##			X - Env�o Aceptado parcialmente (Se aceptan algunos
	##			    Documentos y se rechazan otros)
	## Descriptivo del error: 71 caract�res alfanum�ricos. Vendr� relleno si el 
	##			  env�o es rechazado.

	## Comprobamos la �ltima letra del contenido del fichero de texto
	codigoRespuesta=`cat "$destDir/$acuseReciboTXT"|awk '{ print substr($0,9,1) }'`
	## Obtenemos el nombre del env�o
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
	
	log "Env�o obtenido de la base de datos: '$envio'"
	
	if [ "$envio" = "" ]; then
		log "El fichero no corresponde a un env�o de Agroplus"
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
			
			echo "Resultado de la acutalizaci�n de estados: $resultActualizar"
			
			## Para terminar la recepci�n, si ha habido p�lizas rechazadas, tenemos que enviar correos
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



## Funci�n que se ejecuta cuando el c�digo de respuesta es X : Aceptado parcialmente
## En este caso es necesario leer el archivo AcuseRecibo.xml contenido en el .ZIP asociado
## y comprobar para cada poliza/anexo/siniestro si ha sido aceptada o rechazada 
function GeneraSqlActualizacion
{

	## Se llama a la clase Java GeneraSqlEstadoPoliza
	## usage: GeneradorSqlEstadoPoliza
	## -tipofichero <arg>     Tipo de fichero: poliza/anexo/siniestro (obligatorio)
	## -dirAcuseRecibo <arg>  Directorio en el que se ha descargado el ZIP con el acuse de recibo de la p�liza (obligatorio)
	## -nomAcuseRecibo <arg>  Nombre del ZIP que contiene el acuse de recibo de la p�liza (obligatorio)
	
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

