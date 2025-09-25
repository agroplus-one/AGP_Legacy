package com.rsi.agp.core.webapp.action.sbp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IFechasContratacionSbpService;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.sbp.FechaContratacionSbp;

public class FechasContratacionSbpController extends BaseMultiActionController{
	
	private Log logger = LogFactory.getLog(FechasContratacionSbpController.class);	
	private IFechasContratacionSbpService fechasContratacionSbpService;	
	
	
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	
	/**
	 * Realiza la consulta de fechas de contratacion que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param fechaContratacionSbp Objeto que encapsula el filtro de búsqueda 
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, FechaContratacionSbp fechaContratacionSbp) {				
		
		logger.debug("init - FechasContratacionSbpController");
    	
    	// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	final String perfil = usuario.getPerfil().substring(4);
    	// Map para guardar los parametros que se pasaran a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	// Variable que almacena el codigo de la tabla de polizas
    	String html = null;
    	String origenLlamada = request.getParameter("origenLlamada");
    	
    	FechaContratacionSbp fechaContratacionSbpBusqueda = (FechaContratacionSbp) fechaContratacionSbp;
    	
    	// ---------------------------------------------------------------------------------
    	// -- Búsqueda de polizas para sobreprecio y generacion de la tabla de presentacion --
        // ---------------------------------------------------------------------------------
    	logger.debug("Comienza la búsqueda de fechas de Contratacion");    	
    	
		if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
			html = fechasContratacionSbpService.getTablaFechasContratacionSbp(request, response,
					fechaContratacionSbpBusqueda, origenLlamada);
			if (html == null) {
				return null; // an export
			} else {
				String ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents;
					try {
						contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} catch (UnsupportedEncodingException e) {
						return null;
					} catch (IOException e) {
						return null;
					}
				} else {
					// Pasa a la jsp el codigo de la tabla a traves de este atributo
					request.setAttribute("consultaFechasContratacionSbp", html);
				}
			}
		}
		parameters.put("fechaContratacionSbp", fechaContratacionSbp);
		// Anhade el parametro que indica si la llamada se ha hecho desde el menu lateral
    	parameters.put("origenLlamada", request.getParameter("origenLlamada"));
		String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");
        if (alerta!=null){
        	parameters.put("alerta", alerta);
        }
        if (mensaje !=null){
			parameters.put("mensaje", mensaje);
		}
		parameters.put("perfil", perfil);
		
		
		
		// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	ModelAndView mv = new ModelAndView(successView);
       	mv = new ModelAndView(successView, "fechaContratacionSbp", fechaContratacionSbpBusqueda);
       	mv.addAllObjects(parameters);
       	
       	logger.debug("end - ConsultaSbpController");
    	
    	return mv;
		
	}
	
	/**
	 * Limpia el filtro de búsqueda y realiza la consulta de todos los registros de fechas de contratacion
	 * @param request
	 * @param response
	 * @param fechaContratacionSbp Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redireccion 
	 * @throws Exception
	 */
	public ModelAndView doLimpiar(HttpServletRequest request, HttpServletResponse response, FechaContratacionSbp fechaContratacionSbp) {				
		return new ModelAndView(successView);       	       	       	       
	}
	
