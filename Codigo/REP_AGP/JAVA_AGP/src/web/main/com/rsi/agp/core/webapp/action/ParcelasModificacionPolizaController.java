package com.rsi.agp.core.webapp.action;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.decorators.ModelTableDecoratorParcelasModificadas;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.managers.IDatosParcelaManager;
import com.rsi.agp.core.managers.impl.AnexoModificacionManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.managers.impl.DatosParcelaAnexoManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.ParcelasModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.SigpacManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.SolicitudModificacionManager;
import com.rsi.agp.core.report.anexoMod.BeanParcelaAnexo;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.ParcelaUtil;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.anexo.AnexoModSWCambioMasivo;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ParcelasCoberturasNew;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.TipoRdto;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.OperationResultVO;
import com.rsi.agp.vo.PantallaConfigurableVO;
import com.rsi.agp.vo.ParcelaVO;

public class ParcelasModificacionPolizaController extends BaseMultiActionController {

	// CONSTANTS
	private static final Long PANTALLA_POLIZA = Long.valueOf(7);
	private static final Long PANTALLA_INSTALACION = Long.valueOf(9);

	private Log logger = LogFactory.getLog(ParcelasModificacionPolizaController.class);

	/* Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	private ParcelasModificacionPolizaManager parcelasModificacionPolizaManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private IDatosParcelaManager datosParcelaManager;
	private DatosParcelaAnexoManager datosParcelaAnexoManager;
	private ClaseManager claseManager;
	private AnexoModificacionManager anexoModificacionManager;
	private SolicitudModificacionManager solicitudModificacionManager;
	
	/**
	 * SONAR Q ** MODIF TAM(05.11.2021) ** Se ha eliminado todo el código comentado
	 **/

	/** CONSTANTES SONAR Q ** MODIF TAM (05.11.2021) ** Inicio **/
	private final static String IDPOLIZA = "idPoliza";
	private final static String MODO_LECT = "modoLectura";
	private final static String TIPO_LISTGRID = "tipoListadoGrid";
	private final static String IDROWSCHECK = "idsRowsChecked";
	private final static String IDCAPROWSCHECK = "idsCapAsegRowsChecked";
	private final static String MARCARTODCHK = "marcarTodosChecks";
	private final static String ISCHECKLIST = "isClickInListado";
	private final static String IDANEXO = "idAnexo";
	private final static String IDANEXO_MOD = "idAnexoModificacion";
	private final static String ALERTA = "alerta";
	private final static String ALERTA2 = "alerta2";
	private final static String MENSAJE = "mensaje";
	private final static String TIPO_LIST_ANEXO = "tipolistadoAnexo";
	private final static String IDCUPON = "idCupon";
	private final static String IDCUPONSTR = "idCuponStr";
	private final static String PARCELA = "parcelas";
	private final static String INSTALACIONES = "instalaciones";
	private final static String SORT = "d-5909046-s";
	private final static String ORDER = "d-5909046-o";
	private final static String COLUMN = "columna";
	private final static String ORDEN = "orden";
	private final static String LINEAID = "lineaseguroid";
	private final static String MSJ_ERR_GEN = "mensaje.error.general";
	private final static String VIENE_LSTANX = "vieneDeListadoAnexosMod";
	private final static String VIENE_COBANX = "vieneDeCoberturasAnexo";
	private final static String LST_CODMOD = "listCodModulos";
	private final static String ANX_CAMB_MAS = "anexoModSWCambioMasivo";
	private final static String CAP_ASEG_MOD = "capitalAseguradoModificadaBean";
	private final static String USU = "usuario";
	private final static String MOD_PARC = "modificarParcela";
	private final static String COD_PARC = "codParcela";
	private final static String TIENE_COB = "tieneCoberturas";
	private final static String VIENE_UTL = "vieneDeUtilidades";
	private final static String CLASEID = "claseId";
	private final static String PARC = "parcela";
	private final static String NO_DATOS = "No se han recibido todos los datos de entrada.";
	private final static String ERROR_MSG = "errorMsgs";
	private final static String MSJ_DESHACER = "mensaje.deshacer.KO";
	/** CONSTANTES SONAR Q ** MODIF TAM (05.11.2021) ** Fin **/

	/**
	 * Al cargar la pagina la primera vez (pantalla lista de parcelas de anexo)
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {
		List<CapitalAsegurado> listaCapitalesAseguradosAnexo = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		String idPoliza = StringUtils.nullToString(request.getParameter(IDPOLIZA));
		String modoLectura = StringUtils.nullToString(request.getParameter(MODO_LECT));
		String tipoListadoGrid = request.getParameter(TIPO_LISTGRID);
		Poliza poliza = null;
		String itemCombo = "T";
		String idsRowsChecked = StringUtils.nullToString(request.getParameter(IDROWSCHECK));
		String idsCapAsegRowsChecked = StringUtils.nullToString(request.getParameter(IDCAPROWSCHECK));
		String marcarTodosChecks = StringUtils.nullToString(request.getParameter(MARCARTODCHK));
		String isClickInListado = StringUtils.nullToString(request.getParameter(ISCHECKLIST));
		Long estadoCupon = null;
		
		/* Pet. 78691 ** MODIF TAM (17.12.2021) ** Inicio */
		/* Obtenemos el parametro nuevo de busqueda de S. Cultivo */
		String sistCultivo = StringUtils.nullToString(request.getParameter("sistemaCultivo"));
		/* Pet. 78691 ** MODIF TAM (17.12.2021) ** Inicio */

		/* SONARQ */
		String idAnexo = obteneridAnexo(request);
		parametros = informarParameter(request, parametros);
		idsRowsChecked = obtenerIdsRowsChecked(idsRowsChecked, isClickInListado);
		idsCapAsegRowsChecked = obtenerIdsCapAsegRowsChecked(idsCapAsegRowsChecked, isClickInListado);
		/* SONARQ */

		HttpSession session = request.getSession(true);

		/* SONAR Q */
		tipoListadoGrid = obtenertipoListadoGrid(session);
		/* SONAR Q */

		/* ESC-12653 ** MODIF TAM (02.03.2021) ** Inicio */
		/* El problema es que al paginar se pierde el valor de idAnexo */

		if (idAnexo.equals(""))
			idAnexo = (String) session.getAttribute(IDANEXO);
		/* ESC-12653 ** MODIF TAM (02.03.2021) ** Fin */

		session.setAttribute(TIPO_LIST_ANEXO, "");
		capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().setId(new Long(idAnexo));
		session.setAttribute("parcelaAnexoBusqueda", capitalAseguradoModificadaBean.getParcela());

		/* SONARQ */
		String idCupon = obteneridCupon(capitalAseguradoModificadaBean, request);
		String idCuponStr = obteneridCuponStr(capitalAseguradoModificadaBean, request);
		/* SONARQ */

