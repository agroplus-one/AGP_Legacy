package com.rsi.agp.dao.models.ref;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.ColectivoReferencia;

public class ReferenciaColectivoDao extends BaseDaoHibernate implements IReferenciaColectivoDao {

	public ColectivoReferencia getSiguienteReferenciaColectivo() throws DAOException {
		
		Session session = this.getSessionFactory().openSession();
		Transaction tx = null;
				
		try {
			logger.debug("ReferenciaColectivoDao - Inicia la transacción");
			tx = session.beginTransaction();
			
			String sqlSelect = "select * from tb_colectivos_referencias where referencia = " +
						 "(select min(referencia) from o02agpe0.tb_colectivos_referencias r where fechaenvio is null) for update";
			
			// Obtiene la siguiente referencia
			logger.debug("ReferenciaColectivoDao - Obtiene el objeto referencia");	
			Object[] resultado_referencia = (Object[]) session.createSQLQuery(sqlSelect).uniqueResult();
			
			// Carga el objeto Referencia Colectivo
			ColectivoReferencia ra = new ColectivoReferencia ();
			ra.setReferencia((String)resultado_referencia[1]);
			ra.setDc((String)resultado_referencia[2]);			
			
			// Actualiza el registro de la referencia con la fecha actual;
			logger.debug("ReferenciaColectivoDao - Actualiza el objeto ColectivoReferencia con la fecha actual");
			String sqlUpdate = "update tb_colectivos_referencias set fechaenvio = SYSDATE where id = " + resultado_referencia[0];
			logger.debug("ReferenciaColectivoDao - UPDATE - " + sqlUpdate);
			
			int res = session.createSQLQuery(sqlUpdate).executeUpdate();
			
			if (res == 1) {
				logger.debug("ReferenciaColectivoDao - Update ejecutado correctamente");
			}
			else {
				logger.debug("ReferenciaColectivoDao - No se ha podido ejecutar el update");
			}
														
			
			logger.debug("ReferenciaColectivoDao - Commit de la transacción");
			tx.commit();			
			
			return ra;
		}
		catch (Exception ex) {
			
			if (tx != null) {
				logger.debug("ReferenciaColectivoDao - Ha ocurrido algún error. Rollback de la trasacción");
				tx.rollback();				
			}
			
			logger.error("ReferenciaColectivoDao - Ha ocurrido algún error", ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",ex);
		}
		finally {
			logger.debug("ReferenciaColectivoDao - Cierra la sesion");
			if (session != null) session.close();
		}
		
				
	}
	
	public String getUltimaRef(){
		Session session = obtenerSession();
		List<String> list = null;
		
		String sql = "select ref.referencia from TB_COLECTIVOS_REFERENCIAS ref order by ref.referencia desc";
		if (session.createSQLQuery(sql).list().size()>0){
			list =session.createSQLQuery(sql).list();
			return ( (String)list.get(0));
		}else{
			return "";
		}
		
	}
	
}
