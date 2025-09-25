package com.rsi.agp.dao.models.comisiones;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.GGEEntidades;


public class GGEEntidadesDao extends BaseDaoHibernate implements IGGEEntidadesDao {
	
	private static final Log logger = LogFactory.getLog(GGEEntidadesDao.class);
	
	@Override
	public GGEEntidades getLastGGEPlan() throws DAOException {
		logger.debug("init - getLastGGEPlan");
		Session session = obtenerSession();
		List<GGEEntidades> list = null;
		GGEEntidades aux = null;
		try {			
			
			Criteria criteria = session.createCriteria(GGEEntidades.class);
			criteria.addOrder(Order.desc("plan"));
			criteria.addOrder(Order.desc("id"));		
			
			list = criteria.list();
			
			if(list.size() > 0)
				aux = list.get(0);
			
			logger.debug("end - getLastGGEPlan");
			return aux;
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos:" + ex.getMessage());
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
	}
	
	@Override
	public GGEEntidades getLastGGEPlan(Long year) throws DAOException {
		logger.debug("init - getLastGGEPlan");
		Session session = obtenerSession();
		List<GGEEntidades> list = null;
		GGEEntidades aux = null;
		try {			
			
			Criteria criteria = session.createCriteria(GGEEntidades.class);
			criteria.addOrder(Order.desc("plan"));
			criteria.addOrder(Order.desc("id"));
			criteria.add(Restrictions.eq("plan", new Long(year)));
			
			list = criteria.list();
			
			if(list.size() > 0)
				aux = list.get(0);
			
			logger.debug("end - getLastGGEPlan");
			return aux;
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos:" + ex.getMessage());
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
	}
	
	public GGEEntidades getEntidadByIdPlan(Long idplan) throws DAOException {
		logger.debug("init - getEntidadByIdPlan");
		Session session = obtenerSession();
		List<GGEEntidades> list = null;
		GGEEntidades aux = null;
		try {
			Criteria criteria = session.createCriteria(GGEEntidades.class);
			criteria.add(Restrictions.eq("plan", new Long(idplan)));			
			list = criteria.list();			
			if(list.size() > 0)
				aux = list.get(0);			
			logger.debug("end - getEntidadByIdPlan");
			return aux;
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos:" + ex.getMessage());
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
		
	}
	
	public List<GGEEntidades> getAll() throws DAOException{
		logger.debug("init - getAll");
		Session session = obtenerSession();
		List<GGEEntidades> list = null;
		try {
			Criteria criteria = session.createCriteria(GGEEntidades.class);			
			list = criteria.list();	
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos:" + ex.getMessage());
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
		
		return list;
	}	
	
}
