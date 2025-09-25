package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.InformeFilter;
import com.rsi.agp.core.jmesa.sort.InformeSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.Informe;

public interface IMtoInformeDao extends GenericDao {
	/**
	 * Devuelve el listado de informes ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de informes
	 * @param sort Ordenación para la búsqueda informes
	 * @param rowStart Primer registro que se informes
	 * @param rowEnd Último registro que se informes
	 * @return
	 * @throws BusinessException
	 */
	public Collection<Informe> getInformesWithFilterAndSort(InformeFilter filter, InformeSort sort,
			int rowStart, int rowEnd, final String cadenaUsuarios) throws BusinessException;

	/**
	 * Devuelve el número de informes que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public int getInformesCountWithFilter(final InformeFilter filter, final String cadenaUsuarios);
	
	/**
	 * Devuelve el listado de informes ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de informes
	 * @param sort Ordenación para la búsqueda informes
	 * @param rowStart Primer registro que se informes
	 * @param rowEnd Último registro que se informes
	 * @return
	 * @throws BusinessException
	 */
	public Collection<Informe> getInformesGenWithFilterAndSort(
			final InformeSort sort, final int rowStart, final int rowEnd, final List<Long> lstIdsInforme,
			InformeFilter informeFilter) throws BusinessException;
	
	/**
	 * Devuelve el número de informes que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public List<Long> getInformesGenCountWithFilter(final InformeFilter filter, final Usuario usuario);
	
	public boolean checkInformeExists(String nombre,Long idInforme) throws DAOException;
	
	public void actualizaConsultaInforme(final String clob, final Long idInforme) throws DAOException;
	
	public List<Long> getListIdsInformeByUsuario(String codUsuario);
	
	public List getConsulta(String sql);
	
	/** DAA 20/02/2013 A partir de una select sacamos el numeo de registros que devuelve
	 *  @param sql
	 * 
	 */
	public int getCountNumRegistros(String sql);
	
	/**
	 * Devuelve el registro de informe cuyo campo 'Oculto' coincide con el id pasado como parámetro
	 * @param idOculto
	 * @return
	 * @throws DAOException
	 */
	public Informe getInformeOculto(BigDecimal idOculto) throws DAOException;
}
