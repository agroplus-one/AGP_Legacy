package com.rsi.agp.dao.filters.mtoinforme;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;

public class DatoInformesFiltro implements Filter {
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(DatoInformes.class);
		
		criteria.addOrder(Order.asc("orden"));
		
        return criteria;
	}
}
