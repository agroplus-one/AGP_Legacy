package com.rsi.agp.core.managers.impl.ganado;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SWValidacionSiniestroException;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.contratacionayudas.AgrException;
import es.agroseguro.serviciosweb.contratacionayudas.CoberturasContratadasRequest;
import es.agroseguro.serviciosweb.contratacionayudas.CoberturasContratadasResponse;
import es.agroseguro.serviciosweb.contratacionayudas.ContratacionAyudas;
import es.agroseguro.serviciosweb.contratacionayudas.ModulosCoberturasRequest;
import es.agroseguro.serviciosweb.contratacionayudas.ModulosCoberturasResponse;

public class ContratacionAyudasHelper {
	private static final Log logger = LogFactory.getLog(ContratacionAyudasHelper.class);
	
	/**
	 * 
	 * @param xml
	 * @param realPath
	 * @throws Exception
	 */
	public CoberturasContratadasResponse doCoberturasContratadas (String xml, String realPath) throws Exception {
		
		
		// Se valida el XML antes de llamar al Servicio Web
		WSUtils.getXMLPoliza(xml);
		
		// Crea el objeto utilizado para llamar al SW
		ContratacionAyudas srv = getSrvContratacionAyudas(realPath);
		
		// Se convierte el String a Base64, para enviarlo al SW
		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xml.getBytes("UTF-8");
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			throw new Exception("Se esperaba un XML en formato UTF-8.", e2);
		}
		
		CoberturasContratadasRequest paramIn = new CoberturasContratadasRequest();
		paramIn.setPresupuestoContratacion(base64Binary);
		
		CoberturasContratadasResponse response = null;
		try {
			response = srv.coberturasContratadas(paramIn);
		} 
		catch (AgrException e) {
			logger.error("########## - Error en ContratacionAyudasHelper.coberturasContratadas. ", e);
			logger.error(WSUtils.debugAgrException(e));
			throw e;
		} 
		catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de ayudas a la contratación. " , e);
			throw new Exception("Error inesperado al llamar al servicio web de ayudas a la contratación. " , e);
		}
		
		return response;
	}
	
	/**
	 * 
	 * @param xml
	 * @param realPath
	 * @return
	 * @throws Exception
	 */
	public ModulosCoberturasResponse doModulosCoberturas(String xml, String realPath) throws Exception{
		ModulosCoberturasResponse response = null;
		ContratacionAyudas srvSr = getSrvContratacionAyudas(realPath);
		
		//Recogemos los parÃ¡metros de entrada a enviar al servicio
		ModulosCoberturasRequest paramIn=new ModulosCoberturasRequest();
		
		
		// Se valida el XML antes de llamar al Servicio Web
		WSUtils.getXMLPoliza(xml);
		
		// Se convierte el String a Base64, para enviarlo al WS
		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xml.getBytes("UTF-8");
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			throw new Exception("Se esperaba un XML en formato UTF-8.", e2);
		}
		

		paramIn.setPresupuestoContratacion(base64Binary);
		// -----------------------------------------------------
		//Llamamos al servicio
		try{
			response= srvSr.modulosCoberturas(paramIn);
			
		
			
		}catch(SOAPFaultException e){
			logger.error("########## - Error en ContratacionAyudasHelper.modulosCoberturas. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en ContratacionAyudasHelper.modulosCoberturas. ", e);
			logger.error(WSUtils.debugAgrException(e));
			throw e;
			
		} catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de ayudas a la contratación. " , e);
			throw new Exception("Error inesperado al llamar al servicio web de ayudas a la contratación. " , e);
		}
		return response;
		
	}
	
	private ContratacionAyudas getSrvContratacionAyudas(String realPath) {
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
		try {
			wsdlLocation = new URL("file:" + realPath + System.getProperty("file.separator") + 
					WSUtils.getBundleProp("ayudasContratacion.wsdl"));
		} catch (MalformedURLException e1) {
			throw new SWValidacionSiniestroException("Imposible recuperar el WSDL del servicion de ayudas a la contratación. Revise la Ruta: " + 
					((wsdlLocation != null) ? wsdlLocation.toString() : ""), e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("ayudasContratacion.location");
		String wsPort = WSUtils.getBundleProp("ayudasContratacion.port");
		String wsService = WSUtils.getBundleProp("ayudasContratacion.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		es.agroseguro.serviciosweb.contratacionayudas.ContratacionAyudas_Service srv = new es.agroseguro.serviciosweb.contratacionayudas.ContratacionAyudas_Service(wsdlLocation,serviceName);
		ContratacionAyudas servicio= srv.getPort(portName,ContratacionAyudas.class);
		logger.debug(servicio.toString());		

		//Cabecera de seguridad
		WSUtils.addSecurityHeader(servicio);

				
		return servicio;
	}
	
}
