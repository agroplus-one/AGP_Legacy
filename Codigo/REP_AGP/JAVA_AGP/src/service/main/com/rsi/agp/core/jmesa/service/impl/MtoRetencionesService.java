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
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IMtoRetencionesDao;
import com.rsi.agp.core.jmesa.filter.MtoRetencionesFilter;
import com.rsi.agp.core.jmesa.service.IMtoRetencionesService;
import com.rsi.agp.core.jmesa.sort.MtoRetencionesSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;
import com.rsi.agp.dao.tables.comisiones.Retencion;

@SuppressWarnings("deprecation")
public class MtoRetencionesService implements IMtoRetencionesService {
	
	private IMtoRetencionesDao mtoRetencionesDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private Log logger = LogFactory.getLog(getClass());
	private HashMap<String, String> columnas = new HashMap<String, String>();
	private String id;

	// Constantes para los nombres de las columnas del listado
	private final static String ID_STR = "id";
	private final static String ANYO = "anyo";
	private final static String RETENCION = "retencion";

	@Override
	public Collection<Retencion> getRetencionesWithFilterAndSort(
			MtoRetencionesFilter filter, MtoRetencionesSort sort, int rowStart,
			int rowEnd) throws BusinessException {
		return mtoRetencionesDao.getRetencionesWithFilterAndSort(filter, sort,
				rowStart, rowEnd);

	}

	@Override
	public int getRetencionesCountWithFilter(MtoRetencionesFilter filter)
			throws BusinessException {
		return mtoRetencionesDao.getRetencionesCountWithFilter(filter);
	}

	@Override
	public String getTablaRetenciones(HttpServletRequest request,
			HttpServletResponse response, Retencion retencionBean,
			String origenLlamada, List<BigDecimal> listaGrupoEntidades) {
		
		TableFacade tableFacade = crearTableFacade(request, response,
				retencionBean, origenLlamada);

		setDataAndLimitVariables(tableFacade, listaGrupoEntidades);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());
		
