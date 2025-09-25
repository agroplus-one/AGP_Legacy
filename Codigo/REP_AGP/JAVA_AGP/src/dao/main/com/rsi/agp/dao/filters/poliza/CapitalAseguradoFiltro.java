package com.rsi.agp.dao.filters.poliza;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;

public class CapitalAseguradoFiltro  implements Filter{
	
	private Long idPoliza;

	public CapitalAseguradoFiltro() {}

	public CapitalAseguradoFiltro(Long idPoliza) {
		this.idPoliza = idPoliza;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(CapitalAsegurado.class);
		criteria.createAlias("parcela", "p");
		if(idPoliza != null){
			criteria.add(Restrictions.eq("p.poliza.idpoliza", idPoliza));
			/*criteria.addOrder(Order.asc("p.hoja"));
			criteria.addOrder(Order.asc("p.numero"));*/
					
		}
		return criteria;
	}
	
	public Long getIdPoliza() {
		return idPoliza;
	}
	public void setIdPoliza(Long idPoliza) {
		this.idPoliza = idPoliza;
	}
}