package com.rsi.agp.core.webapp.action;

import java.io.BufferedOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.ISiniestrosInformacionService;
import com.rsi.agp.core.managers.impl.SWSiniestrosInformacionHelper;
import com.rsi.agp.core.managers.impl.SiniestrosSCInformacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.VistaPlzHojaCampoActaTasacion;
import com.rsi.agp.dao.tables.siniestro.Siniestro;
import com.rsi.agp.dao.tables.siniestro.SiniestrosUtilidades;

import es.agroseguro.seguroAgrario.informacionSiniestros.InformacionSiniestro;
import es.agroseguro.seguroAgrario.informacionSiniestros.ListaSiniestrosDocument;
import es.agroseguro.serviciosweb.siniestrosscinformacion.AgrException;
import es.agroseguro.serviciosweb.siniestrosscinformacion.InfoBasicaSiniestrosResponse;

public class SiniestrosInformacionController extends BaseMultiActionController {
	
	private static final String MENSAJE_SW_IMPRESION_LLAMADA_WS_KO = "mensaje.swImpresion.llamadaWs.KO";
	private static final String ERROR_MENSAJE = "errorMensaje";
	private static final String RESULT = "result";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String CODESTADO_SINIESTRO = "codestadoSiniestro";
	private static final String FECHAENVIO_SINIESTRO = "fechaenvioSiniestro";
	private static final String FECHAOCURR_SINIESTRO = "fechaocurrSiniestro";
	private static final String RIESGO_SINIESTRO = "riesgoSiniestro";
	private static final String ID_POLIZA = "idPoliza";
	private static final String ID_POLIZA_HA = "idPoliza_ha";
	private static final String REDIRECT_SINIESTROS_HTML = "redirect:siniestros.html";
	private static final String ALERTA = "alerta";
	private static final String SINIESTROS_INFORMACION_CONTROLLER_DO_HOJAS_CAMPOS_ACTAS_TASACIONES = "######### - SiniestrosInformacionController.doHojasCamposActasTasaciones ";
	private static final String WEB_INF = "/WEB-INF/";
	private static final String USUARIO = "usuario";
	private static final String ORIGEN_LLAMADA = "origenLlamada";
	private SiniestrosSCInformacionManager siniestrosSCInformacionManager;
	private ISiniestrosInformacionService siniestrosInformacionService;
	private static final Log logger = LogFactory
			.getLog(SiniestrosInformacionController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public ModelAndView doHojasCamposActasTasaciones(
			HttpServletRequest request, HttpServletResponse response,
			Siniestro siniestro) throws Exception {
		// viene de declaracionesSiniestro.jsp
		String nifSocio = null;// 20151130 De momento no se va a trabajar con
								// este dato.
		final Map<String, Object> parametros = new HashMap<String, Object>(); // Map
																				// para
																				// guardar
																				// los
																				// parametros
																				// que
																				// se
																				// pasaran
																				// a
																				// la
																				// jsp
		logger.debug("######### - SiniestrosInformacionController.doHojasCamposActasTasaciones");
		VistaPlzHojaCampoActaTasacion vistaBean = new VistaPlzHojaCampoActaTasacion();

		ModelAndView mv = null;
		try {

			String origenLlamada = request.getParameter(ORIGEN_LLAMADA);
			parametros.put(ORIGEN_LLAMADA, Constants.ORIGEN_LLAMADA_CONSULTAR);
			String codPlan = request.getParameter("plan_ha");
			String refPoliza = request.getParameter("refpoliza_ha");

			asignaDatosPoliza(request, vistaBean);
			guardaDatosSiniestro(parametros, request, siniestro);
			if (null != codPlan && !codPlan.isEmpty() && null != refPoliza
					&& !refPoliza.isEmpty()) {
				Usuario usuario = (Usuario) request.getSession().getAttribute(
						USUARIO);
				String realPath = this.getServletContext().getRealPath(
						WEB_INF);
				Integer codPlanInt = new Integer(codPlan);
				logger.info(SINIESTROS_INFORMACION_CONTROLLER_DO_HOJAS_CAMPOS_ACTAS_TASACIONES
						+ "- Llamamos a los servicios. Plan:"
						+ codPlan
						+ " - Referencia:" + refPoliza);

				List<String> mensajes = new ArrayList<String>();

				try {
					siniestrosSCInformacionManager
							.procesoActualizacionHojasCampo(codPlanInt,
									refPoliza, nifSocio, realPath, usuario);
				} catch (AgrException e) {

					if (null != e.getFaultInfo()
							&& null != e.getFaultInfo().getError()
							&& e.getFaultInfo().getError().size() > 0) {
						mensajes.add(getErrorAgrException(e.getFaultInfo()
								.getError()) + " Hojas campo");
					}
				}

				try {
					siniestrosSCInformacionManager
							.procesoActualizacionActasTasacion(codPlanInt,
									refPoliza, nifSocio, realPath, usuario);
				} catch (AgrException e) {
					if (null != e.getFaultInfo()
							&& null != e.getFaultInfo().getError()
							&& e.getFaultInfo().getError().size() > 0) {
						mensajes.add(getErrorAgrException(e.getFaultInfo()
								.getError()) + " Actas tasacion");
					}
				}

				if (!mensajes.isEmpty()) {
					parametros.put("siniestrosInfoAlertas", mensajes);
				}

				mv = getTabla(request, response, vistaBean, origenLlamada,
						parametros);

			} else {
				logger.debug("######### - SiniestrosInformacionController.doHojasCamposActasTasaciones Sin datos.");
				// OJO ASIGNAR mv
			}
		} catch (SOAPFaultException e) {
			logger.error(
					SINIESTROS_INFORMACION_CONTROLLER_DO_HOJAS_CAMPOS_ACTAS_TASACIONES,
					e);
			parametros.put(ALERTA, e.getMessage());
			mv = new ModelAndView(REDIRECT_SINIESTROS_HTML);
			mv.addAllObjects(parametros);
		} catch (Exception e) {
			logger.error(
					SINIESTROS_INFORMACION_CONTROLLER_DO_HOJAS_CAMPOS_ACTAS_TASACIONES,
					e);
			mv = new ModelAndView(REDIRECT_SINIESTROS_HTML);
			mv.addAllObjects(parametros);
		}

		return mv;

	}

	private void asignaDatosPoliza(HttpServletRequest request,
			VistaPlzHojaCampoActaTasacion vistaBean) throws Exception {
		logger.debug("######### - SiniestrosInformacionController.asignaDatosPoliza");
		Long idPoliza = null;
		Poliza poliza = null;
		if (null != request.getParameter(ID_POLIZA_HA)) {
			// Para los datos de la poliza
			idPoliza = new Long(request.getParameter(ID_POLIZA_HA));
			poliza = siniestrosInformacionService.getPoliza(idPoliza);
			vistaBean.setPoliza(poliza);
			vistaBean.setIdpoliza(idPoliza);
		}
	}

	/**
	 * Para guardar las variables necesarias para volver a la pagina de
	 * declaracion de siniestros
	 */
	private void guardaDatosSiniestro(Map<String, Object> parametros,
			HttpServletRequest request, Siniestro siniestro) {
		logger.debug("######### - SiniestrosInformacionController.guardaDatosSiniestro");
		if (null != request.getParameter(ID_POLIZA_HA))
			parametros.put(ID_POLIZA_HA, request.getParameter(ID_POLIZA_HA));
		parametros.put(ID_POLIZA, request.getParameter(ID_POLIZA_HA));
		if (null != request.getParameter(RIESGO_SINIESTRO))
			parametros.put(RIESGO_SINIESTRO,
					request.getParameter(RIESGO_SINIESTRO));
		if (null != request.getParameter(FECHAOCURR_SINIESTRO))
			parametros.put(FECHAOCURR_SINIESTRO,
					request.getParameter(FECHAOCURR_SINIESTRO));
		if (null != request.getParameter(FECHAENVIO_SINIESTRO))
			parametros.put(FECHAENVIO_SINIESTRO,
					request.getParameter(FECHAENVIO_SINIESTRO));
		if (null != request.getParameter(CODESTADO_SINIESTRO))
			parametros.put(CODESTADO_SINIESTRO,
					request.getParameter(CODESTADO_SINIESTRO));

	}

	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response,
			VistaPlzHojaCampoActaTasacion vistaBean) throws Exception {
		ModelAndView mv = null;
		final Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			String origenLlamada = request.getParameter(ORIGEN_LLAMADA);
			parametros.put(ORIGEN_LLAMADA, Constants.ORIGEN_LLAMADA_CONSULTAR);
			asignaDatosPoliza(request, vistaBean);
			guardaDatosSiniestro(parametros, request, null);
			mv = getTabla(request, response, vistaBean, origenLlamada,
					parametros);

		} catch (Exception e) {
			logger.error(
					"######### - SiniestrosInformacionController.doConsulta ",
					e);
			// OJO ASIGNAR mv
		}
		return mv;
	}

	public ModelAndView doPdfActaTasacion(HttpServletRequest request,
			HttpServletResponse response,
			VistaPlzHojaCampoActaTasacion vistaBean) throws Exception {
		ModelAndView mv = null;
		final Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			Long numero = new Long(request.getParameter("numActa_AT"));
			Long serie = new Long(request.getParameter("serie_AT"));

			String realPath = this.getServletContext().getRealPath(WEB_INF);

			byte[] pdf = (byte[]) siniestrosSCInformacionManager
					.getPdfActaTasacion(numero, serie, realPath);
			if (pdf != null) {
				response.setContentType(APPLICATION_PDF);
				response.setHeader(CONTENT_DISPOSITION, "filename=ActaTasa" + numero + "_" + serie + ".pdf");
				try (BufferedOutputStream fos1 = new BufferedOutputStream(response.getOutputStream())) {
					fos1.write(pdf);
					fos1.flush();
				}
				return null;
			}

		} catch (SOAPFaultException e) {
			parametros.put(ALERTA, e.getMessage());
			return new ModelAndView(ERROR_MENSAJE, RESULT, parametros);
		} catch (AgrException e) {
			String mensaje = null;
			if (null != e.getFaultInfo() && null != e.getFaultInfo().getError()
					&& e.getFaultInfo().getError().size() > 0) {
				mensaje = getErrorAgrException(e.getFaultInfo().getError());
				parametros.put(ALERTA, mensaje);
			}
			return new ModelAndView(ERROR_MENSAJE, RESULT, parametros);
		} catch (Exception e) {
			parametros.put(ALERTA,
					bundle.getString(MENSAJE_SW_IMPRESION_LLAMADA_WS_KO));

		}

		mv = this.doConsulta(request, response, vistaBean);
		return mv.addAllObjects(parametros);

	}

	private String getErrorAgrException(
			List<es.agroseguro.serviciosweb.siniestrosscinformacion.Error> errores) {
		StringBuffer mensaje = new StringBuffer();
		for (es.agroseguro.serviciosweb.siniestrosscinformacion.Error error : errores) {
			mensaje.append("Error: " + error.getCodigo());
			mensaje.append(" - " + error.getMensaje());
			mensaje.append("\t- ");
			mensaje.append(System.getProperty("line.separator"));

		}
		return mensaje.toString();

	}

	public ModelAndView doPdfHojaCampo(HttpServletRequest request,
			HttpServletResponse response,
			VistaPlzHojaCampoActaTasacion vistaBean) throws Exception,
			SOAPFaultException {

		final Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		try {
			Integer codPlan = new Integer(request.getParameter("codPlan_hc"));
			String refPoliza = request.getParameter("refPoliza_hc");
			Long numeroHojaCampo = new Long(
					request.getParameter("numHojaCampo_hc"));
			Long tipoHoja = new Long(request.getParameter("tipoHoja_hc"));
			String realPath = this.getServletContext().getRealPath(WEB_INF);

			byte[] pdf = (byte[]) siniestrosSCInformacionManager
					.getPdfHojaCampo(codPlan, refPoliza, numeroHojaCampo,
							tipoHoja, realPath);
			if (pdf != null) {
				response.setContentType(APPLICATION_PDF);
				response.setHeader(CONTENT_DISPOSITION, "filename=HojaCampo" + numeroHojaCampo + ".pdf");
				try (BufferedOutputStream fos1 = new BufferedOutputStream(response.getOutputStream())) {
					fos1.write(pdf);
					fos1.flush();
				}
				return null;
			}
		} catch (SOAPFaultException e) {
			parametros.put(ALERTA, e.getMessage());
			return new ModelAndView(ERROR_MENSAJE, RESULT, parametros);
		} catch (AgrException e) {
			String mensaje = null;
			if (null != e.getFaultInfo() && null != e.getFaultInfo().getError()
					&& e.getFaultInfo().getError().size() > 0) {
				mensaje = getErrorAgrException(e.getFaultInfo().getError());
				parametros.put(ALERTA, mensaje);
			}
			return new ModelAndView(ERROR_MENSAJE, RESULT, parametros);
		} catch (Exception e) {
			parametros.put(ALERTA,
					bundle.getString(MENSAJE_SW_IMPRESION_LLAMADA_WS_KO));
		}

		mv = this.doConsulta(request, response, vistaBean);
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doPdfParte(HttpServletRequest request,
			HttpServletResponse response,
			VistaPlzHojaCampoActaTasacion vistaBean) throws Exception {
		ModelAndView mv = null;
		final Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute(
					USUARIO);
			Integer serie = new Integer(request.getParameter("serieSiniestro"));
			Integer numSiniestro = new Integer(
					request.getParameter("numSiniestro"));
			Long idSiniestro = new Long(request.getParameter("idSiniestro"));
			String realPath = this.getServletContext().getRealPath(WEB_INF);

			/*
			 * DNF 28/9/2018 Esta condicion es porque necesito saber si se viene
			 * de declaracionSiniestro.jsp o listadoSiniestro.jsp Si viene de
			 * declaracionSiniestro.jsp el campo numeroSiniestro vendra con un
			 * valor, si por el contrario viene de listadoSiniestro.jsp el campo
			 * vendra null
			 */
			Integer numeroSiniestro;
			if (request.getParameter("numeroSiniestro") == null) {
				numeroSiniestro = 0;
			} else {
				numeroSiniestro = new Integer(
						request.getParameter("numeroSiniestro"));
			}

			byte[] pdf = (byte[]) siniestrosSCInformacionManager.getPdfParte(
					serie, numSiniestro, realPath, usuario, idSiniestro,
					numeroSiniestro);

			if (pdf != null) {
				response.setContentType(APPLICATION_PDF);
				response.setHeader(CONTENT_DISPOSITION, "filename=Parte_" + numSiniestro + "_" + serie + ".pdf");
				response.setHeader("Cache-Control", "cache, must-revalidate");
				response.setHeader("Pragma", "public");
				try (BufferedOutputStream fos1 = new BufferedOutputStream(response.getOutputStream())) {
					fos1.write(pdf);
					fos1.flush();
				}
				return null;
			}

		} catch (SOAPFaultException e) {
			parametros.put(ALERTA, e.getMessage());
			return new ModelAndView(ERROR_MENSAJE, RESULT, parametros);
		} catch (AgrException e) {
			String mensaje = null;
			if (null != e.getFaultInfo() && null != e.getFaultInfo().getError()
					&& e.getFaultInfo().getError().size() > 0) {
				mensaje = getErrorAgrException(e.getFaultInfo().getError());
				parametros.put(ALERTA, mensaje);
			}
			return new ModelAndView(ERROR_MENSAJE, RESULT, parametros);
		} catch (Exception e) {
			parametros.put(ALERTA,
					bundle.getString(MENSAJE_SW_IMPRESION_LLAMADA_WS_KO));

		}
		parametros.put(ID_POLIZA, request.getParameter(ID_POLIZA));
		mv = new ModelAndView(REDIRECT_SINIESTROS_HTML);
		mv.addAllObjects(parametros);

		return mv;

	}

	private ModelAndView getTabla(HttpServletRequest request,
			HttpServletResponse response,
			VistaPlzHojaCampoActaTasacion vistaBean, String origenLlamada,
			Map<String, Object> parametros) throws Exception {

		ModelAndView mv = null;

		String tablaHTML = siniestrosInformacionService.getTabla(request,
				response, vistaBean, origenLlamada, null, null);

		if (tablaHTML == null) {
			return null;
		} else {
			String ajax = request.getParameter("ajax");
			if (ajax != null && ajax.equals("true")) {
				// byte[] contents = tablaHTML.getBytes("ISO-8859-1"); //con
				// este salen mal las tildes
				byte[] contents = tablaHTML.getBytes("UTF-8");
				// String x= new String(contents, "UTF-8");
				response.getOutputStream().write(contents);
				return null;
			} else
				// Pasa a la jsp el codigo de la tabla a traves de este
				// atributo
				request.setAttribute("consultaSiniestrosInformacion", tablaHTML);
		}

		mv = new ModelAndView(
				"/moduloUtilidades/siniestros/siniestrosInformacion",
				"vistaBean", vistaBean).addAllObjects(parametros);
		return mv;
	}

	public ModelAndView doVerDetalleSiniestro(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		logger.debug("init - doVerDetalleSiniestro");

		Usuario usuario = (Usuario) request.getSession()
				.getAttribute(USUARIO);
		Integer serie = new Integer(request.getParameter("serieSiniestro"));
		Integer numSiniestro = new Integer(request.getParameter("numSiniestro"));
		Long idSiniestro = new Long(request.getParameter("idSiniestro"));
		String realPath = this.getServletContext().getRealPath(WEB_INF);

		JSONObject resultado = new JSONObject();
		
		try {

			SWSiniestrosInformacionHelper servicio = new SWSiniestrosInformacionHelper();
			
			// llamamos al servicio infoBasicaSiniestros
			logger.info("Usuario " + usuario.getCodusuario() + " llamando al SW InfoBasicaSiniestros");
			InfoBasicaSiniestrosResponse response1 = servicio
					.getInfoBasicaSiniestros(serie, numSiniestro, realPath);

			Siniestro siniestro = new Siniestro();
			siniestro.setId(idSiniestro);
			byte[] byteArraySiniestro = response1.getInformacionSiniestros()
					.getValue();
			String xmlDataSiniestro = new String(byteArraySiniestro,
					Constants.DEFAULT_ENCODING);
			logger.debug("xml de respuesta getInfoBasicaSiniestros: "
					+ xmlDataSiniestro);
			logger.debug("Guardamos la comunicacion con el servicio InformacionSiniestros");
			siniestrosSCInformacionManager.guardarSiniestro(siniestro, usuario, xmlDataSiniestro);

			ListaSiniestrosDocument listSiniestros = ListaSiniestrosDocument.Factory
					.parse(new StringReader(xmlDataSiniestro));
			InformacionSiniestro[] infoSini = listSiniestros
					.getListaSiniestros().getSiniestroArray();
			logger.debug("para ver el contenido de resultado");

			List<SiniestrosUtilidades> listaDefinitiva = new ArrayList<SiniestrosUtilidades>();

			for (int x = 0; x < infoSini.length; x++) {

				SiniestrosUtilidades sinisInfo = new SiniestrosUtilidades();

				sinisInfo.setFocurr(infoSini[x].getFechaOcurrencia().getTime());
				sinisInfo.setDesriesgo(infoSini[x]
						.getRiesgoDeclaradoDescriptivo());
				sinisInfo.setDescestado(infoSini[x].getSituacionDescriptivo());
				sinisInfo.setSerie(infoSini[x].getSerie());
				sinisInfo.setNumerosiniestro(new BigDecimal(infoSini[x]
						.getNumeroSiniestro()));

				sinisInfo.setId(new BigDecimal(idSiniestro));
				sinisInfo.setIdpoliza(new BigDecimal(request
						.getParameter(ID_POLIZA)));
				sinisInfo.setNumsiniestro(numSiniestro.toString());
				
				sinisInfo.setCodriesgo(infoSini[x].getRiesgoDeclarado());
				sinisInfo.setIdestado(new BigDecimal(infoSini[x].getSituacion()));

				listaDefinitiva.add(sinisInfo);
			}
			resultado.put("listaS", listaDefinitiva);

		} catch (AgrException e) {
			String mensaje = null;
			if (null != e.getFaultInfo() && null != e.getFaultInfo().getError()
					&& e.getFaultInfo().getError().size() > 0) {
				mensaje = getErrorAgrException(e.getFaultInfo().getError());
				resultado.put(ALERTA, mensaje);
			}
			getWriterJSON(response,resultado);
			logger.error("Error en la llamada al SW InfoBasicaSiniestros: " + mensaje);
			logger.error("[ESC-25843] Usuario llamante: " + usuario.getCodusuario());
			logger.error("[ESC-25843] Datos de entrada: serie --> " + serie + ", numSiniestro --> " + numSiniestro);
		} catch (Exception exception) {
			logger.error("Se ha producido un error al doVerDetalleSiniestro: ",
					exception);
		}
		getWriterJSON(response, resultado);

		return null;
	}

	public void setSiniestrosSCInformacionManager(
			SiniestrosSCInformacionManager siniestrosSCInformacionManager) {
		this.siniestrosSCInformacionManager = siniestrosSCInformacionManager;
	}

	public void setSiniestrosInformacionService(
			ISiniestrosInformacionService siniestrosInformacionService) {
		this.siniestrosInformacionService = siniestrosInformacionService;
	}
	
}
