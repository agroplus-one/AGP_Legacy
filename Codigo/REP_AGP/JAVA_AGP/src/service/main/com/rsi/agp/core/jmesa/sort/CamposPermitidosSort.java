package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class CamposPermitidosSort implements CriteriaCommand {
	
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
		
		// Variable que indica si la ordenación es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);
		
		// Añade el orden al Criteria dependiendo del campo y del sentido de la ordenación
		// Descripción
		if (property.equals("descripcion")) criteria.addOrder(isAsc ? Order.asc("descripcion") : Order.desc("descripcion"));
		// TABLA ORIGEN
		else if (property.equals("vistaCampo.vista.nombre")) criteria.addOrder(isAsc ? Order.asc("vis.nombre") : Order.desc("vis.nombre"));
		// CAMPO
		else if (property.equals("vistaCampo.nombre")) criteria.addOrder(isAsc ? Order.asc("visC.nombre") : Order.desc("visC.nombre"));
		// TIPO
		else if (property.equals("vistaCampo.tipo")) criteria.addOrder(isAsc ? Order.asc("visC.tipo") : Order.desc("visC.tipo"));
		
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
