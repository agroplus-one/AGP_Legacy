package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ConsultaPolizaSbpFilter;
import com.rsi.agp.core.jmesa.service.IConsultaPolizaSbpService;
import com.rsi.agp.core.jmesa.sort.ConsultaPolizaSbpSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.impl.PolizaComplementariaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.sbp.IConsultaSbpDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

@SuppressWarnings("deprecation")
public class ConsultaPolizaSbpService implements IConsultaPolizaSbpService {

	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String FECHAENVIO = "fechaenvio";
	private static final String ESTADO_POLIZA_IDESTADO = "estadoPoliza.idestado";
	private static final String ASEGURADO_NOMBRE = "asegurado.nombre";
	private static final String ASEGURADO_NIFCIF = "asegurado.nifcif";
	private static final String COLECTIVO_IDCOLECTIVO = "colectivo.idcolectivo";
	private static final String CLASE2 = "clase";
	private static final String LINEA_CODLINEA = "linea.codlinea";
	private static final String LINEA_CODPLAN = "linea.codplan";
	private static final String USUARIO_CODUSUARIO = "usuario.codusuario";
	private static final String CODMODULO = "codmodulo";
	private static final String REFERENCIA2 = "referencia";
	private static final String OFICINA2 = "oficina";
	private static final String COLECTIVO_TOMADOR_ID_CODENTIDAD = "colectivo.tomador.id.codentidad";
	private static final String POL_BUSQUEDA = "polBusqueda";
	private IConsultaSbpDao consultaSbpDao;
	private PolizaComplementariaManager polizaComplementariaManager;
	private ISimulacionSbpManager simulacionSbpManager;
	private String id;
	private Log logger = LogFactory.getLog(getClass());

	// Constantes para los nombres de las columnas del listado
	private final String ID_STR = "ID";
	private final String ENTIDAD = "ENTIDAD";
	private final String OFICINA = "OFICINA";
	private final String USUARIO = "USUARIO";
	private final String PLAN = "PLAN";
	private final String LINEA = "LINEA";
	private final String COLECTIVO = "COLECTIVO";
	private final String DCCOLECTIVO = "DCCOLECTIVO";
	private final String REFERENCIA = "REFERENCIA";
	private final String DCREFERENCIA = "DCREFERENCIA";
	private final String NIF = "NIF";
	private final String NOMBRE = "NOMBRE";
	private final String CLASE = "CLASE";
	private final String MODULO = "MODULO";
	private final String ESTADO = "ESTADO";
	private final String FECHA = "FECHA";
	private final String ESTADOSBP = "ESTADOSBP";
	private final String ENTMEDIADORA = "ENTMEDIADORA";
	private final String SUBENTMEDIADORA = "SUBENTMEDIADORA";
	private final String DELEGACION = "DELEGACION";

	// &nbsp;
	private final String NBSP = "&nbsp;";

	// Mapa con las columnas del listado y los campos del filtro de busqueda
	private HashMap<String, String> columnas = new HashMap<String, String>();

	@Override
	public int getConsultaPolizaSbpCountWithFilter(ConsultaPolizaSbpFilter filter, String nombreAseg,
			List<Long> lstLineasSbp) {
		return consultaSbpDao.getConsultaPolizaSbpCountWithFilter(filter, nombreAseg, lstLineasSbp);
	}

	@Override
	public Collection<Poliza> getConsultaPolizasSbpWithFilterAndSort(ConsultaPolizaSbpFilter filter,
			ConsultaPolizaSbpSort sort, int rowStart, int rowEnd, String nombreAseg, List<Long> lstLineasSbp)
			throws BusinessException {
		return consultaSbpDao.getConsultaPolizasSbpWithFilterAndSort(filter, sort, rowStart, rowEnd, nombreAseg,
				lstLineasSbp);
	}

	/**
	 * Busca las polizas para sobreprecio que se ajusten al filtro y genera la tabla
	 * para presentarlas
	 * 
	 * @param request
	 * @param response
	 * @param poliza
	 *            Objeto que encapsula el filtro de la busqueda
	 * @return Codigo de la tabla de presentacion de las polizas
	 * @throws Exception
	 */
	public String getTablaPolizasParaSbp(HttpServletRequest request, HttpServletResponse response, Poliza poliza,
			List<Long> lstLineasSbp, String origenLlamada, List<BigDecimal> listaGrupoEntidades,
			Map<Long, List<BigDecimal>> cultivosPorLinea, List<BigDecimal> listaGrupoOficina) throws Exception {

		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response, poliza, origenLlamada);

