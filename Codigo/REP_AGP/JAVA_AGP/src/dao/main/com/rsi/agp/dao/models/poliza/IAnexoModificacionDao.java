package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.AnexoModificacionFilter;
import com.rsi.agp.core.jmesa.sort.AnexoModificacionSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.poliza.Poliza;

@SuppressWarnings("rawtypes")
public interface IAnexoModificacionDao extends GenericDao {

	public List findFiltered(Class clase, String[] parametros, Object[] valores, String orden) throws DAOException;

	Collection<AnexoModificacion> getAnexoModificacionWithFilterAndSort(AnexoModificacionFilter filter,
			AnexoModificacionSort sort, int rowStart, int rowEnd) throws BusinessException;

	int getAnexoModificacionCountWithFilter(AnexoModificacionFilter filter) throws BusinessException;

	AnexoModificacion getAnexoByIdCupon(Object idCupon) throws DAOException;

	Clob getAcuseConfirmacion(Long idAnexo) throws DAOException;

	void actualizar(AnexoModificacion am) throws DAOException;

	public Clob getXMLSituacionActualizada(Long idAnexo) throws DAOException;

	// public Collection<AnexoModificacion> getAnexoModificacion(Long idPoliza,
	// String tipoModificacion) throws BusinessException;

	public boolean existenExplotacionesEnAnexo(Long idAnexoModificacion) throws DAOException;

	/**
	 * Dado un anexo, comprueba si se han realizado modificaciones en sus
	 * explotaciones
	 * 
	 * @param idAnexoModificacion
	 * @return
	 * @throws DAOException
	 */
	public boolean isAnexoExplotacionesConModificaciones(Long idAnexoModificacion) throws DAOException;

	/**
	 * Dado un anexo, comprueba si se han realizado modificaciones en sus coberturas
	 * @param idAnexoModificacion
	 * @return
	 * @throws DAOException
	 */
	public boolean isAnexoCoberturasConModificaciones(Long idAnexoModificacion) throws DAOException;
	
	/**
	 * Dado un anexo, comprueba si se han realizado modificaciones en sus subvenciones
	 * @param idAnexoModificacion
	 * @return
	 * @throws DAOException
	 */
	public boolean isAnexoSubvencionesConModificaciones(Long idAnexoModificacion) throws DAOException;
	
	/**
	 * Dado un anexo, comprueba si se han realizado modificaciones en sus parcelas
	 * 
	 * @param idAnexoModificacion
	 * @return
	 * @throws DAOException
	 */
	public boolean isAnexoParcelasConModificaciones(Long idAnexoModificacion) throws DAOException;

	public boolean getCountgruposNegocio(Long lineaseguroId) throws DAOException;

	public String getDescGrupoNegocio(Character grupoNegocio);

	public Poliza getPolizaById(Long idPoliza) throws DAOException;

	public boolean checkTCRyD(List<Integer> lista) throws DAOException;

	public boolean checkExplotacionesTCRyD(Long idAnexo) throws DAOException;

	/* Pet. 78691 ** MODIF TAM (15.12.2021) ** Inicio */
	public void guardarCaractExplAnx(Long idAnexo, BigDecimal caractExplAnx) throws DAOException;

	public AnexoModificacion saveAnexoModificacion(AnexoModificacion anexo);
}

