package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
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
import org.jmesa.limit.ExportType;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IMtoZonasDao;
import com.rsi.agp.core.jmesa.dao.impl.MtoZonasDao;
import com.rsi.agp.core.jmesa.filter.MtoZonasFilter;
import com.rsi.agp.core.jmesa.service.IMtoZonasService;
import com.rsi.agp.core.jmesa.sort.MtoZonasSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;
import com.rsi.agp.dao.models.commons.IEntidadDao;
import com.rsi.agp.dao.tables.commons.Zona;

/**
 * @author U028975 (T-Systems) GDLD-63701 - Mantenimiento de Zonas
 */

@SuppressWarnings("deprecation")
public class MtoZonasService implements IMtoZonasService {

	private static final String NOMBRE_ZONA = "Nombre Zona";
	private static final String COD_ZONA = "Cod. Zona";
	private static final String NOMBRE_ENTIDAD = "Nombre Entidad";
	private static final String COD_ENTIDAD = "Cod. Entidad";
	private static final String LISTADO_MANTENIMIENTO_ZONAS = "LISTADO MANTENIMIENTO ZONAS";
	private IMtoZonasDao mtoZonasDao;
	private IEntidadDao entidadDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private Log logger = LogFactory.getLog(getClass());
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String id;
	
	private MtoZonasFilter consultaFilter;
	private MtoZonasSort consultaSort;


	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "id";
	private final static String CODENTIDAD = "codentidad";
	private final static String NOMBENTIDAD = "Nombentidad";
	private final static String CODZONA = "codzona";
	private final static String NOMZONA = "nomzona";
	protected static final String ID_CODENTIDAD = "id.codentidad";

