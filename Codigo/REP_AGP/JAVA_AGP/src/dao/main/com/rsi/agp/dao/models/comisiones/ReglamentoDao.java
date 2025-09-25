package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ReglamentoFilter;
import com.rsi.agp.core.jmesa.filter.ReglamentoSitFilter;
import com.rsi.agp.core.jmesa.sort.ReglamentoSitSort;
import com.rsi.agp.core.jmesa.sort.ReglamentoSort;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.comisiones.Reglamento;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitida;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ReglamentoDao extends BaseDaoHibernate implements IReglamentoDao {
	private static final Log logger = LogFactory.getLog(ReglamentoDao.class);
	
	
	
	@Override
	public int getFicheroReglamentoCountWithFilter(final ReglamentoFilter filter,final ReglamentoProduccionEmitida reglamentoProduccionEmitida) {
		logger.debug("init - [ReglamentoDao] getFicheroReglamentoCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ReglamentoProduccionEmitida.class);
                // Filtro
                criteria = filter.execute(criteria);
                //alias
                criteria.createAlias("fichero", "fichero");
                if (null != reglamentoProduccionEmitida.getFichero()){
                	if (null !=  reglamentoProduccionEmitida.getFichero().getId()){
                		criteria.add(Restrictions.eq("fichero.id", reglamentoProduccionEmitida.getFichero().getId()));
                	}
                }
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [ReglamentoDao] getFicheroReglamentoCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<ReglamentoProduccionEmitida> getFicheroReglamentoWithFilterAndSort(
			final ReglamentoFilter filter, final ReglamentoSort sort, final int rowStart,
			final int rowEnd,final ReglamentoProduccionEmitida reglamentoProduccionEmitida) throws BusinessException {
		try{
			logger.debug("init - [ReglamentoDao] getFicheroReglamentoWithFilterAndSort");
				List<ReglamentoProduccionEmitida> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(ReglamentoProduccionEmitida.class);     
	               
	        		// Filtro
	                criteria = filter.execute(criteria);
	                // Ordenación
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Número máximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	              //alias
	                criteria.createAlias("fichero", "fichero");
	                if (null != reglamentoProduccionEmitida.getFichero()){
	                	if (null !=  reglamentoProduccionEmitida.getFichero().getId()){
	                		criteria.add(Restrictions.eq("fichero.id", reglamentoProduccionEmitida.getFichero().getId()));
	                	}
	                }
	                return criteria.list();
	            }
	        });
			logger.debug("end - [ReglamentoDao] getFicheroReglamentoWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}
	
	@Override
	public int getFicheroReglamentoSitCountWithFilter(final ReglamentoSitFilter filter,final ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit) {
		logger.debug("init - [ReglamentoDao] getFicheroReglamentoCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                
            	Criteria criteria = session.createCriteria(ReglamentoProduccionEmitidaSituacion.class);
                // Filtro
                criteria = filter.execute(criteria);
                criteria.createAlias("reglamentoProduccionEmitida", "reglamentoProduccionEmitida");
                if (null !=  reglamentoProduccionEmitidaSit.getId()){
                		criteria.add(Restrictions.eq("reglamentoProduccionEmitida.id", reglamentoProduccionEmitidaSit.getId()));
                }
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [ReglamentoDao] getFicheroReglamentoSitCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<ReglamentoProduccionEmitidaSituacion> getFicheroReglamentoSitWithFilterAndSort(
			final ReglamentoSitFilter filter, final ReglamentoSitSort sort, final int rowStart,
			final int rowEnd,final ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSit) throws BusinessException {
		try{
			logger.debug("init - [ReglamentoDao] getFicheroReglamentoSitWithFilterAndSort");
				List<ReglamentoProduccionEmitidaSituacion> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(ReglamentoProduccionEmitidaSituacion.class);     
	               
	        		// Filtro
	                criteria = filter.execute(criteria);
	                // Ordenación
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Número máximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	                criteria.createAlias("reglamentoProduccionEmitida", "reglamentoProduccionEmitida");
	                if (null !=  reglamentoProduccionEmitidaSit.getId()){
                		criteria.add(Restrictions.eq("reglamentoProduccionEmitida.id", reglamentoProduccionEmitidaSit.getId()));
	                }
	                return criteria.list();
	            }
	        });
			logger.debug("end - [ReglamentoDao] getFicheroReglamentoSitWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}
	
	
	@Override
	public List<Reglamento> listReglamentos(Reglamento reglamentoBean)throws DAOException {
		logger.debug("init - listReglamentos");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Reglamento.class);
			
			criteria.addOrder(Order.asc("entidad.codentidad"));
			criteria.addOrder(Order.desc("plan"));
			
			if(FiltroUtils.noEstaVacio(reglamentoBean.getPctentidad()))
				criteria.add(Restrictions.eq("pctentidad", reglamentoBean.getPctentidad()));
			
			if(FiltroUtils.noEstaVacio(reglamentoBean.getPctrga()))
				criteria.add(Restrictions.eq("pctrga", reglamentoBean.getPctrga()));
			
			if(FiltroUtils.noEstaVacio(reglamentoBean.getPlan()))
				criteria.add(Restrictions.eq("plan", reglamentoBean.getPlan()));
			
			if(FiltroUtils.noEstaVacio(reglamentoBean.getEntidad())){
				if(FiltroUtils.noEstaVacio(reglamentoBean.getEntidad().getCodentidad()))
					criteria.add(Restrictions.eq("entidad.codentidad", reglamentoBean.getEntidad().getCodentidad()));
			}
			
			logger.debug("end - listReglamentos");
			return criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public Entidad getEntidad(Entidad entidad) throws DAOException{
		logger.debug("init - getEntidad");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Entidad.class);
			
			if(FiltroUtils.noEstaVacio(entidad.getCodentidad())){
				criteria.add(Restrictions.eq("codentidad", entidad.getCodentidad()));
			}
			logger.debug("end - getEntidad");
			return (Entidad)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public Integer existeRegistro(Reglamento reglamentoBean) throws DAOException {
		logger.debug("init - existeRegistro");
		Session session = obtenerSession();
		try {
			
			Criteria criteria =	session.createCriteria(Reglamento.class);
			
			if(FiltroUtils.noEstaVacio(reglamentoBean.getPlan()))
				criteria.add(Restrictions.eq("plan", reglamentoBean.getPlan()));
			
			if(FiltroUtils.noEstaVacio(reglamentoBean.getEntidad())){
				if(FiltroUtils.noEstaVacio(reglamentoBean.getEntidad().getCodentidad())){
					if(FiltroUtils.noEstaVacio(reglamentoBean.getEntidad().getCodentidad()))
						criteria.add(Restrictions.eq("entidad.codentidad",reglamentoBean.getEntidad().getCodentidad()));
				}
			}
			
			if(FiltroUtils.noEstaVacio(reglamentoBean.getId()))
				criteria.add(Restrictions.ne("id", reglamentoBean.getId()));
			
			criteria.setProjection(Projections.rowCount());
			
			logger.debug("end - existeRegistro");
			return (Integer)criteria.uniqueResult();
			
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
			
			Criteria criteria = session.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codplan", new BigDecimal(plan)));
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
	
}
