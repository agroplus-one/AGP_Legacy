package com.rsi.agp.core.jmesa.service.utilidades;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.SiniestrosFilter;
import com.rsi.agp.core.jmesa.sort.SiniestrosSort;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.siniestro.SiniestrosUtilidades;

public interface ISiniestrosUtilidadesService {
	
	/**
	 * Devuelve el listado de siniestros ordenados que se ajustan al filtro indicado
	 * @param filter Filtro para la búsqueda de siniestros 
	 * @param sort Ordenación para la búsqueda campos siniestros
	 * @param rowStart Primer registro que de siniestros que se muestra
	 * @param rowEnd Último registro que de siniestros que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<SiniestrosUtilidades> getSiniestrosWithFilterAndSort(SiniestrosFilter filter, SiniestrosSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el número de siniestros que se ajustan al filtro pasado como parámetro
	 * @param filter Filtro para la búsqueda de siniestros
	 * @return
	 */
	public int getSiniestrosCountWithFilter(final SiniestrosFilter filter) throws BusinessException;
	
	/**
	 * Devuelve la tabla que muestra el listado de siniestros que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param siniestro
	 * @param origenLlamada
	 * @return
	 */
	public String getTablaSiniestros (HttpServletRequest request, HttpServletResponse response, SiniestrosUtilidades siniestro, 
									  String origenLlamada, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas) ;

	
	/**
	 * Devuelve el listado de riesgos posibles
	 * @return
	 */
	public List<Riesgo> getRiesgos ();
	
	
	public List<SiniestrosUtilidades> getAllFilteredAndSorted() throws BusinessException;

}
