package com.rsi.agp.dao.models.imp;

import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CondicionadoFilter;
import com.rsi.agp.core.jmesa.sort.CondicionadoSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cargas.CargasCondicionado;

@SuppressWarnings("rawtypes")
public interface ICargasCondicionadoDao extends GenericDao{
	/**
	 * Devuelve el numero de cargas de condicionado que se ajustan al filtro pasado como parametro
	 * @param filter
	 * @return int
	 */
	int getCondicionadosCountWithFilter(CondicionadoFilter filter);
	
	/**
	 * Devuelve el listado de cargas de condicionado ordenados que se ajustan al filtro indicado 
	 * @param filter
	 * @param sort
	 * @param rowStart
	 * @param rowEnd
	 * @return Collection<CargasCondicionado>
	 */
	Collection<CargasCondicionado> getCondicionadosWithFilterAndSort(
			CondicionadoFilter filter, CondicionadoSort sort, int rowStart,
			int rowEnd)throws BusinessException;
	/**
	 * Devuelve los ficheros asociados de una carga de condicionado
	 * @param LongidCondicionado
	 */
	List getFicherosCondicionado(Long idCondicionado);

}
