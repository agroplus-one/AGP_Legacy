package com.rsi.agp.batch.comisiones.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

/**
 * @author U028893 T-systems
 * 
 * Ayuda para la validación de xml frente a xsd
 * 
 * */

public class XmlComisionesValidationUtil {
	
	private static final Log logger = LogFactory.getLog(XmlComisionesValidationUtil.class);
	
	public static XmlObject getXMLBeanValidado (File xml, XmlObject xmlBean, int tipo) throws XMLValidationException {
		// Se valida el XML 
		ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);
		logger.info("Iniciando getXMLBeanValidado para xmlFile: "+xml.getName()+" de tipo "+tipo);
	
		
		try {
			
			switch (tipo) {    
			case ConfigBuzonInfovia.FICHERO_COMISIONES:
				logger.info("Fichero Comisiones");
				xmlBean = es.agroseguro.recibos.comisiones.FaseDocument.Factory.parse(xml);
				break;
			case ConfigBuzonInfovia.FICHERO_IMPAGADOS:
				logger.info("Fichero impagados");
				xmlBean = es.agroseguro.recibos.gastos.FaseDocument.Factory.parse(xml);
				break;
			case ConfigBuzonInfovia.FICHERO_REGLAMENTO:
				logger.info("Fichero reglamento");
				xmlBean = es.agroseguro.recibos.reglamentoProduccionEmitida.FaseDocument.Factory.parse(xml);	
				break;
			case ConfigBuzonInfovia.FICHERO_EMITIDOS:
				logger.info("Fichero copys");
				xmlBean = es.agroseguro.recibos.emitidos.FaseDocument.Factory.parse(xml);	
				break;
			default:
				throw new XMLValidationException("Tipo incorrecto");
			}
			logger.info("Bean cargado");
		} catch (XmlException e1) {
			logger.error("getXMLBeanValidado", e1);
			throw new XMLValidationException("Error al convertir el XML a XML Bean");
		} catch (IOException e) {
			logger.error("getXMLBeanValidado", e);
			throw new XMLValidationException("Error al convertir el XML a XML Bean");
		}
		
		boolean bValidation = xmlBean.validate(validationOptions);
		logger.info("Bean validado: "+bValidation);
		if (!bValidation) {			
		    Iterator<XmlValidationError> iter = validationErrors.iterator();
		    String cadError = "";
		    while (iter.hasNext()){
		    	XmlValidationError err = iter.next();		        
		        cadError += err + "";
		    }

		    throw new XMLValidationException("XML invalido, no cumple el esquema xsd: " + cadError);
		}
		logger.info("Saliendo validar bean");
		return xmlBean;
	}
}
