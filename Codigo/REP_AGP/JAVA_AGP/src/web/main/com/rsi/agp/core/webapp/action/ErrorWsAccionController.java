package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.limit.Limit;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.IErrorWsAccionService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ErrorWsAccionController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(ErrorWsAccionController.class);
	private IErrorWsAccionService errorWsAccionService;
	
	
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * Realiza la consulta de errores que se ajustan al filtro de busqueda
	 * 
	 * @param request
	 * @param response
	 * @param errorWsAccionBean
	 *            Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, ErrorWsAccion errorWsAccionBean) {
		logger.debug("init - ErrorWsAccionController");
		
		// Obtiene el usuario de la sesion y su sesion
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final String perfil = usuario.getPerfil().substring(4);
		// Map para guardar los parametros que se pasaran a la jsp
		final Map<String, Object> parameters = new HashMap<String, Object>();
		// Variable que almacena el codigo de la tabla de polizas
		String html = null;
		ErrorWsAccion errorWsAccionBusqueda = (ErrorWsAccion) errorWsAccionBean;
		// Se añade este parametro para que pueda funcionar la lupa de entidades
		parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas (errorWsAccionBusqueda.getListaCodEntidadesGrupo(), false, false));
		String origenLlamada = request.getParameter("origenLlamada");
		if(errorWsAccionBusqueda.getEntidad() == null) {
			errorWsAccionBusqueda.setEntidad(new Entidad());
		}
		// ---------------------------------------------------------------------------------
		// -- Busqueda de erroes WS y generacion de la tabla de
		// presentacion --
		// ---------------------------------------------------------------------------------
		
		try {
			parameters.put("errorWsTipos", errorWsAccionService.obtenerListaTiposWsError());
			parameters.put("listaPerfil", errorWsAccionService.obtenerListaPerfiles());
		} catch (DAOException e1) {
			logger.error("ALGO HA IDO MAL", e1);
		}
		
		
		logger.debug("Comienza la busqueda de ErrorWsAccion");
		
		try{
			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				logger.debug("Comienza la busqueda de erroresWS");
				
				html = errorWsAccionService.getTablaErrorWsAccion(request, response, errorWsAccionBusqueda, origenLlamada);
				if (html == null) {
					return null; // an export
				} else {
					String ajax = request.getParameter("ajax");
					// Llamada desde ajax
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este atributo
						request.setAttribute("consultaErrorWsAccion", html);
				}
			}else{
				request.getSession().removeAttribute("consultaErrorWsAccion_LIMIT");
			}
			parameters.put("origenLlamada", origenLlamada);
			
			String mensaje = request.getParameter(Constants.KEY_MENSAJE);
			String alerta = request.getParameter(Constants.KEY_ALERTA);
			
			if (alerta != null) {
				parameters.put(Constants.KEY_ALERTA, alerta);
			}
			
			if (mensaje != null) {
				parameters.put(Constants.KEY_MENSAJE, mensaje);
			}
			
			parameters.put("perfil", perfil);
			// -----------------------------------------------------------------
			// -- Se crea el objeto que contiene la redireccion y se devuelve --
			// -----------------------------------------------------------------
			ModelAndView mv = new ModelAndView(successView);
			mv = new ModelAndView(successView, "errorWsAccionBean", errorWsAccionBean);
			mv.addAllObjects(parameters);
	
			logger.debug("end - ErrorWsAccionController");
	
			return mv;
		
		}catch (Exception e){
			logger.error("Excepcion : ErrorWsAccionController - doConsulta", e);
		}
		return null;
	}

	/**
	 * Da de alta un registro de errorWsAccion
	 * 
	 * @param request
	 * @param response
	 * @param errorWsAccionBean
	 *            Objeto que encapsula la informacion del errorWs a dar de
	 *            alta
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, ErrorWsAccion errorWsAccionBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();

		try{
			
			if (errorWsAccionBean!=null){
				
				if("true".equals(request.getParameter("nuevoError"))){
					//Damos de alta el ErrorWs
					parameters = errorWsAccionService.altaErrorWs(errorWsAccionBean);
				}

				//Si hubo errores en el alta, ni intentamos la operacion siguiente
				if(parameters.size()==0){
					parameters = errorWsAccionService.altaErrorWsAccion(errorWsAccionBean);
				}
			}else{
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.KO"));
			}
			
			// Si el mapa de parametros esta vacio, se ha dado de alta correctamente
			if(parameters != null && parameters.isEmpty()){
				parameters.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.errorWs.alta.OK"));
			}
		}
		catch (Exception e){
			logger.debug("Error inesperado en el alta de errorWsAccion", e);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.alta.KO"));
		}
		
		return doConsulta(request, response, errorWsAccionBean).addAllObjects(parameters);   
	}

	/**
	 * Edita un registro de error
	 * 
	 * @param request
	 * @param response
	 * @param errorWsAccionBean
	 *            Objeto que encapsula la informacion del error a editar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request,HttpServletResponse response, ErrorWsAccion errorWsAccionBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{
			if (!StringUtils.nullToString(errorWsAccionBean.getId()).equals("")){
				
				if("true".equals(request.getParameter("nuevoError"))){
					//Damos de alta el ErrorWs
					parameters = errorWsAccionService.altaErrorWs(errorWsAccionBean);
				}
				
				//Si hubo errores en el alta, ni intentamos la operacion siguiente
				if(parameters.size()==0){
					parameters = errorWsAccionService.editaErrorWsAccion(errorWsAccionBean);
				}
			}else{
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.edicion.KO"));
			}
			mv = doConsulta(request, response, errorWsAccionBean).addAllObjects(parameters);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.edicion.KO"));
			mv = doConsulta(request, response, errorWsAccionBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la edicion de errorWsAccion", e);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.edicion.KO"));
			mv = doConsulta(request, response, errorWsAccionBean).addAllObjects(parameters);
		}
		return mv;       	   
	}

	/**
	 * Borra un registro de error
	 * 
	 * @param request
	 * @param response
	 * @param errorWsAccionBean
	 *            Objeto que encapsula la informacion del error a borrar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, ErrorWsAccion errorWsAccionBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try{
			if (!StringUtils.nullToString(errorWsAccionBean.getId()).equals("")){
				
				errorWsAccionService.bajaErrorWsAccion(errorWsAccionBean);
				parameters.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.errorWs.borrado.OK"));
				//le quitamos el id al objeto
				errorWsAccionBean.setId(null);
			
			}else{
				parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.borrado.KO"));
			}
			mv = doConsulta(request, response, errorWsAccionBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.borrado.KO"));
			mv = doConsulta(request, response, errorWsAccionBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la baja de errorWsAccion", e);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.borrado.KO"));
			mv = doConsulta(request, response, errorWsAccionBean).addAllObjects(parameters);
		}
		return mv;     
	}
	
	/**
	 * Realiza la copia de todos los errores ws de un plan/linea y/o servicio a otro
	 * @param request
	 * @param response
	 * @param errorWsAccionBean
	 * @return
	 */
	public ModelAndView doReplicar(HttpServletRequest request, HttpServletResponse response, ErrorWsAccion errorWsAccionBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		BigDecimal planOrig = null, lineaOrig = null, planDest = null, lineaDest = null;
		String servicioOrig = "", servicioDest = "";
		
		try {
			// Obtiene el plan/linea origen y/o servicio
			planOrig = new BigDecimal (request.getParameter("plan_orig"));
			lineaOrig = new BigDecimal (request.getParameter("linea_orig"));
			servicioOrig = (String) request.getParameter("servicio_orig");
			// Obtiene el plan/linea destino y/o servicio
			planDest = new BigDecimal (request.getParameter("plan_dest"));
			lineaDest = new BigDecimal (request.getParameter("linea_dest"));
			servicioDest = (String) request.getParameter("servicio_dest");
			
			// Llamada al metodo que realiza la replica
			logger.debug("Replicar los errores ws del plan/linea " + planOrig + "/" + lineaOrig + " y/o servicio "+ servicioOrig + " al " + planDest + "/" + lineaDest + " y/o servicio "+ servicioDest);
			parameters = errorWsAccionService.replicar(planOrig, lineaOrig, planDest, lineaDest, servicioOrig, servicioDest);
		}
		catch (Exception e) {
			logger.debug("Error inesperado al replicar los errores ws a otro plan/linea", e);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.replica.KO"));
		}
		
		// Redireccion
		// Si ha ocurrido algun error, se vuelve a la pantalla con el filtro de busqueda anterior. Si el proceso ha finalizado correctamente,
		// se vuelve a la pantalla filtrando por el plan/linea destino
		if (!parameters.containsKey(Constants.KEY_ALERTA)) {
			errorWsAccionBean = new ErrorWsAccion ();
			Linea linea = new Linea ();
			linea.setCodlinea(lineaDest);
			linea.setCodplan(planDest);
			errorWsAccionBean.setLinea(linea);
			errorWsAccionBean.setServicio(servicioDest);
		}
		
		return doConsulta(request, response, errorWsAccionBean).addAllObjects(parameters);
	}
	
	/** DAA 08/02/2013 Cambio masivo de errorWs
	 * 
	 * @param request
	 * @param response
	 * @param errorWsAccionBean
	 * @return mv
	 */
	public ModelAndView doCambioMasivo(HttpServletRequest request, HttpServletResponse response, ErrorWsAccion errorWsAccionBean) {
		logger.debug("init - ErrorWsAccionController - doCambioMasivo");
		Map<String, String> parameters = new HashMap<String, String>();
		ModelAndView mv = null;
		String listaIdsMarcados_cm = StringUtils.nullToString(request.getParameter("listaIdsMarcados_cm"));
		
		try{
			parameters = errorWsAccionService.cambioMasivo(listaIdsMarcados_cm, errorWsAccionBean); 
		
		}catch (Exception e){
			logger.debug("Error inesperado en el Cambio Masivo de ClaseDetalle ", e);
			parameters.put(Constants.KEY_ALERTA, bundle.getString("mensaje.errorWs.edicion.KO"));
		
		}	
		//DAA 05/03/2013 recuperamos el filtro de sesion para pasarlo al nuevo Bean del Cambio Masivo y 
		//no perder el filtro al volver
		ErrorWsAccion cambioMasivoErrorWsAccionBean = errorWsAccionService.getCambioMasivoBeanFromLimit((Limit) request.getSession().getAttribute("consultaErrorWsAccion_LIMIT"));
		
		mv = doConsulta(request, response, cambioMasivoErrorWsAccionBean).addAllObjects(parameters);
		
		logger.debug("end - ErrorWsAccionController - doCambioMasivo");
		return mv;
	}
	
	/**
	 * Comprueba si existe ya un error WS con un determinado codigo
	 * @param request
	 * @param response
	 */
	public void doValidarErrorWsAjax(final HttpServletRequest request, final HttpServletResponse response) {
		
		JSONObject params = new JSONObject();
		Map<String, Object> parameters = new HashMap<String, Object>();		
		boolean existe = false;
		
		try{
			ErrorWsAccion errWs = new ErrorWsAccion();
			String strIdErrorWs = request.getParameter("idErrorWs");
			String strIdCatalogWs = request.getParameter("idCatalogWs");
			errWs.getErrorWs().getId().setCoderror(new BigDecimal(strIdErrorWs));
			errWs.getErrorWs().getId().setCatalogo(strIdCatalogWs.charAt(0));

			// validamos si el codigo del Error existe
			existe = errorWsAccionService.checkCodigoErrorWS(errWs, parameters);
			
			if(existe){
				params.put(Constants.KEY_MENSAJE,"true");
			}else{
				params.put(Constants.KEY_MENSAJE,"false");
			}
			
		    response.setCharacterEncoding("UTF-8");
		    this.getWriterJSON(response, params);
		}
		catch(Exception e){
			logger.error("doValidarErrorWsAjax - Ocurrio un error inesperado.", e);
			try {
				params.put(Constants.KEY_MENSAJE,"false");
			    response.setCharacterEncoding("UTF-8");
			    this.getWriterJSON(response, params);
			} catch (JSONException e1) {}
    	}
	}
	

	/**
	 * Setter de propiedad
	 * 
	 * @param errorWsAccionService
	 */
	public void setErrorWsAccionService(
			IErrorWsAccionService errorWsAccionService) {
		this.errorWsAccionService = errorWsAccionService;
	}

	/**
	 * Setter de propiedad
	 * 
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
}