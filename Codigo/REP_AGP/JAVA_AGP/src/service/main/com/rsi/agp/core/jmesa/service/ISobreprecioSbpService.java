package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.SobreprecioSbpFilter;
import com.rsi.agp.core.jmesa.sort.SobreprecioSbpSort;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public interface ISobreprecioSbpService{
	
	/**
	 * Devuelve el listado de sobreprecios ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de los sobreprecios
	 * @param sort Ordenación para la búsqueda de los sobreprecios
	 * @param rowStart Primer registro que se mostrará
	 * @param rowEnd Último registro que se mostrará
	 * @return
	 * @throws BusinessException
	 */
	public Collection<Sobreprecio> getSobreprecioSbpWithFilterAndSort(SobreprecioSbpFilter filter, SobreprecioSbpSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el número de sobreprecios que se ajustan al filtro pasado por parámetro
	 * @param filter
	 * @return
	 */
	public int getConsultaPolizaSbpCountWithFilter(SobreprecioSbpFilter filter);
	
	public String getTablaSobreprecios (HttpServletRequest request, HttpServletResponse response, Sobreprecio fechaContratacionSbp, String origenLlamada);
	
	
	/**
	 * Baja  de sobreprecio
	 * @param sobreprecio
	 * @throws BusinessException
	 */
	public void bajaSobreprecio(Sobreprecio sobreprecio)throws BusinessException;
		
	/**
	 * Modifica la tabla de Sobreprecio
	 * @param sobreprecio
	 * @throws BusinessException
	 */
	public Map<String, Object> editaSobreprecio(Sobreprecio sobreprecio) throws BusinessException;
	
	/**
	 * @param idsobreprecio
	 * @return
	 * @throws BusinessException
	 */
	public Sobreprecio getSobreprecio(Long idsobreprecio)throws BusinessException;
	/**
	 * alta de Sobreprecio
	 * @param sobreprecio
	 * @throws BusinessException
	 */
	public Map<String, Object> altaSobreprecio(Sobreprecio sobreprecio) throws BusinessException;
	
	Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig,	BigDecimal planDest, BigDecimal lineaDest) throws BusinessException;

}
