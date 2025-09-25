package com.rsi.agp.core.jmesa.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.core.jmesa.filter.IncidenciasComisionesUnificadoFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroIncidenciasUnificado;

public class IncidenciasComisionesUnificadoDao extends BaseDaoHibernate
		implements IIncidenciasComisionesUnificadoDao {
		
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort,final int rowStart,
			final int rowEnd) throws BusinessException {
		
		try {			
			
			logger.debug("init - [IncidenciasComisionesUnificadoDao] getWithFilterAndSort");
			List<Serializable> informes =(List<Serializable>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {							
							final IncidenciasComisionesUnificadoFilter filtro= (IncidenciasComisionesUnificadoFilter)filter;	
							//filtro.execute();
						
							Criteria criteria=null;
							criteria = session.createCriteria(FicheroIncidenciasUnificado.class);
							criteria.createAlias("linea", "linea");
							// Filtro
							criteria = filtro.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<FicheroIncidenciasUnificado> lista = criteria.list();
							return lista;
							
						}
					});
			logger.debug("end - [IncidenciasComisionesUnificadoDao] getWithFilterAndSort");
			return informes;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getCountWithFilter(CriteriaCommand filter) {
		logger.debug("init - [IncidenciasComisionesUnificadoDao] getCountWithFilter");
		final IncidenciasComisionesUnificadoFilter filtro= (IncidenciasComisionesUnificadoFilter)filter;	
		//filtro.execute();
			Integer count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							
							Criteria criteria=null;
							criteria = session.createCriteria(FicheroIncidenciasUnificado.class);
							criteria.createAlias("linea", "linea");
							//criteria = filter.execute(criteria);
							criteria = filtro.execute(criteria);
							return criteria.setProjection(Projections.rowCount()).uniqueResult();
						}
					});
			logger.debug("end - [IncidenciasComisionesUnificadoDao] getCountWithFilter");
			return count.intValue();	
		
	}

	@SuppressWarnings("unchecked")
	public Collection<FicheroIncidenciasUnificado> getIncidenciasFicheroUnificado(
			final CriteriaCommand filter, final CriteriaCommand sort) throws BusinessException {
		try {
			logger.debug("init - [IncidenciasComisionesUnificadoDao] getIncidenciasFicheroUnificado");
			List<FicheroIncidenciasUnificado> informes =(List<FicheroIncidenciasUnificado>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {							
							final IncidenciasComisionesUnificadoFilter filtro= (IncidenciasComisionesUnificadoFilter)filter;	
						
						
							Criteria criteria=null;
							criteria = session.createCriteria(FicheroIncidenciasUnificado.class);
							criteria.createAlias("linea", "linea");
							// Filtro
							criteria = filtro.execute(criteria);
							criteria = sort.execute(criteria);

							final List<FicheroIncidenciasUnificado> lista = criteria.list();
							return lista;
							
						}
					});
			logger.debug("end - [IncidenciasComisionesUnificadoDao] getIncidenciasFicheroUnificado");
			return informes;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	
	public void borraIncidencias(Long idFicheroUnificado) throws DAOException{
		
		logger.debug("init - borraIncidencias");
		Session session = obtenerSession();
		
		try {
			String hql = "delete from TB_COMS_UNIF_FICH_INCIDENCIAS fi where fi.ID_FICHERO_UNIF = :idFichero and fi.ESTADO <> 'R'";
			SQLQuery query = session.createSQLQuery(hql);
			query.setParameter("idFichero", idFicheroUnificado);
            int row = query.executeUpdate();

			if(row == 0)
				logger.debug(" --> No se borro ninguna fila.");
			else
				logger.debug(" --> Se borraron " +  row + " filas.");
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error durante el acceso a base de datos:" + ex.getMessage());
			throw new DAOException ("Se ha produccido un error durante el acceso a base de datos",ex);
		}
		logger.debug("end - borraIncidencias");
	}

	@Override
	public void revisarIncidencia(final Long idIncidencia, final char estado)
			throws DAOException {

		logger.debug("init - revisarIncidencia");

		Session session = obtenerSession();
		boolean actualizar = false;

		try {

			FicheroIncidenciasUnificado incidencia = (FicheroIncidenciasUnificado) session
					.get(FicheroIncidenciasUnificado.class, idIncidencia);

			// Verificamos que el cambio de estado solicitado sea coherente
			// Solo se puede pasar a estado A las incidencias en estado R
			// Solo se puede pasar a estado R las incidencias en estado A o E
			switch (estado) {
			case 'A':
				actualizar = incidencia.getEstado() == 'R';
				break;
			case 'R':
				actualizar = incidencia.getEstado() == 'A'
						|| incidencia.getEstado() == 'E';
				break;
			default:
				actualizar = false;
				break;
			}

			if (actualizar) {

				incidencia.setEstado(estado);
				session.saveOrUpdate(incidencia);
			}

		} catch (Exception ex) {
			logger.error("Se ha produccido un error durante el acceso a base de datos:"
					+ ex.getMessage());
			throw new DAOException(
					"Se ha produccido un error durante el acceso a base de datos",
					ex);
		}
		
		logger.debug("end - revisarIncidencia");
	}

	@Override
	public String getListaIdsTodos(IGenericoFilter consultaFilter)
			throws DAOException {

		String listaids = "";

		try {

			logger.debug("init - [IncidenciasComisionesUnificadoDao] getListaIdsTodos");

			Session session = obtenerSession();

			String sql = "SELECT INC.ID FROM TB_COMS_UNIF_FICH_INCIDENCIAS INC, TB_LINEAS L "
					+ ((IncidenciasComisionesUnificadoFilter) consultaFilter)
							.getSqlWhere();

			@SuppressWarnings("unchecked")
			List<BigDecimal> lista = session.createSQLQuery(sql).list();

			for (int i = 0; i < lista.size(); i++) {
				listaids += lista.get(i).toString() + ",";
			}

			return listaids;

		} catch (Exception e) {

			logger.error("Error: getListaIdsTodos : " + e);
			throw new DAOException(
					"getlistaIdsTodos : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}	
}