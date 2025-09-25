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
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ReglamentoSitFilter;
import com.rsi.agp.core.jmesa.service.IFicheroReglamentoSitService;
import com.rsi.agp.core.jmesa.sort.ReglamentoSitSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.comisiones.IReglamentoDao;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;

public class FicheroReglamentoSitService implements IFicheroReglamentoSitService{
	
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private IReglamentoDao reglamentoDao;
	
	public static final String CAMPO_ID               ="id";
	public static final String CAMPO_CODIGO           ="codigo";
	public static final String CAMPO_DT_MEDREGLAMENTO ="dtMedreglamento";
	public static final String CAMPO_DT_PORCENTAJE    ="dtPorcentaje";   
	public static final String CAMPO_DT_MEDIDA        ="dtMedida";
	public static final String CAMPO_DT_IMPORTEAPL   ="dtImporteApl";
	public static final String CAMPO_DT_IMPORTESRED   ="dtImporteSRed";
	public static final String CAMPO_DC_MEDREGLAMENTO ="dcMedreglamento";
	public static final String CAMPO_DC_PORCENTAJE    ="dcPorcentaje";
	public static final String CAMPO_DC_MEDIDA        ="dcMedida";
	public static final String CAMPO_DC_IMPORTEAPL    ="dcImporteApl";  
	public static final String CAMPO_DC_IMPORTESRED   ="dcImporteSRed";
	
	 public int getFicheroReglamentoSitCountWithFilter(ReglamentoSitFilter filter,
			 ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit) {
		 return reglamentoDao.getFicheroReglamentoSitCountWithFilter(filter,
				 reglamentoProduccionEmitidaSit);
	}
	
	public Collection<ReglamentoProduccionEmitidaSituacion> getFicheroReglamentoSitWithFilterAndSort(
			ReglamentoSitFilter filter, ReglamentoSitSort sort, int rowStart,
			int rowEnd, ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit) throws BusinessException {

		return reglamentoDao.getFicheroReglamentoSitWithFilterAndSort(filter, sort,
				rowStart, rowEnd, reglamentoProduccionEmitidaSit);
	}
	
	@Override
	public String getTablaReglamentoSit(HttpServletRequest request,	HttpServletResponse response,
			ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSitBean,
			String origenLlamada) {
		
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				reglamentoProduccionEmitidaSitBean, origenLlamada);

		setDataAndLimitVariables(tableFacade, reglamentoProduccionEmitidaSitBean);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade,origenLlamada);
	}
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSitBean,
			String origenLlamada) {
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,
				request);
		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		// Carga las columnas a mostrar en el listado en el TableFacade

		tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringFilterMatcher());
		
		
		cargarColumnas(tableFacade);

		// Si no es una llamada a través de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"listadoReglamentoSit_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoReglamentoSit_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de búsqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(reglamentoProduccionEmitidaSitBean, tableFacade);

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
		
		configColumna(table, CAMPO_CODIGO, "C&oacutedigo",true, true,
				"8%");
		configColumna(table, CAMPO_DC_IMPORTEAPL, "DC. Importe Apl.", false, false,
				"8%");
		configColumna(table, CAMPO_DC_IMPORTESRED, "DC. Importe Sin Reduccion", false, false,
				"8%");
		configColumna(table, CAMPO_DC_MEDIDA, "DC. Medida", false, false,
				"8%");
		configColumna(table, CAMPO_DC_MEDREGLAMENTO, "DC. Med. Reglamento", false, false,
				"8%");
		configColumna(table, CAMPO_DC_PORCENTAJE, "DC. Porcentaje", false, false,
				"8%");
		configColumna(table, CAMPO_DT_IMPORTEAPL, "DT. Importe Apl.", false, false,
				"8%");
		configColumna(table, CAMPO_DT_IMPORTESRED, "DT. Importe Sin Reduccion", false, false,
				"8%");
		configColumna(table, CAMPO_DT_MEDIDA, "DT. Medida", false, false,
				"8%");
		configColumna(table, CAMPO_DT_MEDREGLAMENTO, "DT. Med. Reglamento", false, false,
				"8%");
		configColumna(table, CAMPO_DT_PORCENTAJE, "DT. Porcentaje", false, false,
				"8%");


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
		tableFacade.setColumnProperties(CAMPO_CODIGO,CAMPO_DT_MEDREGLAMENTO,CAMPO_DT_PORCENTAJE,CAMPO_DT_MEDIDA
				,CAMPO_DT_IMPORTEAPL,CAMPO_DT_IMPORTESRED,CAMPO_DC_MEDREGLAMENTO,CAMPO_DC_PORCENTAJE  
				,CAMPO_DC_MEDIDA,CAMPO_DC_IMPORTEAPL,CAMPO_DC_IMPORTESRED
				);

	}
	private void cargarFiltrosBusqueda(ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit,
			TableFacade tableFacade) {
		if (FiltroUtils.noEstaVacio(reglamentoProduccionEmitidaSit.getId()))
			tableFacade.getLimit().getFilterSet().addFilter(
					"reglamentoProduccionEmitida.id",
					reglamentoProduccionEmitidaSit.getId().toString());

	}
	
	private void setDataAndLimitVariables(TableFacade tableFacade,
			ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit) {

		// Obtiene el Filter para la búsqueda de ficheros
		Limit limit = tableFacade.getLimit();
		ReglamentoSitFilter reglamentoFilter = getReglamentoSitFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getFicheroReglamentoSitCountWithFilter(reglamentoFilter,
				reglamentoProduccionEmitidaSit);
		logger
				.debug("********** Número de filas  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		ReglamentoSitSort reglamentoSort = getReglamentoSitSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<ReglamentoProduccionEmitidaSituacion> items = new ArrayList<ReglamentoProduccionEmitidaSituacion>();

		try {
			items = getFicheroReglamentoSitWithFilterAndSort(reglamentoFilter,
					reglamentoSort, rowStart, rowEnd, reglamentoProduccionEmitidaSit);
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
	private ReglamentoSitFilter getReglamentoSitFilter(Limit limit) {
		ReglamentoSitFilter reglamentoSitFilter = new ReglamentoSitFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			reglamentoSitFilter.addFilter(property, value);
		}
		return reglamentoSitFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de datos del fichero
	 * 
	 * @param limit
	 * @return
	 */
	private ReglamentoSitSort getReglamentoSitSort(Limit limit) {
		ReglamentoSitSort reglamentoSitSort = new ReglamentoSitSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			reglamentoSitSort.addSort(property, order);
		}

		return reglamentoSitSort;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setReglamentoDao(IReglamentoDao reglamentoDao) {
		this.reglamentoDao = reglamentoDao;
	}

	

}
