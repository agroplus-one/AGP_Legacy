package com.rsi.agp.core.webapp.action.sbp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.SbpSinParcelasException;
import com.rsi.agp.core.managers.ISbpTxtManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.impl.PolizaComplementariaManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public class SimulacionSbpController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(SimulacionSbpController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	private ISimulacionSbpManager simulacionSbpManager;
	private PolizaManager polizaManager;
	private PolizaComplementariaManager polizaComplementariaManager;
	private ISbpTxtManager sbpTxtManager;
	
	private static final String WEB_INF = "/WEB-INF/";
	private static final String ORIGEN_LLAMADA = "origenLlamada";
	private static final String ID_POLIZA_SBP = "idPolizaSbp";
	private static final String REDIRECT_POLIZ_SBP = "redirect:/consultaPolizaSbp.run";
	private static final String POLIZA_SBP = "polizaSbp";
	private static final String ID_POLIZA = "idPoliza";
	private static final String ALERTA = "alerta";
	private static final String CONSULTA_POLIZAS_SBP = "consultaPolizasParaSbp";
	private static final String RECOGER_POL_SESION = "recogerPolSesion";
	private static final String REDIRECT_POL_SBP = "redirect:/consultaPolSbp.run";
	private static final String POLIZA_BEAN = "polizaBean";
	private static final String SELEC_PRECIOS_SBP = "moduloSbp/seleccionPreciosSbp";
	private static final String LOGGER_ERROR = "Se ha producido un error: ";
	private static final String PARCELA_SBPS_MOSTRAR = "parcelaSbpsMostrar";
	private static final String BTN_CAMBIAR_PRECIO = "btnCambiarPrecio";
	private static final String SIM_SOBREPRECIO = "moduloSbp/simulacionSobreprecio";
	private static final String USUARIO = "usuario";
	private static final String ID_POL_SBP = "idPolSbp";
	private static final String ESTADO_PPAL = "estadoPpal";
	private static final String ESTADO_CPL = "estadoCpl";
	private static final String ORIG_LLAM_LIST_POL_SBP = "origenLlamadaListPolSbp";
	private static final String BTN_DEFINITIVA = "btnDefinitiva";
	private static final String LIST_POLIZAS_SBP = "listadoPolizasSbp";
	private static final String EDIT_LIST_POLIZAS_SBP = "edicionlistadoPolizasSbp";
	
	/**
	 * Realiza el alta del objeto que contiene la poliza de sobreprecio
	 * @param request Objeto request
	 * @param response Objeto response
	 * @param polizaSbp Objeto que encapsula la informacion de la poliza de sobreprecio
	 * @return ModelAndView que contiene la redireccion
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		List<String> errores = new ArrayList<String>();
		Map<String, Object> mapErrores = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Poliza poliza = polizaManager.getPoliza(polizaSbp.getPolizaPpal().getIdpoliza());
		
		logger.debug("**@@** SimulacionSbpController - doAlta [INIT]");
		try {
			
			polizaSbp.setPolizaPpal(poliza);
			if (polizaSbp.getPolizaCpl() !=null){
				if (polizaSbp.getPolizaCpl().getIdpoliza()!= null){
					Poliza polizaCpl = polizaManager.getPoliza(polizaSbp.getPolizaCpl().getIdpoliza());
					polizaSbp.setPolizaCpl(polizaCpl);
				}else{
					Poliza polCpl = new Poliza();
					polizaSbp.setPolizaCpl(polCpl);
				}
			}else{
				Poliza polCpl = new Poliza();
				polizaSbp.setPolizaCpl(polCpl);
			}
			PolizaSbp polSbp = simulacionSbpManager.existePolizaSbp(polizaSbp);
			if (polSbp!= null){ //existe la poliza
				// --- REDIRECCION ALTA SBP ---
				parameters.put(ORIGEN_LLAMADA, request.getParameter(ORIGEN_LLAMADA));
				parameters.put("idPolizaSeleccion", polSbp.getPolizaPpal().getIdpoliza().toString());
				parameters.put(ID_POLIZA_SBP, polSbp.getId());
				mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
			}else{
				mapErrores = simulacionSbpManager.validaPoliza(polizaSbp,false,realPath);
				errores = (List<String>) mapErrores.get("errores");
				/*DNF ESC-8985 19/03/2020*/
				logger.debug("polSbp: " + polSbp);
				logger.debug("doAlta.errores.size(): " + errores.size());
				logger.debug("doAlta.errores: " + errores);
				/*fin DNF ESC-8985 19/03/2020*/
				if (errores.size()>0){
					parameters.put(POLIZA_SBP, polizaSbp);
					parameters.put(ID_POLIZA, poliza.getIdpoliza().toString());
					parameters.put(ALERTA, errores.toString().substring(1, errores.toString().length()-1));
					// ---REDIRECCION ERRORES EN EL ALTA SBP ---
					if (StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)).equals(CONSULTA_POLIZAS_SBP)){
						parameters.put(ORIGEN_LLAMADA, null);
						parameters.put(RECOGER_POL_SESION, "true");
						mv = new ModelAndView(REDIRECT_POL_SBP, POLIZA_BEAN, poliza).addAllObjects(parameters);
					}else if (StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)).equals("utilidadesPoliza")){
						parameters.put("recogerPolizaSesion", "true");
						mv = new ModelAndView("redirect:/utilidadesPoliza.html", POLIZA_BEAN, poliza).addAllObjects(parameters);
					}else {
						mv = new ModelAndView("redirect:/seleccionPoliza.html").addAllObjects(parameters);
					}
				}else{
					
					logger.debug("no hay errores en doAlta...");
					logger.debug("polizaSbp : " + polizaSbp);
					
					// TMR incluimos pantalla de seleccion de precios antes de la simulacion definitiva
					List<ParcelaSbp> lstParSbp = (List<ParcelaSbp>) mapErrores.get("lstParSbp");
					parameters = simulacionSbpManager.getSeleccionPreciosSbp(polizaSbp,realPath,lstParSbp);
					parameters.put(ORIGEN_LLAMADA, StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)));
					
					logger.debug("polizaSbp.getPolizaCpl() : " + polizaSbp.getPolizaCpl());
					
					if (polizaSbp.getPolizaCpl()== null)
						polizaSbp.setPolizaCpl(new Poliza());
					mv = new ModelAndView(SELEC_PRECIOS_SBP,POLIZA_SBP,polizaSbp).addAllObjects(parameters);
				}
			}
			
			
		} catch (BusinessException e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SBP));
			if (StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)).equals(CONSULTA_POLIZAS_SBP)){
				parameters.put(ORIGEN_LLAMADA, null);
				parameters.put(RECOGER_POL_SESION, "true");
				mv = new ModelAndView(REDIRECT_POL_SBP, POLIZA_BEAN, poliza).addAllObjects(parameters);
			}else{			
				parameters.put(POLIZA_SBP, polizaSbp);
				mv = new ModelAndView("moduloPolizas/polizas/seleccion/seleccionPolizas",POLIZA_BEAN, poliza).addAllObjects(parameters);
			}
		}
		return mv;
	}
	
	/**
	 * Dados los sobreprecios introducidos por el usuario muestra la pantalla 
	 * de simulacion de una poliza de sbp
	 * @param request
	 * @param response
	 * @param polizaSbp
	 * @return
	 */
	public ModelAndView doContinuar(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Poliza poliza = polizaManager.getPoliza(polizaSbp.getPolizaPpal().getIdpoliza());
		Boolean editar = false;
		
		try {
			polizaSbp.setPolizaPpal(poliza);
			if (polizaSbp.getPolizaCpl() !=null){
				if (polizaSbp.getPolizaCpl().getIdpoliza()!= null){
					Poliza polizaCpl = polizaManager.getPoliza(polizaSbp.getPolizaCpl().getIdpoliza());
					polizaSbp.setPolizaCpl(polizaCpl);
				}else{
					Poliza polCpl = new Poliza();
					polizaSbp.setPolizaCpl(polCpl);
				}
			}else{
				Poliza polCpl = new Poliza();
				polizaSbp.setPolizaCpl(polCpl);
			}
			
			String lstCodComarcaStr = request.getParameter("lstCodComarcaStr");
			List<BigDecimal> lstCodComarca = new ArrayList<>();
			if (lstCodComarcaStr != null && !lstCodComarcaStr.isEmpty()) {
			    String[] values = lstCodComarcaStr.split(",");
			    for (String value : values) {
			        try {
			            lstCodComarca.add(new BigDecimal(value.trim()));
			        } catch (NumberFormatException e) {
			            // Manejo de la excepci�n si alguno de los valores no es un BigDecimal v�lido
			            e.printStackTrace();
			        }
			    }
			}
			
			int i = 0;
			
			// Iterar sobre los par�metros del formulario
	        Enumeration<String> parameterNames = request.getParameterNames();
	        while (parameterNames.hasMoreElements()) {
	            String paramName = parameterNames.nextElement();

	            // Procesar solo los par�metros que corresponden a sobreprecios
	            if (paramName.startsWith("sbp_")) {
	                String[] parts = paramName.split("_");
	                String codProvincia = parts[1];
	                String codCultivo = parts[2];
	                String sobreprecio = request.getParameter(paramName);
	                
	                // Guardar el sobreprecio en la base de datos
	                simulacionSbpManager.actualizaSobreprecio(new BigDecimal(sobreprecio), new BigDecimal(codCultivo), new BigDecimal(codProvincia), lstCodComarca.get(i));
	                i++;
	            }
	        }
			
			////// Devuelve al usuario a la pantalla dde consulta en caso de exitir ya una poliza
//			PolizaSbp polSbp = simulacionSbpManager.existePolizaSbp(polizaSbp);
//			if (polSbp!= null){ //existe la poliza
//				// --- REDIRECCIA CONSULTA ---
//				parameters.put("idPolizaSeleccion", polSbp.getPolizaPpal().getIdpoliza().toString());
//				parameters.put(ID_POLIZA_SBP, polSbp.getId());
//				return new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
//			}
			
			parameters = simulacionSbpManager.altaPolizaSbp(polizaSbp,realPath,request);
			
			// si la poliza Ppal no tiene estado Provisional, habilitamos el boton de pasar a provisional la Sbp
			if (!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
				parameters.put("btnProvisional", true);
				editar = true;
			}
			
			String callSW = StringUtils.nullToString(parameters.get("callSW"));		
			if (callSW.equals("true") && !editar){
					parameters.put("mensaje",bundle.getObject(ConstantsSbp.MSJ_SOBREPRECIO_SW_OK));
			}else if  (callSW.equals("false") && !editar){
					parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_SOBREPRECIO_SW_KO));
			}	
			
			parameters.put(PARCELA_SBPS_MOSTRAR, simulacionSbpManager.getParcelasSimulacion(polizaSbp));
			parameters.put(BTN_CAMBIAR_PRECIO, true);
			
			//DAA 20/05/2013 Parametrizacion de texto de Simulacion Sbp
			parametros = sbpTxtManager.getTxtInformePolizaSbp (polizaSbp.getPolizaPpal().getLinea().getCodplan());

			parameters.put(ORIGEN_LLAMADA, StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)));
			
			if (polizaSbp.getPolizaCpl()== null){
				polizaSbp.setPolizaCpl(new Poliza());
			}
			mv = new ModelAndView(SIM_SOBREPRECIO,POLIZA_SBP,polizaSbp).addAllObjects(parameters).addAllObjects(parametros);
			
		} catch (BusinessException e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SBP));
		    mv = new ModelAndView("moduloPolizas/polizas/seleccion/seleccionPolizas",POLIZA_BEAN, poliza).addAllObjects(parameters);
		} catch (Exception e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SBP));
		    mv = new ModelAndView("moduloPolizas/polizas/seleccion/seleccionPolizas",POLIZA_BEAN, poliza).addAllObjects(parameters);
		}
		return mv;
		
	}
	/**
	 * Realiza la modificacion del objeto que contiene la poliza de sobreprecio
	 * @param request Objeto request
	 * @param response Objeto response
	 * @param polizaSbp Objeto que encapsula la informacion de la poliza de sobreprecio
	 * @return ModelAndView que contiene la redireccion
	 */
	public ModelAndView doEditar(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		ModelAndView mv= null;
		String idPolSbp  = "";
		try{
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			
			if (polizaSbp.getId()!= null){
				polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			}else{
				idPolSbp  = StringUtils.nullToString(request.getParameter(ID_POL_SBP));
				polizaSbp = simulacionSbpManager.getPolizaSbp(Long.parseLong(idPolSbp));
			}
			
			boolean recalculadoConCpl=false;
			
			// AMG 12-03-2014 al editar ya no recalcula
			/*
			if ("true".equals(recalcularConCpl)){
				// recalculamos Sbp con la Cpl
				parameters = recalculaPolizaSbpConCpl(request, idPolizaPpal, idPolizaCpl, idPolSbp, realPath);
				recalculadoConCpl=true;
				
			}else{
				if (DateUtil.isFechaMenorActual(polizaSbp.getFechaProvisional())){
					//recalculamos el alta
					boolean existe = simulacionSbpManager.hayPolizaCopyNueva(polizaSbp,realPath);
					if (existe){
						
						//recalculamos el alta
						parameters = simulacionSbpManager.recalculaSbp(realPath,polizaSbp,usuario);
						
						if (!StringUtils.nullToString(parameters.get("mensaje")).equals("")){
							parameters.put(ORIGEN_LLAMADA, StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)));
							if (polizaSbp.getPolizaCpl()== null)
								polizaSbp.setPolizaCpl(new Poliza());
							return new ModelAndView(SELEC_PRECIOS_SBP,POLIZA_SBP,parameters.get(POLIZA_SBP)).addAllObjects(parameters);
						}
					}
				}
				
			}
			
			*/
			parameters.put(POLIZA_SBP, polizaSbp);
			
			
			if (!recalculadoConCpl){
				//grabamos la poliza en provisional
				if (!polizaSbp.getEstadoPlzSbp().getIdestado().equals(ConstantsSbp.ESTADO_GRAB_PROV))
					simulacionSbpManager.grabacionProvisionalSbp(polizaSbp, usuario);
		    }
			// pasamos por parametros el estado de la Principal y de la Cpl
			Poliza polizaPpal = polizaSbp.getPolizaPpal();
			String estadoPpal = "";
			String estadoCpl = "";
			Poliza polizaCpl = new Poliza();
			if (polizaSbp.getPolizaCpl() !=null){
				if (polizaSbp.getPolizaCpl().getIdpoliza()!= null){
					polizaCpl = polizaManager.getPoliza(polizaSbp.getPolizaCpl().getIdpoliza());
				}
			}
			estadoPpal = polizaPpal.getEstadoPoliza().getIdestado().toString();
			if (polizaCpl !=null && polizaCpl.getEstadoPoliza() != null && polizaCpl.getEstadoPoliza().getIdestado() !=null){
				estadoCpl = polizaCpl.getEstadoPoliza().getIdestado().toString();
			}
			parameters.put(ESTADO_PPAL,estadoPpal);
			parameters.put(ESTADO_CPL,estadoCpl);
			parameters.put(ORIGEN_LLAMADA, StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)));
			parameters.put(ORIG_LLAM_LIST_POL_SBP, StringUtils.nullToString(request.getParameter(ORIG_LLAM_LIST_POL_SBP)));
			parameters.put(BTN_CAMBIAR_PRECIO, true);
			if (polizaSbp.getEstadoPlzSbp().getIdestado().equals(ConstantsSbp.ESTADO_GRAB_PROV)){
				parameters.put("btnProvisional", false);
				parameters.put(BTN_DEFINITIVA, true);
			}else if (polizaSbp.getEstadoPlzSbp().getIdestado().equals(ConstantsSbp.ESTADO_GRAB_DEF)){
				parameters.put("btnProvisional", true);
				parameters.put(BTN_DEFINITIVA, false);
			}
			
		
			// AMG 12-03-2014 Se elimina este mansaje para el 2014, pues al editar ya no recalcula
			/*
			if ("true".equals(recalcularConCpl)){
				parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_GRAB_DEF_CON_CPL));
				parameters.put("sbpRecalculada","true");
			}
			*/

			//parcelas que se muestarn en la JSP agrupadas solo por cultivo y provincia
			parameters.put(PARCELA_SBPS_MOSTRAR, simulacionSbpManager.getParcelasSimulacion(polizaSbp));
			
			//DAA 20/05/2013 Parametrizacion de texto de Simulacion Sbp
			parametros = sbpTxtManager.getTxtInformePolizaSbp (polizaSbp.getPolizaPpal().getLinea().getCodplan());
			
			if (polizaSbp.getPolizaCpl()== null){
				polizaSbp.setPolizaCpl(new Poliza());
			}
			mv = new ModelAndView(SIM_SOBREPRECIO,POLIZA_SBP,polizaSbp).addAllObjects(parameters).addAllObjects(parametros);
		
		} catch (BusinessException e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_EDITAR_KO));
			mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
		} catch (Exception e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_EDITAR_KO));
			mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
		}
		return mv;
	}
	
	/**
	 * Realiza la modificacion del objeto que contiene la poliza de sobreprecio
	 * @param request Objeto request
	 * @param response Objeto response
	 * @param polizaSbp Objeto que encapsula la informacion de la poliza de sobreprecio
	 * @return ModelAndView que contiene la redireccion
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv= null;
		String origenLlamada =  StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA));
		try{
			Long idPolizaSbp = Long.valueOf(request.getParameter(ID_POL_SBP));
			polizaSbp = simulacionSbpManager.getPolizaSbp(idPolizaSbp);
			if (polizaSbp != null){
				simulacionSbpManager.bajaPolizaSbp(polizaSbp);
				parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_BORRAR_OK));
			}else{
				logger.error("la polizaSbp con idPolizaSb: " + idPolizaSbp + " no existe en BBDD");
				parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_BORRAR_KO));
			}
			// --- REDIRECCION BAJA ---
			if (origenLlamada.equals(CONSULTA_POLIZAS_SBP)){
				if ("true".equals(request.getParameter(RECOGER_POL_SESION))){
					parameters.put(RECOGER_POL_SESION, "true");
				}
				mv = new ModelAndView(REDIRECT_POL_SBP).addAllObjects(parameters);
			}else{
				mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
			}
			
		}catch (BusinessException e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_BORRAR_KO));
			if (origenLlamada.equals(CONSULTA_POLIZAS_SBP)){
				mv = new ModelAndView(REDIRECT_POL_SBP).addAllObjects(parameters);
			}else{
				mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
			}
		}
		return mv;
	}
	
	
	/**
	 * Guarda una poliza de sobreprecio (y sus parcelasSbp) a estado provisional
	 * @param request
	 * @param response
	 * @param polizaSbp
	 * @return
	 */
	public ModelAndView doGrabacionProvisional(HttpServletRequest request, HttpServletResponse response,
			PolizaSbp polizaSbp) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		try{
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			String origenLlamada =  StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA));
			
			if (polizaSbp.getId()!= null){
				polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			}else{
				String idPolSbp  = StringUtils.nullToString(request.getParameter(ID_POL_SBP));
				polizaSbp = simulacionSbpManager.getPolizaSbp(Long.parseLong(idPolSbp));
			}
			//DAA 20/05/2013 Parametrizacion de texto de Simulacion Sbp
			parametros = sbpTxtManager.getTxtInformePolizaSbp (polizaSbp.getPolizaPpal().getLinea().getCodplan());
			
			List<ParcelaSbp> parcelaSbpsMostrar = simulacionSbpManager.getParcelasSimulacion(polizaSbp);
			//TMR Facturacion.Anhadimos el usuario para su posterior facturacion
			simulacionSbpManager.grabacionProvisionalSbp(polizaSbp,usuario);
			
			// pasamos por parametros el estado de la Principal y de la Cpl
			Poliza polizaPpal = polizaManager.getPoliza(polizaSbp.getPolizaPpal().getIdpoliza());
			String estadoPpal = "";
			String estadoCpl = "";
			Poliza polizaCpl = new Poliza();
			if (polizaSbp.getPolizaCpl() !=null){
				if (polizaSbp.getPolizaCpl().getIdpoliza()!= null){
					polizaCpl = polizaManager.getPoliza(polizaSbp.getPolizaCpl().getIdpoliza());
					}
			}else{ // AMG anhado new poliza a la complementaria de sobreprecio
				polizaSbp.setPolizaCpl(polizaCpl);
			}
			estadoPpal = polizaPpal.getEstadoPoliza().getIdestado().toString();
			if (polizaCpl !=null && polizaCpl.getEstadoPoliza() != null && polizaCpl.getEstadoPoliza().getIdestado() !=null){
				estadoCpl = polizaCpl.getEstadoPoliza().getIdestado().toString();
			}
			parameters.put(ESTADO_PPAL,estadoPpal);
			parameters.put(ESTADO_CPL,estadoCpl);
		
			parameters.put(ORIGEN_LLAMADA,origenLlamada);
			parameters.put(POLIZA_SBP,polizaSbp);
			parameters.put(PARCELA_SBPS_MOSTRAR, parcelaSbpsMostrar);
			parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_GRAB_PROV_OK));
			parameters.put(BTN_DEFINITIVA, true);
			parameters.put(BTN_CAMBIAR_PRECIO, true);
			if (polizaSbp.getPolizaCpl()== null){
				polizaSbp.setPolizaCpl(new Poliza());
			}
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
			
		}catch (BusinessException e) {
			parameters.put(POLIZA_SBP,polizaSbp);
			logger.error("Se ha producido un error al pasar a provisional la poliza de sobreprecio.", e);
			try {
				parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_GRAB_PROV_KO));
			} catch (Exception e1) {
				logger.error("Error al obtener la propiedad " + ConstantsSbp.ALERT_GRAB_PROV_KO, e1);
				parameters.put(ALERTA, "Se ha producido un error al pasar a provisional la poliza de sobreprecio");
			}
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		}catch (MissingResourceException e) {
			logger.error("Se ha producido un error al obtener los mensajes de sobreprecio.", e);
			parameters.put(POLIZA_SBP,polizaSbp);
			parameters.put(BTN_DEFINITIVA, true);
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		}catch (Exception e) {
			logger.error("Se ha producido un error al pasar a provisional la poliza de sobreprecio.", e);
			parameters.put(POLIZA_SBP,polizaSbp);
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_GRAB_PROV_KO));
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		}
	}
	/**
	 * Metodo que pasa a definitiva una poliza de sobreprecio
	 * @param request
	 * @param response
	 * @param polizaSbp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView doGrabacionDefinitiva (HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		Map<String,Object> mapErrores = new HashMap<String, Object>();
		List<String> errores = new ArrayList<String>();
		ModelAndView mv = null;
		String estadoPpal = "";
		String estadoCpl = "";
		boolean recalcular = false;
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		try{
			if (polizaSbp.getId()!= null){
				polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			}else{
				Long idPolSbp =Long.valueOf(request.getParameter(ID_POL_SBP));
				polizaSbp = simulacionSbpManager.getPolizaSbp(idPolSbp);
				
			}
			mapErrores = simulacionSbpManager.validaPoliza(polizaSbp,true,realPath);
			errores = (List<String>) mapErrores.get("errores");
			
			/*DNF ESC-8985 19/03/2020*/
			logger.debug("polSbp: " + polizaSbp);
			logger.debug("doGrabacionDefinitiva.errores.size(): " + errores.size());
			logger.debug("doGrabacionDefinitiva.errores: " + errores);
			/*fin DNF ESC-8985 19/03/2020*/
			
			if (errores.size()>0){
				parameters.put(ALERTA,errores.toString().substring(1, errores.toString().length()-1));
				parameters.put(POLIZA_SBP,polizaSbp);
				parameters.put(BTN_DEFINITIVA, true);
				if (polizaSbp.getPolizaCpl()== null){
					polizaSbp.setPolizaCpl(new Poliza());
				}
			}else{
				
				logger.debug("no hay errores en doGrabacionDefinitiva...");
				
				/*ha pasado mas de 1 dia desde la grabacion provisional a la definitiva?*/
				try {
					String callSW = "";
					if (DateUtil.isFechaMenorActual(polizaSbp.getFechaProvisional())){			
						//recalculamos el alta
						logger.debug("# Recalculamos por diferencia de fechas #");
						parameters = simulacionSbpManager.recalculaSbp(realPath,polizaSbp,usuario,true);

						if (!StringUtils.nullToString(parameters.get("mensaje")).equals("")){
							parameters.put(ORIGEN_LLAMADA, StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)));
							if (polizaSbp.getPolizaCpl()== null)
								polizaSbp.setPolizaCpl(new Poliza());
							return new ModelAndView(SELEC_PRECIOS_SBP,POLIZA_SBP,parameters.get(POLIZA_SBP)).addAllObjects(parameters);
						}
						recalcular = true;
					}
					callSW = StringUtils.nullToString(parameters.get("callSW"));		
					if (callSW.equals("false")){ //definitivza KO
						parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_SOBREPRECIO_SW_DEFINITIVA_KO));
					}else if (recalcular){
						parameters.put("mensaje",  bundle.getObject(ConstantsSbp.MSJ_SBP_RECALCULADO));
						parameters.put(BTN_DEFINITIVA, true);
						parameters.put(BTN_CAMBIAR_PRECIO, true);
						estadoPpal = polizaSbp.getPolizaPpal().getEstadoPoliza().getIdestado().toString();
						if (polizaSbp.getPolizaCpl() !=null && polizaSbp.getPolizaCpl().getEstadoPoliza() != null && polizaSbp.getPolizaCpl().getEstadoPoliza().getIdestado() !=null){
							estadoCpl = polizaSbp.getPolizaCpl().getEstadoPoliza().getIdestado().toString();
						}
					}else{ // definitiva OK
						simulacionSbpManager.grabacionDefinitivaSbp(polizaSbp,usuario);
						parameters.put("mensaje", bundle.getObject(ConstantsSbp.MSJ_GRAB_DEF_OK));
					}
					parameters.put(POLIZA_SBP,polizaSbp);
					parameters.put(PARCELA_SBPS_MOSTRAR, simulacionSbpManager.getParcelasSimulacion(polizaSbp));
					//DAA 20/05/2013 Parametrizacion de texto de Simulacion Sbp
					parametros = sbpTxtManager.getTxtInformePolizaSbp (polizaSbp.getPolizaPpal().getLinea().getCodplan());
					
					if (polizaSbp.getPolizaCpl()== null){
						polizaSbp.setPolizaCpl(new Poliza());
					}
				}
				catch (SbpSinParcelasException ssex) {
					parameters.put(ALERTA, bundle.getString(ConstantsSbp.ERROR_SITACT_SIN_PARCELAS));
					parameters.put(POLIZA_SBP,polizaSbp);
					parameters.put(BTN_DEFINITIVA, true);
				}
			}
			// -- REDIRECCIONES GRABACION DEFINITIVA --
			String origenLlamada =  request.getParameter(ORIGEN_LLAMADA);
			if (recalcular){
				parameters.put(ORIGEN_LLAMADA, "edicionlistadoPolizasSbp");
				parameters.put(ESTADO_PPAL, estadoPpal);
				parameters.put(ESTADO_CPL, estadoCpl);
				mv =  new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);				
			}else if (StringUtils.nullToString(origenLlamada).equals("utilidadesPoliza")){
				parameters.put(ORIGEN_LLAMADA, origenLlamada);
				mv =  new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
			}else if (StringUtils.nullToString(origenLlamada).equals(CONSULTA_POLIZAS_SBP)){
				parameters.put(ORIGEN_LLAMADA, origenLlamada);
				mv =  new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
			}else if (StringUtils.nullToString(origenLlamada).equals("edicionlistadoPolizasSbp")){
				parameters.put(ORIGEN_LLAMADA, LIST_POLIZAS_SBP);
				mv =  new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
			}else if (StringUtils.nullToString(origenLlamada).equals(LIST_POLIZAS_SBP)){
				parameters.put(ID_POLIZA_SBP, polizaSbp.getId());
				parameters.put(ORIGEN_LLAMADA, "deGrabacionDefinitiva");
				mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
			}else{
				mv =  new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
			}
			
		}catch (BusinessException e) {
			parameters.put(POLIZA_SBP,polizaSbp);
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_GRAB_DEF_KO));
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		} catch (Exception e) {
			parameters.put(POLIZA_SBP,polizaSbp);
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_GRAB_DEF_KO));
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		}
		return mv;
	}
	
	@SuppressWarnings("unchecked") // recalcular
	public ModelAndView doCambiarPrecio (HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		String origenLlamada =  StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA));
		
		try {
			if (polizaSbp.getId()!= null){
				polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			}else{
				String idPolSbp  = StringUtils.nullToString(request.getParameter(ID_POL_SBP));
				polizaSbp = simulacionSbpManager.getPolizaSbp(Long.parseLong(idPolSbp));
			}
			parameters = simulacionSbpManager.getSeleccionPreciosSbp(polizaSbp, realPath,null);
			List<ParcelaSbp> parcelaSbpsMostrar = simulacionSbpManager.getParcelasSimulacion(polizaSbp);
			List<Sobreprecio> sobreprecios = (List<Sobreprecio>)parameters.get("listaSobreprecios");
			List<Sobreprecio> sbpList = simulacionSbpManager.generaSobreprecios (sobreprecios,parcelaSbpsMostrar,parameters);
			
			parameters.put("listaSobreprecios", sbpList);
			parameters.put(ORIGEN_LLAMADA,origenLlamada);
			
			if (polizaSbp.getPolizaCpl()== null)
				polizaSbp.setPolizaCpl(new Poliza());
			
		} catch (BusinessException e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(POLIZA_SBP, polizaSbp);
			parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SBP));
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters);
		}
		return  new ModelAndView(SELEC_PRECIOS_SBP,POLIZA_SBP,polizaSbp).addAllObjects(parameters);
	}
	
	 public ModelAndView doAnular(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) throws Exception {
			Map<String, Object> parameters = new HashMap<String, Object>();
			ModelAndView mv= null;
			
			try{
				String idPolSbp  = StringUtils.nullToString(request.getParameter(ID_POL_SBP));
				String referencia = StringUtils.nullToString(request.getParameter("referenciaPol"));
				simulacionSbpManager.anulaPoliza (referencia);
				
				parameters.put(ID_POLIZA_SBP, idPolSbp);
				parameters.put("mensaje", bundle.getObject(ConstantsSbp.ALERT_ANULADA_OK));
				
				mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
				
			}catch (BusinessException e) {
				parameters.put(POLIZA_SBP,polizaSbp);
				logger.error(LOGGER_ERROR + e.getMessage());
				parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_ANULADA_KO));
				mv = new ModelAndView(REDIRECT_POLIZ_SBP).addAllObjects(parameters);
			}
			return mv;
		}
	
	public ModelAndView doGrabacionDefinitivaSbpSinCpl (HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		HashMap<String,Object> parametros = new HashMap<String, Object>();
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		PolizaSbp p = new PolizaSbp();
		ModelAndView mv;
		try{
			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			if (polizaSbp.getId()!= null){
				polizaSbp = simulacionSbpManager.getPolizaSbp(polizaSbp.getId());
			}else{
				Long idPolSbp =Long.valueOf(request.getParameter(ID_POL_SBP));
				polizaSbp = simulacionSbpManager.getPolizaSbp(idPolSbp);
			}
			
			//DAA 20/05/2013 Parametrizacion de texto de Simulacion Sbp
			parametros = sbpTxtManager.getTxtInformePolizaSbp (polizaSbp.getPolizaPpal().getLinea().getCodplan());
			
			parameters.put(POLIZA_SBP,(PolizaSbp)parameters.get(POLIZA_SBP));
			//parameters.put(PARCELA_SBPS_MOSTRAR, simulacionSbpManager.getParcelasSimulacion(polizaSbp));
			String estadoPpal = "";
			String estadoCpl = "";
			String origen = StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA));
			logger.debug("origenLlamada: "+origen);
			polizaSbp.setIncSbpComp('N');
			parameters = simulacionSbpManager.recalculaSbp(realPath,polizaSbp,usuario,true);
			
			String callSW = StringUtils.nullToString(parameters.get("callSW"));		
			if (callSW.equals("false")){ //definitivza sin cpl KO
				polizaSbp.setIncSbpComp('S');
				parameters.put(ALERTA,bundle.getObject(ConstantsSbp.ALERT_SOBREPRECIO_SW_DEFINITIVA_KO));
				parameters.put(PARCELA_SBPS_MOSTRAR, simulacionSbpManager.getParcelasSimulacion(polizaSbp));
				mv = new ModelAndView(SIM_SOBREPRECIO,POLIZA_SBP,parameters.get(POLIZA_SBP)).addAllObjects(parameters).addAllObjects(parametros);
				
				if (polizaSbp.getPolizaCpl() !=null){
					if (polizaSbp.getPolizaCpl().getIdpoliza()!= null){
						estadoCpl = polizaSbp.getPolizaCpl().getEstadoPoliza().getIdestado().toString();
					}
				}
				estadoPpal = polizaSbp.getPolizaPpal().getEstadoPoliza().getIdestado().toString();
				
				parameters.put(BTN_DEFINITIVA, true);
				parameters.put(POLIZA_SBP, polizaSbp);
			}else{ // denifitva sin cpl OK	
				polizaSbp.setIncSbpComp('N');
				simulacionSbpManager.saveOrUpdate((PolizaSbp)parameters.get(POLIZA_SBP));
				parameters.put(PARCELA_SBPS_MOSTRAR, simulacionSbpManager.getParcelasSimulacion((PolizaSbp)parameters.get(POLIZA_SBP)));
				if (!StringUtils.nullToString(parameters.get("mensaje")).equals("")){
					parameters.put(ORIGEN_LLAMADA, StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA)));
					mv =  new ModelAndView(SELEC_PRECIOS_SBP,POLIZA_SBP,parameters.get(POLIZA_SBP)).addAllObjects(parameters);
				}else{
					parameters.put("mensaje", bundle.getString(ConstantsSbp.MSJ_GRAB_DEF_SIN_CPL));
					parameters.put(BTN_DEFINITIVA, true);
					mv = new ModelAndView(SIM_SOBREPRECIO,POLIZA_SBP,parameters.get(POLIZA_SBP)).addAllObjects(parameters).addAllObjects(parametros);
				}
				PolizaSbp polRecalculada = (PolizaSbp)parameters.get(POLIZA_SBP);
				
				if (polRecalculada.getPolizaCpl() !=null){
					if (polRecalculada.getPolizaCpl().getIdpoliza()!= null){
						estadoCpl = polRecalculada.getPolizaCpl().getEstadoPoliza().getIdestado().toString();
					}
				}
				estadoPpal = polRecalculada.getPolizaPpal().getEstadoPoliza().getIdestado().toString();
				parameters.put("sbpRecalculada","true");
			}
			
			
			if (polizaSbp.getPolizaCpl()== null)
				polizaSbp.setPolizaCpl(new Poliza());

			parameters.put(ESTADO_PPAL,estadoPpal);
			parameters.put(ESTADO_CPL,estadoCpl);
			String origenLlamada =  request.getParameter(ORIGEN_LLAMADA);
			parameters.put(ORIGEN_LLAMADA, origenLlamada);		
			mv.addAllObjects(parameters);
			return mv;
			
		}catch (BusinessException e) {
			parameters.put(POLIZA_SBP,p);
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SBP));
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		}catch (Exception e) {
			parameters.put(POLIZA_SBP,p);
			logger.error(LOGGER_ERROR + e.getMessage());
			parameters.put(ALERTA, bundle.getObject(ConstantsSbp.ALERT_ERROR_CALCULO_SBP));
			return new ModelAndView(SIM_SOBREPRECIO).addAllObjects(parameters).addAllObjects(parametros);
		}
	}
	
	public ModelAndView doSalir(HttpServletRequest request, HttpServletResponse response, PolizaSbp polizaSbp) {
		ModelAndView mv = null;
		final Map<String, Object> parameters = new HashMap<String, Object>();
		String origenLlamada =  StringUtils.nullToString(request.getParameter(ORIGEN_LLAMADA));
		String origenLlamadaListPolSbp =  StringUtils.nullToString(request.getParameter(ORIG_LLAM_LIST_POL_SBP));
		
		if(origenLlamadaListPolSbp.equals(ORIG_LLAM_LIST_POL_SBP)) {
			origenLlamada = LIST_POLIZAS_SBP;
		}
		
		if (origenLlamada.equals(LIST_POLIZAS_SBP) || origenLlamada.equals(EDIT_LIST_POLIZAS_SBP)){
			String sbpRecalculada =  StringUtils.nullToString(request.getParameter("sbpRecalculada"));
			if ("true".equals(sbpRecalculada)){
				parameters.put("sbpRecalculada", sbpRecalculada);
				parameters.put(ID_POLIZA_SBP, polizaSbp.getId());
			}
			parameters.put(ID_POLIZA_SBP, polizaSbp.getId());
			mv = new ModelAndView(REDIRECT_POLIZ_SBP, parameters); //listadoPolizas de Sbp
		}else {
			mv = new ModelAndView("redirect:/seleccionPoliza.html");
		}
		return mv;
	}
	
	
	
	/**
	 * método para buscar la poliza Cpl a partir de la Ppal y viceversa, y validar la poliza si cumple la linea y cultivo
	 * @param request
	 * @param response
	 * @return mv
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView ajax_buscarPolAsocYValidar(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = null;
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject objeto = new JSONObject();
	    try{
	    	List<Poliza> lstPolizas = new ArrayList<Poliza>();
	    	String idPoliza  = StringUtils.nullToString(request.getParameter(ID_POLIZA));
		    String tipoPoliza  = StringUtils.nullToString(request.getParameter("tipoPoliza"));
		    String validarSbp  = StringUtils.nullToString(request.getParameter("validarSbp"));
		    Poliza polizaPpal = new Poliza();
		    logger.debug("idPoliza: " +idPoliza+ " tipoPoliza: "+tipoPoliza+" validarSbp:"+ validarSbp);
		    objeto.put(ID_POLIZA, "");
    		objeto.put("estado", "");
    		objeto.put("mensajeError", "");
		    
		 // 1 - BUSCAR POLIZA ASOCIADA
    		map = this.buscarPolizaAsociada(idPoliza,tipoPoliza);
    		lstPolizas = (List<Poliza>) map.get("lstPolizas");
	    	if (lstPolizas != null && lstPolizas.size()>0){
	    		objeto.put(ID_POLIZA, lstPolizas.get(0).getIdpoliza().toString());
	    		objeto.put("estado", lstPolizas.get(0).getEstadoPoliza().getIdestado().toString());	
	    	}
	    	// 2 - BUSCAR SI TIENE POLIZA SBP ASOCIADA
	    	objeto.put(ID_POLIZA_SBP, "");
	    	objeto.put("incSbpComp", "");
    		PolizaSbp polSbp = new PolizaSbp();
    		polSbp = (PolizaSbp) map.get(POLIZA_SBP);
    		if (polSbp != null){	//existe la poliza sbp
    			objeto.put(ID_POLIZA_SBP, polSbp.getId());
    			objeto.put("estadoPolSbp", polSbp.getEstadoPlzSbp().getIdestado().toString());
    			if (polSbp.getIncSbpComp().equals('S')){
    				objeto.put("incSbpComp", "true");
    				logger.debug("incSbpComp: true");
    			}
    		}
	    	if ("true".equals(validarSbp)){
	    		
	    		if (polSbp == null){	//existe la poliza sbp
	    			objeto.put(ID_POLIZA_SBP, "");
	    			logger.debug(" Validar poliza ppal");
		    		// 3 - VALIDAR POLIZA PPAL
		    		List<String> errores= new ArrayList<String>();
		    		polizaPpal = (Poliza) map.get("polizaPpal");
		    		if (lstPolizas != null && lstPolizas.size()>0){
		    			logger.debug(" lstPolizas.size: "+lstPolizas.size());
			    		errores = this.validarPolizaPpalParaSbp(lstPolizas.get(0).getIdpoliza().toString(),tipoPoliza,polizaPpal );
		    		}else{
		    			errores = this.validarPolizaPpalParaSbp(null,tipoPoliza,polizaPpal );
		    		}
				    if (errores.size()>0){ // con errores
				    	objeto.put("mensajeError", errores.get(0));
					}
	    		}
	    	}
	    	this.getWriterJSON(response, objeto);
			
		} catch (final JSONException e) {
			logger.warn("Fallo al buscar la poliza asociada y validar para Sbp ", e);
		} catch (Exception e) {
			logger.debug("Se ha producido un error en la busqueda de la poliza asociada y validacion para sbp ", e);
		}
		return mv;		
	}
	/**
	 * método que busca la poliza Cpl a partir de la Ppal y viceversa, Y busca la poliza Sbp asociada
	 * @param idPoliza
	 * @param tipoPoliza
	 * @return
	 */
	private Map<String, Object> buscarPolizaAsociada(String idPoliza, String tipoPoliza){
		List<Poliza> lstPolizas = new ArrayList<Poliza>();
		Map<String, Object> map = new HashMap<String, Object>();
		Poliza polizaPpal = new Poliza();
	    Poliza polizaCpl = new Poliza();
	    if (tipoPoliza.equals("P")){
	    	polizaPpal = polizaManager.getPoliza(Long.parseLong(idPoliza));
	    	lstPolizas = polizaComplementariaManager.getPolizaByTipoRef(polizaPpal, 'C');
	    	map.put("polizaPpal", polizaPpal);
	    	// buscamos si la Principal tiene poliza sbp asociada
	    	PolizaSbp polizaSbp = new PolizaSbp();
	    	polizaSbp.setPolizaPpal(polizaPpal);
	    	try {
				PolizaSbp polSbp = simulacionSbpManager.existePolizaSbp(polizaSbp);
				if (polSbp!= null){	//existe la poliza sbp
					logger.info("PolizaSbp asociada = " + polSbp.getId().toString());
					map.put(POLIZA_SBP, polSbp);
				}
			} catch (BusinessException e) {
				logger.error("Se produjo un error en la búsqueda de la poliza Sbp asociada, " + e.toString());
			}
			
    	}else{ // Cpl
    		polizaCpl = polizaManager.getPoliza(Long.parseLong(idPoliza));
    		lstPolizas = polizaComplementariaManager.getPolizaByTipoRef(polizaCpl, 'P');
    		map.put("polizaCpl", polizaCpl);
    		// buscamos si la Principal tiene poliza sbp asociada
	    	PolizaSbp polizaSbp = new PolizaSbp();
	    	if (lstPolizas != null && lstPolizas.size()>0){
	    		Long idPolPpal = Long.parseLong(lstPolizas.get(0).getIdpoliza().toString());
		    	polizaPpal = polizaManager.getPoliza(idPolPpal);
		    	polizaSbp.setPolizaPpal(polizaPpal);
		    	try {
					PolizaSbp polSbp = simulacionSbpManager.existePolizaSbp(polizaSbp);
					if (polSbp!= null){	//existe la poliza sbp
						logger.info("PolizaSbp asociada = " + polSbp.getId().toString());
						map.put(POLIZA_SBP, polSbp);
					}
				} catch (BusinessException e) {
					logger.error("Se produjo un error en la búsqueda de la póliza Sbp asociada, " + e.toString());
				}
	    	}
    	}
	    map.put("lstPolizas",lstPolizas);
		return map;
	}
	
	/**
	 * método para validar si la poliza principal cumple los requisitos de línea y cultivo para Sbp
	 * @param idPoliza
	 * @param tipoPoliza
	 * @param polizaPpal
	 * @return
	 */
	private List<String> validarPolizaPpalParaSbp(String idPoliza, String tipoPoliza, Poliza polizaPpal){
		List<String> errores= new ArrayList<String>();
		logger.debug(" idPoliza: "+idPoliza + " tipoPoliza:"+tipoPoliza);
		if (tipoPoliza.equals("C")){
			polizaPpal = polizaManager.getPoliza(Long.parseLong(idPoliza));
		}
		try {
			errores = simulacionSbpManager.validarPolizaPpalParaSbp(polizaPpal);
		} catch (BusinessException e) {
			logger.warn("error al validar la poliza principal si cumple los requisitos de linea y cultivo para Sbp", e);
		}	
	    return errores;
	}
	
	/**
	 * Método que actualiza la poliza de sobreprecio con la Complementaria
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	public Map<String, Object> recalculaPolizaSbpConCpl(HttpServletRequest request,String idPolizaPpal, String idPolizaCpl,
							String idPolSbp, String realPath) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		Poliza polizaPpal = polizaManager.getPoliza(Long.parseLong(idPolizaPpal));
		Poliza polizaCpl= polizaManager.getPoliza(Long.parseLong(idPolizaCpl));
		PolizaSbp polSbp = new PolizaSbp();
		
		try {
			polSbp = simulacionSbpManager.getPolizaSbp(Long.parseLong(idPolSbp));
		} catch (NumberFormatException e) {
			logger.error("Se ha producido un error al obtener la poliza de Sobreprecio: ", e);
		} catch (BusinessException e) {
			logger.error("Se ha producido un error la poliza de Sobreprecio: ", e);
		}
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		try{
			//TMR Facturacion.Anhadimos el usuario para su posterior facturacion
			parameters = simulacionSbpManager.recalculaPolizaSbpConCpl(polizaPpal,polizaCpl,polSbp,realPath,usuario,request);
		
		}catch (Exception e) {
			logger.error(LOGGER_ERROR + e.getMessage());
			throw e;
			
		}
		return parameters;
	}
	
	/**
	 * Método que verifica si la línea/ plan son válidos para el sobreprecio
	 * @param request
	 * @param response
	 */
	public void ajax_verificarLineaSbp(HttpServletRequest request, HttpServletResponse response) {
		boolean lineaCorrecta = false;
		String strLineaCorrecta = "";
		try{
			String linea  = StringUtils.nullToString(request.getParameter("linea"));
			String plan  = StringUtils.nullToString(request.getParameter("plan"));
			lineaCorrecta = simulacionSbpManager.validarLineaParaSbp(linea,plan);
		    if (!lineaCorrecta){
		    	if (!linea.equals("") && (!plan.equals(""))){
		    		// plan/linea incorrecto
		    		strLineaCorrecta = bundle.getString(ConstantsSbp.ERROR_LINEASEGUROID_INCOMPATIBLE);
		    	}else if (linea.equals("")){
		    		// plan incorrecto
		    		strLineaCorrecta = bundle.getString(ConstantsSbp.ERROR_LINEA_CODPLAN_INCOMPATIBLE);
		    	}else{
		    		//linea incorrecta
		    		strLineaCorrecta = bundle.getString(ConstantsSbp.ERROR_LINEA_CODLINEA_INCOMPATIBLE);
		    	}
		    	
		    }
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(strLineaCorrecta);
		}
		catch(Exception e){
			logger.warn("error al validar por Ajax si cumple los requisitos de línea para Sbp", e);
    	}
	}
	
	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	
	public void setPolizaComplementariaManager(
			PolizaComplementariaManager polizaComplementariaManager) {
		this.polizaComplementariaManager = polizaComplementariaManager;
	}

	public void setSbpTxtManager(ISbpTxtManager sbpTxtManager) {
		this.sbpTxtManager = sbpTxtManager;
	}

	
}
