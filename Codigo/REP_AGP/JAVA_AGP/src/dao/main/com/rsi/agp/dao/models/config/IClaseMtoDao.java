package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ClaseMtoFilter;
import com.rsi.agp.core.jmesa.sort.ClaseMtoSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Clase;

@SuppressWarnings("rawtypes")
public interface IClaseMtoDao extends GenericDao{
	
	/**
	 * Devuelve el listado de clases ordenadas que se ajustan al filtro indicado 
	 * @param filter Filtro para la busqueda de errores
	 * @param sort Ordenacion para la busqueda errores
	 * @param rowStart Primer registro que se mostrara
	 * @param rowEnd Ultimo registro que se mostrara
	 * @return
	 * @throws BusinessException
	 */
	public Collection<Clase> getClaseMtoWithFilterAndSort(ClaseMtoFilter filter, ClaseMtoSort sort, int rowStart, int rowEnd,String descripcion) throws BusinessException;
	
	/**
	 * Devuelve el numero de clases que se ajustan al filtro pasado como parametro
	 * @param filter
	 * @return
	 */
	public int getConsultaClaseMtoCountWithFilter(final ClaseMtoFilter filter,final String descripcion);
	
	/**
	 * Replica todas las clases y sus detalles para un plan y linea.
	 * @param lineaSeguroIdDestino
	 * @param lineaSeguroIdOrigen
	 * @param clase 
	 * @throws DAOException 
	 */
	public void replicaPlanLinea(Long lineaSeguroIdDestino,Long lineaSeguroIdOrigen, BigDecimal clase) throws DAOException;
	
	/**Metodo que comprueba si el lineaseguroid de destino tiene clases
	 * 
	 * @param lineaSeguroIdDestino
	 * @return
	 */
	public boolean existeClaseReplica(Long lineaSeguroIdDestino, BigDecimal clase);
	
	/**
	 * Compueba si una clase esta cargada por un usuario 
	 * @param id
	 * @return true esta cargada ; False no esta cargada
	 */
	public boolean isCargadaClase(Long id);
				
}
