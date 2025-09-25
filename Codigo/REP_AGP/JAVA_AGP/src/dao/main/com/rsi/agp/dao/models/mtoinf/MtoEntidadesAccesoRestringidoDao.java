package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.EntidadAccesoRestringidoFilter;
import com.rsi.agp.core.jmesa.sort.EntidadAccesoRestringidoSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;
import com.rsi.agp.dao.tables.mtoinf.Informe;

public class MtoEntidadesAccesoRestringidoDao extends BaseDaoHibernate implements IMtoEntidadesAccesoRestringidoDao {
	
	private Log logger = LogFactory.getLog(getClass());

	@Override
	public int getEntidadAccesoRestringidoCountWithFilter(final EntidadAccesoRestringidoFilter filter) {
		try {
	        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(EntidadAccesoRestringido.class);
	                // Filtro
	                criteria = filter.execute(criteria);
	                criteria.setProjection(Projections.rowCount()).uniqueResult();
	                return criteria.uniqueResult();
	            }
	        });
	        return count.intValue();
		}
		catch (Exception e) {
			logger.debug("MtoEntidadAccesoRestringidoDao - getEntidadAccesoRestringidoCountWithFilter. Se ha producido un error durante el acceso a la base de datos");
			logger.error("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		// Si llega hasta aquí, la consulta a bd ha generado un error, por lo que se devuelve 0
		return 0;
	}

	@Override
	public Collection<EntidadAccesoRestringido> getEntidadAccesoRestringidoWithFilterAndSort(
			final EntidadAccesoRestringidoFilter filter,
			final EntidadAccesoRestringidoSort sort, final int rowStart, final int rowEnd) {
		
		List<EntidadAccesoRestringido> applications = new ArrayList<EntidadAccesoRestringido>();
		
		try{
			applications = (List<EntidadAccesoRestringido>) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(EntidadAccesoRestringido.class);    
	                criteria = filter.execute(criteria);
	                // Ordenación
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Número máximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	                // Devuelve el listado de tasas
	                return criteria.list();
	            }
	        });
	        
		}
		catch (Exception e) {
			logger.debug("MtoEntidadAccesoRestringidoDao - getEntidadAccesoRestringidoWithFilterAndSort. Se ha producido un error durante el acceso a la base de datos");
			logger.error("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		return applications;
	}
	
	@Override
	public boolean checkEntidadAccesoRestringido (BigDecimal codentidad, Long id) {
		
		List<EntidadAccesoRestringido> lstEntAccRest = new ArrayList<EntidadAccesoRestringido>();
		Session session = obtenerSession();
		boolean entidadExists = false;
	
		try {			
				Criteria criteria = session.createCriteria(EntidadAccesoRestringido.class);
				criteria.add(Restrictions.eq("entidad.codentidad", codentidad));
				
				// Se añade el id al filtro si se ha informado
				if (null != id) criteria.add(Restrictions.ne("id", id));
				
				lstEntAccRest = criteria.list();
				 
				 if (!lstEntAccRest.isEmpty()) {	
					 entidadExists = true;
				 }		
			
		} catch (Exception e) {			
			logger.error("Ocurrio un error al comprobar si existe previamente una entidad con acceso restringido", e);		
		}
	
		return entidadExists;	
	}

}
