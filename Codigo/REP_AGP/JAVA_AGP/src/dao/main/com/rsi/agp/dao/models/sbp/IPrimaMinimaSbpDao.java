package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;


import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.PrimaMinimaSbpFilter;
import com.rsi.agp.core.jmesa.sort.PrimaMinimaSbpSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;

public interface IPrimaMinimaSbpDao extends GenericDao{
	
	/**
	 * Devuelve el listado de primas mínimas ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de primas mínimas
	 * @param sort Ordenación para la búsqueda primas mínimas
	 * @param rowStart Primer registro que se primas mínimas
	 * @param rowEnd Último registro que se primas mínimas
	 * @return
	 * @throws BusinessException
	 */
	public Collection<PrimaMinimaSbp> getPrimaMinimaSbpWithFilterAndSort(PrimaMinimaSbpFilter filter, PrimaMinimaSbpSort sort, int rowStart, int rowEnd) throws BusinessException;

	
	/**
	 * Devuelve el número de primas mínimas que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public int getPrimaMinimaSbpCountWithFilter(final PrimaMinimaSbpFilter filter);
	
	/**
	 * Devuelve listado de todas las primas mínimas 
	 * @param filter
	 * @return
	 */
	public List<PrimaMinimaSbp> getListaPrimaMinimaSbp();
	
	/*
	 * Chequea si existe ya una prima Mínima
	 * 
	 */
	public boolean checkPrimaMinimaSbpExists(Long lineaSeguroId) throws DAOException;
	
}
