package com.rsi.agp.dao.models.ref;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.ref.ReferenciaAgricola;

public class ReferenciaDao extends BaseDaoHibernate implements IReferenciaDao {

	@Override
	public ReferenciaAgricola getSiguienteReferencia() throws DAOException {
		
		Session session = this.getSessionFactory().openSession();
		Transaction tx = null;
				
		try {
			logger.debug("ReferenciaDao - Inicia la transacción");
			tx = session.beginTransaction();
			
			String sqlSelect = "select * from tb_referencias_agricolas where referencia = " +
						 "(select min(referencia) from o02agpe0.tb_referencias_agricolas r where fechaenvio is null) for update";
			
			// Obtiene la siguiente referencia
			logger.debug("ReferenciaDao - Obtiene el objeto referencia");	
			Object[] resultado_referencia = (Object[]) session.createSQLQuery(sqlSelect).uniqueResult();
			
			// Carga el objeto Referencia agrícola
			ReferenciaAgricola ra = new ReferenciaAgricola ();
			ra.setReferencia((String)resultado_referencia[0]);
			ra.setDc((String)resultado_referencia[2]);			
			
			// Actualiza el registro de la referencia con la fecha actual;
			logger.debug("ReferenciaDao - Actualiza el objeto referencia con la fecha actual");
			String sqlUpdate = "update tb_referencias_agricolas set fechaenvio = SYSDATE where id = " + resultado_referencia[3];
			logger.debug("ReferenciaDao - UPDATE - " + sqlUpdate);
			
			int res = session.createSQLQuery(sqlUpdate).executeUpdate();
			
			if (res == 1) {
				logger.debug("ReferenciaDao - Update ejecutado correctamente");
			}
			else {
				logger.debug("ReferenciaDao - No se ha podido ejecutar el update");
			}
														
			
			logger.debug("ReferenciaDao - Commit de la transacción");
			tx.commit();			
			
			return ra;
		}
		catch (Exception ex) {
			
			if (tx != null) {
				logger.debug("ReferenciaDao - Ha ocurrido algún error. Rollback de la trasacción");
				tx.rollback();				
			}
			
			logger.error("ReferenciaDao - Ha ocurrido algún error", ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",ex);
		}
		finally {
			logger.debug("ReferenciaDao - Cierra la sesion");
			if (session != null) session.close();
		}
				
	}
	
	/**
	 * Devuelve la �ltima referencia insertada
	 */
	public String getUltimaRef(){
		Session session = obtenerSession();
		
		Criteria criteria = session.createCriteria(ReferenciaAgricola.class);
		criteria.setProjection(Projections.max("referencia"));	
		Object ref = criteria.uniqueResult();
		
		return (ref != null ? ref.toString() : "");
	}

	@Override
	public boolean hayRefRepetidasEnRango(String refInicial, String refFinal) throws DAOException {
		
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(ReferenciaAgricola.class);		
			criteria.add(Restrictions.between("referencia", refInicial, refFinal));
			criteria.setProjection(Projections.rowCount());
			
			return Integer.parseInt(criteria.uniqueResult().toString())>0;
		} catch (Exception e) {
			throw new DAOException("Ocurri� un error al comprobar si existen referencias ya insertadas del intervalo indicado");
		} 
	}		
}
