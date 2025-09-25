package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;

import com.rsi.agp.core.jmesa.filter.PrimaMinimaSbpFilter;
import com.rsi.agp.core.jmesa.sort.PrimaMinimaSbpSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;
import org.springframework.orm.hibernate3.HibernateCallback;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;


public class PrimaMinimaSbpDao extends BaseDaoHibernate implements IPrimaMinimaSbpDao {

	@Override
	public int getPrimaMinimaSbpCountWithFilter(final PrimaMinimaSbpFilter filter) {
		logger.debug("init - [ConsultaSbpDao] getConsultaPolizaSbpCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(PrimaMinimaSbp.class);
                // Alias
                criteria.createAlias("linea", "lin");
				// Filtro
                criteria = filter.execute(criteria);
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [ConsultaSbpDao] getConsultaPolizaSbpCountWithFilter");
        return count.intValue();
	}

	@Override
	@SuppressWarnings("all")
	public Collection<PrimaMinimaSbp> getPrimaMinimaSbpWithFilterAndSort (
			final PrimaMinimaSbpFilter filter, final PrimaMinimaSbpSort sort, final int rowStart,
			final int rowEnd) throws BusinessException {
		try{
			logger.debug("init - [ConsultaSbpDao] getConsultaPolizasSbpWithFilterAndSort");
				List<PrimaMinimaSbp> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(PrimaMinimaSbp.class);     
	                // Alias
	                criteria.createAlias("linea", "lin");
	        		// Filtro
	                criteria = filter.execute(criteria);
	                // Ordenación
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Número máximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	                // Devuelve el listado de pólizas
	                return criteria.list();
	            }
	        });
			logger.debug("end - [ConsultaSbpDao] getConsultaPolizasSbpWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}
	
	public List<PrimaMinimaSbp> getListaPrimaMinimaSbp(){
		List<PrimaMinimaSbp> lstPrimaMinimaSbp = new ArrayList<PrimaMinimaSbp>();
		Session session = obtenerSession();
		try{
			Criteria criteria = session.createCriteria(PrimaMinimaSbp.class);
			
			// criteria.add(Restrictions.eq("polizaPpal.idpoliza", idPoliza));
			
			lstPrimaMinimaSbp = criteria.list();
			
		} catch (Exception ex) {
			logger.error("[PrimaMinimaDao] listaPrimaMinimaSbp - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return lstPrimaMinimaSbp;
	}
	
	/*
	 * Chequea si existe ya una prima Mínima
	 * 
	 */
	
	public boolean checkPrimaMinimaSbpExists(Long lineaSeguroId) throws DAOException {
		
		List<PrimaMinimaSbp> lstPrimaMinimaSbp = new ArrayList<PrimaMinimaSbp>();
		Session session = obtenerSession();
		boolean primaMinExists = false;
	
		try {			
				Criteria criteria = session.createCriteria(PrimaMinimaSbp.class);
				criteria.createAlias("linea", "linea");
				criteria.add(Restrictions.eq("linea.lineaseguroid", lineaSeguroId));
				lstPrimaMinimaSbp = criteria.list();
				 
				 if (!lstPrimaMinimaSbp.isEmpty()) {	
					 PrimaMinimaSbp primaMin = lstPrimaMinimaSbp.get(0);
					 
					 if (primaMin.getPrimaMinima() != null) { primaMinExists = true; }
				 }		
			
		} catch (Exception e) {			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);		
		}
	
		return primaMinExists;		
	}
}
