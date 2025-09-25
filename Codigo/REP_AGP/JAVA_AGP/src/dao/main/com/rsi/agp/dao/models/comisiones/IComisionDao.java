package com.rsi.agp.dao.models.comisiones;

import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ComisionApliFilter;
import com.rsi.agp.core.jmesa.filter.ComisionFilter;
import com.rsi.agp.core.jmesa.sort.ComisionApliSort;
import com.rsi.agp.core.jmesa.sort.ComisionSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacion;

@SuppressWarnings("rawtypes")
public interface IComisionDao extends GenericDao{

	int getFicheroComisionCountWithFilter(ComisionFilter filter,
			Comision comision);

	Collection<Comision> getFicheroComisionWithFilterAndSort(
			ComisionFilter filter, ComisionSort sort, int rowStart, int rowEnd,
			Comision comision) throws BusinessException;

	Collection<ComisionAplicacion> getFicheroComisionApliWithFilterAndSort(
			ComisionApliFilter filter, ComisionApliSort sort, int rowStart,
			int rowEnd, ComisionAplicacion comision) throws BusinessException;

	int getFicheroComisionApliCountWithFilter(ComisionApliFilter filter,
			ComisionAplicacion comisionApli);



}
