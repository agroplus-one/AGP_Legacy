package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.SocioId;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;

@SuppressWarnings("unchecked")
public interface ISocioDao extends GenericDao {
	public List<PolizaSocio> getPolizasByIdSocio(SocioId socioId) throws DAOException;
	public Set<Socio> getSociosByAseguradoPoliza(Asegurado asegurado, Poliza poliza) throws DAOException;
	public List<Socio> getSociosActivosByAsegurado(Asegurado asegurado) throws DAOException;
	public List<Poliza> getPolizasSinGrabarByIdAsegurado(Long id) throws DAOException;
	public Set<Socio> getSociosByPolizaConSubvenciones(Poliza poliza) throws DAOException;
	/**
	 * Obtiene el siguiente orden de socio para el idpoliza indicado como parámetro
	 * @param idpoliza
	 * @return
	 * @throws DAOException
	 */
	public BigDecimal getOrdenPolizaSocio (Long idpoliza) throws DAOException;
	/**
	 * Actualiza el campo orden de todos los socios asociados a la póliza indicada
	 * @param idpoliza
	 * @throws DAOException
	 */
	public void actualizaOrdenPolizaSocio (Long idpoliza) throws DAOException;
}
