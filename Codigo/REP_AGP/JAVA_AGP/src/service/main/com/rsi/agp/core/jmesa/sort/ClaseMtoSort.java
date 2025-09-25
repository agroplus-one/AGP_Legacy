package com.rsi.agp.core.jmesa.sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class ClaseMtoSort implements CriteriaCommand{

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
    		if (property.equals("linea.codplan")){
    			criteria.addOrder(Order.asc("lin.codplan"));
    		}
    		// Linea
    		else if (property.equals("linea.codlinea")){
    			criteria.addOrder(Order.asc("lin.codlinea"));
    		}
    		// Clase
    		else if (property.equals("clase")){
    			criteria.addOrder(Order.asc("clase"));
    		}
    		// Descripcion
    		else if (property.equals("descripcion") ){
    			criteria.addOrder(Order.asc("descripcion"));
    		}
    		// Maxpolizas
    		else if (property.equals("maxpolizas")){
    			criteria.addOrder(Order.asc("maxpolizas"));
    		}
    		// comprobarAac
    		else if (property.equals("comprobarAac")){
    			criteria.addOrder(Order.asc("comprobarAac"));
    		}
    		// rdtoHistorico
    		else if (property.equals("rdtoHistorico")){
    			criteria.addOrder(Order.asc("rdtoHistorico"));
    		}
    		// comprobarRce (Pet. 63428)
    		else if (property.equals("comprobarRce")){
    			
    			criteria.addOrder(Order.asc("comprobarRce"));
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
    		else if (property.equals("clase")){
    			criteria.addOrder(Order.desc("clase"));
    		}
    		// Descripcion
    		else if (property.equals("descripcion") ){
    			criteria.addOrder(Order.desc("descripcion"));
    		}
    		// Maxpolizas
    		else if (property.equals("maxpolizas")){
    			criteria.addOrder(Order.asc("maxpolizas"));
    		}
    		// comprobarAac
    		else if (property.equals("comprobarAac")){
    			criteria.addOrder(Order.desc("comprobarAac"));
    		}
    		// rdtoHistorico
    		else if (property.equals("rdtoHistorico")){
    			criteria.addOrder(Order.desc("rdtoHistorico"));
    		}
    		// comprobarRce
    		else if (property.equals("comprobarRce")){
    			criteria.addOrder(Order.desc("comprobarRce"));
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
