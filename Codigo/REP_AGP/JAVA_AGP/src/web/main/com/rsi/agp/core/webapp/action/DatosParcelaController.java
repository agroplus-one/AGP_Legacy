package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.IDatosParcelaManager;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.SigpacManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.ParcelaUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.ParcelasCoberturasNew;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.LocalCultVarVO;
import com.rsi.agp.vo.PantallaConfigurableVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.PreciosProduccionesVO;
import com.rsi.agp.vo.ProduccionVO;
import com.rsi.agp.vo.RiesgoVO;
import com.rsi.agp.vo.SigpacVO;

public class DatosParcelaController extends BaseMultiActionController  {
	
	private static final String LIST_COD_MODULOS = "listCodModulos";
	private static final String PARCELA = "parcela";
	private static final String ERROR_MSGS = "errorMsgs";
	private static final String NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA = "No se han recibido todos los datos de entrada.";
	private static final String IDPOLIZA = "idpoliza";
	private static final String CLASE_ID = "claseId";
	private static final String LINEASEGUROID = "lineaseguroid";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String LISTA_IDS_STR = "listaIdsStr";
	private static final String POLIZA_BEAN = "polizaBean";
	private static final String PARCELA_BEAN = "parcelaBean";
	private static final String COD_PARCELA = "codParcela";
	private static final String USUARIO = "usuario";

	private final Log logger = LogFactory.getLog(DatosParcelaController.class);
	
	private IDatosParcelaManager datosParcelaManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private ClaseManager claseManager;
	private SigpacManager sigpacManager;
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	
	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;
	
	private String successView;
	
	// Constantes con los identificadores posibles de 'Pantalla'
	private static final Long PANTALLA_POLIZA        = Long.valueOf(7);
	private static final Long PANTALLA_INSTALACION   = Long.valueOf(9);
	private static final String IDANEXO = "idAnexo";

	/**
	 * Alta de parcela o instalacion
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) throws Exception {
		
		logger.debug("DatosParcelaController.doAlta - Alta de parcela");
		
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		if (StringUtils.isNullOrEmpty(polizaBean.getAsegurado().getNifcif())) {
			polizaBean.getAsegurado().setNifcif(usuario.getAsegurado().getNifcif());
		}
		
		// Carga los parametros genericos para la redireccion a la pantalla de parcelas
		Map<String, Object> parametros = cargarConfiguracionLinea(request, polizaBean);

		parametros.put("codUsuario", usuario.getCodusuario());
		parametros.put("operacion", request.getParameter("operacion"));
		parametros.put("origenLlamadaWS", SigpacManager.ORIGEN_LLAMADA_WS_POLIZA);
		
		ParcelaVO parcela = new ParcelaVO();
		parcela.setTipoParcela(StringUtils.isNullOrEmpty(request.getParameter("tipoParcela"))
				? String.valueOf(Constants.TIPO_PARCELA_PARCELA)
				: request.getParameter("tipoParcela"));	
		// Carga los parametros especificos del alta de parcela o instalacion para la redireccion a la pantalla de parcelas
		String codParcela = request.getParameter(COD_PARCELA);
		if (Constants.TIPO_PARCELA_INSTALACION.equals(parcela.getTipoParcelaChar())) {
			ParcelaVO refParcela = this.datosParcelaManager.getParcela(Long.valueOf(codParcela));
			BeanUtils.copyProperties(parcela, refParcela);
			parcela.resetTC();
			parcela.setCodParcela("");
			parcela.setNombreParcela(refParcela.getNombreParcela());
			parcela.setTipoParcela(String.valueOf(Constants.TIPO_PARCELA_INSTALACION));
			parcela.setIdparcelaanxestructura(codParcela);
		}	
		parametros.put(PARCELA_BEAN, parcela);
		
		cargarConfiguracionPantalla(parametros, polizaBean.getLinea().getLineaseguroid(), parcela, false);
		
		parametros.put(POLIZA_BEAN, polizaBean);
		
		// Se recupera una instancia especifica de la entidad "Linea" a traves del DAO a partir del lineaseguroid
		com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(polizaBean.getLinea().getLineaseguroid().toString());
		// Obtenemos la fecha de fin de contratacion.
		Date fechaInicioContratacion = linea.getFechaInicioContratacion();
		parametros.put("fechaInicioContratacion", fechaInicioContratacion);
		
		// Redirige a la pantalla de parcelas
		return new ModelAndView(successView).addAllObjects(parametros);
	}
	
	/**
	 * Redirige a la edicion/visualizacion de los datos de la parcela o instalacion, dependiendo de los parametros que reciba
	 */
	public ModelAndView doEditar(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean)
			throws Exception {

		logger.debug("DatosParcelaController.doEditar");

		// Carga los parametros genericos para la redireccion a la pantalla de parcelas
		Map<String, Object> parametros = cargarConfiguracionLinea(request, polizaBean);

		// Codigo de parcela
		String codParcela = request.getParameter(COD_PARCELA);
		
		ParcelaVO parcela = this.datosParcelaManager.getParcela(Long.valueOf(codParcela));

		parametros.put(LISTA_IDS_STR, request.getParameter(LISTA_IDS_STR));
		parametros.put("idSigParcela", this.datosParcelaManager.getIdSiguienteParcela(Long.valueOf(codParcela),
				polizaBean.getIdpoliza(), request.getParameter(LISTA_IDS_STR)));

		parametros.put(PARCELA_BEAN, parcela);
		
		cargarPreciosProducciones(polizaBean.getIdpoliza(), polizaBean.getLinea().getLineaseguroid(),
				parcela.getCapitalAsegurado().getProduccion(), parcela.getCapitalAsegurado().getPrecio(), parametros);

		parametros.put("operacion",
				Constants.TIPO_PARCELA_INSTALACION.equals(parcela.getTipoParcelaChar())
						? "modificarInstalacionParcela"
						: "modificarParcela");
		
		/* Pet.50776_63485-Fase II ** MODIF TAM (27.10.2020) ** Inicio */
		// Comprobar si puede tener coberturas segun linea y modulos de la poliza
		boolean tieneCoberturas = false;
		if(null!=polizaBean && polizaBean.getLinea() != null && polizaBean.getModuloPolizas() != null) {
			
				Poliza plz = this.datosParcelaManager.getPoliza(polizaBean.getIdpoliza());
				Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();	

				// Se recupera una instancia especifica de la entidad "Linea" a traves del DAO a partir del lineaseguroid

				com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(polizaBean.getLinea().getLineaseguroid().toString());
				// Obtenemos la fecha de fin de contratacion.
				Date fechaInicioContratacion = linea.getFechaInicioContratacion();
				parametros.put("fechaInicioContratacion", fechaInicioContratacion);
				
				tieneCoberturas = this.datosParcelaManager.isCoberturasElegiblesNivelParcela(polizaBean.getLinea().getLineaseguroid(), modsPoliza);
		}
		
		/* Creamos las vinculaciones de las coberturas para la jsp*/
		parametros.put("tieneCoberturas", tieneCoberturas);
		/* Pet.50776_63485-Fase II ** MODIF TAM (27.10.2020) ** Fin */
		
		cargarConfiguracionPantalla(parametros, polizaBean.getLinea().getLineaseguroid(), parcela, true);

		parametros.put(POLIZA_BEAN, polizaBean);
		parametros.put(COD_PARCELA, codParcela);

		// Redirige a la pantalla de parcelas
		return new ModelAndView(successView).addAllObjects(parametros);
	}

