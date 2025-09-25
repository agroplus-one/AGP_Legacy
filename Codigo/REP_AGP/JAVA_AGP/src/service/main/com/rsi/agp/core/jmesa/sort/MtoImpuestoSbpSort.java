package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class MtoImpuestoSbpSort implements CriteriaCommand{
	
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

	/**
	 * Compone el objeto Criteria para el campo y el orden indicado
	 * @param criteria Objecto Criteria
	 * @param property Campo de la tabla a ordenar
	 * @param order Sentido de la ordenación
	 */
	private void buildCriteria(Criteria criteria, String property, String order) {
		
		// Variable que indica si la ordenación es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);
				
		// Añade el orden al Criteria dependiendo del campo y del sentido de la ordenación
		// codplan
		if (property.equals("codplan")){
			criteria.addOrder(isAsc ? Order.asc("codplan") : Order.desc("codplan"));
		}
		// codimpuesto
		else if (property.equals("impuestoSbp.codigo")){
			criteria.addOrder(isAsc ? Order.asc("impuestoSbp.codigo") : Order.desc("impuestoSbp.codigo"));
		}
		// nomimpuesto
		else if (property.equals("impuestoSbp.descripcion")){
			criteria.addOrder(isAsc ? Order.asc("impuestoSbp.descripcion") : Order.desc("impuestoSbp.descripcion"));
		} 
		// nombase
		else if (property.equals("baseSbp.base")){
			criteria.addOrder(isAsc ? Order.asc("baseSbp.base") : Order.desc("baseSbp.base"));
		} 
		// valor
		else if (property.equals("valor")){
			criteria.addOrder(isAsc ? Order.asc("valor") : Order.desc("valor"));
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
