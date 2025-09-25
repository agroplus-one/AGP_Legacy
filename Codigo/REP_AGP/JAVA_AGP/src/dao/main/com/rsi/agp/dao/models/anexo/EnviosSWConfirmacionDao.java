package com.rsi.agp.dao.models.anexo;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.EnviosSWConfirmacion;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWConfirmacionRC;

public class EnviosSWConfirmacionDao extends BaseDaoHibernate implements IEnviosSWConfirmacionDao {

	@Override
	public void guardarConfirmacion(EnviosSWConfirmacion enviosSWConfirmacion) throws DAOException {
		try {
			this.saveOrUpdate(enviosSWConfirmacion);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al guardar el registro de confirmacion", e);
			throw new DAOException(e);
		}
	}

	//P0079361
	@Override
	public void guardarConfirmacionRC(EnviosSWConfirmacionRC enviosSWConfirmacionRC) throws DAOException {
		try {
			this.saveOrUpdate(enviosSWConfirmacionRC);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al guardar el registro de confirmacion de RC", e);
			throw new DAOException(e);
		}
	}
	//P0079361
}
