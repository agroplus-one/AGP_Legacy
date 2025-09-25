package com.rsi.agp.dao.filters.config;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.TablaCondicionado;

public class TablaCondicionadoTiposCamposLimitesFiltro implements Filter {

	public Criteria getCriteria(Session sesion) {
		
		Criteria criteria = sesion.createCriteria(TablaCondicionado.class);
		criteria.add(Restrictions.gt("codtablacondicionado", new BigDecimal(9000)));
		criteria.addOrder(Order.asc("codtablacondicionado"));
		return criteria;
	}

}
