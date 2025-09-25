package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.FasesCierreComsFilter;
import com.rsi.agp.core.jmesa.sort.FasesCierreComsSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;

import com.rsi.agp.dao.tables.comisiones.unificado.FasesCerradas;

public class FasesCierreComisionesDao extends BaseDaoHibernate implements IFasesCierreComisionesDao {
	private static final Log logger = LogFactory.getLog(FasesCierreComisionesDao.class);

	@Override
	public int getFasesCierreCountWithFilter(final FasesCierreComsFilter filter,
			FasesCerradas fase) {
		logger
		.debug("init - [FasesCierreComisionesDao] getFasesCierreCountWithFilter");
		Integer count = (Integer) getHibernateTemplate().execute(
		new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				Criteria criteria = session
						.createCriteria(FasesCerradas.class);
				// Filtro
				criteria = filter.execute(criteria);
				criteria.setProjection(Projections.rowCount())
						.uniqueResult();
				return criteria.uniqueResult();
				
			
				
				
			}
		});
		logger.debug("end - [FasesCierreComisionesDao] getFasesCierreCountWithFilter");
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<FasesCerradas> getFasesCierreWithFilterAndSort(
			final FasesCierreComsFilter filter, final FasesCierreComsSort sort,
			final int rowStart, final int rowEnd, final FasesCerradas fase) throws BusinessException {
		try {
			logger
					.debug("init - [FasesCierreComisionesDao] getFasesCierreWithFilterAndSort");
			List<FasesCerradas> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(FasesCerradas.class);
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
					.debug("end - [FasesCierreComisionesDao] getFasesCierreWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException(
					"Se ha producido un error durante el acceso a la base de datos"
							+ e.getMessage());
		}
	}

}
