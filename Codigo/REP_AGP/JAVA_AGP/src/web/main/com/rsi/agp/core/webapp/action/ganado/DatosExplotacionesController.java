package com.rsi.agp.core.webapp.action.ganado;

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
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.PrecioGanadoException;
import com.rsi.agp.core.managers.IBaseManager;
import com.rsi.agp.core.managers.IDatosExplotacionesManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.DatosExplotacionesUtil;
import com.rsi.agp.core.util.ExplotacionCoberturaComparator;
import com.rsi.agp.core.util.ParamUtils;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaVinculacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;

public class DatosExplotacionesController extends BaseMultiActionController {
	
	private static final Log logger = LogFactory.getLog(DatosExplotacionesController.class);
	
	private IDatosExplotacionesManager datosExplotacionesManager;
	private ExplotacionesManager explotacionesManager;
	private IBaseManager baseManager;
	private ClaseManager claseManager;
	
	private final String PREFIJO_DV = "dvCpto_";
	private final String OPERACION_GRUPO_RAZA_BORRAR = "borrar";
	private final String OPERACION_GRUPO_RAZA_EDITAR = "editar";
	private final String OPERACION_IDENTIFICATIVOS_GUARDAR = "saveDatosIdentificativos";
	private final String BORRAR_FORM_GRUPO_RAZA = "borradoFormularioGrupoRaza";
	private final String BEAN_EXPLOTACION = "explotacionBean";
	
	private final String ID_POL = "idpol";
	private final String ID_EXP = "idexp";
	private final String TIENE_COBERTURAS = "tieneCoberturas";
	private final String LST_EXP_COBERTURAS = "lstExpCoberturas";
	private final String POLIZA_BEAN = "polizaBean";
	private final String ES_ALTA = "esAlta";
	private final String MENSAJE_EXPLOTACION_KO = "mensaje.datosExplotacion.combinacionKO";
	private final String IS_COBERTURAS = "isCoberturas";
	private final String BOTON_GUARDAR_GR = "botonGuardarGR";
	private final String ID_GUARDAR_REPLICAR = "idGuardarReplicar";
	private final String MODULO_EXPLOTACIONES = "moduloExplotaciones/explotaciones/datosExplotaciones";
	private final String REGISTRO_UNICO = "registroUnico";
	private final String PULSADO = "pulsado";
	private final String FALSE = "false";
	private final String MODO_LECTURA = "modoLectura";
	private final String USUARIO = "usuario";
	private final String WEB_INF = "/WEB-INF/";
	private final String COB_PROCESADAS = "cobProcesadas";
	private final String COB_GUARDADAS = "cobGuardadas";
	private final String COBERTURAS = "coberturas";
	private final String ID_POLIZA = "idpoliza";
	private final String REDIRECT_EXPLOTACIONES = "redirect:/datosExplotaciones.html";
	private final String ORIGEN_LLAMADA = "origenllamada";

	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;
	
	// Parametros requeridos para obtener la información de Rega y Bloquear funcionalidades dependiendo el perfil del usuario
	private void fillMapWithRequiredParams(Map<String, Object> parametros, HttpServletRequest request, Poliza polizaBean) {
		logger.debug("fillMapWithRequiredParams -- INIT");

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		
		if (polizaBean == null) {
			logger.error("La poliza no debe ser nula");
		}
		
		parametros.put("codPlan", polizaBean.getLinea().getCodplan());
		parametros.put("codLinea", polizaBean.getLinea().getCodlinea());

		logger.debug("fillMapWithRequiredParams -- END");
	}
	
