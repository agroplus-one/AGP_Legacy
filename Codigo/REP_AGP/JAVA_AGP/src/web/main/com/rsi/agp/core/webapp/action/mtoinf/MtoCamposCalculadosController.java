package com.rsi.agp.core.webapp.action.mtoinf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.mtoinf.IMtoCamposCalculadosService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;

public class MtoCamposCalculadosController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(MtoCamposCalculadosController.class);
	private IMtoCamposCalculadosService mtoCamposCalculadosService; 
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	/**
	 * Realiza la consulta de campos calculados que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param camposCalculados Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que contiene la redirección a la página de mantenimiento de campos calculados
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CamposCalculados camposCalculados) {
	
		ModelAndView mv = null;
		logger.debug("init - doConsulta en MtoCamposCalculadosController");

		// Map para guardar los parámetros que se pasarán a la jsp
		final Map<String, Object> parameters = new HashMap<String, Object>();
		String html = null;
		List<CamposPermitidos> listaCampoOperando = null;
		
		try {	
			String origenLlamada = request.getParameter("origenLlamada");
			// recupera la lista de campos de Operandos 
			listaCampoOperando = mtoCamposCalculadosService.getListCamposPermitidos();
		
			String ajax = request.getParameter("ajax");
			html = mtoCamposCalculadosService.getTablaCamposCalculados(request, response, camposCalculados, origenLlamada);

			if (html == null) {
				return null; 
			} else {
				ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents = html.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else
					request.setAttribute("consultaCamposCalculados", html);
			}
		} catch (UnsupportedEncodingException ex) {
			logger.error("doConsulta : UnsupportedEncodingException",ex);
		} catch (IOException ex) {
			logger.error("doConsulta : IOException",ex);
		}catch (Exception ex) {
			logger.error("doConsulta : Exception",ex);
		}
		
		parameters.put("listaCampoOperando", listaCampoOperando);
		mv = new ModelAndView(successView, "camposCalculados", camposCalculados);
		mv.addAllObjects(parameters);
		logger.debug("end - doConsulta en MtoCamposCalculadosController");

		return mv;
	}
	
	/**
	 * Realiza el alta del campo calculado
	 * @param request
	 * @param response
	 * @param camposCalculados Objeto que encapsula el campo calculado a dar de alta
	 * @return ModelAndView que contiene la redirección a la página de mantenimiento de campos calculados
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, CamposCalculados camposCalculados) {
		
		ModelAndView mv = null;
		logger.debug("init - doAlta en MtoCamposCalculadosController");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{	
				if (camposCalculados != null){
					parameters = mtoCamposCalculadosService.altaCamposCalculados(camposCalculados);
				}
				
				mv = doConsulta(request, response, new CamposCalculados()).addAllObjects(parameters);
	
		}
    	catch (Exception e) {
			logger.error("doAlta : error a dar de alta un campo calculado" + e);
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_ALTA_KO));
			mv = doConsulta(request, response, camposCalculados).addAllObjects(parameters);
    	}
		
    	logger.debug("end - doAlta en MtoCamposCalculadosController");
		return mv;
	}
	
	/**
	 * Realiza la modificación del campo calculado
	 * @param request
	 * @param response
	 * @param camposCalculados Objeto que encapsula el campo calculado a modificar
	 * @return ModelAndView que contiene la redirección a la página de mantenimiento de campos calculados
	 */
	public ModelAndView doModificacion(HttpServletRequest request, HttpServletResponse response, CamposCalculados camposCalculados) {
		
		ModelAndView mv = null;
		logger.debug("init - doModificacion en MtoCamposCalculadosController");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{	
				if (camposCalculados != null){
					parameters = mtoCamposCalculadosService.modificarCamposCalculados(camposCalculados);
				}	

				mv = doConsulta(request, response, new CamposCalculados()).addAllObjects(parameters);
	
		}
    	catch (Exception e) {
    		
			logger.error("doModificacion : error a modificar un campo calculado: " + e);
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOCALCULADO_MODIF_KO));
			mv = doConsulta(request, response,camposCalculados).addAllObjects(parameters);

    	}
    	logger.debug("end - doModificacion en MtoCamposCalculadosController");
		return mv;
	}
	
	/**
	 * Realiza la baja del campo calculado
	 * @param request
	 * @param response
	 * @param camposCalculados Objeto que encapsula el campo calculado a dar de baja
	 * @return ModelAndView que contiene la redirección a la página de mantenimiento de campos calculados
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, CamposCalculados camposCalculados) {
		
		ModelAndView mv = null;
		logger.debug("init - doBaja en MtoCamposCalculadosController");
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		try{	
				if (camposCalculados != null){
					parameters = mtoCamposCalculadosService.bajaCamposCalculados(camposCalculados);		
				}	

				mv = doConsulta(request, response, new CamposCalculados()).addAllObjects(parameters);
	
		}
    	catch (Exception e) {
    		
			logger.error("doBaja : Se ha producido un error a dar de baja un campo calculado " + e);
			mv = doConsulta(request, response, camposCalculados).addAllObjects(parameters);
		
    	}
		
    	logger.debug("end - doBaja en MtoCamposCalculadosController");
		return mv;
	}
	
	
	/**
	 * Setter del Service para Spring
	 * @param mtoCamposCalculadosService
	 */
	public void setMtoCamposCalculadosService(IMtoCamposCalculadosService mtoCamposCalculadosService) {
		this.mtoCamposCalculadosService = mtoCamposCalculadosService;
	}
	
	/**
	 * Setter de propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

}
