package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ErrorWsAccionFilter;
import com.rsi.agp.core.jmesa.sort.ErrorWsAccionSort;
import com.rsi.agp.dao.filters.commons.ErrorWsIdFiltro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.ErrorPerfiles;
import com.rsi.agp.dao.tables.commons.ErrorWs;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.ErrorWsTipo;
import com.rsi.agp.dao.tables.commons.Perfil;


@SuppressWarnings("rawtypes")
public interface IErrorWsAccionDao extends GenericDao {
	
	/**
	 * Devuelve el listado de errores ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la busqueda de errores
	 * @param sort Ordenacion para la busqueda errores
	 * @param rowStart Primer registro que se mostrara
	 * @param rowEnd Último registro que se mostrara
	 * @return
	 * @throws BusinessException
	 */
	public Collection<ErrorWsAccion> getErrorWsAccionWithFilterAndSort(ErrorWsAccionFilter filter, ErrorWsAccionSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el numero de errores que se ajustan al filtro pasado como parametro
	 * @param filter
	 * @param vieneDeCambioMasivo 
	 * @param idErrorWs 
	 * @return
	 */
	public int getConsultaErrorWsCountWithFilter(final ErrorWsAccionFilter filter, final boolean vieneDeCambioMasivo, final Long idErrorWs);
		
	public List<ErrorWs> getTodosErrores();
	
	/**
	 * Llama al procedimiento encargado de copiar los errores webservice del lineaseguroid y/o servicio origen al de destino
	 * @param origen
	 * @param destino
	 * @param servicioOrig
	 * @param servicioDest
	 * @throws DAOException
	 */
	public void replicar (BigDecimal origen, BigDecimal destino, String servicioOrig, String servicioDest) throws DAOException;
	/**
	 * Obtiene los errores de la tabla TB_COD_ERRORES_WS_ACCION a partir de un String[] de cods de error
	 * @param codsError
	 * @return
	 * @throws DAOException
	 */
	public boolean hayErroresRechazoMostrarPerfilCero(List<String> errores, Long lineaseguroid, String servicio);
	
	
	public Long getErrorWS(ErrorWsAccion errorWs) throws DAOException;
	
	/** DAA 12/02/2013  Metodo para recuperar un String con todos los Ids de ErrorWs segun el filtro
	 * 
	 * @param consultaFilter
	 * @return listaIdsTodos
	 */
	public String getlistaIdsTodos(ErrorWsAccionFilter consultaFilter);

	/**
	 * Recupera una lista de todos los objetos WsErrorTipo (para poder formar los combos)
	 * @return
	 * @throws DAOException
	 */
	public List<ErrorWsTipo> obtenerListaTiposWsError() throws DAOException;
	
	/**
	 * Recupera una lista de perfiles
	 * @throws DAOException
	 */
	public List<Perfil> obtenerListaPerfiles() throws DAOException;
	
	public List getErrores(ErrorWsIdFiltro errorWsidFiltro);
	
	public List<ErrorPerfiles> obtenerPerfiles(ErrorWsAccion accion);
}