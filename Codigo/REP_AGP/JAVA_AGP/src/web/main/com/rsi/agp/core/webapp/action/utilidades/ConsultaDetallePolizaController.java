package com.rsi.agp.core.webapp.action.utilidades;
		
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IPolizasPctComisionesManager;
import com.rsi.agp.core.managers.impl.ConsultaDetallePolizaManager;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.PolizaRCManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.webapp.util.FluxCondensatorObject;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;
import com.rsi.agp.dao.tables.poliza.DatosAval;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.rc.PolizasRC;

public class ConsultaDetallePolizaController extends MultiActionController{
	
	private static final String VIENE_DE_UTILIDADES = "vieneDeUtilidades";
	private static final String ID_POLIZA = "idpoliza";
	private static final String ALERTA = "alerta";
	private static final String RECOGER_POLIZA_SESION = "recogerPolizaSesion";
	private static final String UTILIDADES_POLIZA = "utilidadesPoliza.html";
	private static final String USUARIO = "usuario";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String OFICINA_ACTUAL = "oficinaActual";
	private static final String NOMBO_OFICINA = "nomboficina";
	private static final String VALID_COMPS = "validComps";
	private static final String NUM_AVAL = "numaval";
	private static final String IMPORTE_AVAL = "importeaval";
	private static final String MP_PAGO_M = "mpPagoM";
	private static final String MP_PAGO_C = "mpPagoC";
	
	private SeleccionPolizaManager seleccionPolizaManager;
	private ConsultaDetallePolizaManager consultaDetallePolizaManager;
	private PagoPolizaManager pagoPolizaManager;
	private WebServicesManager webServicesManager;
	private IPolizasPctComisionesManager polizasPctComisionesManager;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private PolizaManager polizaManager;
	private PolizaRCManager polizaRCManager;
	
	public ModelAndView doListaParcelas(HttpServletRequest request, HttpServletResponse response,
			Poliza polizaBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView resultado = null;
		try{
			
			String vieneDeUtilidades = StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES));
			Long idPoliza = null;
			if (!"".equals(StringUtils.nullToString(request.getParameter(ID_POLIZA)))) {
				idPoliza = Long.parseLong(request.getParameter(ID_POLIZA));
			}
			polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
			
			consultaDetallePolizaManager.cargaCabecera (polizaBean,request);
			
			PantallaConfigurable pantalla = seleccionPolizaManager.getPantallaVarPoliza(polizaBean.getLinea().getLineaseguroid(),new Long (7L));
			