	/**
	 * Redirige a la edicion de los datos de la parcela o instalacion desde la pagina de errores de validacion
	 */
	public ModelAndView doEditarErrores(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		logger.debug("DatosParcelaController.doEditarErrores");
		
		// Carga la poliza asociada al idpoliza pasado como parametro
		logger.debug("DatosParcelaController.doEditarErrores - Carga la poliza asociada al id pasado como parametro");
		Poliza poliza = cargarPoliza(request);
		
		if(StringUtils.isNullOrEmpty(request.getParameter(MODO_LECTURA))){
			//Si esta en grabacion provisional, la cambiamos a pendiente validacion porque si volvemos a parcela, no sabemos si va a cambiar algo
			if(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL.equals(poliza.getEstadoPoliza().getIdestado())){
				EstadoPoliza estadoPoliza = new EstadoPoliza();
				estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
				poliza.setEstadoPoliza(estadoPoliza);
			}
		}
		
		// Carga los parametros genericos para la redireccion a la pantalla de parcelas
		Map<String, Object> parametros = cargarConfiguracionLinea(request, poliza);
				
		// Carga la parcela asociada a la poliza con hoja y numero pasados como parametros
		logger.debug("DatosParcelaController.doEditarErrores - Carga la parcela asociada a la poliza con hoja y numero pasados como parametros");
		ParcelaVO parcela = cargarParcela(request.getParameter("numhoja"), request.getParameter("numparcela"), poliza);
		parametros.put(COD_PARCELA, parcela.getCodParcela());
		parametros.put(PARCELA_BEAN, parcela);
		
		cargarPreciosProducciones(poliza.getIdpoliza(), poliza.getLinea().getLineaseguroid(),
				parcela.getCapitalAsegurado().getProduccion(), parcela.getCapitalAsegurado().getPrecio(), parametros);
		
		parametros.put(LISTA_IDS_STR, "");
		parametros.put("idSigParcela", this.datosParcelaManager
				.getIdSiguienteParcela(Long.valueOf(parcela.getCodParcela()), poliza.getIdpoliza(), ""));
		
		cargarConfiguracionPantalla(parametros, poliza.getLinea().getLineaseguroid(), parcela, true);
		
		parametros.put(POLIZA_BEAN, poliza);
		
		// Redirige a la pantalla de parcelas
		return new ModelAndView(successView).addAllObjects(parametros);
	}	
	
