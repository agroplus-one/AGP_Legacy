package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IMtoDescuentosDao;
import com.rsi.agp.core.jmesa.filter.MtoDescuentosFilter;
import com.rsi.agp.core.jmesa.service.IMtoDescuentosService;
import com.rsi.agp.core.jmesa.service.IPagoManualService;
import com.rsi.agp.core.jmesa.sort.MtoDescuentosSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbarMarcarTodos;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;
import com.rsi.agp.dao.models.admin.ISubentidadMediadoraDao;
import com.rsi.agp.dao.models.commons.IEntidadDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.OficinaId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("deprecation")
public class MtoDescuentosService implements IMtoDescuentosService {

	// Constantes
	private static final String NBSP = "&nbsp;";
	private static final String ALERTA = "alerta";
	private static final String LINEA_CODLINEA = "linea.codlinea";
	private static final String LINEA_CODPLAN = "linea.codplan";
	private static final String VER_COMISIONES2 = "verComisiones";
	private static final String PERMITIR_RECARGO = "permitirRecargo";
	private static final String PCT_DESC_MAX = "pctDescMax";
	private static final String DELEGACION2 = "delegacion";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD = "subentidadMediadora.id.codsubentidad";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODENTIDAD = "subentidadMediadora.id.codentidad";
	private static final String OFICINA_ID_CODOFICINA = "oficina.id.codoficina";
	private static final String SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD = "subentidadMediadora.entidad.codentidad";
	
	private IMtoDescuentosDao mtoDescuentosDao;
	private ISubentidadMediadoraDao subentidadMediadoraDao;
	private IEntidadDao entidadDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private Log logger = LogFactory.getLog(getClass());
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String idDto;
	private ILineaDao lineaDao;
	private IPagoManualService pagoManualService;

	// Constantes para los nombres de las columnas del listado
	private final static String ID = "ID";
	private final static String ENTIDAD = "ENTIDAD";
	private final static String NOMBRE_ENTIDAD = "NOMBRE ENTIDAD";
	private final static String OFICINA = "OFICINA";
	private final static String NOMBRE_OFICINA = "NOMBRE OFICINA";
	private final static String ESMEDIADORA = "ESMEDIADORA";
	private final static String SUBENTMEDIADORA = "SUBENTMEDIADORA";
	private final static String DELEGACION = "DELEGACION";
	private final static String PCTDESCMAX = "PCTDESCMAX";
	private final static String FECHABAJA = "FECHABAJA";
	public static final String CAMPO_LISTADOGRUPOENT = "listaGrupoEntidades";
	public static final String PLAN = "PLAN";
	public static final String LINEA = "LINEA";
	public static final String RECARGO = "RECARGO";
	public static final String VER_COMISIONES = "VER COMISIONES";

	@Override
	public Collection<Descuentos> getDescuentosWithFilterAndSort(
			MtoDescuentosFilter filter, MtoDescuentosSort sort, int rowStart,
			int rowEnd) throws BusinessException {

		return mtoDescuentosDao.getDescuentosWithFilterAndSort(filter, sort,
				rowStart, rowEnd);

	}

	@Override
	public int getDescuentosCountWithFilter(MtoDescuentosFilter filter)
			throws BusinessException {

		return mtoDescuentosDao.getDescuentosCountWithFilter(filter);
	}

