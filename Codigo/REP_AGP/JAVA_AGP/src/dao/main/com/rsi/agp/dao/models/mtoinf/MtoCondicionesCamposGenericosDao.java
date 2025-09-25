package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CondicionCamposFilter;

import com.rsi.agp.core.jmesa.service.mtoinf.Estados;
import com.rsi.agp.core.jmesa.sort.CondicionCamposSort;
import com.rsi.agp.dao.filters.poliza.EstadoPolizaFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfCondiciones;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MtoCondicionesCamposGenericosDao  extends BaseDaoHibernate implements IMtoCondicionesCamposGenericosDao {

	/**
			 * Devuelve el número de datos del informe que se ajustan al filtro pasado como parámetro
			 * @param filter
			 * @return
			 */
			@Override
			public int getCamposGenericosCountWithFilter(final CondicionCamposFilter filter,final BigDecimal informeId) throws DAOException {
				logger.debug("init - [MtoCondicionesCamposGenericosDao] getCamposGenericosCountWithFilter");
			
						Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(VistaMtoinfCondiciones.class);
						if(informeId != null){
							criteria.add(Restrictions.eq("idinforme", informeId));
							}
						
						criteria = filter.execute(criteria);
						return ((Integer) criteria.setProjection(Projections.rowCount())
									.uniqueResult()).intValue();
					}
				});
		logger
				.debug("end - [MtoCondicionesCamposGenericosDao] getCamposGenericosCountWithFilter");
		return count.intValue();

			}
				

			public Collection<VistaMtoinfCondiciones> getCamposGenericosWithFilterSort (
					final CondicionCamposFilter filter,final CondicionCamposSort sort,final BigDecimal informeId , final int rowStart,
					final int rowEnd) throws DAOException {
				
				try {
					logger
							.debug("init - [MtoCondicionesCamposGenericosDao] getCamposGenericosCountWithFilter");
					List<VistaMtoinfCondiciones> applications = (List) getHibernateTemplate()
							.execute(new HibernateCallback() {

								public Object doInHibernate(Session session)
										throws HibernateException, SQLException {
									Criteria criteria = session
											.createCriteria(VistaMtoinfCondiciones.class);
									
									if(informeId != null){
										criteria.add(Restrictions.eq("idinforme", informeId));
										}
									criteria = filter.execute(criteria);
									
									criteria = sort.execute(criteria);
									
									criteria.setFirstResult(rowStart);
									
									criteria.setMaxResults(rowEnd - rowStart);
									
									List<VistaMtoinfCondiciones> lista = criteria.list();
									
									return lista;
									
								}
							});
					logger.debug("end - [MtoCondicionesCamposGenericosDao] getCamposGenericosCountWithFilter");
					return applications;
				} catch (Exception e) {
					throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
				}
				
				
			
			}
			
					
			
			public boolean existeCondicion(final VistaMtoinfCondiciones vistaMtoinfCondiciones)
					 throws  DAOException  {
				
				boolean exist = false;
			
				
					try {			
						Integer count = (Integer) getHibernateTemplate().execute(
								new HibernateCallback() {
									public Object doInHibernate(Session session)
											throws HibernateException, SQLException {
										Criteria criteria = session
												.createCriteria(VistaMtoinfCondiciones.class);
										if(vistaMtoinfCondiciones.getId().getCondid() != null){
											criteria.add(Restrictions.not(Restrictions.eq("id.condid", vistaMtoinfCondiciones.getId().getCondid())));
										}
										criteria.add(Restrictions.eq("id.permitidocalculado", vistaMtoinfCondiciones.getId().getPermitidocalculado()));
										criteria.add(Restrictions.eq("idtablaoperadores", vistaMtoinfCondiciones.getIdtablaoperadores()));
										criteria.add(Restrictions.eq("idoperador", vistaMtoinfCondiciones.getIdoperador()));
										criteria.add(Restrictions.eq("datoinformeid",vistaMtoinfCondiciones.getDatoinformeid()));
										criteria.add(Restrictions.eq("condicion",vistaMtoinfCondiciones.getCondicion()));
										
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


		@Override
		public List<Estados> getEstados(Class clase) {
			try {
				return findAll(clase);
			} catch (DAOException e) {
				logger.error("Ocurrio un error al obtener todos los registros de " + clase, e);
			}
			
			return null;
		}
		
		public List getEstadosPol(EstadoPolizaFilter filter) { 
			try {
				return getObjects(filter);
			
			} catch (Exception e) {
				logger.error("Ocurrio un error al obtener todos los registros de EstadoPolizaFilter" , e);
				
			}
			return null;
		}
			
			

}