		String nombreAseg = (String) request.getParameter("nombreAseg");

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		if (!Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())) {
			listaGrupoEntidades = null;
		}
		if (!Constants.PERFIL_USUARIO_JEFE_ZONA.equals(usuario.getPerfil())) {
			listaGrupoOficina = null;
		}
		// Configura el filtro y la ordenacion, busca las polizas y las carga en el
		// TableFacade
		setDataAndLimitVariables(tableFacade, nombreAseg, lstLineasSbp, listaGrupoEntidades, listaGrupoOficina);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade, cultivosPorLinea);

	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla de polizas
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private TableFacade crearTableFacade(HttpServletRequest request, HttpServletResponse response, Poliza poliza,
			String origenLlamada) throws Exception {

		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = "";
		perfil = usuario.getPerfil().substring(4);

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id, request);

		// Carga las columnas a mostrar en el listado en el TableFacade y devuelve un
		// Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade);

		tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.

		// Defino los tipos para los filtros. Habra que redefinir en el filter la forma
		// de filtrar los campos que tienen un tratamiento especial (distinto de 'like
		// %valor%')
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));

		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("consultaPolizasSbp_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaPolizasSbp_LIMIT"));
				}
			}
			Poliza pol = new Poliza();
			if ("true".equals(request.getParameter("recogerPolSesion"))) {
				pol = (Poliza) request.getSession().getAttribute(POL_BUSQUEDA);

				if (pol != null) {
					cargarFiltrosBusqueda(columnas, pol, tableFacade, request);
				}
			} else {
				if (StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
					// -- FILTROS POR DEFECTO --
					// filtro por entidad del usuario
					switch (new Integer(perfil).intValue()) {
					case 1:
						poliza.getColectivo().getTomador().getId()
								.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						break;
					case 3:
						poliza.getColectivo().getTomador().getId()
								.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						poliza.setOficina(usuario.getOficina().getId().getCodoficina().toString());
						break;
					case 2:
						poliza.getColectivo().getTomador().getId()
								.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						poliza.setOficina(usuario.getOficina().getId().getCodoficina().toString());
						break;
					case 4:
						break;
					case 0:
					case 5:
						poliza.getColectivo().getTomador().getId()
								.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						poliza.getColectivo().getTomador().getEntidad()
								.setNomentidad(usuario.getOficina().getEntidad().getNomentidad());
						break;

					default:
						break;
					}
					// Anadimos el filtro por ultimo plan
					BigDecimal codPlan = simulacionSbpManager.getPlanSbp();
					if (codPlan != null) {
						poliza.getLinea().setCodplan(codPlan);
					}
				}
				// Carga en el TableFacade los filtros de busqueda introducidos en el formulario
				if (StringUtils.nullToString(origenLlamada).equals("primeraBusqueda")) {
					cargarFiltrosBusqueda(columnas, poliza, tableFacade, request);
				}
				// guardo filtro en sesion
				request.getSession().setAttribute(POL_BUSQUEDA, poliza);

				// -- ORDENACION POR DEFECTO --> ORDER ASC -> Entidad,oficina,poliza y modulo
				Sort sortEntidad = new Sort(1, COLECTIVO_TOMADOR_ID_CODENTIDAD, Order.ASC);
				Sort sortOficina = new Sort(2, OFICINA2, Order.ASC);
				Sort sortReferencia = new Sort(7, REFERENCIA2, Order.ASC);
				Sort sortModulo = new Sort(9, CODMODULO, Order.ASC);
				tableFacade.getLimit().getSortSet().addSort(sortEntidad);
				tableFacade.getLimit().getSortSet().addSort(sortOficina);
				tableFacade.getLimit().getSortSet().addSort(sortReferencia);
				tableFacade.getLimit().getSortSet().addSort(sortModulo);
			}
		}
		switch (new Integer(perfil).intValue()) {
		case 4:
			Filter filterUsuario = new Filter(USUARIO_CODUSUARIO, usuario.getCodusuario());
			tableFacade.getLimit().getFilterSet().addFilter(filterUsuario);
			break;

		default:
			break;
		}

		// guardamos el filtro
		guardarFiltro(request, tableFacade);
		return tableFacade;

	}

	/**
	 * Metodo para guardar el filtro en sesion
	 * 
	 * @param request
	 * @param tableFacade
	 */
	private void guardarFiltro(HttpServletRequest request, TableFacade tableFacade) {
		Poliza poliza = new Poliza();
		FilterSet filter = tableFacade.getLimit().getFilterSet();
		Collection<Filter> colFil = filter.getFilters();
		Iterator<Filter> it = (colFil.iterator());
		while (it.hasNext()) {
			Filter fil = it.next();
			// Entidad
			if (fil.getProperty().equals(COLECTIVO_TOMADOR_ID_CODENTIDAD)) {
				poliza.getColectivo().getTomador().getId().setCodentidad(new BigDecimal(fil.getValue().toString()));
			}
			// Oficina
			if (fil.getProperty().equals(OFICINA2)) {
				poliza.setOficina(fil.getValue().toString());
			}
			// Usuario
			if (fil.getProperty().equals(USUARIO_CODUSUARIO)) {
				poliza.getUsuario().setCodusuario((fil.getValue().toString()));
			}
			// Plan
			if (fil.getProperty().equals(LINEA_CODPLAN)) {
				poliza.getLinea().setCodplan(new BigDecimal(fil.getValue().toString()));
			}
			// Linea
			if (fil.getProperty().equals(LINEA_CODLINEA)) {
				poliza.getLinea().setCodlinea(new BigDecimal(fil.getValue().toString()));
			}
			// Clase
			if (fil.getProperty().equals(CLASE2)) {
				poliza.setClase(new BigDecimal(fil.getValue().toString()));
			}
			// Referencia de poliza
			if (fil.getProperty().equals(REFERENCIA2)) {
				poliza.setReferencia(fil.getValue().toString());
			}
			// Referencia colectivo
			if (fil.getProperty().equals(COLECTIVO_IDCOLECTIVO)) {
				poliza.getColectivo().setIdcolectivo(fil.getValue().toString());
			}
			// Modulo
			if (fil.getProperty().equals(CODMODULO)) {
				poliza.setCodmodulo(fil.getValue().toString());
			}
			// CIF/NIF asegurado
			if (fil.getProperty().equals(ASEGURADO_NIFCIF)) {
				poliza.getAsegurado().setNifcif(fil.getValue().toString());
			}
			// Nombre asegurado
			if (fil.getProperty().equals(ASEGURADO_NOMBRE)) {
				poliza.getAsegurado().setNombreCompleto(fil.getValue().toString());
			}
			// Estado de la poliza
			if (fil.getProperty().equals(ESTADO_POLIZA_IDESTADO)) {
				poliza.getEstadoPoliza().setIdestado(new BigDecimal(fil.getValue().toString()));
			}
			// Fecha de envio
			if (fil.getProperty().equals(FECHAENVIO)) {
				poliza.setFechaenvio(new Date(fil.getValue().toString()));
			}

			/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
			// Entidad Mediadora
			if (fil.getProperty().equals("colectivo.subentidadMediadora.id.codentidad")) {
				poliza.getColectivo().getSubentidadMediadora().getId()
						.setCodentidad(new BigDecimal(fil.getValue().toString()));
			}
			// Subentidad Mediadora
			if (fil.getProperty().equals("colectivo.subentidadMediadora.id.codsubentidad")) {
				poliza.getColectivo().getSubentidadMediadora().getId()
						.setCodsubentidad(new BigDecimal(fil.getValue().toString()));
			}
			// Delegacion
			if (fil.getProperty().equals("usuario.delegacion")) {
				poliza.getUsuario().setDelegacion(new BigDecimal(fil.getValue().toString()));

			}
			/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */

		}
		// guardo filtro en sesion
		request.getSession().setAttribute(POL_BUSQUEDA, poliza);
	}

	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * 
	 * @param poliza
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String, String> columnas, Poliza poliza, TableFacade tableFacade,
			HttpServletRequest request) {
		// Entidad
		if (FiltroUtils.noEstaVacio(poliza.getColectivo().getTomador().getId().getCodentidad()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ENTIDAD),
					poliza.getColectivo().getTomador().getId().getCodentidad().toString()));
		// Oficina
		if (FiltroUtils.noEstaVacio(poliza.getOficina()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(OFICINA), poliza.getOficina()));
		// Usuario
		if (FiltroUtils.noEstaVacio(poliza.getUsuario().getCodusuario()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(USUARIO), poliza.getUsuario().getCodusuario()));
		// Plan
		if (FiltroUtils.noEstaVacio(poliza.getLinea().getCodplan()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(PLAN), poliza.getLinea().getCodplan().toString()));
		// Linea
		if (FiltroUtils.noEstaVacio(poliza.getLinea().getCodlinea()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(LINEA), poliza.getLinea().getCodlinea().toString()));
		// Clase
		if (FiltroUtils.noEstaVacio(poliza.getClase()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CLASE), poliza.getClase().toString()));
		// Referencia
		if (FiltroUtils.noEstaVacio(poliza.getReferencia()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(REFERENCIA), poliza.getReferencia()));
		// Referencia de colectivo
		if (FiltroUtils.noEstaVacio(poliza.getColectivo().getIdcolectivo()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(COLECTIVO), poliza.getColectivo().getIdcolectivo()));
		// DC de colectivo
		if (FiltroUtils.noEstaVacio(poliza.getColectivo().getDc()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(DCCOLECTIVO), poliza.getColectivo().getDc()));
		// Modulo
		if (FiltroUtils.noEstaVacio(poliza.getCodmodulo()))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(MODULO), poliza.getCodmodulo()));
		// Fecha de envio
		if (FiltroUtils.noEstaVacio(poliza.getFechaenvio()))
			tableFacade.getLimit().getFilterSet().addFilter(
					new Filter(columnas.get(FECHA), new SimpleDateFormat(DD_MM_YYYY).format(poliza.getFechaenvio())));
		// CIF/NIF
		if (FiltroUtils.noEstaVacio(poliza.getAsegurado().getNifcif()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(NIF), poliza.getAsegurado().getNifcif()));
		// Nombre asegurado
		if (FiltroUtils.noEstaVacioSinEspacios(poliza.getAsegurado().getNombreCompleto()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(NOMBRE), poliza.getAsegurado().getNombreCompleto()));
		// Estado
		if (FiltroUtils.noEstaVacio(poliza.getEstadoPoliza().getIdestado()))
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(ESTADO), poliza.getEstadoPoliza().getIdestado().toString()));

		/* Pet. 63473 ** MODIF TAM (20/12/2021) ** inicio */
		// Entidad Mediadora
		String entmediadora = request.getParameter("entMediadora");
		String subentmediadora = request.getParameter("subEntmediadora");
		String delegacion = request.getParameter("deleg");

		if (!StringUtils.nullToString(entmediadora).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(ENTMEDIADORA), entmediadora));
		}

		if (!StringUtils.nullToString(subentmediadora).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(SUBENTMEDIADORA), subentmediadora));
		}

		if (!StringUtils.nullToString(delegacion).equals("")) {
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(DELEGACION), delegacion));
		}
		/* Pet. 63473 ** MODIF TAM (20/12/2021) ** fin */

	}

	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade y devuelve un
	 * Map con ellas
	 * 
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("all")
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {

		// Crea el Map con las columnas del listado y los campos del filtro de busqueda
		// si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(ENTIDAD, COLECTIVO_TOMADOR_ID_CODENTIDAD);
			columnas.put(OFICINA, OFICINA2);
			columnas.put(USUARIO, USUARIO_CODUSUARIO);
			columnas.put(PLAN, LINEA_CODPLAN);
			columnas.put(LINEA, LINEA_CODLINEA);
			columnas.put(COLECTIVO, COLECTIVO_IDCOLECTIVO);
			columnas.put(DCCOLECTIVO, "colectivo.dc");
			columnas.put(REFERENCIA, REFERENCIA2);
			columnas.put(DCREFERENCIA, "dc");
			columnas.put(NIF, ASEGURADO_NIFCIF);
			columnas.put(NOMBRE, ASEGURADO_NOMBRE);
			columnas.put(CLASE, CLASE2);
			columnas.put(MODULO, CODMODULO);
			columnas.put(ESTADO, ESTADO_POLIZA_IDESTADO);
			columnas.put(FECHA, FECHAENVIO);
			columnas.put(ESTADOSBP, "estadoSbp");
			/* Pet. 63473 ** MODIF TAM (21.12.2021) */
			columnas.put(ENTMEDIADORA, "colectivo.subentidadMediadora.id.codentidad");
			columnas.put(SUBENTMEDIADORA, "colectivo.subentidadMediadora.id.codsubentidad");
			columnas.put(DELEGACION, "usuario.delegacion");

		}

		// Configura el TableFacade con las columnas que se quieren mostrar
		/* Pet. 79014 ** MODIF (18.03.2022) ** Inicio */
		/*
		 * Se a�aden las dos nuevas columnas de Entidad Mediadora y Subentidad Mediadora
		 */
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(ENTIDAD), columnas.get(ENTMEDIADORA),
				columnas.get(SUBENTMEDIADORA), columnas.get(OFICINA), columnas.get(USUARIO), columnas.get(PLAN),
				columnas.get(LINEA), columnas.get(COLECTIVO), columnas.get(REFERENCIA), columnas.get(CLASE),
				columnas.get(MODULO), columnas.get(NIF), columnas.get(NOMBRE), columnas.get(ESTADO),
				columnas.get(FECHA), columnas.get(ESTADOSBP));

		// Devuelve el mapa
		return columnas;
	}

	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos
	 * de las polizas y carga el TableFacade con ellas
	 * 
	 * @param tableFacade
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, String nombreAseg, List<Long> lstLineasSbp,
			List<BigDecimal> listaGrupoEntidades, List<BigDecimal> listaGrupoOficina) {

		// Obtiene el Filter para la busqueda de polizas
		Limit limit = tableFacade.getLimit();
		ConsultaPolizaSbpFilter consultaFilter = getConsultaPolizaSbpFilter(limit, listaGrupoEntidades,
				listaGrupoOficina);

		// Obtiene el numero de filas que cumplen el filtro
		int totalRows = getConsultaPolizaSbpCountWithFilter(consultaFilter, nombreAseg, lstLineasSbp);
		logger.debug("********** count filas para Sbp = " + totalRows + " **********");

		// y lo establecemos al tableFacade antes de obtener la fila de inicio y la de
		// fin
		tableFacade.setTotalRows(totalRows);

		// Crea el Sort para la busqueda de polizas
		ConsultaPolizaSbpSort consultaSort = getConsultaPolizasSbpSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();
		Collection<Poliza> items = new ArrayList<Poliza>();
		// Obtiene los registros que cumplen el filtro
		try {
			items = getConsultaPolizasSbpWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd, nombreAseg,
					lstLineasSbp);
			logger.debug("********** list items para Sbp = " + items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);

	}

	/**
	 * Metodo para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	private String html(TableFacade tableFacade, Map<Long, List<BigDecimal>> cultivosPorLinea) {

		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");

		// Configuracion de las columnas de la tabla
		configurarColumnas(table);

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			tableFacade.render(); // Will write the export data out to the response.
			return null; // In Spring return null tells the controller not to do anything.
		} else {
			// Configuracion de los datos de las columnas que requieren un tratamiento para
			// mostrarse
			// campo acciones
			table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer()
					.setCellEditor(getCellEditorAcciones(cultivosPorLinea));
			// Campo colectivo - dc
			table.getRow().getColumn(columnas.get(COLECTIVO)).getCellRenderer().setCellEditor(getCellEditorColectivo());
			// Referencia
			table.getRow().getColumn(columnas.get(REFERENCIA)).getCellRenderer()
					.setCellEditor(getCellEditorReferencia());
			// Campo estado
			table.getRow().getColumn(columnas.get(ESTADO)).getCellRenderer().setCellEditor(getCellEditorEstado());
			// Fecha de envio
			table.getRow().getColumn(columnas.get(FECHA)).getCellRenderer().setCellEditor(getCellEditorFechaEnvio());
			// Nombre Asegurado
			table.getRow().getColumn(columnas.get(NOMBRE)).getCellRenderer()
					.setCellEditor(getCellEditorNombreAsegurado());
			// Nombre Estado Sobreprecio
			table.getRow().getColumn(columnas.get(ESTADOSBP)).getCellRenderer().setCellEditor(getCellEditorEstadoSbp());
		}

		// Devuelve el html de la tabla
		return tableFacade.render();
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones(final Map<Long, List<BigDecimal>> cultivosPorLinea) {
		return new CellEditor() {
			@SuppressWarnings("unchecked")
			public Object getValue(Object item, String property, int rowcount) {
				Long idPolizaLong = (Long) new BasicCellEditor().getValue(item, "idpoliza", rowcount);
				Long idAseg = (Long) new BasicCellEditor().getValue(item, "asegurado.id", rowcount);
				Long idCol = (Long) new BasicCellEditor().getValue(item, "colectivo.id", rowcount);
				BigDecimal clase = (BigDecimal) new BasicCellEditor().getValue(item, CLASE2, rowcount);
				String idPoliza = idPolizaLong.toString();
				BigDecimal estado = ((BigDecimal) new BasicCellEditor().getValue(item, columnas.get(ESTADO), rowcount));
				Set<PolizaSbp> lstPpalSbp = new HashSet<PolizaSbp>();
				Set<PolizaSbp> lstCplSbp = new HashSet<PolizaSbp>();
				lstPpalSbp = (Set<PolizaSbp>) new BasicCellEditor().getValue(item, "polizaPrincipal", rowcount);
				lstCplSbp = (Set<PolizaSbp>) new BasicCellEditor().getValue(item, "polizaComplementaria", rowcount);
				String idPolizaSbp = "";
				BigDecimal estadoSbp = null;
				String codPlanSbp = "";
				String codLineaSbp = "";
				String referenciaSbp = "";
				BigDecimal tipoEnvio = null;
				Long lineaseguroid = (Long) new BasicCellEditor().getValue(item, "linea.lineaseguroid", rowcount);
				String referencia = StringUtils
						.nullToString((String) new BasicCellEditor().getValue(item, REFERENCIA2, rowcount));
				BigDecimal plan = (BigDecimal) new BasicCellEditor().getValue(item, LINEA_CODPLAN, rowcount);
				String tipoRef = StringUtils
						.nullToString((Character) new BasicCellEditor().getValue(item, "tipoReferencia", rowcount));
				String estadoPpal = "";
				if (tipoRef.equals("C")) {
					List<Poliza> lstPolizas = new ArrayList<Poliza>();
					Poliza pol = new Poliza();
					pol.getLinea().setLineaseguroid(lineaseguroid);
					pol.getAsegurado().setId(idAseg);
					pol.getColectivo().setId(idCol);
					pol.setClase(clase);
					lstPolizas = polizaComplementariaManager.getPolizaByTipoRef(pol, 'P');
					if (lstPolizas != null && lstPolizas.size() > 0) {
						estadoPpal = lstPolizas.get(0).getEstadoPoliza().getIdestado().toString();
					}
				}
				if (lstPpalSbp != null) {
					for (PolizaSbp polizaSbp : lstPpalSbp) {
						idPolizaSbp = polizaSbp.getId().toString();
						estadoSbp = polizaSbp.getEstadoPlzSbp().getIdestado();
						codPlanSbp = polizaSbp.getPolizaPpal().getLinea().getCodplan().toString();
						codLineaSbp = polizaSbp.getPolizaPpal().getLinea().getCodlinea().toString();
						referenciaSbp = polizaSbp.getReferencia();
						tipoEnvio = polizaSbp.getTipoEnvio().getId();
						if (!estadoSbp.toString().equals(ConstantsSbp.ESTADO_ANULADA.toString())) {
							break;
						}
					}
				}
				if (lstCplSbp != null) {
					for (PolizaSbp polizaSbp : lstCplSbp) {
						idPolizaSbp = polizaSbp.getId().toString();
						estadoSbp = polizaSbp.getEstadoPlzSbp().getIdestado();
						codPlanSbp = polizaSbp.getPolizaPpal().getLinea().getCodplan().toString();
						codLineaSbp = polizaSbp.getPolizaPpal().getLinea().getCodlinea().toString();
						referenciaSbp = polizaSbp.getReferencia();
						tipoEnvio = polizaSbp.getTipoEnvio().getId();
						if (!estadoSbp.toString().equals(ConstantsSbp.ESTADO_ANULADA.toString())) {
							break;
						}
					}
				}
				HtmlBuilder html = new HtmlBuilder();
				// boton editar

				if (!estado.equals(ConstantsSbp.ESTADO_ANULADA)) {
					if ((lstPpalSbp != null && lstPpalSbp.size() > 0) || (lstCplSbp != null && lstCplSbp.size() > 0)) {
						if (!tipoEnvio.equals(ConstantsSbp.TIPO_ENVIO_SUPLEMENTO)) {
							if (estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_PROV)
									|| estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_DEF)
									|| estadoSbp.equals(ConstantsSbp.ESTADO_ENVIADA_ERRONEA)) {
								html.a().href().quote()
										.append("javascript:editar('" + idPolizaSbp + "','" + codPlanSbp + "','"
												+ codLineaSbp + "'," + "'" + referenciaSbp + "','" + idPoliza + "');")
										.quote().close();
								html.append(
										"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar p&oacute;liza Sbp\" title=\"Editar p&oacute;liza Sbp\"/>");
								html.aEnd();
								html.append(NBSP);
							}
						}
					}
				}
				// boton borrar poliza Sbp
				if (estadoSbp != null) {
					if (!tipoEnvio.equals(ConstantsSbp.TIPO_ENVIO_SUPLEMENTO)) {
						if (estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_PROV)
								|| estadoSbp.equals(ConstantsSbp.ESTADO_GRAB_DEF)) {

							html.a().href().quote().append("javascript:borrar('" + idPolizaSbp + "');").quote().close();
							html.append(
									"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar p&oacute;liza Sbp\" title=\"Borrar p&oacute;liza Sbp\"/>");
							html.aEnd();
							html.append(NBSP);
						}
					}
				}
				// boton ALTA poliza Sbp
				logger.debug("Boton ALTA. estadoSbp=" + estadoSbp);
				if (estadoSbp == null || estadoSbp.equals(ConstantsSbp.ESTADO_ANULADA)
						|| estadoSbp.equals(ConstantsSbp.ESTADO_SIMULACION)) {
					BigDecimal estadoParaAlta = new BigDecimal(0);
					if (tipoRef.equals("C")) {
						if (!estadoPpal.equals(""))
							estadoParaAlta = new BigDecimal(estadoPpal);
					} else {
						estadoParaAlta = new BigDecimal(estado.toString());
					}
					// logger.debug("Boton ALTA. estadoParaAlta="+estadoParaAlta);
					if (estadoParaAlta.equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)
							|| estadoParaAlta.equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)
							|| estadoParaAlta.equals(Constants.ESTADO_POLIZA_DEFINITIVA)
							|| estadoParaAlta.equals(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR)) {
						boolean cumpleLinea = false;

						// comprobamos si la linea de la poliza esta disponible para el sobreprecio
						if (cultivosPorLinea.containsKey(lineaseguroid)) {
							cumpleLinea = true;
						}

						if (cumpleLinea) {
							html.a().href().quote()
									.append("javascript:alta('" + idPoliza + "','" + estado + "','" + tipoRef + "');")
									.quote().close();
							html.append(
									"<span id='imp' style='font-size: 150%;text-decoration:none;font-weight:bold;color: #FF0000' title=\"Alta p&oacute;liza Sbp\"> A</span>");
							html.aEnd();
							html.append("&nbsp;&nbsp;");
						}
					}
				}
				// boton imprimir situaci�n actualizada
				if (referencia != null && ((tipoRef.equals("P") && estado.equals(Constants.ESTADO_POLIZA_DEFINITIVA)
						|| tipoRef.equals("C")))) {
					html.a().href().quote().append(
							"javascript:verSituacionActual('" + referencia + "','" + plan + "','" + tipoRef + "');")
							.quote().close();
					html.append(
							"<img src=\"jsp/img/displaytag/imprimir.png\" alt=\"imprimir situaci&oacute;n actual de la p&oacute;liza\" title=\"imprimir situaci&oacute;n actual de la p&oacute;liza\"/>");
					html.aEnd();
					html.append(NBSP);
				}
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Colectivo'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorColectivo() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				String nifCif, dc;
				nifCif = (String) new BasicCellEditor().getValue(item, columnas.get(COLECTIVO), rowcount);
				dc = (String) new BasicCellEditor().getValue(item, columnas.get(DCCOLECTIVO), rowcount);
				String value = nifCif + "-" + dc;
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Estado'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorEstado() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Obtiene el codigo de estado de la poliza actual
				int estado = 0;
				try {
					estado = ((BigDecimal) new BasicCellEditor().getValue(item, columnas.get(ESTADO), rowcount))
							.intValue();
				} catch (Exception e) {
					logger.error("ConsultaPolizaSbpService - Ocurrio un error al obtener el estado de la poliza", e);
				}
				// Muestra el mensaje correspondiente al estado
				String value = "";
				switch (estado) {
				case 1:
					value = "Pendiente Validaci&oacute;n";
					break;
				case 2:
					value = "Grabaci&oacute;n Provisional";
					break;
				case 3:
					value = "Grabaci&oacute;n Definitiva";
					break;
				case 4:
					value = "Anulada";
					break;
				case 5:
					value = "Enviada Pendiente de Confirmar";
					break;
				case 7:
					value = "Enviada Err&oacute;nea";
					break;
				case 8:
					value = "Enviada Correcta";
					break;

				default:
					break;
				}

				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Fecha de envío'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorFechaEnvio() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Muestra la fecha de envio
				String value = "";

				// Obtiene el codigo de estado de la poliza actual
				try {
					// Si la tiene fecha de envio se formatea y se muestra, si no la tiene nos se
					// muestra nada
					Date dateAux = (Date) new BasicCellEditor().getValue(item, columnas.get(FECHA), rowcount);
					value = (dateAux == null) ? ("") : new SimpleDateFormat(DD_MM_YYYY).format(dateAux);
				} catch (Exception e) {
					logger.error(
							"ConsultaPolizaSbpService - Ocurrio un error al obtener la fecha de envío de la poliza",
							e);
				}

				HtmlBuilder html = new HtmlBuilder();
				html.append(FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Poliza'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorReferencia() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				// Muestra la referencia de la poliza y su dígito de control
				String value = "";
				String referencia = (String) new BasicCellEditor().getValue(item, columnas.get(REFERENCIA), rowcount);
				Object o = new BasicCellEditor().getValue(item, columnas.get(DCREFERENCIA), rowcount);
				String dc = (o == null) ? "" : "-" + ((BigDecimal) o).toString();
				value = referencia == null ? "" : referencia + dc;

				HtmlBuilder html = new HtmlBuilder();
				html.append(FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Asegurado'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorNombreAsegurado() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				// Muestra la referencia de la poliza y su digito de control
				String value = "";

				String asegTipo = (String) new BasicCellEditor().getValue(item, "asegurado.tipoidentificacion",
						rowcount);
				if (asegTipo.equals("CIF")) {
					value = (String) new BasicCellEditor().getValue(item, "asegurado.razonsocial", rowcount);
				} else {
					String asegNombre = (String) new BasicCellEditor().getValue(item, ASEGURADO_NOMBRE, rowcount);
					String asegApe1 = (String) new BasicCellEditor().getValue(item, "asegurado.apellido1", rowcount);
					String asegApe2 = (String) new BasicCellEditor().getValue(item, "asegurado.apellido2", rowcount);
					value = asegNombre + " " + asegApe1 + " " + asegApe2;
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(FiltroUtils.noEstaVacioSinEspacios(value) ? value : NBSP);
				return html.toString();
			}
		};
	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'estado Sbp'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorEstadoSbp() {

		return new CellEditor() {
			@SuppressWarnings("unchecked")
			public Object getValue(Object item, String property, int rowcount) {
				String value = NBSP;
				String Desc = "NBSP";
				Set<PolizaSbp> lstPpalSbp = new HashSet<PolizaSbp>();
				Set<PolizaSbp> lstCplSbp = new HashSet<PolizaSbp>();
				lstPpalSbp = (Set<PolizaSbp>) new BasicCellEditor().getValue(item, "polizaPrincipal", rowcount);
				lstCplSbp = (Set<PolizaSbp>) new BasicCellEditor().getValue(item, "polizaComplementaria", rowcount);
				if (lstPpalSbp.size() > 0) {
					for (PolizaSbp polizaSbp : lstPpalSbp) {
						//value = polizaSbp.getEstadoPlzSbp().getDescEstado();
						if (polizaSbp.getEstadoPlzSbp().getIdestado() != null) {
							switch (polizaSbp.getEstadoPlzSbp().getIdestado().intValue()) {
							case 0: // Simulaci�n
								value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
										+ "\">S</span>";
								break;
							case 1: // Grabada Provisional
								value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
										+ "\">P</span>";
								break;
							case 2:// Grabada Definitiva
								value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
										+ "\">D</span>";
								break;
							case 3: // Enviada Pendiente Aceptacion
								value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
										+ "\">EP</span>";
								break;
							case 4: // Enviada Erronea
								value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
										+ "\">EE</span>";
								break;
							case 5: // Enviada Correcta
								value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
										+ "\">E</span>";
								break;
							case 6: // Anulada
								value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
										+ "\">A</span>";
								break;

							default: // por defecto
								value = NBSP;
								break;
							}
						} else {
							value = NBSP;
						}
						if (!polizaSbp.getEstadoPlzSbp().getIdestado().toString()
								.equals(ConstantsSbp.ESTADO_ANULADA.toString())) {
							
						}
					}
				} else {
					if (lstCplSbp.size() > 0) {
						for (int i = 0; i < lstCplSbp.size(); i++) {
							PolizaSbp polizaSbp = new ArrayList<PolizaSbp>(lstCplSbp).get(i);
							if (polizaSbp.getIncSbpComp().equals('S')) {
								/* Pet. 79014 ** MODIF TAM (18.03.2022) ** Inicio */
								if (polizaSbp.getEstadoPlzSbp().getIdestado() != null) {
									switch (polizaSbp.getEstadoPlzSbp().getIdestado().intValue()) {
									case 0: // Simulaci�n
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">S</span>";
										break;
									case 1: // Grabada Provisional
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">P</span>";
										break;
									case 2:// Grabada Definitiva
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">D</span>";
										break;
									case 3: // Enviada Pendiente Aceptacion
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">EP</span>";
										break;
									case 4: // Enviada Erronea
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">EE</span>";
										break;
									case 5: // Enviada Correcta
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">E</span>";
										break;
									case 6: // Anulada
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">A</span>";
										break;

									default: // por defecto
										value = NBSP;
										break;
									}
								} else {
									value = NBSP;
								}
								/* Pet. 79014 ** MODIF TAM (18.03.2022) ** Fin */
								if (!polizaSbp.getEstadoPlzSbp().getIdestado().toString()
										.equals(ConstantsSbp.ESTADO_ANULADA.toString())) {
									break;
								}
							}else if (polizaSbp.getEstadoPlzSbp().getIdestado().toString().equals(ConstantsSbp.ESTADO_ANULADA.toString())) {
								/* Pet. 79014 ** MODIF TAM (18.03.2022) ** Inicio */
								if (polizaSbp.getEstadoPlzSbp().getIdestado() != null) {
									switch (polizaSbp.getEstadoPlzSbp().getIdestado().intValue()) {
									case 0: // Simulaci�n
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">S</span>";
										break;
									case 1: // Grabada Provisional
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">P</span>";
										break;
									case 2:// Grabada Definitiva
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">D</span>";
										break;
									case 3: // Enviada Pendiente Aceptacion
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">EP</span>";
										break;
									case 4: // Enviada Erronea
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">EE</span>";
										break;
									case 5: // Enviada Correcta
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">E</span>";
										break;
									case 6: // Anulada
										value = "<span id='imp' title=\"" + polizaSbp.getEstadoPlzSbp().getDescEstado()
												+ "\">A</span>";
										break;

									default: // por defecto
										value = NBSP;
										break;
									}
								} else {
									value = NBSP;
								}
								/* Pet. 79014 ** MODIF TAM (18.03.2022) ** Fin */
							}
						}
					}
				}
				// Pet. 79014 ** MODIF TAM (18.03.2022) ** Inicio //
				HtmlBuilder html = new HtmlBuilder();
				html.append(value);
				// Pet. 79014 ** MODIF TAM (18.03.2022) ** Fin //
				return html.toString();
			}
		};
	}

	/**
	 * Configuracion de las columnas de la tabla
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "6%");
		// 1 - Entidad
		configColumna(table, columnas.get(ENTIDAD), "Ent", true, true, "4%");
		// 2 - Ent. Mediadora
		configColumna(table, columnas.get(ENTMEDIADORA), "Ent. Med", true, true, "4%");
		// 3 - SubEnt. Mediadora
		configColumna(table, columnas.get(SUBENTMEDIADORA), "SubEnt. Med", true, true, "4%");
		// 2 - Oficina
		configColumna(table, columnas.get(OFICINA), "Ofi", true, true, "4%");
		// 3 - Usuario
		configColumna(table, columnas.get(USUARIO), "Usuario", true, true, "6%");
		// 4 - Plan
		configColumna(table, columnas.get(PLAN), "Plan", true, true, "4%");
		// 5 - Línea
		configColumna(table, columnas.get(LINEA), "L&iacute;nea", true, true, "3%");
		// 6 - Colectivo
		configColumna(table, columnas.get(COLECTIVO), "Colectivo", true, true, "8%");
		// 7 - Poliza
		configColumna(table, columnas.get(REFERENCIA), "P&oacute;liza", true, true, "8%");
		// 8 - Clase
		configColumna(table, columnas.get(CLASE), "CLS", true, true, "3%");
		// 9 - Modulo
		configColumna(table, columnas.get(MODULO), "M&oacute;d", true, true, "3%");
		// 10 - NIF/CIF
		configColumna(table, columnas.get(NIF), "NIF/CIF", true, true, "8%");
		// 11 - Asegurado
		configColumna(table, columnas.get(NOMBRE), "Asegurado", true, true, "18%");
		// 12 - Estado
		configColumna(table, columnas.get(ESTADO), "Estado", true, true, "8%");
		// 13 - Fecha de envio
		configColumnaFecha(table, columnas.get(FECHA), "Fec.Env", true, true, "8%", DD_MM_YYYY);
		// 14 - Estado Poliza Sobreprecio
		configColumna(table, columnas.get(ESTADOSBP), "Sobreprecio", true, false, "4%");
	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parametro y los incluye en la tabla
	 * 
	 * @param table
	 *            Objeto que contiene la tabla
	 * @param idCol
	 *            Id de la columna
	 * @param title
	 *            Título de la columna
	 * @param filterable
	 *            Indica si se podra buscar por esa columna
	 * @param sortable
	 *            Indica si se podra ordenar por esa columna
	 * @param width
	 *            Ancho de la columna
	 */
	private void configColumna(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parametro y los incluye en la tabla
	 * 
	 * @param table
	 *            Objeto que contiene la tabla
	 * @param idCol
	 *            Id de la columna
	 * @param title
	 *            Título de la columna
	 * @param filterable
	 *            Indica si se podra buscar por esa columna
	 * @param sortable
	 *            Indica si se podra ordenar por esa columna
	 * @param width
	 *            Ancho de la columna
	 * @param fFecha
	 *            Formato de fecha con la que se mostraran los datos de esta columna
	 */
	private void configColumnaFecha(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width, String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// A�ade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor(DD_MM_YYYY));
		} catch (Exception e) {
			logger.error("Ocurrio un error al configurar el formato de fecha de la columna " + idCol, e);
		}
	}

	/**
	 * Crea y configura el Filter para la consulta de polizas
	 * 
	 * @param limit
	 * @return
	 */
	private ConsultaPolizaSbpFilter getConsultaPolizaSbpFilter(Limit limit, List<BigDecimal> listaGrupoEntidades,
			List<BigDecimal> listaGrupoOficina) {
		ConsultaPolizaSbpFilter consultaFilter = new ConsultaPolizaSbpFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			consultaFilter.addFilter(property, value);
		}

		// Si la lista de grupos de entidades no está vacía se incluye en el filtro de
		// búsqueda
		if (listaGrupoEntidades != null && listaGrupoEntidades.size() > 0) {
			consultaFilter.addFilter("listaGrupoEntidades", listaGrupoEntidades);
		}
		// Si la lista de grupos de oficinas no está vacía se incluye en el filtro de
		// búsqueda
		if (listaGrupoOficina != null && listaGrupoOficina.size() > 0) {
			consultaFilter.addFilter("listaGrupoOficina", CriteriaUtils.getCodigosListaOficina(listaGrupoOficina));
		}

		return consultaFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de polizas
	 * 
	 * @param limit
	 * @return
	 */
	private ConsultaPolizaSbpSort getConsultaPolizasSbpSort(Limit limit) {
		ConsultaPolizaSbpSort consultaSort = new ConsultaPolizaSbpSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}

		return consultaSort;
	}

	public void setConsultaSbpDao(IConsultaSbpDao consultaSbpDao) {
		this.consultaSbpDao = consultaSbpDao;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPolizaComplementariaManager(PolizaComplementariaManager polizaComplementariaManager) {
		this.polizaComplementariaManager = polizaComplementariaManager;
	}

	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}

}
