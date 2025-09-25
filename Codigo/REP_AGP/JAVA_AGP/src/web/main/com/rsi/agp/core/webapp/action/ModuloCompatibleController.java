package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.AjaxManager;
import com.rsi.agp.core.managers.impl.ModuloCompatibleManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.cesp.ModuloCompatibleCe;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.poliza.Linea;
	

public class ModuloCompatibleController extends BaseSimpleController implements Controller{
	
	private ModuloCompatibleManager moduloCompatibleManager;
	private static final Log LOGGER = LogFactory.getLog(ModuloCompatibleController.class);
	private AjaxManager ajaxManager;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	
	public ModuloCompatibleController() {
		super();
		setCommandClass(ModuloCompatibleCe.class);
		setCommandName("moduloCompatibleBean"); 

	}	

	protected ModelAndView handle(final HttpServletRequest request, HttpServletResponse response, final Object object,final BindException exception) { 

		final Map<String, Object> parameters = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		ModelAndView mv;
		ModuloCompatibleCe moduloCompatibleCeBean = (ModuloCompatibleCe)object;
		ModuloCompatibleCe moduloCompatibleCeBusqueda = new ModuloCompatibleCe();
		
		String accion = request.getParameter("accion");
		
		if("eliminar".equals(accion)){
			
			moduloCompatibleManager.deleteModuloCompatible(moduloCompatibleCeBean); 
			moduloCompatibleCeBean = new ModuloCompatibleCe();
			parameters.put("mensaje", bundle.getString("mensaje.baja.OK"));
			
			moduloCompatibleCeBean = new ModuloCompatibleCe();
			moduloCompatibleCeBusqueda = new ModuloCompatibleCe();
						
		}else if("alta".equals(accion)){
			
			if (moduloCompatibleManager.existeModuloCompatible(moduloCompatibleCeBean)){
				
				parameters.put("mensaje", bundle.getString("mensaje.alta.duplicado.KO"));
			
			} else{
				try {
					moduloCompatibleManager.saveModuloCompatible(moduloCompatibleCeBean);
					parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
				} catch (DAOException e) {
					parameters.put("mensaje", bundle.getString("mensaje.alta.KO"));
				}
			}
			
			moduloCompatibleCeBusqueda = moduloCompatibleCeBean;
			
		}else if("editarModuloCompatible_ajax".equals(accion)){

			try{	
				String idModuloCompatible = request.getParameter("idModuloCompatible");
				moduloCompatibleCeBean = moduloCompatibleManager.getModuloCompatible(Long.parseLong(idModuloCompatible)); 
				JSONObject moduloCompatibleBeanJSON = new JSONObject();
				moduloCompatibleBeanJSON.put("id", moduloCompatibleCeBean.getId());
				moduloCompatibleBeanJSON.put("plan", moduloCompatibleCeBean.getLinea().getCodplan());
				moduloCompatibleBeanJSON.put("lineaseguroid", moduloCompatibleCeBean.getLinea().getLineaseguroid());
				moduloCompatibleBeanJSON.put("codmoduloppal", moduloCompatibleCeBean.getModuloPrincipal().getId().getCodmodulo());
				moduloCompatibleBeanJSON.put("codmodulocompl", moduloCompatibleCeBean.getModuloComplementario().getId().getCodmodulo());
				moduloCompatibleBeanJSON.put("codriesgocubierto", moduloCompatibleCeBean.getRiesgoCubierto().getId().getCodriesgocubierto());
				
				getWriterJSON(response, moduloCompatibleBeanJSON);
				
				return null;
		
			}catch(Exception e){			
				
				logger.error(e);
				throw new RuntimeException("Se ha producido un error durante la generación y envío del objeto JSON", e);
			}
			
		}else if("modificar".equals(accion)){
			
			if (moduloCompatibleManager.existeModuloCompatible(moduloCompatibleCeBean)){
			
				parameters.put("mensaje", bundle.getString("mensaje.modificacion.duplicado.KO"));
			
			} else{
				try {
					moduloCompatibleManager.saveModuloCompatible(moduloCompatibleCeBean);
					parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
				} catch (DAOException e) {
					parameters.put("mensaje", bundle.getString("mensaje.modificacion.KO"));
				}
			}
			
			moduloCompatibleCeBusqueda = moduloCompatibleCeBean;
		}else if("getLineas_ajax".equals(accion)){
			
			ajax_getLineas(request, response);
			return null;
		
		}else if("getModulos_ajax".equals(accion)){
			
			ajax_getModulos(request, response);
			return null;
		
		}else if("getRiesgos_ajax".equals(accion)){
			
			ajax_getRiesgos(request, response);
			return null;
		}
		
		//consultar y limpiar
		else{
			moduloCompatibleCeBusqueda = moduloCompatibleCeBean;
		}
		List<ModuloCompatibleCe> listModulosCompatibles = moduloCompatibleManager.listModulosCompatibles(moduloCompatibleCeBusqueda);
		List listPlanes = moduloCompatibleManager.getPlanes();
		
		parameters.put("listModulosCompatibles", listModulosCompatibles);
		parameters.put("listPlanes", listPlanes);
		
		mv = new ModelAndView("moduloTaller/condicionesEspeciales/moduloCompatible","moduloCompatibleCeBean", moduloCompatibleCeBean);
		mv.addAllObjects(parameters);
		
		return mv;
		
	}

	
	private void ajax_getLineas(HttpServletRequest request,	HttpServletResponse response) {
		try {
			String idPlan = StringUtils.nullToString(request.getParameter("idPlan"));
			JSONObject element = null;
			JSONArray list = new JSONArray();
			List<Linea> listLineas = moduloCompatibleManager.getLineas(new BigDecimal(idPlan));

			for (Linea linea : listLineas) {
				element = new JSONObject();
				element.put("value", linea.getLineaseguroid());
				element.put("nodeText", linea.getCodlinea()+" - "+linea.getNomlinea());
				list.put(element);
			}
			getWriterJSON(response, list);
		
		} catch (Exception excepcion) {
			logger.error("Excepcion : ModuloCompatibleController - ajax_getLineas", excepcion);
		}
	}
	
