package com.rsi.agp.dao.models.rc;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWAnulacionRC;
@SuppressWarnings("rawtypes")
public interface IEnviosSWAnulacionDao extends GenericDao {
	public void saveEnviosSWAnulacion (EnviosSWAnulacionRC enviosSWAnulacion);
}
