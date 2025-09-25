package com.rsi.agp.core.jmesa.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
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
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.filter.InformesDeudaAplazadaFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeDeudaAplazadaUnificado;
import com.rsi.agp.dao.tables.poliza.Linea;

public class InformesDeudaAplazadaDao extends BaseDaoHibernate implements
IInformesDeudaAplazadaDao {
	
	private static final Log LOGGER = LogFactory.getLog(InformesDeudaAplazadaDao.class);
	
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort,final int rowStart,
			final int rowEnd) throws BusinessException {
try {
			
			
			logger.debug("init - [InformesDeudaAplazadaDao] getWithFilterAndSort");
			List<Serializable> informes =(List<Serializable>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {							
							final InformesDeudaAplazadaFilter filtro= (InformesDeudaAplazadaFilter)filter;	
							//filtro.execute();
						
							Criteria criteria=null;
							criteria = session.createCriteria(InformeDeudaAplazadaUnificado.class);
							// Filtro
							criteria = filtro.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							if (rowStart != -1 && rowEnd != -1) {
						        // Primer registro
						        criteria.setFirstResult(rowStart);
						        // Número máximo de registros a mostrar
						        criteria.setMaxResults(rowEnd - rowStart);
						    }
							final List<InformeDeudaAplazadaUnificado> lista = criteria.list();
							return lista;
							
						}
					});
			logger.debug("end - [InformesDeudaAplazadaDao] getWithFilterAndSort");
			return informes;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getCountWithFilter(CriteriaCommand filter) {
		logger.debug("init - [InformesDeudaAplazadaDao] getCountWithFilter");
		final InformesDeudaAplazadaFilter filtro= (InformesDeudaAplazadaFilter)filter;	
		//filtro.execute();
			Integer count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							
							Criteria criteria=null;
							criteria = session.createCriteria(InformeDeudaAplazadaUnificado.class);
							//criteria = filter.execute(criteria);
							criteria = filtro.execute(criteria);
							return criteria.setProjection(Projections.rowCount()).uniqueResult();
						}
					});
			logger.debug("end - [InformesDeudaAplazadaDao] getCountWithFilter");
			return count.intValue();
	}
	
	
	
	
	
	/**
     * recogemos el nombre de la entidad
     */
    public String getNombreEntidad(BigDecimal codEntidad){
    	logger.info("Init - getNombreEntidad");
    	List<Entidad> lstEntidades = null;
    	String nombreEntidad = "";
    	Session session = obtenerSession();
//		try {
			Criteria criteria = session.createCriteria(Entidad.class);
			criteria.add(Restrictions.eq("codentidad", codEntidad));
			
			lstEntidades = criteria.list();
			
//		} catch(Exception ex){
//			logger.error("Se ha producido un error durante el acceso a la base de datos ",ex);
//    		throw new DAOException("[ERROR] al acceder a la BBDD.",ex); 
//    	}
		if (lstEntidades != null && lstEntidades.size()>0){
			nombreEntidad = lstEntidades.get(0).getNomentidad();
		}
		logger.info("end - getNombreEntidad");
		return nombreEntidad;
    }
	
	
	/**
     * recogemos el nombre de la línea
     */
    public String getNombreLinea(BigDecimal codLinea){
    	logger.info("Init - getNombreLinea");
    	List<Linea> lstLineas = null;
    	String nombreLinea = "";
    	Session session = obtenerSession();
//		try {
			Criteria criteria = session.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codlinea", codLinea));
			
			lstLineas = criteria.list();
			
//		} catch(Exception ex){
//			logger.error("Se ha producido un error durante el acceso a la base de datos ",ex);
//    		throw new DAOException("[ERROR] al acceder a la BBDD.",ex); 
//    	}
		if (lstLineas != null && lstLineas.size()>0){
			nombreLinea = lstLineas.get(0).getNomlinea();
		}
		logger.info("end - getNombreLinea");
		return nombreLinea;
    }

}
