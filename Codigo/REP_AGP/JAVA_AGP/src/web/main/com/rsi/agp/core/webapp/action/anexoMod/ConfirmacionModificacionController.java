package com.rsi.agp.core.webapp.action.anexoMod;

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
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.managers.impl.anexoMod.confirmacion.IConfirmacionModificacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.action.DeclaracionesModificacionPolizaComplementariaController;
import com.rsi.agp.core.webapp.action.DeclaracionesModificacionPolizaController;
import com.rsi.agp.core.webapp.action.ParcelasModificacionPolizaController;
import com.rsi.agp.core.webapp.action.ganado.ListadoExplotacionesAnexoController;
import com.rsi.agp.core.webapp.action.utilidades.AnexoModificacionUtilidadesController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.serviciosweb.contratacionscmodificacion.Error;

public class ConfirmacionModificacionController extends	BaseMultiActionController {
	
	/*** SONAR Q ** MODIF TAM(16.12.2021) ***/
	/** - Se ha eliminado todo el codigo comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/

	private IConfirmacionModificacionManager confirmacionModificacionManager;
	private DeclaracionesModificacionPolizaController declaracionesModificacionPolizaController;
	private AnexoModificacionUtilidadesController anexoModificacionUtilidadesController;
	private ParcelasModificacionPolizaController parcelasModificacionPolizaController;
	private DeclaracionesModificacionPolizaComplementariaController declaracionesModificacionPolizaComplementariaController;
	private PolizaManager polizaManager;
	private WebServicesManager webServicesManager;
	
	//Para la redireccion de pantallas
	private static final String REDIRECT_DECLARACIONES_ANEXOS = "declaracionesAnexos";
	private static final String REDIRECT_LISTADO_ANEXOS = "listadoAnexos";
	private static final String REDIRECT_PARCELAS = "parcelas";
	private static final String REDIRECT_PARCELAS_CPL = "pParcelasCpl";
	private static final String REDIRECT_PARCELAS_CPL_INFO = "pParcelasCplInfo";
	private static final String REDIRECT_EXPLOTACIONES = "explotaciones";
	
	/** CONSTANTES SONAR Q ** MODIF TAM (16.12.2021) ** Inicio **/
	private static final String VIENE_LISTANEXO = "vieneDeListadoAnexosMod";
	private static final String REDIREC = "redireccion";
	private static final String ERRORES ="errores";
	private static final String TIPO_LIN = "tipoLinea";
	private static final String MSJ = "mensaje";
	private static final String ID_ANX = "idAnexo";
	private static final String ALERT = "alerta";
	/** CONSTANTES SONAR Q ** MODIF TAM (16.12.2021) ** Fin  **/
	
	private static final String CAMBIOS_DATOS_ASEGURADO = "hayCambiosDatosAsegurado";

