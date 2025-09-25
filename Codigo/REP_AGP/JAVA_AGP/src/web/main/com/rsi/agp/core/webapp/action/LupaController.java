package com.rsi.agp.core.webapp.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.managers.impl.OrigenDatosManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.vo.ItemVistaVO;
import com.rsi.agp.vo.ListOrgDatosVO;

public class LupaController extends MultiActionController {
	
	private static final Log LOGGER = LogFactory.getLog(LupaController.class);
	private OrigenDatosManager origenDatosManager;

	public void doDatosVariablesLupa(HttpServletRequest request,HttpServletResponse response) throws Exception {
		LOGGER.debug("[LupaController][doDatosVariablesLupa] - init");
		// Con el listado creamos el JSON que se va a utilizar para pintar la tabla
		JSONArray arrayJSON = new JSONArray();

		// obtengo el listado de registros, sin filtrar ni paginar, se obtienen TODOS.
		String codconcepto = StringUtils.nullToString(request.getParameter("codConcepto")); 
		ListOrgDatosVO listOrgDatosVO = new ListOrgDatosVO();
		
		try{
			listOrgDatosVO = this.origenDatosManager.dameLista(codconcepto);
			
			for(int i = 0; i < listOrgDatosVO.getDatosVO().size();i++){
				JSONObject objetoJSON = new JSONObject();
				ItemVistaVO aux = listOrgDatosVO.getDatosVO().get(i);
                objetoJSON.put("descripcion",  aux.getDescripcion());
				objetoJSON.put("codigo", aux.getCodigo() + "");
				arrayJSON.put(objetoJSON);
			}
			
			
		}catch(Exception ex){
			LOGGER.error("[doDatosVariablesLupa]Fallo al obtener datos de lupa variable Pac.", ex);
		}
		
		getWriterJSON(response, arrayJSON);
		
		LOGGER.debug("[LupaController][doDatosVariablesLupa] - end");
	}   
	
	/**
	 * Metodo que va a ser invocado desde cada controller para estribir una
	 * lista JSON en su response
	 * @param response --> objeto response en el que se va a escribir la lista
	 * @param listaJSON  --> la lista JSON que tiene que ser escrita
	 */
	private void getWriterJSON(HttpServletResponse response, JSONArray listaJSON) {
		LOGGER.debug("init -  getWriterJSON");
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(listaJSON.toString());
			LOGGER.debug("end -  getWriterJSON");
		} catch (IOException e) {
			LOGGER.error("Fallo al escribir la lista en el contexto", e);
		}
	}
	
	public void setOrigenDatosManager(OrigenDatosManager origenDatosManager) {
		this.origenDatosManager = origenDatosManager;
	}
}