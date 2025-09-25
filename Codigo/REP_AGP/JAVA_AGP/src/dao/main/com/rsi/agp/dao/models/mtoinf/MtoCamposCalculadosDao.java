package com.rsi.agp.dao.models.mtoinf;

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
import com.rsi.agp.core.jmesa.filter.CamposCalculadosFilter;
import com.rsi.agp.core.jmesa.sort.CamposCalculadosSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;

public class MtoCamposCalculadosDao extends BaseDaoHibernate implements	IMtoCamposCalculadosDao {

	@Override
	public int getCamposCalculadosCountWithFilter(final CamposCalculadosFilter filter) {
		logger
		.debug("init - [CamposCalculadosDao] getConsultaCamposCalculadosCountWithFilter");
			Integer count = (Integer) getHibernateTemplate().execute(
		new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session
						.createCriteria(CamposCalculados.class);
				criteria.createAlias("camposPermitidosByIdoperando1", "operando1");
				criteria.createAlias("camposPermitidosByIdoperando2", "operando2");

				criteria = filter.execute(criteria);
				criteria.setProjection(Projections.rowCount())
						.uniqueResult();
				return criteria.uniqueResult();
			}
		});
			logger.debug("end - [CamposCalculadosDao] getConsultaCamposCalculadosCountWithFilter");
			return count.intValue();

	}

	@Override
	public List<CamposCalculados> getListaCamposCalculados() throws DAOException {
		logger
		.debug("init - [CamposCalculadosDao] getListaCamposCalculados");
		
	
		try {
			logger.debug("end - [CamposCalculadosDao] getListaCamposCalculados");
			return findAll(CamposCalculados.class);
			
			
		} catch (Exception ex) {
			logger.error("Error: getListaCamposCalculados : " + ex);
			throw new DAOException("getListaCamposCalculados:Se ha producido un error durante el acceso a la base de datos", ex);
		}
		
		
		
	}
	
	

	@Override
	public Collection<CamposCalculados> getCamposCalculadosWithFilterAndSort (
			final CamposCalculadosFilter filter, final CamposCalculadosSort sort, final int rowStart,
			final int rowEnd) throws DAOException {
		
		try {
			logger
					.debug("init - [CamposCalculadosDao] getCamposCalculadosWithFilterAndSort");
			Session session = obtenerSession();
							Criteria criteria = session
									.createCriteria(CamposCalculados.class);
							// Alias
							
							criteria.createAlias("camposPermitidosByIdoperando1", "operando1");
							criteria.createAlias("camposPermitidosByIdoperando2", "operando2");

							// Filtro
							criteria = filter.execute(criteria);
							// Ordenación
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Número máximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							
							List<CamposCalculados> rows = criteria.list(); 
						
							 
			logger.debug("end - [CamposCalculadosDao] getCamposCalculadosWithFilterAndSort");
			return rows;
		} catch (Exception e) {
			logger.error("Error: getCamposCalculadosWithFilterAndSort : " + e);
			throw new DAOException("getCamposCalculadosWithFilterAndSort : Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		
	
	}
	
	
	
	@Override
	public boolean existeDatosCamposCalculados(final CamposCalculados camposCalculados) throws  DAOException  {
		logger
		.debug("init - [CamposCalculadosDao] existeDatosCamposCalculados");

		boolean exist = false;
		try {			
			Integer count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(CamposCalculados.class);
							criteria.add(Restrictions.eq("nombre", camposCalculados.getNombre()));	
							if(camposCalculados.getId() != null){
								criteria.add(Restrictions.not(Restrictions.eq("id", camposCalculados.getId())));	
							}
				return ((Integer) criteria.setProjection(Projections.rowCount())
						.uniqueResult()).intValue();
			}
		});
	

		if (count > 0)
			exist	= true;
		
} catch (Exception e) {			
	logger.error("Error: existeDatosCamposCalculados : " + e);
	throw new DAOException("existeDatosCamposCalculados : Se ha producido un error durante el acceso a la base de datos", e);		
}
logger
.debug("end - [CamposCalculadosDao] existeDatosCamposCalculados");

return exist;	
}
}

	


