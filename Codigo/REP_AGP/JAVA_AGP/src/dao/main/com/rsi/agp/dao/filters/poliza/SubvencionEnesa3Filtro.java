package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;

public class SubvencionEnesa3Filtro implements Filter
{

	@Override
	public Criteria getCriteria(Session sesion) 
	{
		final Criteria criteria = sesion.createCriteria(SubvencionEnesa.class);
		Criterion c = Restrictions.eq("id.codtiposubvenesa", new BigDecimal(3));
		criteria.add(c);
		return criteria;
	}

}
