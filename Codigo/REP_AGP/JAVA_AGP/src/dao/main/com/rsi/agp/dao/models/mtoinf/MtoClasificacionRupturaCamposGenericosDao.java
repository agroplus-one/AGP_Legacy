package com.rsi.agp.dao.models.mtoinf;

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

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ClasificacionRupturaCamposGenericosFilter;
import com.rsi.agp.core.jmesa.sort.ClasificacionRupturaCamposGenericosSort;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.ClasificacionRupturaCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.ClasificacionRupturaCamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfClasificacionRuptura;

public class MtoClasificacionRupturaCamposGenericosDao extends	BaseDaoHibernate implements IMtoClasificacionRupturaCamposGenericosDao {

	@Override
	public int getClasificacionRupturaCountWithFilter(final ClasificacionRupturaCamposGenericosFilter filter, final BigDecimal informeId) {
		logger
		.debug("init - [ClasificacionRupturaDao] getConsultaClasificacionRupturaCountWithFilter");
			Integer count = (Integer) getHibernateTemplate().execute(
		new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session
						.createCriteria(VistaMtoinfClasificacionRuptura.class);
				if(informeId != null){
					criteria.add(Restrictions.eq("idinforme", informeId));
					}
				criteria = filter.execute(criteria);
				criteria.setProjection(Projections.rowCount())
						.uniqueResult();
				 return ((Integer) criteria.uniqueResult()).intValue();
			}
		});
		logger
				.debug("end - [ClasificacionRupturaDao] getConsultaClasificacionRupturaCountWithFilter");
		return count.intValue();

	}

	@Override
	public List<VistaMtoinfClasificacionRuptura> getListaClasificacionRuptura() throws DAOException {
	
		try {
			
			return findAll(VistaMtoinfClasificacionRuptura.class);
		
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
	

	
	public Collection<VistaMtoinfClasificacionRuptura> getClasificacionRupturaWithFilterAndSort (
			final ClasificacionRupturaCamposGenericosFilter filter,final ClasificacionRupturaCamposGenericosSort sort,final BigDecimal informeId,final int rowStart,
			final int rowEnd) throws DAOException {
		
		try {
			logger
					.debug("init - [ClasificacionRupturaDao] getClasificacionRupturaSbpWithFilterAndSort");
			List<VistaMtoinfClasificacionRuptura> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(VistaMtoinfClasificacionRuptura.class);
							if(informeId != null){
								criteria.add(Restrictions.eq("idinforme", informeId));
								}
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenación
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Número máximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							
							List<VistaMtoinfClasificacionRuptura> lista = criteria.list();
							
							return lista;
							
						}
					});
			logger.debug("end - [SobrePrecioDao] getClasificacionRupturaWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		
	
	}
	
	public boolean existeDatosClasificacionRuptura(final VistaMtoinfClasificacionRuptura clasificacionRuptura)
	 throws  DAOException  {
		boolean exist = false;
		try {			
			Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
		
						Criteria criteria = null;
						if (clasificacionRuptura.getId().getPermitidocalculado().compareTo(ConstantsInf.CAMPO_CALCULADO) == 0){
							criteria = session.createCriteria(ClasificacionRupturaCamposCalculados.class);
						}else{
							criteria = session.createCriteria(ClasificacionRupturaCamposPermitidos.class);
						}
						criteria.createAlias("datoInformes", "datoInf");
						if(clasificacionRuptura.getIdClasifRupt() != null){
							criteria.add(Restrictions.not(Restrictions.eq("id", clasificacionRuptura.getIdClasifRupt().longValue())));
						}
						criteria.add(Restrictions.eq("datoInf.id", clasificacionRuptura.getId().getIddatoInforme().longValue()));
						return ((Integer) criteria.setProjection(Projections.rowCount())
								.uniqueResult()).intValue();
					}
				});
			
	
				if (count > 0)
					exist	= true;
				
	} catch (Exception e) {			
		throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);		
	}
	return exist;
	}
	
	
	
}

	