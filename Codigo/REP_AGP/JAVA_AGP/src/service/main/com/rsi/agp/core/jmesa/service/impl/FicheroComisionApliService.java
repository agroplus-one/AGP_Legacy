package com.rsi.agp.core.jmesa.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import com.rsi.agp.core.jmesa.filter.ComisionApliFilter;
import com.rsi.agp.core.jmesa.service.IFicheroComisionApliService;
import com.rsi.agp.core.jmesa.sort.ComisionApliSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.comisiones.IComisionDao;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacion;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacionCondicionParticular;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionCondicionParticular;


public class FicheroComisionApliService implements IFicheroComisionApliService{
	
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private IComisionDao comisionDao;
	
	public static final String CAMPO_ID               ="id";
	public static final String CAMPO_ID_COMISION_APLI ="id";
	public static final String CAMPO_ID_COMISION      ="comision.id";
	public static final String CAMPO_DC               ="dc";
	public static final String CAMPO_REFERENCIA	      ="referencia";
	public static final String CAMPO_CODIGOINTERNO    ="codinterno";   
	public static final String CAMPO_TIPOREFERENCIA	  ="tiporeferencia";
	public static final String CAMPO_ANULADAREFUNDIDA ="anuladarefundida";
	public static final String CAMPO_RAZONSOCIAL	  ="razonsocial";
	 
	
	 public int getFicheroComisionApliCountWithFilter(ComisionApliFilter filter,
			 ComisionAplicacion comisionApli) {
		 return comisionDao.getFicheroComisionApliCountWithFilter(filter,
				 comisionApli);
	}
	
	public Collection<ComisionAplicacion> getFicheroComisionApliWithFilterAndSort(
			ComisionApliFilter filter, ComisionApliSort sort, int rowStart,
			int rowEnd, ComisionAplicacion comisionApli) throws BusinessException {

		return comisionDao.getFicheroComisionApliWithFilterAndSort(filter, sort,
				rowStart, rowEnd, comisionApli);
	}
	
	
	@Override
	public String getTablaComisionesApli(HttpServletRequest request,
			HttpServletResponse response, ComisionAplicacion comisionApli,
			String origenLlamada) {
		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				comisionApli, origenLlamada);

