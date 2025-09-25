package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.TasasSbpFilter;
import com.rsi.agp.core.jmesa.sort.TasasSbpSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.sbp.TasasSbp;

public interface ITasasSbpDao extends GenericDao{
	
	/**
	 * Devuelve el listado de tasas ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de tasas
	 * @param sort Ordenación para la búsqueda tasas
	 * @param rowStart Primer registro que se tasas
	 * @param rowEnd Último registro que se tasas
	 * @return
	 * @throws BusinessException
	 */
	public Collection<TasasSbp> getTasasSbpWithFilterAndSort(TasasSbpFilter filter, TasasSbpSort sort, int rowStart, int rowEnd) throws BusinessException;

	
	/**
	 * Devuelve el número de tasas que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public int getTasasSbpCountWithFilter(final TasasSbpFilter filter);
	
	public String replicar (BigDecimal origen, BigDecimal destino) throws DAOException;
	
	public boolean numRegDestinoIgualNumRegOrigen(Long lineaSeguroIdDestino, Long lineaSeguroIdOrigen);

	public void volcarTasasSbpFromFichero() throws BusinessException;
	
}
