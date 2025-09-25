
package com.rsi.agp.core.webapp.action.rc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.manager.impl.anexoRC.confirmacion.IConfirmacionRCManager;
import com.rsi.agp.core.managers.impl.DeclaracionesReduccionCapitalManager;
import com.rsi.agp.core.managers.impl.ParcelasReduccionCapitalManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.managers.impl.anexoMod.confirmacion.IConfirmacionModificacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.action.DeclaracionesModificacionPolizaComplementariaController;
import com.rsi.agp.core.webapp.action.DeclaracionesModificacionPolizaController;
import com.rsi.agp.core.webapp.action.DeclaracionesReduccionCapitalController;
import com.rsi.agp.core.webapp.action.ParcelasModificacionPolizaController;
import com.rsi.agp.core.webapp.action.ParcelasReduccionCapitalController;
import com.rsi.agp.core.webapp.action.ganado.ListadoExplotacionesAnexoController;
import com.rsi.agp.core.webapp.action.utilidades.AnexoModificacionUtilidadesController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;

import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.serviciosweb.contratacionscmodificacion.Error;

public class ConfirmacionRCController extends BaseMultiActionController {

	private PolizaManager polizaManager;
	private WebServicesManager webServicesManager;
	// P0079361
	private IConfirmacionRCManager confirmacionRCManager;
	private ParcelasReduccionCapitalManager parcelasReduccionCapitalManager;
	private ParcelasReduccionCapitalController parcelasReduccionCapitalController;
	private DeclaracionesReduccionCapitalManager declaracionesReduccionCapitalManager;
	private DeclaracionesReduccionCapitalController declaracionesReduccionCapitalController;
	private String successView;

	// P0079361

	// Para la redireccion de pantallas
	private static final String REDIRECT_DECLARACIONES_RC = "declaracionesRC";
	private static final String REDIRECT_LISTADO_ANEXOS = "listadoAnexos";
	private static final String REDIRECT_PARCELAS = "parcelas";
	private static final String REDIRECT_PARCELAS_CPL = "pParcelasCpl";
	private static final String REDIRECT_PARCELAS_CPL_INFO = "pParcelasCplInfo";
	private static final String REDIRECT_EXPLOTACIONES = "explotaciones";

	// P0079361
	private static final String VIENE_LISTA_RC = "vieneDeListadoRC";
	// P0079361

	private static final String VIENE_LISTANEXO = "vieneDeListadoAnexosMod";
	private static final String REDIREC = "redireccion";
	private static final String ERRORES = "errores";
	private static final String TIPO_LIN = "tipoLinea";
	private static final String MSJ = "mensaje";
	private static final String ID_ANX = "idReduccionCapital";
	private static final String ALERT = "alerta";
	private static final String ID_POLIZA = "idPoliza";

	private static final String CAMBIOS_DATOS_ASEGURADO = "hayCambiosDatosAsegurado";

