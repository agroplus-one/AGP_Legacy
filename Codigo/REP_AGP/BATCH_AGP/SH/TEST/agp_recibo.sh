#!/bin/ksh
#########################################################
## Inserción de los recibos de Agroseguro en BBDD      ##
##                                                     ##
## A este sh se llega desde 'agp_batch_recepcion.sh'   ##
## cuando se detecta que se ha descargado un fichero   ##
## de recibos del buzón de entidades de Agroseguro.    ##
## Las operaciones que realizará el script son:        ##
##    1. Descomprimir el ZIP para obtener el XML       ##
##    2. Canonizar el XML con las XSL de recibos       ##
##    3. Insertar los ficheros canonicos en BBDD       ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       30/12/2010 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

function TratarRecibo
{
	reciboZIP=${1}
	
	## Descomprimir el fichero zip. Le indicamos el nombre del fichero y la ruta donde se encuentra
	descomprimir=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                begin
                        o02agpe0.PQ_UTLZIP.UNCOMPRESSFILE ('$reciboZIP', '/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH');
			DBMS_OUTPUT.PUT_LINE('Fichero $reciboZIP descomprimido correctamente');
                end;
                /
                exit;
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al descomprimir el fichero $reciboZIP. Error $ret_val"
		log $descomprimir
		exit 1
	fi
        log $descomprimir
        
	## Obtener los parámetros necesarios para la canonización (valores de las secuencias)
	## Será necesario construir 2 cadenas de texto de la siguiente forma para pasarsela al proceso de
	## canonización de ficheros:
	## idRecibo|idReciboPoliza|idSubvencion|idDetCompensac|idReciboPoliza
	## La otra cadena contendrá los valores para los parámetros
	paramNames = "idRecibo|idReciboPoliza|idSubvencion|idDetCompensac|idPolizaSubv"
	
	paramValues=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                declare
                	idRecibo	NUMBER;
                	idReciboPoliza	NUMBER;
                	idSubvencion	NUMBER;
                	idDetCompensac	NUMBER;
                	idPolizaSubv	NUMBER;
                	
                begin
                	idRecibo := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_recibo');
                	idReciboPoliza := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_recibo_poliza');
                	idSubvencion := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_recibo_subv');
                	idDetCompensac := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_recibo_det_compensacion');
                	idPolizaSubv := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_recibo_poliza_subv');
                        
                        --Pintamos la cadena con los valores
                        DBMS_OUTPUT.PUT_LINE(idRecibo || '|' || idReciboPoliza || '|' || idSubvencion || '|' idDetCompensac || '|' idPolizaSubv);
                end;
                /
                exit;
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al obtener los valores de las secuencias. Error $ret_val"
		log $paramValues
		exit 1
	fi
        log $paramValues
	
	## Canonizar el contenido del zip con los xsl de recibos.
	xmlIn="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/RecibosEmitidos.xml"
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/Recibo.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/Recibo_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/ReciboDetCompensacion.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/ReciboDetCompensacion_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/ReciboPoliza.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/ReciboPoliza_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones/AGP_AGROPLUS/INTERFACES/XSL/ReciboPolizaSubvencion.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/ReciboPolizaSubvencion_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/ReciboSubvencion.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/ReciboSubvencion_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	## Insertar el contenido de los ficheros canonicos.
	## El orden de los ficheros no es importante porque las FK son "DEFERRED"
	resultadoInsercion=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                begin
                        o02agpe0.PQ_CARGA_BATCH.PR_CARGAXMLS('AGP_BATCH', 'RECIBO');
                        DBMS_OUTPUT.PUT_LINE('Carga de ficheros canónicos finalizada');
                end;
                /
                exit;
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al realizar la inserción de los ficheros canónicos de RECIBOS. Error $ret_val"
		log $resultadoInsercion
		exit 1
	fi
        log $resultadoInsercion
	
	## Actualizar los valores de las secuencias
	actualizacionSq=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                begin
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_recibo', 'tb_recibo', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_recibo_poliza', 'tb_recibo_poliza', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_recibo_subv', 'tb_recibo_subv', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_recibo_det_compensacion', 'tb_recibo_det_compensacion', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_recibo_poliza_subv', 'tb_recibo_poliza_subv', 'ID');
                        
                        DBMS_OUTPUT.PUT_LINE('Valores de las secuencias de recibos actualizados');
                end;
                /
                exit;
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al actualizar los valores de las secuencias. Error $ret_val"
		log $actualizacionSq
		exit 1
	fi
        log $actualizacionSq
}
