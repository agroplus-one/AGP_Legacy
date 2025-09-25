package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.CalculoServiceException;
import com.rsi.agp.core.exception.SWConfirmacionSiniestroException;
import com.rsi.agp.core.exception.SWValidacionSiniestroException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.siniestro.Siniestro;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;
import es.agroseguro.serviciosweb.siniestrosscdeclaracion.ConfirmarRequest;
import es.agroseguro.serviciosweb.siniestrosscdeclaracion.ConfirmarResponse;
import es.agroseguro.serviciosweb.siniestrosscdeclaracion.SiniestrosSCDeclaracion;
import es.agroseguro.serviciosweb.siniestrosscdeclaracion.SiniestrosSCDeclaracion_Service;
import es.agroseguro.serviciosweb.siniestrosscdeclaracion.ValidarRequest;
import es.agroseguro.serviciosweb.siniestrosscdeclaracion.ValidarResponse;

/**
 * Helper para el Servicio de siniestros
 * @author T-Systems
 *
 */
public class SWSiniestroHelper {

	private static final Log logger = LogFactory.getLog(SWSiniestroHelper.class);
	
	
	/**
	 * Configura y realiza la llamada al servicio Web de validación de siniestro
	 */
	public Map<String, Object> validarSiniestro(Siniestro siniestro, String realPath, 
			Usuario usuario, Asegurado asegurado) 
			throws SWValidacionSiniestroException, AgrException, Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		String xmlEnvio = XmlTransformerUtil.generateXMLSiniestro(siniestro, asegurado);
		params.put("xmlEnvio", xmlEnvio);
		
		SiniestrosSCDeclaracion srvSr = getSrvSiniestro(realPath);
		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xmlEnvio.getBytes(Constants.DEFAULT_ENCODING);
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
			throw new CalculoServiceException("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
		}

		// Parametros de envio al Servicio Web
		ValidarRequest parameters = new ValidarRequest();		
		parameters.setSiniestro(base64Binary);			
		ValidarResponse response = null;		
		try {
			response = srvSr.validar(parameters);
			if (response != null){							
				Base64Binary resp = response.getAcuseRecibo();
				params = getSiniestroFromResponse(resp,params);
				@SuppressWarnings("unused")
				AcuseRecibo respuesta = (AcuseRecibo)params.get("acuseRecibo");
				return params;
			}
			
		
			
		} 
		catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de validación del siniestro" , e);
			throw new Exception("Error inesperado al llamar al servicio web de validación del siniestro" , e);
		}

		return null;
	}
	
	/**
	 * Configura y realiza la llamada al servicio Web de confirmación de siniestro
	 */
	public Map<String, Object> confirmarSiniestro(Siniestro siniestro, String realPath, 
			Usuario usuario, Asegurado asegurado) 
			throws SWConfirmacionSiniestroException, AgrException, Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		String xmlEnvio = XmlTransformerUtil.generateXMLSiniestro(siniestro, asegurado);
		params.put("xmlEnvio", xmlEnvio);

		SiniestrosSCDeclaracion srvSr = getSrvSiniestro(realPath);

		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xmlEnvio.getBytes(Constants.DEFAULT_ENCODING);
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
			throw new CalculoServiceException("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
		}

		// Parametros de envio al Servicio Web
		ConfirmarRequest parameters = new ConfirmarRequest();		
		parameters.setSiniestro(base64Binary);				
		ConfirmarResponse response = null;		
		try {
			response = srvSr.confirmar(parameters);
			if (response != null){
				Base64Binary resp = response.getAcuseRecibo();
				
				params = this.getSiniestroFromResponse(resp, params);
				@SuppressWarnings("unused")
				AcuseRecibo respuesta = (AcuseRecibo)params.get("acuseRecibo");
				return params;
			}
		} 
		catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de confirmación del siniestro" , e);
			throw new Exception("Error inesperado al llamar al servicio web de confirmación del siniestro" , e);
		}

		return null;
	}

	/**
	 * Metodo para crear los objetos necesarios para las llamadas al servicio web.
	 * @param realPath Ruta real de los ficheros "wsdl"
	 * @return Manejador para las llamadas al servicio de contratación de Siniestros
	 */
	private SiniestrosSCDeclaracion getSrvSiniestro(String realPath) {
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
        String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("siniestrosDeclaracionWS.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new SWValidacionSiniestroException("Imposible recuperar el WSDL de la validación del siniestro. Revise la Ruta: " + url, e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("siniestrosDeclaracionWS.location");
		String wsPort     = WSUtils.getBundleProp("siniestrosDeclaracionWS.port");
		String wsService  = WSUtils.getBundleProp("siniestrosDeclaracionWS.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de validación del siniestro
		SiniestrosSCDeclaracion_Service srv = new SiniestrosSCDeclaracion_Service(wsdlLocation, serviceName);
		SiniestrosSCDeclaracion srvSr = srv.getPort(portName, SiniestrosSCDeclaracion.class);
		logger.debug(srvSr.toString());
		
		// Añade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvSr);
				
		return srvSr;
	}

	/**
	 * MÃ©todo que rellena el objeto para generar el informe a partir de la respuesta del servicio web.
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws XmlException
	 * @throws IOException
	 */
	private Map<String, Object> getSiniestroFromResponse(Base64Binary siniestroBin, Map<String, Object> params)
			throws UnsupportedEncodingException, XmlException, IOException {
		
//		GENERAMOS EL XML DE LA POLIZA
		byte[] byteArraySiniestro = siniestroBin.getValue();
		String xmlDataSiniestro = new String (byteArraySiniestro, Constants.DEFAULT_ENCODING);
		
		AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(xmlDataSiniestro));
		
		AcuseRecibo acuseRecibo = acuseReciboDoc.getAcuseRecibo();
		 
		params.put("xmlRecepcion", xmlDataSiniestro);
		params.put("acuseRecibo", acuseRecibo);
		
		return params;
	}
}
