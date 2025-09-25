package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class FechasContratacionSbpSort implements CriteriaCommand{

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
    		//cod cultivo
    		else if (property.equals("cultivo.id.codcultivo")){
    			criteria.addOrder(Order.asc("cultivo.id.codcultivo"));
    		}
    		// desc cultivo
    		else if (property.equals("cultivo.descultivo")){
    			criteria.addOrder(Order.asc("cult.descultivo"));
    		}
    		// Fecha Inicio
    		else if (property.equals("fechainicio")){
    			criteria.addOrder(Order.asc("fechainicio"));
    		}
    		// Fecha Fin
    		else if (property.equals("fechafin")){
    			criteria.addOrder(Order.asc("fechafin"));
    		}
    		//Fecha Fin Suplementos
    		else if (property.equals("fechaFinSuplementos")){
    			criteria.addOrder(Order.asc("fechaFinSuplementos"));
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
    		//cod cultivo
    		else if (property.equals("cultivo.id.codcultivo")){
    			criteria.addOrder(Order.desc("cultivo.id.codcultivo"));
    		}
    		// desc cultivo
    		else if (property.equals("cultivo.descultivo")){
    			criteria.addOrder(Order.desc("cult.descultivo"));
    		}
    		// Fecha Inicio
    		else if (property.equals("fechainicio")){
    			criteria.addOrder(Order.desc("fechainicio"));
    		}
    		// Fecha Fin
    		else if (property.equals("fechafin")){
    			criteria.addOrder(Order.desc("fechafin"));
    		}
    		//Fecha Fin Suplementos
    		else if (property.equals("fechaFinSuplementos")){
    			criteria.addOrder(Order.asc("fechaFinSuplementos"));
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
