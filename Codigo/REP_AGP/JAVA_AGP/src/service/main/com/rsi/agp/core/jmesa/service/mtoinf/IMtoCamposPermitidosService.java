package com.rsi.agp.core.jmesa.service.mtoinf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CamposPermitidosFilter;
import com.rsi.agp.core.jmesa.sort.CamposPermitidosSort;
import com.rsi.agp.core.util.OperadorInforme;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.Vista;
import com.rsi.agp.dao.tables.mtoinf.VistaCampo;

public interface IMtoCamposPermitidosService {
	
	/**
	 * Devuelve el número de campos permitidos que se ajustan al filtro pasado como parámetro
	 * @param filter
	 * @return
	 */
	public int getCalcPermCountWithFilter(CamposPermitidosFilter filter, String tablaOrigen, String descripcion);
	
	/**
	 * Devuelve el listado de campos permitidos ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de campos permitidos
	 * @param sort Ordenación para la búsqueda campos permitidos
	 * @param rowStart Primer registro que de campos permitidos que se muestra
	 * @param rowEnd Último registro que de campos permitidos que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<CamposPermitidos> getCalcPermWithFilterAndSort(
			CamposPermitidosFilter filter, CamposPermitidosSort sort, int rowStart,
			int rowEnd, String tablaOrigen, String descripcion) throws BusinessException;
	
	/**
	 * Genera la tabla para mostrar el campos permitidos que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param camposPermitidos
	 * @param origenLlamada
	 * @return
	 */
	public String getTablaCamposPermitidos(HttpServletRequest request,
			HttpServletResponse response, CamposPermitidos camposPermitidos,String origenLlamada,
			String tablaOrigen, List<Vista> lstVistas, String descripcion);
	
	/**
	 * Borra el objeto campo permitido pasado como parámetro
	 * @param camposPermitidos
	 * @return Boolean que indica si el borrado ha sido correcto
	 */
	public boolean bajaCampoPermitido(CamposPermitidos camposPermitidos) throws BusinessException;
	
	/**
	 * Obtiene el objeto campo permitido correspondiente al id indicado en parámetro
	 * @param id
	 * @return
	 */
	public CamposPermitidos getCampoPermitido(Long id) throws BusinessException;
	
	/**
	 * Realiza el alta del campo permitido pasado como parámetro
	 * @param camposPermitidos
	 * @return Devuelve un mapa con los errores producidos en el proceso de alta
	 */
	public Map<String, Object> altaCampoPermitido(CamposPermitidos camposPermitidos) throws BusinessException;
	
	/**
	 * Realiza la modificación del campo permitido pasado como parámetro
	 * @param camposPermitidos
	 * @return Devuelve un mapa con los errores producidos en el proceso de modificación
	 */
	public Map<String, Object> updateCampoPermitido(CamposPermitidos camposPermitidos) throws BusinessException;
	
	public List<Vista> getListadoVistas();
	
	public VistaCampo getVistaCampo(Long idVistaCampo);
	
	public Map<String, String> getMapFormatos();
	
	public List<OperadorInforme> getListaTiposCampo ();
	
}
