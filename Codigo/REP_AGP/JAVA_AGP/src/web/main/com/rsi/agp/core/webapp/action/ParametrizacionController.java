package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.config.ConfigAgp;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;

public class ParametrizacionController extends BaseSimpleController implements Controller {

	private ParametrizacionManager parametrizacionManager;

	public ParametrizacionController() {
		super();
		setCommandClass(Parametro.class);
		setCommandName("parametrizacionBean");
	}

	@Override
	protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response, final Object object,
			final BindException exception) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EstadoRenovacionAgroseguro> estadosRenovacion = null;
		List<ConfigAgp> nemosConfigAgp = null;
		Parametro parametro = (Parametro) object;
		final String operacion = request.getParameter("operacion");
		String passwordBuzonInfovia    = StringUtils.nullToString(request.getParameter("passwordBuzonInfovia"));
		
		if ("actualizar".equalsIgnoreCase(operacion)) {
			//Actualizamos los datos en bd.
			parametro.setPasswordBuzonInfovia(passwordBuzonInfovia);
			try {
				this.parametrizacionManager.actualizaParametro(parametro);
				parameters.put("mensaje", "Parametrizaci\u00F3n guardada correctamente.");
			} catch (BusinessException e) {
				parameters.put("alerta", "Error en el guardado de la parametrizaci\u00F3n.");
			}
			
		} else if ("cargaAgpNemo".equalsIgnoreCase(operacion)) {
			String[] errorMsgs;
			String agpNemo = request.getParameter("agpNemo");
			JSONObject result = new JSONObject();
			try {				
				if (StringUtils.isNullOrEmpty(agpNemo)) {
					errorMsgs = new String[] { "No se han recibido todos los par\u00E1metros de esntrada." };
				} else {
					String agpValor = this.parametrizacionManager.getConfigAgpValor(agpNemo);
					result.put("agpValor", agpValor);
					errorMsgs = new String[] {};
				}
			} catch (Exception e) {
				errorMsgs = new String[] { "Error en la obtenci\u00F3n del valor de par\u00E1metro de configuraci\u00F3n." };
			}
			result.put("errorMsgs", new JSONArray(errorMsgs));
			getWriterJSON(response, result);
			return null;
		} else if ("updateAgpValor".equalsIgnoreCase(operacion)) {
			String[] errorMsgs;
			String agpNemo = request.getParameter("agpNemo");
			String agpValor = request.getParameter("agpValor");
			JSONObject result = new JSONObject();
			try {				
				if (StringUtils.isNullOrEmpty(agpNemo) || StringUtils.isNullOrEmpty(agpValor)) {
					errorMsgs = new String[] { "No se han recibido todos los par\u00E1metros de esntrada." };
				} else {
					this.parametrizacionManager.updateConfigAgpValor(agpNemo, agpValor);
					errorMsgs = new String[] {};
				}
			} catch (BusinessException be) {
				errorMsgs = new String[] { be.getMessage() };
			} catch (Exception e) {
				errorMsgs = new String[] { "Error en la obtenci\u00F3n del valor de par\u00E1metro de configuraci\u00F3n." };
			}
			result.put("errorMsgs", new JSONArray(errorMsgs));
			getWriterJSON(response, result);
			return null;	
		} else {
			parametro = parametrizacionManager.getParametro();
			passwordBuzonInfovia = parametro.getPasswordBuzonInfovia();
		}
		//Seleccionamos los estados de renovacion 
		estadosRenovacion = parametrizacionManager.getEstadosRenovacionAgroseguro();		
		//Seleccionamos los nemos de AGP
		nemosConfigAgp = parametrizacionManager.getNemosConfigAgp();
		parameters.put("estadosRenovacion", estadosRenovacion);
		parameters.put("passwordBuzonInfovia", passwordBuzonInfovia);
		parameters.put("nemosConfigAgp", nemosConfigAgp);
		
		return new ModelAndView("moduloTaller/parametrizacion/parametrizacion", "parametrizacionBean", parametro).addAllObjects(parameters);
	}

	public final void setParametrizacionManager(final ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}
}