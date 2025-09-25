package com.rsi.agp.core.managers.confirmarext;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.ConfirmacionServiceException;
import com.rsi.agp.core.exception.ValidacionServiceException;
import com.rsi.agp.core.manager.impl.anexoRC.SWAnexoRCHelper;
import com.rsi.agp.core.managers.impl.anexoMod.calculo.CalculoModificacionHelper;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.contratacionscconfirmacion.ContratacionSCConfirmacion;
import es.agroseguro.serviciosweb.contratacionscconfirmacion.ContratacionSCConfirmacion_Service;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AnulacionCuponRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AnulacionCuponResponse;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion_Service;
import es.agroseguro.serviciosweb.contratacionscmodificacion.EnvioModificacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.EnvioModificacionResponse;
import es.agroseguro.serviciosweb.contratacionscmodificacion.SolicitudModificacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.SolicitudModificacionResponse;
import es.agroseguro.serviciosweb.contratacionscutilidades.CalcularRequest;
import es.agroseguro.serviciosweb.contratacionscutilidades.CalcularResponse;
import es.agroseguro.serviciosweb.contratacionscutilidades.ContratacionSCUtilidades;
import es.agroseguro.serviciosweb.contratacionscutilidades.ContratacionSCUtilidades_Service;
import es.agroseguro.serviciosweb.contratacionscutilidades.ObjectFactory;
import es.agroseguro.serviciosweb.contratacionscutilidades.ValidarRequest;
import es.agroseguro.serviciosweb.contratacionscutilidades.ValidarResponse;
import es.agroseguro.serviciosweb.siniestrosscdeclaracion.SiniestrosSCDeclaracion;
import es.agroseguro.serviciosweb.siniestrosscdeclaracion.SiniestrosSCDeclaracion_Service;


public class ContratacionExtConfirmacionWS {

	private static final String ERROR_INESPERADO_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION = "Error inesperado al llamar al servicio web de Anexos de Modificacion";
	private static final String ERROR_AGR_EXCEPTION_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION = "Error AgrException al llamar al servicio web de Anexos de Modificacion";
	private static final String LLAMADA_AL_WEB_SERVICE_CONTRATACION_SC_MODIFICACION = "Llamada al WebService ContratacionSCModificacion.";
	private static final String TEXT_XML = "text/xml";
	private static final String COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_LEVEL = "com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.level";
	private static final String COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_ENDPOINT_BEFORE = "com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.endpoint.before";
	private static final String COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP = "com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump";
	private static final String SERVICE = ".service";
	private static final String PORT = ".port";
	private static final String LOCATION = ".location";
	private static final String WSDL = ".wsdl";
	private static final String FILE = "file:";
	private static final String FILE_SEPARATOR = "file.separator";

	private static final String DIFERENCIAS_COSTE = "diferenciasCoste";
	private static final String CALCULO_ORIGINAL = "calculoOriginal";
	private static final String CALCULO_MODIFICACION = "calculoModificacion";

	private static final Log logger = LogFactory.getLog(ContratacionExtConfirmacionWS.class);

	public static final int MAP_POS_POLIZA = 1;
	public static final int MAP_POS_POLIZA_COMP = 2;
	public static final int MAP_POS_ESTADO_CONT = 3;
	public static final int MAP_POS_CUPON_MOD = 4;
	public static final int MAP_POS_POLIZA_RC = 5;
	public static final int MAP_POS_POLIZA_COMP_RC = 6;

	private static final String PROPIEDAD_ANEXO = "anexoModificacionWS";
	private static final String CONTRATACION_SC_MODIFICACION = "anexoReduccionCapiWS";
	private static final String PROPIEDAD_CONFIRMACION = "confirmacion";
	private static final String PROPIEDAD_SINIESTRO = "siniestrosDeclaracionWS";
	/* Pet. 733228 ** MODIF TAM (16/03/2021) */
	private static final String PROPIEDAD_CONTRATACION = "contratacion";
	
