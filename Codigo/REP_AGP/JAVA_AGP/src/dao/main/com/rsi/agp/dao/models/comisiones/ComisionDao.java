package com.rsi.agp.dao.models.comisiones;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ComisionApliFilter;
import com.rsi.agp.core.jmesa.filter.ComisionFilter;
import com.rsi.agp.core.jmesa.sort.ComisionApliSort;
import com.rsi.agp.core.jmesa.sort.ComisionSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacion;

public class ComisionDao extends BaseDaoHibernate implements IComisionDao{

	private static final Log logger = LogFactory.getLog(ComisionDao.class);
	@Override
	public int getFicheroComisionCountWithFilter(final ComisionFilter filter,
			final Comision comision) {
		logger.debug("init - [ComisionDao] getFicheroComisionCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(Comision.class);
                // Filtro
                criteria = filter.execute(criteria);
                //alias
                criteria.createAlias("fichero", "fichero");
                if (null != comision.getFichero()){
                	if (null !=  comision.getFichero().getId()){
                		criteria.add(Restrictions.eq("fichero.id", comision.getFichero().getId()));
                	}
                }
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [ComisionDao] getFicheroComisionCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<Comision> getFicheroComisionWithFilterAndSort(
			final ComisionFilter filter, final ComisionSort sort, final int rowStart, final int rowEnd,
			final Comision comision) throws BusinessException {
		try{
			logger.debug("init - [ComisionDao] getFicheroComisionWithFilterAndSort");
				List<Comision> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(Comision.class);     
	               
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
	                if (null != comision.getFichero()){
	                	if (null !=  comision.getFichero().getId()){
	                		criteria.add(Restrictions.eq("fichero.id", comision.getFichero().getId()));
	                	}
	                }
	                
	                return criteria.list();
	            }
	        });
			logger.debug("end - [ComisionDao] getFicheroComisionWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}
	
	public int getFicheroComisionApliCountWithFilter(final ComisionApliFilter filter,
			final ComisionAplicacion comisionApli) {
		logger.debug("init - [ComisionDao] getFicheroComisionApliCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ComisionAplicacion.class);
                // Filtro
                criteria = filter.execute(criteria);
                //alias
                criteria.createAlias("comision", "comision");
                
            	if (null !=  comisionApli.getComision().getId()){
            		criteria.add(Restrictions.eq("comision.id", comisionApli.getComision().getId()));
            	}
               
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [ComisionDao] getFicheroComisionApliCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<ComisionAplicacion> getFicheroComisionApliWithFilterAndSort(
			final ComisionApliFilter filter, final ComisionApliSort sort, final int rowStart, final int rowEnd,
			final ComisionAplicacion comisionApli) throws BusinessException {
		try{
			logger.debug("init - [ComisionDao] getFicheroComisionApliWithFilterAndSort");
				List<ComisionAplicacion> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(ComisionAplicacion.class);     
	               
	        		// Filtro
	                criteria = filter.execute(criteria);
	                // Ordenación
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Número máximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	                //alias
	                criteria.createAlias("comision", "comision");
	                
	                if (null !=  comisionApli.getComision().getId()){
	            		criteria.add(Restrictions.eq("comision.id", comisionApli.getComision().getId()));
	            	}
	                
	                return criteria.list();
	            }
	        });
			logger.debug("end - [ComisionDao] getFicheroComisionWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}

	

}
