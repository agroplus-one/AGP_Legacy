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
import com.rsi.agp.core.jmesa.filter.EmitidosApliFilter;
import com.rsi.agp.core.jmesa.filter.EmitidosFilter;
import com.rsi.agp.core.jmesa.sort.EmitidosApliSort;
import com.rsi.agp.core.jmesa.sort.EmitidosSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitido;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoAplicacion;

public class EmitidosDao extends BaseDaoHibernate implements IEmitidosDao{

	private static final Log logger = LogFactory.getLog(EmitidosDao.class);
	@Override
	public int getFicheroEmitidosCountWithFilter(final EmitidosFilter filter,
			final ReciboEmitido emitido) {
		logger.debug("init - [EmitidosDao] getFicheroEmitidosCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ReciboEmitido.class);
                // Filtro
                criteria = filter.execute(criteria);
                //alias
                criteria.createAlias("fichero", "fichero");
                if (null != emitido.getFichero()){
                	if (null !=  emitido.getFichero().getId()){
                		criteria.add(Restrictions.eq("fichero.id", emitido.getFichero().getId()));
                	}
                }
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [ComisionDao] getFicheroEmitidosCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<ReciboEmitido> getFicheroEmitidosWithFilterAndSort(
			final EmitidosFilter filter, final EmitidosSort sort, final int rowStart, final int rowEnd,
			final ReciboEmitido emitido) throws BusinessException {
		try{
			logger.debug("init - [EmitidosDao] getFicheroEmitidosWithFilterAndSort");
				List<ReciboEmitido> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(ReciboEmitido.class);     
	               
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
	                if (null != emitido.getFichero()){
	                	if (null !=  emitido.getFichero().getId()){
	                		criteria.add(Restrictions.eq("fichero.id", emitido.getFichero().getId()));
	                	}
	                }
	                
	                return criteria.list();
	            }
	        });
			logger.debug("end - [EmitidosDao] getFicheroEmitidosWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}

	public int getFicheroEmitidosApliCountWithFilter(final EmitidosApliFilter filter,
			final ReciboEmitidoAplicacion emitidoApli) {
		logger.debug("init - [EmitidosDao] getFicheroEmitidosApliCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ReciboEmitidoAplicacion.class);
                // Filtro
                criteria = filter.execute(criteria);
                //alias
                criteria.createAlias("reciboEmitido", "reciboEmitido");
                if (null != emitidoApli.getReciboEmitido()){
                	if (null !=  emitidoApli.getReciboEmitido().getId()){
                		criteria.add(Restrictions.eq("reciboEmitido.id", emitidoApli.getReciboEmitido().getId()));
                	}
                }
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [EmitidosDao] getFicheroEmitidosApliCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<ReciboEmitidoAplicacion> getFicheroEmitidosApliWithFilterAndSort(
			final EmitidosApliFilter filter, final EmitidosApliSort sort, final int rowStart, final int rowEnd,
			final ReciboEmitidoAplicacion emitidoapli) throws BusinessException {
		try{
			logger.debug("init - [EmitidosDao] getFicheroEmitidosApliWithFilterAndSort");
				List<ReciboEmitidoAplicacion> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(ReciboEmitidoAplicacion.class);     
	               
	        		// Filtro
	                criteria = filter.execute(criteria);
	                // Ordenación
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Número máximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	              //alias
	                criteria.createAlias("reciboEmitido", "reciboEmitido");
	                if (null != emitidoapli.getReciboEmitido()){
	                	if (null !=  emitidoapli.getReciboEmitido().getId()){
	                		criteria.add(Restrictions.eq("reciboEmitido.id", emitidoapli.getReciboEmitido().getId()));
	                	}
	                }
	                
	                return criteria.list();
	            }
	        });
			logger.debug("end - [EmitidosDao] getFicheroEmitidosApliWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}
	
	
	

}
