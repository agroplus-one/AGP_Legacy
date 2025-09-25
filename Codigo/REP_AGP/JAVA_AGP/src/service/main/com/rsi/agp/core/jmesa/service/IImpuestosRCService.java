package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.gan.ImpuestosRCFilter;
import com.rsi.agp.core.jmesa.sort.ImpuestosRCSort;
import com.rsi.agp.dao.tables.rc.ImpuestosRC;

public interface IImpuestosRCService {

	String getTablaImpuestos(final HttpServletRequest req,
			final ImpuestosRC impuestosRC, final String origenLlamada)
			throws BusinessException;

	ImpuestosRC guardarImpuesto(final ImpuestosRC impuestosRS)
			throws BusinessException;

	void borrarImpuesto(final Long impuestoRCId) throws BusinessException;

	int getImpuestoRCCountWithFilter(final ImpuestosRCFilter filter)
			throws BusinessException;

	ImpuestosRC modificarImpuesto(final ImpuestosRC impuestosRC)
			throws BusinessException;

	List<ImpuestosRC> getImpuestosRCWithFilterAndSort(
			final ImpuestosRCFilter filter, final ImpuestosRCSort sort,
			final int rowStart, final int rowEnd) throws BusinessException;

	String validarImpuestosRC(final ImpuestosRC impuestosRC)
			throws BusinessException;

	String validarImpuestosRCReplica(final BigDecimal planDest)
			throws BusinessException;

	void replicarImpuestosRC(final BigDecimal planOrig,
			final BigDecimal planDest) throws BusinessException;
}
