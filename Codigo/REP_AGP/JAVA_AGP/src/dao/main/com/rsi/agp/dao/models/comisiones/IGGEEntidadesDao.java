package com.rsi.agp.dao.models.comisiones;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.GGEEntidades;

@SuppressWarnings("rawtypes")
public interface IGGEEntidadesDao extends GenericDao{

	public GGEEntidades getLastGGEPlan() throws DAOException;
	
	public GGEEntidades getLastGGEPlan(Long year) throws DAOException;

	public GGEEntidades getEntidadByIdPlan(Long idplan)throws DAOException ;

	public List<GGEEntidades> getAll() throws DAOException;	
	 
}
