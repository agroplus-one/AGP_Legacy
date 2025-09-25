package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.CalculoServiceException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.jmesa.service.IMtoComisionesRenovService;
import com.rsi.agp.core.managers.IPolizasPctComisionesManager;
import com.rsi.agp.core.managers.impl.CaracteristicaExplotacionManager;
import com.rsi.agp.core.managers.impl.ComparativaManager;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.WebServicesCplManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.FluxCondensatorObject;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.cgen.LineaCondicionado;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.DatosAval;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument;

public class WebServicesCplController extends BaseMultiActionController{
	private Log logger = LogFactory.getLog(WebServicesCplController.class);
	
	private PolizaManager polizaManager;
	private ParametrizacionManager parametrizacionManager;
	private WebServicesCplManager webServicesCplManager;
	private CaracteristicaExplotacionManager caracteristicaExplotacionManager;
	private ComparativaManager comparativaManager;
	private PagoPolizaManager pagoPolizaManager;
	private WebServicesManager webServicesManager;
	private IPolizasPctComisionesManager polizasPctComisionesManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	
	private IMtoComisionesRenovService mtoComisionesRenovService;
	
	private final static String ORIGEN_FINANCIAR = "financiacion";
	private final static String ORIGEN_PAGO = "pago";
	private final static String ORIGEN_PASAR_DEFINITIVA = "pasarDefinitiva";
	
	public ModelAndView doValidar(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean)throws Exception{
		logger.debug("init - doValidar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		Poliza poliza = null;
		AcuseRecibo acuseRecibo = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
		String origenllamada = StringUtils.nullToString(request.getParameter("origenllamada"));
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		
		try {
			Parametro parametro = parametrizacionManager.getParametro();			
			poliza = polizaManager.getPoliza(new Long(idPoliza));
			logger.debug("idpoliza : " + idPoliza + "  estado: " + poliza.getEstadoPoliza().getIdestado());
			
			// BORRAMOS LA DISTRIBUCION DE COSTES ANTERIOR SIEMPRE QUE NO SEA POLIZA DEFINITIVA Y NO SE VENGA DE PAGOS
			if(!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) &&
					!ORIGEN_PAGO.equals(origenllamada) &&
				!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)){
				//poliza.getPagoPolizas().clear();//Para borrar el pago cuando se pasa a pendiente de validacion y no interfiera luego
				logger.debug("Borramos la distribucion de costes si no es definitiva");
				seleccionPolizaManager.deleteDistribucionCostes(poliza);
				logger.debug("distribucion de costes borrada");
				
				logger.debug("Borramos los datos del aval");
				DatosAval dv = polizaManager.GetDatosAval(poliza.getIdpoliza());
				if (null!=dv) {
					polizaManager.DeleteDatosAval(dv);
					poliza.setDatosAval(null);
				}
			}
			else if (!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) &&
					ORIGEN_PAGO.equals(origenllamada) &&
				!poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)){
				
				//Insertamos los datos del pago, si venimos de la pantalla de pagos, por si el usuario no pasa por la pantalla de pagos
				pagoPolizaManager.savePagoPoliza(polizaBean.getIdpoliza(),usuario);
				
				// Redireccion a pantalla de paso a definitiva
				parametros.put("idpoliza", poliza.getIdpoliza());
				return new ModelAndView("redirect:/grabacionPoliza.html").addAllObjects(parametros);
				
			}
			//DAA 21/03/2013  llamada a validacion y calculo de CPL al servicio de caracteristica de la explotacion
			boolean aplicaCaractExpl = caracteristicaExplotacionManager.aplicaYBorraCaractExplocion(poliza);
			if (aplicaCaractExpl){
				
				BigDecimal caractExlp = caracteristicaExplotacionManager.calcularCaractExplotacion(poliza, realPath, null, poliza.getPolizaPpal());	
				
				//comprobamos que la poliza principal tiene comparativas y si tiene cojo una 
				//para poder guardar la carct de explot.
				List<ComparativaPoliza> listComparativasPoliza = poliza
						.getComparativaPolizas() != null ? Arrays.asList(poliza
						.getPolizaPpal().getComparativaPolizas()
						.toArray(new ComparativaPoliza[] {}))
						: new ArrayList<ComparativaPoliza>();
				ComparativaPoliza cp = new ComparativaPoliza();
				if(listComparativasPoliza.size()>0)
					cp = listComparativasPoliza.get(0);
				
				ComparativaPoliza compP = comparativaManager.guardarComparatCaracExplot(cp, poliza, caractExlp);
				poliza.getComparativaPolizas().add(compP);
			}	
			