	// P0079361
	public ModelAndView doValidarRC(HttpServletRequest request, HttpServletResponse response)
			throws DAOException, BusinessException {

		String vieneDeListadoRC = request.getParameter(VIENE_LISTA_RC);
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		String idRedCapStr = request.getParameter("parcela.reduccionCapital.id");
		Long idRedCap = new Long(idRedCapStr);

		logger.debug("idReduccionCapital :" + idRedCapStr);

		// String redireccion = request.getParameter(REDIREC);
		String redireccion = "parcelas";
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		AcuseRecibo acuseRecibo = null;
		ReduccionCapital reduccionCapital = null;

		if (idRedCap != null) {
			// reduccionCapital = confirmacionRCManager.getRcByIdCupon(idCuponRC);
			reduccionCapital = parcelasReduccionCapitalManager.getReduccionCapitalById(idRedCap);
			if (reduccionCapital.getCupon().getId() != null) {
				Poliza poliza = reduccionCapital.getPoliza();
				declaracionesReduccionCapitalManager.actualizaXmlRC(reduccionCapital);
				declaracionesReduccionCapitalManager.guardarReduccionCapital(reduccionCapital,
						reduccionCapital.getEstado(), usuario);
				// FALTAR CONFIRMAR
				// caractExpl =
				// confirmacionModificacionManager.calcularCaractExplotacionAnx(anexo,
				// realPath,usuario.getCodusuario(), poliza, isGanado);

				// if (caractExpl != null) {
				// anexo.setCodCaractExplotacion(caractExpl);
				// }

				String hayCambiosDatosAsegurado = "";
				// request.getParameter(CAMBIOS_DATOS_ASEGURADO);

				// if (hayCambiosDatosAsegurado==null) {
				// hayCambiosDatosAsegurado="";
				// }

				// acuseRecibo =
				// confirmacionRCManager.generarPolizaActualizada(reduccionCapital,
				// realPath,usuario, cambiosDatosAsegurados);
				acuseRecibo = confirmacionRCManager.generarPolizaActualizada(reduccionCapital, realPath,
						usuario.getCodusuario(), false);
				// FALTAR CONFIRMAR

			} else {
				logger.debug("Id Cupon es null");
			}
		}

		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put("hayCambiosDatosAsegurado", request.getParameter(CAMBIOS_DATOS_ASEGURADO));

		@SuppressWarnings("rawtypes")
		List listaErrores = new ArrayList();

		if (acuseRecibo != null) {
			if (acuseRecibo.getDocumentoArray() != null && acuseRecibo.getDocumentoArray().length > 0) {
				Poliza poliza = reduccionCapital.getPoliza();
				BigDecimal codPlan = poliza.getLinea().getCodplan();
				BigDecimal codLinea = poliza.getLinea().getCodlinea();
				BigDecimal codEntidad = poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad();
				// Req10
				// acuseRecibo =
				// confirmacionModificacionManager.limpiaErroresWsAnexo(acuseRecibo,
				// Constants.WS_VALIDACION_AM, codPlan, codLinea, codEntidad);

				listaErrores = Arrays.asList(acuseRecibo.getDocumentoArray(0).getErrorArray());

				String xpath = "/PolizaReduccionCapital/ObjetosAsegurados/Parcela[hoja=1 and numero=2]/CapitalesAsegurados/CapitalAsegurado[tipo=0]";
				String num = xpath.substring(xpath.indexOf("numero=") + 7, xpath.indexOf("]/"));
				logger.debug("********AQUI ****** " + num); //ELIMINAR
				if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) {

					parametros.put("puedeCalcular", Boolean.TRUE);
				} else {
					BigDecimal tipoUsuario = usuario.getTipousuario();
					tipoUsuario = usuario.getExterno().equals(Constants.USUARIO_EXTERNO)
							? tipoUsuario.add(Constants.NUMERO_DIEZ)
							: tipoUsuario;
					List<ErrorWsAccion> errorWsAccionList = this.webServicesManager.getErroresWsAccion(codPlan,
							codLinea, codEntidad, tipoUsuario, Constants.REDUCCION_CAPITAL);
					parametros.put("puedeCalcular", WSUtils.comprobarErroresPorPerfil(acuseRecibo, errorWsAccionList));
				}
			}
		} else {

			parametros.put("errorEnValidacion", true);
		}
		logger.debug("ACUSE DE RECIBO **************************** "
				+ ((acuseRecibo != null) ? acuseRecibo.toString() : ""));

		mostrarMensajeConfirmacion(usuario, reduccionCapital, parametros);

		request.setAttribute(ERRORES, listaErrores);
		request.setAttribute("errLength", listaErrores.size());
		request.setAttribute(REDIREC, redireccion);
		request.setAttribute(TIPO_LIN, Constants.TipoLinea.AGR);
		// return null;

		/*
		 * REQUISITO 10
		 */
		ModelAndView mv = new ModelAndView("moduloUtilidades/reduccionCapital/erroresValidacionRC", "reduccionCapital",
				reduccionCapital);
		parametros.put(VIENE_LISTA_RC, vieneDeListadoRC);

		parametros.put("mostrarCalcular", reduccionCapital.getPoliza().isPlanMayorIgual2015());

