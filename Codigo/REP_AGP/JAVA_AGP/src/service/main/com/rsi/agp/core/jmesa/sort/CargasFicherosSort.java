package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;



public class CargasFicherosSort implements CriteriaCommand{
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
    		// fichero
    		if (property.equals("fichero")){
    			criteria.addOrder(Order.asc("fichero"));
    		}
    		// tipo
    		else if (property.equals("tipo")){
    			criteria.addOrder(Order.asc("tipo"));
    		}
    		// plan
    		else if (property.equals("plan")){
    			criteria.addOrder(Order.asc("plan"));
    		}
    		//Linea
    		else if (property.equals("linea")){
    			criteria.addOrder(Order.asc("linea"));
    		}
        } else if (order.equals(Sort.DESC)) {// ***** DESC *****
        	// fichero
    		if (property.equals("fichero")){
    			criteria.addOrder(Order.desc("fichero"));
    		}
    		// tipo
    		else if (property.equals("tipo")){
    			criteria.addOrder(Order.desc("tipo"));
    		}
    		// plan
    		else if (property.equals("plan")){
    			criteria.addOrder(Order.desc("plan"));
    		}
    		//Linea
    		else if (property.equals("linea")){
    			criteria.addOrder(Order.desc("linea"));
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
