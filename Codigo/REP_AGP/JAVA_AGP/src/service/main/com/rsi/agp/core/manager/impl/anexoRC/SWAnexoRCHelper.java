package com.rsi.agp.core.manager.impl.anexoRC;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Clob;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3._2005._05.xmlmime.Base64Binary;
import org.xml.sax.InputSource;

import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.manager.impl.anexoRC.ayudaCausa.Causas;
import com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion.SolicitudReduccionCapResponse;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument;
import es.agroseguro.seguroAgrario.estadoContratacion.EstadoContratacionDocument;
import es.agroseguro.serviciosweb.contratacionayudas.AyudaCausaResponse;
import es.agroseguro.serviciosweb.contratacionayudas.ContratacionAyudas;
import es.agroseguro.serviciosweb.contratacionayudas.ContratacionAyudas_Service;
import es.agroseguro.serviciosweb.contratacionayudas.ParametrosAyudaCausa;
import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.CalculoModificacionCuponActivoRequest;
import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.ContratacionSCCalculoModificaciones;
import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.ContratacionSCCalculoModificaciones_Service;
import es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.ParametrosSalida;
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
import es.agroseguro.serviciosweb.contratacionscmodificacion.ValidacionModificacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ValidacionModificacionResponse;
import es.agroseguro.tipos.PolizaReferenciaTipo;

//P0079361
public class SWAnexoRCHelper {

	private static final Log logger = LogFactory.getLog(SWAnexoRCHelper.class);

	private es.agroseguro.contratacion.PolizaDocument polizaPrincipalUnif;

	// BLOQUE 1
	public PolizaActualizadaRCResponse consultarContratacionRC(final String referencia, final BigDecimal plan,
			final String realPath) throws SWConsultaContratacionException, Exception {
		logger.debug("SWAnexoRCHelper.consultarContratacionRC() [INIT] con el plan/referencia " + plan.intValue() + "/"
				+ referencia);

		ContratacionSCModificacion srvAnexoMod = getSrvReduccionCapitalModificacion(realPath);

		ConsultarContratacionRequest parameters = new ConsultarContratacionRequest();
		parameters.setPlan(plan.intValue());
		parameters.setReferencia(referencia);

		ConsultarContratacionResponse response = null;
		PolizaActualizadaRCResponse respuesta = null;

		try {
			// LLamamos al servicio de consulta de la contratacion
			response = srvAnexoMod.consultarContratacion(parameters);

			if (response != null) {
				respuesta = getPolizaActualizadaFromResponse(response);
			}
		} catch (AgrException e) {
			logger.error(
					"SWAnexoRCHelper.consultarContratacionRC() error al llamar a la operacion consultarContratacion",
					e);
			throw e;
		} catch (Exception e) {
			logger.error(
					"SWAnexoRCHelper.consultarContratacionRC() error al llamar a la operacion consultarContratacion",
					e);

			throw new Exception("Error al llamar al servicio web de contratacion", e);
		}

		logger.debug("SWAnexoRCHelper.consultarContratacionRC() [END]");
		return respuesta;
	}
	// BLOQUE 1

