package com.rsi.agp.core.jmesa.service.mtoinf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CamposCalculadosFilter;
import com.rsi.agp.core.jmesa.sort.CamposCalculadosSort;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;

public interface IMtoCamposCalculadosService {
	
	/**
	 * Devuelve el número de campos calculados que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public int getConsultaCamposCalculadosCountWithFilter(CamposCalculadosFilter filter); 
	
	/**
	 * Devuelve el listado de campos calculados ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de campos calculados
	 * @param sort Ordenación para la búsqueda campos calculados
	 * @param rowStart Primer registro de campos calculados que se muestra
	 * @param rowEnd Último registro de campos calculados que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<CamposCalculados> getCamposCalculadosWithFilterAndSort(
			CamposCalculadosFilter filter, CamposCalculadosSort sort, int rowStart,
			int rowEnd) throws BusinessException;
	
	/**
	 * Genera la tabla para mostrar el campos calculados que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param camposcalculados
	 * @param origenLlamada
	 * @return
	 */
	public String getTablaCamposCalculados (HttpServletRequest request, HttpServletResponse response, CamposCalculados camposCalculados, String origenLlamada);
	
	/**
	 * Borra el objeto campo permitido pasado como parámetro
	 * @param camposcalculados
	 * @return Boolean que indica si el borrado ha sido correcto
	 */
	public Map<String, Object> bajaCamposCalculados(
			CamposCalculados camposCalculados) throws BusinessException;
	
	

	/**
	 * Realiza el alta del campo calculado del informe pasado como parámetro
	 * @param un campo calculado
	 */
	public Map<String, Object> altaCamposCalculados (CamposCalculados camposCalculados) throws BusinessException;
		
	/**
	 * Realiza la modificacion del campo calculado del informe pasado como parámetro
	 * @param un campo calculado
	 */
	public Map<String, Object> modificarCamposCalculados(CamposCalculados camposCalculados) throws BusinessException;
	
	
	
	/**
	 * Recupera la lista de campos permitidos
	 */
	public List<CamposPermitidos> getListCamposPermitidos() throws  BusinessException;
	
	
}
