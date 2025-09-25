function doWork
{
    ## Se comprueba si se pasó como parámetro el código de plan
    ## Si no se recibió, se indica el año actual

    codNewPlan=$1
    if [ $# -ne 1 ]; then
	codNewPlan=`date +"%Y"`
    fi
    log "Codigo de Plan: $codNewPlan"

    principal="P"
    complementario="C"
	
	# Grupo de seguro para el envio diferenciado de polizas agricolas y ganaderas
	#gsAgricola = "A01"
	#gsGanado = "G01"
    
    ## Se deben seleccionar las pólizas del año indicado, y generar un fichero, 
    ## y luego las del año anterior y generar otro fichero. Para ello, se resta 1 al 
    ## codigo de plan para saber el año anterior
    let codOldPlan=codNewPlan-1

    log "Codigo de Plan Antiguo: $codOldPlan"
    
	## Actualización del número de pólizas definitivas
	resultAct=`sqlplus -s /@$oracleSIDProp << EOF
		whenever sqlerror exit failure;
		set heading off;
		set feedback off;
		set serveroutput on;
		set termout off;
		BEGIN
			--Llamada al pl
			o02agpe0.pq_genera_envios_agroseguro.actualizaPolizasDefinitivas;
		END;
		/
	exit
	EOF`
	
    export NLS_LANG=SPANISH
	
	for grupoSeguro in G01 A01
    do
	log "[inicio]Bucle0 grupoSeguro: $grupoSeguro"
    for codplan in "$codNewPlan" "$codOldPlan"
    do
    
        log "1"
        log "\n\n\n[inicio]Bucle1 codplan: $codplan"
        
    	for tipopoliza in "$principal" "$complementario"
    	do
    	        log "2"
    	        log "\n[inicio]Bucle1.1 tipo poliza: $tipopoliza, codplan: $codplan"
				
		log "Se buscan las polizas en definitiva del plan $codplan, tipo $tipopoliza y grupo de seguro $grupoSeguro"
    	
		## Se llama al PL-SQL que genera los archivos para el envío de las pólizas definitivo, 
		## recogiendo en la variable filename el nombre del archivo generado por el plsql para luego poder enviarlo
		filename=`sqlplus -s /@$oracleSIDProp << EOF
		        set heading off;
		        set feedback off;
			set serveroutput on;
			whenever OSERROR exit OSCODE rollback;
			whenever SQLERROR exit SQL.SQLCODE rollback;
			declare
				file VARCHAR2(50);
		  	begin
		  		file := o02agpe0.PQ_GENERA_ENVIOS_AGROSEGURO.genera_fichero_polizas('$codplan', '$tipopoliza', '$grupoSeguro');
				dbms_output.put_line(file);
		  	end;
			/
			exit;
			EOF`
		
		ret_val=$?
		
		log "3"
		log "Fichero recibido del pl de generación del ZIP: $filename"  | tee -a $LOG_NAME_1
		log "4"
		
		if [ $ret_val -gt 0 ]; then
			log "5"
			log "Error al ejecutar el PLSQL PQ_GENERA_ENVIOS_AGROSEGURO para el Plan $codplan. Error $ret_val" | tee -a $LOG_NAME_1
			echo "Es posible que no existan polizas para el Plan $codplan."
			log $filename  | tee -a $LOG_NAME_1
			
			continue
	
			#if [ $codplan -eq $codNewPlan ]; then
			#	log "5.1"
			#	log "[fin-continue -c-]Bucle1.1 tipopoliza"
			#	continue
			#fi
	        #
			#if [ $tipopoliza = $principal ]; then 
			#	log "5.2"
			#	log "[fin-continue -c-]Bucle1.1 tipopoliza"
			#	continue
			#fi
			#
			#if [ $grupoSeguro = G01]; then 
			#	log "5.5"
			#	log "[fin-continue -c-]Bucle1.1 grupoSeguro"
			#	continue
			#fi
	        #
			#if [ $ret_val -eq 150 ]; then
			#	log "5.3"
			#	log "[exit 0]"
			#	exit 0
			#else
			#	log "5.4"
			#	log "[exit 1]"
			#	exit 1
			#fi
		fi
		log "6"
		
		## Se recupera el directorio donde se están generando los archivos
		## directoryName=$(GetDirectory)
		directoryName="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH"
		
		log "Nombre del Directorio: $directoryName"  | tee -a $LOG_NAME_1
		log "8"
		## Se llama al java que envía el fichero por https a AgroSeguro
		## usage: FileUploader
		## -address <arg>         URL donde se quiere enviar el fichero
		##                        (obligatorio)
		## -domainName <arg>      Nombre del dominio si la autentificación por
		##                        proxy es NTLM (opcional)
		## -file <arg>            Nombre del fichero sin extension que se quiere
		##                        enviar (obligatorio, mismo nombre para .TXT y
		##                        .ZIP)
		## -folder <arg>          Directorio donde se encuentran los ficheros que se
		##                        quieren enviar (opcional, por defecto, directorio
		##                        actual)
		## -h,--help              Imprime el mensaje de ayuda
		## -httpsPort <arg>       Puerto HTTPS para la conexión (opcional, por
		##                        defecto se utiliza el puerto 443)
		## -machineName <arg>     Nombre de la máquina si la autentificación por
		##                        proxy es NTLM (opcional)
		## -passwordAgro <arg>    Contraseña para la conexión al servlet de envío
		##                        de ficheros de AgroSeguro (obligatorio)
		## -proxyHost <arg>       IP del servidor proxy (opcional)
		## -proxyPassword <arg>   Password para el usuario del proxy (opcional)
		## -proxyPort <arg>       Puerto del servidor proxy (opcional, por defecto
		##                        se utiliza el puerto 8080)
		## -proxyUser <arg>       Usuario del proxy (opcional)
		## -userAgro <arg>        Usuario para la conexión al servlet de envío de
		##                        ficheros de AgroSeguro (obligatorio)
		
		log "Llamada a FileUploader.java"  | tee -a $LOG_NAME_1
		log "Ruta de FileUploader: $fileManagerProp"  | tee -a $LOG_NAME_1
		log "Parametros: address=$urlUploadProp; file=$filename; folder=$directoryName; userAgro=$userAgroseguroProp; passwordAgro=$pwdAgroseguroProp; numIntentos=$numIntentos; tiempoEspera=$tiempoEspera;"  | tee -a $LOG_NAME_1
		
		$javaPathProp/java -jar $fileManagerProp/FileUploader.jar \
		     -address $urlUploadProp \
		     -file $filename \
		     -folder $directoryName \
		     -verbose true \
		     -userAgro $userAgroseguroProp \
		     -passwordAgro $pwdAgroseguroProp \
			 -numIntentos $numIntentos \
			 -tiempoEspera $tiempoEspera
		
		ret_val=$?
		log "9"
		log "Llamada a FileUploader.java OK!!"  | tee -a $LOG_NAME_1
		
		if [ $ret_val -gt 0 ]; then
			log "10"
			echo "Error al realizar el upload de los ficheros a AgroSeguro. Error $ret_val"
			exit 1
		fi
		log "11"

	done
	log "[fin]Bucle1.1 tipopoliza"
	log "12"
    done
    log "[fin]Bucle1 codplan"
    log "13"
	done
    log "[fin]Bucle grupo seguro"
    log "13"

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
       echo "\nERROR000: El directorio de logs <${PATH_LOG}> no existe o no tiene permisos de escritura\n"
       exit 1
    fi

    LOG_NAME_1="${PATH_LOG}"/AGP_ENVIO_FICHEROS_"${fecha}".log
    log "" | tee -a $LOG_NAME_1
    log "Las variables de entrada leidas del fichero de configuracion son:"  | tee -a $LOG_NAME_1
    log "userAgroseguroProp: ${userAgroseguroProp}"  | tee -a $LOG_NAME_1
    log "urlUploadProp: ${urlUploadProp}"  | tee -a $LOG_NAME_1
    log "urlDownloadProp: ${urlDownloadProp}"  | tee -a $LOG_NAME_1
    log "userDBProp: ${userDBProp}"  | tee -a $LOG_NAME_1
    log "directoryNameProp: ${directoryNameProp}"  | tee -a $LOG_NAME_1
    log "oracleSIDProp: ${oracleSIDProp}"  | tee -a $LOG_NAME_1
    log "PATH_LOG: ${PATH_LOG}" | tee -a $LOG_NAME_1
	log "numIntentos: ${numIntentos}" | tee -a $LOG_NAME_1
	log "tiempoEspera: ${tiempoEspera}" | tee -a $LOG_NAME_1
    log "" | tee -a $LOG_NAME_1
    log "\t\t Hora comienzo: " `date '+%d/%m/%y %H:%M:%S'` | tee -a $LOG_NAME_1

    log "\n\n --- Comprobando conexion a la base de datos ---" | tee -a $LOG_NAME_1
    CompruebaConexion /@${oracleSIDProp} ${LOG_NAME_1}
	
	## Llama al plsql y realiza el envío
    doWork $1
}

# Script Principal
main $1