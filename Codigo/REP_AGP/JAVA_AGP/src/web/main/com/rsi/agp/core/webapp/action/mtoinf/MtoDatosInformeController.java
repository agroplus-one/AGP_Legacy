package com.rsi.agp.core.webapp.action.mtoinf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.mtoinf.IMtoDatosInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoInformeService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.mtoinf.CampoInforme;
import com.rsi.agp.dao.tables.mtoinf.FormatoCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfDatosInformes;

public class MtoDatosInformeController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(MtoDatosInformeController.class);
	private IMtoDatosInformeService mtoDatosInformeService;
	private IMtoInformeService mtoInformeService;
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	private final static String VACIO = "";
	
	/**
	 * Realiza la consulta de datos de informe que se ajustan al filtro de
	 * búsqueda
	 * 
	 * @param request
	 * @param response
	 * @param datoInformes
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de datos de informes
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, VistaMtoinfDatosInformes vistaMtoinfDatosInformes ) {
		
		ModelAndView mv = null;
		logger.debug("init - doConsulta en MtoDatosInformeController");
		
		// Map para guardar los parámetros que se pasarán a la jsp
		final Map<String, Object> parameters = new HashMap<String, Object>();
		
		// Obtiene a qué jsp se va a redirigir
		String redireccion = StringUtils.nullToString(request.getParameter("redireccion"));
		
		parameters.put("recogerInformeSesion", StringUtils.nullToString(request.getParameter("recogerInformeSesion")));
		// Clasificación y ruptura
		if (redireccion.equals("clasificacionYRuptura")){
			
			parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
			parameters.put("nombre", StringUtils.nullToString(request.getParameter("nombre")));
			parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			mv = new ModelAndView("redirect:/mtoClasificacionRuptura.run").addAllObjects(parameters);
			
		}
		// Condiciones de informe
		else if (redireccion.equals("condiciones")){
			
			parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
			parameters.put("nombre", StringUtils.nullToString(request.getParameter("nombre")));
			parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			mv = new ModelAndView("redirect:/mtoCondicionCampos.run").addAllObjects(parameters);
		
		}
		// Informes
		else if (redireccion.equals("informes")){
			
			parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
			parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			mv = new ModelAndView("redirect:/mtoInformes.run").addAllObjects(parameters);
		
		}
		// Datos de informe
		else{
			mv = doConsultaDatosInforme(request, response,	vistaMtoinfDatosInformes, parameters);
		}
		
		logger.debug("end - ConsultaMtoDatosInformeController");
		return mv;

	}


	/**
	 * Consulta el listado de datos del informe seleccionado que se ajustan al filtro de búsqueda introducito
	 * @param request
	 * @param response
	 * @param vistaMtoinfDatosInformes Objeto que encapsula el filtro de búsqueda
	 * @param parameters Mapa de parámetros que se reenviará a la jsp
	 * @return
	 */
	private ModelAndView doConsultaDatosInforme(HttpServletRequest request, HttpServletResponse response, VistaMtoinfDatosInformes vistaMtoinfDatosInformes,
			final Map<String, Object> parameters) {
		
		ModelAndView mv;
		String origenLlamada = request.getParameter("origenLlamada");
		String html = null;
		
		BigDecimal informeId = null;
		List<CampoInforme> listaCampoInforme  = null;
		String ajax = request.getParameter("ajax");
		try {

			if(!VACIO.equals(StringUtils.nullToString(request.getParameter("modificarValidCalculado")))){
				parameters.put("modificarValidCalculado", "true");
			}
		
			listaCampoInforme = mtoDatosInformeService.getListCamposInforme();

			if(vistaMtoinfDatosInformes.getIdinforme() !=null){
			
				html = mtoDatosInformeService.getTablaDatosInforme(request, response,vistaMtoinfDatosInformes ,origenLlamada);
				parameters.put("idInforme", vistaMtoinfDatosInformes.getIdinforme());	
			
			}else if(request.getParameter("idInforme") != null){
			
				informeId = new BigDecimal(request.getParameter("idInforme"));
				vistaMtoinfDatosInformes.setIdinforme(informeId);
				html = mtoDatosInformeService.getTablaDatosInforme(request, response, vistaMtoinfDatosInformes,origenLlamada);
				parameters.put("idInforme", request.getParameter("idInforme"));
				parameters.put("nombre", request.getParameter("nombre"));
			}

			if (html == null) {
				return null; // an export
			} else {
				ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents = html.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else
					// Pasa a la jsp el código de la tabla a través de este
					// atributo
					request.setAttribute("consultaDatosInforme", html);
			}
		} catch (UnsupportedEncodingException ex) {
				
				logger.error("doConsulta : UnsupportedEncodingException",ex);
				mv = new ModelAndView(successView, "vistaMtoinfDatosInformes", vistaMtoinfDatosInformes);
				
		} catch (IOException ex) {
				
				logger.error("doConsulta : IOException",ex);
				mv = new ModelAndView(successView, "vistaMtoinfDatosInformes", vistaMtoinfDatosInformes);
				
		}
		catch (Exception ex) {
				
				logger.error("doConsulta : Exception",ex);
				mv = new ModelAndView(successView, "vistaMtoinfDatosInformes", vistaMtoinfDatosInformes);
				
		}

		// Carga el mapa de parámetros que se reenviará a la jsp
		cargarParametros(request, parameters, listaCampoInforme);
		
		mv = new ModelAndView(successView, "vistaMtoinfDatosInformes", vistaMtoinfDatosInformes);
		mv.addAllObjects(parameters);
		return mv;
	}


	/**
	 * Carga el mapa de parámetros que se reenviará a la jsp
	 * @param request
	 * @param parameters
	 * @param listaCampoInforme
	 */
	private void cargarParametros(HttpServletRequest request, final Map<String, Object> parameters,	List<CampoInforme> listaCampoInforme) {
		// -----------------------------------
    	// -- Carga de la lista de Formatos --
        // -----------------------------------
    	Map<String, String> mapFormatos= new HashMap<String, String>();
    	mapFormatos = mtoDatosInformeService.getMapFormatos();
    	parameters.put("codFormatosFec", mapFormatos.get("codFormatosFec"));
    	parameters.put("formatosFec", mapFormatos.get("formatosFec"));
    	parameters.put("codFormatosNum", mapFormatos.get("codFormatosNum"));
    	parameters.put("formatosNum", mapFormatos.get("formatosNum"));
		
		// --------------------------------------
    	// -- Carga la lista de tipos de campo --
    	// --------------------------------------
    	parameters.put("tipoNumerico", ConstantsInf.CAMPO_TIPO_NUMERICO);
    	parameters.put("tipoFecha", ConstantsInf.CAMPO_TIPO_FECHA);
    	parameters.put("tipoTexto", ConstantsInf.CAMPO_TIPO_TEXTO);
    	
    	// ---------------------------------------------
    	// -- Carga la lista de tipos de totalización --
    	// ---------------------------------------------
    	parameters.put("totalizaNo", ConstantsInf.COD_TOTALIZA_NO);
    	parameters.put("totalizaSuma", ConstantsInf.COD_TOTALIZA_SUMA);
		
    	// ----------------------------------------------
		// -- Carga de la lista de formatos de informe --
    	// ----------------------------------------------    	
    	List<FormatoCampoGenerico> lstFormatosInforme = mtoInformeService.getFormatosInforme();
    	parameters.put("lstFormatosInforme", lstFormatosInforme);
    	parameters.put("codFormatoPDF", ConstantsInf.COD_FORMATO_PDF);
    	parameters.put("codOrientacionV", ConstantsInf.COD_ORIENTACION_VERTICAL);
    	
    	// ----------------------------------------------------
    	// -- Carga de la lista de orientaciones del informe --
    	// ----------------------------------------------------    	
    	List<FormatoCampoGenerico> lstOrientacionesInforme = mtoInformeService.getOrientacionesInforme();
    	parameters.put("lstOrientacionesInforme", lstOrientacionesInforme);
		
		parameters.put("origenLlamada", request.getParameter("origenLlamada"));
		parameters.put("nombre", request.getParameter("nombre"));
		parameters.put("inputFormat", request.getParameter("inputFormat"));
		parameters.put("listaCampoInforme", listaCampoInforme);
	}


	/**
	 * Realiza la bajada de orden en la lista del dato del informe
	 * 
	 * @param request
	 * @param response
	 * @param datoInformes
	 *            Objeto que encapsula el dato del informe a dar de baja
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de datos de informes
	 */
	
	public ModelAndView bajarNivelDatoInformesyActualizar(HttpServletRequest request,
			HttpServletResponse response, VistaMtoinfDatosInformes vistaMtoinfDatosInformes) {
		
		ModelAndView mv = null;
		logger.debug("init - bajarNivelDatoInformesyActualizar en MtoDatosInformeController");
		
	    Map<String, Object> parameters = new HashMap<String, Object>();
		
		try {
		
		if (vistaMtoinfDatosInformes != null) {
			
			parameters = mtoDatosInformeService.bajarNivelDatoInformesyActualizar(vistaMtoinfDatosInformes);
		}
		
		mv = doConsulta(request, response,vistaMtoinfDatosInformes).addAllObjects(parameters);
		logger.debug("end - bajarNivelDatoInformesyActualizar en MtoDatosInformeController");
		
		} catch (Exception e) {
			
		logger.error("Se ha producido un error: " + e);
		parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_MODIF_KO));
		}
	
	return mv;
	}
	
	/**
	 * Realiza la subida de orden en la lista del dato del informe
	 * 
	 * @param request
	 * @param response
	 * @param vistaMtoinfDatosInformes
	 *            Objeto que encapsula el dato del informe a dar de baja
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de datos de informes
	 */
	
	public ModelAndView subirNivelDatoInformesyActualizar(HttpServletRequest request,
			HttpServletResponse response,VistaMtoinfDatosInformes vistaMtoinfDatosInformes) {
	
		ModelAndView mv = null;
		logger.debug("init - subirNivelDatoInformesyActualizar en MtoDatosInformeController");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		try {

			if (vistaMtoinfDatosInformes != null) {
				
				parameters = mtoDatosInformeService.subirNivelDatoInformesyActualizar(vistaMtoinfDatosInformes);
			
			}
		
		mv = doConsulta(request, response,vistaMtoinfDatosInformes).addAllObjects(parameters);
		logger.debug("end - subirNivelDatoInformesyActualizar en MtoDatosInformeController");
		

		} catch (Exception e) {
			
		logger.error("Se ha producido un error: " + e);
		parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_MODIF_KO));
	}
	
	return mv;
	}
	
	
	/**
	 * Realiza la baja del dato del informe
	 * 
	 * @param request
	 * @param response
	 * @param vistaMtoinfDatosInformes
	 *            Objeto que encapsula el dato del informe a dar de baja
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de datos de informes
	 */
	public ModelAndView doBaja(HttpServletRequest request,
			HttpServletResponse response, VistaMtoinfDatosInformes vistaMtoinfDatosInformes)
			throws Exception {
		
		logger.debug("init - doBaja en MtoDatosInformeController");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		try {
			// Baja del dato del informe
			if (vistaMtoinfDatosInformes != null) parameters = mtoDatosInformeService.bajaDatoInformesyActualizar(vistaMtoinfDatosInformes);
			
			// Si el mapa de parámetros está vacío el borrado ha sido correcto, se añade el mensaje correspondiente
			if (parameters.isEmpty()) parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_DATOSINFORME_BAJA_OK));
			
			// Redirección al listado
			mv = doConsulta(request, response, new VistaMtoinfDatosInformes()).addAllObjects(parameters);

			logger.debug("end - doBaja en MtoDatosInformeController");
				
		} catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERT_BORRAR_DATOS_INFORME_KO));

		}

		return mv;

	}
	
	
	/**
	 * Realiza la modificación del dato del informe
	 * 
	 * @param request
	 * @param response
	 * @param vistaMtoinfDatosInformes
	 *            Objeto que encapsula el dato del informe a dar de baja
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de datos de informes
	 */
	public ModelAndView modificarCampo(HttpServletRequest request,
			HttpServletResponse response, VistaMtoinfDatosInformes vistaMtoinfDatosInformes)
			throws Exception {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		logger.debug("init - modificarCampo en MtoDatosInformeController");
		
		
		try {
			if (vistaMtoinfDatosInformes != null) {
				parameters = mtoDatosInformeService.modificarDatoInformes(vistaMtoinfDatosInformes);
				mv = doConsulta(request, response, new VistaMtoinfDatosInformes (vistaMtoinfDatosInformes)).addAllObjects(parameters);
			}
			
			logger.debug("end - modificarCampo en MtoDatosInformeController");
	
		} catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_DATOSINFORME_MODIF_KO));

		}

		return mv;

	}
	
	/**
	 * Realiza el alta del dato del informe
	 * 
	 * @param request
	 * @param response
	 * @param vistaMtoinfDatosInformes
	 *            Objeto que encapsula el dato de informe a dar de alta
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de datos de informes
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, VistaMtoinfDatosInformes vistaMtoinfDatosInformes) {
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{	
			logger.debug("init - doAlta en MtoDatosInformeController");
			
			if (vistaMtoinfDatosInformes != null){
				parameters = mtoDatosInformeService.altaCampoInforme(vistaMtoinfDatosInformes);
				mv = doConsulta(request, response, new VistaMtoinfDatosInformes (vistaMtoinfDatosInformes)).addAllObjects(parameters);
			}	
			
			logger.debug("end - doAlta en MtoDatosInformeController");
		
		}
    	catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			parameters.put("alerta", bundle
					.getObject(ConstantsInf.ALERTA_DATOSINFORME_ALTA_KO));

    	}
		
		return mv;

	}

	/**
	 * Setter de Service para Spring
	 * 
	 * @param mtoDatosInformeService
	 */
	public void setMtoDatosInformeService(
			IMtoDatosInformeService mtoDatosInformeService) {
		this.mtoDatosInformeService = mtoDatosInformeService;
	}


	public String getSuccessView() {
		return successView;
	}


	public void setSuccessView(String successView) {
		this.successView = successView;
	}


	public void setMtoInformeService(IMtoInformeService mtoInformeService) {
		this.mtoInformeService = mtoInformeService;
	}

}
