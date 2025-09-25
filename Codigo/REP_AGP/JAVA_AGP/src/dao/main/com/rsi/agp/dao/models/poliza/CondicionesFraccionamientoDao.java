package com.rsi.agp.dao.models.poliza;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.poliza.CondicionesFraccionamientoFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;

public class CondicionesFraccionamientoDao extends BaseDaoHibernate implements
		ICondicionesFraccionamientoDao {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort, final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [CondFraccionaDao] getWithFilterAndSort");
			List<Serializable> informes =(List<Serializable>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(final Session session) throws HibernateException, SQLException {							
				final CondicionesFraccionamientoFiltro condFraccionaFilter= (CondicionesFraccionamientoFiltro)filter;	
				Criteria criteria = null;

				criteria = session.createCriteria(CondicionesFraccionamiento.class);
				criteria.createAlias("linea", "linea");
				criteria.createAlias("id", "id");
				// Filtro
				criteria = condFraccionaFilter.execute(criteria);
				// Ordenacion
				criteria = sort.execute(criteria);
				// Primer registro
				criteria.setFirstResult(rowStart);
				// Numero maximo de registros a mostrar
				criteria.setMaxResults(rowEnd - rowStart);
				final List<CondicionesFraccionamiento> lista = criteria.list();
				return lista;
							
			}
			});
			logger.debug("end - [CondFraccionaDao] getWithFilterAndSort");
			return informes;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getCountWithFilter(CriteriaCommand filter) {
		logger.debug("init - [CondFraccionaDao] getCountWithFilter");
		final CondicionesFraccionamientoFiltro condFraccionaFilter = (CondicionesFraccionamientoFiltro)filter;	
		
			Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(CondicionesFraccionamiento.class);;
							criteria = condFraccionaFilter.execute(criteria);
							return criteria.setProjection(Projections.rowCount()).uniqueResult();
						}
					});
			logger.debug("end - [CondFraccionaDao] getCountWithFilter");
			return count.intValue();
	}
	
	public List<CondicionesFraccionamiento> listCondicionesFraccionamiento(CondicionesFraccionamiento condicionesFraccionamiento) throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(CondicionesFraccionamiento.class);
			
			criteria.addOrder(Order.asc("id.periodoFracc"));		
			
			if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getId())) {
				if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getId().getLineaseguroid())) {
					criteria.add(Restrictions.eq("id.lineaseguroid", condicionesFraccionamiento.getId().getLineaseguroid()));
				}
				if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getId().getPeriodoFracc())) {
					criteria.add(Restrictions.eq("id.periodoFracc", condicionesFraccionamiento.getId().getPeriodoFracc()));
				}
			}		
		
			
			if(FiltroUtils.noEstaVacio(condicionesFraccionamiento.getLinea())){
				if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getLinea().getCodplan())) {			
					criteria.add(Restrictions.eq("linea.codplan", condicionesFraccionamiento.getLinea().getCodplan()));
				}
				if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getLinea().getCodlinea())) {			
					criteria.add(Restrictions.eq("linea.codlinea", condicionesFraccionamiento.getLinea().getCodlinea()));
				}
				if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getLinea().getLineaseguroid())) {
					criteria.add(Restrictions.eq("linea.lineaseguroid", condicionesFraccionamiento.getLinea().getLineaseguroid()));
				}
			}
			
			
			
			if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getPctRecAval())) {
				criteria.add(Restrictions.eq("pctRecAval", condicionesFraccionamiento.getPctRecAval()));
			}
			
			if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getPctRecFracc())) {
				criteria.add(Restrictions.eq("pctRecFracc", condicionesFraccionamiento.getPctRecFracc()));
			}
			
			if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getImpMinRecAval())) {
				criteria.add(Restrictions.eq("impMinRecAval", condicionesFraccionamiento.getImpMinRecAval()));
			}

			if (condicionesFraccionamiento.getModulo()!=null){
				if (condicionesFraccionamiento.getModulo().getId()!=null){
					if (FiltroUtils.noEstaVacio(condicionesFraccionamiento.getModulo().getId().getCodmodulo())){
						criteria.add(Restrictions.eq("modulo.id.codmodulo",condicionesFraccionamiento.getModulo().getId().getCodmodulo()));
					}
				}
			}
			
			return criteria.list();
			
		} catch (Exception e) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + e.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}		
	}
	
	public List<CondicionesFraccionamiento> getAll()throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(CondicionesFraccionamiento.class);			
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

}
