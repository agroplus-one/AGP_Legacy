package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class InformeSort implements CriteriaCommand {

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
		// Nombre
		if (property.equals("nombre")) criteria.addOrder(isAsc ? Order.asc("nombre") : Order.desc("nombre"));
		// Titulo1
		if (property.equals("titulo1")) criteria.addOrder(isAsc ? Order.asc("titulo1") : Order.desc("titulo1"));
		// Titulo2
		if (property.equals("titulo2")) criteria.addOrder(isAsc ? Order.asc("titulo2") : Order.desc("titulo2"));
		// Titulo3
		if (property.equals("titulo3")) criteria.addOrder(isAsc ? Order.asc("titulo3") : Order.desc("titulo3"));
		// Visibilidad
		if (property.equals("visibilidad")) criteria.addOrder(isAsc ? Order.asc("visibilidad") : Order.desc("visibilidad"));
		// Cuenta
		if (property.equals("cuenta")) criteria.addOrder(isAsc ? Order.asc("cuenta") : Order.desc("cuenta"));
		// propietario
		if (property.equals("usuario.codusuario")) criteria.addOrder(isAsc ? Order.asc("usuario.codusuario") : Order.desc("usuario.codusuario"));
		
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
