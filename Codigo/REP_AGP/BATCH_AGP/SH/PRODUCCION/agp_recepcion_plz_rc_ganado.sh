#!/bin/ksh
#########################################################	
## Se procesa el fichero recibido de OMEGA y se        ##
## actualizan los datos de las pólizas de R.C Ganado   ##
## dependiendo del código recibido.  		       ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       16/01/2018 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

function doWork
{

	echo "Comenzamos con la recepcion de polizas de R.C Ganado."        

	## Se llama al PLSQL PQ_RECEPCION_POLIZAS_RC
		cierre=`sqlplus -s /@$oracleSIDProp << EOF
			set heading off;
			set feedback off;
			set serveroutput on;
			whenever OSERROR exit OSCODE rollback;
			whenever SQLERROR exit SQL.SQLCODE rollback;
		declare
			resultado VARCHAR2(2000);
		begin
			resultado := o02agpe0.pq_recepcion_polizas_rc.recepcion_polizas_rc_ganado;
					DBMS_OUTPUT.PUT_LINE(resultado);
		end;
		/
		exit;
		EOF`

	ret_val=$?
	
	log "Resultado de la llamada a pq_envio_polizas_rc.recepcion_polizas_rc_ganado(): '$cierre'" | tee -a $LOG_NAME_1
	
	if [ $ret_val -gt 0 ]; then
		log "Error al procesar el fichero de pólizas de R.C Ganado recibido de Omega. Error $ret_val" | tee -a $LOG_NAME_1
		exit 1
	fi
	
	log "Ha finalizado el proceso de recepcion de polizas de R.C Ganado. Se envía el correo resumen."  | tee -a $LOG_NAME_1;



	## Envio del correo resumen con los resultados de la recepción de pólizas de sobreprecio
	resultMail=`sqlplus -s /@$oracleSIDProp << EOF
		whenever sqlerror exit failure;
		set heading off;
		set feedback off;
		set serveroutput on;
		set termout off;
		BEGIN
			--Llamada al pl que genera los emails de sobreprecio
			o02agpe0.PQ_ENVIO_CORREOS.generaCorreoResumenEnviosRC;
		END;
		/
	exit
	EOF`
	
	log "Resultado del envío del correo resumen: $resultMail"  | tee -a $LOG_NAME_1;

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

    LOG_NAME_1="${PATH_LOG}"/AGP_RECEPCION_PLZ_RC"${fecha}".log
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

