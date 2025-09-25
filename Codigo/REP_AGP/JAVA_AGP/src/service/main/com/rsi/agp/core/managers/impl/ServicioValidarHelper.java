package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.exception.ValidacionServiceException;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion_Service;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ObjectFactory;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ValidacionModificacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ValidacionModificacionResponse;
import es.agroseguro.serviciosweb.contratacionscutilidades.AgrException;
import es.agroseguro.serviciosweb.contratacionscutilidades.ContratacionSCUtilidades;
import es.agroseguro.serviciosweb.contratacionscutilidades.ContratacionSCUtilidades_Service;
import es.agroseguro.serviciosweb.contratacionscutilidades.ValidarRequest;
import es.agroseguro.serviciosweb.contratacionscutilidades.ValidarResponse;


/**
 * Helper para el Servicio de Validacion
 * 
 * @author U028648
 *
 */
public class ServicioValidarHelper {

	private static final Log logger = LogFactory.getLog(ServicioValidarHelper.class);
	
	/**
	 * Configura y realiza la llamada al servicio Web de Validacion
	 */
	public AcuseRecibo doWork(Long idEnvio, Long idPoliza, String realPath, IPolizaDao polizaDao, Character tipoWS) throws ValidacionServiceException {
		
		//Cogemos el XML de la BBDD		
		logger.debug("Obtenemos en un Clob el XML que ha generado el PL");
		String xml = WSUtils.obtenXMLPoliza(idEnvio, polizaDao); 
		
		// No se recupero el XML...
		if (xml == null) throw new ValidacionServiceException("No se ha podido obtener el XML de la Póliza");

		// Realiza la llamada al WS
		logger.debug("Llamando a Servicio de Contratacion: VALIDAR>> " + xml);
		return doWorkGenerico(xml, realPath);				
	}
	
	/**
	 * Configura y realiza la llamada al servicio web de validación para la situación actualizada de la póliza
	 * @param idEnvio
	 * @param idPoliza
	 * @param realPath
	 * @param tipoWS
	 * @return
	 * @throws ValidacionServiceException
	 */
	public AcuseRecibo doWork(final XmlObject poliza, final String realPath)
			throws ValidacionServiceException {
		// Cogemos el XML de la BBDD
		logger.debug("Obtenemos en un Clob el XML que ha generado el PL");
		String xml = poliza.toString();
		// No se recupero el XML...
		if (xml == null)
			throw new ValidacionServiceException(
					"No se ha podido obtener el XML de la Póliza");
		// Modifica el xml para que lo acepte el servicio de validación
		final String cabecera = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xml = cabecera + xml.replace("xml-fragment", "ns2:Poliza");
		// Realiza la llamada al WS
		logger.debug("Llamando a Servicio de Contratacion: VALIDAR>> " + xml);
		return doWorkGenerico(xml, realPath);
	}
	
