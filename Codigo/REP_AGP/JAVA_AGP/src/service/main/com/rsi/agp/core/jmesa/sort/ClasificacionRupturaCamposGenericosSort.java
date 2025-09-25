package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class ClasificacionRupturaCamposGenericosSort implements CriteriaCommand {

	List<Sort> sorts = new ArrayList<Sort>();

	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
			buildCriteria(criteria, sort.getProperty(), sort.getOrder());
		}
		return criteria;
	}

	private void buildCriteria(Criteria criteria, String property, String order) {
		if (order.equals(Sort.ASC)) {// ***** ASC *****
			// Plan
			if (property.equals("id.iddatoInforme")) {
				criteria.addOrder(Order.asc("nombre"));
			}

			else if (property.equals("sentido")) {
				criteria.addOrder(Order.asc("sentido"));
			}

			else if (property.equals("ruptura")) {
				criteria.addOrder(Order.asc("ruptura"));
			}

		} else if (order.equals(Sort.DESC)) {// ***** DESC *****

			if (property.equals("id.iddatoInforme")) {
				criteria.addOrder(Order.desc("nombre"));
			}

			else if (property.equals("sentido")) {
				criteria.addOrder(Order.desc("sentido"));
			}

			else if (property.equals("ruptura")) {
				criteria.addOrder(Order.desc("ruptura"));
			}

		}
	}

	public void addSort(String property, String order) {
		sorts.add(new Sort(property, order));

	}

	private static class Sort {
		public final static String ASC = "asc";
		public final static String DESC = "desc";

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