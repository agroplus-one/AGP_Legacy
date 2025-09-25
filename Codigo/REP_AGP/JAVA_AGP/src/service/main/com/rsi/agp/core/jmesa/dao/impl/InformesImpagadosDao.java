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
import com.rsi.agp.core.jmesa.filter.InformesImpagadosFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.impagados.InformeImpagados;
import com.rsi.agp.dao.tables.comisiones.impagados.InformeImpagados2015;

public class InformesImpagadosDao extends BaseDaoHibernate implements IGenericoDao {

	@SuppressWarnings("unchecked")
	public Collection<Serializable> getWithFilterAndSort(final CriteriaCommand filter, final CriteriaCommand sort,
			final int rowStart, final int rowEnd) throws BusinessException {

		try {

			logger.debug("init - [InformesImpagadosDao] getWithFilterAndSort");
			List<Serializable> informes = (List<Serializable>) getHibernateTemplate().execute(new HibernateCallback() {

				public Object doInHibernate(final Session session) throws HibernateException, SQLException {
					final InformesImpagadosFilter infImpFilter = (InformesImpagadosFilter) filter;
					infImpFilter.execute();
					Criteria criteria = null;
					if (infImpFilter.getEsMayorIgual2015()) {
						criteria = session.createCriteria(InformeImpagados2015.class);
					} else {
						criteria = session.createCriteria(InformeImpagados.class);
					}
					// Filtro
					criteria = infImpFilter.execute(criteria);
					// Ordenacion
					criteria = sort.execute(criteria);
					// Primer registro
					criteria.setFirstResult(rowStart);
					// Numero maximo de registros a mostrar
					criteria.setMaxResults(rowEnd - rowStart);
					final List<InformeImpagados> lista = criteria.list();
					return lista;
				}
			});
			logger.debug("end - [InformesImpagadosDao] getWithFilterAndSort");
			return informes;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	public int getCountWithFilter(final CriteriaCommand filter) {
		logger.debug("init - [InformesImpagadosDao] getCountWithFilter");
		final InformesImpagadosFilter infImpFilter = (InformesImpagadosFilter) filter;
		infImpFilter.execute();
		Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = null;
				if (infImpFilter.getEsMayorIgual2015()) {
					criteria = session.createCriteria(InformeImpagados2015.class);
				} else {
					criteria = session.createCriteria(InformeImpagados.class);
				}

				// criteria = filter.execute(criteria);
				criteria = infImpFilter.execute(criteria);
				return criteria.setProjection(Projections.rowCount()).uniqueResult();
			}
		});
		logger.debug("end - [InformesImpagadosDao] getCountWithFilter");
		return count.intValue();
	}
}