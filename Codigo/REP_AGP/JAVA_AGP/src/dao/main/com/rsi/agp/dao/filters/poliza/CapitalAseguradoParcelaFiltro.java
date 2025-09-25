package com.rsi.agp.dao.filters.poliza;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;

public class CapitalAseguradoParcelaFiltro  implements Filter{
	
	private Long codParcela;
	
	public CapitalAseguradoParcelaFiltro(Long codParcela){
		super();
		this.codParcela = codParcela;
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(CapitalAsegurado.class);
        criteria.add(Restrictions.eq("parcela.idparcela",this.codParcela));
        
        criteria.addOrder(Order.asc("tipoCapital.codtipocapital"));
        
        return criteria;
	}
}

