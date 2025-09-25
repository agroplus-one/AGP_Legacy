package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IMtoRetencionesDao;
import com.rsi.agp.core.jmesa.filter.MtoRetencionesFilter;
import com.rsi.agp.core.jmesa.sort.MtoRetencionesSort;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;
import com.rsi.agp.dao.tables.comisiones.Retencion;
import com.rsi.agp.dao.tables.commons.Usuario;

public class MtoRetencionesDao extends BaseDaoHibernate implements IMtoRetencionesDao {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Retencion> getRetencionesWithFilterAndSort(
			final MtoRetencionesFilter filter, final MtoRetencionesSort sort, final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [MtoRetencionesDao] getRetencionesWithFilterAndSort");
			List<Retencion> applications = (List<Retencion>) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(Retencion.class);
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<Usuario> lista = criteria.list();
							return lista;
						}
					});
			logger.debug("end - [MtoRetencionesDao] getRetencionesWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	

	@Override
	public int getRetencionesCountWithFilter(final MtoRetencionesFilter filter) {
		logger.debug("init - [MtoRetencionesDao] getRetencionesCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {						
						Criteria criteria = session.createCriteria(Retencion.class);
						criteria = filter.execute(criteria);	
						return criteria.setProjection(Projections.rowCount()).uniqueResult();
					}
				});
		logger.debug("end - [MtoRetencionesDao] getRetencionesCountWithFilter");
		return count.intValue();
	}


	@Override
	public boolean existeRegistro(Integer anyo, BigDecimal retencion) throws Exception{
		Integer count=0;
		try {
			Session session = obtenerSession();
			
			Criteria criteria = session.createCriteria(Retencion.class);

			// si el id viene a null es un alta y si viene relleno estamos editando
			// y comprobamos que el registro que vamos a editar no exista sin
			// tener en cuenta el id que le estamos pasando

			criteria.add(Restrictions.eq("anyo", anyo));
			//criteria.add(Restrictions.eq("retencion", retencion));
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en existeRegistro", e);
			throw new DAOException("Se ha producido un error en existeRegistro", e);
		}
	}
	
}