//			GENERAMOS EL XML DE LA POLIZA
			logger.debug("generamos el xml de poliza");
			Long idEnvio = generarXMLPolizaCpl(poliza, Constants.WS_VALIDACION, null, realPath);
			
//			LLAMADA AL WS DE VALIDACION
			logger.debug("realizamos la llamada al webservice de validacion");
			acuseRecibo = webServicesCplManager.validarCpl(idEnvio, poliza.getIdpoliza(), realPath, parametro.getValidacion());
			
//			LIMPIAMOS LOS ERRORES QUE NO DEBEN MOSTRARSE
			logger.debug("limpiamos errores");
			BigDecimal codPlan = poliza.getLinea().getCodplan();
			BigDecimal codLinea = poliza.getLinea().getCodlinea();
			BigDecimal codEntidad = poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad();
			webServicesCplManager.limpiaErroresWs(acuseRecibo, Constants.WS_VALIDACION, parametro, codPlan, codLinea, codEntidad, Constants.VALIDACION);
			
//			MENSAJES DE VALIDACION Y BOTONES
//			PARAMETROS CON LOS ERRORES DE VALIDACION QUE SE PINTARAN CON DISPLAYTAG EN LA JSP	
			parametros = WSUtils.getMensajesYBotones(acuseRecibo.getDocumentoArray(0));
			
			if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) {
				
				parametros.put("botonCalcular", Boolean.TRUE);
			} else {
				BigDecimal tipoUsuario = usuario.getTipousuario();
				tipoUsuario = usuario.getExterno().equals(Constants.USUARIO_EXTERNO) ? tipoUsuario.add(Constants.NUMERO_DIEZ) : tipoUsuario;
				List<ErrorWsAccion> errorWsAccionList = this.webServicesManager.getErroresWsAccion(codPlan, 
						codLinea, codEntidad, tipoUsuario, Constants.VALIDACION);
				parametros.put("botonCalcular", WSUtils.comprobarErroresPorPerfil(acuseRecibo, errorWsAccionList));
			}
			
			
			parametros.put("origenllamada", origenllamada);
			parametros.put("idpolizaCpl", poliza.getIdpoliza());
			parametros.put("WS", Constants.WS_VALIDACION);
			parametros.put("estado", poliza.getEstadoPoliza().getIdestado());
			parametros.put("perfil", usuario.getPerfil().substring(4));
			
			
			mv = new ModelAndView("moduloPolizas/webservices/erroresValidacionCpl", "resultado", acuseRecibo).addAllObjects(parametros);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al llamar al servicio de validacion de la poliza complementaria", be);
			throw new Exception("Se ha producido un error al llamar al servicio de validacion de la poliza complementaria",be);
		} catch (ValidacionPolizaException be) {
			logger.error("Error de validacion de xml de la poliza complementaria", be);
			throw be;
		} catch (Exception be) {
			logger.error("Se ha producido un error al llamar al servicio de validacion de la poliza complementaria", be);
			throw new Exception("Se ha producido un error al llamar al servicio de validacion de la poliza complementaria",be);
		}
		
		logger.debug("end - doValidar");
		return mv;
	}
	

	@SuppressWarnings("unchecked")
	public ModelAndView doCalcular(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean)throws Exception{
		logger.debug("init - doCalcular");
		Map<String, Object> parametros = new HashMap<String, Object>();
		Map<String, Object> paramsDesc = new HashMap<String, Object>();
		ModelAndView mv = null;
		Poliza poliza = null;
		
		Long idPolizaPpl = null;
		Boolean tieneSubvenciones = false;
		FluxCondensatorObject fluxCondensator = null;
		String complementaria = StringUtils.nullToString(request.getParameter("complementaria"));
		String idPoliza;
		
		logger.debug("**@@** WebServicesCplController - Dentro de doCalcular");
		
		if("".equals(complementaria)) {
			idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			if(idPoliza.equals("")){
				idPoliza = StringUtils.nullToString(request.getParameter("idpolizaComp"));
			}
			
		}else {//cuando viene del popup de descuento o recargo
			idPoliza = StringUtils.nullToString(request.getParameter("idpolizacpl"));
		}
		
		String modoLectura = StringUtils.nullToString(request.getParameter("modoLectura"));
		String origenllamada = StringUtils.nullToString(request.getParameter("origenllamada"));
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String operacion = StringUtils.nullToString(request.getParameter("operacion"));// para descuento / recargo
		try {
			
			poliza = polizaManager.getPoliza(new Long(idPoliza));
			logger.debug("idpoliza cpl: " + idPoliza + "  estado: " + poliza.getEstadoPoliza().getIdestado());
			
			//TMR 01/04/2013
			
			logger.debug("idpoliza ppal: " + idPolizaPpl + "  estado: " + poliza.getPolizaPpal().getEstadoPoliza().getIdestado());
			
			 //Descuentos/recargos ----------------------------------------------------------------------
			if (Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA.equals(poliza.getEstadoPoliza().getIdestado())
					|| modoLectura.equals("modoLectura")) {
				parametros.put("descuentoLectura", null);
				parametros.put("recargoLectura", null);
			}
			 
			 /*PET 70105.FII DNF*/
			parametros.put("lineaContrataSup2021",
					pagoPolizaManager.lineaContratacion2021(poliza.getLinea().getCodplan(),
							poliza.getLinea().getCodlinea(), poliza.getLinea().isLineaGanado()));
			 /*PET 70105.FII DNF*/
			 
			 if ("calcular".equalsIgnoreCase(operacion)) {//Descuentos
				 String [] dctoPctComisiones =	request.getParameterValues("dctoPctComisiones");
				 webServicesManager.actualizaPolizaPctComisionesDescuento(dctoPctComisiones, poliza, usuario);
			 }else if ("calcularRecargo".equalsIgnoreCase(operacion)) {
				 String [] recargoPctComisiones =	request.getParameterValues("recPctComisiones");
				 webServicesManager.actualizaPolizaPctComisionesRecargo(recargoPctComisiones, poliza, usuario);
			 } 
			 //------------------------------------------------------------------------------------------

			Long idEnvio;
			Map<String, Object> resultadoCalculo;
			
			Boolean esSaeca = polizaManager.esFinanciadaSaeca(poliza.getLinea().getLineaseguroid(),poliza.getColectivo().getSubentidadMediadora());
			if (ORIGEN_FINANCIAR.equals(origenllamada) && esSaeca.equals(true)){
				// Anadimos datos ficticios de aval para el calculo				
				poliza.setDatosAval(new DatosAval(poliza.getIdpoliza(), poliza, new BigDecimal(1), new BigDecimal(1)));
				// Creamos el objeto asegurado y mapeamos la cuenta bancaria, para poder superar la validacion del formato del XML
				if(poliza.getAsegurado().getDatoAsegurados() != null && poliza.getAsegurado().getDatoAsegurados().size() == 0) {
					DatoAsegurado datosFicticiosAseg = new DatoAsegurado();
					datosFicticiosAseg.setCcc("11111111111111111111");
					datosFicticiosAseg.setIban("ES22");
					datosFicticiosAseg.setLineaCondicionado(new LineaCondicionado(poliza.getLinea().getCodlinea(), null, ""));
					poliza.getAsegurado().getDatoAsegurados().add(datosFicticiosAseg);
				}
			}
			
			//GENERAMOS EL XML DE LA POLIZA
			logger.debug("generamos el xml de la poliza para el calculo");
			idEnvio = generarXMLPolizaCpl(poliza, Constants.WS_CALCULO, null, realPath);
			//LLAMADA AL WS DE CALCULO
			logger.debug("hacemos la llamada al webservice de calculo");
			resultadoCalculo = webServicesCplManager.calcularCpl(idEnvio, poliza, poliza.getColectivo().getPctdescuentocol(), realPath);
			
			// MPM - 16/02/2016
			// Obtiene el dato 'filacomparativa' de la tabla de comparativas de poliza asociada a esta complementaria para utilizarlo
			// en las inserciones en la tabla de distribucion de costes
			ComparativaPolizaId comparativaPoliza = obtencionFilaIdComparativa(poliza);
			
			es.agroseguro.distribucionCostesSeguro.Poliza calculo = (es.agroseguro.distribucionCostesSeguro.Poliza) resultadoCalculo.get("calculo");
			Map<Character, ComsPctCalculado> comsPctCalculado = this.mtoComisionesRenovService.getComisRenovParaCalculo(
					poliza.getLinea().getCodplan(), poliza.getLinea().getCodlinea(),
					poliza.getCodmodulo(),
					comparativaPoliza.getIdComparativa(),
					poliza.getColectivo().getTomador().getEntidad().getCodentidad(),
					poliza.getColectivo().getSubentidadMediadora().getId()
							.getCodentidad(),
					poliza.getColectivo().getSubentidadMediadora().getId()
							.getCodsubentidad(),
							calculo.getDatosCalculo().getCostePoliza()
							.getCosteGrupoNegocioArray());
			
			// REGENERAMOS EL XML PARA APLICAR EL % DE COMISION POR E-S MED
			idEnvio = generarXMLPolizaCpl(poliza, Constants.WS_CALCULO, comsPctCalculado, realPath);
			resultadoCalculo = webServicesCplManager.calcularCpl(idEnvio, poliza,
					poliza.getColectivo().getPctdescuentocol(), realPath);
			
			// MPM - 07/05/2015
			// Se comprueba si hay que llamar al servicio de financiacion
			FinanciacionDocument financiacion = null;
			if (origenllamada.equals(ORIGEN_FINANCIAR)) {
				logger.debug("Llama al servicio de financiaci�n");
				
				//Se pasa poliza.getCodmodulo() ya que el de la comparativa puede venir sin la C que identifica a los modulos de complementaria
				Map<String, Object> resultadoFinanciacion = this.webServicesManager.callWSFinanciacion(realPath, request.getParameterMap(), poliza, poliza.getCodmodulo(), comparativaPoliza.getFilacomparativa() , usuario.getCodusuario());
				
				// Si existe el objeto 'alerta' en el resultado de la financiacion ha ocurrido algun error en la llamada al SW
				if (resultadoFinanciacion != null && resultadoFinanciacion.containsKey(Constants.KEY_ALERTA)) {
					parametros.put(Constants.KEY_ALERTA, resultadoFinanciacion.get(Constants.KEY_ALERTA));
				}
				else if (resultadoFinanciacion != null && resultadoFinanciacion.containsKey("financiacion")) {
					financiacion = (es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument) resultadoFinanciacion.get("financiacion");
				}
			} else {				
				// Objeto que encapsula el resultado del calculo para polizas de Agrarios con el esquema antiguo 
				es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo = null;
				// Objeto que encapsula el resultado del calculo para polizas de Ganado y Agrario con el esquema unificado
				es.agroseguro.distribucionCostesSeguro.Poliza plzCalculoUnificado = null;
				
				// MPM - Se comprueba si la respuesta del calculo es del esquema unificado o no
				if (resultadoCalculo.get("calculo") instanceof es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) {
					logger.debug("Respuesta de c�lculo del esquema antiguo");
					polizaCalculo = (es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza) resultadoCalculo.get("calculo");
					logger.debug("Resultado del calculo: " + polizaCalculo.toString());
				}
				else if (resultadoCalculo.get("calculo") instanceof es.agroseguro.distribucionCostesSeguro.Poliza) {
					logger.debug("Respuesta de c�lculo del esquema unificado");
					plzCalculoUnificado = (es.agroseguro.distribucionCostesSeguro.Poliza) resultadoCalculo.get("calculo");
					logger.debug("Resultado del calculo: " + plzCalculoUnificado.toString());
				}
				
				// Si es una respuesta de calculo del formato unificado
				if (plzCalculoUnificado != null) {
					webServicesManager.actualizaDistribCostesUnificado(plzCalculoUnificado, poliza, poliza.getCodmodulo(), comparativaPoliza.getFilacomparativa(), comparativaPoliza.getIdComparativa());
					//this.actualizaDistribCostesUnificado(plzCalculoUnificado, poliza, cp.getId().getCodmodulo(), cp.getId().getFilacomparativa());
				}
				// Si es una respuesta de calculo del formato antiguo
				else {
					/* Pet. 57626 ** MODIF TAM (08/06/2020) ***/
					/*webServicesCplManager.actualizaDistribCostes(idEnvio, poliza, poliza.getCodmodulo(), comparativaPoliza.getFilacomparativa(), comparativaPoliza.getIdComparativa());*/
					logger.error("Nunca deberia entrar por aqui, por que siempre deberia entrar por Formato Unificado");
				}
			}
			
			logger.debug("generamos el condensador de flujo con los datos de la distribucion de costes");
			fluxCondensator = webServicesCplManager.generarCondensadorDeFlujo(resultadoCalculo, poliza, financiacion);
			
			tieneSubvenciones = tieneSubvenciones(fluxCondensator);
			
			parametros.put("tieneSubvenciones", tieneSubvenciones);
			
			if(origenllamada.equals(ORIGEN_PASAR_DEFINITIVA)) {
				DistribucionCoste2015 distCoste2015 = new DistribucionCoste2015();
				
				webServicesCplManager.guardarDistribucionCosteCpl(fluxCondensator, distCoste2015, poliza.getIdpoliza(),
						comparativaPoliza.getFilacomparativa(), poliza.getLinea().getCodplan(), financiacion,
						comparativaPoliza.getIdComparativa());	
								
				mv = new ModelAndView("redirect:/utilidadesPoliza.html");
				
				mv.addObject("polizaOperacion", poliza.getIdpoliza());				
				mv.addObject("operacion", "pasarDefinitivaPostValidacionesComplementaria");
			}else if(origenllamada.equals(ORIGEN_PAGO)){
				parametros.put("idpoliza", poliza.getIdpoliza());
				mv = new ModelAndView("redirect:/grabacionPoliza.html").addAllObjects(parametros);
			}else{
				if (poliza.getPolizaPpal().getReferencia()==null){
					parametros.put("ocultarBtnGrabar","true");
				}
				
				// Se recogen los valores indicados para la opcion y el valor de financiacion para almacenarlo en la distribucion de costes
				if (financiacion != null) {
					try {
						
						Integer opcionFracc = null; BigDecimal valorOpcionFracc = null;
						opcionFracc=new Integer(request.getParameter("opcion_cf"));
						//valorOpcionFracc = new BigDecimal (request.getParameter(""));
						String valorOpcionFraccStr=null;
						parametros.put("muestraBotonFinanciar", "false");
						
						switch (opcionFracc) {
						case 0:
							valorOpcionFraccStr = request.getParameter("porcentajeCosteTomador_txt");
							break;
						case 1:
							valorOpcionFraccStr = request.getParameter("importeFinanciar_txt");
							break;
						case 2:
							valorOpcionFraccStr = request.getParameter("importeAval_txt");
							break;
						default:
							break;
						}
						
						valorOpcionFraccStr = valorOpcionFraccStr.replace(".", "");
						valorOpcionFraccStr = valorOpcionFraccStr.replace(",", ".");
						valorOpcionFracc = new BigDecimal(valorOpcionFraccStr);
						 
						fluxCondensator.setOpcionFracc(opcionFracc);
						fluxCondensator.setValorOpcionFracc(valorOpcionFracc);
					} catch (Exception e) {
						logger.error("Error al obtener los par�metros de la financiaci�n", e);
					}
				}
				
				parametros.put("WS", Constants.WS_CALCULO);
				parametros.put("idpolizaCpl", poliza.getIdpoliza());
				parametros.put("idpoliza", poliza.getIdpoliza());//para los popups de descuentos y recargos
				parametros.put("codModulo", poliza.getCodmodulo());
				parametros.put("estado", poliza.getEstadoPoliza().getIdestado());
				parametros.put("plan", poliza.getLinea().getCodplan());
				parametros.put("numeroCuenta", AseguradoUtil.getFormattedBankAccount(poliza, true));
				parametros.put("numeroCuenta2", AseguradoUtil.getFormattedBankAccount(poliza, false));
				
				// boton descuentos y ver comisiones solo plan 2015 o mayor
				if (poliza.isPlanMayorIgual2015()) {
					
					paramsDesc = webServicesManager.muestraBotonDescuento(poliza,usuario);
					parametros.put("complementaria",true);					
					
					// Ver Comisiones
					// Pet. 57626 ** MODIF TAM (04.06.2020) ** Inicio */
					/* Por los desarrollos de la peticion la contratacion de complementarias tambien va con el formato Unificado  */
					
					es.agroseguro.distribucionCostesSeguro.Poliza polizaCalculo = null;
					es.agroseguro.distribucionCostesSeguro.DatosCalculo	  datosCalculo			 = null;
					es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio[] distribCostes1 = null;
					
					polizaCalculo = (es.agroseguro.distribucionCostesSeguro.impl.PolizaImpl)resultadoCalculo.get("calculo");
					
					if (polizaCalculo != null){
			  			datosCalculo = polizaCalculo.getDatosCalculo();
					}
					
				  	if (datosCalculo != null) {
				  		distribCostes1 = datosCalculo.getCostePoliza().getCosteGrupoNegocioArray();
				  	}
				  	
				  	/* Pet. 57626 ** MODIF TAM (04.06.2020) */
				  	for (es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio costeGNActual:distribCostes1) {
						//fluxCondensator =  polizasPctComisionesManager.dameComisiones(fluxCondensator, poliza.getPolizaPpal(), usuario,distribCostes1.getPrimaComercialNeta());					
					  	fluxCondensator =  polizasPctComisionesManager.dameComisiones(fluxCondensator, poliza, usuario, costeGNActual.getPrimaComercialNeta());
				  	}
				  	
				  	// Se obtiene la lista de condiciones de fraccionamiento para el plan/linea de la poliza
				  	String moduloSeleccionado = poliza.getCodmodulo();
				  	List<CondicionesFraccionamiento> condFracc = webServicesManager.getCondicionesFraccionamiento(poliza.getLinea().getLineaseguroid(), moduloSeleccionado);
				  	parametros.put("condicionesFraccionamiento", condFracc);
				  	
				}
				
				// ESC-25609: Antes de guardar la distribucion de costes recuperamos las
				// comisiones porque si no se pierden
				BigDecimal bdComsEntidad = null;
				BigDecimal bdComsESMed = null;
				
				for (DistribucionCoste2015 dc : poliza.getDistribucionCoste2015s()) {
					if (comparativaPoliza.getIdComparativa().toString().equals(dc.getIdcomparativa().toString())) {
						bdComsEntidad = dc.getImpComsEntidad();
						bdComsESMed = dc.getImpComsESMed();
						break;
					}
				}
				DistribucionCoste2015 distCoste2015 = new DistribucionCoste2015();
				distCoste2015.setImpComsEntidad(bdComsEntidad);
				distCoste2015.setImpComsESMed(bdComsESMed);
										
				//DAA 17/01/2013 Guardamos la distribucion de coste antes de sacar los importes
				webServicesCplManager.guardarDistribucionCosteCpl(fluxCondensator, distCoste2015, poliza.getIdpoliza(),
						comparativaPoliza.getFilacomparativa(), poliza.getLinea().getCodplan(), financiacion,
						comparativaPoliza.getIdComparativa());
				
				fluxCondensator.setCosteTomador(NumberUtils.formatear(new BigDecimal(fluxCondensator.getCosteTomador()),2));
				fluxCondensator.setImporteTomador(NumberUtils.formatear(new BigDecimal(fluxCondensator.getImporteTomador()),2));
				fluxCondensator.setTotalCosteTomador(NumberUtils.formatear(new BigDecimal(fluxCondensator.getTotalCosteTomador()),2));
				
				//parametros.put("isPagoFraccionado", financiacion != null || pagoPolizaManager.compruebaPagoFraccionado(poliza));
				//parametros.put("isPagoFraccionado", financiacion != null);
				parametros.put("isPagoFraccionado", financiacion != null);
				parametros.put("isPagoFraccionado", pagoPolizaManager.compruebaPagoFraccionado(poliza));
				
				// MPM - Paso a definitiva
				Poliza polizaDefinitiva = new Poliza ();
				polizaDefinitiva.setIdpoliza(poliza.getIdpoliza());
				parametros.put("polizaDefinitiva", polizaDefinitiva);
				
				parametros.put("dataCodlinea", poliza.getLinea().getCodlinea());
				parametros.put("dataCodplan", poliza.getLinea().getCodplan());
				parametros.put("dataNifcif", poliza.getAsegurado().getNifcif());
				parametros.put("perfil", usuario.getPerfil().substring(4));
				
				mv = new ModelAndView("moduloPolizas/polizas/importes/importesCpl", "fluxCondensator", fluxCondensator).
						addAllObjects(parametros).addAllObjects(paramsDesc);
			}
				
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al llamar al servicio de calculo de la poliza complementaria", be);
			throw new Exception("Se ha producido un error al llamar al servicio de calculo de la poliza complementaria",be);
		} catch (CalculoServiceException be) {
			logger.error("Se ha producido un error al llamar al servicio de calculo de la poliza complementaria", be);
			mv = HTMLUtils.errorMessage("Servicio de c�lculo", "Ocurrio un error inesperado al llamar a los servicios Web: " + be.getMessage());
		} catch (Exception be) {
			logger.error("Se ha producido un error al llamar al servicio de calculo de la poliza complementaria", be);
			throw new Exception("Se ha producido un error al llamar al servicio de calculo de la poliza complementaria",be);
		}
		
		logger.debug("end - doCalcular");
		return mv;
	}
	
	private Boolean tieneSubvenciones(FluxCondensatorObject fco){
		Boolean result = false;
		
		if(fco.getSubvCCAA() != null && fco.getSubvCCAA().size() > 0){
			result = true;
		}
		if(fco.getSubvEnesa() != null && fco.getSubvEnesa().size() > 0){
			result = true;
		}

		return result;
	}
	
	/*public ModelAndView doCalcularDescuentoRecargo(HttpServletRequest request, HttpServletResponse response, Poliza poliza)throws Exception{
		ModelAndView mv = null;
		Map<String,Object> params = new HashMap<String, Object>();
		Map<String,Object> paramsDesc = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String origenllamada = StringUtils.nullToString(request.getParameter("origenllamada")); //Se usa en el módulo de utilidades antes de cambiar el estado de una póliza a definitiva
		// Path real para luego buscar el WSDL de los servicios Web
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		Parametro parametro = parametrizacionManager.getParametro();	
		String modoLectura = "";
		
		params.put("externo", usuario.getExterno());
		String idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
		
		
		request.setAttribute("idpoliza", idPoliza);
		request.setAttribute("origenllamada", origenllamada);
		request.setAttribute("modoLectura", modoLectura);
		
		if (poliza.getEstadoPoliza().getIdestado()
				.equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)
				|| poliza.getEstadoPoliza().getIdestado()
						.equals(Constants.ESTADO_POLIZA_DEFINITIVA))
			modoLectura = "modoLectura";
		String operacion = StringUtils.nullToString(request.getParameter("operacion"));
		
		 if ("calcular".equalsIgnoreCase(operacion)) {//Descuentos
			 mv=calcularDescuento(request,response,poliza, usuario,origenllamada,modoLectura,realPath, parametro);
		 }else {//calcularRecargo
			mv=calcularRecargo(request, response, poliza,origenllamada, modoLectura, realPath,parametro); 
		 }
		 
		 paramsDesc = webServicesManager.muestraBotonDescuento(poliza,usuario);
			if (mv!= null) {
				mv.addAllObjects(params).addAllObjects(paramsDesc);
			}
		
		return mv;
	}*/
	
	/*private ModelAndView calcularDescuento(HttpServletRequest request, HttpServletResponse response, 
			Poliza poliza,Usuario usuario, String origenllamada, 
			String modoLectura, String realPath,Parametro parametro)throws Exception{
		ModelAndView mv = null;
		boolean actualizaDistribucionCostes = false;		
		if (StringUtils.nullToString(request.getParameter("descuento"))!= "") {
			webServicesManager.updateDescuento(poliza.getIdpoliza(),request.getParameter("descuento"));
			webServicesManager.insertHistoricoDescuento(poliza,request.getParameter("descuento"),usuario);
			poliza.getPolizaPctComisiones().setPctdescelegido(new BigDecimal(request.getParameter("descuento")));
			//params.put("descuento", request.getParameter("descuento"));
			if (poliza.getEstadoPoliza().getIdestado()
					.equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
				actualizaDistribucionCostes = true;
			}
			
		}
		mv = webServicesManager.callWebService(poliza, Constants.WS_CALCULO, origenllamada,
				modoLectura,realPath, parametro, request.getParameterMap(),request,actualizaDistribucionCostes);
		
		return mv;
	}*/
	
	/*private ModelAndView calcularRecargo(HttpServletRequest request, HttpServletResponse response, Poliza poliza,
			String origenllamada, String modoLectura, String realPath,Parametro parametro)throws Exception{
		ModelAndView mv = null;
		boolean actualizaDistribucionCostes = false;		
		if (StringUtils.nullToString(request.getParameter("recargo"))!= "") {
			webServicesManager.updateRecargo(poliza.getIdpoliza(),request.getParameter("recargo"));
			//webServicesManager.insertHistoricoDescuento(poliza,request.getParameter("descuento"),usuario);
			poliza.getPolizaPctComisiones().setPctrecarelegido(new BigDecimal(request.getParameter("recargo")));
			if (poliza.getEstadoPoliza().getIdestado()
					.equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
				actualizaDistribucionCostes = true;
			}
			
		}
		mv = webServicesManager.callWebService(poliza, Constants.WS_CALCULO, origenllamada,
				modoLectura,realPath, parametro, request.getParameterMap(),request,actualizaDistribucionCostes);
		
		return mv;
	}*/
	
	
