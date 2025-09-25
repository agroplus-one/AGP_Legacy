package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CamposCalculadosFilter;
import com.rsi.agp.core.jmesa.sort.CamposCalculadosSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;

public interface IMtoCamposCalculadosDao extends GenericDao {
	/**
	 * Devuelve el listado de campos calculados ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de campos calculados
	 * @param sort Ordenación para la búsqueda campos calculados
	 * @param rowStart Primer registro que de campos calculados que se muestra
	 * @param rowEnd Último registro que de campos calculados que se muestra
	 * @return
	 * @throws BusinessException
	 */


	public Collection<CamposCalculados> getCamposCalculadosWithFilterAndSort(
			CamposCalculadosFilter filter, CamposCalculadosSort sort, int rowStart,
			int rowEnd)throws DAOException ; 
	/**s
	 * Devuelve el número de campos calculados que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public int getCamposCalculadosCountWithFilter(final CamposCalculadosFilter filter);
	public List<CamposCalculados> getListaCamposCalculados() throws DAOException;
	public boolean existeDatosCamposCalculados(final CamposCalculados camposCalculados) throws  DAOException ;
}