		return html (tableFacade);// + script;
	}

	/**
	 * Crea y configura el objeto TableFacade que encapsulara la tabla
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, Retencion retencion,
			String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,request);

		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		tableFacade.addFilterMatcher(new MatcherKey(String.class), new StringLikeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class), new DateFromFilterMatcher("d/M/yyyy"));
		tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class), new BigDecimalFilterMatcher());
		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute("consultaRetenciones_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro en algun momento
					tableFacade.setLimit((Limit) request.getSession().getAttribute("consultaRetenciones_LIMIT"));
				}
			} else {
				// Carga en el TableFacade los filtros de busqueda introducidos en el formulario
				cargarFiltrosBusqueda(retencion, tableFacade);
			}
		}
		return tableFacade;
	}
	
	/**
	 * Carga en el TableFacade los filtros de busqueda introducidos en el formulario
	 * 
	 * @param columnas2
	 * @param usuario
	 * @param tableFacade
	 */
	private void cargarFiltrosBusqueda(Retencion retencion, TableFacade tableFacade) {
		if (retencion != null) {
		// EJERCICIO
		if (retencion.getAnyo() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
							new Filter(columnas.get(ANYO), retencion.getAnyo().toString()));
		// RETENCION
		if (retencion.getRetencion() != null)
			tableFacade.getLimit().getFilterSet().addFilter(
							new Filter(columnas.get(RETENCION), retencion.getRetencion().toString()));
		}
	}

	/**
	 * Crea los objetos de filtro y ordenacion, llama al dao para obtener los
	 * datos de Descuentos y carga el TableFacade con ellas
	 * 
	 * @param tableFacade
	 * @param consultaFilter
	 * @param limit
	 */
	private void setDataAndLimitVariables(TableFacade tableFacade,
			List<BigDecimal> listaGrupoEntidades) {

		Collection<Retencion> items = new ArrayList<Retencion>();
		// Obtiene el Filter para la busqueda de polizas
		Limit limit = tableFacade.getLimit();
		MtoRetencionesFilter consultaFilter = getConsultaRetencionesFilter(limit,listaGrupoEntidades);

		try {
			int totalRows = getRetencionesCountWithFilter(consultaFilter);
			logger.debug("********** count filas para Retenciones  = "+ totalRows + " **********");

			tableFacade.setTotalRows(totalRows);

			MtoRetencionesSort consultaSort = getConsultaRetencionesSort(limit);
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items = getRetencionesWithFilterAndSort(consultaFilter,consultaSort, rowStart, rowEnd);
			logger.debug("********** list items para Retenciones  = "+ items.size() + " **********");
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
	private MtoRetencionesFilter getConsultaRetencionesFilter(Limit limit,
			List<BigDecimal> listaGrupoEntidades) {
		MtoRetencionesFilter consultaFilter = new MtoRetencionesFilter();
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
	 * Crea y configura el Sort para la consulta de usuarios U029769
	 * 
	 * @param limit
	 * @return
	 */
	private MtoRetencionesSort getConsultaRetencionesSort(Limit limit) {
		MtoRetencionesSort consultaSort = new MtoRetencionesSort();
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
	 * Metodo para construir el html de la tabla a mostrar
	 * 
	 * @param tableFacade
	 * @return
	 */
	private String html(TableFacade tableFacade) {

		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.getRow().setUniqueProperty(ANYO);

		configurarColumnas(table);

		Limit limit = tableFacade.getLimit();
		if (limit.isExported()) {
			tableFacade.render(); // Will write the export data out to the response.
			return null; // In Spring return null tells the controller not to do anything.
		} else {

			// tratamiento para mostrarse como campo acciones
			table.getRow().getColumn(columnas.get(ID_STR)).getCellRenderer().setCellEditor(getCellEditorAcciones());
		}

		return tableFacade.render();
	}

	
	private CellEditor getCellEditorAcciones() {

		return new CellEditor() {

			public Object getValue(Object item, String property, int rowcount) {
				HtmlBuilder html = new HtmlBuilder();
				StringBuilder funcion = new StringBuilder();
				Integer anyo 	     = (Integer) new BasicCellEditor().getValue(item, "anyo", rowcount);
				BigDecimal retencion = (BigDecimal) new BasicCellEditor().getValue(item, "retencion", rowcount);
				
				funcion.append(anyo != null ? anyo : "''")
				.append(",")
				.append(retencion != null ? retencion : "''")
				.append(")");
				
				// boton Editar
				StringBuilder funcionEdita = new StringBuilder();
				funcionEdita.append("javascript:modificar( ").append(funcion);
				html.a().href().quote().append(funcionEdita).quote().close();
				html.append("<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar Retención\" title=\"Editar Retención\"/>");
				html.aEnd();
				html.append("&nbsp;");

				// boton Borrar
				StringBuilder funcionBorra = new StringBuilder();
				funcionBorra.append("javascript:borrar( ").append(funcion);
				html.a().href().quote().append(funcionBorra).quote().close();
				html.append("<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar Retención\" title=\"Borrar Retención\"/>");
				html.aEnd();
				html.append("&nbsp;");
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
		configColumna(table, columnas.get(ID_STR), "&nbsp;&nbsp;Acciones", false,false, "33%");
		configColumna(table, columnas.get(ANYO), "Ejercicio", true, true, "33%");
		configColumna(table, columnas.get(RETENCION), "Retención",true, true, "33%");
	}

	/**
	 * Configura la columna de la tabla identificada por 'idCol' con los valores
	 * pasados como parametro y los incluye en la tabla 
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


	@Override
	public Map<String, Object> validaAltaModificacion(Retencion ret)
			throws Exception {

		Map<String, Object> errores = new HashMap<String, Object>();
		try {			
			if (mtoRetencionesDao.existeRegistro(ret.getAnyo(),ret.getRetencion())) {
				errores.put("alerta", bundle.getString("mensaje.mtoDescuentos.existeRegistro"));
			}
		} catch (Exception e) {
			logger.error("Ocurrio un error al validar el registro " + e);
			throw e;
		}
		return errores;
	}

	@Override
	public void guardaRegistro(Retencion retencionBean) throws Exception {
		try {
			mtoRetencionesDao.saveOrUpdate(retencionBean);

		} catch (Exception e) {
			logger.error("Ocurrio un error al dar de alta el registro " + e);
			throw e;
		}
	}
		
	@Override
	public void borraRegistro(Retencion retencionBean) throws Exception {
		try {
			
			mtoRetencionesDao.delete(retencionBean);

		} catch (Exception e) {
			logger.error("Ocurrio un error al hacer borrado físico del registro "
					+ e);
			throw e;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMtoRetencionesDao(IMtoRetencionesDao mtoRetencionesDao) {
		this.mtoRetencionesDao = mtoRetencionesDao;
	}

	

}
