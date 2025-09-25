package com.rsi.agp.core.jmesa.service;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ClaseMtoFilter;
import com.rsi.agp.core.jmesa.sort.ClaseMtoSort;
import com.rsi.agp.dao.tables.admin.Clase;

public interface IClaseMtoService{
	
	/**
	 * Devuelve el listado de clases ordenadas que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de los errores
	 * @param sort Ordenación para la búsqueda de los errores
	 * @param rowStart Primer registro que se mostrará
	 * @param rowEnd Último registro que se mostrará
	 * @return
	 * @throws BusinessException
	 */
	public Collection<Clase> getClaseMtoWithFilterAndSort(ClaseMtoFilter filter, ClaseMtoSort sort, int rowStart, int rowEnd,String descripcion) throws BusinessException;
	
	/**
	 * Devuelve el número de clases que se ajustan al filtro pasado por parámetro
	 * @param filter
	 * @return
	 */
	public int getConsultaClaseMtoCountWithFilter(ClaseMtoFilter filter,String descripcion);

	public String getTablaClaseMto (HttpServletRequest request, HttpServletResponse response, Clase clase, String origenLlamada,String descripcion);
	
	/**
	 * alta de clase
	 * @param clase
	 * @throws BusinessException
	 */
	public Map<String, Object> altaClaseMto(Clase clase) throws BusinessException;
	/**
	 * Baja  de clase
	 * @param clase
	 * @throws BusinessException
	 */
	public Map<String, Object> bajaClaseMto(Clase clase)throws BusinessException;
		
	/**
	 * Modifica la tabla de ClaseMto
	 * @param clase
	 * @throws BusinessException
	 */
	public Map<String, Object> editaClaseMto(Clase clase) throws BusinessException;
	
	/**
	 * @param idClase
	 * @return
	 * @throws BusinessException
	 */
	public Clase getClase(Long idClase)throws BusinessException;
	
	/**Comprueba que exista el lineaseguroid 
	 * @param clase 
	 * @return
	 * @throws BusinessException
	 */
	public boolean existeLineaseguroid(Clase clase) throws BusinessException;
	
	/**Metodo para replicar todas las clases y sus claseDetalle de un Plan y Linea. 
	 * @param lineaDestinoReplica 
	 * @param planDestinoReplica 
	 * @param clase 
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> replicaPlanLineaClaseMto(Clase claseMtoBean, String planDestinoReplica, String lineaDestinoReplica);

}
