package com.rsi.agp.core.jmesa.service.mtoinf;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.OperadorCampoGenericoFilter;
import com.rsi.agp.core.jmesa.sort.OperadorCampoGenericoSort;
import com.rsi.agp.core.util.OperadorInforme;
import com.rsi.agp.dao.tables.mtoinf.OperadorCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.Vista;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfOperadores;

public interface IMtoOperadorCamposGenericosService {

	/**
	 * Devuelve el listado de operadores de campos permitidos y calculados ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de los operadores de campos permitidos y calculados
	 * @param sort Ordenación para la búsqueda de los operadores de campos permitidos y calculados
	 * @param rowStart Primer registro que se mostrará
	 * @param rowEnd Último registro que se mostrará
	 * @return
	 */
	public String getTablaOperadorCampos(HttpServletRequest request, HttpServletResponse response, 
			OperadorCampoGenerico operadorCampoGenerico, String origenLlamada);
	
	/**
	 * Devuelve el número de operadores que se ajustan al filtro de búsqueda
	 * @param filter
	 * @return
	 */
	public int getOpGenericoCountWithFilter(OperadorCampoGenericoFilter filter);
		
	
	public Collection<VistaMtoinfOperadores> getOpGenericoWithFilterAndSort(
			OperadorCampoGenericoFilter filter, OperadorCampoGenericoSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	public List<Vista> getListadoVistas();
	
	public List<OperadorInforme> getListaOperadores();
}
