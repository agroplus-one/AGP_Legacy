package com.rsi.agp.dao.models.comisiones;

import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ImpagadosFilter;
import com.rsi.agp.core.jmesa.sort.ImpagadosSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;

@SuppressWarnings("rawtypes")
public interface IFicheroImpagadosDao extends GenericDao{

	Collection<ReciboImpagado> getFicheroImpagadosWithFilterAndSort(
			ImpagadosFilter filter, ImpagadosSort sort, int rowStart,
			int rowEnd) throws BusinessException;

	int getFicheroImpagadosCountWithFilter(ImpagadosFilter filter);

	

}
