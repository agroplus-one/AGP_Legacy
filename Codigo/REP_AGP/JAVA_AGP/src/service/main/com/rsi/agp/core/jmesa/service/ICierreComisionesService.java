package com.rsi.agp.core.jmesa.service;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CierreFilter;
import com.rsi.agp.core.jmesa.sort.CierreSort;
import com.rsi.agp.dao.tables.comisiones.Cierre;

public interface ICierreComisionesService {
	
	String getTablaCierre(HttpServletRequest request,
			HttpServletResponse response, Cierre cierreBean,
			String origenLlamada);

	public Collection<Cierre> getCierreWithFilterAndSort(
			CierreFilter filter, CierreSort sort, int rowStart,
			int rowEnd) throws BusinessException ;
	
	public int getCierreCountWithFilter(CierreFilter filter);

}
