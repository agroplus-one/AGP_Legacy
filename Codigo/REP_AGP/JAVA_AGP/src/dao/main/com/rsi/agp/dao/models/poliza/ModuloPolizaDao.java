package com.rsi.agp.dao.models.poliza;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;

public class ModuloPolizaDao extends BaseDaoHibernate implements IModuloPolizaDao{

	@Override
	public ModuloPoliza getModuloPoliza(Long idPoliza, Long lineaseguroid) throws DAOException {

		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(ModuloPoliza.class);
			criteria.add(Restrictions.eq("id.idpoliza", idPoliza));			
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));			
			
			// Obtiene la lista de referencias y si ésta no es vacía devuelve el primer resultado
			return (ModuloPoliza)criteria.list().get(0);
		}
		catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",ex);
		}
	}

}
