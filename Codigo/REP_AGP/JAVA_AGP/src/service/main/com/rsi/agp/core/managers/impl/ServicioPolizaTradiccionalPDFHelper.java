package com.rsi.agp.core.managers.impl;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.polizapdf.AgrException;
import es.agroseguro.serviciosweb.polizapdf.ObtenerPolizaActualPDFRequest;
import es.agroseguro.serviciosweb.polizapdf.ObtenerPolizaActualPDFResponse;
import es.agroseguro.serviciosweb.polizapdf.PolizaPDF;
import es.agroseguro.serviciosweb.polizapdf.PolizaPDF_Service;

/**
 * 
 * Helper para obtener un pdf con la situacion de la poliza  tradiccional 
 * 
 * @author U028982
 *
 */
public class ServicioPolizaTradiccionalPDFHelper {

	private static final Log logger = LogFactory.getLog(ServicioPolizaTradiccionalPDFHelper.class);
	
	//Helper para obtener un pdf con la situacion de la poliza desde el menu duplicados informaticos
	public Base64Binary doWork2(String CodPlan, String RefPoliza, String tipoRef,String realPath) throws Exception{		
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
		PolizaPDF_Service srv = new PolizaPDF_Service(wsdlLocation,serviceName);
		PolizaPDF srvImpresion = srv.getPort(portName,PolizaPDF.class);
		
//		Cabecera de seguridad
		WSUtils.addSecurityHeader(srvImpresion);

//		Montamos el XML de envio con los parametros
		ObtenerPolizaActualPDFRequest parameters = new ObtenerPolizaActualPDFRequest();	
		
		parameters.setCodplan(new BigInteger(CodPlan));				
		parameters.setReferencia(RefPoliza);
		
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