	public ModelAndView doValidarAnexo (HttpServletRequest request, HttpServletResponse response, AnexoModificacion am) throws DAOException, BusinessException {
		
		String vieneDeListadoAnexosMod = request.getParameter(VIENE_LISTANEXO);
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		BigDecimal idCupon = NumberUtils.formatToNumber(request.getParameter("idCuponValidar"));
		
		/* SONAR Q INICIO */
		idCupon = obtenerIdCupon(am, idCupon);
		/* SONAR Q FIN */
		
		String redireccion = request.getParameter(REDIREC);
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		// Genera la situacion actualizada de la poliza más los cambios del anexo y la valida contra el SW de Agroseguro 
		AcuseRecibo ar = null;
		AnexoModificacion anexo = null;
		
		if(idCupon != null) {
			// Obtiene el anexo indicado por el idcupon
			anexo = confirmacionModificacionManager.getAnexoByIdCupon (idCupon.longValue());
			logger.debug("CONFIRMACION: ");
			for (Cobertura c: anexo.getCoberturas()) {
				logger.debug(c.getCodconcepto() + " - " + c.getCodvalor());
			}
			
			/* P0078691 ** MODIF TAM (14.12.2021) ** Inicio */
			/* Antes de llamar al S.Web de Validacion de anexos, se llama al S.W de características de la explotacion si corresponde */
			Poliza poliza = anexo.getPoliza();
			boolean isGanado = anexo.getPoliza().getLinea().isLineaGanado();
			BigDecimal caractExpl = null;
			
			caractExpl = confirmacionModificacionManager.calcularCaractExplotacionAnx(anexo, realPath,usuario.getCodusuario(), poliza, isGanado);
				
			if (caractExpl != null) {
				anexo.setCodCaractExplotacion(caractExpl);
			}
			/* P0078691 ** MODIF TAM (14.12.2021) ** Fin */
			
			String hayCambiosDatosAsegurado = request.getParameter(CAMBIOS_DATOS_ASEGURADO);
			
			if (hayCambiosDatosAsegurado==null) {
				hayCambiosDatosAsegurado="";
			}
			/* SONAR Q INICIO */
			ar = obtenerAr(anexo, realPath, usuario.getCodusuario(), hayCambiosDatosAsegurado.equals("true"));
			/* SONAR Q FIN */
		}
		
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		parametros.put("hayCambiosDatosAsegurado", request.getParameter(CAMBIOS_DATOS_ASEGURADO));

		
		@SuppressWarnings("rawtypes")
		List listaErrores = new ArrayList();
		
		// Si se ha recibido el acuse de recibo correctamente
		if(ar != null ) {
			if(ar.getDocumentoArray() != null && ar.getDocumentoArray().length > 0) {
				Poliza poliza = anexo.getPoliza();
				BigDecimal codPlan = poliza.getLinea().getCodplan();
				BigDecimal codLinea = poliza.getLinea().getCodlinea();
				BigDecimal codEntidad = poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad();
				// AMG 06/03/2014 Eliminamos los errores que no queremos que aparezcan en el listado
				ar = confirmacionModificacionManager.limpiaErroresWsAnexo(ar, Constants.WS_VALIDACION_AM, codPlan, codLinea, codEntidad);
		
				listaErrores = Arrays.asList(ar.getDocumentoArray(0).getErrorArray());
				
				if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) {
					
					parametros.put("puedeCalcular", Boolean.TRUE);
				} else {
					BigDecimal tipoUsuario = usuario.getTipousuario();
					tipoUsuario = usuario.getExterno().equals(Constants.USUARIO_EXTERNO) ? tipoUsuario.add(Constants.NUMERO_DIEZ) : tipoUsuario;
					List<ErrorWsAccion> errorWsAccionList = this.webServicesManager.getErroresWsAccion(codPlan, codLinea, codEntidad, tipoUsuario, Constants.ANEXO_MODIFICACION);
					parametros.put("puedeCalcular", WSUtils.comprobarErroresPorPerfil(ar, errorWsAccionList));
				}
			}
		}else {	
			// Si ha habido algún error en la validacion
			parametros.put("errorEnValidacion", true);
		}
		logger.debug("ACUSE DE RECIBO **************************** " + ((ar != null) ? ar.toString() : ""));
		
		// Comprueba si hay que mostrar el mensaje de confirmacion antes de llamar al SW del cupon		
		mostrarMensajeConfirmacion(usuario, anexo, parametros);
		
		// Envia la lista de errores y el tamaño a través de la request
		request.setAttribute(ERRORES, listaErrores);
		request.setAttribute("errLength", listaErrores.size());
		request.setAttribute(REDIREC, redireccion);
		
		/* SONAR Q INICIO */
		request = informarRequest(anexo, request);
		
		
		ModelAndView mv = new ModelAndView("moduloUtilidades/modificacionesPoliza/erroresValidacionAM", "anexoModificacion", anexo);
		parametros.put(VIENE_LISTANEXO, vieneDeListadoAnexosMod);
		
		// Se envía el parámetro que indica si el anexo se puede confirmar directamente (si el plan es < 2015 o de ganado) o si hay que calcular
		// previamente (si el plan es >= 2015)
		parametros.put("mostrarCalcular", anexo.getPoliza().isPlanMayorIgual2015());
		
		mv.addAllObjects(parametros);
		
