/*
 **************************************************************************************************
 *
 *  CReACION:
 *  ------------
 *
 * REFERENCIA  FECHA       AUTOR             DESCRIPCION
 * ----------  ----------  ----------------  ------------------------------------------------------
 * P000015034              Miguel Granadino  Controlador para relacionCampos.jsp
 *
 **************************************************************************************************
 */

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

import com.rsi.agp.core.managers.impl.AjaxManager;
import com.rsi.agp.core.managers.impl.RelacionCamposManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.config.GrupoFactores;
import com.rsi.agp.dao.tables.config.RelacionCampo;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.org.Ubicacion;
import com.rsi.agp.dao.tables.org.Uso;
import com.rsi.agp.dao.tables.poliza.Linea;

public class RelacionCamposController extends BaseSimpleController implements
		Controller {

	private Map<String, Object> parameters = new HashMap<String, Object>();
	private RelacionCamposManager relacionCamposManager;
	private AjaxManager ajaxManager;
	protected final Log logger = LogFactory.getLog(getClass());
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public RelacionCamposController() {
		setCommandClass(RelacionCampo.class);
		setCommandName("RelacionCampoBean");
	}

	public ModelAndView handle(HttpServletRequest request,HttpServletResponse response, Object object, BindException exception)
			throws Exception {
		ModelAndView mv = null;
		RelacionCampo relacionCampoBean = (RelacionCampo) object;
		String operacion = StringUtils.nullToString(request.getParameter("operacion"));

		parameters.clear();

		try {
			if (operacion.equals("alta")) {
					mv = altaRelacionCampos(request,relacionCampoBean);
			} else if (operacion.equals("baja")) {
				 mv = bajaRelacionCampos(request,relacionCampoBean);
			} else if (operacion.equals("modificacion")) {
				 mv = modificacion(request,relacionCampoBean);
			} else if (operacion.equals("consulta")) {
				 mv = consultaRelacionCampos(request, relacionCampoBean);
			} else if (operacion.equals("editar")) {
				editarRelacionCampos(request,response);				
			} else if(operacion.equals("ajax_getSC")){
				ajax_getSC(request,response);
			}else if(operacion.equals("ajax_getUsos")){
				ajax_getUsos(request,response);
			}else if(operacion.equals("ajax_getUbicaciones")){
				ajax_getUbicaciones(request,response);
			} else if (operacion.equals("ajax_getLineas")) {
				ajax_getLineas(request, response);
			} else if (operacion.equals("ajax_getGrupoFactores")) {
				ajax_getGrupoFactores(request, response);
			} else if (operacion.equals("ajax_getFactoresGrupo")) {
				ajax_getFactoresGrupo(request, response);
			} else {
				 //se quita el id para que no ponga el boton modificar en la jsp
				 relacionCampoBean.setIdrelacion(null);
				mv = loadDefaultData("allRegisters", relacionCampoBean);
			}
		} catch (Exception excepcion) {
			logger.error("Excepcion : RelacionCamposController - handle", excepcion);
		}
		if (mv != null)
			mv.addAllObjects(parameters);

		return mv;
	}	

	
	

	/**
	 * 
	 * @param rows
	 * @return ModelAndView
	 */
	private ModelAndView loadDefaultData(String rows,RelacionCampo relacionCampoBean) {

		List listPlanes = ajaxManager.getPlanes();
		parameters.put("listPlanes", listPlanes);

		List listFactores = relacionCamposManager.getFactores();
		parameters.put("listFactores", listFactores);

		if (rows.equals("allRegisters")) {
			putAllRelacionCampos();
		}else{
			putRelacionCampos(relacionCamposManager.consultaRelacionCampos(relacionCampoBean));
		}

		return new ModelAndView("moduloTaller/relacionCampos/relacionCampos","RelacionCampoBean", relacionCampoBean);
	}

	/**
	 * 
	 * @param HttpServletRequest
	 * @return ModelAndView
	 */
	 private ModelAndView altaRelacionCampos(HttpServletRequest request, RelacionCampo relacionCampoBean){
		 try {
			 RelacionCampo rc = relacionCampoBean;
			 if(relacionCamposManager.existeRelacionCampo(rc)){
				 parameters.put("alerta", bundle.getString("mensaje.alta.duplicado.KO"));
			 }else{
				 relacionCamposManager.saveRelacionCampos(rc);
				 parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
			 }			 
		 }
		 catch(Exception excepcion){
			 logger.error("Excepcion : RelacionCamposController - altaRelacionCampos", excepcion);
		 }
		 return loadDefaultData("noneAllRegisters",relacionCampoBean);
	 }
	/**
	 * 
	 * @param HttpServletRequest
	 * @return ModelAndView
	 */
	 private ModelAndView modificacion(HttpServletRequest request,RelacionCampo relacionCampoBean){
		 try {
			 RelacionCampo rc = relacionCampoBean;
//			 String idRelacionCampo = StringUtils.nullToString(request.getParameter("idRowRelacionCampos"));
//			 if(!idRelacionCampo.equalsIgnoreCase("")){
//				 rc = relacionCamposManager.getRelacionCampo(new Long(idRelacionCampo));
//				 setRelacionCamposFromRequest(request,rc);
				 relacionCamposManager.saveRelacionCampos(rc);
				 parameters.put("mensaje",bundle.getString("mensaje.modificacion.OK"));				
				 
//				 List relacionesCampo=new ArrayList<RelacionCampo>();
//				 relacionesCampo.add(relacionCamposManager.getRelacionCampo(new Long(idRelacionCampo)));
//				 putRelacionCampos(relacionesCampo);			
//			 }
		 }
		 catch(Exception excepcion){
			 logger.error("Excepcion : RelacionCamposController - modificacion", excepcion);
		 }
		 return loadDefaultData("noneAllRegisters",relacionCampoBean);
	 }
	/**
	 * 
	 * @param HttpServletRequest
	 * @return ModelAndView
	 */
	 private ModelAndView bajaRelacionCampos(HttpServletRequest request, RelacionCampo relacionCampoBean){
		 try{	  		
			 relacionCamposManager.deleteRelacionCampos(relacionCampoBean.getIdrelacion());
			 parameters.put("mensaje",bundle.getString("mensaje.baja.OK"));
		 }catch(Exception excepcion){
			    		
			 logger.error("Excepcion : RelacionCamposController - bajaRelacionCampos", excepcion);
					
		 }
		//se quita el id para que no ponga el boton modificar en la jsp
		 relacionCampoBean.setIdrelacion(null);
		 return loadDefaultData("allRegisters",relacionCampoBean);
	 }
	/**
	 * 
	 * @param HttpServletRequest
	 * @return ModelAndView
	 */
	 private ModelAndView consultaRelacionCampos(HttpServletRequest request, RelacionCampo relacionCampoBean){
//		 List list = null;		    	
//		 try{
//			
//			 list = relacionCamposManager.consultaRelacionCampos(relacionCampoBean);
//		 }catch(Exception excepcion){
//			 logger.error(excepcion);
//			 excepcion.printStackTrace();
//		 }		    	   	
//				    	
//		 putRelacionCampos(list);
		 return loadDefaultData("noneAllRegisters",relacionCampoBean);
	 }
	/**
	 * 
	 * @param HttpServletRequest
	 * @return ModelAndView
	 */
	 private void editarRelacionCampos(HttpServletRequest request,HttpServletResponse response){
		 RelacionCampo relacionCampo = relacionCamposManager.getRelacionCampo(new Long(request.getParameter("idRelacionCampo")));
//		 parameters.put("RowRelacionCampo", relacionCampo);
//		 parameters.put("Modif", "modif");
//		 Linea linea = ajaxManager.getPlanLinea(relacionCampo.getLinea().getLineaseguroid());
//		    	
//		 List<Linea> listLineas =ajaxManager.getLineas(relacionCampo.getLinea().getCodplan());
		 JSONObject relacionCampoJSON = new JSONObject();
		 try {
			relacionCampoJSON.put("idrelacioncampo", relacionCampo.getIdrelacion());
			relacionCampoJSON.put("plan", relacionCampo.getLinea().getCodplan());
			relacionCampoJSON.put("linea", relacionCampo.getLinea().getLineaseguroid());
			relacionCampoJSON.put("uso", relacionCampo.getUso().getCoduso());
			relacionCampoJSON.put("ubicacion", relacionCampo.getUbicacion().getCodubicacion());
			relacionCampoJSON.put("sc", relacionCampo.getDiccionarioDatos().getCodconcepto());
			relacionCampoJSON.put("tipo", relacionCampo.getTipocampo());
			relacionCampoJSON.put("calculo", relacionCampo.getProcesocalculo());
			if(relacionCampo.getGrupoFactores()!= null){
				if(relacionCampo.getGrupoFactores().getIdgrupofactores() !=null){
					relacionCampoJSON.put("factor", relacionCampo.getGrupoFactores().getIdgrupofactores());
				}else{
					relacionCampoJSON.put("factor", "");
				}
			}else{
				relacionCampoJSON.put("factor", "");
			}
			
			//pendiente grupo factores
			
			getWriterJSON(response, relacionCampoJSON);
		} catch (JSONException e) {
			logger.error("Excepcion : RelacionCamposController - editarRelacionCampos", e);
		}		 
	 }
	/**
	 * 
	 * @param
	 * @return
	 */
	private void putAllRelacionCampos() {
		List listRelacionCampos = relacionCamposManager.getRelacionCampos();
		parameters.put("listRelacionCampos", listRelacionCampos);
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private void putRelacionCampos(List consulta) {
		parameters.put("listRelacionCampos", consulta);
	}

	/**
	 * 
	 * @param HttpServletRequest
	 *            y HttpServletResponse
	 * @return
	 */
	private void ajax_getGrupoFactores(HttpServletRequest request,HttpServletResponse response) {
		try {
			String camposc = StringUtils.nullToString(request.getParameter("idCampo"));
			JSONArray list = new JSONArray();
			JSONObject element = null;
			DiccionarioDatos dd = (DiccionarioDatos) relacionCamposManager.getGrupoFactores(new BigDecimal(camposc));

			for(GrupoFactores gf: dd.getGrupoFactoreses()){
				element = new JSONObject();
				element.put("value", gf.getIdgrupofactores());
				element.put("nodeText", gf.getDescgrupofactores() );
				list.put(element);
			}			

			getWriterJSON(response, list);
		} catch (Exception excepcion) {
			logger.error("Excepcion : RelacionCamposController - ajax_getGrupoFactores", excepcion);
		}
	}

	/**
	 * 
	 * @param HttpServletRequest
	 *            y HttpServletResponse
	 * @return
	 */
	private void ajax_getFactoresGrupo(HttpServletRequest request,HttpServletResponse response) {

		String factor = StringUtils	.nullToString(request.getParameter("idFactor"));
		JSONArray list = new JSONArray();
		JSONObject element = null;
		GrupoFactores grupo = relacionCamposManager.getFactoresPorGrupo(new BigDecimal(factor));

		for (DiccionarioDatos dd : grupo.getDiccionarioDatoses_1()) {
			element = new JSONObject();
			try {
				element.put("value", dd.getCodconcepto());
				element.put("nodeText", dd.getNomconcepto());
			} catch (JSONException e) {
				logger.error("Excepcion : RelacionCamposController - ajax_getFactoresGrupo", e);
			}

			list.put(element);
		}

		getWriterJSON(response, list);
	}

	private void ajax_getLineas(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			JSONArray list = new JSONArray();
			JSONObject element = null;
			String codplan = StringUtils.nullToString(request
					.getParameter("idPlan"));

			List<Linea> lineas = ajaxManager.getLineas(new BigDecimal(codplan));

			for (Linea linea : lineas) {
				element = new JSONObject();
				element.put("value", linea.getLineaseguroid());
				element.put("nodeText", linea.getCodlinea() + " - "
						+ linea.getNomlinea());
				list.put(element);
			}
			getWriterJSON(response, list);
		} catch (JSONException e) {
			logger.error("Excepcion : RelacionCamposController - ajax_getLineas", e);
		}
	}
	private void ajax_getUsos(HttpServletRequest request,HttpServletResponse response) {
		try{
			JSONArray list = new JSONArray();
			JSONObject element=null;
			String lineaseguroid = StringUtils.nullToString(request.getParameter("idLinea"));
			
			//Filtramos por linea
			List<Uso> usos= relacionCamposManager.getUsos(new Long(lineaseguroid));
			
			for(Uso uso:usos){
				element =new JSONObject();
				element.put("value", uso.getCoduso());
				element.put("nodeText", uso.getDesuso());
				list.put(element);
			}
			getWriterJSON(response, list);
			
		}catch (JSONException e) {
			logger.error("Excepcion : RelacionCamposController - ajax_getUsos", e);
		}		
	}
	private void ajax_getUbicaciones(HttpServletRequest request,HttpServletResponse response) {
		try {
			JSONArray list = new JSONArray();
			JSONObject element = null;
			String lineaseguroid = StringUtils.nullToString(request.getParameter("idLinea"));
			String coduso = StringUtils.nullToString(request.getParameter("idUso"));
			
			//Filtramos por linea,uso
			List<Ubicacion> ubicaciones = relacionCamposManager.getUbicaciones(new Long(lineaseguroid),new Long(coduso)) ;
			
			for(Ubicacion ubicacion:ubicaciones){
				element = new JSONObject();
				element.put("value", ubicacion.getCodubicacion());
				element.put("nodeText", ubicacion.getDesubicacion());
				list.put(element);
			}
			getWriterJSON(response, list);
			
		} catch (JSONException e) {
			logger.error("Excepcion : RelacionCamposController - ajax_getUbicaciones", e);
		}		
	}
	private void ajax_getSC(HttpServletRequest request,HttpServletResponse response) {
		try {
			JSONArray list = new JSONArray();
			JSONObject element= null;
			String lineaseguroid = StringUtils.nullToString(request.getParameter("idLinea"));
			String coduso = StringUtils.nullToString(request.getParameter("idUso"));
			String codubi = StringUtils.nullToString(request.getParameter("idUbi"));
			
			//Filtramos por linea,uso y ubica
			List<OrganizadorInformacion> sc = relacionCamposManager.getCampoSC(new Long(lineaseguroid), new BigDecimal(coduso), new BigDecimal(codubi));
			
			for(OrganizadorInformacion oi:sc){
				element = new JSONObject();
				element.put("value", oi.getDiccionarioDatos().getCodconcepto());
				element.put("nodeText", oi.getDiccionarioDatos().getNomconcepto());
				list.put(element);
			}
			getWriterJSON(response, list);
		} catch (JSONException e) {
			logger.error("Excepcion : RelacionCamposController - ajax_getSC", e);
		}		
	}


	/**
	 * 
	 * @param RelacionCamposManager
	 * @return
	 */
	public void setRelacionCamposManager(
			RelacionCamposManager relacionCamposManager) {
		this.relacionCamposManager = relacionCamposManager;
	}

	/**
	 * 
	 * @param AjaxManager
	 * @return
	 */
	public void setAjaxManager(AjaxManager ajaxManager) {
		this.ajaxManager = ajaxManager;
	}
}