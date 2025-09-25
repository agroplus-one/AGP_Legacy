package com.rsi.agp.dao.models.comisiones;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ImpagadosFilter;
import com.rsi.agp.core.jmesa.sort.ImpagadosSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cargas.CargasCondicionado;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;

public class FicheroImpagadosDao extends BaseDaoHibernate implements IFicheroImpagadosDao{

	public int getFicheroImpagadosCountWithFilter(final ImpagadosFilter filter) {
		logger
		.debug("init - [FicheroImpagadosDao] getFicheroImpagadosCountWithFilter");
		Integer count = (Integer) getHibernateTemplate().execute(
		new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				Criteria criteria = session
						.createCriteria(ReciboImpagado.class);
				criteria.createAlias("fichero","id");
				// Filtro
				criteria = filter.execute(criteria);
				criteria.setProjection(Projections.rowCount())
						.uniqueResult();
				return criteria.uniqueResult();
			}
		});
		logger.debug("end - [FicheroImpagadosDao] getFicheroImpagadosCountWithFilter");
		return count.intValue();
	}

	
	@SuppressWarnings("unchecked")
	public Collection<ReciboImpagado> getFicheroImpagadosWithFilterAndSort(
			final ImpagadosFilter filter,final ImpagadosSort sort, final int rowStart, final int rowEnd) throws BusinessException {
		
		try {
			logger
					.debug("init - [FicheroImpagadosDao] getFicheroImpagadosWithFilterAndSort");
			List<ReciboImpagado> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(ReciboImpagado.class);
							criteria.createAlias("fichero","id"); 
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
					.debug("end - [FicheroImpagadosDao] getFicheroImpagadosWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException(
					"Se ha producido un error durante el acceso a la base de datos"
							+ e.getMessage());
		}
	}
}
