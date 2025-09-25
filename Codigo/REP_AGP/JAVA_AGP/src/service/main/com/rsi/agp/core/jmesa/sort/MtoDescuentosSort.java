package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class MtoDescuentosSort implements CriteriaCommand {

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
		 
		//oficina.id.codentidad
		if (property.equals("subentidadMediadora.entidad.codentidad")){
			criteria.addOrder(isAsc ? Order.asc("subentidadMediadora.entidad.codentidad") : Order.desc("subentidadMediadora.entidad.codentidad"));
		} 
		//oficina.id.codoficina
		else if (property.equals("oficina.id.codoficina")){
			criteria.addOrder(isAsc ? Order.asc("oficina.id.codoficina") : Order.desc("oficina.id.codoficina"));
		} 
		//subentidadMediadora.id.codentidad
		else if (property.equals("subentidadMediadora.id.codentidad")){
			criteria.addOrder(isAsc ? Order.asc("subentidadMediadora.id.codentidad") : Order.desc("subentidadMediadora.id.codentidad"));
		} 
		//delegacion
		else if (property.equals("delegacion")){
			criteria.addOrder(isAsc ? Order.asc("delegacion") : Order.desc("delegacion"));
		} 
		//plan
		else if (property.equals("linea.codplan")){
			criteria.addOrder(isAsc ? Order.asc("linea.codplan") : Order.desc("linea.codplan"));
		} 
		//linea
		else if (property.equals("linea.codlinea")){
			criteria.addOrder(isAsc ? Order.asc("linea.codlinea") : Order.desc("linea.codlinea"));
		}
		//permitirRecargo
		else if (property.equals("permitirRecargo")){
			criteria.addOrder(isAsc ? Order.asc("permitirRecargo") : Order.desc("permitirRecargo"));
		}
		//verComisiones
		else if (property.equals("verComisiones")){
			criteria.addOrder(isAsc ? Order.asc("verComisiones") : Order.desc("verComisiones"));
		}
		//pctDescMax
		else if (property.equals("pctDescMax")){
			criteria.addOrder(isAsc ? Order.asc("pctDescMax") : Order.desc("pctDescMax"));
		} 
		//fechaBaja
		else if (property.equals("fechaBaja")){
			criteria.addOrder(isAsc ? Order.asc("fechaBaja") : Order.desc("fechaBaja"));
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