	/**
	 * Duplica la parcela indicada en el codigo de parametro y se muestra en modo edicion
	 */
	public ModelAndView doDuplicar(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) throws Exception {
		
		logger.debug("DatosParcelaController.doDuplicar");
		
		// Obtiene el id de la parcela a duplicar y se clona
		String idParcela = request.getParameter(COD_PARCELA);
		logger.debug("DatosParcelaController.doDuplicar - Duplica la parcela con id " + idParcela);
		Parcela clonParcela = seleccionPolizaManager.clonarParcela(Long.parseLong(idParcela));

		// Carga los parametros genericos para la redireccion a la pantalla de parcelas
		Map<String, Object> parametros = cargarConfiguracionLinea(request, polizaBean);
		
		ParcelaVO parcela;
		try {
			parcela = this.datosParcelaManager.getParcela(clonParcela);
		} catch (BusinessException e) {
			logger.error(e);
			parcela = new ParcelaVO();
		}
		parametros.put(PARCELA_BEAN, parcela);
		
		cargarConfiguracionPantalla(parametros, polizaBean.getLinea().getLineaseguroid(), parcela, true);
		
		// Carga los parametros especificos de la duplicacion de polizas
		// Codigo de parcela
		parametros.put(COD_PARCELA, idParcela);
		
		parametros.put(POLIZA_BEAN, polizaBean);
		
		// Redirige a la pantalla de parcelas
		return new ModelAndView(successView).addAllObjects(parametros);
	}
	
