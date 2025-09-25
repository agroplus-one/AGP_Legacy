package com.rsi.agp.core.webapp.action.utilidades;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.CambioModuloManager;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;

public class CambioModuloController extends BaseMultiActionController {

	private static final Log LOGGER = LogFactory.getLog(CambioModuloController.class);

	private CambioModuloManager cambioModuloManager;

	public ModelAndView doCambioModulo(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String[] errorMsgs;
		LOGGER.debug("[doCambioModulo] init");
		JSONObject result = new JSONObject();
		String idPoliza = req.getParameter("idPoliza");
		try {
			if (StringUtils.isNullOrEmpty(idPoliza)) {
				errorMsgs = new String[] { "No se han recibido todos los par\u00E1metros de entrada" };
			} else {
				String realPath = this.getServletContext().getRealPath("/WEB-INF/");
				String moduloDestino = this.cambioModuloManager.cambioModulo(Long.valueOf(idPoliza), realPath);
				result.put("moduloDestino", moduloDestino);
				errorMsgs = new String[] {};
			}
		} catch (BusinessException be) {
			errorMsgs = new String[] { be.getMessage() };
		} catch (Exception e) {
			errorMsgs = new String[] { "Error en el cambio de m\u00F3dulo de la p\u00F3liza" };
		}
		result.put("errorMsgs", new JSONArray(errorMsgs));
		getWriterJSON(res, result);
		LOGGER.debug("[doCambioModulo] end");
		return null;
	}

	public void setCambioModuloManager(final CambioModuloManager cambioModuloManager) {
		this.cambioModuloManager = cambioModuloManager;
	}
}