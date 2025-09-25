#!/bin/ksh
#########################################################	
## Se procesa la respuesta del SW de seguimiento y     ##
## se actualizan los datos de las pólizas, anexos      ##
## e incidencias.                        		       ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       18/01/2019 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

function doWork
{
	log "Inicio del proceso de carga de Condicionados: `date '+%d/%m/%y %H:%M:%S'`"  | tee -a $LOG_NAME_1; 	

	# Ruta de LD_LIBRARY_PATH para conexiones a Oracle con autoconnect
    export LD_LIBRARY_PATH=$ORACLE_HOME/lib
	log "LD_LIBRARY_PATH - $LD_LIBRARY_PATH" | tee -a $LOG_NAME_1;	  
	log "javaPath64Prop: ${javaPath64Prop}"  | tee -a $LOG_NAME_1;  

	$javaPath64Prop/java -jar -Xms512m -Xmx2048m -Djava.library.path=$LD_LIBRARY_PATH $fileManagerProp/CargaCondicionados.jar
	
	resultado=$?
	
	# Si se ha devuelto cualquier valor mayor que 0 (error), sale con valor 1 para que la cadena finalice KO
    if [ $resultado -gt 0 ]; then
    	log "Error durante el proceso de carga de recibos. Error $resultado"  | tee -a $LOG_NAME_1;
    	exit 1
    fi
	
	log "Resultado de la carga: $resultado"  | tee -a $LOG_NAME_1;

	log "Ha finalizado el proceso de carga de condicionados."  | tee -a $LOG_NAME_1;
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
    [ "${userDBProp}" = "" ] && ERROR=1
    [ "${pwdDBProp}" = "" ] && ERROR=1
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

    LOG_NAME_1="${PATH_LOG}"/AGP_CARGA_CONDICIONADOS_"${fecha}".log
    log "" | tee -a $LOG_NAME_1
    log "Las variables de entrada leidas del fichero de configuracion son:"  | tee -a $LOG_NAME_1
    log "userAgroseguroProp: ${userAgroseguroProp}"  | tee -a $LOG_NAME_1
    log "userDBProp: ${userDBProp}"  | tee -a $LOG_NAME_1
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