package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("rawtypes")
public interface ILineaDao extends GenericDao {
	
	public List<Linea> getAll()throws DAOException;
	public Long getLineaSeguroId(BigDecimal codLinea, BigDecimal codPlan)throws DAOException;
	public Linea getLinea(BigDecimal codLinea, BigDecimal codPlan)throws DAOException;
	
	// chequea si existe Linea en tabla "TB_LINEAS"
	public boolean existeLinea(BigDecimal codplan, BigDecimal codlinea);		
	public boolean existePlan(BigDecimal codplan) throws DAOException;	
	public Linea insertaLineaGenerica(BigDecimal codplan);
	
	/**
	 * Comprobar que el plan/linea de la poliza asociada corresponde con el ultimo plan cargado para esa linea y que este activo y no bloqueado
	 * @param codLinea
	 * @param codPlan
	 * @return
	 * @throws DAOException
	 */
	public boolean noExisteLineaMayorPlanActivo(BigDecimal codLinea, BigDecimal codPlan) throws DAOException;
	
	public boolean esLineaGanado(final Long lineaseguroId) throws DAOException;
	
	Linea getLinea(String lineaseguroid);
}
