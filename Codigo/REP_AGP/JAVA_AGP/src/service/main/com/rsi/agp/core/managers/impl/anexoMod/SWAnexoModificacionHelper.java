package com.rsi.agp.core.managers.impl.anexoMod;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Clob;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument;
import es.agroseguro.seguroAgrario.estadoContratacion.EstadoContratacionDocument;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AnulacionCuponRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AnulacionCuponResponse;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ConsultarContratacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ConsultarContratacionResponse;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion_Service;
import es.agroseguro.serviciosweb.contratacionscmodificacion.EnvioModificacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.EnvioModificacionResponse;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ObjectFactory;
import es.agroseguro.serviciosweb.contratacionscmodificacion.SolicitudModificacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.SolicitudModificacionResponse;

/**
 * Helper para el Servicio de anexos de modificacion.
 * 
 * @author T-Systems
 * 
 */
public class SWAnexoModificacionHelper {

	private static final Log logger = LogFactory
			.getLog(SWAnexoModificacionHelper.class);

	public PolizaActualizadaResponse getPolizaActualizada(
			final String referencia, final BigDecimal plan,
			final String realPath) throws SWConsultaContratacionException,
			AgrException, Exception {
		return getPolizaActualizadaUnificada(referencia, plan, realPath, false);
	}
	
	public PolizaActualizadaResponse getPolizaActualizadaUnificado(final String referencia, final BigDecimal plan,
			final String realPath, final boolean isPolizaGanado)
			throws SWConsultaContratacionException, AgrException, Exception {
		return getPolizaActualizadaUnificada(referencia, plan, realPath, isPolizaGanado);
	}

	/**
	 * Configura y realiza la llamada al servicio Web para obtener la poliza
	 * actualizada de agroseguro
	 */
	public PolizaActualizadaResponse getPolizaActualizadaUnificada(
			final String referencia, final BigDecimal plan,
			final String realPath, final boolean isPolizaGanado)
			throws SWConsultaContratacionException, AgrException, Exception {
		ContratacionSCModificacion srvAnexoMod = getSrvAnexoModificacion(
				realPath, isPolizaGanado);
		// Parametros de envio al Servicio Web
		ConsultarContratacionRequest parameters = new ConsultarContratacionRequest();
		parameters.setPlan(plan.intValue());
		parameters.setReferencia(referencia);
		ConsultarContratacionResponse response = null;
		try {
			
			logger.debug("plan: " + plan.intValue());
			logger.debug("referencia: " + referencia);
			
			response = srvAnexoMod.consultarContratacion(parameters);
			logger.debug("response: " + response);
			if (response != null) {
				/*
				 * En la respuesta tendre: - El xml de la poliza principal - El
				 * xml de la complementaria (si está contratada) - Un xml que
				 * indica el estado de la contratacion
				 */
				logger.debug("la response es distinta de null....");
				PolizaActualizadaResponse respuesta = getPolizaActualizadaFromResponse(
						response, isPolizaGanado);
				logger.debug("respuesta: " + respuesta);
				return respuesta;
			}
		} catch (AgrException e) {
			// El servicio ha devuelto una excepcion => tratar el error e
			// informar al usuario
			throw e;
		} catch (Exception e) {
			// Error inesperado
			logger.error("Error inesperado al llamar al servicio web de consulta de contratacion",		   e);
			throw new Exception("Error inesperado al llamar al servicio web de consulta de contratacion",	e);
		}
		return null;
	}

	public com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionResponse getSolicitudModificacion(
			final String referencia, final BigDecimal plan,
			final String realPath) throws AgrException, Exception {
		return getSolicitudModificacionUnificada(referencia, plan, realPath,
				false);
	}

	/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
	/* Por los desarrollos de esta peticion tanto las polizas agricolas como las de ganado
	 * irán por el mismo end-point y con formato Unificado
	 */
	public com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionResponse getSolicitudModificacionUnificado(
			final String referencia, final BigDecimal plan,
			final String realPath) throws AgrException, Exception {
		return getSolicitudModificacionUnificada(referencia, plan, realPath,
				true);
	}

