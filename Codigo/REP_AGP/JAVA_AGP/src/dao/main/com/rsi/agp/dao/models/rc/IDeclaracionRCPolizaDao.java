package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.copy.Asegurado;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.reduccionCap.Estado;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

@SuppressWarnings("rawtypes")
public interface IDeclaracionRCPolizaDao extends GenericDao {

	/*public void eliminarDeclaracionModificacionPoliza(Long idAnexoModificacion) throws DAOException;

	public boolean listByIdPolizaBorradorYDefinitivo(Long idPoliza) throws DAOException;

	public List<AnexoModificacion> listarByIdPolizaBorradorYDefinitivo(Long idPoliza) throws DAOException;

	public List<AnexoModificacion> listAnexModifByIdPoliza(Long idPoliza) throws DAOException;

	public AnexoModificacion getAnexoModifById(Long idAnexo) throws DAOException;*/

	public ReduccionCapital saveAnexoModificacion (ReduccionCapital reduccionCap ,String codUsuario,Estado estado,boolean esAlta) throws DAOException;

	/*public Asegurado getAseguradoCopy(Long idcopy) throws DAOException;

	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException;

	public List<com.rsi.agp.dao.tables.anexo.Estado> getEstadosAnexoModificacion() throws DAOException;

	public List getSegSocialAnexoAnterior(Long idpoliza) throws DAOException; */

	public BigDecimal isEditableAMCuponCaducado(Long idAnexo, Long idPoliza) throws DAOException; /*

	public AnexoModificacion getAnexoModifById(Long idAnexo, boolean evict) throws DAOException;

	public List<Parcela> getParcelas(Long idAnexo) throws DAOException;

	/**
	 * Comprueba que todos los campos de gastos de la póliza renovable asociada a la
	 * póliza sobre la que se quiere dar de alta el anexo están correctamente
	 * informados
	 * 
	 * @param idPoliza
	 *            Identificador de la póliza sobre la que se quiere dar de alta el
	 *            anexo
	 * @return Boolean indicando si se puede dar de alta el anexo
	 * /
	public boolean isValidoAnexoRenovable(BigDecimal idPoliza);

	/* ESC-14312 ** MODIF TAM (21.06.2021) ** Inicio * /
	public void saveCoberturasAnexo(Cobertura cob) throws DAOException;

	/* ESC-14671 ** MODIF TAM (26.07.2021) ** Inicio * /
	public BigDecimal getfilaRiesgoCubModulo(Long lineaseguroid, String codmodulo, BigDecimal cPmodulo,
			BigDecimal codRiesgoCub) throws Exception; */
}
