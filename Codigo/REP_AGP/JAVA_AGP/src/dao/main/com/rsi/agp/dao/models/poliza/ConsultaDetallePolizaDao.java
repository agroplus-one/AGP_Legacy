package com.rsi.agp.dao.models.poliza;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class ConsultaDetallePolizaDao extends BaseDaoHibernate implements IConsultaDetallePolizaDao {
	
	
	@SuppressWarnings("unchecked")
	public EnvioAgroseguro getXMLCalculoImportes(Poliza polizaBean) throws DAOException{
		
		List<EnvioAgroseguro> result = new ArrayList<EnvioAgroseguro>();
		EnvioAgroseguro envio = null;
		Session sesion = obtenerSession();
		Criteria criteria = sesion.createCriteria(EnvioAgroseguro.class);
		criteria.add(Restrictions.eq("poliza.idpoliza",polizaBean.getIdpoliza()));
		criteria.add(Restrictions.eq("tipoenvio","CL"));
		criteria.add(Restrictions.eq("codmodulo",polizaBean.getCodmodulo()));
		criteria.addOrder(Order.desc("fechaEnvio"));
		
		 result = (List<EnvioAgroseguro>) criteria.list();
		if (result.size()>0){
			envio = (EnvioAgroseguro) result.get(0);
		}
		return envio;
	}
	

}
