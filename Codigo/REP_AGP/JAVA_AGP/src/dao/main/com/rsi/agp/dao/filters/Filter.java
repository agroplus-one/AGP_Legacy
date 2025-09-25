package com.rsi.agp.dao.filters;

import org.hibernate.Criteria;
import org.hibernate.Session;

public interface Filter {
	public Criteria getCriteria(Session sesion);
}
