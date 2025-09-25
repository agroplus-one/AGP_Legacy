package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ClaseMtoFilter;
import com.rsi.agp.core.jmesa.sort.ClaseMtoSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ClaseMtoDao extends BaseDaoHibernate implements IClaseMtoDao {

	@Override
	public int getConsultaClaseMtoCountWithFilter(final ClaseMtoFilter filter,final String descripcion) {
		logger.debug("init - [ClaseMtoDao] getConsultaClaseMtoCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session.createCriteria(Clase.class);
						// Alias
						criteria.createAlias("linea", "lin");
						// Filtro
						criteria = filter.execute(criteria);
						// filtro por descripcion
		                if (descripcion != null && !descripcion.equals("")){
		                	criteria.add(Restrictions.ilike("descripcion", "%".concat(descripcion).concat("%")));
		                }
						criteria.setProjection(Projections.rowCount()).uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger.debug("end - [ClaseMtoDao] getConsultaClaseMtoCountWithFilter");
		return count.intValue();
	}

	@Override
	@SuppressWarnings("all")
	public Collection<Clase> getClaseMtoWithFilterAndSort(
			final ClaseMtoFilter filter,final ClaseMtoSort sort,final int rowStart,
			final int rowEnd,final String descripcion) throws BusinessException {
		try {
			logger
					.debug("init - [ClaseMtoDao] getClaseMtoWithFilterAndSort");
			List<Clase> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(Clase.class);
							// Alias
							criteria.createAlias("linea", "lin");
							// Filtro
							criteria = filter.execute(criteria);
							// filtro por descripcion
			                if (descripcion != null && !descripcion.equals("")){
			                	criteria.add(Restrictions.ilike("descripcion", "%".concat(descripcion).concat("%")));
			                }
							// Ordenación
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Número máximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							return criteria.list();
						}
					});
			logger.debug("end - [ClaseMtoDao] getClaseMtoWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public void replicaPlanLinea(Long lineaSeguroIdDestino, Long lineaSeguroIdOrigen, BigDecimal claseOrigen) throws DAOException {
		
		try{
			String procedure = "PQ_REPLICAR.replicarClase(LINEASEGUROID_DESTINO IN NUMBER,LINEASEGUROID_ORIGEN IN NUMBER,CLASE IN NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("LINEASEGUROID_DESTINO", lineaSeguroIdDestino);
			parametros.put("LINEASEGUROID_ORIGEN", lineaSeguroIdOrigen);
			parametros.put("CLASE", claseOrigen);
			
			databaseManager.executeStoreProc(procedure, parametros);
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD: Ha fallado la ejecucion del PL"
					+ e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD", e);
		}
		
	}

	@Override
	public boolean existeClaseReplica(Long lineaSeguroIdDestino, BigDecimal clase) {
		
		Session session = obtenerSession();
		String sql = "SELECT * FROM TB_CLASE WHERE LINEASEGUROID = " + lineaSeguroIdDestino ;
		if (clase != null){
			sql += " AND CLASE = " + clase;
		}
		List list = session.createSQLQuery(sql).list();
		
		return list.size()>0;
	}

	@Override
	public boolean isCargadaClase(Long id) {
		Session session = obtenerSession();
		
		Criteria criteria = session.createCriteria(Usuario.class);
		
		criteria.createAlias("clase", "clase");
		
		criteria.add(Restrictions.eq("clase.id", id));
		criteria.setProjection(Projections.rowCount());
		Integer count = (Integer) criteria.uniqueResult();
		if (count>0) 
			return true;
		
		return false;
	}
}
