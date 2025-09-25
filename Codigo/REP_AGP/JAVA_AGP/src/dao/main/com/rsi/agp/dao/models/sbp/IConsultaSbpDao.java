package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ConsultaPolizaSbpFilter;
import com.rsi.agp.core.jmesa.sort.ConsultaPolizaSbpSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.ErrorSbp;
import com.rsi.agp.dao.tables.sbp.EstadoPlzSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

@SuppressWarnings("rawtypes")
public interface IConsultaSbpDao extends GenericDao {
	
	public List<PolizaSbp> consultaPolizaSobreprecio (PolizaSbp polizaSbp) throws DAOException;
	public List<EstadoPlzSbp> getEstadosPolSbp(BigDecimal estadosPolizaExcluir[]);
	public List<ErrorSbp> getDetalleErroresSbp(BigDecimal detalleErroresExcluir[]);
	
	/**
	 * Devuelve el listado de polizas ordenadas que se ajustan al filtro indicado 
	 * @param filter Filtro para la busqueda de las polizas
	 * @param sort Ordenacion para la busqueda de las polizas
	 * @param rowStart Primer registro que se mostrara
	 * @param rowEnd Ultimo registro que se mostrara
	 * @return
	 * @throws BusinessException
	 */
	public Collection<Poliza> getConsultaPolizasSbpWithFilterAndSort(final ConsultaPolizaSbpFilter filter,final  ConsultaPolizaSbpSort sort,
			final int rowStart, final int rowEnd, final String nombreAseg, List<Long> lstLineasSbp) throws BusinessException;
	
	/**
	 * Devuelve el numero de polizas que se ajustan al filtro pasado como parametro
	 * @param filter
	 * @return
	 */
	public int getConsultaPolizaSbpCountWithFilter(final ConsultaPolizaSbpFilter filter, final String nombreAseg, List<Long> lstLineasSbp);
	
	public List<Sobreprecio> getLineasSobrePrecio();
	public List<PolizaSbp> getListaPolizasSbp(Long idPoliza, boolean complementaria);
	public Map<Long, List<BigDecimal>> getCultivosPorLineaseguroid(BigDecimal maxPlan);	
}
