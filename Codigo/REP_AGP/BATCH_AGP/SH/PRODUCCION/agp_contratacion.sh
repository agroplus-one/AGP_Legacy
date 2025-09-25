#!/bin/ksh
#########################################################
## Env�o de P�lizas a Agroseguro                       ##
##                                                     ##
## Se comprobar� en la base de datos cual es la forma  ##
## de confirmar las p�lizas: bien por servicio web o   ##
## bien por https.                                     ##
## Si es por servicio web, habr� que consultar en BD   ##
## las p�lizas que est�n en estado 'Grabaci�n          ##
## definitiva' y, por cada una de ellas, obtener su    ##
## xml de validaci�n y enviarlo a Agroseguro.          ##
## En caso contrario, se llamar� al sh encargado de    ##
## generar el fichero txt y el zip con las p�lizas     ##
## para enviarlo por https al buz�n de Agroseguro.     ##
## Si el servicio web de Agroseguro no estuviera       ##
## disponible, tambi�n se enviar�an por https.         ##
##                                                     ##
##  Parametros: CODPLAN                                ##
##  El pl-sql Crea_Ficheros_Envio requiere el codigo   ##
##  de Plan (Formato YYYY) para su ejecuci�n.Si no se  ##
##  indica se especifica el a�o actual                 ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       30/12/2010 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

function doWork
{
	## Obtenemos el tipo de env�o a realizar haciendo una consulta en la tabla de parametrizaci�n
	tipoEnvio=`sqlplus -s /@$oracleSIDProp << EOF
	        set heading off;
	        set feedback off;
		set serveroutput on;
		whenever OSERROR exit OSCODE rollback;
		whenever SQLERROR exit SQL.SQLCODE rollback;
		declare
			param VARCHAR2(50);
	  	begin
	  		SELECT CONFIRMACION INTO param FROM o02agpe0.TB_PARAMETROS;
			dbms_output.put_line(param);
	  	end;
		/
		exit;
		EOF`
	
	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al consultar la tabla de parametrizaci�n. Error $ret_val"
		exit 1
	fi
	
	## Si tipoEnvio=0 => Confirmaci�n por servicio web: llamar a clase java que realiza las consultas a BD,
	## va haciendo uno a uno los env�os a Agroseguro y tratando los acuses de recibo.
	## Si tipoEnvio=1 => Confirmaci�n por https: llamar al sh 'agp_envio_ficheros.sh' que se encargar� de 
	## generar los ficheros txt y zip que se env�an a Agroseguro. En este caso, los acuses de recibo se tratar�n
	## a posteriori mediante otro proceso batch.
	
	echo "Tipo de env�o: $tipoEnvio"
	
	if [ $tipoEnvio = 0 ]; then
		##Llamada a clase java
		resultadoContratacion=`$javaPathProp/java -jar $fileManagerProp/ContratacionWS.jar`
		echo "Resultado de la llamada al servicio de contratacion: $resultadoContratacion"
	else
		##Llamada a sh
		echo "Llamada al sh de envio de ficheros a Agroseguro"
		/aplicaciones2/AGP_AGROPLUS/batchs/agp_envio_ficheros.sh $1
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
    log "" | tee -a $LOG_NAME_1
    log "\t\t Hora comienzo: " `date '+%d/%m/%y %H:%M:%S'` | tee -a $LOG_NAME_1

    log "\n\n --- Comprobando conexion a la base de datos ---" | tee -a $LOG_NAME_1
    CompruebaConexion /@${oracleSIDProp} ${LOG_NAME_1}

    ## Realiza las validaciones de configuraci�n y de estado del servicio y lanza el env�o de polizas a agroseguro
    doWork $1
    
}

# Script Principal
main $1