	/**
	 * Da de alta un registro de fecha de contratacion minima
	 * @param request
	 * @param response
	 * @param fechaContratacionSbp Objeto que encapsula la informacion de la fecha de contratacion a dar de alta
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, FechaContratacionSbp fechaContratacionSbp) {				
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = new ModelAndView(successView);
		try{
			if (fechaContratacionSbp!=null){
				parameters = fechasContratacionSbpService.altaFechasContratacionSbp(fechaContratacionSbp);
				parameters.put("showModificar", "true");
				// parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_ALTA_FECHA_CONTRATACION_OK));
			}else{
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_ALTA_FECHA_CONTRATACION_KO));
			}
			//mv = new ModelAndView("redirect:/periodoContSbp.run", "fechaContratacionSbp",fechaContratacionSbp).addAllObjects(parameters);
			return doConsulta(request, response, fechaContratacionSbp).addAllObjects(parameters);
			
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error al dar de alta fechas contratacion sobreprecio", e);
			mv = new ModelAndView("redirect:/periodoContSbp.run", "fechaContratacionSbp",fechaContratacionSbp).addAllObjects(parameters);
			
		}catch (Exception e) {
			logger.error("Se ha producido un error al dar de alta fechas contratacion sobreprecio", e);
			mv = new ModelAndView("redirect:/periodoContSbp.run", "fechaContratacionSbp",fechaContratacionSbp).addAllObjects(parameters);
			
		}
		return mv;              	       	       	       
	}
	
	/**
	 * Edita un registro de fecha de contratacion minima
	 * @param request
	 * @param response
	 * @param fechaContratacionSbp Objeto que encapsula la informacion de la fecha de contratacion a editar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request, HttpServletResponse response, FechaContratacionSbp fechaContratacionSbp) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{
			if (!StringUtils.nullToString(fechaContratacionSbp.getId()).equals("")){
				//fechaContratacionSbp.setId(new Long(request.getParameter("idFechaContatacionSbp")));
				parameters = fechasContratacionSbpService.editaFechasContratacionSbp(fechaContratacionSbp);
				// parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_EDITA_FECHA_CONTRATACION_OK));
				parameters.put("showModificar", "true");
			}else{
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITA_FECHA_CONTRATACION_KO));
			}
			return doConsulta(request, response, fechaContratacionSbp).addAllObjects(parameters);
		
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error", e);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITA_FECHA_CONTRATACION_KO));
			mv = new ModelAndView("redirect:/periodoContSbp.run", "fechaContratacionSbp",fechaContratacionSbp).addAllObjects(parameters);
			
		}catch (Exception e) {
			logger.error("Se ha producido un error", e);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_EDITA_FECHA_CONTRATACION_KO));
			mv = new ModelAndView("redirect:/periodoContSbp.run", "fechaContratacionSbp",fechaContratacionSbp).addAllObjects(parameters);
			
		}
		return mv;       	     	       	       
	}
	
	/**
	 * Borra un registro de fecha de contratacion minima
	 * @param request
	 * @param response
	 * @param fechaContratacionSbp Objeto que encapsula la informacion de la fecha de contratacion a borrar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, FechaContratacionSbp fechaContratacionSbp) {				
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = new ModelAndView(successView);
		try{
			if (!StringUtils.nullToString(fechaContratacionSbp.getId()).equals("")){
				
				fechasContratacionSbpService.bajaFechasContratacionSbp(fechaContratacionSbp);
				parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_BORRAR_FECHA_CONTRATACION_OK));
			
			}else{
				parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_BORRAR_FECHA_CONTRATACION_KO));
			}
			return doConsulta(request, response, fechaContratacionSbp).addAllObjects(parameters);
			//mv = new ModelAndView("redirect:/periodoContSbp.run", "fechaContratacionSbp",fechaContratacionSbp).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error", e);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_BORRAR_FECHA_CONTRATACION_KO));
			mv = new ModelAndView("redirect:/periodoContSbp.run", "fechaContratacionSbp",fechaContratacionSbp).addAllObjects(parameters);
			
		}catch (Exception e) {
			logger.error("Se ha producido un error", e);
			parameters.put("alerta", bundle.getObject(ConstantsSbp.ALERT_BORRAR_FECHA_CONTRATACION_KO));
			mv = new ModelAndView("redirect:/periodoContSbp.run", "fechaContratacionSbp",fechaContratacionSbp).addAllObjects(parameters);
			
		}
		return mv;       	    	       	       	       
	}	
	
	
	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. En MultiActionController no se hace este bind
     * automaticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacias
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }

	/**
	 * Setter de propiedad
	 * @param fechasContratacionSbpService
	 */
	public void setFechasContratacionSbpService(
			IFechasContratacionSbpService fechasContratacionSbpService) {
		this.fechasContratacionSbpService = fechasContratacionSbpService;
	}


	/**
	 * Setter de propiedad
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	

}
