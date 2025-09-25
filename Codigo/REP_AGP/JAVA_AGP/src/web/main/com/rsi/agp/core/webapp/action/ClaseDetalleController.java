package com.rsi.agp.core.webapp.action;

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
import com.rsi.agp.core.jmesa.service.IClaseDetalleService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ClaseDetalleController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(ClaseDetalleController.class);
	private IClaseDetalleService claseDetalleService;
	
	
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * Realiza la consulta del detalle de clases que se ajustan al filtro de busqueda
	 * 
	 * @param request
	 * @param response
	 * @param claseDetalleBean
	 *            Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, ClaseDetalle claseDetalleBean) {
		logger.debug("init - ClaseDetalleController");
		
		// Obtiene el usuario de la sesion y su sesion
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final String perfil = usuario.getPerfil().substring(4);
		// Map para guardar los parametros que se pasaran a la jsp
		final Map<String, Object> parameters = new HashMap<String, Object>();
		// Variable que almacena el codigo de la tabla de polizas
		String html = null;
		ClaseDetalle claseDetalleBusqueda = (ClaseDetalle) claseDetalleBean;		
		String origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));
	
		// ---------------------------------------------------------------------------------
		// -- Busqueda de clases y generacion de la tabla de
		// presentacion --
		// ---------------------------------------------------------------------------------
		logger.debug("Comienza la busqueda de ClaseDetalle");
		
		try{
			html = claseDetalleService.getTablaClaseDetalle(request, response, claseDetalleBusqueda, origenLlamada);
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
					request.setAttribute("consultaClaseDetalle", html);
			}
				
			String mensaje = request.getParameter("mensaje");
			String alerta = request.getParameter("alerta");
			if (alerta != null) {
				parameters.put("alerta", alerta);
			}
			if (mensaje != null) {
				parameters.put("mensaje", mensaje);
			}
			parameters.put("perfil", perfil);
			
			if (origenLlamada.equals("cargaClases")){
				parameters.put("vieneDeCargaClases", true);
				parameters.put("origenLlamada", "cargaClases");
		    }
			
			parameters.put("codlinea",
					request.getParameter("codlinea") == null ? claseDetalleBusqueda.getClase().getLinea().getCodlinea()
							: request.getParameter("codlinea"));
			parameters.put("fechaInicioContratacion", request.getParameter("fechaInicioContratacion"));
			
			// -----------------------------------------------------------------
			// -- Se crea el objeto que contiene la redireccion y se devuelve --
			// -----------------------------------------------------------------
			ModelAndView mv = new ModelAndView(successView);
			mv = new ModelAndView(successView, "claseDetalleBean", claseDetalleBean);
			mv.addAllObjects(parameters);
	
			logger.debug("end - ClaseDetalleController");
	
			return mv;
		
		}catch (Exception e){
			logger.error("Excepcion : ClaseDetalleController - doConsulta", e);
		}
		return null;
	}

	/**
	 * Da de alta una ClaseDetalle
	 * 
	 * @param request
	 * @param response
	 * @param claseDetalleBean
	 *            Objeto que encapsula la informacion de la clase a dar de
	 *            alta
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, ClaseDetalle claseDetalleBean) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			try{								
				if (claseDetalleBean!=null){
					String detallelineaseguroid = claseDetalleBean.getClase().getLinea().getLineaseguroid().toString();
					claseDetalleBean.setId(null);
					
					parameters = claseDetalleService.insertOrUpdateClaseDetalle(claseDetalleBean, new Long(detallelineaseguroid));
					if (!parameters.containsKey("alerta")){
						//Ha ido todo bien
						parameters.put("mensaje", bundle.getString("mensaje.clase.detalle.alta.OK"));
					}
					if (!parameters.containsKey("alerta") && !parameters.containsKey("mensaje")){
						//Ha dado una excepcion el alta
						parameters.put("alerta", bundle.getString("mensaje.clase.detalle.alta.KO"));
					}
				}else{
					parameters.put("alerta", bundle.getString("mensaje.clase.detalle.alta.KO"));
				}
			}
			catch (BusinessException e) {
				logger.error("Se ha producido un error: " + e.getMessage());
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.alta.KO"));
				
			}
			catch (Exception e){
				logger.debug("Error inesperado en el alta de la claseDetalle", e);
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.alta.KO"));
			}
			
			return doConsulta(request, response, claseDetalleBean).addAllObjects(parameters);   
	}

	/**
	 * Edita una claseDetalle
	 * 
	 * @param request
	 * @param response
	 * @param claseDetalleBean
	 *            Objeto que encapsula la informacion la claseDetalle a editar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request,HttpServletResponse response, ClaseDetalle claseDetalleBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{	
			String detallelineaseguroid = claseDetalleBean.getClase().getLinea().getLineaseguroid().toString();
		
			if (!StringUtils.nullToString(claseDetalleBean.getId()).equals("")){
				parameters = claseDetalleService.insertOrUpdateClaseDetalle(claseDetalleBean, new Long(detallelineaseguroid));
				if (!parameters.containsKey("alerta")){
					//Ha ido todo bien
					parameters.put("mensaje", bundle.getString("mensaje.clase.detalle.edicion.OK"));
				}
				if (!parameters.containsKey("alerta") && !parameters.containsKey("mensaje")){
					//Ha dado una excepcion la modificacion
					parameters.put("alerta", bundle.getString("mensaje.clase.detalle.edicion.KO"));
				}
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.edicion.KO"));
			}
				
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.clase.edicion.KO"));
			
		}catch (Exception e){
			logger.debug("Error inesperado en la edicion de la clase", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.edicion.KO"));
		}
		
		return doConsulta(request, response, claseDetalleBean).addAllObjects(parameters);       	   
	}

	/**
	 * Borra una claseDetalle
	 * 
	 * @param request
	 * @param response
	 * @param claseDetalleBean
	 *            Objeto que encapsula la informacion de la clase a borrar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, ClaseDetalle claseDetalleBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try{
			if (!StringUtils.nullToString(claseDetalleBean.getId()).equals("")){
				
				claseDetalleService.bajaClaseDetalle(claseDetalleBean);
				parameters.put("mensaje", bundle.getString("mensaje.clase.detalle.borrado.OK"));
			
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.detalle.borrado.KO"));
			}

			mv = doConsulta(request, response, claseDetalleBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.clase.detalle.borrado.KO"));
			mv = doConsulta(request, response, claseDetalleBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la baja de la clase", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.borrado.KO"));
			mv = doConsulta(request, response, claseDetalleBean).addAllObjects(parameters);
		}
		return mv;     
	}
	/** DAA 08/02/2013 Cambio masivo de claseDetalle
	 * 
	 * @param request
	 * @param response
	 * @param claseDetalleBean
	 * @return mv
	 */
	public ModelAndView doCambioMasivo(HttpServletRequest request, HttpServletResponse response, ClaseDetalle claseDetalleBean) {
		logger.debug("init - ClaseDetalleController - doCambioMasivo");
		Map<String, String> parameters = new HashMap<String, String>();
		ModelAndView mv = null;
		String listaIdsMarcados_cm = StringUtils.nullToString(request.getParameter("listaIdsMarcados_cm"));
		
		String cicloCultivoCheck = StringUtils.nullToString(request.getParameter("cicloCultivoCheck"));
		String sistemaCultivoCheck = StringUtils.nullToString(request.getParameter("sistemaCultivoCheck"));
		String tipoCapitalCheck = StringUtils.nullToString(request.getParameter("tipoCapitalCheck"));
		String tipoPlantacionCheck = StringUtils.nullToString(request.getParameter("tipoPlantacionCheck"));
		
		try{
			parameters = claseDetalleService.cambioMasivo(listaIdsMarcados_cm, claseDetalleBean,cicloCultivoCheck,
					sistemaCultivoCheck,tipoCapitalCheck,tipoPlantacionCheck);
			
			
			
		}catch (Exception e){
			logger.debug("Error inesperado en el Cambio Masivo de ClaseDetalle ", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.detalle.edicion.KO"));
		
		}	
		//TMR 05/03/2013 recuperamos el filtro de sesion para pasarlo al nuevo Bean del Cambio Masivo y 
		//no perder el filtro al volver
		ClaseDetalle cambioMasivoClaseDetalleBean = claseDetalleService.getCambioMasivoBeanFromLimit((Limit) request.getSession().getAttribute("consultaClaseDetalle_LIMIT"));
		
		mv = doConsulta(request, response, cambioMasivoClaseDetalleBean).addAllObjects(parameters);
		
		logger.debug("end - ClaseDetalleController - doCambioMasivo");
		return mv;
	}
	
	/**
	 * Setter de propiedad
	 * 
	 * @param claseDetalleService
	 */
	public void setClaseDetalleService(
			IClaseDetalleService claseDetalleService) {
		this.claseDetalleService = claseDetalleService;
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
