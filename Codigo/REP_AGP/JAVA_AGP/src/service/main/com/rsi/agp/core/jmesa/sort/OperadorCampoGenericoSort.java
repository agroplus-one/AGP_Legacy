package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

public class OperadorCampoGenericoSort {
		List<Sort> sorts = new ArrayList<Sort>();
		
		public Criteria execute(Criteria criteria) {
			
			for (Sort sort : sorts) {
	            buildCriteria(criteria, sort.getProperty(), sort.getOrder());
	        }
			return criteria;
		}
		
		public void addSort(String property, String order) {
			sorts.add(new Sort(property, order));
			
		}
		
		private void buildCriteria(Criteria criteria, String property, String order) {
			
			// Añade el orden al Criteria dependiendo del campo y del sentido de la ordenación
			criteria.addOrder(Sort.ASC.equals(order) ? Order.asc(property) : Order.desc(property));
			
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
