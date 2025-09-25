package com.rsi.agp.core.jmesa.service;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ImpagadosFilter;
import com.rsi.agp.core.jmesa.sort.ImpagadosSort;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;

public interface IFicheroImpagadosService {

	String getTablaImpagados(HttpServletRequest request,
			HttpServletResponse response, ReciboImpagado reciboImpagadoBean,
			String origenLlamada);

	public int getFicheroImpagadosCountWithFilter(ImpagadosFilter filter);

	public Collection<ReciboImpagado> getFicheroImpagadosWithFilterAndSort(
			ImpagadosFilter filter, ImpagadosSort sort, int rowStart, int rowEnd)
			throws BusinessException;

	ReciboImpagado getDatosImpagados(Long idImpagado) throws BusinessException;

}
