package com.rsi.agp.core.jmesa.service;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ReglamentoSitFilter;
import com.rsi.agp.core.jmesa.sort.ReglamentoSitSort;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;

public interface IFicheroReglamentoSitService {

	String getTablaReglamentoSit(
			HttpServletRequest request,
			HttpServletResponse response,
			ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSitBean,
			String origenLlamada);
	 
	public int getFicheroReglamentoSitCountWithFilter(ReglamentoSitFilter filter,
			 ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit);
	 
	 public Collection<ReglamentoProduccionEmitidaSituacion> getFicheroReglamentoSitWithFilterAndSort(
				ReglamentoSitFilter filter, ReglamentoSitSort sort, int rowStart,
				int rowEnd, ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit) throws BusinessException;

}