	@Override
	public String getTablaDescuentos(HttpServletRequest request,
			HttpServletResponse response, Descuentos descuentosBean,
			String origenLlamada, List<BigDecimal> listaGrupoEntidades) {

		TableFacade tableFacade = crearTableFacade(request, response,
				descuentosBean, origenLlamada);

		Limit limit = tableFacade.getLimit();
		MtoDescuentosFilter consultaFilter = getConsultaDescuentosFilter(limit,
				listaGrupoEntidades);

		setDataAndLimitVariables(tableFacade, listaGrupoEntidades);

		String listaIdsTodos = getlistaIdsTodos(consultaFilter);
		String script = "<script>$(\"#listaIdsTodos\").val(\"" + listaIdsTodos + "\");</script>";
		//tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setToolbar(new CustomToolbarMarcarTodos());
		tableFacade.setView(new CustomView());
		

		return html (tableFacade) + script;

		
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, Descuentos descuentos,
			String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(idDto,
				request);

		// Carga las columnas a mostrar en el listado en el TableFacade y
		// devuelve un Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade);

		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringLikeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class),
				new DateFromFilterMatcher("d/M/yyyy"));
		tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class),
				new BigDecimalFilterMatcher());
		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"consultaDescuentos_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("consultaDescuentos_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(columnas, descuentos, tableFacade);
			}
		}

		return tableFacade;
	}

	/**
	 * U029769
	 * 
	 * @param tableFacade
	 * @return
	 */
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de
		// busqueda si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID, "id");
			columnas.put(ENTIDAD, SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD);
			columnas.put(NOMBRE_ENTIDAD,
					"subentidadMediadora.entidad.nomentidad");
			columnas.put(OFICINA, OFICINA_ID_CODOFICINA);
			columnas.put(NOMBRE_OFICINA, "oficina.nomoficina");
			columnas.put(ESMEDIADORA, SUBENTIDAD_MEDIADORA_ID_CODENTIDAD);
			columnas.put(SUBENTMEDIADORA,
					SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD);
			columnas.put(DELEGACION, DELEGACION2);
			columnas.put(PLAN, LINEA_CODPLAN);
			columnas.put(LINEA, LINEA_CODLINEA);
			columnas.put(PCTDESCMAX, PCT_DESC_MAX);
			columnas.put(RECARGO, PERMITIR_RECARGO);
			columnas.put(VER_COMISIONES, VER_COMISIONES2);
			columnas.put(FECHABAJA, "fechaBaja");

		}
		tableFacade.setColumnProperties(columnas.get(ID),
				columnas.get(ENTIDAD), columnas.get(NOMBRE_ENTIDAD),
				columnas.get(OFICINA), columnas.get(NOMBRE_OFICINA),
				columnas.get(ESMEDIADORA), columnas.get(DELEGACION),
				columnas.get(PLAN), columnas.get(LINEA),
				columnas.get(PCTDESCMAX), columnas.get(RECARGO),
				columnas.get(VER_COMISIONES), columnas.get(FECHABAJA));

		return columnas;
	}

	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el
	 * formulario 06/05/2014 U029769
	 * 
	 * @param columnas2
	 * @param usuario
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String, String> columnas2,
			Descuentos descuentos, TableFacade tableFacade) {

		// ENTIDAD
		if (descuentos.getSubentidadMediadora().getEntidad().getCodentidad() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(ENTIDAD), descuentos
									.getSubentidadMediadora().getEntidad()
									.getCodentidad().toString()));
		// OFICINA
		if (descuentos.getOficina().getId().getCodoficina() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(OFICINA), descuentos
									.getOficina().getId().getCodoficina()
									.toString()));
		// ESMEDIADORA
		if (descuentos.getSubentidadMediadora().getId().getCodentidad() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(ESMEDIADORA), descuentos
									.getSubentidadMediadora().getId()
									.getCodentidad().toString()));
		// SUBENTIDADMEDIADORA
		if (descuentos.getSubentidadMediadora().getId().getCodsubentidad() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(SUBENTMEDIADORA),
									descuentos.getSubentidadMediadora().getId()
											.getCodsubentidad().toString()));
		// DELEGACION
		if (descuentos.getDelegacion() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(DELEGACION), descuentos
									.getDelegacion().toString()));
		// RECARGO
		if (descuentos.getPermitirRecargo() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(RECARGO), descuentos
									.getPermitirRecargo().toString()));
		// VER COMISIONES
		if (descuentos.getVerComisiones() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(VER_COMISIONES), descuentos
									.getVerComisiones().toString()));
		// PctDescMax
		if (descuentos.getPctDescMax() != null)
			tableFacade
					.getLimit()
					.getFilterSet()
					.addFilter(
							new Filter(columnas.get(PCTDESCMAX), descuentos
									.getPctDescMax().toString()));
		//LINEA
		if(null!=descuentos.getLinea()) {
			if (null!= descuentos.getLinea().getCodlinea()) {
				tableFacade
				.getLimit()
				.getFilterSet()
				.addFilter(
						new Filter(columnas.get(LINEA), descuentos
								.getLinea().getCodlinea().toString()));
			}
			if (null!= descuentos.getLinea().getCodplan()) {
				tableFacade
				.getLimit()
				.getFilterSet()
				.addFilter(
						new Filter(columnas.get("PLAN"), descuentos
								.getLinea().getCodplan().toString()));
			}
			
		}
	}

	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los
	 * datos de descuentos y carga el TableFacade con ellas U029769
	 * 
	 * @param tableFacade
	 * @param consultaFilter
	 * @param limit
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade,
			List<BigDecimal> listaGrupoEntidades) {

		Collection<Descuentos> items = new ArrayList<Descuentos>();
		// Obtiene el Filter para la busqueda de polizas
		Limit limit = tableFacade.getLimit();
		MtoDescuentosFilter consultaFilter = getConsultaDescuentosFilter(limit,
				listaGrupoEntidades);

		try {
			int totalRows = getDescuentosCountWithFilter(consultaFilter);
			logger.debug("********** count filas para Descuentos  = "
					+ totalRows + " **********");

			tableFacade.setTotalRows(totalRows);

			MtoDescuentosSort consultaSort = getConsultaDescuentosSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getDescuentosWithFilterAndSort(consultaFilter,
					consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para Descuentos  = "
					+ items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);

	}

	/**
	 * Crea y configura el Filter para la consulta de usuarios U029769
	 * 
	 * @param limit
	 * @return
	 */
	private MtoDescuentosFilter getConsultaDescuentosFilter(Limit limit,
			List<BigDecimal> listaGrupoEntidades) {
		MtoDescuentosFilter consultaFilter = new MtoDescuentosFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			consultaFilter.addFilter(property, value);
		}
		// Si la lista de grupos de entidades no esta vacia se incluye en el
		// filtro de busqueda
		if (listaGrupoEntidades != null && listaGrupoEntidades.size() > 0) {
			consultaFilter
					.addFilter(CAMPO_LISTADOGRUPOENT, listaGrupoEntidades);
		}
		
		return consultaFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de usuarios U029769
	 * 
	 * @param limit
	 * @return
	 */
	private MtoDescuentosSort getConsultaDescuentosSort(Limit limit) {
		MtoDescuentosSort consultaSort = new MtoDescuentosSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			consultaSort.addSort(property, order);
		}

		return consultaSort;
	}

	/**
	 * Metodo para construir el html de la tabla a mostrar U029769
	 * 
	 * @param tableFacade
	 * @return
	 */
	private String html(TableFacade tableFacade) {

		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");

		configurarColumnas(table);

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			tableFacade.render(); // Will write the export data out to the
									// response.
			return null; // In Spring return null tells the controller not to do
							// anything.
		} else {
			// Configuracion de los datos de las columnas que requieren un
			// tratamiento para mostrarse
			// campo acciones
			table.getRow().getColumn(columnas.get(ID)).getCellRenderer().setCellEditor(getCellEditorAcciones());
			table.getRow().getColumn(columnas.get(OFICINA)).getCellRenderer().setCellEditor(getCellEditorOficina());
			table.getRow().getColumn(columnas.get(NOMBRE_OFICINA)).getCellRenderer().setCellEditor(getCellEditorNombreOficina());
			table.getRow().getColumn(columnas.get(ESMEDIADORA)).getCellRenderer().setCellEditor(getCellEditorESMed());
			table.getRow().getColumn(columnas.get(PCTDESCMAX)).getCellRenderer().setCellEditor(getCellEditorPctDescMax());
			table.getRow().getColumn(columnas.get(DELEGACION)).getCellRenderer().setCellEditor(getCellEditorDelegacion());
			table.getRow().getColumn(columnas.get(RECARGO)).getCellRenderer().setCellEditor(getCellEditorRecargo());
			table.getRow().getColumn(columnas.get(VER_COMISIONES)).getCellRenderer().setCellEditor(getCellEditorVerComisiones());

		}

		return tableFacade.render();
	}

	/**
	 * Devuelve el objeto que muestra la informaci�n de la columna 'Recargo'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorRecargo() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Obtiene si se permite el recargo en el descuento o no
				int recargo = 0;
				try {
					recargo = ((Integer) new BasicCellEditor().getValue(item,
							columnas.get(RECARGO), rowcount)).intValue();
				} catch (Exception e) {
					logger.error(
							"MtoDescuentosService - Ocurri� un error al obtener la columna Permitir Recargo del descuento",
							e);
				}
				// Muestra el mensaje correspondiente al Recargo
				String value = "";
				switch (recargo) {
				case Constants.PERMITIR_RECARGO_NO:
					value = "No";
					break;
				case Constants.PERMITIR_RECARGO_SI:
					value = "Si";
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
	 * Devuelve el objeto que muestra la informaci�n de la columna 'Recargo'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorVerComisiones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				// Obtiene si se permite el recargo en el descuento o no
				int verComis = 0;
				try {
					verComis = ((Integer) new BasicCellEditor().getValue(item,
							columnas.get(VER_COMISIONES), rowcount)).intValue();
				} catch (Exception e) {
					logger.error(
							"MtoDescuentosService - Ocurri� un error al obtener la columna Ver Comisiones del descuento",
							e);
				}
				// Muestra el mensaje correspondiente al Recargo
				String value = "";
				switch (verComis) {
				case Constants.VER_COMISIONES_NO:
					value = "Ninguna";
					break;
				case Constants.VER_COMISIONES_ENTIDAD:
					value = "Entidad";
					break;
				case Constants.VER_COMISIONES_ENTIDAD_MEDIADORA:
					value = "E-S Med.";
					break;
				case Constants.VER_COMISIONES_TODAS:
					value = "Todas";
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

	private CellEditor getCellEditorAcciones() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();

				Long id = (Long) new BasicCellEditor().getValue(item, "id",
						rowcount);
				BigDecimal codentidad = (BigDecimal) new BasicCellEditor()
						.getValue(item,
								SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD,
								rowcount);
				String nomEntidad = (String) new BasicCellEditor().getValue(
						item, "subentidadMediadora.entidad.nomentidad",
						rowcount);
				BigDecimal codoficina = (BigDecimal) new BasicCellEditor()
						.getValue(item, OFICINA_ID_CODOFICINA, rowcount);
				String nomOfi = (String) new BasicCellEditor().getValue(item,
						"oficina.nomoficina", rowcount);
				BigDecimal entMedia = (BigDecimal) new BasicCellEditor()
						.getValue(item, SUBENTIDAD_MEDIADORA_ID_CODENTIDAD,
								rowcount);
				BigDecimal subEntMedia = (BigDecimal) new BasicCellEditor()
						.getValue(item, SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD,
								rowcount);
				BigDecimal delegacion = (BigDecimal) new BasicCellEditor()
						.getValue(item, DELEGACION2, rowcount);
				BigDecimal pctDescMax = (BigDecimal) new BasicCellEditor()
						.getValue(item, PCT_DESC_MAX, rowcount);
				Date fechaBaja = (Date) new BasicCellEditor().getValue(item,
						"fechaBaja", rowcount);
				BigDecimal codplan = (BigDecimal) new BasicCellEditor()
						.getValue(item, LINEA_CODPLAN, rowcount);
				BigDecimal codlinea = (BigDecimal) new BasicCellEditor()
						.getValue(item, LINEA_CODLINEA, rowcount);
				Integer permitirRecargo = (Integer) new BasicCellEditor()
						.getValue(item, PERMITIR_RECARGO, rowcount);
				Integer verComisiones = (Integer) new BasicCellEditor()
						.getValue(item, VER_COMISIONES2, rowcount);
				String nomlinea = (String) new BasicCellEditor().getValue(item,
						"linea.nomlinea", rowcount);
				
				if (fechaBaja == null) {
					html.append("<input type=\"checkbox\" id=\"check_" + id + "\"  name=\"check_" + id + "\" onClick =\"listaCheckId(\'" + id + "')\" class=\"dato\"/>");
					html.append(NBSP);
				}
				// boton consultar
				StringBuilder funcion = new StringBuilder();
				funcion.append(id)
						.append(",")
						.append(codentidad != null ? codentidad : "''")
						.append(",")
						.append(codoficina != null ? codoficina : "''")
						.append(",")
						.append(entMedia != null ? entMedia : "''")
						.append(",")
						.append(subEntMedia != null ? subEntMedia : "''")
						.append(",")
						.append(delegacion != null ? delegacion : "''")
						.append(",'" + StringUtils.nullToString(nomEntidad)
								+ "'")
						.append(",'" + StringUtils.nullToString(nomOfi) + "'")
						.append(",")
						.append(pctDescMax != null ? pctDescMax.setScale(2,
								BigDecimal.ROUND_DOWN) : "''")
						// .append(");")
						.append(",")
						.append(codplan != null ? codplan : "''")
						.append(",")
						.append(codlinea != null ? codlinea : "''")
						.append(",")
						.append(permitirRecargo != null ? permitirRecargo
								: "''")
						.append(",")
						.append(verComisiones != null ? verComisiones : "''")
						.append(",'" + StringUtils.nullToString(nomlinea) + "'")
						.append(")");

				// consultar
				StringBuilder funcionSube = new StringBuilder();
				funcionSube.append("javascript:consultarHistorico( ").append(
						funcion);
				html.a().href().quote().append(funcionSube).quote().close();
				html.append("<img src=\"jsp/img/magnifier.png\" alt=\"Consultar Hist�rico\" title=\"Consultar Hist�rico\"/>");
				html.aEnd();
				html.append(NBSP);

				// boton editar
				if (fechaBaja == null) {
					StringBuilder funcionEdita = new StringBuilder();
					funcionEdita.append("javascript:modificar( ").append(
							funcion);
					html.a().href().quote().append(funcionEdita).quote()
							.close();
					html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Descuento\" title=\"Editar Descuento\"/>");
					html.aEnd();
					html.append(NBSP);

					// boton borrar
					StringBuilder funcionBorra = new StringBuilder();
					funcionBorra.append("javascript:borrar( ").append(funcion);
					html.a().href().quote().append(funcionBorra).quote()
							.close();
					html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Descuento\" title=\"Borrar Descuento\"/>");
					html.aEnd();
					html.append(NBSP);
				}

				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorESMed() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal entMedia = (BigDecimal) new BasicCellEditor()
						.getValue(item, SUBENTIDAD_MEDIADORA_ID_CODENTIDAD,
								rowcount);
				BigDecimal subEntMedia = (BigDecimal) new BasicCellEditor()
						.getValue(item, SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD,
								rowcount);

				String esMed;
				esMed = entMedia + "-" + subEntMedia;
				HtmlBuilder html = new HtmlBuilder();
				html.append(esMed);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorOficina() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal oficina = (BigDecimal) new BasicCellEditor()
						.getValue(item, OFICINA_ID_CODOFICINA, rowcount);
				String codOficina;
				if (StringUtils.nullToString(oficina).equals("")
						|| oficina.intValue()== Constants.SIN_OFICINA.intValue()) {
					codOficina = NBSP;
				} else {
					codOficina = oficina.toString();

					if (codOficina.length() < 4) {
						while (codOficina.length() < 4) {
							codOficina = "0" + codOficina;
						}
					}
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(codOficina);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorNombreOficina() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal oficina = (BigDecimal) new BasicCellEditor()
						.getValue(item, OFICINA_ID_CODOFICINA, rowcount);

				String nombreOficina = (String) new BasicCellEditor().getValue(
						item, "oficina.nomoficina", rowcount);
				if (StringUtils.nullToString(oficina).equals("")
						|| oficina.intValue() == Constants.SIN_OFICINA.intValue()) {
					nombreOficina = Constants.SIN_OFICINA_NOMBRE;
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(nombreOficina);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorDelegacion() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal delegacion = (BigDecimal) new BasicCellEditor()
						.getValue(item, DELEGACION2, rowcount);

				String dele = delegacion != null ? delegacion.toString()
						: "Todas";

				HtmlBuilder html = new HtmlBuilder();
				html.append(dele);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorPctDescMax() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal pctDescMax = (BigDecimal) new BasicCellEditor()
						.getValue(item, PCT_DESC_MAX, rowcount);

				String pct = pctDescMax.setScale(2, BigDecimal.ROUND_DOWN)
						+ "%";
				HtmlBuilder html = new HtmlBuilder();
				html.append(pct);
				return html.toString();
			}
		};
	}

	/**
	 * Configuracion de las columnas de la tabla 08/05/2014 U029769
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		configColumna(table, columnas.get(ID), "&nbsp;&nbsp;Acciones", false,false, "10%");
		configColumna(table, columnas.get(ENTIDAD), "Entidad", true, true, "4%");
		configColumna(table, columnas.get(NOMBRE_ENTIDAD), "Nombre Entidad",true, true, "20%");
		configColumna(table, columnas.get(OFICINA), "Oficina", true, true, "4%");
		configColumna(table, columnas.get(NOMBRE_OFICINA), "Nombre Oficina",true, true, "15%");
		configColumna(table, columnas.get(ESMEDIADORA), "E-S Med.", true, true,"7%");
		configColumna(table, columnas.get(DELEGACION), "Delegaci\u00F3n", true,true, "5%");
		configColumna(table, columnas.get(PLAN), "Plan", true, true, "4%");
		configColumna(table, columnas.get(LINEA), "L\u00EDnea", true, true, "3%");
		configColumna(table, columnas.get(PCTDESCMAX), "%Dto. M\u00E1x.", true,true, "9%");
		configColumna(table, columnas.get(RECARGO), "Recargo", true, true, "3%");
		configColumna(table, columnas.get(VER_COMISIONES), "Ver comis.", true,true, "8%");
		configColumnaFecha(table, columnas.get(FECHABAJA), "Fecha Baja", true, true, "8%", "dd/MM/yyyy");

	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parametro y los incluye en la tabla 08/05/2014 U029769
	 * 
	 * @param table
	 * @param idCol
	 * @param title
	 * @param filterable
	 * @param sortable
	 * @param width
	 */
	private void configColumna(HtmlTable table, String idCol, String title,
			boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);

	}

	private void configColumnaFecha(HtmlTable table, String idCol,
			String title, boolean filterable, boolean sortable, String width,
			String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Añade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer()
					.setCellEditor(new DateCellEditor(fFecha));
		} catch (Exception e) {
			logger.error(
					"Ocurrio un error al configurar el formato de fecha de la columna "
							+ idCol, e);
		}
	}

	@Override
	public Map<String, Object> validaAltaModificacion(Descuentos d)
			throws Exception {

		Map<String, Object> errores = new HashMap<String, Object>();
		try {
			// comprobamos si ya existe el registro en descuentos
			BigDecimal codOficina= d.getOficina().getId().getCodoficina();
			if (null==codOficina) {
				codOficina=Constants.SIN_OFICINA;
			}
			
			if (mtoDescuentosDao.existeRegistro(d.getSubentidadMediadora().getEntidad().getCodentidad(), 
					d.getSubentidadMediadora().getId().getCodentidad(), 
					d.getSubentidadMediadora().getId().getCodsubentidad(), 
					codOficina, d.getDelegacion(), d.getId(),
					d.getLinea().getCodplan(), d.getLinea().getCodlinea())) {

				errores.put(ALERTA, bundle
						.getString("mensaje.mtoDescuentos.existeRegistro"));
				// validamos que existe un registro para la entidad, ent med y
				// subEnt med en la tabla subentidadesMediadoras
			} else if (subentidadMediadoraDao.existeRegistro(
					d.getSubentidadMediadora(), true, d
							.getSubentidadMediadora().getEntidad()
							.getCodentidad()) == 0) {
				errores.put(
						ALERTA,
						bundle.getString("mensaje.mtoDescuentos.subentidadMediadora.KO"));
				// validamos si existe un registro para la entidad/oficinas en
				// la tabla oficinas

			} else if (null == d.getOficina().getId().getCodoficina()
					&& !entidadDao.existeEntidad(d.getSubentidadMediadora()
							.getEntidad().getCodentidad())) {// Validamos solo
																// la entidad
				errores.put(ALERTA,
						bundle.getString("mensaje.mtoDescuentos.entidad.KO"));
			} else if (null != d.getOficina().getId().getCodoficina()
					&& !entidadDao.existeEntidadOficina(d
							.getSubentidadMediadora().getEntidad()
							.getCodentidad(), d.getOficina().getId()
							.getCodoficina())) {// Validamos Entidad - Oficina
				errores.put(ALERTA,
						bundle.getString("mensaje.mtoDescuentos.oficina.KO"));
			} else if (!lineaDao.existeLinea(d.getLinea().getCodplan(), d
					.getLinea().getCodlinea())) {
				errores.put(ALERTA,
						bundle.getString("mensaje.mtoDescuentos.Linea.KO"));
			}

		} catch (Exception e) {
			logger.error("Ocurrio un error al validar el registro " + e);
			throw e;
		}
		return errores;
	}

	@Override
	public void guardaRegistro(Descuentos descuentosBean) throws Exception {
		BigDecimal codEntidad;
		BigDecimal codOficina;
		String nombreOficina;
		Oficina oficina;
		try {
			codEntidad=descuentosBean.getSubentidadMediadora().getEntidad().getCodentidad();
			oficina=descuentosBean.getOficina();
			
			descuentosBean.getOficina().getEntidad().setCodentidad(codEntidad);
			descuentosBean.getOficina().getId().setCodentidad(codEntidad);
								
			Linea nuevaLin = lineaDao.getLinea(descuentosBean.getLinea()
					.getCodlinea(), descuentosBean.getLinea().getCodplan());
			descuentosBean.setLinea(nuevaLin);
			
			if (null==oficina.getId().getCodoficina() || 
				oficina.getId().getCodoficina().intValue()==Constants.SIN_OFICINA.intValue() ) {
				codOficina=Constants.SIN_OFICINA;
				nombreOficina=Constants.SIN_OFICINA_NOMBRE;
			}else {
				codOficina=descuentosBean.getOficina().getId().getCodoficina();
				nombreOficina=descuentosBean.getOficina().getNomoficina();
			}
			
			Oficina nuevaOficina = entidadDao.getOficina(codEntidad, codOficina);
					
			if(null==nuevaOficina) {
				//descuentosBean.getOficina().setNomoficina(nombreOficina);
				//descuentosBean.getOficina().getId().setCodoficina(codOficina);
				//descuentosBean.getOficina().getId().setCodentidad(codEntidad);
				
				oficina = altaOficina(descuentosBean, codEntidad, codOficina, nombreOficina);
				descuentosBean.setOficina(oficina);
			}else {
				descuentosBean.setOficina(nuevaOficina);
			}
				

			
			/*if ((null != descuentosBean.getOficina().getId().getCodoficina() && 
					(null == descuentosBean.getOficina().getNomoficina() || 
					!descuentosBean.getOficina().getNomoficina().trim().equals("")))|| 
					(null == descuentosBean.getOficina().getId().getCodoficina())) {
				
				Oficina nuevaOficina = entidadDao.getOficina(descuentosBean
						.getOficina().getId().getCodentidad(), descuentosBean
						.getOficina().getId().getCodoficina());
				if (null != nuevaOficina) {
					descuentosBean.getOficina().setNomoficina(
							nuevaOficina.getNomoficina());
				} else {
					Oficina oficina;
					oficina = altaOficina(descuentosBean);
					descuentosBean.setOficina(oficina);
				}
			}*/

			/*
			 * if(null==descuentosBean.getOficina().getId().getCodoficina()) {
			 * Oficina oficina; oficina=altaOficina(descuentosBean);
			 * descuentosBean.setOficina(oficina); }
			 */

			mtoDescuentosDao.saveOrUpdate(descuentosBean);

		} catch (Exception e) {
			logger.error("Ocurrio un error al dar de alta el registro " + e);
			throw e;
		}
	}

	private Oficina altaOficina(Descuentos descuentosBean, BigDecimal codEntidad,
			BigDecimal codOficina, String nombreOficina) throws Exception {
		Oficina oficinaBean;
		try {
			// Damos de alta la oficina en la entidad
			oficinaBean = new Oficina();
			oficinaBean.setEntidad(descuentosBean.getOficina().getEntidad());
			oficinaBean.setNomoficina(nombreOficina);
			oficinaBean.setPagoManual(new BigDecimal(0));
			OficinaId oficinaId = new OficinaId();
			oficinaId.setCodentidad(codEntidad);
			oficinaId.setCodoficina(codOficina);
			oficinaBean.setId(oficinaId);
			/* Pet. 63701 ** MODIF TAM (24.06.2021) */
			/* Le pasamos el valor null en la lista de zonas*/
			pagoManualService.altaOficina(oficinaBean, null);

		} catch (Exception e) {
			logger.error("Ocurrio un error al dar de alta la oficina " + e);
			throw e;
		}
		return oficinaBean;
	}

	/*
	 * private Oficina getOficina(BigDecimal codentidad, BigDecimal
	 * codoficina)throws DAOException { Oficina result = null; OficinaFiltro
	 * filtro = new OficinaFiltro(codentidad, codoficina); Session session =
	 * obtenerSession(); return filtro.getCriteria(session).list();
	 * 
	 * List<Linea> idLineaList = this.getObjects(filtro);
	 * 
	 * try{
	 * 
	 * if(idLineaList.size() > 0){ result = idLineaList.get(0); }else{ result =
	 * null; } }catch(Exception ex){
	 * logger.info("Se ha producido un error al recuperar la linea: " +
	 * ex.getMessage()); throw new
	 * DAOException("Se ha producido un error al recuperar la linea", ex); }
	 * 
	 * return result;
	 * 
	 * }
	 */

	@Override
	public void borraRegistro(Descuentos descuentosBean) throws Exception {
		try {
			descuentosBean
					.getOficina()
					.getEntidad()
					.setCodentidad(
							descuentosBean.getSubentidadMediadora()
									.getEntidad().getCodentidad());
			descuentosBean
					.getOficina()
					.getId()
					.setCodentidad(
							descuentosBean.getSubentidadMediadora()
									.getEntidad().getCodentidad());
			descuentosBean.setFechaBaja(new Date());
			Linea nuevaLin = lineaDao.getLinea(descuentosBean.getLinea()
					.getCodlinea(), descuentosBean.getLinea().getCodplan());
			descuentosBean.setLinea(nuevaLin);
			mtoDescuentosDao.saveOrUpdate(descuentosBean);

		} catch (Exception e) {
			logger.error("Ocurrio un error al hacer borrado logico del registro "
					+ e);
			throw e;
		}
	}

	@Override
	public void guardaHistorico(Descuentos db, BigDecimal operacion,
			String usuario) throws Exception {
		try {
			DescuentosHistorico dh = new DescuentosHistorico();
			// db.getOficina().getEntidad().setCodentidad(db.getSubentidadMediadora().getEntidad().getCodentidad());
			// db.getOficina().getId().setCodentidad(db.getSubentidadMediadora().getEntidad().getCodentidad());

			dh.getEntidad().setCodentidad(
					db.getSubentidadMediadora().getEntidad().getCodentidad());
			dh.setCodentmed(db.getSubentidadMediadora().getId().getCodentidad());
			dh.setCodsubentmed(db.getSubentidadMediadora().getId()
					.getCodsubentidad());
			// dh.getOficina().getId().setCodoficina(db.getOficina().getId().getCodoficina());
			// dh.getOficina().getId().setCodentidad(db.getSubentidadMediadora().getEntidad().getCodentidad());
			dh.setOficina(db.getOficina());
			dh.setDelegacion(db.getDelegacion());
			dh.setDescuentos(db);
			dh.setOperacion(operacion);
			dh.setPctDescMax(db.getPctDescMax());
			dh.setUsuario(usuario);
			dh.setFecha(new Date());
			dh.setPermitirRecargo(db.getPermitirRecargo());
			dh.setVerComisiones(db.getVerComisiones());

			mtoDescuentosDao.saveOrUpdate(dh);
		} catch (Exception e) {
			logger.error("Ocurrio un error al consultar el historico del registro "
					+ e);
			throw e;
		}
	}
	
	@Override
	public Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, 
			BigDecimal lineaDest, String codUsuario, BigDecimal entidadReplica) throws BusinessException {

		Map<String, Object> parameters = new HashMap<String, Object>();
		
		// Obtiene el lineaseguroid de los plan/linea origen y destino
		try {
			// Validaci�n del plan/linea origen
			Long lineaSeguroIdOrigen = lineaDao.getLineaSeguroId(lineaOrig, planOrig);
			if (lineaSeguroIdOrigen == null) {
				logger.debug("El plan/l�nea origen no existe, no se continua con la r�plica");
				parameters.put(ALERTA, bundle.getString("mensaje.mtoDescuentos.replica.planlinea.origen.KO"));
				return parameters;
			}
			
			// Validaci�n del plan/linea destino
			Long lineaSeguroIdDestino = lineaDao.getLineaSeguroId(lineaDest, planDest);
			if (lineaSeguroIdDestino == null) {
				logger.debug("El plan/l�nea destino no existe, no se continua con la r�plica");
				parameters.put(ALERTA, bundle.getString("mensaje.mtoDescuentos.replica.planlinea.destino.KO"));
				return parameters;
			}
			
			// Valida que el plan/linea destino no tenga descuentos dados de alta previamente para la entidad seleccionada
			MtoDescuentosFilter filter = new MtoDescuentosFilter ();
			filter.addFilter(LINEA_CODPLAN, planDest);
			filter.addFilter(LINEA_CODLINEA, lineaDest);
			filter.addFilter(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, entidadReplica);
			
			if (getDescuentosCountWithFilter(filter) != 0) {
				logger.debug("El plan/l�nea destino de la entidad seleccionada tiene descuentos dados de alta, no se continua con la r�plica");
				parameters.put(ALERTA, bundle.getString("mensaje.mtoDescuentos.replica.planlinea.KO"));
				return parameters;
			}
			
			// Llamada al método del DAO que realiza la réplica
			mtoDescuentosDao.replicar(new BigDecimal(lineaSeguroIdOrigen), new BigDecimal(lineaSeguroIdDestino), codUsuario, entidadReplica);
		} 
		catch (DAOException e) {
			logger.error("Ocurri� un error al replicar los descuentos", e);
			parameters.put(ALERTA, bundle.getString("mensaje.mtoDescuentos.replica.KO"));
			return parameters;
		}
		
		// Si llega hasta aquí, el proceso de réplica ha finalizado correctamente
		logger.debug("El proceso de r�plica ha finalizado correctamente");
		parameters.put("mensaje", bundle.getString("mensaje.mtoDescuentos.replica.OK"));
		return parameters;
	}