	/**
	 * Configura y realiza la llamada para obtener el cupon de modificacion, las
	 * polizas principal y complementaria y el estado de contratacion de estas
	 * 
	 * @param referencia
	 *            Referencia de la poliza sobre la cual se solicita el cupon de
	 *            modificacion
	 * @param plan
	 *            Plan
	 * @param realPath
	 * @throws AgrException
	 *             Error en la llamada al SW
	 */
	private com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionResponse getSolicitudModificacionUnificada(
			final String referencia, final BigDecimal plan,
			final String realPath, final boolean isPolizaGanado)
			throws AgrException, Exception {
		ContratacionSCModificacion srvAnexoMod = getSrvAnexoModificacion(
				realPath, isPolizaGanado);
		// Parámetros a enviar al SW
		SolicitudModificacionRequest params = new SolicitudModificacionRequest();
		params.setPlan(plan.intValue());
		params.setReferencia(referencia);
		logger.debug("SWAnexoModificacionHelper - getSolicitudModificacionUnificada - params->plan: " + params.getPlan() + " params->referencia: "+ params.getReferencia());
		// Llamada al SW
		SolicitudModificacionResponse respuesta = srvAnexoMod
				.solicitudModificacion(params);
		if (respuesta != null) {
			logger.debug("SWAnexoModificacionHelper - getSolicitudModificacionUnificada - respuesta: " + params.toString());
			return getSolicitudModificacionResponse(respuesta, isPolizaGanado);
		}
		return null;
	}

	public AcuseRecibo getConfirmacionModificacion(final String idCupon,
			final boolean revAdministrativa, final Clob xmlPpal,
			final Clob xmlCpl, final String realPath) throws AgrException,
			Exception {
		return getConfirmacionModificacionUnificada(idCupon, revAdministrativa,
				xmlPpal, xmlCpl, realPath, false);
	}

	public AcuseRecibo getConfirmacionModificacionUnificada(final String idCupon,
			final boolean revAdministrativa, final Clob xmlPpal,
			final String realPath) throws AgrException, Exception {
		return getConfirmacionModificacionUnificada(idCupon, revAdministrativa,
				xmlPpal, null, realPath, true);
	}

	/**
	 * Configura y realiza la llamada al SW para confirmar el AM
	 * 
	 * @param idCupon
	 * @param revAdministrativa
	 * @param xmlPpal
	 * @param xmlCpl
	 * @param realPath
	 * @throws AgrException
	 * @throws Exception
	 */
	private AcuseRecibo getConfirmacionModificacionUnificada(
			final String idCupon, final boolean revAdministrativa,
			final Clob xmlPpal, final Clob xmlCpl, final String realPath,
			final boolean isPolizaGanado) throws AgrException, Exception {
		
		ContratacionSCModificacion srvAnexoMod = getSrvAnexoModificacion(
				realPath, isPolizaGanado);
		
		logger.debug("SWAnexoModificacionHelper- getConfirmacionModificacionUnificada");
		
		// Comprueba si el anexo es de principal o de complementaria
		boolean amPpal = xmlPpal != null;
		// Parametros a enviar al SW
		EnvioModificacionRequest params = new EnvioModificacionRequest();
		params.setCuponModificacion(idCupon);
		params.setForzarRevisionAdministrativa(revAdministrativa);
		// Xml de la principal o complementaria
		String xml = WSUtils.convertClob2String(xmlPpal != null ? xmlPpal
				: xmlCpl);
		// Modifica el xml para que lo acepte el servicio de validacion
		final String cabecera = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		
		xml = cabecera + xml.replace("xml-fragment", "ns2:Poliza");
		
		logger.debug("Valor de xml << CONFIRMACION ANX: >>" +xml);
		
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		base64Binary.setValue(xml.getBytes("UTF-8"));
		if (amPpal) {
			params.setPolizaPrincipal(new ObjectFactory()
					.createEnvioModificacionRequestPolizaPrincipal(base64Binary));
		} else {
			params.setPolizaComplementaria(new ObjectFactory()
					.createEnvioModificacionRequestPolizaComplementaria(base64Binary));
		}
		EnvioModificacionResponse envioModificacionResponse = srvAnexoMod
				.envioModificacion(params);
		if (envioModificacionResponse != null) {
			// Se crea el String con el XML recibido y se envuelve en un Acuse
			// de Recibo
			Base64Binary respuesta = envioModificacionResponse.getAcuse();
			byte[] arrayAcuse = respuesta.getValue();
			String acuse = new String(arrayAcuse, "UTF-8");
			AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory
					.parse(new StringReader(acuse));
			return acuseReciboDoc.getAcuseRecibo();
		}
		return null;
	}

	public String getAnulacionCupon(final String idCupon, final String realPath)
			throws AgrException, Exception {
		return getAnulacionCuponUnificada(idCupon, realPath, false);
	}

	public String getAnulacionCuponUnificado(final String idCupon,
			final String realPath) throws AgrException, Exception {
		return getAnulacionCuponUnificada(idCupon, realPath, true);
	}

