package com.rsi.agp.core.webapp.action.ganado;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.PrecioGanadoException;
import com.rsi.agp.core.managers.IDatosExplotacionesManager;
import com.rsi.agp.core.managers.impl.AnexoModificacionManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.managers.impl.ganado.DatosExplotacionesAnexoManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesAnexoManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.DatosExplotacionesUtil;
import com.rsi.agp.core.util.ExplotacionCoberturaAnexoComparator;
import com.rsi.agp.core.util.ParamUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaVincAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModuloAnexo;


public class DatosExplotacionesAnexoController extends BaseMultiActionController {
	
	private static final String FALSE = "false";
	private static final String MENSAJE_DATOS_EXPLOTACION_COMBINACION_KO = "mensaje.datosExplotacion.combinacionKO";
	private static final String COBERTURAS2 = "coberturas";
	private static final String COB_PROCESADAS = "cobProcesadas";
	private static final String ID_GUARDAR_REPLICAR = "idGuardarReplicar";
	private static final String BOTON_GUARDAR_GR = "botonGuardarGR";
	private static final String TIENE_COBERTURAS = "tieneCoberturas";
	private static final String LST_EXP_COBERTURAS = "lstExpCoberturas";
	private static final String GRUPORAZAID = "gruporazaid";
	private static final String PRIMER_ACCESO = "primerAcceso";
	private static final String ANEXO_MODIFICACION = "anexoModificacion";
	private static final String EXPLOTACION_ANEXO_ID = "explotacionAnexoId";
	private static final String MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO = "moduloExplotaciones/explotaciones/datosExplotacionAnexo";
	private static final String IS_COBERTURAS = "isCoberturas";
	
	private static final Log logger = LogFactory.getLog(DatosExplotacionesAnexoController.class);
	private ExplotacionesAnexoManager explotacionesAnexoManager;
	private IDatosExplotacionesManager datosExplotacionesManager;
	private DatosExplotacionesAnexoManager datosExplotacionesAnexoManager;
	private AnexoModificacionManager anexoModificacionManager;
	private ClaseManager claseManager;

	private final String POLIZA_BEAN = "polizaBean";
	private final String USUARIO = "usuario";
	private final String PREFIJO_DV = "dvCpto_";
	private final String OPERACION_GRUPO_RAZA_BORRAR = "borrar";
	private final String OPERACION_GRUPO_RAZA_EDITAR = "editar";
	private final String OPERACION_IDENTIFICATIVOS_GUARDAR = "guardarDatosIdentificativos";
	private final String BORRAR_FORM_GRUPO_RAZA = "borradoFormularioGrupoRaza";
	private final String BEAN_EXPLOTACION_ANEXO = "explotacionAnexoBean";
	private final String KEY_ANEXO_ID = "anexoModificacionId";
	private final static String VACIO = "";
	
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	

	
	/**
	 * Redirige a la pantalla de alta/modificación de explotaciones de anexo
	 */
	public ModelAndView doAltaExplotacionAnexo(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacionBean) {
		
		logger.debug("doPantallaAltaExplotacionAnexo - Redirección a pantalla de alta/modificación de explotaciones de anexo");
		String vieneDeListadoAnexosMod = request.getParameter(Constants.KEY_ORIGEN_LISTADO_ANX_MOD);
		
		AnexoModificacion anexoModificacion = null;
		Long idAnexo = Long.parseLong(request.getParameter(KEY_ANEXO_ID));

		try {
			anexoModificacion = anexoModificacionManager.obtenerAnexoModificacionById(idAnexo);
		} catch (DAOException e) {
			logger.error("Error al obtener el AnexoModificación", e);
		}
		
		
		
		
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		parametros.put("codPlan", anexoModificacionBean.getPoliza().getLinea().getCodplan());
		parametros.put("codLinea", anexoModificacionBean.getPoliza().getLinea().getCodlinea());
		parametros.put("fechaInicioContratacion", request.getParameter("fechaInicioContratacion"));

		
		parametros.put(DatosExplotacionesAnexoController.ANEXO_MODIFICACION, anexoModificacionBean);

		
		
		parametros.put(BEAN_EXPLOTACION_ANEXO, new ExplotacionAnexo(anexoModificacion));

		// Carga los datos variables configurados para el plan y línea de la póliza
		parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(anexoModificacionBean.getPoliza().getLinea().getLineaseguroid()));
		
		parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
		
		//Restricciones lupas
		
		Long idClase = obtenerIdClasePorAnexo(anexoModificacion);
		datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		
		// Comprobar si puede tener coberturas segun línea y modulos del anexo
		Poliza plz = datosExplotacionesManager.getPoliza(anexoModificacion.getPoliza().getIdpoliza());
		if(null!=plz && plz.getLinea() != null && plz.getModuloPolizas() != null) {
			Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();
			boolean isCoberturas = datosExplotacionesManager.isCoberturasElegiblesNivelExplotacion(plz.getLinea().getLineaseguroid(), modsPoliza);
			parametros.put(DatosExplotacionesAnexoController.IS_COBERTURAS,isCoberturas);
		}
		
		//Cargamos los tipos de capital con grupo de negocio que no dependen del número de animales
		addGruposNegocioNoDepNumAnimales(parametros,plz.getLinea());
		
