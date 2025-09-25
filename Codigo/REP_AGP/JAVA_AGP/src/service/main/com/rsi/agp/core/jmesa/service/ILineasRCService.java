package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.gan.LineasRCFilter;
import com.rsi.agp.core.jmesa.sort.LineasRCSort;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.LineasRC;

public interface ILineasRCService {

	public Collection<EspeciesRC> getEspeciesRC() throws BusinessException;

	public String getTablaLineasRC(final HttpServletRequest request,
			final HttpServletResponse response, final LineasRC lineasRC,
			final String origenLlamada) throws BusinessException;

	public Collection<LineasRC> getLineasRCWithFilterAndSort(
			final LineasRCFilter filter, final LineasRCSort sort, int rowStart,
			int rowEnd) throws BusinessException;

	public int getLineasRCCountWithFilter(final LineasRCFilter filter)
			throws BusinessException;

	public String validateLineaRC(final LineasRC lineasRC)
			throws BusinessException;

	public LineasRC grabarLineaRC(final LineasRC lineasRC)
			throws BusinessException;
	
	public LineasRC modificarLineaRC(final LineasRC lineasRC)
			throws BusinessException;

	public void borrarLineaRC(final Long idLineaRC) throws BusinessException;

	public String validateReplicaLineaRC(final BigDecimal planDest,
			final BigDecimal lineaDest) throws BusinessException;
	
	public void replicaLineaRC(final BigDecimal planOrig,
			final BigDecimal lineaOrig, final BigDecimal planDest,
			final BigDecimal lineaDest) throws BusinessException;
}