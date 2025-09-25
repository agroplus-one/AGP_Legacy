package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.ConfirmacionServiceException;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.poliza.IPolizaDao;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ConfirmarRequest;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ConfirmarResponse;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ContratacionSCConfirmacion;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ContratacionSCConfirmacion_Service;

public class ServicioConfirmarCplHelper {
	
	private static final Log logger = LogFactory.getLog(ServicioConfirmarCplHelper.class);
	
	/**
	 * Configura y realiza la llamada al servicio web de Confirmacion.
	 * @param idEnvio
	 * @param idPoliza
	 * @param realPath
	 * @return
	 * @throws ConfirmacionServiceException
	 */

    public AcuseRecibo doWork(Long idEnvio, Long idPoliza, String realPath, IPolizaDao polizaDao) throws ConfirmacionServiceException {
		//Cogemos el XML de la BBDD		
		logger.debug("Obtenemos en un Clob el XML que ha generado el PL");
		String xml = WSUtils.obtenXMLPoliza(idEnvio, polizaDao); 
		
		// No se recupero el XML...
		if (xml == null) throw new ConfirmacionServiceException("No se ha podido obtener el XML de la Poliza");

		// Realiza la llamada al WS
		logger.debug("Llamando a Servicio de Confirmacion: CONFIRMAR>> " + xml);
		return doWorkGenerico(xml, realPath);
	}
	
	private AcuseRecibo doWorkGenerico (String xml, String realPath) throws ConfirmacionServiceException {
		
		// Establece el Proxy
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
		URL wsdlLocation = null;
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("confirmacion.wsdl");
        try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			logger.error("Imposible recuperar el WSDL de Confirmacion. Revise la Ruta: " + url, e1);
			throw new ConfirmacionServiceException("Imposible recuperar el WSDL de Confirmacion. Revise la Ruta: " + url, e1);				
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("confirmacion.location");
		String wsPort     = WSUtils.getBundleProp("confirmacion.port");
		String wsService  = WSUtils.getBundleProp("confirmacion.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de confirmacion
		ContratacionSCConfirmacion_Service srv = new ContratacionSCConfirmacion_Service(wsdlLocation, serviceName);
		ContratacionSCConfirmacion srvConfirmacion = srv.getPort(portName, ContratacionSCConfirmacion.class);

		// Añade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvConfirmacion);

		logger.debug("Llamando a Servicio de Confirmacion: CONFRIMAR>> " + xml);				

		// Se valida el XML antes de llamar al Servicio Web
		WSUtils.getXMLPolizaCpl(xml);
		
		// Se convierte el String a Base64, para enviarlo al WS
		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xml.getBytes("UTF-8");
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error("Se esperaba un XML en formato UTF-8.", e2);
			throw new ConfirmacionServiceException("Se esperaba un XML en formato UTF-8.", e2);
		}
		// Parametros de envio al Servicio Web
		
		// Parametros de envio al Servicio Web
		ConfirmarRequest parameters = new ConfirmarRequest();
		parameters.setPoliza(base64Binary);				
		
		ConfirmarResponse response = null;		
		try {
			logger.debug("Llamando a Servicio de Confirmacion: CONFIRMAR>> " + xml);				
			response = srvConfirmacion.confirmar(parameters);
			if (response != null)
			{
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				Base64Binary respuesta = response.getAcuseRecibo();
				byte[]arrayAcuse = respuesta.getValue();
				String acuse = new String (arrayAcuse, "UTF-8");
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(acuse));
				
				return acuseReciboDoc.getAcuseRecibo(); 
			}
		} catch (AgrException e) {
			logger.error("Error inesperado devuelto por el servicio web de Confirmacion" , e);
			WSUtils.debugAgrException(e);
			throw new ConfirmacionServiceException("Error inesperado devuelto por el servicio web de Confirmacion" , e);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Confirmacion" , e);
			throw new ConfirmacionServiceException("Error inesperado al llamar al servicio web de Confirmacion" , e);
		}

		return null;
// TATY (22.03.2018)-Fin
		
	}
	
}
