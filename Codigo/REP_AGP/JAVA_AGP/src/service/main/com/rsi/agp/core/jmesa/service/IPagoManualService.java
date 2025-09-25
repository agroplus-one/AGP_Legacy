package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.limit.Limit;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.PagoManualFilter;
import com.rsi.agp.core.jmesa.sort.PagoManualSort;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.Zona;

public interface IPagoManualService {
	/**
	 * 
	 * 07/05/2014 U029769
	 * @param request
	 * @param response
	 * @param oficinaBusqueda
	 * @param origenLlamada
	 * @return
	 * @throws BusinessException 
	 */
	String getTablaOficinasPagoManual(HttpServletRequest request,
			HttpServletResponse response, Oficina oficinaBusqueda,
			String origenLlamada) throws BusinessException;
	/**
	 * A partir de un filter devuelve una lista de oficinas
	 * 07/05/2014 U029769
	 * @param consultaFilter
	 * @param consultaSort
	 * @param rowStart
	 * @param rowEnd
	 * @return
	 * @throws BusinessException
	 */
	Collection<Oficina> getOficinasPagoManualWithFilterAndSort(
				PagoManualFilter consultaFilter, PagoManualSort consultaSort,
				int rowStart, int rowEnd, String codZonas) throws BusinessException;
	/**
	 * A partir de un filter hace un count de la tabla oficinas
	 * 07/05/2014 U029769
	 * @param filter
	 * @return int
	 * @throws BusinessException
	 */
	int getOficinasPagoManualCountWithFilter(
			PagoManualFilter filter) throws BusinessException;
	/**
	 * Edita el campo pago manual de una oficina
	 * 07/05/2014 U029769
	 * @param oficinaBean
	 * @return Map<String, Object>
	 * @throws BusinessException
	 */
	Map<String, Object> editaOficina(Oficina oficinaBean, List<Zona> zonaListSel) throws BusinessException;
	
	/**
	 * Borra una oficina
	 * 07/05/2014 U029769
	 * @param oficinaBean
	 * @return Map<String, Object>
	 * @throws BusinessException
	 */
	Map<String, Object> borraOficina(Oficina oficinaBean)throws BusinessException;
	/**
	 * Alta de oficina
	 * 08/05/2014 U029769
	 * @param oficinaBean
	 * @return Map<String, Object>
	 */
	Map<String, Object> altaOficina(Oficina oficinaBean, List<Zona> zonaListSel)throws BusinessException;
	/**
	 * Cambio masivo de oficinas
	 * 16/07/2014 U029769
	 * @param listaIdsMarcados_cm
	 * @param oficinaBean
	 * @return
	 */
	Map<String, String> cambioMasivo(String listaIdsMarcados_cm, Oficina oficinaBean, List<Zona> zonaListSel);
	/**
	 * Metodo para no perder el filtro al volver de un cambio masivo.
	 *  Recupera el limit de sesion para pasarlo al bean de cambio Masivo.
	 * 16/07/2014 U029769
	 * @param attribute
	 * @return
	 */
	Oficina getCambioMasivoBeanFromLimit(Limit attribute);
	/* Pet. 63701 ** MODIF TAM (25.06.2021) ** inicio */
	public List<Zona> obtenerListaZonas(BigDecimal codEntidad) throws DAOException;
	public List<String> obtenerListaNombZonasOficina(BigDecimal codEntidad, BigDecimal codoficina) throws DAOException;
	public List<Zona> obtenerListaZonasOficina(BigDecimal codEntidad, BigDecimal codoficina) throws DAOException;
	/**
	 * Cambio masivo de oficinas
	 * 16/07/2014 U029769
	 * @param listaIdsMarcados_cm
	 * @param oficinaBean
	 * @return
	 */
	Map<String, String> adiccionMasiva(String listaIdsMarcados_cm, Oficina oficinaBean, List<Zona> zonaListSel);
	
	public List<Oficina> getAllFilteredAndSorted() throws BusinessException;
	 
}