		try {
			if (StringUtils.nullToString(tipoListadoGrid).equals("")) {
				tipoListadoGrid = "todas";
			}
			/* SONAR Q */
			capitalAseguradoModificadaBean = asignarTipoParcela(tipoListadoGrid, capitalAseguradoModificadaBean);
			/* SONAR Q */

			// Recuperamos la poliza para mostrarlos en la cabecera de la pantalla
			if (!idPoliza.equals("")) {
				poliza = this.declaracionesModificacionPolizaManager.getPoliza(new Long(idPoliza));
			} else {
				AnexoModificacion anexo = this.declaracionesModificacionPolizaManager
						.getAnexoModifById(new Long(idAnexo));
				poliza = anexo.getPoliza();

				// Guarda el estado del cupon para comprobar si hay que mostrar el boton de
				// 'Importes' en la pantalla
				if (anexo.getCupon() != null && anexo.getCupon().getEstadoCupon() != null) {
					estadoCupon = anexo.getCupon().getEstadoCupon().getId();
				}

				// Si el anexo esta en estado 'Enviado Correcto' se visualiza el listado en modo
				// lectura
				if (Constants.ANEXO_MODIF_ESTADO_CORRECTO.equals(anexo.getEstado().getIdestado())) {
					modoLectura = "true";
				}
				// Se anade esta condicion para recuperar el id del cupon del anexo para
				// paginar correctamente
				if (StringUtils.nullToString(idCupon).equals("") && anexo.getCupon() != null) {
					idCupon = StringUtils.nullToString(anexo.getCupon().getId());
					idCuponStr = StringUtils.nullToString(anexo.getCupon().getIdcupon());
				}
			}
			List<CapitalAsegurado> listaCapitalesAseguradosAnexoFiltradas = null;
			List<CapitalAsegurado> listaCapAseguradosAnxFinal = null;
			if (!StringUtils.nullToString(idAnexo).equals("")) {

				/* SONAR Q */
				copiarParcelas(idCupon, idAnexo, poliza);
				getOrdenacionDisplaytag(request);
				/* SONAR Q */

				String columna = (String) request.getSession().getAttribute(COLUMN);
				String orden = (String) request.getSession().getAttribute(ORDEN);
				listaCapitalesAseguradosAnexo = this.parcelasModificacionPolizaManager
						.getParcelasAnexo(capitalAseguradoModificadaBean, columna, orden);
				
				/* Pet. 78691 ** MODIF TAM (21/12/2021) ** Inicio*/
				listaCapAseguradosAnxFinal= this.parcelasModificacionPolizaManager.getParcelasAnxFiltradas(listaCapitalesAseguradosAnexo, sistCultivo);
				

				listaCapitalesAseguradosAnexoFiltradas = filtraPorTipoRdto(listaCapAseguradosAnxFinal,
						capitalAseguradoModificadaBean.getTipoRdto());

				parametros.put(IDANEXO, idAnexo);
			}
			parametros.put("listaParcelasModificadas", listaCapitalesAseguradosAnexoFiltradas);

			/* SONAR Q */
			AnexoModificacion anexo = null;
			estadoCupon = obtenerEstadoCupon(idAnexo, estadoCupon, anexo);
			/* FIN SONAR Q */

			boolean tieneRdtoHist = false;
			if (poliza != null) {
				parametros.put(LINEAID, poliza.getLinea().getLineaseguroid());
				Clase clase = new Clase();
				clase = this.claseManager.getClase(poliza);
				tieneRdtoHist = Constants.TIENE_RDTO_HISTORICO.equals(clase.getRdtoHistorico())
						&& clase.getLinea().getCodlinea().equals(new BigDecimal(300));
			}

			/* ESC-12653 ** MODIF TAM (02.03.2021) ** Inicio */
			session.setAttribute(IDANEXO, idAnexo);
			/* ESC-12653 ** MODIF TAM (02.03.2021) ** Fin */
			
			
			/* Pet. 78691 ** MODIF TAM (22.12.2021) ** Inicio */
			parametros.put("sist_cultivo", 	  sistCultivo);
			if (!"".equals(StringUtils.nullToString(sistCultivo))) {
				/* obtenemos la descripción del cultivo*/
				String desc_sistCultivo = this.parcelasModificacionPolizaManager.obtenerDescSistCultivo(sistCultivo);
				parametros.put("des_sist_cultivo", desc_sistCultivo);
			}
			
			/* Pet. 78691 ** MODIF TAM (22.12.2021) ** Fin */

			parametros.put("tieneRdtoHist", tieneRdtoHist);
			/* Pet. 78691 ** MODIF TAM (30.12.2021) ** Inicio * Resolución Defecto Nº1*/
			parametros.put("numParcelasListado",
					listaCapitalesAseguradosAnexoFiltradas != null ? listaCapitalesAseguradosAnexoFiltradas.size() : 0);
			parametros.put(MARCARTODCHK, marcarTodosChecks);
			parametros.put("parcelasString",
					listaCapitalesAseguradosAnexoFiltradas != null ? getListParcelasString(listaCapitalesAseguradosAnexoFiltradas)
							: null);
			parametros.put("capAsegString",
					listaCapitalesAseguradosAnexoFiltradas != null ? getListCapAsegString(listaCapitalesAseguradosAnexoFiltradas) : null);
			parametros.put(IDROWSCHECK, idsRowsChecked);
			parametros.put(IDCAPROWSCHECK, idsCapAsegRowsChecked);
			parametros.put("poliza", poliza);
			parametros.put("itemCombo", itemCombo);
			parametros.put(TIPO_LISTGRID, tipoListadoGrid);
			parametros.put(MODO_LECT, modoLectura);
			parametros.put("anexoModificacionBean", new AnexoModificacion(Long.parseLong(idAnexo)));
			parametros.put("mostrarImportes", Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.equals(estadoCupon)
					|| Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.equals(estadoCupon));

			if (idAnexo != null) {
				AnexoModificacion anx = this.declaracionesModificacionPolizaManager
						.getAnexoModifById(new Long(idAnexo));

				/* Primero comprobamos si el boton da Cambio de Iban se debe mostrar */
				parametros.put("mostrarbtnIban", true);

				parametros.put("ibanAsegOriginal", anx.getIbanAsegOriginal());
				parametros.put("esIbanAsegModificado", anx.getEsIbanAsegModificado());
				parametros.put("ibanAsegModificado", anx.getIbanAsegModificado());

				parametros.put("iban2AsegOriginal", anx.getIban2AsegOriginal());
				parametros.put("esIban2AsegModificado", anx.getEsIban2AsegModificado());
				parametros.put("iban2AsegModificado", anx.getIban2AsegModificado());
				
				// Comprueba si hay que mostrar el boton 'Cambiar datos asegurados'
				parametros.put("mostrarBotonCambiarDatosAsegurado", mostrarBotonCambiarDatosAsegurado(anx));
				
				// Setea el hidden de cambios de asegurados para los volver
				parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
				
			}

		} catch (BusinessException be) {
			logger.error("[doConsulta] Se ha producido un error durante la consulta de parcelas de anexo", be);
			parametros.put(ALERTA, bundle.getString(MSJ_ERR_GEN));
		} catch (Exception ex) {
			logger.error("[doConsulta] Se ha producido un error durante la consulta de parcelas de anexo", ex);
			parametros.put(ALERTA, bundle.getString(MSJ_ERR_GEN));
		}
		// TMR 3-9-2012 Utilidades anexos de modificacion
		/* SONAR Q */
		String opcion = "A";
		String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getParameter(VIENE_LSTANX));

		parametros = informarParams(poliza, vieneDeListadoAnexosMod, opcion, parametros);
		/* SONAR Q */

		String vieneDeCoberturasAnexo = StringUtils.nullToString(request.getAttribute(VIENE_COBANX));

		/* SONAR Q */
		String clearStatus = obtenerclearStatus(vieneDeCoberturasAnexo);
		/* SONAR Q */

		parametros.put("clearStatus", clearStatus);
		List<TipoRdto> listaTipoRendimientos = this.seleccionPolizaManager.getTiposRendimiento();
		parametros.put("listaTipoRendimientos", listaTipoRendimientos);
		parametros.put("rdtoHist",
				capitalAseguradoModificadaBean.getTipoRdto() != null ? parametros.get("rdtoHist") : "0");

		/* SONAR Q */
		opcion = "B";
		parametros = informarParams(poliza, vieneDeListadoAnexosMod, opcion, parametros);
		/* SONAR Q */

		if (!StringUtils.nullToString(idCupon).equals("")) {
			parametros.put(IDCUPONSTR, idCuponStr);
			// objeto para el cambio masivo
			parametros.put(ANX_CAMB_MAS, new AnexoModSWCambioMasivo());
			capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getCupon().setIdcupon(idCuponStr);
			capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getCupon().setId(new Long(idCupon));
			// Redirigimos a la pantalla de parcelas de anexos de tipo SW (con cupon)
			return new ModelAndView("/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacionSw", CAP_ASEG_MOD,
					capitalAseguradoModificadaBean).addAllObjects(parametros);
		} else {
			// Redirigimos a la pantalla de parcelas de anexos de tipo FTP
			return new ModelAndView("/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacion", CAP_ASEG_MOD,
					capitalAseguradoModificadaBean).addAllObjects(parametros);
		}
	}
	
	private boolean mostrarBotonCambiarDatosAsegurado(AnexoModificacion anexoModificacion) {
		
		XmlObject polizaDoc = this.solicitudModificacionManager
				.getPolizaActualizadaFromCupon(anexoModificacion.getCupon().getIdcupon());
		es.agroseguro.contratacion.Poliza sitAct = ((es.agroseguro.contratacion.PolizaDocument) polizaDoc)
				.getPoliza();
		
		return anexoModificacionManager.isAnexoAseguradoConModificaciones(anexoModificacion.getPoliza(), sitAct);
		
	}

	private String getListParcelasString(List<CapitalAsegurado> listParcelas) {
		StringBuilder result = new StringBuilder("");
		for (int i = 0; i < listParcelas.size(); i++) {
			String id = "";
			String tipoMod = "";
			String tipoPar = "";
			if (listParcelas.get(i).getParcela().getId() != null) {
				id = listParcelas.get(i).getParcela().getId().toString();
			} else {
				id = " ";
			}
			if (listParcelas.get(i).getParcela().getTipomodificacion() != null) {
				tipoMod = listParcelas.get(i).getParcela().getTipomodificacion().toString();
			} else {
				tipoMod = " ";
			}
			if (listParcelas.get(i).getParcela().getTipoparcela() != null) {
				tipoPar = listParcelas.get(i).getParcela().getTipoparcela().toString();
			} else {
				tipoPar = " ";
			}
			result.append(id + "_" + tipoMod + "_" + tipoPar + ";");
		}
		return result.toString();
	}

	private String getListCapAsegString(List<CapitalAsegurado> listCapAseg) {
		StringBuilder result = new StringBuilder("");
		for (int i = 0; i < listCapAseg.size(); i++) {
			String id = "";
			if (listCapAseg.get(i).getId() != null) {
				id = listCapAseg.get(i).getId().toString();
			} else {
				id = " ";
			}
			result.append(id + ";");
		}
		return result.toString();
	}

	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {
		logger.debug("ParcelasModificacionPolizaController.doAlta - Alta de parcela de anexo");
		Map<String, Object> parametros;
		ParcelaVO parcela = new ParcelaVO();
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU);
		String idAnexoModificacion = request.getParameter(IDANEXO_MOD);
		try {
			AnexoModificacion anexo = this.parcelasModificacionPolizaManager
					.getAnexo(Long.parseLong(idAnexoModificacion));
			capitalAseguradoModificadaBean.getParcela().setAnexoModificacion(anexo);
			parametros = cargarConfiguracionLinea(request, anexo);
			parametros.put("codUsuario", usuario.getCodusuario());
			parametros.put("origenLlamadaWS", SigpacManager.ORIGEN_LLAMADA_WS_ANEXOMOD);
			parcela.setTipoParcela(StringUtils.isNullOrEmpty(request.getParameter("tipoParcela"))
					? String.valueOf(Constants.TIPO_PARCELA_PARCELA)
					: request.getParameter("tipoParcela"));
			parametros.put("operacion",
					Constants.TIPO_PARCELA_INSTALACION.equals(parcela.getTipoParcelaChar())
							? "modificarInstalacionParcela"
							: MOD_PARC);
			String codParcela = request.getParameter(COD_PARC);
			if (Constants.TIPO_PARCELA_INSTALACION.equals(parcela.getTipoParcelaChar())) {
				ParcelaVO refParcela = this.datosParcelaAnexoManager.getParcela(Long.valueOf(codParcela));
				BeanUtils.copyProperties(parcela, refParcela);
				parcela.resetTC();
				parcela.setCodParcela("");
				parcela.setNombreParcela(refParcela.getNombreParcela());
				parcela.setTipoParcela(String.valueOf(Constants.TIPO_PARCELA_INSTALACION));
				parcela.setIdparcelaanxestructura(codParcela);
			}
			cargarConfiguracionPantalla(parametros, anexo.getPoliza().getLinea().getLineaseguroid(), parcela, false);
			/* Modif TAM (24.11.2020) ** En alta de parcelas siempre ira a false */
			parametros.put(TIENE_COB, false);
			/* Modif TAM (24.11.2020) ** Fin */
			cargarParametrosListaParcelas(parametros, request);
		} catch (Exception e) {
			logger.error("[doAlta]Se ha producido un error al dar de alta parela/inst.", e);
			parametros = new HashMap<String, Object>();
			parametros.put(ALERTA, bundle.getString(MSJ_ERR_GEN));
		}
		parametros.put("parcelaBean", parcela);
		parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
		return new ModelAndView("/moduloUtilidades/modificacionesPoliza/datosParcelaAnexo", CAP_ASEG_MOD,
				capitalAseguradoModificadaBean).addAllObjects(parametros);
	}

	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {
		logger.debug("ParcelasModificacionPolizaController.doEdita - Edicion/consulta de parcela de anexo");
		Map<String, Object> parametros;
		ParcelaVO parcelaVO;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU);
		String idAnexoModificacion = request.getParameter(IDANEXO_MOD);
		try {
			AnexoModificacion anexo = this.parcelasModificacionPolizaManager
					.getAnexo(Long.parseLong(idAnexoModificacion));
			capitalAseguradoModificadaBean.getParcela().setAnexoModificacion(anexo);
			parametros = cargarConfiguracionLinea(request, anexo);
			parametros.put("codUsuario", usuario.getCodusuario());
			parametros.put("origenLlamadaWS", SigpacManager.ORIGEN_LLAMADA_WS_ANEXOMOD);
			// Codigo de parcela
			String codParcela = request.getParameter(COD_PARC);
			parcelaVO = this.datosParcelaAnexoManager.getParcela(Long.valueOf(codParcela));
			parametros.put("operacion",
					Constants.TIPO_PARCELA_INSTALACION.equals(parcelaVO.getTipoParcelaChar())
							? "modificarInstalacionParcela"
							: MOD_PARC);
			cargarConfiguracionPantalla(parametros, anexo.getPoliza().getLinea().getLineaseguroid(), parcelaVO, false);
			parametros.put(TIENE_COB, this.datosParcelaManager.isCoberturasElegiblesNivelParcela(anexo.getPoliza().getLinea().getLineaseguroid(), "'" + anexo.getCodmodulo() + "'"));
			cargarParametrosListaParcelas(parametros, request);
			parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
		} catch (Exception e) {
			logger.error("[doAlta]Se ha producido un error al editar/visualizar parela/inst.", e);
			parametros = new HashMap<String, Object>();
			parcelaVO = new ParcelaVO();
			parametros.put(ALERTA, bundle.getString(MSJ_ERR_GEN));
		}
		parametros.put("parcelaBean", parcelaVO);
		return new ModelAndView("/moduloUtilidades/modificacionesPoliza/datosParcelaAnexo", CAP_ASEG_MOD,
				capitalAseguradoModificadaBean).addAllObjects(parametros);
	}

	/**
	 * Carga en el mapa de parametros los datos correspondientes a la linea que
	 * hay que pasar a la pantalla de parcelas
	 */
	private Map<String, Object> cargarConfiguracionLinea(final HttpServletRequest request,
			final AnexoModificacion anexo) {
		Map<String, Object> p = new HashMap<String, Object>();
		// Anade al mapa el parametro que indica si viene del listado de utilidades
		if (!StringUtils.isNullOrEmpty(request.getParameter(VIENE_UTL)))
			p.put(VIENE_UTL, request.getParameter(VIENE_UTL));
		// Anade al mapa el parametro que indica si viene de modo lectura
		if (!StringUtils.isNullOrEmpty(request.getParameter(MODO_LECT)))
			p.put(MODO_LECT, request.getParameter(MODO_LECT));
		// Anade al mapa el identificador de linea
		p.put(LINEAID, anexo.getPoliza().getLinea().getLineaseguroid());
		// Anade al mapa el codigo de plan
		p.put("codPlan", anexo.getPoliza().getLinea().getCodplan());
		// Anade al mapa el codigo de linea
		p.put("codLinea", anexo.getPoliza().getLinea().getCodlinea());
		Long claseId = this.claseManager.getClase(anexo.getPoliza().getLinea().getLineaseguroid(),
				anexo.getPoliza().getClase());
		// Anade las restricciones por clase
		p.putAll(this.datosParcelaManager.getListaCodigosLupasParcelas(claseId));
		// Anade al mapa el parametro correspondiente a la clase de la poliza
		p.put(CLASEID, claseId);
		// Anade al mapa el parametro correspondiente al nif/cif del asegurado
		p.put("nifAsegurado", anexo.getPoliza().getAsegurado().getNifcif());
		// Anade al mapa la lista de modulos seleccionados para las lupas que lo
		// puedan
		// necesitar
		// En anexos solo hay un modulo
		p.put(LST_CODMOD, anexo.getPoliza().getCodmodulo());
		// Anade al mapa la lista conceptos principales de los modulos seleccionados
		// para las lupas que lo puedan necesitar
		p.put("listCPModulos", this.seleccionPolizaManager
				.getListCodCPModulos(anexo.getPoliza().getLinea().getLineaseguroid(), anexo.getCodmodulo()));
		// Anade al mapa la lista riesgos cubiertos de los modulos seleccionados para
		// las lupas que lo puedan necesitar
		p.put("listRCModulos", this.seleccionPolizaManager
				.getListCodRCModulos(anexo.getPoliza().getLinea().getLineaseguroid(), anexo.getCodmodulo()));
		return p;
	}

	/**
	 * Carga en el mapa los parametros relativos a la pantalla configurable asociada
	 * a la linea de la poliza
	 */
	private void cargarConfiguracionPantalla(final Map<String, Object> parametros, final Long lineaseguroid,
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
					(String) parametros.get(LST_CODMOD), pantalla, parcela, calcularMascaras);
			parametros.put("listaDV", pantallaConfigurableVO.getListCampos());
			parametros.put("mustFillDVs", pantallaConfigurableVO.getListCodConceptosMascaras());
		}
		parametros.put("alturaPanelDV", alturaPanelDV);
		// Para que la lupa de tipos de capital saque los que correspondan segun el tipo
		// de la parcela
		if (!"".equals(parcela.getTipoParcela())
				&& Constants.TIPO_PARCELA_INSTALACION.equals(parcela.getTipoParcelaChar())) {
			parametros.put("filtroTipoCapitalGE", Constants.TIPOCAPITAL_INSTALACIONES_MINIMO);
		} else {
			parametros.put("filtroTipoCapitalLT", Constants.TIPOCAPITAL_INSTALACIONES_MINIMO);
		}
	}

	/**
	 * Carga en el mapa los parametros recibidos desde la pantalla de listado de
	 * parcelas
	 */
	private void cargarParametrosListaParcelas(final Map<String, Object> parametros, final HttpServletRequest request) {
		parametros.put(TIPO_LISTGRID, request.getParameter(TIPO_LISTGRID));
		parametros.put(IDROWSCHECK, request.getParameter(IDROWSCHECK));
		parametros.put(IDCAPROWSCHECK, request.getParameter(IDCAPROWSCHECK));
		parametros.put(TIPO_LISTGRID, request.getParameter(TIPO_LISTGRID));
		parametros.put("d5909046s", request.getParameter(SORT));
		parametros.put("d5909046o", request.getParameter(ORDER));
		parametros.put(VIENE_LSTANX, request.getParameter(VIENE_LSTANX));
		parametros.put(VIENE_COBANX, request.getParameter(VIENE_COBANX));
		parametros.put(MARCARTODCHK, request.getParameter(MARCARTODCHK));
		parametros.put(ISCHECKLIST, request.getParameter(ISCHECKLIST));
		parametros.put("fechaInicioContratacion", request.getParameter("fechaInicioContratacion"));
	}

	public ModelAndView doGuardarTC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("ParcelasModificacionPolizaController.doGuardarTC [INIT]");
		String lsIdStr = request.getParameter("lineaseguroid");
		String claseStr = request.getParameter("claseId");
		String nifcif = request.getParameter("nifcif");
		String parcelaStr = request.getParameter("parcela");
		String isAlreadySaved = request.getParameter("isAlreadySaved");
		
		if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(claseStr)
				|| StringUtils.isNullOrEmpty(nifcif) || StringUtils.isNullOrEmpty(parcelaStr)
				|| StringUtils.isNullOrEmpty(isAlreadySaved)) {
			errorMsgs = new String[] { "No se han recibido todos los datos de entrada." };
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
				if (errorMsgs.length == 0) {
					String operacionCapital = StringUtils.isNullOrEmpty(parcelaVO.getCapitalAsegurado().getId())
							? DatosParcelaAnexoManager.ALTA_CAPITAL_ASEGURADO
							: DatosParcelaAnexoManager.MODIFICAR_CAPITAL_ASEGURADO;
					
					/* SONAR Q */
					String operacionParcela = obtenerOperacionParcela(parcelaVO);
					/* FIN SONAR Q */
					OperationResultVO resultVO = this.datosParcelaAnexoManager.saveOrUpdateParcela(Long.valueOf(lsIdStr), parcelaVO, operacionParcela);					
					if (resultVO.getMessageErrors().size() == 0) {
						parcelaVO.setCodParcela(resultVO.getCodNuevaParcela());
						this.datosParcelaAnexoManager.saveCapitalAsegurado(Long.valueOf(lsIdStr), parcelaVO,
								parcelaVO.getCapitalAsegurado(), operacionCapital, operacionParcela);
					} else {
						errorMsgs = resultVO.getMessageErrors().toArray(new String[] {});
					}
					
					if (errorMsgs.length == 0 && !StringUtils.isNullOrEmpty(parcelaVO.getCodParcela())) {
						
						Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
						String codUsuario = usuario.getCodusuario();					
						errorMsgs = parcelasModificacionPolizaManager.actualizarParcelasCoberturas(parcelaVO, codUsuario, Long.valueOf(parcelaVO.getCodPoliza()));
					}
					/* Pet.50776_63485-Fase II ** MODIF TAM (05.11.2020) ** Fin */
				}
			}
			result.put("parcela", new JSONObject(parcelaVO));
		}
		result.put("errorMsgs", new JSONArray(errorMsgs));
		logger.debug("ParcelasModificacionPolizaController.doGuardarTC [END]");
		getWriterJSON(response, result);
		return null;
	}

	public ModelAndView doBorrarTC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("ParcelasModificacionPolizaController.doBorrarTC [INIT]");
		String idCapitalAsegurado = request.getParameter("idCapitalAsegurado");
		if (StringUtils.isNullOrEmpty(idCapitalAsegurado)) {
			errorMsgs = new String[] { NO_DATOS };
		} else {
			this.datosParcelaAnexoManager.deleteCapitalAsegurado(idCapitalAsegurado);
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSG, new JSONArray(errorMsgs));
		logger.debug("ParcelasModificacionPolizaController.doBorrarTC [END]");
		getWriterJSON(response, result);
		return null;
	}

	public ModelAndView doObtenerDatosTC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("ParcelasModificacionPolizaController.doObtenerDatosTC [INIT]");
		String idCapitalAsegurado = request.getParameter("idCapitalAsegurado");
		if (StringUtils.isNullOrEmpty(idCapitalAsegurado)) {
			errorMsgs = new String[] { NO_DATOS };
		} else {
			CapitalAseguradoVO capAsegVO = this.datosParcelaAnexoManager
					.getCapitalAsegurado(Long.valueOf(idCapitalAsegurado));
			result.put("capAseg", new JSONObject(capAsegVO));
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSG, new JSONArray(errorMsgs));
		logger.debug("ParcelasModificacionPolizaController.doObtenerDatosTC [END]");
		getWriterJSON(response, result);
		return null;
	}

	public ModelAndView doDuplicarAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("ParcelasModificacionPolizaController.doDuplicarAjax [INIT]");
		String idParcelaStr = request.getParameter("idParcela");
		if (StringUtils.isNullOrEmpty(idParcelaStr)) {
			errorMsgs = new String[] { NO_DATOS };
		} else {
			com.rsi.agp.dao.tables.anexo.Parcela clon = this.datosParcelaAnexoManager
					.clonarParcelaAnexo(Long.parseLong(idParcelaStr));
			result.put(COD_PARC, clon.getId());
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSG, new JSONArray(errorMsgs));
		logger.debug("ParcelasModificacionPolizaController.doDuplicarAjax [END]");
		getWriterJSON(response, result);
		return null;
	}

	public ModelAndView doObtenerDatosParcela(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String[] errorMsgs;
		JSONObject result = new JSONObject();
		logger.debug("ParcelasModificacionPolizaController.doObtenerDatosParcela [INIT]");
		String idParcela = request.getParameter("idParcela");
		if (StringUtils.isNullOrEmpty(idParcela)) {
			errorMsgs = new String[] { NO_DATOS };
		} else {
			ParcelaVO parcelaVO = this.datosParcelaAnexoManager.getParcela(Long.valueOf(idParcela));
			result.put(PARC, new JSONObject(parcelaVO));
			result.put("idSigParcela", "");
			errorMsgs = new String[] {};
		}
		result.put(ERROR_MSG, new JSONArray(errorMsgs));
		logger.debug("ParcelasModificacionPolizaController.doObtenerDatosParcela [END]");
		getWriterJSON(response, result);
		return null;
	}

	/**
	 * Metodo para dar de baja una o varias parcelas
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<Long> listaIdsParcelas = new ArrayList<Long>();
		String codParcela = StringUtils.nullToString(request.getParameter(COD_PARC));

		if (!codParcela.equals("")) {
			listaIdsParcelas.add(Long.parseLong(codParcela));
		} else {
			String idsRowsChecked = StringUtils.nullToString(request.getParameter(IDROWSCHECK));
			listaIdsParcelas = getListaIdsMarcados(idsRowsChecked);
			// Elimino el parametro con los ids seleccionados
			parametros.put(IDROWSCHECK, null);
		}
		// objeto para el cambio masivo
		parametros.put(ANX_CAMB_MAS, new AnexoModSWCambioMasivo());
		
		parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
		
		try {
			this.parcelasModificacionPolizaManager.establecerBajaParcelas(listaIdsParcelas,
					capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getId());
			parametros.put(MENSAJE, bundle.getString("mensaje.baja.OK"));

		} catch (BusinessException be) {
			logger.error("[doBaja]Se ha producido un error durante la baja de la parcela/inst. del anexo", be);
			parametros.put(ALERTA, bundle.getString("mensaje.baja.KO"));
		} catch (Exception ex) {
			logger.error("[doBaja]Se ha producido un error durante la baja de la parcela/inst. del anexo", ex);
			parametros.put(ALERTA, bundle.getString("mensaje.baja.KO"));
		}
		return doConsulta(request, response, capitalAseguradoModificadaBean).addAllObjects(parametros);
	}

	/**
	 * Metodo para deshacer los cambios sobre una o varias parcelas.
	 */
	public ModelAndView doDeshaz(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {

		Map<String, Object> parametros = new HashMap<String, Object>();
		List<Long> listaIds = new ArrayList<Long>();

		Usuario usuario = (Usuario) request.getSession().getAttribute(USU);

		String idAnexo = StringUtils.nullToString(request.getParameter(IDANEXO));
		if (idAnexo.equals(""))
			idAnexo = StringUtils.nullToString(request.getParameter(IDANEXO_MOD));

		String codParcela = request.getParameter(COD_PARC);

		if (!codParcela.equals("")) {
			// Deshacer solo una parcela
			listaIds.add(Long.parseLong(codParcela));
		} else {
			// Deshacer masivo
			String idsRowsChecked = StringUtils.nullToString(request.getParameter(IDROWSCHECK));
			listaIds = getListaIdsMarcados(idsRowsChecked);
			// Elimino el parametro con los ids seleccionados
			parametros.put(IDROWSCHECK, null);
		}

		try {
			// El deshacer hay que hacerlo en orden si no se pierden los ids de las
			// instalaciones
			// deshago las instalaciones
			if (listaIds.size() > 0) {
				this.parcelasModificacionPolizaManager.deshacerCambiosParcelas(Long.parseLong(idAnexo), listaIds,
						usuario.getCodusuario());
			}

			parametros.put(MENSAJE, bundle.getString("mensaje.deshacer.OK"));

			if (capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getCupon() != null)
				parametros.put(IDCUPON, StringUtils.nullToString(
						capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getCupon().getId()));
			
			parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));

		} catch (BusinessException be) {
			logger.error(
					"[doDeshaz]Se ha producido un error deshaciendo los cambios en las parcelas/inst. de anexo de modificacion",
					be);
			parametros.put(ALERTA, bundle.getString(MSJ_DESHACER));
		} catch (ValidacionAnexoModificacionException e) {
			logger.error("[doDeshaz]Se ha producido un error de validaciÃ³n del anexo de modificacion", e);
			parametros.put(ALERTA, bundle.getString(MSJ_DESHACER));
		} catch (DAOException e) {
			logger.error(
					"[doDeshaz]Se ha producido un error de acceso a datos deshaciendo los cambios en las parcelas/inst. de anexo de modificacion",
					e);
			parametros.put(ALERTA, bundle.getString(MSJ_DESHACER));
		} catch (Exception ex) {
			logger.error(
					"[doDeshaz]Se ha producido un error deshaciendo los cambios en las parcelas/inst. de anexo de modificacion",
					ex);
			parametros.put(ALERTA, bundle.getString(MSJ_DESHACER));
		}

		return doConsulta(request, response, capitalAseguradoModificadaBean).addAllObjects(parametros);
	}

	/**
	 * Redirige a la pantalla anterior: coberturas
	 */
	public ModelAndView doVolver(HttpServletRequest request, HttpServletResponse response) {
		// recogemos el id de poliza y redireccionamos al doConsulta del controlador de
		// coberturas modificacion
		String idAnexo = StringUtils.nullToString(request.getParameter(IDANEXO_MOD));
		String idPoliza = StringUtils.nullToString(request.getParameter(IDPOLIZA));
		String modoLectura = StringUtils.nullToString(request.getParameter(MODO_LECT));
		String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getParameter(VIENE_LSTANX));

		/***
		 * DNF PET.63485.FIII 15/01/2021 necesito saber el codmodulo del anexo para
		 * cargarlo en el combo
		 */
		AnexoModificacion anexoMod = new AnexoModificacion();
		try {
			anexoMod = this.declaracionesModificacionPolizaManager.getAnexoModifById(new Long(idAnexo));
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
		/*** fin DNF PET.63485.FIII 15/01/2021 */

		return new ModelAndView("redirect:/coberturasModificacionPoliza.html").addObject("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado")).addObject(IDANEXO, idAnexo)
				.addObject(IDPOLIZA, idPoliza).addObject(MODO_LECT, modoLectura)
				.addObject(VIENE_LSTANX, vieneDeListadoAnexosMod).addObject("codModuloAnexo", anexoMod.getCodmodulo());
	}

	/**
	 * Redirige a la pantalla anterior: declaraciones anexos
	 */
	public ModelAndView doVolverAnexo(HttpServletRequest request, HttpServletResponse response) {
		// recogemos el id de poliza y redireccionamos al doConsulta del controlador de
		// coberturas modificacion

		String idPoliza = StringUtils.nullToString(request.getParameter(IDPOLIZA));
		String modoLectura = StringUtils.nullToString(request.getParameter(MODO_LECT));

		return new ModelAndView("redirect:/declaracionesModificacionPoliza.html").addObject(IDPOLIZA, idPoliza)
				.addObject(MODO_LECT, modoLectura);
	}

	public ModelAndView doImprimirInformeListadoParcelasAnexo(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {

		logger.debug("doImprimirInformeListadoParcelasAnexo - Inicio");

		ModelAndView mv = null;

		try {
			String tipoListadoGrid = request.getParameter(TIPO_LISTGRID);
			/* SONAR Q */
			capitalAseguradoModificadaBean = asignarTipoParcela(tipoListadoGrid, capitalAseguradoModificadaBean);
			getOrdenacionDisplaytag(request);
			/* SONAR Q */

			String columna = (String) request.getSession().getAttribute(COLUMN);
			String orden = (String) request.getSession().getAttribute(ORDEN);

			String idAnexo = StringUtils.nullToString(request.getParameter(IDANEXO));
			if (idAnexo.equals("")) {
				idAnexo = StringUtils.nullToString(request.getParameter(IDANEXO_MOD));
			}
			capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().setId(new Long(idAnexo));

			List<CapitalAsegurado> listaCapitalesAseguradosAnexo = this.parcelasModificacionPolizaManager
					.getParcelasAnexo(capitalAseguradoModificadaBean, columna, orden);
			List<BeanParcelaAnexo> listParcelasAnexo = obtenerListaBeanParcelaAnexo(listaCapitalesAseguradosAnexo);
			request.setAttribute("listaParcelasAnexo", listParcelasAnexo);
			request.setAttribute("esPrincipal", true);
			mv = new ModelAndView("forward:/informes.html?method=doInformeListadoParcelasAnexo");
		} catch (Exception e) {
			logger.error("Error");
		}
		return mv;
	}

	private List<BeanParcelaAnexo> obtenerListaBeanParcelaAnexo(List<CapitalAsegurado> listaCapitalesAseguradosAnexo) {
		List<BeanParcelaAnexo> listParcelasAnexo = new ArrayList<BeanParcelaAnexo>();
		ModelTableDecoratorParcelasModificadas decorator = new ModelTableDecoratorParcelasModificadas();

		Iterator<CapitalAsegurado> it = listaCapitalesAseguradosAnexo.iterator();
		while (it.hasNext()) {
			CapitalAsegurado capitalAsegurado = it.next();
			
			/* SONAR Q */
			BeanParcelaAnexo bpa = informarParcelaBeanAnexo(capitalAsegurado, decorator);
			/* FIN SONAR Q */
			
			listParcelasAnexo.add(bpa);
		}
		return listParcelasAnexo;
	}

	/**
	 * Devuelve una lista de Long con los check's seleccionados en el jsp
	 * 
	 * @param idsRowsChecked
	 *            Identificadores de parcelas marcadas separados por ";"
	 * @return Lista con los identificadores
	 */
	private List<Long> getListaIdsMarcados(String idsRowsChecked) {

		List<Long> listaIdsParcelasModificadas = new ArrayList<Long>();

		StringTokenizer tokens = new StringTokenizer(idsRowsChecked, ";");
		while (tokens.hasMoreTokens()) {
			listaIdsParcelasModificadas.add(Long.valueOf(tokens.nextToken()));
		}

		return listaIdsParcelasModificadas;
	}

	private void getOrdenacionDisplaytag(final HttpServletRequest request) {
		/* SONAR Q */
		/* Añadimos este if dentro de esta propia función */

		// Si PARAMETER_SORT y PARAMETER_ORDER no son nulos he pinchado en ordenar
		if (!StringUtils.nullToString(request.getParameter(SORT)).equals("")
				&& !StringUtils.nullToString(request.getParameter(ORDER)).equals("")) {

			String columna = request.getParameter(SORT); // PARAMETER_SORT
			String orden = null;
			if (("2").equals(request.getParameter(ORDER))) { // PARAMETER_ORDER
				orden = "asc";
			}
			if (("1").equals(request.getParameter(ORDER))) {
				orden = "desc";
			}

			// Guardo la ordenacion en sesion:
			request.getSession().setAttribute(ORDEN, orden);
			request.getSession().setAttribute(COLUMN, columna);
		}
	}

	public ModelAndView doCalculoRdtoHist(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {
		
		List<CapitalAsegurado> listaCapitalesAseguradosAnexo = null;
		List<CapitalAsegurado> listaCapitalesAseguradosAnexoFiltradas = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		String idPoliza = StringUtils.nullToString(request.getParameter(IDPOLIZA));
		String modoLectura = StringUtils.nullToString(request.getParameter(MODO_LECT));
		String tipoListadoGrid = request.getParameter(TIPO_LISTGRID);
		Poliza poliza = null;
		String itemCombo = "T";
		String idsRowsChecked = StringUtils.nullToString(request.getParameter(IDROWSCHECK));
		String idsCapAsegRowsChecked = StringUtils.nullToString(request.getParameter(IDCAPROWSCHECK));
		String marcarTodosChecks = StringUtils.nullToString(request.getParameter(MARCARTODCHK));
		String isClickInListado = StringUtils.nullToString(request.getParameter(ISCHECKLIST));
		Long estadoCupon = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU);
		String rutaWebInfEncod = null;
		
		try {
			// Indica la ruta a 'WEB-INF'
			rutaWebInfEncod = URLEncoder.encode(this.getServletContext().getRealPath("/WEB-INF/"), "UTF-8");
			parametros.put("realPath", rutaWebInfEncod);

		} catch (UnsupportedEncodingException e) {
			logger.error("Error al hacer el encoding de la ruta de Web-Inf", e);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
		/* SONARQ */
		String idAnexo = obteneridAnexo(request);
		parametros = informarParameter(request, parametros);
		idsRowsChecked = obtenerIdsRowsChecked(idsRowsChecked, isClickInListado);
		idsCapAsegRowsChecked = obtenerIdsCapAsegRowsChecked(idsCapAsegRowsChecked, isClickInListado);
		/* SONARQ */

		// Por si vengo de la pantalla de parcelas, saber en que tipo listado estaba
		// antes y si esta modoLectura
		HttpSession session = request.getSession(true);
		
		/* SONAR Q */
		tipoListadoGrid = obtenertipoListadoGrid(session);
		/* SONAR Q */

		session.setAttribute(TIPO_LIST_ANEXO, "");

		capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().setId(new Long(idAnexo));
		// AMG 19/02/2014 guado en sesion el filtro para los ids de parcela en la
		// pantalla de parcelas
		session.setAttribute("parcelaAnexoBusqueda", capitalAseguradoModificadaBean.getParcela());
		
		/* SONARQ */
		String idCupon = obteneridCupon(capitalAseguradoModificadaBean, request);
		String idCuponStr = obteneridCuponStr(capitalAseguradoModificadaBean, request);
		/* SONARQ */

		try {
			
			/* SONAR Q */
			capitalAseguradoModificadaBean = asignarTipoParcela(tipoListadoGrid, capitalAseguradoModificadaBean);
			/* SONAR Q */

			AnexoModificacion anexo = null;
			// Recuperamos la poliza para mostrarlos en la cabecera de la pantalla
			if (!idPoliza.equals("")) {
				poliza = this.declaracionesModificacionPolizaManager.getPoliza(new Long(idPoliza));
			} else {
				anexo = this.declaracionesModificacionPolizaManager.getAnexoModifById(new Long(idAnexo));
				poliza = anexo.getPoliza();

				

				/* SONAR Q */
				// Guarda el estado del cupon para comprobar si hay que mostrar el boton de
				// 'Importes' en la pantalla
				estadoCupon = obtenerEstadoCupon(anexo, estadoCupon);
				
				modoLectura = obtenerModoLectura(anexo, modoLectura);
				/* SONAR Q */
			}
			anexo = this.declaracionesModificacionPolizaManager.getAnexoModifById(new Long(idAnexo));
			List<CapitalAsegurado> listaCapitalesAseguradosAnexoFiltradasRecalculados = null;
			if (!StringUtils.nullToString(idAnexo).equals("")) {
				
				/* SONAR Q */
				copiarParcelas(idCupon, idAnexo, poliza);
				getOrdenacionDisplaytag(request);
				/* SONAR Q */

				String columna = (String) request.getSession().getAttribute(COLUMN);
				String orden = (String) request.getSession().getAttribute(ORDEN);
				
				listaCapitalesAseguradosAnexo = this.parcelasModificacionPolizaManager
						.getParcelasAnexo(capitalAseguradoModificadaBean, columna, orden);
				
				listaCapitalesAseguradosAnexoFiltradas = filtraPorTipoRdto(listaCapitalesAseguradosAnexo,
						capitalAseguradoModificadaBean.getTipoRdto());
				
				/* SONAR Q */
				// recuperar la lista de parcelas seleccionadas en la pantalla
				Set<Long> idsPacelas = new HashSet<Long>(0);
				idsPacelas = obteneridsParcela(idsRowsChecked);
				logger.debug("Valor de idsPacelas: " + idsPacelas.toString());
				/* SONAR Q */

				try {
					listaCapitalesAseguradosAnexoFiltradasRecalculados = this.seleccionPolizaManager.calculoRtoHist(
							idsPacelas, rutaWebInfEncod, usuario, 0, listaCapitalesAseguradosAnexoFiltradas, anexo);

				} catch (es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException e) {
					logger.error("[doCalculoRdtoHist] Se ha producido un error (AgrException) durante el proceso", e);
					parametros.put("alertaLargo", WSUtils.debugAgrException(e));
				}

				List<TipoRdto> listaTipoRendimientos = this.seleccionPolizaManager.getTiposRendimiento();
				parametros.put("listaTipoRendimientos", listaTipoRendimientos);
				Clase clase = new Clase();
				clase = this.claseManager.getClase(poliza);
				boolean tieneRdtoHist = Constants.TIENE_RDTO_HISTORICO.equals(clase.getRdtoHistorico())
						&& clase.getLinea().getCodlinea().equals(new BigDecimal(300));
				parametros.put("tieneRdtoHist", tieneRdtoHist);

				parametros.put(IDANEXO, idAnexo);
			}

			parametros.put("listaParcelasModificadas", listaCapitalesAseguradosAnexoFiltradasRecalculados);

			
			/* SONAR Q */
			// Se obtiene el estado del cupon del anexo si no se ha hecho ya antex
			estadoCupon = obtenerEstadoCupon(idAnexo, estadoCupon, anexo);
			
			parametros = cargarLineaId(parametros, poliza);
			/* SONAR Q */
			
				parametros.put("numParcelasListado",
					listaCapitalesAseguradosAnexo != null ? listaCapitalesAseguradosAnexo.size() : 0);
			parametros.put(MARCARTODCHK, marcarTodosChecks);
			parametros.put("parcelasString",
					listaCapitalesAseguradosAnexo != null ? getListParcelasString(listaCapitalesAseguradosAnexo)
							: null);
			// ESC-16099 ** MODIF TAM (29.11.2021) ** Inicio //
			//parametros.put(IDROWSCHECK, idsRowsChecked);
			//parametros.put(IDCAPROWSCHECK, idsCapAsegRowsChecked);
			
			parametros.put("poliza", poliza);
			parametros.put("itemCombo", itemCombo);
			parametros.put(TIPO_LISTGRID, tipoListadoGrid);
			parametros.put(MODO_LECT, modoLectura);
			parametros.put("anexoModificacionBean", new AnexoModificacion(Long.parseLong(idAnexo)));
			parametros.put("mostrarImportes", Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.equals(estadoCupon)
					|| Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.equals(estadoCupon));
		}

		catch (BusinessException be) {
			logger.error("[doCalculoRdtoHist] Se ha producido un error (BusinessException) durante el proceso", be);
			parametros.put(ALERTA, bundle.getString(MSJ_ERR_GEN));
		} catch (Exception ex) {
			logger.error("[doCalculoRdtoHist] Se ha producido un error durante el proceso ", ex);
			parametros.put(ALERTA, bundle.getString(MSJ_ERR_GEN));
		}
		
		// TMR 3-9-2012 Utilidades anexos de modificacion
		String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getParameter(VIENE_LSTANX));
		if ("true".equals(vieneDeListadoAnexosMod)) {
			parametros.put(VIENE_LSTANX, vieneDeListadoAnexosMod);
		}
		
		String vieneDeCoberturasAnexo = StringUtils.nullToString(request.getAttribute(VIENE_COBANX));
		
		/* SONAR Q */
		String clearStatus = obtenerclearStatus(vieneDeCoberturasAnexo);
		/* SONAR Q */
		
		/* ESC-15981 ** MODIF TAM (22.11.2021) ** Inicio */
		parametros.put("capAsegString",
				listaCapitalesAseguradosAnexo != null ? getListCapAsegString(listaCapitalesAseguradosAnexo) : null);
		/* ESC-15981 ** MODIF TAM (22.11.2021) ** Fin */
		
		parametros.put("clearStatus", clearStatus);

		if (!StringUtils.nullToString(idCupon).equals("")) {
			parametros.put(IDCUPONSTR, idCuponStr);
			// objeto para el cambio masivo
			parametros.put(ANX_CAMB_MAS, new AnexoModSWCambioMasivo());
			capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getCupon().setIdcupon(idCuponStr);
			capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getCupon().setId(new Long(idCupon));
			// Redirigimos a la pantalla de parcelas de anexos de tipo SW (con cupon)
			return new ModelAndView("/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacionSw", CAP_ASEG_MOD,
					capitalAseguradoModificadaBean).addAllObjects(parametros);
		} else {
			// Redirigimos a la pantalla de parcelas de anexos de tipo FTP
			return new ModelAndView("/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacion", CAP_ASEG_MOD,
					capitalAseguradoModificadaBean).addAllObjects(parametros);
		}
	}

	/* Pet. 78877 ** MODIF TAM (04.11.2021) ** Inicio */
	public ModelAndView doCalcRdtoOrientativo(HttpServletRequest request, HttpServletResponse response,
			CapitalAsegurado capitalAseguradoModificadaBean) {

		logger.debug("ParcelasModificacionPolizaController - doCalcRdtoOrientativo [INIT]");

		List<CapitalAsegurado> listaCapitalesAseguradosAnexo = null;
		List<CapitalAsegurado> listaCapitalesAseguradosAnexoFiltradas = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		String idPoliza = StringUtils.nullToString(request.getParameter(IDPOLIZA));
		String modoLectura = StringUtils.nullToString(request.getParameter(MODO_LECT));
		String tipoListadoGrid = request.getParameter(TIPO_LISTGRID);
		Poliza poliza = null;
		String itemCombo = "T";
		String idsRowsChecked = StringUtils.nullToString(request.getParameter(IDROWSCHECK));
		String idsCapAsegRowsChecked = StringUtils.nullToString(request.getParameter(IDCAPROWSCHECK));
		String marcarTodosChecks = StringUtils.nullToString(request.getParameter(MARCARTODCHK));
		String isClickInListado = StringUtils.nullToString(request.getParameter(ISCHECKLIST));
		Long estadoCupon = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute(USU);
		String rutaWebInfEncod = null;

		try {
			// Indica la ruta a 'WEB-INF'
			rutaWebInfEncod = URLEncoder.encode(this.getServletContext().getRealPath("/WEB-INF/"), "UTF-8");
			parametros.put("realPath", rutaWebInfEncod);

		} catch (UnsupportedEncodingException e) {
			logger.error("Error al hacer el encoding de la ruta de Web-Inf", e);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		/* SONARQ */
		String idAnexo = obteneridAnexo(request);
		parametros = informarParameter(request, parametros);
		idsRowsChecked = obtenerIdsRowsChecked(idsRowsChecked, isClickInListado);
		idsCapAsegRowsChecked = obtenerIdsCapAsegRowsChecked(idsCapAsegRowsChecked, isClickInListado);
		/* SONARQ */

		// Por si vengo de la pantalla de parcelas, saber en que tipo listado estaba
		// antes y si esta modoLectura
		HttpSession session = request.getSession(true);

		/* SONAR Q */
		tipoListadoGrid = obtenertipoListadoGrid(session);
		/* SONAR Q */

		session.setAttribute(TIPO_LIST_ANEXO, "");

		capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().setId(new Long(idAnexo));
		// AMG 19/02/2014 guado en sesion el filtro para los ids de parcela en la
		// pantalla de parcelas
		session.setAttribute("parcelaAnexoBusqueda", capitalAseguradoModificadaBean.getParcela());

		/* SONARQ */
		String idCupon = obteneridCupon(capitalAseguradoModificadaBean, request);
		String idCuponStr = obteneridCuponStr(capitalAseguradoModificadaBean, request);
		/* SONARQ */

		try {

			/* SONAR Q */
			capitalAseguradoModificadaBean = asignarTipoParcela(tipoListadoGrid, capitalAseguradoModificadaBean);
			/* SONAR Q */

			AnexoModificacion anexo = null;
			// Recuperamos la poliza para mostrarlos en la cabecera de la pantalla
			if (!idPoliza.equals("")) {
				poliza = this.declaracionesModificacionPolizaManager.getPoliza(new Long(idPoliza));
			} else {
				anexo = this.declaracionesModificacionPolizaManager.getAnexoModifById(new Long(idAnexo));
				poliza = anexo.getPoliza();

				/* SONAR Q */
				// Guarda el estado del cupon para comprobar si hay que mostrar el boton de
				// 'Importes' en la pantalla
				estadoCupon = obtenerEstadoCupon(anexo, estadoCupon);
				
				modoLectura = obtenerModoLectura(anexo, modoLectura);
				/* SONAR Q */
			}

			anexo = this.declaracionesModificacionPolizaManager.getAnexoModifById(new Long(idAnexo));
			List<CapitalAsegurado> listaCapitalesAseguradosAnexoFiltradasRecalculados = null;
			if (!StringUtils.nullToString(idAnexo).equals("")) {

				/* SONAR Q */
				copiarParcelas(idCupon, idAnexo, poliza);
				getOrdenacionDisplaytag(request);
				/* SONAR Q */

				String columna = (String) request.getSession().getAttribute(COLUMN);
				String orden = (String) request.getSession().getAttribute(ORDEN);

				listaCapitalesAseguradosAnexo = this.parcelasModificacionPolizaManager
						.getParcelasAnexo(capitalAseguradoModificadaBean, columna, orden);

				listaCapitalesAseguradosAnexoFiltradas = filtraPorTipoRdto(listaCapitalesAseguradosAnexo,
						capitalAseguradoModificadaBean.getTipoRdto());
				

				/* SONAR Q */
				// recuperar la lista de parcelas seleccionadas en la pantalla
				Set<Long> idsPacelas = new HashSet<Long>(0);
				idsPacelas = obteneridsParcela(idsRowsChecked);
				logger.debug("Valor de idsPacelas: " + idsPacelas.toString());
				/* SONAR Q */

				try {
					listaCapitalesAseguradosAnexoFiltradasRecalculados = this.seleccionPolizaManager.calculoRtoOrient(
							idsPacelas, rutaWebInfEncod, usuario, 0, listaCapitalesAseguradosAnexoFiltradas, anexo);

				} catch (es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException e) {
					logger.error("[doCalcRdtoOrientativo] Se ha producido un error (AgrException) durante el proceso",
							e);
					parametros.put("alertaLargo", WSUtils.debugAgrException(e));
				}

				List<TipoRdto> listaTipoRendimientos = this.seleccionPolizaManager.getTiposRendimiento();
				parametros.put("listaTipoRendimientos", listaTipoRendimientos);
				Clase clase = new Clase();
				clase = this.claseManager.getClase(poliza);
				boolean tieneRdtoHist = Constants.TIENE_RDTO_HISTORICO.equals(clase.getRdtoHistorico())
						&& clase.getLinea().getCodlinea().equals(new BigDecimal(300));
				parametros.put("tieneRdtoHist", tieneRdtoHist);

				parametros.put(IDANEXO, idAnexo);
			}

			parametros.put("listaParcelasModificadas", listaCapitalesAseguradosAnexoFiltradasRecalculados);

			/* SONAR Q */
			estadoCupon = obtenerEstadoCupon(idAnexo, estadoCupon, anexo);
			parametros = cargarLineaId(parametros, poliza);
			/* SONAR Q */

			parametros.put("numParcelasListado",
					listaCapitalesAseguradosAnexo != null ? listaCapitalesAseguradosAnexo.size() : 0);
			parametros.put(MARCARTODCHK, marcarTodosChecks);
			parametros.put("parcelasString",
					listaCapitalesAseguradosAnexo != null ? getListParcelasString(listaCapitalesAseguradosAnexo)
							: null);
			// ESC-16099 ** MODIF TAM (29.11.2021) ** Inicio //
			//parametros.put(IDROWSCHECK, idsRowsChecked);
			//parametros.put(IDCAPROWSCHECK, idsCapAsegRowsChecked);
			parametros.put("poliza", poliza);
			parametros.put("itemCombo", itemCombo);
			parametros.put(TIPO_LISTGRID, tipoListadoGrid);
			parametros.put(MODO_LECT, modoLectura);
			parametros.put("anexoModificacionBean", new AnexoModificacion(Long.parseLong(idAnexo)));
			parametros.put("mostrarImportes", Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.equals(estadoCupon)
					|| Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.equals(estadoCupon));
			
			/* ESC-15981 ** MODIF TAM (22.11.2021) ** Inicio */
			parametros.put("capAsegString",
					listaCapitalesAseguradosAnexo != null ? getListCapAsegString(listaCapitalesAseguradosAnexo) : null);
			/* ESC-15981 ** MODIF TAM (22.11.2021) ** Fin */
		}

		catch (BusinessException be) {
			logger.error("[doCalcRdtoOrientativo] Se ha producido un error (BusinessException) durante el proceso", be);
			parametros.put(ALERTA, bundle.getString(MSJ_ERR_GEN));
		} catch (Exception ex) {
			logger.error("[doCalcRdtoOrientativo] Se ha producido un error durante el proceso ", ex);
			parametros.put(ALERTA, bundle.getString(MSJ_ERR_GEN));
		}

		// TMR 3-9-2012 Utilidades anexos de modificacion
		String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getParameter(VIENE_LSTANX));
		if ("true".equals(vieneDeListadoAnexosMod)) {
			parametros.put(VIENE_LSTANX, vieneDeListadoAnexosMod);
		}

		String vieneDeCoberturasAnexo = StringUtils.nullToString(request.getAttribute(VIENE_COBANX));
		/* SONAR Q */
		String clearStatus = obtenerclearStatus(vieneDeCoberturasAnexo);
		/* SONAR Q */

		parametros.put("clearStatus", clearStatus);

		logger.debug("ParcelasModificacionPolizaController - doCalcRdtoOrientativo [END]");

		if (!StringUtils.nullToString(idCupon).equals("")) {

			parametros.put(IDCUPONSTR, idCuponStr);
			// objeto para el cambio masivo
			parametros.put(ANX_CAMB_MAS, new AnexoModSWCambioMasivo());
			capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getCupon().setIdcupon(idCuponStr);
			capitalAseguradoModificadaBean.getParcela().getAnexoModificacion().getCupon().setId(new Long(idCupon));
			// Redirigimos a la pantalla de parcelas de anexos de tipo SW (con cupon)
			return new ModelAndView("/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacionSw", CAP_ASEG_MOD,
					capitalAseguradoModificadaBean).addAllObjects(parametros);
		} else {
			// Redirigimos a la pantalla de parcelas de anexos de tipo FTP
			return new ModelAndView("/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacion", CAP_ASEG_MOD,
					capitalAseguradoModificadaBean).addAllObjects(parametros);
		}
	}
	/* Pet. 78877 ** MODIF TAM (04.11.2021) ** Fin */

	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Inicio */
	/**
	 * Obtenemos la tabla de coberturas de la parcela
	 * 
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

		logger.debug("ParcelasModificacionPolizaController - doCargarCoberturasParcela [INIT]");

		Usuario usuario = (Usuario) request.getSession().getAttribute(USU);

		String lsIdStr = request.getParameter(LINEAID);
		String claseStr = request.getParameter(CLASEID);
		String nifcif = request.getParameter("nifcif");
		String parcelaStr = request.getParameter(PARC);

		Boolean isCoberturasParc = true;

		List<ParcelasCoberturasNew> lstCobParc = null;

		if (isCoberturasParc) {
			String realPath = this.getServletContext().getRealPath("/WEB-INF/");

			if (StringUtils.isNullOrEmpty(lsIdStr) || StringUtils.isNullOrEmpty(claseStr)
					|| StringUtils.isNullOrEmpty(nifcif) || StringUtils.isNullOrEmpty(parcelaStr)) {
				errorMsgs = new String[] { NO_DATOS };
			} else {

				ParcelaVO parcelaVO = ParcelaUtil.getParcelaVO(new JSONObject(parcelaStr));

				/* SONAR Q */
				String codParcela = obtenercodParcela(request, parcelaVO);
				/* FIN SONAR Q */

				/* si la parcela Existe */
				if (!StringUtils.isNullOrEmpty(codParcela)) {

					logger.debug("IdAnexoModificacion: " + parcelaVO.getIdAnexoModificacion());
					
					boolean isCoberturas = false;					
					if (StringUtils.isNullOrEmpty(parcelaVO.getIdAnexoModificacion())) {
						Poliza plz = this.datosParcelaManager.getPoliza(new Long(parcelaVO.getCodPoliza()));
						if (StringUtils.isNullOrEmpty(plz.getCodmodulo())) {
							Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();
							isCoberturas = this.datosParcelaManager.isCoberturasElegiblesNivelParcela(plz.getLinea().getLineaseguroid(), modsPoliza);
						} else {
							isCoberturas = this.datosParcelaManager.isCoberturasElegiblesNivelParcela(plz.getLinea().getLineaseguroid(), "'" + plz.getCodmodulo() + "'");
						}
					} else {
						AnexoModificacion am = this.declaracionesModificacionPolizaManager.getAnexoModifById(Long.valueOf(parcelaVO.getIdAnexoModificacion()));
						isCoberturas = this.datosParcelaManager.isCoberturasElegiblesNivelParcela(am.getPoliza().getLinea().getLineaseguroid(), "'" + am.getCodmodulo() + "'");						
					}
					
					result.put("isCoberturas", isCoberturas);
					result.put(TIENE_COB, isCoberturas);
					
					/* SONAR Q */
					lstCobParc = obtenerListCobParc(isCoberturas, parcelaVO, codParcela, usuario, realPath);
					/* FIN SONAR Q */
				} else {

					/* SONAR Q */
					AnexoModificacion anexo  = obtenerAnexo(parcelaVO);
					/* FIN SONAR Q*/ 

					com.rsi.agp.dao.tables.anexo.Parcela parcela = new com.rsi.agp.dao.tables.anexo.Parcela();
					this.parcelasModificacionPolizaManager.setParcelaAnxVO(parcelaVO, parcela, "altaParcela", anexo);

					Poliza plz = this.datosParcelaManager.getPoliza(new Long(parcelaVO.getCodPoliza()));
					Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();

					Boolean isCoberturas = this.datosParcelaManager
							.isCoberturasElegiblesNivelParcela(plz.getLinea().getLineaseguroid(), modsPoliza);
					result.put("isCoberturas", isCoberturas);

					/* SONAR Q */
					lstCobParc = obtenerCobParcelas(parcela, parcelaVO,
								usuario, realPath, Long.parseLong(parcelaVO.getIdAnexoModificacion()), lstCobParc, isCoberturas);
					/* FIN SONAR Q */
					
				} /* Fin del if de codParcela */

			}

		}

		result.put(ERROR_MSG, new JSONArray(errorMsgs));
		result.put("listaCobParcelas", new JSONArray(lstCobParc));

		logger.debug("ParcelasModificacionPolizaController - doCargarCoberturasParcela [END]");

		getWriterJSON(response, result);
		return null;

	}

	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Fin */

	private List<CapitalAsegurado> filtraPorTipoRdto(List<CapitalAsegurado> listaCapitalesAseguradosAnexo,
			Long tipoRdto) {
		List<CapitalAsegurado> listaCapitalesAseguradosAnexoFiltradas = new ArrayList<CapitalAsegurado>();
		if (tipoRdto != null && tipoRdto != 0) {
			for (CapitalAsegurado cap : listaCapitalesAseguradosAnexo) {
				if (null != cap.getTipoRdto() && tipoRdto.equals(cap.getTipoRdto())) {
					listaCapitalesAseguradosAnexoFiltradas.add(cap);
				}
			}

		} else {
			listaCapitalesAseguradosAnexoFiltradas = listaCapitalesAseguradosAnexo;
		}
		return listaCapitalesAseguradosAnexoFiltradas;
	}

	/** SONAR Q ** MODIF TAM(04.11.2021) ** Inicio **/
	/** Añadimos nuevos método para descargar de ifs/for */
	private String obteneridCupon(CapitalAsegurado capAsegModifBean, HttpServletRequest request) {
		String idCupon = "";

		if (capAsegModifBean.getParcela().getAnexoModificacion().getCupon() != null) {
			idCupon = StringUtils.nullToString(capAsegModifBean.getParcela().getAnexoModificacion().getCupon().getId());

			if (StringUtils.nullToString(idCupon).equals("")) {
				idCupon = StringUtils.nullToString(request.getParameter(IDCUPON));
			}
			if (StringUtils.nullToString(idCupon).equals("")) {
				idCupon = StringUtils.nullToString(request.getAttribute(IDCUPON));
			}

		}
		return idCupon;
	}

	private String obteneridCuponStr(CapitalAsegurado capAsegModifBean, HttpServletRequest request) {

		String idCuponStr = "";

		if (capAsegModifBean.getParcela().getAnexoModificacion().getCupon() != null) {
			idCuponStr = StringUtils
					.nullToString(capAsegModifBean.getParcela().getAnexoModificacion().getCupon().getIdcupon());

			if (StringUtils.nullToString(idCuponStr).equals("")) {
				idCuponStr = StringUtils.nullToString(request.getParameter(IDCUPONSTR));
			}
		}
		return idCuponStr;
	}

	private String obteneridAnexo(HttpServletRequest request) {
		String idAnexo = StringUtils.nullToString(request.getParameter(IDANEXO));

		if (idAnexo.equals(""))
			idAnexo = StringUtils.nullToString(request.getParameter(IDANEXO_MOD));

		if (idAnexo.equals(""))
			idAnexo = StringUtils.nullToString(request.getAttribute(IDANEXO));

		return idAnexo;
	}

	private Map<String, Object> informarParameter(HttpServletRequest request, Map<String, Object> parametros) {

		if (request.getParameter(ALERTA) != null)
			parametros.put(ALERTA, request.getParameter(ALERTA));
		if (request.getParameter(ALERTA2) != null)
			parametros.put(ALERTA2, request.getParameter(ALERTA2));
		if (request.getParameter(MENSAJE) != null)
			parametros.put(MENSAJE, request.getParameter(MENSAJE));

		return parametros;
	}

	private Set<Long> obteneridsParcela(String idsRowsChecked) {
		Set<Long> idsParcelas = new HashSet<Long>(0);

		if (idsRowsChecked != null) {
			String[] arrParcelas = idsRowsChecked.split(";");

			for (int i = 0; i < arrParcelas.length; i++) {
				if (!arrParcelas[i].isEmpty()) {
					idsParcelas.add(new Long(arrParcelas[i]));
				}
			}
		}
		return idsParcelas;
	}

	private String obtenertipoListadoGrid(HttpSession session) {
		String tipoListadoPorSesion = "";
		String tipoListadoGrid = "";

		// Por si vengo de la pantalla de parcelas, saber en que tipo listado estaba
		// antes y si esta modoLectura
		if (session.getAttribute(TIPO_LIST_ANEXO) != null) {
			tipoListadoPorSesion = (String) session.getAttribute(TIPO_LIST_ANEXO);
			if (!"".equals(tipoListadoPorSesion)) {
				tipoListadoGrid = tipoListadoPorSesion;
			}
		}

		if (StringUtils.nullToString(tipoListadoGrid).equals("")) {
			tipoListadoGrid = "todas";
		}
		return tipoListadoGrid;
	}

	private CapitalAsegurado asignarTipoParcela(String tipoListadoGrid, CapitalAsegurado capAsegModifBean) {
		if (tipoListadoGrid.equals(PARCELA)) {
			capAsegModifBean.getParcela().setTipoparcela('P');
		} else if (tipoListadoGrid.equals(INSTALACIONES)) {
			capAsegModifBean.getParcela().setTipoparcela('E');
		}
		return capAsegModifBean;
	}

	private void copiarParcelas(String idCupon, String idAnexo, Poliza poliza)
			throws NumberFormatException, BusinessException, XmlException {
		if (!StringUtils.nullToString(idCupon).equals("")) {
			this.parcelasModificacionPolizaManager.copiarParcelasFromPolizaActualizada(Long.parseLong(idAnexo), idCupon,
					poliza.getLinea().getLineaseguroid());
		} else {
			this.parcelasModificacionPolizaManager.copiarParcelasFromPolizaOrCopy(Long.parseLong(idAnexo));
		}

	}

	private String obtenerclearStatus(String vieneDeCoberturasAnexo) {
		String clearStatus = "false";

		if ("true".equals(vieneDeCoberturasAnexo)) {
			clearStatus = "true";
		}
		return clearStatus;
	}

	private Long obtenerEstadoCupon(AnexoModificacion anexo, Long estadoCupon) {
		if (anexo.getCupon() != null && anexo.getCupon().getEstadoCupon() != null) {
			estadoCupon = anexo.getCupon().getEstadoCupon().getId();
		}
		return estadoCupon;
	}
	
	private Long obtenerEstadoCupon(String idAnexo, Long estadoCupon, AnexoModificacion anexo)
			throws BusinessException {

		// Se obtiene el estado del cupon del anexo si no se ha hecho ya antex
		if (estadoCupon == null) {
			anexo = this.declaracionesModificacionPolizaManager.getAnexoModifById(new Long(idAnexo));
			if (anexo.getCupon() != null && anexo.getCupon().getEstadoCupon() != null) {
				// Guarda el estado del cupon para comprobar si hay que mostrar el boton de
				// 'Importes' en la pantalla
				estadoCupon = anexo.getCupon().getEstadoCupon().getId();
			}
		}
		return estadoCupon;

	}

	private String obtenerModoLectura(AnexoModificacion anexo, String modoLectura) {
		// Si el anexo esta en estado 'Enviado Correcto' se visualiza el listado en modo
		// lectura
		if (Constants.ANEXO_MODIF_ESTADO_CORRECTO.equals(anexo.getEstado().getIdestado())) {
			modoLectura = "true";
		}
		return modoLectura;
	}

	private Map<String, Object> cargarLineaId(Map<String, Object> parametros, Poliza poliza) {

		if (poliza != null) {
			parametros.put(LINEAID, poliza.getLinea().getLineaseguroid());
		}
		return parametros;
	}

	private String obtenerIdsRowsChecked(String idsRowsChecked, String isClickInListado) {
		if (isClickInListado.equals("si")) {
			idsRowsChecked = "";
		}
		return idsRowsChecked;
	}

	private String obtenerIdsCapAsegRowsChecked(String idsCapAsegRowsChecked, String isClickInListado) {

		// es que no esta paginando, reset valores checkeds
		if (isClickInListado.equals("si")) {
			idsCapAsegRowsChecked = "";
		}
		return idsCapAsegRowsChecked;
	}

	private Map<String, Object> informarParams(Poliza poliza, String vieneDeListadoAnexosMod, String opcion,
			Map<String, Object> params) {
		String listCodModulos = "";

		if (opcion.equals("A")) {
			/* Opción A */
			if ("true".equals(vieneDeListadoAnexosMod)) {
				params.put(VIENE_LSTANX, vieneDeListadoAnexosMod);
			}
		} else {
			/* Opción B */
			if (null != poliza && null != poliza.getCodmodulo()) {
				listCodModulos = poliza.getCodmodulo();
				params.put(LST_CODMOD, listCodModulos);
			}
		}
		return params;
	}
	
	private BeanParcelaAnexo informarParcelaBeanAnexo(CapitalAsegurado capAseg, ModelTableDecoratorParcelasModificadas decorator) {
		
		BeanParcelaAnexo bpa = new BeanParcelaAnexo();
	
		//Numero
		if (capAseg.getParcela().getHoja() != null && capAseg.getParcela().getNumero() != null) {
			bpa.setNumero(
					capAseg.getParcela().getHoja() + "-" + capAseg.getParcela().getNumero());
		}
	
		// PRV, CMC, TRM, SBT
		bpa.setCodProvincia(capAseg.getParcela().getCodprovincia());
		bpa.setCodComarca(capAseg.getParcela().getCodcomarca());
		bpa.setCodTermino(capAseg.getParcela().getCodtermino());
		bpa.setSubtermino(capAseg.getParcela().getSubtermino() != null
				? capAseg.getParcela().getSubtermino().toString()
				: null);
	
		// CUL
		bpa.setCodCultivo(capAseg.getParcela().getCodcultivo());
	
		// VAR
		bpa.setCodVariedad(capAseg.getParcela().getCodvariedad());
		
		// Id Cat/SIGPAC
		if (capAseg.getParcela().getPoligono() != null
				&& capAseg.getParcela().getParcela_1() != null) {
			bpa.setIdCatSigpac(capAseg.getParcela().getPoligono() + "-"
					+ capAseg.getParcela().getParcela_1());
		} else {
			String sigPac = "";
			/* SONAR Q */
			sigPac = obtenersigPac(capAseg);
			/* FIN SONAR Q */
			
			bpa.setIdCatSigpac(sigPac);
		}
	
		// Para informe Excel
		if (capAseg.getParcela().getPoligono() != null
				&& capAseg.getParcela().getParcela_1() != null) {
			bpa.setParcela(capAseg.getParcela().getParcela_1());
			bpa.setPoligono(capAseg.getParcela().getPoligono());
		} else {
			bpa.setCodprovsigpac(capAseg.getParcela().getCodprovsigpac());
			bpa.setCodtermsigpac(capAseg.getParcela().getCodtermsigpac());
			bpa.setAgrsigpac(capAseg.getParcela().getAgrsigpac());
			bpa.setZonasigpac(capAseg.getParcela().getZonasigpac());
			bpa.setPoligonosigpac(capAseg.getParcela().getPoligonosigpac());
			bpa.setParcelasigpac(capAseg.getParcela().getParcelasigpac());
			bpa.setRecintosigpac(capAseg.getParcela().getRecintosigpac());
		}
	
		// Nombre
		bpa.setNombre(capAseg.getParcela().getNomparcela());
	
		// Super./m
		bpa.setSuperm(decorator.getSuperf(capAseg));
	
		// Precio
		bpa.setPrecio(capAseg.getPrecio());
	
		// Prod
		bpa.setProduccion(capAseg.getProduccion());
	
		// T.Capital
		bpa.setTipoCapital(capAseg.getTipoCapital().getDestipocapital());
	
		// Estado
		bpa.setEstado(capAseg.getParcela().getTipomodificacion() != null
				? capAseg.getParcela().getTipomodificacion().toString()
				: null);
	
		/* SONAR Q */
		bpa = informarbpaSist(capAseg, bpa);
		/* FIN SONAR Q */
		
		// Incremento Produccion
		if (capAseg.getIncrementoproduccionanterior() != null) {
			bpa.setIncrementoProduccion(capAseg.getIncrementoproduccionanterior().toString());
		}
	
		// Incremento Modificado
		if (capAseg.getIncrementoproduccion() != null) {
			bpa.setIncrementoModificado(capAseg.getIncrementoproduccion().toString());
		}
		return bpa;
	}
	
	private String obtenersigPac(CapitalAsegurado capAseg) {
		String sigPac = "";
	
		if (capAseg.getParcela().getCodprovsigpac() != null)
			sigPac = capAseg.getParcela().getCodprovsigpac().toString();
		if (capAseg.getParcela().getCodtermsigpac() != null)
			sigPac += "-" + capAseg.getParcela().getCodtermsigpac().toString();
		if (capAseg.getParcela().getAgrsigpac() != null)
			sigPac += "-" + capAseg.getParcela().getAgrsigpac().toString();
		if (capAseg.getParcela().getZonasigpac() != null)
			sigPac += "-" + capAseg.getParcela().getZonasigpac().toString();
		if (capAseg.getParcela().getPoligonosigpac() != null)
			sigPac += "-" + capAseg.getParcela().getPoligonosigpac().toString();
		if (capAseg.getParcela().getParcelasigpac() != null)
			sigPac += "-" + capAseg.getParcela().getParcelasigpac().toString();
		if (capAseg.getParcela().getRecintosigpac() != null)
			sigPac += "-" + capAseg.getParcela().getRecintosigpac().toString();
		
		return sigPac;
	}
	
	private BeanParcelaAnexo informarbpaSist(CapitalAsegurado capAseg, BeanParcelaAnexo bpa) {
		for (CapitalDTSVariable captDTSVariable : capAseg.getCapitalDTSVariables()) {
			if (captDTSVariable.getCodconcepto()
					.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {
				// Sistema Cultivo
				bpa.setSistemaCultivo(captDTSVariable.getValor());
			}
			if (captDTSVariable.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_SISTCOND))) {
				bpa.setSistemaConduccion(captDTSVariable.getValor().toString());
			}
		}
		return bpa;
	}
	
	private com.rsi.agp.dao.tables.anexo.Parcela informarparcAnx(ParcelaVO parcelaVO, com.rsi.agp.dao.tables.anexo.Parcela parcela, com.rsi.agp.dao.tables.anexo.Parcela parcAnx){
		Set<CapitalAsegurado> capAsegurado = new HashSet<CapitalAsegurado>();
	
		if (parcelaVO.getCapitalAsegurado().getCodtipoCapital().equals("1")) {
			if (parcela != null) {
				for (CapitalAsegurado cap : parcela.getCapitalAsegurados()) {
					capAsegurado.add(cap);
				}
			}
			for (CapitalAsegurado capvo : parcAnx.getCapitalAsegurados()) {
				capAsegurado.add(capvo);
			}
	
			parcAnx.setCapitalAsegurados(capAsegurado);
		}
		return parcAnx;
	}
	private Set<CapitalDTSVariable> informarCobParcExt(Set<CapitalDTSVariable> cobParcExistentes, com.rsi.agp.dao.tables.anexo.Parcela parcAnx){
	
		if (parcAnx.getCapitalAsegurados() != null && parcAnx.getCapitalAsegurados().size() > 0) {
			for (CapitalAsegurado capAseg : parcAnx.getCapitalAsegurados()) {
				cobParcExistentes = capAseg.getCapitalDTSVariables();
			}
	
		}
		return cobParcExistentes;
	}
	
	private AnexoModificacion obtenerAnexo(ParcelaVO parcelaVO) throws NumberFormatException, BusinessException {
		AnexoModificacion anexo = new AnexoModificacion();
		
		if (!StringUtils.isNullOrEmpty(parcelaVO.getIdAnexoModificacion())) {
			anexo = this.declaracionesModificacionPolizaManager
					.getAnexoModifById(new Long(parcelaVO.getIdAnexoModificacion()));
		}
		return anexo;
	}
	
	/*
	 * Solo para las lineas que tengan riesgos elegibles a nivel de Parcela, se
	 * llamaria al servio web de Ayuda de contratacion par obtener el cuadro de
	 * coberturas correspondiente
	 */
	private List<ParcelasCoberturasNew> obtenerCobParcelas(com.rsi.agp.dao.tables.anexo.Parcela parcela, ParcelaVO parcelaVO, 
															Usuario usuario, String realPath, Long idAnexo, 
															List<ParcelasCoberturasNew> lstCobParc, boolean isCoberturas) throws NumberFormatException, BusinessException {
		if (isCoberturas) {
	
			Set<CapitalDTSVariable> cobParcExistentes = new HashSet<CapitalDTSVariable>();
			lstCobParc = this.parcelasModificacionPolizaManager.getCoberturasParcela(parcela, parcelaVO,
					usuario.getCodusuario(), realPath, cobParcExistentes,
					Long.parseLong(parcelaVO.getIdAnexoModificacion()));
		}
		return lstCobParc;
	}
	
	private String obtenercodParcela(HttpServletRequest request, ParcelaVO parcelaVO) {
		String codParcela = request.getParameter(COD_PARC);
		if (codParcela == null || codParcela == "") {
			codParcela = parcelaVO.getCodParcela();
		}
		return codParcela;
	}
	
	private List<ParcelasCoberturasNew> obtenerListCobParc(boolean isCoberturas, ParcelaVO parcelaVO, 
												String codParcela, Usuario usuario, String realPath) throws NumberFormatException, BusinessException{
	
		List<ParcelasCoberturasNew> listaCobParc = null;
		
		if (isCoberturas) {
	
			/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Inicio */
			/* Comprobamos si estamos cargando los datos de la parcela de poliza o de
			 * Anexo */
			if (!StringUtils.isNullOrEmpty(parcelaVO.getIdAnexoModificacion())) {
	
				Long idParcela = Long.parseLong(codParcela);
	
				Set<CapitalDTSVariable> cobParcExistentes = new HashSet<CapitalDTSVariable>(0);
	
				/* SONAR Q */
				AnexoModificacion anexo = obtenerAnexo(parcelaVO);
				/* FIN SONAR Q */
	
				/* Aunque la parcela exista, hay que pasarle los datos de la parcela de la
				 * pantalla. */
				
				com.rsi.agp.dao.tables.anexo.Parcela parcAnx = new com.rsi.agp.dao.tables.anexo.Parcela();
				this.parcelasModificacionPolizaManager.setParcelaAnxVO(parcelaVO, parcAnx, MOD_PARC, anexo);
	
				com.rsi.agp.dao.tables.anexo.Parcela parcela = null;
				parcela = this.parcelasModificacionPolizaManager.getParcelaAnx(idParcela);
	
				/* Si el tipo de Capital es Plantones, comprobamos si la parcela tiene otro
				 * capital asegurado con Produccion para obtener datos en SW de Modulos */
				
				/* SONAR Q */
				parcAnx = informarparcAnx(parcelaVO, parcela, parcAnx);
				cobParcExistentes = informarCobParcExt(cobParcExistentes, parcAnx);
				/* FINS SONAR Q */
	
				listaCobParc = this.parcelasModificacionPolizaManager.getCoberturasParcela(parcAnx, parcelaVO,
						usuario.getCodusuario(), realPath, cobParcExistentes,
						Long.parseLong(parcelaVO.getIdAnexoModificacion()));
			}
		}
		return listaCobParc;
	}
	
	/* SONAR Q */
	private String obtenerOperacionParcela(ParcelaVO parcelaVO) {
	  String operacionParcela = "";	
		if (Constants.TIPO_PARCELA_PARCELA.equals(parcelaVO.getTipoParcelaChar())) {
			operacionParcela = StringUtils.isNullOrEmpty(parcelaVO.getCodParcela())
					? DatosParcelaAnexoManager.ALTA_PARCELA
					: DatosParcelaAnexoManager.MODIFICAR_PARCELA;
		} else {
			operacionParcela = StringUtils.isNullOrEmpty(parcelaVO.getCodParcela())
					? DatosParcelaAnexoManager.ALTA_ESTRUCTURA_PARCELA
					: DatosParcelaAnexoManager.MODIFICAR_INSTALACION_PARCELA;
		}
		return operacionParcela;
	}
	/* FIN SONAR Q */

	
	/** SONAR Q ** MODIF TAM(04.11.2021) ** Fin **/

	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setDatosParcelaManager(IDatosParcelaManager datosParcelaManager) {
		this.datosParcelaManager = datosParcelaManager;
	}

	public void setDatosParcelaAnexoManager(DatosParcelaAnexoManager datosParcelaAnexoManager) {
		this.datosParcelaAnexoManager = datosParcelaAnexoManager;
	}

	public void setDeclaracionesModificacionPolizaManager(
			DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}

	public void setParcelasModificacionPolizaManager(
			ParcelasModificacionPolizaManager parcelasModificacionPolizaManager) {
		this.parcelasModificacionPolizaManager = parcelasModificacionPolizaManager;
	}

	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}
	
	public void setAnexoModificacionManager(
			AnexoModificacionManager anexoModificacionManager) {
		this.anexoModificacionManager = anexoModificacionManager;
	}
	
	public void setSolicitudModificacionManager(SolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}
}