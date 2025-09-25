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
import com.rsi.agp.core.jmesa.filter.CondicionadoFilter;
import com.rsi.agp.core.jmesa.sort.CondicionadoSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cargas.CargasCondicionado;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;

public class CargasCondicionadoDao extends BaseDaoHibernate implements
		ICargasCondicionadoDao {

	
	@Override
	public int getCondicionadosCountWithFilter(final CondicionadoFilter filter) {
		logger
				.debug("init - [CargasCondicionadoDao] getCondicionadosCountWithFilter");
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(CargasCondicionado.class);
						// Filtro
						criteria = filter.execute(criteria);
						criteria.setProjection(Projections.rowCount())
								.uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger
				.debug("end - [CargasCondicionadoDao] getCondicionadosCountWithFilter");
		return count.intValue();
	}

	@Override
	@SuppressWarnings("all")
	public Collection<CargasCondicionado> getCondicionadosWithFilterAndSort(
			final CondicionadoFilter filter, final CondicionadoSort sort,
			final int rowStart, final int rowEnd) throws BusinessException {
		try {
			logger
					.debug("init - [CargasCondicionadoDao] getCondicionadosWithFilterAndSort");
			List<CargasCondicionado> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(CargasCondicionado.class);

							// Filtro
							criteria = filter.execute(criteria);
							// Ordenación
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Número máximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							return criteria.list();
						}
					});
			logger
					.debug("end - [CargasCondicionadoDao] getCondicionadosWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException(
					"Se ha producido un error durante el acceso a la base de datos"
							+ e.getMessage());
		}
	}

	@Override
	@SuppressWarnings("all")
	public List<CargasFicheros> getFicherosCondicionado(Long idCondicionado) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(CargasFicheros.class);
		criteria.createAlias("cargasCondicionado", "cargasCondicionado");
		criteria.add(Restrictions.eq("cargasCondicionado.id", idCondicionado));
		return criteria.list();
	}

}
