package com.rsi.agp.core.jmesa.dao;

import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ListadoPolizaSbpFilter;
import com.rsi.agp.core.jmesa.sort.ListadoPolizaSbpSort;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

public interface IListadoPolizaSbpDao {
	public Collection<PolizaSbp> getListadoPolizasSbpWithFilterAndSort(
			final ListadoPolizaSbpFilter filter, final ListadoPolizaSbpSort sort, final int rowStart, final int rowEnd, final String filtrarDetalle) throws BusinessException;
	public int getListadoPolizaSbpCountWithFilter(final ListadoPolizaSbpFilter filter, final String filtrarDetalle);
	
}
