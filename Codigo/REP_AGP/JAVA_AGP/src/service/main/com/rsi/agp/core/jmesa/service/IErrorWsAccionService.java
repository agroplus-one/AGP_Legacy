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
import com.rsi.agp.core.jmesa.filter.ErrorWsAccionFilter;
import com.rsi.agp.core.jmesa.sort.ErrorWsAccionSort;
import com.rsi.agp.dao.tables.commons.ErrorWs;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.ErrorWsTipo;
import com.rsi.agp.dao.tables.commons.Perfil;

public interface IErrorWsAccionService{
	
	/**
	 * Devuelve el listado de errores ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la bísqueda de los errores
	 * @param sort Ordenación para la bísqueda de los errores
	 * @param rowStart Primer registro que se mostrará
	 * @param rowEnd Ãšltimo registro que se mostrará
	 * @return
	 * @throws BusinessException
	 */
	public Collection<ErrorWsAccion> getErrorWsAccionWithFilterAndSort(ErrorWsAccionFilter filter, ErrorWsAccionSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el nímero de errores que se ajustan al filtro pasado por parámetro
	 * @param filter
	 * @return
	 */
	public int getConsultaErrorWsCountWithFilter(ErrorWsAccionFilter filter);

	
	public String getTablaErrorWsAccion (HttpServletRequest request, HttpServletResponse response, ErrorWsAccion errorWs, String origenLlamada);
	
	/**
	 * Da de alta un ErrorWs a partir de un bean de ErrorWsAccion
	 * @param errorWsAccion
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> altaErrorWs(ErrorWsAccion errorWsAccion) throws BusinessException;
	
	/**
	 * alta de error
	 * @param errorWs
	 * @throws BusinessException
	 */
	public Map<String, Object> altaErrorWsAccion(ErrorWsAccion errorWs) throws BusinessException;
	/**
	 * Baja  de error
	 * @param errorWs
	 * @throws BusinessException
	 */
	public void bajaErrorWsAccion(ErrorWsAccion errorWs)throws BusinessException;
		
	/**
	 * Modifica la tabla de errorWsAccion
	 * @param sobreprecio
	 * @throws BusinessException
	 */
	public Map<String, Object> editaErrorWsAccion(ErrorWsAccion errorWs) throws BusinessException;
	
	/**
	 * @param idErrorWsAccion
	 * @return
	 * @throws BusinessException
	 */
	public ErrorWsAccion getErrorWsAccion(Long idErrorWsAccion)throws BusinessException;
	
	/** Obtiene los errores Ws.
	 * @param 
	 * @return
	 */
	public List<ErrorWs> getTodosErrores();

	
	/**
	 * Copia los errores webservice del plan/línea y/o servicio origen al destino
	 * @param planOrig
	 * @param lineaOrig
	 * @param planDest
	 * @param lineaDest
	 * @param servicioOrig
	 * @param servicioDest
	 * @throws BusinessException
	 */
	public Map<String, Object> replicar (BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, BigDecimal lineaDest, String servicioOrig, String servicioDest) throws BusinessException;
	
	/** DAA 12/02/2013  Metodo para recuperar un String con todos los Ids de detalleClase segun el filtro
	 * 
	 * @param errorWsAccionFilter
	 * @return listaIdsTodos
	 */
	public String getlistaIdsTodos(ErrorWsAccionFilter errorWsAccionFilter);
	
	/** DAA 13/02/2013  Metodo para actualizar un String con todos los Ids de ErrorWs segun el filtro
	 * 
	 * @param listaIdsMarcados_cm
	 * @param errorWsAccionBean
	 * @throws DAOException 
	 */
	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, ErrorWsAccion errorWsAccionBean) throws DAOException;
	
	/** DAA 05/03/2013  Recupera el limit de sesion para pasarlo al bean de cambio Masivo.
	 * @param Limit
	 * @return ErrorWsAccion
	 */
	public ErrorWsAccion getCambioMasivoBeanFromLimit(Limit consultaErrorWsAccion_LIMIT);

	public boolean checkCodigoErrorWS(ErrorWsAccion errorWs, Map<String, Object> parameters);
	
	public List<ErrorWsTipo> obtenerListaTiposWsError() throws DAOException;
	
	public List<Perfil> obtenerListaPerfiles() throws DAOException;
}