	/**
	 * Redirige a la pantalla de alta/modificación de explotaciones
	 * @param request
	 * @param response
	 * @param polizaRenovableBean
	 * @return
	 */
	public ModelAndView doPantallaAltaExplotacion (HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) {
		
		logger.debug("doPantallaAltaExplotacion - Redirección a pantalla de alta/modificación de explotaciones");
		Integer botonGuardarGR=null;
		Long idGuardarReplicar=null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		//Poliza plz = this.datosExplotacionesManager.getPoliza(polizaBean.getIdpoliza());

		

		
		Poliza plz = null;
		if(polizaBean.getIdpoliza() == null){// Viene de edición
			plz = this.datosExplotacionesManager.getPoliza(new Long(request.getParameter(ID_POL)));
			Explotacion exp = datosExplotacionesManager.getExplotacion(new Long(request.getParameter(ID_EXP)));
			if (exp.getExplotacionCoberturas() != null && exp.getExplotacionCoberturas().size()>0){
				crearVinculacionesCoberturas(exp.getExplotacionCoberturas());
				parametros.put(TIENE_COBERTURAS, true);
				// convertimos el set a List
				List<ExplotacionCobertura> lstExpCoberturas = new ArrayList<ExplotacionCobertura>();
				lstExpCoberturas.addAll(exp.getExplotacionCoberturas());
				Collections.sort(lstExpCoberturas, new ExplotacionCoberturaComparator());
				parametros.put(LST_EXP_COBERTURAS, lstExpCoberturas);
			}else {
				parametros.put(TIENE_COBERTURAS, false);
			}
			parametros.put(BEAN_EXPLOTACION, exp);
			// Carga los datos variables configurados para el plan y lónea de la póliza
			parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(plz.getLinea().getLineaseguroid()));
			parametros.put(POLIZA_BEAN, plz);
			
			//Comprobamos si se viene de alta de explotacion y en caso afirmativo pasamos el parametro
			String esAlta = request.getParameter(ES_ALTA);
			if (esAlta != null){
				parametros.put(ES_ALTA, "true");
			}
			
			//Pasamos el resto de parametros de la accion de guardado para visualizar y preparar la pagina correctamente
			addParametersGuardar(exp, parametros);
			
			if(request.getParameter(Constants.KEY_ALERTA) != null){
				parametros.put(Constants.KEY_ALERTA, bundle.getString(MENSAJE_EXPLOTACION_KO));
			}
			if(request.getParameter(Constants.KEY_MENSAJE) != null){
				parametros.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.datosExplotacion.altaOK"));
			}
			
		}
		else{
			plz = this.datosExplotacionesManager.getPoliza(polizaBean.getIdpoliza());
			parametros.put(BEAN_EXPLOTACION, new Explotacion(plz));
			// Carga los datos variables configurados para el plan y lónea de la póliza
			parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(polizaBean.getLinea().getLineaseguroid()));

			
			parametros.put(POLIZA_BEAN, plz);
		}
		
		
		this.fillMapWithRequiredParams(parametros, request, plz);

		// Carga los datos variables configurados para el plan y lónea de la póliza
		//parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(polizaBean.getLinea().getLineaseguroid()));

		if(null!=plz && null!=plz.getIdpoliza()&& null!=plz.getClase()&& null!=plz.getLinea()
			&& null!=plz.getLinea().getLineaseguroid()){			
			Long idClase=getIdClase(plz.getClase(), plz.getLinea().getLineaseguroid());
			datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);			
		}
		
		// Comprobar si puede tener coberturas segun lónea y modulos de la poliza
		if(null!=plz && plz.getLinea() != null && plz.getModuloPolizas() != null) {
			Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();
			boolean isCoberturas = datosExplotacionesManager.isCoberturasElegiblesNivelExplotacion(plz.getLinea().getLineaseguroid(), modsPoliza);
			parametros.put(IS_COBERTURAS,isCoberturas);
		}
		
		
		if(null!=request.getParameter(BOTON_GUARDAR_GR) && !request.getParameter(BOTON_GUARDAR_GR).isEmpty()){
			botonGuardarGR=new Integer(request.getParameter(BOTON_GUARDAR_GR));
		}
		parametros.put(BOTON_GUARDAR_GR, botonGuardarGR);
		
		if(null!=request.getParameter(ID_GUARDAR_REPLICAR) && !request.getParameter(ID_GUARDAR_REPLICAR).isEmpty()){
			idGuardarReplicar=new Long(request.getParameter(ID_GUARDAR_REPLICAR));
		}
		parametros.put(ID_GUARDAR_REPLICAR, idGuardarReplicar);
		
		
		//Cargamos los tipos de capital con grupo de negocio que no dependen del nómero de animales
		addGruposNegocioNoDepNumAnimales(parametros,plz.getLinea());
		
		
		// Se recupera una instancia especifica de la entidad "Linea" a traves del DAO a partir del lineaseguroid
		com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(plz.getLinea().getLineaseguroid().toString());
		// Obtenemos la fecha de fin de contratacion.
		Date fechaInicioContratacion = linea.getFechaInicioContratacion();
		parametros.put("fechaInicioContratacion", fechaInicioContratacion);
		
		//parametros.put(BORRAR_FORM_GRUPO_RAZA, request.getParameter(BORRAR_FORM_GRUPO_RAZA));
		return new ModelAndView(MODULO_EXPLOTACIONES).addAllObjects(parametros);
	}
	
	private void addGruposNegocioNoDepNumAnimales(final Map<String, Object> parametros, Linea linea){
		String lstTc=null;
		if(linea.tieneLineaGrupoNegocio(Constants.GRUPO_NEGOCIO_VIDA)){
			lstTc=datosExplotacionesManager.getTipoCapitalConGrupoNegocio(true);			
		}
		parametros.put("listaTCapNoDepNumAni",lstTc);
	}
	
	
	private Long getIdClase(BigDecimal clase, Long lineaSeguroId){
		Long idClase=null;		
		if (null!=clase && null!=lineaSeguroId)
			idClase= claseManager.getClase(lineaSeguroId, clase);
		return idClase;
	}
	
	public void addParametersGuardar (Explotacion exp, Map<String, Object> parametros){
		if (exp.getGrupoRazas().size() == 1){
			parametros.put(REGISTRO_UNICO, "true");
			parametros.put("gr0", exp.getGrupoRazas().iterator().next());
			parametros.put(PULSADO, "true");
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
		}
		if (exp.getGrupoRazas().size() > 1){
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
			parametros.put(REGISTRO_UNICO, FALSE);
			parametros.put(PULSADO, "true");
		}
	}
	
	/**
	 * Redirige a la pantalla de alta/modificación de explotaciones con los datos de la explotación indicada cargados
	 * @param request
	 * @param response
	 * @param polizaRenovableBean
	 * @return
	 */
	public ModelAndView doEditar(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) {
		
		logger.debug("doEditar - Redirección a pantalla de alta/modificación de explotaciones");
			
		String modoLectura = StringUtils.nullToString(request.getParameter(MODO_LECTURA));		
		Boolean esModoLectura=(modoLectura.compareTo(MODO_LECTURA)==0);
		String vieneDeUtilidades=StringUtils.nullToString(request.getParameter("vieneDeUtilidades"));
		String origenLlamada=StringUtils.nullToString(request.getParameter("origenLlamada"));
		Explotacion ex = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			if(origenLlamada.compareTo("erroresValidacion")==0){
				String strIdPoliza=request.getParameter("idPolizaExplotaciones");
				String strNumExplotacion=request.getParameter("numexplotacion");
				Long idPoliza= new Long(strIdPoliza);
				Integer numExp=new Integer(strNumExplotacion);
				ex=datosExplotacionesManager.getExplotacion(idPoliza, numExp);
				polizaBean.setIdpoliza(idPoliza);
				//parametros.put("idPoliza", idPoliza);
			}else{
				ex = this.datosExplotacionesManager.getExplotacion(Long.parseLong(request.getParameter("idExplotacion")));
			}
			
		} catch (Exception e) {
			logger.error("Error al obtener la explotación a editar", e);
		}
		
		String arrayParametros[] = {Constants.KEY_MENSAJE};
		
		ParamUtils.recuperarRequest(request, parametros, arrayParametros);//Por si viene de un guardado, para que muestre el mensaje=
		
		// Carga los datos variables configurados para el plan y lónea de la póliza
		parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(ex != null ? ex.getPoliza().getLinea().getLineaseguroid() :
																			 polizaBean.getLinea().getLineaseguroid()));
		//Comprobamos la existencia o no de grupos raza para la explotacion y clonamos la explotacion pero con un grupo raza ficticio
		Explotacion explotacionBean = null;
		if(ex.getGrupoRazas().size() == 0){
			explotacionBean = new Explotacion(ex.getId(), ex.getTermino(), ex.getPoliza(), ex.getLatitud(), ex.getLongitud(), ex.getNumero(), ex.getRega(), ex.getSigla(), ex.getSubexplotacion(), ex.getEspecie(), ex.getRegimen(), ex.getSubvExplotacionCCAAs(), ex.getSubvExplotacionENESAs());
			GrupoRaza grvac = new GrupoRaza(explotacionBean, null, null, null, null, null, null);
			PrecioAnimalesModulo pam = new PrecioAnimalesModulo(null, grvac, null);
			grvac.getPrecioAnimalesModulos().add(pam);
			
			explotacionBean.setNomespecie(ex.getNomespecie());
			explotacionBean.setNomregimen(ex.getNomregimen());
			explotacionBean.getGrupoRazas().clear();
			explotacionBean.getGrupoRazas().add(grvac);
			explotacionBean.setPoliza(polizaBean);
			parametros.put(BEAN_EXPLOTACION, explotacionBean);
		}
		else {
			parametros.put(BEAN_EXPLOTACION, ex == null ? new Explotacion(polizaBean) : ex);
		}
			
		
		this.fillMapWithRequiredParams(parametros, request, ex != null ? ex.getPoliza() :
			 polizaBean);


		
		if(esModoLectura) {
			parametros.put(MODO_LECTURA,modoLectura);
		}else {
			parametros.put(MODO_LECTURA,request.getParameter(FALSE));
		}
		if(vieneDeUtilidades.compareTo("true")==0){
			parametros.put("vieneDeUtilidades","true");
		}else{
			parametros.put("vieneDeUtilidades",FALSE);
		}
		//Pasamos el resto de parametros de la accion de edicion para visualizar y preparar la pagina correctamente
		addParametersInicio(ex, parametros);
		
		if (esModoLectura && vieneDeUtilidades.compareTo("true")==0){
			try {
				baseManager.cargaCabecera(polizaBean.getIdpoliza(), request);
			} catch (Exception e) {
				logger.error("Excepcion : DatosExplotacionesController - doEditar", e);
			}
		}
		
		Poliza pol = datosExplotacionesManager.getPoliza(polizaBean.getIdpoliza());
		polizaBean.getLinea().setLineaseguroid(pol.getLinea().getLineaseguroid());
		
		parametros.put(POLIZA_BEAN, polizaBean);

		if(null!=pol && null!=pol.getIdpoliza()&& null!=pol.getClase()&& null!=pol.getLinea()
				&& null!=pol.getLinea().getLineaseguroid()){			
				Long idClase=getIdClase(pol.getClase(), pol.getLinea().getLineaseguroid());
				datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);			
		}		
		
		// Comprobar si puede tener coberturas segun lónea y modulos de la poliza
		if(null!=pol && pol.getLinea() != null && pol.getModuloPolizas() != null) {
			Set<ModuloPoliza> modsPoliza = pol.getModuloPolizas();
			boolean isCoberturas = datosExplotacionesManager.isCoberturasElegiblesNivelExplotacion(pol.getLinea().getLineaseguroid(), modsPoliza);
			parametros.put(IS_COBERTURAS,isCoberturas);
		}
		
		// Creamos las vinculaciones de las coberturas para la jsp
		List<ExplotacionCobertura> lstExpCoberturas = new ArrayList<ExplotacionCobertura>();
		if (ex.getExplotacionCoberturas() != null && ex.getExplotacionCoberturas().size()>0){
			crearVinculacionesCoberturas(ex.getExplotacionCoberturas());
			// convertimos el set a List
			lstExpCoberturas.addAll(ex.getExplotacionCoberturas());
			Collections.sort(lstExpCoberturas, new ExplotacionCoberturaComparator());
			parametros.put(LST_EXP_COBERTURAS, lstExpCoberturas);
			if(ex.getGrupoRazas().size() == 0){
				Set<ExplotacionCobertura> setExp = ex.getExplotacionCoberturas();
				explotacionBean.setExplotacionCoberturas(setExp);
			}		
			parametros.put(TIENE_COBERTURAS, true);
		}else {
			parametros.put(TIENE_COBERTURAS, false);
		}
		
		if(pol.getLinea()!=null && pol.getLinea().getLineaseguroid()!=null){
			Linea linea=datosExplotacionesManager.getLinea(pol.getLinea().getLineaseguroid());
			if (linea!=null){
				addGruposNegocioNoDepNumAnimales(parametros,linea);
				
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaLimite;
				try {
					fechaLimite = formatter.parse("01/03/2023");

					Date fechaInicioContratacion = linea.getFechaInicioContratacion();
					parametros.put("fechaInicioContratacion", fechaInicioContratacion);
					
					if(fechaInicioContratacion.compareTo(fechaLimite) < 0) {
					    parametros.put("isFechaMenor", true);
					} else {
					    parametros.put("isFechaMenor", false);
					}
				} catch (ParseException e) {
				    logger.error("Hubo un problema al parsear la fecha de inicio de contratacion de la linea", e);
				}
				
			}
		}
		
		return new ModelAndView(MODULO_EXPLOTACIONES).addAllObjects(parametros);
	}
	
	public void crearVinculacionesCoberturas(Set<ExplotacionCobertura> setExpCob) {
		for (ExplotacionCobertura cob:setExpCob) {
			String vinculada ="";
			Set<ExplotacionCoberturaVinculacion> setCobVinc = cob.getExplotacionCoberturaVinculacions();
			for (ExplotacionCoberturaVinculacion vinc :setCobVinc) {
				vinculada += vinc.getVinculacion()+"."+vinc.getFila()+"."+vinc.getVinculacionElegida()+"."+cob.getCodmodulo()+"."+vinc.getDvColumna()+"."+vinc.getDvValor()+"-";
				logger.debug("Cob id:"+cob.getId()+ " vinculadas: "+vinculada);
			}
			if (vinculada.length()>0)
				vinculada=vinculada.substring(0, vinculada.length()-1);
			cob.setVinculada(vinculada);
		}
	}
	
	public void addParametersInicio (Explotacion ex, Map<String, Object> parametros){
		if (ex.getGrupoRazas().size() == 1){
			parametros.put(REGISTRO_UNICO, "true");
			parametros.put("gr0", ex.getGrupoRazas().iterator().next());
			parametros.put(PULSADO, FALSE);
		}
		if (ex.getGrupoRazas().size() > 1){
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
			parametros.put(REGISTRO_UNICO, FALSE);
		}
	}
	
	/**
	 * Da de alta el registro de explotación y vuelve a la pantalla de datos de la explotación con los mismos datos precargados
	 * para poder replicar la explotación.
	 * @param request
	 * @param response
	 * @param explotacionBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView doGuardarReplicar(HttpServletRequest request, HttpServletResponse response, Explotacion explotacionBean) {
		
		logger.debug("doGuardarReplicar - Alta y róplica de explotaciones");
		
		ModelAndView mv = null;
		
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		
		doGuardarGrupoRaza(request, response, explotacionBean);
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

		//this.fillMapWithRequiredParams(parametros, request, explotacionBean.getPoliza());

		//recogemos coberturas de BBDD
		Explotacion exp = null;
		if(explotacionBean.getId()!=null)
			exp = datosExplotacionesManager.getExplotacion(explotacionBean.getId());

		// procesamiento coberturas
		String cobProcesadas = StringUtils.nullToString(request.getParameter(COB_PROCESADAS));
		if (cobProcesadas == "")
			cobProcesadas = StringUtils.nullToString(request.getAttribute(COB_PROCESADAS));
		Set<ExplotacionCobertura> setExpCoberturas = new HashSet<ExplotacionCobertura>();

		String cobGuardadas = StringUtils.nullToString(request.getParameter(COB_GUARDADAS));
		if (cobGuardadas == "")
			cobGuardadas = StringUtils.nullToString(request.getAttribute(COB_GUARDADAS));
		if (cobGuardadas.equals(COB_GUARDADAS)){;
			setExpCoberturas = (Set<ExplotacionCobertura>) request.getSession().getAttribute("setExpCoberturas");
			request.getSession().removeAttribute("setExpCoberturas");
		}
				
		if (!cobProcesadas.equals(COB_PROCESADAS)){
			String coberturas = request.getParameter(COBERTURAS);
			setExpCoberturas = datosExplotacionesManager.procesarCoberturas(exp, coberturas, realPath, usuario.getCodusuario());
			if (setExpCoberturas != null && setExpCoberturas.size()>0) {
				explotacionBean.setExplotacionCoberturas(setExpCoberturas);
				parametros.put(TIENE_COBERTURAS, true);
			}else {
				parametros.put(TIENE_COBERTURAS, false);
			}
			// convertimos el set a List
			List<ExplotacionCobertura> lstCob = new ArrayList<ExplotacionCobertura>();
			lstCob.addAll(setExpCoberturas);
			Collections.sort(lstCob, new ExplotacionCoberturaComparator());
			parametros.put(LST_EXP_COBERTURAS, lstCob);
		} else {
			datosExplotacionesManager.getCoberturasElegiblesExplotacion(exp, realPath, usuario.getCodusuario(), "");
		}
		
		String arrayParametros[] = {Constants.KEY_ALERTA};
		
		ParamUtils.recuperarRequest(request, parametros, arrayParametros);
		
		boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
		boolean isFalloReplicar = false;
//		//Cargamos los tipos de capital con grupo de negocio que no dependen del nómero de animales
//		addGruposNegocioNoDepNumAnimales(parametros,exp.getPoliza().getLinea());
		// Si el alta ha sido correcta
		if (isGuardadoOk) {
			try {
				Explotacion explotacionNuevo = new Explotacion();
				if (cobGuardadas.equals(COB_GUARDADAS)){
					explotacionNuevo = explotacionesManager.duplicarExplotacion(Long.valueOf(explotacionBean.getId()),setExpCoberturas);
				}else{
					explotacionNuevo = explotacionesManager.duplicarExplotacion(Long.valueOf(explotacionBean.getId()),explotacionBean.getExplotacionCoberturas());
				}
				
				parametros.put(ID_POLIZA, explotacionNuevo.getPoliza().getIdpoliza());
				parametros.put("idExplotacion", explotacionNuevo.getId());
				parametros.put("method", "doEditar");
				
				String arrayParametros2[] = {Constants.KEY_MENSAJE};
				ParamUtils.recuperarRequest(request, parametros, arrayParametros2);
				
				mv = new ModelAndView(REDIRECT_EXPLOTACIONES).addAllObjects(parametros);
			} catch (Exception e) {
				isFalloReplicar = true;
				parametros.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.replicar.KO"));
			}			
		}
		
		if(!isGuardadoOk || isFalloReplicar) {
			//Recargar para que aparezcan todos los grupos raza
			if(explotacionBean.getId()!=null){
				explotacionBean = datosExplotacionesManager.getExplotacion(explotacionBean.getId());
			}
			parametros.put(BEAN_EXPLOTACION, explotacionBean);
			parametros.put(POLIZA_BEAN, explotacionBean.getPoliza());
			parametros.put(ID_POLIZA, explotacionBean.getPoliza().getIdpoliza());
			
			// Carga los datos variables configurados para el plan y lónea de la póliza
			parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionBean.getPoliza().getLinea().getLineaseguroid()));

			String arrayParametros2[] = {BORRAR_FORM_GRUPO_RAZA};
			ParamUtils.recuperarRequest(request, parametros, arrayParametros2);
			
			// Redirige a la pantalla de datos de la explotación
			mv = new ModelAndView(MODULO_EXPLOTACIONES).addAllObjects(parametros);
		}
		return mv;
	}
	
	/**
	 * Da de alta el registro de explotación y, si no hay error durante el proceso, vuelve a la pantalla de datos de la explotación sin 
	 * datos para poder dar otro alta. Si el alta no se completara, vuelve a la pantalla de datos de la explotación con los datos que han fallado
	 * @param request
	 * @param response
	 * @param explotacionBean
	 * @return
	 */
	public ModelAndView doGuardarNuevo(HttpServletRequest request, HttpServletResponse response, Explotacion explotacionBean) {
		
		logger.debug("doGuardarNuevo - Alta y redirección a la pantalla de datos de las explotaciones");
		
		ModelAndView mv = null;
		
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		
		// Realiza el alta o modificación de la explotación
		doGuardarGrupoRaza(request, response, explotacionBean);
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

		
		//recogemos coberturas de BBDD
		Explotacion exp = null;
		if(explotacionBean.getId()!=null)
			exp = datosExplotacionesManager.getExplotacion(explotacionBean.getId());
		
		List<ExplotacionCobertura> lstCob = new ArrayList<ExplotacionCobertura>();
		// procesamiento coberturas
		String cobProcesadas = StringUtils.nullToString(request.getParameter(COB_PROCESADAS));
		if (cobProcesadas == "")
			cobProcesadas = StringUtils.nullToString(request.getAttribute(COB_PROCESADAS));
		Set<ExplotacionCobertura> setExpCoberturas = new HashSet<ExplotacionCobertura>();
		if (!cobProcesadas.equals(COB_PROCESADAS)){
			String coberturas = request.getParameter(COBERTURAS);
			setExpCoberturas = datosExplotacionesManager.procesarCoberturas(exp ,coberturas, realPath, usuario.getCodusuario());
			if (setExpCoberturas != null && setExpCoberturas.size()>0) {
				explotacionBean.setExplotacionCoberturas(setExpCoberturas);
				parametros.put(TIENE_COBERTURAS, true);
			}else {
				parametros.put(TIENE_COBERTURAS, false);
			}
			// convertimos el set a List				
			lstCob.addAll(setExpCoberturas);
			Collections.sort(lstCob, new ExplotacionCoberturaComparator());
			parametros.put(LST_EXP_COBERTURAS, lstCob);
		} else {
			datosExplotacionesManager.getCoberturasElegiblesExplotacion(exp, realPath, usuario.getCodusuario(), "");
		}
		
		String arrayParametros[] = {Constants.KEY_ALERTA, Constants.KEY_MENSAJE, BORRAR_FORM_GRUPO_RAZA};
		
		ParamUtils.recuperarRequest(request, parametros, arrayParametros);
		
		String origenLlamada = request.getParameter(ORIGEN_LLAMADA);
		if(null!= origenLlamada) {
			parametros.put(ORIGEN_LLAMADA, origenLlamada);
		}
		
		boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
		
		// Si el alta ha sido correcta
		if (isGuardadoOk) {
			mv = doPantallaAltaExplotacion(request, response, explotacionBean.getPoliza());
			
		}else{
			//Recargar para que aparezcan todos los grupos raza
			if(explotacionBean.getId()!=null){
				explotacionBean = datosExplotacionesManager.getExplotacion(explotacionBean.getId());
			}
			
			parametros.put(BEAN_EXPLOTACION, explotacionBean);
			parametros.put(POLIZA_BEAN, explotacionBean.getPoliza());
			//Carga los datos variables configurados para el plan y lónea de la póliza
			parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionBean.getPoliza().getLinea().getLineaseguroid()));
			
			//Restricciones lupas
