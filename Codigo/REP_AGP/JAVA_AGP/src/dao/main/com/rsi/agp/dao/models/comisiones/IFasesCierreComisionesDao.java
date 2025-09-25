package com.rsi.agp.dao.models.comisiones;

import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.FasesCierreComsFilter;
import com.rsi.agp.core.jmesa.sort.FasesCierreComsSort;
import com.rsi.agp.dao.tables.comisiones.unificado.FasesCerradas;

public interface IFasesCierreComisionesDao {

	int getFasesCierreCountWithFilter(FasesCierreComsFilter filter, FasesCerradas fase);

	Collection<FasesCerradas> getFasesCierreWithFilterAndSort(
			FasesCierreComsFilter filter, FasesCierreComsSort sort,
			int rowStart, int rowEnd, FasesCerradas emitido) throws BusinessException;

}