		return mv;
	}

	
	public ModelAndView doConfirmarAnexo (HttpServletRequest request, HttpServletResponse response, AnexoModificacion am) {
		
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String indRevAdm = StringUtils.nullToString(request.getParameter("indRevAdm"));
		String redireccion = StringUtils.nullToString(request.getParameter(REDIREC));
		
		Map<String, String> mensajes = new HashMap<String, String>();
		
		if(am.getCupon() != null && am.getCupon().getId() != null) {
			Cupon cupon = confirmacionModificacionManager.obtenerCuponByIdCupon(am.getCupon().getId());
			
			if(cupon!=null && cupon.getEstadoCupon()!=null){
				//Si no está confirmado, se envía. Si no, no.
				if(Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.compareTo(cupon.getEstadoCupon().getId())!=0 && Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.compareTo(cupon.getEstadoCupon().getId())!=0){
					mensajes = confirmacionModificacionManager.confirmarAnexo(am, "S".equals(indRevAdm), usuario.getCodusuario(), realPath);
					redireccion = REDIRECT_DECLARACIONES_ANEXOS;
				}else{
					mensajes.put(Constants.KEY_ALERTA, "No se ha podido realizar la operacion al estar el cupon en estado " + cupon.getEstadoCupon().getEstado());
					redireccion = REDIRECT_EXPLOTACIONES;
				}
			}else{
				mensajes.put(Constants.KEY_ALERTA, "Error al obtener el estado del cupon");
				redireccion = REDIRECT_DECLARACIONES_ANEXOS;
			}
		}
		
		// Redireccion
		return redireccion(request, response, am, redireccion, mensajes);
	}
	
	
	/**
	 * Método para para la accion 'Volver' desde la pantalla de resultado de validacion de AM por SW
	 * @param request
	 * @param response
	 * @param am
	 * @return
	 */
	public ModelAndView doVolver (HttpServletRequest request, HttpServletResponse response, AnexoModificacion am) {
		
		// Recoje la cadena que indica a qué pantalla hay que redirigir
		String redireccion = StringUtils.nullToString(request.getParameter(REDIREC));
		
		// Devuelve el ModelAndView con la redireccion
		return redireccion(request, response, am, redireccion, null);
	}
	
	/**
	 * Carga el último acuse de recibo de confirmacion del anexo indicado y redirige a la pantalla para visualizarlo
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView doVerAcuseConfirmacion (HttpServletRequest request, HttpServletResponse response) {
		
		BigDecimal idAnexo = NumberUtils.formatToNumber(request.getParameter("idAnexoAcuse"));
		BigDecimal idPoliza = NumberUtils.formatToNumber(request.getParameter("idPolizaAcuse"));
		BigDecimal idCupon = NumberUtils.formatToNumber(request.getParameter("idCuponAcuse"));
		// Recoje la cadena que indica a qué pantalla hay que redirigir
		String redireccion = StringUtils.nullToString(request.getParameter(REDIREC));
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		if(idAnexo != null) {
			respuesta = confirmacionModificacionManager.getAcuseConfirmacion(idAnexo.longValue());
		}
		
		// Si el mapa no contiene nada, ha ocurrido algún error en el proceso
		if(respuesta.isEmpty()) {
			parametros.put(MSJ, "No se ha podido recuperar el acuse de recibo del anexo");
		}
		else {
			if(respuesta.containsKey(MSJ)) {
				parametros.put(MSJ, respuesta.get(MSJ));
			}
			else if(respuesta.containsKey(ERRORES)) {
				@SuppressWarnings("unchecked")
				List<Error> listaErrores = (List<Error>) respuesta.get(ERRORES);
				// Envia la lista de errores y el tamaño a través de la request
				request.setAttribute(ERRORES, listaErrores);
				request.setAttribute("errLength", listaErrores.size());
			}
		}
		
		// Incluye los parámetros fijos para la pantalla
		parametros.put(ID_ANX, idAnexo);
		parametros.put("idPoliza", idPoliza);
		parametros.put("idCupon", idCupon);
		parametros.put(REDIREC, redireccion);
		
		try {
			boolean esPolizaGanado = polizaManager.esPolizaGanadoByIdPoliza(idPoliza.longValue());
			
			if(esPolizaGanado){
				request.setAttribute(TIPO_LIN, Constants.TipoLinea.GAN);	
			}else{
				request.setAttribute(TIPO_LIN, Constants.TipoLinea.AGR);
			}
		} catch (DAOException e) {
			logger.error("Error al calcular si la poliza es de ganado: " + idPoliza);
		}
		
		// Redireccion a la página de visualizacion del acuse de recibo
		ModelAndView mv = new ModelAndView("moduloUtilidades/modificacionesPoliza/acuseReciboAM");
		mv.addAllObjects(parametros);
		
		return mv;
	}
	
	/**
	 * Añade al mapa de parámetros los booleanos indicando si el cupon está en estado 'Error-Trámite' y si el usuario tiene perfil 3 o 4
	 * @param usuario
	 * @param anexo
	 * @param parametros
	 */
	private void mostrarMensajeConfirmacion(Usuario usuario, AnexoModificacion anexo, Map<String, Object> parametros) {
		// Si el cupon está en estado 'Error-Trámite'
		boolean errorTramite = Constants.AM_CUPON_ESTADO_ERROR_TRAMITE.equals(anexo.getCupon().getEstadoCupon().getId());
		// Si el perfil del usuario es 3 o 4
		boolean perfil34 = this.confirmacionModificacionManager.checkPerfil34(usuario.getPerfil(), usuario.getSubentidadMediadora().getForzarRevisionAM());
		// Se añade al mapa de parámetros
		parametros.put("errorTramite", errorTramite);
		parametros.put("perfil34", perfil34);
	}
	
	
	private ModelAndView redireccion (HttpServletRequest request, HttpServletResponse response, AnexoModificacion am, String redireccion, Map<String, String> mensajes ) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		if(mensajes!=null){
			String mensaje = mensajes.get(MSJ);
			String alerta =  mensajes.get(ALERT);
			
			if(alerta != null) {
				request.setAttribute(ALERT, alerta);
				parametros.put(ALERT, alerta);
			}
			
			if(mensaje != null) {
				request.setAttribute(MSJ, mensaje);
				parametros.put(MSJ, mensaje);
			}
		}
		logger.debug("[ESC-28168] redireccion: " + redireccion);
		// Redireccion a pantalla de declaraciones de modificacion
		if(REDIRECT_DECLARACIONES_ANEXOS.equals(redireccion)) {
			
			return declaracionesModificacionPolizaController.doConsulta(request, response, am);
		}
		else if(REDIRECT_LISTADO_ANEXOS.equals(redireccion)) {
			parametros.put("volver", true);		
			ModelAndView mv = anexoModificacionUtilidadesController.doConsulta(request, response, am);
			mv.addAllObjects(parametros);
			
			return mv;
		}
		else if(REDIRECT_PARCELAS.equals(redireccion)) {			
			request.setAttribute(ID_ANX, am.getId());
			
			if(am.getCupon()!=null){
				request.setAttribute("idCupon", am.getCupon().getId());
			}
			
			return parcelasModificacionPolizaController.doConsulta(request, response, new CapitalAsegurado());
		}
		else if(REDIRECT_PARCELAS_CPL.equals(redireccion)) {	
			request.setAttribute(ID_ANX, am.getId());
			return declaracionesModificacionPolizaComplementariaController.doConsulta(request, response, new CapitalAsegurado());
		}
		else if(REDIRECT_PARCELAS_CPL_INFO.equals(redireccion)) {	
			request.setAttribute(ID_ANX, am.getId());
			return declaracionesModificacionPolizaComplementariaController.doVisualiza(request, response, new CapitalAsegurado());
		}
		else if(REDIRECT_EXPLOTACIONES.equals(redireccion)) {
			String vieneDeListadoAnexosMod = request.getParameter(VIENE_LISTANEXO);
			parametros.put(VIENE_LISTANEXO, vieneDeListadoAnexosMod);
			parametros.put("anexoModificacionId", am.getId());
			parametros.put("origenLlamada", ListadoExplotacionesAnexoController.ORIGEN_PANTALLA_VALIDACION_ANEXO);
			parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
			return new ModelAndView("redirect:/listadoExplotacionesAnexo.html").addAllObjects(parametros);
		}
		
		return null;
	}
	
	/** SONAR Q ** MODIF TAM (16.12.2021) ** Inicio **/
	private AcuseRecibo  obtenerAr(AnexoModificacion anexo, String realPath, String usuario, boolean cambiosDatosAsegurados) {
		
		AcuseRecibo ar = null;
		
		if(anexo.getPoliza().getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
			ar = confirmacionModificacionManager.generarPolizaActualizada(anexo, realPath,usuario, cambiosDatosAsegurados);
		}else{
			ar = confirmacionModificacionManager.generarPolizaActualizadaCpl(anexo, realPath,usuario, cambiosDatosAsegurados);
		}
		return ar;
	}
	
	private HttpServletRequest informarRequest(AnexoModificacion anexo, HttpServletRequest request) {
		if(anexo.getPoliza().getLinea().isLineaGanado()){
			request.setAttribute(TIPO_LIN, Constants.TipoLinea.GAN);	
		}else{
			request.setAttribute(TIPO_LIN, Constants.TipoLinea.AGR);
		}
		return request;
	}
	
	private BigDecimal  obtenerIdCupon(AnexoModificacion am, BigDecimal idCupon) {
		if(StringUtils.nullToString(idCupon).equals("")){
			idCupon = new BigDecimal(am.getCupon().getId());
		}
		return idCupon;
	}

	/** SONAR Q ** MODIF TAM (16.12.2021) ** Fin **/

	// SETTER PARA SPRING
	public IConfirmacionModificacionManager getConfirmacionModificacionManager() {
		return confirmacionModificacionManager;
	}

	public void setConfirmacionModificacionManager(IConfirmacionModificacionManager confirmacionModificacionManager) {
		this.confirmacionModificacionManager = confirmacionModificacionManager;
	}

	public void setDeclaracionesModificacionPolizaController(
			DeclaracionesModificacionPolizaController declaracionesModificacionPolizaController) {
		this.declaracionesModificacionPolizaController = declaracionesModificacionPolizaController;
	}

	public void setAnexoModificacionUtilidadesController(
			AnexoModificacionUtilidadesController anexoModificacionUtilidadesController) {
		this.anexoModificacionUtilidadesController = anexoModificacionUtilidadesController;
	}

	public void setParcelasModificacionPolizaController(
			ParcelasModificacionPolizaController parcelasModificacionPolizaController) {
		this.parcelasModificacionPolizaController = parcelasModificacionPolizaController;
	}


	public void setDeclaracionesModificacionPolizaComplementariaController(
			DeclaracionesModificacionPolizaComplementariaController declaracionesModificacionPolizaComplementariaController) {
		this.declaracionesModificacionPolizaComplementariaController = declaracionesModificacionPolizaComplementariaController;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}	
}