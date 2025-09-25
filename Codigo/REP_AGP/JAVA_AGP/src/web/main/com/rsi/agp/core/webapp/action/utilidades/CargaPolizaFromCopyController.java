package com.rsi.agp.core.webapp.action.utilidades;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.managers.impl.utilidades.ICargaPolizaFromCopyManager;
import com.rsi.agp.dao.tables.poliza.Poliza;

//ASF - Mejora para crear una póliza a partir de los datos de una copy

public class CargaPolizaFromCopyController extends MultiActionController{
	
	/*
	 * Llamada: http://localhost:8080/rsi_agp/cargaPolizaFromCopyController.html
	 */
	
	private Log logger = LogFactory.getLog(CargaPolizaFromCopyController.class);
	private String successView;
	private ICargaPolizaFromCopyManager cargaPolizaFromCopyManager;
	
	
	/**
	 * Accede a la pantalla de carga de pólizas
	 * @param request
	 * @param response
	 * @param 
	 * @return ModelAndView que contiene la redireccion a la pantalla
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) {
		
		logger.debug("doConsulta - inicio");
		ModelAndView mv = new ModelAndView(successView);
    	mv = new ModelAndView(successView, "polizaBean", new Poliza());
    	logger.debug("doConsulta - fin");
		
		return mv;
	}
	
	public ModelAndView doCargar(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) {
		
		logger.debug("doCargar - inicio");
		
		Long idpoliza = cargaPolizaFromCopyManager.doCargar(polizaBean, this.getServletContext().getRealPath("/WEB-INF/"));

		ModelAndView mv = new ModelAndView(successView);
    	mv = new ModelAndView(successView, "polizaBean",  new Poliza());
    	if (idpoliza != null){
    		mv.addObject("mensaje", "Poliza cargada");
		}
    	else{
    		mv.addObject("alerta", "No se pudo cargar la poliza");
    	}
		
    	logger.debug("doCargar - fin");
		
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
	 * Setter de la propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setCargaPolizaFromCopyManager(
			ICargaPolizaFromCopyManager cargaPolizaFromCopyManager) {
		this.cargaPolizaFromCopyManager = cargaPolizaFromCopyManager;
	}

}
