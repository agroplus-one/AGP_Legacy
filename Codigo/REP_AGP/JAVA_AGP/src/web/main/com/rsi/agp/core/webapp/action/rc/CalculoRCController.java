package com.rsi.agp.core.webapp.action.rc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.manager.impl.anexoRC.calculo.ICalculoRCManager;
import com.rsi.agp.core.managers.impl.anexoMod.calculo.ICalculoModificacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import org.springframework.ui.ModelMap;
import com.rsi.agp.core.webapp.action.utilidades.ListaIncidenciasAgroController;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

public class CalculoRCController extends BaseMultiActionController {
	
	private ICalculoRCManager calculoRCManager;
	private String successView;
//	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
//	
//	private ImpresionIncidenciasModController impresionIncidenciasModController;
//	private ListaIncidenciasAgroController listaIncidenciasAgroController;
	
	/**
	 * Llama al SW de cálculo de modificaciones para el anexo recibido como parámetro y muestra el resultado en pantalla
	 * @param request
	 * @param response
	 * @param anexoModificacion Objeto que encapsula la información del anexo de modificación cuya distribución de costes se va a solicitar
	 * @return
	 */
	public ModelAndView doCalculoRC (HttpServletRequest request, 	HttpServletResponse response, ReduccionCapital reduccionCapital) {
		
		// Obtiene la ruta para buscar el WSDL del SW 
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		//private static final Log logger = LogFactory.getLog(ConfirmacionRCManager.class);
		
		// Obtiene el usuario cargado en sesión para registrar en el sistema la llamada al SW
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String idRC  = com.rsi.agp.core.webapp.util.StringUtils.nullToString(request.getParameter("id"));
		boolean actualizaComMediadora = false;
		
		logger.debug("CALCULO RC: ");
		
		ReduccionCapital rc = null;
		try {	
			rc = calculoRCManager.getRC(Long.parseLong(idRC));
			logger.info("RC NUEVO");
			if (rc == null) throw new NullPointerException("La reducción es NULL!");
		} catch (RuntimeException e) {
			throw new WebServiceException("Error en la llamada a los Servicios Web. El Id del anexo " + reduccionCapital.getId() + " no existe en la BBDD!! ", e);
		}
		
		// Llama al SW de cálculo de modificación para el anexo recibido como parámetro y devuelve un mapa con la respuesta
		Map<String, Object> resultado = calculoRCManager.calcularModificacion(realPath, rc.getId(), usuario,actualizaComMediadora);
		 
		// Se reenvían a la pantalla de cálculo de modificación los parámetros que gestionan las redirecciones
		resultado.put("vieneDeListadoAnexosMod", request.getParameter("vieneDeListadoAnexosMod"));		resultado.put("redireccion", request.getParameter("redireccion"));
		resultado.put("errorTramite", request.getParameter("errorTramite"));
		resultado.put("perfil34", request.getParameter("perfil34"));
		resultado.put("id", idRC);
//		
//		//Antes de llamar a la parte de jMesa hay que comprobar el estado por si debe aparecer en modo lectura
//		// Si el anexo está en estado 'Enviado Correcto' se visualiza el listado en modo lectura sí o sí
//		String modoLectura = request.getParameter("modoLectura");
//		if (Constants.ANEXO_MODIF_ESTADO_CORRECTO.equals(anexoModificacion.getEstado().getIdestado())){
//			modoLectura = "true";
//			request.setAttribute("modoLectura", modoLectura);
//		}
//		resultado.put("modoLectura", modoLectura);
//		
		//Cargamos la poliza de la BBDD
		Poliza poliza = null;
		
		try {	
			poliza = calculoRCManager.getPoliza(new Long(reduccionCapital.getPoliza().getIdpoliza()));
			if (poliza == null) throw new NullPointerException("La poliza es NULL!");
		} catch (RuntimeException e) {
			throw new WebServiceException("Error en la llamada a los Servicios Web. El Id de la Poliza " + reduccionCapital.getPoliza().getIdpoliza() + " no existe en la BBDD!! ", e);
		}
		
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("muestraBotonRecargos", false);
		params.put("muestraBotonDescuentos", false);
		
		ModelMap modelo = new ModelMap().addAttribute("reduccionCapital", reduccionCapital);
		
		ModelAndView mv = new ModelAndView(successView, modelo).addAllObjects(resultado).addAllObjects(params);
		

		// Redirige a la pantalla de 'Cálculo de modificación'
		return mv;
	}

