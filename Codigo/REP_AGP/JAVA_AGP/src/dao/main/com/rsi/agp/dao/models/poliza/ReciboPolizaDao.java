package com.rsi.agp.dao.models.poliza;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.cgen.TipificacionRecibos;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;

public class ReciboPolizaDao extends BaseDaoHibernate implements IReciboPolizaDao {

	@Override
	public List<ReciboPoliza> list(ReciboPoliza reciboPoliza)throws DAOException {

		Session session = obtenerSession();
		List lista = null;
		try {
			
			Criteria criteria = session.createCriteria(ReciboPoliza.class);
			
			//criteria.createAlias("recibo","reciboTip", CriteriaSpecification.LEFT_JOIN);
			
			if(reciboPoliza.getRefpoliza() != null && !"".equals(reciboPoliza.getRefpoliza())){
				
				criteria.createAlias("recibo", "r");
				criteria.add(Restrictions.eq("refpoliza", reciboPoliza.getRefpoliza()));
				
				if(reciboPoliza.getTiporef() != null && !"".equals(reciboPoliza.getTiporef().toString())){
					criteria.add(Restrictions.eq("tiporef", reciboPoliza.getTiporef()));
				}
				if(reciboPoliza.getRecibo().getCodfase() != null && !"".equals(reciboPoliza.getRecibo().getCodfase())){
					criteria.add(Restrictions.eq("r.codfase", reciboPoliza.getRecibo().getCodfase()));
				}
				if(reciboPoliza.getRecibo().getFecemisionrecibo() != null){
					criteria.add(Restrictions.eq("r.fecemisionrecibo", reciboPoliza.getRecibo().getFecemisionrecibo()));
				}
				if(reciboPoliza.getRecibo().getCodrecibo() !=null ){
					criteria.add(Restrictions.eq("r.codrecibo", reciboPoliza.getRecibo().getCodrecibo()));
				}
				if (reciboPoliza.getRecibo().getTipificacionRecibos().getTipificacionRecibo() != null) {
					criteria.add(Restrictions.eq("r.tipificacionRecibos.tipificacionRecibo", 
							reciboPoliza.getRecibo().getTipificacionRecibos().getTipificacionRecibo()));
				}
				if (reciboPoliza.getRecibo().getCodplan() != null) {
					criteria.add(Restrictions.eq ("r.codplan", reciboPoliza.getRecibo().getCodplan()));
				}
				criteria.addOrder(Order.asc("r.fecemisionrecibo"));
				
				lista = criteria.list();	
			}
			return 	lista;
			
		} catch (Exception e) {			
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
			
		}finally{
		}			
	}

	@Override
	public List<TipificacionRecibos> getListTipificacionRecibos()
			throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(TipificacionRecibos.class);			
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}	

}
