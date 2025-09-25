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
import com.rsi.agp.core.jmesa.service.IMtoImpuestoSbpService;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;


public class MtoImpuestoSbpController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(MtoImpuestoSbpController.class);
	private IMtoImpuestoSbpService mtoImpuestoSbpService;
	
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");

	/**
	 * Realiza la consulta de mtoImpuestoSbp que se ajustan al filtro de búsqueda
	 * 
	 * @param request
	 * @param response
	 * @param mtoImpuestoSbpBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, MtoImpuestoSbp mtoImpuestoSbpBean) {
		logger.debug("init - MtoImpuestoSbpController");
		
		// Obtiene el usuario de la sesión y su sesión
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final String perfil = usuario.getPerfil().substring(4);
		// Map para guardar los parámetros que se pasarán a la jsp
		final Map<String, Object> parameters = new HashMap<String, Object>();
		// Variable que almacena el código de la tabla de pólizas
		String html = null;
		MtoImpuestoSbp mtoImpuestoSbpBusqueda = (MtoImpuestoSbp) mtoImpuestoSbpBean;

		String origenLlamada = request.getParameter("origenLlamada");
		try{
		// ---------------------------------------------------------------------------------
		// -- Búsqueda de erroes WS y generación de la tabla de
		// presentación --
		// ---------------------------------------------------------------------------------
			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				logger.debug("Comienza la búsqueda de MtoImpuestoSbp");
				
				html = mtoImpuestoSbpService.getTablaMtoImpuestoSbp(request, response, mtoImpuestoSbpBusqueda, origenLlamada);
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
						request.setAttribute("consultaMtoImpuestoSbp", html);
				}
			}
				
				parameters.put("origenLlamada", request.getParameter("origenLlamada"));
				String mensaje = request.getParameter("mensaje");
				String alerta = request.getParameter("alerta");
				if (alerta != null) {
					parameters.put("alerta", alerta);
				}
				if (mensaje != null) {
					parameters.put("mensaje", mensaje);
				}
				parameters.put("perfil", perfil);
		
				// -----------------------------------------------------------------
				// -- Se crea el objeto que contiene la redirección y se devuelve --
				// -----------------------------------------------------------------
				ModelAndView mv = new ModelAndView(successView);
				mv = new ModelAndView(successView, "mtoImpuestoSbpBean", mtoImpuestoSbpBean);
				mv.addAllObjects(parameters);
		
				logger.debug("end - MtoImpuestoSbpController");
		
				return mv;
		
		}catch (Exception e){
			logger.error("Excepcion : MtoImpuestoSbpController - doConsulta", e);
		}
		return null;
	}

	/**
	 * Da de alta un registro de mtoImpuestoSbp
	 * 
	 * @param request
	 * @param response
	 * @param mtoImpuestoSbpBean
	 *            Objeto que encapsula la información del mtoImpuestoSbp a dar de
	 *            alta
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, MtoImpuestoSbp mtoImpuestoSbpBean) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			ModelAndView mv = new ModelAndView(successView);
			try{
					if (mtoImpuestoSbpBean!=null){
						parameters = mtoImpuestoSbpService.altaMtoImpuestoSbp(mtoImpuestoSbpBean);
						parameters.put("showModificar", "true");
					}else{
						parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.KO"));
					}
					mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
					
			}catch (BusinessException e) {
				logger.error("Se ha producido un error: " + e.getMessage());
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.KO"));
				mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
				
			}catch (Exception e){
				logger.debug("Error inesperado en el alta de mtoImpuestoSbp", e);
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.alta.KO"));
				mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
			}
			return mv;   
	}

	/**
	 * Edita un registro de mtoImpuestoSbp
	 * 
	 * @param request
	 * @param response
	 * @param mtoImpuestoSbpBean
	 *            Objeto que encapsula la información del mtoImpuestoSbp a editar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request,HttpServletResponse response, MtoImpuestoSbp mtoImpuestoSbpBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{
			if (!StringUtils.nullToString(mtoImpuestoSbpBean.getId()).equals("")){
				parameters = mtoImpuestoSbpService.editaMtoImpuestoSbp(mtoImpuestoSbpBean);
			}else{
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.edicion.KO"));
			}
			parameters.put("showModificar", "true");
			mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.edicion.KO"));
			mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la edición de mtoImpuestoSbp", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.edicion.KO"));
			mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
		}
		return mv;       	   
	}

	/**
	 * Borra un registro de mtoImpuestoSbp
	 * 
	 * @param request
	 * @param response
	 * @param mtoImpuestoSbpBean
	 *            Objeto que encapsula la información del mtoImpuestoSbp a borrar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, MtoImpuestoSbp mtoImpuestoSbpBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try{
			if (!StringUtils.nullToString(mtoImpuestoSbpBean.getId()).equals("")){
				
				mtoImpuestoSbpService.bajaMtoImpuestoSbp(mtoImpuestoSbpBean.getId());
				parameters.put("mensaje", bundle.getString("mensaje.mtoImpuestoSbp.borrado.OK"));
				//le quitamos el id al objeto
				mtoImpuestoSbpBean.setId(null);
			
			}else{
				parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.borrado.KO"));
			}
			mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.borrado.KO"));
			mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la baja de mtoImpuestoSbp", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.borrado.KO"));
			mv = doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
		}
		return mv;     
	}
	
	/**
	 * Realiza la copia de todos los mtoImpuestoSbp de un plan/línea a otro
	 * @param request
	 * @param response
	 * @param mtoImpuestoSbpBean
	 * @return
	 */
	public ModelAndView doReplicar(HttpServletRequest request, HttpServletResponse response, MtoImpuestoSbp mtoImpuestoSbpBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		BigDecimal planDest = null;
		
		try {
			
			BigDecimal planOrig = mtoImpuestoSbpBean.getCodplan();
			planDest = new BigDecimal (request.getParameter("planreplica"));
			
			// Llamada al método que realiza la réplica
			logger.debug("Replicar los mtoImpuestoSbp del plan " + planOrig + " al" + planDest);
			parameters = mtoImpuestoSbpService.replicar(planOrig, planDest);
		}
		catch (Exception e) {
			logger.debug("Error inesperado al replicar los mtoImpuestoSbp a otro plan", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoImpuestoSbp.replica.KO"));
		}
		
		// Redirección
		// Si ha ocurrido algún error, se vuelve a la pantalla con el filtro de búsqueda anterior. Si el proceso ha finalizado correctamente,
		// se vuelve a la pantalla filtrando por el plan destino
		if (!parameters.containsKey("alerta")) {
			mtoImpuestoSbpBean = new MtoImpuestoSbp ();
			mtoImpuestoSbpBean.setCodplan(planDest);
		}
		
		return doConsulta(request, response, mtoImpuestoSbpBean).addAllObjects(parameters);
	}
	
	/**
	 * Setter de propiedad
	 * 
	 * @param mtoImpuestoSbpService
	 */
	public void setMtoImpuestoSbpService(IMtoImpuestoSbpService mtoImpuestoSbpService) {
		this.mtoImpuestoSbpService = mtoImpuestoSbpService;
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

