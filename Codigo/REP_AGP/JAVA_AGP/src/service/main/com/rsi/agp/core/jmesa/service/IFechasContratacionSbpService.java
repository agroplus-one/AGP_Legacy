package com.rsi.agp.core.jmesa.service;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.FechasContratacionSbpFilter;
import com.rsi.agp.core.jmesa.sort.FechasContratacionSbpSort;
import com.rsi.agp.dao.tables.sbp.FechaContratacionSbp;

public interface IFechasContratacionSbpService {
	/**
	 * Devuelve el listado de fechas de contratación ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de fechas de contratación
	 * @param sort Ordenación para la búsqueda fechas de contratación
	 * @param rowStart Primer registro que se muestra
	 * @param rowEnd Último registro que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<FechaContratacionSbp> getFechasContratacionSbpWithFilterAndSort(FechasContratacionSbpFilter filter, FechasContratacionSbpSort sort, int rowStart, int rowEnd) throws BusinessException;

	
	/**
	 * Devuelve el número de fechas de contratación que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public int getFechasContratacionSbpCountWithFilter(final FechasContratacionSbpFilter filter);
	
	public String getTablaFechasContratacionSbp (HttpServletRequest request, HttpServletResponse response, FechaContratacionSbp fechaContratacionSbp, String origenLlamada);
	
	/**
	 * Baja Fechas de contratacion de sobreprecio
	 * @param fechaContratacionSbp
	 * @throws BusinessException
	 */
	public void bajaFechasContratacionSbp(FechaContratacionSbp fechaContratacionSbp)throws BusinessException;
		
	/**
	 * Modifica la tabla de fechas de contratacion de sobreprecio
	 * @param fechaContratacionSbp
	 * @throws BusinessException
	 */
	public Map<String, Object> editaFechasContratacionSbp(FechaContratacionSbp fechaContratacionSbp) throws BusinessException;
	
	/**
	 * @param idFechaContratacion
	 * @return
	 * @throws BusinessException
	 */
	public FechaContratacionSbp getFechaContratacionSbp(Long idFechaContratacion)throws BusinessException;
	/**
	 * alta de fecha de contratacion de sobreprecio
	 * @param fechaContratacionSbp
	 * @throws BusinessException
	 */
	public  Map<String, Object> altaFechasContratacionSbp(FechaContratacionSbp fechaContratacionSbp) throws BusinessException;
}