	public static Base64Binary confirmarSiniestro(final Base64Binary siniestroB64, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		logger.debug("Llamada al WebService SiniestrosSCDeclaracion.");
		Base64Binary acuseRecibo = null;
		// Establece el Proxy
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();
		URL wsdlLocation = null;
		String url = null;
		try {
			url = FILE + realPath + System.getProperty(FILE_SEPARATOR)
					+ WSUtils.getBundleProp(PROPIEDAD_SINIESTRO + WSDL);
			wsdlLocation = new URL(url);
		} catch (MalformedURLException e1) {
			throw new Exception("Imposible recuperar el WSDL de Siniestros. Revise la Ruta: " + url, e1);
		} catch (NullPointerException e1) {
			throw new Exception("Imposible obtener el WSDL de Siniestros. Revise la Ruta: " + url, e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp(PROPIEDAD_SINIESTRO + LOCATION);
		String wsPort = WSUtils.getBundleProp(PROPIEDAD_SINIESTRO + PORT);
		String wsService = WSUtils.getBundleProp(PROPIEDAD_SINIESTRO + SERVICE);
		// jax-ws properties
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_ENDPOINT_BEFORE, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_LEVEL, "FINE");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		try {
			// Crea el envoltorio para la llamada al servicio web de confirmacion
			SiniestrosSCDeclaracion_Service srv = new SiniestrosSCDeclaracion_Service(wsdlLocation, serviceName);
			SiniestrosSCDeclaracion srvDeclaracionSiniestro = srv.getPort(portName, SiniestrosSCDeclaracion.class);
			// A�ade la cabecera de seguridad
			WSUtils.addSecurityHeader(srvDeclaracionSiniestro);
			// Se valida el XML. Posteriormente se enviar� en el servicio
			String strSiniestroB64 = WSUtils.getStringResponse(siniestroB64);
			WSUtils.getXMLSiniestros(strSiniestroB64);
			// Parametros de envio al Servicio Web
			es.agroseguro.serviciosweb.siniestrosscdeclaracion.ConfirmarRequest parameters = new es.agroseguro.serviciosweb.siniestrosscdeclaracion.ConfirmarRequest();
			parameters.setSiniestro(siniestroB64);
			logger.debug("Llamando a Servicio de Siniestros: DECLARACION>> ");
			es.agroseguro.serviciosweb.siniestrosscdeclaracion.ConfirmarResponse response = srvDeclaracionSiniestro.confirmar(parameters);
			if (response != null) {
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				acuseRecibo = new Base64Binary();
				acuseRecibo.setValue(response.getAcuseRecibo().getValue());
				acuseRecibo.setContentType(TEXT_XML);
			}
		} catch (ValidacionServiceException e) {
			logger.error("Error al validar el siniestro antes de enviarlo al Servicio Web de declaraci�n de siniestro", e);
			throw new ConfirmacionServiceException(e);
		} catch (es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException e) {
			logger.error("Error AgrException al llamar al servicio web de declaraci�n de siniestro", e);
			throw new ConfirmarExtException.AgrWSException(WSUtils.debugAgrException(e));
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de declaraci�n de siniestro", e);
			throw new ConfirmacionServiceException("Error inesperado al llamar al servicio web de declaraci�n de siniestro", e);
		}
		return acuseRecibo;
	}

	public static Base64Binary anularCupon(final String idCupon, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		logger.debug(LLAMADA_AL_WEB_SERVICE_CONTRATACION_SC_MODIFICACION);
		Base64Binary result = null;
		try {
			// Parametros de envio al Servicio Web
			AnulacionCuponRequest parameters = new AnulacionCuponRequest();
			parameters.setCuponModificacion(idCupon);
			logger.debug("Llamando a Servicio de Anexos de Modificacion: ANULAR CUPON>> ");
			AnulacionCuponResponse response = getServicioModificacion(realPath, Boolean.TRUE).anulacionCupon(parameters);
			if (response != null) {
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				result = new Base64Binary();
				result.setValue(response.getRespuesta().getBytes());
				result.setContentType(TEXT_XML);
			}
		} catch (es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException e) {
			logger.error(ERROR_AGR_EXCEPTION_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
			throw new ConfirmarExtException.AgrWSException(WSUtils.debugAgrException(e));
		} catch (Exception e) {
			logger.error(ERROR_INESPERADO_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
			throw new ConfirmacionServiceException(
					ERROR_INESPERADO_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
		}
		return result;
	}

	public static Map<Integer, Base64Binary> solicitarCupon(final BigDecimal plan, final String referencia,
			final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		logger.debug(LLAMADA_AL_WEB_SERVICE_CONTRATACION_SC_MODIFICACION);
		Map<Integer, Base64Binary> result = new HashMap<Integer, Base64Binary>(4);
		Base64Binary poliza = null;
		Base64Binary polizaComp = null;
		Base64Binary estadoCont = null;
		Base64Binary cupon = null;
		Base64Binary polizaRC = null;
		Base64Binary polizaCompRC = null;
		try {
			// Parametros de envio al Servicio Web
			SolicitudModificacionRequest parameters = new SolicitudModificacionRequest();
			parameters.setPlan(plan.intValue());
			parameters.setReferencia(referencia);
			logger.debug("Llamando a Servicio de Anexos de Modificacion: SOLICITAR CUPON>> ");
			SolicitudModificacionResponse response = getServicioModificacion(realPath, Boolean.TRUE)
					.solicitudModificacion(parameters);
			if (response != null) {
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				poliza = new Base64Binary();
				poliza.setValue(response.getPolizaPrincipal().getValue());
				poliza.setContentType(TEXT_XML);
				result.put(MAP_POS_POLIZA, poliza);
				polizaComp = new Base64Binary();
				polizaComp.setValue(response.getPolizaComplementaria().getValue());
				polizaComp.setContentType(TEXT_XML);
				result.put(MAP_POS_POLIZA_COMP, polizaComp);
				estadoCont = new Base64Binary();
				estadoCont.setValue(response.getEstadoContratacion().getValue());
				estadoCont.setContentType(TEXT_XML);
				result.put(MAP_POS_ESTADO_CONT, estadoCont);
				cupon = new Base64Binary();
				cupon.setValue(response.getCuponModificacion().getValue());
				cupon.setContentType(TEXT_XML);
				result.put(MAP_POS_CUPON_MOD, cupon);
				polizaRC = new Base64Binary();
				polizaRC.setValue(response.getPolizaPrincipalRC().getValue());
				polizaRC.setContentType(TEXT_XML);
				result.put(MAP_POS_POLIZA_RC, polizaRC);
				polizaCompRC = new Base64Binary();
				polizaCompRC.setValue(response.getPolizaComplementariaRC().getValue());
				polizaCompRC.setContentType(TEXT_XML);
				result.put(MAP_POS_POLIZA_COMP_RC, polizaCompRC);
			}
		} catch (es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException e) {
			logger.error(ERROR_AGR_EXCEPTION_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
			throw new ConfirmarExtException.AgrWSException(WSUtils.debugAgrException(e));
		} catch (Exception e) {
			logger.error(ERROR_INESPERADO_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
			throw new ConfirmacionServiceException(
					ERROR_INESPERADO_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
		}
		return result;
	}

	//P0079361
	public static Base64Binary confirmarCupon(final String idCupon, final Boolean flgRevAdmin,
			final Base64Binary polizaPpal, final Base64Binary polizaComp, final String realPath, Boolean isAnexRedCap)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		//TODO
		//diferenciar entre Anexo Modificacion y Reduccion Capital
		logger.debug(LLAMADA_AL_WEB_SERVICE_CONTRATACION_SC_MODIFICACION);
		Base64Binary acuseRecibo = null;
		try {
			// Parametros de envio al Servicio Web
			EnvioModificacionRequest parameters = new EnvioModificacionRequest();
			parameters.setCuponModificacion(idCupon);
			parameters.setForzarRevisionAdministrativa(flgRevAdmin);
			if (polizaPpal != null && polizaPpal.getValue() != null) {
				parameters.setPolizaPrincipal(new JAXBElement<Base64Binary>(
						new QName("http://www.agroseguro.es/serviciosWeb/ContratacionSCModificacion/",
								"polizaPrincipal"),
						Base64Binary.class, polizaPpal));
			}
			if (polizaComp != null && polizaComp.getValue() != null) {
				parameters.setPolizaComplementaria(new JAXBElement<Base64Binary>(
						new QName("http://www.agroseguro.es/serviciosWeb/ContratacionSCModificacion/",
								"polizaComplementaria"),
						Base64Binary.class, polizaComp));
			}
			logger.debug("Llamando a Servicio de Anexos de Modificacion: CONFIRMAR CUPON>> ");
			EnvioModificacionResponse response = getServicioModificacion(realPath, isAnexRedCap).envioModificacion(parameters);
			if (response != null) {
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				acuseRecibo = new Base64Binary();
				acuseRecibo.setValue(response.getAcuse().getValue());
				acuseRecibo.setContentType(TEXT_XML);
			}
		} catch (es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException e) {
			logger.error(ERROR_AGR_EXCEPTION_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
			throw new ConfirmarExtException.AgrWSException(WSUtils.debugAgrException(e));
		} catch (Exception e) {
			logger.error(ERROR_INESPERADO_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
			throw new ConfirmacionServiceException(
					ERROR_INESPERADO_AL_LLAMAR_AL_SERVICIO_WEB_DE_ANEXOS_DE_MODIFICACION, e);
		}
		return acuseRecibo;
	}
	//P0079361

	private static ContratacionSCModificacion getServicioModificacion(final String realPath, final Boolean isAnexRedCap)
			throws Exception {
		// Establece el Proxy
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();
		URL wsdlLocation = null;
		String url = null;
		try {
			if (isAnexRedCap) {
				url = FILE + realPath + System.getProperty(FILE_SEPARATOR)
						+ WSUtils.getBundleProp(CONTRATACION_SC_MODIFICACION + WSDL);
			} else {
				url = FILE + realPath + System.getProperty(FILE_SEPARATOR)
						+ WSUtils.getBundleProp(PROPIEDAD_ANEXO + WSDL);
			}
			wsdlLocation = new URL(url);
		} catch (MalformedURLException e1) {
			throw new Exception("Imposible recuperar el WSDL de Anexos de Modificacion. Revise la Ruta: " + url, e1);
		} catch (NullPointerException e1) {
			throw new Exception("Imposible obtener el WSDL de Anexos de Modificacion. Revise la Ruta: " + url, e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp(PROPIEDAD_ANEXO + LOCATION);
		String wsPort = WSUtils.getBundleProp(PROPIEDAD_ANEXO + PORT);
		String wsService = WSUtils.getBundleProp(PROPIEDAD_ANEXO + SERVICE);
		// jax-ws properties
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_ENDPOINT_BEFORE, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_LEVEL, "FINE");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de confirmacion
		ContratacionSCModificacion_Service srv = new ContratacionSCModificacion_Service(wsdlLocation, serviceName);
		ContratacionSCModificacion srvModificacion = srv.getPort(portName, ContratacionSCModificacion.class);
		
		logger.debug("ContratacionModificacion <srvAnexoMod>>" + srvModificacion.toString());
		
		// Aniade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvModificacion);
		return srvModificacion;
	}

	public static Base64Binary confirmarPoliza(final Base64Binary polizaB64, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		logger.debug("Llamada al WebService ContratacionSCConfirmacion.");
		Base64Binary acuseRecibo = null;
		// Establece el Proxy
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();
		URL wsdlLocation = null;
		String url = null;
		try {
			url = FILE + realPath + System.getProperty(FILE_SEPARATOR)
					+ WSUtils.getBundleProp(PROPIEDAD_CONFIRMACION + WSDL);
			wsdlLocation = new URL(url);
		} catch (MalformedURLException e1) {
			throw new Exception("Imposible recuperar el WSDL de Contrataci�n. Revise la Ruta: " + url, e1);
		} catch (NullPointerException e1) {
			throw new Exception("Imposible obtener el WSDL de Contrataci�n. Revise la Ruta: " + url, e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp(PROPIEDAD_CONFIRMACION + LOCATION);
		String wsPort = WSUtils.getBundleProp(PROPIEDAD_CONFIRMACION + PORT);
		String wsService = WSUtils.getBundleProp(PROPIEDAD_CONFIRMACION + SERVICE);
		// jax-ws properties
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_ENDPOINT_BEFORE, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_LEVEL, "FINE");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		try {
			// Crea el envoltorio para la llamada al servicio web de confirmacion
			ContratacionSCConfirmacion_Service srv = new ContratacionSCConfirmacion_Service(wsdlLocation, serviceName);
			ContratacionSCConfirmacion srvConfirmacion = srv.getPort(portName, ContratacionSCConfirmacion.class);
			// A�ade la cabecera de seguridad
			WSUtils.addSecurityHeader(srvConfirmacion);
			// Se valida el XML. Posteriormente se enviar� en el servicio
			String strPolizaB64 = WSUtils.getStringResponse(polizaB64);
			WSUtils.getXMLPoliza(strPolizaB64);
			// Parametros de envio al Servicio Web
			es.agroseguro.serviciosweb.contratacionscconfirmacion.ConfirmarRequest parameters = new es.agroseguro.serviciosweb.contratacionscconfirmacion.ConfirmarRequest();
			parameters.setPoliza(polizaB64);
			logger.debug("Llamando a Servicio de Contratacion: CONFIRMAR>> ");
			es.agroseguro.serviciosweb.contratacionscconfirmacion.ConfirmarResponse response = srvConfirmacion.confirmar(parameters);
			if (response != null) {
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				acuseRecibo = new Base64Binary();
				acuseRecibo.setValue(response.getAcuseRecibo().getValue());
				acuseRecibo.setContentType(TEXT_XML);
			}
		} catch (ValidacionServiceException e) {
			logger.error("Error al validar la p�liza antes de enviarla al Servicio Web de Confirmaci�n", e);
			throw new ConfirmacionServiceException(e);
		} catch (es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException e) {
			logger.error("Error AgrException al llamar al servicio web de confirmaci�n", e);
			throw new ConfirmarExtException.AgrWSException(WSUtils.debugAgrException(e));
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Confirmacion", e);
			throw new ConfirmacionServiceException("Error inesperado al llamar al servicio web de Confirmacion", e);
		}
		return acuseRecibo;
	}
	
	public static  Map<String, Base64Binary> validarPoliza(final Base64Binary polizaB64, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		
		logger.debug("Llamada al WebService ContratacionSCUtilidades.");
		Base64Binary acuseRecibo = null;
		
		Map<String, Base64Binary> retorno = new HashMap<String, Base64Binary>();
		
		// Establece el Proxy
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();
		URL wsdlLocation = null;
		String url = null;
		try {
			wsdlLocation = new URL(FILE + realPath + System.getProperty(FILE_SEPARATOR) + WSUtils.getBundleProp("contratacion.wsdl"));		
		} catch (MalformedURLException e1) {
			throw new Exception("Imposible recuperar el WSDL de Validar. Revise la Ruta: " + url, e1);
		} catch (NullPointerException e1) {
			throw new Exception("Imposible recuperar el WSDL de Validar. Revise la Ruta: " + url, e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp(PROPIEDAD_CONTRATACION + LOCATION);
		String wsPort = WSUtils.getBundleProp(PROPIEDAD_CONTRATACION + PORT);
		String wsService = WSUtils.getBundleProp(PROPIEDAD_CONTRATACION + SERVICE);
		// jax-ws properties
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_ENDPOINT_BEFORE, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_LEVEL, "FINE");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		try {
			// Crea el envoltorio para la llamada al servicio web de calculo
			// Crea el envoltorio para la llamada al servicio web de contratacion
			ContratacionSCUtilidades_Service srv = new ContratacionSCUtilidades_Service(wsdlLocation, serviceName);
			ContratacionSCUtilidades srvCalculo = srv.getPort(portName, ContratacionSCUtilidades.class);

			
			// A�ade la cabecera de seguridad
			WSUtils.addSecurityHeader(srvCalculo);
			// Se valida el XML. Posteriormente se enviar� en el servicio
			String strPolizaB64 = WSUtils.getStringResponse(polizaB64);
			//WSUtils.getXMLPoliza(strPolizaB64);
			
			int intEncontrado = strPolizaB64.indexOf("SeguroAgrario"); 	   
	        logger.debug ("Valor de intEncontrado: "+intEncontrado);
			   
	        // validamos el XML
	        WSUtils.getXMLValidar(strPolizaB64);
			
			// Parametros de envio al Servicio Web
			ValidarRequest parameters = new ValidarRequest();
			parameters.setPoliza(polizaB64);
			
			logger.debug("Llamando a Servicio de ContratacionUtilidades para Corredurias Externas : VALIDAR>> ");
			
			ValidarResponse response = srvCalculo.validar(parameters);
			
			if (response != null) {
				Base64Binary respuesta = response.getAcuseRecibo();
				
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				acuseRecibo = new Base64Binary();
				acuseRecibo.setValue(respuesta.getValue());
				acuseRecibo.setContentType(TEXT_XML);
				
				logger.debug ("Valor de acuseRecibo:"+acuseRecibo.toString());
				retorno.put("acuse", acuseRecibo);
				return retorno;
			}
		} catch (ValidacionServiceException e) {
			logger.error("Error al validar la poliza antes de enviarla al Servicio Web de Validar", e);
			throw new ConfirmacionServiceException(e);
		} catch (es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException e) {
			logger.error("Error AgrException al llamar al servicio web de Validar", e);
			throw new ConfirmarExtException.AgrWSException(WSUtils.debugAgrException(e));
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Validar", e);
			throw new ConfirmacionServiceException("Error inesperado al llamar al servicio web de Validar", e);
		}
		return retorno;
	}
	
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Inicio */
	public static  Map<String, Base64Binary> calcularPoliza(final Base64Binary polizaB64, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		
		logger.debug("Llamada al WebService ContratacionSCCalculo.");
		Base64Binary acuseRecibo = null;
		
		Map<String, Base64Binary> retorno = new HashMap<String, Base64Binary>();
		
		// Establece el Proxy
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();
		URL wsdlLocation = null;
		String url = null;
		try {
			wsdlLocation = new URL(FILE + realPath + System.getProperty(FILE_SEPARATOR) + WSUtils.getBundleProp("contratacion.wsdl"));		
		} catch (MalformedURLException e1) {
			throw new Exception("Imposible recuperar el WSDL de Calculo. Revise la Ruta: " + url, e1);
		} catch (NullPointerException e1) {
			throw new Exception("Imposible recuperar el WSDL de Calculo. Revise la Ruta: " + url, e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp(PROPIEDAD_CONTRATACION + LOCATION);
		String wsPort = WSUtils.getBundleProp(PROPIEDAD_CONTRATACION + PORT);
		String wsService = WSUtils.getBundleProp(PROPIEDAD_CONTRATACION + SERVICE);
		// jax-ws properties
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_ENDPOINT_BEFORE, "true");
		System.setProperty(COM_SUN_XML_WSS_PROVIDER_WSIT_SECURITY_TUBE_FACTORY_DUMP_LEVEL, "FINE");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		try {
			// Crea el envoltorio para la llamada al servicio web de calculo
			// Crea el envoltorio para la llamada al servicio web de contratacion
			ContratacionSCUtilidades_Service srv = new ContratacionSCUtilidades_Service(wsdlLocation, serviceName);
			ContratacionSCUtilidades srvCalculo = srv.getPort(portName, ContratacionSCUtilidades.class);

			
			// A�ade la cabecera de seguridad
			WSUtils.addSecurityHeader(srvCalculo);
			// Se valida el XML. Posteriormente se enviar� en el servicio
			String strPolizaB64 = WSUtils.getStringResponse(polizaB64);
			//WSUtils.getXMLPoliza(strPolizaB64);
			
			int intEncontrado = strPolizaB64.indexOf("SeguroAgrario"); 	   
	        logger.debug ("Valor de intEncontrado: "+intEncontrado);
			   
            if(intEncontrado < 0){
        	   logger.debug ("Formato Unificado");
  			   WSUtils.getXMLCalculoUnificado(strPolizaB64);
  			   logger.debug("Llamando a Servicio de Contratacion: CALCULO>> " +strPolizaB64);	
            }else {
        	   logger.debug ("Formato No Unificado");
        	   WSUtils.getXMLCalculo(strPolizaB64);
        	   logger.debug("Llamando a Servicio de Contratacion (No Unif): CALCULO>> " +strPolizaB64);
            }
			
			// Parametros de envio al Servicio Web
			CalcularRequest parameters = new CalcularRequest();
			parameters.setPoliza(polizaB64);
			
			ObjectFactory o = new ObjectFactory();	
			parameters.setDatosAdicionales(o.createCalcularRequestDatosAdicionales(Boolean.TRUE));
			
			logger.debug("Llamando a Servicio de Calculo para Corredurias Externas : CALCULAR>> ");
			
			CalcularResponse response = srvCalculo.calcular(parameters);
			
			if (response != null) {
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				acuseRecibo = new Base64Binary();
				acuseRecibo.setValue(response.getAcuseRecibo().getValue());
				acuseRecibo.setContentType(TEXT_XML);
				
				Base64Binary respuesta = response.getAcuseRecibo();
				// Se comprueba si hay XML de calculo, y si lo hay , se crea el objeto y se devuelve
				if (response.getCalculo() != null && 
					response.getCalculo().getValue() != null && 
					response.getCalculo().getValue().length > 0) {
					
					respuesta = response.getCalculo();
					retorno.put("calculo", respuesta);
				}
				
				logger.debug ("Valor de acuseRecibo:"+acuseRecibo.toString());
				retorno.put("acuse", acuseRecibo);
				return retorno;

			}
		} catch (ValidacionServiceException e) {
			logger.error("Error al validar la p�liza antes de enviarla al Servicio Web de C�lculo", e);
			throw new ConfirmacionServiceException(e);
		} catch (es.agroseguro.serviciosweb.contratacionscconfirmacion.AgrException e) {
			logger.error("Error AgrException al llamar al servicio web de C�lculo", e);
			throw new ConfirmarExtException.AgrWSException(WSUtils.debugAgrException(e));
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de C�lculo", e);
			throw new ConfirmacionServiceException("Error inesperado al llamar al servicio web de C�lculo", e);
		}
		return retorno;
	}
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Fin */
	
	/**
	 * 
	 * @param idCupon
	 * @param tipoPoliza
	 * @param calcularSituacionActual
	 * @param xml
	 * @param realPath
	 * @param isAnexRedCap
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Base64Binary> calcularAnx(final String idCupon, final String tipoPoliza,
			final boolean calcularSituacionActual, final Base64Binary xml, final String realPath,
			final Boolean isAnexRedCap) throws Exception {
		
		logger.debug("ContratacionExtConfirmacionWS - calcularAnx() - init");
				
		Map<String, Base64Binary> retorno = null;
		Map<String, Base64Binary> response = null;
		
		if (isAnexRedCap) {
			SWAnexoRCHelper h = new SWAnexoRCHelper();
			response = h.calculoModificacionCuponActivoRCExt(realPath, idCupon, calcularSituacionActual, xml);
		} else {
			CalculoModificacionHelper h = new CalculoModificacionHelper();
			response = h.getCalculoModificacionCuponActivoACM(realPath, idCupon, tipoPoliza.charAt(0), calcularSituacionActual, xml);
		}	
		
		if (response != null) {
			retorno = new HashMap<String, Base64Binary>();

			Base64Binary calculoModificacion = response.get(CALCULO_MODIFICACION);
			Base64Binary calculoOriginal = response.get(CALCULO_ORIGINAL);
			Base64Binary diferenciasCoste = response.get(DIFERENCIAS_COSTE);

			// Se comprueba si hay XMLs de calculo, y si los hay, se a�aden al retorno
			if (calculoModificacion != null && calculoModificacion.getValue() != null
					&& calculoModificacion.getValue().length != 0)
				retorno.put(CALCULO_MODIFICACION, calculoModificacion);

			if (calculoOriginal != null && calculoOriginal.getValue() != null && calculoOriginal.getValue().length != 0)
				retorno.put(CALCULO_ORIGINAL, calculoOriginal);

			if (diferenciasCoste != null && diferenciasCoste.getValue() != null
					&& diferenciasCoste.getValue().length != 0)
				retorno.put(DIFERENCIAS_COSTE, diferenciasCoste);

			logger.debug("ContratacionExtConfirmacionWS - calcularAnx() - end");
		}
		
		return retorno;
	}
	
}
