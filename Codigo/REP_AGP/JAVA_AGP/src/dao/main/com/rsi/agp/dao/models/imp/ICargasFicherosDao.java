package com.rsi.agp.dao.models.imp;

import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CargasFicherosFilter;
import com.rsi.agp.core.jmesa.sort.CargasFicherosSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;

@SuppressWarnings("rawtypes")
public interface ICargasFicherosDao extends GenericDao{
	/**
	 * Devuelve el numero de cargas de ficheros que se ajustan al filtro pasado como parametro
	 * @param filter
	 * @return
	 */
	int getFicherosCountWithFilter(final CargasFicherosFilter filter,final CargasFicheros cargasFicheros);
	/**
	 * Devuelve el listado de cargas de ficheros ordenados que se ajustan al filtro indicado 
	 * @param filter
	 * @param sort
	 * @param rowStart
	 * @param rowEnd
	 * @return
	 */
	Collection<CargasFicheros> getFicherosWithFilterAndSort(
			CargasFicherosFilter filter, CargasFicherosSort sort, int rowStart,
			int rowEnd,final CargasFicheros cargasFicheros)throws BusinessException;
	
	String getTipoFichero(CargasFicheros cargasFicherosBean, List listTablas);
	
	

}
