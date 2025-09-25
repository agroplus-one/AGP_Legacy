package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.impresionscpoliza.AgrException;
import es.agroseguro.serviciosweb.impresionscpoliza.ImpresionSCPoliza;
import es.agroseguro.serviciosweb.impresionscpoliza.ImpresionSCPoliza_Service;
import es.agroseguro.serviciosweb.impresionscpoliza.ObtenerSituacionActualPDFRequest;
import es.agroseguro.serviciosweb.impresionscpoliza.ObtenerSituacionActualPDFResponse;
import es.agroseguro.tipos.PolizaReferenciaTipo;

/**
 * 
 * Helper para obtener un pdf con la Situacion Actualizada de la poliza con c�lculo de coste
 * 
 * @author T-Systems (U028975) 
 * 
 */
public class ServicioSituacionActualPDFHelper {

	private static final Log logger = LogFactory.getLog(ServicioSituacionActualPDFHelper.class);
	
	/**
	 * Configura y realiza la llamada al web service de pdf actual de la poliza
	 * @throws Exception 
	 */
	public Base64Binary doWork(BigDecimal plan, String referencia, String tipoRef, String realPath) throws Exception{		
		
		logger.debug("**@@** ServicioSituacionActualPDFHelper - doWork");
		URL wsdlLocation = null;
		
//		Establecemos proxy
		if(WSUtils.isProxyFixed())
			WSUtils.setProxy();		

		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("impresion.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		
		} catch (MalformedURLException e) {		
			logger.error("Imposible recuperar el WSDL de PDF de Situacion Actualizada. Revise la Ruta: " + url, e);
			throw new WebServiceException("Imposible recuperar el WSDL de PDF de Situacion Actualizada. Revise la Ruta: " + url, e);
		}
		
//		Recogemos de webservice.properties los valores para el ServiceName y Port
		String wsLocation = WSUtils.getBundleProp("impresion.location");
		String wsPort = WSUtils.getBundleProp("impresion.port");
		String wsService = WSUtils.getBundleProp("impresion.service");
		
		QName serviceName = new QName(wsLocation,wsService);
		QName portName = new QName(wsLocation,wsPort);
		
//		Envoltorio para la llamda al servicio web de impresion
		ImpresionSCPoliza_Service srv = new ImpresionSCPoliza_Service(wsdlLocation,serviceName);
		ImpresionSCPoliza srvImpresion = srv.getPort(portName,ImpresionSCPoliza.class);
		
//		Cabecera de seguridad
		WSUtils.addSecurityHeader(srvImpresion);

//		Montamos el XML de envio con los parametros
		ObtenerSituacionActualPDFRequest parameters = new ObtenerSituacionActualPDFRequest();
		
		parameters.setCodplan(plan.intValue());		
		parameters.setReferencia(referencia);
		parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(tipoRef));
		
		logger.debug("ServicioSituacionActualPDFHelper - doWork - codplan = " + plan + 
				", referencia = " + referencia + ", tipoReferencia: " + tipoRef);
		
			
		
//		Respuesta del webService	
		ObtenerSituacionActualPDFResponse response = null;
	
		try {		
			response = srvImpresion.obtenerSituacionActualPDF(parameters);
			if(response!=null){
				return response.getDocumento();		
			}
		} catch (AgrException e) {		
			logger.error("Error inesperado al llamar al web service",e);
			WSUtils.debugAgrException(e);
			throw new Exception("Error inesperado al llamar al web service",e);
		}						
		return null;
	}
	
	
	//Helper para obtener un pdf con la situación de la póliza desde el menu duplicados informaticos
	public Base64Binary doWorkCopy(String CodPlan, String RefPoliza, String tipoRef,String realPath) throws Exception{		
		URL wsdlLocation = null;
		
//		Establecemos proxy
		if(WSUtils.isProxyFixed())
			WSUtils.setProxy();		

		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("impresion.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		
		} catch (MalformedURLException e) {		
			logger.error("Imposible recuperar el WSDL de PDF de Situacion Actualizada . Revise la Ruta: " + url, e);
			throw new WebServiceException("Imposible recuperar el WSDL de PDF de Situacion Actualizada. Revise la Ruta: " + url, e);
		}
		
//		Recogemos de webservice.properties los valores para el ServiceName y Port
		String wsLocation = WSUtils.getBundleProp("impresion.location");
		String wsPort = WSUtils.getBundleProp("impresion.port");
		String wsService = WSUtils.getBundleProp("impresion.service");
		
		QName serviceName = new QName(wsLocation,wsService);
		QName portName = new QName(wsLocation,wsPort);
		
//		Envoltorio para la llamda al servicio web de impresion
		ImpresionSCPoliza_Service srv = new ImpresionSCPoliza_Service(wsdlLocation,serviceName);
		ImpresionSCPoliza srvImpresion = srv.getPort(portName,ImpresionSCPoliza.class);
		
//		Cabecera de seguridad
		WSUtils.addSecurityHeader(srvImpresion);

//		Montamos el XML de envio con los parametros
		ObtenerSituacionActualPDFRequest parameters = new ObtenerSituacionActualPDFRequest();	
		
		parameters.setCodplan(new Integer(CodPlan));		
		parameters.setReferencia(RefPoliza);
		parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(tipoRef));
		
//		Respuesta del webService	
		ObtenerSituacionActualPDFResponse response = null;
	
		try {		
			response = srvImpresion.obtenerSituacionActualPDF(parameters);
			if(response!=null){
				return response.getDocumento();		
			}
		} catch (AgrException e) {		
			logger.error("Error inesperado al llamar al web service",e);
			WSUtils.debugAgrException(e);
			throw new Exception("Error inesperado al llamar al web service",e);
		}						
		return null;
	}
}
