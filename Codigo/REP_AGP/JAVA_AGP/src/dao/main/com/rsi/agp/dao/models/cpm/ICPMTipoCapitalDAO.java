package com.rsi.agp.dao.models.cpm;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CPMTipoCapitalFilter;
import com.rsi.agp.core.jmesa.sort.CPMTipoCapitalSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpm.CPMTipoCapital;

@SuppressWarnings("rawtypes")
public interface ICPMTipoCapitalDAO extends GenericDao {

	/**
	 * Devuelve el listado de CPM permitidos para una póliza, configurados en la
	 * tabla TB_CPM_TIPO_CAPITAL
	 * 
	 * @param idPoliza
	 * @return
	 * @throws DAOException
	 */
	public List<BigDecimal> getCPMDePoliza(Long codtipoCapital, Long idPoliza, String codModulo) throws DAOException;

	/**
	 * Devuelve el listado de CPM permitidos para una póliza y un AM relacionado
	 * con ella, configurados en la tabla TB_CPM_TIPO_CAPITAL
	 * 
	 * @param idPoliza
	 * @return
	 * @throws DAOException
	 */
	public List<BigDecimal> getCPMDePolizaAnexoMod(Long idPoliza, Long idAnexo, String codModulo) throws DAOException;

	/**
	 * Comprueba si el CPM indicado existe en la tabla TB_CPM_TIPO_CAPITAL para los
	 * demás valores contenidos en el parámetro
	 * 
	 * @param cpmTipoCapital
	 * @return
	 */
	public boolean isCPMPermitido(CPMTipoCapital cpmTipoCapital);

	/**
	 * Devuelve el listado de cpmTipoCapital ordenados que se ajustan al filtro
	 * indicado
	 * 
	 * @param filter
	 *            Filtro para la búsqueda de cpmTipoCapital
	 * @param sort
	 *            Ordenación para la búsqueda cpmTipoCapital
	 * @param rowStart
	 *            Primer registro que se mostrará
	 * @param rowEnd
	 *            Último registro que se mostrará
	 * @return
	 * @throws BusinessException
	 */
	public Collection<CPMTipoCapital> getCPMTipoCapitalWithFilterAndSort(CPMTipoCapitalFilter filter,
			CPMTipoCapitalSort sort, int rowStart, int rowEnd) throws BusinessException;

	/**
	 * Devuelve el número de cpmTipoCapital que se ajustan al filtro pasado como
	 * parámetro
	 * 
	 * @param filter
	 * @param apital
	 * @return
	 */
	public int getConsultaCPMTipoCapitalCountWithFilter(final CPMTipoCapitalFilter filter);

	/**
	 * Llama al procedimiento encargado de copiar los cpmTipoCapital del
	 * lineaseguroid origen al de destino
	 * 
	 * @param origen
	 * @param destino
	 * @throws DAOException
	 */
	public String replicar(BigDecimal origen, BigDecimal destino) throws DAOException;

	public Long getCPMTipoCapital(CPMTipoCapital cpmTipoCapital) throws DAOException;

	public boolean numRegDestinoIgualNumRegOrigen(Long lineaSeguroIdDestino, Long lineaSeguroIdOrigen);

}
