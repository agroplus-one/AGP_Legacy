package com.rsi.agp.dao.models.poliza;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.recibos.Recibo;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;

public class ReciboDao extends BaseDaoHibernate implements IReciboDao {
	
	/**
	 * Devuelve un recibo si existe en BBDD, dado un codrecibo, si no existe devuelve null
	 */
	public Recibo getRecibo(Integer codRecibo, String codPlan, Integer codLinea, String refColectivo) throws DAOException {
		Session session = obtenerSession();
		Recibo reciboEncontrado = null;
		try {
			Criteria criteria =	session.createCriteria(Recibo.class);
			
			criteria.add(Restrictions.eq("codrecibo", codRecibo));
			criteria.add(Restrictions.eq("codplan", codPlan));
			criteria.add(Restrictions.eq("codlinea", codLinea));
			criteria.add(Restrictions.eq("refcolectivo", refColectivo));
			
			List<Recibo> lstRecibos = criteria.list();
			if (lstRecibos !=null && lstRecibos.size()>0)
				reciboEncontrado =lstRecibos.get(0);

			return reciboEncontrado;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}finally{
		}
	}
	
	/**
	 * Devuelve true si existe un reciboPoliza en BBDD dado un codrecibo, tipoReferencia y refPoliza
	 */
	public boolean existeReciboPoliza(Integer codrecibo, Character tipoRefPoliza, String refPoliza) throws DAOException {
		boolean resultado = false;
		logger.debug("init - [ReciboDao] existeReciboPoliza");
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(ReciboPoliza.class);
			criteria.createAlias("recibo", "rec");
			criteria.add(Restrictions.eq("rec.codrecibo", codrecibo));		
			criteria.add(Restrictions.eq("refpoliza", refPoliza));
			criteria.add(Restrictions.eq("tiporef", tipoRefPoliza));
			criteria.setProjection(Projections.rowCount()); 

			Integer count= (Integer) criteria.uniqueResult();			
			if (count > 0){
				resultado=true;
			}
			logger.debug("end - [ReciboDao] existeReciboPoliza");
			return resultado;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}finally{
		}
	}

}