	/**
	 * Obtiene la zonificacion de Agroplus a partir del SigPac
	 */
	public ModelAndView doSigPac2Agro(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObj;
		JSONArray result = new JSONArray();
		logger.debug("DatosParcelaController.doSigPac2Agro [INIT]");
		final Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		String codParcela = request.getParameter(COD_PARCELA);
		String prov = request.getParameter("prov");
		String term = request.getParameter("term");
		String agr = request.getParameter("agr");
		String zona = request.getParameter("zona");
		String pol = request.getParameter("pol");
		String parc = request.getParameter("parc");
		String codPlan = request.getParameter("codPlan");
		String codLinea = request.getParameter("codLinea");
		String codCultivo = request.getParameter("codCultivo");
		SigpacVO sigpacVO = new SigpacVO();
		sigpacVO.setProv(prov);
		sigpacVO.setTerm(term);
		sigpacVO.setAgr(agr);
		sigpacVO.setZona(zona);
		sigpacVO.setPol(pol);
		sigpacVO.setParc(parc);
		sigpacVO.setCodPlan(codPlan);
		sigpacVO.setCodLinea(codLinea);
		sigpacVO.setCodCultivo(codCultivo);
		try {
			List<LocalCultVarVO> localCultVarLst = this.sigpacManager.getLocalCultVar(sigpacVO, realPath,
					usuario.getCodusuario(), StringUtils.isNullOrEmpty(codParcela) ? null : Long.valueOf(codParcela),
					SigpacManager.ORIGEN_LLAMADA_WS_POLIZA);		
			for (LocalCultVarVO localCultVar : localCultVarLst) {
				jsonObj = new JSONObject();				
				jsonObj.put("codProvincia", localCultVar.getCodProvincia());
				jsonObj.put("nomProvincia", localCultVar.getNomProvincia());
				jsonObj.put("codComarca", localCultVar.getCodComarca());
				jsonObj.put("nomComarca", localCultVar.getNomComarca());
				jsonObj.put("codTermino", localCultVar.getCodTermino());
				jsonObj.put("nomTermino", localCultVar.getNomTermino());
				jsonObj.put("codSubTermino", localCultVar.getSubTermino());
				result.put(jsonObj);
			}
		} catch (BusinessException e) {
			jsonObj = new JSONObject();				
			jsonObj.put("swErrorMsg", e.getMessage());
			result.put(jsonObj);
		} catch (JSONException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("DatosParcelaController.doSigPac2Agro [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	/**
	 * Valida los datos identificativos de la parcela
	 */
	public ModelAndView doValidaDatosIdent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		Boolean isValid;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doValidaDatosIdent [INIT]");
		String lsIdStr = request.getParameter(LINEASEGUROID);
		String claseStr = request.getParameter(CLASE_ID);
		String cultivoStr = request.getParameter("cultivo");
		String variedadStr = request.getParameter("variedad");
		String provinciaStr = request.getParameter("provincia");
		String comarcaStr = request.getParameter("comarca");
		String terminoStr = request.getParameter("termino");
		String subterminoStr = request.getParameter("subtermino");
		String polizaIdStr = request.getParameter(IDPOLIZA);
		String nifcif = request.getParameter("nifcif");
		if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(claseStr)
				|| StringUtils.isNullOrEmpty(cultivoStr) || StringUtils.isNullOrEmpty(variedadStr)
				|| StringUtils.isNullOrEmpty(provinciaStr) || StringUtils.isNullOrEmpty(comarcaStr)
				|| StringUtils.isNullOrEmpty(terminoStr) || StringUtils.isNullOrEmpty(subterminoStr)
				|| StringUtils.isNullOrEmpty(polizaIdStr) || StringUtils.isNullOrEmpty(nifcif)) {
			errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
			isValid = Boolean.FALSE;
		} else {
			errorMsgs = this.datosParcelaManager.validaDatosIdent(Long.valueOf(lsIdStr), Long.valueOf(claseStr),
					Long.valueOf(polizaIdStr), nifcif, new BigDecimal(cultivoStr), new BigDecimal(variedadStr),
					new BigDecimal(provinciaStr), new BigDecimal(comarcaStr), new BigDecimal(terminoStr),
					subterminoStr.charAt(0));
			isValid = ArrayUtils.isEmpty(errorMsgs);
		}
		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		result.put("isValid", isValid);
		logger.debug("DatosParcelaController.doValidaDatosIdent [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doGuardarTC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doGuardarTC [INIT]");
		String lsIdStr = request.getParameter(LINEASEGUROID);
		String claseStr = request.getParameter(CLASE_ID);
		String nifcif = request.getParameter("nifcif");
		String parcelaStr = request.getParameter(PARCELA);
		String isAlreadySaved = request.getParameter("isAlreadySaved");
		if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(claseStr)
				|| StringUtils.isNullOrEmpty(nifcif) || StringUtils.isNullOrEmpty(parcelaStr)
				|| StringUtils.isNullOrEmpty(isAlreadySaved)) {
			errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
		} else {
			ParcelaVO parcelaVO = ParcelaUtil.getParcelaVO(new JSONObject(parcelaStr));
			if (Boolean.parseBoolean(isAlreadySaved)) {
				errorMsgs = new String[] {};
			} else {			
				// PRIMERO VALIDAMOS
				errorMsgs = this.datosParcelaManager.validaDatosIdent(Long.valueOf(lsIdStr), Long.valueOf(claseStr),
						Long.valueOf(parcelaVO.getCodPoliza()), nifcif, new BigDecimal(parcelaVO.getCultivo()),
						new BigDecimal(parcelaVO.getVariedad()), new BigDecimal(parcelaVO.getCodProvincia()),
						new BigDecimal(parcelaVO.getCodComarca()), new BigDecimal(parcelaVO.getCodTermino()),
						parcelaVO.getCodSubTermino().charAt(0));
				ArrayUtils.addAll(errorMsgs,
						this.datosParcelaManager.validaDatosVariables(Long.valueOf(lsIdStr), parcelaVO));
				if (errorMsgs.length == 0) {
					errorMsgs = this.datosParcelaManager.guardarParcela(parcelaVO);
					/* Pet.50776_63485-Fase II ** MODIF TAM (05.11.2020) ** Inicio */
					if (errorMsgs.length == 0 && !StringUtils.isNullOrEmpty(parcelaVO.getCodParcela())) {
						
						Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
						String codUsuario = usuario.getCodusuario();					
						errorMsgs = datosParcelaManager.actualizarParcelasCoberturas(parcelaVO, codUsuario, Long.valueOf(parcelaVO.getCodPoliza()));
					}
					/* Pet.50776_63485-Fase II ** MODIF TAM (05.11.2020) ** Fin */
				}
			}
			result.put(PARCELA, new JSONObject(parcelaVO));
		}
		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doGuardarTC [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doBorrarTC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doBorrarTC [INIT]");
		String idCapitalAsegurado = request.getParameter("idCapitalAsegurado");
		if (StringUtils.isNullOrEmpty(idCapitalAsegurado)) {
			errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
		} else {
			this.datosParcelaManager.borrarTC(Long.valueOf(idCapitalAsegurado));
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doBorrarTC [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doObtenerDatosParcela(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doObtenerDatosParcela [INIT]");
		String idPoliza = request.getParameter(IDPOLIZA);
		String idParcela = request.getParameter("idParcela");
		String listaIdsStr = request.getParameter(LISTA_IDS_STR);
		if (StringUtils.isNullOrEmpty(idPoliza) || StringUtils.isNullOrEmpty(idParcela)) {
			errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
		} else {
			ParcelaVO parcelaVO = this.datosParcelaManager.getParcela(Long.valueOf(idParcela));
			result.put(PARCELA, new JSONObject(parcelaVO));
			result.put("idSigParcela", this.datosParcelaManager.getIdSiguienteParcela(Long.valueOf(idParcela),
					Long.valueOf(idPoliza), listaIdsStr));
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doObtenerDatosParcela [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doObtenerDatosTC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doObtenerDatosTC [INIT]");
		String idCapitalAsegurado = request.getParameter("idCapitalAsegurado");
		if (StringUtils.isNullOrEmpty(idCapitalAsegurado)) {
			errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
		} else {
			CapitalAseguradoVO capAsegVO = this.datosParcelaManager.getCapitalAsegurado(Long.valueOf(idCapitalAsegurado));
			result.put("capAseg", new JSONObject(capAsegVO));
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doObtenerDatosTC [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doCamposMascara(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doCamposMascara [INIT]");
		String lsIdStr = request.getParameter(LINEASEGUROID);
		String listCodModulos = request.getParameter(LIST_COD_MODULOS);
		String parcelaStr = request.getParameter(PARCELA);
		if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(listCodModulos)
				|| StringUtils.isNullOrEmpty(parcelaStr)) {
			errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
		} else {
			ParcelaVO parcelaVO = ParcelaUtil.getParcelaVO(new JSONObject(parcelaStr));
			PantallaConfigurable pantalla = this.seleccionPolizaManager.getPantallaVarPoliza(Long.valueOf(lsIdStr),
					!"".equals(parcelaVO.getTipoParcela())
							&& Constants.TIPO_PARCELA_INSTALACION.equals(parcelaVO.getTipoParcelaChar())
									? PANTALLA_INSTALACION
									: PANTALLA_POLIZA);
			PantallaConfigurableVO pantallaConfigurableVO = this.datosParcelaManager
					.getPantallaConfigurableVO(listCodModulos, pantalla, parcelaVO, true);
			result.put("mustFillDVs", pantallaConfigurableVO.getListCodConceptosMascaras());
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doCamposMascara [END]");
		getWriterJSON(response, result);
		return null;
	}

	public ModelAndView doCalculoPrecioProduccion(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String[] errorMsgs = new String[] {};
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doCalculoPrecioProduccion [INIT]");
		final Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		String lsIdStr = request.getParameter(LINEASEGUROID);
		String listCodModulos = request.getParameter(LIST_COD_MODULOS);
		String parcelaStr = request.getParameter(PARCELA);
		if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(listCodModulos)
				|| StringUtils.isNullOrEmpty(parcelaStr)) {
			errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
		} else {
			ParcelaVO parcelaVO = ParcelaUtil.getParcelaVO(new JSONObject(parcelaStr));
			List<Integer> conceptosVacios = new ArrayList<Integer>();
			if (StringUtils.isNullOrEmpty(parcelaVO.getCapitalAsegurado().getCodtipoCapital())) {
				conceptosVacios.add(ConstantsConceptos.CODCPTO_TIPOCAPITAL);
			}
			if (StringUtils.isNullOrEmpty(parcelaVO.getCapitalAsegurado().getSuperficie())) {
				conceptosVacios.add(ConstantsConceptos.CODCPTO_SUPERFICIE);
			}
			
			PreciosProduccionesVO preciosProduccionesVO;
			
			if (conceptosVacios.isEmpty()) {
				
				// Venimos de la pantalla de datos parcela de anexo
				if (StringUtils.isNullOrEmpty(request.getParameter(IDANEXO))) {
					preciosProduccionesVO = this.calculoPrecioProduccionManager
							.getProduccionPrecioPolizaWS(parcelaVO, realPath, usuario.getCodusuario());
				} 
				else {
					preciosProduccionesVO = this.calculoPrecioProduccionManager
							.getProduccionPrecioAnexoWS2(parcelaVO, realPath, usuario.getCodusuario());
				}
				
				if (!StringUtils.isNullOrEmpty(preciosProduccionesVO.getMensajeError())) {
					errorMsgs = new String[] { preciosProduccionesVO.getMensajeError() };
				}
				if (!preciosProduccionesVO.getListPrecios().isEmpty()) {
					result.put("listPrecios", new JSONArray(preciosProduccionesVO.getListPrecios()));
				}
				if (!preciosProduccionesVO.getListProducciones().isEmpty()) {
					result.put("listProducciones", new JSONArray(preciosProduccionesVO.getListProducciones()));
				}
				result.put("rdtosLibres", preciosProduccionesVO.isRdtosLibres());
			} else {
				errorMsgs = new String[] { "No se han rellenado todos los datos necesarios para realizar el c&aacute;lculo." };
				result.put("conceptosVacios",  new JSONArray(conceptosVacios));
			}
		}
		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doCalculoPrecioProduccion [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doObtenerDatosConcepto(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONObject result = new JSONObject();
		String desCpto = "";
		BigDecimal cptoAsociadoTC = null;
		String lsIdStr = request.getParameter(LINEASEGUROID);
		String listCodModulos = request.getParameter(LIST_COD_MODULOS);
		String codConcepto = request.getParameter("codConcepto");
		String valor = request.getParameter("valor");
		if (!StringUtils.isNullOrEmpty(lsIdStr) && !StringUtils.isNullOrEmpty(listCodModulos)
				&& !StringUtils.isNullOrEmpty(codConcepto) && !StringUtils.isNullOrEmpty(valor)) {
			desCpto = this.datosParcelaManager.getDescDatoVariable(Long.valueOf(lsIdStr), listCodModulos,
					Integer.parseInt(codConcepto), valor);
			if (StringUtils.isNullOrEmpty(desCpto)) {
				if ("0".equals(valor)) {
					desCpto = "SIN VALOR EN FACTOR";
				}
			}
			if (ConstantsConceptos.CODCPTO_TIPOCAPITAL == Integer.parseInt(codConcepto)) {
				cptoAsociadoTC = this.datosParcelaManager.getCptoAsociadoTC(new BigDecimal(valor));
			}
		}
		result.put("desCpto", desCpto);
		result.put("cptoAsociadoTC", cptoAsociadoTC);
		getWriterJSON(response, result);
		return null;
	}
	
	public ModelAndView doDuplicarAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("DatosParcelaController.doDuplicarAjax [INIT]");
		String idParcelaStr = request.getParameter("idParcela");
		if (StringUtils.isNullOrEmpty(idParcelaStr)) {
			errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
		} else {
			Parcela clon = this.seleccionPolizaManager.clonarParcela(Long.parseLong(idParcelaStr));
			result.put(COD_PARCELA, clon.getIdparcela());
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		logger.debug("DatosParcelaController.doDuplicarAjax [END]");
		getWriterJSON(response, result);
		return null;
	}
	
	/**
	 * Devuelve la poliza asociada al idpoliza contenida en la request
	 */
	private Poliza cargarPoliza (HttpServletRequest request) {
		Poliza p = null;		
		// Si esta informado el idpoliza
		if (!StringUtils.isNullOrEmpty(request.getParameter(IDPOLIZA))) {
			try {
				// Carga la poliza asociada
				p = this.seleccionPolizaManager.getPolizaById(Long.parseLong(request.getParameter(IDPOLIZA)));
			}
			catch (Exception e) {
				logger.debug("DatosParcelaController.cargarPoliza - Ocurrio un error al cargar la poliza asociada al id " 
						  	+ request.getParameter(IDPOLIZA) , e);
			}
		} else {
			logger.debug("DatosParcelaController.cargarPoliza - No se pudo cargar la poliza debido a que falta algun dato");
		}		
		return p;
	}
	
	/**
	 * Devuelve la parcela asociada a la poliza que tenga la hoja y numero contenidos en la request
	 */
	private ParcelaVO cargarParcela(String numHoja, String numParcela, Poliza poliza) {		
		ParcelaVO p = null;		
		// Si estan informados la hoja y el numero de la parcela
		if (!StringUtils.isNullOrEmpty(numHoja) && !StringUtils.isNullOrEmpty(numParcela)) {
			try {
				// Se obtiene la parcela a partir de la poliza asociada y la hoja y numero
				Parcela parcela = new Parcela();
				parcela.setPoliza(poliza);
				parcela.setHoja(Integer.parseInt(numHoja));
				parcela.setNumero(Integer.parseInt(numParcela));
				List<Parcela> listaParcelas = this.seleccionPolizaManager.getParcelas(parcela, null, null);
				// Si la busqueda ha encontrado resultado
				if (listaParcelas != null && listaParcelas.size() > 0) {
					p = this.datosParcelaManager.getParcela(listaParcelas.get(0));
				}
			} catch (Exception e) {
				logger.debug(
						"DatosParcelaController.cargarPoliza - Ocurrio un error al cargar la parcela asociada a la poliza "
								+ (poliza != null ? poliza.getIdpoliza() : "") + " con hoja " + numHoja + " y numero "
								+ numParcela,
						e);
			}
		} else {
			logger.debug("DatosParcelaController.cargarPoliza - No se pudo cargar la parcela debido a que falta algun dato");
		}		
		return p;
	}	
	
	/**
	 * Carga en el mapa de parametros los datos correspondientes a la linea que hay que pasar a la pantalla de parcelas
	 */
	private Map<String, Object> cargarConfiguracionLinea(HttpServletRequest request, Poliza polizaBean) {
		
		Map<String, Object> p = new HashMap<String, Object>();
		
		// Anhade al mapa el parametro que indica si viene del listado de utilidades
		if (!StringUtils.isNullOrEmpty(request.getParameter("vieneDeUtilidades"))) p.put("vieneDeUtilidades", request.getParameter("vieneDeUtilidades"));
		// Anhade al mapa el parametro que indica si viene de modo lectura
		if (!StringUtils.isNullOrEmpty(request.getParameter(MODO_LECTURA))) p.put(MODO_LECTURA, request.getParameter(MODO_LECTURA));
		// Anhade al mapa el codigo de plan
		p.put("codPlan", polizaBean.getLinea().getCodplan());
		// Anhade al mapa el codigo de linea
		p.put("codLinea", polizaBean.getLinea().getCodlinea());
		Long claseId = getClaseId(polizaBean);
		// Anhade las restricciones por clase 
		p.putAll(this.datosParcelaManager.getListaCodigosLupasParcelas(claseId));
		// Anhade al mapa el parametro correspondiente a la clase de la poliza
		p.put(CLASE_ID, claseId);	
		// Anhade al mapa el parametro correspondiente al nif/cif del asegurado
		p.put("nifAsegurado", polizaBean.getAsegurado().getNifcif());	
		// Anhade al mapa la lista de modulos seleccionados para las lupas que lo puedan necesitar
		String listCodModulos = this.seleccionPolizaManager.getListCodModulos(polizaBean.getIdpoliza());
		p.put(LIST_COD_MODULOS, listCodModulos);
		// Anhade al mapa la lista conceptos principales de los modulos seleccionados
		// para las lupas que lo puedan necesitar
		p.put("listCPModulos", this.seleccionPolizaManager.getListCodCPModulos(polizaBean.getLinea().getLineaseguroid(),
				listCodModulos));
		// Anhade al mapa la lista riesgos cubiertos de los modulos seleccionados para
		// las lupas que lo puedan necesitar
		p.put("listRCModulos", this.seleccionPolizaManager.getListCodRCModulos(polizaBean.getLinea().getLineaseguroid(),
				listCodModulos));
		
		return p;
	}	
	
	
	/**
	 * Carga en el mapa los parametros relativos a la pantalla configurable asociada a la linea de la poliza 
	 */
	private void cargarConfiguracionPantalla(Map<String, Object> parametros, final Long lineaseguroid,
			final ParcelaVO parcela, final boolean calcularMascaras) throws BusinessException {
		// Carga la pantalla configurable asociada a la poliza, diferenciando si es de
		// parcela o de instalacion
		PantallaConfigurable pantalla = this.seleccionPolizaManager.getPantallaVarPoliza(lineaseguroid,
				!"".equals(parcela.getTipoParcela())
						&& Constants.TIPO_PARCELA_INSTALACION.equals(parcela.getTipoParcelaChar())
								? PANTALLA_INSTALACION
								: PANTALLA_POLIZA);
		// Obtiene la altura por defecto del panel de datos variables
		String alturaPanelDV = PantallaConfigurable.MAX_ALTURA_PANEL_DV_DEFECTO.toString();
		// Si la pantalla asociada a la poliza esta configurada
		if (pantalla != null) {
			alturaPanelDV = pantalla.getAlturaPanelDV().toString();
			PantallaConfigurableVO pantallaConfigurableVO = this.datosParcelaManager.getPantallaConfigurableVO(
					(String) parametros.get(LIST_COD_MODULOS), pantalla, parcela, calcularMascaras);
			parametros.put("listaDV", pantallaConfigurableVO.getListCampos());
			parametros.put("mustFillDVs", pantallaConfigurableVO.getListCodConceptosMascaras());
		}
		parametros.put("alturaPanelDV", alturaPanelDV);
		// Para que la lupa de tipos de capital saque los que correspondan segun el tipo de la parcela
		if (!"".equals(parcela.getTipoParcela())
				&& Constants.TIPO_PARCELA_INSTALACION.equals(parcela.getTipoParcelaChar())) {
			parametros.put("filtroTipoCapitalGE", Constants.TIPOCAPITAL_INSTALACIONES_MINIMO);
		} else {
			parametros.put("filtroTipoCapitalLT", Constants.TIPOCAPITAL_INSTALACIONES_MINIMO);
		}
	}
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Inicio */ 
	/**
	 * Obtenemos la tabla de coberturas de la parcela
	 * @param request
	 * @param response
	 * @param parcelaBean
	 * @throws JSONException
	 * @throws BusinessException
	 */
	public ModelAndView doCargarCoberturasParcela(HttpServletRequest request, HttpServletResponse response)
			throws BusinessException, JSONException {

		String[] errorMsgs = new String[] { "" };
		JSONObject result = new JSONObject();

		logger.debug("DatosParcelaController - doCargarCoberturasParcela [INIT]");

		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

		String lsIdStr = request.getParameter(LINEASEGUROID);
		String claseStr = request.getParameter(CLASE_ID);
		String nifcif = request.getParameter("nifcif");
		String parcelaStr = request.getParameter(PARCELA);

		Boolean isCoberturasParc = true;

		Long idCapAseg = new Long(0);
		Long lineaSeguroid = Long.parseLong(lsIdStr);

		List<ParcelasCoberturasNew> lstCobParc = null;

		if (isCoberturasParc) {
			String realPath = this.getServletContext().getRealPath("/WEB-INF/");

			if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(claseStr)
					|| StringUtils.isNullOrEmpty(nifcif) || StringUtils.isNullOrEmpty(parcelaStr)) {
				errorMsgs = new String[] { NO_SE_HAN_RECIBIDO_TODOS_LOS_DATOS_DE_ENTRADA };
			} else {

				ParcelaVO parcelaVO = ParcelaUtil.getParcelaVO(new JSONObject(parcelaStr));

				Long idPoliza = Long.parseLong(parcelaVO.getCodPoliza());

				if (!StringUtils.isNullOrEmpty(parcelaVO.getCapitalAsegurado().getId())) {
					idCapAseg = Long.parseLong(parcelaVO.getCapitalAsegurado().getId());
				}

				String codParcela = request.getParameter(COD_PARCELA);
				if (codParcela == null || codParcela == "") {
					codParcela = parcelaVO.getCodParcela();
				}
				
				Poliza plz = this.datosParcelaManager.getPoliza(idPoliza);
				Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();

				Parcela parcela = new Parcela();

				this.datosParcelaManager.generateParcela(parcelaVO, parcela, plz.getLinea().getLineaseguroid());
				if (!StringUtils.isNullOrEmpty(codParcela)) {
					parcela.setIdparcela(Long.valueOf(codParcela));
				}

				boolean isCoberturas = false;
				isCoberturas = this.datosParcelaManager.isCoberturasElegiblesNivelParcela(lineaSeguroid, modsPoliza);
				result.put("isCoberturas", isCoberturas);
				
				/* si la parcela Existe */
				if (!StringUtils.isNullOrEmpty(codParcela)) {

					Parcela parc = new Parcela();
					if (isCoberturas) {

						Set<ParcelaCobertura> cobParcExistentes = new HashSet<ParcelaCobertura>(0);
						cobParcExistentes = parc.getCoberturasParcela();

						lstCobParc = this.datosParcelaManager.getCoberturasParcela(parcela, parcelaVO,
								usuario.getCodusuario(), realPath, cobParcExistentes, idCapAseg, plz);
					}

					result.put("tieneCoberturas",
							parc.getCoberturasParcela() != null && parc.getCoberturasParcela().size() > 0);
					/* si la parcela No Existe */
				} else {

					/*
					 * Solo para las lineas que tengan riesgos elegibles a nivel de Parcela, se
					 * llamaria al servio web de Ayuda de contratacion par obtener el cuadro de
					 * coberturas correspondiente
					 */
					if (isCoberturas) {
						Set<ParcelaCobertura> cobParcExistentes = new HashSet<ParcelaCobertura>(0);

						lstCobParc = this.datosParcelaManager.getCoberturasParcela(parcela, parcelaVO,
								usuario.getCodusuario(), realPath, cobParcExistentes, idCapAseg, plz);
					}
				} /* Fin del if de codParcela */

			}

		}

		result.put(ERROR_MSGS, new JSONArray(errorMsgs));
		result.put("listaCobParcelas", new JSONArray(lstCobParc));

		getWriterJSON(response, result);
		return null;

	}
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Fin */ 
		
	/**
	 * Devuelve el id de clase asociada al usuario
	 */
	private Long getClaseId (Poliza polizaBean) {		
		Clase clase = claseManager.getClase(polizaBean);		
		return (clase != null && clase.getId() != null) ? clase.getId() : 0L;
	}
	
	/**
	 * Setea los rangos de precio y produccion para las lupas en edicion
	 */
	private void cargarPreciosProducciones(final Long idpoliza, final Long lineaseguroid, final String produccion, final String precio,
			final Map<String, Object> parametros) throws BusinessException {
		List<ProduccionVO> producciones = new ArrayList<ProduccionVO>();
		List<PrecioVO> precios = new ArrayList<PrecioVO>();
		List<Modulo> modulos = this.calculoPrecioProduccionManager.getModulosPoliza(idpoliza, lineaseguroid);
		for (Modulo mod : modulos) {
			if (!StringUtils.isNullOrEmpty(produccion)) {
				ProduccionVO prodVO = new ProduccionVO();
				prodVO.setCodModulo(mod.getId().getCodmodulo());
				prodVO.setDesModulo(mod.getDesmodulo());
				prodVO.setProduccion(produccion);
				prodVO.setLimMin("1");
				prodVO.setLimMax(produccion);
				producciones.add(prodVO);
			}
			if (!StringUtils.isNullOrEmpty(precio)) {
				PrecioVO precVO = new PrecioVO();
				precVO.setCodModulo(mod.getId().getCodmodulo());
				precVO.setDesModulo(mod.getDesmodulo());
				precVO.setLimMin("0");
				precVO.setLimMax(precio);
				precios.add(precVO);
			}
		}
		if ("0".equals(produccion)) {
			parametros.put("rdtosLibres", true);
			parametros.put("listProducciones", "");
		} else if (!producciones.isEmpty()) {
			parametros.put("rdtosLibres", false);
			parametros.put("listProducciones", new JSONArray(producciones));
		}
		if (!precios.isEmpty()) {
			parametros.put("listPrecios", new JSONArray(precios));
		}
	}

	public void setDatosParcelaManager(IDatosParcelaManager datosParcelaManager) {
		this.datosParcelaManager = datosParcelaManager;
	}
	
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}

	public void setSigpacManager(SigpacManager sigpacManager) {
		this.sigpacManager = sigpacManager;
	}	
	
	public void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
}