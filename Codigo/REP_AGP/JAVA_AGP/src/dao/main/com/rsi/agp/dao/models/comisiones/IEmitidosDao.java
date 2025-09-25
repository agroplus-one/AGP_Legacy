package com.rsi.agp.dao.models.comisiones;

import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.EmitidosApliFilter;
import com.rsi.agp.core.jmesa.filter.EmitidosFilter;
import com.rsi.agp.core.jmesa.sort.EmitidosApliSort;
import com.rsi.agp.core.jmesa.sort.EmitidosSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitido;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoAplicacion;

@SuppressWarnings("rawtypes")
public interface IEmitidosDao extends GenericDao{

	int getFicheroEmitidosCountWithFilter(EmitidosFilter filter,
			ReciboEmitido emitido);

	Collection<ReciboEmitido> getFicheroEmitidosWithFilterAndSort(
			EmitidosFilter filter, EmitidosSort sort, int rowStart, int rowEnd,
			ReciboEmitido emitido) throws BusinessException;

	Collection<ReciboEmitidoAplicacion> getFicheroEmitidosApliWithFilterAndSort(
			EmitidosApliFilter filter, EmitidosApliSort sort, int rowStart,
			int rowEnd, ReciboEmitidoAplicacion emitidoApli) throws BusinessException;

	int getFicheroEmitidosApliCountWithFilter(EmitidosApliFilter filter,
			ReciboEmitidoAplicacion emitidoApli);
}