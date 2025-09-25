package com.rsi.agp.core.jmesa.filter;

import org.hibernate.Criteria;

public class ExplotacionesFilter extends GenericoFilter {

	@Override
	public Criteria execute(final Criteria criteria) {
		for (Filter filter : this.filters) {
			this.buildCriteria(criteria, filter.getProperty(),
					filter.getValue(), filter.getTipo());
		}
		return criteria;
	}

	public void execute() {
		// Empty method
	}
}