		mv.addAllObjects(parametros);

		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		return mv;

	}

	private void mostrarMensajeConfirmacion(Usuario usuario, ReduccionCapital rc, Map<String, Object> parametros) {
		boolean errorTramite = Constants.AM_CUPON_ESTADO_ERROR_TRAMITE.equals(rc.getCupon().getEstadoCupon().getId());
		// boolean perfil34 =
		// this.confirmacionModificacionManager.checkPerfil34(usuario.getPerfil(),
		// usuario.getSubentidadMediadora().getForzarRevisionAM());

		parametros.put("errorTramite", errorTramite);
		// parametros.put("perfil34", perfil34);
		parametros.put("perfil34", false);
	}

	// P0079361

	public ModelAndView doConfirmarRC(HttpServletRequest request, HttpServletResponse response, ReduccionCapital rc) {

		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String indRevAdm = StringUtils.nullToString(request.getParameter("indRevAdm"));
		String redireccion = StringUtils.nullToString(request.getParameter(REDIREC));

		Map<String, String> mensajes = new HashMap<String, String>();

		// falta probar y depurar
		if (rc.getCupon() != null && rc.getCupon().getId() != null) {

			CuponRC cuponRC = confirmacionRCManager.getCuponRCByIdCupon(rc.getCupon().getId());

			if (cuponRC != null && cuponRC.getEstadoCupon() != null && cuponRC.getEstadoCupon().getId() != null) {

				rc.setCupon(cuponRC);

				ReduccionCapital redCap = confirmacionRCManager.getRCByIdRC(rc.getId());

				if (redCap != null && redCap.getXml() != null) {
					rc.setXml(redCap.getXml());
				}

				if (Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.compareTo(cuponRC.getEstadoCupon().getId()) != 0
						&& Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO
								.compareTo(cuponRC.getEstadoCupon().getId()) != 0) {
					mensajes = confirmacionRCManager.confirmarReduccionCapital(rc, "S".equals(indRevAdm),
							usuario.getCodusuario(), realPath);
					redireccion = REDIRECT_DECLARACIONES_RC;
				} else {
					mensajes.put(Constants.KEY_ALERTA,
							"No se ha podido realizar la operacion al estar el cupon en estado "
									+ cuponRC.getEstadoCupon().getEstado());
					redireccion = REDIRECT_DECLARACIONES_RC;
				}
			} else {
				mensajes.put(Constants.KEY_ALERTA, "Error al obtener el estado del cupon");
				redireccion = REDIRECT_DECLARACIONES_RC;
			}
		}

		return redireccion(request, response, rc, redireccion, mensajes);
		// falta probar y depurar
	}

	private ModelAndView redireccion(HttpServletRequest request, HttpServletResponse response, ReduccionCapital rc,
			String redireccion, Map<String, String> mensajes) {

		Map<String, Object> parametros = new HashMap<String, Object>();

		if (mensajes != null) {
			String mensaje = mensajes.get(MSJ);
			String alerta = mensajes.get(ALERT);

			if (alerta != null) {
				request.setAttribute(ALERT, alerta);
				parametros.put(ALERT, alerta);
			}

			if (mensaje != null) {
				request.setAttribute(MSJ, mensaje);
				parametros.put(MSJ, mensaje);
			}
		}
		logger.debug("Valor redireccion tras envio a AGRO de RC: " + redireccion);
		/*
		 * if(REDIRECT_DECLARACIONES_ANEXOS.equals(redireccion)) {
		 * 
		 * return declaracionesModificacionPolizaController.doConsulta(request,
		 * response, rc); } else if(REDIRECT_LISTADO_ANEXOS.equals(redireccion)) {
		 * parametros.put("volver", true);
		 * 
		 * ModelAndView mv = anexoModificacionUtilidadesController.doConsulta(request,
		 * response, am); mv.addAllObjects(parametros);
		 * 
		 * return mv; } else
		 */
		if (REDIRECT_PARCELAS.equals(redireccion)) {
			request.setAttribute(ID_ANX, rc.getId());

			if (rc.getCupon() != null) {
				request.setAttribute("idCupon", rc.getCupon().getId());
			}

			try {
				return parcelasReduccionCapitalController.doConsulta(request, response,
						new com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado());
			} catch (Exception ex) {
				logger.error("Error: " + ex);
				return null;
			}
		} else if (REDIRECT_DECLARACIONES_RC.equals(redireccion)) {
			request.setAttribute(ID_POLIZA, rc.getPoliza().getIdpoliza());

			try {
				return declaracionesReduccionCapitalController.doConsulta(request, response, rc);
			} catch (Exception ex) {
				logger.error("Error: " + ex);
				return null;
			}
		}

		return null;
	}

	/**
	 * Método para para la accion 'Volver' desde la pantalla de resultado de
	 * validacion de AM por SW
	 * 
	 * @param request
	 * @param response
	 * @param am
	 * @return
	 */
	public ModelAndView doVolver(HttpServletRequest request, HttpServletResponse response, ReduccionCapital rc) {

		// Recoje la cadena que indica a qué pantalla hay que redirigir
		String redireccion = StringUtils.nullToString(request.getParameter(REDIREC));

		// Devuelve el ModelAndView con la redireccion
		return redireccion(request, response, rc, redireccion, null);
	}

	// /**
	// * Carga el último acuse de recibo de confirmacion del anexo indicado y
	// redirige a la pantalla para visualizarlo
	// * @param request
	// * @param response
	// * @return
	// */
	public ModelAndView doVerAcuseConfirmacion(HttpServletRequest request, HttpServletResponse response) {

		BigDecimal idAnexo = NumberUtils.formatToNumber(request.getParameter("idAnexoAcuse"));
		BigDecimal idPoliza = NumberUtils.formatToNumber(request.getParameter("idPolizaAcuse"));
		BigDecimal idCupon = NumberUtils.formatToNumber(request.getParameter("idCuponAcuse"));
		String redireccion = StringUtils.nullToString(request.getParameter(REDIREC));

		Map<String, Object> parametros = new HashMap<String, Object>();
		Map<String, Object> respuesta = new HashMap<String, Object>();

		if (idAnexo != null) {
			respuesta = confirmacionRCManager.getAcuseConfirmacion(idAnexo.longValue());
		}

		if (respuesta.isEmpty()) {
			parametros.put(MSJ, "No se ha podido recuperar el acuse de recibo del anexo");
		} else {
			if (respuesta.containsKey(MSJ)) {
				parametros.put(MSJ, respuesta.get(MSJ));
			} else if (respuesta.containsKey(ERRORES)) {
				@SuppressWarnings("unchecked")
				List<Error> listaErrores = (List<Error>) respuesta.get(ERRORES);
				request.setAttribute(ERRORES, listaErrores);
				request.setAttribute("errLength", listaErrores.size());
			}
		}

		parametros.put(ID_ANX, idAnexo);
		parametros.put("idPoliza", idPoliza);
		parametros.put("idCupon", idCupon);
		parametros.put(REDIREC, redireccion);

		ModelAndView mv = new ModelAndView("moduloUtilidades/reduccionCapital/acuseReciboRC");
		mv.addAllObjects(parametros);

		return mv;
	}

	// /**
	// * Añade al mapa de parámetros los booleanos indicando si el cupon está en
	// estado 'Error-Trámite' y si el usuario tiene perfil 3 o 4
	// * @param usuario
	// * @param anexo
	// * @param parametros
	// */
	// private void mostrarMensajeConfirmacion(Usuario usuario, AnexoModificacion
	// anexo, Map<String, Object> parametros) {
	// // Si el cupon está en estado 'Error-Trámite'
	// boolean errorTramite =
	// Constants.AM_CUPON_ESTADO_ERROR_TRAMITE.equals(anexo.getCupon().getEstadoCupon().getId());
	// // Si el perfil del usuario es 3 o 4
	// boolean perfil34 =
	// this.confirmacionModificacionManager.checkPerfil34(usuario.getPerfil(),
	// usuario.getSubentidadMediadora().getForzarRevisionAM());
	// // Se añade al mapa de parámetros
	// parametros.put("errorTramite", errorTramite);
	// parametros.put("perfil34", perfil34);
	// }
	//
	//
	// private ModelAndView redireccion (HttpServletRequest request,
	// HttpServletResponse response, AnexoModificacion am, String redireccion,
	// Map<String, String> mensajes ) {
	//
	// Map<String, Object> parametros = new HashMap<String, Object>();
	//
	// if(mensajes!=null){
	// String mensaje = mensajes.get(MSJ);
	// String alerta = mensajes.get(ALERT);
	//
	// if(alerta != null) {
	// request.setAttribute(ALERT, alerta);
	// parametros.put(ALERT, alerta);
	// }
	//
	// if(mensaje != null) {
	// request.setAttribute(MSJ, mensaje);
	// parametros.put(MSJ, mensaje);
	// }
	// }
	// logger.debug("[ESC-28168] redireccion: " + redireccion);
	// // Redireccion a pantalla de declaraciones de modificacion
	// if(REDIRECT_DECLARACIONES_ANEXOS.equals(redireccion)) {
	//
	// return declaracionesModificacionPolizaController.doConsulta(request,
	// response, am);
	// }
	// else if(REDIRECT_LISTADO_ANEXOS.equals(redireccion)) {
	// parametros.put("volver", true);
	//
	// ModelAndView mv = anexoModificacionUtilidadesController.doConsulta(request,
	// response, am);
	// mv.addAllObjects(parametros);
	//
	// return mv;
	// }
	// else if(REDIRECT_PARCELAS.equals(redireccion)) {
	// request.setAttribute(ID_ANX, am.getId());
	//
	// if(am.getCupon()!=null){
	// request.setAttribute("idCupon", am.getCupon().getId());
	// }
	//
	// return parcelasModificacionPolizaController.doConsulta(request, response, new
	// CapitalAsegurado());
	// }
	// else if(REDIRECT_PARCELAS_CPL.equals(redireccion)) {
	// request.setAttribute(ID_ANX, am.getId());
	// return
	// declaracionesModificacionPolizaComplementariaController.doConsulta(request,
	// response, new CapitalAsegurado());
	// }
	// else if(REDIRECT_PARCELAS_CPL_INFO.equals(redireccion)) {
	// request.setAttribute(ID_ANX, am.getId());
	// return
	// declaracionesModificacionPolizaComplementariaController.doVisualiza(request,
	// response, new CapitalAsegurado());
	// }
	// else if(REDIRECT_EXPLOTACIONES.equals(redireccion)) {
	// String vieneDeListadoAnexosMod = request.getParameter(VIENE_LISTANEXO);
	// parametros.put(VIENE_LISTANEXO, vieneDeListadoAnexosMod);
	// parametros.put("anexoModificacionId", am.getId());
	// parametros.put("origenLlamada",
	// ListadoExplotacionesAnexoController.ORIGEN_PANTALLA_VALIDACION_ANEXO);
	// parametros.put("hayCambiosDatosAsegurado",
	// request.getParameter("hayCambiosDatosAsegurado"));
	// return new
	// ModelAndView("redirect:/listadoExplotacionesAnexo.html").addAllObjects(parametros);
	// }
	//
	// return null;
	// }
	//
	// /** SONAR Q ** MODIF TAM (16.12.2021) ** Inicio **/
	// private AcuseRecibo obtenerAr(AnexoModificacion anexo, String realPath,
	// String usuario, boolean cambiosDatosAsegurados) {
	//
	// AcuseRecibo ar = null;
	//
	// if(anexo.getPoliza().getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
	// ar = confirmacionModificacionManager.generarPolizaActualizada(anexo,
	// realPath,usuario, cambiosDatosAsegurados);
	// }else{
	// ar = confirmacionModificacionManager.generarPolizaActualizadaCpl(anexo,
	// realPath,usuario, cambiosDatosAsegurados);
	// }
	// return ar;
	// }
	//
	// private HttpServletRequest informarRequest(AnexoModificacion anexo,
	// HttpServletRequest request) {
	// if(anexo.getPoliza().getLinea().isLineaGanado()){
	// request.setAttribute(TIPO_LIN, Constants.TipoLinea.GAN);
	// }else{
	// request.setAttribute(TIPO_LIN, Constants.TipoLinea.AGR);
	// }
	// return request;
	// }
	//
	// private BigDecimal obtenerIdCupon(AnexoModificacion am, BigDecimal idCupon) {
	// if(StringUtils.nullToString(idCupon).equals("")){
	// idCupon = new BigDecimal(am.getCupon().getId());
	// }
	// return idCupon;
	// }

	/** SONAR Q ** MODIF TAM (16.12.2021) ** Fin **/

	// SETTER PARA SPRING

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setParcelasReduccionCapitalManager(ParcelasReduccionCapitalManager parcelasReduccionCapitalManager) {
		this.parcelasReduccionCapitalManager = parcelasReduccionCapitalManager;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public void setDeclaracionesReduccionCapitalManager(
			DeclaracionesReduccionCapitalManager declaracionesReduccionCapitalManager) {
		this.declaracionesReduccionCapitalManager = declaracionesReduccionCapitalManager;
	}

	// P0079361
	public IConfirmacionRCManager getConfirmacionRCManager() {
		return confirmacionRCManager;
	}

	public void setConfirmacionRCManager(IConfirmacionRCManager confirmacionRCManager) {
		this.confirmacionRCManager = confirmacionRCManager;
	}

	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public DeclaracionesReduccionCapitalController getDeclaracionesReduccionCapitalController() {
		return declaracionesReduccionCapitalController;
	}

	public void setDeclaracionesReduccionCapitalController(
			DeclaracionesReduccionCapitalController declaracionesReduccionCapitalController) {
		this.declaracionesReduccionCapitalController = declaracionesReduccionCapitalController;
	}

	public void setParcelasReduccionCapitalController(
			ParcelasReduccionCapitalController parcelasReduccionCapitalController) {
		this.parcelasReduccionCapitalController = parcelasReduccionCapitalController;
	}

	// P0079361
}