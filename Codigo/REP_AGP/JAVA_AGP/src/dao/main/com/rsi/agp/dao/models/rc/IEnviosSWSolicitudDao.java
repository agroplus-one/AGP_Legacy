package com.rsi.agp.dao.models.rc;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWSolicitudRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWSolicitudRC;

@SuppressWarnings("rawtypes")
public interface IEnviosSWSolicitudDao extends GenericDao {
	public EnviosSWSolicitudRC getEnviosSWSolicitud (String idCupon) throws DAOException;
}
