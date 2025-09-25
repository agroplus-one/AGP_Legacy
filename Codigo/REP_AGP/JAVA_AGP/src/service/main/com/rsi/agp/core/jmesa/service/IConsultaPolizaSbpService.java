package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ConsultaPolizaSbpFilter;
import com.rsi.agp.core.jmesa.sort.ConsultaPolizaSbpSort;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public interface IConsultaPolizaSbpService {
	
	/**
	 * Devuelve el listado de polizas ordenadas que se ajustan al filtro indicado 
	 * @param filter Filtro para la busqueda de las polizas
	 * @param sort Ordenacion para la busqueda de las polizas
	 * @param rowStart Primer registro que se mostrara
	 * @param rowEnd ultimo registro que se mostrara
	 * @return
	 * @throws BusinessException
	 */
	public Collection<Poliza> getConsultaPolizasSbpWithFilterAndSort(ConsultaPolizaSbpFilter filter, ConsultaPolizaSbpSort sort,
			int rowStart, int rowEnd, String nombreAseg, List<Long> lstLineasSbp) throws BusinessException;
	
	/**
	 * Devuelve el numero de polizas que se ajustan al filtro pasado por parametro
	 * @param filter
	 * @return
	 */
	public int getConsultaPolizaSbpCountWithFilter(ConsultaPolizaSbpFilter filter, String nombreAseg, List<Long> lstLineasSbp);
	
	public String getTablaPolizasParaSbp (HttpServletRequest request, 
			HttpServletResponse response, Poliza poliza, List<Long> lstLineasSbp, String origenLlamada, List<BigDecimal> listaGrupoEntidades, Map<Long, List<BigDecimal>> cultivosPorLinea,List<BigDecimal> listaGrupoOficinas) throws Exception;
}
