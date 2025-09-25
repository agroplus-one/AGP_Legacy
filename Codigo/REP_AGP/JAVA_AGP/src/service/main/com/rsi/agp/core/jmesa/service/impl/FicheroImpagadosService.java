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
import com.rsi.agp.core.jmesa.filter.ImpagadosFilter;
import com.rsi.agp.core.jmesa.service.IFicheroImpagadosService;
import com.rsi.agp.core.jmesa.sort.ImpagadosSort;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringFilterMatcher;
import com.rsi.agp.dao.models.comisiones.IFicheroImpagadosDao;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;

public class FicheroImpagadosService implements IFicheroImpagadosService{
	private String id;
	private Log logger = LogFactory.getLog(getClass());
	private IFicheroImpagadosDao ficheroImpagadosDao;
	
	
	 public static final String CAMPO_ID               ="id";
	 public static final String CAMPO_RECIBO           ="recibo";
	 public static final String CAMPO_PLAN             ="plan";
	 public static final String CAMPO_LINEA	           ="linea";
	 public static final String CAMPO_GRUPONEGOCIO     ="gruponegocio"; 
	 public static final String CAMPO_RAZONSOCIAL      ="razonsocial";   
	 public static final String CAMPO_NOMBRE		   ="nombre";
	 public static final String CAMPO_APELLIDO1        ="apellido1";
	 public static final String CAMPO_APELLIDO2		   ="apellido2";
	 

	public int getFicheroImpagadosCountWithFilter(ImpagadosFilter filter) {
		return ficheroImpagadosDao.getFicheroImpagadosCountWithFilter(filter);
	}

	public Collection<ReciboImpagado> getFicheroImpagadosWithFilterAndSort(
			ImpagadosFilter filter, ImpagadosSort sort, int rowStart,
			int rowEnd) throws BusinessException {

		return ficheroImpagadosDao.getFicheroImpagadosWithFilterAndSort(filter,
				sort, rowStart, rowEnd);
	}
	
	public String getTablaImpagados(HttpServletRequest request,
			HttpServletResponse response,
			ReciboImpagado reciboImpagado, String origenLlamada) {

		// Crea el TableFacade
		TableFacade tableFacade = crearTableFacade(request, response,
				reciboImpagado, origenLlamada);
		setDataAndLimitVariables(tableFacade);

		tableFacade.setToolbar(new CustomToolbar());
		tableFacade.setView(new CustomView());

		// Genera el html de la tabla y lo devuelve
		return html(tableFacade);
	}

