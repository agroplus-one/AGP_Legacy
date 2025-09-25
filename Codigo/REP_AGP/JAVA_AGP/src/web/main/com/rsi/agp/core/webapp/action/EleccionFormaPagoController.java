package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.impl.EleccionFormaPagoManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.PolizaRCManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.PolizaRCManager.SumaAseguraImportePolizaRC;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.DatosAval;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;



public class EleccionFormaPagoController extends BaseMultiActionController {

	private EleccionFormaPagoManager eleccionFormaPagoManager;
	private PolizaManager polizaManager;
	private PasarADefinitivaPlzController pasarADefinitivaPlzController;
	private PolizaRCManager polizaRCManager;
	private IHistoricoEstadosManager historicoEstadosManager;
	
	public ModelAndView doMostrar(HttpServletRequest request,HttpServletResponse response, Object object) throws Exception {
		ModelAndView mv;
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		Long idPoliza = null;
		String modolectura = request.getParameter("modoLectura");
		String cicloPoliza = request.getParameter("cicloPoliza");
		
		if (!"".equals(StringUtils.nullToString(request.getParameter("idpoliza")))) {
			idPoliza = Long.parseLong(request.getParameter("idpoliza"));
		}

		Map<String, Object> params = getParametros(request);
			 
		parameters = eleccionFormaPagoManager.obtenerDatosFormaPago(usuario, idPoliza, request);

		// GrupoEntidades para los distintos perfiles
		String grupoEntidades = StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(),false,false);		
		parameters.put("grupoEntidades", grupoEntidades);
		parameters.put("perfil", usuario.getPerfil().substring(4));
		parameters.put("modoLectura", modolectura);
		if (modolectura.equals("modoLectura"))
				parameters.put("verFormaPagoCliente", false);
		parameters.put("idpoliza", idPoliza);
		parameters.put("cicloPoliza", cicloPoliza);
		Date fecLimite= null;
		if (usuario.getFechaLimite() != null){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(usuario.getFechaLimite());
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			fecLimite= calendar.getTime();
		}
		
		parameters.putAll(this.sumaAseguradaImportePolizaRC(idPoliza));
		
		parameters.put("fechalimiteUsuario", fecLimite);
		parameters.put("impMinimoUsuario", usuario.getImpMinFinanciacion());
		parameters.put("impMaximoUsuario", usuario.getImpMaxFinanciacion());
		logger.debug("codUsu: "  +usuario.getCodusuario());
		
		parameters.put("cargoCuenta", usuario.getSubentidadMediadora().getCargoCuenta());
		parameters.put("permiteOficina", usuario.getOficina().getPagoManual());
		
		parameters.put("isFechaEnvioPosteriorSep2020", request.getParameter("isFechaEnvioPosteriorSep2020"));
		
		if((Boolean) parameters.get("lineaContratSuperior2019")){
			mv = new ModelAndView("moduloPolizas/polizas/pago/eleccionFormaPago2019").addAllObjects(params).addAllObjects(parameters);
		}else{
			mv = new ModelAndView("moduloPolizas/polizas/pago/eleccionFormaPago").addAllObjects(params).addAllObjects(parameters);
		}
		
		//TODO quitar cuando suba a TEST
		//mv = new ModelAndView("moduloPolizas/polizas/pago/eleccionFormaPago2019").addAllObjects(params).addAllObjects(parameters);
		
