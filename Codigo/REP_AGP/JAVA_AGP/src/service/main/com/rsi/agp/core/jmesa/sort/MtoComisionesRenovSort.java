package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

/**
 * @author U029769
 *
 */
public class MtoComisionesRenovSort implements CriteriaCommand {

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
		// codplan
		if (property.equals("codplan")){
			criteria.addOrder(isAsc ? Order.asc("codplan") : Order.desc("codplan"));
		}
		// codlinea
		else if (property.equals("codlinea")){
			criteria.addOrder(isAsc ? Order.asc("codlinea") : Order.desc("codlinea"));
		}
		// codentidad
		else if (property.equals("codentidad")){
			criteria.addOrder(isAsc ? Order.asc("codentidad") : Order.desc("codentidad"));
		} 
		//codentmed
		else if (property.equals("codentmed")){
			criteria.addOrder(isAsc ? Order.asc("codentmed") : Order.desc("codentmed"));
		} 
		//codsubmed
		else if (property.equals("codsubmed")){
			criteria.addOrder(isAsc ? Order.asc("codsubmed") : Order.desc("codsubmed"));
		} 
		//codsubmed
		else if (property.equals("idgrupo")){
			criteria.addOrder(isAsc ? Order.asc("idgrupo") : Order.desc("idgrupo"));
		} 
		//modulo
		else if (property.equals("codmodulo")){
			criteria.addOrder(isAsc ? Order.asc("codmodulo") : Order.desc("codmodulo"));
		}
		//refimporte
		else if (property.equals("refimporte")){
			criteria.addOrder(isAsc ? Order.asc("refimporte") : Order.desc("refimporte"));
		} 
		//impDesde
		else if (property.equals("impDesde") ){
			criteria.addOrder(isAsc ? Order.asc("impDesde") : Order.desc("impDesde"));
		} 
		//impHasta
		else if (property.equals("impHasta") ){
			criteria.addOrder(isAsc ? Order.asc("impHasta") : Order.desc("impHata"));
		} 
		//comision
		else if (property.equals("comision") ){
			criteria.addOrder(isAsc ? Order.asc("comision") : Order.desc("comision"));
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
