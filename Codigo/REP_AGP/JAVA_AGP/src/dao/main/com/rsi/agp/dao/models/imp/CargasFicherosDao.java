package com.rsi.agp.dao.models.imp;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CargasFicherosFilter;
import com.rsi.agp.core.jmesa.sort.CargasFicherosSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;
import com.rsi.agp.dao.tables.cargas.CargasTablas;
import com.rsi.agp.dao.tables.commons.RelacionTablaXml;

public class CargasFicherosDao extends BaseDaoHibernate implements ICargasFicherosDao{

	@Override
	public int getFicherosCountWithFilter(final CargasFicherosFilter filter,final CargasFicheros cargasFicheros) {
		logger.debug("init - [CargasFicherosDao] getFicherosCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CargasFicheros.class);
                // Filtro
                criteria = filter.execute(criteria);
                //alias
                criteria.createAlias("cargasCondicionado", "cargasCondicionado");
                if (null != cargasFicheros.getCargasCondicionado()){
                	if (null != cargasFicheros.getCargasCondicionado().getId()){
                		criteria.add(Restrictions.eq("cargasCondicionado.id", cargasFicheros.getCargasCondicionado().getId()));
                	}
                }
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [CargasFicherosDao] getFicherosCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings(value={"all"})
	public Collection<CargasFicheros> getFicherosWithFilterAndSort(
			final CargasFicherosFilter filter, final CargasFicherosSort sort, final int rowStart,
			final int rowEnd,final CargasFicheros cargasFicheros) throws BusinessException {
		try{
			logger.debug("init - [CargasFicherosDao] getFicherosWithFilterAndSort");
				List<CargasFicheros> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(CargasFicheros.class);     
	               
	        		// Filtro
	                criteria = filter.execute(criteria);
	                // Ordenación
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Número máximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	                //alias
	                criteria.createAlias("cargasCondicionado", "cargasCondicionado");
	                if (null != cargasFicheros.getCargasCondicionado()){
	                	if (null != cargasFicheros.getCargasCondicionado().getId()){
	                		criteria.add(Restrictions.eq("cargasCondicionado.id", cargasFicheros.getCargasCondicionado().getId()));
	                	}
	                }
	                return criteria.list();
	            }
	        });
			logger.debug("end - [CargasFicherosDao] getFicherosWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}

	@Override
	public String getTipoFichero(CargasFicheros cargasFicherosBean,List listTablas) {
		
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(RelacionTablaXml.class);   
		criteria.add(Restrictions.in("numtabla", listTablas));
		criteria.setProjection(Projections.distinct(Projections.property("tiposc"))); 
		return (String) criteria.uniqueResult();
	}

	
	

}
