package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class PrimaMinimaSbpSort implements CriteriaCommand{

	List<Sort> sorts = new ArrayList<Sort>();
	@Override
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
        if (order.equals(Sort.ASC)) {// ***** ASC *****
    		// Plan
    		if (property.equals("linea.codplan")){
    			criteria.addOrder(Order.asc("lin.codplan"));
    		}
    		// Linea
    		else if (property.equals("linea.codlinea")){
    			criteria.addOrder(Order.asc("lin.codlinea"));
    		}
    		// Clase
    		else if (property.equals("primaMinima")){
    			criteria.addOrder(Order.asc("primaMinima"));
    		}
        } else if (order.equals(Sort.DESC)) {// ***** DESC *****
    		// Plan
    		if (property.equals("linea.codplan")){
    			criteria.addOrder(Order.desc("lin.codplan"));
    		}
    		// Linea
    		else if (property.equals("linea.codlinea")){
    			criteria.addOrder(Order.desc("lin.codlinea"));
    		}
    		// Clase
    		else if (property.equals("primaMinima")){
    			criteria.addOrder(Order.desc("primaMinima"));
    		}
        }
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
