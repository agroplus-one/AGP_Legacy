package com.rsi.agp.core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import org.xml.sax.SAXException;

import com.rsi.agp.core.exception.XMLValidationException;

/**
 * @author U028893 T-systems
 * 
 * Ayuda para la validación de xml frente a xsd
 * 
 * */

public class XmlComisionesValidationUtil {
	
	private static final Log LOGGER = LogFactory.getLog(XmlComisionesValidationUtil.class); 
	
	public static boolean validarXml(File xmlFile, File xsdFile) {
		try {
			LOGGER.debug("Entrando a validarXml(xmlFile: " + xmlFile.getName() + " xdfFile: " + xsdFile.getName());

			SchemaFactory factorySchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			factorySchema.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factorySchema.setProperty(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			factorySchema.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

			Source schemaFile = new StreamSource(xsdFile);
			Schema schema = factorySchema.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			Source source = new StreamSource(xmlFile);
			validator.validate(source);
		} catch (SAXException e) {
			LOGGER.error("validarXML" + e.getMessage());
			return false;
		} catch (IOException ex) {
			LOGGER.error("validarXML" + ex.getMessage());
			return false;
		}
		return true;
	}
	
	public static boolean validarXml(String xmlFile, String xsdFile){	
		LOGGER.debug("Entrando a validarXml(xml: "+xmlFile+" xdf: "+xsdFile);
		File xmlfile=new File(xmlFile);
		File xsdfile=new File(xsdFile);
		return validarXml(xmlfile,xsdfile);
		
	}
	
	public static XmlObject getXMLBeanValidado (File xml, int tipo) throws XMLValidationException {
		// Se valida el XML 
		XmlObject xmlBean = null;
		ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);
		LOGGER.debug("Iniciando getXMLBeanValidado para xmlFile: "+xml.getName()+" de tipo "+tipo);
		try {
			
			switch (tipo) {    
				case Constants.FICHERO_COMISIONES:
					LOGGER.debug("Fichero Comisiones");
					xmlBean = es.agroseguro.recibos.comisiones.FaseDocument.Factory.parse(xml);
					break;
				case Constants.FICHERO_IMPAGADOS:
					LOGGER.debug("Fichero impagados");
					xmlBean = es.agroseguro.recibos.gastos.FaseDocument.Factory.parse(xml);
					break;
				case Constants.FICHERO_REGLAMENTO:
					LOGGER.debug("Fichero reglamento");
					xmlBean = es.agroseguro.recibos.reglamentoProduccionEmitida.FaseDocument.Factory.parse(xml);	
					break;
				case Constants.FICHERO_EMITIDOS:
					LOGGER.debug("Fichero copys");
					xmlBean = es.agroseguro.recibos.emitidos.FaseDocument.Factory.parse(xml);	
					break;
				case Constants.FICHERO_DEUDA:
					LOGGER.debug("Fichero deuda aplazada");
					xmlBean = es.agroseguro.recibos.comisionesCobroDeudaAplazada.FasesDocument.Factory.parse(xml);	
					break;
				case Constants.FICHERO_UNIFICADO_GASTOS_RECIBOS_EMITIDOS:
					LOGGER.debug("getXMLBeanValidado - Fichero de comisiones unificadas de gastos de recibos emitidos");
					xmlBean = es.agroseguro.recibos.gastosRecibos.FasesDocument.Factory.parse(xml);
					break;
				case Constants.FICHERO_UNIFICADO_GASTOS_RECIBOS_IMPAGADOS:
					LOGGER.debug("getXMLBeanValidado - Fichero de comisiones unificadas de gastos de recibos impagados");
					xmlBean = es.agroseguro.recibos.gastosRecibosImpagados.FaseDocument.Factory.parse(xml);
					break;
				case Constants.FICHERO_UNIFICADO_GASTOS_RECIBOS_DEUDA_APLAZADA:
					LOGGER.debug("getXMLBeanValidado - Fichero de comisiones unificadas de gastos de recibos de deuda aplazada");
					xmlBean = es.agroseguro.recibos.gastosCobroDeudaAplazada.FasesDocument.Factory.parse(xml);
					break;
				case Constants.FICHERO_UNIFICADO_GASTOS_ENTIDAD_UNIFICADO:
					LOGGER.debug("getXMLBeanValidado - Fichero de comisiones unificadas de gastos de entidad unificado");
					xmlBean = es.agroseguro.recibos.abonoGastosEntidad.EntidadDocument.Factory.parse(xml);
					break;
				default:
					break;
			}
			LOGGER.debug("Bean cargado");
		} catch (XmlException e1) {
			LOGGER.error("getXMLBeanValidado " + e1.getMessage());
			throw new XMLValidationException("Error al convertir el XML a XML Bean");
		} catch (IOException e) {
			LOGGER.error("getXMLBeanValidado "+ e.getMessage());
			throw new XMLValidationException("Error de entrada/salida al leer el contenido del XML: " + e.getMessage());
		} catch (Exception ex)  {
			throw new XMLValidationException("Error no esperado al convertir el XML a XML Bean: " + ex.getMessage());
		}
		
		boolean bValidation = xmlBean.validate(validationOptions);
		LOGGER.debug("Bean validado: "+ bValidation);
		if (!bValidation) {
		    Iterator<XmlValidationError> iter = validationErrors.iterator();
		    String cadError = "";
		    while (iter.hasNext()){
		    	XmlValidationError err = iter.next();
		        cadError += err + "";
		    }

		    throw new XMLValidationException("XML invalido, no cumple el esquema xsd: " + cadError);
		}
		LOGGER.debug("Saliendo validar bean");
		return xmlBean;
	}
}
