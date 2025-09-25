package com.rsi.agp.dao.models.poliza;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.DatosAval;

@SuppressWarnings("rawtypes")
public interface IDatosAvalDao extends GenericDao {
	
	public void Add(DatosAval datosAval) throws DAOException;

	public DatosAval GetDatosAval(Long idPoliza) throws DAOException;

	public void DeleteDatosAval(DatosAval datosAval) throws DAOException;
}
