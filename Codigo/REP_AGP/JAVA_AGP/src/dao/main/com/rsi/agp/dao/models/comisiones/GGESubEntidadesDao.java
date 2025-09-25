package com.rsi.agp.dao.models.comisiones;


import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.GGEEntidades;
import com.rsi.agp.dao.tables.comisiones.GGESubentidades;

public class GGESubEntidadesDao extends BaseDaoHibernate implements IGGESubEntidadesDao {
	
	private static final Log logger = LogFactory.getLog(GGESubEntidadesDao.class);
	
	public List<GGESubentidades> getListGGESubentidades(GGESubentidades GGESubentidadesBean) throws DAOException{
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(GGESubentidades.class);
			
			criteria.addOrder(Order.asc("subentidadMediadora.id.codsubentidad"));
			criteria.addOrder(Order.asc("subentidadMediadora.id.codentidad"));
			criteria.addOrder(Order.desc("plan"));
			
			if(FiltroUtils.noEstaVacio(GGESubentidadesBean.getSubentidadMediadora())){
				if(FiltroUtils.noEstaVacio(GGESubentidadesBean.getSubentidadMediadora().getId())){
					
					if(GGESubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad()!= null){
						criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", GGESubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad()));
					}
					if(GGESubentidadesBean.getSubentidadMediadora().getId().getCodentidad()!= null){
						criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", GGESubentidadesBean.getSubentidadMediadora().getId().getCodentidad()));
					}
				}
			}
			
			if(FiltroUtils.noEstaVacio(GGESubentidadesBean.getPlan()))
				criteria.add(Restrictions.eq("plan", GGESubentidadesBean.getPlan()));
			
			if(!StringUtils.nullToString(GGESubentidadesBean.getPctmediador()).equals(""))
				criteria.add(Restrictions.eq("pctmediador", GGESubentidadesBean.getPctmediador()));
	
			return criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos", ex);
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
	}

	@Override
	public Integer existeRegistro(GGESubentidades geeSubentidadesBean)	throws DAOException {
		logger.debug("init - existeRegistro");
		Session session = obtenerSession();
		try {
			
			Criteria criteria =	session.createCriteria(GGESubentidades.class);
			
			if(FiltroUtils.noEstaVacio(geeSubentidadesBean.getPlan()))
				criteria.add(Restrictions.eq("plan", geeSubentidadesBean.getPlan()));
			
			if(FiltroUtils.noEstaVacio(geeSubentidadesBean.getSubentidadMediadora())){
				if(FiltroUtils.noEstaVacio(geeSubentidadesBean.getSubentidadMediadora().getId())){
					if(FiltroUtils.noEstaVacio(geeSubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad()))
						criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", geeSubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad()));
					if(FiltroUtils.noEstaVacio(geeSubentidadesBean.getSubentidadMediadora().getId().getCodentidad()))
						criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", geeSubentidadesBean.getSubentidadMediadora().getId().getCodentidad()));
				}
			}
			
			if(FiltroUtils.noEstaVacio(geeSubentidadesBean.getId()))
					criteria.add(Restrictions.ne("id", geeSubentidadesBean.getId()));
			
			criteria.setProjection(Projections.rowCount());
			
			return (Integer)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public SubentidadMediadora getSubentidadMediadora(SubentidadMediadora subentidadMediadora) throws DAOException {
		logger.debug("init - getSubentidadMediadora");
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(SubentidadMediadora.class);
			criteria.add(Restrictions.eq("id.codentidad", subentidadMediadora.getId().getCodentidad()));
			criteria.add(Restrictions.eq("id.codsubentidad", subentidadMediadora.getId().getCodsubentidad()));
			
			logger.debug("end -  getSubentidadMediadora");
			return (SubentidadMediadora)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public boolean existePlan(String plan) throws DAOException {
		logger.debug("init - existePlan");
		Session session = obtenerSession();
		Integer result = null;
		try {
			
			Criteria criteria = session.createCriteria(GGEEntidades.class);
			criteria.add(Restrictions.eq("plan", new Long(plan)));
			criteria.setProjection(Projections.rowCount());
			
			result = (Integer) criteria.uniqueResult();
			
			logger.debug("end - existePlan");
			if(result > 0)
				return true;
			else
				return false;
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	public BigDecimal getPctSectorAgricola(Long planDestino) throws DAOException {
		logger.debug("init - getPctSectorAgricola");
		Session session = obtenerSession();
		String sql;
		BigDecimal pct = new BigDecimal(0);
		try {
			
			sql=" SELECT DISTINCT GASTOSCOL FROM O02AGPE0.TB_SC_C_COMISIONES_AGR CA INNER JOIN " +
					"O02AGPE0.TB_LINEAS L ON CA.LINEASEGUROID= L.LINEASEGUROID where l.codplan="+planDestino;
			
			
			pct = (BigDecimal) session.createSQLQuery(sql.toString()).uniqueResult();
			
			logger.debug("end - getPctSectorAgricola");
			
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return pct;
	}
}
