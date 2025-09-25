package com.rsi.agp.core.managers.confirmarext;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;


public class ContratacionExtConfirmacionController extends BaseMultiActionController{

	private ContratacionExtConfirmacionManager manager;
	private String successView;
	private static final Log logger = LogFactory.getLog(ContratacionExtConfirmacionController.class);
	
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception {
		ModelAndView mv = null;
		logger.debug("init - doAlta en ContratacionExtConfirmacionController");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{	
			
			ModelMap modelMap = new ModelMap(); 
			
			String realPath = this.getServletContext().getRealPath("/WEB-INF/");
			String resultado = procesar(modelMap, realPath);
			parameters.put("listaCampoOperando", resultado);
			mv = new ModelAndView(successView);
			mv.addAllObjects(parameters);
			logger.debug("end - doConsulta en ContratacionExtConfirmacionController");
	
		}
    	catch (Exception e) {
			logger.error("doAlta : error a dar de alta un campo calculado" + e);
    	}
		
    	logger.debug("end - doAlta en ContratacionExtConfirmacionController");
		return mv;
	}
	
	public String procesar(ModelMap map, String realPath) throws ConfirmarExtException.AgrWSException, Exception {
		Base64Binary base64Binary = null;
		File fichero = new File("D:\\Users\\PMARTIN6\\Desktop\\RGA\\WebService\\poliza1.xml");
		byte[] bytesArray = new byte[(int) fichero.length()];
		try (FileInputStream fis = new FileInputStream(fichero)) {
			@SuppressWarnings("unused")
			int len;
			while ((len = fis.read(bytesArray)) > 0) {
				// read file into bytes[]
			}
			base64Binary = new Base64Binary();
			base64Binary.setValue(bytesArray);
		} catch (Exception e) {
			logger.error("Excepcion : ContratacionExtConfirmacionController - procesar", e);
		}
		map.addAttribute("resultado", getManager().doConfirmar(base64Binary, realPath));
		return "resultado";
	}

	public ContratacionExtConfirmacionManager getManager() {
		return manager;
	}

	public void setManager(ContratacionExtConfirmacionManager manager) {
		this.manager = manager;
	}
	
	
	
	
}
