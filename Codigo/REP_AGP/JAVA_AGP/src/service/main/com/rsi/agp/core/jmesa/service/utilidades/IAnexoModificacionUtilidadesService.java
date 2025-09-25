package com.rsi.agp.core.jmesa.service.utilidades;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.AnexoModificacionFilter;
import com.rsi.agp.core.jmesa.sort.AnexoModificacionSort;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;

public interface IAnexoModificacionUtilidadesService {

	public String getTablaAnexoModificacion(HttpServletRequest request,
			HttpServletResponse response,
			AnexoModificacion anexoModificacionBean, String primeraBusqueda, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas);

	Collection<AnexoModificacion> getAnexoModificacionWithFilterAndSort( 
			AnexoModificacionFilter filter, AnexoModificacionSort sort,
			int rowStart, int rowEnd) throws BusinessException;

	int getAnexoModificacionCountWithFilter(AnexoModificacionFilter filter)
			throws BusinessException;
	
	TableFacade crearTableFacade(HttpServletRequest request,
			HttpServletResponse response, AnexoModificacion anexoMod,
			String primeraBusqueda); 
	void setDataAndLimitVariables(TableFacade tableFacade, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas);
	
	public AnexoModificacionFilter getAnexoModificacionFilter(Limit limit, List<BigDecimal> listaGrupoEntidades,List<BigDecimal> listaGrupoOficinas);
	
	public AnexoModificacionSort getAnexoModificacionSort(Limit limit);

	public List<AnexoModificacion> getAllFilteredAndSorted() throws BusinessException;

	
}
