package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.commons.Comarca;

public class ComarcaDao extends BaseDaoHibernate implements IComarcaDao {

	@Override
	public boolean checkComarcaExists(BigDecimal codcomarca, BigDecimal codprovincia) throws DAOException {
		
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(Comarca.class);
			criteria.add(Restrictions.eq("id.codprovincia", codprovincia));
			criteria.add(Restrictions.eq("id.codcomarca", codcomarca));
			return criteria.list().size() >0;
		}
		catch (Exception e) {
			throw new DAOException ("Ocurrió un error al buscar las comarcas", e);
		}
	}

}
