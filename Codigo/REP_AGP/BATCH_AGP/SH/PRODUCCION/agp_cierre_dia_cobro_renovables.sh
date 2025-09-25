#!/bin/ksh
#########################################################
## Generación de ficheros de Cierre de Día y Cobro     ##
## de pólizas renovables                               ##
## Se recogerán las pólizas renovables estado defini-  ##
## tivas, y aquellas que hayan sido financiadas y co-  ##
## rresponda la fecha de segundo pago, y aquellas que  ##
## consten como fraccionadas (según línea).            ##
##                                                     ##
## Se generará un recibo (CSB19) a adeudar en la       ##
## cuenta del cliente y a abonar a la cuenta remesado- ##
## ra de la oficina correspondiente.                   ##
##                                                     ## 
## El importe a cobrar será: la totalidad en caso de   ## 
## no financiada ni fraccionada, o la parte que corres-##
## ponda en caso de financiadas o fraccionadas.        ##
##                                                     ##
## En la medida de lo posible, se reutilizará el       ##
## proceso de la aplicación actual para evitar         ##
## discrepancias                                       ##
##                                                     ##
## Se retrasará hasta el máximo posible siempre y      ##
## cuando se asigne fecha del día (provisionalmente    ##
## hasta las 20:00 y no como actualmente a las 17:00)  ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       12/05/2014 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

function doWork
{

	## Se llama al PLSQL PQ_CIERREDIACOBRO
        cierre=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
declare
resultado VARCHAR2(2000);
begin
resultado := o02agpe0.pq_envio_polizas_renovables.load_polizas_renovables;
DBMS_OUTPUT.PUT_LINE(resultado);
end;
				/
                exit;
                EOF`
	
	ret_val=$?
	
	log "Resultado de la ejecucion del proceso de pagos de pol. renovables: $cierre" | tee -a $LOG_NAME_1
	
	if [ $ret_val -gt 0 ]; then
		log "Error al generar el fichero de Cierre de Dia y Cobro de polizas renovables. Error $ret_val" | tee -a $LOG_NAME_1
		exit 1
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

    LOG_NAME_1="${PATH_LOG}"/AGP_CIERRE_DIA_COBRO_REN"${fecha}".log
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
    log "\t\t Hora comienzo: " ${date '+%d/%m/%y %H:%M:%S'} | tee -a $LOG_NAME_1

    log "\n\n --- Comprobando conexion a la base de datos ---" | tee -a $LOG_NAME_1
    CompruebaConexion /@${oracleSIDProp} ${LOG_NAME_1}

    ## Llama al plsql y realiza el envío
    doWork $1
    
}

# Script Principal
main $1

