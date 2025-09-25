package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.PasarADefinitivaPlzManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class GrabacionPolizasController extends BaseSimpleController implements Controller
{
	private static final Log LOGGER = LogFactory.getLog(GrabacionPolizasController.class); 
	private PolizaManager polizaManager;	
	private IHistoricoEstadosManager historicoEstadosManager;
	private MetodoPagoController metodoPagoController;
	private PagoPolizaManager pagoPolizaManager;
	
	public GrabacionPolizasController() 
	{
		super();
		setCommandClass(String.class);
	}
	
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		try{
			LOGGER.debug("init - GrabacionPolizasController");		
			final Map<String, Object> parameters = new HashMap<String, Object>();
	 		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	 		ModelAndView resultado = null;		
			String operacion = StringUtils.nullToString(request.getParameter("operacion"));
			Long idEnvio= null;
			String idEnvioStr="";
			if (request.getParameter("idEnvio") != null)
			{
				idEnvioStr= request.getParameter("idEnvio");
				if (!idEnvioStr.equals("") && !idEnvioStr.equals("N/D")){
					idEnvio = Long.parseLong(idEnvioStr);
				}
			}
			parameters.put("idEnvio", idEnvio);
			final String idpoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			Poliza poliza = polizaManager.getPoliza(new Long(idpoliza));
	//		Recuperamos el modulo del set de modulos seleccionados (En estos momentos solo tenemos 1) y lo guardamos en codmodulo
			if(poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL))
			{
				String modulo = poliza.getModuloPolizas().iterator().next().getId().getCodmodulo();
				poliza.setCodmodulo(modulo);
			}
			
			parameters.put("poliza", poliza);
	
		
		
		if ("".equalsIgnoreCase(operacion)){			
			LOGGER.debug("init - Actualizamos el estado de la poliza a grabacion provisional");
			
			// DAA 31/05/2012  Actualizamos el estado de la poliza a grabacion provisional
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			
			EstadoPoliza estadoPoliza = new EstadoPoliza();
			estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL);
			poliza.setEstadoPoliza(estadoPoliza);
			//actualizamos el pago poliza si no es financiada(si fuese financiada se actualiza ya en la financiación), por si tuviese ya un pagopoliza financiado previo.
			if (!poliza.getEsFinanciada().equals('S') && poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size()>0){
				poliza.getPagoPolizas().iterator().next().setImporte(poliza.getImporte());
			}
			
			/// Mejora 96: Angel 26/01/2012 - Calculamos totalSuperfice y se lo insertamos a la poliza al pasar a provisional
			BigDecimal totalSuperficie = null;
			totalSuperficie=polizaManager.getTotalSuperficie(poliza);
			if (totalSuperficie!=null){
				poliza.setTotalsuperficie(totalSuperficie);
			}

//			Actualizamos el estado de la Poliza
			polizaManager.savePoliza(poliza);
 
			historicoEstadosManager.insertaEstado(Tabla.POLIZAS, 
					poliza.getIdpoliza(),usuario.getCodusuario(),Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL);
			
//			OCULTAMOS EL BOTON IMPRIMIR TEMPORALMENTE SI ES UNA POLIZA COMPLEMENTARIA
			if(poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_COMPLEMENTARIO)){
				parameters.put("polizaComplementaria", true);
			}

			if (poliza.getEstadoPoliza() != null && 
					poliza.getEstadoPoliza().getIdestado() == Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA){
				parameters.put("titulo", bundle.getString("mensaje.alta.definitiva.OK"));
				parameters.put("mensajeCentral", bundle.getString("mensaje.alta.definitiva.OK"));
				parameters.put("mostrarBtnDef", "NO");
				
				boolean avisoMan = DateUtil.horaActualMayor (PasarADefinitivaPlzManager.HORA, PasarADefinitivaPlzManager.MINUTO);
				String mensajeGrabacion = bundle.getString("mensaje.poliza.change.state.definitiva") + ".<br/>" +
						bundle.getString("mensaje.poliza.change.state.definitiva.envio." + (avisoMan ? "man" : "hoy"));
				parameters.put("mensajeGrabacion", mensajeGrabacion);
			}else{
				String grProvisional = StringUtils.nullToString(request.getParameter("grProvisional"));
				if ("true".equals(grProvisional)){
					resultado= grabacionProvisional(request, parameters, response, poliza, bundle);
					return resultado;
				}
				// LIMITACIONES AL METODO DE PAGO
				// String volverPagos = StringUtils.nullToString(request.getParameter("volverPagos"));
				
  				boolean mpPagoM = metodoPagoController.isPagoMAllowed(usuario, poliza);
  				boolean mpPagoC = metodoPagoController.isPagoCCAllowed(usuario, poliza);
  				parameters.put("mpPagoM", mpPagoM);
				parameters.put("mpPagoC", mpPagoC);
			
				if (!mpPagoM && !mpPagoC) {
					parameters.put("alerta2", bundle.getString("mensaje.formaPago.inexistente"));
				}
				// FIN DE LIMITACIONES AL METODO DE PAGO
				// anadimos el numero de cuenta e importe para el popup de forma de pago
				parameters.put("numeroCuenta", AseguradoUtil.getFormattedBankAccount(poliza, true));
				parameters.put("importe1", poliza.getImporte());
				
				parameters.put("titulo", bundle.getString("mensaje.alta.provisional.OK"));
				
				//añadimos el importe y la fecha y banco de pagosPoliza si lo tiene
				
				Set<PagoPoliza> pagoPolizas = poliza.getPagoPolizas();
				
				for (PagoPoliza pag: pagoPolizas){
					if (null != pag.getImporte())
						parameters.put("import", pag.getImporte());
					if (null != pag.getBanco())
						parameters.put("banDestino", String.format("%04d", pag.getBanco().intValue()));
					if (null != pag.getFecha()){
						DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					    parameters.put("fecPago", (dateFormat.format(pag.getFecha())).toString());
					}
				}
				parameters.put("idpoliza", poliza.getIdpoliza());
				parameters.put("mensajeCentral", bundle.getString("mensaje.alta.provisional.OK"));
				parameters.put("mensajeGrabacion", bundle.getString("mensaje.alta.provisional.detail"));
				
				// DAA 06/07/13 si poliza principal es distinto de nulo significa que estoy con la complementaria 
				//y si  el estado de la misma es enviada correcta muestro el boton de paso a definityiva de la CPL
				if (poliza.getPolizaPpal()!=null && (Constants.ESTADO_POLIZA_DEFINITIVA).equals(poliza.getPolizaPpal().getEstadoPoliza().getIdestado()))
					parameters.put("mostrarBtnDef", "SI");
				else
					parameters.put("mostrarBtnDef", "NO");
				}
			
			// MPM - Paso a definitiva
			Poliza polizaDefinitiva = new Poliza ();
			polizaDefinitiva.setIdpoliza(poliza.getIdpoliza());
			parameters.put("polizaDefinitiva", polizaDefinitiva);
			// ---
			Boolean esSaeca = polizaManager.esFinanciadaSaeca(poliza.getLinea().getLineaseguroid(),poliza.getColectivo().getSubentidadMediadora());
			if(esSaeca.equals(true)){
				DistribucionCoste2015 dcte = polizaManager.getDistCosteSaeca(poliza);
				
				if(dcte.getValorOpcionFracc()!=null && dcte.getImportePagoFracc()!=null){
					PagoPoliza pagpol = pagoPolizaManager.getPagoPolizaByPolizaId(poliza.getIdpoliza());
					pagpol.setImporte(dcte.getImportePagoFracc());
					pagoPolizaManager.savePagoPoliza(pagpol);
					parameters.put("importeSaeca", dcte.getImportePagoFracc());
				}
			}
			resultado = new ModelAndView("moduloPolizas/polizas/grabacion/grabaciondef", "grabDef", parameters);
			
			LOGGER.debug("end - Actualizamos el estado de la poliza a grabacion provisional");
		}
		else if ("salir".equalsIgnoreCase(operacion)){
			resultado = salir(poliza);					
		}else if ("imprimirPoliza".equalsIgnoreCase(operacion)){
			resultado=imprimirPoliza(request,parameters,poliza.getIdpoliza(), poliza.getTipoReferencia());			
		}
		
		LOGGER.debug("end - GrabacionPolizasController");
		

		
		return  (resultado != null) ? resultado.addAllObjects(parameters) : new ModelAndView("redirect:/seleccionPoliza.html");
		} catch  (Exception ex) {		
			logger.error("# Error global en WS Exception: ",ex);
			throw new Exception(ex);
		}
	}	
	
	
	private ModelAndView grabacionProvisional(final HttpServletRequest request, final Map<String, Object> parameters, 
			final HttpServletResponse response,  final Poliza poliza, final ResourceBundle bundle) throws JSONException{
		
			parameters.put("lineaContrataSup2021", request.getParameter("lineaContrataSup2021"));
			parameters.put("grProvisional", "true");
			parameters.put("mensaje", bundle.getString("mensaje.alta.provisional.detail"));
			parameters.put("operacion", "importes");
			parameters.put("idpoliza", poliza.getIdpoliza());
			parameters.put("totalCosteTomadorAFinanciar", StringUtils.nullToString(request.getParameter("totalCosteTomadorAFinanciar")));
			if (request.getParameter("netoTomFinanAgrNum") == null
					|| request.getParameter("netoTomFinanAgrNum")
							.isEmpty()) {
				parameters.put("importe1", poliza.getImporte());
				LOGGER.debug("importe pol: "+poliza.getImporte());
			} else {
				parameters.put("importe1", request.getParameter("netoTomFinanAgrNum"));
				LOGGER.debug("importe financiado: "+request.getParameter("netoTomFinanAgrNum"));
			}
	
			parameters.put("fecha1", StringUtils.nullToString(request.getParameter("fecha1")));
			
			if(poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_COMPLEMENTARIO)){
				JSONObject list = new JSONObject();
				//Comentar if para pruebas menos parameters.put("mostrarBtnDef", "SI");
				if(poliza.getPolizaPpal().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)){
					parameters.put("mostrarBtnDef", "SI");
				}else{
					parameters.put("mostrarBtnDef", "NO");
				}						
				parameters.put("grProvisionalOK","true");
			
				if (null!=poliza.getDistribucionCoste2015s() && poliza.getDistribucionCoste2015s().size()>0){
					DistribucionCoste2015 dc = poliza.getDistribucionCoste2015s().iterator().next();
					String periodoFracc = "";
					if(null!=dc.getPeriodoFracc())
						periodoFracc=dc.getPeriodoFracc().toString();
					
					parameters.put("periodoFracc", periodoFracc);
				}
					
				for (Map.Entry<String,Object> entry : parameters.entrySet()) {
					list.put(entry.getKey(), entry.getValue());				    						    
				}
				
				getWriterJSON(response, list);
				return null;
			}else{
				return new ModelAndView("redirect:/seleccionPoliza.html", parameters);
			}
		
	}
	
	private ModelAndView  imprimirPoliza(final HttpServletRequest request, final Map<String, Object> parameters, Long idpoliza, Character tipoReferencia){
		LOGGER.debug("init - operacion imprimir");
		ModelAndView resultado = null;
		String  StrImprimirReducida = StringUtils.nullToString(request.getParameter("imprimirReducida"));
		parameters.put("StrImprimirReducida", StrImprimirReducida);
		if (tipoReferencia == 'C'){
			resultado = new ModelAndView("redirect:/informes.html").addObject("idPoliza", idpoliza).addObject("method", "doInformePolizaComplementaria");
		} else {
			resultado = new ModelAndView("redirect:/informes.html").addObject("idPoliza", idpoliza).addObject("method", "doInformePoliza");
		}
		LOGGER.debug("end - operacion imprimir");
		return resultado;
		
	}
	
	private ModelAndView  salir(final Poliza poliza ){
		LOGGER.debug("init - operacion salir");
		ModelAndView resultado = null;
//		Debloqueamos la poliza al pasarla a provisional
		poliza.setBloqueadopor(null);
		poliza.setFechabloqueo(null);
//		Actualizamos el estado de la Poliza
		polizaManager.savePoliza(poliza);
		
		resultado = new ModelAndView("redirect:/seleccionPoliza.html");
		
		LOGGER.debug("end - operacion salir");
		return resultado;
	}
	
	public final void setPolizaManager(final PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}

	public void setMetodoPagoController(MetodoPagoController metodoPagoController) {
		this.metodoPagoController = metodoPagoController;
	}

	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}


}
