package com.rsi.agp.core.webapp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.impl.AseguradoManager;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;

public class ControlAccesoSubvsAsegController extends BaseMultiActionController {

	private static final Log LOGGER = LogFactory.getLog(ControlAccesoSubvsAsegController.class);
	
	private AseguradoManager aseguradoManager;
	
	public void doControlSubvsAsegurado(final HttpServletRequest req, final HttpServletResponse res) {
		LOGGER.debug("Control de acceso de subvenciones del asegurado");
		String html = null;
		JSONObject json = new JSONObject();
		String nifCif 	= req.getParameter("nifCif");
		String codPlan 	= req.getParameter("codPlan");
		String codLinea = req.getParameter("codLinea");
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		try {
			html = this.aseguradoManager.getControlSubvsAseguradoImportes(nifCif, codLinea, codPlan, realPath);
			LOGGER.debug(html.toString());
			json.put("html", html);
			LOGGER.debug("Llamada al SW de Agroseguro realizada con exito");
		} catch (Exception e) {
			LOGGER.debug("Ha habido un error al llamar al SW de Agroseguro", e);
			try {
				json.put("agroMsg", e.getMessage());
			} catch (JSONException e1) {
				LOGGER.debug(e1.getMessage());
			}
		}
		this.getWriterJSON(res, json);
	}
	
	public void setAseguradoManager(AseguradoManager aseguradoManager) {
		this.aseguradoManager = aseguradoManager;
	}
}
