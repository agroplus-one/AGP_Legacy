package com.rsi.agp.dao.models.anexo;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.EnviosSWAnulacion;

public class EnviosSWAnulacionDao extends BaseDaoHibernate implements IEnviosSWAnulacionDao {

	@Override
	public void saveEnviosSWAnulacion(EnviosSWAnulacion enviosSWAnulacion) {
		try {
			this.saveOrUpdate(enviosSWAnulacion);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al guardar el registro de envio al SW de Anulacion", e);
		}
	}

}
