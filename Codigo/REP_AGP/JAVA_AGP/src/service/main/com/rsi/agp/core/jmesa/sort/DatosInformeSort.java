package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class DatosInformeSort implements CriteriaCommand {

	List<Sort> sorts = new ArrayList<Sort>();
	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
            buildCriteria(criteria, sort.getProperty(), sort.getOrder());
        }
		return criteria;
	}
	
	
	private void buildCriteria(Criteria criteria, String property, String order) {
		
		// Variable que indica si la ordenación es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);
		
		// Añade el orden al Criteria dependiendo del campo y del sentido de la ordenación
		// Orden
		if (property.equals("orden")) criteria.addOrder(isAsc ? Order.asc("orden") : Order.desc("orden"));
		// Abreviado
		if (property.equals("abreviado")) criteria.addOrder(isAsc ? Order.asc("abreviado") : Order.desc("abreviado"));
		// Tipo
		if (property.equals("tipo")) criteria.addOrder(isAsc ? Order.asc("tipo") : Order.desc("tipo"));
		// Formato
		if (property.equals("formato")) criteria.addOrder(isAsc ? Order.asc("formato") : Order.desc("formato"));
		// Decimales
		if (property.equals("decimales")) criteria.addOrder(isAsc ? Order.asc("decimales") : Order.desc("decimales"));
		// Totaliza
		if (property.equals("totaliza")) criteria.addOrder(isAsc ? Order.asc("totaliza") : Order.desc("totaliza"));
		// Total por grupo
		if (property.equals("total_por_grupo")) criteria.addOrder(isAsc ? Order.asc("total_por_grupo") : Order.desc("total_por_grupo"));
		
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