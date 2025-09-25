#!/bin/ksh
#########################################################
## Envío de Siniestros a Agroseguro                    ##
##                                                     ##
##  Parametros: CODPLAN                                ##
##  El pl-sql genera_fichero_siniestros requiere el    ##
##  codigo de Plan (Formato YYYY) para su ejecución.   ##
##  Si no se indica se especifica el año actual.       ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       04/01/2011 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

function doWork
{

	## Se comprueba si se pasó como parámetro el código de plan
	## Si no se recibió, se indica el año actual
	
	codNewPlan=$1
	if [ $# -ne 1 ]; then
	codNewPlan=`date +"%Y"`
	fi
	log "Codigo de Plan: $codNewPlan"  | tee -a $LOG_NAME_1;
	
	## Se deben seleccionar los siniestros del año indicado
	## y luego los del año anterior y generar otro fichero. Para ello, se resta 1 al 
	## codigo de plan para saber el año anterior
	let codOldPlan=codNewPlan-1

	log "Codigo de Plan Antiguo: $codOldPlan"  | tee -a $LOG_NAME_1;
	
	## Actualización del número de siniestors definitivos
	resultAct=`sqlplus -s /@$oracleSIDProp << EOF
		whenever sqlerror exit failure;
		set heading off;
		set feedback off;
		set serveroutput on;
		set termout off;
		BEGIN
			--Llamada al pl
			o02agpe0.pq_genera_envios_agroseguro.actualizaSiniestrosDefinitivos;
		END;
		/
	exit
	EOF`
	
	log "Actualizado el numero de siniestros en definitivo $resultAct"  | tee -a $LOG_NAME_1;
	
	for codplan in "$codNewPlan" "$codOldPlan"
	do
		## Se llama al PL-SQL que genera los archivos para el envío de los siniestros, 
		## recogiendo en la variable filename el nombre del archivo generado por el plsql para luego poder enviarlo
		filename=`sqlplus -s /@$oracleSIDProp << EOF
		        set heading off;
		        set feedback off;
			set serveroutput on;
			whenever OSERROR exit OSCODE rollback;
			whenever SQLERROR exit SQL.SQLCODE rollback;
			declare
				fileNameToSend VARCHAR2(50);
		  	begin
		  		fileNameToSend := o02agpe0.PQ_GENERA_ENVIOS_AGROSEGURO.genera_fichero_siniestros('$codplan');
				dbms_output.put_line('%%' || fileNameToSend || '%%');
		  	end;
			/
			exit;
			EOF`
		
		ret_val=$?
		
		if [ $ret_val -gt 0 ]; then
			log "Error al ejecutar el PLSQL de generación de siniestros para el Plan $codplan. Error $ret_val"  | tee -a $LOG_NAME_1;
			log "Es posible que no existan siniestros para el Plan $codplan."  | tee -a $LOG_NAME_1;
			log $filename  | tee -a $LOG_NAME_1;
			
			if [ $codplan -eq $codNewPlan ]; then
				log "Continuamos con el plan anterior"  | tee -a $LOG_NAME_1;
				continue;
			fi
			
			if [ $ret_val -eq 150 ]; then
				continue;
			else
				exit 1;
			fi
		fi
		
		## Se recupera solo el nombre del fichero, que está al final entre caracteres %%
		filename=$(echo $filename | sed -e 's/\(^.*%%\)\(.*\)\(%%.*$\)/\2/')
		
		if [[ "$filename" = "" ]]; then
			log "No se generó fichero de siniestros"  | tee -a $LOG_NAME_1;
		else
		
			log "Nombre del Fichero de siniestros: $filename"  | tee -a $LOG_NAME_1;
			
			## Se recupera el directorio donde se están generando los archivos
			directoryName=$(GetDirectory)
			
			log "Nombre del Directorio: $directoryName"  | tee -a $LOG_NAME_1;
			
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
			
			$javaPathProp/java -jar $fileManagerProp/FileUploader.jar \
			     -address $urlUploadProp \
			     -file $filename \
			     -folder $directoryName \
			     -verbose true \
			     -userAgro $userAgroseguroProp \
			     -passwordAgro $pwdAgroseguroProp 
			
			ret_val=$?
			
			if [ $ret_val -gt 0 ]; then
				log "Error al realizar el upload de los ficheros a AgroSeguro. Error $ret_val"  | tee -a $LOG_NAME_1;
				exit 1;
			fi
		
		fi
	done
	
	log "Finalizada la subida de ficheros de siniestros. Actualizamos los siniestros no enviados."  | tee -a $LOG_NAME_1;
	
	## Actualización del número de siniestors definitivos no enviados a Agroseguro
	resultActNoEnv=`sqlplus -s /@$oracleSIDProp << EOF
		whenever sqlerror exit failure;
		set heading off;
		set feedback off;
		set serveroutput on;
		set termout off;
		BEGIN
			--Llamada al pl
			o02agpe0.pq_genera_envios_agroseguro.actualizaSiniestrosNoEnviados;
		END;
		/
	exit
	EOF`
	
	ret_val=$?
		
	log "Resultado de la acutalización de siniestros no enviados: $resultActNoEnv"  | tee -a $LOG_NAME_1;

	if [ $ret_val -gt 0 ]; then
		log "Error al actualizar el numero de siniestros no enviados. Error $ret_val"  | tee -a $LOG_NAME_1;
		exit 1;
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

    LOG_NAME_1="${PATH_LOG}"/AGP_ENVIO_SINIESTROS_"${fecha}".log
    log "" | tee -a $LOG_NAME_1;
    log "Las variables de entrada leidas del fichero de configuracion son:"  | tee -a $LOG_NAME_1;
    log "userAgroseguroProp: ${userAgroseguroProp}"  | tee -a $LOG_NAME_1;
    log "urlUploadProp: ${urlUploadProp}"  | tee -a $LOG_NAME_1;
    log "urlDownloadProp: ${urlDownloadProp}"  | tee -a $LOG_NAME_1;
    log "userDBProp: ${userDBProp}"  | tee -a $LOG_NAME_1;
    log "directoryNameProp: ${directoryNameProp}"  | tee -a $LOG_NAME_1;
    log "oracleSIDProp: ${oracleSIDProp}"  | tee -a $LOG_NAME_1;
    log "PATH_LOG: ${PATH_LOG}" | tee -a $LOG_NAME_1;
    log "" | tee -a $LOG_NAME_1;
    log "\t\t Hora comienzo: " `date '+%d/%m/%y %H:%M:%S'` | tee -a $LOG_NAME_1;

    log "\n\n --- Comprobando conexion a la base de datos ---" | tee -a $LOG_NAME_1;
    CompruebaConexion /@${oracleSIDProp} ${LOG_NAME_1}

    ## Llama al plsql y realiza el envío
    doWork $1
    
}

# Script Principal
main $1

