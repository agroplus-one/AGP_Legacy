package com.rsi.agp.dao.models.anexo;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.EnviosSWSolicitud;

@SuppressWarnings("rawtypes")
public interface IEnviosSWSolicitudDao extends GenericDao {
	public EnviosSWSolicitud getEnviosSWSolicitud (String idCupon) throws DAOException;
}
