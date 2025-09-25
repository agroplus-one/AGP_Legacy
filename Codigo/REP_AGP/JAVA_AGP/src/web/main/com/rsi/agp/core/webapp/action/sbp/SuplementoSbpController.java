package com.rsi.agp.core.webapp.action.sbp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.ISbpTxtManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public class SuplementoSbpController extends BaseMultiActionController {
	
	
	private static final String WEB_INF = "/WEB-INF/";
	private static final String ID_POLIZA_SBP = "idPolizaSbp";
	private static final String ALERTA = "alerta";
	private static final String SELEC_PRECIOS_SBP = "moduloSbp/seleccionPreciosSbp";
	private static final String POLIZA_SBP = "polizaSbp";
	private static final String ALTA_SUPLEMENTO = "altaSuplemento";
	private static final String LOGGER_ERROR = "Se ha producido un error: ";
	private static final String PARCELA_SBPS_MOSTRAR = "parcelaSbpsMostrar";
	private static final String SIM_SUPLEMENTO_SOBREPRECIO = "moduloSbp/simulacionSuplementoSbp";
	private static final String VALIDAR_SUPLEMENTO = "validarSuplemento";	
	private static final String REDIRECT_POLIZ_SBP = "redirect:/consultaPolizaSbp.run";
	private static final String BTN_DEFINITIVA = "btnDefinitiva";
	private static final String BTN_CAMBIAR_PRECIO = "btnCambiarPrecio";
	private static final String ID_POL_SBP = "idPolSbp";

	
	private Log logger = LogFactory.getLog(SuplementoSbpController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	private ISimulacionSbpManager simulacionSbpManager;
	private PolizaManager polizaManager;
	private ISbpTxtManager sbpTxtManager;

	/**
	 * 
	 * - Verificamos que estemos en periodo de contratacion y NO haya un suplemento de sobreprecio ya generado sobre esta poliza y que este pendiente de envio/respuesta
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView validaSuplemento(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("SuplementoSbpController - validaSuplemento - init");
		
		JSONObject objeto = new JSONObject();
				
		String idPolizaSbp  = StringUtils.nullToString(request.getParameter(ID_POLIZA_SBP));
		
		try {
			
			objeto.put(VALIDAR_SUPLEMENTO, "");
			
			if (null!=idPolizaSbp && !idPolizaSbp.isEmpty()) {
				
				PolizaSbp polizaSbp = simulacionSbpManager.getPolizaSbp(Long.parseLong(idPolizaSbp));
				
				if (!simulacionSbpManager.isLineaEnPeriodoContratacion(polizaSbp)) {
					objeto.put("validarSuplemento", bundle.getString(ConstantsSbp.ERROR_VAL_FUERA_PER_CONT));
				} else if (!simulacionSbpManager.validarSuplemento(polizaSbp)){
					objeto.put("validarSuplemento", bundle.getString(ConstantsSbp.EXISTE_SUPLEMENTO_PENDIENTE_RESPUESTA));
		    	}
			}
			
		} catch (BusinessException e) {
			logger.warn("Error al validar si el nuevo suplemento cumple los requisitos para ser dado de alta", e);
		} catch (JSONException e) {
			logger.warn("Fallo al buscar el suplento y validar para Sbp ", e);
		}		
		
		this.getWriterJSON(response, objeto);
		
		logger.debug("SuplementoSbpController - validaSuplemento - end");

		return null;
	}
	
	/**
	 * 
	 */
	public ModelAndView doAltaSuplemento(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("SuplementoSbpController - doAltaSuplemento - init");

		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		String idPolizaSbp = request.getParameter(ID_POLIZA_SBP);
		
		PolizaSbp polizaSbp = new PolizaSbp();
		List<ParcelaSbp> lstParSbp = new ArrayList<>();
		
		try {
			polizaSbp = simulacionSbpManager.getPolizaSbp(Long.parseLong(idPolizaSbp));						
			lstParSbp = simulacionSbpManager.obtenerParcelasSituacionActualizadaParaSuplemento(polizaSbp.getPolizaPpal(), realPath);	
			
			if (null!=lstParSbp) {
				logger.debug("Se han recuperado las parcelas de la situacion actualizada");
				
				for (ParcelaSbp parcelaSbp : lstParSbp) {
					logger.debug("PARCELA " + parcelaSbp.getId() + " PRECIO --> " + parcelaSbp.getSobreprecio());
				}
			}
			
			parameters = simulacionSbpManager.getSeleccionPreciosSbp(polizaSbp,realPath,lstParSbp);	
			
		} catch (BusinessException e) {
			logger.warn("Error al dar de alta el suplemento", e);
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ALTA_SOBREPRECIO_KO));
		    return new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
		} catch (Exception e) {
			logger.warn("Error al dar de alta el suplemento", e);
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ALTA_SOBREPRECIO_KO));
		    return new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
		}
		
		if (polizaSbp.getPolizaCpl()== null) {
			polizaSbp.setPolizaCpl(new Poliza());
			
		}
		
		parameters.put(ALTA_SUPLEMENTO, true);
		
		logger.debug("SuplementoSbpController - doAltaSuplemento - end");

		return new ModelAndView(SELEC_PRECIOS_SBP,POLIZA_SBP,polizaSbp).addAllObjects(parameters);
	}
	
	/**
	 * Dados los sobreprecios introducidos por el usuario muestra la pantalla 
	 * de simulacion de un suplemento de sbp
	 * @param request
	 * @param response
	 * @param polizaSbp
	 * @return
	 */
	public ModelAndView doContinuar(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		
		logger.debug("SuplementoSbpController - doContinuar - init");

		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Poliza poliza = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		
		try {
			
			if (polizaSbp.getId()!= null){
				polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			}else{
				String idPolSbp  = StringUtils.nullToString(request.getParameter(ID_POL_SBP));
				polizaSbp = simulacionSbpManager.getPolizaSbp(Long.parseLong(idPolSbp));
			}
			
			if (null!=polizaSbp) {
				
				poliza = polizaManager.getPoliza(polizaSbp.getPolizaPpal().getIdpoliza());	
				
				// Si ya existe el suplemento es que es un cambio de precio
				if (polizaSbp.getTipoEnvio().getId().equals(ConstantsSbp.TIPO_ENVIO_SUPLEMENTO)) {
					mv = recalcularSuplemento(request, poliza, polizaSbp);
				} 
				
				else {
				
					Long idSuplemento = simulacionSbpManager.calculaSuplemento(poliza, polizaSbp.getFechaEnvioSbp(), usuario, realPath, null, false, request);
					
					if (null!=idSuplemento) {
											
						polizaSbp = simulacionSbpManager.getPolizaSbp(idSuplemento);
							
						parametros = sbpTxtManager.getTxtInformePolizaSbp (polizaSbp.getPolizaPpal().getLinea().getCodplan());
						
						if (polizaSbp.getPolizaCpl()== null){
							polizaSbp.setPolizaCpl(new Poliza());
						}
						
						parameters.put(PARCELA_SBPS_MOSTRAR, polizaSbp.getParcelaSbps());						
						parameters.put(BTN_DEFINITIVA, true);
						parameters.put(BTN_CAMBIAR_PRECIO, true);
						parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_ALTA_SUPL_SOBREPRECIO_OK));
	
						mv = new ModelAndView(SIM_SUPLEMENTO_SOBREPRECIO,POLIZA_SBP,polizaSbp).addAllObjects(parameters).addAllObjects(parametros);
					} 
					else {
						parameters.put(POLIZA_SBP, polizaSbp);
						parameters.put(ALERTA, bundle.getObject(ConstantsSbp.NO_HAY_CAMBIOS_EN_LAS_PARCELAS));
						mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
					}
				}
			} else {
				logger.debug("Poliza sbp es null");
			}
			
		} catch (BusinessException e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SUPLEMENTO_SBP));
		    mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
		} catch (Exception e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SUPLEMENTO_SBP));
		    mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
		}
		
		logger.debug("SuplementoSbpController - doContinuar - end");

		return mv;
		
	}
	
	/**
	 * 
	 * @param polizaSbp
	 * @return
	 */
	private ModelAndView recalcularSuplemento(HttpServletRequest request, Poliza poliza, PolizaSbp polizaSbp) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		
		try {
			simulacionSbpManager.recalcularImporteSuplemento(poliza, simulacionSbpManager.getPolizaSbp(polizaSbp.getId()), request);
		} catch (BusinessException e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SUPLEMENTO_SBP));
		    return new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
		}
		parameters.put("mensaje", "Se ha recalculado el importe del suplemento");
		parameters.put(PARCELA_SBPS_MOSTRAR, polizaSbp.getParcelaSbps());
		parameters.put(BTN_DEFINITIVA, true);
		parameters.put(BTN_CAMBIAR_PRECIO, true);
		parametros = sbpTxtManager.getTxtInformePolizaSbp (polizaSbp.getPolizaPpal().getLinea().getCodplan());
		
		return new ModelAndView(SIM_SUPLEMENTO_SOBREPRECIO,POLIZA_SBP,polizaSbp).addAllObjects(parameters).addAllObjects(parametros);
		
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param polizaSbp
	 * @return
	 */
	public ModelAndView doGrabacionDefinitiva (HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		
		logger.debug("SuplementoSbpController - doGrabacionDefinitiva - init");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		try {
						
			polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			simulacionSbpManager.grabacionDefinitivaSbp(polizaSbp, usuario);
			parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_SUPL_GRAB_DEF_OK));

			polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			logger.debug("Parcelas mostrar: " + polizaSbp.getParcelaSbps().size());			
			parameters.put(PARCELA_SBPS_MOSTRAR, polizaSbp.getParcelaSbps());
			
			parametros = sbpTxtManager.getTxtInformePolizaSbp (polizaSbp.getPolizaPpal().getLinea().getCodplan());
			parameters.put(BTN_DEFINITIVA, false);
			parameters.put(BTN_CAMBIAR_PRECIO, false);
			
			
			mv = new ModelAndView(SIM_SUPLEMENTO_SOBREPRECIO,POLIZA_SBP,polizaSbp).addAllObjects(parameters).addAllObjects(parametros);

		
		}catch (BusinessException e) {
			parameters.put(POLIZA_SBP,polizaSbp);
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_SUPL_GRAB_DEF_KO));
			return new ModelAndView(SIM_SUPLEMENTO_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		} catch (Exception e) {
			parameters.put(POLIZA_SBP,polizaSbp);
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_SUPL_GRAB_DEF_KO));
			return new ModelAndView(SIM_SUPLEMENTO_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		}
		
	
		logger.debug("SuplementoSbpController - doGrabacionDefinitiva - end");
		
		return mv;
		
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param polizaSbp
	 * @return
	 */
	public ModelAndView doSalir(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		return new ModelAndView(REDIRECT_POLIZ_SBP);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param polizaSbp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView doCambiarPrecio(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		
		logger.debug("SuplementoSbpController - doCambiarPrecio - init");

		Map<String, Object> parameters = new HashMap<String, Object>();
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		
		try {
			if (polizaSbp.getId()!= null){
				polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			}else{
				String idPolSbp  = StringUtils.nullToString(request.getParameter(ID_POL_SBP));
				polizaSbp = simulacionSbpManager.getPolizaSbp(Long.parseLong(idPolSbp));
			}
			
			/*logger.debug("ID PARCELA ANTES DE CAMBIO DE PRECIO: " + polizaSbp.getId());
			
			List<ParcelaSbp> lstParSbp = new ArrayList<>();
			lstParSbp.addAll(polizaSbp.getParcelaSbps());
			
			for (ParcelaSbp parcelaSbp : lstParSbp) {
				logger.debug("PRECIO PARCELA ACTUAL: " + parcelaSbp.getSobreprecio());
			}
			
			parameters = simulacionSbpManager.getSeleccionPreciosSbp(polizaSbp,realPath,lstParSbp);*/
			
			List<ParcelaSbp> lstParSbp = new ArrayList<>();
			lstParSbp = simulacionSbpManager.obtenerParcelasSituacionActualizadaParaSuplemento(polizaSbp.getPolizaPpal(), realPath);
						
			parameters = simulacionSbpManager.getSeleccionPreciosSbp(polizaSbp, realPath,lstParSbp);
			List<ParcelaSbp> parcelaSbpsMostrar = simulacionSbpManager.getParcelasSimulacion(polizaSbp);
			List<Sobreprecio> sobreprecios = (List<Sobreprecio>)parameters.get("listaSobreprecios");
			List<Sobreprecio> sbpList = simulacionSbpManager.generaSobreprecios (sobreprecios,parcelaSbpsMostrar,parameters);
			
			parameters.put("listaSobreprecios", sbpList);
			
			if (polizaSbp.getPolizaCpl()== null)
				polizaSbp.setPolizaCpl(new Poliza());
			
			parameters.put(ALTA_SUPLEMENTO, true);
			
		} 
		
		catch (BusinessException e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SUPLEMENTO_SBP));
			return new ModelAndView(SIM_SUPLEMENTO_SOBREPRECIO).addAllObjects(parameters);
		}
		catch (Exception e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP,polizaSbp);
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SUPLEMENTO_SBP));
			return new ModelAndView(SIM_SUPLEMENTO_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parameters);
		}
		
		logger.debug("SuplementoSbpController - doCambiarPrecio - end");
		
		return  new ModelAndView(SELEC_PRECIOS_SBP,POLIZA_SBP,polizaSbp).addAllObjects(parameters);

	}
	
	/**
	 * 
	 * @param simulacionSbpManager
	 */
	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}
	
	/**
	 * 
	 * @param polizaManager
	 */
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	/**
	 * 
	 * @param sbpTxtManager
	 */
	public void setSbpTxtManager(ISbpTxtManager sbpTxtManager) {
		this.sbpTxtManager = sbpTxtManager;
	}

}
