package com.rsi.agp.dao.models.anexo;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.EnviosSWConfirmacion;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWConfirmacionRC;

@SuppressWarnings("rawtypes")
public interface IEnviosSWConfirmacionDao extends GenericDao {
	void guardarConfirmacion (EnviosSWConfirmacion enviosSWConfirmacion) throws DAOException;
	//P0079361
	void guardarConfirmacionRC (EnviosSWConfirmacionRC enviosSWConfirmacionRC) throws DAOException;
	//P0079361
}
