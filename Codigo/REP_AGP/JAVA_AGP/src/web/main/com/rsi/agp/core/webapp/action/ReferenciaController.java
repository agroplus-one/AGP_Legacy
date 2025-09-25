package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.ReferenciaDuplicadaException;
import com.rsi.agp.core.managers.impl.ReferenciaManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.ref.ReferenciaAgricola;

public class ReferenciaController extends BaseMultiActionController  {

	private static final Log logger = LogFactory.getLog(ReferenciaController.class);
	private ReferenciaManager referenciaManager;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public final ModelAndView doConsulta (HttpServletRequest request, HttpServletResponse response, ReferenciaAgricola referenciaBean) throws Exception {
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		final ModelAndView mv = new ModelAndView("moduloTaller/referencia/referencia", "referenciaBean", referenciaBean);
		
		HashMap<String, Object> filtroConsulta = new HashMap<String, Object>();
		
		final String referenciaInicio = request.getParameter("referenciaIni");
		final String referenciaFin = request.getParameter("referenciaFin");
		
		filtroConsulta.put("referenciaIni", referenciaInicio );
		filtroConsulta.put("referenciaFin", referenciaFin );

		parametros.put("refLibres", referenciaManager.getNumRefLibres());
		parametros.put("ultimaRef", referenciaManager.getUltimaRef());
		
		parametros.put("filtro", filtroConsulta);
		
		// Si está informado el parámetro de aviso se pasa a la página el mensaje de carga correcta
		if (!StringUtils.nullToString(request.getParameter("aviso")).equals("")) {
			parametros.put("mensaje", bundle.getString("mensaje.referencia.alta.OK"));
		}
		
		logger.debug("end - doConsulta");
		return mv.addAllObjects(parametros);
	}
	
	
	public final void doGenerar (HttpServletRequest request, HttpServletResponse response) {
		logger.debug("init - doGenerar");
		
		final String referenciaIni = request.getParameter("referenciaIni");
		final String referenciaFin = request.getParameter("referenciaFin");
		
		JSONObject json = new JSONObject();
		
		// Si el intervalo de referencias indicado es válido
		if (isIntervaloValido(referenciaIni, referenciaFin)) {
			
			// Inserta los registros de referencias
			try {
				referenciaManager.insertarReferencias(referenciaIni, referenciaFin);
				putJSON(json, "cod", new Integer(0).toString());
				putJSON(json, "msg", bundle.getString("mensaje.referencia.alta.OK"));
			} catch (ReferenciaDuplicadaException e) {
				logger.error("Referencias ya existentes", e);
				putJSON(json, "cod", new Integer(1).toString());
				putJSON(json, "msg", bundle.getString("mensaje.referencia.duplicada"));
			} catch (Exception e1) {
				logger.error("Ocurrio un error inesperado al insertar las referencias", e1);
				putJSON(json, "cod", new Integer(2).toString());
				putJSON(json, "msg", bundle.getString("mensaje.referencia.KO"));
			}
		}
		else{
			putJSON(json, "cod", new Integer(3).toString());
			putJSON(json, "msg", bundle.getString("mensaje.referencia.entrada.KO"));
		}
		
		
		putJSON(json, "refLibres", referenciaManager.getNumRefLibres().toString());
		putJSON(json, "ultimaRef", referenciaManager.getUltimaRef());
		
		// Escribe el objeto json en el response
		getWriterJSON(response, json);
		
		logger.debug("end - doGenerar");
	}
	
	/**
	 * Escribe en el response el n�mero de referencias insertadas hasta el momento
	 * @param request
	 * @param response
	 */
	public void doAjaxGetInsertados (HttpServletRequest request, HttpServletResponse response) {
			
			// Se escribe en el response la información sobre el proceso de carga de referencias
			JSONObject json = new JSONObject();
			response.setCharacterEncoding("UTF-8");
			putJSON(json, "numInsertados", referenciaManager.getNumInsertados().toString());
			putJSON(json, "finalizado", referenciaManager.isFinalizado().toString());
			// Escribe el objeto json en el response
			getWriterJSON(response, json);
	}
	
	/**
	 * Indica si las referencias de inicio y fin son v�lidas para iniciar la generaci�n
	 * @return
	 */
	private boolean isIntervaloValido (String referenciaIni, String referenciaFin) {
		
		Integer longitudRef = null;
		try {
			longitudRef = Integer.parseInt(bundle.getString("referencia.longitud"));
		} catch (Exception e1) {
			logger.error("Ocurri� un error al obtener del properties la longitud de las referencias", e1);
			return false;
		}
		
		// Comprobamos que hemos recibido ambos c�digos de referencia
		if (!StringUtils.isNullOrEmpty(referenciaIni) && !StringUtils.isNullOrEmpty(referenciaFin)) {
			
			Integer numRefIni = null;
			Integer numRefFin = null;
			
			// Comprobamos que los n�mero de referencia inicial y final son v�lidos
			try {
				numRefIni = Integer.parseInt(referenciaIni.substring(1));
				numRefFin = Integer.parseInt(referenciaFin.substring(1));
			} catch (NumberFormatException e) {
				logger.error("Error al pasar a entero", e);
			}
			
			if (null != numRefIni && null != numRefFin && 
				referenciaIni.length() <= longitudRef && referenciaFin.length() <= longitudRef) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public final void setReferenciaManager(final ReferenciaManager referenciaManager) {
		this.referenciaManager = referenciaManager;
	}

}
