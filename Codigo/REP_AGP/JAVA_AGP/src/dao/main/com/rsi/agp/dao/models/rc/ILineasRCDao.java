package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.Collection;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.LineasRCFilter;
import com.rsi.agp.core.jmesa.sort.LineasRCSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.LineasRC;

public interface ILineasRCDao extends GenericDao {

	public Collection<EspeciesRC> getEspeciesRC() throws DAOException;

	public String getlistaIdsTodos(final LineasRCFilter consultaFilter)
			throws DAOException;

	public int getLineasRCCountWithFilter(final LineasRCFilter filter)
			throws DAOException;

	public Collection<LineasRC> getLineasRCWithFilterAndSort(
			final LineasRCFilter filter, final LineasRCSort sort,
			final int rowStart, final int rowEnd) throws DAOException;

	public void replicaLineaRC(final BigDecimal planOrig,
			final BigDecimal lineaOrig, final BigDecimal planDest,
			final BigDecimal lineaDest) throws DAOException;
}