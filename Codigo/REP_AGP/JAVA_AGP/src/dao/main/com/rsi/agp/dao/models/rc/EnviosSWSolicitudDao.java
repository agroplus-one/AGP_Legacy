package com.rsi.agp.dao.models.rc;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWSolicitudRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWSolicitudRC;

public class EnviosSWSolicitudDao extends BaseDaoHibernate implements IEnviosSWSolicitudDao {

	@Override
	public EnviosSWSolicitudRC getEnviosSWSolicitud(String idCupon) throws DAOException {

		try {
			List<EnviosSWSolicitudRC> lista = this.getObjects(EnviosSWSolicitudRC.class, "idcupon", idCupon);
			
			if (lista != null && lista.size()>0) return lista.get(0);
			else throw new DAOException("No existe registro de envio al SW de Solicitud del cupon " + idCupon);
			
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener el registro de envio al SW de Solicitud del cupon " + idCupon, e);
			throw new DAOException(e);
		}
		
	}

}
