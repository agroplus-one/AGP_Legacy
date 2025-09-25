package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.decorators.ModelTableDecoratorListaParcelasTodas;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IPolizasPctComisionesManager;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.managers.impl.AseguradoManager;
import com.rsi.agp.core.managers.impl.CalculoPrecioProduccionManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.managers.impl.ColectivoManager;
import com.rsi.agp.core.managers.impl.ConsultaDetallePolizaManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.PolizaRCManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.managers.impl.ganado.ExplotacionesManager;
import com.rsi.agp.core.managers.impl.sbp.ConsultaSbpManager;
import com.rsi.agp.core.report.BeanParcela;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsRC;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.core.webapp.util.VistaImportesPorGrupoNegocio;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.TipoRdto;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;
import com.rsi.agp.dao.tables.rc.PolizasRC;
import com.rsi.agp.vo.ProduccionVO;

public class SeleccionPolizaController extends MetodoPagoController {

	// Constantes con los identificadores posibles de 'Pantalla'
	private static final Log LOGGER = LogFactory.getLog(SeleccionPolizaController.class);

	private PolizaManager polizaManager;
	private ColectivoManager colectivoManager;
	private AseguradoManager aseguradoManager;
	private ConsultaSbpManager consultaSbpManager;
	private ConsultaDetallePolizaManager consultaDetallePolizaManager;
	private ClaseManager claseManager;
	private IPolizasPctComisionesManager polizasPctComisionesManager;
	private WebServicesManager webServicesManager;
	private PagoPolizaManager pagoPolizaManager;
	private ExplotacionesManager explotacionesManager;
	private PolizaRCManager polizaRCManager;
	private HistoricoEstadosManager historicoEstadosManager;
	private IDocumentacionGedManager documentacionGedManager;	

	ResourceBundle bundle = ResourceBundle.getBundle("agp");
	ResourceBundle bundleSbp = ResourceBundle.getBundle("agp_sbp");

	private final static String VACIO = "";

	/**
	 * SONAR Q ** MODIF TAM(28.10.2021) ** Se ha eliminado todo el c�digo comentado
	 **/

	/** CONSTANTES SONAR Q ** MODIF TAM (27.10.2021) ** Inicio **/
	private final static String POLIZA = "polizaBean";
	private final static String OPERACION = "operacion";
	private final static String TIPO_LIST = "tipoListadoGrid";
	private final static String IDPOLIZA = "idpoliza";
	private final static String MODO_LECT = "modoLectura";
	private final static String SORT = "d-1622193-s";
	private final static String ORDER = "d-1622193-o";
	private final static String PANTALLA = "pantalla";
	private final static String LIST_MODULOS = "listCodModulos";
	private final static String MENSAJE = "mensaje";
	private final static String ALERT = "alerta";
	private final static String ALERT2 = "alerta2";

	private final static String MV_LISPARCELAS = "moduloPolizas/polizas/listadoParcelas";
	private final static String MV_SELPOLIZAS = "moduloPolizas/polizas/seleccion/seleccionPolizas";
	private final static String MV_ERRCONTRATACION = "/moduloUtilidades/erroresContratacion";

	private final static String DATOS = "datos";
	private final static String SELPOLIZA = "seleccionPoliza";
	private final static String MENSAJE_KO = "mensaje.poliza.pantallaConfigurada.KO";
	private final static String LISTA_POL = "listaPolizas";
	private final static String POLIZASBP = "polizaSbp";
	private final static String COLUMNA = "columna";
	private final static String ORDEN = "orden";
	private final static String NUM_PARC_LISTADO = "numParcelasListado";
	private final static String PARCELAS_STR = "parcelasString";
	private final static String LIST_PARCELAS = "listadoParcelas";
	private final static String MODELTABLE = "modelTableDecorator";
	private final static String CODESTADO_POL = "codEstadoPoliza";
	private final static String IDENVIO = "idEnvio";
	private final static String MPAGOM = "mpPagoM";
	private final static String MPAGOC = "mpPagoC";
	private final static String DTO_LECTURA = "descuentoLectura";
	private final static String RECARGO_LECTURA = "recargoLectura";
	private final static String MENSAJE_BAJAKO = "mensaje.baja.KO";
	private final static String VIENE_UTILIDADES = "vieneDeUtilidades";
	private final static String IDPOL = "idPoliza";
	private final static String ERR_LENGTH = "errLength";
	private final static String SEL_POL = "selecpoliza";
	private final static String RDTO_HIST = "rdtoHist";
	/** CONSTANTES SONAR Q ** MODIF TAM (27.10.2021) ** Fin **/

	/* DNF 24/07/2020 PET.63485 */
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	/* FIN DNF 24/07/2020 PET.63485 */

	public SeleccionPolizaController() {
		super();
		setCommandClass(Poliza.class);
		setCommandName(POLIZA);
	}

