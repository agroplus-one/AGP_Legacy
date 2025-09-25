package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.DatosAval;

public class DatosAvalDao extends BaseDaoHibernate implements IDatosAvalDao {

	@Override
	public void Add(DatosAval datosAval) throws DAOException {
		try {
			this.saveOrUpdate(datosAval);
		}catch(Exception e) {
			logger.debug("Se ha producido un error añadiendo los datos del aval en la base de datos. "+e);
			throw new DAOException("Se ha producido un error añadiendo los datos del aval en la base de datos.",e);
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public DatosAval GetDatosAval(Long idPoliza) throws DAOException {
		List<DatosAval> lista=null;
		DatosAval da=null;
		try {
			lista=	this.getObjects(DatosAval.class, "idpoliza", idPoliza);
			if(null!=lista && lista.size()>0) {
				da=(DatosAval)lista.toArray()[0];
			}
				
			
		}catch(Exception e) {
			logger.debug("Se ha producido un error seleccionado los datos del en la base de datos. "+e);
			throw new DAOException("Se ha producido un error seleccionado los datos del aval en la base de datos.",e);
		}
		return da;
		
	}

	@Override
	public void DeleteDatosAval(DatosAval datosAval) throws DAOException {		
		try {
			this.delete(datosAval)	;		
		}catch(Exception e) {
			logger.debug("Se ha producido un error eliminando los datos del aval en la base de datos. "+e);
			throw new DAOException("Se ha producido un error eliminando los datos del aval en la base de datos.",e);
		}
		
	}

}
