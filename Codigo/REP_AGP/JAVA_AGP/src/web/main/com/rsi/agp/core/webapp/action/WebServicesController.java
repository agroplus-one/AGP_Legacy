package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.DistribucionCostesException;
import com.rsi.agp.core.managers.impl.ConsultaDetallePolizaManager;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.PolizaRCManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsRC;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.DatosAval;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.rc.PolizasRC;

/**
 * Controlador para los Servicios Web
 * 
 * @author T-Systems
 *
 */
public class WebServicesController extends BaseSimpleController implements Controller
{
	private static final String ES_SAECA_VAL = "esSaecaVal";
	private static final String VIENE_DE_UTILIDADES = "vieneDeUtilidades";
	private static final String ID_ENVIO = "idEnvio";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String ALERTA = "alerta";
	private static final String NETO_TOMADOR_FINANCIADO_AGR = "netoTomadorFinanciadoAgr";
	private static final String IDPOLIZA = "idpoliza";
	private static final String OPERACION = "operacion";
	
	private WebServicesManager webServicesManager;
	private PolizaManager polizaManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private ParametrizacionManager parametrizacionManager;
	private ConsultaDetallePolizaManager consultaDetallePolizaManager;
	private PagoPolizaManager pagoPolizaManager;
	private PolizaRCManager polizaRCManager;
	
	private final Log logger = LogFactory.getLog(getClass());
	private Parametro parametro;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private final static String ORIGEN_FINANCIAR = "financiacion";
	private final static String ORIGEN_PAGO = "pago"; 
	private final static String ORIGEN_GRABAR_DIST_COSTE = "grabarDistCoste";
	
	public WebServicesController() 
	{
		setCommandClass(String.class);
		setCommandName("string");
	}
	
	/**
	 * Discriminador de operaciones
	 *
	 * @param request
	 * @param response
	 * @param command
	 * @param errors
	 * @return ModelAndView con la redireccion a la que enviar el flujo
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView handle(HttpServletRequest request, 
							    HttpServletResponse response, 
							    Object command, 
							    BindException errors) throws Exception 
	{
		ModelAndView mv = null;								
		Poliza poliza = null;
		parametro = parametrizacionManager.getParametro();
		String operacion = StringUtils.nullToString(request.getParameter(OPERACION));
		String idPoliza = StringUtils.nullToString(request.getParameter(IDPOLIZA));
		String origenllamada = StringUtils.nullToString(request.getParameter("origenllamada")); //Se usa en el modulo de utilidades antes de cambiar el estado de una poliza a definitiva
		String netoTomadorFinanciadoAgr = StringUtils.nullToString(request.getParameter(NETO_TOMADOR_FINANCIADO_AGR));
		String oficinaActual = StringUtils.nullToString(request.getParameter("oficinaActual"));
		String oficina = StringUtils.nullToString(request.getParameter("oficina"));
		String nombreOficina = StringUtils.nullToString(request.getParameter("nomboficina"));
		String validComps = StringUtils.nullToString(request.getParameter("validComps"));
		String mensaje = StringUtils.nullToString(request.getParameter("mensaje"));
		String alerta = StringUtils.nullToString(request.getParameter(ALERTA));
		
		Boolean esGanado=false;
		if(request.getParameter(NETO_TOMADOR_FINANCIADO_AGR) != null){
			request.getSession().setAttribute(NETO_TOMADOR_FINANCIADO_AGR, netoTomadorFinanciadoAgr);	
		}
		
		String modoLectura = "";
		Map<String,Object> params = new HashMap<String, Object>();
		Map<String,Object> paramsDesc = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		params.put("externo", usuario.getExterno());
		boolean actualizaDistribucionCostes = false;
		// Path real para luego buscar el WSDL de los servicios Web
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");

		request.setAttribute(IDPOLIZA, idPoliza);
		request.setAttribute("origenllamada", origenllamada);
		request.setAttribute(MODO_LECTURA, modoLectura);
		request.setAttribute(NETO_TOMADOR_FINANCIADO_AGR, netoTomadorFinanciadoAgr);
		
		request.setAttribute("oficina", oficina);
		request.setAttribute("oficinaActual", oficinaActual);
		request.setAttribute("nomboficina", nombreOficina);
		request.setAttribute("validComps", validComps);
		request.setAttribute("mensaje", mensaje);
		request.setAttribute(ALERTA, alerta);

		//if ("financiacion".equalsIgnoreCase(origenllamada))
		//	params.put("isPagoFraccionado", "true");
		
		
		logger.info("WebServicesController: Procesando la Poliza: " + idPoliza);
		
		//Cargamos la poliza de la BBDD
		try {	
			poliza = polizaManager.getPoliza(new Long(idPoliza));
			
			if (poliza == null) throw new NullPointerException("La poliza es NULL!");
		} catch (RuntimeException e) {
			logger.error("Excepcion : WebServicesController - handle", e);
			throw new WebServiceException("Error en la llamada a los Servicios Web. El Id de la Poliza " + idPoliza + " no existe en la BBDD!! ", e);
		}
		
		params.put("oficinaActual",poliza.getOficina());
		params.put("oficina","");
		params.put("nomboficina",polizaManager.getNombreOficina(new BigDecimal(poliza.getOficina()), poliza.getAsegurado().getEntidad().getCodentidad()));
		params.put("entidad",poliza.getAsegurado().getEntidad().getCodentidad());
		params.put("perfil", usuario.getPerfil().substring(4));
		params.put("validComps", validComps);
		params.put("mensaje", mensaje);
		params.put(ALERTA, alerta);
		
		
		/*PET.70105 DNF 24/02/2021 anado la comprobacion para saber si la poliza es superior a 2021*/
		if(pagoPolizaManager.lineaContratacion2021(poliza.getLinea().getCodplan(), 
				poliza.getLinea().getCodlinea(), 
				poliza.getLinea().isLineaGanado())) {
			params.put("lineaContrataSup2021", "true");
		}else {
			params.put("lineaContrataSup2021", "false");
		}
		/*fin PET.70105 DNF 24/02/2021*/
		
		
		if (operacion == null || operacion.equals(""))
			throw new WebServiceException("Error en la llamada a los Servicios Web. No se indico la operacion a ejecutar ");
		
