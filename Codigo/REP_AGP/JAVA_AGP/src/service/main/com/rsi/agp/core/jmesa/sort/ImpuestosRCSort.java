package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.dao.tables.rc.ImpuestosRC;

public class ImpuestosRCSort implements CriteriaCommand {

	List<Sort> sorts = new ArrayList<Sort>();
	
	@Override
	public Criteria execute(Criteria criteria) {
		for(Sort sort : sorts){
			buildCriteria(criteria, sort.getProperty(), sort.getOrder());
		}
		return criteria;
	}
	
	public void addSort(final String property, final String order) {
		sorts.add(new Sort(property, order));
	}
	
	private void buildCriteria(final Criteria criteria, final String property,
			final String order) {

		// Variable que indica si la ordenacion es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);
		
		// Agrega el orden al Criteria dependiendo del campo y del sentido de la ordenacion
		// PLAN
		if (property.equals("codPlan")) {
			criteria.addOrder(isAsc ? Order.asc("codPlan") : Order.desc("codPlan"));
		// VALOR
		} else if (property.equals("valor")) {
			criteria.addOrder(isAsc ? Order.asc("valor") : Order.desc("valor"));
		// BASE
		} else if (property.equals("baseSbp.base")) {
			criteria.addOrder(isAsc ? Order.asc("baseSbp.base") : Order.desc("baseSbp.base"));
		// CODIGO
		} else if (property.equals("impuestoSbp.codigo")) {
			criteria.addOrder(isAsc ? Order.asc("impuestoSbp.codigo") : Order.desc("impuestoSbp.codigo"));
		// DESCRIPCION
		} else if (property.equals("impuestoSbp.descripcion")) {
			criteria.addOrder(isAsc ? Order.asc("impuestoSbp.descripcion") : Order.desc("impuestoSbp.descripcion"));
		}
	}
	
	private static class Sort {
		public final static String ASC = "asc";

		private final String property;
		private final String order;

		public Sort(String property, String order) {
			this.property = property;
			this.order = order;
		}

		public String getProperty() {
			return property;
		}

		public String getOrder() {
			return order;
		}
	}

}
