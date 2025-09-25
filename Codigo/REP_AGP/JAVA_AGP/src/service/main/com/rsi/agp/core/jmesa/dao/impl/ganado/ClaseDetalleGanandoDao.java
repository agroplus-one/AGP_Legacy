package com.rsi.agp.core.jmesa.dao.impl.ganado;

import java.io.Serializable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.impl.IClaseDetalleGanadoDao;
import com.rsi.agp.core.jmesa.filter.gan.ClaseDetalleGanadoFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.ClaseDetalleGanado;

public class ClaseDetalleGanandoDao extends BaseDaoHibernate implements IClaseDetalleGanadoDao{
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort, final int rowStart,
			final int rowEnd) throws BusinessException {
		
		try {
			logger.debug("init - [ClaseDetalleGanandoDao] getWithFilterAndSort");
			List<Serializable> clases =(List<Serializable>) getHibernateTemplate().execute(new HibernateCallback() {
			
						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {							
							final ClaseDetalleGanadoFilter claseFilter= (ClaseDetalleGanadoFilter)filter;	
							//infImpFilter.execute();
			
							Criteria criteria=null;
							
							criteria = session.createCriteria(ClaseDetalleGanado.class);
							//Solo del campo nullable
							//criteria.createAlias("tipoCapitalConGrupoNegocio","tCap", CriteriaSpecification.LEFT_JOIN);
							criteria = claseFilter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<ClaseDetalleGanado> lista = criteria.list();
							return lista;
							
						}
					});
			logger.debug("end - [ClaseDetalleGanandoDao] getWithFilterAndSort");
			return clases;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getCountWithFilter(CriteriaCommand filter) {
		logger.debug("init - [ClaseDetalleGanandoDao] getCountWithFilter");
		final ClaseDetalleGanadoFilter clase= (ClaseDetalleGanadoFilter)filter;	
		//clase.execute(filter);
			Integer count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							
							Criteria criteria=null;							
							criteria = session.createCriteria(ClaseDetalleGanado.class);
							//Solo del campo nullable
							//criteria.createAlias("codtipocapital","tCap", CriteriaSpecification.LEFT_JOIN);
							//criteria = filter.execute(criteria);
							criteria = clase.execute(criteria);
							return criteria.setProjection(Projections.rowCount()).uniqueResult();
						}
					});
			logger.debug("end - [ClaseDetalleGanandoDao] getCountWithFilter");
			return count.intValue();	

	}
	
	@SuppressWarnings("rawtypes")
	public String getlistaIdsTodos(ClaseDetalleGanadoFilter consultaFilter) {
		String listaids="";
		Session session = obtenerSession();
		String sql = "SELECT D.ID FROM TB_CLASE_DETALLE_GANADO D " +consultaFilter.getSqlWhere();
		List lista = session.createSQLQuery(sql).list();
		
		for(int i=0;i<lista.size();i++){
			listaids += lista.get(i)+",";
		}
		return listaids;
	}
	
	@SuppressWarnings("rawtypes")
	public Boolean existeClaseDetalleGanado(ClaseDetalleGanadoFilter consultaFilter, Long id) {
		Boolean res=false;
		Session session = obtenerSession();
		String sql=null;
		if(null==id) {
			sql = "SELECT D.ID FROM TB_CLASE_DETALLE_GANADO D " +consultaFilter.getSqlWhere();
		}else {
			sql = "SELECT D.ID FROM TB_CLASE_DETALLE_GANADO D " +consultaFilter.getSqlWhere(id);
		}
		List lista = session.createSQLQuery(sql).list();
		res=(null!=lista && lista.size()>0);
		return res;
	}
	
	@SuppressWarnings("rawtypes")
	public List getListaClase (Class clase, String [] parametros, Object [] valores, String orden) throws DAOException {
		return this.findFiltered(clase, parametros, valores, orden);
	}
	

}
