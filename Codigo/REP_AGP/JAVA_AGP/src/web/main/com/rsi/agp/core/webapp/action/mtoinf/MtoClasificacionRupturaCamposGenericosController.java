package com.rsi.agp.core.webapp.action.mtoinf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.mtoinf.IMtoClasificacionRupturaCamposGenericosService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoDatosInformeService;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoInformeService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.mtoinf.CampoInforme;
import com.rsi.agp.dao.tables.mtoinf.FormatoCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfClasificacionRuptura;

public class MtoClasificacionRupturaCamposGenericosController extends
		BaseMultiActionController {

	private Log logger = LogFactory
			.getLog(MtoClasificacionRupturaCamposGenericosController.class);
	private IMtoClasificacionRupturaCamposGenericosService mtoClasificacionRupturaCamposGenericosService;
	private IMtoDatosInformeService mtoDatosInformeService;
	private IMtoInformeService mtoInformeService;
	private String successView;

	/**
	 * Realiza la consulta de campos calculados que se ajustan al filtro de
	 * búsqueda
	 * 
	 * @param request
	 * @param response
	 * @param campoCalculadoBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de campos calculados
	 */
	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response,
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura) {

		ModelAndView mv = null;
		logger.debug("init - doConsulta en MtoClasificacionRupturaGenericoController");

		final Map<String, Object> parameters = new HashMap<String, Object>();
		String html = null;
		List<CampoInforme> listaCampoInforme = null;
		
		String redireccion = StringUtils.nullToString(request.getParameter("redireccion"));
		
		parameters.put("recogerInformeSesion", StringUtils.nullToString(request.getParameter("recogerInformeSesion")));
		
		if (redireccion.equals("condiciones")){
			
			parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
			parameters.put("nombre", StringUtils.nullToString(request.getParameter("nombre")));
			parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			mv = new ModelAndView("redirect:/mtoCondicionCampos.run").addAllObjects(parameters);
		}
		else if (redireccion.equals("datoInformes")){
			
			parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
			parameters.put("nombre", StringUtils.nullToString(request.getParameter("nombre")));
			parameters.put("origenLlamada", request.getParameter("origenLlamada"));
			mv = new ModelAndView("redirect:/mtoDatosInforme.run").addAllObjects(parameters);
	
		}
		else if (redireccion.equals("informes")){
		
		parameters.put("idInforme", StringUtils.nullToString(request.getParameter("idInforme")));
		parameters.put("nombre", StringUtils.nullToString(request.getParameter("nombre")));
		parameters.put("origenLlamada", request.getParameter("origenLlamada"));
		mv = new ModelAndView("redirect:/mtoInformes.run").addAllObjects(parameters);
	
	}
	else{
		
		try {
			if(!StringUtils.nullToString(request.getParameter("modificarValidCalculado")).equals("")){
				parameters.put("modificarValidCalculado", request.getParameter("modificarValidCalculado"));
			}
			String origenLlamada = request.getParameter("origenLlamada");
			// recupera la lista de campos de datos de informe

			
			
			if (request.getParameter("idInforme") != null) {
				vistaMtoinfClasificacionRuptura.setIdinforme(new BigDecimal(
						request.getParameter("idInforme")));
				listaCampoInforme = mtoDatosInformeService
						.getListaCampos(new BigDecimal(request
								.getParameter("idInforme")));
				parameters.put("idInforme", request
						.getParameter("idInforme"));

			} else {
				listaCampoInforme = mtoDatosInformeService
						.getListaCampos(vistaMtoinfClasificacionRuptura
								.getIdinforme());

			}
			String ajax = request.getParameter("ajax");

			html = mtoClasificacionRupturaCamposGenericosService
					.getTablaClasificacionRuptura(request, response,
							vistaMtoinfClasificacionRuptura, origenLlamada);

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
					request.setAttribute("mtoConsultaClasificacionRuptura",
							html);
			}
		}
		 catch (UnsupportedEncodingException ex) {
			logger.debug("end - MtoClasificacionRupturaGenericoController :" + ex + "");
		} catch (IOException ex) {
			logger.debug("end - MtoClasificacionRupturaGenericoController :" + ex + "");
		} catch (Exception ex) {
			logger.debug("end - MtoClasificacionRupturaGenericoController:" + ex + "");
		}
		
		// --- carga de la lista de formatos de informe
    	List<FormatoCampoGenerico> lstFormatosInforme = mtoInformeService.getFormatosInforme();
    	parameters.put("lstFormatosInforme", lstFormatosInforme);
    	parameters.put("codFormatoPDF", ConstantsInf.COD_FORMATO_PDF);
    	parameters.put("codOrientacionV", ConstantsInf.COD_ORIENTACION_VERTICAL);
    	
    	// --- carga de la lista de orientaciones del informe
    	List<FormatoCampoGenerico> lstOrientacionesInforme = mtoInformeService.getOrientacionesInforme();
    	parameters.put("lstOrientacionesInforme", lstOrientacionesInforme);
		
		parameters.put("nombre", request.getParameter("nombre"));		
		parameters.put("listaCampo", listaCampoInforme);
		mv = new ModelAndView(successView, "vistaMtoinfClasificacionRuptura",
				vistaMtoinfClasificacionRuptura);
		mv.addAllObjects(parameters);
		}
		logger.debug("end - MtoClasificacionRupturaGenericoController");

		return mv;
	}

	/**
	 * Realiza el alta del campo calculado
	 * 
	 * @param request
	 * @param response
	 * @param campoCalculadoBean
	 *            Objeto que encapsula el campo calculado a dar de alta
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de campos calculados
	 */
	public ModelAndView doAlta(HttpServletRequest request,
			HttpServletResponse response,
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura) {

		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			if (vistaMtoinfClasificacionRuptura != null) {
				parameters = mtoClasificacionRupturaCamposGenericosService.altaClasificacionRupturaGenerico(vistaMtoinfClasificacionRuptura);
			}
			
			mv = doConsulta(request, response, new VistaMtoinfClasificacionRuptura()).addAllObjects(parameters);

		} catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			
			mv = doConsulta(request, response, vistaMtoinfClasificacionRuptura);

		}

		return mv;

	}

	/**
	 * Realiza la modificación del campo calculado
	 * 
	 * @param request
	 * @param response
	 * @param campoCalculadoBean
	 *            Objeto que encapsula el campo calculado a modificar
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de campos calculados
	 */
	public ModelAndView doModificacion(HttpServletRequest request,
			HttpServletResponse response,
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura) {
		
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			if (vistaMtoinfClasificacionRuptura != null) {
				parameters = mtoClasificacionRupturaCamposGenericosService.modificarClasificacionRupturaGenerico(vistaMtoinfClasificacionRuptura);
			}
			mv = doConsulta(request, response, new VistaMtoinfClasificacionRuptura()).addAllObjects(parameters);

		} catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			mv = doConsulta(request, response, vistaMtoinfClasificacionRuptura);
		}
		return mv;
	}

	/**
	 * Realiza la baja del campo calculado
	 * 
	 * @param request
	 * @param response
	 * @param campoCalculadoBean
	 *            Objeto que encapsula el campo calculado a dar de baja
	 * @return ModelAndView que contiene la redirección a la página de
	 *         mantenimiento de campos calculados
	 */
	public ModelAndView doBaja(HttpServletRequest request,
			HttpServletResponse response,
			VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura) {

		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		
	try {
			if (vistaMtoinfClasificacionRuptura != null) {
				parameters = mtoClasificacionRupturaCamposGenericosService.bajaClasificacionRuptura(vistaMtoinfClasificacionRuptura);
			}

			mv = doConsulta(request, response, vistaMtoinfClasificacionRuptura).addAllObjects(parameters);

		} catch (Exception e) {
			logger.error("Se ha producido un error: " + e);
			mv = doConsulta(request, response, vistaMtoinfClasificacionRuptura);
		}

		return mv;

	}

	public IMtoDatosInformeService getMtoDatosInformeService() {
		return mtoDatosInformeService;
	}

	public void setMtoDatosInformeService(
			IMtoDatosInformeService mtoDatosInformeService) {
		this.mtoDatosInformeService = mtoDatosInformeService;
	}

	public IMtoClasificacionRupturaCamposGenericosService getMtoClasificacionRupturaCamposGenericosService() {
		return mtoClasificacionRupturaCamposGenericosService;
	}

	public void setMtoClasificacionRupturaCamposGenericosService(
			IMtoClasificacionRupturaCamposGenericosService mtoClasificacionRupturaCamposGenericosService) {
		this.mtoClasificacionRupturaCamposGenericosService = mtoClasificacionRupturaCamposGenericosService;
	}

	/**
	 * Setter de propiedad para Spring
	 * 
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setMtoInformeService(IMtoInformeService mtoInformeService) {
		this.mtoInformeService = mtoInformeService;
	}

}
