package com.rsi.agp.core.webapp.action;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.ICargaComisionesManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;

public class CargaComisionesController extends BaseSimpleController implements Controller {
	
	private static final Log LOGGER = LogFactory.getLog(CargaComisionesController.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("displaytag");
	
	private ICargaComisionesManager cargaComisionesManager;
		
	public CargaComisionesController () {
		setCommandClass(Object.class);
	}
	
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, final Object object,
			final BindException exception) throws Exception {
		
		LOGGER.debug("CargaComisionesController - handle - init");
		
		JSONObject resultado = new JSONObject();
		
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			int resultadoCarga = cargaComisionesManager.cargaFichero(request, usuario);
			
			if (resultadoCarga==ICargaComisionesManager.FICHERO_NOT_FOUND) {
				LOGGER.debug("No se encuentra el fichero de comisiones");
				actualizarEstado(request, resultado, Constants.ESTADO_AJAX_DONE, "No se encuentra el fichero de comisiones");
			}
			else if (resultadoCarga==ICargaComisionesManager.FICHERO_CARGADO) {
				LOGGER.debug("El fichero de comisiones mas reciente se ha cargado");
				actualizarEstado(request, resultado, Constants.ESTADO_AJAX_DONE, "El fichero de comisiones mas reciente ya se encuentra cargado");
			}
			else if (resultadoCarga==ICargaComisionesManager.ERROR) {
				LOGGER.error("No se ha podido cargar el fichero");
				request.getSession().setAttribute("progressStatus", Constants.ESTADO_AJAX_ERROR_GENERICO);
			}
			else {
				LOGGER.debug("Fichero importado correctamente");
				actualizarEstado(request, resultado, Constants.ESTADO_AJAX_DONE, "Fichero importado correctamente");				
			}
			
			this.getWriterJSON(response, resultado);
		
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al cargar automaticamente el fichero de comisiones", e);
			request.getSession().setAttribute("progressStatus", Constants.ESTADO_AJAX_ERROR_GENERICO);
		}
		
		LOGGER.debug("CargaComisionesController - handle - end");
		
		return null;

	}
	
	/**
	 * 
	 * @param mensaje 
	 * @param estadoAjax 
	 * @param resultado 
	 * @param request 
	 * @param cargaComisionesManager
	 * @throws JSONException 
	 */
	public void actualizarEstado(HttpServletRequest request, JSONObject resultado, String estadoAjax, String mensaje) throws JSONException {
		
		
		LOGGER.debug("CargaComisionesController - actualizarEstado - init");

		request.getSession().setAttribute("progressStatus", estadoAjax);
		request.getSession().setAttribute("texto", mensaje);
		resultado.put("progressStatus", estadoAjax);
		resultado.put("texto", mensaje);
		
		LOGGER.debug("CargaComisionesController - actualizarEstado - end");

	}

	public void setCargaComisionesManager(
			ICargaComisionesManager cargaComisionesManager) {
		this.cargaComisionesManager = cargaComisionesManager;
	}
}
