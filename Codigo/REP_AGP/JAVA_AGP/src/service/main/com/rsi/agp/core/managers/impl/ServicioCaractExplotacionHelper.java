package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.CalculoServiceException;
import com.rsi.agp.core.exception.CaractExplotacionServiceException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.poliza.IPolizaDao;

import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.serviciosweb.caracteristicaexplotacion.CaracteristicaExplotacion;
import es.agroseguro.serviciosweb.caracteristicaexplotacion.CaracteristicaExplotacion_Service;
import es.agroseguro.serviciosweb.caracteristicaexplotacion.ObtenerCaracteristicaExplotacionRequest;
import es.agroseguro.serviciosweb.caracteristicaexplotacion.ObtenerCaracteristicaExplotacionResponse;

/**
 * Helper para el Servicio de CÃ¡lculo
 * 
 * @author T-Systems
 *
 */
public class ServicioCaractExplotacionHelper {

	private static final Log logger = LogFactory.getLog(ServicioCaractExplotacionHelper.class);
	
	/**
	 * Configura y realiza la llamada al servicio Web de Cálculo de la característica de la explotación
	 */
	public Map<String, Object> doWork(String xml, String realPath, Long idEnvio, IPolizaDao polizaDao) throws CaractExplotacionServiceException {
		
		Map<String, Object> retorno = new HashMap<String, Object>();
		
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
        String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("caractExpl.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
			logger.info("doWork - wsdlLocation='" + wsdlLocation + "'");
		} catch (MalformedURLException e1) {
			throw new CalculoServiceException("Imposible recuperar el WSDL de calculo de la característica de la Explotación. Revise la Ruta: " + url, e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("caractExpl.location");
		String wsPort     = WSUtils.getBundleProp("caractExpl.port");
		String wsService  = WSUtils.getBundleProp("caractExpl.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de contratacion
		CaracteristicaExplotacion_Service srv = new CaracteristicaExplotacion_Service(wsdlLocation, serviceName);
		CaracteristicaExplotacion srvCarExpl = srv.getPort(portName, CaracteristicaExplotacion.class);
		logger.info(srvCarExpl.toString());

		// Añaade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvCarExpl);
		
		/***** Nuevo Taty 21.05.2020 *****/
		//Cogemos el XML de la BBDD		
		logger.info("Obtenemos en un Clob el XML que ha generado el PL");
		xml = WSUtils.obtenXMLPoliza(idEnvio, polizaDao); 
				
		// No se recupero el XML...
		if (xml == null) throw new CalculoServiceException("No se ha podido obtener el XML de la Póliza");
				
			xml = xml.replaceAll("xmlns=\"http://www.agroseguro.es/Contratacion\"", "xmlns=\"http://www.agroseguro.es/PresupuestoContratacion\"");
			
			WSUtils.getXMLCalculoUnificado(xml);
				
		/* Pet. 57626 ** MODIF TAM (12/05/2020) ** Inicio */
		// Se valida el XML antes de llamar al Servicio Web
		//WSUtils.getXMLCalculo(xml);
				
		/* Hay que validar el XML de calculo pero en formato unificado */
		/*WSUtils.getXMLCalcCaractExpl(xml);
		/* Pet. 57626 ** MODIF TAM (12/05/2020) ** Fin */
					
		logger.info("Llamando a Servicio de Contratacion: CARACT. EXPLOTACION>> " + xml);			
		
		// Se convierte el String a Base64, para enviarlo al WS
		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xml.getBytes(Constants.DEFAULT_ENCODING);
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error("Error al codificar el xml para calcular la característica de la explotación", e2);
			throw new CalculoServiceException("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
		}
		// Parametros de envio al Servicio Web
		ObtenerCaracteristicaExplotacionRequest parameters = new ObtenerCaracteristicaExplotacionRequest();
		parameters.setPoliza(base64Binary);
		
		ObtenerCaracteristicaExplotacionResponse response = null;		
		try {
			response = srvCarExpl.obtenerCaracteristicaExplotacion(parameters);
			if (response != null)
			{
				//devuelvo la característica
				retorno.put("carExpl", new BigDecimal(response.getCaracteristicaExplotacion()));
				
				// devuelvo el acuse de recibo
				Base64Binary respuesta = response.getAcuseRecibo();
				byte[] byteArray = respuesta.getValue();
				String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(xmlData));
				
				retorno.put("acuse", acuseReciboDoc.getAcuseRecibo());
				return retorno;
			}
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web para obtener la característica de la explotación" , e);
			throw new CaractExplotacionServiceException("Error inesperado al llamar al servicio web para obtener la característica de la explotación" , e);
		}

		return null;
	}
	
	public Map<String, Object> doWorkAnx(String xml, String realPath, IPolizaDao polizaDao) throws CaractExplotacionServiceException {
		
		Map<String, Object> retorno = new HashMap<String, Object>();
		
		logger.debug("ServicioCarctEplotacionHelper - doWorkAnx [INIT] ");
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
		try {
			wsdlLocation = new URL("file:" + realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("caractExpl.wsdl"));
			logger.info("doWork - wsdlLocation='" + wsdlLocation + "'");
		} catch (MalformedURLException e1) {
			throw new CalculoServiceException("Imposible recuperar el WSDL de calculo de la característica de la Explotación. Revise la Ruta: " + ((wsdlLocation != null) ? wsdlLocation.toString() : ""), 
							  				  e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("caractExpl.location");
		String wsPort     = WSUtils.getBundleProp("caractExpl.port");
		String wsService  = WSUtils.getBundleProp("caractExpl.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de contratacion
		CaracteristicaExplotacion_Service srv = new CaracteristicaExplotacion_Service(wsdlLocation, serviceName);
		CaracteristicaExplotacion srvCarExpl = srv.getPort(portName, CaracteristicaExplotacion.class);
		logger.info(srvCarExpl.toString());

		// Añaade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvCarExpl);
		
		// No se recupero el XML...
		if (xml == null) throw new CalculoServiceException("No se ha podido obtener el XML del Anexo");
				
		xml = xml.replaceAll("\"http://www.agroseguro.es/Contratacion\"", "\"http://www.agroseguro.es/PresupuestoContratacion\"");
		
		WSUtils.getXMLCalculoUnificado(xml);
			
		logger.info("Llamando a Servicio de Contratacion: CARACT. EXPLOTACION (Anexos)>> " + xml);			
		
		// Se convierte el String a Base64, para enviarlo al WS
		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xml.getBytes(Constants.DEFAULT_ENCODING);
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error("Error al codificar el xml para calcular la característica de la explotación", e2);
			throw new CalculoServiceException("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
		}
		
		// Parametros de envio al Servicio Web
		ObtenerCaracteristicaExplotacionRequest parameters = new ObtenerCaracteristicaExplotacionRequest();
		parameters.setPoliza(base64Binary);
		
		ObtenerCaracteristicaExplotacionResponse response = null;		
		try {
			response = srvCarExpl.obtenerCaracteristicaExplotacion(parameters);
			if (response != null)
			{
				//devuelvo la característica
				retorno.put("carExpl", new BigDecimal(response.getCaracteristicaExplotacion()));
				
				// devuelvo el acuse de recibo
				Base64Binary respuesta = response.getAcuseRecibo();
				byte[] byteArray = respuesta.getValue();
				String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(xmlData));
				
				retorno.put("acuse", acuseReciboDoc.getAcuseRecibo());
				return retorno;
			}
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web para obtener la característica de la explotación" , e);
			throw new CaractExplotacionServiceException("Error inesperado al llamar al servicio web para obtener la característica de la explotación" , e);
		}

		return null;
	}

}
