#!/bin/ksh
#########################################################
## Importación de pólizas de corredurías externas      ##
##                                                     ##
##                                                     ##
##                                                     ##
##                                                     ##
##                                                     ##
##                                                     ##
##                                                     ##
##                                                     ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       06/03/2014 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

# Funcion principal
main()
{
    ERROR_MAIN=0
    PATH_EJECUTION="/aplicaciones/AGP_AGROPLUS/batchs"
    PATH_LOG="/aplicaciones/AGP_AGROPLUS/batchs"
    fecha=`date '+%d%m%y%H%M%S'`

    ## Se cargan las propiedades definidas en agp.batch.properties
    if [ ! -f ${PATH_EJECUTION}/agp.batch.properties ]; then
       echo "\nERROR000: Imposible localizar el fichero de configuracion ${PATH_EJECUTION}/agp.batch.properties>\n"
       exit 1
    fi

    ## Se cargan variables y funciones comunes
    . ${PATH_EJECUTION}/agp_common.sh
    
    ERROR=0
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

    LOG_NAME_1="${PATH_LOG}"/AGP_IMPORTACION_POLIZAS_EXT_"${fecha}".log
    log "" | tee -a $LOG_NAME_1;
    log "Las variables de entrada leidas del fichero de configuracion son:"  | tee -a $LOG_NAME_1;
    log "userDBProp: ${userDBProp}"  | tee -a $LOG_NAME_1;
    log "directoryNameProp: ${directoryNameProp}"  | tee -a $LOG_NAME_1;
    log "oracleSIDProp: ${oracleSIDProp}"  | tee -a $LOG_NAME_1;
    log "PATH_LOG: ${PATH_LOG}" | tee -a $LOG_NAME_1;
    log "" | tee -a $LOG_NAME_1;
    log "\t\t Hora comienzo: " `date '+%d/%m/%y %H:%M:%S'` | tee -a $LOG_NAME_1;

    # Ruta de LD_LIBRARY_PATH para conexiones a Oracle con autoconnect
    export LD_LIBRARY_PATH=$ORACLE_HOME/lib
    
    log "Inicio de la importación de pólizas externas"  | tee -a $LOG_NAME_1;
    resultado=`$javaPathProp/java -jar -Djava.library.path=$LD_LIBRARY_PATH $fileManagerProp/ImportacionPolizasExt.jar`
    
    ret_val=$?

    log "Resultado llamada Java: $ret_val"  | tee -a $LOG_NAME_1;
    log "Resultado de la importación de pólizas externas: $resultado"  | tee -a $LOG_NAME_1;
    
    if [ $ret_val -gt 0 ]; then
    	log "Error durante la importación de pólizas externas. Error $ret_val"  | tee -a $LOG_NAME_1;
    	exit 1
    fi
    
}

# Script Principal
main