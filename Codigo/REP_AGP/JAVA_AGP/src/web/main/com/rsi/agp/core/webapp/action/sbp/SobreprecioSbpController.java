package com.rsi.agp.core.webapp.action.sbp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.ISobreprecioSbpService;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;


public class SobreprecioSbpController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(SobreprecioSbpController.class);
	private ISobreprecioSbpService sobreprecioSbpService;
	
	
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");

	/**
	 * Realiza la consulta de sobreprecios que se ajustan al filtro de búsqueda
	 * 
	 * @param request
	 * @param response
	 * @param sobreprecioBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Sobreprecio sobreprecioBean) {
		logger.debug("init - SobreprecioSbpController");
		
		// Obtiene el usuario de la sesión y su sesión
		final Usuario usuario = (Usuario) request.getSession().getAttribute(
				"usuario");
		final String perfil = usuario.getPerfil().substring(4);
		// Map para guardar los parámetros que se pasarán a la jsp
		final Map<String, Object> parameters = new HashMap<String, Object>();
		// Variable que almacena el código de la tabla de pólizas
		String html = null;
		String origenLlamada = request.getParameter("origenLlamada");
		Sobreprecio sobreprecioBusqueda = (Sobreprecio) sobreprecioBean;

		// ---------------------------------------------------------------------------------
		// -- Búsqueda de pólizas para sobreprecio y generación de la tabla de
		// presentación --
		// ---------------------------------------------------------------------------------
		logger.debug("Comienza la búsqueda de sobreprecios");
		
		try{
			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				html = sobreprecioSbpService.getTablaSobreprecios(
						request, response, sobreprecioBusqueda, origenLlamada);
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
						// Pasa a la jsp el código de la tabla a través de este atributo
						request.setAttribute("consultaSobreprecioSbp", html);
				}
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
			// Añade el parametro que indica si la llamada se ha hecho desde el menu lateral
	    	parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			// -----------------------------------------------------------------
			// -- Se crea el objeto que contiene la redirección y se devuelve --
			// -----------------------------------------------------------------
			ModelAndView mv = new ModelAndView(successView);
			mv = new ModelAndView(successView, "sobreprecioBean", sobreprecioBean);
			mv.addAllObjects(parameters);
	
			logger.debug("end - SobreprecioSbpController");
	
			return mv;
		
		}catch (Exception e){
			logger.error("Excepcion : SobreprecioSbpController - doConsulta", e);
		}
		return null;
	}

	/**
	 * Limpia el filtro de búsqueda y realiza la consulta de todos los registros
	 * de sobreprecio
	 * 
	 * @param request
	 * @param response
	 * @param sobreprecioBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doLimpiar(HttpServletRequest request,
			HttpServletResponse response, Sobreprecio sobreprecioBean) {
		return new ModelAndView(successView);
	}

	/**
	 * Da de alta un registro de sobreprecio
	 * 
	 * @param request
	 * @param response
	 * @param sobreprecioBean
	 *            Objeto que encapsula la información del sobreprecio a dar de
	 *            alta
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, Sobreprecio sobreprecioBean) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			ModelAndView mv = new ModelAndView(successView);
			try{
					if (sobreprecioBean!=null){
						parameters = sobreprecioSbpService.altaSobreprecio(sobreprecioBean);
						parameters.put("showModificar", "true");
						// parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_ALTA_SOBREPRECIO_OK));
					}else{
						parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_ALTA_SOBREPRECIO_KO));
					}
					mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
					
			}catch (BusinessException e) {
				logger.error("Se ha producido un error: " + e.getMessage());
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_ALTA_SOBREPRECIO_KO));
				mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
				
			}catch (Exception e){
				logger.debug("Error inesperado en el alta de sobreprecio", e);
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_ALTA_SOBREPRECIO_KO));
				mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
			}
			return mv;   
	}

	/**
	 * Edita un registro de sobreprecio
	 * 
	 * @param request
	 * @param response
	 * @param sobreprecioBean
	 *            Objeto que encapsula la información del sobreprecio a editar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request,
			HttpServletResponse response, Sobreprecio sobreprecioBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{
				if (!StringUtils.nullToString(sobreprecioBean.getId()).equals("")){
					parameters = sobreprecioSbpService.editaSobreprecio(sobreprecioBean);
					parameters.put("showModificar", "true");
					
				}else{
					parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITA_SOBREPRECIO_KO));
				}
				mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITA_SOBREPRECIO_KO));
			mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la edición de sobreprecio", e);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITA_SOBREPRECIO_KO));
			mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
		}
		return mv;       	   
	}

	/**
	 * Borra un registro de sobreprecio
	 * 
	 * @param request
	 * @param response
	 * @param sobreprecioBean
	 *            Objeto que encapsula la información del sobreprecio a borrar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, Sobreprecio sobreprecioBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try{
			if (!StringUtils.nullToString(sobreprecioBean.getId()).equals("")){
				
				sobreprecioSbpService.bajaSobreprecio(sobreprecioBean);
				parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_BORRAR_SOBREPRECIO_OK));
			
			}else{
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_BORRAR_SOBREPRECIO_KO));
			}
			mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_BORRAR_SOBREPRECIO_KO));
			mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la baja de sobreprecio", e);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_BORRAR_SOBREPRECIO_KO));
			mv = doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
		}
		return mv;     
	}
	
	/**
	 * Realiza la copia de todos las sobreprecios de un plan/línea a otro
	 * @param request
	 * @param response
	 * @param sobreprecioBean
	 * @return
	 * @throws Exception 
	 */
	public ModelAndView doReplicar(HttpServletRequest request, HttpServletResponse response, Sobreprecio sobreprecioBean) throws Exception{
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		BigDecimal planDest = null;
		BigDecimal lineaDest = null;
		
		try {
			// Obtiene el plan/línea destino
			BigDecimal planOrig = sobreprecioBean.getLinea().getCodplan();
			BigDecimal lineaOrig = sobreprecioBean.getLinea().getCodlinea();
			planDest = new BigDecimal (request.getParameter("planreplica"));
			lineaDest = new BigDecimal (request.getParameter("lineareplica"));
			
			// Llamada al método que realiza la réplica
			logger.debug("Replicar los Sbp del plan/linea " + planOrig + "/" + lineaOrig + " al " + planDest + "/" + lineaDest);
			parameters = sobreprecioSbpService.replicar(planOrig, lineaOrig, planDest, lineaDest);
		}
		catch (Exception e) {
			logger.debug("Error inesperado al replicar los Sbp a otro plan/linea", e);
			parameters.put("alerta", bundle.getString("mensaje.replicarSobreprecio.KO"));
		}
		
		// Redirección
		// Si ha ocurrido algún error, se vuelve a la pantalla con el filtro de búsqueda anterior. Si el proceso ha finalizado correctamente,
		// se vuelve a la pantalla filtrando por el plan/línea destino
		if (!parameters.containsKey("alerta")) {
			sobreprecioBean = new Sobreprecio ();
			Linea linea = new Linea ();
			linea.setCodlinea(lineaDest);
			linea.setCodplan(planDest);
			sobreprecioBean.setLinea(linea);
		}
		
		return doConsulta(request, response, sobreprecioBean).addAllObjects(parameters);
	}
	
	
	/**
	 * Setter de propiedad
	 * 
	 * @param sobreprecioSbpService
	 */
	public void setSobreprecioSbpService(
			ISobreprecioSbpService sobreprecioSbpService) {
		this.sobreprecioSbpService = sobreprecioSbpService;
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
