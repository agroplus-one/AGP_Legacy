#!/bin/ksh
#########################################################
## Envia un correo con el resumen de los envios a      ##
## Agroseguro y a Pagos de la aplicación Agroplus      ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       21/12/2013 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

function doWork
{
	log "llamamos al PL generaCorreoResumenEnvios"  | tee -a $LOG_NAME_1;
	## Envio del correo resumen con los resultados del batch
	resultMail=`sqlplus -s /@${oracleSIDProp} << EOF
		whenever sqlerror exit failure;
		set heading off;
		set feedback off;
		set serveroutput on;
		set termout off;
		BEGIN
			--Llamada al pl que genera los emails
			o02agpe0.PQ_ENVIO_CORREOS.generaCorreoResumenEnvios;
		END;
		/
	exit
	EOF`

	log " FIN llamamos al PL generaCorreoResumenEnvios $resultMail"  | tee -a $LOG_NAME_1;
}

# Funcion principal
main()
{
    ERROR_MAIN=0
    PATH_EJECUTION="/aplicaciones2/AGP_AGROPLUS/batchs"
    PATH_LOG="/aplicaciones2/AGP_AGROPLUS/batchs"
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

    LOG_NAME_1="${PATH_LOG}"/AGP_ENVIO_CORREO_RESUMEN_"${fecha}".log
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
    #CompruebaConexion $userDBProp/$pwdDBProp@$oracleSIDProp ${LOG_NAME_1}

    ## Obtiene el listado de ficheros del buzón de Agroseguro y los trata según su tipo
    doWork $1
    
}

# Script Principal
main $1
