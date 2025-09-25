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

/**
 * Helper para el Servicio de Validacion
 * 
 * @author T-Systems
 *
 */
public class ServicioConfirmarHelper {

	private static final Log logger = LogFactory.getLog(ServicioConfirmarHelper.class);
	
	/**
	 * Configura y realiza la llamada al servicio Web de Validacion
	 */
	public AcuseRecibo doWork(Long idEnvio, Long idPoliza, String realPath, IPolizaDao polizaDao) throws ConfirmacionServiceException {
		String xml = null;
		
		
		logger.debug("ServicioConfirmarHelper -doWork");
		logger.debug("Valor de idPoliza: "+idPoliza +" y valor de idEnvio: "+idEnvio);
		// Establece el Proxy
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
        String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("confirmacion.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new ConfirmacionServiceException("Imposible recuperar el WSDL de Confirmación. Revise la Ruta: " + url, e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("confirmacion.location");
		String wsPort     = WSUtils.getBundleProp("confirmacion.port");
		String wsService  = WSUtils.getBundleProp("confirmacion.service");
		
		// jax-ws properties
		System.setProperty("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump", "true");
		System.setProperty("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.endpoint.before", "true");
		System.setProperty("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.level", "FINE");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de confirmacion
		ContratacionSCConfirmacion_Service srv = new ContratacionSCConfirmacion_Service(wsdlLocation, serviceName);
		ContratacionSCConfirmacion srvConfirmacion = srv.getPort(portName, ContratacionSCConfirmacion.class);
		
		logger.debug("Valor de srvConfirmacion: " +srvConfirmacion );

		// Añade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvConfirmacion);
		
		//Cogemos el XML de la BBDD		
		logger.debug("Obtenemos en un Clob el XML que ha generado el PL");
		xml = WSUtils.obtenXMLPoliza(idEnvio, polizaDao); 
		
		// No se recupero el XML...
		if (xml == null) throw new ConfirmacionServiceException("No se ha podido obtener el XML de la Póliza");

		logger.debug("Valor del xml recuperado: "+xml);
		
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
		ConfirmarRequest parameters = new ConfirmarRequest();
		parameters.setPoliza(base64Binary);				
		
		ConfirmarResponse response = null;		
		try {
			logger.debug("Llamando a Servicio de Contratacion: CONFIRMAR>> " + xml);				
			response = srvConfirmacion.confirmar(parameters);
			logger.debug("Despues de lanzar llamada a Confirmar");
			if (response != null)
			{
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				Base64Binary respuesta = response.getAcuseRecibo();
				byte[]arrayAcuse = respuesta.getValue();
				String acuse = new String (arrayAcuse, "UTF-8");
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(acuse));
				
				logger.debug("Antes de hacer return del acuse de recibo");
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
	}
}
