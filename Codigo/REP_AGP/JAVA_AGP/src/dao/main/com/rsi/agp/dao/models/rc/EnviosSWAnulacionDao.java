package com.rsi.agp.dao.models.rc;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.EnviosSWAnulacion;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWAnulacionRC;

public class EnviosSWAnulacionDao extends BaseDaoHibernate implements IEnviosSWAnulacionDao {

	@Override
	public void saveEnviosSWAnulacion(EnviosSWAnulacionRC enviosSWAnulacion) {
		try {
			this.saveOrUpdate(enviosSWAnulacion);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al guardar el registro de envio al SW de Anulacion", e);
		}
	}

}
