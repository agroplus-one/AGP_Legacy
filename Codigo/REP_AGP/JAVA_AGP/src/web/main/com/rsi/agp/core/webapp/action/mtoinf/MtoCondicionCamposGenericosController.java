package com.rsi.agp.core.webapp.action.mtoinf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.mtoinf.Estados;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoCondicionCamposGenericosService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoDatosInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoInformeService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.CampoInforme;
import com.rsi.agp.dao.tables.mtoinf.FormatoCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.Operador;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfCondiciones;

public class MtoCondicionCamposGenericosController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(MtoCondicionCamposGenericosController.class);
	private IMtoCondicionCamposGenericosService mtoCondicionCamposGenericosService;
	private IMtoDatosInformeService mtoDatosInformeService;
	private IMtoInformeService mtoInformeService;
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");

	/**
	 * Realiza la consulta de condiciones de l informe que se ajustan al filtro de
	 * búsqueda
	 * 
	 * @param request
	 * @param response
	 * @param vistaMtoinfCondiciones
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de condiciones de l informe
	 */
	
	
	
	
	public  ModelAndView doConsulta(HttpServletRequest request,	HttpServletResponse response, VistaMtoinfCondiciones vistaMtoinfCondiciones) {

		ModelAndView mv = null;
		logger.debug("init - doConsulta en MtoDatosInformeController");

		final Map<String, Object> parameters = new HashMap<String, Object>();
		String html = null;
		
		String origenLlamada = request.getParameter("origenLlamada");
		String redireccion = StringUtils.nullToString(request.getParameter("redireccion"));
		
		parameters.put("recogerInformeSesion", StringUtils.nullToString(request.getParameter("recogerInformeSesion")));
		
		if (redireccion.equals("clasificacionYRuptura")){
			
			parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
			parameters.put("nombre", StringUtils.nullToString(request.getParameter("nombre")));
			parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			mv = new ModelAndView("redirect:/mtoClasificacionRuptura.run").addAllObjects(parameters);
			
		}else if (redireccion.equals("datoInformes")){
			
			parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
			parameters.put("nombre", StringUtils.nullToString(request.getParameter("nombre")));
			parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			mv = new ModelAndView("redirect:/mtoDatosInforme.run").addAllObjects(parameters);
			
		}
		else if (redireccion.equals("informes")){
			
			parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
			parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			mv = new ModelAndView("redirect:/mtoInformes.run").addAllObjects(parameters);
			
		}
		else{
			List<CampoInforme> listaCampoInforme  =new ArrayList<CampoInforme>();
			try {
				
				String ajax = request.getParameter("ajax");
				
				if(!StringUtils.nullToString(request.getParameter("modificarValidCalculado")).equals("")){
					parameters.put("modificarValidCalculado", request.getParameter("modificarValidCalculado"));
				}
				
				if(vistaMtoinfCondiciones.getIdinforme() !=null){
					html = mtoCondicionCamposGenericosService.getTablaCondicionInforme(request, response,
					vistaMtoinfCondiciones,origenLlamada);
					listaCampoInforme = mtoDatosInformeService.getListaCampos(vistaMtoinfCondiciones.getIdinforme());
					parameters.put("idInforme", vistaMtoinfCondiciones.getIdinforme());	
		
				}
				else if(request.getParameter("idInforme") != null){
					vistaMtoinfCondiciones.setIdinforme((new BigDecimal(request.getParameter("idInforme"))));
					html = mtoCondicionCamposGenericosService.getTablaCondicionInforme(request, response,
					vistaMtoinfCondiciones,origenLlamada);
					parameters.put("idInforme", request.getParameter("idInforme"));	
					listaCampoInforme = mtoDatosInformeService.getListaCampos(new BigDecimal(request.getParameter("idInforme")));
			
			}
		
				if (html == null) {
					return null; // an export
				} 
				else {
					ajax = request.getParameter("ajax");
				// Llamada desde ajax
						if (ajax != null && ajax.equals("true")) {
							byte[] contents = html.getBytes("UTF-8");
							response.getOutputStream().write(contents);
							return null;
						} else
							request.setAttribute("mtoCondicionesCampos", html);
						}	
			} catch (UnsupportedEncodingException ex) {
			
			mv = new ModelAndView(successView, "vistaMtoinfCondiciones", vistaMtoinfCondiciones);
			mv.addAllObjects(parameters);
			
			} catch (IOException ex) {
			
			mv = new ModelAndView(successView, "vistaMtoinfCondiciones", vistaMtoinfCondiciones);
			mv.addAllObjects(parameters);
		
			} catch (Exception ex) {
			
			mv = new ModelAndView(successView, "vistaMtoinfCondiciones", vistaMtoinfCondiciones);
			mv.addAllObjects(parameters);
			    
		}
		
		// --- carga de la lista de formatos de informe
    	List<FormatoCampoGenerico> lstFormatosInforme = mtoInformeService.getFormatosInforme();
    	parameters.put("lstFormatosInforme", lstFormatosInforme);
    	parameters.put("codFormatoPDF", ConstantsInf.COD_FORMATO_PDF);
    	parameters.put("codOrientacionV", ConstantsInf.COD_ORIENTACION_VERTICAL);
    	
    	// --- carga de la lista de orientaciones del informe
    	List<FormatoCampoGenerico> lstOrientacionesInforme = mtoInformeService.getOrientacionesInforme();
    	parameters.put("lstOrientacionesInforme", lstOrientacionesInforme);
    	
    	// Envía el código que indica que el campo no tiene origen de datos asociado
    	parameters.put("odValorLibre", ConstantsInf.OD_VALOR_LIBRE);
			
		HttpSession session = request.getSession();
    	session.removeAttribute("listaOperador");	
        parameters.put("nombre", request.getParameter("nombre"));	
		parameters.put("listaCampoInforme", listaCampoInforme);
		parameters.put("tipoNumerico", ConstantsInf.CAMPO_TIPO_NUMERICO);
		parameters.put("tipoFecha", ConstantsInf.CAMPO_TIPO_FECHA);
		mv = new ModelAndView(successView);
		mv = new ModelAndView(successView, "vistaMtoinfCondiciones", vistaMtoinfCondiciones);
		mv.addAllObjects(parameters);
		
		}
		 
		
		logger.debug("end - ConsultaMtoDatosInformeController");
		return mv;

	}

	
	/**
	 * Realiza la llamada ajax para recuperar la lista de operadores
	 * búsqueda
	 * 
	 * @param request
	 * @param response
	 */	
	
	public void ajax_getOperador(HttpServletRequest request,HttpServletResponse response){
    	try{
    		
    		JSONObject element      = null;
        	JSONArray list          = new JSONArray();
        	List<Operador>	listaOperadores  = mtoCondicionCamposGenericosService.getListaOperadores(new Integer (request.getParameter("permOcal")),new Long(StringUtils.nullToString(request.getParameter("datoInfoId"))));
        	if(listaOperadores != null){
        	HttpSession session = request.getSession();
        	session.setAttribute("listaOperador", listaOperadores);
    		for(Operador operador : listaOperadores){
    			element = new JSONObject();
    			element.put("value",operador.getValue() + "-" + operador.getIdOperador());
    			element.put("nodeText",operador.getProperty());
    			list.put(element);
    		}
        	}
    		getWriterJSON(response, list);
    	}
    	catch(Exception excepcion){
    		logger.error("Excepcion : MtoCondicionCamposGenericosController - ajax_getOperador", excepcion);
    	}
    }
	
	/**
	 * Devuelve la lista de valores correspondiente al código de origen de datos enviado como parámetro
	 * @param request
	 * @param response
	 */
	public void ajax_getOrigenDatos (HttpServletRequest request,HttpServletResponse response){
		
		// Recibe el código de origen de datos de la jsp
		BigDecimal od;
		try {
			od = new BigDecimal (request.getParameter("od"));
		}
		catch (Exception e1) {
			logger.error("Ocurrio un error al obtener el codigo de origen de datos", e1);
			return;
		}
		
		// Obtiene el listado de objetos IEstados correspondiente al origen de datos
		List<Estados>	listaEstados  = mtoCondicionCamposGenericosService.getListaEstados (od);
		
		// Si la lista de estados es nula no se continúa
		if (listaEstados == null) {
			logger.debug("Ocurrio un error al obtener el listado de estados asociados al origen de datos");
			return;
		}
		
		JSONObject element      = null;
    	JSONArray list          = new JSONArray();
		// Recorre los estados obtenidos y crea el listado de objetos JSON
		for (Estados estados : listaEstados) {
			element = new JSONObject();
			try {
				element.put("idEstado", estados.getIdEstadoInformes());
				element.put("descEstado", estados.getDescEstadoInformes());
			} catch (JSONException e2) {
				logger.error("Ocurrio un error al rellenar el objeto JSON", e2);
			}
			
			list.put(element);
		}
		
		// Escribe la lista JSON en el response
		getWriterJSON(response, list);
	}
	
	/**
	 * Metodo que va a ser invocado desde cada controller para estribir una lista JSON en su response
	 * @param response --> objeto response en el que se va a escribir la lista
	 * @param listaJSON --> la lista JSON que tiene que ser escrita
	 */
	protected void getWriterJSON(HttpServletResponse response, JSONArray listaJSON){		
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(listaJSON.toString());
		} catch (IOException e) {			
			logger.warn("Fallo al escribir la lista en el contexto", e);
		}
	}
	
	/**
	 * Realiza la baja del dato del informe
	 * 
	 * @param request
	 * @param response
	 * @param vistaMtoinfCondiciones
	 *            Objeto que encapsula el dato del informe a dar de baja
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de condiciones de l informe
	 */
	public ModelAndView doBaja(HttpServletRequest request,
			HttpServletResponse response, VistaMtoinfCondiciones vistaMtoinfCondiciones)
			throws Exception {
		
		logger.debug("init - doBaja en MtoDatosInformeController");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		try {

			if (vistaMtoinfCondiciones != null) {
				parameters = mtoCondicionCamposGenericosService.bajaCondicionInforme(vistaMtoinfCondiciones);
			}

			mv = doConsulta(request,response,vistaMtoinfCondiciones).addAllObjects(parameters);
			logger.debug("end - doBaja en MtoDatosInformeController");
				
		} catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CONDICIONINFORME_BAJA_KO));
			mv = doConsulta(request,response,vistaMtoinfCondiciones).addAllObjects(parameters);
		    
		}

		return mv;

	}
	
	
	/**
	 * Realiza la modificación del dato del informe
	 * 
	 * @param request
	 * @param response
	 * @param vistaMtoinfCondiciones
	 *            Objeto que encapsula el dato del informe a dar de baja
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de condiciones de l informe
	 */
	public ModelAndView modificar(HttpServletRequest request,
			HttpServletResponse response, VistaMtoinfCondiciones vistaMtoinfCondiciones)
			throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		

		logger.debug("init - modificarCampo en MtoDatosInformeController");
		
		
		try {
			if (vistaMtoinfCondiciones != null){
			parameters = mtoCondicionCamposGenericosService.modificarCondicionInforme(vistaMtoinfCondiciones);
			
			}
			mv = doConsulta(request,response, new VistaMtoinfCondiciones()).addAllObjects(parameters);
			logger.debug("end - modificarCampo en MtoDatosInformeController");
	
			
		} catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CONDICIONINFORME_MODIF_KO));
			mv = doConsulta(request,response,vistaMtoinfCondiciones).addAllObjects(parameters);
		    
		}

		return mv;

	}
	
	
	
	/**
	 * Realiza el alta del dato del informe
	 * 
	 * @param request
	 * @param response
	 * @param VistaMtoinfCondiciones
	 *            Objeto que encapsula el dato de informe a dar de alta
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de condiciones de l informe
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, VistaMtoinfCondiciones vistaMtoinfCondiciones) {
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		try{
			if (vistaMtoinfCondiciones != null){
					vistaMtoinfCondiciones.getId().setCondid(null);
					parameters = 	mtoCondicionCamposGenericosService.altaCondicionInforme(vistaMtoinfCondiciones);
				}	
				
			mv = doConsulta(request,response, new VistaMtoinfCondiciones()).addAllObjects(parameters);
				
		}
    	catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			mv = doConsulta(request,response,vistaMtoinfCondiciones).addAllObjects(parameters);
			parameters.put("alerta", bundle
					.getObject(ConstantsInf.ALERTA_CONDICIONINFORME_ALTA_KO));
			
    	}
		
		return mv;

	}

	

	/**
	 * Setter del Service para Spring
	 * @param mtoCondicionCamposGenericosService
	 */
	public void setMtoCondicionCamposGenericosService(
			IMtoCondicionCamposGenericosService mtoCondicionCamposGenericosService) {
		this.mtoCondicionCamposGenericosService = mtoCondicionCamposGenericosService;
	}

	/**
	 * Setter de propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}





	public IMtoDatosInformeService getMtoDatosInformeService() {
		return mtoDatosInformeService;
	}





	public void setMtoDatosInformeService(
			IMtoDatosInformeService mtoDatosInformeService) {
		this.mtoDatosInformeService = mtoDatosInformeService;
	}





	public IMtoCondicionCamposGenericosService getMtoCondicionCamposGenericosService() {
		return mtoCondicionCamposGenericosService;
	}


	public void setMtoInformeService(IMtoInformeService mtoInformeService) {
		this.mtoInformeService = mtoInformeService;
	}
	
}
