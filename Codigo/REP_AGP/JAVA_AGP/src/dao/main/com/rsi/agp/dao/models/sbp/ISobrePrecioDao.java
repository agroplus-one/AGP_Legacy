package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.SobreprecioSbpFilter;
import com.rsi.agp.core.jmesa.sort.SobreprecioSbpSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

@SuppressWarnings("rawtypes")
public interface ISobrePrecioDao extends GenericDao {

	public List<Sobreprecio> getLineaSobrePrecio(Long lineaseguroid) throws DAOException;

	/**
	 * Devuelve el listado de sobreprecios ordenados que se ajustan al filtro
	 * indicado
	 * 
	 * @param filter
	 *            Filtro para la búsqueda de sobreprecios
	 * @param sort
	 *            Ordenación para la búsqueda sobreprecios
	 * @param rowStart
	 *            Primer registro que se mostrará
	 * @param rowEnd
	 *            último registro que se mostrará
	 * @return
	 * @throws BusinessException
	 */
	public Collection<Sobreprecio> getSobreprecioSbpWithFilterAndSort(SobreprecioSbpFilter filter,
			SobreprecioSbpSort sort, int rowStart, int rowEnd) throws BusinessException;

	/**
	 * Devuelve el numero de sobreprecios que se ajustan al filtro pasado como
	 * parametro
	 * 
	 * @param filter
	 * @return
	 */
	public int getConsultaPolizaSbpCountWithFilter(final SobreprecioSbpFilter filter);

	public void actualizaSobreprecio(Sobreprecio sobreprecio)
			throws ConstraintViolationException, DataIntegrityViolationException, Exception;

	public boolean existeCultivo(Long lineaseguroid, BigDecimal codCultivo);

	public List<Sobreprecio> getLineaSbpFromLineaPlan(String linea, String plan) throws DAOException;

	public boolean numRegDestinoIgualNumRegOrigen(Long lineaSeguroIdDestino, Long lineaSeguroIdOrigen);

	public String replicar(BigDecimal lineaSeguroIdOrigen, BigDecimal lineaSeguroIdDestino) throws DAOException;

	public BigDecimal buscaParcelasAnexoSBP(List cultivosSbp, Long idPoliza) throws DAOException;

	public boolean existeTipoCapital(BigDecimal codTipoCapital);
	
	public Date getFechaFinGarantiasSbp(final Long lineaseguroid, final BigDecimal[] codcultivos) throws Exception;
}
