package com.rsi.agp.dao.models.comisiones;

import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.DeudaAplazadaApliFilter;
import com.rsi.agp.core.jmesa.filter.DeudaAplazadaFilter;
import com.rsi.agp.core.jmesa.sort.DeudaAplazadaApliSort;
import com.rsi.agp.core.jmesa.sort.DeudaAplazadaSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DeudaAplazadaAplicacion;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;

@SuppressWarnings("rawtypes")
public interface IDeudaAplazadaDao extends GenericDao {

	int getFicheroDeudaAplazadaCountWithFilter(DeudaAplazadaFilter filter,
			FicheroMult ficheroMult);

	Collection<FicheroMult> getFicheroDeudaAplazadaWithFilterAndSort(
			DeudaAplazadaFilter filter, DeudaAplazadaSort sort, int rowStart, int rowEnd,
			FicheroMult ficheroMult) throws BusinessException;

	Collection<DeudaAplazadaAplicacion> getFicheroDeudaAplazadaApliWithFilterAndSort(
			DeudaAplazadaApliFilter filter, DeudaAplazadaApliSort sort, int rowStart,
			int rowEnd, DeudaAplazadaAplicacion emitidoApli) throws BusinessException;

	int getFicheroDeudaAplazadaApliCountWithFilter(DeudaAplazadaApliFilter filter,
			DeudaAplazadaAplicacion deudaAplazadaApli);
}