		setDataAndLimitVariables(tableFacade, comisionApli);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade,origenLlamada);
	}
	
	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			ComisionAplicacion comisionBean,
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
						"listadoComisionApli_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoComisionApli_LIMIT"));
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
		configColumna(table, CAMPO_DC, "DC",false, false,
				"5%");
		configColumna(table, CAMPO_REFERENCIA, "Referencia", true, true,
				"5%");
		configColumna(table, CAMPO_TIPOREFERENCIA, "Tipo Referencia", false, false,
				"5%");
		configColumna(table, CAMPO_CODIGOINTERNO, "C&oacutedigo Interno", false, false,
				"5%");
		configColumna(table, CAMPO_ANULADAREFUNDIDA, "Anulada Refundida", false, false,
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
		tableFacade.setColumnProperties(CAMPO_ID,CAMPO_DC,CAMPO_REFERENCIA, CAMPO_TIPOREFERENCIA,CAMPO_CODIGOINTERNO,
				CAMPO_ANULADAREFUNDIDA,CAMPO_RAZONSOCIAL
				);

	}
	private void cargarFiltrosBusqueda(ComisionAplicacion comisionApli,
			TableFacade tableFacade) {
		
		tableFacade.getLimit().getFilterSet().addFilter(
				"comision.id",
				comisionApli.getComision().getId().toString());

	}
	
	private void setDataAndLimitVariables(TableFacade tableFacade,
			ComisionAplicacion comisionApli) {

		// Obtiene el Filter para la búsqueda de ficheros
		Limit limit = tableFacade.getLimit();
		ComisionApliFilter comisionapliFilter = getComisionApliFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getFicheroComisionApliCountWithFilter(comisionapliFilter,
				comisionApli);
		logger
				.debug("********** Número de filas  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		ComisionApliSort comisionSort = getComisionApliSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<ComisionAplicacion> items = new ArrayList<ComisionAplicacion>();
		
		try {
			items = getFicheroComisionApliWithFilterAndSort(comisionapliFilter,
					comisionSort, rowStart, rowEnd, comisionApli);
			logger.debug("********** Items de la lista   = "
					+ items.size() + " **********");
			for (Iterator<ComisionAplicacion> iterator = items.iterator(); iterator.hasNext();) {
				ComisionAplicacion c = (ComisionAplicacion) iterator.next();
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
	private ComisionApliFilter getComisionApliFilter(Limit limit) {
		ComisionApliFilter comisionapliFilter = new ComisionApliFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			comisionapliFilter.addFilter(property, value);
		}
		return comisionapliFilter;
	}

	/**
	 * Crea y configura el Sort para la consulta de datos del fichero
	 * 
	 * @param limit
	 * @return
	 */
	private ComisionApliSort getComisionApliSort(Limit limit) {
		ComisionApliSort comisionapliSort = new ComisionApliSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			comisionapliSort.addSort(property, order);
		}

		return comisionapliSort;
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
				String idComisionApli = new BasicCellEditor().getValue(item,
						CAMPO_ID_COMISION_APLI, rowcount).toString();
				HtmlBuilder html = new HtmlBuilder();
				
				// botón consulta
				html.a().href().quote().append(
						"javascript:detalle('" + idComision + "','"+idComisionApli+"');").quote()
						.close();
				html
						.append("<img src=\"jsp/img/magnifier.png\" alt=\"Detalle\" title=\"Detalle\"/>");
				html.aEnd();
				html.append("&nbsp;");
				
				return html.toString();
			}
		};
	}
	
	@Override
	public ComisionAplicacion getDatosComisiones(Long idComision) throws BusinessException {
		try{
			return (ComisionAplicacion) comisionDao.getObject(ComisionAplicacion.class, idComision);
		
		}catch (Exception e) {
			logger.error("Error al obtener los datos de comisiones de base de datos");
			throw new BusinessException();
		} 
	}
	@SuppressWarnings("unchecked")
	@Override
	public String getMarcaCondComisiones(Long idComision) throws BusinessException {
		List<ComisionCondicionParticular> list =  null;
		String marcasComi="";
		try {
			list =  (List<ComisionCondicionParticular>) comisionDao.getObjects(ComisionCondicionParticular.class,"comision.id", idComision);
			for (int i=0;i<list.size();i++){
				ComisionCondicionParticular c = list.get(i);
				if (c.getMarcacondiciones() != null)
					if (i==0)
						marcasComi = c.getMarcacondiciones().toString();
					else
						marcasComi += "," +c.getMarcacondiciones().toString();
				
			}
		} catch (Exception e) {
			logger.error("Error al obtener los datos de comisiones de base de datos");
			throw new BusinessException();
		}
		return marcasComi;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getMarcaCondComisionesApli(Long idComisionApli) throws BusinessException {
		List<ComisionAplicacionCondicionParticular> list =  null;
		String marcasApli = "";
		try {
			list = (List<ComisionAplicacionCondicionParticular>) comisionDao.getObjects(ComisionAplicacionCondicionParticular.class,"comisionAplicacion.id", idComisionApli);
			for (int i=0;i<list.size();i++){
				ComisionAplicacionCondicionParticular c = list.get(i);
				if (c.getMarcacondiciones() != null)
					if (i==0)
						marcasApli = c.getMarcacondiciones().toString();
					else
						marcasApli += "," +c.getMarcacondiciones().toString();
				
			}
		} catch (Exception e) {
			logger.error("Error al obtener los datos de comisiones de base de datos");
			throw new BusinessException();
		}
		return marcasApli;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setComisionDao(IComisionDao comisionDao) {
		this.comisionDao = comisionDao;
	}
	
	

}
