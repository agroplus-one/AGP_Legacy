package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.Poliza;


/**
 * Interfaz
 */
public interface IIncompatibilidadClaseDao extends GenericDao {
	public boolean isCompatible(List<Poliza> listPolizas)throws DAOException;
	public boolean isCompatible(Poliza poliza)throws DAOException;
}
