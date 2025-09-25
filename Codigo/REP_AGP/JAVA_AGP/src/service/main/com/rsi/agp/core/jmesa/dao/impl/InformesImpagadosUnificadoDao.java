package com.rsi.agp.core.jmesa.dao.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.InformesImpagadosUnificadoFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeImpagadosUnificado;

public class InformesImpagadosUnificadoDao extends BaseDaoHibernate implements
		IGenericoDao {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort, final int rowStart,
			final int rowEnd) throws BusinessException {
try {
			
			
			logger.debug("init - [InformesImpagadosUnificadoDao] getWithFilterAndSort");
			List<Serializable> informes =(List<Serializable>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {							
							final InformesImpagadosUnificadoFilter filtro= (InformesImpagadosUnificadoFilter)filter;	
							//filtro.execute();
						
							Criteria criteria=null;
							criteria = session.createCriteria(InformeImpagadosUnificado.class);							
							criteria = filtro.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							
							if (rowStart!=-1 && rowEnd !=-1) {
								// Primer registro
								criteria.setFirstResult(rowStart);
								// Numero maximo de registros a mostrar
								criteria.setMaxResults(rowEnd - rowStart);
							}
							final List<InformeImpagadosUnificado> lista = criteria.list();
							return lista;
							
						}
					});
			logger.debug("end - [InformesImpagadosUnificadoDao] getWithFilterAndSort");
			return informes;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getCountWithFilter(CriteriaCommand filter) {
		logger.debug("init - [InformesImpagadosUnificadoDao] getCountWithFilter");
		final InformesImpagadosUnificadoFilter filtro= (InformesImpagadosUnificadoFilter)filter;	
		//filtro.execute();
			Integer count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							
							Criteria criteria=null;
							criteria = session.createCriteria(InformeImpagadosUnificado.class);							
							criteria = filtro.execute(criteria);
							return criteria.setProjection(Projections.rowCount()).uniqueResult();
						}
					});
			logger.debug("end - [InformesImpagadosUnificadoDao] getCountWithFilter");
			return count.intValue();	
	}
}