	/**
	 * Configura y realiza la llamada para anular el cupon de modificacion
	 * 
	 * @param idCupon
	 *            Id del cupon a anular
	 * @param realPath
	 * @return
	 * @throws AgrException
	 * @throws Exception
	 */
	private String getAnulacionCuponUnificada(final String idCupon,
			final String realPath, final boolean isPolizaGanado)
			throws AgrException, Exception {
		ContratacionSCModificacion srvAnexoMod = getSrvAnexoModificacion(
				realPath, isPolizaGanado);
		// Parámetros a enviar al SW
		AnulacionCuponRequest params = new AnulacionCuponRequest();
		params.setCuponModificacion(idCupon);
		// Llamada al SW
		AnulacionCuponResponse respuesta = srvAnexoMod.anulacionCupon(params);
		if (respuesta != null) {
			return respuesta.getRespuesta();
		}
		return null;
	}

	/**
	 * Metodo para crear los objetos necesarios para las llamadas al servicio
	 * web.
	 * 
	 * @param realPath
	 *            Ruta real de los ficheros "wsdl"
	 * @return Manejador para las llamadas al servicio de contratacion de
	 *         anexos.
	 */
	private ContratacionSCModificacion getSrvAnexoModificacion(
			final String realPath, final boolean isPolizaGanado) {
		if (!WSUtils.isProxyFixed()) {
			WSUtils.setProxy();
		}
		
		logger.debug("Dento de getSrvAnexoModificacion");
		URL wsdlLocation = null;
		try {
			/* Pet. 57626 ** MODIF TAM (17.06.2020) */
			/* Tanto Ganado como Agricolas van siempre por Formato Unificado */
			wsdlLocation = new URL(
					"file:"
							+ realPath
							+ System.getProperty("file.separator")
							+ WSUtils
									.getBundleProp("anexoModificacionWS.wsdl"));
		} catch (MalformedURLException e1) {
			throw new SWConsultaContratacionException(
					"Imposible recuperar el WSDL de consulta de contratacion. Revise la Ruta: "
							+ ((wsdlLocation != null) ? wsdlLocation.toString()
									: ""), e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y
		// Port
		String wsLocation = WSUtils
				.getBundleProp("anexoModificacionWS.location");
		String wsPort = WSUtils.getBundleProp("anexoModificacionWS.port");
		String wsService = WSUtils.getBundleProp("anexoModificacionWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de contratacion
		ContratacionSCModificacion_Service srv = new ContratacionSCModificacion_Service( wsdlLocation, serviceName);
		ContratacionSCModificacion srvAnexoMod = srv.getPort(portName, ContratacionSCModificacion.class);
		
		
		
		logger.debug("ContratacionModificacion <srvAnexoMod>>" +srvAnexoMod.toString());
		// AÃ±ade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvAnexoMod);
		return srvAnexoMod;
	}

	/**
	 * Metodo que rellena el objeto para generar el informe a partir de la
	 * respuesta del servicio web.
	 * 
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws XmlException
	 * @throws IOException
	 */
	private PolizaActualizadaResponse getPolizaActualizadaFromResponse(
			final ConsultarContratacionResponse response,
			final boolean isPolizaGanado) throws UnsupportedEncodingException,
			XmlException, IOException {
		logger.debug("INIT - getPolizaActualizadaFromResponse ... ");
		PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
		// xml de la poliza principal
		
		logger.debug("response.getPolizaPrincipal(): " + response.getPolizaPrincipal());
		Base64Binary ppal = response.getPolizaPrincipal();
		byte[] byteArrayPpal = ppal.getValue();
		String xmlDataPpal = new String(byteArrayPpal, Constants.DEFAULT_ENCODING);
		XmlObject poliza = XmlObject.Factory
				.parse(new StringReader(xmlDataPpal));
		
		logger.debug("poliza ***** : " + poliza);
		logger.debug("isPolizaGanado: " + isPolizaGanado);
		
		/* Pet. 57626 ** MODIF TAM (15.06.2020) ** Inicio */
		/* Por los desarrollos de esta peticion, tatno los anexos de ganado como agricolas van por el mismo
		 * end-point y los dos por formato Unificado.
		 */
		if (isPolizaGanado) {
			respuesta.setPolizaGanado((es.agroseguro.contratacion.PolizaDocument) poliza);
		} else {
			respuesta.setPolizaPrincipalUnif((es.agroseguro.contratacion.PolizaDocument) poliza);
		}
		
		// xml de la poliza complementaria
		if (!isPolizaGanado) {	
			Base64Binary cpl = response.getPolizaComplementaria();
			logger.debug("response.getPolizaComplementaria(): " + response.getPolizaComplementaria());
			byte[] byteArrayCpl = cpl.getValue();
			logger.debug("byteArrayCpl: " + byteArrayCpl);
			logger.debug("byteArrayCpl length: " + byteArrayCpl.length);
			
			if (byteArrayCpl != null && byteArrayCpl.length > 0) {
				String xmlDataCpl = new String(byteArrayCpl,
						Constants.DEFAULT_ENCODING);
				logger.debug("xmlDataCpl: " + xmlDataCpl);
				es.agroseguro.contratacion.PolizaDocument polizaCpl = es.agroseguro.contratacion.PolizaDocument.Factory
						.parse(new StringReader(xmlDataCpl));
				logger.debug("polizaCpl: " + polizaCpl);
				respuesta.setPolizaComplementariaUnif(polizaCpl);
			}
		}
		// estado de la contratacion
		logger.debug("... para el estado de la contratacion ... ");
		Base64Binary estado = response.getEstadoContratacion();
		logger.debug("estado: " + estado);
		byte[] byteArrayEstado = estado.getValue();
		logger.debug("byteArrayEstado: " + byteArrayEstado);
		String xmlDataEstado = new String(byteArrayEstado,
				Constants.DEFAULT_ENCODING);
		logger.debug("xmlDataEstado: " + xmlDataEstado);
		EstadoContratacionDocument estadoContratacion = EstadoContratacionDocument.Factory
				.parse(new StringReader(xmlDataEstado));
		logger.debug("estadoContratacion: " + estadoContratacion);
		respuesta.setEstadoContratacion(estadoContratacion);
		return respuesta;
	}

	/**
	 * Rellena el objeto que encapsula la respuesta del SW de Solicitud de
	 * Modificacion
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws XmlException
	 */
	private com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionResponse getSolicitudModificacionResponse(
			final SolicitudModificacionResponse response,
			final boolean isPolizaGanado) throws XmlException, IOException {
		com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionResponse respuesta = new com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionResponse();
		// Xml de la poliza principal
		Base64Binary ppal = response.getPolizaPrincipal();
		byte[] byteArrayPpal = ppal.getValue();
		if (byteArrayPpal != null && byteArrayPpal.length > 0) {
			String xmlDataPpal = new String(byteArrayPpal,
					Constants.DEFAULT_ENCODING);
			XmlObject poliza = XmlObject.Factory.parse(new StringReader(
					xmlDataPpal));
			
			/* Pet. 57626 ** MODIF TAM (15.06.2020) ** Inicio */
			/* Por los desarrollos de esta peticion, tatno los anexos de ganado como agricolas van por el mismo
			 * end-point y los dos por formato Unificado.
			 */
			if (isPolizaGanado) {
				respuesta.setPolizaGanado((es.agroseguro.contratacion.PolizaDocument) poliza);
			} else {
				respuesta.setPolizaPrincipalUnif((es.agroseguro.contratacion.PolizaDocument) poliza);
			}
		}
		if (!isPolizaGanado) {
			// Xml de la poliza complementaria
			Base64Binary cpl = response.getPolizaComplementaria();
			byte[] byteArrayCpl = cpl.getValue();
			if (byteArrayCpl != null && byteArrayCpl.length > 0) {
				String xmlDataCpl = new String(byteArrayCpl,
						Constants.DEFAULT_ENCODING);
				
				es.agroseguro.contratacion.PolizaDocument polizaCpl = es.agroseguro.contratacion.PolizaDocument.Factory.parse(new StringReader(xmlDataCpl));
				respuesta.setPolizaComplementariaUnif(polizaCpl);
			}
		}
		// Estado de la contratacion
		Base64Binary estado = response.getEstadoContratacion();
		byte[] byteArrayEstado = estado.getValue();
		if (byteArrayEstado != null && byteArrayEstado.length > 0) {
			String xmlDataEstado = new String(byteArrayEstado,
					Constants.DEFAULT_ENCODING);
			EstadoContratacionDocument estadoContratacion = EstadoContratacionDocument.Factory
					.parse(new StringReader(xmlDataEstado));

			respuesta.setEstadoContratacion(estadoContratacion);
		}
		// Cupon de modificacion
		Base64Binary cupon = response.getCuponModificacion();
		byte[] byteCupon = cupon.getValue();
		if (byteCupon != null && byteCupon.length > 0) {
			String xmlCupon = new String(byteCupon, Constants.DEFAULT_ENCODING);
			CuponModificacionDocument cuponModificacion = CuponModificacionDocument.Factory
					.parse(new StringReader(xmlCupon));

			respuesta.setCuponModificacion(cuponModificacion);
		}
		return respuesta;
	}
	
	/* Pet. 57626 ** MODIF TAM (15.06.2020) ** Inicio */
	/* Pet. 57626 ** MODIF TAM (15.06.2020) ** Fin */
}