		if (poliza.getEstadoPoliza().getIdestado()
				.equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)
				|| poliza.getEstadoPoliza().getIdestado()
						.equals(Constants.ESTADO_POLIZA_DEFINITIVA))
			modoLectura = MODO_LECTURA;

		esGanado=(poliza.getLinea()!=null && poliza.getLinea().getEsLineaGanadoCount()>0);
		if(esGanado){
			params.put("columnaNumero", "Explotaci&oacuten");
		}else{
			params.put("columnaNumero", "N&uacutemero de Parcela");
		}
		params.put("esLineaGanado", esGanado);
		
		// Si la poliza tiene distibucion de coste se elimina para que pueda realizar el calculo siempre y cuando no venga de pagos
		//Si en modo lectura no borro la distribucion de costes
		if (poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)
				|| poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
			if (!ORIGEN_PAGO.equals(origenllamada) && !ORIGEN_GRABAR_DIST_COSTE.equalsIgnoreCase(operacion) && !ORIGEN_FINANCIAR.equals(origenllamada)) {
				if(!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
						//poliza.getPagoPolizas().clear();//Para borrar el pago cuando se pasa a pendiente de validacion y no interfiera luego	
						seleccionPolizaManager.deleteDistribucionCostes(poliza);
						DatosAval dv = polizaManager.GetDatosAval(poliza.getIdpoliza());
						if (null!=dv) {
							polizaManager.DeleteDatosAval(dv);
							poliza.setDatosAval(null);
						}
						//seleccionPolizaManager.deleteDistribucionCostes2015(poliza);
				}
			}else if(ORIGEN_FINANCIAR.equals(origenllamada)){
				//poliza.getPagoPolizas().clear();
				String seleccionada = request.getParameter("financiacionSeleccionada");
				String[] cadena = seleccionada.split("\\|");
				String codModulo = cadena[1];
				BigDecimal filaComp = new BigDecimal(cadena[7]);
				Long idComparativa = new Long(cadena[0]);
				seleccionPolizaManager.deleteDistribucionCoste(poliza, codModulo, filaComp,idComparativa);
			}
		}
		
		/**
		 * VALIDAR POLIZA
		 */
		if ("validar".equalsIgnoreCase(operacion))
		{
			mv = webServicesManager.callWebService(poliza, Constants.WS_VALIDACION, origenllamada,modoLectura, realPath,
					parametro, null,request,actualizaDistribucionCostes);
		}
		/**
		 * CALCULAR POLIZA
		 */
		else if ("calcular".equalsIgnoreCase(operacion) || "calcularRecargo".equalsIgnoreCase(operacion)) {
			String[] dctoPctComisiones = request.getParameterValues("dctoPctComisiones");
			String[] recargoPctComisiones = request.getParameterValues("recPctComisiones");
			if (poliza.getEstadoPoliza().getIdestado().intValue() < Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL
					.intValue()) {
				if (dctoPctComisiones != null && dctoPctComisiones.length > 0) {
					actualizaPolizaPctComisionesDescuento(dctoPctComisiones, poliza, usuario);
					if (poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
						actualizaDistribucionCostes = true;
					}
				}
				if (recargoPctComisiones != null && recargoPctComisiones.length > 0) {
					actualizaPolizaPctComisionesRecargo(recargoPctComisiones, poliza, usuario);
					if (poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
						actualizaDistribucionCostes = true;
					}
				}
				if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) >= 0) {
					// No podemos filtrar por modulo porque no sabemos cual va a elegir
					// finalmente
					List<CondicionesFraccionamiento> condFracc = webServicesManager
							.getCondicionesFraccionamiento(poliza.getLinea().getLineaseguroid());
					params.put("condicionesFraccionamiento", condFracc);
				}
			}
			if (Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION.equals(poliza.getEstadoPoliza().getIdestado())) {
				mv = webServicesManager.callWebService(poliza, Constants.WS_CALCULO, origenllamada, modoLectura,
						realPath, parametro, request.getParameterMap(), request, actualizaDistribucionCostes);
			} else {
				List<DistribucionCoste2015> listDc = consultaDetallePolizaManager
						.getDistribucionCoste2015ByIdPoliza(poliza.getIdpoliza());
				Set<VistaImportes> fluxCondensatorHolder = new LinkedHashSet<VistaImportes>();
				fluxCondensatorHolder = consultaDetallePolizaManager.getDataImportes(listDc, poliza, usuario, realPath);
				request.getSession().setAttribute("distCoste", fluxCondensatorHolder);
				params = getParametrosComparativaImportes(request, poliza, usuario, fluxCondensatorHolder);
				mv = new ModelAndView("moduloPolizas/polizas/importes/importes", "resultado", fluxCondensatorHolder)
						.addAllObjects(params);
			}
			// RC DE GANADO
			if (esGanado) {
				prepararDatosRCGanado(poliza, modoLectura, params);
			}
		}
		/**
		 * CONFIRMAR POLIZA
		 */		
		else if ("confirmar".equalsIgnoreCase(operacion))
		{
			mv = webServicesManager.callWebService(poliza, Constants.WS_CONFIRMACION, origenllamada,modoLectura,realPath, 
					parametro, null,request,actualizaDistribucionCostes);
		} 
		/**
		 * Elimina las comparativas no elegidas y guarda la distribucion de costes de la poliza
		 */		
		else if (ORIGEN_GRABAR_DIST_COSTE.equalsIgnoreCase(operacion)) {
			
			// Obtiene el id de envio 
			Long idEnvio = getIdEnvio(request.getParameter(ID_ENVIO));
			String totalCosteTomadorAFinanciar= StringUtils.nullToString(request.getParameter("totalCosteTomadorAFinanciar"));
			String[] seleccionados = StringUtils.nullToString(request.getParameter("modSeleccionado")).split(";");
			
			// MPM - Se obtienen los parametros relativos al fraccionamiento via Agroseguro
			BigDecimal impFracc = null;
			Integer enviarIBANFraccAgr = null;
			try {
				impFracc = new BigDecimal (request.getParameter(NETO_TOMADOR_FINANCIADO_AGR));
				logger.debug("--> " + impFracc.longValue());
				enviarIBANFraccAgr = new Integer ("true".equals(request.getParameter("enviarIBANFinanciadoAgr")) ? 1 : 0);
			}
			catch (Exception e) {
				logger.debug("No hay parametros de fraccionamiento de Agroseguro o son incorrectos");
			}
		
			// Guarda la poliza
			try {
				BigDecimal totalCosteTomadorAFinanciar_bd=null;
				if(null!=totalCosteTomadorAFinanciar && !totalCosteTomadorAFinanciar.isEmpty()){
					BigDecimal numero=new BigDecimal(org.apache.commons.lang.StringUtils.replace(org.apache.commons.lang.StringUtils.remove(totalCosteTomadorAFinanciar,"."),",","."));
					totalCosteTomadorAFinanciar_bd=numero;
				}else {
					String importeSeleccionado = request.getParameter("importeSeleccionado");
					BigDecimal numero2=new BigDecimal(org.apache.commons.lang.StringUtils.replace(org.apache.commons.lang.StringUtils.remove(importeSeleccionado,"."),",","."));
					totalCosteTomadorAFinanciar_bd =numero2;
				}
				grabaPoliza(idEnvio, poliza, seleccionados, realPath, impFracc, 
						enviarIBANFraccAgr, totalCosteTomadorAFinanciar_bd);
			}
			catch (DistribucionCostesException e) {
				logger.error("Error al grabar la distribucion de costes de la poliza", e);
				
				JSONObject objeto = new JSONObject();
				objeto.put("datos", bundle.getString("mensaje.poliza.ErrorGuardadoDistribucionCostes"));
				
				getWriterJSON (response, objeto);
				
				return null;
				
			}
			catch (Exception e) {
				logger.error("Error al grabar la poliza", e);
				
				JSONObject objeto = new JSONObject();
				objeto.put("datos", bundle.getString("mensaje.poliza.ErrorGuardadoPoliza"));
				
				getWriterJSON (response, objeto);
				
				return null;
			}
			
			//RC DE GANADO
			if (esGanado) {
				
				PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(Long.valueOf(idPoliza));
				
				if (polizaRC != null) {
					
					String radioSumaAseg = StringUtils.nullToString(request.getParameter("selectedSumaAseg"));
					String fechaEfectoString = StringUtils.nullToString(request.getParameter("fechaEfectoRC"));
					Date fechaEfecto = null;
					
					if ("-1".equals(radioSumaAseg) || "".equals(radioSumaAseg)) {
						
						this.polizaRCManager.deletePolizaRC(polizaRC);
						polizaRC = null;
						
					} else {
						
						// OBTENEMOS LOS DATOS RELATIVOS A LA SUMA ASEGURADA SELECCIONADA
						PolizasRC datosSA = this.polizaRCManager.getCalculoRC(
											poliza.getLinea().getCodplan(), 
											poliza.getLinea().getCodlinea(), 
											poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
											poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(), 
											polizaRC.getEspeciesRC().getCodespecie(),
											polizaRC.getRegimenRC().getCodregimen(), 
											polizaRC.getNumanimales(), 
											new BigDecimal(radioSumaAseg));
						
						if (datosSA != null) {
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							fechaEfecto = sdf.parse(fechaEfectoString);
							
							if(!this.comprobarFechaEfecto(fechaEfecto)){
								Map<String, Object> parameters = new HashMap<String, Object>();
								parameters.put(ALERTA, "Error en la Fecha de Efecto RC");
								parameters.put("grProvisionalKO", "true");
								parameters.put(OPERACION, "importes");
								parameters.put(IDPOLIZA, poliza.getIdpoliza());
								return new ModelAndView("redirect:/seleccionPoliza.html", parameters);
							}
							
							polizaRC.setFechaEfecto(fechaEfecto);
							polizaRC.setSumaAsegurada(datosSA.getSumaAsegurada());
							polizaRC.setTasa(datosSA.getTasa());
							polizaRC.setFranquicia(datosSA.getFranquicia());
							polizaRC.setPrimaMinima(datosSA.getPrimaMinima());
							polizaRC.setPrimaNeta(datosSA.getPrimaNeta());							
							polizaRC.setImporte(datosSA.getImporte());
							
							this.polizaRCManager.cambiaEstadoPolizaRC(polizaRC,
									ConstantsRC.ESTADO_RC_PROVISIONAL,
									usuario.getCodusuario());							
						}
					}
					
					params.put("selectedSumaAseg", radioSumaAseg);
					params.put("fechaEfectoRC", fechaEfecto);
					poliza.setPolizaRC(polizaRC);
					params.put("polizaRC", polizaRC);
				}
			}
		}
		else if ("grabar".equalsIgnoreCase(operacion)) {
				
				// Obtiene el id de envio 
				Long idEnvio = getIdEnvio(request.getParameter(ID_ENVIO));
				
				
				
				try {
					String noRevPrecioProduccion= StringUtils.nullToString((request.getParameter("noRevPrecioProduccion")));
					String revPagos= StringUtils.nullToString((request.getParameter("revPagos")));
					String grabacionProvisional= StringUtils.nullToString((request.getParameter("grabacionProvisional")));
					
					if (noRevPrecioProduccion.equals("true")){
						mv = new ModelAndView(new RedirectView("revProduccionPrecio.html"));
						mv.addObject(IDPOLIZA, poliza.getIdpoliza());	
						//mv.addObject("operacion", "cambiar");
						mv.addObject(ID_ENVIO, idEnvio);
					}
					if (revPagos.equals("true")){
						// MPM - 15/06/12 -- Si va a la pantalla de pagos se comprueba si es en modo lectura
						String modoLecturaPagos = StringUtils.nullToString((request.getParameter(MODO_LECTURA)));
						
						mv = new ModelAndView(new RedirectView("pagoPoliza.html"));
						mv.addObject(ID_ENVIO, idEnvio);
						mv.addObject(IDPOLIZA, poliza.getIdpoliza());
						mv.addObject(MODO_LECTURA, modoLecturaPagos);
						mv.addObject(NETO_TOMADOR_FINANCIADO_AGR, request.getSession().getAttribute(NETO_TOMADOR_FINANCIADO_AGR));
						if(poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_COMPLEMENTARIO)) {
							mv.addObject(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
						}
						if (request.getParameter("esAgrSend") != null && request.getParameter("esAgrSend").equals("") || request.getParameter("esAgrSend").equals("false")){
							if (request.getParameter(ES_SAECA_VAL) != null && !request.getParameter(ES_SAECA_VAL).equals("")){
								mv.addObject(ES_SAECA_VAL, request.getParameter(ES_SAECA_VAL));
							}
							else{
								DistribucionCoste2015 dc = polizaManager.getDistCosteSaeca(poliza);
								mv.addObject(ES_SAECA_VAL, dc.getImportePagoFracc());
							}
						}
						
						return mv;
					}
					if ("true".equals(grabacionProvisional)){
						params.put(IDPOLIZA, poliza.getIdpoliza());
						params.put(OPERACION, "");
						params.put("grProvisional", "true");
						params.put("netoTomFinanAgrNum", request.getSession().getAttribute(NETO_TOMADOR_FINANCIADO_AGR));
						params.put(ID_ENVIO, idEnvio);						
						params.put("totalCosteTomadorAFinanciar", StringUtils.nullToString("15,00"));
						
						mv= new ModelAndView("redirect:/grabacionPoliza.html").addAllObjects(params);
						return mv;
					}
					
				}
				catch (Exception e) {
					logger.error("Error al grabar la poliza", e);
					
					// Redirige a la pantalla de importes indicando el error 
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put(ALERTA, bundle.getString("mensaje.poliza.ErrorGuardadoPoliza"));
					parameters.put("grProvisionalKO", "true");
					parameters.put(OPERACION, "importes");
					parameters.put(IDPOLIZA, poliza.getIdpoliza());
					return new ModelAndView("redirect:/seleccionPoliza.html", parameters);
				}
				
				
		}
			if(!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
				paramsDesc = webServicesManager.muestraBotonDescuento(poliza,usuario);
			}
		if (mv!= null) {
			mv.addAllObjects(params).addAllObjects(paramsDesc);
		}
		return mv;
	}
	
	private boolean comprobarFechaEfecto(Date fechaEfectoDate) {
		Calendar ayer = Calendar.getInstance();
		ayer.add(Calendar.HOUR, -24);		
		Calendar hoyMasOnceMeses = Calendar.getInstance();
		hoyMasOnceMeses.add(Calendar.MONTH, 11);
		Calendar fechaEfecto = Calendar.getInstance();
		fechaEfecto.setTime(fechaEfectoDate);
		boolean fechaValida = true;
		if(fechaEfecto.after(hoyMasOnceMeses)){
			fechaValida = false;
		}
		if(fechaEfecto.before(ayer)){
			fechaValida = false;
		}
		return fechaValida;
	}
	
	private void prepararDatosRCGanado(final Poliza poliza, final String modoLectura, final Map<String,Object> params) throws BusinessException {
		
		PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(poliza.getIdpoliza());
		
		if (polizaRC != null) {
			
			if (!MODO_LECTURA.equals(modoLectura)) {
				
				List<PolizasRC> result = this.polizaRCManager.getListadoCalculosRC(
						poliza.getLinea().getCodplan(), 
						poliza.getLinea().getCodlinea(), 
						poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
						poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(), 
						polizaRC.getEspeciesRC().getCodespecie(),
						polizaRC.getRegimenRC().getCodregimen(), 
						polizaRC.getNumanimales());
				
				params.put("sumasAseguradas", result);
				
				if(polizaRC.getFechaEfecto() == null){
					polizaRC.setFechaEfecto(new Date());
				}
				
				if (polizaRC.getSumaAsegurada() != null) {
					
					for (PolizasRC dato : result) {						
						if (polizaRC.getSumaAsegurada().equals(dato.getSumaAsegurada())) {	
							polizaRC.setCodSumaAsegurada(dato.getCodSumaAsegurada());
							params.put("radioSumaAseg", dato.getCodSumaAsegurada());
							break;
						}
					}
				}
			}
			
			poliza.setPolizaRC(polizaRC);
			params.put("polizaRC", polizaRC);
		}
	}

	
	/**
	 * Obtiene el id de envio seleccionado en la pantalla de importes
	 * @param idEnvioStr
	 * @return
	 */
	private Long getIdEnvio(String idEnvioStr) {
		
		Long idEnvio = null;
		
		if (idEnvioStr != null && !idEnvioStr.equals("") && !idEnvioStr.equals("N/D"))
			idEnvio = Long.parseLong(idEnvioStr);
		
		return idEnvio;
	}
	
	
	/**
	 * Graba la poliza despues de mostrar el resultado de calculo: Borra las comparativas no seleccionadas y toda
	 * la informacion asociada a ellas.
	 * 
	 * @param poliza Poliza
	 * @param seleccionados Cadena te texto con el identificador de la comparativa seleccionada
	 * @return
	 */
	private void grabaPoliza(Long idEnvio, Poliza poliza, String[] seleccionados, 
		String realPath, BigDecimal impPrimeraFracc, Integer enviarIBAN,
		BigDecimal totalCosteTomadorAFinanciar) throws DistribucionCostesException, Exception {
		
		Boolean esGanado = (poliza.getLinea()!=null && poliza.getLinea().getEsLineaGanadoCount()>0);
		// Obtenemos la comparativa seleccionada
		if (seleccionados.length > 0) {
			String st = seleccionados[0];
			String[] seleccionados2 = st.split("\\|");

			String codModulo = seleccionados2[1];
			String idComparativa = seleccionados2[0];
			
			// ESC-13305
			logger.debug("Valor de codModulo >>" + codModulo + "<<");
			if (!StringUtils.isNullOrEmpty(codModulo)) {

				//Borramos las comparativas no seleccionadas
				Iterator<ComparativaPoliza> itComparativa = poliza.getComparativaPolizas().iterator();
				List<ComparativaPoliza> lstCmpEliminar = new ArrayList<ComparativaPoliza>();
				while (itComparativa.hasNext()){
					ComparativaPoliza comp = itComparativa.next();
					if (!esGanado){
						if(!codModulo.equals(comp.getId().getCodmodulo())){
							lstCmpEliminar.add(comp);
						}else{
							if(!idComparativa.equals(comp.getId().getIdComparativa().toString())){
								lstCmpEliminar.add(comp);
							}
						}
					}else{
						// si el idmodulo es diferente se elimina.
						if (!idComparativa.equals(comp.getId().getIdComparativa().toString())){	
							lstCmpEliminar.add(comp);
						}
					}
				}
				
				polizaManager.deleteComparativaPoliza(lstCmpEliminar);
				
				for (ComparativaPoliza cp: lstCmpEliminar){
					poliza.getComparativaPolizas().remove(cp);
					//polizaManager.deleteComparativaPoliza(cp);
				}
				
				
				// borramos las relaciones de capital asegurado con los modulos que no se han seleccionado
				List<CapAsegRelModulo> lstCapAsegEliminar = new ArrayList<CapAsegRelModulo>();
				for (Parcela parcela : poliza.getParcelas()) {
					for (CapitalAsegurado capAseg : parcela.getCapitalAsegurados()) {
						Iterator<CapAsegRelModulo> itCapAseg = capAseg.getCapAsegRelModulos().iterator();
						while (itCapAseg.hasNext()){
							CapAsegRelModulo capAsegRelMod = itCapAseg.next();
							if (!codModulo.equals(capAsegRelMod.getCodmodulo())) {
								lstCapAsegEliminar.add(capAsegRelMod);
							}
						}
						for (CapAsegRelModulo carm: lstCapAsegEliminar){
							capAseg.getCapAsegRelModulos().remove(carm);
						}
					}
				}
				polizaManager.deleteCapAsegRelModulo(lstCapAsegEliminar);
		
				
				//DAA 15/01/2013
				//borramos las subvenciones de los modulos que no he elegido
				polizaManager.deleteSubvsModsNoElec(codModulo, poliza);

				
				//borramos los modulos no seleccionados
				Iterator<ModuloPoliza> itModPoliza = poliza.getModuloPolizas().iterator();
				List<ModuloPoliza> lstModEliminar = new ArrayList<ModuloPoliza>();
				while (itModPoliza.hasNext()){
					ModuloPoliza mp = itModPoliza.next();
					if (!idComparativa.equals("")){
						if (!mp.getId().getNumComparativa().equals(new Long(idComparativa))){
							lstModEliminar.add(mp);
						}
					}
				}
				
				for (ModuloPoliza mp1 : lstModEliminar){
					poliza.getModuloPolizas().remove(mp1);
					
				}
				
				polizaManager.deleteModuloPoliza(lstModEliminar);
				
				// Borramos las distribuciones de coste no seleccionadas
				// Seleccionamos todas las distribuciones por IdPoliza y borramos las que no se
				// correspondan con el modulo y fila seleccionada
				Iterator<DistribucionCoste2015> itDistCostes2015 = poliza.getDistribucionCoste2015s().iterator();
				List<DistribucionCoste2015> lstDistCosteEliminar = new ArrayList<DistribucionCoste2015>();

				while (itDistCostes2015.hasNext()) {
					DistribucionCoste2015 dc = itDistCostes2015.next();
					if (esGanado) {
						if (!dc.getIdcomparativa().equals(new BigDecimal(idComparativa))) {
							lstDistCosteEliminar.add(dc);
						} else {
							// Comprueba si se envian los parametros de la financiacion via
							// Agroseguro
							if (impPrimeraFracc != null && enviarIBAN != null) {
								// Se guardan en la distribucion de costes elegida los datos de la
								// financiacion
								dc.setImportePagoFraccAgr(impPrimeraFracc);
								dc.setEnviarIBANAgr(enviarIBAN);
								polizaManager.guardarDCFraccAgroseguro(dc);
							}
							if (poliza.getEsFinanciada().equals('S') && null != totalCosteTomadorAFinanciar) {
								dc.setTotalcostetomadorafinanciar(totalCosteTomadorAFinanciar);
								polizaManager.guardarDCFraccAgroseguro(dc);
							}
						}
					} else {
						if (!(dc.getCodmodulo().compareTo(codModulo) == 0
								&& dc.getIdcomparativa().compareTo(new BigDecimal(idComparativa)) == 0)) {
							lstDistCosteEliminar.add(dc);
						} else {
							// Comprueba si se envian los parametros de la financiacion via
							// Agroseguro
							if (impPrimeraFracc != null && enviarIBAN != null) {
								// Se guardan en la distribucion de costes elegida los datos de la
								// financiacion
								dc.setImportePagoFraccAgr(impPrimeraFracc);
								dc.setEnviarIBANAgr(enviarIBAN);
								polizaManager.guardarDCFraccAgroseguro(dc);
							}
						}
					}

				}
				for (DistribucionCoste2015 dc1 : lstDistCosteEliminar) {
					poliza.getDistribucionCoste2015s().remove(dc1);
				}
				polizaManager.deleteDistribucionCostes(lstDistCosteEliminar);

				// Habr� una distribuci�n de coste por grupo de negocio del m�dulo elegido
				BigDecimal sumaDc = new BigDecimal(0.00);
				sumaDc.setScale(2);
				for (DistribucionCoste2015 dc1 : poliza.getDistribucionCoste2015s()) {
					if (dc1.getOpcionFracc() != null && dc1.getTotalcostetomador() != null) {
						sumaDc = sumaDc.add(dc1.getTotalcostetomador());
					} else {
						sumaDc = sumaDc.add(dc1.getCostetomador());
					}
				}
				poliza.setImporte(sumaDc);
				polizaManager.actualizaImporte(poliza);
								
				/* ESC-13247 ** MODIF TAM (06.04.2021) ** Inicio */
				/* Aunque la p�liza tenga m�dulo, si este valor cambia hay que actualizarlo */
				// ESC-13305
				logger.debug("Valor de poliza.getCodmodulo() >>" + poliza.getCodmodulo() + "<<");
				if(StringUtils.isNullOrEmpty(poliza.getCodmodulo()) || !codModulo.equals(poliza.getCodmodulo())) {
					poliza.setCodmodulo(codModulo);
					polizaManager.savePoliza(poliza);
				}
				/* ESC-13247 ** MODIF TAM (06.04.2021) ** Fin */

				logger.debug("Comparativas no seleccionadas borradas satisfactoriamente");
			}
		}
	}
	
	public Map<String,Object> getParametrosComparativaImportes(HttpServletRequest request, Poliza poliza, 
															   Usuario usuario, Set<VistaImportes> fluxCondensatorHolder) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametrosPagos = new HashMap<String, Object>();
		Map<String,Object> paramsDesc = new HashMap<String, Object>();
		Map<String,Object> paramsFraccionamiento = new HashMap<String, Object>();
		
		Poliza polizaDefinitiva = new Poliza();
		polizaDefinitiva.setIdpoliza(poliza.getIdpoliza());
		
		parameters.put("polizaDefinitiva", polizaDefinitiva);
		parameters.put("grProvisionalOK", true);
		parameters.put("numeroCuenta",AseguradoUtil.getFormattedBankAccount(poliza, true));
		parameters.put("numeroCuenta2",AseguradoUtil.getFormattedBankAccount(poliza, false));
		parameters.put(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
		parameters.put(IDPOLIZA, poliza.getIdpoliza());
		parameters.put("esFinanciacionCpl", "false");
		parameters.put("plan", poliza.getLinea().getCodplan());
		parameters.put("isLineaGanado", poliza.getLinea().isLineaGanado());
		if(null != poliza.getEstadoPoliza() && null != poliza.getEstadoPoliza().getIdestado())
			parameters.put("estadoPoliza", poliza.getEstadoPoliza().getIdestado());
		
		parametrosPagos= getParametrosPagos(poliza);
		parameters.putAll(parametrosPagos);
		
		int countImportes = fluxCondensatorHolder.size();
		parameters.put("countImportes", countImportes);
		
		if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 0 ||
			poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 1) {
			paramsDesc = getParametrosDescuentosRecargos(poliza, usuario);
			parameters.putAll(paramsDesc);
		
			paramsFraccionamiento= getParametrosFraccionamiento(poliza, fluxCondensatorHolder);
			parameters.putAll(paramsFraccionamiento);
			parameters.put(ALERTA, bundle.getString("mensaje.errorWs.descuentos.recargos.KO"));
		}
		
		parameters.put("dataCodlinea", poliza.getLinea().getCodlinea());
		parameters.put("dataCodplan", poliza.getLinea().getCodplan());
		parameters.put("dataNifcif", poliza.getAsegurado().getNifcif());
		
		return parameters;
	}
	
	public Map<String,Object> getParametrosFraccionamiento(Poliza poliza, Set<VistaImportes> fluxCondensatorHolder) throws Exception {
		Map<String,Object> paramsFraccionamiento = new HashMap<String, Object>();
		
		List<CondicionesFraccionamiento> condFracc = webServicesManager.getCondicionesFraccionamiento(poliza.getLinea().getLineaseguroid());
		paramsFraccionamiento.put("condicionesFraccionamiento", condFracc);

		return paramsFraccionamiento;
	}
	
	public Map<String,Object> getParametrosDescuentosRecargos(Poliza poliza, Usuario usuario) throws Exception {
		Map<String,Object> paramsDesc = new HashMap<String, Object>();
		paramsDesc = webServicesManager.muestraBotonDescuento(poliza, usuario);
		
		logger.debug("ESC-13208 --> forzado a true");
		paramsDesc.put("descuentoLectura", true);
		paramsDesc.put("recargoLectura", true);
		
		paramsDesc.put("descuento", (poliza.getPolizaPctComisiones() != null &&
									 poliza.getPolizaPctComisiones().getPctdescelegido() != null)
				? poliza.getPolizaPctComisiones().getPctdescelegido().setScale(2)
				: new BigDecimal(0));
		
		paramsDesc.put("recargo", (poliza.getPolizaPctComisiones() != null &&
								   poliza.getPolizaPctComisiones().getPctrecarelegido() != null)
				? poliza.getPolizaPctComisiones().getPctrecarelegido().setScale(2)
				: new BigDecimal(0));
		
		return paramsDesc;
	}
	public Map<String,Object> getParametrosPagos(Poliza poliza) throws DAOException {
		Map<String, Object> parametrosPagos = new HashMap<String, Object>();
		PagoPoliza pagoPoliza = pagoPolizaManager.getPagoPolizaByPolizaId(poliza.getIdpoliza());
		if(null!= pagoPoliza) {
			Map<String,Object> paramsPagoPoliza = getParametrosPagoPoliza(pagoPoliza);
			parametrosPagos.putAll(paramsPagoPoliza);
		}
		if (null!= pagoPoliza && pagoPoliza.getFormapago().equals('F')) {
			
			parametrosPagos.put("idestado", poliza.getEstadoPoliza().getIdestado());
			DatosAval dv = polizaManager.GetDatosAval(poliza.getIdpoliza());
			if (dv != null){
				parametrosPagos.put("numaval", dv.getNumeroAval());
				parametrosPagos.put("importeaval", dv.getImporteAval());
			}
			else{
				parametrosPagos.put("numaval", "No Data");
				parametrosPagos.put("importeaval", "0.00");
			}
		}
		return parametrosPagos;
	}
	
	private Map<String, Object> getParametrosPagoPoliza(PagoPoliza pagoPoliza) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (pagoPoliza.getEnvioIbanAgro() != null)
			if (pagoPoliza.getEnvioIbanAgro().equals('S'))
				parameters.put("envioIBANAgr", true);
			else
				parameters.put("envioIBANAgr", false);
		parameters.put("metodoDePago", pagoPoliza.getTipoPago());
		parameters.put("formaDePago", pagoPoliza.getFormapago());
		
		parameters.put("bancoDestino", pagoPoliza.getBanco());
		parameters.put("importe", pagoPoliza.getImporte());
		parameters.put("fechaPago", pagoPoliza.getFecha());
		parameters.put("iban", pagoPoliza.getIban());
		if (pagoPoliza.getTipoPago().equals(Constants.PAGO_MANUAL)) {
			parameters.put("mpPagoM", true);
			parameters.put("mpPagoC", false);
			
		}else {
			parameters.put("mpPagoC", true);
			parameters.put("mpPagoM", false);
		}
		parameters
				.put("cuenta1",
						pagoPoliza.getCccbanco() != null
								&& !"".equals(pagoPoliza.getCccbanco()) &&
								pagoPoliza.getCccbanco().length() >= 4 ? pagoPoliza
								.getCccbanco().substring(0, 4) : "");
		parameters
				.put("cuenta2",
						pagoPoliza.getCccbanco() != null
								&& !"".equals(pagoPoliza.getCccbanco()) &&
								pagoPoliza.getCccbanco().length() >= 8 ? pagoPoliza
								.getCccbanco().substring(4, 8) : "");
		parameters
				.put("cuenta3",
						pagoPoliza.getCccbanco() != null
								&& !"".equals(pagoPoliza.getCccbanco()) &&
								pagoPoliza.getCccbanco().length() >= 12 ? pagoPoliza
								.getCccbanco().substring(8, 12) : "");
		parameters
				.put("cuenta4",
						pagoPoliza.getCccbanco() != null
								&& !"".equals(pagoPoliza.getCccbanco()) &&
								pagoPoliza.getCccbanco().length() >= 16 ? pagoPoliza
								.getCccbanco().substring(12, 16) : "");
		parameters
				.put("cuenta5",
						pagoPoliza.getCccbanco() != null
								&& !"".equals(pagoPoliza.getCccbanco()) &&
								pagoPoliza.getCccbanco().length() == 20 ? pagoPoliza
								.getCccbanco().substring(16, 20) : "");
				
		return parameters; 
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
			logger.error("Fallo al escribir la lista en el response", e);
		}
	}	
	
	private void actualizaPolizaPctComisionesDescuento(final String [] varRequest, final Poliza poliza, final Usuario usuario) throws Exception{
		webServicesManager.actualizaPolizaPctComisionesDescuento(varRequest, poliza, usuario);
	}
	
	private void actualizaPolizaPctComisionesRecargo(final String [] varRequest, final Poliza poliza, final Usuario usuario) throws Exception{
		webServicesManager.actualizaPolizaPctComisionesRecargo(varRequest, poliza, usuario);
	}
	
	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setParametrizacionManager(
			ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}
	
	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}
	
	public void setConsultaDetallePolizaManager(
			ConsultaDetallePolizaManager consultaDetallePolizaManager) {
		this.consultaDetallePolizaManager = consultaDetallePolizaManager;
	}
	
	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}

	public void setPolizaRCManager(PolizaRCManager polizaRCManager) {
		this.polizaRCManager = polizaRCManager;
	}	
}