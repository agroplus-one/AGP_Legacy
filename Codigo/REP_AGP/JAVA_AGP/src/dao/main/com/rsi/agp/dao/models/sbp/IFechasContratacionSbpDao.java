package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.FechasContratacionSbpFilter;
import com.rsi.agp.core.jmesa.sort.FechasContratacionSbpSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.sbp.FechaContratacionSbp;

@SuppressWarnings("rawtypes")
public interface IFechasContratacionSbpDao extends GenericDao {
	/**
	 * Devuelve el listado de fechas de contratacion ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la busqueda de fechas de contratacion
	 * @param sort Ordenacion para la busqueda fechas de contratacion
	 * @param rowStart Primer registro que se muestra
	 * @param rowEnd Ultimo registro que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<FechaContratacionSbp> getFechasContratacionSbpWithFilterAndSort(FechasContratacionSbpFilter filter, FechasContratacionSbpSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el numero de fechas de contratacion que se ajustan al filtro pasado como parametro
	 * @param filter
	 * @return
	 */
	public int getFechasContratacionSbpCountWithFilter(final FechasContratacionSbpFilter filter);
	
	/*
	 * Chequea si existe ya una Linea- cultivo de Seguro en tabla "TB_SBP_FECHACONTRATACION"
	 * 
	 */
	public boolean existeLineaSeguroIdCultivo(Long lineaseguroid,BigDecimal codcultivo,Long id);			
}