	private AcuseRecibo doWorkGenerico (String xml, String realPath) throws ValidacionServiceException {
		
		// Establece el Proxy
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
		
		
		
        URL wsdlLocation = null;
		try {
			wsdlLocation = new URL("file:" + realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("contratacion.wsdl"));
		} catch (MalformedURLException e1) {
			logger.error("Imposible recuperar el WSDL de Contratación. Revise la Ruta: " + ((wsdlLocation != null) ? wsdlLocation.toString() : ""), e1);
			throw new ValidacionServiceException("Imposible recuperar el WSDL de Contratación. Revise la Ruta: " + ((wsdlLocation != null) ? wsdlLocation.toString() : ""), e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("contratacion.location");
		String wsPort     = WSUtils.getBundleProp("contratacion.port");
		String wsService  = WSUtils.getBundleProp("contratacion.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de contratacion
		ContratacionSCUtilidades_Service srv = new ContratacionSCUtilidades_Service(wsdlLocation, serviceName);
		ContratacionSCUtilidades srvContratacion = srv.getPort(portName, ContratacionSCUtilidades.class);

		// AÃ±ade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvContratacion);					
		
		logger.debug("Llamando a Servicio de Contratacion: VALIDAR>> " + xml);				

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
			logger.error("Se esperaba un XML en formato UTF-8.", e2);
			throw new ValidacionServiceException("Se esperaba un XML en formato UTF-8.", e2);
		}
		// Parametros de envio al Servicio Web
		ValidarRequest parameters = new ValidarRequest();
		parameters.setPoliza(base64Binary);				
		
		ValidarResponse response = null;		
		try {
			response = srvContratacion.validar(parameters);
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
			logger.error("Error inesperado devuelto por el servicio web de Validacion" , e);
			WSUtils.debugAgrException(e);
			throw new ValidacionServiceException("Error inesperado devuelto por el servicio web de Validacion" , e);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Validacion" , e);
			throw new ValidacionServiceException("Error inesperado al llamar al servicio web de Validacion" , e);
		}

		return null;
	}
	
	/* Pet. 57626 ** MODIF TAM (15.06.2020) ** Inicio */
	/**
	 * Configura y realiza la llamada al servicio web de validación para la situación actualizada de la póliza
	 * @param idEnvio
	 * @param idPoliza
	 * @param realPath
	 * @param tipoWS
	 * @return
	 * @throws ValidacionServiceException
	 */
	public AcuseRecibo doWorkValAnexo(final XmlObject poliza, final AnexoModificacion am, final String realPath)
			throws ValidacionServiceException {
		
		logger.debug("Dentro de ServicioValidarHelper-doWorkValAnexo");
		
		try {
			// Cogemos el XML de la BBDD
			logger.debug("Obtenemos en un Clob el XML que ha generado el PL");
			
			String xml = poliza.toString();
			// No se recupero el XML...
			if (xml == null)
				throw new ValidacionServiceException("No se ha podido obtener el XML de la Póliza");
			
			// Modifica el xml para que lo acepte el servicio de validación
			final String cabecera = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			xml = cabecera + xml.replace("xml-fragment", "ns2:Poliza");
			// Realiza la llamada al WS
			logger.debug("Llamando a Servicio de ContratacionSCModificacion: VALIDAR>> " + xml);
			
			return doWorkGenericoAnexo(xml, am, realPath);
		
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Validacion" , e);
			throw new ValidacionServiceException("Error inesperado al llamar al servicio web de Validacion" , e);
		}
		
	}
	/*
	 * Configura y realiza la llamada al servicio web de validación para la situación actualizada de la póliza de Reduccion Capital
	 * @param poliza xml
	 * @param reduccionCapital
	 * @param realPath
	 * @return AcuseRecibo
	 * @throws ValidacionServiceException
	 */
	public AcuseRecibo doWorkValRC(final XmlObject poliza, final ReduccionCapital reduccionCapital, final String realPath)
			throws ValidacionServiceException {
		
		logger.debug("Dentro de ServicioValidarHelper-doWorkValAnexo");
		
		try {
			// Cogemos el XML de la BBDD
			logger.debug("Obtenemos en un Clob el XML que ha generado el PL");
			
			String xml = poliza.toString();
			// No se recupero el XML...
			if (xml == null)
				throw new ValidacionServiceException("No se ha podido obtener el XML de la Póliza");
			
			// Modifica el xml para que lo acepte el servicio de validación
			final String cabecera = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			xml = cabecera + xml.replace("xml-fragment", "ns2:Poliza");
			// Realiza la llamada al WS
			logger.debug("Llamando a Servicio de ContratacionSCModificacion V2: VALIDAR>> " + xml);
			
			return doWorkGenericoRC(xml, reduccionCapital, realPath);
		
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Validacion" , e);
			throw new ValidacionServiceException("Error inesperado al llamar al servicio web de Validacion" , e);
		}
		
	}
	
	/* Nos creamos un nuevo método para cambiar la llamada al método de validación de ContratacionSCModificación */
	private AcuseRecibo doWorkGenericoAnexo (String Xmlpoliza, AnexoModificacion am, String realPath) throws Exception {
		
		logger.debug("Dentro de ServicioValidarHelper-doWorkValAnexo");
		
		ContratacionSCModificacion srvAnexoMod = getSrvAnexoModificacion(realPath);
		

		// Comprueba si el anexo es de principal o de complementaria
		// Parametros a enviar al SW
		
		ValidacionModificacionRequest params = new ValidacionModificacionRequest();
		
		params.setCuponModificacion(am.getCupon().getIdcupon());
		logger.debug("ServicioValidarCplHelper-doWorkGenericoCplAnexo. Valor de cupón enviado:" + params.getCuponModificacion());
		
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		base64Binary.setValue(Xmlpoliza.getBytes("UTF-8"));
		
		params.setPolizaPrincipal(new ObjectFactory().createValidacionModificacionRequestPolizaPrincipal(base64Binary));
			
		try {
			ValidacionModificacionResponse validacionModificacionResponse = srvAnexoMod.validacionModificacion(params);
		
			if (validacionModificacionResponse != null)	{
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				Base64Binary respuesta = validacionModificacionResponse.getAcuse();
				byte[]arrayAcuse = respuesta.getValue();
				String acuse = new String (arrayAcuse, "UTF-8");
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(acuse));
				
				return acuseReciboDoc.getAcuseRecibo(); 
			}
		} catch (es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException e) {
			logger.error("Error inesperado al llamar al servicio web de Validacion");
			List<es.agroseguro.serviciosweb.contratacionscmodificacion.Error> errores = e.getFaultInfo().getError() ;
			for (es.agroseguro.serviciosweb.contratacionscmodificacion.Error error : errores) {
				logger.error(error.getCodigo() + " - " + error.getMensaje());
			}
			throw new ValidacionServiceException("Error inesperado al llamar al servicio web de Validacion" , e);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Validacion" , e);
			throw new ValidacionServiceException("Error inesperado al llamar al servicio web de Validacion" , e);
		}

		return null;
	}
	
	
	/* Nos creamos un nuevo método para cambiar la llamada al método de validación de ContratacionSCModificación */
	private AcuseRecibo doWorkGenericoRC (String Xmlpoliza, ReduccionCapital reduccionCapital, String realPath) throws Exception {
		
		logger.debug("Dentro de ServicioValidarHelper-doWorkValRC");
		
		ContratacionSCModificacion srvAnexoMod = getSrvAnexoModificacion(realPath);
		
		
		// Comprueba si el anexo es de principal o de complementaria
		// Parametros a enviar al SW
		
		ValidacionModificacionRequest params = new ValidacionModificacionRequest();
		
		params.setCuponModificacion(reduccionCapital.getCupon().getIdcupon());
		logger.debug("ServicioValidarHelper-doWorkGenericoRC. Valor de cupón enviado:" + params.getCuponModificacion());
		
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		base64Binary.setValue(Xmlpoliza.getBytes("UTF-8"));
		
		params.setPolizaPrincipal(new ObjectFactory().createValidacionModificacionRequestPolizaPrincipal(base64Binary));
		
		try {
			ValidacionModificacionResponse validacionModificacionResponse = srvAnexoMod.validacionModificacion(params);
			
			if (validacionModificacionResponse != null)	{
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				Base64Binary respuesta = validacionModificacionResponse.getAcuse();
				byte[]arrayAcuse = respuesta.getValue();
				String acuse = new String (arrayAcuse, "UTF-8");
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(acuse));
				
				return acuseReciboDoc.getAcuseRecibo(); 
			}
		} catch (es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException e) {
			logger.error("Error inesperado al llamar al servicio web de Validacion");
			List<es.agroseguro.serviciosweb.contratacionscmodificacion.Error> errores = e.getFaultInfo().getError() ;
			for (es.agroseguro.serviciosweb.contratacionscmodificacion.Error error : errores) {
				logger.error(error.getCodigo() + " - " + error.getMensaje());
			}
			throw new ValidacionServiceException("Error inesperado al llamar al servicio web de Validacion" , e);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Validacion" , e);
			throw new ValidacionServiceException("Error inesperado al llamar al servicio web de Validacion" , e);
		}
		
		return null;
	}
	
	
	/**
	 * Metodo para crear los objetos necesarios para las llamadas al servicio
	 * web.
	 * 
	 * @param realPath
	 *            Ruta real de los ficheros "wsdl"
	 * @return Manejador para las llamadas al servicio de contratación de
	 *         anexos.
	 */
	private ContratacionSCModificacion getSrvAnexoModificacion(	final String realPath) {
		
		if (!WSUtils.isProxyFixed()) {
			WSUtils.setProxy();
		}
		
		URL wsdlLocation = null;
		try {
			wsdlLocation = new URL("file:"
								+ realPath
								+ System.getProperty("file.separator")
								+ WSUtils
										.getBundleProp("anexoModificacionWS.wsdl"));
		
		} catch (MalformedURLException e1) {
			throw new SWConsultaContratacionException(
					"Imposible recuperar el WSDL de consulta de contratación. Revise la Ruta: "
							+ ((wsdlLocation != null) ? wsdlLocation.toString()
									: ""), e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y
		// Port 
		String wsLocation = WSUtils.getBundleProp("anexoModificacionWS.location");
		String wsPort = WSUtils.getBundleProp("anexoModificacionWS.port");
		String wsService = WSUtils.getBundleProp("anexoModificacionWS.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		// Crea el envoltorio para la llamada al servicio web de contratacion
		ContratacionSCModificacion_Service srv = new ContratacionSCModificacion_Service(
				wsdlLocation, serviceName);
		ContratacionSCModificacion srvAnexoMod = srv.getPort(portName,
				ContratacionSCModificacion.class);
		logger.debug(srvAnexoMod.toString());
		// AÃ±ade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvAnexoMod);
		return srvAnexoMod;
	}

}
