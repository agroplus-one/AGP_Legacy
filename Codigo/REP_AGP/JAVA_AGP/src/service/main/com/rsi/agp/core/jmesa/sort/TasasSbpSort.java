package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.dao.tables.sbp.TasasSbp;

public class TasasSbpSort implements CriteriaCommand{
	
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
		// Plan
		if (TasasSbp.CAMPO_PLAN.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(TasasSbp.CAMPO_PLAN) : Order.desc(TasasSbp.CAMPO_PLAN));
		}
		// Linea
		else if (TasasSbp.CAMPO_LINEA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(TasasSbp.CAMPO_LINEA) : Order.desc(TasasSbp.CAMPO_LINEA));
		}
		// Provincia
		else if (TasasSbp.CAMPO_CODPROVINCIA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(TasasSbp.CAMPO_CODPROVINCIA) : Order.desc(TasasSbp.CAMPO_CODPROVINCIA));
		}
		// Tasa de incendio
		else if (TasasSbp.CAMPO_TASA_INCENDIO.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(TasasSbp.CAMPO_TASA_INCENDIO) : Order.desc(TasasSbp.CAMPO_TASA_INCENDIO));
		}
		// Tasa de pedrisco
		else if (TasasSbp.CAMPO_TASA_PEDRISCO.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(TasasSbp.CAMPO_TASA_PEDRISCO) : Order.desc(TasasSbp.CAMPO_TASA_PEDRISCO));
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
