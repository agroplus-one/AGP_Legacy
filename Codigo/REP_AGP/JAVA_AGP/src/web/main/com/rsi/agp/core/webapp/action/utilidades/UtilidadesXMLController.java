package com.rsi.agp.core.webapp.action.utilidades;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.utilidades.UtilidadesXMLManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;

public class UtilidadesXMLController extends MultiActionController {

	private static final Log LOGGER = LogFactory.getLog(UtilidadesXMLController.class);

	private String successView;
	private UtilidadesXMLManager utilidadesXMLManager;

	public ModelAndView doConsulta(final HttpServletRequest request, final HttpServletResponse response) {
		LOGGER.debug("init - UtilidadesXMLController - doConsulta");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		String origenLlamada = request.getParameter("origenLlamada");
		String idPolizaStr = request.getParameter("idPoliza");
		String servicioStr = request.getParameter("codServicio");
		try {
			if (StringUtils.isNullOrEmpty(origenLlamada) || !"menuGeneral".equals(origenLlamada)) {
				if (StringUtils.isNullOrEmpty(idPolizaStr) || StringUtils.isNullOrEmpty(servicioStr)) {
					parametros.put("alerta", "No se han recibido todos los par\u00E1metros de entrada.");
				} else {
					String xmlPoliza = this.utilidadesXMLManager.generarXML(Long.valueOf(idPolizaStr), servicioStr);
					parametros.put("xmlPoliza", xmlPoliza);
				}
			}
		} catch (BusinessException e) {
			parametros.put("alerta", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Error en doConsulta de UtilidadesXMLController", e);
			parametros.put("alerta", "Error no esperado. Por favor, contacte con su administrador.");
		}
		mv = new ModelAndView(this.successView);
		mv.addAllObjects(parametros);
		LOGGER.debug("end - UtilidadesXMLController - doConsulta");
		return mv;
	}
	
	public ModelAndView doGetXMLCalculo (final HttpServletRequest request, final HttpServletResponse response) {
		
		LOGGER.debug("UtilidadesXMLController - doGetXMLCalculo [INIT]");
		ModelAndView mv = null;
		String idPoliza = request.getParameter("idPoliza");
		String filaComparativa = request.getParameter("filaComparativa");
		logger.debug("Valor de idPoliza:"+idPoliza);

		String nombreFichero = null;
		
		if (filaComparativa != null) {
			if (filaComparativa.equals("") ) {
				filaComparativa = null;
			}
		}
		
		try {
			logger.debug("Antes de obtener el xml de Calculo");
			String xmlCalculo = this.utilidadesXMLManager.getXMLCalculo(idPoliza, filaComparativa);
			
			if (xmlCalculo != null) {
				
				response.setContentType("text/xml");
				
				if(filaComparativa != null) {
					nombreFichero = "XMLCalculo_" + idPoliza + "_" + filaComparativa + ".xml";
				} else {
					nombreFichero = "XMLCalculo_" + idPoliza + "_0.xml";
				}
				
				response.setHeader("Content-Disposition","attachment; filename=" + nombreFichero);
				response.setHeader("cache-control", "no-cache");
				byte[] fileBytes = xmlCalculo.getBytes();
				ServletOutputStream outs = response.getOutputStream();
				outs.write(fileBytes);
				outs.flush();
				outs.close();
				
			}
			
		} catch (DAOException e) {
			LOGGER.error(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Error en doGetXMLCalculo de UtilidadesXMLController", e);
		}
		
		LOGGER.debug("end - UtilidadesXMLController - doGetXMLCalculo");
		return mv;
	}
	
	public ModelAndView doGetXMLValidacion (final HttpServletRequest request, final HttpServletResponse response) {
		
		LOGGER.debug("init - UtilidadesXMLController - doGetXMLValidacion");
		ModelAndView mv = null;
		String idPoliza = request.getParameter("idPoliza");
		String filaComparativa = request.getParameter("filaComparativa");
		String nombreFichero = null;
		logger.debug("Valor de idPoliza:"+idPoliza);
		
		if (filaComparativa != null) {
			if (filaComparativa.equals("") ) {
				filaComparativa = null;
			}
		}	
		
		try {
			
			logger.debug("Antes de obtener el xml de validacion");
			String xmlValidacion = this.utilidadesXMLManager.getXMLValidacion(idPoliza, filaComparativa);
			
			if (xmlValidacion != null) {
				logger.debug("Se ha obtenido xml de Validación se muestra");
				
				response.setContentType("text/xml");
				if(filaComparativa != null) {
					nombreFichero = "XMLValidacion_" + idPoliza + "_" + filaComparativa + ".xml";
				} else {
					nombreFichero = "XMLValidacion_" + idPoliza + "_0.xml";
				}
				response.setHeader("Content-Disposition","attachment; filename=" + nombreFichero);
				response.setHeader("cache-control", "no-cache");
				byte[] fileBytes = xmlValidacion.getBytes();
				ServletOutputStream outs = response.getOutputStream();
				outs.write(fileBytes);
				outs.flush();
				outs.close();
				
			} 
			
		} catch (DAOException e) {
			LOGGER.error(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Error en doGetXMLValidacion de UtilidadesXMLController", e);
		}
		
		LOGGER.debug("end - UtilidadesXMLController - doGetXMLValidacion");
		return mv;
	}

/* ESC-15883 ** MODIF TAM (16.11.2021) ** Inicio */
/* Comprobamos primero si hay xml que devolver, en caso de que haya se lanza llamada
 * y en caso contrario se muestra mensaje de error por pantalla */
public void doValidarXMLAjax (final HttpServletRequest request, final HttpServletResponse response) {
		
		logger.debug("UtilidadesXMLController - doValidarXMLAjax [INIT]");
		
		String idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
		String filaComparativa = StringUtils.nullToString(request.getParameter("filaComparativa"));
		
		String valor = StringUtils.nullToString(request.getParameter("valor"));
		
		if (filaComparativa.equals("") || filaComparativa ==null) {
			filaComparativa = null;
		}
		
		JSONObject resultado = new JSONObject();
		
		try {
			String xml = null;
		
			logger.debug("Obtenemos el xml para:" + valor);
			if (valor.equals("CALC")) {
				xml = this.utilidadesXMLManager.getXMLCalculo(idPoliza, filaComparativa);
			}else {
				xml = this.utilidadesXMLManager.getXMLValidacion(idPoliza, filaComparativa);
			}
			
			if (xml == null) {
				logger.debug("El valor de xml es nulo");
				resultado.put("alert", "No se ha encontrado XML de Cálculo");
			}
			
		} catch (DAOException e) {
			LOGGER.error(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Error en doGetXMLCalculo de UtilidadesXMLController", e);
		}
		
		logger.debug("UtilidadesXMLController - doValidarXMLAjax [END]");
		getWriterJSON(response, resultado);
	}

	/**
	 * Metodo que va a ser invocado desde cada controller para estribir una lista JSON en su response
	 * @param response --> objeto response en el que se va a escribir la lista
	 * @param listaJSON --> la lista JSON que tiene que ser escrita
	 */
	protected void getWriterJSON(HttpServletResponse response, JSONObject listaJSON){		
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(listaJSON.toString());
		} catch (IOException e) {			
			LOGGER.warn("Fallo al escribir la lista en el contexto", e);
		}
	}

	public String getSuccessView() {
		return this.successView;
	}
	
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setUtilidadesXMLManager(UtilidadesXMLManager utilidadesXMLManager) {
		this.utilidadesXMLManager = utilidadesXMLManager;
	}	
}