	@Override
	protected final ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
			final Object object, final BindException exception) throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> paramsComs = new HashMap<String, Object>();
		ModelAndView resultado = null;
		List<Poliza> listaPolizas = null;
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		Map<String, Object> paramsDesc = new HashMap<String, Object>();

		Poliza polizaBean = (Poliza) object;
		
		//ESC-30568 / GD-18421
        String rdtoOrientativoPulsado = request.getParameter("rdtoOrientativoPulsado");
        if(rdtoOrientativoPulsado == null) {
        	rdtoOrientativoPulsado = "false";
        }
        String rdtoHistPulsado = request.getParameter("rdtoHistPulsado");
        if(rdtoHistPulsado == null) {
        	rdtoHistPulsado = "false";
        }
        logger.debug("SeleccionPolizaControler rdtoOrientativoPulsado: " + rdtoOrientativoPulsado);
        logger.debug("SeleccionPolizaControler rdtoHistPulsado: " + rdtoHistPulsado);
        //ESC-30568 / GD-18421

		logger.debug("SeleccionPolizaControler-handle");

		polizaBean = getSeleccionPolizaManager().getPolizaById(polizaBean.getIdpoliza());
		if (polizaBean == null) {
			polizaBean = new Poliza();
		}
		BigDecimal entidad = null;

		Colectivo colectivo = new Colectivo();

		/** CONSTANTES SONAR Q ** MODIF TAM (27.10.2021) ** Inicio **/
		colectivo = informarColectivo(usuario, polizaBean);
		Long idCol = colectivo.getId();
		colectivo = colectivoManager.getColectivo(idCol);

		entidad = informarEntidad(usuario, polizaBean);

		Asegurado asegurado = new Asegurado();
		asegurado = informarAsegurado(usuario, polizaBean);
		Long idAseg = asegurado.getId();
		asegurado = aseguradoManager.getAsegurado(idAseg);

		/** CONSTANTES SONAR Q ** MODIF TAM (27.10.2021) ** Inicio **/

		String realPath = this.getServletContext().getRealPath("/WEB-INF/");

		BigDecimal Varclase = new BigDecimal(0);
		Clase clase = new Clase();

		if (usuario.getClase() != null) {
			clase = usuario.getClase();
			Varclase = usuario.getClase().getClase();
		} else {
			clase = claseManager.getClase(polizaBean);
			Varclase = polizaBean.getClase();
		}

		Poliza polizaBusqueda = new Poliza();

		polizaBusqueda.setColectivo(colectivo);
		polizaBusqueda.setAsegurado(asegurado);
		polizaBusqueda.setClase(Varclase);

		if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_OTROS)) {
			polizaBusqueda.setUsuario(usuario);
		}

		boolean tieneRdtoHist = clase != null && new BigDecimal(300).equals(clase.getLinea().getCodlinea())
				&& Constants.TIENE_RDTO_HISTORICO.equals(clase.getRdtoHistorico());

		String operacion = request.getParameter(OPERACION);
		String idsRowsChecked = StringUtils.nullToString(request.getParameter("idsRowsChecked"));
		String marcarTodosChecks = StringUtils.nullToString(request.getParameter("marcarTodosChecks"));

		logger.debug("SeleccionPolizaControler-handle. Valor de operacion:" + operacion);

		String tipoListadoGrid = consultaDetallePolizaManager.getTipoListadoGrid(request.getParameter(TIPO_LIST));

		String modelTableDecorator = consultaDetallePolizaManager.getModelTableDecorator(tipoListadoGrid);

		String d1338802o = request.getParameter("d-1338802-o");
		String d1338802p = request.getParameter("d-1338802-p");
		String d1338802s = request.getParameter("d-1338802-s");

		// recuperamos el idpoliza
		Long idPoliza = null;// new Long(-1);
		if (!"".equals(StringUtils.nullToString(request.getParameter(IDPOLIZA)))) {
			idPoliza = Long.parseLong(request.getParameter(IDPOLIZA));
		}

		String codEstadoPolizaMayor3 = consultaDetallePolizaManager.getCodEstadoPolizaMayor3(idPoliza);

		String modoLectura = StringUtils.nullToString(request.getParameter(MODO_LECT));

		listaPolizas = getSeleccionPolizaManager().getPolizas(polizaBusqueda);
		
		logger.debug("Consulta Polizas OK");


		// ----------------------------------------------------------------
		// RECALCULAR PARCELAS
		// ----------------------------------------------------------------

		if ("recalcularParcela".equalsIgnoreCase(operacion)) {

			logger.debug("init: SeleccionPolizaController - recalcularParcela");

			Set<Long> colIdParcelasParaRecalculo = new HashSet<Long>();

			for (Parcela par : polizaBean.getParcelas()) {

				colIdParcelasParaRecalculo.add(par.getIdparcela());
			}

			List<String> codsModuloPoliza = new ArrayList<String>();

			Parcela parcela = new Parcela();
			parcela.getPoliza().setIdpoliza(polizaBean.getIdpoliza());
			List<Parcela> parcelas = getSeleccionPolizaManager().getParcelas(parcela, null, null);

			boolean recalcularRendimientoConSWprueba = calculoPrecioProduccionManager.calcularRendimientoProdConSW();

			try {
				if (recalcularRendimientoConSWprueba) {

					Map<String, ProduccionVO> mapaRendimientosProd;

					mapaRendimientosProd = calculoPrecioProduccionManager.calcularRendimientosPolizaWS(
							polizaBean.getIdpoliza(), null, realPath, usuario.getCodusuario(), 0);

					for (ComparativaPoliza comp : polizaBean.getComparativaPolizas()) {
						if (!codsModuloPoliza.contains(comp.getId().getCodmodulo()))
							codsModuloPoliza.add(comp.getId().getCodmodulo());
					}

					getSeleccionPolizaManager().recalculoPrecioProduccion(parcelas, codsModuloPoliza,
							recalcularRendimientoConSWprueba, mapaRendimientosProd);

				} else {
					getSeleccionPolizaManager().recalculoPrecioProduccion(parcelas, codsModuloPoliza);
				}
			} catch (Throwable e) {
				logger.error(e);
			}

			operacion = "listParcelas";
		}
		// ----------------------------------------------------------------
		// LISTA DE PARCELAS
		// ----------------------------------------------------------------
		if ("listParcelas".equalsIgnoreCase(operacion) || d1338802o != null || d1338802p != null || d1338802s != null) {

			// DAA 21/11/12 si PARAMETER_SORT y PARAMETER_ORDER no son nulos
			if (!StringUtils.nullToString(request.getParameter(SORT)).equals("")
					&& !StringUtils.nullToString(request.getParameter(ORDER)).equals("")) {
				getOrdenacionDisplaytag(request);
			}

			// TMR
			polizaBean = getSeleccionPolizaManager().getPolizaById(idPoliza);
			String cambioProvisional = request.getParameter("cambioProvisional");

			/* ESC-9312 ** MODIF TAM (21/04/2020) ** Inicio */
			/*
			 * Si el usuario no ejecuta las operaciones correspondientes, hay veces que
			 * despues de haber Confirmado una p�liza, en la ventana de Importes se entra
			 * como si la poliza no se hubiera finalizado por eso si venimos de ejecutar el
			 * bot�n volver y la variable modoLectura viene vac�a, se consulta el estado de
			 * la poliza y si esta est� en ENVIADA CORRECTA, se actualiza dicho valor como
			 * si estuvieramos consultando la p�liza.
			 */
			logger.debug("**@@** SeleccionPolizaController-Handle(listParcelas)");

			boolean activarmodoLectura = false;

			if (StringUtils.isNullOrEmpty(modoLectura)) {
				logger.debug("**@@** modoLectura, validamos el estado de la p�liza");
				logger.debug("**@@** Estado de la poliza:" + polizaBean.getEstadoPoliza().getIdestado());
				// Si esta en grabacion provisional, la cambiamos a pendiente validacion porque
				// si volvemos a parcela, no sabemos si va a cambiar algo
				if (Constants.ESTADO_POLIZA_DEFINITIVA.compareTo(polizaBean.getEstadoPoliza().getIdestado()) == 0) {
					activarmodoLectura = true;
					logger.debug("**@@**Entramos en el if. valor de activarmodoLectura:" + activarmodoLectura);
				}
			}
			/* ESC-9312 ** MODIF TAM (21/04/2020) ** Fin */

			if ("true".equals(cambioProvisional) && !MODO_LECT.equals(modoLectura)) {
				// Si esta en grabacion provisional, la cambiamos a pendiente validacion porque
				// si volvemos a parcela, no sabemos si va a cambiar algo
				if (Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL
						.compareTo(polizaBean.getEstadoPoliza().getIdestado()) == 0) {
					EstadoPoliza estadoPoliza = new EstadoPoliza();
					estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
					polizaBean.setEstadoPoliza(estadoPoliza);
				}
			}

			PantallaConfigurable pantalla = getSeleccionPolizaManager().getPantallaVarPoliza(
					polizaBean.getLinea().getLineaseguroid(),
					polizaBean.getLinea().isLineaGanado() ? Constants.PANTALLA_EXPLOTACIONES
							: Constants.PANTALLA_POLIZA);

			if (pantalla != null) {

				parameters = consultaDetallePolizaManager.getDatosParcela(request, polizaBean.getIdpoliza());
				parameters.put(POLIZA, polizaBean);
				parameters.put(PANTALLA, pantalla);

				/* ESC-9312 ** MODIF TAM (21/04/2020) ** Inicio */
				if (activarmodoLectura) {
					parameters.put(MODO_LECT, modoLectura);
				}
				/* ESC-9312 ** MODIF TAM (21/04/2020) ** Fin */

				String listCodModulos = this.getSeleccionPolizaManager()
						.getListModulesWithComparativas(new Long(idPoliza));
				listCodModulos = listCodModulos.replace(";", ",");
				parameters.put(LIST_MODULOS, listCodModulos);

				if (!StringUtils.nullToString(request.getParameter(MENSAJE)).equals(""))
					parameters.put(MENSAJE, request.getParameter(MENSAJE));

				if (!StringUtils.nullToString(request.getParameter(ALERT)).equals(""))
					parameters.put(ALERT, request.getParameter(ALERT));

				if (!StringUtils.nullToString(request.getParameter(ALERT2)).equals(""))
					parameters.put(ALERT2, request.getParameter(ALERT2));

				if (!StringUtils.nullToString(request.getParameter("mensajeOrigen")).equals(""))
					parameters.put(MENSAJE, request.getParameter("mensajeOrigen"));

				if (!StringUtils.nullToString(request.getParameter("alertaOrigen")).equals(""))
					parameters.put(ALERT, request.getParameter("alertaOrigen"));

				if (polizaBean.getLinea().isLineaGanado()) {
					resultado = new ModelAndView("redirect:/listadoExplotaciones.html").addAllObjects(parameters);
				} else {

					String lineaDanhoFauna = ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");

					if (lineaDanhoFauna.contains(polizaBean.getLinea().getCodlinea().toString())) {
						String danhoFaunaMaxParcelas = ResourceBundle.getBundle("agp")
								.getString("danhosFauna.maxParcelas");
						parameters.put("danhoFaunaMaxParcelas", danhoFaunaMaxParcelas);
						parameters.put("mostrarDanhosFauna", true);
						if (request.getSession().getAttribute("mapDanhoFauna") != null) {
							parameters.put("mapDanhoFauna", request.getSession().getAttribute("mapDanhoFauna"));
							request.getSession().removeAttribute("mapDanhoFauna");
						}
					} else {
						parameters.put("mostrarDanhosFauna", false);
						parameters.put("danhoFaunaMaxParcelas", 0);
					}

					resultado = new ModelAndView(MV_LISPARCELAS, DATOS, parameters);
				}

			} else {
				return HTMLUtils.errorMessage(SELPOLIZA, bundle.getString(MENSAJE_KO));
			}
		}
		// ----------------------------------------------------------------
		// ALTA POLIZA
		// ----------------------------------------------------------------
		else if ("alta".equalsIgnoreCase(operacion)) {
			logger.debug("Alta poliza INIT ");
			// Llamada al procedimiento en pl/sql CHECK_ASEG_AUTORIZADOS
			getSeleccionPolizaManager().check_aseg_autorizados(colectivo.getLinea().getLineaseguroid(),
					asegurado.getNifcif());

			// Creamos un objeto poliza y buscamos todas las polizas asociadas
			// para realizar las busquedas.
			Poliza poliza = new Poliza();
			poliza.setAsegurado(asegurado);
			poliza.setColectivo(colectivo);
			poliza.setClase(Varclase);
			poliza.setLinea(colectivo.getLinea());

			poliza.getAsegurado().getEntidad().setCodentidad(entidad);
			poliza.getColectivo().getTomador().getId().setCodentidad(entidad);
			poliza.getReferencia();

			listaPolizas = getSeleccionPolizaManager().getPolizas(poliza);
			parameters.put(LISTA_POL, listaPolizas);
			boolean altaKO = true;
			ResourceBundle bundle = ResourceBundle.getBundle("agp");
			// Solo permitimos el alta de una poliza si la linea y el colectivo seleccionado
			// esta Activa
			if (colectivo.getLinea().getActivo().equals("SI") && colectivo.getActivo().toString().equals("1")) {
				// Comprobamos si se permite realizar un nuevo alta, sino redireccionamos y
				// mostramos una alerta.
				// Si la lista de polizas es vacia tambien se permite realizar un alta.
				if (!isPermiteAlta(request, listaPolizas) && !listaPolizas.isEmpty()) {
					resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
					resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
							usuario, clase.getLinea().isLineaGanado()));
					parameters.put(ALERT, bundle.getString("mensaje.alta.KO"));
					altaKO = false;
				}

				if (poliza.getLinea().isLineaGanado()) {
					PantallaConfigurable pantalla = getSeleccionPolizaManager().getPantallaVarPoliza(
							poliza.getLinea().getLineaseguroid(), Constants.PANTALLA_EXPLOTACIONES);
					if (pantalla == null) {
						resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
						resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters,
								listaPolizas, usuario, clase.getLinea().isLineaGanado()));
						parameters.put(ALERT, bundle.getString("mensaje.alta.KO.pantallaConf"));
						altaKO = false;
					}
				}

				// Asignamos oficina a la poliza
				poliza.setOficina(getSeleccionPolizaManager().getOficina(usuario, asegurado, colectivo));
				// formulacion de primas.Polizas >= 2015. Validaciones
				if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 0
						|| poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 1) {

					paramsComs = polizasPctComisionesManager.validaComisiones(poliza, usuario);
					logger.debug("paramsComs: " + paramsComs.toString());
															
					if (paramsComs.get(ALERT) != null) {
						logger.debug("paramsComs.get(ALERT): " + paramsComs.get(ALERT));
						resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
						resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters,
								listaPolizas, usuario, clase.getLinea().isLineaGanado()));
						parameters.put(ALERT, paramsComs.get(ALERT));
						altaKO = false;
					}
				}
				
				logger.debug("altaKO: " + altaKO);
				
				if (altaKO) {

					polizaBean.setAsegurado(asegurado);
					polizaBean.setColectivo(colectivo);
					polizaBean.setUsuario(usuario);
					polizaBean.setIdpoliza(null);
					polizaBean.setLinea(colectivo.getLinea());
					polizaBean.setTipoReferencia(Constants.MODULO_POLIZA_PRINCIPAL);
					polizaBean.setOficina(getSeleccionPolizaManager().getOficina(usuario, asegurado, colectivo));

					EstadoPagoAgp estadoPago = new EstadoPagoAgp(Constants.POLIZA_NO_PAGADA, null, null);
					polizaBean.setEstadoPagoAgp(estadoPago);
					polizaBean.setClase(Varclase);
					polizaBean.setTienesiniestros('N');
					polizaBean.setTieneanexomp('N');
					polizaBean.setTieneanexorc('N');
					// DAA 31/10/2012
					polizaBean.setReferencia(null);

					EstadoPoliza estadoPoliza = new EstadoPoliza();
					estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
					
					Long id = getSeleccionPolizaManager().savePoliza(polizaBean, estadoPoliza, usuario.getCodusuario());
					
					//generamos la info de ged incluyendo el codigo de barras
					this.documentacionGedManager.saveNewGedDocPoliza(id, usuario.getCodusuario());
					
					logger.debug("paramsComs.get('polizaPctComisiones'): " + paramsComs.get("polizaPctComisiones").toString());
					
					if (paramsComs.get("polizaPctComisiones") != null) {
						@SuppressWarnings("unchecked")
						List<PolizaPctComisiones> listappc = (List<PolizaPctComisiones>) paramsComs
								.get("polizaPctComisiones");
						for (PolizaPctComisiones polizaPctComisiones : listappc) {
							polizaPctComisiones.setPoliza(polizaBean);
							polizasPctComisionesManager.savePolPctComs(polizaPctComisiones);
						}

					}
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(IDPOLIZA, id);
					params.put("tieneParcelas", "no"); // para que no recalcule al dar a continuar en modulos

					resultado = new ModelAndView(new RedirectView("polizaController.html")).addAllObjects(params);
					resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
							usuario, clase.getLinea().isLineaGanado()));
				}

			} else {

				if (!colectivo.getActivo().toString().equals("1")) {
					// el colectivo no esta activo
					parameters.put(ALERT, bundle.getString("mensaje.alta.colectivo.activo.KO"));
				} else {
					// la linea no esta activa
					parameters.put(ALERT, bundle.getString("mensaje.alta.colectivo.linea.KO"));
				}

				resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
				// Comprueba si tiene que mostrar el boton de sobreprecio
				resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
						usuario, clase.getLinea().isLineaGanado()));
			}
			logger.debug("Alta poliza END ");
		}

		// ----------------------------------------------------------------
		// VOLVER: Volver al listado de polizas
		// LIMPIAR: idem volver
		// ----------------------------------------------------------------
		else if ("volver".equalsIgnoreCase(operacion)) {
			polizaBean = new Poliza();
			listaPolizas = getSeleccionPolizaManager().getPolizas(polizaBusqueda);
			parameters.put(LISTA_POL, listaPolizas);

			resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
			// Comprueba si tiene que mostrar el boton de sobreprecio
			resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
					usuario, clase.getLinea().isLineaGanado()));
		}

		// ----------------------------------------------------------------
		// LIMPIAR PARCELAS
		// ----------------------------------------------------------------
		else if ("limpiarParcela".equalsIgnoreCase(operacion)) {
			polizaBean = new Poliza();
			polizaBean = getSeleccionPolizaManager().getPolizaById(idPoliza);
			polizaBean.setIdpoliza(idPoliza);
			parameters.put(POLIZA, polizaBean);

			// Recuperamos todas las parcelas
			Parcela parcela = consultaDetallePolizaManager.getBeanParcelaFromRequest(request, idPoliza,
					tipoListadoGrid);
			request.getSession().removeAttribute(COLUMNA);
			request.getSession().removeAttribute(ORDEN);
			List<Parcela> listaParcelas = getSeleccionPolizaManager().getParcelas(parcela, null, null);

			String parcelasString = consultaDetallePolizaManager.getListParcelasString(listaParcelas);

			String listCodModulos = getSeleccionPolizaManager().getListModulesWithComparativas(new Long(idPoliza));

			parameters.put(LIST_MODULOS, listCodModulos);
			parameters.put(NUM_PARC_LISTADO, listaParcelas.size());
			parameters.put(PARCELAS_STR, parcelasString);
			parameters.put(LIST_PARCELAS, listaParcelas);
			parameters.put(TIPO_LIST, tipoListadoGrid);
			parameters.put(MODELTABLE, modelTableDecorator);
			parameters.put(CODESTADO_POL, codEstadoPolizaMayor3);
			parameters.put(IDPOLIZA, idPoliza);

			String lineaDanhoFauna = ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");

			if (lineaDanhoFauna.contains(polizaBean.getLinea().getCodlinea().toString())) {

				parameters.put("mostrarDanhosFauna", true);
				if (request.getSession().getAttribute("mapDanhoFauna") != null) {
					parameters.put("mapDanhoFauna", request.getSession().getAttribute("mapDanhoFauna"));
					request.getSession().removeAttribute("mapDanhoFauna");
				}
			} else {
				parameters.put("mostrarDanhosFauna", false);
			}
			resultado = new ModelAndView(MV_LISPARCELAS, DATOS, parameters);
		}

		// ----------------------------------------------------------------
		// CONSULTAR PARCELAS
		// ----------------------------------------------------------------
		else if ("consultarParcela".equalsIgnoreCase(operacion)) {

			// DAA 21/11/12 si PARAMETER_SORT y PARAMETER_ORDER no son nulos he pinchado en
			// ordenar
			if (!StringUtils.nullToString(request.getParameter(SORT)).equals("")
					&& !StringUtils.nullToString(request.getParameter(ORDER)).equals("")) {
				getOrdenacionDisplaytag(request);
			}
			// TMR

			parameters = consultaDetallePolizaManager.consulta(request, idPoliza);

			String lineaDanhoFauna = ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");

			if (lineaDanhoFauna.contains(polizaBean.getLinea().getCodlinea().toString())) {

				parameters.put("mostrarDanhosFauna", true);
				if (request.getSession().getAttribute("mapDanhoFauna") != null) {
					parameters.put("mapDanhoFauna", request.getSession().getAttribute("mapDanhoFauna"));
					request.getSession().removeAttribute("mapDanhoFauna");
				}
			} else {
				parameters.put("mostrarDanhosFauna", false);
			}
			String listCodModulos = this.getSeleccionPolizaManager().getListModulesWithComparativas(new Long(idPoliza));
			listCodModulos = listCodModulos.replace(";", ",");
			parameters.put(LIST_MODULOS, listCodModulos);

			resultado = new ModelAndView(MV_LISPARCELAS, DATOS, parameters);

		}

		// ----------------------------------------------------------------
		// EDITAR: Load poliza and redirect to eleccion modulos
		// ----------------------------------------------------------------
		else if ("editar".equalsIgnoreCase(operacion)) {

			request.getSession().removeAttribute(COLUMNA);
			request.getSession().removeAttribute(ORDEN);
			String borrarPolizaSbp = StringUtils.nullToString(request.getParameter("borrarPolizaSbp"));

			// MPM - Se comprueba si la linea esta activa
			if ("SI".equals(colectivo.getLinea().getActivo())
					&& (colectivo.getActivo() != null && "1".equals(colectivo.getActivo().toString()))) {
				// ASF - LLAMADA AL METODO PARA EDITAR
				resultado = getSeleccionPolizaManager().doEditar(idPoliza, asegurado, modoLectura, borrarPolizaSbp,
						usuario, parameters, realPath);

				if (!MODO_LECT.equals(modoLectura) && polizaBean.getLinea().isLineaGanado()) {
					this.polizaRCManager.cambiaEstadoPolizaRC(idPoliza, ConstantsRC.ESTADO_RC_BORRADOR,
							usuario.getCodusuario());
				}
			} else {
				if (colectivo.getActivo() == null || !"1".equals(colectivo.getActivo().toString())) {
					// el colectivo no esta activo
					parameters.put(ALERT, bundle.getString("mensaje.edicion.colectivo.activo.KO"));
				} else {
					// la linea no esta activa
					parameters.put(ALERT, bundle.getString("mensaje.edicion.colectivo.linea.KO"));
				}

				parameters.put(LISTA_POL, getListPlzAsociadas(entidad, colectivo, asegurado, Varclase));

				resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
				// Comprueba si tiene que mostrar el boton de sobreprecio
				resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
						usuario, clase.getLinea().isLineaGanado()));
			}

		} else if ("desbloquear".equalsIgnoreCase(operacion)) {
			// Desbloqueamos la poliza para que el usuario pueda entrar.
			polizaBean = getSeleccionPolizaManager().getPolizaById(idPoliza);
			polizaBean.setBloqueadopor(null);
			polizaBean.setFechabloqueo(null);

			getSeleccionPolizaManager().savePoliza(polizaBean);

			listaPolizas = getSeleccionPolizaManager().getPolizas(polizaBean);
			parameters.put(MENSAJE, bundle.getString("mensaje.poliza.desbloqueada.usuario.OK"));
			parameters.put(POLIZA, polizaBean);
			parameters.put(LISTA_POL, listaPolizas);

			resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
			// Comprueba si tiene que mostrar el boton de sobreprecio
			resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
					usuario, clase.getLinea().isLineaGanado()));

			// ----------------------------------------------------------------
			// IMPORTES
			// ----------------------------------------------------------------
		} else if ("importes".equalsIgnoreCase(operacion)) {

			Boolean tieneSubvenciones = true;
			// String modoLectura = "";

			LOGGER.debug("redirigiendo a importes.jsp");

			String grProvisional = request.getParameter("grProvisional");
			String grDefinitiva = request.getParameter("grDefinitiva");
			String grDefinitivaKO = request.getParameter("grDefinitivaKO");
			// String grProvisionalKO = request.getParameter("grProvisionalKO");
			String netoTomadorFinanciadoAgr = request.getParameter("importe1");

			String mensaje = request.getParameter(MENSAJE);
			String alerta = request.getParameter(ALERT);
			Long idEnvio = null;
			String idEnvioStr = null;
			if (request.getParameter(IDENVIO) != null) {
				idEnvioStr = request.getParameter(IDENVIO);
				if (!idEnvioStr.equals(""))
					idEnvio = Long.parseLong(idEnvioStr);
			}
			parameters.put(IDENVIO, idEnvio);
			parameters.put("netoTomadorFinanciadoAgr", netoTomadorFinanciadoAgr);
			parameters.put("lineaContrataSup2021", request.getParameter("lineaContrataSup2021"));
			parameters.put("validComps", request.getParameter("validComps"));

			Poliza polizaActual = getSeleccionPolizaManager().getPolizaById(idPoliza);

			/*
			 * PET.70105.fII cambiamos el estado en caso de pulsar volver una vez la poliza
			 * ha sido cambiada a grabacion provisional
			 */
			// ESC-13304
			if (!MODO_LECT.equals(modoLectura) && polizaActual.getEstadoPoliza().getIdestado()
					.equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
				// ESC-13304
				// ACTUALIZAMOS EL ESTADO DE LA POLIZA A PENDIENTE DE VALIDACION CADA VEZ QUE SE
				// EDITA
				logger.debug("actualizamos el estado de la poliza complementario a pendiente de validacion");
				EstadoPoliza estadoPoliza = new EstadoPoliza();
				estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
				polizaBean.setEstadoPoliza(estadoPoliza);

				polizaManager.savePoliza(polizaBean);
				historicoEstadosManager.insertaEstado(Tabla.POLIZAS, polizaBean.getIdpoliza(), usuario.getCodusuario(),
						Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL);
			}
			/* fin PET.70105.fII */

			// Aqui se guardan los objetos que ayudaran a pintar la pantalla de Importes
			Set<VistaImportes> fluxCondensatorHolder = new LinkedHashSet<VistaImportes>();

			VistaImportes vistaImportes = new VistaImportes();
			Set<ComparativaPoliza> comparativasPoliza = polizaActual.getComparativaPolizas();

			Map<String, Object> comparativas = null;
			List<String> lstModulos = new ArrayList<String>();
			// PRIMERA ITERACION PARA OBTENER LOS CODMODULOS SELECCIONADOS
			for (ComparativaPoliza cp : comparativasPoliza) {
				String codModulo;
				if (polizaActual.getLinea().isLineaGanado()) {
					codModulo = cp.getRiesgoCubiertoModuloGanado().getModulo().getId().getCodmodulo();
				} else {
					codModulo = cp.getRiesgoCubiertoModulo().getModulo().getId().getCodmodulo();
				}
				if (!lstModulos.contains(codModulo)) {
					lstModulos.add(codModulo);
				}
			}

			Long idComparativaAux = new Long(-1);
			// ------------------------
			// -- comparativa poliza --
			// ------------------------
			for (ComparativaPoliza cp : comparativasPoliza) {

				Modulo modulo = null;
				if (polizaActual.getLinea().isLineaGanado()) {
					RiesgoCubiertoModuloGanado rcmg = cp.getRiesgoCubiertoModuloGanado();
					vistaImportes.setConceptoPpalMod(
							StringUtils.nullToString(rcmg.getConceptoPpalModulo().getDesconceptoppalmod()));
					modulo = rcmg.getModulo();
				} else {
					RiesgoCubiertoModulo rcm = cp.getRiesgoCubiertoModulo();
					vistaImportes.setConceptoPpalMod(
							StringUtils.nullToString(rcm.getConceptoPpalModulo().getDesconceptoppalmod()));
					modulo = rcm.getModulo();
				}

				// modulos
				if (modulo != null) {

					vistaImportes.setDescModulo(StringUtils.nullToString(modulo.getDesmodulo()));
					vistaImportes.setIdModulo(StringUtils.nullToString(modulo.getId().getCodmodulo()));
					vistaImportes.setAdmiteComplementario(
							modulo.getTotcomplementarios() != null && modulo.getTotcomplementarios() > 0 ? "S&iacute;"
									: "No");
					String totalProduccion = polizaManager.getTotalProdComparativa(modulo.getId().getCodmodulo(),
							polizaActual);
					vistaImportes.setTotalProduccion(totalProduccion);

					if (polizaActual.getLinea().isLineaGanado()) {
						// MPM - 28/04/16
						// Obtiene el listado de comparativas de la p�liza
						Set<ComparativaPoliza> comparativaPolizas = polizaActual.getComparativaPolizas();
						List<ComparativaPoliza> listComparativasPoliza = comparativaPolizas != null
								? Arrays.asList(comparativaPolizas.toArray(new ComparativaPoliza[] {}))
								: new ArrayList<ComparativaPoliza>();

						// Genera el html de la tabla de coberturas elegidas en la comparativa
						vistaImportes.setComparativaCompleta(webServicesManager.getTablaComparativaGanadoSeleccionadas(
								cp, listComparativasPoliza, polizaActual, realPath, usuario));

						// Indica el identificador de la comparativa - c�digo de m�dulo y fila de la
						// comparativa, separados por los '|' necesarios
						// para la integraci�n con la pantalla de improtes
						vistaImportes.setComparativaSeleccionada(cp.getId().getIdComparativa() + "|"
								+ modulo.getId().getCodmodulo() + "||||||" + cp.getId().getFilacomparativa());
						if (null != idEnvio)
							vistaImportes.setIdEnvioComp(idEnvio.toString());
						/* Pet. 63485 ** MODIF TAM (27.07.2020) ** Inicio */
					} else {
						// Obtiene el listado de comparativas de la p�liza
						Set<ComparativaPoliza> comparativaPolizas = polizaActual.getComparativaPolizas();
						List<ComparativaPoliza> listComparativasPoliza = comparativaPolizas != null
								? Arrays.asList(comparativaPolizas.toArray(new ComparativaPoliza[] {}))
								: new ArrayList<ComparativaPoliza>();
						if (MODO_LECT.equals(modoLectura) && !polizaActual.getEstadoPoliza().getIdestado()
								.equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
							vistaImportes.setComparativaCompleta(
									webServicesManager.consultaTablaComparativaAgriSeleccionadas(cp,
											listComparativasPoliza, polizaActual, realPath, usuario));
						} else {
							// Genera el html de la tabla de coberturas elegidas en la comparativa
							vistaImportes
									.setComparativaCompleta(webServicesManager.getTablaComparativaAgriSeleccionadas(cp,
											listComparativasPoliza, polizaActual, realPath, usuario));
						}

						// Indica el identificador de la comparativa - c�digo de m�dulo y fila de la
						// comparativa, separados por los '|' necesarios
						// para la integraci�n con la pantalla de improtes
						vistaImportes.setComparativaSeleccionada(cp.getId().getIdComparativa() + "|"
								+ modulo.getId().getCodmodulo() + "||||||" + cp.getId().getFilacomparativa());
						if (null != idEnvio)
							vistaImportes.setIdEnvioComp(idEnvio.toString());

					}

					webServicesManager.creaComparativasUnificado(vistaImportes, cp, comparativas, polizaActual,
							idEnvio);
				}
				idComparativaAux = cp.getId().getIdComparativa();
			} // fin for comparativas

			/**********
			 * ESC-8487 DNF 10/02/2020 -- Tengo que crear una comparativa auxiliar para
			 * poder mostrar los importes
			 */
			if (polizaBean.getLinea().isLineaGanado() && Constants.CHARACTER_S.equals(polizaBean.getRenovableSn())
					&& fluxCondensatorHolder.isEmpty()) {

				for (DistribucionCoste2015 dc2015 : polizaActual.getDistribucionCoste2015s()) {
					ComparativaPoliza fake = new ComparativaPoliza();
					ComparativaPolizaId fakeId = new ComparativaPolizaId();
					fakeId.setIdpoliza(polizaBean.getIdpoliza());
					fakeId.setLineaseguroid(polizaBean.getLinea().getLineaseguroid());
					fakeId.setCodmodulo(polizaBean.getCodmodulo());
					fakeId.setFilacomparativa(dc2015.getFilacomparativa());
					fakeId.setIdComparativa(
							dc2015.getIdcomparativa() != null ? dc2015.getIdcomparativa().longValue() : null);
					fake.setId(fakeId);
					fake.setEsFinanciada(dc2015.getImportePagoFracc() != null);

					/* ESC-15083 ** MODIF TAM (10.09.2021) ** Inicio */

					vistaImportes = webServicesManager.generateDataForImportesByDC(
							Arrays.asList(new DistribucionCoste2015[] { dc2015 }), polizaBean, fake, usuario, realPath);

					vistaImportes.setComparativaCompleta(vistaImportes.getComparativaCompleta());

					ModuloId moduloId = new ModuloId();
					moduloId.setCodmodulo(polizaBean.getCodmodulo());
					moduloId.setLineaseguroid(polizaBean.getLinea().getLineaseguroid());

					Modulo modulo = polizaManager.obtenerModulo(moduloId);

					vistaImportes.setDescModulo(modulo.getDesmodulo());
					vistaImportes.setIdModulo(moduloId.getCodmodulo());

					fluxCondensatorHolder.add(vistaImportes);

				}

			} else {
				/* P0063482 ** MODIF TAM (27.08.2021) ** Defecto 31 y 32 * Inicio */

				/*
				 * ESC- BOT�N VOLVER DESDE FORMA DE PAGO EN P�LIZAS CONTRATADAS DESDE EL BOT�N
				 * DE ALTA DE P�LIZA
				 */
				if (!polizaBean.getLinea().isLineaGanado()
						&& polizaBean.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
					/* Creamos una comparativa ficticia en caso de no tenerla */
					if (comparativasPoliza.size() <= 0) {
						logger.debug("Entramos a crear la comparativa ficticia por que no recupera nada");

						List<DistribucionCoste2015> listDc = new ArrayList<DistribucionCoste2015>();
						listDc.addAll(polizaBean.getDistribucionCoste2015s());

						DistribucionCoste2015 dc2015 = listDc.get(0);

						ComparativaPolizaId cpId = new ComparativaPolizaId();
						ComparativaPoliza cp = new ComparativaPoliza();
						cpId.setIdpoliza(polizaBean.getIdpoliza());
						cpId.setLineaseguroid(polizaBean.getLinea().getLineaseguroid());
						cpId.setCodmodulo(polizaBean.getCodmodulo());
						cpId.setFilacomparativa(dc2015.getFilacomparativa());
						cpId.setIdComparativa(
								dc2015.getIdcomparativa() != null ? dc2015.getIdcomparativa().longValue() : null);
						cp.setId(cpId);
						cp.setEsFinanciada(dc2015.getImportePagoFracc() != null);

						// ESC-10195 DNF 8 Julio 2020 Necesito recuperar la comparativa completa de la
						// vista anterior
						// para pintar la cabecera de las comparativas

						vistaImportes = webServicesManager.generateDataForImportesByDC(
								Arrays.asList(new DistribucionCoste2015[] { dc2015 }), polizaBean, cp, usuario,
								realPath);

						vistaImportes.setComparativaCompleta(vistaImportes.getComparativaCompleta());

						ModuloId moduloId = new ModuloId();
						moduloId.setCodmodulo(polizaBean.getCodmodulo());
						moduloId.setLineaseguroid(polizaBean.getLinea().getLineaseguroid());

						Modulo modulo = polizaManager.obtenerModulo(moduloId);

						vistaImportes.setDescModulo(modulo.getDesmodulo());
						vistaImportes.setIdModulo(moduloId.getCodmodulo());
					}
				}
				/* P0063482 ** MODIF TAM (27.08.2021) ** Defecto 31 y 32 * Fin */
				/**********
				 * En el ELSE dejo lo que habia antes, es decir la opci�n que se aplicaba a la
				 * distribuci�n de costes
				 */
				/********** FIN ESC-8487 DNF 10/02/2020 */

				// ----------------------------
				// -- distribucion de costes --
				// ----------------------------
				getSeleccionPolizaManager().generaDistribucionCostes(polizaActual, vistaImportes, idComparativaAux);

				// Ver Comisiones para plan >= 2015
				if (polizaActual.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 0
						|| polizaActual.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 1) {
					vistaImportes = polizasPctComisionesManager.dameComisiones(vistaImportes, polizaActual, usuario);
				}

				fluxCondensatorHolder.add(vistaImportes);

			}

			if ("true".equals(grProvisional) && polizaActual.getEsFinanciada().equals('S')) {
				ImporteFraccionamiento impFrac = webServicesManager.getImporteFraccionamiento(
						polizaActual.getLinea().getLineaseguroid(),
						polizaActual.getColectivo().getSubentidadMediadora());
				if (null != impFrac) {
					webServicesManager.muestraFinanciar(fluxCondensatorHolder, impFrac);
				}
				List<CondicionesFraccionamiento> condFracc = webServicesManager
						.getCondicionesFraccionamiento(polizaActual.getLinea().getLineaseguroid());
				parameters.put("condicionesFraccionamiento", condFracc);

				if (polizaActual.getDistribucionCoste2015s() != null
						&& polizaActual.getDistribucionCoste2015s().size() > 0) {
					DistribucionCoste2015 dc = polizaActual.getDistribucionCoste2015s().iterator().next();
					if (null != dc.getPeriodoFracc())
						parameters.put("periodoFracc", dc.getPeriodoFracc());
					if (null != dc.getValorOpcionFracc())
						parameters.put("valorOpcionFracc", dc.getValorOpcionFracc());
					if (null != dc.getOpcionFracc())
						parameters.put("opcionFracc", dc.getOpcionFracc());
				}
			}

			tieneSubvenciones = tieneSubvenciones(fluxCondensatorHolder);

			HashMap<String, Object> params = new HashMap<String, Object>();

			if (polizaActual.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) {
				parameters.put("grProvisionalOK", "true");
				String volverFormaPago = StringUtils.nullToString(request.getParameter("volverFormaPago"));
				if (!volverFormaPago.equals("true")) {
					parameters.put(MENSAJE, bundle.getString("mensaje.alta.provisional.detail"));
				}
			} else if (polizaActual.getEstadoPoliza().getIdestado()
					.equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)) {
				parameters.put("grDefinitivaOK", "true");
			}

			if ("true".equals(grDefinitiva)) {
				if ("true".equals(grDefinitivaKO)) {
					parameters.put("grProvisionalOK", "true");
					parameters.put(ALERT, alerta);
				} else {
					parameters.put("grDefinitivaOK", "true");
					parameters.put(MENSAJE, mensaje);
				}
			}

			// comprobamos si el asegurado tiene nif o cif
			Asegurado aseguradoSesion = null;
			aseguradoSesion = (Asegurado) polizaManager.getAsegurado(idAseg.toString());

			if ("CIF".equalsIgnoreCase(aseguradoSesion.getTipoidentificacion())) {
				parameters.put("tieneCIF", "true");
			}

			int countImportes = fluxCondensatorHolder.size();

			// 26-01-12 Miguel Nº69 inicio
			String popUpAmbiCont = request.getParameter("popUpAmbiCont");
			parameters.put("popUpAmbiCont", popUpAmbiCont);

			// 26-01-12 Miguel Nº69 inicio
			if (null != request.getParameter(MPAGOM))
				parameters.put(MPAGOM, request.getParameter(MPAGOM));
			if (null != request.getParameter(MPAGOC))
				parameters.put(MPAGOC, request.getParameter(MPAGOC));

			// RC DE GANADO
			if (polizaActual.getLinea().isLineaGanado()) {

				PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(Long.valueOf(idPoliza));

				if (polizaRC != null) {

					if (!MODO_LECT.equals(modoLectura)) {

						parameters.put("sumasAseguradas",
								this.polizaRCManager.getListadoCalculosRC(polizaActual.getLinea().getCodplan(),
										polizaActual.getLinea().getCodlinea(),
										polizaActual.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
										polizaActual.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(),
										polizaRC.getEspeciesRC().getCodespecie(),
										polizaRC.getRegimenRC().getCodregimen(), polizaRC.getNumanimales()));
					}

					polizaActual.setPolizaRC(polizaRC);
					parameters.put("polizaRC", polizaRC);
				}
			}

			parameters.put("countImportes", countImportes);
			parameters.put("tieneSubvenciones", tieneSubvenciones.toString());
			parameters.put(MODO_LECT, modoLectura);
			parameters.put(IDPOLIZA, idPoliza);
			parameters.put("estadoPoliza", polizaActual.getEstadoPoliza().getIdestado());
			parameters.put("perfil", usuario.getPerfil().substring(4));
			parameters.put("pintarTablaError", request.getParameter("pintarTablaError"));
			parameters.put("plan", polizaActual.getLinea().getCodplan());
			// MPM - Paso a definitiva
			Poliza polizaDefinitiva = new Poliza();
			polizaDefinitiva.setIdpoliza(polizaActual.getIdpoliza());
			parameters.put("polizaDefinitiva", polizaDefinitiva);
			// DAA 03/05/12
			parameters.put("numeroCuenta", AseguradoUtil.getFormattedBankAccount(polizaActual, true));
			parameters.put("numeroCuenta2", AseguradoUtil.getFormattedBankAccount(polizaActual, false));
			paramsDesc = webServicesManager.muestraBotonDescuento(polizaActual, usuario);
			if (Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL.equals(polizaActual.getEstadoPoliza().getIdestado())) {
				if (polizaActual.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 0
						|| polizaActual.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 1) {
					paramsDesc.put(DTO_LECTURA, true);
					paramsDesc.put(RECARGO_LECTURA, true);
					paramsDesc.put("descuento",
							polizaActual.getPolizaPctComisiones().getPctdescelegido() != null
									? polizaActual.getPolizaPctComisiones().getPctdescelegido().setScale(2)
									: new BigDecimal(0));
					paramsDesc.put("recargo",
							polizaActual.getPolizaPctComisiones().getPctrecarelegido() != null
									? polizaActual.getPolizaPctComisiones().getPctrecarelegido().setScale(2)
									: new BigDecimal(0));
				}
			}

			/* ESC-9312 ** MODIF TAM (21/04/2020) ** Inicio */
			/*
			 * Si el usuario no ejecuta las operaciones correspondientes, hay veces que
			 * despues de haber Confirmado una p�liza, en la ventana de Importes se entra
			 * como si la poliza no se hubiera finalizado por eso si venimos de ejecutar el
			 * bot�n volver y la variable modoLectura viene vac�a, se consulta el estado de
			 * la poliza y si esta est� en ENVIADA CORRECTA, se actualiza dicho valor como
			 * si estuvieramos consultando la p�liza.
			 */
			logger.debug("**@@** SeleccionPolizaController-Handle(importe)");
			if (StringUtils.isNullOrEmpty(modoLectura)) {
				logger.debug("**@@** modoLectura, validamos el estado de la p�liza");
				logger.debug("**@@** Estado de la poliza:" + polizaBean.getEstadoPoliza().getIdestado());
				// Si esta en grabacion provisional, la cambiamos a pendiente validacion porque
				// si volvemos a parcela, no sabemos si va a cambiar algo
				if (Constants.ESTADO_POLIZA_DEFINITIVA.compareTo(polizaBean.getEstadoPoliza().getIdestado()) == 0) {
					String modoLecturaAux = MODO_LECT;
					logger.debug("**@@**Entramos en el if. valor de modoLecturaAux:" + MODO_LECT);
					parameters.put(MODO_LECT, modoLecturaAux);
				}
			}
			/* ESC-9312 ** MODIF TAM (21/04/2020) ** Fin */

			/* ESC-5979 ** MODIF TAM (10.05.2019) ** Inicio */
			/* Informamos los parametros de lectura de Descuentos y de Recargos */
			boolean esModoLectura = MODO_LECT.equals(StringUtils.nullToString(request.getParameter(MODO_LECT)));
			if (esModoLectura) {
				paramsDesc.put(DTO_LECTURA, esModoLectura);
				paramsDesc.put(RECARGO_LECTURA, esModoLectura);
			} else {
				paramsDesc.put(DTO_LECTURA, null);
				paramsDesc.put(RECARGO_LECTURA, null);
			}
			/* ESC-5979 ** MODIF TAM (10.05.2019) ** Fin */

			params.put("isPagoFraccionado", pagoPolizaManager.compruebaPagoFraccionado(polizaActual));
			params.put("isLineaGanado", polizaActual.getLinea().isLineaGanado());

			parameters.put("dataCodlinea", polizaActual.getLinea().getCodlinea());
			parameters.put("dataCodplan", polizaActual.getLinea().getCodplan());
			parameters.put("dataNifcif", polizaActual.getAsegurado().getNifcif());
			parameters.put("isFechaEnvioPosteriorSep2020", request.getParameter("isFechaEnvioPosteriorSep2020"));

			resultado = new ModelAndView("moduloPolizas/polizas/importes/importes", "resultado", fluxCondensatorHolder)
					.addAllObjects(parameters).addAllObjects(params).addAllObjects(paramsDesc);

		} else if ("eliminarParcela".equals(operacion)) {

			/* MODIF TAM (28.10.2021) ** SONAR Q ** Inicio */
			/*
			 * Sacamos a otra funci�n el proceso de eliminar parcela, para descargar el
			 * handle de ifs
			 */
			polizaBean = getSeleccionPolizaManager().getPolizaById(idPoliza);

			parameters = eliminarParcela(request, idPoliza, polizaBean, tipoListadoGrid);

			parameters.put(TIPO_LIST, tipoListadoGrid);
			parameters.put(MODELTABLE, modelTableDecorator);
			parameters.put(CODESTADO_POL, codEstadoPolizaMayor3);

			/* MODIF TAM (28.10.2021) ** SONAR Q ** Fin */

			// Recuperamos la poliza con las parcelas actualizadas

			PantallaConfigurable pantalla = getSeleccionPolizaManager().getPantallaVarPoliza(
					polizaBean.getLinea().getLineaseguroid(),
					polizaBean.getLinea().isLineaGanado() ? Constants.PANTALLA_EXPLOTACIONES
							: Constants.PANTALLA_POLIZA);

			if (pantalla != null) {
				resultado = new ModelAndView("moduloPolizas/polizas/listadoParcelas", "datos", parameters);
			} else {
				return HTMLUtils.errorMessage(SELPOLIZA, bundle.getString(MENSAJE_KO));

			}
		} else if ("baja".equals(operacion)) {

			try {

				Poliza poliza = getSeleccionPolizaManager().getPolizaById(idPoliza);

				// *** SBP: borramos la poliza de Sbp asociada ***
				String msjSbp = consultaSbpManager.borrarPolizaSbpByPoliza(poliza, usuario, realPath);
				// ************************************************
				// TMR.Facturacion. Le a�adimos el usuario para facturar las bajas de las
				// polizas
				getSeleccionPolizaManager().borrarPoliza(poliza, usuario);
				if (msjSbp.equals("msjSbpBorrada")) {
					parameters.put("mensaje2", bundleSbp.getString("mensaje.borrar.ok"));
				} else if (msjSbp.equals("msjSbpRecalculada")) {
					parameters.put("mensaje2", bundleSbp.getString("mensaje.grabacion.definitiva.SinCpl"));
				}

				parameters.put(MENSAJE, bundle.getString("mensaje.baja.OK"));
				// SOLO PARA POLIZA PRINCIPAL: borramos todas las polizas complementarias de la
				// principal al pasar a definitiva
				if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL))
					polizaManager.borrarPolizasComplementariasByPpal(poliza.getIdpoliza());
			} catch (BusinessException be) {
				logger.error("Se ha producido un error durante el borrado de una poliza", be);
				parameters.put(ALERT, bundle.getString(MENSAJE_BAJAKO));
			} catch (Exception be) {
				logger.error("Se ha producido un error indefinido durante el borrado de una poliza", be);
				parameters.put(ALERT, bundle.getString(MENSAJE_BAJAKO));
			}
			polizaBean = new Poliza();
			listaPolizas = getSeleccionPolizaManager().getPolizas(polizaBusqueda);
			parameters.put(LISTA_POL, listaPolizas);
			resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
			// Comprueba si tiene que mostrar el boton de sobreprecio
			resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
					usuario, clase.getLinea().isLineaGanado()));
		}

		// ----------------------------------------------------------------
		// CONTINUAR
		// ----------------------------------------------------------------
		else if ("continuar".equalsIgnoreCase(operacion)) {
			polizaBean = getSeleccionPolizaManager().getPolizaById(idPoliza);

			if (StringUtils.isNullOrEmpty(modoLectura)) {
				// Si esta en grabacion provisional, la cambiamos a pendiente validacion porque
				// si volvemos a parcela, no sabemos si va a cambiar algo
				if (Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL
						.compareTo(polizaBean.getEstadoPoliza().getIdestado()) == 0) {
					EstadoPoliza estadoPoliza = new EstadoPoliza();
					estadoPoliza.setIdestado(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
					polizaBean.setEstadoPoliza(estadoPoliza);
				}
			}

			List<Parcela> listaParcelas = null;
			Parcela parcela = new Parcela();
			parcela.getPoliza().setIdpoliza(idPoliza);
			String columna = (String) request.getSession().getAttribute(COLUMNA);
			String orden = (String) request.getSession().getAttribute(ORDEN);
			listaParcelas = getSeleccionPolizaManager().getParcelas(parcela, columna, orden);

			// get pantalla a utilizar en la seccion de datos variables
			PantallaConfigurable pantalla = getSeleccionPolizaManager().getPantallaVarPoliza(
					polizaBean.getLinea().getLineaseguroid(),
					polizaBean.getLinea().isLineaGanado() ? Constants.PANTALLA_EXPLOTACIONES
							: Constants.PANTALLA_POLIZA);

			if (pantalla == null) {
				return HTMLUtils.errorMessage(SELPOLIZA, bundle.getString(MENSAJE_KO));
			}

			boolean hayParcelas = (polizaBean.getParcelas() != null && polizaBean.getParcelas().size() > 0);

			// --> YA NO SE COMPRUEBA .Hay parcelas repetidas
			boolean parcelasRepetidas = false;
			String parcelasRepetidasOK = StringUtils.nullToString(request.getParameter("parcelasRepOK"));

			// --> YA NO SE COMPRUEBA .Hay alguna instalacion
			Boolean hayAlgunaInstalacion = false;
			String hayAlgunaInstalacionOK = StringUtils.nullToString(request.getParameter("sinInstalacionOK"));

			// ------------- HAY PARCELAS REPETIDAS(sin OK) O HO HAY
			// INSTALACIONES (sin OK) -------------
			// return --> listado de parcelas
			if ((hayParcelas && parcelasRepetidas && parcelasRepetidasOK.equals(""))
					|| (hayParcelas && !hayAlgunaInstalacion && hayAlgunaInstalacionOK.equals(""))) {
				parameters.put(POLIZA, polizaBean);
				request.getSession().setAttribute("poliza", polizaBean);

				listaParcelas = new ArrayList<Parcela>(polizaBean.getParcelas().size());
				for (Parcela parcela2 : polizaBean.getParcelas()) {
					listaParcelas.add(parcela2);
				}

				parameters.put(PANTALLA, pantalla);
				parameters.put(LIST_PARCELAS, listaParcelas);
				parameters.put(IDPOLIZA, polizaBean.getIdpoliza());
				parameters.put("codplan", polizaBean.getLinea().getCodplan());
				parameters.put("codlinea", polizaBean.getLinea().getCodlinea());
				parameters.put(TIPO_LIST, tipoListadoGrid);
				parameters.put(MODELTABLE, modelTableDecorator);

				if (parcelasRepetidas)
					parameters.put("parcelasRepetidas", true);
				else
					parameters.put("parcelasRepetidas", false);

				if (hayAlgunaInstalacion)
					parameters.put("polizaSinInstalacion", false);
				else
					parameters.put("polizaSinInstalacion", true);

				String lineaDanhoFauna = ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");

				if (lineaDanhoFauna.contains(polizaBean.getLinea().getCodlinea().toString())) {

					parameters.put("mostrarDanhosFauna", true);
					if (request.getSession().getAttribute("mapDanhoFauna") != null) {
						parameters.put("mapDanhoFauna", request.getSession().getAttribute("mapDanhoFauna"));
						request.getSession().removeAttribute("mapDanhoFauna");
					}
				} else {
					parameters.put("mostrarDanhosFauna", false);
				}
				resultado = new ModelAndView(MV_LISPARCELAS, DATOS, parameters);
			}

			// ------------- HAY PARCELAS -------------
			// return --> listado de parcelas o subvenciones (puede
			// continuar)
			else if (polizaBean.getParcelas() != null && polizaBean.getParcelas().size() > 0) {
				// compruebo que para todas las parcelas hay produccion y precio para el/los
				// modulo/s seleccionado/s
				boolean correcto = getSeleccionPolizaManager().isParcelasCorrectas(polizaBean.getIdpoliza());
				if (!correcto) {
					Long totalProd = new Long(0);
					parameters.put("totalProd", totalProd);
					parameters.put("marcarTodosChecks", marcarTodosChecks);
					String parcelasString = consultaDetallePolizaManager.getListParcelasString(listaParcelas);
					parameters.put(PARCELAS_STR, parcelasString);
					parameters.put("idsRowsChecked", idsRowsChecked);
					parameters.put(TIPO_LIST, tipoListadoGrid);
					parameters.put(CODESTADO_POL, codEstadoPolizaMayor3);
					parameters.put(POLIZA, polizaBean);
					parameters.put(LIST_PARCELAS, listaParcelas);
					parameters.put(NUM_PARC_LISTADO, listaParcelas.size());
					parameters.put(PANTALLA, pantalla);
					parameters.put(IDPOLIZA, idPoliza);
					parameters.put(MODELTABLE, modelTableDecorator);

					String lineaDanhoFauna = ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");

					if (lineaDanhoFauna.contains(polizaBean.getLinea().getCodlinea().toString())) {

						parameters.put("mostrarDanhosFauna", true);
						if (request.getSession().getAttribute("mapDanhoFauna") != null) {
							parameters.put("mapDanhoFauna", request.getSession().getAttribute("mapDanhoFauna"));
							request.getSession().removeAttribute("mapDanhoFauna");
						}
					} else {
						parameters.put("mostrarDanhosFauna", false);
					}

					logger.debug("Los capitales asegurados no son correctos.");
					parameters.put(ALERT,
							"Los capitales asegurados no son correctos. Para poder continuar debe corregirlos.");
					resultado = new ModelAndView(MV_LISPARCELAS, DATOS, parameters);
				} else {
					// se obtiene la hoja y el numero de las parcelas

					// Comprobar si el indicador de recalculo de hoja y num de la poliza o de alguna
					// parcela est� a 1
					boolean recalcularHojaNumero = getSeleccionPolizaManager().checkRecalculoHojaYNumPoliza(idPoliza);
					if (recalcularHojaNumero) {
						getSeleccionPolizaManager().obtenerHojaNumero(polizaBean);
						// guardamos las parcelas con la hoja y numero
						getSeleccionPolizaManager().savePoliza(polizaBean);

						getSeleccionPolizaManager().inicializarRecalculoHojaYNumPoliza(idPoliza);
					}
					// redirigir a la pantalla de AseguradoSubvenciones
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(IDPOLIZA, idPoliza);
					params.put(OPERACION, "");
					params.put(MODO_LECT, StringUtils.nullToString(request.getParameter(MODO_LECT)));
					params.put(VIENE_UTILIDADES, request.getParameter(VIENE_UTILIDADES));
					if (null == asegurado.getSocios() || asegurado.getSocios().isEmpty())
						params.put("tieneSocios", "false");
					return new ModelAndView("redirect:/aseguradoSubvencion.html").addAllObjects(params);
				}
			}
			// ------------- NO HAY PARCELAS -------------
			else if (!hayParcelas) {
				parameters.put(POLIZA, polizaBean);
				parameters.put(LIST_PARCELAS, polizaBean.getParcelas());
				parameters.put(PANTALLA, pantalla);
				parameters.put(MODELTABLE, modelTableDecorator);
				parameters.put(ALERT, "No hay parcelas. Rellene al menos una para continuar.");
				resultado = new ModelAndView(MV_LISPARCELAS, DATOS, parameters);
			}
			// ------------- OTROS CASOS -------------
			// return --> listado de parcelas
			else {
				parameters.put(POLIZA, polizaBean);
				parameters.put(LIST_PARCELAS, polizaBean.getParcelas());
				parameters.put(PANTALLA, pantalla);
				parameters.put(MODELTABLE, modelTableDecorator);
				resultado = new ModelAndView(MV_LISPARCELAS, DATOS, parameters);
			}

			// Si la poliza tiene distibucion de coste se elimina para que
			// pueda realizar el calculo
			getSeleccionPolizaManager().deleteDistribucionCostes(polizaBean);
		}

		// -------------------------------------------------------
		// -- CONTINUAR A SUBVENCIONES - PARA POLIZAS DE GANADO --
		// -------------------------------------------------------
		else if ("continuarGanado".equalsIgnoreCase(operacion)) {

			// Valida que la poliza tenga al menos una explotacion dada de alta
			Set<Explotacion> listadoExplotaciones = polizaBean.getExplotacions();
			if (modoLectura.compareTo(MODO_LECT) != 0) {
				if (listadoExplotaciones == null || listadoExplotaciones.isEmpty()) {
					return redirigirListadoExplotaciones(idPoliza,
							"No hay explotaciones. Rellene al menos una para continuar");
				}

				// Valida que todas las explotaciones de la poliza tengan asignado precio para
				// todos los modulos seleccionado

				if (!validaPrecioExplotaciones(listadoExplotaciones, polizaBean.getModuloPolizas())) {
					return redirigirListadoExplotaciones(idPoliza,
							"Alguna de las explotaciones no tiene precio correctamente asignado. Corrija para continuar");
				}

			}

			// Asignamos n�mero de explotacion y guardamos en la base de datos
			List<Explotacion> listadoExplotacionesOrdenada = explotacionesManager
					.asignaNumeroExplotacion(listadoExplotaciones);
			explotacionesManager.guardaExplotaciones(listadoExplotacionesOrdenada);

			// Redirigir a la pantalla de subvenciones
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(IDPOLIZA, idPoliza);
			params.put(OPERACION, "");
			params.put(MODO_LECT, StringUtils.nullToString(request.getParameter(MODO_LECT)));
			params.put(VIENE_UTILIDADES, request.getParameter(VIENE_UTILIDADES));
			if (null == asegurado.getSocios() || asegurado.getSocios().isEmpty())
				params.put("tieneSocios", "false");

			return new ModelAndView("redirect:/aseguradoSubvencion.html").addAllObjects(params);
		}

		// ----------------------------------------------------------------
		// VER ACUSE RECIBO
		// ----------------------------------------------------------------
		else if ("verAcuseRecibo".equals(operacion)) {
			LOGGER.debug("init - operacion ver Acuse Recibo");
			Map<String, Object> parametros = new HashMap<String, Object>();
			es.agroseguro.acuseRecibo.Error[] errores = null;
			Poliza pol = null;
			Long idPol = null;
			String refPoliza = null;
			BigDecimal linea = null;
			BigDecimal plan = null;
			BigDecimal idEnvio = null;

			try {

				if (request.getParameter(IDPOLIZA) != null) {
					idPol = new Long(request.getParameter(IDPOLIZA));
					pol = getSeleccionPolizaManager().getPolizaById(idPol);

					if (pol != null) {
						if (pol.getIdenvio() != null) {
							idEnvio = pol.getIdenvio();
						}
						refPoliza = pol.getReferencia();
						logger.debug("idPoliza:  " + idPol + " refPoliza: " + refPoliza + " idEnvio: " + idEnvio);
					}

					if (idEnvio == null || refPoliza == null) {

						parametros.put(IDPOL, idPoliza);
						parametros.put(ERR_LENGTH, 0);
						parametros.put(OPERACION, SEL_POL);

						resultado = new ModelAndView(MV_ERRCONTRATACION, POLIZA, polizaBean).addAllObjects(parametros);

					} else {
						linea = pol.getLinea().getCodlinea();
						plan = pol.getLinea().getCodplan();
						logger.debug("plan:  " + plan + " linea: " + linea);
						// Se obtiene un array con los errores
						errores = getSeleccionPolizaManager().getFicheroContenido(idEnvio, refPoliza, linea, plan);
						logger.debug("listado de errores - Size :  " + errores.length);

						if (errores.length == 0) {
							parametros.put(ERR_LENGTH, 0);
							parametros.put(IDPOL, idPoliza);
							parametros.put(OPERACION, SEL_POL);
						} else {
							parametros.put(IDPOL, idPoliza);
							parametros.put("errores", errores);
							parametros.put(ERR_LENGTH, errores.length);
							parametros.put(OPERACION, SEL_POL);
						}

						resultado = new ModelAndView(MV_ERRCONTRATACION, POLIZA, polizaBean).addAllObjects(parametros);

					}
				} else {
					parametros.put(IDPOLIZA, idPol);
					resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
					// Comprueba si tiene que mostrar el boton de sobreprecio
					resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
							usuario, clase.getLinea().isLineaGanado()));
				}

			} catch (BusinessException be) {
				logger.error("Se ha producido un error al recuperar los documentos de Acuse de Recibo", be);
				parametros.put(IDPOLIZA, idPoliza);
				parametros.put(ALERT, bundle.getString("mensaje.acuseRecibo.KO"));
				return new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean);
			}

			LOGGER.debug("fin - operacion ver Acuse Recibo");
			resultado = new ModelAndView(MV_ERRCONTRATACION, POLIZA, polizaBean).addAllObjects(parametros)
					.addObject("errores", errores);

		} else if ("imprimirInformeListadoParcelas".equals(operacion)) {
			if (!StringUtils.nullToString(request.getParameter(SORT)).equals("")
					&& !StringUtils.nullToString(request.getParameter(ORDER)).equals("")) {
				getOrdenacionDisplaytag(request);
			}
			parameters = consultaDetallePolizaManager.consulta(request, idPoliza);
			@SuppressWarnings("unchecked")
			List<BeanParcela> listadoParcelasPoliza = obtenerListaBeanParcela(
					(List<Parcela>) parameters.get(LIST_PARCELAS));

			request.setAttribute("esPrincipal", true);

			request.setAttribute("listaParcelasPoliza", listadoParcelasPoliza);
			resultado = new ModelAndView("forward:/informes.html?method=doInformeListadoParcelasPoliza");
			
			
		}else if("calculoRdtoHist".equals(operacion)){
			
			parameters = consultaDetallePolizaManager.consulta(request, idPoliza);
			@SuppressWarnings("unchecked")
			List<Parcela> listadoParcelasPantalla = (List<Parcela>)parameters.get("listadoParcelas");
		
		
			//recuperar la lista de parcelas seleccionadas en la pantalla
			String cadenaParcelas=(String)request.getParameter("parcelasString");
			Set<Long> idsPacelas= new HashSet<Long>(0);
			if(idsRowsChecked!=null){
				String[] arrParcelas=idsRowsChecked.split(";");
				
				for(int i=0;i<arrParcelas.length;i++){
					if(!arrParcelas[i].isEmpty()){
						idsPacelas.add(new Long(arrParcelas[i]));
					}
				}
			}
				
			try {
				listadoParcelasPantalla=getSeleccionPolizaManager().calculoRtoHist(polizaBean.getIdpoliza(), idsPacelas, realPath, usuario, 0,listadoParcelasPantalla,null);
			}catch(es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException e){
				logger.error("[doCalculoRdtoHist] Se ha producido un error (AgrException) durante el proceso", e);
				parameters.put("alertaLargo",WSUtils.debugAgrException(e));
			
			} catch (Throwable e) {
				logger.error("Se ha producido un error en calculoRdtoHist de SeleccionPolizaController",e);
			
			}
				
            parameters.put("polizaBean", polizaBean);
			parameters.put("numParcelasListado", listadoParcelasPantalla.size());
			parameters.put("parcelasString",      cadenaParcelas);
			parameters.put("listadoParcelas", listadoParcelasPantalla);
			parameters.put("tipoListadoGrid", tipoListadoGrid);
			parameters.put("modelTableDecorator", modelTableDecorator);
			parameters.put("codEstadoPoliza", codEstadoPolizaMayor3);
			parameters.put("idpoliza", idPoliza);
			
			String lineaDanhoFauna=ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");
			
			if(lineaDanhoFauna.contains(polizaBean.getLinea().getCodlinea().toString())) {
	
				parameters.put("mostrarDanhosFauna", true);
				if (request.getSession().getAttribute("mapDanhoFauna") != null) {
					parameters.put("mapDanhoFauna", request.getSession().getAttribute("mapDanhoFauna"));
					request.getSession().removeAttribute("mapDanhoFauna");
				}						
			}else {
				parameters.put("mostrarDanhosFauna", false);
			}
		
			resultado = new ModelAndView("moduloPolizas/polizas/listadoParcelas", "datos", parameters);
	
			/* Pet. 78877 ** MODIF TAM (25.10.2021) ** Inicio */
			/* Tratamiento para el calculo del Rendimiento Orientativo */
		} else if ("calcRdtoOrientativo".equals(operacion)) {

			/**** SONAR Q ** MODIF TAM (27.10.2021) ** Inicio ***/
			/* Lo sacamos a otra funcion para descargar de if el handle */

			logger.debug("Dentro de calculo de Rendimiento Orientativo [INIT]");

			parameters = obtenerDatosCalculoRdtoOrientativo(request, idPoliza, idsRowsChecked, polizaBean);

			parameters.put(TIPO_LIST, tipoListadoGrid);
			parameters.put(MODELTABLE, modelTableDecorator);
			parameters.put(CODESTADO_POL, codEstadoPolizaMayor3);

			String lineaDanhoFauna = ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");

			if (lineaDanhoFauna.contains(polizaBean.getLinea().getCodlinea().toString())) {
				if (request.getSession().getAttribute("mapDanhoFauna") != null) {
					request.getSession().removeAttribute("mapDanhoFauna");
				}
			}
			/**** SONAR Q ** MODIF TAM (27.10.2021) ** Fin ***/

			resultado = new ModelAndView(MV_LISPARCELAS, DATOS, parameters);
			/* Pet. 78877 ** MODIF TAM (25.10.2021) ** Inicio */

		} // ----------------------------------------------------------------
			// EN CUALQUIER OTRO CASO
			// ----------------------------------------------------------------
		else {
			// Mensaje de alerta en caso de no poder dar de alta poliza complementaria
			String mensajeAlerta = (String) request.getParameter(ALERT);
			if (!VACIO.equals(StringUtils.nullToString(mensajeAlerta))) {
				parameters.put(ALERT, mensajeAlerta);
			}
			// Si la operacion es volver, le metemos el idPoliza
			if ("volver".equals(operacion)) {
				LOGGER.debug("init - operacion volver");
				polizaBusqueda.setIdpoliza(idPoliza);
			}

			polizaBusqueda.setEstadoPoliza(polizaBean.getEstadoPoliza());

			listaPolizas = getSeleccionPolizaManager().getPolizas(polizaBusqueda); // get listado de polizas
			polizaBean.setLinea(clase.getLinea());
			parameters.put(LISTA_POL, listaPolizas);

			// redirect seleccionPolizas
			resultado = new ModelAndView(MV_SELPOLIZAS, POLIZA, polizaBean).addObject("usuario", usuario);

			// Comprueba si tiene que mostrar el boton de sobreprecio - Solo para polizas de
			// agrarios
			resultado.addObject(POLIZASBP, getSeleccionPolizaManager().mostrarBotonSbp(parameters, listaPolizas,
					usuario, clase.getLinea().isLineaGanado()));
		}

		// COMPROBAMOS QUE TENEMOS AL MENOS UNA POLIZA ENVIADA CORRECTA PARA
		// PODER DAR DE ALTA UNA POLIZA COMPLEMENTARIA
		// A�adida la opcion de dar de alta tambien con la poliza en estado provisional
		parameters.put("PolEnvidasCorrectas", clase.getLinea().isLineaGanado() ? false
				: getSeleccionPolizaManager().habilitarComplementaria(parameters, listaPolizas));

		// Obtenemos la lista de estados posibles de la poliza
		List<EstadoPoliza> listaEstados = getSeleccionPolizaManager().getEstadosPoliza(null);
		parameters.put("listaEstados", listaEstados);

		List<TipoRdto> listaTipoRendimientos = getSeleccionPolizaManager().getTiposRendimiento();
		parameters.put("listaTipoRendimientos", listaTipoRendimientos);
		parameters.put(RDTO_HIST, parameters.get(RDTO_HIST) != null ? parameters.get(RDTO_HIST) : "0");
		parameters.put("tieneRdtoHist", tieneRdtoHist);

		if (!StringUtils.nullToString(request.getParameter(VIENE_UTILIDADES)).equals("")) {
			parameters.put(VIENE_UTILIDADES, request.getParameter(VIENE_UTILIDADES));
		}
		
		//ESC-30568 / GD-18421
		parameters.put("rdtoOrientativoPulsado", rdtoOrientativoPulsado);
		parameters.put("rdtoHistPulsado", rdtoHistPulsado);
		//ESC-30568 / GD-18421
		
		resultado.addAllObjects(parameters);

		return resultado;
	}

	private List<Poliza> getListPlzAsociadas(BigDecimal entidad, Colectivo colectivo, Asegurado asegurado,
			BigDecimal Varclase) {
		List<Poliza> listaPolizas;
		// Creamos un objeto poliza y buscamos todas las polizas asociadas
		// para realizar las busquedas.
		Poliza poliza = new Poliza();
		poliza.setAsegurado(asegurado);
		poliza.setColectivo(colectivo);
		poliza.setClase(Varclase);
		poliza.setLinea(colectivo.getLinea());

		poliza.getAsegurado().getEntidad().setCodentidad(entidad);
		poliza.getColectivo().getTomador().getId().setCodentidad(entidad);
		poliza.getReferencia();

		listaPolizas = getSeleccionPolizaManager().getPolizas(poliza);
		return listaPolizas;
	}

	/**
	 * Valida que todas las explotaciones de la poliza tengan registro de precios
	 * para cada modulo elegido
	 * 
	 * @param listadoExplotaciones
	 * @param moduloPolizas
	 * @return
	 */
	private boolean validaPrecioExplotaciones(Set<Explotacion> listadoExplotaciones, Set<ModuloPoliza> moduloPolizas) {

		if (moduloPolizas == null || moduloPolizas.isEmpty())
			return false;
		List<String> lstModulos = new ArrayList<String>();
		for (ModuloPoliza modP : moduloPolizas) {
			if (!lstModulos.contains(modP.getId().getCodmodulo()))
				lstModulos.add(modP.getId().getCodmodulo());
		}
		for (Explotacion exp : listadoExplotaciones) {
			Set<GrupoRaza> listaGR = exp.getGrupoRazas();
			if (listaGR == null || listaGR.isEmpty())
				return false;

			for (GrupoRaza gr : listaGR) {
				Set<PrecioAnimalesModulo> listaPAM = gr.getPrecioAnimalesModulos();
				// Comprueba que el grupo de raza tiene tantos registros de precio como modulos
				// elegidos la poliza
				if (listaPAM == null || listaPAM.isEmpty() || listaPAM.size() != lstModulos.size())
					return false;
			}
		}

		return true;
	}

	/**
	 * Redirige a la pantalla de explotaciones de la poliza asociada al idPoliza
	 * mostrando la alerta indicada
	 * 
	 * @param idPoliza
	 * @return
	 */
	private ModelAndView redirigirListadoExplotaciones(Long idPoliza, String alerta) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(IDPOLIZA, idPoliza);
		if (alerta != null)
			params.put(ALERT, alerta);
		// Redirige a la pantalla de listado de explotaciones mostrando el mensaje de
		// error
		return new ModelAndView("redirect:/listadoExplotaciones.html").addAllObjects(params);
	}

	/**
	 * DAA 21/11/12 ordenacion para la pantalla de parcelas
	 * 
	 * @param request
	 */
	private void getOrdenacionDisplaytag(final HttpServletRequest request) {

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
		request.getSession().setAttribute(COLUMNA, columna);
	}

	/**
	 * Reliza las comprobaciones pertinentes para comprobar si se permite el alta.
	 * 
	 * @param request
	 *            Si listaPolizas tiene valor null lo usaremos para recoger de
	 *            sesion datos necesarios para recuperar el listado de las polizas
	 *            que pertenecen a ese usuario y colectivo
	 * @param listaPolizas
	 *            Si viene a null recogeremos de nuevo el listado de las polizas que
	 *            pertenecen a ese usuario y colectivo
	 */
	private boolean isPermiteAlta(HttpServletRequest request, List<Poliza> listaPolizas) {
		try {
			// Comprobamos si cumple alguna de las condiciones para las cuales
			// se encuentra permitido el alta.
			if (isPermiteAltaPorAnulacion(listaPolizas))
				return true;
			else if (getSeleccionPolizaManager().isPermiteAltaPorModulosCompatibles(listaPolizas))
				return true;
			else if (isPermiteAltaPorPendienteValidacion(listaPolizas))
				return true;

		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	private boolean isPermiteAltaPorPendienteValidacion(List<Poliza> listaPolizas) {
		Iterator<Poliza> itListaPolizas = listaPolizas.iterator();
		while (itListaPolizas.hasNext()) {
			Poliza poliza = (Poliza) itListaPolizas.next();
			if (null == poliza.getEstadoPoliza().getIdestado()
					|| poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION))
				return false;
		}
		return true;
	}

	/**
	 * Indica si dentro de un listado de polizas, existe alguna con estado anulada,
	 * en cuyo caso se modifica una variable que indica si se muestra el alta o no.
	 * 
	 * @param listaPolizas
	 */
	private boolean isPermiteAltaPorAnulacion(List<Poliza> listaPolizas) {
		Iterator<Poliza> itListaPolizas = listaPolizas.iterator();
		while (itListaPolizas.hasNext()) {
			Poliza poliza = (Poliza) itListaPolizas.next();
			if (null == poliza.getEstadoPoliza().getIdestado()
					|| !poliza.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_ANULADA))
				return false;
		}
		return true;
	}

	public Boolean tieneSubvenciones(Set<VistaImportes> fluxCondensatorHolder) {
		Boolean result = false;
		for (VistaImportes fco : fluxCondensatorHolder) {
			if (result)
				break;
			for (VistaImportesPorGrupoNegocio viGn : fco.getVistaImportesPorGrupoNegocio()) {
				if (viGn.getSubvCCAA() != null && viGn.getSubvCCAA().size() > 0) {
					result = true;
					break;
				}
				if (viGn.getSubvEnesa() != null && viGn.getSubvEnesa().size() > 0) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

	private List<BeanParcela> obtenerListaBeanParcela(List<Parcela> listaParcelas) {
		List<BeanParcela> listParcelasAnexo = new ArrayList<BeanParcela>();
		ModelTableDecoratorListaParcelasTodas decorator = new ModelTableDecoratorListaParcelasTodas();

		Iterator<Parcela> it = listaParcelas.iterator();
		while (it.hasNext()) {
			Parcela parcela = it.next();

			for (CapitalAsegurado capitalAsegurado : parcela.getCapitalAsegurados()) {

				/** SONAR Q ** MODIF TAM (27.10.2021) ** Inicio **/
				/* Sacamos a una funci�n a parte la carga de la parcela */
				BeanParcela bp = new BeanParcela();

				bp = cargarBeanParcela(parcela);

				bp.setIdCatSigpac(decorator.getIdCat(parcela));

				// Prod
				bp.setProduccion(decorator.getProduccion(capitalAsegurado));

				// Precio
				bp.setPrecio(decorator.getPrecio(capitalAsegurado));

				// T.Capital
				bp.setTipoCapital(decorator.getTcapital(capitalAsegurado));

				// Fecha Garantia
				bp.setFechaGarantia(
						parcela.getTipoparcela().equals('P') ? decorator.getFechaFin(capitalAsegurado) : "");

				// Numero unidades
				bp.setNumUnidades(decorator.getNumUnidades(capitalAsegurado));

				// Sistema Cultivo
				bp.setSistemaCultivo(decorator.getSistemaCultivo(capitalAsegurado));

				// Sistema Conduccion
				bp.setSistemaConduccion(decorator.getSistemaConduccion(capitalAsegurado));

				// Datos dependientes del capital asegurado
				// Super./m - Si es una instalacion la superficie siempre es 0
				bp.setSuperm(new Character('E').equals(parcela.getTipoparcela()) ? "0"
						: decorator.getSuperf(capitalAsegurado));

				listParcelasAnexo.add(bp);

			}

		}
		return listParcelasAnexo;
	}

	/** SONAR Q ** MODIF TAM (27.10.2021) ** Inicio **/
	/* Nueva funci�n para cargar los datos de la parcela */
	private BeanParcela cargarBeanParcela(Parcela parc) {
		BeanParcela bparc = new BeanParcela();

		// Datos dependientes de la parcela
		// PRV, CMC, TRM, SBT
		bparc.setCodProvincia(parc.getTermino().getId().getCodprovincia());
		bparc.setCodComarca(parc.getTermino().getId().getCodcomarca());
		bparc.setCodTermino(parc.getTermino().getId().getCodtermino());
		bparc.setSubtermino(
				parc.getTermino().getId().getSubtermino() != null ? parc.getTermino().getId().getSubtermino().toString()
						: null);

		// CUL
		bparc.setCodCultivo(parc.getCodcultivo());

		// VAR
		bparc.setCodVariedad(parc.getCodvariedad());

		// NUM
		if (parc.getHoja() != null && parc.getNumero() != null) {
			bparc.setNumero(parc.getHoja() + "-" + parc.getNumero());
		}

		// Id Cat/SIGPAC

		// Para informe Excel
		if (parc.getPoligono() != null && parc.getParcela() != null) {
			bparc.setParcela(parc.getParcela());
			bparc.setPoligono(parc.getPoligono());
		} else {
			bparc.setCodprovsigpac(parc.getCodprovsigpac());
			bparc.setCodtermsigpac(parc.getCodtermsigpac());
			bparc.setAgrsigpac(parc.getAgrsigpac());
			bparc.setZonasigpac(parc.getZonasigpac());
			bparc.setPoligonosigpac(parc.getPoligonosigpac());
			bparc.setParcelasigpac(parc.getParcelasigpac());
			bparc.setRecintosigpac(parc.getRecintosigpac());
		}

		// Nombre
		bparc.setNombre(parc.getNomparcela());

		return bparc;
	}

	private Colectivo informarColectivo(Usuario usu, Poliza polBean) {
		Colectivo col = new Colectivo();

		if (usu.getColectivo() != null) {
			col = usu.getColectivo();
		} else {
			col = polBean.getColectivo();
		}
		return col;
	}

	private BigDecimal informarEntidad(Usuario usu, Poliza polBean) {

		BigDecimal ent = null;

		if (usu.getColectivo() != null) {
			ent = usu.getColectivo().getTomador().getId().getCodentidad();
		} else {
			ent = polBean.getColectivo().getTomador().getId().getCodentidad();
		}

		return ent;
	}

	private Asegurado informarAsegurado(Usuario usu, Poliza polBean) {

		Asegurado ase = new Asegurado();
		if (usu.getAsegurado() != null) {
			ase = usu.getAsegurado();
		} else {
			ase = polBean.getAsegurado();
		}

		return ase;
	}

	private Map<String, Object> obtenerDatosCalculoRdtoOrientativo(final HttpServletRequest request, Long idPoliza,
			String idsRowsChecked, Poliza polBean) throws Exception {
		
		logger.debug("SeleccionPolizaController-obtenerDatosCalculoRdtoOrientativo [INIT]");

		Map<String, Object> params = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");

		params = consultaDetallePolizaManager.consulta(request, idPoliza);
		@SuppressWarnings("unchecked")
		List<Parcela> listadoParcelasPantalla = (List<Parcela>) params.get(LIST_PARCELAS);

		// recuperar la lista de parcelas seleccionadas en la pantalla
		String cadenaParcelas = (String) request.getParameter(PARCELAS_STR);
		Set<Long> idsPacelas = new HashSet<Long>(0);
		if (idsRowsChecked != null) {
			String[] arrParcelas = idsRowsChecked.split(";");

			for (int i = 0; i < arrParcelas.length; i++) {
				if (!arrParcelas[i].isEmpty()) {
					idsPacelas.add(new Long(arrParcelas[i]));
				}
			}
		}

		try {
			listadoParcelasPantalla = getSeleccionPolizaManager().calculoRdtoOrientativo(polBean.getIdpoliza(),
					idsPacelas, realPath, usuario, 0, listadoParcelasPantalla, null);
		} catch (es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException e) {
			logger.error("[calcRdtoOrientativo] Se ha producido un error (AgrException) durante el proceso", e);
			params.put("alertaLargo", WSUtils.debugAgrException(e));

		} catch (Throwable e) {
			logger.error("Se ha producido un error en calculoRdtoHist de SeleccionPolizaController", e);

		}

		params.put(POLIZA, polBean);
		params.put(NUM_PARC_LISTADO, listadoParcelasPantalla.size());
		params.put(PARCELAS_STR, cadenaParcelas);
		params.put(LIST_PARCELAS, listadoParcelasPantalla);
		params.put(IDPOLIZA, idPoliza);

		String lineaDanhoFauna = ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");

		if (lineaDanhoFauna.contains(polBean.getLinea().getCodlinea().toString())) {

			params.put("mostrarDanhosFauna", true);
			if (request.getSession().getAttribute("mapDanhoFauna") != null) {
				params.put("mapDanhoFauna", request.getSession().getAttribute("mapDanhoFauna"));
			}
		} else {
			params.put("mostrarDanhosFauna", false);
		}
		
		logger.debug("SeleccionPolizaController-obtenerDatosCalculoRdtoOrientativo [END]");

		return params;
	}

	/**
	 * @throws DAOException
	 * @throws NumberFormatException
	 **/
	private Map<String, Object> eliminarParcela(final HttpServletRequest request, Long idPoliza, Poliza polBean,
			String tipoListadoGrid) throws DAOException {

		Map<String, Object> params = new HashMap<String, Object>();

		// ----------------------------------------------------------------
		// ELIMINAR PARCELA: eliminar parcela in BD
		// ----------------------------------------------------------------
		// 1.- Obtenemos el id de la parcela a eliminar
		String idParcela = request.getParameter("codParcela");

		// DAA 19/12/2012 Si la parcela a eliminar tiene instalaciones hay que comprobar
		// si existe otra parcela
		// para esa poliza con el mismo SIGPAC para moverla
		if (idPoliza != null && !"".equals(idParcela)) {
			boolean correcto = getSeleccionPolizaManager().borrarParcela(idPoliza, new Long(idParcela), true);
			if (correcto) {
				getSeleccionPolizaManager().marcarRecalculoHojaYNum(idPoliza);
				params.put(MENSAJE, bundle.getString("mensaje.baja.OK"));
			} else {
				params.put(ALERT, bundle.getString(MENSAJE_BAJAKO));
			}
		}

		// Recuperamos la poliza con las parcelas actualizadas
		params.put(POLIZA, polBean);

		PantallaConfigurable pantalla = getSeleccionPolizaManager().getPantallaVarPoliza(
				polBean.getLinea().getLineaseguroid(),
				polBean.getLinea().isLineaGanado() ? Constants.PANTALLA_EXPLOTACIONES : Constants.PANTALLA_POLIZA);

		if (pantalla != null) {
			List<Parcela> listaParcelas = null;
			Parcela parcela = new Parcela();

			params.put(PANTALLA, pantalla);

			// Recuperamos todas las parcelas
			parcela = consultaDetallePolizaManager.getBeanParcelaFromRequest(request, idPoliza, tipoListadoGrid);
			String columna = (String) request.getSession().getAttribute(COLUMNA);
			String orden = (String) request.getSession().getAttribute(ORDEN);
			listaParcelas = getSeleccionPolizaManager().getParcelas(parcela, columna, orden);

			String parcelasString = consultaDetallePolizaManager.getListParcelasString(listaParcelas);

			String listCodModulos = getSeleccionPolizaManager().getListModulesWithComparativas(new Long(idPoliza));

			params.put(LIST_MODULOS, listCodModulos);
			params.put(NUM_PARC_LISTADO, listaParcelas.size());
			params.put(PARCELAS_STR, parcelasString);
			params.put(LIST_PARCELAS, listaParcelas);
			params.put(IDPOLIZA, idPoliza);

			String lineaDanhoFauna = ResourceBundle.getBundle("agp").getString("lineas.danhosFauna");

			if (lineaDanhoFauna.contains(polBean.getLinea().getCodlinea().toString())) {

				params.put("mostrarDanhosFauna", true);
				if (request.getSession().getAttribute("mapDanhoFauna") != null) {
					params.put("mapDanhoFauna", request.getSession().getAttribute("mapDanhoFauna"));
					request.getSession().removeAttribute("mapDanhoFauna");
				}
			} else {
				params.put("mostrarDanhosFauna", false);
			}
		}

		return params;
	}

	/** SONAR Q ** MODIF TAM (27.10.2021) ** Fin **/

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setColectivoManager(ColectivoManager colectivoManager) {
		this.colectivoManager = colectivoManager;
	}

	public void setAseguradoManager(AseguradoManager aseguradoManager) {
		this.aseguradoManager = aseguradoManager;
	}

	public void setConsultaSbpManager(ConsultaSbpManager consultaSbpManager) {
		this.consultaSbpManager = consultaSbpManager;
	}

	public void setConsultaDetallePolizaManager(ConsultaDetallePolizaManager consultaDetallePolizaManager) {
		this.consultaDetallePolizaManager = consultaDetallePolizaManager;
	}

	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}

	public void setPolizasPctComisionesManager(IPolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}

	public void setExplotacionesManager(ExplotacionesManager explotacionesManager) {
		this.explotacionesManager = explotacionesManager;
	}

	public void setPolizaRCManager(PolizaRCManager polizaRCManager) {
		this.polizaRCManager = polizaRCManager;
	}

	/* DNF 24/07/2020 PET.63485 */
	public void setCalculoPrecioProduccionManager(CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}

	public void setHistoricoEstadosManager(HistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}
	/* FIN DNF 24/07/2020 PET.63485 */
	
	public void setDocumentacionGedManager(IDocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}
}