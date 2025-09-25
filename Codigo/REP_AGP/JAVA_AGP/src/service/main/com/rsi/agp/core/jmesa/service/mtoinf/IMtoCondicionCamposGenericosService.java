package com.rsi.agp.core.jmesa.service.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CondicionCamposFilter;
import com.rsi.agp.core.jmesa.sort.CondicionCamposSort;
import com.rsi.agp.dao.tables.mtoinf.Operador;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfCondiciones;

public interface IMtoCondicionCamposGenericosService {

	
	/**
	 * Devuelve el listado de condiciones de campos permitidos y calculados ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de las condiciones de campos permitidos y calculados
	 * @param sort Ordenación para la búsqueda de los condiciones de campos permitidos y calculados
	 * @param rowStart Primer registro que se mostrará
	 * @param rowEnd Último registro que se mostrará
	 * @return
	 */
	public String getTablaCondicionInforme(HttpServletRequest request,
			HttpServletResponse response,VistaMtoinfCondiciones vistaMtoinfCondiciones,
			String origenLlamada); 
	

	public Collection<VistaMtoinfCondiciones> getCondicionInformeWithFilterAndSort(
			CondicionCamposFilter filter, CondicionCamposSort sort,
			BigDecimal informeId, int rowStart, int rowEnd) throws BusinessException;	
	public List<Operador> getListaOperadores(
			Integer permitidOCalculado,Long datoInformesId) throws BusinessException ;
	public Map<String, Object>  altaCondicionInforme(
			VistaMtoinfCondiciones vistaMtoinfCondiciones)
			throws BusinessException; 
	public Map<String, Object>  bajaCondicionInforme(
			VistaMtoinfCondiciones vistaMtoinfCondiciones)
			throws BusinessException;
	public Map<String, Object> modificarCondicionInforme(
			VistaMtoinfCondiciones vistaMtoinfCondiciones)
			throws BusinessException;
	
	public int getCondicionesCountWithFilter(final BigDecimal informeId);
	
	/**
	 * Devuelve la lista de estados correspondiente al código de origen de datos pasado como parámetro
	 * @param od
	 * @return
	 */
	public List<Estados> getListaEstados (BigDecimal od);
}