		return mv;
	}

	private Map<String, Object> sumaAseguradaImportePolizaRC(Long idPoliza) throws Exception {
		Map<String, Object> datos = new HashMap<String, Object>();
		if(this.polizaRCManager.polizaPplTieneRC(idPoliza)){
			SumaAseguraImportePolizaRC sumaAseguradaImportePolizaRC = this.polizaRCManager.getSumaAseguradaImportePolizaRCPorPolizaPpl(idPoliza);
			datos.put("tieneRC", true);
			datos.put("sumaAseguradaRC", sumaAseguradaImportePolizaRC.getSumaAsegurada());
			datos.put("importeRC", sumaAseguradaImportePolizaRC.getImporte());
		}
		return datos;
	}
	
	public ModelAndView doGuardar(HttpServletRequest request,HttpServletResponse response, PagoPoliza pagoPoliza) throws Exception {
		ModelAndView mv=null;
		
//		Obtiene los datos de la forma de pago informados en la pantalla encapsulados en el bean 
//		PagosPoliza.java y llama al nuevo metodo EleccionFormaPagoManager.guardarDatosFormaPago 
//		para que se guarden en BBDD.
//
//		Si la llamada a este metodo devuelve algun error, volvera a la pantalla eleccionFormaPago.jsp 
//		mostrando la alerta correspondiente.
//
//		Si el guardado es correcto, redirigira a PasarADefinitivaPlzController.doPasarADefinitiva.
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String idpoliza = request.getParameter("idpoliza");
			String oficina=StringUtils.nullToString(request.getParameter("oficina"));
			String importe = StringUtils.nullToString(request.getParameter("importe1"));
			
			if (pagoPoliza.getEnvioIbanAgro() == null){
				String indEnvIbanAux = StringUtils.nullToString(request.getParameter("indEnvIbanAux"));
				if (indEnvIbanAux != "" && indEnvIbanAux !=null){
					pagoPoliza.setEnvioIbanAgro(indEnvIbanAux.charAt(0));
				}
			}

			BigDecimal bdImporte = importe.length()>0 ? new BigDecimal(importe) : new BigDecimal(0);
			
			//Si esta por encima del 1 Marzo 2019 la linea de contratacion y el tipopago es 2
			//le asigno el valor 'S' al envio iban a agroseguro
			String strLineaContratSuperior2019 = StringUtils.nullToString(request.getParameter("idLineaContratSuperior2019"));
			
			String destinatarioDomic = StringUtils.nullToString(request.getParameter("idDestinatarioDomiciliacion"));
			String titularCuen = StringUtils.nullToString(request.getParameter("idValorTitularCuenta"));
			
			if (Constants.DOMICILIACION_AGRO.equals(pagoPoliza.getTipoPago()) && strLineaContratSuperior2019.equals("true")) {
				pagoPoliza.setEnvioIbanAgro('S');
				pagoPoliza.setIban(request.getParameter("ibanAux"));
				pagoPoliza.setIban2(request.getParameter("ibanAux2"));
				if (StringUtils.isNullOrEmpty(destinatarioDomic) || StringUtils.isNullOrEmpty(titularCuen)) {
					DatoAsegurado da = AseguradoUtil.obtenerDatoAsegurado(pagoPoliza.getPoliza());
					destinatarioDomic = da.getDestinatarioDomiciliacion();
					titularCuen = da.getTitularCuenta();
				}
				pagoPoliza.setDestinatarioDomiciliacion(destinatarioDomic.charAt(0));
				pagoPoliza.setTitularCuenta(titularCuen);
			}
			
			if(Constants.PAGO_MANUAL.equals(pagoPoliza.getTipoPago())){
				String fechaPago=StringUtils.nullToString(request.getParameter("fecha"));
				
				if (!StringUtils.nullToString(fechaPago).equals("")) {
					pagoPoliza.setFecha(df.parse(fechaPago));
				}
				String importePagado = StringUtils.nullToString(request.getParameter("importePago"));
				BigDecimal bdImportePagado = importePagado.length()>0 ? new BigDecimal(importePagado) : pagoPoliza.getImporte();
				
				pagoPoliza.setImportePago(bdImportePagado);
				
				/*ESC-7612 DNF 06/04/2020*/
				if(null != destinatarioDomic && !destinatarioDomic.equals("")) {
					pagoPoliza.setDestinatarioDomiciliacion(destinatarioDomic.charAt(0));
				}
				/*FIN ESC-7612 DNF 06/04/2020*/
				
				//valores por defecto para pago manual
				pagoPoliza.setPctprimerpago(new BigDecimal(100));
				pagoPoliza.setPctsegundopago(null);
				pagoPoliza.setFechasegundopago(null);
			} else if(Constants.CARGO_EN_CUENTA.equals(pagoPoliza.getTipoPago())) {
				
				logger.debug("financiarOK en doGuardar: " + request.getParameter("financiarOK"));
				logger.debug("pctprimerpago en doGuardar: " + request.getParameter("pctprimerpago"));
				if (!Boolean.TRUE.equals(Boolean.valueOf(request.getParameter("financiarOK"))) && (!BigDecimal
						.valueOf(100).equals(new BigDecimal(request.getParameter("pctprimerpago")))
						|| !df.format(Calendar.getInstance().getTime()).equals(request.getParameter("fecha1")))) {
					parameters.put(Constants.KEY_ALERTA, "Usuario sin permiso de financianción.");
					return doMostrar(request, response, pagoPoliza).addAllObjects(parameters);
				}
				
				String fechaPago=StringUtils.nullToString(request.getParameter("fecha1"));
				String fechasegundopago=StringUtils.nullToString(request.getParameter("fechasegundopago1"));
				if (!StringUtils.nullToString(fechaPago).equals("")) {
					pagoPoliza.setFecha(df.parse(fechaPago));
				}
				if (!StringUtils.nullToString(fechasegundopago).equals("")) {
					pagoPoliza.setFechasegundopago(df.parse(fechasegundopago));
				}else{// pct y fecha segundo pago nulos
					pagoPoliza.setFechasegundopago(null);
					pagoPoliza.setPctsegundopago(null);
				}
				pagoPoliza.setImportePago(bdImporte);
				
				/*ESC-7612 DNF 06/04/2020*/
				if(null != destinatarioDomic && !destinatarioDomic.equals("")) {
					pagoPoliza.setDestinatarioDomiciliacion(destinatarioDomic.charAt(0));
				}
				/*FIN ESC-7612 DNF 06/04/2020*/
			}
			
			pagoPoliza.setImporte(bdImporte);
			
			// Pet. 54046 ** MODIF TAM (29.06.2018) ** Inicio //
			boolean esPolPrincipal = false;

			Poliza p = polizaManager.getPoliza(new Long(idpoliza));
			
			/* MODIF TAM (06.11.2018) ** Inicio */
			if(p.getPolizaPpal() == null){
				esPolPrincipal = true;
			}else{
				esPolPrincipal = false;
			}
			
			if(esPolPrincipal == false){
				
				for(PagoPoliza ppPpal :p.getPolizaPpal().getPagoPolizas()) {
					if (ppPpal.getEnvioIbanAgro() != null && ppPpal.getEnvioIbanAgro().equals('S')){
						pagoPoliza.setDomiciliado('S');
						pagoPoliza.setDestinatarioDomiciliacion(ppPpal.getDestinatarioDomiciliacion().charValue());
						pagoPoliza.setTitularCuenta(ppPpal.getTitularCuenta());
						pagoPoliza.setEnvioIbanAgro(ppPpal.getEnvioIbanAgro());
					}
					/*ESC-9332 DNF 30/04/2020 el destinatario domiciliacion se debe guardar siempre independientemente de la forma de pago elegida en las
					 * polizas complementarias*/
					if (null != ppPpal.getEnvioIbanAgro() && !ppPpal.getEnvioIbanAgro().equals('S')) {
						if(null != ppPpal.getDestinatarioDomiciliacion()) {
							pagoPoliza.setDestinatarioDomiciliacion(ppPpal.getDestinatarioDomiciliacion().charValue());
						}
					}	
					/*FIN ESC-9332 DNF 30/04/2020*/
				}
			}
			/* MODIF TAM (06.11.2018) ** Fin */			
			
			eleccionFormaPagoManager.guardarDatosFormaPago(pagoPoliza, new Long(idpoliza),oficina);
			
			
			mv = pasarADefinitivaPlzController.doPasarADefinitiva(request, response, p);
			
		} catch (Exception e) {
			parameters.put(Constants.KEY_ALERTA, "Error guardando los datos de la forma de pago");
			logger.debug("Error guardando los datos de la forma de pago. ", e);
			mv=doMostrar(request, response, pagoPoliza).addAllObjects(parameters);
		}
		
		return mv;
	}
	

	public ModelAndView doPasarADefPol(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv=null;
		logger.debug("... INIT doPasarADefPol ...");
		String idpoliza = request.getParameter("idpoliza");				
		Poliza p = polizaManager.getPoliza(new Long(idpoliza));
		mv = pasarADefinitivaPlzController.doPasarADefinitiva(request, response, p);		
		logger.debug("... FIN doPasarADefPol ...");
		return mv;
	}
	
	
	public ModelAndView doGuardarDatosAval(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String numeroAval = null;
		String importeAval = null;
		String idPoliza = null;
		ModelAndView mv=null;
		try{
			numeroAval = request.getParameter("numero_da");
			importeAval = request.getParameter("importe_da");
			idPoliza = request.getParameter("idpoliza");
				
			Poliza p = polizaManager.getPoliza(new Long(idPoliza));
				
			DatosAval datosAval = null;
				
			if(p.getDatosAval()!=null){
				datosAval = p.getDatosAval();
			}else{
				datosAval = new DatosAval();
				datosAval.setPoliza(p);
			}
			
			datosAval.setNumeroAval(new BigDecimal(numeroAval));
			datosAval.setImporteAval(new BigDecimal(importeAval));

			polizaManager.AddDatosAval(datosAval);
			p.setDatosAval(datosAval);
			mv= doMostrar(request, response, p);
			
		}catch(DAOException e){
			//errores.put(Constants.KEY_ALERTA, "Error inesperado al guardar los datos del aval");
			logger.error("Error inesperado al guardar los datos del aval de la poliza con id = " + idPoliza);
		}
		return mv;
	}
	
	/*DNF 23/02/2021 PET.70105.FII*/
	public ModelAndView grabacionProvisional(HttpServletRequest request,HttpServletResponse response) throws JSONException {
		
		logger.debug("*** INIT grabacionProvisional ...");
		
		//ModelAndView mv=null;
		JSONObject params = new JSONObject();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		
		// Actualizamos el estado de la poliza a grabacion provisional
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String idPoliza = request.getParameter("idpoliza");
		Poliza p = polizaManager.getPoliza(new Long(idPoliza));
		
		EstadoPoliza estadoPoliza = new EstadoPoliza();
		estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL);
		p.setEstadoPoliza(estadoPoliza);
		
		PagoPoliza pagoPol;
		logger.debug("p.getPagoPolizas(): " + p.getPagoPolizas());
		logger.debug("p.getPagoPolizas().size(): " + p.getPagoPolizas().size());
		if(p.getPagoPolizas() != null && p.getPagoPolizas().size()>0) {
			pagoPol = p.getPagoPolizas().iterator().next();
		}else {
			pagoPol = new PagoPoliza();
		}
		
		logger.debug("GrabacionProvisional - Valor de pagoPol.envioIban: "+pagoPol.getEnvioIbanAgro());
		
		pagoPol.setFormapago(request.getParameter("formapago").charAt(0));
		pagoPol.setCccbanco(request.getParameter("ccc"));
		pagoPol.setCccbanco2(request.getParameter("ccc2"));
		
		//actualizamos el pago poliza si no es financiada(si fuese financiada se actualiza ya en la financiacion), por si tuviese ya un pagopoliza financiado previo.
		if (!p.getEsFinanciada().equals('S')){
			pagoPol.setImporte(p.getImporte());
		}
		
		
		String destinatarioDomic = StringUtils.nullToString(request.getParameter("idDestinatarioDomiciliacion"));
		String titularCuen = StringUtils.nullToString(request.getParameter("idValorTitularCuenta"));
		String oficina=StringUtils.nullToString(request.getParameter("oficina"));
		
		//guardamos los datos relativos a la forma de pago
		BigDecimal tipoPago = new BigDecimal(request.getParameter("tipoPago"));
		pagoPol.setTipoPago(tipoPago);
		logger.debug("tipoPago: " + tipoPago);
		if(Constants.CARGO_EN_CUENTA.equals(tipoPago)) {//cargo en cuenta
			
			BigDecimal pctPrimerPagoCliente = new BigDecimal(request.getParameter("pctprimerpagocliente"));
			pagoPol.setPctprimerpago(pctPrimerPagoCliente);
			
			String pctsegundopagocliente = request.getParameter("pctsegundopagocliente");
			if( pctsegundopagocliente != null && !pctsegundopagocliente.equals("")) {
				pagoPol.setPctsegundopago(new BigDecimal(pctsegundopagocliente));
			}			
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			
			
			logger.debug("financiarOK en grabacionProvisional: " + request.getParameter("fOK"));
			logger.debug("pctprimerpago en grabacionProvisional: " + pctPrimerPagoCliente);
			if (!Boolean.TRUE.equals(Boolean.valueOf(request.getParameter("fOK")))
					&& (!BigDecimal.valueOf(100).equals(pctPrimerPagoCliente)
							|| !sdf.format(Calendar.getInstance().getTime()).equals(request.getParameter("fecha1")))) {
				params.put("grabProv", "false");
				params.put("mensaje", "Usuario sin permiso de financianción.");
				getWriterJSON(response, params);
				return null;
			}
			
			//fechas
			try {				
				Date fecha1 = sdf.parse(request.getParameter("fecha1"));
				pagoPol.setFecha(fecha1);
				String fechaSegundoPago = request.getParameter("fechasegundopago1");
				if( fechaSegundoPago != null && !fechaSegundoPago.equals("")) {
					pagoPol.setFechasegundopago(sdf.parse(request.getParameter("fechasegundopago1")));
				}
			} catch (ParseException e) {
				logger.error(e);
			}
			
			/* ESC-13211 ** Inicio */
			pagoPol.setEnvioIbanAgro(null);
		}
		if(Constants.PAGO_MANUAL.equals(tipoPago)) {//pago manual
			pagoPol.setBanco(new BigDecimal(request.getParameter("bancoDestino")));
			pagoPol.setImportePago(new BigDecimal(request.getParameter("importePago")));
			try {
				pagoPol.setFecha(new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("fechaPago")));
			} catch (ParseException e) {
				logger.error(e);
			}	
			//valores por defecto para pago manual
			pagoPol.setPctprimerpago(new BigDecimal(100));
			pagoPol.setPctsegundopago(null);
			pagoPol.setFechasegundopago(null);
		}
		if(Constants.DOMICILIACION_AGRO.equals(tipoPago)) {
			if (StringUtils.isNullOrEmpty(destinatarioDomic) || StringUtils.isNullOrEmpty(titularCuen)) {
				DatoAsegurado da = AseguradoUtil.obtenerDatoAsegurado(p);
				destinatarioDomic = da.getDestinatarioDomiciliacion();
				titularCuen = da.getTitularCuenta();
			}
			pagoPol.setEnvioIbanAgro('S');	
		}
		
		//ADD LOS DATOS A PAGO POLIZA
		pagoPol.setIban(request.getParameter("ibanAux"));
		pagoPol.setIban2(request.getParameter("ibanAux2"));
		pagoPol.setTitularCuenta(titularCuen);
		if(null != destinatarioDomic && !destinatarioDomic.equals("")) {
			pagoPol.setDestinatarioDomiciliacion(destinatarioDomic.charAt(0));
		}
		
		/// Calculamos totalSuperfice y se lo insertamos a la poliza al pasar a provisional
		BigDecimal totalSuperficie = null;
		totalSuperficie=polizaManager.getTotalSuperficie(p);
		if (totalSuperficie!=null){
			p.setTotalsuperficie(totalSuperficie);
		}

