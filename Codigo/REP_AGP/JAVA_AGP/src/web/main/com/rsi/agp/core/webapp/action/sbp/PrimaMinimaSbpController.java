package com.rsi.agp.core.webapp.action.sbp;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IPrimaMinimaSbpService;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;

public class PrimaMinimaSbpController extends BaseMultiActionController{
	
	private Log logger = LogFactory.getLog(PrimaMinimaSbpController.class);	
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	private IPrimaMinimaSbpService primaMinimaSbpService;	

	private String successView;	

	/**
	 * Realiza la consulta de primas minimas que se ajustan al filtro de busqueda
	 * @param request
	 * @param response
	 * @param primaMinimaSbp Objeto que encapsula el filtro de busqueda 
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaBean) throws Exception{				
		logger.debug("init - ConsultaPrimaMinimaSbpController");
    	
    	// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	final String perfil = usuario.getPerfil().substring(4);
    	// Map para guardar los parametros que se pasaran a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	// Variable que almacena el codigo de la tabla de polizas
    	String html = null;
    	String origenLlamada = request.getParameter("origenLlamada");
    	PrimaMinimaSbp primaMinimaBusqueda = (PrimaMinimaSbp) primaMinimaBean;
    	      	
		// --------------------------------------------------------
    	// -- Busqueda de las lineas que cumplen el sobreprecio --
        // --------------------------------------------------------
		// List<Sobreprecio> lineas = ConsultaSbpManager.getLineasSobrePrecio();
		
    	// ---------------------------------------------------------------------------------
    	// -- Busqueda de polizas para sobreprecio y generacion de la tabla de presentacion --
        // ---------------------------------------------------------------------------------
    	logger.debug("Comienza la busqueda de Primas Minimas de sobreprecio");    	
    	html = primaMinimaSbpService.getTablaPrimaMinimaParaSbp(request, response, primaMinimaBusqueda, origenLlamada);
        
		if (html == null) {
			return null; // an export
		} else {
			String ajax = request.getParameter("ajax");
			// Llamada desde ajax
			if (ajax != null && ajax.equals("true")) {
				byte[] contents = html.getBytes("UTF-8");
				response.getOutputStream().write(contents);
				return null;
			} else {
				// Pasa a la jsp el codigo de la tabla a traves de este atributo
				request.setAttribute("consultaPrimaMinimaSbp", html);
			}
		}
    	 		
		// ----------------------------------
    	// -- Carga del mapa de parametros --
        // ----------------------------------
		
        PrimaMinimaSbp primaMinimaSbp = new PrimaMinimaSbp();
		parameters.put("primaMinimaSbp", primaMinimaSbp);
		
		String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");
        if (alerta!=null){
        	parameters.put("alerta", alerta);
        }
        if (mensaje !=null){
			parameters.put("mensaje", mensaje);
		}
		parameters.put("perfil", perfil);
		logger.debug("Establecemos perfil");
    	
		// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	ModelAndView mv = new ModelAndView(successView);
       	mv = new ModelAndView(successView, "primaMinimaBean", primaMinimaBusqueda);
       	mv.addAllObjects(parameters);
       	
       	logger.debug("end - PrimaMinimaSbpController");
    	
    	return mv;     	       	       	       
	}
	
	/**
	 * Limpia el filtro de busqueda y realiza la consulta de todos los registros de primas minimas
	 * @param request
	 * @param response
	 * @param primaMinimaSbp Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que realiza la redireccion 
	 * @throws Exception
	 */
	public ModelAndView doLimpiar(HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaSbp) throws Exception{						
		return new ModelAndView(successView);       	       	       	       
	}
	
	/**
	 * Da de alta un registro de prima minima
	 * @param request
	 * @param response
	 * @param primaMinimaSbp Objeto que encapsula la informacion de la prima minima a dar de alta
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaSbp) throws Exception{				
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv= null;
		
		try{	
				if (primaMinimaSbp != null){
					parameters = primaMinimaSbpService.altaPrimaMinimaSbp(primaMinimaSbp);
				}
				
				mv = new ModelAndView("redirect:/primaMinimaSbp.run").addAllObjects(parameters);
						
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_GRAB_PRIMA_MINIMA_KO));		
				mv = new ModelAndView("redirect:/primaMinimaSbp.run").addAllObjects(parameters);
		}
		return mv; 		       	       	       	       
	}
	
	/**
	 * Edita un registro de prima minima
	 * @param request
	 * @param response
	 * @param primaMinimaSbp Objeto que encapsula la informacion de la prima minima a editar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaSbp) throws Exception{				
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv= null;
		
		try{
				if (!StringUtils.nullToString(request.getParameter("idPrimaMinimaSbp")).equals("")){

					primaMinimaSbp.setId(new Long(request.getParameter("idPrimaMinimaSbp")));
					parameters = primaMinimaSbpService.editarPrimaMinimaSbp(primaMinimaSbp);
				} else{
						parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITAR_PRIMA_MINIMA_KO));
				}
				
				mv = new ModelAndView("redirect:/primaMinimaSbp.run").addAllObjects(parameters);
					
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITAR_PRIMA_MINIMA_KO));		
				mv = new ModelAndView("redirect:/primaMinimaSbp.run").addAllObjects(parameters);
		}
		return mv; 	
		// return new ModelAndView(successView);       	       	       	       
	}
	
	/**
	 * Borra un registro de prima minima
	 * @param request
	 * @param response
	 * @param primaMinimaSbp Objeto que encapsula la informacion de la prima minima a borrar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaSbp) throws Exception{				
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv= null;

		try{
			String idPrimaMinimaSbp = (String) request.getParameter("idPrimaMinimaSbp");
			Long idPrimaMinimaSbpBigD = new Long(idPrimaMinimaSbp);
			primaMinimaSbp = primaMinimaSbpService.getPrimaMinimaSbp(idPrimaMinimaSbpBigD);
			
			if (primaMinimaSbp != null){
				primaMinimaSbpService.bajaPrimaMinimaSbp(primaMinimaSbp);
				parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_BORRAR_PRIMA_MINIMA_OK));
			}else{
				logger.error("la Prima Minima con idPrimaMinimaSbp: " + idPrimaMinimaSbp + " no existe en BBDD");
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_BORRAR_PRIMA_MINIMA_KO));
			}
			
		mv = new ModelAndView("redirect:/primaMinimaSbp.run").addAllObjects(parameters);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_BORRAR_PRIMA_MINIMA_KO));		
				mv = new ModelAndView("redirect:/primaMinimaSbp.run").addAllObjects(parameters);
		}
		return mv;     	       	       	       
	}	
	
	
	/**
	 * Setter de la propiedad
	 * @param primaMinimaSbpService
	 */
	public void setPrimaMinimaSbpService(
			IPrimaMinimaSbpService primaMinimaSbpService) {
		this.primaMinimaSbpService = primaMinimaSbpService;
	}
	
	/**
	 * Setter de la propiedad
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	
}
