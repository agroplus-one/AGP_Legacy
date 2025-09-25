package com.rsi.agp.core.jmesa.service.utilidades;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ReduccionCapitalFilter;

import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import com.rsi.agp.core.jmesa.sort.ReduccionCapitalSort;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapitalUtilidades;

public interface IReduccionCapitalUtilidadesService {
	
	/**
	 * Devuelve el listado de ReduccionCapital ordenados que se ajustan al filtro indicado
	 * @param filter Filtro para la búsqueda de ReduccionCapital 
	 * @param sort Ordenación para la búsqueda campos ReduccionCapital
	 * @param rowStart Primer registro que de ReduccionCapital que se muestra
	 * @param rowEnd Último registro que de ReduccionCapital que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<ReduccionCapitalUtilidades> getReduccionCapitalWithFilterAndSort(ReduccionCapitalFilter filter, ReduccionCapitalSort sort, int rowStart,	int rowEnd
			//P0079361
			,String fechadanioId, 
			String fechadanioIdHasta,
			String fechaEnvioId,
			String fechaEnvioIdHasta,
			String fechaEnvioPolId,
			String fechaEnvioPolIdHasta,
			String strTipoEnvioId
			//P0079361
			) throws BusinessException;
	
	/**
	 * Devuelve el número de ReduccionCapital que se ajustan al filtro pasado como parámetro
	 * @param filter Filtro para la búsqueda de ReduccionCapital
	 * @return
	 */
	public int getReduccionCapitalCountWithFilter(final ReduccionCapitalFilter filter
			//P0079361
			,String fechadanioId, 
			String fechadanioIdHasta,
			String fechaEnvioId,
			String fechaEnvioIdHasta,
			String fechaEnvioPolId,
			String fechaEnvioPolIdHasta,
			String strTipoEnvioId
			//P0079361
			) throws BusinessException;
	
	/**
	 * Devuelve la tabla que muestra el listado de ReduccionCapital que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param siniestro
	 * @param origenLlamada
	 * @return
	 */
	public String getTablaReduccionCapital (HttpServletRequest request, HttpServletResponse response, ReduccionCapitalUtilidades siniestro, 
											String origenLlamada, List<BigDecimal> listaGrupoEntidades, List<BigDecimal> listaGrupoOficinas) ;

	
	/**
	 * Devuelve el listado de riesgos posibles
	 * @return
	 */
	public List<Riesgo> getRiesgos ();
	
	public TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response, ReduccionCapitalUtilidades redCapital, String primeraBusqueda);
	
	public void setDataAndLimitVariables(TableFacade tableFacade, 
			//P0079361
			String fechadanioId, 
			String fechadanioIdHasta,
			String fechaEnvioId,
			String fechaEnvioIdHasta,
			String fechaEnvioPolId,
			String fechaEnvioPolIdHasta,
			String strTipoEnvioId,
			//String strEstadoCuponId,
			//P0079361
			List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas);
	
	public ReduccionCapitalFilter getReduccionCapitalFilter(Limit limit, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas);
	
	public ReduccionCapitalSort getReduccionCapitalSort(Limit limit);

	//P0079361
	public List<ReduccionCapitalUtilidades> getAllFilteredAndSorted(String estadoCuponRC, String tipoEnvioRC, String fEEnvio, String fEEnvioHasta, String fEdanio, String fEdanioHasta, String fEEnvioPol, String fEEnvioPolHasta) throws BusinessException;
	//P0079361
}
