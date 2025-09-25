package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.SiniestrosFilter;
import com.rsi.agp.core.jmesa.sort.SiniestrosSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.siniestro.EstadoSiniestro;
import com.rsi.agp.dao.tables.siniestro.Siniestro;
import com.rsi.agp.dao.tables.siniestro.SiniestrosAnteriores;
import com.rsi.agp.dao.tables.siniestro.SiniestrosUtilidades;

public interface ISiniestroDao extends GenericDao {

	public List<Siniestro> list(Siniestro siniestro) throws DAOException;	
	public List<EstadoSiniestro> getEstadosSiniestro () throws DAOException;
	public Poliza getPoliza(Long idPoliza) throws DAOException;
	public Siniestro getSiniestro(Long idSiniestro) throws DAOException;
	public Siniestro guardarSiniestro (Siniestro siniestro, Usuario usuario, boolean facturacion) throws DAOException;
	public void eliminarSiniestro(Long idSiniestro) throws DAOException;
	/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio */
	public void bajaSiniestro(Long idSiniestro) throws DAOException;
	/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Fin */
	public boolean tieneEstado(Long idSiniestro,Short estado) throws DAOException;
	public Map<String, Object> buscarDescripciones (Siniestro siniestro) throws DAOException;
	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException;
	
	/**
	 * Devuelve el listado de siniestros ordenados que se ajustan al filtro indicado
	 * @param filter Filtro para la busqueda de siniestros 
	 * @param sort Ordenacion para la busqueda campos siniestros
	 * @param rowStart Primer registro que de siniestros que se muestra
	 * @param rowEnd Ultimo registro que de siniestros que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<SiniestrosUtilidades> getSiniestrosWithFilterAndSort(SiniestrosFilter filter, SiniestrosSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el numero de siniestros que se ajustan al filtro pasado como parametro
	 * @param filter Filtro para la busqueda de siniestros
	 * @return
	 */
	public int getSiniestrosCountWithFilter(final SiniestrosFilter filter) throws BusinessException;
	
	/**DAA 09/01/2013 Obtiene la descripcion del riesgo del siniestro enviado correcto 
	 * anterior al siniestro seleccionado
	 * @param idSiniestro
	 * @return
	 * @throws Exception 
	 */
	public String getRiesgoSiniestroAnterior(Integer idSiniestro) throws Exception;
	public boolean isSiniestroConParcelas(Long idSineistro)throws DAOException;
	
	/**
	 * Contamos el número total de siniestros.
	 * @author U029114 21/06/2017
	 * @param idPoliza
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal getNumTotalSiniestros(Long idPoliza) throws DAOException;
	
	/**
	 * Obtenemos un nuevo numsiniestro del siniestro.
	 * @author U029114 27/06/2017
	 * @param idPoliza
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal getNuevoNumSiniestro(Long idPoliza) throws DAOException;
	public List<SiniestrosAnteriores> getRiesgoSiniestrosAnteriores(Integer idSiniestro) throws Exception;

}
