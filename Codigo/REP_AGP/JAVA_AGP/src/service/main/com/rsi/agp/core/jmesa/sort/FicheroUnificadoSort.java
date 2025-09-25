package com.rsi.agp.core.jmesa.sort;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.sort.GenericoSort.Sort;

public class FicheroUnificadoSort extends GenericoSort implements IGenericoSort {
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
}
