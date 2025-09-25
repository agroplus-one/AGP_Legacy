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
import com.rsi.agp.core.jmesa.filter.DeudaAplazadaFilter;
import com.rsi.agp.core.jmesa.service.IFicheroDeudaAplazadaService;
import com.rsi.agp.core.jmesa.sort.DeudaAplazadaSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.comisiones.IDeudaAplazadaDao;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DetalleAbonoPoliza;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;


public class FicheroDeudaAplazadaService implements IFicheroDeudaAplazadaService{
	
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private IDeudaAplazadaDao deudaAplazadaDao;
	
	public static final String CAMPO_ID              ="id";
	public static final String NOMBRE_FICHERO        ="nombreFichero";
	public static final String TIPO_FICHERO	         ="tipoFichero";
	public static final String FECHA_CARGA           ="fechaCarga";   
	public static final String FECHA_ACEPTACION  	 ="fechaAceptacion";
	public static final String CODUSUARIO   		 ="codusuario";
	public static final String FICHEROMULTCONTENIDO  ="ficheroMultContenido";
	
	
	
	
	 public int getFicheroDeudaAplazadaCountWithFilter(DeudaAplazadaFilter filter,
			 FicheroMult ficheroMult) {
		 return deudaAplazadaDao.getFicheroDeudaAplazadaCountWithFilter(filter,
				 ficheroMult);
	}
	
	public Collection<FicheroMult> getFicheroDeudaAplazadaWithFilterAndSort(
			DeudaAplazadaFilter filter, DeudaAplazadaSort sort, int rowStart,
			int rowEnd,  FicheroMult ficheroMult) throws BusinessException {

		return deudaAplazadaDao.getFicheroDeudaAplazadaWithFilterAndSort(filter, sort,
				rowStart, rowEnd, ficheroMult);
	}
	
	
	@Override
	public String getTablaDeudaAplazada(HttpServletRequest request,
			HttpServletResponse response, FicheroMult ficheroMult,
			String origenLlamada) {
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				ficheroMult, origenLlamada);

		setDataAndLimitVariables(tableFacade, ficheroMult);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade,origenLlamada);
	}
	
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			FicheroMult ficheroMult,
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

		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"listadoDeudaAplazada_LIMIT") != null) {
					// Si venimos por aqui� es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoDeudaAplazada_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(ficheroMult, tableFacade);

			}
		}

		return tableFacade;
	}
	/**
	 * Metodo para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String html(TableFacade tableFacade,String origenLlamada) {

		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty("id");
		
		// Configuracion de las columnas de la tabla
		configurarColumnas(table);

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			tableFacade.render(); // Will write the export data out to the
									// response.
			return null; // In Spring return null tells the controller not to
							// do anything.
		} else {
			// Configuracion de los datos de las columnas que requieren un
			// tratamiento para mostrarse
			// Acciones
			table.getRow().getColumn(CAMPO_ID).getCellRenderer()
					.setCellEditor(getCellEditorAcciones());
			
		}

		// Devuelve el html de la tabla
		return tableFacade.render();
	}
	/**
	 * Configuracion de las columnas de la tabla
	 * 
	 * @param table
	 */
	private void configurarColumnas(HtmlTable table) {
		// Acciones
		configColumna(table, CAMPO_ID, "&nbsp;&nbsp;Acciones",false, false,
				"4%");
		configColumna(table, NOMBRE_FICHERO, "Nombre fichero",true, true,
				"5%");
		configColumna(table, TIPO_FICHERO, "Tipo fichero", true, true,
				"5%");
		configColumna(table, FECHA_CARGA, "Fecha carga", false, false,
				"5%");
		configColumna(table, FECHA_ACEPTACION, "Fecha aceptaci�n", false, false,
				"4%");
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
		tableFacade.setColumnProperties(CAMPO_ID, NOMBRE_FICHERO, TIPO_FICHERO, FECHA_CARGA, FECHA_ACEPTACION
				);

	}
	private void cargarFiltrosBusqueda(FicheroMult deudaAplazada,
			TableFacade tableFacade) {/*
		if (FiltroUtils.noEstaVacio(deudaAplazada.getFichero().getId()))
			tableFacade.getLimit().getFilterSet().addFilter(
					"fichero.id",
					deudaAplazada.getFichero().getId().toString());
*/
	}
	
	private void setDataAndLimitVariables(TableFacade tableFacade,
			FicheroMult ficheroMult) {

		// Obtiene el Filter para la búsqueda de ficheros
		Limit limit = tableFacade.getLimit();
		DeudaAplazadaFilter deudaAplazaFilter = getDeudaAplazadaFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getFicheroDeudaAplazadaCountWithFilter(deudaAplazaFilter,
				ficheroMult);
		logger
				.debug("********** Numero de filas  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		DeudaAplazadaSort deudaAplazaSort = getDeudaAplazadaSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<FicheroMult> items = new ArrayList<FicheroMult>();
		
		try {
			items = getFicheroDeudaAplazadaWithFilterAndSort(deudaAplazaFilter,
					deudaAplazaSort, rowStart, rowEnd, ficheroMult);
			logger.debug("********** Items de la lista   = "
					+ items.size() + " **********");
			for (Iterator<FicheroMult> iterator = items.iterator(); iterator.hasNext();) {
				FicheroMult c = (FicheroMult) iterator.next();
				/*
				if (c.getRazonsocial()== null)
	        		c.setRazonsocial(c.getNombre()+" " +c.getApellido1()+" "+ c.getApellido2());
	        		*/
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
	private DeudaAplazadaFilter getDeudaAplazadaFilter(Limit limit) {
		DeudaAplazadaFilter deudaAplazaFilter = new DeudaAplazadaFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			deudaAplazaFilter.addFilter(property, value);
		}
		return deudaAplazaFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de datos del fichero
	 * 
	 * @param limit
	 * @return
	 */
	private DeudaAplazadaSort getDeudaAplazadaSort(Limit limit) {
		DeudaAplazadaSort deudaAplazaSort = new DeudaAplazadaSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			deudaAplazaSort.addSort(property, order);
		}

		return deudaAplazaSort;
	}

	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				String idEmitido= new BasicCellEditor().getValue(item,
						CAMPO_ID, rowcount).toString();
				HtmlBuilder html = new HtmlBuilder();
				
				// botón consulta
				html.a().href().quote().append(
						"javascript:consultar('" + idEmitido + "');").quote()
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

	public void setDeudaAplazadaDao(IDeudaAplazadaDao deudaAplazaDao) {
		this.deudaAplazadaDao = deudaAplazaDao;
	}

	
	
	

}
