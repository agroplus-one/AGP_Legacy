package com.rsi.agp.core.jmesa.service.impl;

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
import com.rsi.agp.core.jmesa.filter.ReglamentoFilter;
import com.rsi.agp.core.jmesa.service.IFicheroReglamentoService;
import com.rsi.agp.core.jmesa.sort.ReglamentoSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.comisiones.IReglamentoDao;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitida;

public class FicheroReglamentoService implements IFicheroReglamentoService{

	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private IReglamentoDao reglamentoDao;
	
	public static final String CAMPO_ID               ="id";
	public static final String CAMPO_ID_REGLAMENTO    ="id";
	public static final String CAMPO_LINEA	          ="linea";
	public static final String CAMPO_REFERENCIA       ="referencia";   
	public static final String CAMPO_DC		          ="dc";
	public static final String CAMPO_CODINTERNO       ="codigointerno";
	public static final String CAMPO_TIPOREFERENCIA   ="tiporeferencia";
	public static final String CAMPO_TIPORECIBO       ="tiporecibo";
	public static final String CAMPO_GASTOSEXTENT     ="gastosextEnt";
	public static final String CAMPO_COMISIONES       ="comisiones";
	public static final String CAMPO_GRUPONEGOCIO     ="gruponegocio";  
	public static final String CAMPO_COL_REFERENCIA   ="colReferencia";
	public static final String CAMPO_COL_DC           ="colDc";
	public static final String CAMPO_COL_CODINTERNO   ="colCodigointerno";
	public static final String CAMPO_DR_FECHARECEPCION="drFecharecepcion";
	public static final String CAMPO_DR_FECHAENTVIGOR = "drFechaentvigor";
	public static final String CAMPO_DR_FECHAPAGO     = "drFechapago" ;
	public static final String CAMPO_DR_LIMITE        ="drLimite";
	public static final String CAMPO_DR_COMPUTODIAS   ="drComputodias";
	
	 
	 
	 public int getFicheroReglamentoCountWithFilter(ReglamentoFilter filter,
			 ReglamentoProduccionEmitida reglamentoProduccionEmitida) {
		 return reglamentoDao.getFicheroReglamentoCountWithFilter(filter,
					reglamentoProduccionEmitida);
	}
	
	public Collection<ReglamentoProduccionEmitida> getFicheroReglamentoWithFilterAndSort(
			ReglamentoFilter filter, ReglamentoSort sort, int rowStart,
			int rowEnd, ReglamentoProduccionEmitida reglamentoProduccionEmitida) throws BusinessException {

		return reglamentoDao.getFicheroReglamentoWithFilterAndSort(filter, sort,
				rowStart, rowEnd, reglamentoProduccionEmitida);
	}
	 
	 
	 
	 @Override
	public String getTablaReglamento(HttpServletRequest request,
			HttpServletResponse response,
			ReglamentoProduccionEmitida reglamentoProduccionEmitidaBean,
			String origenLlamada) {
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				reglamentoProduccionEmitidaBean, origenLlamada);

