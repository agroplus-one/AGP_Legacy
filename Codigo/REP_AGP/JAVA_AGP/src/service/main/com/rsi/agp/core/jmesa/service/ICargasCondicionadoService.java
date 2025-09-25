package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CondicionadoFilter;
import com.rsi.agp.core.jmesa.sort.CondicionadoSort;
import com.rsi.agp.dao.tables.cargas.CargasCondicionado;

public interface ICargasCondicionadoService {
	
	public String getTablaCargasCondicionado (HttpServletRequest request, HttpServletResponse response,
			CargasCondicionado cargasCondicionado, String origenLlamada); 
	
	public int getCondicionadosCountWithFilter(CondicionadoFilter filter);
	
	public Collection<CargasCondicionado> getCondicionadosWithFilterAndSort(CondicionadoFilter filter, CondicionadoSort sort, 
			int rowStart, int rowEnd) throws BusinessException;

	public CargasCondicionado saveCondicionado() throws BusinessException;

	public void borraCondicionado(Long id) throws BusinessException, Exception;

	public void cambiaEstadoCarga(Long id,BigDecimal bigDecimal) throws BusinessException;

}
