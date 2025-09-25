package com.rsi.agp.core.managers.impl;


import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SWValidacionSiniestroException;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ImpresionProrrogaResponse;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ParametrosImpresionProrroga;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ParametrosRecuperacionExplotaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.RecuperacionExplotacionesResponse;

/**
 * 
 * Helper para obtener un pdf con la impresion de la poliza renovable
 * @author U028982
 *
 */
public class ContratacionRenovacionesHelper {
	private static final Log logger = LogFactory.getLog(ContratacionRenovacionesHelper.class);
	
	//Helper para obtener un pdf con la impresion de la poliza renovable
	// valorWS: P-> estado agroseguro Primera comunicacion D-> estado agroseguro Comunicacion definitiva
	public Base64Binary doWorkImprimirProrroga(String planWs, String referenciaWs, String valorWs, String realPath) throws Exception{		
		URL wsdlLocation = null;
		
		// Establecemos proxy
		if(WSUtils.isProxyFixed())
			WSUtils.setProxy();		
 
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("contratacionRenovacionesWS.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		
		} catch (MalformedURLException e) {		
			logger.error("Imposible recuperar el WSDL de contratacionRenovaciones. Revise la Ruta: " + url,e);
			throw new WebServiceException("Imposible recuperar el WSDL de contratacionRenovaciones. Revise la Ruta: " + url,e);
		}
		
		// Recogemos de webservice.properties los valores para el ServiceName y Port
		String wsLocation = WSUtils.getBundleProp("contratacionRenovacionesWS.location");
		String wsPort = WSUtils.getBundleProp("contratacionRenovacionesWS.port");
		String wsService = WSUtils.getBundleProp("contratacionRenovacionesWS.service");
		
		QName serviceName = new QName(wsLocation,wsService);
		QName portName = new QName(wsLocation,wsPort);
		
		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsService: " + wsService.toString());
		
		// Envoltorio para la llamda al servicio web de impresion
		
		es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service srv = new es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service(wsdlLocation,serviceName);

		ContratacionRenovaciones srvImpresion = srv.getPort(portName,ContratacionRenovaciones.class);

		// Cabecera de seguridad
		WSUtils.addSecurityHeader(srvImpresion);

		// Montamos el XML de envio con los parametros
		ParametrosImpresionProrroga parameters = new ParametrosImpresionProrroga();
		
		parameters.setPlan(Integer.parseInt(planWs));				
		parameters.setReferencia(referenciaWs);
		parameters.setTipoComunicacion(valorWs);
		
//		Respuesta del webService	
		ImpresionProrrogaResponse response = null;

		response = srvImpresion.impresionProrroga(parameters);
		if(response!=null){
			return response.getDocumento();		
		}
		
		return null;
		
		
	}

	public RecuperacionExplotacionesResponse recuperarExplotaciones(BigDecimal plan, String referencia, String realPath) throws SOAPFaultException, AgrException, Exception{
		//Llamará al método ‘ContratacionRenovaciones.recuperacionExplotaciones’ con el objeto 
		//‘ParametrosRecuperacionExplotaciones’; las propiedades de este objeto se establecerán 
		//con los valores de plan y referencia recibidos como parámetro.
		
		RecuperacionExplotacionesResponse response=null;
		ContratacionRenovaciones srvSr = getSrvContratacionRenovaciones(realPath);
		
		//Recogemos los parámetros de entrada a enviar al servicio
		ParametrosRecuperacionExplotaciones paramIn=new ParametrosRecuperacionExplotaciones();
		paramIn.setPlanAnterior(plan.intValue());
		paramIn.setReferenciaBase(referencia);
		// -----------------------------------------------------

			response= srvSr.recuperacionExplotaciones(paramIn);
			return response;
		
	}
	
	private ContratacionRenovaciones getSrvContratacionRenovaciones(String realPath) {
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
        String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("contratacionRenovacionesWS.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new SWValidacionSiniestroException("Imposible recuperar el WSDL del servicion de contratacion de renovaciones. Revise la Ruta: " + url, e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("contratacionRenovacionesWS.location");
		String wsPort = WSUtils.getBundleProp("contratacionRenovacionesWS.port");
		String wsService = WSUtils.getBundleProp("contratacionRenovacionesWS.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service srv = new es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service(wsdlLocation,serviceName);
		ContratacionRenovaciones servicio= srv.getPort(portName,ContratacionRenovaciones.class);
		logger.debug(servicio.toString());		

		//Cabecera de seguridad
		WSUtils.addSecurityHeader(servicio);

				
		return servicio;
	}
	
	
}