	// BLOQUE 2
	public SolicitudReduccionCapResponse solicitudModificacionRC(final String referencia, final BigDecimal plan,
			final String realPath) throws Exception {

		logger.debug("SWAnexoRCHelper.solicitudModificacionRC() [INIT] con el plan/referencia " + plan.intValue() + "/"
				+ referencia);

		ContratacionSCModificacion srvAnexoMod = getSrvReduccionCapitalModificacion(realPath);

		SolicitudModificacionRequest params = new SolicitudModificacionRequest();
		params.setPlan(plan.intValue());
		params.setReferencia(referencia);

		SolicitudModificacionResponse responseAgro = null;
		SolicitudReduccionCapResponse respuestaInt = new SolicitudReduccionCapResponse();
		try {

			// SOLICITUD MODIFICACION
			responseAgro = srvAnexoMod.solicitudModificacion(params);
			// SOLICITUD MODIFICACION

			if (responseAgro != null) {
				// BLOQUE 1
				Base64Binary ppal = responseAgro.getPolizaPrincipal();
				byte[] byteArrayPpal = ppal.getValue();
				if (byteArrayPpal != null && byteArrayPpal.length > 0) {
					String xmlDataPpal = new String(byteArrayPpal, Constants.DEFAULT_ENCODING);
					XmlObject poliza = XmlObject.Factory.parse(new StringReader(xmlDataPpal));

					respuestaInt.setPolizaPrincipalUnif((es.agroseguro.contratacion.PolizaDocument) poliza);
				}
				// BLOQUE 1

				// BLOQUE 2
				Base64Binary estado = responseAgro.getEstadoContratacion();
				byte[] byteArrayEstado = estado.getValue();
				if (byteArrayEstado != null && byteArrayEstado.length > 0) {
					String xmlDataEstado = new String(byteArrayEstado, Constants.DEFAULT_ENCODING);
					EstadoContratacionDocument estadoContratacion = EstadoContratacionDocument.Factory
							.parse(new StringReader(xmlDataEstado));

					respuestaInt.setEstadoContratacion(estadoContratacion);
				}
				// BLQOUE 2

				// BLOQUE 2
				Base64Binary cupon = responseAgro.getCuponModificacion();
				byte[] byteCupon = cupon.getValue();
				if (byteCupon != null && byteCupon.length > 0) {
					String xmlCupon = new String(byteCupon, Constants.DEFAULT_ENCODING);
					CuponModificacionDocument cuponModificacion = CuponModificacionDocument.Factory
							.parse(new StringReader(xmlCupon));

					respuestaInt.setCuponModificacion(cuponModificacion);
				}
				// BLQOUE 2
			}

		} catch (AgrException e) {
			logger.error(
					"SWAnexoRCHelper.solicitudModificacionRC() error al llamar a la operacion solicitudModificacion",
					e);
			throw e;
		} catch (Exception e) {
			logger.error(
					"SWAnexoRCHelper.solicitudModificacionRC() error al llamar a la operacion solicitudModificacion",
					e);

			throw new Exception("Error al llamar al servicio web de contratacion", e);
		}

		logger.debug("SWAnexoRCHelper.solicitudModificacionRC() [END]");
		return respuestaInt;
	}
	// BLOQUE 2

	// BLOQUE 3
	public AcuseRecibo envioModificacionRC(final String idCupon, final boolean revAdministrativa, final Clob xmlPpal,
			final Clob xmlCpl, final String realPath) throws Exception {

		logger.debug("SWAnexoRCHelper.envioModificacionRC() [INIT] con el idCupon: " + idCupon);

		ContratacionSCModificacion srvAnexoMod = getSrvReduccionCapitalModificacion(realPath);

		EnvioModificacionResponse envioModificacionResponse = null;

		AcuseRecibo acuseReciboToRet = null;

		EnvioModificacionRequest params = new EnvioModificacionRequest();
		params.setCuponModificacion(idCupon);

		// falta validar si es necesario
		String xml = WSUtils.convertClob2String(xmlPpal != null ? xmlPpal : xmlCpl);
		logger.error("[Valor de xml de CONFIRMACION RC]" + xml);
		// falta validar si es neceario

		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		base64Binary.setValue(xml.getBytes("UTF-8"));

		params.setPolizaPrincipal(new ObjectFactory().createEnvioModificacionRequestPolizaPrincipal(base64Binary));

		try {
			// ENVIO MODIFICACION
			envioModificacionResponse = srvAnexoMod.envioModificacion(params);
			// ENVIO MODIFICACION

			if (envioModificacionResponse != null) {
				Base64Binary respuesta = envioModificacionResponse.getAcuse();
				byte[] arrayAcuse = respuesta.getValue();
				String acuse = new String(arrayAcuse, "UTF-8");
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(acuse));

				acuseReciboToRet = acuseReciboDoc.getAcuseRecibo();
			}

		} catch (AgrException e) {
			logger.error("SWAnexoRCHelper.envioModificacionRC() error al llamar a la operacion envioModificacion", e);
			throw e;
		} catch (Exception e) {
			logger.error("SWAnexoRCHelper.envioModificacionRC() error al llamar a la operacion envioModificacion", e);

			throw new Exception("Error al llamar al servicio web de contratacion", e);
		}

		logger.debug("SWAnexoRCHelper.envioModificacionRC() [END]");

