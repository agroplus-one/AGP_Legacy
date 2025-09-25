package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.managers.impl.AseguradoManager;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class PagoPolizaController extends MetodoPagoController {

	// Constantes
	private static final String UTF_8 = "UTF-8";
	private static final String ORIGENLLAMADA = "origenllamada";
	private static final String ES_SAECA_VAL = "esSaecaVal";
	private static final String IMPORTE_PRIMER_PAGO_CLIENTE = "importePrimerPagoCliente";
	private static final String NETO_TOMADOR_FINANCIADO_AGR = "netoTomadorFinanciadoAgr";
	private static final String METHOD = "method";
	private static final String VIENE_DE_UTILIDADES = "vieneDeUtilidades";
	private static final String OFICINA = "oficina";
	private static final String IMPORTE_POLIZA = "importePoliza";
	private static final String VOLVER_PAGOS = "volverPagos";
	private static final String ID_ENVIO = "idEnvio";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String OPERACION = "operacion";
	private static final String IDPOLIZA = "idpoliza";
	
	private static final Log LOGGER = LogFactory.getLog(PagoPolizaController.class);
	private PagoPolizaManager pagoPolizaManager;
	private AseguradoManager aseguradoManager;
	private MetodoPagoController metodoPagoController;	

	public PagoPolizaController() {
		super();
		setCommandClass(PagoPoliza.class);
		setCommandName("pagoPolizaBean");
	} 

	@Override
	protected ModelAndView handle(HttpServletRequest request,HttpServletResponse response, Object object, BindException exception) throws Exception {
		LOGGER.debug("inicio - PagoPolizaController");
		
		final Map<String, Object> parameters = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		final String idpoliza = StringUtils.nullToString(request.getParameter(IDPOLIZA));
		ModelAndView resultado;
		boolean revisar = false;
		String operacion = request.getParameter(OPERACION);
		/*POPUP FORMA DE PAGO*/
		if ("doValidaBancoDestinoAjax".equals(operacion)) {
			doValidaBancoDestinoAjax(request, response);
			return null;
		}else if ("guardaDatosCuentaAjax".equalsIgnoreCase(operacion)) {
			guardaDatosCuentaAjax(request, response);
			return null;
		}else if("isPagoCCPermitidoAjax".equalsIgnoreCase(operacion)){
			isPagoCCPermitidoAjax(request, response);
			return null;
		}else if ("ValidaEntidadPermitidaAjax".equalsIgnoreCase(operacion)) {
			doValidaEntidadPermitidaAjax(request, response);
			return null;
		}
		
		final Poliza poliza = pagoPolizaManager.getPolizaById(new Long(idpoliza));	
		
		// Si no tenemos asegurado en sesion, redirigimos a la pantala de carga de asegurados
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		Asegurado aseguradoSesion ;
		if (usuario.getAsegurado()!= null){
			aseguradoSesion = usuario.getAsegurado();
		}else{
			aseguradoSesion = poliza.getAsegurado();
		}
		
		Long id = aseguradoSesion.getId();
		aseguradoSesion = this.aseguradoManager.getAsegurado(id);
		
		if (null == aseguradoSesion)
			return new ModelAndView("redirect:/cargaAsegurado.html");

		String grabacionProvisional = request.getParameter("grabacionProvisional");
		Long idEnvio= null;
		String idEnvioStr="";
		PagoPoliza pagoPolizaBean = (PagoPoliza) object;
		pagoPolizaBean.setPoliza(poliza);
		parameters.put(MODO_LECTURA, request.getParameter(MODO_LECTURA));
		parameters.put("cicloPoliza",request.getParameter("cicloPoliza"));
		if (request.getParameter(ID_ENVIO) != null){
			idEnvioStr= request.getParameter(ID_ENVIO);
			if (!idEnvioStr.equals("") && !idEnvioStr.equals("N/D"))
				idEnvio = Long.parseLong(idEnvioStr);
		}
		parameters.put(ID_ENVIO, idEnvio);
		parameters.put("isPagoFraccionado",pagoPolizaManager.compruebaPagoFraccionado(poliza));
		
		BigDecimal[] auxEnts = new BigDecimal[] { BigDecimal.valueOf(4000),
				BigDecimal.valueOf(5000), BigDecimal.valueOf(6000),
				BigDecimal.valueOf(7000), BigDecimal.valueOf(8000) };
		
		if (Constants.USUARIO_EXTERNO.equals(usuario.getExterno())
				&& Arrays.asList(auxEnts).contains(
						usuario.getSubentidadMediadora().getEntidad()
								.getCodentidad())) {
			pagoPolizaBean.getPoliza().setOficina(
					usuario.getOficina().getId().getCodoficina().toString());
		} else {
			if (!StringUtils.nullToString(request.getParameter("oficinaccc"))
					.equals(""))
				pagoPolizaBean.getPoliza().setOficina(
						request.getParameter("oficinaccc"));
		}
		
		
		
		String pagoManual=request.getParameter("pagoManual");
		if("SI".equals(pagoManual)){
			pagoPolizaBean.setTipoPago(Constants.PAGO_MANUAL);
		}else{
			//
			revisar = true;
			Set<PagoPoliza> pagoPolizas = poliza.getPagoPolizas();
			for (PagoPoliza pag: pagoPolizas){
				if (null != pag.getTipoPago()) {
					if (pag.getTipoPago().equals(Constants.CARGO_EN_CUENTA)) {
						revisar = true;
					}else {
						pagoPolizaBean.setTipoPago(Constants.PAGO_MANUAL);
						revisar = false;
					}
				}
				if (null != pag.getEnvioIbanAgro()){
					pagoPolizaBean.setEnvioIbanAgro(pag.getEnvioIbanAgro());
				}
			}
			//
			if (revisar) {
				if (isPagoCCAllowed(usuario, poliza)) {
					pagoPolizaBean.setTipoPago(Constants.CARGO_EN_CUENTA); 
				} else {
					pagoPolizaBean.setTipoPago(Constants.PAGO_MANUAL); 
				}
			}
		}
		
		
		// Grabar da de alta los datos de pago en las entidades correspondientes
		// Se invocaran sucesivamente los servicios remotos de validacion y calculo.
		// No se admitiran errores de tramite. Si se produjesen, se actuaria como se
		// indico en el caso de errores de rechazo, dando la lista de errores y retrocediendo 
		// a la lista de parcelas.
		
		if ("grabar".equalsIgnoreCase(operacion)) {
			//recojo las fechas del cliente en el formulario
			String fechaPrimerPago = request.getParameter("fecha1");
			String fechaSegundoPago = request.getParameter("fechasegundopago1");
			String volverPagos = StringUtils.nullToString(request.getParameter(VOLVER_PAGOS));
			String importePoliza = request.getParameter(IMPORTE_POLIZA);
			BigDecimal tipoPago = new BigDecimal(0);//Para grabar datos de pago éste de be de ser siempre cargo en cuenta
			
			if (importePoliza != null && !"".equals(importePoliza)) {
				BigDecimal importe = new BigDecimal(importePoliza.replace(",", "."));
				pagoPolizaBean.setImporte(importe);
				
			}
			parameters.put(VOLVER_PAGOS, volverPagos);
			if(null==poliza.getPagoPolizas() || poliza.getPagoPolizas().size()==0){
				
				if(poliza.getEsFinanciada().equals('S')){
					pagoPolizaBean.setEnvioIbanAgro('S');
					pagoPolizaBean.setDestinatarioDomiciliacion('A');
				}
				
				pagoPolizaManager.savePagoPoliza(poliza, fechaPrimerPago, pagoPolizaBean.getImporte(),
						pagoPolizaBean.getCccbanco(), pagoPolizaBean.getCccbanco2(), pagoPolizaBean.getPctprimerpago(),
						fechaSegundoPago, pagoPolizaBean.getPctsegundopago(), request.getParameter(OFICINA),
						pagoPolizaBean.getIban(), pagoPolizaBean.getIban2(), tipoPago, false, pagoPolizaBean.getBanco(),
						pagoPolizaBean.getEnvioIbanAgro(), pagoPolizaBean.getDestinatarioDomiciliacion(),
						pagoPolizaBean.getTitularCuenta());
			}else{
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				PagoPoliza pagoPoliza = poliza.getPagoPolizas().iterator().next();
				pagoPoliza.setTipoPago(tipoPago);
				pagoPoliza.setBanco(null);
				if(poliza.getEsFinanciada().equals('S')){
					pagoPoliza.setFormapago(Constants.FORMA_PAGO_FINANCIADO);
				}else{
					pagoPoliza.setFormapago(Constants.FORMA_PAGO_ALCONTADO);
				}
				
				if (!StringUtils.nullToString(fechaPrimerPago).equals("")) {
					pagoPolizaBean.setFecha(df.parse(fechaPrimerPago));
				}				
				
				if (null!=pagoPolizaBean.getPctprimerpago()) pagoPoliza.setPctprimerpago(pagoPolizaBean.getPctprimerpago());
				if (null!=pagoPolizaBean.getFecha()) pagoPoliza.setFecha(pagoPolizaBean.getFecha());
				if (null!=pagoPolizaBean.getPctsegundopago()) {
					pagoPoliza.setPctsegundopago(pagoPolizaBean.getPctsegundopago());
					if (!StringUtils.nullToString(fechaSegundoPago).equals("")) {
						pagoPoliza.setFechasegundopago(df.parse(fechaSegundoPago));
					}
				}else{
					pagoPoliza.setPctsegundopago(null);
					pagoPoliza.setFechasegundopago(null);
				}
								
				if (!StringUtils.nullToString(request.getParameter(OFICINA)).equals("")) {
					Integer oficina = new Integer(StringUtils.nullToString(request.getParameter(OFICINA)));					
						pagoPolizaBean.getPoliza().setOficina(String.format("%04d", oficina.intValue()));
						pagoPoliza.getPoliza().setOficina(pagoPolizaBean.getPoliza().getOficina());					
				}
				
				pagoPolizaManager.savePagoPoliza(pagoPoliza);
			}
			if ("true".equals(grabacionProvisional)){
				parameters.put("grabacionProvisional", "true");
				//parameters.put("idEnvio", idEnvio);
			}

			parameters.put(IDPOLIZA, poliza.getIdpoliza());
			parameters.put(OPERACION, "importes");
			parameters.put(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
			parameters.put("primPago", request.getParameter("primPago"));
			//parameters.put("origenllamada", "pago");
			return (new ModelAndView("redirect:/eleccionFormaPago.html")).
					addAllObjects(parameters).addObject(METHOD, "doMostrar");

			
		}else if ("altaCCC".equalsIgnoreCase(operacion)) {
			parameters.put(IDPOLIZA, poliza.getIdpoliza());
			parameters.put("volverPago", "SI");
			parameters.put("idAsegurado", aseguradoSesion.getId());
			return new ModelAndView("redirect:/datoAsegurado.html").addAllObjects(parameters);
		
		}else if ("volverDePagos".equalsIgnoreCase(operacion)){
			parameters.put(IDPOLIZA, poliza.getIdpoliza());
			parameters.put(VOLVER_PAGOS, "true");
			parameters.put(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
			return (new ModelAndView("redirect:/eleccionFormaPago.html")).addAllObjects(parameters).addObject(METHOD,"doMostrar");
		}
		//cargamos los datos en el bean
		pagoPolizaManager.loadPagoPoliza(pagoPolizaBean, poliza);
		
		if (poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0)
			pagoPolizaBean.setId(((PagoPoliza)poliza.getPagoPolizas().toArray()[0]).getId());
		
		//comprobamos que la cuenta tiene 20 digitos para evitar errores
		if (pagoPolizaBean.getCccbanco() != null && pagoPolizaBean.getCccbanco().length() == 20){
			
			parameters.put("cuenta1", pagoPolizaBean.getCccbanco().substring(0, 4));
			parameters.put("cuenta2", pagoPolizaBean.getCccbanco().substring(4, 8));
			parameters.put("cuenta3", pagoPolizaBean.getCccbanco().substring(8, 12));
			parameters.put("cuenta4", pagoPolizaBean.getCccbanco().substring(12,16));
			parameters.put("cuenta5", pagoPolizaBean.getCccbanco().substring(16,20));
			parameters.put("AltaCuenta", false);
		}else{
			parameters.put("AltaCuenta", true);
		}
		
//		GrupoEntidades para los distintos perfiles
		String grupoEntidades = StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(),false,false);
//		Calculamos los importes que se deben de introducir para primer y segundo pago.

		if(request.getParameter(NETO_TOMADOR_FINANCIADO_AGR) != null && !request.getParameter(NETO_TOMADOR_FINANCIADO_AGR).equals("")){
			parameters.put(IMPORTE_PRIMER_PAGO_CLIENTE, request.getParameter(NETO_TOMADOR_FINANCIADO_AGR));
			parameters.put(IMPORTE_POLIZA,request.getParameter(NETO_TOMADOR_FINANCIADO_AGR));
		}
		else if(request.getParameter(MODO_LECTURA) != null && request.getParameter(MODO_LECTURA).equals(MODO_LECTURA)){
			if(!request.getParameter("formaDePago").equals("F")){
				if(request.getParameter(IMPORTE_PRIMER_PAGO_CLIENTE) != null){
					parameters.put(IMPORTE_PRIMER_PAGO_CLIENTE, request.getParameter(IMPORTE_PRIMER_PAGO_CLIENTE));
					parameters.put(IMPORTE_POLIZA,pagoPolizaBean.getImporte());
				}
				else{
					parameters.put(IMPORTE_PRIMER_PAGO_CLIENTE, pagoPolizaBean.getImporte());
					parameters.put(IMPORTE_POLIZA,pagoPolizaBean.getImporte());
				}
			}
			else{
				parameters.put(IMPORTE_PRIMER_PAGO_CLIENTE, getPrimerPago(poliza));
				parameters.put(IMPORTE_POLIZA,pagoPolizaBean.getImporte());
			}
		}
		else{
			if (request.getParameter(ES_SAECA_VAL) != null && !request.getParameter(ES_SAECA_VAL).equals("")){
				parameters.put(IMPORTE_PRIMER_PAGO_CLIENTE, request.getParameter(ES_SAECA_VAL));
				parameters.put(IMPORTE_POLIZA,request.getParameter(ES_SAECA_VAL));
			}
			else{
				parameters.put(IMPORTE_PRIMER_PAGO_CLIENTE, getPrimerPago(poliza));
				parameters.put(IMPORTE_POLIZA,pagoPolizaBean.getImporte());
			}
			
		}
		
		parameters.put("importeSegundoPagoCliente", getSegundoPago(poliza));
		parameters.put("perfil", usuario.getPerfil().substring(4));
		parameters.put("grupoEntidades", grupoEntidades);
		if(poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_COMPLEMENTARIO))
			parameters.put("polizaComplementaria", true);
		else
			parameters.put("polizaComplementaria", false);
		
		parameters.put(MODO_LECTURA, StringUtils.nullToString(request.getParameter(MODO_LECTURA)));
		parameters.put(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
		parameters.put(ORIGENLLAMADA, StringUtils.nullToString(request.getParameter(ORIGENLLAMADA)));
		parameters.put(NETO_TOMADOR_FINANCIADO_AGR, StringUtils.nullToString(request.getParameter(NETO_TOMADOR_FINANCIADO_AGR)));
		resultado = new ModelAndView("/moduloPolizas/polizas/pago/pagos", "pagoPolizaBean", pagoPolizaBean);
		resultado.addAllObjects(parameters);
		
		if ("grProvisional".equalsIgnoreCase(operacion)) {
			LOGGER.debug("operacion: "+operacion);
			resultado=grabacionProvisional(request, pagoPolizaBean, parameters, bundle, poliza);
		}
		
		
		LOGGER.debug("end - PagoPolizaController");
		return resultado;
	}
	
	
	private ModelAndView grabacionProvisional(HttpServletRequest request,
			PagoPoliza pagoPolizaBean, final Map<String, Object> parameters, final ResourceBundle bundle,
			final Poliza poliza) throws Exception {
		ModelAndView resultado=null;
		if (pagoPolizaBean.getCccbanco() != null && pagoPolizaBean.getCccbanco().length() != 20
				&& pagoPolizaBean.getIban() != null && pagoPolizaBean.getIban().length() != 4 || 
				pagoPolizaBean.getCccbanco()== null || pagoPolizaBean.getIban() == null){
			pagoPolizaBean.setIban("ES");
		
		}
		parameters.put(OPERACION, "grabar");
		parameters.put(IDPOLIZA, poliza.getIdpoliza());
		parameters.put("idPagoPolizaBean", pagoPolizaBean.getId());
		String fechaFinal1=null;
		String fechaFinal2=null;
		
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
		if (pagoPolizaBean.getFecha() !=null){
			fechaFinal1= sf.format(pagoPolizaBean.getFecha());
			parameters.put("fecha1", fechaFinal1);
		}
		if (pagoPolizaBean.getFechasegundopago() !=null){
			fechaFinal2= sf.format(pagoPolizaBean.getFechasegundopago());
			parameters.put("fechasegundopago1", fechaFinal2);
		}
		//Si la poliza no tiene asignada oficina, la calculamos del numero de cuenta.
		//En caso contrario, se habra guardado desde el apartado "c/c - oficina"
		String oficina = "";
		if (StringUtils.nullToString(poliza.getOficina()).equals("")){
			oficina = StringUtils.nullToString(pagoPolizaBean.getCccbanco().substring(4, 8));
		}
		
		// Si se elegido fraccionamiento vía Agroseguro
		String netoTomadorFinanciadoAgr = StringUtils.nullToString((request.getParameter(NETO_TOMADOR_FINANCIADO_AGR)));
		BigDecimal netoTomFinanAgrNum = null;
		if (!"".equals(netoTomadorFinanciadoAgr)) {
			try {
				netoTomFinanAgrNum = new BigDecimal (netoTomadorFinanciadoAgr);
				String newval = request.getParameter(NETO_TOMADOR_FINANCIADO_AGR);
				parameters.put(IMPORTE_PRIMER_PAGO_CLIENTE, newval);

				
			}
			catch (Exception e) {
				logger.debug("No existe neto tomador financiado o es incorrecto");
			}
		}
		
		pagoPolizaManager.savePagoPoliza(poliza, fechaFinal1,
				netoTomFinanAgrNum != null ? netoTomFinanAgrNum : pagoPolizaBean.getImporte(),
				pagoPolizaBean.getCccbanco(), pagoPolizaBean.getCccbanco2(), pagoPolizaBean.getPctprimerpago(),
				fechaFinal2, pagoPolizaBean.getPctsegundopago(), oficina, pagoPolizaBean.getIban(),
				pagoPolizaBean.getIban2(), pagoPolizaBean.getTipoPago(), false, pagoPolizaBean.getBanco(),
				pagoPolizaBean.getEnvioIbanAgro(), null, null);
		
		if(poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_COMPLEMENTARIO)){
			parameters.put(METHOD, "doValidar");
			parameters.put(IDPOLIZA, poliza.getIdpoliza());
			parameters.put(ORIGENLLAMADA, "pago");
			resultado=  new ModelAndView("redirect:/webservicesCpl.html").addAllObjects(parameters);
		}else{
			//llamamos al web service de validacion pero en este caso no se admitiran errores
			parameters.put(IDPOLIZA, poliza.getIdpoliza());
			parameters.put(OPERACION, "");
			parameters.put("grProvisional", "true");
			parameters.put("netoTomFinanAgrNum", netoTomFinanAgrNum);
			resultado= new ModelAndView("redirect:/grabacionPoliza.html").addAllObjects(parameters);
		}
		return resultado;
	}

	public void validaFormaPagoAjax(final HttpServletRequest request, final HttpServletResponse response) {
		try{
			
			// validamos si el banco destino existe
			JSONObject mensajes = pagoPolizaManager.validaFormaPago(request.getParameter(IDPOLIZA));

		    response.setCharacterEncoding(UTF_8);
		    this.getWriterJSON(response, mensajes);
		}
		catch(IOException e){
			logger.error("validaFormaPagoAjax - Ocurrio un error al escribir el resultado de la validacion.", e);			
    	}
		catch(Exception e){
			logger.error("validaFormaPagoAjax - Ocurrio un error inesperado.", e);			
    	}
		
	}
	
	public void isPagoCCPermitidoAjax(final HttpServletRequest request, final HttpServletResponse response) {
		try{			
			// validamos si el banco destino existe
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			String idpoliza = StringUtils.nullToString(request.getParameter(IDPOLIZA));
			Poliza poliza = pagoPolizaManager.getPolizaById(new Long(idpoliza));
			Boolean res = metodoPagoController.isPagoCCAllowed(usuario, poliza);
			
			JSONObject params =new JSONObject();
			params.put("esPermitido", res);

		    response.setCharacterEncoding(UTF_8);
		    this.getWriterJSON(response, params);
		
		}catch(Exception e){
			logger.error("isPagoCCPermitidoAjax - Ocurrio un error inesperado.", e);			
    	}
		
	}
	

	
	
	public void doValidaBancoDestinoAjax (final HttpServletRequest request, final HttpServletResponse response) {
		
		try{
			// validamos si el banco destino existe
		    boolean existe = pagoPolizaManager.existeBancoDestino(request.getParameter("bancoDestino"));

		   response.setCharacterEncoding(UTF_8);			
			response.getWriter().write(new Boolean(existe).toString());
		}
		catch(IOException e){
			logger.error("doValidaBancoDestinoAjax - Ocurrio un error al escribir el resultado de la validacion.", e);			
    	}
		catch(Exception e){
			logger.error("doValidaBancoDestinoAjax - Ocurrio un error inesperado.", e);			
    	}
	}
	
//	public void guardaDatosManualAjax (final HttpServletRequest request, final HttpServletResponse response) {
//		
//		try{
//			JSONObject mensajes = new JSONObject();
//			String importe = request.getParameter("importe");
//			String fechaPago = request.getParameter("fechaPago");
//			String iban = request.getParameter("iban");
//			String cccbanco = request.getParameter("cccbanco");
//			String banco = request.getParameter("banco");
//			String idpoliza = request.getParameter("idpoliza");
//			Character envioIBANAgr = null;
//			String strEnvioIBANAgr = request.getParameter("envioIBANAgr");
//			if (strEnvioIBANAgr != null && strEnvioIBANAgr.equals("true")){
//				envioIBANAgr = 'S';
//			}else{
//				envioIBANAgr = 'N';
//			}
//			final Poliza poliza = pagoPolizaManager.getPolizaById(new Long(idpoliza));
//			pagoPolizaManager.savePagoPoliza(poliza, fechaPago, new BigDecimal(importe), cccbanco, new BigDecimal(100), 
//						null, null, null, iban, new BigDecimal(1),true,new BigDecimal(banco),envioIBANAgr);
//		   
//		   response.setCharacterEncoding("UTF-8");		
//		   mensajes.put("cccbanco", cccbanco);
//		   mensajes.put("iban", iban);
//		   mensajes.put("tipoPagoGuardado", "manual");
//		   this.getWriterJSON(response, mensajes);
//		   
//		}
//		catch(Exception e){
//			logger.error("guardaDatosManualAjax - Ocurrio un error inesperado.", e);			
//    	}
//	}
	private void guardaDatosCuentaAjax(HttpServletRequest request,
			HttpServletResponse response) {
		try{
			JSONObject mensajes = new JSONObject();
			String idpoliza = request.getParameter(IDPOLIZA);
			Character envioIBANAgr = null;
			String strEnvioIBANAgr = request.getParameter("envioIBANAgr");
			if (strEnvioIBANAgr != null && strEnvioIBANAgr.equals("true")){
				envioIBANAgr = 'S';
			}else{
				envioIBANAgr = 'N';
			}
			pagoPolizaManager.guardaDatosCuenta(new Long(idpoliza), envioIBANAgr);
		    
		    response.setCharacterEncoding(UTF_8);	
		    mensajes.put("tipoPagoGuardado", "cargoEnCuenta");
		    response.getWriter().write("OK");
		    this.getWriterJSON(response, mensajes);
		}
		catch(IOException e){
			logger.error("guardaDatosManualAjax - Ocurrio un error al escribir el resultado de la validacion.", e);			
    	}
		catch(Exception e){
			logger.error("guardaDatosManualAjax - Ocurrio un error inesperado.", e);			
    	}
		
	}
	
	/**
	 * Visualiza el importe del primer pago, que se calcula aplicando
	 * el % del primer pago al importe total de la poliza.
	 * @param poliza
	 * @return
	 */
	private BigDecimal getPrimerPago(Poliza poliza){
		LOGGER.debug("init - getPrimerPago");
		BigDecimal importe = poliza.getImporte(), pct = null;;
		
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getPctprimerpago() != null){
					pct = pp.getPctprimerpago();
				}
			}
		}
		else{
			pct = poliza.getColectivo().getPctprimerpago();
		}
		LOGGER.debug("end - getPrimerPago");
		return (pct != null && importe != null) ? pct.multiply(importe).divide(new BigDecimal(100)) : null;
	}
	
	/**
	 * Visualiza el importe del segundo pago, que se calcula aplicando
	 * el % del segundo pago al importe total de la poliza.
	 * @param poliza
	 * @return
	 */	
	private BigDecimal getSegundoPago(Poliza poliza){
		LOGGER.debug("init - getSegundoPago");
		BigDecimal importe = poliza.getImporte(), pct = null;
		
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getPctsegundopago() != null){
					pct = pp.getPctsegundopago();
				}
			}
		}
		else{
			if (poliza.getColectivo().getPctsegundopago() != null){
				pct = poliza.getColectivo().getPctsegundopago();
			}
		}
		LOGGER.debug("end - getSegundoPago");
		if (importe != null && pct != null)
			return pct.multiply(importe).divide(new BigDecimal(100));
		else
			return null;
	}
	
public void doValidaEntidadPermitidaAjax (final HttpServletRequest request, final HttpServletResponse response) {
		logger.debug("validaEntidadPermitida");
	try{
		String idpoliza=request.getParameter(IDPOLIZA);
		String entidad= request.getParameter("ent");
		// validamos si el banco destino existe
		JSONObject mensajes = pagoPolizaManager.validaEntidadPermitida(idpoliza, entidad);

	    response.setCharacterEncoding(UTF_8);
	    this.getWriterJSON(response, mensajes);
	}
	catch(IOException e){
		logger.error("validaFormaPagoAjax - Ocurrio un error al escribir el resultado de la validacion.", e);			
	}
	catch(Exception e){
		logger.error("validaFormaPagoAjax - Ocurrio un error inesperado.", e);			
	}
	}
	
	public final void setPagoPolizaManager(final PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}
	
	public void setAseguradoManager(AseguradoManager aseguradoManager) {
		this.aseguradoManager = aseguradoManager;
	}

	public void setMetodoPagoController(MetodoPagoController metodoPagoController) {
		this.metodoPagoController = metodoPagoController;
	}

}