			if (pantalla != null) {
				boolean isLineaGanado = polizaBean.getLinea().isLineaGanado();

				if(isLineaGanado){
					parameters.put("pantalla", pantalla);
					parameters.put("polizaBean", polizaBean);
					parameters.put("idpoliza", idPoliza);
					parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
					resultado = new ModelAndView("redirect:/listadoExplotaciones.html").addAllObjects(parameters);
				}else{
					parameters = consultaDetallePolizaManager.getDatosParcela(request,polizaBean.getIdpoliza());
					parameters.put("pantalla", pantalla);
					parameters.put("polizaBean", polizaBean);
					parameters.put("idpoliza", idPoliza);
					parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
					String listCodModulos = seleccionPolizaManager.getListModulesWithComparativas(new Long(idPoliza));
					listCodModulos=listCodModulos.replace(";", ",");
					parameters.put("listCodModulos", listCodModulos);
					resultado = new ModelAndView("moduloPolizas/polizas/listadoParcelas", "datos",parameters).addAllObjects(parameters);	
				}				
			}else{
				resultado=  HTMLUtils.errorMessage("seleccionPoliza", bundle.getString("mensaje.poliza.pantallaConfigurada.KO"));
			}
		}catch (Exception e) {
			logger.error("Error al listar las parcelas " + e.getMessage(), e);
			parameters.put(ALERTA,"Error al listar las  parcelas");
			parameters.put(RECOGER_POLIZA_SESION, "true");
			resultado = new ModelAndView(new RedirectView(UTILIDADES_POLIZA)).addAllObjects(parameters);
			
		}
		return resultado;
		
	}
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response,
			Poliza polizaBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv= null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		final BigDecimal entidad = usuario.getColectivo().getTomador().getId().getCodentidad();
		polizaBean.getAsegurado().getEntidad().setCodentidad(entidad); 
		polizaBean.getColectivo().getTomador().getId().setCodentidad(entidad);
		try{
			Long idPoliza = null;
			if (!"".equals(StringUtils.nullToString(request.getParameter(ID_POLIZA)))) {
				idPoliza = Long.parseLong(request.getParameter(ID_POLIZA));
			}
			parameters = consultaDetallePolizaManager.consulta(request, idPoliza);
			mv = new ModelAndView("moduloPolizas/polizas/listadoParcelas", "datos", parameters).addAllObjects(parameters);
		
		}catch (Exception e) {
			logger.error("Error al listar  las parcelas" + e.getMessage(), e);
			parameters.put(ALERTA,"Error al listar las parcelas");
			parameters.put(RECOGER_POLIZA_SESION, "true");
			mv = new ModelAndView(new RedirectView(UTILIDADES_POLIZA)).addAllObjects(parameters);
		}
		return  mv;
		
	}
	
	public ModelAndView doContinuar(HttpServletRequest request, HttpServletResponse response,
			Poliza polizaBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv= null;
		try{
			Long idPoliza = null;
			if (!"".equals(StringUtils.nullToString(request.getParameter(ID_POLIZA)))) {
				idPoliza = Long.parseLong(request.getParameter(ID_POLIZA));
			}
			parameters.put(ID_POLIZA, idPoliza);
			parameters.put("operacion", "");
			parameters.put(MODO_LECTURA, StringUtils.nullToString(request.getParameter(MODO_LECTURA)));
			parameters.put(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
			mv= new ModelAndView(new RedirectView("aseguradoSubvencion.html")).addAllObjects(parameters);
			
		}catch (Exception e) {
			logger.error("Error al consultar las subvenciones"  + e.getMessage(), e);
			parameters.put(ALERTA,"Error al consultar las subvenciones");
			parameters.put(RECOGER_POLIZA_SESION, "true");
			mv = new ModelAndView(new RedirectView(UTILIDADES_POLIZA)).addAllObjects(parameters);
		}
		return  mv;
		
	}
	
	
	// Pet. 54046 ** MODIF TAM (28.06.2018) ** Inicio //
	/*public ModelAndView getParametrosOficina(HttpServletRequest request, HttpServletResponse response) throws DAOException{
		logger.debug("**@@** ConsultaDetallePolizaController - getParametrosOficina");
		Map<String, Object> parametrosOficinas = new HashMap<String, Object>();
		Long idPoliza = null;
		
		idPoliza = Long.parseLong(request.getParameter(ID_POLIZA));
		
		Poliza poliza = polizaManager.getPoliza(idPoliza);
		String oficina = poliza.getOficina();
		String nombOficina = poliza.getNombreOfi();
		BigDecimal entidad = poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad();
		
		parametrosOficinas.put("entidad", entidad);
		parametrosOficinas.put("oficina", oficina);
		parametrosOficinas.put(NOMBO_OFICINA, nombOficina);
		parametrosOficinas.put(ID_POLIZA, idPoliza);
		
		logger.debug("** Valor de entidad:"+parametrosOficinas.get("entidad"));
		logger.debug("** Valor de oficina:"+parametrosOficinas.get("oficina"));
		logger.debug("** Valor de nomboficina:"+parametrosOficinas.get(NOMBO_OFICINA));
		logger.debug("** Valor de idpoliza:"+parametrosOficinas.get(ID_POLIZA));		
		
		ModelAndView mv = new ModelAndView("moduloPolizas/polizas/importes/importes").addAllObjects(parametrosOficinas);
		
		return mv;
	}*/
	
	public ModelAndView cambiarOficina(HttpServletRequest request, HttpServletResponse response, Poliza poliza) throws DAOException{
		logger.debug("**@@** ConsultaDetallePolizaController - cambiarOficina");
		Map<String, Object> parametrosOficinas = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		
		String oficina = request.getParameter(OFICINA_ACTUAL);
		String nombOficina = request.getParameter(NOMBO_OFICINA);
		String validComps = request.getParameter(VALID_COMPS);
		
		Poliza polizaBuscada = new Poliza();
		polizaBuscada = seleccionPolizaManager.getPolizaById(new Long(poliza.getIdpoliza()));
		
		/*Obtenemos el nombre de la oficina nueva*/
		String nombreOficina = polizaManager.getNombreOficina(new BigDecimal(oficina), polizaBuscada.getAsegurado().getEntidad().getCodentidad());

		/* En caso de que no se obtengan resultados, significa que la oficina no pertenece a la entidad actual
			No hacemos cambios */
		if (nombreOficina == null){
			//parametrosOficinas.put("mensaje", bundle.getString("mensaje.modificacion.KO"));
			parametrosOficinas.put(ALERTA,"Error: la oficina no pertenece a la entidad de la p\u00f3liza");
			parametrosOficinas.put(OFICINA_ACTUAL,polizaBuscada.getOficina());
			parametrosOficinas.put("oficina","");
			parametrosOficinas.put(NOMBO_OFICINA,nombOficina);
			parametrosOficinas.put("entidad",polizaBuscada.getAsegurado().getEntidad().getCodentidad());
			parametrosOficinas.put("externo",usuario.getExterno());
			parametrosOficinas.put("perfil", usuario.getPerfil().substring(4));
			parametrosOficinas.put(ID_POLIZA,polizaBuscada.getIdpoliza().toString());
			parametrosOficinas.put("operacion","calcular");
			parametrosOficinas.put(VALID_COMPS,validComps);
			return new ModelAndView("redirect:/webservices.html", parametrosOficinas);
		}
		
		if (!(oficina.equals("")) && isNumeric(oficina)){
			Integer oficinaNueva = new Integer(StringUtils.nullToString(oficina));
			Integer oficinaAntigua = new Integer(StringUtils.nullToString(polizaBuscada.getOficina()));
			
			/* Si la oficina ha cambiado actualizamos la poliza*/
			if (!(oficinaNueva.equals(oficinaAntigua))){
				polizaBuscada.setOficina(String.format("%04d", oficinaNueva.intValue()));
				polizaBuscada.setFechaModificacion(new Date());
				seleccionPolizaManager.savePoliza(polizaBuscada);
				parametrosOficinas.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
				logger.debug("cambiarOficina --  Cambio de oficina realizado a " + oficina);
			}
		}
		
		
		/* Cargamos de nuevo la vista de importes y ponemos los parametros */
		//ModelAndView mv = this.doVerImportes(request, response, polizaBuscada);
		
		
		parametrosOficinas.put(ID_POLIZA,polizaBuscada.getIdpoliza().toString());
		parametrosOficinas.put("operacion","calcular");
		parametrosOficinas.put(OFICINA_ACTUAL,polizaBuscada.getOficina());
		parametrosOficinas.put("oficina","");
		parametrosOficinas.put(VALID_COMPS,validComps);
		
		if (!(oficina.equals("")) && isNumeric(oficina)){
			parametrosOficinas.put(NOMBO_OFICINA,nombreOficina);
		} else {
			parametrosOficinas.put(NOMBO_OFICINA,request.getParameter(NOMBO_OFICINA));
			
		}
		parametrosOficinas.put("entidad",polizaBuscada.getAsegurado().getEntidad().getCodentidad());
		parametrosOficinas.put("externo",usuario.getExterno());
		parametrosOficinas.put("perfil", usuario.getPerfil().substring(4));
			
		logger.debug("End - Cambio Oficina");
		
		//return mv.addAllObjects(parametrosOficinas);
		return new ModelAndView("redirect:/webservices.html", parametrosOficinas);

	}
	// Pet. 54046 ** MODIF TAM (28.06.2018) ** Fin //
	
	public ModelAndView doVerImportes(HttpServletRequest request, HttpServletResponse response,
			Poliza polizaBean) {		
		Map<String, Object> parameters = new HashMap<String, Object>();		
		Long idPoliza = null;
		ModelAndView mv= null;
		Set<VistaImportes> fluxCondensatorHolder = new LinkedHashSet<VistaImportes>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		String realPath= this.getServletContext().getRealPath("/WEB-INF/");
		if (!"".equals(StringUtils.nullToString(request.getParameter(ID_POLIZA)))) {
			idPoliza = Long.parseLong(request.getParameter(ID_POLIZA));
		}
		try{
			polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
			List<DistribucionCoste2015> listDc = consultaDetallePolizaManager.getDistribucionCoste2015ByIdPoliza (polizaBean.getIdpoliza());
			
			/* Pet. 63485 ** MODIF TAM (28.07.2020) ** Inicio */
			/* Si entramos en modo consulta para polizas no confirmadas de Agrícola habrá que recuperar las comparativas del 
			 * XML grabado en BBDD, si no hay nada en BBDD no se muestra la tabla de coberturas, para ello nos declaramos un metodo nuevo.
			 */
			Boolean modoLectura = false;
			if (MODO_LECTURA.equals(request.getParameter(MODO_LECTURA))) {
				modoLectura = true; 
			}
			
			//fluxCondensatorHolder = consultaDetallePolizaManager.getDataImportes(listDc,polizaBean,usuario,realPath);
			if ( !polizaBean.getLinea().isLineaGanado() && modoLectura && !polizaBean.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
				fluxCondensatorHolder = consultaDetallePolizaManager.getDataImportesConsultaAgri(listDc,polizaBean,usuario,realPath);
			}else {
				fluxCondensatorHolder = consultaDetallePolizaManager.getDataImportes(listDc,polizaBean,usuario,realPath);
			}
			/* Pet. 63485 ** MODIF TAM (28.07.2020) ** Fin */
			
			request.getSession().setAttribute("distCoste", fluxCondensatorHolder);
			
			
			if(""!=StringUtils.nullToString(request.getParameter(MODO_LECTURA)) && polizaBean.getEsFinanciada().equals('S')){
				//Cuando es solo lectura, como es posible que se hayan cambiado los datos de fraccionamiento en el mantenimiento, 
				//siempre que la póliza ha sido financiada hay que mostrar el botón 
				ImporteFraccionamiento ifrac = new ImporteFraccionamiento();
				ifrac.setImporte(new BigDecimal(0));
				webServicesManager.muestraFinanciar(fluxCondensatorHolder,ifrac);
			}else{
				ImporteFraccionamiento impFrac = webServicesManager.getImporteFraccionamiento(polizaBean.getLinea()
									.getLineaseguroid(),polizaBean.getColectivo().getSubentidadMediadora());
					// Si hay configurado un importe de fraccionamiento para el
					// plan/línea de la póliza
					if (impFrac != null) {
						webServicesManager.muestraFinanciar(fluxCondensatorHolder,impFrac);
					}
			}
			
			parameters = getParametrosComparativaImportes(request, polizaBean, usuario, fluxCondensatorHolder);
			
			if (polizaBean.getLinea().isLineaGanado()) {
				
				PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(polizaBean.getIdpoliza());
				
				if (polizaRC != null) {
					
					if (!MODO_LECTURA.equals(request.getParameter(MODO_LECTURA))) {
						
						List<PolizasRC> result = this.polizaRCManager.getListadoCalculosRC(
								polizaBean.getLinea().getCodplan(), 
								polizaBean.getLinea().getCodlinea(), 
								polizaBean.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
								polizaBean.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(), 
								polizaRC.getEspeciesRC().getCodespecie(),
								polizaRC.getRegimenRC().getCodregimen(), 
								polizaRC.getNumanimales());
						
						parameters.put("sumasAseguradas", result);
						
						if (polizaRC.getSumaAsegurada() != null) {
							
							for (PolizasRC dato : result) {						
								if (polizaRC.getSumaAsegurada().equals(dato.getSumaAsegurada())) {	
									polizaRC.setCodSumaAsegurada(dato.getCodSumaAsegurada());
									parameters.put("radioSumaAseg", dato.getCodSumaAsegurada());
									break;
								}
							}
						}
					}
					
					polizaBean.setPolizaRC(polizaRC);
					parameters.put("polizaRC", polizaRC);
				}
			}			
			
			String oficina = request.getParameter(OFICINA_ACTUAL);
			String vieneDeUtilidades = StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES));
			
			parameters.put(OFICINA_ACTUAL,polizaBean.getOficina());
			parameters.put("oficina","");
			
			if (oficina == null){
				parameters.put(NOMBO_OFICINA,polizaManager.getNombreOficina(new BigDecimal(polizaBean.getOficina()), polizaBean.getAsegurado().getEntidad().getCodentidad()));
			} else {
				if (!(oficina.equals("")) && isNumeric(oficina)){
					parameters.put(NOMBO_OFICINA,polizaManager.getNombreOficina(new BigDecimal(oficina), polizaBean.getAsegurado().getEntidad().getCodentidad()));
				} else {
					parameters.put(NOMBO_OFICINA,request.getParameter(NOMBO_OFICINA));
					
				}
			}
			parameters.put("entidad",polizaBean.getAsegurado().getEntidad().getCodentidad());
			parameters.put("externo",usuario.getExterno());
			parameters.put("perfil", usuario.getPerfil().substring(4));
			parameters.put(VIENE_DE_UTILIDADES, vieneDeUtilidades);
			
			if(null != polizaBean && null != polizaBean.getFechaenvio()) {
				parameters.put("isFechaEnvioPosteriorSep2020", consultaDetallePolizaManager.isFechaEnvioPosteriorSep2020(polizaBean.getFechaenvio()));
			}else {
				parameters.put("isFechaEnvioPosteriorSep2020", false);
			}
			
			
			mv = new ModelAndView("moduloPolizas/polizas/importes/importes", "resultado", fluxCondensatorHolder).
					addAllObjects(parameters);
			
			
		}catch (Exception e) {
			parameters.put(ALERTA,"Error al obtener los importes");
			parameters.put(RECOGER_POLIZA_SESION, "true");
			mv = new ModelAndView(new RedirectView(UTILIDADES_POLIZA)).addAllObjects(parameters);
			logger.error("Error al consultar los importes"  + e.getMessage(), e);
		}
		return mv;
		
	}
	
	public Map<String,Object> getParametrosComparativaImportes(HttpServletRequest request, Poliza poliza, 
			Usuario usuario, Set<VistaImportes> fluxCondensatorHolder) throws Exception{
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametrosPagos = new HashMap<String, Object>();
		Map<String,Object> paramsDesc = new HashMap<String, Object>();
		Map<String,Object> paramsFraccionamiento = new HashMap<String, Object>();
		
		Poliza polizaDefinitiva = new Poliza ();
		polizaDefinitiva.setIdpoliza(poliza.getIdpoliza());
		boolean esModoLectura =(""!=StringUtils.nullToString(request.getParameter(MODO_LECTURA)));
		
		parameters.put("polizaDefinitiva", polizaDefinitiva);
		parameters.put("numeroCuenta",AseguradoUtil.getFormattedBankAccount(poliza, true));
		parameters.put("numeroCuenta2",AseguradoUtil.getFormattedBankAccount(poliza, false));
		parameters.put(MODO_LECTURA, StringUtils.nullToString(request.getParameter(MODO_LECTURA)));
		parameters.put(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
		parameters.put(ID_POLIZA, poliza.getIdpoliza());
		parameters.put("esFinanciacionCpl", "false");
		parameters.put("plan", poliza.getLinea().getCodplan());
		parameters.put("isLineaGanado", poliza.getLinea().isLineaGanado());
		parameters.put(VALID_COMPS, StringUtils.nullToString(request.getParameter(VALID_COMPS)));
		
		if(null!=poliza.getEstadoPoliza() && null!=poliza.getEstadoPoliza().getIdestado())
			parameters.put("estadoPoliza", poliza.getEstadoPoliza().getIdestado());
		
		parametrosPagos= getParametrosPagos(poliza);
		parameters.putAll(parametrosPagos);
		if(!esModoLectura){
			int countImportes = fluxCondensatorHolder.size();
			parameters.put("countImportes", countImportes);
		}
		
		
		if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 0 ||
				poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 1) {
			paramsDesc= getParametrosDescuentosRecargos(poliza, usuario, esModoLectura);
			parameters.putAll(paramsDesc);
		
			paramsFraccionamiento= getParametrosFraccionamiento( poliza, fluxCondensatorHolder, esModoLectura);
			parameters.putAll(paramsFraccionamiento);			
			
		}
		
		parameters.put("dataCodlinea", poliza.getLinea().getCodlinea());
		parameters.put("dataCodplan", poliza.getLinea().getCodplan());
		parameters.put("dataNifcif", poliza.getAsegurado().getNifcif());		
		
		return parameters;
	}
	
	public Map<String,Object> getParametrosPagos(Poliza poliza) throws DAOException{
		Map<String, Object> parametrosPagos = new HashMap<String, Object>();
		PagoPoliza pagoPoliza = pagoPolizaManager.getPagoPolizaByPolizaId(poliza.getIdpoliza());
		if(null!= pagoPoliza){
			Map<String,Object> paramsPagoPoliza = getParametrosPagoPoliza(pagoPoliza);
			parametrosPagos.putAll(paramsPagoPoliza);
		}
		if (null!= pagoPoliza && pagoPoliza.getFormapago().equals('F')){
			
			parametrosPagos.put("idestado", poliza.getEstadoPoliza().getIdestado());
			DatosAval dv = polizaManager.GetDatosAval(poliza.getIdpoliza());
			if (dv != null){
				parametrosPagos.put(NUM_AVAL, dv.getNumeroAval());
				parametrosPagos.put(IMPORTE_AVAL, dv.getImporteAval());
			}
			else{
				parametrosPagos.put(NUM_AVAL, "No Data");
				parametrosPagos.put(IMPORTE_AVAL, "0.00");
			}
		}
		return parametrosPagos;
	}
	
	public Map<String,Object> getParametrosDescuentosRecargos(Poliza poliza, Usuario usuario, boolean esModoLectura) throws Exception{
		Map<String,Object> paramsDesc = new HashMap<String, Object>();
		paramsDesc = webServicesManager.muestraBotonDescuento(poliza, usuario);
		if(esModoLectura){
			paramsDesc.put("descuentoLectura", esModoLectura);
			paramsDesc.put("recargoLectura", esModoLectura);			
		}else{
			paramsDesc.put("descuentoLectura", null);
			paramsDesc.put("recargoLectura", null);
			
		}
		
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
	
	public Map<String,Object> getParametrosFraccionamiento(Poliza poliza, 
			Set<VistaImportes> fluxCondensatorHolder, boolean esModoLectura) throws Exception{
		Map<String,Object> paramsFraccionamiento = new HashMap<String, Object>();
		
		List<CondicionesFraccionamiento> condFracc = webServicesManager.getCondicionesFraccionamiento(poliza.getLinea().getLineaseguroid());
		paramsFraccionamiento.put("condicionesFraccionamiento", condFracc);

		//20/02/2017
		//OJO. Esta parte no funcionará cuando tengamos más de una comparartiva ya que no tenemos preparado el popUp financiar
		// como un array de valores
		if(esModoLectura){
			Set<DistribucionCoste2015> listdc = poliza.getDistribucionCoste2015s();
			Iterator<DistribucionCoste2015> iteratorDistCoste = listdc.iterator();
			DistribucionCoste2015 dc = new DistribucionCoste2015();
			while (iteratorDistCoste.hasNext()){
				dc = iteratorDistCoste.next();
				if(dc.getPeriodoFracc()!=null){
					fluxCondensatorHolder.iterator().next().setPeriodoFracc(dc.getPeriodoFracc().toString());
				}
				if(null!=dc.getImportePagoFraccAgr()){
					fluxCondensatorHolder.iterator().next().setImportePagoFraccAgr(dc.getImportePagoFraccAgr().toString());
				}
				if(null!=dc.getImportePagoFracc()){
					fluxCondensatorHolder.iterator().next().setImportePagoFracc(NumberUtils.formatear(dc.getImportePagoFracc(),2));
				}
			}
			paramsFraccionamiento.put("periodoFracc", dc.getPeriodoFracc());
			paramsFraccionamiento.put("valorOpcionFracc", dc.getValorOpcionFracc());
			paramsFraccionamiento.put("opcionFracc", dc.getOpcionFracc());
	
		}
		
		
		return paramsFraccionamiento;
	}
	
	
	public ModelAndView doVerPagos(HttpServletRequest request, HttpServletResponse response,
			Poliza polizaBean) {
		
		ModelAndView mv= null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(MODO_LECTURA,         StringUtils.nullToString(request.getParameter(MODO_LECTURA)));
		parameters.put(VIENE_DE_UTILIDADES, StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES)));
		parameters.put("idEnvio", StringUtils.nullToString(request.getParameter("idEnvio")));
		parameters.put(ID_POLIZA, StringUtils.nullToString(request.getParameter(ID_POLIZA)));
		parameters.put("formaDePago", StringUtils.nullToString(request.getParameter("formaDePago")));
		parameters.put("cicloPoliza", StringUtils.nullToString(request.getParameter("cicloPoliza")));
		parameters.put("origenllamada", StringUtils.nullToString(request.getParameter("origenllamada")));
		// Comprobamos si tiene financiacion por Saeca y si la tiene cambiamos el valor del importe en la pagina de pagos
		Poliza pol = polizaManager.getPoliza(polizaBean.getIdpoliza());
		Boolean esSaeca = polizaManager.esFinanciadaSaeca(pol.getLinea().getLineaseguroid(),pol.getColectivo().getSubentidadMediadora());
		if(esSaeca){
			DistribucionCoste2015 dcte = polizaManager.getDistCosteSaeca(pol);
			parameters.put("importePrimerPagoCliente", dcte.getImportePagoFracc());
		}
		
		mv = new ModelAndView(new RedirectView("pagoPoliza.html")).addAllObjects(parameters);
		
		return mv;
		
	}
	
	public ModelAndView doVerImportesCpl(HttpServletRequest request, HttpServletResponse response,
			Poliza polizaBean) {
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> paramsDesc = new HashMap<String, Object>();
		String modoLectura=null;
		
		Long idPoliza = null;
		ModelAndView mv= null;
		FluxCondensatorObject fluxCondensator = null;
		String vieneDeUtilidades = StringUtils.nullToString(request.getParameter(VIENE_DE_UTILIDADES));
		String grProvisionalOK= StringUtils.nullToString(request.getParameter("grProvisionalOK"));
		if (!"".equals(StringUtils.nullToString(request.getParameter(ID_POLIZA)))) {
			idPoliza = Long.parseLong(request.getParameter(ID_POLIZA));
		}
		try{
			polizaBean = seleccionPolizaManager.getPolizaById(idPoliza);
			
			modoLectura=request.getParameter(MODO_LECTURA);
			
			/*********DNF 9 Julio 2020 ESC-9321 Necesito cargar la cabecera cuando la poliza sea complementaria en modo consulta*/
			consultaDetallePolizaManager.cargaCabecera (polizaBean,request);
			
			parameters.put(MODO_LECTURA,modoLectura);
			parameters.put(VIENE_DE_UTILIDADES,vieneDeUtilidades);
			parameters.put("idpolizaCpl", idPoliza);
			parameters.put("esFinanciacionCpl", "true");
			parameters.put("muestraBotonFinanciar", "true");
			parameters.put("grProvisionalOK", grProvisionalOK);
			parameters.put("complementaria", true);
			
			/*DNF PET.70105.FII 04/03/2021*/
			parameters.put("lineaContrataSup2021", request.getParameter("lineaContrataSup2021"));
			/*fin DNF PET.70105.FII 04/03/2021*/
			
			fluxCondensator = consultaDetallePolizaManager.getDataImportesCpl(polizaBean,usuario);
			fluxCondensator.setMuestraBotonFinanciar(false);
			
			// anhadimos los datos para la forma de pago
			parameters.put("numeroCuenta", AseguradoUtil.getFormattedBankAccount(polizaBean, true));
			parameters.put("numeroCuenta2", AseguradoUtil.getFormattedBankAccount(polizaBean, false));
			parameters.put("importe1", polizaBean.getImporte());
			
			Set<PagoPoliza> pagoPolizas = polizaBean.getPagoPolizas();
			PagoPoliza pagoPoliza = pagoPolizaManager.getPagoPolizaByPolizaId(idPoliza);
			if(null!=pagoPoliza){
				if (pagoPoliza.getTipoPago().equals(Constants.PAGO_MANUAL)) {
					parameters.put(MP_PAGO_M, true);
					parameters.put(MP_PAGO_C, false);
					
				}else {
					parameters.put(MP_PAGO_C, true);
					parameters.put(MP_PAGO_M, false);
				}
				//aï¿½adimos el importe y la fecha y banco de pagosPoliza si lo tiene
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
				
				parameters.put("metodoDePago", pagoPoliza.getTipoPago());
				
				
				parameters.put("metodoDePago", pagoPoliza.getTipoPago());
				
				parameters.put("bancoDestino", pagoPoliza.getBanco());
				parameters.put("importe", pagoPoliza.getImporte());
				parameters.put("fechaPago", pagoPoliza.getFecha());
				parameters.put("iban", pagoPoliza.getIban());
				parameters
						.put("cuenta1",
								pagoPoliza.getCccbanco() != null
										&& !"".equals(pagoPoliza.getCccbanco()) ? pagoPoliza
										.getCccbanco().substring(0, 4) : "");
				parameters
						.put("cuenta2",
								pagoPoliza.getCccbanco() != null
										&& !"".equals(pagoPoliza.getCccbanco()) ? pagoPoliza
										.getCccbanco().substring(4, 8) : "");
				parameters
						.put("cuenta3",
								pagoPoliza.getCccbanco() != null
										&& !"".equals(pagoPoliza.getCccbanco()) ? pagoPoliza
										.getCccbanco().substring(8, 12) : "");
				parameters
						.put("cuenta4",
								pagoPoliza.getCccbanco() != null
										&& !"".equals(pagoPoliza.getCccbanco()) ? pagoPoliza
										.getCccbanco().substring(12, 16) : "");
				parameters
						.put("cuenta5",
								pagoPoliza.getCccbanco() != null
										&& !"".equals(pagoPoliza.getCccbanco()) ? pagoPoliza
										.getCccbanco().substring(16, 20) : "");
			}

			
			parameters.put("plan", polizaBean.getLinea().getCodplan());
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
			if (polizaBean.getLinea().getCodplan().compareTo(Constants.PLAN_2015) != -1) {
				List<CondicionesFraccionamiento> condFracc = webServicesManager.getCondicionesFraccionamiento(polizaBean.getLinea().getLineaseguroid());
				parameters.put("condicionesFraccionamiento", condFracc);
				Set<DistribucionCoste2015> listdc = polizaBean.getDistribucionCoste2015s();
				Iterator<DistribucionCoste2015> iteratorDistCoste = listdc.iterator();
				DistribucionCoste2015 dc = new DistribucionCoste2015();
				while (iteratorDistCoste.hasNext()){
					dc = iteratorDistCoste.next();
				}
				parameters.put("periodoFracc", dc.getPeriodoFracc());
				parameters.put("valorOpcionFracc", dc.getValorOpcionFracc());
				parameters.put("opcionFracc", dc.getOpcionFracc());
				
				//Código para cuando volvemos de formulario de forma de pago en la grabación definitiva a la
				//página de importes. Para saber si tenemos que mostrar el botón de financiación, como no hemos guardado
				// los datos de pago, evaluamos la distribución de coste.
				if(dc.getPeriodoFracc()!=null)
					fluxCondensator.setMuestraBotonFinanciar(true);
				//*******************************************************************************************
				
			}
			if (null!=pagoPoliza && pagoPoliza.getFormapago().equals('F')){
				fluxCondensator.setMuestraBotonFinanciar(true);
				//webServicesManager.muestraFinanciar(fluxCondensatorHolder, idPoliza, ifrac);
				parameters.put("isPagoFraccionado", pagoPolizaManager.compruebaPagoFraccionado(polizaBean));
				parameters.put("idestado", polizaBean.getEstadoPoliza().getIdestado());
				DatosAval dv = polizaManager.GetDatosAval(polizaBean.getIdpoliza());
				if (dv != null){
					parameters.put(NUM_AVAL, dv.getNumeroAval());
					parameters.put(IMPORTE_AVAL, dv.getImporteAval());
				}
				else{
					parameters.put(NUM_AVAL, "No Data");
					parameters.put(IMPORTE_AVAL, "0.00");
				}
			}
			if(""!=StringUtils.nullToString(request.getParameter(MODO_LECTURA)) && polizaBean.getEsFinanciada().equals('S')){
				//Cuando es solo lectura, como es posible que se hayan cambiado los datos de fraccionamiento en el mantenimiento, 
				//siempre que la póliza ha sido financiada hay que mostrar el botón
				fluxCondensator.setMuestraBotonFinanciar(true);			
			}		
			
			if(!modoLectura.equals(MODO_LECTURA)){
				String muestraBoton = request.getParameter("muestraBotonFinanciar");
				if(muestraBoton.equals("true")){
					fluxCondensator.setMuestraBotonFinanciar(true);
				}else{
					fluxCondensator.setMuestraBotonFinanciar(false);
				}
			}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
			if (polizaBean.getLinea().getCodplan().compareTo(Constants.PLAN_2015) != -1) {
				paramsDesc = webServicesManager.muestraBotonDescuento(polizaBean.getPolizaPpal(),usuario);
				paramsDesc.put("descuentoLectura", MODO_LECTURA.equals(modoLectura) ? true : null);
				paramsDesc.put("recargoLectura", MODO_LECTURA.equals(modoLectura) ? true : null);

				if(null!=polizaBean.getPolizaPctComisiones())
					paramsDesc.put("pctComisiones", polizaBean.getSetPolizaPctComisiones());
				
				ImporteFraccionamiento ifrac = webServicesManager.getImporteFraccionamiento(polizaBean.getLinea().getLineaseguroid(),polizaBean.getColectivo().getSubentidadMediadora());
				if(null!=ifrac && null!=ifrac.getTipo()){
					switch (ifrac.getTipo()) {
					case 0:
						paramsDesc.put("esFraccAgr", false);
						break;
					case 1:
						paramsDesc.put("esFraccAgr", true);
						break;
					default:
						break;
					}
				}else{
					paramsDesc.put("esFraccAgr", false);
				}

				// Ver Comisiones
				fluxCondensator =  polizasPctComisionesManager.dameComisiones(fluxCondensator, polizaBean, usuario,fluxCondensator.getPrimaNetaB());	
			}
			fluxCondensator.setCosteTomador(NumberUtils.formatear(new BigDecimal(fluxCondensator.getCosteTomador()),2));
			fluxCondensator.setImporteTomador(NumberUtils.formatear(new BigDecimal(fluxCondensator.getImporteTomador()),2));
			fluxCondensator.setTotalCosteTomador(NumberUtils.formatear(new BigDecimal(fluxCondensator.getTotalCosteTomador()),2));
			
			parameters.put("dataCodlinea", polizaBean.getLinea().getCodlinea());
			parameters.put("dataCodplan", polizaBean.getLinea().getCodplan());
			parameters.put("dataNifcif", polizaBean.getAsegurado().getNifcif());
			parameters.put("perfil", usuario.getPerfil().substring(4));
			
			mv = new ModelAndView("moduloPolizas/polizas/importes/importesCpl", "fluxCondensator", fluxCondensator).
					addAllObjects(parameters).addAllObjects(paramsDesc);
		}catch (Exception e) {
			parameters.put(ALERTA,"Error al obtener los importes");
			parameters.put(RECOGER_POLIZA_SESION, "true");
			if (vieneDeUtilidades.equals(VIENE_DE_UTILIDADES) || vieneDeUtilidades.equals("true"))
				mv = new ModelAndView(new RedirectView(UTILIDADES_POLIZA)).addAllObjects(parameters);
			else
				mv = new ModelAndView(new RedirectView("seleccionPoliza.html")).addAllObjects(parameters);
				
			logger.error("Error al consultar los importes"  + e.getMessage(), e);
		}
		return mv;
		
	}

	private Map<String, Object> getParametrosPagoPoliza(PagoPoliza pagoPoliza){
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
			parameters.put(MP_PAGO_M, true);
			parameters.put(MP_PAGO_C, false);
			
		}else {
			parameters.put(MP_PAGO_C, true);
			parameters.put(MP_PAGO_M, false);
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
	
	public static boolean isNumeric(String inputData) {
		  return inputData.matches("[-]?\\d+(\\.\\d+)?");
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

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public void setPolizasPctComisionesManager(
			IPolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setPolizaRCManager(PolizaRCManager polizaRCManager) {
		this.polizaRCManager = polizaRCManager;
	}	
}
