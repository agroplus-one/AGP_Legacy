#!/bin/ksh
#########################################################
## Inserción de copys de Agroseguro en BBDD            ##
##                                                     ##
## A este sh se llega desde 'agp_batch_recepcion.sh'   ##
## cuando se detecta que se ha descargado un fichero   ##
## de copys del buzón de entidades de Agroseguro.      ##
## Las operaciones que realizará el script son:        ##
##    1. Descomprimir el ZIP para obtener el XML       ##
##    2. Canonizar el XML con las XSL de copy          ##
##    3. Insertar los ficheros canonicos en BBDD       ##
##                                                     ##
##  REVISIONS:                                         ##
##  Ver       Date       Author     Description        ##
##  --------- ---------- ---------- ----------------   ##
##  1.0       27/12/2010 T-SYSTEMS  1. Created.        ##
##                                                     ##
##                                                     ##
#########################################################

function TratarCopy
{
	copyZIP=${1}
	
	## Descomprimir el fichero zip. Le indicamos el nombre del fichero y la ruta donde se encuentra
	descomprimir=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                begin
                        o02agpe0.PQ_UTLZIP.UNCOMPRESSFILE ('$copyZIP', '/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH');
			DBMS_OUTPUT.PUT_LINE('Fichero $copyZIP descomprimido correctamente');
                end;
                /
                exit;
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al descomprimir el fichero $copyZIP. Error $ret_val"
		log $descomprimir
		exit 1
	fi
        log $descomprimir
        
	## Obtener los parámetros necesarios para la canonización (valores de las secuencias)
	## Será necesario construir 2 cadenas de texto de la siguiente forma para pasarsela al proceso de
	## canonización de ficheros:
	## idCopy|idAsegurado|idCapAseg|idCopyParcela|idCobertura|idColectivo|idDatoVariable|idPago|idSubvDeclarada|idSubvEnesa|idSubvOrganismo
	## La otra cadena contendrá los valores para los parámetros
	paramNames = "idCopy|idAsegurado|idCapAseg|idCopyParcela|idCobertura|idColectivo|idDatoVariable|idPago|idSubvDeclarada|idSubvEnesa|idSubvOrganismo"
	
	paramValues=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                declare
                	idCopy		NUMBER;
                	idAsegurado	NUMBER;
                	idCapAseg	NUMBER;
                	idCopyParcela	NUMBER;
                	idCobertura	NUMBER;
                	idColectivo	NUMBER;
                	idDatoVariable	NUMBER;
                	idPago		NUMBER;
                	idSubvDeclarada	NUMBER;
                	idSubvEnesa	NUMBER;
                	idSubvOrganismo	NUMBER;
                begin
                        idCopy := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_polizas');
                        idAsegurado := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_asegurados');
                        idCapAseg := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_capitales_aseg');
                        idCopyParcela := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_parcelas');
                        idCobertura := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_coberturas');
                        idColectivo := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_colectivos');
                        idDatoVariable := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_datos_var_parc');
                        idPago := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_pago');
                        idSubvDeclarada := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_subv_declarada');
                        idSubvEnesa := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_subv_enesa');
                        idSubvOrganismo := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_subv_organismos');
                        
                        --Pintamos la cadena con los valores
                        DBMS_OUTPUT.PUT_LINE(idCopy || '|' || idAsegurado || '|' || idCapAseg || '|' idCopyParcela || '|' idCobertura || '|' idColectivo || '|' idDatoVariable || '|' idPago || '|' idSubvDeclarada || '|' idSubvEnesa || '|' idSubvOrganismo);
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
        
        ## Obtengo los identificadores de Coberturas y Datos Variables que necesitaremos más adelante
        idCobertura=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                declare
                	idCobertura	NUMBER;
                begin
                        idCobertura := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_coberturas');
                        
                        --Pintamos el valor
                        DBMS_OUTPUT.PUT_LINE(idCobertura);
                end;
                /
                exit;
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al obtener el valor para el idCobertura. Error $ret_val"
		log $idCobertura
		exit 1
	fi
        log $idCobertura
        
        idDatoVariable=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                declare
                	idDatoVariable	NUMBER;
                begin
                        idDatoVariable := o02agpe0.PQ_CARGA_BATCH.FN_GET_VALOR_SQ('sq_tb_copy_datos_var_parc');
                        
                        --Pintamos el valor
                        DBMS_OUTPUT.PUT_LINE(idDatoVariable);
                end;
                /
                exit;
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al obtener el valor para el idDatoVariable. Error $ret_val"
		log $idDatoVariable
		exit 1
	fi
        log $idDatoVariable
	
	
	## Canonizar el contenido del zip con los xsl de copys.
	## En los ficheros canónicos de Coberturas y de Datos Variables hay que actualizar el ID.
	xmlIn="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/Copy.xml"
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopyAsegurado.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopyAsegurado_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopyCapitalesAsegurados.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopyCapitalesAsegurados_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopyCoberturas.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopyCoberturas_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	$javaPathProp/java -jar $fileManagerProp/XmlIdUpdater.jar $xmlOut $idCobertura
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopyColectivo.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopyColectivo_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopyDatoVariableParcela.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopyDatoVariableParcela_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	$javaPathProp/java -jar $fileManagerProp/XmlIdUpdater.jar $xmlOut $idDatoVariable
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopyPago.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopyPago_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopyParcela.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopyParcela_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopyPoliza.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopyPoliza_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopySubvDeclaradas.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopySubvDeclaradas_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopySubvEnesa.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopySubvEnesa_canonico.xml"
	$javaPathProp/java -jar $fileManagerProp/XmlTransformer.jar $xmlIn $xsl $xmlOut $paramNames $paramValues
	
	xsl="/aplicaciones2/AGP_AGROPLUS/INTERFACES/XSL/CopySubvOrganismos.xsl"
	xmlOut="/aplicaciones2/AGP_AGROPLUS/INTERFACES/BATCH/CopySubvOrganismos_canonico.xml"
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
                        o02agpe0.PQ_CARGA_BATCH.PR_CARGAXMLS('AGP_BATCH', 'COPY');
                        DBMS_OUTPUT.PUT_LINE('Carga de ficheros canónicos finalizada');
                end;
                /
                exit;
                EOF`

	ret_val=$?
	
	if [ $ret_val -gt 0 ]; then
		echo "Error al realizar la inserción de los ficheros canónicos de COPYS. Error $ret_val"
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
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_polizas', 'tb_copy_polizas', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_asegurados', 'tb_copy_asegurados', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_capitales_aseg', 'tb_copy_capitales_aseg', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_parcelas', 'tb_copy_parcelas', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_coberturas', 'tb_copy_coberturas', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_colectivos', 'tb_copy_colectivos', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_datos_var_parc', 'tb_copy_datos_var_parc', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_pago', 'tb_copy_pago', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_subv_declarada', 'tb_copy_subv_declarada', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_subv_enesa', 'tb_copy_subv_enesa', 'ID');
                        o02agpe0.PQ_CARGA_BATCH.PR_SET_VALOR_SQ('sq_tb_copy_subv_organismos', 'tb_copy_subv_organismos', 'ID');
                        
                        DBMS_OUTPUT.PUT_LINE('Valores de las secuencias de copys actualizados');
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