		return new ModelAndView(DatosExplotacionesAnexoController.MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO).addAllObjects(parametros);
	}
	
	/**
	 * Redirige a la pantalla de alta/modificación de explotaciones con los datos de la explotación indicada cargados
	 * @param request
	 * @param response
	 * @param polizaRenovableBean
	 * @return
	 */
	public ModelAndView doEditarExplotacionAnexo(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) {
		
		logger.debug("******* doEditar - Redirección a pantalla de alta/modificación de explotaciones");
		String vieneDeListadoAnexosMod = request.getParameter(Constants.KEY_ORIGEN_LISTADO_ANX_MOD);
		//Obtiene el id de la explotación de la request
		ExplotacionAnexo explotacionAnexo = null;
		Integer botonGuardarGR=null;
		Long idGuardarReplicar=null;
		
		try {
			explotacionAnexo = explotacionesAnexoManager.getExplotacionAnexo(Long.parseLong(request.getParameter(DatosExplotacionesAnexoController.EXPLOTACION_ANEXO_ID)));
		} catch (Exception e) {
			logger.error("Error al obtener la explotación a editar", e);
		}
	
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		
		
		ExplotacionAnexo expAnxAux = null;
		//Si no tiene grupos raza, creamos uno ficticio pero con un bean nuevo
		if(explotacionAnexo.getGrupoRazaAnexos().size() == 0){
			expAnxAux = copiarExplotacionAnexoEnBean(explotacionAnexo);
			GrupoRazaAnexo grvac = new GrupoRazaAnexo(expAnxAux, null, null, null, null, null, null);
			PrecioAnimalesModuloAnexo pam = new PrecioAnimalesModuloAnexo(null, grvac, null);
			grvac.getPrecioAnimalesModuloAnexos().add(pam);
			expAnxAux.getGrupoRazaAnexos().clear();
			expAnxAux.getGrupoRazaAnexos().add(grvac);
			parametros.put(BEAN_EXPLOTACION_ANEXO, expAnxAux);	
		}else{
			parametros.put(BEAN_EXPLOTACION_ANEXO, explotacionAnexo);
		}
		
		parametros.put("codPlan", explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getCodplan());
		parametros.put("codLinea", explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getCodlinea());
		
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaLimite;
		try {
			fechaLimite = formatter.parse("01/03/2023");

			Date fechaInicioContratacion = explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getFechaInicioContratacion();
			parametros.put("fechaInicioContratacion", fechaInicioContratacion);
			
			if(fechaInicioContratacion.compareTo(fechaLimite) < 0) {
			    parametros.put("isFechaMenor", true);
			} else {
			    parametros.put("isFechaMenor", false);
			}
		} catch (ParseException e) {
		    logger.error("Hubo un problema al parsear la fecha de inicio de contratacion de la linea", e);
		}
		
		parametros.put("fechaInicioContratacion", request.getParameter("fechaInicioContratacion"));
		
		parametros.put(DatosExplotacionesAnexoController.ANEXO_MODIFICACION, explotacionAnexo.getAnexoModificacion());
		
		String operacion = request.getParameter("operacion");
		if("visualizarDatosRegistro".equals(operacion)){
			parametros.put("modoLectura", "modoLectura");
		}
		
		// Carga los datos variables configurados para el plan y línea de la póliza
		parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid()));
	
		parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
		
		//Restricciones lupas
		Long idClase = obtenerIdClasePorExplotacionAnexo(explotacionAnexo);
		datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		
		//Por si viene de guardar grupo raza, recuperamos los parámetros
		String arrayParametros[] = {Constants.KEY_ALERTA, Constants.KEY_MENSAJE, BORRAR_FORM_GRUPO_RAZA};
		ParamUtils.recuperarParametros(request, parametros, arrayParametros);
		
		//Si sólo tiene un grupo raza, forzar el modo edición de grupo raza
		if(explotacionAnexo.getGrupoRazaAnexos().size()!=1){
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
			
			if("SI".equals(request.getParameter(DatosExplotacionesAnexoController.PRIMER_ACCESO))){
				parametros.put(DatosExplotacionesAnexoController.PRIMER_ACCESO, "SI");
			}
		}else{
			request.setAttribute(DatosExplotacionesAnexoController.GRUPORAZAID, explotacionAnexo.getGrupoRazaAnexos().iterator().next().getId());
		}
		
		// Comprobar si puede tener coberturas segun línea y modulos del anexo
		Poliza plz = datosExplotacionesManager.getPoliza(anexoModificacion.getPoliza().getIdpoliza());
		if(null==plz) {
			if (explotacionAnexo.getAnexoModificacion() != null && explotacionAnexo.getAnexoModificacion().getPoliza() != null) {
				plz = datosExplotacionesManager.getPoliza(explotacionAnexo.getAnexoModificacion().getPoliza().getIdpoliza());
			}
		}
		if(null!=plz && plz.getLinea() != null && plz.getModuloPolizas() != null) {
			Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();
			boolean isCoberturas = datosExplotacionesManager.isCoberturasElegiblesNivelExplotacion(plz.getLinea().getLineaseguroid(), modsPoliza);
			parametros.put(DatosExplotacionesAnexoController.IS_COBERTURAS,isCoberturas);
		}
		
		// Creamos las vinculaciones de las coberturas para la jsp
		
		logger.debug ("******* EDITANDO UNA EXPLOTACION, del Anexo: "+anexoModificacion.getId());
		logger.debug ("******* Valor de id de la Explotacion:"+explotacionAnexo.getId());
		logger.debug ("******* Valor de Grupos de Raza de la explotacion:"+explotacionAnexo.getGrupoRazaAnexos().size());
		
		
		// Creamos las vinculaciones de las coberturas para la jsp
		List<ExplotacionCoberturaAnexo> lstExpCoberturas = new ArrayList<ExplotacionCoberturaAnexo>();
		logger.debug("*******Tamaño de lista de ExplotacionesCoberturasAnexo:"+explotacionAnexo.getExplotacionCoberturasAnexo().size());
		
		/* ESC-17260 ** MODIF TAM (23.02.2022) ** Inicio */
		/* Obtenemos las Coberturas de la explotación del Anexo */
		if (explotacionAnexo.getExplotacionCoberturasAnexo() != null && explotacionAnexo.getExplotacionCoberturasAnexo().size()>0){
			
			logger.debug("******* Entramos en el if ");
			crearVinculacionesCoberturas(explotacionAnexo.getExplotacionCoberturasAnexo());
			// convertimos el set a List
			lstExpCoberturas.addAll(explotacionAnexo.getExplotacionCoberturasAnexo());
			Collections.sort(lstExpCoberturas, new ExplotacionCoberturaAnexoComparator());
			logger.debug ("Valor de lista de Coberturas de la Explotación: "+lstExpCoberturas.toString());
			parametros.put(LST_EXP_COBERTURAS, lstExpCoberturas);
			if(explotacionAnexo.getGrupoRazaAnexos().size() == 0){
				Set<ExplotacionCoberturaAnexo> setExp = explotacionAnexo.getExplotacionCoberturasAnexo();
				expAnxAux.setExplotacionCoberturasAnexo(setExp);
			}		
			parametros.put(TIENE_COBERTURAS, true);
			logger.debug("******* seteamos el TieneCoberturas a true: ");
		}else {
			parametros.put(TIENE_COBERTURAS, false);
			logger.debug("******* seteamos el TieneCoberturas a false: ");
		}
		
		if(null!=request.getParameter(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR) && !request.getParameter(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR).isEmpty()){
			botonGuardarGR=new Integer(request.getParameter(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR));
		}
		parametros.put(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR, botonGuardarGR);
		
		if(null!=request.getParameter(DatosExplotacionesAnexoController.ID_GUARDAR_REPLICAR) && !request.getParameter(DatosExplotacionesAnexoController.ID_GUARDAR_REPLICAR).isEmpty()){
			idGuardarReplicar=new Long(request.getParameter(DatosExplotacionesAnexoController.ID_GUARDAR_REPLICAR));
		}
		parametros.put(DatosExplotacionesAnexoController.ID_GUARDAR_REPLICAR, idGuardarReplicar);
		
		
		if(explotacionAnexo.getAnexoModificacion().getPoliza().getLinea()!=null && explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid()!=null){
			Linea linea=datosExplotacionesManager.getLinea(explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid());
			if (linea!=null){
				addGruposNegocioNoDepNumAnimales(parametros,linea);
			}
		}
		
		parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
		
		return new ModelAndView(DatosExplotacionesAnexoController.MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO).addAllObjects(parametros);
	}
	
	
	public void crearVinculacionesCoberturas(Set<ExplotacionCoberturaAnexo> setExpCob) {
		for (ExplotacionCoberturaAnexo cob:setExpCob) {
			String vinculada ="";
			Set<ExplotacionCoberturaVincAnexo> setCobVinc = cob.getExplotacionCoberturaVincAnexos();
			for (ExplotacionCoberturaVincAnexo vinc :setCobVinc) {
				vinculada += vinc.getVinculacion()+"."+vinc.getFila()+"."+vinc.getVinculacionElegida()+"."+cob.getCodmodulo()+"."+vinc.getDvColumna()+"."+vinc.getDvValor()+"-";
				logger.debug("Cob id:"+cob.getId()+ " vinculadas: "+vinculada);
			}
			if (vinculada.length()>0)
				vinculada=vinculada.substring(0, vinculada.length()-1);
			cob.setVinculada(vinculada);
		}
	}
	
	/**
	 * Guarda la explotación de anexo del formulario, crea otra idéntica y pasa a modo edición
	 * @param request
	 * @param response
	 * @param explotacionAnexoBean
	 * @return
	 */
	public ModelAndView doGuardarReplicar(HttpServletRequest request, HttpServletResponse response, ExplotacionAnexo explotacionAnexoBean) {
		
		logger.debug("doGuardarReplicar - Alta y réplica de explotaciones");
		ModelAndView mv = null;

		Map<String, Object> parametros = this.guardarExplotacion(request, response, explotacionAnexoBean);
		boolean isMV = parametros.containsKey("mv");
		if (isMV && parametros.containsKey(Constants.KEY_ALERTA)){
			mv = (ModelAndView) parametros.get("mv");
		}else{
			boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
			boolean isFalloReplicar = false;
			// Si el alta ha sido correcta
			if (isGuardadoOk) {
				try {
					ExplotacionAnexo expAnexo= null;
					if(explotacionAnexoBean.getId()!=null)
						expAnexo = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(explotacionAnexoBean.getId());
					
					
					ExplotacionAnexo explotacionAnexoNuevo = explotacionesAnexoManager.duplicarExplotacionAnexo(expAnexo);
					parametros.put(DatosExplotacionesAnexoController.EXPLOTACION_ANEXO_ID, explotacionAnexoNuevo.getId());
					
					String arrayParametros2[] = {Constants.KEY_MENSAJE};
					ParamUtils.recuperarRequest(request, parametros, arrayParametros2);
					
					//Para que la edición sea como si hubiese accedido desde el listado de explotaciones de anexo
					parametros.put(DatosExplotacionesAnexoController.PRIMER_ACCESO, "SI");
					
					mv = new ModelAndView("redirect:/datosExplotacionesAnexo.html").addAllObjects(parametros);
				} catch (DAOException e) {
					isFalloReplicar = true;
					parametros.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.replicar.KO"));
				}
			}
			if(!isGuardadoOk || isFalloReplicar) {
				mv=devuelveErrorAVista(parametros, explotacionAnexoBean);
			}
		}
		return mv;
	}
	

	public ModelAndView doGuardarNuevo(HttpServletRequest request, HttpServletResponse response, ExplotacionAnexo explotacionAnexoBean) {
		
		logger.debug("doGuardarNuevo - Alta y redirección a la pantalla de datos de las explotaciones");
		String vieneDeListadoAnexosMod = request.getParameter(Constants.KEY_ORIGEN_LISTADO_ANX_MOD);
		
		ModelAndView mv = null;
		ExplotacionAnexo expAnexo = null;
		// Realiza el alta o modificación de la explotación
		mv = doGuardarGrupoRazaAnexo(request, response, explotacionAnexoBean);
		
		
		
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		

		
		
		String arrayParametros[] = {Constants.KEY_ALERTA, Constants.KEY_MENSAJE, BORRAR_FORM_GRUPO_RAZA};
		
		ParamUtils.recuperarRequest(request, parametros, arrayParametros);
		
		parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
		
		boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
		if (!isGuardadoOk){
			return mv;
		}
		
		
		if(explotacionAnexoBean.getId()!=null)
			expAnexo = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(explotacionAnexoBean.getId());
		
		// procesamiento coberturas
		String cobProcesadas = StringUtils.nullToString(request.getParameter(DatosExplotacionesAnexoController.COBERTURAS2));
		if (cobProcesadas == "")
			cobProcesadas = StringUtils.nullToString(request.getAttribute(DatosExplotacionesAnexoController.COB_PROCESADAS));
		Set<ExplotacionCoberturaAnexo> setExpCoberturas = new HashSet<ExplotacionCoberturaAnexo>();
		if (!cobProcesadas.equals(DatosExplotacionesAnexoController.COB_PROCESADAS)){
			String coberturas = request.getParameter(DatosExplotacionesAnexoController.COBERTURAS2);
			setExpCoberturas = datosExplotacionesAnexoManager.procesarCoberturasAnexo(expAnexo, coberturas);  //datosExplotacionesAnexoManager.procesarCoberturasAnexo(expAnexo,coberturas);
			if (setExpCoberturas != null && setExpCoberturas.size()>0) {
				explotacionAnexoBean.setExplotacionCoberturasAnexo(setExpCoberturas);
			}
		}
		parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, false);
		
		
		
		ParamUtils.recuperarRequest(request, parametros, arrayParametros);
		
		parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
		
		//boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
		
		//Cargamos los tipos de capital con grupo de negocio que no dependen del número de animales
		addGruposNegocioNoDepNumAnimales(parametros,expAnexo.getAnexoModificacion().getPoliza().getLinea());
		
		// Si el alta ha sido correcta
		if (isGuardadoOk) {
			Long anexoModificacionId = explotacionAnexoBean.getAnexoModificacion().getId();

			AnexoModificacion anexoModificacion = null;
			try {
				anexoModificacion = anexoModificacionManager.obtenerAnexoModificacionById(anexoModificacionId);
			} catch (DAOException e) {
				logger.error("Error al cargar el anexo de modificación de id = " + anexoModificacionId);
			}
			
			String arrayParametros2[] = {Constants.KEY_MENSAJE};
			ParamUtils.recuperarRequest(request, parametros, arrayParametros2);
			
			// Parámetros para la pantalla de datos de la explotación
			parametros.put(BEAN_EXPLOTACION_ANEXO, new ExplotacionAnexo(anexoModificacion));
			
			
			parametros.put("codPlan", anexoModificacion.getPoliza().getLinea().getCodplan());
			parametros.put("codLinea", anexoModificacion.getPoliza().getLinea().getCodlinea());
			
			
			// Redirige a la pantalla de listado de explotaciones
			parametros.put(KEY_ANEXO_ID, anexoModificacionId);
			parametros.put("origenLlamada", "datosExplotacionAnexo");
			// Carga los datos variables configurados para el plan y línea de la póliza
			parametros.put(DatosExplotacionesAnexoController.PRIMER_ACCESO, "SI");
			parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(anexoModificacion.getPoliza().getLinea().getLineaseguroid()));
			mv = new ModelAndView(DatosExplotacionesAnexoController.MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO).addAllObjects(parametros);
		}
		else {
			//Recargar para que aparezcan todos los grupos raza
			if(explotacionAnexoBean.getId()!=null){
				explotacionAnexoBean = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(explotacionAnexoBean.getId());
			}
			
			parametros.put(BEAN_EXPLOTACION_ANEXO, explotacionAnexoBean);
			parametros.put(DatosExplotacionesAnexoController.ANEXO_MODIFICACION, explotacionAnexoBean.getAnexoModificacion());
			
			// Carga los datos variables configurados para el plan y línea de la póliza
			parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionAnexoBean.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid()));
			
			//Restricciones lupas
			Long idClase = obtenerIdClasePorExplotacionAnexo(explotacionAnexoBean);
			datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
			
			// Redirige a la pantalla de datos de la explotación
			mv = new ModelAndView(DatosExplotacionesAnexoController.MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO).addAllObjects(parametros);
		}

		return mv;
	}



	public ModelAndView doGuardarVolver(HttpServletRequest request, HttpServletResponse response, ExplotacionAnexo explotacionAnexoBean) {
		
		logger.debug("doGuardarVolver - Alta de explotación y volver al listado");
		ModelAndView mv = null;

		Map<String, Object> parametros = this.guardarExplotacion(request, response, explotacionAnexoBean);
		boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
		boolean isMV = parametros.containsKey("mv");
		if (isMV && !isGuardadoOk){
			mv = (ModelAndView) parametros.get("mv");
		}else{
			//boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
			
			// Si el alta ha sido correcta
			if (isGuardadoOk) {
				// Parámetros para la pantalla de datos de la explotación
				parametros.put("idpoliza", explotacionAnexoBean.getAnexoModificacion().getPoliza().getIdpoliza());
				parametros.put("linea.lineaseguroid", explotacionAnexoBean.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid());
				// Redirige a la pantalla de listado de explotaciones
				parametros.put(KEY_ANEXO_ID, explotacionAnexoBean.getAnexoModificacion().getId());
				parametros.put("origenLlamada", "datosExplotacionAnexo");
				parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
	
				mv = new ModelAndView("redirect:/listadoExplotacionesAnexo.html").addAllObjects(parametros);
			}else {
				mv=devuelveErrorAVista(parametros, explotacionAnexoBean);
			}
	}
		return mv;
	}


	public void doCalcularPrecio(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("**@@** DatosExplotacionesAnexoController - doCalcularPrecio [INIT]");
		logger.debug("**@@** Valor de cobExistentes(request):"+StringUtils.nullToString(request.getParameter("cobExistentes")));
		logger.debug("**@@** Valor de explotacion.id():"+request.getParameter("idExplotacion"));

		// Crea el objeto ExplotacionAnexo a partir de los datos recibidos en request
		ExplotacionAnexo explotacion = getExplotacionFromRequest(request);
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		List<ExplotacionCoberturaAnexo> lstCob = new ArrayList<ExplotacionCoberturaAnexo>();
		try {
			if (explotacion != null) {
				logger.debug("**@@** Entramos en el if de explotaciones");
				logger.debug("**@@** Antes de calcularPrecio");
				logger.debug("**@@** Valor de explotacion.id():"+explotacion.getId());
				logger.debug("**@@** Valor de explotacion.getGrupoRazaAnexos().size():"+explotacion.getGrupoRazaAnexos().size());
				
				// Calcula el precio de la explotación y lo vuelca en la respuesta para ser tratado en la pantalla
				JSONArray jsonPrecios = new JSONArray(datosExplotacionesAnexoManager.calcularPrecio(explotacion));
				
				// COB EXPLOTACION	
				Boolean isCoberturas = (new Boolean(request.getParameter(DatosExplotacionesAnexoController.IS_COBERTURAS)));
				if(isCoberturas) {			
					String realPath = this.getServletContext().getRealPath("/WEB-INF/");
					logger.debug("**@@** ----------------------------------------------------------");
					String cobExistentes = (StringUtils.nullToString(request.getParameter("cobExistentes")));
					logger.debug("**@@** Valor de cobExistentes: "+ cobExistentes);
					logger.debug("**@@** ----------------------------------------------------------");
					String idExplotacion = (StringUtils.nullToString(request.getParameter("idExplotacion")));
					String idPoliza 	 = (StringUtils.nullToString(request.getParameter("idPoliza")));
					String idAnexo 		 = (StringUtils.nullToString(request.getParameter("anexoModificacionId")));
					
					logger.info("idPoliza: "+ idPoliza + " idAnexo: "+idAnexo + " idExplotacion: "+idExplotacion +" cobExistentes: "+cobExistentes);
					if(!idExplotacion.equals("")){
						explotacion.setId(Long.parseLong(idExplotacion));
					}
					if(!idPoliza.equals("") && !idAnexo.equals("")) {
						AnexoModificacion anexoModificacion = null;
						try {
							anexoModificacion = anexoModificacionManager.obtenerAnexoModificacionById(Long.parseLong(idAnexo));
						} catch (DAOException ex) {
							logger.error("Error al cargar el anexo de modificación de id = " + idAnexo,ex);
						}
						lstCob = datosExplotacionesAnexoManager.getCoberturasElegiblesExpAnexo(explotacion,realPath,usuario.getCodusuario(),cobExistentes,Long.parseLong(idPoliza),anexoModificacion);
					}
				}

				String res2 = "";
				if (lstCob != null && lstCob.size()>0) {
					JSONArray jsonCoberturas = new JSONArray(lstCob);
					res2 = jsonCoberturas.toString();
				}
				
				// unimos las dos listas
				String res  = jsonPrecios.toString();
				String resTotal = "";
				if (!res2.equals("")){
					res=res.substring(0, res.length()-1);
					res2=res2.substring(1, res2.length());
					resTotal = res +","+res2;
				}else {
					resTotal = res;
				}
				this.getWriterJSON(response, resTotal);
			}
		} catch (PrecioGanadoException e) {
			logger.info("No todos los precios encontrados son iguales");
			JSONObject json = DatosExplotacionesUtil.respuestaErrorPrecio(bundle.getString("mensaje.datosExplotacion.precioGanado.KO"));
			this.getWriterJSON(response, json);
		} catch (Exception e){
			logger.error("Ha habido un error al calcular el precio", e);
		}
	}
	
	protected void getWriterJSON(HttpServletResponse response, String resultado){		
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(resultado);
		} catch (IOException e) {			
			logger.warn("Fallo al escribir la lista en el contexto", e);
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param explotacionBean
	 * @return
	 */
	public ModelAndView doGuardarGrupoRazaAnexo(HttpServletRequest request, HttpServletResponse response, 
			ExplotacionAnexo explotacionBean) {
		
		ModelAndView mv = null;
		
		logger.debug("doGuardarGrupoRazaAnexo");
		Long codgruporaza = null;
		BigDecimal codtipocapital = null;
		Long codtipoanimal = null;
		Long numanimales = null;
		//String codmodulo = null;
		BigDecimal precioAnimalesModulos = null;
		Integer botonGuardarGR=null;
		
		String accion = request.getParameter("accion");
		
		Boolean isCoberturas = (new Boolean(request.getParameter(DatosExplotacionesAnexoController.IS_COBERTURAS)));
		
		if(null!=request.getParameter(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR) && !request.getParameter(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR).isEmpty()){
			botonGuardarGR=new Integer(request.getParameter(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR));
		}
		
		//Si es borrar, no se cargan porque no hace falta
		if (!OPERACION_GRUPO_RAZA_BORRAR.equals(accion) && !OPERACION_IDENTIFICATIVOS_GUARDAR.equals(accion)){
			codgruporaza = new Long(request.getParameter("grupoRazaAnexos[0].codgruporaza"));
			codtipocapital = new BigDecimal(request.getParameter("grupoRazaAnexos[0].codtipocapital"));
			codtipoanimal = new Long(request.getParameter("grupoRazaAnexos[0].codtipoanimal"));
			numanimales = new Long(request.getParameter("grupoRazaAnexos[0].numanimales"));
			//codmodulo = request.getParameter("grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].codmodulo");
			precioAnimalesModulos = new BigDecimal(request.getParameter("grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].precio"));
		}
		
		Long idClase = obtenerIdClasePorExplotacionAnexo(explotacionBean);
		
		if (OPERACION_GRUPO_RAZA_EDITAR.equals(accion)){// Realiza la edición del grupo raza

			mv=doGuardarGrupoRazaAccionEditar(request,  explotacionBean, 
					 isCoberturas,  codgruporaza,  codtipocapital,
					 codtipoanimal,  numanimales,  precioAnimalesModulos, botonGuardarGR);
			
		}else if(OPERACION_GRUPO_RAZA_BORRAR.equals(accion)){// Realiza el borrado del grupo raza
			mv= doGuardarGrupoRazaAccionBorrar(request,  explotacionBean,
					 isCoberturas,  idClase);
			
		}else if(OPERACION_IDENTIFICATIVOS_GUARDAR.equals(accion)){
			mv=doGuardarGrupoRazaDatosIdentificativos( request, explotacionBean);
			
		}else{//Cuando se le da a guardar grupo raza sin haberle dado a editar

			// Explotación ya creada: realiza el alta del grupo raza
			if (explotacionBean != null && explotacionBean.getId() != null) {
				
				mv=doGuardarGrupoRazaExisteExplotacion( request,  
						 explotacionBean,  idClase,  codgruporaza, 
						 codtipocapital,  codtipoanimal,  numanimales,  precioAnimalesModulos,
						isCoberturas,  botonGuardarGR);
				
			} else {
				// Realiza el alta de la explotación y del grupo raza
				mv=doGuardarGrupoRazaNoExisteExplotacion(request,  
						explotacionBean,  isCoberturas,  idClase,  botonGuardarGR);
			}
		}
		
		// Redirige a la pantalla de datos de la explotación
		return mv;
	}

	
	@SuppressWarnings("unchecked")
	private ModelAndView doGuardarGrupoRazaAccionEditar(
			HttpServletRequest request, ExplotacionAnexo explotacionBean, 
			Boolean isCoberturas, Long codgruporaza, BigDecimal codtipocapital,
			Long codtipoanimal, Long numanimales, BigDecimal precioAnimalesModulos, Integer botonGuardarGR) {
		
		ModelAndView mv=null;
		List<String> names	= new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		ExplotacionAnexo expBbDd=null;
		Set<GrupoRazaAnexo> gr = null;
		Set<DatosVarExplotacionAnexo> datosVariables = null;
		Character tipoModificacion = null;
		List<ExplotacionCoberturaAnexo> lstCob = new ArrayList<ExplotacionCoberturaAnexo>();
		
		Map<String, String[]> parameters = request.getParameterMap();
		for(String parameter : parameters.keySet()) {
		    if(parameter.startsWith("dvCpto_")) {
		        names.add(parameter.split("_")[1]);
		        values.add(parameters.get(parameter)[0]);
		    }
		}
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		
		parametros.put("codPlan", explotacionBean.getAnexoModificacion().getPoliza().getLinea().getCodplan());
		parametros.put("codLinea", explotacionBean.getAnexoModificacion().getPoliza().getLinea().getCodlinea());
		
		
		parametros.put(DatosExplotacionesAnexoController.ANEXO_MODIFICACION, explotacionBean.getAnexoModificacion());

		
		
		Long idgruporaza = new Long (request.getParameter(DatosExplotacionesAnexoController.GRUPORAZAID));
		String idexplotacion = request.getParameter("id");
		expBbDd = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(new Long(idexplotacion));
		Map<String, Object> mapErroresDatosIdent = guardarDatosIdentificativos(explotacionBean, expBbDd);
		
		if(!mapErroresDatosIdent.containsKey(Constants.KEY_ALERTA)){
			gr = expBbDd.getGrupoRazaAnexos();
			
			Iterator<GrupoRazaAnexo> itGrupoRazaAnexoCol = gr.iterator();
			
			boolean repetido = false;
			GrupoRazaAnexo grEditar = null;
			
			while(itGrupoRazaAnexoCol.hasNext() && !repetido){
				GrupoRazaAnexo grAux = itGrupoRazaAnexoCol.next();
				
				//Tiene el mismo id -> lo guardamos
				if(grAux.getId().compareTo(idgruporaza)==0){
					grEditar = grAux;
				}else{
					//Si no es el mismo pero coincide el grupo raza, tipo capital y tipo animal...
					if(grAux.getCodgruporaza().compareTo(codgruporaza)==0 && 
							grAux.getCodtipocapital().compareTo(codtipocapital)==0 &&
							grAux.getCodtipoanimal().compareTo(codtipoanimal)==0){
						repetido = true;
					}
				}
			}
			
			if(!repetido && grEditar!=null){
				grEditar.setCodgruporaza(codgruporaza);
				grEditar.setCodtipoanimal(codtipoanimal);
				grEditar.setCodtipocapital(codtipocapital);
				grEditar.setNumanimales(numanimales);
				
				for(PrecioAnimalesModuloAnexo pam : grEditar.getPrecioAnimalesModuloAnexos()){
					if (pam.getGrupoRazaAnexo().equals(grEditar)){
						pam.setPrecio(precioAnimalesModulos);
					}
				}
				
				datosVariables = grEditar.getDatosVarExplotacionAnexos();
				////////////////////////////////////////////////////////////////////
				ArrayList<String> valores = new ArrayList<String>();
				
				for(DatosVarExplotacionAnexo dv : datosVariables){
					String insert = dv.getCodconcepto() + "_" + dv.getValor();
					valores.add(insert);
				}

				if(!datosVariables.isEmpty() && datosVariables != null){
					datosVariables.clear();
				}
				
				for(int i=0; i<names.size(); i++){
					if(!values.get(i).isEmpty()){
						datosVariables.add(new DatosVarExplotacionAnexo(null, grEditar, null, null, new Integer(names.get(i)), values.get(i)));
					}
				}
				
				grEditar.setDatosVarExplotacionAnexos(datosVariables);
				//gr.add(grEditar);
				expBbDd.setGrupoRazaAnexos(gr);
				
				// procesamiento coberturas
				String coberturas = request.getParameter(DatosExplotacionesAnexoController.COBERTURAS2);
				ExplotacionAnexo expAnexo = new ExplotacionAnexo();
				if(expBbDd.getId()!=null)
					expAnexo = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(expBbDd.getId());
				
				Set<ExplotacionCoberturaAnexo> setExpCoberturas = datosExplotacionesAnexoManager.procesarCoberturasAnexo(expAnexo,coberturas);
				request.setAttribute(DatosExplotacionesAnexoController.COB_PROCESADAS, DatosExplotacionesAnexoController.COB_PROCESADAS);
				if (setExpCoberturas != null && setExpCoberturas.size()>0) {
					explotacionBean.setExplotacionCoberturasAnexo(setExpCoberturas);
					parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, true);
				}else {
					parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, false);
				}
				;
				// convertimos el set a List				
				lstCob.addAll(setExpCoberturas);
				Collections.sort(lstCob, new ExplotacionCoberturaAnexoComparator());
				parametros.put(DatosExplotacionesAnexoController.LST_EXP_COBERTURAS, lstCob);
				
				//Tipo modificación
				tipoModificacion = explotacionBean.getTipoModificacion();
				expAnexo.setTipoModificacion(tipoModificacion);
				parametros = altaModificacionExplotacion(request, expAnexo,false);
				
				//Adaptación para versión request
				if(parametros.containsKey(Constants.KEY_MENSAJE)){
					request.setAttribute(Constants.KEY_MENSAJE, parametros.get(Constants.KEY_MENSAJE));
				}
				if(parametros.containsKey(Constants.KEY_ALERTA)){
					request.setAttribute(Constants.KEY_ALERTA, parametros.get(Constants.KEY_ALERTA));
				}
				
			}else{
				parametros.put(Constants.KEY_ALERTA, bundle.getString(DatosExplotacionesAnexoController.MENSAJE_DATOS_EXPLOTACION_COMBINACION_KO));
				request.setAttribute(Constants.KEY_ALERTA, bundle.getString(DatosExplotacionesAnexoController.MENSAJE_DATOS_EXPLOTACION_COMBINACION_KO));
			}
		}else{
			parametros.put(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
			request.setAttribute(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
		}
		
		
		parametros.put(DatosExplotacionesAnexoController.IS_COBERTURAS, isCoberturas);
		parametros.put(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR, botonGuardarGR);
		
		if(null!= botonGuardarGR && botonGuardarGR.compareTo(new Integer("1"))==0){
			parametros.put(DatosExplotacionesAnexoController.ID_GUARDAR_REPLICAR, idgruporaza);	
			parametros.put(BORRAR_FORM_GRUPO_RAZA, DatosExplotacionesAnexoController.FALSE);
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, DatosExplotacionesAnexoController.FALSE);
		}else{
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, "true");
		}
				
		parametros.put(DatosExplotacionesAnexoController.IS_COBERTURAS, isCoberturas);		
		String vieneDeListadoAnexosMod = request.getParameter(Constants.KEY_ORIGEN_LISTADO_ANX_MOD);
		parametros.put(DatosExplotacionesAnexoController.EXPLOTACION_ANEXO_ID, expBbDd.getId());
		parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
		mv = new ModelAndView("redirect:/datosExplotacionesAnexo.html").addAllObjects(parametros);
		return mv;
		
	}
	
	
	private ModelAndView doGuardarGrupoRazaAccionBorrar(
			HttpServletRequest request, ExplotacionAnexo explotacionBean,
			Boolean isCoberturas, Long idClase) {
		
		ModelAndView mv=null;
		ExplotacionAnexo expBbDd=null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		parametros.put("codPlan", explotacionBean.getAnexoModificacion().getPoliza().getLinea().getCodplan());
		parametros.put("codLinea", explotacionBean.getAnexoModificacion().getPoliza().getLinea().getCodlinea());
		parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));

		
		Character tipoModificacion=null;
		String idGrupoRazaBorrar = request.getParameter(DatosExplotacionesAnexoController.GRUPORAZAID);
		List<ExplotacionCoberturaAnexo> lstCob = new ArrayList<ExplotacionCoberturaAnexo>();
		
		String idexplotacion = request.getParameter("id");
		
		datosExplotacionesAnexoManager.borrarGrupoRazaAnexo(new Long(idGrupoRazaBorrar));
		expBbDd = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(new Long(idexplotacion));
		
		parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionBean.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid()));
		
		//Tipo modificación
		tipoModificacion = explotacionBean.getTipoModificacion();
		expBbDd.setTipoModificacion(tipoModificacion);
		
		//Si se queda sin grupos raza, creamos uno ficticio
		if(expBbDd.getGrupoRazaAnexos().size() == 0){
			GrupoRazaAnexo grvac = new GrupoRazaAnexo(expBbDd, null, null, null, null, null, null);
			PrecioAnimalesModuloAnexo pam = new PrecioAnimalesModuloAnexo(null, grvac, null);
			grvac.getPrecioAnimalesModuloAnexos().add(pam);
			explotacionBean.getGrupoRazaAnexos().clear();
			explotacionBean.getGrupoRazaAnexos().add(grvac);
			explotacionBean.setTipoModificacion(tipoModificacion);
			parametros.put(BEAN_EXPLOTACION_ANEXO, explotacionBean);	
		}else{
			parametros.put(BEAN_EXPLOTACION_ANEXO, expBbDd);				
		}
		parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
		parametros.put(DatosExplotacionesAnexoController.ANEXO_MODIFICACION, expBbDd.getAnexoModificacion());

		parametros.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.datosExplotacion.altaOK"));
		
		// procesamiento coberturas
		String coberturas = request.getParameter(DatosExplotacionesAnexoController.COBERTURAS2);
		Set<ExplotacionCoberturaAnexo> setExpCoberturas = datosExplotacionesAnexoManager.procesarCoberturasAnexo(expBbDd,coberturas);
		request.setAttribute(DatosExplotacionesAnexoController.COB_PROCESADAS, DatosExplotacionesAnexoController.COB_PROCESADAS);
		if (setExpCoberturas != null && setExpCoberturas.size()>0) {
			explotacionBean.setExplotacionCoberturasAnexo(setExpCoberturas);
			parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, true);
		}else {
			parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, false);
		}
		;
		// convertimos el set a List				
		lstCob.addAll(setExpCoberturas);
		Collections.sort(lstCob, new ExplotacionCoberturaAnexoComparator());
		parametros.put(DatosExplotacionesAnexoController.LST_EXP_COBERTURAS, lstCob);

		//Restricciones lupas
		//idClase = obtenerIdClasePorExplotacionAnexo(explotacionBean);
		datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		
		String vieneDeListadoAnexosMod = request.getParameter(Constants.KEY_ORIGEN_LISTADO_ANX_MOD);
		parametros.put(DatosExplotacionesAnexoController.EXPLOTACION_ANEXO_ID, expBbDd.getId());
		parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
		parametros.put(DatosExplotacionesAnexoController.IS_COBERTURAS, isCoberturas);
		
		addGruposNegocioNoDepNumAnimales(parametros,expBbDd.getAnexoModificacion().getPoliza().getLinea());
		mv = new ModelAndView(DatosExplotacionesAnexoController.MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO).addAllObjects(parametros);
		return mv;
		
	}
	
	private ModelAndView doGuardarGrupoRazaDatosIdentificativos(HttpServletRequest request,  
			ExplotacionAnexo explotacionBean) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		ExplotacionAnexo expBbDd=null;
		Character tipoModificacion;
		String idexplotacion = request.getParameter("id");
		expBbDd = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(new Long(idexplotacion));
		Map<String, Object> mapErroresDatosIdent = guardarDatosIdentificativos(explotacionBean, expBbDd);
		
		if(!mapErroresDatosIdent.containsKey(Constants.KEY_ALERTA)){
			//Tipo modificación
			tipoModificacion = explotacionBean.getTipoModificacion();
			expBbDd.setTipoModificacion(tipoModificacion);
			
			parametros = altaModificacionExplotacionGr(request, expBbDd,true);
			
			//Adaptación para versión request
			if(parametros.containsKey(Constants.KEY_MENSAJE)){
				request.setAttribute(Constants.KEY_MENSAJE, parametros.get(Constants.KEY_MENSAJE));
			}
			if(parametros.containsKey(Constants.KEY_ALERTA)){
				request.setAttribute(Constants.KEY_ALERTA, parametros.get(Constants.KEY_ALERTA));
			}
		}else{
			parametros.put(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
			request.setAttribute(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
		}
		return null;
	}
	
	
	
	private ModelAndView doGuardarGrupoRazaExisteExplotacion(HttpServletRequest request,  
			ExplotacionAnexo explotacionBean,  Long idClase, Long codgruporaza, 
			BigDecimal codtipocapital, Long codtipoanimal, Long numanimales, BigDecimal precioAnimalesModulos,
			Boolean isCoberturas, Integer botonGuardarGR){
		
		ModelAndView mv=null;
		ExplotacionAnexo expBbDd=null;
		Set<GrupoRazaAnexo> gr=null;
		Character tipoModificacion=null;
		GrupoRazaAnexo ngr=null;
		Long idGuardarReplicar=null; // id del nuevo grupo de raza. Solo lo alimentamos cuando se ha puulsado el botón de Guardar y replicar el GR
		
		boolean operacionCorrecta = false;
		
		expBbDd = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(explotacionBean.getId());
		gr = expBbDd.getGrupoRazaAnexos();
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		parametros.put("codPlan", explotacionBean.getAnexoModificacion().getPoliza().getLinea().getCodplan());
		parametros.put("codLinea", explotacionBean.getAnexoModificacion().getPoliza().getLinea().getCodlinea());
		
		
		
		boolean esNuevo = true;

		String codModuloAnx = explotacionBean.getAnexoModificacion().getCodmodulo();
		Iterator<GrupoRazaAnexo> itGrupoRazaAnexoIterator = gr.iterator();
		
		//Sin la condición de esNuevo para forzar el lazy
		while(itGrupoRazaAnexoIterator.hasNext()) {
			GrupoRazaAnexo gri = itGrupoRazaAnexoIterator.next();
			
			if (gri.getCodgruporaza().equals(codgruporaza)
					&& gri.getCodtipocapital().equals(codtipocapital)
					&& gri.getCodtipoanimal().equals(codtipoanimal)) {
				
				esNuevo = false;
				parametros.put(Constants.KEY_ALERTA, bundle.getString(DatosExplotacionesAnexoController.MENSAJE_DATOS_EXPLOTACION_COMBINACION_KO));
				request.setAttribute(Constants.KEY_ALERTA, bundle.getString(DatosExplotacionesAnexoController.MENSAJE_DATOS_EXPLOTACION_COMBINACION_KO));
			}
		}
		
		Map<String, Object> mapErroresDatosIdent = guardarDatosIdentificativos(explotacionBean, expBbDd);
		
		if(!mapErroresDatosIdent.containsKey(Constants.KEY_ALERTA)){
			//Tipo modificación
			tipoModificacion = explotacionBean.getTipoModificacion();
			expBbDd.setTipoModificacion(tipoModificacion);
			
			String vieneDeListadoAnexosMod = request.getParameter(Constants.KEY_ORIGEN_LISTADO_ANX_MOD);
			parametros.put(DatosExplotacionesAnexoController.EXPLOTACION_ANEXO_ID, expBbDd.getId());
			parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
			
			if(esNuevo){
				
				ngr = new GrupoRazaAnexo(expBbDd, codgruporaza, codtipocapital,
						codtipoanimal, numanimales, codModuloAnx,
						precioAnimalesModulos);
				getDatosVariables(ngr, request);
				
				gr.add(ngr);
				expBbDd.setGrupoRazaAnexos(gr);
								
				parametros = altaModificacionExplotacionGr(request, expBbDd,true);
				// recogemos de nuevo la explotacion de base de datos para completarla
				expBbDd = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(explotacionBean.getId());
				// procesamiento coberturas
				String coberturas = request.getParameter(DatosExplotacionesAnexoController.COBERTURAS2);
				Set<ExplotacionCoberturaAnexo> setExpCoberturas = datosExplotacionesAnexoManager.procesarCoberturasAnexo(expBbDd,coberturas);
				request.setAttribute(DatosExplotacionesAnexoController.COB_PROCESADAS, DatosExplotacionesAnexoController.COB_PROCESADAS);
				if (setExpCoberturas != null && setExpCoberturas.size()>0) {
					explotacionBean.setExplotacionCoberturasAnexo(setExpCoberturas);
					parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, true);
				}else {
					parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, false);
				}
				
				//Adaptación para versión request
				if(parametros.containsKey(Constants.KEY_MENSAJE)){
					request.setAttribute(Constants.KEY_MENSAJE, parametros.get(Constants.KEY_MENSAJE));
				}
				if(parametros.containsKey(Constants.KEY_ALERTA)){
					request.setAttribute(Constants.KEY_ALERTA, parametros.get(Constants.KEY_ALERTA));
				}else{
					operacionCorrecta = true;
				}
			}
			
			if(!operacionCorrecta){
				parametros.put(Constants.KEY_ALERTA, bundle.getString(DatosExplotacionesAnexoController.MENSAJE_DATOS_EXPLOTACION_COMBINACION_KO));
				request.setAttribute(Constants.KEY_ALERTA, bundle.getString(DatosExplotacionesAnexoController.MENSAJE_DATOS_EXPLOTACION_COMBINACION_KO));
			}
			
		}else{
			parametros.put(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
			request.setAttribute(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
		}
		
		//Tanto si es correcto como no, nos conviene borrar el formulario del siguiente paso
		
		parametros.put(DatosExplotacionesAnexoController.IS_COBERTURAS, isCoberturas);
		parametros.put(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR, botonGuardarGR);
		
		if(null!= botonGuardarGR && botonGuardarGR.compareTo(new Integer("1"))==0){
			if(null!=ngr && null!= ngr.getId())idGuardarReplicar=ngr.getId();
			parametros.put(BORRAR_FORM_GRUPO_RAZA, DatosExplotacionesAnexoController.FALSE);
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, DatosExplotacionesAnexoController.FALSE);
		}else{
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, "true");
		}
		parametros.put(DatosExplotacionesAnexoController.ID_GUARDAR_REPLICAR, idGuardarReplicar);
		
		if(operacionCorrecta){
			parametros.put(DatosExplotacionesAnexoController.EXPLOTACION_ANEXO_ID, expBbDd.getId());
			mv = new ModelAndView("redirect:/datosExplotacionesAnexo.html").addAllObjects(parametros);
			
		}else{
			//Restricciones lupas
			//Long idClase = obtenerIdClasePorExplotacionAnexo(explotacionBean);
			datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
			
			// procesamiento coberturas
			String coberturas = request.getParameter(DatosExplotacionesAnexoController.COBERTURAS2);
			Set<ExplotacionCoberturaAnexo> setExpCoberturas = datosExplotacionesAnexoManager.procesarCoberturasAnexo(expBbDd,coberturas);
			//request.setAttribute("cobProcesadas", "cobProcesadas");
			if (setExpCoberturas != null && setExpCoberturas.size()>0) {
				expBbDd.setExplotacionCoberturasAnexo(setExpCoberturas);

				crearVinculacionesCoberturas(expBbDd.getExplotacionCoberturasAnexo());
				// convertimos el set a List	
				List<ExplotacionCoberturaAnexo> lstCob = new ArrayList<ExplotacionCoberturaAnexo>();
				lstCob.addAll(expBbDd.getExplotacionCoberturasAnexo());
				Collections.sort(lstCob, new ExplotacionCoberturaAnexoComparator());
				parametros.put(DatosExplotacionesAnexoController.LST_EXP_COBERTURAS, lstCob);

//				if(expBbDd.getGrupoRazaAnexos().size() == 0){
//					Set<ExplotacionCoberturaAnexo> setExp = expBbDd.getExplotacionCoberturasAnexo();
//					//expAnxAux.setExplotacionCoberturasAnexo(setExp);
//				}		

				parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, true);
			}else {
				parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, false);
			}
			;
		
			
			parametros.put(BEAN_EXPLOTACION_ANEXO, expBbDd);
			parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionBean.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid()));
			parametros.put(DatosExplotacionesAnexoController.ANEXO_MODIFICACION, expBbDd.getAnexoModificacion());
			parametros.put(DatosExplotacionesAnexoController.IS_COBERTURAS, isCoberturas);
			mv = new ModelAndView(DatosExplotacionesAnexoController.MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO).addAllObjects(parametros);
		}
		return mv;
	}
	
	private Map<String, Object> guardarExplotacion(HttpServletRequest request, HttpServletResponse response, 
			ExplotacionAnexo explotacionAnexoBean) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		
		String vieneDeListadoAnexosMod = request.getParameter(Constants.KEY_ORIGEN_LISTADO_ANX_MOD);
		ExplotacionAnexo expAnexo = null;
		ModelAndView mv = doGuardarGrupoRazaAnexo(request, response, explotacionAnexoBean);
		
		String arrayParametros[] = {Constants.KEY_ALERTA, Constants.KEY_MENSAJE, BORRAR_FORM_GRUPO_RAZA};
		
		ParamUtils.recuperarRequest(request, parametros, arrayParametros);
		
		parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
		
		
		logger.debug("fechaInicioContratacion al guardar explotacion anexo: " + request.getParameter("fechaInicioContratacion"));
		parametros.put("fechaInicioContratacion", request.getParameter("fechaInicioContratacion"));

		boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
	
		
		
		
		if (mv != null && !isGuardadoOk){
			parametros.put("mv", mv);
			return parametros;
		}
		if(explotacionAnexoBean.getId()!=null)
			expAnexo = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(explotacionAnexoBean.getId());
		
		// procesamiento coberturas
		String cobProcesadas = StringUtils.nullToString(request.getParameter(DatosExplotacionesAnexoController.COB_PROCESADAS));
		if (cobProcesadas == "")
			cobProcesadas = StringUtils.nullToString(request.getAttribute(DatosExplotacionesAnexoController.COB_PROCESADAS));
		Set<ExplotacionCoberturaAnexo> setExpCoberturas = new HashSet<ExplotacionCoberturaAnexo>();
		if (!cobProcesadas.equals(DatosExplotacionesAnexoController.COB_PROCESADAS)){
			String coberturas = request.getParameter(DatosExplotacionesAnexoController.COBERTURAS2);
			setExpCoberturas = datosExplotacionesAnexoManager.procesarCoberturasAnexo(expAnexo, coberturas);  //datosExplotacionesAnexoManager.procesarCoberturasAnexo(expAnexo,coberturas);
			if (setExpCoberturas != null && setExpCoberturas.size()>0) {
				explotacionAnexoBean.setExplotacionCoberturasAnexo(setExpCoberturas);
				parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, true);
			}else {
				parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, false);
			}
		}
		
		
		
		
		return parametros;
	
	}
	
	private ModelAndView devuelveErrorAVista(Map<String, Object> parametros, ExplotacionAnexo explotacionAnexoBean){
		ModelAndView mv = null;
		
		if(explotacionAnexoBean.getId()!=null){
			
			explotacionAnexoBean = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(explotacionAnexoBean.getId());
		}
		
		parametros.put(BEAN_EXPLOTACION_ANEXO, explotacionAnexoBean);
		// Carga los datos variables configurados para el plan y línea de la póliza
		parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionAnexoBean.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid()));
		
		//Restricciones lupas
		Long idClase = obtenerIdClasePorExplotacionAnexo(explotacionAnexoBean);
		datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		
		// Redirige a la pantalla de datos de la explotación
		mv = new ModelAndView(DatosExplotacionesAnexoController.MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO).addAllObjects(parametros);
		
		return mv;
	}
	
	private ModelAndView doGuardarGrupoRazaNoExisteExplotacion(HttpServletRequest request,  
			ExplotacionAnexo explotacionBean, Boolean isCoberturas, Long idClase, Integer botonGuardarGR){
		
		ModelAndView mv=null;
		Map<String, Object> parametros = altaModificacionExplotacion(request, explotacionBean,true);
		

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		parametros.put("codPlan", explotacionBean.getAnexoModificacion().getPoliza().getLinea().getCodplan());
		parametros.put("codLinea", explotacionBean.getAnexoModificacion().getPoliza().getLinea().getCodlinea());
		
		
		parametros.put(DatosExplotacionesAnexoController.ANEXO_MODIFICACION, explotacionBean.getAnexoModificacion());


		String vieneDeListadoAnexosMod = request.getParameter(Constants.KEY_ORIGEN_LISTADO_ANX_MOD);
		parametros.put(Constants.KEY_ORIGEN_LISTADO_ANX_MOD, vieneDeListadoAnexosMod);
		
		boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
		Long idGuardarReplicar=null; // id del nuevo grupo de raza. Solo lo alimentamos cuando se ha puulsado el botón de Guardar y replicar el GR
		ExplotacionAnexo aux=null; 
				
		//Si se ha realizado bien el alta
		if(isGuardadoOk){
			//Se ha de recargar para rellenar los campos de fórmula
			aux = datosExplotacionesAnexoManager.obtenerExplotacionAnexoById(explotacionBean.getId());
			parametros.put(BEAN_EXPLOTACION_ANEXO, aux);
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, "true");
			request.setAttribute(Constants.KEY_MENSAJE, parametros.get(Constants.KEY_MENSAJE));
			
		}else{
			request.setAttribute(Constants.KEY_ALERTA, parametros.get(Constants.KEY_ALERTA));
		}
		parametros.put(DatosExplotacionesAnexoController.IS_COBERTURAS, isCoberturas);
		parametros.put(DatosExplotacionesAnexoController.BOTON_GUARDAR_GR, botonGuardarGR);
		
		if(null!= botonGuardarGR && 
				botonGuardarGR.compareTo(new Integer("1"))==0 && 
				null!= aux){
			idGuardarReplicar=aux.getId();
			parametros.put(BORRAR_FORM_GRUPO_RAZA, DatosExplotacionesAnexoController.FALSE);
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, DatosExplotacionesAnexoController.FALSE);
		}
		parametros.put(DatosExplotacionesAnexoController.ID_GUARDAR_REPLICAR, idGuardarReplicar);
		
		
		
		// procesamiento coberturas
		String coberturas = request.getParameter(DatosExplotacionesAnexoController.COBERTURAS2);
		Set<ExplotacionCoberturaAnexo> setExpCoberturas = datosExplotacionesAnexoManager.procesarCoberturasAnexo(aux,coberturas);
		request.setAttribute(DatosExplotacionesAnexoController.COB_PROCESADAS, DatosExplotacionesAnexoController.COB_PROCESADAS);
		if (setExpCoberturas != null && setExpCoberturas.size()>0) {
			explotacionBean.setExplotacionCoberturasAnexo(setExpCoberturas);
			parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, true);
		}else {
			parametros.put(DatosExplotacionesAnexoController.TIENE_COBERTURAS, false);
		}
		;
		// convertimos el set a List	
		List<ExplotacionCoberturaAnexo> lstCob = new ArrayList<ExplotacionCoberturaAnexo>();
		lstCob.addAll(setExpCoberturas);
		Collections.sort(lstCob, new ExplotacionCoberturaAnexoComparator());
		parametros.put(DatosExplotacionesAnexoController.LST_EXP_COBERTURAS, lstCob);
		
		
		// Carga los datos variables configurados para el plan y línea de la póliza
		parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager
				.cargarDatosVariables(explotacionBean.getAnexoModificacion().getPoliza()
						.getLinea().getLineaseguroid()));

		//Restricciones lupas		
		datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		mv = new ModelAndView(DatosExplotacionesAnexoController.MODULO_EXPLOTACIONES_EXPLOTACIONES_DATOS_EXPLOTACION_ANEXO)
				.addAllObjects(parametros);
		return mv;
	}
	
	/**
	 * Copia una explotación de anexo en otro objeto del mismo tipo con los datos básicos
	 * Sirve para cuando hay que añadir algo a la explotación de anexo sin que persista en base de datos.
	 * @param explotacionAnexo
	 * @return
	 */
	private ExplotacionAnexo copiarExplotacionAnexoEnBean(ExplotacionAnexo explotacionAnexo) {
		ExplotacionAnexo explotacionAnexoCopia = new ExplotacionAnexo();
		AnexoModificacion anexoModificacionCopia = new AnexoModificacion();
		
		AnexoModificacion anexoModificacion = explotacionAnexo.getAnexoModificacion();
		anexoModificacionCopia.setId(anexoModificacion.getId());
		anexoModificacionCopia.setCodmodulo(anexoModificacion.getCodmodulo());
		
		Poliza polizaCopia = new Poliza();
		polizaCopia.setIdpoliza(anexoModificacion.getPoliza().getIdpoliza());
		Linea lineaCopia = new Linea();
		lineaCopia.setLineaseguroid(anexoModificacion.getPoliza().getLinea().getLineaseguroid());
		polizaCopia.setLinea(lineaCopia);
		anexoModificacionCopia.setPoliza(polizaCopia);
		
		explotacionAnexoCopia.setAnexoModificacion(anexoModificacionCopia);
		
		explotacionAnexoCopia.setEspecie(explotacionAnexo.getEspecie());
		explotacionAnexoCopia.setId(explotacionAnexo.getId());
		explotacionAnexoCopia.setLatitud(explotacionAnexo.getLatitud());
		explotacionAnexoCopia.setLongitud(explotacionAnexo.getLongitud());
		explotacionAnexoCopia.setNomespecie(explotacionAnexo.getNomespecie());
		explotacionAnexoCopia.setNomregimen(explotacionAnexo.getNomregimen());
		explotacionAnexoCopia.setNumero(explotacionAnexo.getNumero());
		explotacionAnexoCopia.setRega(explotacionAnexo.getRega());
		explotacionAnexoCopia.setRegimen(explotacionAnexo.getRegimen());
		explotacionAnexoCopia.setSigla(explotacionAnexo.getSigla());
		explotacionAnexoCopia.setSubexplotacion(explotacionAnexo.getSubexplotacion());
		explotacionAnexoCopia.setTermino(explotacionAnexo.getTermino());
		explotacionAnexoCopia.setTipoModificacion(explotacionAnexo.getTipoModificacion());
		return explotacionAnexoCopia;
	}

	
	private Map<String, Object> altaModificacionExplotacion(HttpServletRequest request, ExplotacionAnexo explotacionAnexoBean,boolean validar) {
		// Si se está editando se borran los datos variables previos, para que no se dupliquen al guardar el objeto de explotación
		
		if (explotacionAnexoBean != null && explotacionAnexoBean.getId() != null) { // Se está editando si la explotación tiene id
			datosExplotacionesAnexoManager.borrarListaDatosVariables(explotacionAnexoBean, request.getParameter(DatosExplotacionesAnexoController.GRUPORAZAID));
		}
		
		// Obtiene la lista de datos variables informados en la pantalla y los carga en el objeto de explotacion
		cargarListaDatosVarExplotacionAnexosFromRequest(request, explotacionAnexoBean);
		
		// Guarda en BBDD el objeto explotación
		return datosExplotacionesAnexoManager.alta(explotacionAnexoBean,validar);
	}
	
	private Map<String, Object> altaModificacionExplotacionGr(HttpServletRequest request, ExplotacionAnexo explotacionAnexoBean,boolean validar) {
		// Si se está editando se borran los datos variables previos, para que no se dupliquen al guardar el objeto de explotación
//		if (explotacionAnexoBean != null && explotacionAnexoBean.getId() != null) { // Se está editando si la explotación tiene id
//			datosExplotacionesAnexoManager.borrarListaDatosVariables(explotacionAnexoBean, request.getParameter("gruporazaid"));
//		}

		// Guarda en BBDD el objeto explotación
		return datosExplotacionesAnexoManager.alta(explotacionAnexoBean,validar);
	}

	
	@SuppressWarnings("unchecked")
	private void cargarListaDatosVarExplotacionAnexosFromRequest (HttpServletRequest request, ExplotacionAnexo explotacionAnexoBean) {
		
		logger.debug("DatosExplotacionesAnexoController - cargarListaDatosVarExplotacionAnexosFromRequest - init ");

		String idGr = StringUtils.nullToString(request.getParameter(DatosExplotacionesAnexoController.GRUPORAZAID));
		try {
			
			logger.debug("GRUPO RAZA ID: " + idGr);
			logger.debug("NUM. GRUPO RAZA ANEXOS: " + explotacionAnexoBean.getGrupoRazaAnexos().size());
			
			Iterator<Map.Entry<String,String[]>> it = request.getParameterMap().entrySet().iterator();
			
			Set<DatosVarExplotacionAnexo> listaDV = new HashSet<DatosVarExplotacionAnexo>();
			
			for (GrupoRazaAnexo gr : explotacionAnexoBean.getGrupoRazaAnexos()) {
				
				logger.debug("Hay grupos de raza de anexos");
				logger.debug("GrupoRazaAnexo id: " + gr.getId());
				
				if (null == gr.getId() || gr.getId().toString().equals(idGr)){
					while (it.hasNext()) {
						Map.Entry<String,String[]> entry = it.next();
						
						String key = entry.getKey();
						
						logger.debug("Llave: " + key);
						
						if (key != null && key.startsWith(PREFIJO_DV)) {
							String[] value         = entry.getValue();
							
							logger.debug("Value: " + value.toString());

							if (value.length > 0 && !StringUtils.isNullOrEmpty(value[0])) {
								DatosVarExplotacionAnexo dv = new DatosVarExplotacionAnexo(null, gr, null, null, Integer.valueOf(key.substring(PREFIJO_DV.length())), value[0]);
								listaDV.add(dv);
								
								logger.debug("DV de explotación cargado:  " + dv.getCodconcepto() + " = " + dv.getValor());
							}
						}
					}
				}
				gr.setDatosVarExplotacionAnexos(listaDV);
			}
		} catch (Exception e) {
			logger.error("Error al cargar la lista de datos variables en la explotación", e);
		}
		
		logger.debug("DatosExplotacionesAnexoController - cargarListaDatosVarExplotacionAnexosFromRequest - end");

		
	}
