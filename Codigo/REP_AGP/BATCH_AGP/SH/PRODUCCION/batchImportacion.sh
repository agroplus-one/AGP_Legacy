#!/bin/ksh
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

    LOG_NAME_1="${PATH_LOG}"/AGP_IMPORTACION_"${fecha}".log
    log "" | tee -a $LOG_NAME_1;
    log "Las variables de entrada leidas del fichero de configuracion son:"  | tee -a $LOG_NAME_1;
    log "javaPath64Prop: ${javaPath64Prop}"  | tee -a $LOG_NAME_1;
    log "fileManagerProp: ${fileManagerProp}"  | tee -a $LOG_NAME_1;  

    # Ruta de LD_LIBRARY_PATH para conexiones a Oracle con autoconnect
    export LD_LIBRARY_PATH=$ORACLE_HOME/lib	
	log "LD_LIBRARY_PATH - $LD_LIBRARY_PATH" | tee -a $LOG_NAME_1;			
			
	
	# Ejecuci�n del .jar correspondiente
	log "" | tee -a $LOG_NAME_1;	    	
	log "Inicio del proceso de carga de condicionado: `date '+%d/%m/%y %H:%M:%S'`"  | tee -a $LOG_NAME_1;        
	resultado=`$javaPath64Prop/java -jar -Xms256m -Xmx2048m -Djava.library.path=$LD_LIBRARY_PATH $fileManagerProp/BatchImportacion.jar`
	
	log "Resultado de la carga: $resultado"  | tee -a $LOG_NAME_1;
	
	# Si se ha devuelto un 0, la ejecuci�n es correcta y sale con valor 0 para que la cadena finalice OK
	log "Fin del proceso: `date '+%d/%m/%y %H:%M:%S'`"  | tee -a $LOG_NAME_1;
    exit 0
}

# Script Principal
main