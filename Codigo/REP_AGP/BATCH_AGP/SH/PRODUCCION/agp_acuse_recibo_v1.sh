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

function doWork
{

	## Se consulta la Tabla TB_COMUNICACIONES_POLIZA para encontrar cuales son los envios
	## realizados con anterioridad, los cuales tendran TIPO_MOV=ENVIO. Se devuelven
	## los nombres de los ficheros, concatenados por doble almohadilla
	envios=`sqlplus -s /@$oracleSIDProp << EOF
                whenever sqlerror exit failure
                set heading off;
                set feedback off;
                set serveroutput on;
                set termout off;
                DECLARE
                        TYPE TpCursor IS REF CURSOR;
                        l_tp_cursor TpCursor;
			l_idpoliza NUMBER(15);
			l_refpoliza VARCHAR2(8);
                        l_fichero_envio VARCHAR2(8);
                        l_query VARCHAR2(1000);
                BEGIN
                        l_query := 'SELECT COM.IDPOLIZA, FICHERO_ENVIO, REFERENCIA FROM o02agpe0.TB_COMUNICACIONES_POLIZA COM JOIN o02agpe0.TB_POLIZAS POL ON COM.IDPOLIZA=POL.IDPOLIZA WHERE TIPO_MOV=''ENVIO'' AND RESULTADO IS NULL';
                        OPEN l_tp_cursor FOR l_query;
                        FETCH l_tp_cursor INTO l_idpoliza, l_fichero_envio, l_refpoliza;
                        IF l_tp_cursor%FOUND THEN
                                LOOP
                                        DBMS_OUTPUT.PUT_LINE(l_idpoliza || '==' || l_fichero_envio || '==' || l_refpoliza || '##');
                                        FETCH l_tp_cursor INTO l_idpoliza, l_fichero_envio, l_refpoliza;
                                        EXIT WHEN l_tp_cursor%NOTFOUND;
                                END LOOP;
                        END IF;
                END;
                /
                exit
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al seleccionar los nombres de ficheros enviados. Error $ret_val"
		log $filename
		exit 1
	fi

	## Por cada nombre de fichero recuperado, llamamos a comprobarAcuseDeRecibo, que nos devuelve
	## el nombre del fichero de Acuse de Recibo en AgroSeguro
	
	for envio in `echo "$envios" | grep -e "[^##]*"`; do 
		## envio viene con el formato 'poliza==fichero==referenciapoliza##'
		log "Envio: $envio"
		
		## Se extrae la poliza
		idpoliza=`echo $envio| cut -d'==' -f1` 
		
		log "IdPoliza: $idpoliza"
		
		## La referencia de la póliza tiene 7 caracteres de longitud
		refpoliza=`echo $envio| cut -d'==' -f5`
		refpoliza=`echo "$refpoliza"|awk '{ print substr($0,1,7) }'`     													
		
		log "RefPoliza: $refpoliza"
		
		## El nombre del fichero con el acuse de recibo tiene 8 caracteres de longitud
		fichero=`echo $envio| cut -d'==' -f3` 
		fichero=`echo "$fichero"|awk '{ print substr($0,1,8) }'`
		log "Nombre del Fichero: $fichero"
		acuseReciboTXT=$(CompruebaAcuseDeRecibo $fichero)
		log "Fichero Remoto con el acuse de Recibo: $acuseReciboTXT"

		## Se descarga el fichero de acuse de Recibo .TXT y se intenta descargar
		## su .ZIP asociado (puede que no exista). Ver DES-N-0042-5.0.pdf.
		## Se llama a la clase Java FileDownloader
		## usage: FileDownloader
		##  -address <arg>           URL del fichero a descargar (obligatorio)
		##  -destdir <arg>           Directorio de destino donde almacenar el archivo
		##                           descargado. Por defecto se usa el directorio
		##                           actual.
		##  -do,--disableOverwrite   Por defecto, si el fichero existe lo
		##                           sobreescribe. Habilitando esta opción se
		##                           mantiene el fichero antiguo.
		##  -domainName <arg>        Nombre del dominio si la autentificación por
		##                           proxy es NTLM (opcional)
		##  -filename <arg>          Nombre del fichero que se quiere
		##                           descargar. (Obligatorio)
		##  -h,--help                Imprime el mensaje de ayuda
		##  -httpsPort <arg>         Puerto HTTPS para la conexión (opcional, por
		##                           defecto se utiliza el puerto 443)
		##  -machineName <arg>       Nombre de la máquina si la autentificación por
		##                           proxy es NTLM (opcional)
		##  -proxyHost <arg>         IP del servidor proxy (opcional)
		##  -proxyPassword <arg>     Password para el usuario del proxy (opcional)
		##  -proxyPort <arg>         Puerto del servidor proxy (opcional)
		##  -proxyUser <arg>         Usuario del proxy (opcional)
		##  -userAgro <arg>          Usuario para la conexión al servlet de envío de
		##                           ficheros de AgroSeguro (obligatorio)
		##  -passwordAgro <arg>      Contraseña para la conexión al servlet de envío
		##                           de ficheros de AgroSeguro (obligatorio)

		## Se obtiene el directorio de destino
		destDir=$(GetDirectory)

		## Primero se descarga el .TXT
		$javaPathProp/java -jar $fileManagerProp/FileDownloader.jar \
	     	    	    -address $urlDownloadProp \
	     	    	    -filename $acuseReciboTXT \
			    -destdir $destDir \
	     	            -userAgro $userAgroseguroProp \
	     	    	    -passwordAgro $pwdAgroseguroProp

		## Después se descarga el ZIP, donde estará el XML con los datos del acuse de recibo
		## Cambiamos la extensión del fichero de .TXT a .ZIP
		acuseReciboZIP=`echo "$acuseReciboTXT"|awk '{ print substr($0,1,8) }'`.ZIP
		$javaPathProp/java -jar $fileManagerProp/FileDownloader.jar \
	     	    	    -address $urlDownloadProp \
	     	    	    -filename $acuseReciboZIP \
			    -destdir $destDir \
	     	            -userAgro $userAgroseguroProp \
	     	    	    -passwordAgro $pwdAgroseguroProp

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
		codigoRespuesta=`cat "$destDir/$acuseReciboTXT"|awk '{ print substr($0,9,9) }'`
		
		
		log "Codigo de Respuesta: $codigoRespuesta"

		## Estados posibles de la Póliza
		## 1 Pendiente Validación
		## 2 Grabación Provisional
		## 3 Grabación Definitiva
		## 4 Anulada
		## 5 Enviada Pendiente de Confirmar
		## 6 Enviada Correcta
		## 7 Enviada Errónea
		## 8 Definitiva

		if [ $codigoRespuesta = "A" ]; then
			resultado="CORRECTO"
			estado="6"
		fi
		
		if [ $codigoRespuesta = "R" ]; then
			resultado="ERROR"
			estado="7"
		fi

		if [ $codigoRespuesta = "X" ]; then
			estado=$(CompruebaAceptacionPoliza $refpoliza $destDir $acuseReciboZIP)
			
			if [ $estado = 6 ]; then
				resultado="CORRECTO"
			elif [ $estado = 7 ]; then
				resultado="ERROR"
			else
				log "No se puede determinar el estado de la poliza"
			fi
		
		fi	

		log "Resultado: $resultado"
		log "Estado de la Poliza: $estado"

		## Se actualiza el estado de la poliza (Ver TB_ESTADOS_POLIZA) a 'enviada correcta'
		## o a 'enviada errónea'. También se actualiza la tabla TB_COMUNICACIONES_POLIZA
		## con el movimiento de acuse de recibo y el resultado (correcto o error)
        	updates=`sqlplus -s /@$oracleSIDProp << EOF
                whenever sqlerror exit failure;
                set heading off;
                set feedback off;
                set serveroutput on;
                set termout off;
                BEGIN
			UPDATE o02agpe0.TB_POLIZAS SET IDESTADO=$estado WHERE IDPOLIZA=$idpoliza;
       			UPDATE o02agpe0.TB_COMUNICACIONES_POLIZA SET RESULTADO = '$resultado' WHERE IDPOLIZA = $idpoliza AND TIPO_MOV = 'ENVIO';
		 	COMMIT;   
                END;
                /
           exit
           EOF`

		log $updates

	done 
}


# Funcion que comprueba en que fichero está el acuse de recibo de nuestro envio
function CompruebaAcuseDeRecibo
{
	## Se llama a la clase Java AcuseReciboFinder
	## usage: AcuseReciboFinder
	## -address <arg>         URL del fichero a descargar (obligatorio)
	## -domainName <arg>      Nombre del dominio si la autentificación por
	##                        proxy es NTLM (opcional)
	## -fileID <arg>          Nombre del archivo que se envió a AgroSeguro
	##                        (obligatorio)
	## -h,--help              Imprime el mensaje de ayuda
	## -httpsPort <arg>       Puerto HTTPS para la conexión (opcional, por
	##                        defecto se utiliza el puerto 443)
	## -machineName <arg>     Nombre de la máquina si la autentificación por
	##                        proxy es NTLM (opcional)
	## -passwordAgro <arg>    Contraseña para la conexión al servlet de envío
	##                        de ficheros de AgroSeguro (obligatorio)
	## -proxyHost <arg>       IP del servidor proxy (opcional)
	## -proxyPassword <arg>   Password para el usuario del proxy (opcional)
	## -proxyPort <arg>       Puerto del servidor proxy (opcional)
	## -proxyUser <arg>       Usuario del proxy (opcional)
	## -userAgro <arg>        Usuario para la conexión al servlet de envío de
	##                        ficheros de AgroSeguro (obligatorio)

	## La clase Java, devuelve el nombre del fichero TXT que contiene un identificador
	## igual al fileID indicado, con formato AAAAAAAA.TXT
	## Si la respuesta de la clase Java, no tiene 12 caracteres de longitud, es que 
	## ha ocurrido un error o no se ha encontrado el fichero.

	fileNameAcuseRecibo=`$javaPathProp/java -jar $fileManagerProp/AcuseReciboFinder.jar \
	     		    -address $urlDownloadProp \
	     		    -fileID $1 \
	     		    -userAgro $userAgroseguroProp \
	     		    -passwordAgro $pwdAgroseguroProp` 

	## Se comprueba la longitud del valor retornado por el Java
	longitud=`echo $fileNameAcuseRecibo | wc -c`
	let longitud=longitud-1

	if [ $longitud -ne 12 ]; then
		echo "Error al recuperar el Acuse de Recibo del envio $filename"
		log $fileNameAcuseRecibo
		exit 1
	fi

	echo $fileNameAcuseRecibo
}


## Función que se ejecuta cuando el código de respuesta es X : Aceptado parcialmente
## En este caso es necesario leer el archivo AcuseRecibo.xml contenido en el .ZIP asociado
## y comprobar si la póliza que está siendo tratada ha sido aceptada o rechazada 

function CompruebaAceptacionPoliza
{

	## Se llama a la clase Java ComprobadorEstadoPoliza
	## usage: ComprobadorEstadoPoliza
	## -refPoliza <arg>       Referencia de la póliza a tratar (obligatorio)
	## -dirAcuseRecibo <arg>  Directorio en el que se ha descargado el ZIP con el acuse de recibo de la póliza (obligatorio)
	## -nomAcuseRecibo <arg>  Nombre del ZIP que contiene el acuse de recibo de la póliza (obligatorio)

	resultadoAceptacionPoliza=`$javaPathProp/java -jar $fileManagerProp/comprobadorEstadoPoliza.jar \
					-refPoliza $refpoliza \
	     				-dirAcuseRecibo $destDir \
	     				-nomAcuseRecibo $acuseReciboZIP`

	if [ $resultadoAceptacionPoliza = "1" ]; then
		echo 6
	elif [ $resultadoAceptacionPoliza = "2" ]; then
		echo 7
	else 
		echo -1
	fi		     		    								
	 
	     		    

}

# Funcion principal
main()
{
    ERROR_MAIN=0
    PATH_EJECUTION=`dirname $0`
    PATH_LOG=`dirname $0`
    fecha=`date '+%d%m%y%H%M%S'`

    ## Se cargan las propiedades definidas en agp.batch.properties
    if [ ! -f ${PATH_EJECUTION}/agp.batch.properties ]; then
       echo "\nERROR000: Imposible localizar el fichero de configuracion ${PATH_EJECUTION}/agp.batch.properties>\n"
       exit 1
    fi

    ## Se cargan variables y funciones comunes
    . ${PATH_EJECUTION}/agp_common.sh
    
    ERROR=0
    [ "${userAgroseguroProp}" = "" ] && ERROR=1
    [ "${pwdAgroseguroProp}" = "" ] && ERROR=1
    [ "${urlUploadProp}" = "" ] && ERROR=1
    [ "${urlDownloadProp}" = "" ] && ERROR=1
    [ "${userDBProp}" = "" ] && ERROR=1
    [ "${pwdDBProp}" = "" ] && ERROR=1
    [ "${directoryNameProp}" = "" ] && ERROR=1
    [ "${oracleSIDProp}" = "" ] && ERROR=1


    if [ "$ERROR" -eq 1 ]; then
       echo "\nERROR000: Parametro de configuracion no definido correctamente en ${PATH_EJECUTION}/agp.batch.properties>\n"
       exit 1
    fi

    #Chequea el PATH del directorio de LOG
    if [ ! -d "${PATH_LOG}" -o ! -w "${PATH_LOG}" ]; then
       echo "\nERROR000: El directorio de log <${PATH_LOG}> no existe o no tiene permisos de escritura\n"
       exit 1
    fi

    LOG_NAME_1="${PATH_LOG}"/AGP_ACUSE_RECIBO"${fecha}".log
    log "" | tee -a $LOG_NAME_1
    log "Las variables de entrada leidas del fichero de configuracion son:"  | tee -a $LOG_NAME_1
    log "userAgroseguroProp: ${userAgroseguroProp}"  | tee -a $LOG_NAME_1
    log "urlUploadProp: ${urlUploadProp}"  | tee -a $LOG_NAME_1
    log "urlDownloadProp: ${urlDownloadProp}"  | tee -a $LOG_NAME_1
    log "userDBProp: ${userDBProp}"  | tee -a $LOG_NAME_1
    log "directoryNameProp: ${directoryNameProp}"  | tee -a $LOG_NAME_1
    log "oracleSIDProp: ${oracleSIDProp}"  | tee -a $LOG_NAME_1
    log "PATH_LOG: ${PATH_LOG}" | tee -a $LOG_NAME_1
    log "" | tee -a $LOG_NAME_1
    log "\t\t Hora comienzo: " `date '+%d/%m/%y %H:%M:%S'` | tee -a $LOG_NAME_1

    log "\n\n --- Comprobando conexion a la base de datos ---" | tee -a $LOG_NAME_1
    CompruebaConexion /@${oracleSIDProp} ${LOG_NAME_1}

    ## Llama al plsql y realiza el envío
    doWork $1
    
}

# Script Principal
main $1