	@SuppressWarnings("deprecation")
	private TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response,
			ReciboImpagado reciboImpagado, String origenLlamada) {

		// Crea el objeto TableFacade
		TableFacade tableFacade = TableFacadeFactory.createTableFacade(id,
				request);
		tableFacade.setStateAttr("restore");
		
		// Carga las columnas a mostrar en el listado en el TableFacade
		tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringFilterMatcher());
		
		cargarColumnas(tableFacade);

		// Si no es una llamada a través de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"listadoFicheroImpagados_LIMIT") != null) {
					// Si venimos por aquí es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("listadoFicheroImpagados_LIMIT"));
				}else {
					cargarFiltrosBusqueda(reciboImpagado, tableFacade);
				}
			} else {
				// Carga en el TableFacade los filtros de búsqueda introducidos
				// en el formulario
				cargarFiltrosBusqueda(reciboImpagado, tableFacade);

				// -- ORDENACIÓN POR DEFECTO --> ORDER ASC -> linea
				//tableFacade.getLimit().getSortSet().addSort(
						//new Sort(3,CAMPO_LINEA,Order.DESC));
			}
		}

		return tableFacade;
	}
	
	@SuppressWarnings("deprecation")
	private void cargarColumnas(TableFacade tableFacade) {

		// Configura el TableFacade con las columnas que se quieren mostrar
		tableFacade.setColumnProperties(CAMPO_ID,
				CAMPO_RECIBO,
				CAMPO_PLAN,
				CAMPO_LINEA,
				CAMPO_GRUPONEGOCIO,
				CAMPO_RAZONSOCIAL
				);

	}
	
	private void cargarFiltrosBusqueda(ReciboImpagado reciboImpagado,
			TableFacade tableFacade) {
		
		if (FiltroUtils.noEstaVacio(reciboImpagado.getFichero().getId()))
			tableFacade.getLimit().getFilterSet().addFilter(
					"fichero.id",
					reciboImpagado.getFichero().getId().toString());
	}

	
	private void setDataAndLimitVariables(TableFacade tableFacade) {

		// Obtiene el Filter 
		Limit limit = tableFacade.getLimit();
		ImpagadosFilter ficheroImpagadosFilter = getFicheroImpagadosFilter(limit);

		// Obtiene el número de filas que cumplen el filtro
		int totalRows = getFicheroImpagadosCountWithFilter(ficheroImpagadosFilter);
		logger
				.debug("********** Número de filas para la búsqueda de impagados  = "
						+ totalRows + " **********");
		tableFacade.setTotalRows(totalRows);

		// Crea el Sort 
		ImpagadosSort ficheroImpagadosSort = getFicheroImpagadosSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		// Obtiene los registros que cumplen el filtro
		Collection<ReciboImpagado> items = new ArrayList<ReciboImpagado>();

		try {
			items = getFicheroImpagadosWithFilterAndSort(ficheroImpagadosFilter,
					ficheroImpagadosSort, rowStart, rowEnd);
			logger.debug("********** Items de la lista de impagados = "
					+ items.size() + " **********");
			for (Iterator<ReciboImpagado> iterator = items.iterator(); iterator.hasNext();) {
				ReciboImpagado c = (ReciboImpagado) iterator.next();
				if (c.getRazonsocial()== null)
	        		c.setRazonsocial(c.getNombre()+" " +c.getApellido1()+" "+ c.getApellido2());
			}
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		tableFacade.setItems(items);
	}
	
	private ImpagadosFilter getFicheroImpagadosFilter(Limit limit) {
		ImpagadosFilter impagadosFilter = new ImpagadosFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();

			logger.debug("property:" + property);
			logger.debug("value:" + value);

			impagadosFilter.addFilter(property, value);
		}
		return impagadosFilter;
	}
	
	
	private ImpagadosSort getFicheroImpagadosSort(Limit limit) {
		ImpagadosSort impagadosSort = new ImpagadosSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			impagadosSort.addSort(property, order);
		}

		return impagadosSort;
	}
	
	@SuppressWarnings("deprecation")
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
		configColumna(table,CAMPO_RECIBO, "Recibo", true,
				true, "5%");
		configColumna(table,CAMPO_PLAN, "Plan", true,
				true, "5%");
		configColumna(table,CAMPO_LINEA, "L&iacutenea", true,
				true, "5%");
		configColumna(table,CAMPO_GRUPONEGOCIO, "Grupo Negocio", false,
				false, "5%");
		configColumna(table,CAMPO_RAZONSOCIAL, "Razon Social/Nombre apellidos", false,
				false, "25%");
		
	}
	
	
	
	private void configColumna(HtmlTable table, String idCol, String title,
			boolean filterable, boolean sortable, String width) {
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
	}
	
	/**
	 * Devuelve el objeto que muestra la información de la columna 'Acciones'
	 * 
	 * @return
	 */
	private CellEditor getCellEditorAcciones() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {

				String recibo = new BasicCellEditor().getValue(item,
						CAMPO_RECIBO, rowcount).toString();
				String idImpagado = new BasicCellEditor().getValue(item,CAMPO_ID, rowcount).toString();
				HtmlBuilder html = new HtmlBuilder();
				
				//Boton ver detalle
				html.a().href().quote().append(
						"javascript:detalle('"+idImpagado+"');").quote()
						.close();
				html
						.append("<img src=\"jsp/img/displaytag/information.png\" alt=\"ver detalle\" title=\"ver detalle\"/>");
				html.aEnd();
				html.append("&nbsp;");
				
				return html.toString();
			}
		};
	}
	
	@Override
	public ReciboImpagado getDatosImpagados(Long idImpagado) throws BusinessException {
		try{
			return (ReciboImpagado) ficheroImpagadosDao.getObject(ReciboImpagado.class, idImpagado);
		
		}catch (Exception e) {
			logger.error("Error al obtener los datos de recibos impagados de base de datos");
			throw new BusinessException();
		} 
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFicheroImpagadosDao(IFicheroImpagadosDao ficheroImpagadosDao) {
		this.ficheroImpagadosDao = ficheroImpagadosDao;
	}
	

}