	@Override
	public Map<String, Object> altaZona(Zona zonaBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			// Se valida que la zona no exista ya.
			if (mtoZonasDao.existeZona(zonaBean)) {
				parameters.put("alerta", bundle.getString("mensaje.mtoZonas.existeRegistro"));
				// se valida que la entidad exista
			} else if (!entidadDao.existeEntidad(zonaBean.getId().getCodentidad())) {
				parameters.put("alerta", bundle.getString("mensaje.mtoZonas.EntidadNoExiste"));
				// Si todo es correcto se da de alta la zona
			} else {
				mtoZonasDao.saveOrUpdate(zonaBean);
				parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
			}
			if (parameters.get("alerta") != null) {
				parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO") + " " + parameters.get("alerta"));
			}
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al dar de alta la zona", ex);
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
		} catch (Exception ex) {
			logger.debug("Error al dar de alta la zona", ex);
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
		}
		return parameters;
	}

	@Override
	public Map<String, Object> editaZona(Zona zonaBean, HttpServletRequest request) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			// se valida que la entidad exista
			if (!entidadDao.existeEntidad(zonaBean.getId().getCodentidad())) {
				parameters.put("alerta", bundle.getString("mensaje.mtoZonas.EntidadNoExiste"));
			} else {
				String codEntidadIni = request.getParameter("codentidadInicial");
				BigDecimal codEntidad = new BigDecimal(codEntidadIni);
				String codZonaIni = request.getParameter("codzonaInicial");
				BigDecimal codzona = new BigDecimal(codZonaIni);
				// Modificamos la zona
				mtoZonasDao.modificarZona(zonaBean, codEntidad, codzona);

				parameters.put("mensaje", bundle.getString("mensaje.mtoZonas.edicion.OK"));
			}
			if (parameters.get("alerta") != null) {
				parameters.put("alerta",
						bundle.getString("mensaje.mtoZonas.edicion.KO") + " " + parameters.get("alerta"));
			}
		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al editar la Zona", ex);
			parameters.put("alerta", bundle.getString("mensaje.mtoZonas.edicion.KO"));
		} catch (Exception ex) {
			logger.debug("Error al editar la Zona", ex);
			parameters.put("alerta", bundle.getString("mensaje.mtoZonas.edicion.KO"));
		}
		return parameters;
	}

	@Override
	public Map<String, Object> borraZona(Zona zonaBean) throws BusinessException {

		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			// comprobamos que la zona a borrar no esté asociada a una oficina
			if (mtoZonasDao.esZonaConOficina(zonaBean)) {
				parameters.put("alerta", bundle.getString("mensaje.mtoZonas.borrar.esZonaConOficinas"));
				// Si todo es correcto se borra la zona
			} else {
				mtoZonasDao.borrarZona(zonaBean);
				parameters.put("mensaje", bundle.getString("mensaje.mtoZonas.borrar.OK"));
			}
			if (parameters.get("alerta") != null) {
				parameters.put("alerta",
						bundle.getString("mensaje.mtoZonas.borrar.KO") + " " + parameters.get("alerta"));
			}

		} catch (DataIntegrityViolationException ex) {
			logger.debug("Error al dar de baja la zona", ex);
			parameters.put("alerta", bundle.getString("mensaje.mtoZonas.borrar.KO"));
		} catch (Exception ex) {
			logger.debug("Error al dar de baja la zona", ex);
			parameters.put("alerta", bundle.getString("mensaje.mtoZonas.borrar.KO"));
		}
		return parameters;
	}

	@Override
	public String getTablaZonas(HttpServletRequest request, HttpServletResponse response, Zona zonaBusqueda,
			String origenLlamada) {
		
		TableFacade tableFacade = crearTableFacade(request, response, zonaBusqueda, origenLlamada);

		Limit limit = tableFacade.getLimit();
		consultaFilter = getConsultaZonasFilter(limit);

		setDataAndLimitVariables(tableFacade, consultaFilter, limit);

		tableFacade.setExportTypes(response, ExportType.EXCEL);
		
		String ajax = request.getParameter("ajax");
		
		if (!"false".equals(ajax) && request.getParameter("export") == null) {
			tableFacade.setToolbar(new CustomToolbar());
			tableFacade.setView(new CustomView());
		} 
		return html(tableFacade);
	}

	/**
	 * 16/06/2021 U028975 (T-Systems) Crea y configura el objeto TableFacade que
	 * encapsulara la tabla
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade(HttpServletRequest request, HttpServletResponse response, Zona zonaBusqueda,
			String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = new TableFacade(id, request);

		// Carga las columnas a mostrar en el listado en el TableFacade y devuelve un
		// Map con ellas
		HashMap<String, String> columnas = cargarColumnas(tableFacade);
		
		tableFacade.setExportTypes(response, ExportType.EXCEL);			

		tableFacade.setStateAttr("restore");// return to the table in the same state that the user left it.
		tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringLikeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		
		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null){
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("consultaZonas_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaZonas_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos en el formulario
				cargarFiltrosBusqueda(columnas, zonaBusqueda, tableFacade);
			}
		}

		return tableFacade;
	}

	/**
	 * 16/06/2021 U028975 (T-Systems)
	 * 
	 * @param tableFacade
	 * @return
	 */
	private HashMap<String, String> cargarColumnas(TableFacade tableFacade) {
		// Crea el Map con las columnas del listado y los campos del filtro de busqueda
		// si no se ha hecho anteriormente
		if (columnas.size() == 0) {
			columnas.put(ID_STR, "id");
			columnas.put(CODENTIDAD, "id.codentidad");
			columnas.put(NOMBENTIDAD, "nombentidad");
			columnas.put(CODZONA, "id.codzona");
			columnas.put(NOMZONA, "nomzona");
		}
		tableFacade.setColumnProperties(columnas.get(ID_STR), columnas.get(CODENTIDAD), columnas.get(NOMBENTIDAD),
				columnas.get(CODZONA), columnas.get(NOMZONA));

		return columnas;
	}

	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * 06/05/2014 U029769
	 * 
	 * @param columnas2
	 * @param Zona
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(HashMap<String, String> columnas2, Zona zona, TableFacade tableFacade) {
		// CODENTIDAD
		if (zona.getId().getCodentidad() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CODENTIDAD), zona.getId().getCodentidad().toString()));
		// CODZONA
		if (zona.getId().getCodzona() != null)
			tableFacade.getLimit().getFilterSet()
					.addFilter(new Filter(columnas.get(CODZONA), zona.getId().getCodzona().toString()));
		// NOMZONA
		if (zona.getNomzona() != null && !zona.getNomzona().equals(""))
			tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(NOMZONA), zona.getNomzona()));

	}

	/**
	 * Crea y configura el Filter para la consulta de Zonas 16/06/2021 U028975
	 * (T-Systems)
	 * 
	 * @param limit
	 * @return
	 */
	private MtoZonasFilter getConsultaZonasFilter(Limit limit) {
		MtoZonasFilter consultaFilter = new MtoZonasFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			consultaFilter.addFilter(property, value);
		}
		return consultaFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de Zonas 16/06/2021 U028975
	 * (T-Systems)
	 * 
	 * @param limit
	 * @return
	 */
	private MtoZonasSort getConsultaZonasSort(Limit limit) {
		MtoZonasSort consultaSort = new MtoZonasSort();
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
	 * Metodo para construir el html de la tabla a mostrar 06/05/2014 U029769
	 * 
	 * @param tableFacade
	 * @return
	 */
	private String html(TableFacade tableFacade) {

		Limit limit = tableFacade.getLimit();
		if (limit.hasExport()) {
			
			Table exportTable = tableFacade.getTable();
			// eliminamos la columna "Id"
			eliminarColumnaId(tableFacade, exportTable);

			// configuramos cada columna
			configurarColumnasExport(exportTable);

			// renombramos las cabeceras
			configurarCabecerasColumnasExport(exportTable);
			
			tableFacade.render(); // Will write the export data out to the response.
			return null; // In Spring return null tells the controller not to do anything.
		} else {
			
			HtmlTable table = (HtmlTable) tableFacade.getTable();
			table.getRow().setUniqueProperty("id");
			
			configurarColumnas(table);
			
			// Configuracion de los datos de las columnas que requieren un tratamiento para
			// mostrarse
			// campo acciones
			table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
			table.getRow().getColumn(columnas.get(CODENTIDAD)).getCellRenderer().setCellEditor(getCellEditorEntidad());
			table.getRow().getColumn(columnas.get(NOMBENTIDAD)).getCellRenderer()
					.setCellEditor(getCellEditorNombEntidad());
			table.getRow().getColumn(columnas.get(CODZONA)).getCellRenderer().setCellEditor(getCellEditorZona());
			// table.getRow().getColumn(columnas.get(NOMZONA)).getCellRenderer().setCellEditor(getCellEditorNombreUsu());
		}

		return tableFacade.render();
	}

	private void configurarCabecerasColumnasExport(Table table) {
		
		table.setCaption(LISTADO_MANTENIMIENTO_ZONAS);
		Row row = table.getRow();

		Column colCodEntidad = row.getColumn(columnas.get(CODENTIDAD));
		colCodEntidad.setTitle(COD_ENTIDAD);
		
		Column colNombreEntidad = row.getColumn(columnas.get(NOMBENTIDAD));
		colNombreEntidad.setTitle(NOMBRE_ENTIDAD);
		
		Column colCodZona = row.getColumn(columnas.get(CODZONA));
		colCodZona.setTitle(COD_ZONA);
		
		Column colNombreZona = row.getColumn(columnas.get(NOMZONA));
		colNombreZona.setTitle(NOMBRE_ZONA);
		
	}

	private void configurarColumnasExport(Table table) {
		
		// campo nombre entidad
		table.getRow().getColumn(columnas.get(NOMBENTIDAD)).getCellRenderer().setCellEditor(getCellEditorNombEntidad());
		
	}

	/**
	 * metodo para eliminar la columna Id en los informes
	 * 
	 * @param tableFacade
	 * @param table
	 */
	private void eliminarColumnaId(TableFacade tableFacade, Table table) {
		Row row = table.getRow();
		Row rowFinal = new Row();
		List<Column> lstColumns = row.getColumns();
		for (Column col : lstColumns) {
			if (!col.getProperty().equals("id")) {
				rowFinal.addColumn(col);
			}
		}
		table.setRow(rowFinal);
		tableFacade.setTable(table);
	}

	/**
	 * Configuracion de las columnas de la tabla 08/05/2014 U029769
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false, false, "10%");
		configColumna(table, columnas.get(CODENTIDAD), COD_ENTIDAD, true, true, "15%");
		configColumna(table, columnas.get(NOMBENTIDAD), NOMBRE_ENTIDAD, false, false, "25%");
		configColumna(table, columnas.get(CODZONA), COD_ZONA, true, true, "10%");
		configColumna(table, columnas.get(NOMZONA), NOMBRE_ZONA, true, true, "35%");

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
	private void configColumna(HtmlTable table, String idCol, String title, boolean filterable, boolean sortable,
			String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);

	}

	/**
	 * Devuelve el objeto que muestra la informacion de la columna 'Acciones'
	 * 06/05/2014 U029769
	 * 
	 * @return CellEditor
	 */
	private CellEditor getCellEditorAcciones() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();

				BigDecimal codentidad = (BigDecimal) new BasicCellEditor().getValue(item, "id.codentidad", rowcount);
				BigDecimal codzona = (BigDecimal) new BasicCellEditor().getValue(item, "id.codzona", rowcount);
				String nomzona = (String) new BasicCellEditor().getValue(item, "nomzona", rowcount);
				String nombentidad = getNombEntidad(codentidad);

				StringBuilder funcion = new StringBuilder();
				funcion.append(codentidad != null ? codentidad : "''").append(",")
						.append(codzona != null ? codzona : "''").append(");");

				// boton editar
				html.a().href().quote().append("javascript:editar(" + codentidad + ", '" + nombentidad + "'," + codzona
						+ ",'" + nomzona + "');").quote().close();
				html.append(
						"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Modificar Zona\" title=\"Modificar Zona\"/>");
				html.aEnd();
				html.append("&nbsp;");

				// boton borrar
				StringBuilder funcionBorra = new StringBuilder();
				funcionBorra.append("javascript:borrar( ").append(funcion);
				html.a().href().quote().append(funcionBorra).quote().close();
				html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Zona\" title=\"Borrar Zona\"/>");
				html.aEnd();
				html.append("&nbsp;");

				return html.toString();
			}
		};
	}

	@Override
	public String getNombEntidad(BigDecimal codEntidad) {
		return this.mtoZonasDao.getNombEntidad(codEntidad);
	}

	private CellEditor getCellEditorEntidad() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal entidad = (BigDecimal) new BasicCellEditor().getValue(item, "id.codentidad", rowcount);

				String codEntidad;
				if (StringUtils.nullToString(entidad).equals("")) {
					codEntidad = "&nbsp;";
				} else {
					codEntidad = entidad.toString();

					if (codEntidad.length() < 4) {
						while (codEntidad.length() < 4) {
							codEntidad = "0" + codEntidad;
						}
					}
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(codEntidad);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorZona() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal zona = (BigDecimal) new BasicCellEditor().getValue(item, "id.codzona", rowcount);

				String codZona;
				if (StringUtils.nullToString(zona).equals("")) {
					codZona = "&nbsp;";
				} else {
					codZona = zona.toString();

					if (codZona.length() < 5) {
						while (codZona.length() < 5) {
							codZona = "0" + codZona;
						}
					}
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(codZona);
				return html.toString();
			}
		};
	}

	private CellEditor getCellEditorNombEntidad() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {

				BigDecimal entidad = (BigDecimal) new BasicCellEditor().getValue(item, "id.codentidad", rowcount);

				String NombEntidad = getNombEntidad(entidad);

				HtmlBuilder html = new HtmlBuilder();
				html.append(NombEntidad);
				return html.toString();
			}
		};
	}

	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los datos
	 * de Zonas y carga el TableFacade con ellas 16/06/2021 (U028975)
	 * 
	 * @param tableFacade
	 * @param consultaFilter
	 * @param limit
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade, MtoZonasFilter consultaFilter, Limit limit) {

		Collection<Zona> items = new ArrayList<Zona>();
		try {
			int totalRows = getZonasCountWithFilter(consultaFilter);
			logger.debug("********** count filas para Zonas  = " + totalRows + " **********");

			tableFacade.setTotalRows(totalRows);

			consultaSort = getConsultaZonasSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getZonasWithFilterAndSort(consultaFilter, consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para Zonas  = " + items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	@Override
	public Collection<Zona> getZonasWithFilterAndSort(MtoZonasFilter filter, MtoZonasSort sort, int rowStart,
			int rowEnd) throws BusinessException {
		return mtoZonasDao.getZonasWithFilterAndSort(filter, sort, rowStart, rowEnd);
	}

	@Override
	public int getZonasCountWithFilter(MtoZonasFilter filter) throws BusinessException {
		return mtoZonasDao.getZonasCountWithFilter(filter);
	}
	
	public List<Zona> getAllFilteredAndSorted() throws BusinessException {
	    // Obtener todos los registros filtrados y ordenados sin límites de paginación
	    Collection<Zona> allResults = mtoZonasDao.getZonasWithFilterAndSort(consultaFilter, consultaSort, -1, -1);
	    return (List<Zona>) allResults;
	}

	public void setMtoZonasDao(IMtoZonasDao mtoZonasDao) {
		this.mtoZonasDao = mtoZonasDao;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setEntidadDao(IEntidadDao entidadDao) {
		this.entidadDao = entidadDao;
	}
}
