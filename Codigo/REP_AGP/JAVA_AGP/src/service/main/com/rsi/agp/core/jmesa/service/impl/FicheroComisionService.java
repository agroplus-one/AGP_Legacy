package com.rsi.agp.core.jmesa.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ComisionFilter;
import com.rsi.agp.core.jmesa.service.IFicheroComisionService;
import com.rsi.agp.core.jmesa.sort.ComisionSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.comisiones.IComisionDao;
import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;


public class FicheroComisionService implements IFicheroComisionService{
	
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private IComisionDao comisionDao;
	
	public static final String CAMPO_ID              ="id";
	public static final String CAMPO_ID_COMISION     ="id";
	public static final String CAMPO_LINEA	         ="linea";
	public static final String CAMPO_RECIBO          ="recibo";   
	public static final String CAMPO_LOCALIDAD		 ="localidad";
	public static final String CAMPO_GRUPONEGOCIO	 ="gruponegocio";
	public static final String CAMPO_RAZONSOCIAL     ="razonsocial";
	public static final String CAMPO_NOMBRE          ="nombre";
	public static final String CAMPO_APELLIDO1        ="apellido1";
	public static final String CAMPO_APELLIDO2        ="apellido2";
	
	
	
	 public int getFicheroComisionCountWithFilter(ComisionFilter filter,
			 Comision comision) {
		 return comisionDao.getFicheroComisionCountWithFilter(filter,
				 comision);
	}
	
	public Collection<Comision> getFicheroComisionWithFilterAndSort(
			ComisionFilter filter, ComisionSort sort, int rowStart,
			int rowEnd, Comision comision) throws BusinessException {

		return comisionDao.getFicheroComisionWithFilterAndSort(filter, sort,
				rowStart, rowEnd, comision);
	}
	
	
	@Override
	public String getTablaComisiones(HttpServletRequest request,
			HttpServletResponse response, Comision comisionBean,
			String origenLlamada) {
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				comisionBean, origenLlamada);
		
		setDataAndLimitVariables(tableFacade, comisionBean);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade,origenLlamada);
	}
	
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			Comision comisionBean,
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
						"listadoComision_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoComision_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de búsqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(comisionBean, tableFacade);

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
		configColumna(table, CAMPO_RECIBO, "Recibo", true, true,
				"5%");
		configColumna(table, CAMPO_LOCALIDAD, "Localidad", false, false,
				"15%");
		configColumna(table, CAMPO_GRUPONEGOCIO, "Grupo Negocio", false, false,
				"4%");
		configColumna(table,CAMPO_RAZONSOCIAL, "Razon Social/Nombre apellidos", false, false,
				"20%");
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
		tableFacade.setColumnProperties(CAMPO_ID,CAMPO_LINEA,CAMPO_RECIBO, CAMPO_LOCALIDAD,CAMPO_GRUPONEGOCIO,
				CAMPO_RAZONSOCIAL
				);

	}
	private void cargarFiltrosBusqueda(Comision comision,
			TableFacade tableFacade) {
		if (FiltroUtils.noEstaVacio(comision.getFichero().getId()))
			tableFacade.getLimit().getFilterSet().addFilter(
					"fichero.id",
					comision.getFichero().getId().toString());

	}
	
	private void setDataAndLimitVariables(TableFacade tableFacade,
			Comision comision) {

		// Obtiene el Filter para la búsqueda de ficheros
		Limit limit = tableFacade.getLimit();
		ComisionFilter comisionFilter = getComisionFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getFicheroComisionCountWithFilter(comisionFilter,
				comision);
		logger
				.debug("********** Número de filas  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		ComisionSort comisionSort = getComisionSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<Comision> items = new ArrayList<Comision>();
		
		try {
			items = getFicheroComisionWithFilterAndSort(comisionFilter,
					comisionSort, rowStart, rowEnd, comision);
			logger.debug("********** Items de la lista   = "
					+ items.size() + " **********");
			for (Iterator<Comision> iterator = items.iterator(); iterator.hasNext();) {
				Comision c = (Comision) iterator.next();
				if (c.getRazonsocial()== null)
	        		c.setRazonsocial(c.getNombre()+" " +c.getApellido1()+" "+ c.getApellido2());
			}
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
	private ComisionFilter getComisionFilter(Limit limit) {
		ComisionFilter comisionFilter = new ComisionFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			comisionFilter.addFilter(property, value);
		}
		return comisionFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de datos del fichero
	 * 
	 * @param limit
	 * @return
	 */
	private ComisionSort getComisionSort(Limit limit) {
		ComisionSort comisionSort = new ComisionSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			comisionSort.addSort(property, order);
		}

		return comisionSort;
	}

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				String idComision = new BasicCellEditor().getValue(item,
						CAMPO_ID_COMISION, rowcount).toString();
				HtmlBuilder html = new HtmlBuilder();
				
				// botón consulta
				html.a().href().quote().append(
						"javascript:consultar('" + idComision + "');").quote()
						.close();
				html
						.append("<img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/>");
				html.aEnd();
				html.append("&nbsp;");
				
				return html.toString();
			}
		};
	}
	public void setId(String id) {
		this.id = id;
	}

	public void setComisionDao(IComisionDao comisionDao) {
		this.comisionDao = comisionDao;
	}
	
	

}