	public void setCalculoRCManager(ICalculoRCManager calculoRCManager) {
		this.calculoRCManager = calculoRCManager;
	}
	
	
	/**
	 * Obtiene la distribución de costes de anexo de modificación recibido como parámetro y la muestra en pantalla
	 * @param request
	 * @param response
	 * @param anexoModificacion Objeto que encapsula la información del anexo de modificación cuya distribución de costes se va a visualizar
	 * @return
	 */
//	public ModelAndView doConsultaDistCoste (HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) {
//		
//		// Obtiene la distribución de costes almacenada en BBDD asociada al anexo recibido como parámetro  
//		Map<String, Object> resultado = calculoModificacionManager.consultaDistribucionCoste(anexoModificacion);
//		
//		// Parámetros que gestionan las redirecciones desde la pantalla de visualización de cálculo de modificación
//		if (anexoModificacion.getCupon() != null && anexoModificacion.getCupon().getIdcupon() != null) {
//			// Si se ha informado el identificador del cupón, viene de la pantalla 'Relación de Modificaciones e Incidencias'
//			resultado.put("vieneDeRelacionModfInc", 1);
//		}
//		
//		resultado.put("vieneDeListadoAnexosMod", request.getParameter("vieneDeListadoAnexosMod"));
//		resultado.put("redireccion", request.getParameter("redireccion"));
//		
//		// Se indica a la página que se accede en modo lectura para que no se muestre el botón 'Enviar'
//		resultado.put("modoLectura", true);		
//		
//		
//		// Obtiene el usuario cargado en sesión para registrar en el sistema la llamada al SW
//		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
//		AnexoModificacion anexo = null;
//		try {	
//			anexo = calculoModificacionManager.getAnexo(new Long(anexoModificacion.getId()));
//			if (anexo == null) throw new NullPointerException("El anexo es NULL!");
//		} catch (RuntimeException e) {
//			throw new WebServiceException("Error en la llamada a los Servicios Web. El Id del anexo " + anexoModificacion.getId() + " no existe en la BBDD!! ", e);
//		}
//		Poliza poliza = null;
//		try {	
//			poliza = calculoModificacionManager.getPoliza(new Long(anexo.getPoliza().getIdpoliza()));
//			if (poliza == null) throw new NullPointerException("La poliza es NULL!");
//		} catch (RuntimeException e) {
//			throw new WebServiceException("Error en la llamada a los Servicios Web. El Id de la Poliza " + anexo.getPoliza().getIdpoliza() + " no existe en la BBDD!! ", e);
//		}
//		
//		// MODO CONSULTA BOTONES DESCUENTO Y RECARGO
//		Map<String,Object> paramsDesc = new HashMap<String, Object>();
//		// Si en el anexo se aplicaron descuentos o recargos 
//		if (anexo.getPctdescelegido() != null || anexo.getPctrecarelegido() != null){
//			try {
//				paramsDesc = calculoModificacionManager.muestraBotonDescuentoAnexo(poliza,anexo,usuario);
//			} catch (Exception e) {
//				logger.error("Error en la lógica de los botones de Descuentos y recargos del anexo en modo lectura",e);
//			}
//			paramsDesc.put("descuentoLectura", true);
//			paramsDesc.put("recargoLectura", true);
//			
//			paramsDesc.put("descuento", (anexo.getPctdescelegido() != null)
//					? anexo.getPctdescelegido().setScale(2)
//					: new BigDecimal(0));
//			
//			paramsDesc.put("recargo", (anexo.getPctrecarelegido() != null)
//					? anexo.getPctrecarelegido().setScale(2)
//					: new BigDecimal(0));
//		}
//		
//		// Redirige a la pantalla de 'Cálculo de modificación'
//		return new ModelAndView(successView).addAllObjects(resultado).addAllObjects(paramsDesc);
//	}
//	
//	public ModelAndView doConsultaDistCosteDesdeMofidicacionesIncidencias(HttpServletRequest req, HttpServletResponse res){
//		String idCupon = null; 
//		idCupon = req.getParameter("idCuponVerDC");
//		
//		// Pet. 50775 ** MODIF TAM (07.05.2018) * Resolución de Incidencias ** INICIO
//		// Si viene informado con "null", lo pasamos al valor null
//		if(idCupon.equals("null")){
//			idCupon = null;
//		}
//		logger.debug("Valor de idCupon:"+idCupon);
//		
//		AnexoModificacion anexo = new AnexoModificacion();
//		
//		
//		//logger.debug("Valor de anexo:"+anexo.getId());
//		try {
//			// Pet. 50775 ** Se incluye el if de idCupon
//			if(idCupon !=null){
//			   anexo = this.calculoModificacionManager.getAnexoPorIdCupon(idCupon);
//			   logger.debug("Valor de anexo:"+anexo);
//			}
//			
//		} catch (DAOException e) {
//			throw new WebServiceException("Error en la llamada a los Servicios Web. El Cupón " + idCupon + " no existe en la BBDD!! ", e);
//		}
//
//		// Pet. 50775 ** Se incluye el if y el elese
//		// esto es lo que había antes: return this.doConsultaDistCoste(req, res, anexo);
//		if (idCupon !=null && anexo !=null){
//			return this.doConsultaDistCoste(req, res, anexo);
//		}else{
//			
//			Poliza poliza = null;
//			Long idpoliza = null;
//			
//			idpoliza = new Long(req.getParameter("polizaOperacion"));
//			
//			poliza = calculoModificacionManager.getPoliza(idpoliza);
//
//			//ModelAndView mv =("redirect:/impresionIncidenciasMod.html"); 
//			ModelAndView mv = null;
//	
//            AnexoModificacion anexoModificacion = new AnexoModificacion();
//            BigDecimal codLinea = new BigDecimal(req.getParameter("linea"));
//            String nombreComplAsegurado = poliza.getAsegurado().getNombreCompleto();
//			
//            anexoModificacion.setPoliza(poliza);
//            
//            anexoModificacion.getPoliza().setReferencia(req.getParameter("referencia"));
//			anexoModificacion.getPoliza().getLinea().setCodlinea(codLinea);
//			anexoModificacion.getPoliza().getLinea().setCodplan(poliza.getLinea().getCodplan());
//			anexoModificacion.getPoliza().getLinea().setNomlinea(poliza.getLinea().getNomlinea());
//			anexoModificacion.getPoliza().setModuloPolizas(poliza.getModuloPolizas());
//			anexoModificacion.getPoliza().getAsegurado().setNombreCompleto(nombreComplAsegurado);
//			anexoModificacion.getPoliza().setFechaenvio(poliza.getFechaenvio());
//			
//			req.setAttribute("fechaEnvio", poliza.getFechaenvio());
//			
//			
//            mv  = impresionIncidenciasModController.doImprimirIncidencias(req, res, anexoModificacion);
//            
//            mv.addObject("alerta", "Se ha producido un error durante la consulta de Distribución de Coste. Anexo o idCupon nulos");
//            mv.addObject("poliza", poliza);
//            
//			return mv;
//			// Pet. 50775 ** MODIF TAM (07.05.2018) * Resolución de Incidencias ** FIN
//		}
//	}
//
//    // Pet. 50775 ** MODIF TAM (07.05.2018) ** Fin * Resolución Incidencias //
//	
//	public ModelAndView doConsultaDistCosteDesdeIncidencias(HttpServletRequest req, HttpServletResponse res){
//		String idCupon = req.getParameter("idCuponVerDC");
//		AnexoModificacion anexo = new AnexoModificacion();
//		
//		Map<String, Object> params = new HashMap<String, Object>();
//		
//		// MODIF TAM (27.07.2018)
//		VistaIncidenciasAgro vistaIncAgro = new VistaIncidenciasAgro();
//		this.parametrosVueltaConsultaDistCoste(req, vistaIncAgro);
//		params.put("vuelta", vistaIncAgro);
//		params.put("fechaEnvioDesdeStr", vistaIncAgro.getFechaEnvioDesdeStr());
//		params.put("fechaEnvioHastaStr", vistaIncAgro.getFechaEnvioHastaStr());
//			
//		if(idCupon.equals("null")){
//			idCupon = null;
//		}
//		
//		try {
//			// Pet. 50775 ** Se incluye el if de idCupon
//			if(idCupon !=null){
//				anexo = this.calculoModificacionManager.getAnexoPorIdCupon(idCupon);
//			}
//		} catch (DAOException e) {
//			throw new WebServiceException("Error en la llamada a los Servicios Web. El Cupón " + idCupon + " no existe en la BBDD!! ", e);
//		}
//		
//		String urlVuelta = this.generarUrlVueltaListaIncidenciasAgro(req, vistaIncAgro);
//		
//		
//		params.put("listaIncidenciasAgro", urlVuelta);
//		
//		// Pet. 50775 ** Se incluye el if y el elese
//		// esto es lo que había antes: return this.doConsultaDistCoste(req, res, anexo).addAllObjects(params);
//		if (idCupon !=null && anexo !=null){
//			return this.doConsultaDistCoste(req, res, anexo).addAllObjects(params);
//		}else{
//			ModelAndView mv = null;
//			logger.debug("Valor de opcionBusqueda:"+req.getParameter("opcionBusqueda"));
//			
//			
//			mv = listaIncidenciasAgroController.doConsultar(req, res);
//			
//			params.put("codplan", req.getParameter("planConsList"));
//
//			mv.addAllObjects(params);
//            
//            mv.addObject("alerta", "Se ha producido un error durante la consulta de Distribución de Coste. Anexo o idCupon nulos");
//            
//			return mv;
//		}
//	}
//	
//	
//	/**** MODIF TAM (27.07.2018) **/
//	private boolean parametrosVueltaConsultaDistCoste(final HttpServletRequest req, final VistaIncidenciasAgro vista) {
//		boolean hasFilters = false;
//		String idIncidencia = req.getParameter("idincidencia");
//		String codEntidad = req.getParameter("codentidad");
//		String oficina = req.getParameter("oficina");
//		String referencia =req.getParameter("referenciaCons");
//		String entMediadora = req.getParameter("entmediadora");
//		String subentMediadora = req.getParameter("subentmediadora");
//		String delegacion = req.getParameter("delegacion");
//		String codPlan = req.getParameter("codplan");
//		String codLinea = req.getParameter("codlinea");
//		String codEstado = req.getParameter("codestado");
//		String codEstadoAgro = req.getParameter("codestadoagro");
//		String nifCif = req.getParameter("nifcifCons");
//		String tipoReferencia = req.getParameter("tiporef");
//		String idCupon = req.getParameter("idcupon");
//		String asunto = req.getParameter("asunto");
//		String fechaEnvioDesde = req.getParameter("fechaEnvioDesdeId");
//		String fechaEnvioHasta = req.getParameter("fechaEnvioHastaId");
//		String numIncidencia = req.getParameter("numIncidencia");
//		String codUsuario = req.getParameter("codUsuarioVolver");
//		
//		if(StringUtils.isNotBlank(idIncidencia)) {
//			hasFilters = true;
//			vista.setIdincidencia(Long.parseLong(idIncidencia));
//		}
//		if(StringUtils.isNotBlank(codEntidad)) {
//			hasFilters = true;
//			vista.setCodentidad(new BigDecimal(codEntidad));
//		}
//		if(StringUtils.isNotBlank(oficina)) {
//			hasFilters = true;
//			vista.setOficina(oficina);
//		}
//		if(StringUtils.isNotBlank(entMediadora)) {
//			hasFilters = true;
//			vista.setEntmediadora(new BigDecimal(entMediadora));
//		}
//		if(StringUtils.isNotBlank(subentMediadora)) {
//			hasFilters = true;
//			vista.setSubentmediadora(new BigDecimal(subentMediadora));
//		}
//		if(StringUtils.isNotBlank(delegacion)) {
//			hasFilters = true;
//			vista.setDelegacion(new BigDecimal(delegacion));
//		}
//		if(StringUtils.isNotBlank(codPlan)) {
//			hasFilters = true;
//			vista.setCodplan(new BigDecimal(codPlan));
//		}
//		if(StringUtils.isNotBlank(codLinea)) {
//			hasFilters = true;
//			vista.setCodlinea(new BigDecimal(codLinea));
//		}
//		if(StringUtils.isNotBlank(codEstado)) {
//			hasFilters = true;
//			vista.setCodestado(new BigDecimal(codEstado));
//		}
//		if(StringUtils.isNotBlank(codEstadoAgro)) {
//			hasFilters = true;
//			vista.setCodestadoagro(codEstadoAgro.charAt(0));
//		}
//		if(StringUtils.isNotBlank(nifCif)) {
//			hasFilters = true;
//			vista.setNifcif(nifCif);
//		}
//		if(StringUtils.isNotBlank(tipoReferencia)) {
//			hasFilters = true;
//			vista.setTiporef(tipoReferencia.charAt(0));
//		}
//		if(StringUtils.isNotBlank(idCupon)) {
//			hasFilters = true;
//			vista.setIdcupon(idCupon);
//		}
//		if(StringUtils.isNotBlank(asunto)) {
//			hasFilters = true;
//			vista.setAsunto(asunto);
//			vista.setCodasunto(asunto);
//		}
//		
//		if(StringUtils.isNotBlank(referencia)) {
//			hasFilters = true;
//			vista.setReferencia(referencia);
//		}
//		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			if(StringUtils.isNotBlank(fechaEnvioDesde)) {
//				hasFilters = true;
//				vista.setFechaEnvioDesde(sdf.parse(fechaEnvioDesde));
//			}
//			if(StringUtils.isNotBlank(fechaEnvioHasta)) {
//				hasFilters = true;
//				vista.setFechaEnvioHasta(sdf.parse(fechaEnvioHasta));
//			}	
//		} catch (ParseException e) {
//			logger.error(e.getMessage());
//		}
//		if(StringUtils.isNotBlank(numIncidencia)) {
//			hasFilters = true;
//			vista.setNumero(new BigDecimal(numIncidencia));
//		}
//		if(StringUtils.isNotBlank(codUsuario)) {
//			hasFilters = true;
//			vista.setCodusuario(codUsuario);
//		}
//		
//		return hasFilters;
//	}
//	/**** MODIF TAM (27.07.2018) **/
//	
//	private String generarUrlVueltaListaIncidenciasAgro(HttpServletRequest req, VistaIncidenciasAgro vistaIncAgro) {
//		StringBuilder url = new StringBuilder("listaIncidenciasAgro.run?method=doConsultar&opcionBusqueda=");
//		String tipoBusqueda = req.getParameter("tipoBusqueda");
//		url.append(tipoBusqueda);
//		if(StringUtils.equals(tipoBusqueda, "p")){
//			String plan = req.getParameter("plan");
//			String referencia = req.getParameter("referenciaDC");
//			url.append("&poliza_plan=").append(plan).append("&referencia=").append(referencia);
//		} else {
//			String plan = req.getParameter("plan");
//			String linea = req.getParameter("linea");
//			String nifCif = req.getParameter("nifcif");
//			url.append("&plan=").append(plan).append("&linea=").append(linea).append("&nifcif=").append(nifCif);
//		}
//		if (vistaIncAgro != null) {
//			if(vistaIncAgro.getIdincidencia() != null) {
//				url.append("&idincidencia=").append(vistaIncAgro.getIdincidencia());
//			}
//			if(vistaIncAgro.getCodentidad() != null) {
//				url.append("&codentidad=").append(vistaIncAgro.getCodentidad());
//			}
//			if(StringUtils.isNotEmpty(vistaIncAgro.getOficina())) {
//				url.append("&oficina=").append(vistaIncAgro.getOficina());
//			}
//			if(StringUtils.isNotEmpty(vistaIncAgro.getReferencia())) {
//				url.append("&referencia=").append(vistaIncAgro.getReferencia());
//			}
//			if(vistaIncAgro.getEntmediadora() != null) {
//				url.append("&entmediadora=").append(vistaIncAgro.getEntmediadora());
//			}
//			if(vistaIncAgro.getSubentmediadora() != null) {
//				url.append("&subentmediadora=").append(vistaIncAgro.getSubentmediadora());
//			}
//			if(vistaIncAgro.getDelegacion() != null) {
//				url.append("&delegacion=").append(vistaIncAgro.getDelegacion());
//			}
//			if(vistaIncAgro.getCodplan() != null) {
//				url.append("&codplan=").append(vistaIncAgro.getCodplan());
//			}
//			if(vistaIncAgro.getCodlinea() != null) {
//				url.append("&codlinea=").append(vistaIncAgro.getCodlinea());
//			}
//			if(vistaIncAgro.getCodestado() != null) {
//				url.append("&codestado=").append(vistaIncAgro.getCodestado());
//			}
//			if(vistaIncAgro.getCodestadoagro() != null) {
//				url.append("&codestadoagro=").append(vistaIncAgro.getCodestadoagro());
//			}
//			if(StringUtils.isNotEmpty(vistaIncAgro.getNifcif())) {
//				url.append("&nifcif=").append(vistaIncAgro.getNifcif());
//			}
//			if(vistaIncAgro.getTiporef() != null) {
//				url.append("&tiporef=").append(vistaIncAgro.getTiporef());
//			}
//			if(StringUtils.isNotEmpty(vistaIncAgro.getIdcupon())) {
//				url.append("&idcupon=").append(vistaIncAgro.getIdcupon());
//			}
//			if(StringUtils.isNotEmpty(vistaIncAgro.getCodasunto())) {
//				url.append("&asunto=").append(vistaIncAgro.getCodasunto());
//			}
//			if(vistaIncAgro.getFechaEnvioDesde() != null) {
//				url.append("&fechaEnvioDesdeId=").append(vistaIncAgro.getFechaEnvioDesdeStr());
//			}
//			if(vistaIncAgro.getFechaEnvioHasta() != null) {
//				url.append("&fechaEnvioHastaId=").append(vistaIncAgro.getFechaEnvioHastaStr());
//			}
//		}
//		return url.toString();
//	}
//	
//	/**
//	 * Setter para Spring
//	 * @param calculoModificacionManager
//	 */
//	public void setCalculoModificacionManager(ICalculoModificacionManager calculoModificacionManager) {
//		this.calculoModificacionManager = calculoModificacionManager;
//	}
//
	/**
	 * Setter para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

//	public void doComprobarTotalRecargo(HttpServletRequest request, HttpServletResponse response){
//		//idAnexo=" + idAnexo + "&recargo=" + recargo,
//		Long  idAnexo = new Long(request.getParameter("idAnexo"));
//		BigDecimal recargo = new BigDecimal(request.getParameter("recargo"));
//		
//		String resultado = "";
//		if (recargo != null) {
//			BigDecimal sumaComisiones=new BigDecimal(0);
//			try {
//				sumaComisiones = this.calculoModificacionManager.comprobarTotalRecargo(idAnexo,recargo);
//			} catch (Exception e) {
//				logger.debug("error en CalculoModificacionController.doComprobarTotalRecargo ",e);
//			}
//				if (sumaComisiones.compareTo(new BigDecimal(90.00))==1){
//					logger.debug("sumaComisiones: "+sumaComisiones);
//					resultado =  bundle.getString("anexoModificacion.recargo.KO");
//				}
//		}
//		
//		try {
//			JSONObject json = new JSONObject();
//			json.put("alerta", resultado);
//			getWriterJSON(response,  json);
//		} catch (JSONException e) {
//			logger.error("Ocurrio un error al crear el objeto json - CalculoModificacionController.doComprobarTotalRecargo", e);
//		}
//	}
//	
//	//Pet. 50775 ** MODIF TAM (07.05.2018) ** INICIO * Resolución Incidencias //
//	public void setImpresionIncidenciasModController(
//			ImpresionIncidenciasModController impresionIncidenciasModController) {
//		this.impresionIncidenciasModController = impresionIncidenciasModController;
//	}
//
//	public void setListaIncidenciasAgroController(
//			ListaIncidenciasAgroController listaIncidenciasAgroController){
//		this.listaIncidenciasAgroController = listaIncidenciasAgroController;
//	}
	
	
	
	
}
