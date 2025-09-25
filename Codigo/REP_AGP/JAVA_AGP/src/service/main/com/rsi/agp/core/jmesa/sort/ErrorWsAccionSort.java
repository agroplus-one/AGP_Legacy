package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class ErrorWsAccionSort implements CriteriaCommand {

	List<Sort> sorts = new ArrayList<Sort>();

	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
			buildCriteria(criteria, sort.getProperty(), sort.getOrder());
		}
		return criteria;
	}

	private void buildCriteria(Criteria criteria, String property, String order) {
		if (order.equals(Sort.ASC)) {// ***** ASC *****
			// Plan
			if (property.equals("linea.codplan")) {
				criteria.addOrder(Order.asc("lin.codplan"));
			}
			// Linea
			else if (property.equals("linea.codlinea")) {
				criteria.addOrder(Order.asc("lin.codlinea"));
			}
			/* Pet. 63481 (Resolución de Defectos) */
			// Catálogo de Error
			else if (property.equals("errorWs.id.catalogo")) {
				criteria.addOrder(Order.asc("error.id.catalogo"));
			}
			// Coderror
			else if (property.equals("errorWs.id.coderror")) {

				criteria.addOrder(Order.asc("errorWs.id.coderror"));
			}
			// Descripcion
			else if (property.equals("errorWs.descripcion")) {
				criteria.addOrder(Order.asc("error.descripcion"));
			}
			// Descripcion_Tipo
			else if (property.equals("errorWs.errorWsTipo.descripcion")) {
				criteria.addOrder(Order.asc("ErrTipo.descripcion"));
			}
			// Servicio
			else if (property.equals("servicio")) {
				criteria.addOrder(Order.asc("servicio"));
			}
			// Ocultar
			else if (property.equals("ocultar")) {
				criteria.addOrder(Order.asc("ocultar"));
			}
			// Entidad
			else if (property.equals("entidad.codentidad")) {
				criteria.addOrder(Order.asc("entidad.codentidad"));
			}

		} else if (order.equals(Sort.DESC)) {// ***** DESC *****
			// Plan
			if (property.equals("linea.codplan")) {
				criteria.addOrder(Order.desc("lin.codplan"));
			}
			// Linea
			else if (property.equals("linea.codlinea")) {
				criteria.addOrder(Order.desc("lin.codlinea"));
			}
			// Coderror
			else if (property.equals("errorWs.id.coderror")) {
				criteria.addOrder(Order.desc("error.id.coderror"));
			}
			// Descripcion
			else if (property.equals("errorWs.descripcion")) {
				criteria.addOrder(Order.desc("error.descripcion"));
			}
			// Descripcion_Tipo
			else if (property.equals("errorWs.errorWsTipo.descripcion")) {
				criteria.addOrder(Order.desc("ErrTipo.descripcion"));
			}
			// Servicio
			else if (property.equals("servicio")) {
				criteria.addOrder(Order.desc("servicio"));
			}
			// Ocultar
			else if (property.equals("ocultar")) {
				criteria.addOrder(Order.desc("ocultar"));
			}
			// Entidad
			else if (property.equals("entidad.codentidad")) {
				criteria.addOrder(Order.desc("entidad.codentidad"));
			}
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
