package com.rsi.agp.core.webapp.action.anexoMod;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionBean;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;

public class SolicitudModificacionController extends BaseMultiActionController {
	
	private ISolicitudModificacionManager solicitudModificacionManager;
	private static final Log logger = LogFactory.getLog(SolicitudModificacionController.class);
	
	/**
	 * Llama al SW de solicitud de modificación y escribe en el response un objeto con los datos de la respuesta necesarios para mostrar
	 * la ventana de estado de contratación
	 * @param request
	 * @param response
	 */
	public void doSolicitudModificacion (HttpServletRequest request, HttpServletResponse response) {
		
		// Path real para luego buscar el WSDL de los servicios Web
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		// Obtiene los parámetros plan y referencia necesarios para la llamada al SW
		String referencia = request.getParameter("referencia");
		BigDecimal plan = NumberUtils.formatToNumber(request.getParameter("codPlan"));
		// Obtiene el usuario de la sesión
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		// Si los parámetros son correctos
		SolicitudModificacionBean bean = new SolicitudModificacionBean();
		if (referencia != null && plan != null) {
			// Obtiene el bean resultante de la llamada al SW
			bean = this.solicitudModificacionManager.solicitarModificacion(referencia, plan, realPath, usuario != null ? usuario.getCodusuario() : "");
		}
		
		// Crea un JSONObject a partir del bean y lo escribe en el response
		getWriterJSON(response, new JSONObject(bean));
	}
	
	/**
	 * Llama al SW de anulación del cupón y escribe en el response la respuesta del SW
	 * @param request
	 * @param response
	 */
	public void doAnularCupon (HttpServletRequest request, HttpServletResponse response) {
		
		// Path real para luego buscar el WSDL de los servicios Web
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		// Obtiene los parámetros id e idCupon necesarios para la llamada al SW
		BigDecimal id = NumberUtils.formatToNumber (request.getParameter("id"));
		String idCupon = request.getParameter("idCupon");
		// Obtiene el usuario de la sesión
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		String resultado = null;
		if (idCupon != null) {
			resultado = this.solicitudModificacionManager.anularCupon(id != null ? id.longValue() : null, 
																	  idCupon, realPath, usuario != null ? usuario.getCodusuario() : "");
		}
		
		try {
			JSONObject json = new JSONObject();
			json.put("msg", resultado);
			getWriterJSON(response,  json);
		} catch (JSONException e) {
			logger.error("Ocurrió un error al crear el objeto json a partir de la respuesta del SW de anulación", e);
		}
	}
	
	/**
	 * Setter para Spring
	 * @param solicitudModificacionManager
	 */
	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}		
}
