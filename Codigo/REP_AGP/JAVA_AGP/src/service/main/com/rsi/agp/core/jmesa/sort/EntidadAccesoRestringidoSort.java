package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;

public class EntidadAccesoRestringidoSort implements CriteriaCommand {

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
		// Entidad
		if (EntidadAccesoRestringido.CAMPO_CODENTIDAD.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(EntidadAccesoRestringido.CAMPO_CODENTIDAD) : Order.desc(EntidadAccesoRestringido.CAMPO_CODENTIDAD));
		}
		// Acceso al diseñador
		else if (EntidadAccesoRestringido.CAMPO_ACCESO_DISENADOR.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(EntidadAccesoRestringido.CAMPO_ACCESO_DISENADOR) : Order.desc(EntidadAccesoRestringido.CAMPO_ACCESO_DISENADOR));
		}
		// Acceso al generador
		else if (EntidadAccesoRestringido.CAMPO_ACCESO_GENERADOR.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(EntidadAccesoRestringido.CAMPO_ACCESO_GENERADOR) : Order.desc(EntidadAccesoRestringido.CAMPO_ACCESO_GENERADOR));
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