//		Actualizamos el estado de la Poliza
		polizaManager.savePoliza(p);

		historicoEstadosManager.insertaEstado(Tabla.POLIZAS, p.getIdpoliza(),usuario.getCodusuario(),Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL);
			
		boolean esPolPrincipal = false;
		
		if(p.getPolizaPpal() == null){
			esPolPrincipal = true;
		}else{
			esPolPrincipal = false;
		}
		
		if(esPolPrincipal == false){
			
			for(PagoPoliza ppPpal :p.getPolizaPpal().getPagoPolizas()) {
				if (ppPpal.getEnvioIbanAgro() != null && ppPpal.getEnvioIbanAgro().equals('S')){
					pagoPol.setDomiciliado('S');
					pagoPol.setDestinatarioDomiciliacion(ppPpal.getDestinatarioDomiciliacion().charValue());
					pagoPol.setTitularCuenta(ppPpal.getTitularCuenta());
					pagoPol.setEnvioIbanAgro(ppPpal.getEnvioIbanAgro());
				}
				
				if (null != ppPpal.getEnvioIbanAgro() && !ppPpal.getEnvioIbanAgro().equals('S')) {
					if(null != ppPpal.getDestinatarioDomiciliacion()) {
						pagoPol.setDestinatarioDomiciliacion(ppPpal.getDestinatarioDomiciliacion().charValue());
					}
				}	
			}
		}		
		
		try {
			logger.debug("GrabacionProvisional - Antes de Guardar Datos Forma de Pago: "+pagoPol.getEnvioIbanAgro());
			eleccionFormaPagoManager.guardarDatosFormaPago(pagoPol, new Long(idPoliza),oficina);
		} catch (Exception e) {
			logger.error(e);
		}
		
		params.put("idpoliza", p.getIdpoliza());
		params.put("mensaje", bundle.getString("mensaje.alta.provisional.detail"));
		if(p.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)){
			params.put("grabProv", "true");
		}else {
			params.put("grabProv", "false");
		}
		getWriterJSON (response, params);
		
		logger.debug("*** FIN grabacionProvisional ...");
		return null;
	}
	
	public ModelAndView doImprimirPolizas(HttpServletRequest request, HttpServletResponse response){
		
		logger.debug("init - operacion imprimir");
		
		final Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView resultado = null;		
		
		String  StrImprimirReducida = StringUtils.nullToString(request.getParameter("imprimirReducida"));
		final String idpoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
		Poliza poliza = polizaManager.getPoliza(new Long(idpoliza));
		
		parameters.put("StrImprimirReducida", StrImprimirReducida);
		
		if (poliza.getTipoReferencia() == 'C'){
			resultado = new ModelAndView("redirect:/informes.html").addObject("idPoliza", poliza.getIdpoliza()).addObject("method", "doInformePolizaComplementaria");
		} else {
			resultado = new ModelAndView("redirect:/informes.html").addObject("idPoliza", poliza.getIdpoliza()).addObject("method", "doInformePoliza");
		}			
		logger.debug("end - operacion imprimir");
		
		return resultado.addAllObjects(parameters);
	}
	
	
	/*fin DNF 23/02/2021 PET.70105.FII*/
	
	private Map<String, Object> getParametros(HttpServletRequest request){
		Map<String, Object> parameters = new HashMap<String, Object>();
		//Parametros necesarios a recoger y volver a enviar para la funcion de volver
		String numeroAval = null;
		String importeAval = null;
		String idpoliza = null;
		String modoLectura=null;
		String vieneDeUtilidades=null;
		String idEnvio=null;
		String grProvisional=null;
		String origenllamada=null;
		String vieneDeImportes=null;
		String numeroCuenta=null;
		String numeroCuenta2=null;
		String mpPagoM=null;
		String mpPagoC=null;
		String grProvisionalOK = null;
		String muestraBotonFinanciar=null;
		String validComps=null;
		
		numeroAval = request.getParameter("numero_da");
		importeAval = request.getParameter("importe_da");
		idpoliza = request.getParameter("idpoliza");
		modoLectura = request.getParameter("modoLectura");
		vieneDeUtilidades = request.getParameter("vieneDeUtilidades");
		idEnvio = request.getParameter("idEnvio");
		grProvisional = request.getParameter("grProvisional");
		origenllamada = request.getParameter("origenllamada");
		vieneDeImportes = request.getParameter("vieneDeImportes");
		numeroCuenta = request.getParameter("numeroCuenta");
		numeroCuenta2 = request.getParameter("numeroCuenta2");
		mpPagoM = request.getParameter("mpPagoM");
		mpPagoC = request.getParameter("mpPagoC");
		grProvisionalOK = request.getParameter("grProvisionalOK_da");
		muestraBotonFinanciar = request.getParameter("muestraBotonFinanciar_da");
		validComps = request.getParameter("validComps");
		
		parameters.put("numero_da",numeroAval);
		parameters.put("importe_da", importeAval);
		parameters.put("idpoliza",idpoliza);
		parameters.put("modoLectura",modoLectura);
		parameters.put("vieneDeUtilidades",vieneDeUtilidades);
		parameters.put("idEnvio",idEnvio);
		parameters.put("grProvisional", grProvisional);
		parameters.put("origenllamada",origenllamada);
		parameters.put("vieneDeImportes",vieneDeImportes);
		parameters.put("numeroCuenta",numeroCuenta);
		parameters.put("numeroCuenta2",numeroCuenta2);
		parameters.put("mpPagoM",mpPagoM);
		parameters.put("mpPagoC",mpPagoC);
		parameters.put("grProvisionalOK",grProvisionalOK);
		parameters.put("muestraBotonFinanciar",muestraBotonFinanciar);
		parameters.put("validComps",validComps);
		
		return parameters;
	}

	public String getCodigoPerfilUsuarioEI(Usuario usuario){
		
		String perfil = "";
		//Recuperamos el perfil en forma de String
		if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)){
			perfil = "0";
		}
		else if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES)){
			perfil = "1";
		}
		else if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_JEFE_ZONA)){
			perfil = "2";
		}
		else if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_OFICINA )){
			perfil = "3";
		}
		else if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_OTROS  )){
			perfil = "4";
		}
		else if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)){
			perfil = "5";
		}
		//Concatenamos al perfil el tipo de usuario que es
		if(usuario.isUsuarioExterno()){
			perfil = perfil + "E";
		}else{
			perfil = perfil + "I";
		}
		return perfil;
	}
	
	
	
	public void setEleccionFormaPagoManager(
			EleccionFormaPagoManager eleccionFormaPagoManager) {
		this.eleccionFormaPagoManager = eleccionFormaPagoManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public PasarADefinitivaPlzController getPasarADefinitivaPlzController() {
		return pasarADefinitivaPlzController;
	}

	public void setPasarADefinitivaPlzController(
			PasarADefinitivaPlzController pasarADefinitivaPlzController) {
		this.pasarADefinitivaPlzController = pasarADefinitivaPlzController;
	}

	public void setPolizaRCManager(PolizaRCManager polizaRCManager) {
		this.polizaRCManager = polizaRCManager;
	}
	
	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}
}