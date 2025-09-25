package com.rsi.agp.core.managers.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;

import es.agroseguro.serviciosweb.impresionscpoliza.AgrException;
import es.agroseguro.serviciosweb.impresionscpoliza.ImpresionSCPoliza;
import es.agroseguro.serviciosweb.impresionscpoliza.ImpresionSCPoliza_Service;
import es.agroseguro.serviciosweb.impresionscpoliza.ObtenerPolizaActualPDFRequest;
import es.agroseguro.serviciosweb.impresionscpoliza.ObtenerPolizaActualPDFResponse;
import es.agroseguro.tipos.PolizaReferenciaTipo;

/**
 * 
 * Helper para obtener un pdf con la situacion de la poliza a dicha fecha
 * 
 * @author U028844
 *
 */
public class ServicioPolizaActualPDFHelper {

	private static final Log logger = LogFactory.getLog(ServicioPolizaActualPDFHelper.class);
	
	/**
	 * Configura y realiza la llamada al web service de pdf actual de la poliza
	 * @throws Exception 
	 */
	public Base64Binary doWork(ReciboPoliza reciboPoliza,String realPath) throws Exception{		
		URL wsdlLocation = null;
		
//		Establecemos proxy
		if(WSUtils.isProxyFixed())
			WSUtils.setProxy();		

		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("impresion.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		
		} catch (MalformedURLException e) {		
			logger.error("Imposible recuperar el WSDL de Contratacion. Revise la Ruta: " + url,e);
			throw new WebServiceException("Imposible recuperar el WSDL de Contratacion. Revise la Ruta: " + url,e);
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
		ObtenerPolizaActualPDFRequest parameters = new ObtenerPolizaActualPDFRequest();	
		
		parameters.setCodplan(new Integer(reciboPoliza.getRecibo().getCodplan()));		
		parameters.setReferencia(reciboPoliza.getRefpoliza());
		parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(reciboPoliza.getTiporef().toString()));
		
		logger.debug("ServicioPolizaActualPDFHelper - doWork - codplan = " + reciboPoliza.getRecibo().getCodplan() + 
				", referencia = " + reciboPoliza.getRefpoliza() + ", tipoReferencia: " + reciboPoliza.getTiporef());
		
		if(reciboPoliza.getRecibo().getCodrecibo()!=null){
			parameters.setRecibo(reciboPoliza.getRecibo().getCodrecibo().intValue());
			logger.debug("ServicioPolizaActualPDFHelper - doWork - codrecibo = " + reciboPoliza.getRecibo().getCodrecibo());
		}		
		if(reciboPoliza.getRecibo().getFecemisionrecibo()!=null){
			GregorianCalendar gcal = new GregorianCalendar();
			gcal.setTime(reciboPoliza.getRecibo().getFecemisionrecibo());
			XMLGregorianCalendar fecha = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
			parameters.setFecha(fecha);
			logger.debug("ServicioPolizaActualPDFHelper - doWork - fecha = " + fecha);
		}		
		
//		Respuesta del webService	
		ObtenerPolizaActualPDFResponse response = null;
	
		try {		
			response = srvImpresion.obtenerPolizaActualPDF(parameters);
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
	
	
	//Helper para obtener un pdf con la situacion de la poliza desde el menu duplicados informaticos
	public Base64Binary doWorkCopy(String CodPlan, String RefPoliza, String tipoRef,String realPath) throws Exception{		
		URL wsdlLocation = null;
		
//		Establecemos proxy
		if(WSUtils.isProxyFixed())
			WSUtils.setProxy();		

		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("impresion.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		
		} catch (MalformedURLException e) {		
			logger.error("Imposible recuperar el WSDL de Contratacion. Revise la Ruta: " + url, e);
			throw new WebServiceException("Imposible recuperar el WSDL de Contratacion. Revise la Ruta: " + url, e);
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
		ObtenerPolizaActualPDFRequest parameters = new ObtenerPolizaActualPDFRequest();	
		
		parameters.setCodplan(new Integer(CodPlan));		
		parameters.setReferencia(RefPoliza);
		parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(tipoRef));
		
//		Respuesta del webService	
		ObtenerPolizaActualPDFResponse response = null;
	
		try {		
			response = srvImpresion.obtenerPolizaActualPDF(parameters);
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
