package com.rsi.agp.core.jmesa.filter;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class FicheroUnificadoFilter extends GenericoFilter implements
		IGenericoFilter {
	
	
	@Override
	public Criteria execute(final Criteria criteria) {
		for (Filter filter : this.filters) {
			if("nombreFichero".equals(filter.getProperty())){
				String val = (String) filter.getValue();
				criteria.add(Restrictions.ilike("nombreFichero", val, MatchMode.ANYWHERE));	
			} 
			if("tipoFichero".equals(filter.getProperty())){
				Character val = (Character) filter.getValue();
				if (Character.valueOf('C').equals(val)
						|| Character.valueOf('U').equals(val)) {
					criteria.add(Restrictions.in(filter.getProperty(),
							new Character[] { 'C', 'U' }));
				} else {
					this.buildCriteria(criteria, filter.getProperty(),
							filter.getValue(), filter.getTipo());
				}
			} else {
				this.buildCriteria(criteria, filter.getProperty(),
						filter.getValue(), filter.getTipo());
			}
		}
		return criteria;
	}
}
