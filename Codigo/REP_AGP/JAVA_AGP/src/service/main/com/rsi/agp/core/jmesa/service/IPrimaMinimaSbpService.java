package com.rsi.agp.core.jmesa.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.PrimaMinimaSbpFilter;
import com.rsi.agp.core.jmesa.sort.PrimaMinimaSbpSort;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public interface IPrimaMinimaSbpService {
	
	/**
	 * Devuelve el listado de primas mínimas ordenadas que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de las primas mínimas
	 * @param sort Ordenación para la búsqueda de los primas mínimas
	 * @param rowStart Primer registro que se mostrará
	 * @param rowEnd Último registro que se mostrará
	 * @return
	 * @throws BusinessException
	 */
	public Collection<PrimaMinimaSbp> getPrimaMinimaSbpWithFilterAndSort(PrimaMinimaSbpFilter filter, PrimaMinimaSbpSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el número de primas mínimas que se ajustan al filtro pasado por parámetro
	 * @param filter
	 * @return
	 */
	public int getPrimaMinimaSbpCountWithFilter(PrimaMinimaSbpFilter filter);

	// public String getTablaPrimaMinimaParaSbp (HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaSbp, List<Sobreprecio> lineas, String origenLlamada);
	public String getTablaPrimaMinimaParaSbp (HttpServletRequest request, HttpServletResponse response, PrimaMinimaSbp primaMinimaSbp, String origenLlamada);
	
	/**
	 * Obtiene una PrimaMinimaSbp
	 * @param Long idPrimaMinimaSbp
	 * @return PrimaMinimaSbp
	 * @throws BusinessException
	 */
	
	public Map altaPrimaMinimaSbp(PrimaMinimaSbp primaMinimaSbp) throws BusinessException;
		
	public PrimaMinimaSbp getPrimaMinimaSbp(Long idPrimaMinimaSbp) throws BusinessException;
			
	/**
	 * Baja de Prima Minima de sobreprecio
	 * @param primaMinimaSbp Objeto que encapsula los datos de la póliza de sobreprecio
	 * @throws BusinessException 
	 */
	public void bajaPrimaMinimaSbp(PrimaMinimaSbp primaMinimaSbp) throws BusinessException;
	
	/**
	 * Edición de Prima Minima de sobreprecio
	 * @param primaMinimaSbp Objeto que encapsula los datos de la Prima Minima de sobreprecio
	 * @throws BusinessException 
	 */
	public Map<String, Object> editarPrimaMinimaSbp(PrimaMinimaSbp primaMinimaSbpEdit) throws BusinessException;
}