		setDataAndLimitVariables(tableFacade, reglamentoProduccionEmitidaBean);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade,origenLlamada);
	}
	
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			ReglamentoProduccionEmitida reglamentoProduccionEmitidaBean,
			String origenLlamada) {
		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,
				request);
		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		// Carga las columnas a mostrar en el listado en el TableFacade

		tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class),
				new DateFromFilterMatcher("d/M/yyyy"));
		
		cargarColumnas(tableFacade);

		// Si no es una llamada a través de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"listadoReglamento_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoReglamento_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de búsqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(reglamentoProduccionEmitidaBean, tableFacade);

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
		} else {
			// Configuración de los datos de las columnas que requieren un
			// tratamiento para mostrarse
			// Acciones
			table.getRow().getColumn(CAMPO_ID).getCellRenderer()
					.setCellEditor(getCellEditorAcciones());
			
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
		configColumna(table, CAMPO_ID, "&nbsp;&nbsp;Acciones",false, false,
				"4%");
		configColumna(table, CAMPO_LINEA, "L&iacutenea",true, true,
				"5%");
		configColumna(table, CAMPO_REFERENCIA, "Ref.", true, true,
				"10%");
		configColumna(table, CAMPO_DC, "DC", false, false,
				"5%");
		configColumna(table, CAMPO_CODINTERNO, "Cod. Interno", false, false,
				"10%");
		configColumna(table, CAMPO_TIPORECIBO, "Tipo Recibo", false, false,
				"5%");
		configColumna(table, CAMPO_TIPOREFERENCIA, "Tipo Ref.", false, false,
				"5%");
		configColumna(table, CAMPO_GASTOSEXTENT, "Gastos Ext. Ent.", false, false,
				"5%");
		configColumna(table, CAMPO_COMISIONES, "Comisiones", false, false,
				"5%");
		configColumna(table, CAMPO_GRUPONEGOCIO, "Grupo Negocio", false, false,
				"5%");
		configColumna(table, CAMPO_COL_REFERENCIA, "Col. Ref.", false, false,
				"10%");
		configColumna(table, CAMPO_COL_DC, "Col. DC", false, false,
				"5%");
		configColumna(table, CAMPO_COL_CODINTERNO, "Col. Cod.Interno", false, false,
				"10%");
		configColumna(table, CAMPO_DR_LIMITE, "DR. Limite", false, false,
				"5%");
		configColumna(table, CAMPO_DR_COMPUTODIAS, "Comp. D&iacuteas", false, false,
				"5%");
		configColumnaFecha(table, CAMPO_DR_FECHAENTVIGOR,
				"Fecha Ent. Vigor", false, false, "5%", "dd/MM/yyyy");
		configColumnaFecha(table, CAMPO_DR_FECHAPAGO,
				"Fecha Pago", false, false, "20%", "dd/MM/yyyy");
		configColumnaFecha(table, CAMPO_DR_FECHARECEPCION,
				"Fecha Recepcion", false, false, "5%", "dd/MM/yyyy");


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
	
	private void configColumnaFecha(HtmlTable table, String idCol,
			String title, boolean filterable, boolean sortable, String width,
			String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Añade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(
					new DateCellEditor("dd/MM/yyyy"));
		} catch (Exception e) {
			logger.error(
					"Ocurrió un error al configurar el formato de fecha de la columna "
							+ idCol, e);
		}
	}

	/**
	 * Carga las columnas a mostrar en el listado en el TableFacade
	 * 
	 * @param tableFacade
	 */
	@SuppressWarnings("all")
	private void cargarColumnas(TableFacade tableFacade) {

		// Configura el TableFacade con las columnas que se quieren mostrar
		tableFacade.setColumnProperties(CAMPO_ID,CAMPO_LINEA,CAMPO_REFERENCIA, CAMPO_DC,CAMPO_CODINTERNO,
				CAMPO_TIPORECIBO,CAMPO_TIPOREFERENCIA,CAMPO_GASTOSEXTENT,CAMPO_COMISIONES,CAMPO_GRUPONEGOCIO,
				CAMPO_COL_REFERENCIA,CAMPO_COL_DC,CAMPO_COL_CODINTERNO,CAMPO_DR_FECHAENTVIGOR,CAMPO_DR_FECHAPAGO,
				CAMPO_DR_FECHARECEPCION,CAMPO_DR_LIMITE,CAMPO_DR_COMPUTODIAS	
				);

	}
	private void cargarFiltrosBusqueda(ReglamentoProduccionEmitida reglamentoProduccionEmitida,
			TableFacade tableFacade) {
		if (FiltroUtils.noEstaVacio(reglamentoProduccionEmitida.getFichero().getId()))
			tableFacade.getLimit().getFilterSet().addFilter(
					"fichero.id",
					reglamentoProduccionEmitida.getFichero().getId().toString());

	}
	
	private void setDataAndLimitVariables(TableFacade tableFacade,
			ReglamentoProduccionEmitida reglamentoProduccionEmitida) {

		Limit limit = tableFacade.getLimit();
		ReglamentoFilter reglamentoFilter = getReglamentoFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getFicheroReglamentoCountWithFilter(reglamentoFilter,
				reglamentoProduccionEmitida);
		logger
				.debug("********** Número de filas  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		ReglamentoSort reglamentoSort = getReglamentoSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<ReglamentoProduccionEmitida> items = new ArrayList<ReglamentoProduccionEmitida>();

		try {
			items = getFicheroReglamentoWithFilterAndSort(reglamentoFilter,
					reglamentoSort, rowStart, rowEnd, reglamentoProduccionEmitida);
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
	private ReglamentoFilter getReglamentoFilter(Limit limit) {
		ReglamentoFilter reglamentoFilter = new ReglamentoFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			reglamentoFilter.addFilter(property, value);
		}
		return reglamentoFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de datos del fichero
	 * 
	 * @param limit
	 * @return
	 */
	private ReglamentoSort getReglamentoSort(Limit limit) {
		ReglamentoSort reglamentoSort = new ReglamentoSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			reglamentoSort.addSort(property, order);
		}

		return reglamentoSort;
	}

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				String idReglamento = new BasicCellEditor().getValue(item,
						CAMPO_ID_REGLAMENTO, rowcount).toString();
				HtmlBuilder html = new HtmlBuilder();
				
				// botón consulta
				html.a().href().quote().append(
						"javascript:consultar('" + idReglamento + "');").quote()
						.close();
				html
						.append("<img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/>");
				html.aEnd();
				html.append("&nbsp;");
				
				return html.toString();
			}
		};
	}
	public void setReglamentoDao(IReglamentoDao reglamentoDao) {
		this.reglamentoDao = reglamentoDao;
	}

	public void setId(String id) {
		this.id = id;
	}



	

}
