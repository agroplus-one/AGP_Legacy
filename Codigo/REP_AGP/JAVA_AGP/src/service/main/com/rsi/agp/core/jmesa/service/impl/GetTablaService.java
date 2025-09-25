package com.rsi.agp.core.jmesa.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.Limit;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.service.IGetTablaService;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;
import com.rsi.agp.dao.filters.BigDecimalFilterMatcher;
import com.rsi.agp.dao.filters.DateFromFilterMatcher;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.StringLikeFilterMatcher;

public class GetTablaService implements IGetTablaService {
	protected HashMap<String, String> columnas = new HashMap<String, String>();
	protected String id;
	private Log logger = LogFactory.getLog(getClass());
	
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			CriteriaCommand filter, CriteriaCommand sort, int rowStart,
			int rowEnd, IGenericoDao genericoDao) throws BusinessException {
		return genericoDao.getWithFilterAndSort(filter, sort,
				rowStart, rowEnd);
	}
	

	@Override
	public int getCountWithFilter(CriteriaCommand filter,
			IGenericoDao genericoDao) throws BusinessException {
		
		return genericoDao.getCountWithFilter(filter);		
	}

	@Override
	public TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, 
			String origenLlamada, HashMap<String, String> columnas) {
	
		TableFacade tableFacade=new TableFacade(this.id,request);

		tableFacade.setStateAttr("restore");// return to the table in the same
											// state that the user left it.
		tableFacade.addFilterMatcher(new MatcherKey(String.class),
				new StringLikeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class),
				new DateFromFilterMatcher("d/M/yyyy"));
		tableFacade.addFilterMatcher(new MatcherKey(BigDecimal.class),
				new BigDecimalFilterMatcher());
		// Si no es una llamada a traves de ajax
		if (request.getParameter("ajax") == null) {
			if (origenLlamada == null) {
				if (request.getSession().getAttribute(
						"consulta_LIMIT") != null) {
					// Si venimos por aqui es que ya hemos pasado por el filtro
					// en algun momento
					tableFacade.setLimit((Limit) request.getSession()
							.getAttribute("consulta_LIMIT"));
				}
			} 
		}

		return tableFacade;
	}

	/*@Override
	public void addColumnaFiltro(TableFacade tableFacade, String columna,Object valor) {
		tableFacade.getLimit().getFilterSet().addFilter(new Filter(columnas.get(columna), valor.toString()));			
	}*/
	
	@Override
	public void addColumnaFiltro(TableFacade tableFacade, String columna,
			Object valor, IGenericoFilter filtro) {
		
		String property = columnas.get(columna);		
		if(null!=tableFacade) {
			//Filtro para jmesa
			if(valor.getClass().getSimpleName().compareTo("Date")==0) {
				tableFacade.getLimit().getFilterSet().addFilter(new Filter(property,  new SimpleDateFormat("dd/MM/yyyy").format(valor)));
			}else {			
				tableFacade.getLimit().getFilterSet().addFilter(new Filter(property, valor.toString()));
			}
		}
		//Filtro para Dao
		filtro.addFilter(property, valor);
		
	}
	
	@Override
	public void addListaEntidadesFilter(List<BigDecimal> listaGrupoEntidades,
			IGenericoFilter filtro, String campoListaEntidades) {
		if (listaGrupoEntidades != null && listaGrupoEntidades.size() > 0) {
			filtro.addFilter( campoListaEntidades, listaGrupoEntidades);
		}
		
	}

	/*@Override
	public void getConsultaFilter(Limit limit, List<BigDecimal> listaGrupoEntidades, 
			IGenericoFilter filtro, String campoListaEntidades) {
		
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			Object value = filter.getValue();
			filtro.addFilter(property, value);
		}
		// Si la lista de grupos de entidades no esta vacia se incluye en el
		// filtro de busqueda
		
	}*/

	public void configColumna(HtmlTable table, String idCol, String title,
			boolean filterable, boolean sortable, String width) {
		
		configColumna(table, idCol, title,
				filterable,sortable,  width, new String());

	}
	
	public void configColumna(HtmlTable table, String idCol, String title,
			boolean filterable, boolean sortable, String width,String style) {
		
		table.getRow().getColumn(idCol).setTitle(title);
		table.getRow().getColumn(idCol).setFilterable(filterable);
		table.getRow().getColumn(idCol).setSortable(sortable);
		table.getRow().getColumn(idCol).setWidth(width);
		table.getRow().getColumn(idCol).setStyle(style);

	}
	
	@SuppressWarnings("deprecation")
	public void configColumnaFecha(HtmlTable table, String idCol,
			String title, boolean filterable, boolean sortable, String width,
			String fFecha) {
		// Configura los valores generales de la columna
		configColumna(table, idCol, title, filterable, sortable, width);
		// Añade el formato de la fecha a la columna
		try {
			table.getRow().getColumn(idCol).getCellRenderer().setCellEditor(new DateCellEditor(fFecha));
		} catch (Exception e) {
			logger.error("Ocurrio un error al configurar el formato de fecha de la columna " + idCol, e);
		}
	}
	
		
	public void setDataAndLimitVariables(TableFacade tableFacade, 
			CriteriaCommand filtro, IGenericoDao genericoDao,
			IGenericoSort genericoSort, CriteriaCommand sort) {

		Collection<java.io.Serializable> items = new ArrayList<java.io.Serializable>();
		// Obtiene el Filter para la busqueda de polizas
		Limit limit = tableFacade.getLimit();		

		try {
			int totalRows = genericoDao.getCountWithFilter(filtro);
			logger.debug("********** count filas  = " + totalRows + " **********");

			tableFacade.setTotalRows(totalRows);
			genericoSort.getConsultaSort(limit);			
			
			int rowStart = limit.getRowSelect().getRowStart();
			int rowEnd = limit.getRowSelect().getRowEnd();

			items=genericoDao.getWithFilterAndSort(filtro, sort, rowStart, rowEnd);
			tableFacade.setItems(items);
			logger.debug("********** lista de items   = "+ items.size() + " **********");
		} catch (BusinessException e) {
			logger.debug("setDataAndLimitVariables error. " + e.getMessage());
		}
		// Carga los registros obtenidos del bd en la tabla
		//tableFacade.setItems(items);

	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getTabla(HttpServletRequest request,
			HttpServletResponse response, Serializable objetoBean,
			String origenLlamada, List<BigDecimal> listaGrupoEntidades,
			IGenericoDao genericoDao) {

		return null;
	}

	public CellEditor getCellEditorFecha(final String Campo) {
		return new CellEditor() {
		    public Object getValue(Object item, String property, int rowcount) {
		    	
		    	String value = "";
		    			    	
				try {
					// Si hay fecha se formatea y se muestra, si no la tiene nos se muestra nada
					Date dateAux = (Date)new BasicCellEditor().getValue(item, columnas.get(Campo), rowcount);
					value = (dateAux == null) ? ("") : new SimpleDateFormat("dd/MM/yyyy").format(dateAux);
				} catch (Exception e) {
					logger.error("getCellEditorFecha - Ocurrio un error al obtener la fecha " + Campo , e);
				}
		    			    	
		        HtmlBuilder html = new HtmlBuilder();
		        //html.append (FiltroUtils.noEstaVacioSinEspacios(value) ? value : "&nbsp;");
		        html.append (FiltroUtils.noEstaVacioSinEspacios(value) ? value : "");
		        return html.toString();
		    }
		};
	}

	public CellEditor getCellEditorNulos() {
		return new CellEditor() {
			public Object getValue(Object item, String property, int rowcount) {
				
				String valor=(String)new BasicCellEditor().getValue(item, property, rowcount);
				String valorStr="";
				
				if (null==valor) {
					valorStr = "&nbsp;";
				} else {
					valorStr = valor.toString();
				}
				HtmlBuilder html = new HtmlBuilder();
				html.append(valorStr);
				return html.toString();
			}
		};
	}


	@Override
	public List<Serializable> getAllFilteredAndSorted(IGenericoDao genericoDao) throws BusinessException {
		return null;
	}
	
	
}
