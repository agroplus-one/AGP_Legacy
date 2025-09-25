package com.rsi.agp.core.jmesa.service;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.limit.Limit;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ClaseDetalleFilter;
import com.rsi.agp.core.jmesa.sort.ClaseDetalleSort;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;


public interface IClaseDetalleService{
	
	/**
	 * Devuelve el listado de los detalles de clases ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de las clases
	 * @param sort Ordenación para la búsqueda de las clases
	 * @param rowStart Primer registro que se mostrará
	 * @param rowEnd Último registro que se mostrará
	 * @return
	 * @throws BusinessException
	 */
	public Collection<ClaseDetalle> getClaseDetalleWithFilterAndSort(ClaseDetalleFilter filter, ClaseDetalleSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el número de clases que se ajustan al filtro pasado por parámetro
	 * @param filter
	 * @return
	 */
	public int getConsultaClaseDetalleCountWithFilter(ClaseDetalleFilter filter);

	
	public String getTablaClaseDetalle (HttpServletRequest request, HttpServletResponse response, ClaseDetalle claseDetalle,String origenLlamada);
	
	/**
	 * alta/modificación de claseDetalle
	 * @param claseDetalle
	 * @throws BusinessException
	 */
	public Map<String, Object> insertOrUpdateClaseDetalle(ClaseDetalle claseDetalle, Long lineaseguroid) throws BusinessException;
	/**
	 * Baja  de ClaseDetalle
	 * @param claseDetalle
	 * @throws BusinessException
	 */
	public void bajaClaseDetalle(ClaseDetalle claseDetalle)throws BusinessException;

	/**
	 * @param idClaseDetalle
	 * @return
	 * @throws BusinessException
	 */
	public ClaseDetalle getClaseDetalle (Long idClaseDetalle)throws BusinessException;
	
	/** DAA 06/02/2013  Metodo para recuperar un String con todos los Ids de detalleClase segun el filtro
	 * 
	 * @param claseDetalleBusqueda
	 * @return
	 */
	public String getlistaIdsTodos(ClaseDetalleFilter claseDetalleFilter);
	
	/** DAA 11/02/2013  Metodo para actualizar un String con todos los Ids de detalleClase segun el filtro
	 * 
	 * @param listaIdsMarcados_cm
	 * @param claseDetalleBean
	 */
	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm,ClaseDetalle claseDetalleBean,String cicloCultivoCheck,
			String sistemaCultivoCheck,String tipoCapitalCheck,String tipoPlantacionCheck);
	
	/** TMR 05/03/2013  Recupera el limit de sesion para pasarlo al bean de cambio Masivo.
	 * @param Limit
	 * @return ClaseDetalle
	 */
	public ClaseDetalle getCambioMasivoBeanFromLimit(Limit consultaClaseDetalle_LIMIT);
}
