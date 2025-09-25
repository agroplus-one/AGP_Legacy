package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.tables.commons.Provincia;

public class ProvinciaDao extends BaseDaoHibernate implements IProvinciaDao{

	
	/*
	 * Chequea si existe una Provincia
	 * 
	*/
	
	public boolean checkProvinciaExists(BigDecimal codprovincia) throws DAOException {
		
		List<Provincia> lstProvincia = new ArrayList<Provincia>();
		Session session = obtenerSession();
		boolean provinciaExists = false;
	
		try {			
				Criteria criteria = session.createCriteria(Provincia.class);
				
				criteria.add(Restrictions.eq("codprovincia", codprovincia));
				lstProvincia = criteria.list();
				 
				 if (!lstProvincia.isEmpty()) {	
						provinciaExists = true;
				 }		
			
		} catch (Exception e) {			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);		
		}
	
		return provinciaExists;		
	}

}
