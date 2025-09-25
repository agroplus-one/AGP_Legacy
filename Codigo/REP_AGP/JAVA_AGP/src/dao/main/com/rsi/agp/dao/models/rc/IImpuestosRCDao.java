package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.ImpuestosRCFilter;
import com.rsi.agp.core.jmesa.sort.ImpuestosRCSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.rc.ImpuestosRC;

public interface IImpuestosRCDao extends GenericDao {

	int getImpuestosRCCountWithFilter(final ImpuestosRCFilter filter)
			throws DAOException;

	List<ImpuestosRC> getImpuestosRCWithFilterAndSort(
			final ImpuestosRCFilter filter, final ImpuestosRCSort sort,
			final int rowStart, final int rowEnd) throws DAOException;

	void replicarImpuestosRC(final BigDecimal planOrig,
			final BigDecimal planDest) throws DAOException;

	StringBuilder getlistaIdsTodos(final ImpuestosRCFilter filter) throws DAOException;

	boolean existeImpuestoRC(final Long ImpuestoRCId) throws DAOException;

	Collection<ImpuestosRC> getImpuestosRC(final BigDecimal plan) throws DAOException;
}
