package com.rsi.agp.core.jmesa.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.facade.TableFacade;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.component.HtmlTable;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.sort.IGenericoSort;

public interface IGetTablaService {
	String getTabla(HttpServletRequest request,
			HttpServletResponse response, java.io.Serializable objetoBean,
			String origenLlamada,List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao);
	
	
	public Collection<Serializable> getWithFilterAndSort(
			CriteriaCommand filter, CriteriaCommand sort, int rowStart,
			int rowEnd, IGenericoDao genericoDao) throws BusinessException;
	
	int getCountWithFilter(CriteriaCommand filter, IGenericoDao genericoDao)
			throws BusinessException;
	
	TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, 
			String origenLlamada, HashMap<String, String> columnas);
	
	//public void addColumnaFiltro( TableFacade tableFacade, String columna, Object valor);
	public void addColumnaFiltro( TableFacade tableFacade, String columna, Object valor, 
			 IGenericoFilter filtro);
	
	public void addListaEntidadesFilter(List<BigDecimal> listaGrupoEntidades, 
			IGenericoFilter filtro, String campoListaEntidades);
	
	public void configColumna(HtmlTable table, String idCol, String title,
			boolean filterable, boolean sortable, String width) ;
	
	public void configColumnaFecha(HtmlTable table, String idCol,
			String title, boolean filterable, boolean sortable, String width,
			String fFecha);
	
	public void setDataAndLimitVariables(TableFacade tableFacade, 
			CriteriaCommand filtro, IGenericoDao genericoDao,
			IGenericoSort genericoSort, CriteriaCommand sort);
	
	public CellEditor getCellEditorFecha(final String Campo);
	public CellEditor getCellEditorNulos();
	
	public List<Serializable> getAllFilteredAndSorted(IGenericoDao genericoDao) throws BusinessException;
	
}
