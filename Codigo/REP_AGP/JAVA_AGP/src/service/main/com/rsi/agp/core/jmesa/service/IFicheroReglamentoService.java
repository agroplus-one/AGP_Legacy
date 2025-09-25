package com.rsi.agp.core.jmesa.service;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ReglamentoFilter;
import com.rsi.agp.core.jmesa.sort.ReglamentoSort;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitida;

public interface IFicheroReglamentoService {

	String getTablaReglamento(HttpServletRequest request,
			HttpServletResponse response, ReglamentoProduccionEmitida reglamentoProduccionEmitidaBean,
			String origenLlamada);

	public Collection<ReglamentoProduccionEmitida> getFicheroReglamentoWithFilterAndSort(
			ReglamentoFilter filter, ReglamentoSort sort, int rowStart,
			int rowEnd, ReglamentoProduccionEmitida reglamentoProduccionEmitida) throws BusinessException ;
	
	public int getFicheroReglamentoCountWithFilter(ReglamentoFilter filter,
			 ReglamentoProduccionEmitida reglamentoProduccionEmitida);
}
