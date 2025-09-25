package com.rsi.agp.dao.models.poliza;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;

public interface IModuloPolizaDao {
	
	public ModuloPoliza getModuloPoliza (Long idPoliza, Long lineaseguroid) throws DAOException;

}
