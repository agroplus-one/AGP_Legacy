package com.rsi.agp.core.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.CargasTablasManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;
import com.rsi.agp.dao.tables.cargas.CargasTablas;

public class CargasTablasController extends BaseMultiActionController{
	
	private Log logger = LogFactory.getLog(CargasTablasController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private String successView;
	private CargasTablasManager cargasTablasManager;
	
	public ModelAndView doGetTablas (HttpServletRequest request, HttpServletResponse response, CargasTablas cargasTablasBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		MultipartFile file= null;
		List<CargasTablas> listTablas = new ArrayList<CargasTablas>();
		
		String idFichero = request.getParameter("idFichero");
		if (!StringUtils.nullToString(request.getAttribute("alerta")).equals("")){
			parameters.put("alerta", (String) request.getAttribute("alerta"));
			idFichero =  (String) request.getAttribute("idFichero");
		}
		try {
			
			file = (MultipartFile) request.getSession().getAttribute("fichtxt");
			listTablas = cargasTablasManager.getTablasAndSave(file,new Long(idFichero));
			
		} catch (BusinessException e) {
			logger.error("Error al obtener las tablas de bb" + e);
		} catch (Exception e) {
			logger.error("Error generico" + e);
		}
		 
		parameters.put("nombreFichero", (file != null) ? file.getOriginalFilename().substring(0,file.getOriginalFilename().length()-4) : "");
		parameters.put("listTablas", listTablas);
		parameters.put("idFichero", idFichero);
		
		return new ModelAndView(successView, "cargasTablasBean", cargasTablasBean).addAllObjects(parameters); 
	}
	
	public ModelAndView doEditarTablas (HttpServletRequest request, HttpServletResponse response, CargasTablas cargasTablasBean){
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		List<CargasTablas> listTablas = new ArrayList<CargasTablas>();
		
		String idFichero = request.getParameter("idFichero");
		
		listTablas = cargasTablasManager.getTablasBBDD(idFichero);
		
		parameters.put("idFichero", idFichero);
		parameters.put("listTablas", listTablas);
		parameters.put("nombreFichero",cargasTablasManager.getNombreFichero(idFichero));
		
		if (StringUtils.nullToString(request.getParameter("modoConsulta")).equals("true")){
			parameters.put("modoConsulta", "true");
		}
				
		return new ModelAndView(successView, "cargasTablasBean", cargasTablasBean).addAllObjects(parameters); 
		
	}
	
	
	public ModelAndView doGuardar (HttpServletRequest request, HttpServletResponse response, CargasTablas cargasTablasBean){
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		ModelAndView mv= null;
		//Tablas seleccionadas por el usuario
		String[] tablasSel = request.getParameterValues("tabla");
		//todas las tablas que vienen de la jsp
		String[] arrayTablas = request.getParameterValues("otraListTablas");
		String idFichero = request.getParameter("idFichero");
		
		try {
			cargasTablasManager.saveTablasSeleccionadas(tablasSel,Long.parseLong(idFichero),arrayTablas);
			request.getSession().removeAttribute("fichtxt");
			parameters.put("origenLlamada", "cargasTablas");
			mv = new ModelAndView("redirect:/cargasFicheros.run").addObject("method", "doConsulta").addAllObjects(parameters);
		
		} catch (Exception e) {
			logger.error("Error al guardar las tablas",e);
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_TABLAS));
			request.setAttribute("idFichero", idFichero);
			mv = doGetTablas(request, response, new CargasTablas());
		}
		return mv;
		
	}
	
	/**
	 * Vuelve a la pantalla de ficheros
	 * 
	 * @param request
	 * @param response
	 * @param cargasFicherosBean
	 * @return
	 */
	public ModelAndView doSalir(HttpServletRequest request,
			HttpServletResponse response, CargasFicheros cargasFicherosBean) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		
		String volver = StringUtils.nullToString(request.getParameter("volver"));
		if(("").equals(volver)){
			parameters.put("origenLlamada", "consulta");
		}
		return new ModelAndView("redirect:/cargasFicheros.run").addObject(
				"method", "doConsulta").addAllObjects(parameters);

	}
	
	public void setSuccessView(String successView) {
		this.successView = successView;
	}


	public void setCargasTablasManager(CargasTablasManager cargasTablasManager) {
		this.cargasTablasManager = cargasTablasManager;
	}


	
}
