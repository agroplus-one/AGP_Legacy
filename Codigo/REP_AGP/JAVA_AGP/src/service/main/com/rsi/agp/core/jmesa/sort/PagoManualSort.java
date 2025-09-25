package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class PagoManualSort implements CriteriaCommand {

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
	 * @param order Sentido de la ordenacion
	 */
	private void buildCriteria(Criteria criteria, String property, String order) {
		
		// Variable que indica si la ordenacion es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);
				
		// Anade el orden al Criteria dependiendo del campo y del sentido de la ordenacion
		// id.codentidad
		if (property.equals("id.codentidad")){
			criteria.addOrder(isAsc ? Order.asc("id.codentidad") : Order.desc("id.codentidad"));
		}
		// id.codoficina
		else if (property.equals("id.codoficina")){
			criteria.addOrder(isAsc ? Order.asc("id.codoficina") : Order.desc("id.codoficina"));
		}
		// nomimpuesto
		else if (property.equals("pagoManual")){
			criteria.addOrder(isAsc ? Order.asc("pagoManual") : Order.desc("pagoManual"));
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
