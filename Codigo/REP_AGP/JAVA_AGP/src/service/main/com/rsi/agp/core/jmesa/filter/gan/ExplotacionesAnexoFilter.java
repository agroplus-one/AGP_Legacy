package com.rsi.agp.core.jmesa.filter.gan;

import org.hibernate.Criteria;

import com.rsi.agp.core.jmesa.filter.GenericoFilter;

public class ExplotacionesAnexoFilter extends GenericoFilter {

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