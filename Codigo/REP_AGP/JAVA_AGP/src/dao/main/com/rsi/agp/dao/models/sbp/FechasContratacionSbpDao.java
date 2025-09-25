package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.FechasContratacionSbpFilter;
import com.rsi.agp.core.jmesa.sort.FechasContratacionSbpSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.sbp.FechaContratacionSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public class FechasContratacionSbpDao extends BaseDaoHibernate implements
		IFechasContratacionSbpDao {

	@Override
	public int getFechasContratacionSbpCountWithFilter(
			final FechasContratacionSbpFilter filter) {

		logger
				.debug("init - [FechasContratacionSbpDao] getFechasContratacionSbpCountWithFilter");
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(FechaContratacionSbp.class);
						// Alias
						criteria.createAlias("linea", "lin");
						// Filtro
						criteria = filter.execute(criteria);
						criteria.setProjection(Projections.rowCount())
								.uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger
				.debug("end - [FechasContratacionSbpDao] getFechasContratacionSbpCountWithFilter");
		return count.intValue();
	}

	@Override
	@SuppressWarnings("all")
	public Collection<FechaContratacionSbp> getFechasContratacionSbpWithFilterAndSort(
			final FechasContratacionSbpFilter filter,
			final FechasContratacionSbpSort sort, final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger
					.debug("init - [FechasContratacionSbpDao] getFechasContratacionSbpWithFilterAndSort");
			List<FechaContratacionSbp> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(FechaContratacionSbp.class);
							// Alias
							criteria.createAlias("linea", "lin");
							criteria.createAlias("cultivo", "cult");
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
			logger
					.debug("end - [FechasContratacionSbpDao] getFechasContratacionSbpWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException(
					"Se ha producido un error durante el acceso a la base de datos"
							+ e.getMessage());
		}
	}
	
	/*
	 * Chequea si existe ya una Linea - cultivo  en tabla "TB_SBP_FECHACONTRATACION"
	 * 
	*/
	
	public boolean existeLineaSeguroIdCultivo(Long lineaseguroid,BigDecimal codcultivo,Long id){
		List<FechaContratacionSbp> lstFechaContratacionSbp = new ArrayList<FechaContratacionSbp>();
		Session session = obtenerSession();
		boolean existeLineaSeguroId = false;
		
		try{
			Criteria criteria = session.createCriteria(FechaContratacionSbp.class);
			// Alias
			criteria.createAlias("linea", "linea");
			criteria.createAlias("cultivo", "cultivo");
			criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("cultivo.id.codcultivo", codcultivo));
			if (id!= null)
				criteria.add(Restrictions.ne("id", id));
			lstFechaContratacionSbp = criteria.list();
			
			if (!lstFechaContratacionSbp.isEmpty()){
				existeLineaSeguroId = true;
			}
			
		} catch (Exception ex) {
			logger.error("[FechaContratacionSbpDao] lstFechaContratacionSbp - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return existeLineaSeguroId;
	}
}
