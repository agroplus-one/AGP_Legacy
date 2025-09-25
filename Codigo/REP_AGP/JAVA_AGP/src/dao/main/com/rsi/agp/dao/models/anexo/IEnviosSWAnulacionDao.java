package com.rsi.agp.dao.models.anexo;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.EnviosSWAnulacion;

@SuppressWarnings("rawtypes")
public interface IEnviosSWAnulacionDao extends GenericDao {
	public void saveEnviosSWAnulacion (EnviosSWAnulacion enviosSWAnulacion);
}
