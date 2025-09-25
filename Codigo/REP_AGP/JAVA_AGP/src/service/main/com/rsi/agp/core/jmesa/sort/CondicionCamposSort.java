package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class  CondicionCamposSort implements CriteriaCommand {

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
    	
    		if (property.equals("nombre")){
    			criteria.addOrder(Order.asc("nombre"));
    		}
    		
    		else if (property.equals("idoperador")){
    			criteria.addOrder(Order.asc("idtablaoperadores"));
    		}
    		
    		else if (property.equals("condicion")){
    			criteria.addOrder(Order.asc("condicion"));
    		}
    		
    	
    		
    		
        } else if (order.equals(Sort.DESC)) {// ***** DESC *****

    		if (property.equals("nombre")){
    			criteria.addOrder(Order.desc("nombre"));
    		}
    		
    		else if (property.equals("idoperador")){
    			criteria.addOrder(Order.desc("idtablaoperadores"));
    		}
    		
    		else if (property.equals("condicion")){
    			criteria.addOrder(Order.desc("condicion"));
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