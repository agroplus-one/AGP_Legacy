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
public class MtoUsuariosSort implements CriteriaCommand {

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
		// codUsuario
		if (property.equals("codusuario")){
			criteria.addOrder(isAsc ? Order.asc("codusuario") : Order.desc("codusuario"));
		}
		// nombreusu
		else if (property.equals("nombreusu")){
			criteria.addOrder(isAsc ? Order.asc("nombreusu") : Order.desc("nombreusu"));
		}
		// perfil
		else if (property.equals("tipousuario")){
			criteria.addOrder(isAsc ? Order.asc("tipousuario") : Order.desc("tipousuario"));
		} 
		//oficina.id.codentidad
		else if (property.equals("oficina.id.codentidad")){
			criteria.addOrder(isAsc ? Order.asc("oficina.id.codentidad") : Order.desc("oficina.id.codentidad"));
		} 
		//oficina.id.codoficina
		else if (property.equals("oficina.id.codoficina")){
			criteria.addOrder(isAsc ? Order.asc("oficina.id.codoficina") : Order.desc("oficina.id.codoficina"));
		} 
		//subentidadMediadora.id.codentidad
		else if (property.equals("subentidadMediadora.id.codentidad")){
			criteria.addOrder(isAsc ? Order.asc("subentidadMediadora.id.codentidad") : Order.desc("subentidadMediadora.id.codentidad"));
			criteria.addOrder(isAsc ? Order.asc("subentidadMediadora.id.codsubentidad") : Order.desc("subentidadMediadora.id.codsubentidad"));
		} 
		//delegacion
		else if (property.equals("delegacion")){
			criteria.addOrder(isAsc ? Order.asc("delegacion") : Order.desc("delegacion"));
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
