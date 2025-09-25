package com.rsi.agp.core.jmesa.sort;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.jmesa.limit.Limit;
import org.jmesa.limit.SortSet;

public class InformesComisionesUnificadoSort extends GenericoSort {

	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
            buildCriteria(criteria, sort.getProperty(), sort.getOrder());
        }
		return criteria;
	}
	
	private void buildCriteria(Criteria criteria, String property, String order) {
		boolean isAsc = Sort.ASC.equals(order);
		
		// Anade el orden al Criteria dependiendo del campo y del sentido de la ordenacion
		criteria.addOrder(isAsc ? Order.asc(property) : Order.desc(property));
		
	}
	
	@Override
	public void getConsultaSort(Limit limit) {
		//MtoDescuentosSort consultaSort = new MtoDescuentosSort();
		SortSet sortSet = limit.getSortSet();
		Collection<org.jmesa.limit.Sort> sorts = sortSet.getSorts();
		for (org.jmesa.limit.Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			this.addSort(property, order);
			if (property.equals("entmediadora")) {
				this.addSort("subentmediadora", order);
			}
		}
		
		//return consultaSort;
	}
	
}
