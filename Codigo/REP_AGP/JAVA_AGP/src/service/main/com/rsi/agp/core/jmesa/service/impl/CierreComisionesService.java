package com.rsi.agp.core.jmesa.service.impl;

import java.util.ArrayList;
import java.util.Collection;

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
import com.rsi.agp.core.jmesa.filter.CierreFilter;
import com.rsi.agp.core.jmesa.service.ICierreComisionesService;
import com.rsi.agp.core.jmesa.sort.CierreSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.comisiones.ICierreComisionesDao;
import com.rsi.agp.dao.tables.comisiones.Cierre;



@SuppressWarnings("deprecation")
public class CierreComisionesService implements ICierreComisionesService{
	
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private ICierreComisionesDao cierreComisionesDao;
	private String idBorrable = null;
	
	public static final String CAMPO_ID      	  ="id";
	public static final String CAMPO_PERIODO 	  ="periodo";
	public static final String CAMPO_USUARIO      ="usuario";
	public static final String CAMPO_FECHA_CIERRE ="fechacierre";
	
	@Override
	public int getCierreCountWithFilter(CierreFilter filter) {
		return cierreComisionesDao.getCierreCountWithFilter(filter);
	}

	@Override
	public Collection<Cierre> getCierreWithFilterAndSort(CierreFilter filter,
			CierreSort sort, int rowStart, int rowEnd)
			throws BusinessException {
		return cierreComisionesDao.getCierreWithFilterAndSort(filter,
				sort, rowStart, rowEnd);
	}

	@Override
	public String getTablaCierre(HttpServletRequest request,
			HttpServletResponse response, Cierre cierreBean,
			String origenLlamada) {
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				cierreBean, origenLlamada);
		setDataAndLimitVariables(tableFacade);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());
		
		idBorrable = (String)request.getAttribute("idBorrable");

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade);
	}
	private void setDataAndLimitVariables(TableFacade tableFacade) {
		// Obtiene el Filter 
		Limit limit = tableFacade.getLimit();
		CierreFilter cierreFilter = getCierreFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getCierreCountWithFilter(cierreFilter);
		logger
				.debug("********** Número de filas para la búsqueda de cierres  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		// Crea el Sort 
		CierreSort cierreSort = getCierreSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<Cierre> items = new ArrayList<Cierre>();

		try {
			items = getCierreWithFilterAndSort(cierreFilter,
					cierreSort, rowStart, rowEnd);
			logger.debug("********** Items de la lista de cierre = "
					+ items.size() + " **********");
			
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
		
	}
	private CierreFilter getCierreFilter(Limit limit) {
		CierreFilter cierreFilter = new CierreFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			cierreFilter.addFilter(property, value);
		}
		return cierreFilter;
	}
	
	private CierreSort getCierreSort(Limit limit) {
		CierreSort cierreSort = new CierreSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			cierreSort.addSort(property, order);
		}

		return cierreSort;
	}
	
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, Cierre cierre,
			String origenLlamada) {
		
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,
				request);
		tableFacade.setStateAttr("restore");
		
		// Carga las columnas a mostrar en el listado en el TableFacade
		tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringFilterMatcher());
		
		cargarColumnas(tableFacade);
		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"listadoCierres_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoCierres_LIMIT"));					
				}
			} 
			Sort fechaCierre = new Sort(1, CAMPO_FECHA_CIERRE, Order.DESC);
			tableFacade.getLimit().getSortSet().addSort(fechaCierre);
		}

		return tableFacade;
	}

	private void cargarColumnas(TableFacade tableFacade) {
		// Configura el TableFacade con las columnas que se quieren mostrar
		tableFacade.setColumnProperties(CAMPO_ID,
				CAMPO_PERIODO,
				CAMPO_USUARIO,
				CAMPO_FECHA_CIERRE);
	}

	private String html(TableFacade tableFacade) {

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
		} else {
			// Acciones
			table.getRow().getColumn(CAMPO_ID)
				.getCellRenderer().setCellEditor(getCellEditorAcciones());
			
		}

		// Devuelve el html de la tabla
		return tableFacade.render();
	}
	
	private void configurarColumnas(HtmlTable table) {
		configColumna(table,CAMPO_ID, "&nbsp;&nbsp;Acciones",false,
				false,"4%");
		
		configColumnaFecha(table, CAMPO_PERIODO, "Periodo", true, true, "5%", "dd/MM/yyyy");
		configColumna(table,CAMPO_USUARIO, "Usuario", true,	true, "5%");
		configColumnaFecha(table,CAMPO_FECHA_CIERRE, "Fecha Cierre", true,true, "5%", "dd/MM/yyyy");
		
		
	}
	private void configColumna(HtmlTable table, String idCol, String title,
			boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
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
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				String id = new BasicCellEditor().getValue(item,
						CAMPO_ID, rowcount).toString();
				
				HtmlBuilder html = new HtmlBuilder();
				
				//Boton ver detalle
				html.a().href().quote().append(
						"javascript:detalle('"+id+"');").quote()
						.close();
				html
						.append("<img src=\"jsp/img/displaytag/information.png\" alt=\"ver detalle\" title=\"ver detalle\"/>");
				html.aEnd();
				html.append("&nbsp;");
				//Boton abrir informes
				html.a().href().quote().append(
						"javascript:abrirInformebyIdCierre('" + id + "');").quote()
						.close();
				html
						.append("<img src=\"jsp/img/folderopen.gif\" alt=\"Abrir Informes\" title=\"Abrir Informes\"/>");
				html.aEnd();
				html.append("&nbsp;");
				
				//M00000529 - Icono papelera para borrar
				if(id.equals(idBorrable)){
					html.a().href().quote().append(
							"javascript:borrarCierre('"+id+"');").quote().close();
					html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/>");
					html.aEnd();
				}
				
				return html.toString();
			}
		};
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCierreComisionesDao(ICierreComisionesDao cierreComisionesDao) {
		this.cierreComisionesDao = cierreComisionesDao;
	}
}