public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, Descuentos descuentosBean, Usuario usuario) throws DAOException {
		
		Map<String, String> parameters = new HashMap<String, String>();
		try {
			String listaIds = listaIdsMarcados_cm.substring(0,listaIdsMarcados_cm.length()-1);
			mtoDescuentosDao.cambioMasivo(listaIds, descuentosBean);
			mtoDescuentosDao.cambioMasivoHistorico(listaIds, descuentosBean, usuario);
		} catch (DAOException e) {
			logger.error("Error al ejecutar el Cambio Masivo ", e);		
		} catch (Exception e) {
			logger.error("Error al ejecutar el Cambio Masivo ", e);	
		}
		
		return parameters;		
	}

public Descuentos getCambioMasivoBeanFromLimit(Limit consultaDescuentos_LIMIT) {
	Descuentos descuentosBean = new Descuentos();
	
	// ID
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(ID))){
		descuentosBean.setId(new Long(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(ID)).getValue()));
	}
	// ENTIDAD
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD))){
		descuentosBean.getSubentidadMediadora().getEntidad().setCodentidad(new BigDecimal(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(ENTIDAD)).getValue()));
	}
	// NOMBRE_ENTIDAD
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(NOMBRE_ENTIDAD))){
		descuentosBean.getSubentidadMediadora().getEntidad().setNomentidad(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(NOMBRE_ENTIDAD)).getValue().toString());
	}
	//OFICINA
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(OFICINA))){
		descuentosBean.getOficina().getId().setCodoficina(new BigDecimal (consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(OFICINA)).getValue()));
	}		
	//NOMBRE_OFICINA
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(NOMBRE_OFICINA))){
		descuentosBean.getOficina().setNomoficina(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(NOMBRE_OFICINA)).getValue().toString());
	}
	//ESMEDIADORA
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(ESMEDIADORA))){
		descuentosBean.getSubentidadMediadora().getId().setCodentidad(new BigDecimal (consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(ESMEDIADORA)).getValue()));
	}
	//SUBENTMEDIADORA
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(SUBENTMEDIADORA))){
		descuentosBean.getSubentidadMediadora().getId().setCodsubentidad(new BigDecimal(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(SUBENTMEDIADORA)).getValue()));
	}
	//DELEGACION
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(DELEGACION))){
		descuentosBean.setDelegacion(new BigDecimal(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(DELEGACION)).getValue()));
	}
	//EXTERNO
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(PCTDESCMAX))){
		descuentosBean.setPctDescMax(new BigDecimal(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(PCTDESCMAX)).getValue()));
	}
	
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(FECHABAJA)){ 
		descuentosBean.setFechaBaja(new Date(consultaDescuentos_LIMIT.getFilterSet().getFilter(FECHABAJA).getValue().toString()));
	}
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(PLAN))){
		descuentosBean.getLinea().setCodplan(new BigDecimal(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(PLAN)).getValue()));
	}
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(LINEA))){
		descuentosBean.getLinea().setCodlinea(new BigDecimal(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(LINEA)).getValue()));
	}
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(RECARGO))){
		descuentosBean.setPermitirRecargo(new Integer (consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(RECARGO)).getValue()));
	}
	if(null != consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(VER_COMISIONES))){
		descuentosBean.setVerComisiones(new Integer(consultaDescuentos_LIMIT.getFilterSet().getFilter(columnas.get(VER_COMISIONES)).getValue()));
	}
	
	return descuentosBean;
}

