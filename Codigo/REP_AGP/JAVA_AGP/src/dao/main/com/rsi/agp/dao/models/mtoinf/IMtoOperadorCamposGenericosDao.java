package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.OperadorCampoGenericoFilter;
import com.rsi.agp.core.jmesa.sort.OperadorCampoGenericoSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfOperadores;

public interface IMtoOperadorCamposGenericosDao extends GenericDao {
	/**
	 * Devuelve el listado de campos permitidos ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de campos permitidos
	 * @param sort Ordenación para la búsqueda campos permitidos
	 * @param rowStart Primer registro que de campos permitidos que se muestra
	 * @param rowEnd Último registro que de campos permitidos que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<VistaMtoinfOperadores> getOpGenericoWithFilterAndSort(
			final OperadorCampoGenericoFilter filter, final OperadorCampoGenericoSort sort, final int rowStart,
			final int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el número de campos permitidos que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public int getOpGenericoCountWithFilter(final OperadorCampoGenericoFilter filter);

	public List<CamposPermitidos> getListaCamposPermitidos() throws DAOException; 

	public List<DatoInformes> getListaDatosInformes(final Long idCampoPermitido);
	
	public boolean checkCampPermExists(BigDecimal idCampoPermitido) throws DAOException;

}
