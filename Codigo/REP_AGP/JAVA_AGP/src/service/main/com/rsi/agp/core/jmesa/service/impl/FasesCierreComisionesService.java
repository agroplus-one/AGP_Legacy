package com.rsi.agp.core.jmesa.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
import com.rsi.agp.core.jmesa.filter.FasesCierreComsFilter;
import com.rsi.agp.core.jmesa.service.IFasesCierreComisionesService;
import com.rsi.agp.core.jmesa.sort.FasesCierreComsSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.LongFilterMatcher;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.comisiones.IFasesCierreComisionesDao;
import com.rsi.agp.dao.tables.comisiones.unificado.FasesCerradas;



public class FasesCierreComisionesService implements IFasesCierreComisionesService{
	
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private IFasesCierreComisionesDao fasesCierreComisionesDao;
	
	public static final String CAMPO_ID              ="id";
	public static final String CAMPO_CIERRE          ="cierre";
	public static final String CAMPO_FASE            ="fase";
	public static final String CAMPO_PLAN            ="plan";   
	public static final String CAMPO_FECHAEMISION  	 ="fecha";
	
	
	
	
	 public int getFasesCierreCountWithFilter(FasesCierreComsFilter filter,
			 FasesCerradas fase) {
		 return fasesCierreComisionesDao.getFasesCierreCountWithFilter(filter,
				 fase);
	}
	
	public Collection<FasesCerradas> getFasesCierreWithFilterAndSort(
			FasesCierreComsFilter filter, FasesCierreComsSort sort, int rowStart,
			int rowEnd,  FasesCerradas fase) throws BusinessException {

		return fasesCierreComisionesDao.getFasesCierreWithFilterAndSort(filter, sort,
				rowStart, rowEnd, fase);
	}
	
	
	@Override
	public String getTablaFasesCierre(HttpServletRequest request,
			HttpServletResponse response, FasesCerradas fase,
			String origenLlamada) {
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				fase, origenLlamada);

		setDataAndLimitVariables(tableFacade, fase);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade,origenLlamada);
	}
	
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			FasesCerradas fase,
			String origenLlamada) {
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,
				request);
		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		// Carga las columnas a mostrar en el listado en el TableFacade

		tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		tableFacade.addFilterMatcher(new MatcherKey(Long.class), new LongFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
		
		cargarColumnas(tableFacade);

		// Si no es una llamada a través de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"listadoFasesCierre_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoFasesCierre_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de búsqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(fase, tableFacade);

			}
		}

		return tableFacade;
	}
	/**
	 * Método para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade,String origenLlamada) {

		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");
		
		// Configuración de las columnas de la tabla
		configurarColumnas(table);

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			tableFacade.render(); // Will write the export data out to the
									// response.
			return null; // In Spring return null tells the controller not to
							// do anything.
		} 
			
		// Devuelve el html de la tabla
		return tableFacade.render();
	}
	/**
	 * Configuración de las columnas de la tabla
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
		/*configColumna(table, CAMPO_ID, "&nbsp;&nbsp;Acciones",false, false,
				"4%");*/
		configColumna(table, CAMPO_FASE, "Fase",true, true,"2%");
		configColumna(table, CAMPO_PLAN, "Plan", true, true,"2%");
		configColumnaFecha(table,CAMPO_FECHAEMISION, "Fecha Emisi&oacuten", true,true, "2%", "dd/MM/yyyy");
		
		
	}
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores pasados como parámetro y los incluye en la tabla
	 * @param table Objeto que contiene la tabla
	 * @param idCol Id de la columna
	 * @param title Título de la columna
	 * @param filterable Indica si se podrá buscar por esa columna
	 * @param sortable Indica si se podrá ordenar por esa columna
	 * @param width Ancho de la columna
	 * @param fFecha Formato de fecha con la que se mostrarán los datos de esta columna
	 */
	@SuppressWarnings("deprecation")
	private void configColumnaFecha (HtmlTable table, String idCol, String title, boolean filterable, boolean sortable, String width, String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Añade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor(fFecha));
		} catch (Exception e) {
			logger.error("Ocurrió un error al configurar el formato de fecha de la columna " + idCol, e);
		}
	}
	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parámetro y los incluye en la tabla
	 * 
	 * @param table
	 *            Objeto que contiene la tabla
	 * @param idCol
	 *            Id de la columna
	 * @param title
	 *            Título de la columna
	 * @param filterable
	 *            Indica si se podrá buscar por esa columna
	 * @param sortable
	 *            Indica si se podrá ordenar por esa columna
	 * @param width
	 *            Ancho de la columna
	 */
	private void configColumna(HtmlTable table, String idCol, String title,
			boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
	}
	
	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade
	 * 
	 * @param tableFacade
	 */
	@SuppressWarnings("all")
	private void cargarColumnas(TableFacade tableFacade) {

		// Configura el TableFacade con las columnas que se quieren mostrar
		tableFacade.setColumnProperties(CAMPO_FASE,CAMPO_PLAN, CAMPO_FECHAEMISION
				);

	}
	private void cargarFiltrosBusqueda(FasesCerradas fase,
			TableFacade tableFacade) {
		if (FiltroUtils.noEstaVacio(fase.getCierre()))
			tableFacade.getLimit().getFilterSet().addFilter(
					"cierre",
					fase.getCierre().toString());

	}
	
	private void setDataAndLimitVariables(TableFacade tableFacade,
			FasesCerradas fase) {

		// Obtiene el Filter para la búsqueda de ficheros
		Limit limit = tableFacade.getLimit();
		FasesCierreComsFilter fasesFilter = getFasesCierreFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getFasesCierreCountWithFilter(fasesFilter,
				fase);
		logger
				.debug("********** Número de filas  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		FasesCierreComsSort fasesSort = getFasesCierreSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<FasesCerradas> items = new ArrayList<FasesCerradas>();
		
		try {
			items = getFasesCierreWithFilterAndSort(fasesFilter,
					fasesSort, rowStart, rowEnd, fase);
			logger.debug("********** Items de la lista   = "
					+ items.size() + " **********");
			
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}

	

	/**
	 * Crea y configura el Filter para la consulta de datos del fichero
	 * 
	 * @param limit
	 * @return
	 */
	private FasesCierreComsFilter getFasesCierreFilter(Limit limit) {
		FasesCierreComsFilter fasesFilter = new FasesCierreComsFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			fasesFilter.addFilter(property, value);
		}
		return fasesFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de datos del fichero
	 * 
	 * @param limit
	 * @return
	 */
	private FasesCierreComsSort getFasesCierreSort(Limit limit) {
		FasesCierreComsSort fasesSort = new FasesCierreComsSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			fasesSort.addSort(property, order);
		}

		return fasesSort;
	}

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				String idFase= new BasicCellEditor().getValue(item,
						CAMPO_ID, rowcount).toString();
				HtmlBuilder html = new HtmlBuilder();
				
				// botón abrir informe
				html.a().href().quote().append(
						"javascript:doAbrirInformes('" + idFase + "');").quote()
						.close();
				html
						.append("<img src=\"jsp/img/folderopen.gif\" alt=\"Abrir Informes\" title=\"Abrir Informes\"/>");
				html.aEnd();
				html.append("&nbsp;");
				
				return html.toString();
			}
		};
	}
	public void setId(String id) {
		this.id = id;
	}

	public void setFasesCierreComisionesDao(
			IFasesCierreComisionesDao fasesCierreComisionesDao) {
		this.fasesCierreComisionesDao = fasesCierreComisionesDao;
	}

	


}
