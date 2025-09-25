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
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.DeudaAplazadaApliFilter;
import com.rsi.agp.core.jmesa.filter.DeudaAplazadaFilter;
import com.rsi.agp.core.jmesa.sort.DeudaAplazadaApliSort;
import com.rsi.agp.core.jmesa.sort.DeudaAplazadaSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DetalleAbonoPoliza;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DeudaAplazadaAplicacion;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;

public class DeudaAplazadaDao extends BaseDaoHibernate implements IDeudaAplazadaDao{

	private static final Log logger = LogFactory.getLog(DeudaAplazadaDao.class);
	@Override
	public int getFicheroDeudaAplazadaCountWithFilter(final DeudaAplazadaFilter filter,
			final FicheroMult deudaAplazada) {
		logger.debug("init - [DeudaAplazadaDao] getFicheroDeudaAplazadaCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(FicheroMult.class);
                // Filtro
                criteria = filter.execute(criteria);
                //alias
                //criteria.createAlias("fichero", "fichero");
                /*
                if (null != deudaAplazada.getFichero()){
                	if (null !=  deudaAplazada.getFichero().getId()){
                		criteria.add(Restrictions.eq("fichero.id", deudaAplazada.getFichero().getId()));
                	}
                }
                */
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [ComisionDao] getFicheroDeudaAplazadaCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<FicheroMult> getFicheroDeudaAplazadaWithFilterAndSort(
			final DeudaAplazadaFilter filter, final DeudaAplazadaSort sort, final int rowStart, final int rowEnd,
			final FicheroMult deudaAplazada) throws BusinessException {
		try{
			logger.debug("init - [DeudaAplazadaDao] getFicheroDeudaAplazadaWithFilterAndSort");
				List<FicheroMult> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(FicheroMult.class);     
	               
	        		// Filtro
	                criteria = filter.execute(criteria);
	                // Ordenacion
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Numero maximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	              //alias
	                //criteria.createAlias("fichero", "fichero");
	                /*
	                if (null != deudaAplazada.getFichero()){
	                	if (null !=  deudaAplazada.getFichero().getId()){
	                		criteria.add(Restrictions.eq("fichero.id", deudaAplazada.getFichero().getId()));
	                	}
	                }
	                */
	                return criteria.list();
	            }
	        });
			logger.debug("end - [DeudaAplazadaDao] getFicheroDeudaAplazadaWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}

	public int getFicheroDeudaAplazadaApliCountWithFilter(final DeudaAplazadaApliFilter filter,
			final DeudaAplazadaAplicacion deudaAplazadaApli) {
		logger.debug("init - [DeudaAplazadaDao] getFicheroDeudaAplazadaApliCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DeudaAplazadaAplicacion.class);
                // Filtro
                criteria = filter.execute(criteria);
                //alias
                criteria.createAlias("reciboEmitido", "reciboEmitido");
                /*
                if (null != deudaAplazadaApli.getDeudaAplazada()){
                	if (null !=  deudaAplazadaApli.getDeudaAplazada().getId()){
                		criteria.add(Restrictions.eq("reciboEmitido.id", deudaAplazadaApli.getDeudaAplazada().getId()));
                	}
                }
                */
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [DeudaAplazadaDao] getFicheroDeudaAplazadaApliCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<DeudaAplazadaAplicacion> getFicheroDeudaAplazadaApliWithFilterAndSort(
			final DeudaAplazadaApliFilter filter, final DeudaAplazadaApliSort sort, final int rowStart, final int rowEnd,
			final DeudaAplazadaAplicacion deudaAplazadaapli) throws BusinessException {
		try{
			logger.debug("init - [DeudaAplazadaDao] getFicheroDeudaAplazadaApliWithFilterAndSort");
				List<DeudaAplazadaAplicacion> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(DeudaAplazadaAplicacion.class);     
	               
	        		// Filtro
	                criteria = filter.execute(criteria);
	                // Ordenacion
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Numero maximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	              //alias
	                criteria.createAlias("reciboEmitido", "reciboEmitido");
	                /*
	                if (null != deudaAplazadaapli.getDeudaAplazada()){
	                	if (null !=  deudaAplazadaapli.getDeudaAplazada().getId()){
	                		criteria.add(Restrictions.eq("reciboEmitido.id", deudaAplazadaapli.getDeudaAplazada().getId()));
	                	}
	                }
	                */
	                return criteria.list();
	            }
	        });
			logger.debug("end - [DeudaAplazadaDao] getFicheroDeudaAplazadaApliWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}
	
	
	

}