//			Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
//			Long idClase = usuario.getClase().getId();
//			
//			if(null!=explotacionBean.getPoliza() && null!=explotacionBean.getPoliza().getIdpoliza()){
//				Poliza pol = datosExplotacionesManager.getPoliza(explotacionBean.getPoliza().getIdpoliza());
////				if(null!=pol && null!=pol.getClase()){
////					Long idClase=pol.getClase().longValue();
////					datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
////				}
//				if(null!=pol && null!=pol.getIdpoliza()&& null!=pol.getClase()&& null!=pol.getLinea()
//						&& null!=pol.getLinea().getLineaseguroid()){			
//						Long idClase=getIdClase(pol.getClase(), pol.getLinea().getLineaseguroid());
//						datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);			
//				}		
//			}
			
			


			// Redirige a la pantalla de datos de la explotación
			mv = new ModelAndView(MODULO_EXPLOTACIONES).addAllObjects(parametros);
		}
		
		return mv;
	}

	/**
	 * Da de alta el registro de explotación y, si no hay error durante el proceso, vuelve a la pantalla de listado de explotaciones.
	 * Si el alta no se completara, vuelve a la pantalla de datos de la explotación con los datos que han fallado
	 * @param request
	 * @param response
	 * @param explotacionBean
	 * @return
	 */
	public ModelAndView doGuardarVolver(HttpServletRequest request, HttpServletResponse response, Explotacion explotacionBean) {
		
		logger.debug("doGuardarNuevo - Alta de explotación y volver al listado");
		
		ModelAndView mv = null;
		
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		
		doGuardarGrupoRaza(request, response, explotacionBean);
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

		
		//recogemos coberturas de BBDD
		Explotacion exp = null;
		if(explotacionBean.getId()!=null)
			exp = datosExplotacionesManager.getExplotacion(explotacionBean.getId());
		
		List<ExplotacionCobertura> lstCob = new ArrayList<ExplotacionCobertura>();
		// procesamiento coberturas
		String cobProcesadas = StringUtils.nullToString(request.getParameter(COB_PROCESADAS));
		if (cobProcesadas == "")
			cobProcesadas = StringUtils.nullToString(request.getAttribute(COB_PROCESADAS));
		if (!cobProcesadas.equals(COB_PROCESADAS)){
			String coberturas = request.getParameter(COBERTURAS);
			Set<ExplotacionCobertura> setExpCoberturas = datosExplotacionesManager.procesarCoberturas(exp,coberturas, realPath, usuario.getCodusuario());
			if (setExpCoberturas != null && setExpCoberturas.size()>0) {
				explotacionBean.setExplotacionCoberturas(setExpCoberturas);
				parametros.put(TIENE_COBERTURAS, true);
			}else {
				parametros.put(TIENE_COBERTURAS, false);
			}
			// convertimos el set a List				
			lstCob.addAll(setExpCoberturas);
			Collections.sort(lstCob, new ExplotacionCoberturaComparator());
			parametros.put(LST_EXP_COBERTURAS, lstCob);
		} else {
			datosExplotacionesManager.getCoberturasElegiblesExplotacion(exp, realPath, usuario.getCodusuario(), "");
		}
		
		String arrayParametros[] = {Constants.KEY_ALERTA, Constants.KEY_MENSAJE, BORRAR_FORM_GRUPO_RAZA};
		
		ParamUtils.recuperarRequest(request, parametros, arrayParametros);
		
		//Explotacion exp = datosExplotacionesManager.getExplotacion(explotacionBean.getId());
			
		// Realiza el alta o modificación de la explotación
		//Map<String, Object> parametros = altaModificacionExplotacion(request, exp);
			
		String origenLlamada = request.getParameter(ORIGEN_LLAMADA);
		if(null!= origenLlamada) {
			parametros.put(ORIGEN_LLAMADA, origenLlamada);
		}
		
		boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
		
		// Si el alta ha sido correcta
		if (isGuardadoOk) {
			// Parómetros para la pantalla de datos de la explotación
			parametros.put(ID_POLIZA, explotacionBean.getPoliza().getIdpoliza());
			parametros.put("linea.lineaseguroid", explotacionBean.getPoliza().getLinea().getLineaseguroid());
			// Redirige a la pantalla de listado de explotaciones
			mv = new ModelAndView("redirect:/listadoExplotaciones.html").addAllObjects(parametros);
		}
		else {
			//Recargar para que aparezcan todos los grupos raza
			if(explotacionBean.getId()!=null){
				explotacionBean = datosExplotacionesManager.getExplotacion(explotacionBean.getId());
			}
			
			parametros.put(BEAN_EXPLOTACION, explotacionBean);
			if (explotacionBean.getPoliza() != null)
				parametros.put(POLIZA_BEAN, explotacionBean.getPoliza());
			//Carga los datos variables configurados para el plan y lónea de la póliza
			parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionBean.getPoliza().getLinea().getLineaseguroid()));
			
			//Restricciones lupas
			if(null!=explotacionBean.getPoliza() && null!=explotacionBean.getPoliza().getIdpoliza()){
				Poliza pol = datosExplotacionesManager.getPoliza(explotacionBean.getPoliza().getIdpoliza());
				if(null!=pol && null!=pol.getClase()){
					Long idClase=pol.getClase().longValue();
					datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
				}
			}
			

			// Redirige a la pantalla de datos de la explotación
			mv = new ModelAndView(MODULO_EXPLOTACIONES).addAllObjects(parametros);
		}
		
		
		
		return mv;
	}
	
	/**
	 * Calcula el precio correspondiente a la explotacion y lo escribe en el response
	 * @param request
	 * @param response
	 * @param explotacionBean
	 */
	public void doCalcularPrecio (HttpServletRequest request, HttpServletResponse response) {	
		// Crea el objeto Explotacion a partir de los datos recibidos en request
		Explotacion explotacion = getExplotacionFromRequest(request);
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		List<ExplotacionCobertura> lstCob = new ArrayList<ExplotacionCobertura>();
		
		if (explotacion != null) {
			try {
				List<PrecioAnimalesModulo> lstPrecioAnimalesMod =  this.datosExplotacionesManager.calcularPrecio(explotacion);
				PrecioAnimalesModulo precioAni = new PrecioAnimalesModulo();
				precioAni.setCodmodulo("1");
				precioAni.setPrecioMax(new BigDecimal(2));
				precioAni.setPrecioMin(new BigDecimal(3));
				lstPrecioAnimalesMod.add(precioAni);
				JSONArray jsonPrecios = new JSONArray(lstPrecioAnimalesMod);
				Set<PrecioAnimalesModulo> setPrAnimalsMod = new HashSet<PrecioAnimalesModulo>();
				for (PrecioAnimalesModulo precioAnimalesModulo : lstPrecioAnimalesMod) {
					setPrAnimalsMod.add(precioAnimalesModulo);
				}
				explotacion.getGrupoRazas().iterator().next().setPrecioAnimalesModulos(setPrAnimalsMod);
				// COB EXPLOTACION	
				Boolean isCoberturas = (new Boolean(request.getParameter(IS_COBERTURAS)));
				if (isCoberturas) {			
					String realPath = this.getServletContext().getRealPath(WEB_INF);
					String cobExistentes = (StringUtils.nullToString(request.getParameter("cobExistentes")));
					String idExplotacion = (StringUtils.nullToString(request.getParameter("idExplotacion")));
					if (!idExplotacion.equals(""))
						explotacion.setId(Long.parseLong(idExplotacion));
				    lstCob = datosExplotacionesManager.getCoberturasElegiblesExplotacion(explotacion,realPath,usuario.getCodusuario(),cobExistentes);
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
				// Calcula el precio de la explotacion y lo vuelca en la respuesta para ser tratado en la pantalla
				this.getWriterJSON(response, resTotal);
			} catch (PrecioGanadoException e) {
				logger.info("No todos los precios encontrados son iguales");
				JSONObject json = DatosExplotacionesUtil.respuestaErrorPrecio(bundle.getString("mensaje.datosExplotacion.precioGanado.KO"));
				this.getWriterJSON(response, json);
			} catch (Exception e){
				logger.error("Ha habido un error al calcular el precio", e);
			}
		}
	}

	private Map<String, Object> altaModificacionExplotacionGr(HttpServletRequest request, Explotacion explotacionBean) { 
		//Map<String, Object> result = new HashMap<String, Object>();
		// Si se estó editando se borran los datos variables previos, para que no se dupliquen al guardar el objeto de explotación
		/*if (explotacionBean != null && explotacionBean.getId() != null) { // Se estó editando si la explotación tiene id
			this.datosExplotacionesManager.borrarListaDatosVariables(explotacionBean,  request.getParameter("gruporazaid"));
		}*/
		
		// Obtiene la lista de datos variables informados en la pantalla y los carga en el objeto de explotacion
		//cargarListaDatosVariablesFromRequest(request, explotacionBean);
		
		// Guarda en BBDD el objeto explotación
		return this.datosExplotacionesManager.alta(explotacionBean);

	}
	
	/**
	 * Da de alta el registro de explotación y vuelve a la pantalla de datos de la explotación con los mismos datos precargados
	 * para poder replicar la explotación.
	 * @param request
	 * @param response
	 * @param explotacionBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView doGuardarGrupoRaza(HttpServletRequest request, HttpServletResponse response, Explotacion explotacionBean) {
		
		ModelAndView mv = null;
		
		String idexplotacion;
		Explotacion expBbDd = new Explotacion();		
		
		List<String> names = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		Long codgruporaza = null;
		BigDecimal codtipocapital = null;
		Long codtipoanimal = null;
		Long numanimales = null;
		String codmodulo = null;
		BigDecimal precioAnimalesModulos = null;
		Long idClase=null;
		Integer botonGuardarGR=null;
		
		logger.debug("doGuardarGrupoRaza - Alta de explotaciones");
		
		String accion = request.getParameter("accion");
		
		Boolean isCoberturas = (new Boolean(request.getParameter(IS_COBERTURAS)));
		
		if(null!=request.getParameter(BOTON_GUARDAR_GR) && !request.getParameter(BOTON_GUARDAR_GR).isEmpty()){
			botonGuardarGR=new Integer(request.getParameter(BOTON_GUARDAR_GR));
		}
		
		//Si es borrar, no se cargan porque no hace falta
		if (!OPERACION_GRUPO_RAZA_BORRAR.equals(accion) && !OPERACION_IDENTIFICATIVOS_GUARDAR.equals(accion)){
			codgruporaza = new Long(request.getParameter("grupoRazas[0].codgruporaza"));
			codtipocapital = new BigDecimal(request.getParameter("grupoRazas[0].codtipocapital"));
			codtipoanimal = new Long(request.getParameter("grupoRazas[0].codtipoanimal"));
			numanimales = new Long(request.getParameter("grupoRazas[0].numanimales"));
			codmodulo = request.getParameter("grupoRazas[0].precioAnimalesModulos[0].codmodulo");
			precioAnimalesModulos = new BigDecimal(request.getParameter("grupoRazas[0].precioAnimalesModulos[0].precio"));
		}
		
		Map<String, String[]> parameters = request.getParameterMap();
		
		for(String parameter : parameters.keySet()) {
		    if(parameter.startsWith("dvCpto_")) {
		        names.add(parameter.split("_")[1]);
		        values.add(parameters.get(parameter)[0]);
		    }
		}
		
		
		if(null!=explotacionBean.getPoliza()&& null!=explotacionBean.getPoliza().getIdpoliza()){
			Poliza pol = datosExplotacionesManager.getPoliza(explotacionBean.getPoliza().getIdpoliza());
			if(null!=pol && null!=pol.getIdpoliza()&& null!=pol.getClase()&& null!=pol.getLinea()
					&& null!=pol.getLinea().getLineaseguroid()){			
					idClase=getIdClase(pol.getClase(), pol.getLinea().getLineaseguroid());
			}		
		}
		
		if (request.getParameter("id") != null
				&& !request.getParameter("id").isEmpty()) {
			idexplotacion = request.getParameter("id");
			expBbDd = datosExplotacionesManager.getExplotacion(new Long(idexplotacion));
		}
				
		if (OPERACION_GRUPO_RAZA_EDITAR.equals(accion)){// Realiza la edición del grupo raza
			mv=doGuardarGrupoRazaAccionEditar(request,  explotacionBean, expBbDd, isCoberturas, idClase,
					codgruporaza, codtipocapital, codtipoanimal, numanimales, codmodulo, precioAnimalesModulos,
					names, values,  botonGuardarGR);					
		}
		else if (OPERACION_GRUPO_RAZA_BORRAR.equals(accion)){// Realiza el borrado del grupo raza
			mv=doGuardarGrupoRazaAccionBorrar(request,  explotacionBean, expBbDd, isCoberturas, idClase);
		}
		else if (OPERACION_IDENTIFICATIVOS_GUARDAR.equals(accion)){
			mv=doGuardarGrupoRazaDatosIdentificativos(request,  explotacionBean, expBbDd);
		}
		else {
			
			// Realiza el alta del grupo raza
			if (explotacionBean != null && explotacionBean.getId() != null) {
				mv=doGuardarGrupoRazaExisteExplotacion(request,explotacionBean, expBbDd, idClase, codgruporaza, 
						codtipocapital, codtipoanimal, numanimales, precioAnimalesModulos,
						codmodulo, isCoberturas, botonGuardarGR);
				
			} else {
				// Realiza el alta de la explotación y del grupo raza
				mv=doGuardarGrupoRazaNoExisteExplotacion(request, explotacionBean, isCoberturas, 
						idClase, botonGuardarGR);
			}
		}
	
		// Redirige a la pantalla de datos de la explotación
		return mv;
	}
	
	@SuppressWarnings("unchecked")
	public void getDatosVariables (GrupoRaza grupoRaza, HttpServletRequest request){
		Map<String, Object> map = request.getParameterMap();
		String[] aux; ArrayList<String> one = new ArrayList<String>(); ArrayList<String> two = new ArrayList<String>();
		
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
			if(two.get(i) != null && !two.get(i).equals("")){
				grupoRaza.getDatosVariables().add(new DatosVariable(null, grupoRaza, null, null, new Integer(one.get(i)), two.get(i)));
			}
		}
	}
	
	public void addParametersBorrado (Explotacion ex, Map<String, Object> parametros, Set<GrupoRaza> gr){
		if (gr.size() == 1){
			parametros.put(REGISTRO_UNICO, "true");
			parametros.put("gr0", ex.getGrupoRazas().iterator().next());
			parametros.put(PULSADO, "true");
		}
		if (gr.size() > 1){
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
			parametros.put(REGISTRO_UNICO, FALSE);
			parametros.put(PULSADO, "true");
		}
		if (gr.size() == 0){
			parametros.put(PULSADO, "true");
		}
	}
	
	/**
	 * Obtiene la lista de datos variables informados en la pantalla y la asocia al objeto explotación
	 * @param request
	 * @param explotacionBean
	 */
	@SuppressWarnings("unchecked")
	private void cargarListaDatosVariablesFromRequest (HttpServletRequest request, Explotacion explotacionBean) {
		
		try {
			Iterator<Map.Entry<String,String[]>> it = request.getParameterMap().entrySet().iterator();
			
			Set<DatosVariable> listaDV = new HashSet<DatosVariable>();
			
			for (GrupoRaza gr : explotacionBean.getGrupoRazas()) {
				while (it.hasNext()) {
					Map.Entry<String,String[]> entry = it.next();
					
					String key = entry.getKey();
					
					if (key != null && key.startsWith(PREFIJO_DV)) {
						String[] value         = entry.getValue();
						
						if (value.length > 0 && !StringUtils.isNullOrEmpty(value[0])) {
							DatosVariable dv = new DatosVariable(null, gr, null, null, Integer.valueOf(key.substring(PREFIJO_DV.length())), value[0]);
							listaDV.add(dv);
							
							logger.debug("DV de explotación cargado: " + dv.getCodconcepto() + " = " + dv.getValor());
						}
					}
				}
				
				gr.setDatosVariables(listaDV);
			}
		} catch (Exception e) {
			logger.error("Error al cargar la lista de datos variables en la explotación", e);
		}
		
	}

	/**
	 * Crea un objeto Explotación a partir de los parametros informados en la request
	 * @param request
	 * @return Objeto Explotacion o nulo si ha habido errores en el proceso
	 */
	private Explotacion getExplotacionFromRequest(HttpServletRequest request) {

		//Recoge los parómetros de la explotación necesarios para el cólculo y crea el objeto que los encapsula
		Explotacion e = new Explotacion();
		
		try {
			
			e.getPoliza().setIdpoliza(new Long (request.getParameter(ID_POLIZA))); // Idpoliza (para obtener plan, lónea y módulos)
			e.getTermino().getId().setCodprovincia(new BigDecimal (request.getParameter("codProvincia"))); // Provincia
			e.getTermino().getId().setCodcomarca(new BigDecimal (request.getParameter("codComarca"))); // Comarca
			e.getTermino().getId().setCodtermino(new BigDecimal (request.getParameter("codTermino"))); // Tórmino
			e.getTermino().getId().setSubtermino(new Character (request.getParameter("subtermino").charAt(0))); // Subtórmino
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
			
			Long numAnimales = (new Long (request.getParameter("numanimales")));
			GrupoRaza gr = new GrupoRaza();
			gr.setCodgruporaza(new Long (request.getParameter("grupoRaza")));
			gr.setCodtipoanimal(new Long (request.getParameter("tipoAnimal")));
			gr.setCodtipocapital(new BigDecimal (request.getParameter("tipoCapital")));
			gr.setNumanimales(numAnimales);
			Set<GrupoRaza> setGR = new HashSet<GrupoRaza>(0);
			setGR.add(gr);
			e.setGrupoRazas(setGR);
			
			// Carga los datos variables
			cargarListaDatosVariablesFromRequest (request, e);

			return e;
		} catch (Exception ex) {
			logger.error("Error al crear el objeto Explotacion con los datos de la request", ex);
			return null;
		}
	}
	
	
	/**
	 * Graba (si pasa las validaciones) los datos identificativos de una explotación
	 * @param expFuente Viene del formulario que se rellena
	 * @param expDestino Explotación de anexo a grabar en base de datos
	 * @return
	 */
	private Map<String, Object> guardarDatosIdentificativos(Explotacion expFuente, Explotacion expDestino){

		Map<String, Object> mapaErrores = datosExplotacionesManager.validacionesPreviasDatosIdentificativos(expFuente);
		
		if(!mapaErrores.containsKey(Constants.KEY_ALERTA)){
			
			BigDecimal codProvincia = expFuente.getTermino().getId().getCodprovincia();
			BigDecimal codComarca = expFuente.getTermino().getId().getCodcomarca();
			BigDecimal codTermino = expFuente.getTermino().getId().getCodtermino();
			Character subtermino = expFuente.getTermino().getId().getSubtermino();
			
			Termino termino = datosExplotacionesManager.obtenerTermino(codProvincia, codComarca, codTermino, subtermino);
			expDestino.setTermino(termino);
			
			expDestino.setLatitud(expFuente.getLatitud());
			expDestino.setLongitud(expFuente.getLongitud());
			
			expDestino.setRega(expFuente.getRega());
			expDestino.setSigla(expFuente.getSigla());
			
			expDestino.setSubexplotacion(expFuente.getSubexplotacion());

			expDestino.setEspecie(expFuente.getEspecie());
			expDestino.setRegimen(expFuente.getRegimen());
		}
		
		return mapaErrores;
	}
	
	private ModelAndView doGuardarGrupoRazaAccionEditar(
			HttpServletRequest request, Explotacion explotacionBean, Explotacion expBbDd,
			Boolean isCoberturas, Long idClase, Long codgruporaza, BigDecimal codtipocapital,
			Long codtipoanimal, Long numanimales, String codmodulo, BigDecimal precioAnimalesModulos,
			List<String> names, List<String> values, Integer botonGuardarGR ) {
		
		ModelAndView mv = null;
		Set<GrupoRaza> gr = null;
		Set<DatosVariable> datosVariables = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		Long idgruporaza;
		
		
		String grId = request.getParameter("gruporazaid");
		//if(request.getParameter("gruporazaid") != ""){
		if(!grId.equals("")){
			idgruporaza = new Long (request.getParameter("gruporazaid"));
		}
		else{
			GrupoRaza grFirst = new GrupoRaza();
			Iterator<GrupoRaza> itGrupoRazaFirst = explotacionBean.getGrupoRazas().iterator();
			while(itGrupoRazaFirst.hasNext()){
				grFirst = itGrupoRazaFirst.next();
				break;
			}
			idgruporaza = grFirst.getId();
		}
		//Long idgruporaza = new Long (request.getParameter("gruporazaid"));
		
		
		Map<String, Object> mapErroresDatosIdent = guardarDatosIdentificativos(explotacionBean, expBbDd);
		List<ExplotacionCobertura> lstCob = new ArrayList<ExplotacionCobertura>();
		if(!mapErroresDatosIdent.containsKey(Constants.KEY_ALERTA)){
			gr = expBbDd.getGrupoRazas();
			
			Iterator<GrupoRaza> itGrupoRazaCol = gr.iterator();
			
			boolean repetido = false;
			GrupoRaza grEditar = null;
			
			while(itGrupoRazaCol.hasNext() && !repetido){
				GrupoRaza grAux = itGrupoRazaCol.next();
				
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
				
				for(PrecioAnimalesModulo pam : grEditar.getPrecioAnimalesModulos()){
					if (pam.getGrupoRaza().equals(grEditar)){
						pam.setPrecio(precioAnimalesModulos);
					}
				}
				
				datosVariables = grEditar.getDatosVariables();
				ArrayList<String> valores = new ArrayList<String>();
				
				for(DatosVariable dv : datosVariables){
					String insert = dv.getCodconcepto() + "_" + dv.getValor();
					valores.add(insert);
				}

				if(!datosVariables.isEmpty() && datosVariables != null){
					datosVariables.clear();
				}
				
				for(int i=0; i<names.size(); i++){
					if(!values.get(i).isEmpty()){
						datosVariables.add(new DatosVariable(null, grEditar, null, null, new Integer(names.get(i)), values.get(i)));
					}
				}
				
				grEditar.setDatosVariables(datosVariables);
				datosExplotacionesManager.borrarListaDatosVariables(expBbDd,  grEditar.getId().toString());
				expBbDd.setGrupoRazas(gr);
				
				// procesamiento coberturas
				String coberturas = request.getParameter(COBERTURAS);
				Set<ExplotacionCobertura> setExpCoberturas = datosExplotacionesManager.procesarCoberturas(expBbDd, coberturas);
				request.setAttribute(COB_PROCESADAS, COB_PROCESADAS);
				if (setExpCoberturas != null && setExpCoberturas.size()>0) {
					explotacionBean.setExplotacionCoberturas(setExpCoberturas);
					parametros.put(TIENE_COBERTURAS, true);
				}else {
					parametros.put(TIENE_COBERTURAS, false);
				}
				;
				// convertimos el set a List				
				lstCob.addAll(setExpCoberturas);
				Collections.sort(lstCob, new ExplotacionCoberturaComparator());
				parametros.put(LST_EXP_COBERTURAS, lstCob);
								
				parametros = altaModificacionExplotacionGr(request, expBbDd);
				
				//Adaptación para versión request
				if(parametros.containsKey(Constants.KEY_MENSAJE)){
					request.setAttribute(Constants.KEY_MENSAJE, parametros.get(Constants.KEY_MENSAJE));
				}
				if(parametros.containsKey(Constants.KEY_ALERTA)){
					request.setAttribute(Constants.KEY_ALERTA, parametros.get(Constants.KEY_ALERTA));
				} 
				
				parametros.put(POLIZA_BEAN, explotacionBean.getPoliza());
				parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(expBbDd.getPoliza().getLinea().getLineaseguroid()));
				//parametros.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.datosExplotacion.altaOK"));
			
			}else{
				parametros.put(Constants.KEY_ALERTA, bundle.getString(MENSAJE_EXPLOTACION_KO));
				request.setAttribute(Constants.KEY_ALERTA, bundle.getString(MENSAJE_EXPLOTACION_KO));
				
				for(Iterator<Entry<String, Object>> it = parametros.entrySet().iterator(); it.hasNext(); ) {
				      Entry<String, Object> entry = it.next();
				      if(entry.getKey().equals(Constants.KEY_MENSAJE)) {
				        it.remove();
				      }
				}
			}
		}else{
			parametros.put(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
			request.setAttribute(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
		}
	
		
		if(null!= botonGuardarGR && botonGuardarGR.compareTo(new Integer("1"))==0){
			parametros.put(BORRAR_FORM_GRUPO_RAZA, FALSE);
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, FALSE);
			parametros.put(ID_GUARDAR_REPLICAR, idgruporaza);
		}else{
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, "true");
		}
		
		

		
		parametros.put(ID_POL, request.getParameter("poliza.idpoliza"));
		parametros.put(ID_EXP, expBbDd.getId());
		parametros.put(IS_COBERTURAS, isCoberturas);
		parametros.put(LST_EXP_COBERTURAS, lstCob);
		if(null!=idClase)datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		mv = new ModelAndView(REDIRECT_EXPLOTACIONES).addAllObjects(parametros);
		return mv;
	}
	
	private ModelAndView doGuardarGrupoRazaAccionBorrar(
			HttpServletRequest request, Explotacion explotacionBean, Explotacion expBbDd,
			Boolean isCoberturas, Long idClase) {
		ModelAndView mv=null;
		Set<GrupoRaza> gr = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("perfilUsuario", usuario.getTipousuario());
		
		String idgruporaza = request.getParameter("gruporazaid");
		
		
		gr = expBbDd.getGrupoRazas();
		
		GrupoRaza dgr = null;
		for (GrupoRaza g : gr){
			if (g.getId().toString().equals(idgruporaza)){
				dgr = g;
			}
		}
		gr.remove(dgr);

		datosExplotacionesManager.borrarGrupoRaza(expBbDd, idgruporaza);
		
		parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionBean.getPoliza().getLinea().getLineaseguroid()));
		
		if(gr.size() == 0){
			GrupoRaza grvac = new GrupoRaza(expBbDd, null, null, null, null, null, null);
			PrecioAnimalesModulo pam = new PrecioAnimalesModulo(null, grvac, null);
			grvac.getPrecioAnimalesModulos().add(pam);
			
			explotacionBean.getGrupoRazas().clear();
			explotacionBean.getGrupoRazas().add(grvac);
		}
		else{
			explotacionBean.setGrupoRazas(gr);
		}

		addParametersBorrado(expBbDd, parametros, gr);
		
		// procesamiento coberturas
		String coberturas = request.getParameter(COBERTURAS);
		Set<ExplotacionCobertura> setExpCoberturas = datosExplotacionesManager.procesarCoberturas(expBbDd, coberturas);
		request.setAttribute(COB_PROCESADAS, COB_PROCESADAS);
		if (setExpCoberturas != null && setExpCoberturas.size()>0) {
			explotacionBean.setExplotacionCoberturas(setExpCoberturas);
			parametros.put(TIENE_COBERTURAS, true);
		}else {
			parametros.put(TIENE_COBERTURAS, false);
		}
		// convertimos el set a List
		List<ExplotacionCobertura> lstExpCoberturas = new ArrayList<ExplotacionCobertura>();
		lstExpCoberturas.addAll(setExpCoberturas);
		Collections.sort(lstExpCoberturas, new ExplotacionCoberturaComparator());
		parametros.put(LST_EXP_COBERTURAS, lstExpCoberturas);
		
		parametros.put(BEAN_EXPLOTACION, explotacionBean);
		parametros.put(POLIZA_BEAN, explotacionBean.getPoliza());
		parametros.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.datosExplotacion.altaOK"));
		parametros.put(IS_COBERTURAS, isCoberturas);
		if(null!=idClase)datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		addGruposNegocioNoDepNumAnimales(parametros,expBbDd.getPoliza().getLinea());
		
		
		this.fillMapWithRequiredParams(parametros, request, explotacionBean.getPoliza());

		
		mv = new ModelAndView(MODULO_EXPLOTACIONES).addAllObjects(parametros);
		return mv;
	}
	
	private ModelAndView doGuardarGrupoRazaDatosIdentificativos(HttpServletRequest request,  
			Explotacion explotacionBean, Explotacion expBbDd) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Map<String, Object> mapErroresDatosIdent = guardarDatosIdentificativos(explotacionBean, expBbDd);
		
		if(!mapErroresDatosIdent.containsKey(Constants.KEY_ALERTA)){
			parametros = altaModificacionExplotacionGr(request, expBbDd);
			
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
			Explotacion explotacionBean, Explotacion expBbDd, Long idClase, Long codgruporaza, 
			BigDecimal codtipocapital, Long codtipoanimal, Long numanimales, BigDecimal precioAnimalesModulos,
			String codModulo, Boolean isCoberturas, Integer botonGuardarGR){
		ModelAndView mv=null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		Boolean nuevo = false;
		Set<GrupoRaza> gr = null;
		GrupoRaza ngr = null;	
		Long idGuardarReplicar=null; // id del nuevo grupo de raza. Solo lo alimentamos cuando se ha puulsado el botón de Guardar y replicar el GR
		
		gr = expBbDd.getGrupoRazas();
		
		if(gr.size() == 0){
			//Poliza pol = datosExplotacionesManager.getPoliza(explotacionBean.getPoliza().getIdpoliza());
			ngr = new GrupoRaza(expBbDd, codgruporaza, codtipocapital,
					codtipoanimal, numanimales, codModulo,
					precioAnimalesModulos);
			nuevo = true;
			getDatosVariables(ngr, request);
		}
		for (GrupoRaza gri : gr) {
			if (gri.getCodgruporaza().equals(codgruporaza)
					&& gri.getCodtipocapital().equals(codtipocapital)
					&& gri.getCodtipoanimal().equals(codtipoanimal)) {
				
				nuevo = false;
				parametros.put(BEAN_EXPLOTACION, expBbDd);
				parametros.put(POLIZA_BEAN, explotacionBean.getPoliza());
				parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionBean.getPoliza().getLinea().getLineaseguroid()));
				parametros.put(Constants.KEY_ALERTA, bundle.getString(MENSAJE_EXPLOTACION_KO));
				request.setAttribute(Constants.KEY_ALERTA, bundle.getString(MENSAJE_EXPLOTACION_KO));
				parametros.put(ID_POL, request.getParameter("poliza.idpoliza"));
				parametros.put(ID_EXP, expBbDd.getId());
				break;
			} else {
				ngr = new GrupoRaza(expBbDd, codgruporaza, codtipocapital,
						codtipoanimal, numanimales, codModulo,
						precioAnimalesModulos);
				nuevo = true;
				
				getDatosVariables(ngr, request);
			}
		}

		Map<String, Object> mapErroresDatosIdent = guardarDatosIdentificativos(explotacionBean, expBbDd);

		if(!mapErroresDatosIdent.containsKey(Constants.KEY_ALERTA)){
			if(nuevo){
				gr.add(ngr);
				expBbDd.setGrupoRazas(gr);

				parametros = altaModificacionExplotacionGr(request, expBbDd);
				
				// procesamiento coberturas
				String coberturas = request.getParameter(COBERTURAS);
				Set<ExplotacionCobertura> setExpCoberturas = datosExplotacionesManager.procesarCoberturas(expBbDd,coberturas);
				request.setAttribute(COB_PROCESADAS, COB_PROCESADAS);
				if (setExpCoberturas != null && setExpCoberturas.size()>0) {
					explotacionBean.setExplotacionCoberturas(setExpCoberturas);
					parametros.put(TIENE_COBERTURAS, true);
				}else {
					parametros.put(TIENE_COBERTURAS, false);
				}
				
				//Adaptación para versión request
				if(parametros.containsKey(Constants.KEY_MENSAJE)){
					request.setAttribute(Constants.KEY_MENSAJE, parametros.get(Constants.KEY_MENSAJE));
				}
				if(parametros.containsKey(Constants.KEY_ALERTA)){
					request.setAttribute(Constants.KEY_ALERTA, parametros.get(Constants.KEY_ALERTA));
				}
				
				parametros.put(POLIZA_BEAN, explotacionBean.getPoliza());
				parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager.cargarDatosVariables(explotacionBean.getPoliza().getLinea().getLineaseguroid()));
				parametros.put(ID_POL, request.getParameter("poliza.idpoliza"));
				parametros.put(ID_EXP, expBbDd.getId());
			}
		}else{
			parametros.put(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
			request.setAttribute(Constants.KEY_ALERTA, mapErroresDatosIdent.get(Constants.KEY_ALERTA));
		}
		
		
		parametros.put(IS_COBERTURAS, isCoberturas);
		parametros.put(BOTON_GUARDAR_GR, botonGuardarGR);
		
		if(null!= botonGuardarGR && botonGuardarGR.compareTo(new Integer("1"))==0){
			if(null!=ngr && null!= ngr.getId()){
				idGuardarReplicar=ngr.getId();
			}else{
				if(null!=request.getParameter("gruporazaOrginal")){
					idGuardarReplicar= new Long(request.getParameter("gruporazaOrginal"));
				}
				
			}
			parametros.put(BORRAR_FORM_GRUPO_RAZA, FALSE);
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, FALSE);
		}else{
			parametros.put(BORRAR_FORM_GRUPO_RAZA, "true");
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, "true");
		}
		parametros.put(ID_GUARDAR_REPLICAR, idGuardarReplicar);
		
		if(null!=idClase)datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		mv = new ModelAndView(REDIRECT_EXPLOTACIONES).addAllObjects(parametros);
		return mv;
	}
	
	private ModelAndView doGuardarGrupoRazaNoExisteExplotacion(HttpServletRequest request,  
			Explotacion explotacionBean, Boolean isCoberturas, Long idClase, Integer botonGuardarGR){
		ModelAndView mv=null;
		Map<String, Object> parametros = altaModificacionExplotacionGr(
				request, explotacionBean);
		



		boolean isGuardadoOk = !parametros.containsKey(Constants.KEY_ALERTA);
		Long idGuardarReplicar=null; // id del nuevo grupo de raza. Solo lo alimentamos cuando se ha puulsado el botón de Guardar y replicar el GR
		
		//Si se ha realizado bien el alta
		parametros.put(POLIZA_BEAN, explotacionBean.getPoliza());
		
		
		// procesamiento coberturas
		String coberturas = request.getParameter(COBERTURAS);
		Set<ExplotacionCobertura> setExpCoberturas = datosExplotacionesManager.procesarCoberturas(explotacionBean,coberturas);
		request.setAttribute(COB_PROCESADAS, COB_PROCESADAS);
		request.setAttribute(COB_GUARDADAS, COB_GUARDADAS);
		request.getSession().setAttribute("setExpCoberturas", setExpCoberturas);
		if (setExpCoberturas != null && setExpCoberturas.size()>0) {
			//explotacionBean.setExplotacionCoberturas(setExpCoberturas);
			parametros.put(TIENE_COBERTURAS, true);
		}else {
			parametros.put(TIENE_COBERTURAS, false);
		}
		// convertimos el set a List
		List<ExplotacionCobertura> lstCob = new ArrayList<ExplotacionCobertura>();
		lstCob.addAll(setExpCoberturas);
		Collections.sort(lstCob, new ExplotacionCoberturaComparator());
		parametros.put(LST_EXP_COBERTURAS, lstCob);
		
		// Carga los datos variables configurados para el plan y lónea de la póliza
		parametros.put(Constants.KEY_DATOS_VARIABLES, datosExplotacionesManager
				.cargarDatosVariables(explotacionBean.getPoliza()
						.getLinea().getLineaseguroid()));

		parametros.put(ID_POL, explotacionBean.getPoliza().getIdpoliza());
		GrupoRaza grAux = explotacionBean.getGrupoRazas().iterator().next();
		getDatosVariables(grAux, request);

		explotacionBean.getGrupoRazas().add(grAux);
		parametros.put(ES_ALTA, "true");
		if(null!=idClase)datosExplotacionesManager.getListaCodigosLupasExplotaciones(idClase, parametros);
		parametros.put(IS_COBERTURAS, isCoberturas);
		parametros.put(BOTON_GUARDAR_GR, botonGuardarGR);
		
		if(null!= botonGuardarGR && botonGuardarGR.compareTo(new Integer("1"))==0){
			idGuardarReplicar=grAux.getId();
			parametros.put(BORRAR_FORM_GRUPO_RAZA, FALSE);
			request.setAttribute(BORRAR_FORM_GRUPO_RAZA, FALSE);
		}
		parametros.put(ID_GUARDAR_REPLICAR, idGuardarReplicar);
		
		this.fillMapWithRequiredParams(parametros, request, explotacionBean.getPoliza());

		
		if(isGuardadoOk){
			parametros.put(ID_EXP, explotacionBean.getId());
			request.setAttribute(Constants.KEY_MENSAJE, parametros.get(Constants.KEY_MENSAJE));
			mv = new ModelAndView(REDIRECT_EXPLOTACIONES).addAllObjects(parametros);
		}else{
			request.setAttribute(Constants.KEY_ALERTA, parametros.get(Constants.KEY_ALERTA));
			mv = new ModelAndView(MODULO_EXPLOTACIONES).addAllObjects(parametros);
		}

		return mv;
	}
	
	/**
	 * Setter para Spring
	 * @param datosExplotacionesManager
	 */
	public void setDatosExplotacionesManager(IDatosExplotacionesManager datosExplotacionesManager) {
		this.datosExplotacionesManager = datosExplotacionesManager;
	}

	public void setExplotacionesManager(ExplotacionesManager explotacionesManager) {
		this.explotacionesManager = explotacionesManager;
	}

	public void setBaseManager(IBaseManager baseManager) {
		this.baseManager = baseManager;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}
}