		return acuseReciboToRet;
	}

	// BLOQUE 3

	// BLOQUE 4
	public String anulacionCuponRC(final String idCupon, final String realPath) throws Exception {
		logger.debug("SWAnexoRCHelper.anulacionCuponRC() [INIT] con el idCupon: " + idCupon);

		ContratacionSCModificacion srvAnexoMod = getSrvReduccionCapitalModificacion(realPath);

		AnulacionCuponResponse respuesta = null;

		String retorno = Constants.STR_EMPTY;

		try {
			AnulacionCuponRequest params = new AnulacionCuponRequest();
			params.setCuponModificacion(idCupon);

			// ANULACION CUPON
			respuesta = srvAnexoMod.anulacionCupon(params);
			// ANULACION CUPON

			if (respuesta != null) {
				retorno = respuesta.getRespuesta();
			}

		} catch (AgrException e) {
			logger.error("SWAnexoRCHelper.anulacionCuponRC() error al llamar a la operacion anulacionCupon", e);
			throw e;
		} catch (Exception e) {
			logger.error("SWAnexoRCHelper.anulacionCuponRC() error al llamar a la operacion anulacionCupon", e);

			throw new Exception("Error al llamar al servicio web de contratacion", e);
		}

		logger.debug("SWAnexoRCHelper.anulacionCuponRC() [END]");
		return retorno;
	}
	// BLOQUE 4

	// BLOQUE 5
	public AcuseRecibo validacionModificacionRC(String xmlpoliza, final String idCupon, final String realPath)
			throws Exception {

		logger.debug("SWAnexoRCHelper.validacionModificacionRC() [INIT] con el idCupon: " + idCupon);

		ContratacionSCModificacion srvAnexoMod = getSrvReduccionCapitalModificacion(realPath);

		// Saber que dato es obligatorio y necesario settear
		ValidacionModificacionResponse response = null;

		AcuseRecibo acuseReciboToRet = null;

		ValidacionModificacionRequest params = new ValidacionModificacionRequest();
		params.setCuponModificacion(idCupon);

		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		base64Binary.setValue(xmlpoliza.getBytes("UTF-8"));

		params.setPolizaPrincipal(new ObjectFactory().createValidacionModificacionRequestPolizaPrincipal(base64Binary));

		try {
			// LLAMADA AL SERVICIO WEB DE VALIDACION
			response = srvAnexoMod.validacionModificacion(params);
			// LLAMADA AL SERVICIO WEB DE VALIDACION

			if (response != null) {
				Base64Binary respuesta = response.getAcuse();
				byte[] arrayAcuse = respuesta.getValue();
				String acuse = new String(arrayAcuse, "UTF-8");
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(acuse));

				acuseReciboToRet = acuseReciboDoc.getAcuseRecibo();
			}
		} catch (AgrException e) {
			logger.error(
					"SWAnexoRCHelper.validacionModificacionRC() error al llamar a la operacion validacionModificacion",
					e);
			throw e;
		} catch (Exception e) {
			logger.error(
					"SWAnexoRCHelper.validacionModificacionRC() error al llamar a la operacion validacionModificacion",
					e);

			throw new Exception("Error al llamar al servicio web de contratacion", e);
		}

		logger.debug("SWAnexoRCHelper.validacionModificacionRC() [END]");
		return acuseReciboToRet;
	}
	// BLOQUE 5

	// BLOQUE 6
	public Causas getAyudaCausaRC(final String realPath) throws Exception {
		logger.debug("SWAnexoRCHelper.getAyudaCausaRC() [INIT]");

		ContratacionAyudas srvAyudaRiesgo = getSrvAyudaRiesgo(realPath);

		ParametrosAyudaCausa request = new ParametrosAyudaCausa();
		request.setSeguroTipo(Constants.STR_A);

		AyudaCausaResponse response = null;

		Causas causas = null;
		try {

			response = srvAyudaRiesgo.ayudaCausa(request);

			String xmlString = WSUtils.getStringResponse(response.getAyudaCausa());

			com.rsi.agp.core.manager.impl.anexoRC.ayudaCausa.ObjectFactory of = new com.rsi.agp.core.manager.impl.anexoRC.ayudaCausa.ObjectFactory();
			JAXBContext jaxbContext = JAXBContext.newInstance(of.getClass().getPackage().getName());

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			StringReader reader = new StringReader(xmlString);
			JAXBElement<Object> jObj = null;

			jObj = (JAXBElement<Object>) unmarshaller.unmarshal(new InputSource(reader));

			causas = (Causas) jObj.getValue();

		} catch (JAXBException e) {
			logger.error("SWAnexoRCHelper.getAyudaCausaRC() error al llamar a la operacion ayudaCausa", e);
		} catch (AgrException e) {
			logger.error("SWAnexoRCHelper.getAyudaCausaRC() error al llamar a la operacion ayudaCausa", e);
			throw e;
		} catch (Exception e) {
			logger.error("SWAnexoRCHelper.getAyudaCausaRC() error al llamar a la operacion ayudaCausa", e);

			throw new Exception("Error al llamar al servicio web de ayudas contratacion", e);
		}

		logger.debug("SWAnexoRCHelper.getAyudaCausaRC() [END]");
		return causas;
	}
	// BLOQUE 6

	private ContratacionAyudas getSrvAyudaRiesgo(final String realPath) {
		if (!WSUtils.isProxyFixed()) {
			WSUtils.setProxy();
		}

		logger.debug("Dento de getSrvAyudaRiesgo");

		URL wsdlLocation = null;

		try {
			wsdlLocation = new URL("file:" + realPath + System.getProperty("file.separator")
					+ WSUtils.getBundleProp("ayudasContratacion.wsdl"));
		} catch (MalformedURLException e1) {
			throw new SWAyudaRiesgoException("Imposible recuperar el WSDL de ayuda de contratacion. Revise la Ruta: "
					+ ((wsdlLocation != null) ? wsdlLocation.toString() : ""), e1);
		}

		String wsLocation = WSUtils.getBundleProp("ayudasContratacion.location");
		String wsPort = WSUtils.getBundleProp("ayudasContratacion.port");
		String wsService = WSUtils.getBundleProp("ayudasContratacion.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		ContratacionAyudas_Service srv = new ContratacionAyudas_Service(wsdlLocation, serviceName);
		ContratacionAyudas srvAyudaRiesgo = srv.getPort(portName, ContratacionAyudas.class);

		logger.debug("ContratacionAyudas <srvAyudaRiesgo>>" + srvAyudaRiesgo.toString());

		WSUtils.addSecurityHeader(srvAyudaRiesgo);

		return srvAyudaRiesgo;
	}

	private ContratacionSCModificacion getSrvReduccionCapitalModificacion(final String realPath) {
		if (!WSUtils.isProxyFixed()) {
			WSUtils.setProxy();
		}

		logger.debug("Dento de getSrvAnexoModificacion");

		URL wsdlLocation = null;

		try {
			wsdlLocation = new URL("file:" + realPath + System.getProperty("file.separator")
					+ WSUtils.getBundleProp("anexoReduccionCapiWS.wsdl"));
		} catch (MalformedURLException e1) {
			throw new SWConsultaContratacionException(
					"Imposible recuperar el WSDL de consulta de contratacion. Revise la Ruta: "
							+ ((wsdlLocation != null) ? wsdlLocation.toString() : ""),
					e1);
		}

		String wsLocation = WSUtils.getBundleProp("anexoModificacionWS.location");
		String wsPort = WSUtils.getBundleProp("anexoModificacionWS.port");
		String wsService = WSUtils.getBundleProp("anexoModificacionWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		ContratacionSCModificacion_Service srv = new ContratacionSCModificacion_Service(wsdlLocation, serviceName);
		ContratacionSCModificacion srvAnexoMod = srv.getPort(portName, ContratacionSCModificacion.class);

		logger.debug("ContratacionModificacion <srvAnexoMod>>" + srvAnexoMod.toString());

		WSUtils.addSecurityHeader(srvAnexoMod);

		return srvAnexoMod;
	}

	private PolizaActualizadaRCResponse getPolizaActualizadaFromResponse(final ConsultarContratacionResponse response)
			throws UnsupportedEncodingException, XmlException, IOException {
		logger.debug("INIT - getPolizaActualizadaFromResponse ... ");

		PolizaActualizadaRCResponse respuesta = new PolizaActualizadaRCResponse();

		// Poliza principal
		logger.debug("response.getPolizaPrincipal(): " + response.getPolizaPrincipal());
		
		Base64Binary ppal = response.getPolizaPrincipal();
		byte[] byteArrayPpal = ppal.getValue();
		String xmlDataPpal = new String(byteArrayPpal, Constants.DEFAULT_ENCODING);
		XmlObject poliza = XmlObject.Factory.parse(new StringReader(xmlDataPpal));

		logger.debug("poliza ***** : " + poliza);

		respuesta.setPolizaPrincipalUnif((es.agroseguro.contratacion.PolizaDocument) poliza);

		// Estado de la contratacion
		logger.debug("... para el estado de la contratacion ... ");
		Base64Binary estado = response.getEstadoContratacion();
		byte[] byteArrayEstado = estado.getValue();
		String xmlDataEstado = new String(byteArrayEstado, Constants.DEFAULT_ENCODING);
		logger.debug("xmlDataEstado: " + xmlDataEstado);
		EstadoContratacionDocument estadoContratacion = EstadoContratacionDocument.Factory.parse(new StringReader(xmlDataEstado));
		logger.debug("estadoContratacion: " + estadoContratacion);

		respuesta.setEstadoContratacion(estadoContratacion);

		// Poliza principal RC
		Base64Binary ppalRC = response.getPolizaPrincipalRC();
		byte[] byteArrayPpalRC = ppalRC.getValue();
		String xmlDataPpalRC = new String(byteArrayPpalRC, Constants.DEFAULT_ENCODING);
		if (!xmlDataPpalRC.equals("")) {
			XmlObject polizaRC = XmlObject.Factory.parse(new StringReader(xmlDataPpalRC));
			
			logger.debug("polizaRC ***** : " + polizaRC);
			
			respuesta.setPolizaPrincipalRC(polizaRC);
		}
		
		
		// Poliza principal RC
		Base64Binary ppalRCComp = response.getPolizaComplementariaRC();
		byte[] byteArrayPpalRCComp = ppalRCComp.getValue();
		String xmlDataPpalRCComp = new String(byteArrayPpalRCComp, Constants.DEFAULT_ENCODING);
		if (!xmlDataPpalRCComp.equals("")) {
			XmlObject polizaRCComp = XmlObject.Factory.parse(new StringReader(xmlDataPpalRCComp));
		
			logger.debug("polizaRCComp ***** : " + polizaRCComp);
		
			respuesta.setPolizaComplementariaRC(polizaRCComp);
		}
		
		return respuesta;
	}

	public Map<String, Object> calculoModificacionCuponActivoRC(final String realPath, final String cupon,
			final boolean calcularSituacionActual, final Base64Binary xml) throws Exception {
		Map<String, Object> resultado = new HashMap<String, Object>();
		ParametrosSalida respuesta = null;

		ContratacionSCCalculoModificaciones srvContratCalc = getSrvContratacionSCCalculoModificaciones(realPath);

		CalculoModificacionCuponActivoRequest request = new CalculoModificacionCuponActivoRequest();
		request.setCalcularSituacionActual(calcularSituacionActual);
		request.setCuponModificacion(cupon);
		request.setModificacionPoliza(xml);
		request.setTipoPoliza(PolizaReferenciaTipo.P);

		try {

			// LLamamos al servicio de calculo de la modificacion
			respuesta = srvContratCalc.calculoModificacionCuponActivo(request);
			resultado.put("respuesta", respuesta);

			if (respuesta != null) {
				if (respuesta.getCalculoModificacion() != null)
					resultado.put("calculoModificacion",
							getStringFromBase64Binary(respuesta.getCalculoModificacion().getValue()));
				if (respuesta.getCalculoOriginal() != null)
					resultado.put("calculoOriginal",
							getStringFromBase64Binary(respuesta.getCalculoOriginal().getValue()));
				if (respuesta.getDiferenciasCoste() != null)
					resultado.put("diferenciasCoste", getStringFromBase64Binary(respuesta.getDiferenciasCoste()));
			}

		} catch (es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrException ex) {
			logger.error(
					"SWAnexoRCHelper.calculoModificacionCuponActivoRC() error al llamar a la operacion calculoModificacionCuponActivo",
					ex);
			resultado.put("alerta", WSUtils.debugAgrException(ex));
		}catch (Exception e) {
			logger.error(
					"SWAnexoRCHelper.calculoModificacionCuponActivoRC() error al llamar a la operacion calculoModificacionCuponActivo",
					e);
			
			throw new Exception("Error al llamar al servicio web de calculo modificaciones", e);
		}

		return resultado;
	}

	/**
	 * Realiza la llamada al servicio de calculo de modificacion de un anexo de
	 * Reduccion de Capital
	 * 
	 * @param realPath
	 * @param cupon
	 * @param calcularSituacionActual
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public Map<String, Base64Binary> calculoModificacionCuponActivoRCExt(final String realPath, final String cupon,
			final boolean calcularSituacionActual, final Base64Binary xml) throws Exception {
		Map<String, Base64Binary> resultado = new HashMap<String, Base64Binary>();
		ParametrosSalida respuesta = null;

		// Crea el objeto para llamar al SW
		ContratacionSCCalculoModificaciones srvCalculoModificacion = getSrvContratacionSCCalculoModificaciones(
				realPath);

		// Crea el objeto que encapsula los parametros de entrada del servicio
		CalculoModificacionCuponActivoRequest cuponActivoRequest = new CalculoModificacionCuponActivoRequest();
		cuponActivoRequest.setCalcularSituacionActual(calcularSituacionActual);
		cuponActivoRequest.setCuponModificacion(cupon);
		cuponActivoRequest.setModificacionPoliza(xml);
		cuponActivoRequest.setTipoPoliza(PolizaReferenciaTipo.P);

		try {
			// LLamamos al servicio de calculo de la modificacion
			respuesta = srvCalculoModificacion.calculoModificacionCuponActivo(cuponActivoRequest);

			if (respuesta != null) {
				if (respuesta.getCalculoModificacion() != null)
					resultado.put("calculoModificacion", respuesta.getCalculoModificacion().getValue());
				if (respuesta.getCalculoOriginal() != null)
					resultado.put("calculoOriginal", respuesta.getCalculoOriginal().getValue());
				if (respuesta.getDiferenciasCoste() != null)
					resultado.put("diferenciasCoste", respuesta.getDiferenciasCoste());
			}
		} catch (es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.AgrException e) {
			logger.error(
					"SWAnexoRCHelper.calculoModificacionCuponActivoRCExt() error al llamar a la operacion calculoModificacionCuponActivo",
					e);
			if (e.getFaultInfo().getError() != null && !e.getFaultInfo().getError().isEmpty()) {
				for (es.agroseguro.serviciosweb.contratacionsccalculomodificaciones.Error err : e.getFaultInfo().getError()) {
					logger.error(err.getMensaje());
				}
			}
			throw new Exception(e.getFaultInfo().getError().get(0).getMensaje(), e);
		} catch (Exception e) {
			logger.error(
					"SWAnexoRCHelper.calculoModificacionCuponActivoRCExt() error al llamar a la operacion calculoModificacionCuponActivo",
					e);

			throw new Exception("Error al llamar al servicio web de calculo modificaciones", e);
		}

		return resultado;
	}

	private ContratacionSCCalculoModificaciones getSrvContratacionSCCalculoModificaciones(final String realPath) {
		if (!WSUtils.isProxyFixed()) {
			WSUtils.setProxy();
		}

		logger.debug("SWAnexoRCHelper.getSrvContratacionSCCalculoModificaciones() INIT");

		URL wsdlLocation = null;

		try {
			wsdlLocation = new URL("file:" + realPath + System.getProperty("file.separator")
					+ WSUtils.getBundleProp("anexoReduccionCapiCalcWS.wsdl"));
		} catch (MalformedURLException e1) {
			throw new SWConsultaContratacionException(
					"Imposible recuperar el WSDL de contratacion del calculo de modificaciones. Revise la Ruta: "
							+ ((wsdlLocation != null) ? wsdlLocation.toString() : ""),
					e1);
		}

		String wsLocation = WSUtils.getBundleProp("calculoAnexoModificacion.location");
		String wsPort = WSUtils.getBundleProp("calculoAnexoModificacion.port");
		String wsService = WSUtils.getBundleProp("calculoAnexoModificacion.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		ContratacionSCCalculoModificaciones_Service srv = new ContratacionSCCalculoModificaciones_Service(wsdlLocation,
				serviceName);
		ContratacionSCCalculoModificaciones srvContratCalMod = srv.getPort(portName,
				ContratacionSCCalculoModificaciones.class);

		logger.debug("ContratacionSCCalculoModificaciones <srvAnexoMod>>" + srvContratCalMod.toString());

		WSUtils.addSecurityHeader(srvContratCalMod);

		logger.debug("SWAnexoRCHelper.getSrvContratacionSCCalculoModificaciones() FIN");

		return srvContratCalMod;
	}

	public es.agroseguro.contratacion.PolizaDocument getPolizaPrincipalUnif() {
		return polizaPrincipalUnif;
	}

	public void setPolizaPrincipalUnif(final es.agroseguro.contratacion.PolizaDocument polizaPrincipal) {
		this.polizaPrincipalUnif = polizaPrincipal;
	}

	private String getStringFromBase64Binary(Base64Binary base) {

		try {
			return new String(base.getValue(), "UTF-8");
		} catch (Exception e) {
			logger.error("Error al obtener la cadena asociada a la respuesta del servicio", e);
		}

		return null;
	}
}
// P0079361