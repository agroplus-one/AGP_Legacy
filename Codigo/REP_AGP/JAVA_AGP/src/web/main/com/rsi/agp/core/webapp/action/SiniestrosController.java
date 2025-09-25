package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.SiniestrosManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestradoDV;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.EstadoSiniestro;
import com.rsi.agp.dao.tables.siniestro.ParcelaSiniestro;
import com.rsi.agp.dao.tables.siniestro.Siniestro;

import es.agroseguro.acuseRecibo.AcuseRecibo;

public class SiniestrosController extends BaseMultiActionController {
	
	/*** SONAR Q ** MODIF TAM(26.11.2021) ***/
	/** - Se ha eliminado todo el código comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/

	private Log logger = LogFactory.getLog(SiniestrosController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private SiniestrosManager siniestrosManager;
	private ParcelasSiniestradasController parcelasSiniestradasController;
	
	/** CONSTANTES SONAR Q ** MODIF TAM (26.11.2021) ** Inicio **/
	private final static String FORMAT_DATE = "dd/MM/yyyy";
	private final static String USU = "usuario";
	private final static String MODO_LECT = "modoLectura";
	private final static String IDPOL = "idPoliza";
	private final static String ALT_WS = "altaWs";
	private final static String ERROR = "Se ha producido un error";
	private final static String ALERT = "alerta"; 
	private final static String SIN_OK= "mensaje.siniestro.alta.KO";
	private final static String ERROR_INDF = "Se ha producido un error indefinido";
	private final static String SIN = "siniestroBean"; 
	private final static String DIR_DATSIN = "/moduloUtilidades/siniestros/datosSiniestro";
	private final static String UTILIDADES = "fromUtilidades"; 
	private final static String ID_SIN = "idSiniestro";
	private final static String OR_LLAM = "origenLlamada";
	private final static String WEB_INF = "/WEB-INF/";
	private final static String MSJ = "mensaje";
	private final static String RED_UTL_SIN = "redirect:/utilidadesSiniestros.run";
	private final static String MSJ_BAJ_KO = "mensaje.baja.KO";
	private final static String ERR_LENGTH = "errLength";
	private final static String OPER = "operacion";
	private final static String SINIESTRO = "siniestro";
			