//	private void actualizarDescuento(HttpServletRequest request, Poliza poliza,Usuario usuario)throws Exception{
//		
//		webServicesManager.updateDescuento(poliza.getIdpoliza(),request.getParameter("descuento"));
//		webServicesManager.insertHistoricoDescuento(poliza,request.getParameter("descuento"),usuario);
//		poliza.getPolizaPctComisiones().setPctdescelegido(new BigDecimal(request.getParameter("descuento")));
//	}
//	
//	private void actualizarRecargo(HttpServletRequest request,Poliza poliza)throws Exception{
//	
//		webServicesManager.updateRecargo(poliza.getIdpoliza(),request.getParameter("recargo"));
//		poliza.getPolizaPctComisiones().setPctrecarelegido(new BigDecimal(request.getParameter("recargo")));
//	}

	
	
	/**
	 * Se llama a la generacion del XML de la Poliza  y se almacena el resultado
	 * @param poliza
	 * @return
	 * @throws BusinessException 
	 * @throws ValidacionPolizaException 
	 * @throws DAOException 
	 * @throws Exception
	 */
	private Long generarXMLPolizaCpl(Poliza poliza, String WS, Map<Character, ComsPctCalculado> comsPctCalculado, String realPath)
			throws DAOException, ValidacionPolizaException, BusinessException {
		logger.debug("init - generarXMLPolizaCpl");
		Long idEnvio = null;
		GregorianCalendar fechaInicio = null;
		GregorianCalendar fechaFin = null;
		fechaInicio = new GregorianCalendar();
			
		idEnvio = webServicesCplManager.generateAndSaveXMLPolizaCpl(poliza, WS, comsPctCalculado, realPath);
		logger.debug("id envio a agroseguro: " + idEnvio);		
		
		if (idEnvio == null) 
			throw new WebServiceException("Error en la llamada a los Servicios Web. Imposible Obtener la Poliza de Base de Datos...");
		
		fechaFin = new GregorianCalendar();
		Long tiempo = fechaFin.getTimeInMillis() - fechaInicio.getTimeInMillis();
		logger.debug("Tiempo desde que se inicia la generacion del xml:  "+tiempo+" milisegundos.");
		
		logger.debug("end - generarXMLPolizaCpl");
		return idEnvio;
	}
	
	private ComparativaPolizaId obtencionFilaIdComparativa(final Poliza pol) {		
		logger.debug("WebServicesCplController - obtencionFilaIdComparativa");
		Set<ModuloPoliza> mpList = pol.getModuloPolizas();		
		logger.debug("WebServicesCplController - despues de obtener por BBDD las comparativas");		
		ComparativaPolizaId compPol = new ComparativaPolizaId();				
		for(ModuloPoliza mp : mpList) {
			if(mp.getId().getCodmodulo().equalsIgnoreCase(pol.getCodmodulo())) {
				compPol.setCodmodulo(mp.getId().getCodmodulo());
				compPol.setIdComparativa(mp.getId().getNumComparativa());
				if (mp.getRiesgoCubierto() != null) {
					compPol.setCodriesgocubierto(mp.getRiesgoCubierto().getId().getCodriesgocubierto());
				}
				break;
			}			
		}
		return compPol;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setParametrizacionManager(ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}

	public void setWebServicesCplManager(WebServicesCplManager webServicesCplManager) {
		this.webServicesCplManager = webServicesCplManager;
	}

	

	public void setCaracteristicaExplotacionManager(
			CaracteristicaExplotacionManager caracteristicaExplotacionManager) {
		this.caracteristicaExplotacionManager = caracteristicaExplotacionManager;
	}


	public void setComparativaManager(ComparativaManager comparativaManager) {
		this.comparativaManager = comparativaManager;
	}


	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}


	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}


	public void setPolizasPctComisionesManager(
			IPolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}


	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}	
	
	public void setMtoComisionesRenovService(IMtoComisionesRenovService mtoComisionesRenovService) {
		this.mtoComisionesRenovService = mtoComisionesRenovService;
	}
}