@Override
public String getlistaIdsTodos(MtoDescuentosFilter consultaFilter) {
	//para cambio masivo. Cambiamos la consulta para que sean los que tengan FechaBaja == Null
	consultaFilter.addFilter("fechaBajaCambioMasivo", 1);
	String listaIdsTodos =mtoDescuentosDao.getlistaIdsTodos(consultaFilter);
	return listaIdsTodos;
	
}
	
	@Override
	public ArrayList<DescuentosHistorico> consultaHistorico(Long id)
			throws Exception {
		try {
			return mtoDescuentosDao.consultaHistorico(id);

		} catch (DAOException dao) {
			logger.error("Se ha producido un error al consultar el historico: "
					+ dao.getMessage());
			throw new BusinessException(
					"Se ha producido un error al consultar el historico", dao);
		}
	}

	public void setMtoDescuentosDao(IMtoDescuentosDao mtoDescuentosDao) {
		this.mtoDescuentosDao = mtoDescuentosDao;
	}

	public void setId(String id) {
		this.idDto = id;
	}

	public void setSubentidadMediadoraDao(
			ISubentidadMediadoraDao subentidadMediadoraDao) {
		this.subentidadMediadoraDao = subentidadMediadoraDao;
	}

	public void setEntidadDao(IEntidadDao entidadDao) {
		this.entidadDao = entidadDao;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setPagoManualService(IPagoManualService pagoManualService) {
		this.pagoManualService = pagoManualService;
	}

	

}