	/** CONSTANTES SONAR Q ** MODIF TAM (26.11.2021) ** Fin **/

	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(	new SimpleDateFormat(FORMAT_DATE), true));
	}
	
	
	public ModelAndView doAlta(HttpServletRequest request,
			HttpServletResponse response, Siniestro siniestro) throws Exception {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU); 
		Poliza poliza = null;
		String idPoliza = null;
		String altaWs = null;
		try {
			boolean modoLectura = (FiltroUtils.noEstaVacio(request.getParameter(MODO_LECT ))) &&
					  "true".equals(request.getParameter(MODO_LECT )) ? true : false;
			
			if (request.getParameter(IDPOL) == null) {
				idPoliza = siniestro.getPoliza().getIdpoliza().toString();
			} else {
				idPoliza = request.getParameter(IDPOL);
			}
			altaWs = StringUtils.nullToString(request.getParameter(ALT_WS));
			parametros.put(ALT_WS, altaWs);
			if (altaWs.equals("true")){
				siniestro.setNumerosiniestro(Constants.SINIESTRO_WS_SIN_NUMERO);
			}
			poliza = siniestrosManager.getPoliza(new Long(idPoliza));
			
			siniestro = setPersonaContacto(poliza.getAsegurado(), siniestro);
			if(poliza.getSiniestros().size() == 0) {
				siniestro.setNumsiniestro(new BigDecimal(poliza.getSiniestros().size() + 1));
			}
			else {
				siniestro.setNumsiniestro(siniestrosManager.getNuevoNumSiniestro(new Long(idPoliza)));
			}
			
			
			if (!StringUtils.nullToString(siniestro.getRazonsocial()).equals("") && siniestro.getRazonsocial().length() > 20){
				siniestro.setRazonsocial(siniestro.getRazonsocial().substring(0, 20));
			}
			EstadoSiniestro estadoSiniestro = new EstadoSiniestro(Constants.SINIESTRO_ESTADO_PROVISIONAL);
			
			siniestrosManager.guardarSiniestro(siniestro, estadoSiniestro, usuario, true);
			
			parametros = rellenaParametros(modoLectura,poliza,request, siniestro != null ? siniestro.getId() : null);
			
		} catch (BusinessException be) {

			logger.error(ERROR, be);
			parametros.put(ALERT, bundle.getString(SIN_OK));

		} catch (Exception be) {

			logger.error(ERROR_INDF, be);
			parametros.put(ALERT, bundle.getString(SIN_OK));

		}
		return new ModelAndView(DIR_DATSIN, SIN, siniestro).addAllObjects(parametros);
		
	}
	/**
	 * 
	 * 24/04/2014 U029769
	 * @param modoLectura
	 * @param poliza
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> rellenaParametros(boolean modoLectura, Poliza poliza,HttpServletRequest request, Long idSiniestro) throws Exception {
		
		List<Riesgo> listaRiesgos = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			listaRiesgos = siniestrosManager.getRiesgos(poliza);
			parametros.put("listaRiesgos", listaRiesgos);
			
			parametros.put("nifcif", poliza.getAsegurado().getTipoidentificacion());
			parametros.put(IDPOL, poliza.getIdpoliza());
			
			if (!parametros.containsKey(MODO_LECT )) {				
				parametros.put(MODO_LECT , modoLectura);
			}
			boolean fromUtilidades	= (FiltroUtils.noEstaVacio(request.getParameter(UTILIDADES))) &&
			  						  "true".equals(request.getParameter(UTILIDADES)) ? true : false;
			parametros.put(UTILIDADES, fromUtilidades);
			
			if (idSiniestro != null) parametros.put(ID_SIN, idSiniestro);
			
			
		} catch (BusinessException be) {

			logger.error(ERROR, be);
			throw be;

		} catch (Exception be) {
			logger.error(ERROR_INDF, be);
			throw be;

		}
		return parametros;
	}


	public ModelAndView doEdita(HttpServletRequest request,
			HttpServletResponse response, Siniestro siniestro) throws Exception {
		
		String idSiniestro = null;
		String idPoliza = null;
		Poliza poliza = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		Map<String, Object> descripciones = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU); 
		
		boolean modoLectura = (FiltroUtils.noEstaVacio(request.getParameter(MODO_LECT ))) &&
				  "true".equals(request.getParameter(MODO_LECT )) ? true : false;

		/* SONAR Q */
		idPoliza = obtenerIdPoliza(request, siniestro);
		idSiniestro = obtenerIdSin2(request, siniestro);
		/* FIN SONAR Q */
		
		try {
			poliza = siniestrosManager.getPoliza(new Long(idPoliza));
			
			parametros = rellenaParametros(modoLectura,poliza,request, idSiniestro != null ? new Long (idSiniestro) : null);

			//  Modo "EDICION" solo si esta en estado 1(borrador) , 4(erroneo) y 5 (definitivo)
			if ((siniestrosManager.tieneEstado(new Long(idSiniestro),Constants.SINIESTRO_ESTADO_PROVISIONAL) ||
					siniestrosManager.tieneEstado(new Long(idSiniestro),Constants.SINIESTRO_ESTADO_ENVIADO_ERROR) || 
					siniestrosManager.tieneEstado(new Long(idSiniestro),Constants.SINIESTRO_ESTADO_DEFINITIVO))&& !modoLectura) {
					
					siniestro = siniestrosManager.buscarSiniestro(new Long(idSiniestro));
					// llamo a una funcion que me devuelve un hasmap con las descripciones de via, provincia,localidad
					descripciones = siniestrosManager.buscarDescripciones(siniestro);
                     
					parametros.put("desc_via", descripciones.get("via"));
					parametros.put("desc_localidad", descripciones.get("localidad"));
					parametros.put("desc_provincia", descripciones.get("provincia"));
					
					EstadoSiniestro estadoSiniestro = new EstadoSiniestro(Constants.SINIESTRO_ESTADO_PROVISIONAL);
					//Facturacion: al editar no hay que facturar
					siniestrosManager.guardarSiniestro(siniestro, estadoSiniestro, usuario, false);
					
			// Modo "LECTURA"
			} else {
					siniestro = siniestrosManager.buscarSiniestro(new Long(idSiniestro));
					parametros.put(MODO_LECT , true);
			}
			siniestro.setPoliza(poliza);
			
			if (!StringUtils.nullToString(siniestro.getRazonsocial()).equals("") && siniestro.getRazonsocial().length() > 20){
				siniestro.setRazonsocial(siniestro.getRazonsocial().substring(0, 20));
			}
		} catch (BusinessException be) {
			logger.error(ERROR, be);
			parametros.put(ALERT, bundle.getString("mensaje.modificacion.KO"));
		} catch (Exception be) {
			logger.error(ERROR_INDF, be);
			parametros.put(ALERT, bundle.getString("mensaje.modificacion.KO"));
		}
		return new ModelAndView(DIR_DATSIN, SIN, siniestro).addAllObjects(parametros);
	}
	
	public ModelAndView doPasarDefinitiva(HttpServletRequest request,HttpServletResponse response, Siniestro siniestro) throws Exception{
		String origenLlamada=null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		Siniestro siniestroBusqueda = siniestro;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU); 
		
		/* SONAR Q */
		origenLlamada = obtenerOrigenLlamada(request);
		String idPoliza = obteneridPol(request, siniestro);
		
		Long idSiniestro = obtenerIdSin(request, siniestro); 
		/* FIN SONAR Q */
		Long lineaSeguroId=null;
		
		
		try{
			siniestro = siniestrosManager.buscarSiniestro(idSiniestro);
			lineaSeguroId=siniestro.getPoliza().getLinea().getLineaseguroid();			
			boolean tieneParcelas = false;
			boolean tieneDuplicados = false;
			String mensajeSin = "";
			//revisamos si hay algun codconcepto duplicado en los datos variables de cada capital asegurado del siniestro
			// si hay duplicados mostramos una alerta
			tieneDuplicados = siniestrosManager.verificarDuplicadosSiniestro(siniestro);
			if (!tieneDuplicados){
				for (ParcelaSiniestro ps : siniestro.getParcelasSiniestros()){
					for (CapAsegSiniestro cas: ps.getCapAsegSiniestros()){
						if (cas.getAltaensiniestro() != null && cas.getAltaensiniestro().equals(new Character('S'))){
							tieneParcelas = true;
							Short estadoSin = Constants.SINIESTRO_ESTADO_DEFINITIVO;
							//	-------------------------------------
							//  llamada al servicio web de VALIDACIÓN
							//	-------------------------------------
							if (siniestro.getNumerosiniestro() != null){ // siniestro por WS
								
								String realPath = this.getServletContext().getRealPath(WEB_INF);
								
								parametros = siniestrosManager.validarSiniestro(idPoliza,siniestro,realPath,usuario,lineaSeguroId);
								AcuseRecibo acuseReciboValidacion = (AcuseRecibo)parametros.get("acuseRecibo");
								parametros.put(IDPOL, idPoliza);
								parametros.put(OR_LLAM, origenLlamada);
								
								/* SONAR Q */
								return obtenerModAndView(parametros, siniestroBusqueda, idSiniestro, acuseReciboValidacion);
								/* FIN SONAR Q */
								
							}else{ // siniestro por FTP
								mensajeSin = bundle.getString("mensaje.alta.definitiva.OK");
							}
							// fin llamada al servicio web de validación y confirmación
							
							//Guardamos usuario y fecha de paso a def
							siniestrosManager.pasarDefinitiva(siniestro, usuario,estadoSin);
							if (!mensajeSin.equals(""))
								parametros.put(MSJ, mensajeSin);
							break;
						}
					}
					if (tieneParcelas){
						break;
					}
				}
				if (!tieneParcelas)
					parametros.put(ALERT, "No hay ninguna parcela dada de alta en Siniestro.");
			}else{
				parametros.put(ALERT, bundle.getString("mensaje.parcelaSiniestro.KO.Duplicados"));
			}
		}
		catch (BusinessException e){
			logger.error("Error al pasar a definitivo el siniestro", e);
			parametros.put(ALERT, "Error al pasar a definitivo el siniestro: " + e.getMessage());
		}
		catch (Exception e){
			logger.error("Error inesperado al pasar a definitivo el siniestro", e);
			parametros.put(ALERT, "Error inesperado al pasar a definitivo el siniestro: " + e.getMessage());
		}
		
		// Se indica a la jsp que la llamada se ha hecho desde el listado de utilidades
		if (!StringUtils.nullToString(request.getParameter(UTILIDADES)).equals("true")) {
			parametros.put("volver", true);
			return new ModelAndView(RED_UTL_SIN).addAllObjects(parametros);
		}
		
		return doConsulta(request, response, siniestroBusqueda).addAllObjects(parametros); 
	}
	
	
	/**
	 * Cuando llamamamos desde la página de errores de validación
	 * @param request
	 * @param response
	 * @param siniestro
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doConfirmarSiniestro(HttpServletRequest request,HttpServletResponse response, Siniestro siniestro) throws Exception{
		//Definición y asignación de variables
		Map<String, Object> parametros = new HashMap<String, Object>();
		Siniestro siniestroConf = new Siniestro();
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU); 
		Long idSiniestro=new Long(request.getParameter(ID_SIN));
		Long idPoliza= new Long(request.getParameter(IDPOL));
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Short estadoSin;
		String mensajeSin=new String();
		siniestroConf = siniestrosManager.buscarSiniestro(idSiniestro);
		siniestro.setPoliza(siniestroConf.getPoliza());// Para el return
		
		parametros = siniestrosManager.confirmarSiniestro(idPoliza.toString(),siniestroConf,realPath,usuario);
		AcuseRecibo acuseReciboConfirm = (AcuseRecibo)parametros.get("acuseRecibo");
		siniestrosManager.asignaDatosAsociados(acuseReciboConfirm, siniestroConf);
		
		String esRechazado = (String)parametros.get("esRechazado");
		if (esRechazado != null && esRechazado.equals("true")){	
			// errores en la confirmación del siniestro
			estadoSin = Constants.SINIESTRO_ESTADO_ENVIADO_ERROR;
			parametros.put(ALERT, bundle.getString("mensaje.siniestro.enviado.KO"));
		}else{			
			// Siniestro confirmado OK
			estadoSin = Constants.SINIESTRO_ESTADO_ENVIADO_CORRECTO;
			mensajeSin = bundle.getString("mensaje.siniestro.enviado.OK");					
		}
		
		siniestrosManager.pasarDefinitiva(siniestroConf, usuario,estadoSin);
		if (!mensajeSin.equals(""))
			parametros.put(MSJ, mensajeSin);
		
		
		return doConsulta(request, response, siniestro).addAllObjects(parametros);
	}
	

	public ModelAndView doEliminar(HttpServletRequest request,HttpServletResponse response, Siniestro siniestro) throws Exception {

		Long idSiniestro = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		String idPoliza = null;
		
		// Viene del listado de utilidades
		if (siniestro.getId() == null) {
			idSiniestro = new Long (request.getParameter(ID_SIN)).longValue();
			idPoliza = request.getParameter(IDPOL);
		}
		// Viene del ciclo de siniestros
		else {
			idSiniestro = siniestro.getId();
			idPoliza = siniestro.getPoliza().getIdpoliza().toString();
		}
		
		try {
			if (siniestrosManager.tieneEstado(idSiniestro,Constants.SINIESTRO_ESTADO_PROVISIONAL) ||
				siniestrosManager.tieneEstado(idSiniestro,Constants.SINIESTRO_ESTADO_ENVIADO_ERROR)||
				siniestrosManager.tieneEstado(idSiniestro,Constants.SINIESTRO_ESTADO_DEFINITIVO)) {
				// Realizamos la baja del siniestro
				siniestrosManager.eliminarSiniestro(idSiniestro);
				
				//Comprobamos si en la poliza hay mas siniestros, si no hay modificamos el campo tienesiniesrtros de polizas a N
				BigDecimal totalSiniestros = siniestrosManager.getNumTotalSiniestros(new Long(idPoliza));
				if (totalSiniestros != null && totalSiniestros.intValue() == 0) { // no tiene siniestros, actualizamos la poliza
					siniestrosManager.actualizaFlagTieneSiniestrosPoliza(new Long(idPoliza), Constants.CHARACTER_N);
				}
				// mensaje de baja correcta
				parametros.put(MSJ, bundle.getString("mensaje.baja.OK"));
			} else {
				// mensaje error
				parametros.put(ALERT, bundle.getString(MSJ_BAJ_KO));
			}

		} catch (BusinessException be) {

			logger.error("Se ha producido un error durante el borrado de un siniestro", be);
			parametros.put(ALERT, bundle.getString(MSJ_BAJ_KO));

		} catch (Exception be) {

			logger.error("Se ha producido un error indefinido durante el borrado de un siniestro", be);
			parametros.put(ALERT, bundle.getString(MSJ_BAJ_KO));

		}
		
		// Se indica a la jsp que la llamada se ha hecho desde el listado de utilidades
		if (!StringUtils.nullToString(request.getParameter(UTILIDADES)).endsWith("true")) {
			return new ModelAndView(RED_UTL_SIN).addAllObjects(parametros);
		}
		siniestro.setFlagBaja(true);
		return doConsulta(request, response, siniestro).addAllObjects(parametros); // enviar un siniestro nuevo con el idpoliza
	}
	
	/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio */
	/* Damos de alta una nueva función para realizar la baja lógica del siniestro */
	public ModelAndView doBajaSiniestro(HttpServletRequest request,HttpServletResponse response, Siniestro siniestro) throws Exception {

		Long idSiniestro = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		// Viene del listado de utilidades
		if (siniestro.getId() == null) {
			idSiniestro = new Long (request.getParameter(ID_SIN)).longValue();
		}
		// Viene del ciclo de siniestros
		else {
			idSiniestro = siniestro.getId();
		}
		
		try {
			// Realizamos la baja del siniestro
			siniestrosManager.bajaSiniestro(idSiniestro);
				
			// mensaje de baja correcta
			parametros.put(MSJ, bundle.getString("mensaje.baja.OK"));
			

		} catch (BusinessException be) {

			logger.error("Se ha producido un error durante el borrado de un siniestro", be);
			parametros.put(ALERT, bundle.getString(MSJ_BAJ_KO));

		} catch (Exception be) {

			logger.error("Se ha producido un error indefinido durante el borrado de un siniestro", be);
			parametros.put(ALERT, bundle.getString(MSJ_BAJ_KO));

		}
		
		// MPM - 29/06/2012
		// Se indica a la jsp que la llamada se ha hecho desde el listado de utilidades
		if (!StringUtils.nullToString(request.getParameter(UTILIDADES)).endsWith("true")) {
			return new ModelAndView(RED_UTL_SIN).addAllObjects(parametros);
		}
		siniestro.setFlagBaja(true);
		return doConsulta(request, response, siniestro).addAllObjects(parametros); // enviar un siniestro nuevo con el idpoliza
	}
	
	/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Fin */
	
	public ModelAndView doGuarda(HttpServletRequest request,HttpServletResponse response, Siniestro siniestro) throws Exception {
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		String altaWs = "";
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU);
		CapAsegSiniestradoDV capAseDV = new CapAsegSiniestradoDV();
		try {
			
			altaWs = StringUtils.nullToString(request.getParameter(ALT_WS));
			parametros.put(ALT_WS, altaWs);
			
			capAseDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela().getPoliza().setIdpoliza(siniestro.getPoliza().getIdpoliza());
			capAseDV.getCapAsegSiniestro().getParcelaSiniestro().getSiniestro().setId(siniestro.getId());
			/* compruebo si el siniestro tiene parcelas. 
			Si tiene -> estoy editando y me voy al doConsulta de parcelasSiniestradasController
			Si no tiene -> redirigmos a doCargaParcelas de parcelasSiniestradasController*/
			
			this.siniestrosManager.doGuardarSiniestro(siniestro, realPath, usuario, false);
			if (siniestrosManager.isSiniestroConParcelas(siniestro.getId())) {
				return parcelasSiniestradasController.doConsulta(request, response, capAseDV).addAllObjects(parametros);
			}
			
			parametros.put(IDPOL, siniestro.getPoliza().getIdpoliza());
			parametros.put(ID_SIN, siniestro.getId());
			
			// Se indica a la jsp si viene del listado de siniestros de utilidades
			boolean fromUtilidades	= (FiltroUtils.noEstaVacio(request.getParameter(UTILIDADES))) &&
			  						  "true".equals(request.getParameter(UTILIDADES)) ? true : false;
			parametros.put(UTILIDADES, fromUtilidades);
			
			return parcelasSiniestradasController.doCargaParcelas(request, response, capAseDV).addAllObjects(parametros);
		
		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante el alta de un siniestro", be);
			parametros.put(ALERT, bundle.getString(SIN_OK));
			mv = new ModelAndView( DIR_DATSIN,SIN, siniestro).addAllObjects(parametros);
		} catch (Exception be) {
			logger.error("Se ha producido un error indefinido durante el alta de un siniestro", be);
			parametros.put(ALERT, bundle.getString(SIN_OK));
			mv = new ModelAndView( DIR_DATSIN,SIN, siniestro).addAllObjects(parametros);
		}
		return mv; 
	}

	public ModelAndView doConsulta(HttpServletRequest request,HttpServletResponse response, Siniestro siniestro) throws Exception {

		String origenLlamada=null;
		if(null!=request.getParameter(OR_LLAM))origenLlamada=request.getParameter(OR_LLAM);
		
		String idPoliza = request.getParameter(IDPOL);
		if (idPoliza == null){
			idPoliza = (request.getParameter("idPol"));
		}
		List<Siniestro> listaSiniestros = null;
		Set<Siniestro> listaMapaSiniestros = null;
		List<Riesgo> listaRiesgos = null;
		List<EstadoSiniestro> listaEstados = null;
		Poliza poliza = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String mensajeAlerta = request.getParameter(MSJ);
		if (!StringUtils.nullToString(mensajeAlerta).equals("")) {
			parametros.put(MSJ, mensajeAlerta);
		}
		String alerta = request.getParameter(ALERT);
		if (!StringUtils.nullToString(alerta).equals("")) {
			parametros.put(ALERT, alerta);
		}

		if (idPoliza != null)
			siniestro.getPoliza().setIdpoliza(Long.parseLong(idPoliza));
		else
			idPoliza = siniestro.getPoliza().getIdpoliza().toString();

		try {
			poliza = siniestrosManager.getPoliza(new Long(idPoliza));
			listaRiesgos = siniestrosManager.getRiesgos(poliza);
			listaEstados = siniestrosManager.getEstadosSiniestro();
			siniestro.setPoliza(poliza);
			if(null!=origenLlamada && 
					(origenLlamada.compareTo(new String("siniestrosInformacion"))==0
						||origenLlamada.compareTo(new String("erroresValidacionSiniestros"))==0))//Vuelve de Información de siniestros (Hojas de campo y actas)
				asignaValoresBean(request, siniestro);

			// Array para las descripciones de los riesgos en el datagrid
			JSONArray arrayDatos = new JSONArray();
			JSONObject objeto = null;
			for (Riesgo lista : listaRiesgos) {
				objeto = new JSONObject();
				objeto.put(lista.getId().getCodriesgo(), lista.getDesriesgo());
				arrayDatos.put(objeto);
			}

			parametros.put("listaRiesgosCombo", listaRiesgos);
			parametros.put("listaRiesgos", arrayDatos);
			if("declaracionesSiniestros".equals(origenLlamada) && !siniestro.isFlagBaja()) {
				listaSiniestros = siniestrosManager.buscarSiniestros(siniestro);
				parametros.put("listaSiniestros", listaSiniestros);
			}
			else {
				listaMapaSiniestros = poliza.getSiniestros();
				parametros.put("listaSiniestros", listaMapaSiniestros);
			}

			parametros.put("listaEstados", listaEstados);

		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante la consulta de siniestros", be);
			parametros.put(ALERT, bundle.getString("mensaje.error.general"));
		} catch (Exception be) {
			logger.error("Se ha producido un error indefinido durante la consulta de siniestros", be);
			parametros.put(ALERT, bundle.getString("mensaje.error.general"));
		}

		return new ModelAndView("/moduloUtilidades/siniestros/declaracionesSiniestro",SIN, siniestro).addAllObjects(parametros);

	}
	
	private void asignaValoresBean(HttpServletRequest request, Siniestro siniestro) throws ParseException{
		DateFormat formatoDelTexto = new SimpleDateFormat(FORMAT_DATE);
		Date d_fechaOcurr = null;
		Date d_fechaEnvio=null;
		String riesgo=StringUtils.nullToString(request.getParameter("riesgoSiniestro"));
		String fechaOcurr=StringUtils.nullToString(request.getParameter("fechaocurrSiniestro"));
		String fechaEnvio=StringUtils.nullToString(request.getParameter("fechaenvioSiniestro"));
		String codEstado=StringUtils.nullToString(request.getParameter("codestadoSiniestro"));
		
		if(!riesgo.isEmpty())siniestro.setCodriesgo(new Short(riesgo));
		if(!fechaOcurr.isEmpty()){
			d_fechaOcurr=formatoDelTexto.parse(fechaOcurr);
			siniestro.setFechaocurrencia(d_fechaOcurr);
		}
		if(!fechaEnvio.isEmpty()){
			d_fechaEnvio=formatoDelTexto.parse(fechaEnvio);
			siniestro.getComunicaciones().setFechaEnvio(d_fechaEnvio);
		}
		if(!codEstado.isEmpty())siniestro.getEstadoSiniestro().setIdestado(new Short(codEstado));
	}

	public ModelAndView doImprimir(HttpServletRequest request,HttpServletResponse response, Siniestro siniestro) throws Exception {

		return new ModelAndView("redirect:/informes.html").addObject(ID_SIN, siniestro.getId()).addObject("method","doInformeSiniestro");

	}

	public ModelAndView doVerRecibo(HttpServletRequest request,HttpServletResponse response, Siniestro siniestro) throws Exception {
		logger.debug("init - doVerRecibo");
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Siniestro sini = null;
		ModelAndView mv = null;
		
		try {
			
			Long idSiniestro = siniestro.getId();
			if (idSiniestro == null){
				idSiniestro = Long.parseLong(StringUtils.nullToString(request.getParameter(ID_SIN)));
			}
			
			logger.debug("idsiniestro:  "+ idSiniestro + " idPoliza: " + siniestro.getPoliza().getIdpoliza());

			sini = siniestrosManager.buscarSiniestro(new Long(idSiniestro));
			
			if (sini != null) {
				
				/* SONAR Q */
				mv = obtenerErroresCont(sini, parametros, request, siniestro);
				/* FIN SONAR Q */
			}else {
				parametros.put(IDPOL, siniestro.getPoliza().getIdpoliza());
				
				// Se indica a la jsp que la llamada se ha hecho desde el listado de utilidades
				if (request.getParameter(UTILIDADES) != null) {
					return new ModelAndView(RED_UTL_SIN).addAllObjects(parametros);
				}
				
				mv = doConsulta(request, response, siniestro);
			} 
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al recuperar los documentos de Acuse de Recibo: " + be.getMessage());
			parametros.put(ALERT, bundle.getString("mensaje.acuseRecibo.KO"));
			parametros.put(IDPOL, siniestro.getPoliza().getIdpoliza());
			mv = doConsulta(request, response, siniestro);
		}
		logger.debug("end - doVerRecibo"); 
		return mv;
	}

	/**
	 * Metodo para parsear el Asegurado de la poliza a la persona de contacto
	 * del siniestro
	 */
	private Siniestro setPersonaContacto(Asegurado asegurado,
			Siniestro siniestroBean) {

		siniestroBean.setApellido1(asegurado.getApellido1());
		siniestroBean.setApellido2(asegurado.getApellido2());
		siniestroBean.setBloque(asegurado.getBloque());
		siniestroBean.setClavevia(asegurado.getVia().getClave());
		siniestroBean.setCodlocalidad(asegurado.getLocalidad().getId()
				.getCodlocalidad());
		siniestroBean.setCodpostal(asegurado.getCodpostal());
		siniestroBean.setCodprovincia(asegurado.getLocalidad().getProvincia()
				.getCodprovincia());
		siniestroBean.setDireccion(asegurado.getDireccion());
		siniestroBean.setEscalera(asegurado.getEscalera());
		siniestroBean.setNombre(asegurado.getNombre());
		siniestroBean.setNumvia(asegurado.getNumvia());
		siniestroBean.setPiso(asegurado.getPiso());
		siniestroBean.setRazonsocial(asegurado.getRazonsocial());
		siniestroBean.setSublocalidad(asegurado.getLocalidad().getId()
				.getSublocalidad());
		siniestroBean.setTelefono1(asegurado.getTelefono());
		siniestroBean.setTelefono2(asegurado.getMovil());

		return siniestroBean;
	}
	
	public void setSiniestrosManager(SiniestrosManager siniestrosManager) {
		this.siniestrosManager = siniestrosManager;
	}

	public void setParcelasSiniestradasController(
			ParcelasSiniestradasController parcelasSiniestradasController) {
		this.parcelasSiniestradasController = parcelasSiniestradasController;
	}
	
	/* SONAR Q */
	/* MODIF TAM (23.11.2021) ** SONAR Q ** Inicio */
	/* Creamos nuevas funciones, para descargar las funciones principales de ifs/fors */
	private static String obtenerIdPoliza(HttpServletRequest request,Siniestro siniestro) {
	
	   String idPol = null;
 	   if (siniestro.getId() == null) {
			if (request.getParameter(IDPOL) == null) {
				idPol = siniestro.getPoliza().getIdpoliza().toString();
			} else {
				idPol = request.getParameter(IDPOL);
			}
		} else {
			idPol = siniestro.getPoliza().getIdpoliza().toString();
		}
		
		return idPol; 
	}
	
	private static String obtenerOrigenLlamada(HttpServletRequest request) {
		String origenLlamada=null;
		
		if(null!=request.getParameter(OR_LLAM)){
			origenLlamada=request.getParameter(OR_LLAM);			
		}
		return origenLlamada;
	}
	
	private String obteneridPol(HttpServletRequest request, Siniestro siniestro) {
	
		String idPoliza = "";
		
		try {
			idPoliza = siniestro.getPoliza().getIdpoliza().toString();
		}
		catch (Exception e) {
			logger.debug("SiniestrosController.doPasarDefinitiva - La llamada se ha hecho desde el listado de utilidades de siniestros.");
		}
	
		//Obtiene el id de poliza de la request
		if (idPoliza == null || idPoliza.equals("")) {
			idPoliza = request.getParameter("idPol");
		}
	
		return idPoliza;
	}
	
	private Long obtenerIdSin(HttpServletRequest request, Siniestro siniestro) {
	
		Long idSiniestro = siniestro.getId();
	
		if (idSiniestro == null){
			idSiniestro = Long.parseLong(StringUtils.nullToString(request.getParameter("idSin")));
		}
		return idSiniestro;
		
	}
	
	private static String obtenerIdSin2(HttpServletRequest request, Siniestro siniestro) {
		String idSin = null;
		
		if (siniestro.getId() == null) {
			idSin = request.getParameter(ID_SIN);
		} else {
			idSin = siniestro.getId().toString();
		}
			
		return idSin; 
	}
	
	
	private static ModelAndView obtenerModAndView(Map<String, Object> parametros, Siniestro siniestroBusq, Long idSin, AcuseRecibo acuseReciboVal) {
			
		String tieneErroresVal = (String)parametros.get("tieneErrores");
		if (tieneErroresVal != null && tieneErroresVal.equals("true")){		
			parametros.put(ID_SIN, idSin);
			parametros.put("validacionOk", false);
			return new ModelAndView("moduloPolizas/webservices/erroresValidacionSiniestro", "resultado", acuseReciboVal).addAllObjects(parametros);								
		}else{
			parametros.put(MSJ, "Proceso de validación correcto. ");
			parametros.put("validacionOk", true);
			parametros.put(ID_SIN, idSin);
			/*parámetros necesarios para el botón de volver de la página de validación (Errores)*/
			  DateFormat df = new SimpleDateFormat(FORMAT_DATE);
	
			if(null!=siniestroBusq.getCodriesgo()) {
				parametros.put("riesgoSiniestro", siniestroBusq.getCodriesgo());
			}
			if(null!=siniestroBusq.getEstadoSiniestro() && 
					null!= siniestroBusq.getEstadoSiniestro().getIdestado()) {
				parametros.put("codestadoSiniestro", siniestroBusq.getEstadoSiniestro().getIdestado());
			}
				
			if (null!= siniestroBusq.getFechaocurrencia()) {
				parametros.put("fechaocurrSiniestro", df.format(siniestroBusq.getFechaocurrencia()));
			}
			if(null!=siniestroBusq.getComunicaciones() && 
					null!=siniestroBusq.getComunicaciones().getFechaEnvio()) {
				parametros.put("fechaenvioSiniestro", df.format(siniestroBusq.getComunicaciones().getFechaEnvio()));
			}
			
			return new ModelAndView("moduloPolizas/webservices/erroresValidacionSiniestro", "resultado", acuseReciboVal).addAllObjects(parametros);
		}
	}
	
	/* SONAR Q */
	private  ModelAndView obtenerErroresCont(Siniestro sini, Map<String, Object> parametros, HttpServletRequest request, Siniestro siniestro) throws BusinessException {
	
		Long idPoliza = null;
		String refPoliza = null;
		BigDecimal linea = null;
		BigDecimal plan = null;
		BigDecimal idEnvio = null;
		ModelAndView mv = null;
		
		es.agroseguro.acuseRecibo.Error[] errores = null;
	
		if (sini.getComunicaciones()!= null && sini.getComunicaciones().getIdenvio() != null){
			idEnvio = sini.getComunicaciones().getIdenvio();
		}
	
		idPoliza = sini.getPoliza().getIdpoliza();
		refPoliza = sini.getPoliza().getReferencia();
		logger.debug("idPoliza:  "+ idPoliza + " refPoliza: " + refPoliza + " idEnvio: "+ idEnvio );
	
		if (idEnvio == null || refPoliza == null) {
			
			parametros.put(IDPOL, idPoliza);
			parametros.put(ERR_LENGTH, 0);
			parametros.put(OPER, SINIESTRO);
			parametros.put(UTILIDADES, request.getParameter(UTILIDADES));
			
			mv = new ModelAndView("/moduloUtilidades/erroresContratacion",SIN, siniestro).addAllObjects(parametros);
			
		} else {
			
			linea = sini.getPoliza().getLinea().getCodlinea();
			plan = sini.getPoliza().getLinea().getCodplan();
			logger.debug("plan:  "+ plan + " linea: " + linea );
			// Se obtiene un array con los errores
			errores = siniestrosManager.getFicheroContenido(idEnvio, refPoliza, linea, plan);
			logger.debug("listado de errores - Size :  "+ errores.length );
			
			if (errores.length == 0) {
				parametros.put(ERR_LENGTH, 0);
				parametros.put(IDPOL, idPoliza);
				parametros.put(OPER, SINIESTRO);
				parametros.put(UTILIDADES, request.getParameter(UTILIDADES));
			} else {
				parametros.put(IDPOL, idPoliza);
				parametros.put("errores", errores);
				parametros.put(ERR_LENGTH, errores.length);
				parametros.put(OPER, SINIESTRO);
				parametros.put(UTILIDADES, request.getParameter(UTILIDADES));
			}
			
			mv = new ModelAndView("/moduloUtilidades/erroresContratacion",SIN, siniestro).addAllObjects(parametros).addObject("errores", errores);	
	
		}
		return mv;
	}
}
