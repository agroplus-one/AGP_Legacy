#!/bin/ksh
#########################################################
##  Revisión de documentos para subida a GED           ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       12-02-2020 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

# Funcion principal
main()
{
    log "Entramos dentro de agp_ged_rev_docum.sh";        
    
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
    
    LOG_NAME_1="${PATH_LOG}"/AGP_GED_REV_DOCUM_"${fecha}".log
    log "" | tee -a $LOG_NAME_1;
    log "Las variables de entrada leidas del fichero de configuracion son:"  | tee -a $LOG_NAME_1;
    log "userDBProp: ${userDBProp}"  | tee -a $LOG_NAME_1;
    log "directoryNameProp: ${directoryNameProp}"  | tee -a $LOG_NAME_1;
    log "oracleSIDProp: ${oracleSIDProp}"  | tee -a $LOG_NAME_1;
    log "PATH_LOG: ${PATH_LOG}" | tee -a $LOG_NAME_1;  

   
    export LD_LIBRARY_PATH=$ORACLE_HOME/lib
    log "LD_LIBRARY_PATH - $LD_LIBRARY_PATH" | tee -a $LOG_NAME_1;
    
    	# Ejecución del .jar correspondiente
	log "" | tee -a $LOG_NAME_1;	    	
	
	log "Inicio de la revisión de subida de documentación a GED: `date '+%d/%m/%y %H:%M:%S'`"  | tee -a $LOG_NAME_1;    

        $javaPathProp/java -jar -Xms256m -Xmx2048m -Djava.library.path=$LD_LIBRARY_PATH $fileManagerProp/DocumentacionGed.jar

	# Almacena en la variable ret_val el valor devuelto por la última instrucción ejecutada (la ejecución del .jar)
	ret_val=$?
	log "Resultado de la ejecucion: $ret_val"  | tee -a $LOG_NAME_1;	
	
	# Si se ha devuelto cualquier valor mayor que 0 (error), sale con valor 1 para que la cadena finalice KO
    if [ $ret_val -gt 0 ]; then
    	log "Error durante la revisión de documentación GED. Error $ret_val"  | tee -a $LOG_NAME_1;
    	exit 1
    fi
	
    # Si se ha devuelto un 0, la ejecución es correcta y sale con valor 0 para que la cadena finalice OK
    log "Fin del proceso: `date '+%d/%m/%y %H:%M:%S'`"  | tee -a $LOG_NAME_1;
    exit 0
}

# Script Principal
main