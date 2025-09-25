package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class CamposCalculadosSort implements CriteriaCommand {

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
		// Nombre
		if (property.equals("nombre")) criteria.addOrder(isAsc ? Order.asc("nombre") : Order.desc("nombre"));
		// Operando 1
		if (property.equals("camposPermitidosByIdoperando1.abreviado")) {
			criteria.addOrder(isAsc ? Order.asc("operando1.vistaCampo.nombre") : Order.desc("operando1.vistaCampo.nombre"));
		}
		// Operando 2
		if (property.equals("camposPermitidosByIdoperando2.abreviado")) {
			criteria.addOrder(isAsc ? Order.asc("operando2.vistaCampo.nombre") : Order.desc("operando2.vistaCampo.nombre"));
		}
		// Operador
		if (property.equals("idoperador")) criteria.addOrder(isAsc ? Order.asc("idoperador") : Order.desc("idoperador"));
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
