package com.rsi.agp.core.jmesa.dao;

import java.math.BigDecimal;
import java.util.Collection;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.MtoRetencionesFilter;
import com.rsi.agp.core.jmesa.sort.MtoRetencionesSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.Retencion;

public interface IMtoRetencionesDao extends GenericDao{

	
	Collection<Retencion> getRetencionesWithFilterAndSort(
		final MtoRetencionesFilter filter, final MtoRetencionesSort sort, final int rowStart,
		final int rowEnd) throws BusinessException;

	int getRetencionesCountWithFilter(final MtoRetencionesFilter filter);
	public boolean existeRegistro(Integer anyo, BigDecimal retencion) throws Exception;
}
