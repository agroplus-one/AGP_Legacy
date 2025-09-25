package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ReduccionCapitalFilter;
import com.rsi.agp.core.jmesa.sort.ReduccionCapitalSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.Estado;
import com.rsi.agp.dao.tables.reduccionCap.EstadoCuponRC;
import com.rsi.agp.dao.tables.reduccionCap.Parcela;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalDistribucionCostes;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapitalUtilidades;
 
public interface IReduccionCapitalDao extends GenericDao {
	
	public List<ReduccionCapital> list(ReduccionCapital reduccionCapital) throws DAOException;	
	public List<Estado> getEstadosReduccionCapital() throws DAOException;
	public Poliza getPoliza(Long idPoliza) throws DAOException;
	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException;
	public ReduccionCapital getReduccionCapital(Long idReduccionCapital) throws DAOException;
	public CuponRC getCuponRCByIdCuponRC(Long idCuponRC) throws DAOException;
	public void guardarReduccionCapital (ReduccionCapital reduccionCapital) throws DAOException;
	//P0079361 INICIO
	public ReduccionCapital saveReduccionCapital(ReduccionCapital reduccionCapital) throws DAOException;
	public void saveParcelas(Set<Parcela> parcelas) throws DAOException;
	public void saveDistCostes(Set<RedCapitalDistribucionCostes> distribucionCostes) throws DAOException;
	//P0079361 FIN
	public void eliminarReduccionCapital(ReduccionCapital rc) throws DAOException;
	public boolean tieneEstado(Long idReduccionCapital,Short estado) throws DAOException;
	public boolean tieneReduccionesCapital(Long idPoliza) throws DAOException;
	public boolean getCountgruposNegocio(Long lineaseguroId) throws DAOException;
	public String getDescGrupoNegocio(Character grupoNegocio);
	public CuponRC nuevoCupon(String idCupon) throws DAOException;

	/**
	 * Devuelve el listado de redCapital ordenados que se ajustan al filtro indicado
	 * @param filter Filtro para la busqueda de redCapital 
	 * @param sort Ordenacion para la busqueda campos redCapital
	 * @param rowStart Primer registro que de redCapital que se muestra
	 * @param rowEnd ultimo registro que de redCapital que se muestra
	 * @return
	 * @throws BusinessException
	 */
	public Collection<ReduccionCapitalUtilidades> getReduccionCapitalWithFilterAndSort(ReduccionCapitalFilter filter, ReduccionCapitalSort sort, int rowStart, int rowEnd
			//P0079361
			,final String fechadanioId, 
			final String fechadanioIdHasta,
			final String fechaEnvioId,
			final String fechaEnvioIdHasta,
			final String fechaEnvioPolId,
			final String fechaEnvioPolIdHasta,
			final String strTipoEnvioId
			//P0079361
			) throws BusinessException;
	
	/**
	 * Devuelve el número de redCapital que se ajustan al filtro pasado como parámetro
	 * @param filter Filtro para la búsqueda de redCapital
	 * @return
	 */
	public int getReduccionCapitalCountWithFilter(final ReduccionCapitalFilter filter
			//P0079361
			,String fechadanioId, 
			String fechadanioIdHasta,
			String fechaEnvioId,
			String fechaEnvioIdHasta,
			String fechaEnvioPolId,
			String fechaEnvioPolIdHasta,
			String strTipoEnvioId
			//P0079361
			) throws BusinessException;
	public boolean isRCconParcelas(Long id)throws DAOException;
	public EstadoCuponRC getEstadoCupon(Long idEstado) throws DAOException;
	public void actualizar(ReduccionCapital redCap) throws DAOException;
	public ReduccionCapital getRCByIdRC(Long id) throws DAOException;
	public Clob getAcuseConfirmacion(long idRC) throws DAOException;

}