//
//	/**
//	 * Crea un objeto Explotación a partir de los parametros informados en la request
//	 * @param request
//	 * @return Objeto ExplotacionAnexo o nulo si ha habido errores en el proceso
//	 */
	private ExplotacionAnexo getExplotacionFromRequest(HttpServletRequest request) {

		logger.debug("DatosExploracionesAnexoController - getExplotacionFromRequest - init");
		//Recoge los parámetros de la explotación necesarios para el cálculo y crea el objeto que los encapsula
		ExplotacionAnexo e = new ExplotacionAnexo();
		
		try {
			
			e.getAnexoModificacion().getPoliza().setIdpoliza(new Long (request.getParameter("idPoliza"))); // Idpoliza (para obtener plan, línea y módulos)
			e.getAnexoModificacion().setCodmodulo(request.getParameter("codModulo"));
			e.getTermino().getId().setCodprovincia(new BigDecimal (request.getParameter("codProvincia"))); // Provincia
			e.getTermino().getId().setCodcomarca(new BigDecimal (request.getParameter("codComarca"))); // Comarca
			e.getTermino().getId().setCodtermino(new BigDecimal (request.getParameter("codTermino"))); // Término
			e.getTermino().getId().setSubtermino(new Character (request.getParameter("subtermino").charAt(0))); // Subtérmino
			e.setEspecie(new Long (request.getParameter("especie")));
			e.setRegimen(new Long (request.getParameter("regimen")));
			
			if (null != request.getParameter("latitud") && !request.getParameter("latitud").equals(""))
				e.setLatitud(new Integer((request.getParameter("latitud"))));
			if (null != request.getParameter("longitud") && !request.getParameter("longitud").equals(""))
				e.setLongitud(new Integer((request.getParameter("longitud"))));
			e.setRega(StringUtils.nullToString(request.getParameter("rega")));
			e.setSigla(StringUtils.nullToString(request.getParameter("sigla")));
			if (null != request.getParameter("subexplotacion") && !request.getParameter("subexplotacion").equals(""))
				e.setSubexplotacion(new Integer((request.getParameter("subexplotacion"))));
			logger.debug("latitud: "+e.getLatitud()+ " longitud: "+e.getLongitud()+" rega: "+e.getRega()+ " sigla: "+e.getSigla()+ " subexplotacion: "+e.getSubexplotacion());

			
			logger.debug("Valores para informar objeto GrupoRazaAnexo: ");
			logger.debug("numAnimales: " + request.getParameter("numanimales"));
			logger.debug("grupoRaza: " + request.getParameter("grupoRaza"));
			logger.debug("tipoAnimal: " + request.getParameter("tipoAnimal"));
			logger.debug("tipoCapital: " + request.getParameter("tipoCapital"));

			Long numAnimales = (new Long (request.getParameter("numanimales")));
			GrupoRazaAnexo gr = new GrupoRazaAnexo();
			gr.setCodgruporaza(new Long (request.getParameter("grupoRaza")));
			gr.setCodtipoanimal(new Long (request.getParameter("tipoAnimal")));
			gr.setCodtipocapital(new BigDecimal (request.getParameter("tipoCapital")));
			gr.setNumanimales(numAnimales);
			Set<GrupoRazaAnexo> setGR = new HashSet<GrupoRazaAnexo>(0);
			setGR.add(gr);
			e.setGrupoRazaAnexos(setGR);
			
			// Carga los datos variables
			cargarListaDatosVarExplotacionAnexosFromRequest (request, e);
			
			//Le asignamos un valor al número de la explotación por ser un dato obligatorio en el servicio 
			//para recoger las coberturas elegibles
			String numExplotacion=request.getParameter("numeroExp");
			if(numExplotacion.isEmpty())numExplotacion="1";
			e.setNumero(new Integer(numExplotacion));
			
			logger.debug("DatosExploracionesAnexoController - getExplotacionFromRequest - end");

			return e;
		} catch (Exception ex) {
			logger.error("Error al crear el objeto ExplotacionAnexo con los datos de la request", ex);
			logger.debug("DatosExploracionesAnexoController - getExplotacionFromRequest - end");

			return null;
		}
	}

	
	@SuppressWarnings("unchecked")
	private void getDatosVariables(GrupoRazaAnexo grupoRaza, HttpServletRequest request){
		Map<String, Object> map = request.getParameterMap();
		String[] aux;
		ArrayList<String> one = new ArrayList<String>();
		ArrayList<String> two = new ArrayList<String>();
		
		for (Map.Entry<String, Object> entry : map.entrySet()) {
		    String key = entry.getKey();
		    //Object value = entry.getValue();
		    if(key.contains("dvCpto_")){
		    	aux = key.split("_");
		    	one.add(aux[1]);
		    	two.add(request.getParameter(key));
		    }
		}
		for (int i=0; i < one.size(); i++){
			if(two.get(i) != null && !VACIO.equals(two.get(i))){
				grupoRaza.getDatosVarExplotacionAnexos().add(new DatosVarExplotacionAnexo(null, grupoRaza, null, null, new Integer(one.get(i)), two.get(i)));
			}
		}
	}
	
	private Long obtenerIdClasePorAnexo(AnexoModificacion anexoModificacion){
		
		Long lineaSeguroId = anexoModificacion.getPoliza().getLinea().getLineaseguroid();
		BigDecimal clase = anexoModificacion.getPoliza().getClase();
		Long idClase = claseManager.getClase(lineaSeguroId, clase);
		return idClase;
	}
	
	private Long obtenerIdClasePorExplotacionAnexo(ExplotacionAnexo explotacionAnexo){
		
		Long lineaSeguroId = explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid();
		BigDecimal clase = explotacionAnexo.getAnexoModificacion().getPoliza().getClase();
		Long idClase = claseManager.getClase(lineaSeguroId, clase);
		return idClase;
	}
	
	
	/**
	 * Graba (si pasa las validaciones) los datos identificativos de una explotación
	 * @param expFuente Viene del formulario que se rellena
	 * @param expDestino Explotación de anexo a grabar en base de datos
	 * @return
	 */
	private Map<String, Object> guardarDatosIdentificativos(ExplotacionAnexo expFuente, ExplotacionAnexo expDestino){

		Map<String, Object> mapaErrores = datosExplotacionesAnexoManager.validacionesPrevias(expFuente,true);

		if(!mapaErrores.containsKey(Constants.KEY_ALERTA)){
			
			BigDecimal codProvincia = expFuente.getTermino().getId().getCodprovincia();
			BigDecimal codComarca = expFuente.getTermino().getId().getCodcomarca();
			BigDecimal codTermino = expFuente.getTermino().getId().getCodtermino();
			Character subtermino = expFuente.getTermino().getId().getSubtermino();
			
			Termino termino = datosExplotacionesManager.obtenerTermino(codProvincia, codComarca, codTermino, subtermino);
			expDestino.setTermino(termino);
			
			expDestino.setRega(expFuente.getRega());
			expDestino.setSigla(expFuente.getSigla());
			
			expDestino.setSubexplotacion(expFuente.getSubexplotacion());

			expDestino.setEspecie(expFuente.getEspecie());
			expDestino.setRegimen(expFuente.getRegimen());
			
			expDestino.setLatitud(expFuente.getLatitud());
			expDestino.setLongitud(expFuente.getLongitud());
		}
		
		return mapaErrores;
	}
	
	private void addGruposNegocioNoDepNumAnimales(final Map<String, Object> parametros, Linea linea){
		String lstTc=null;
		if(linea.tieneLineaGrupoNegocio(Constants.GRUPO_NEGOCIO_VIDA)){
			lstTc=datosExplotacionesManager.getTipoCapitalConGrupoNegocio(true);			
		}
		parametros.put("listaTCapNoDepNumAni",lstTc);
	}
	
	//SETTERS
	public void setAnexoModificacionManager(
			AnexoModificacionManager anexoModificacionManager) {
		this.anexoModificacionManager = anexoModificacionManager;
	}
	
	public void setDatosExplotacionesManager(IDatosExplotacionesManager datosExplotacionesManager) {
		this.datosExplotacionesManager = datosExplotacionesManager;
	}

	public void setExplotacionesAnexoManager(ExplotacionesAnexoManager explotacionesAnexoManager){
		this.explotacionesAnexoManager = explotacionesAnexoManager;
	}

	public void setDatosExplotacionesAnexoManager(
			DatosExplotacionesAnexoManager datosExplotacionesAnexoManager) {
		this.datosExplotacionesAnexoManager = datosExplotacionesAnexoManager;
	}

	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}
}