    private void ajax_getModulos(HttpServletRequest request,HttpServletResponse response) {
    	String idLinea  = StringUtils.nullToString(request.getParameter("idLinea"));
    	List<Modulo> listaMod   = moduloCompatibleManager.getModulos(new Long(idLinea));
    	JSONObject element = null;
    	JSONArray list     = new JSONArray();
    	
    	try {
    		if (listaMod!= null && listaMod.size() > 0) {
    			for (Modulo oi : listaMod) {
    				element = new JSONObject();
    				element.put("value",oi.getId().getCodmodulo());
    				element.put("nodeText", oi.getId().getCodmodulo()+" - "+oi.getDesmodulo());
    				element.put("ppalComp", oi.getPpalcomplementario().charValue() + "");
    				list.put(element);
    			}
    		}
    		getWriterJSON(response, list);
    		
    	} catch (Exception e) {
    		logger.error("Excepcion : ModuloCompatibleController - ajax_getModulos", e);
	    }
    }
    
    private void ajax_getRiesgos(HttpServletRequest request,HttpServletResponse response) {
    	String codModulo     = StringUtils.nullToString(request.getParameter("codModulo"));
    	String idLinea     = StringUtils.nullToString(request.getParameter("idLinea"));
    	List<RiesgoCubierto> listRiesgoCubierto   = moduloCompatibleManager.getRiesgos(codModulo, new Long(idLinea));
    	JSONObject element = null;
    	JSONArray list     = new JSONArray();
    	
    	try 
    	{
    		if (listRiesgoCubierto!= null && listRiesgoCubierto.size() > 0) 
    		{
    			for (RiesgoCubierto oi : listRiesgoCubierto) 
    			{
    				element = new JSONObject();			
    				element.put("value",oi.getId().getCodriesgocubierto());
    				element.put("nodeText", oi.getDesriesgocubierto());
    				list.put(element);
    			}
    		}
    		getWriterJSON(response, list);
    		
    	} catch (Exception e) {
    		logger.error("Excepcion : ModuloCompatibleController - ajax_getRiesgos", e);
	    }
    }
	
	public void setAjaxManager(AjaxManager ajaxManager) {
		this.ajaxManager = ajaxManager;
	}
	
	public void setModuloCompatibleManager(ModuloCompatibleManager moduloCompatibleManager) {
		this.moduloCompatibleManager = moduloCompatibleManager;
	}
}
