package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class DatosRCSort implements CriteriaCommand {

	List<Sort> sorts = new ArrayList<Sort>();

	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
			buildCriteria(criteria, sort.getProperty(), sort.getOrder());
		}
		return criteria;
	}

	public void addSort(final String property, final String order) {
		sorts.add(new Sort(property, order));
	}

	private void buildCriteria(final Criteria criteria, final String property,
			final String order) {

		// Variable que indica si la ordenacion es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);

		// Anade el orden al Criteria dependiendo del campo y del sentido de la
		// ordenacion
		// PLAN
		if (property.equals("linea.codplan")) {
			criteria.addOrder(isAsc ? Order.asc("lineaseguro.codplan") : Order
					.desc("lineaseguro.codplan"));
		}
		// LINEA
		else if (property.equals("linea.codlinea")) {
			criteria.addOrder(isAsc ? Order.asc("lineaseguro.codlinea") : Order
					.desc("lineaseguro.codlinea"));
		}
		// ENTIDAD MEDIADORA
		else if (property.equals("subentidadMediadora.id.codentidad")) {
			criteria.addOrder(isAsc ? Order
					.asc("subentidadMediadora.id.codentidad") : Order
					.desc("subentidadMediadora.id.codentidad"));
		}
		// SUBENTIDAD MEDIADORA
		else if (property.equals("subentidadMediadora.id.codsubentidad")) {
			criteria.addOrder(isAsc ? Order
					.asc("subentidadMediadora.id.codsubentidad") : Order
					.desc("subentidadMediadora.id.codsubentidad"));
		}
		// ESPECIE PARA RC
		else if (property.equals("especiesRC.descripcion")) {
			criteria.addOrder(isAsc ? Order.asc("especiesRC.descripcion")
					: Order.desc("especiesRC.descripcion"));
		}
		// REGIMEN PARA RC
		else if (property.equals("regimenRC.descripcion")) {
			criteria.addOrder(isAsc ? Order.asc("regimenRC.descripcion")
					: Order.desc("regimenRC.descripcion"));
		}
		// SUMA ASEGURADA
		else if (property.equals("sumaAseguradaRC.valor")) {
			criteria.addOrder(isAsc ? Order.asc("sumaAseguradaRC.valor")
					: Order.desc("sumaAseguradaRC.valor"));
		}
		// TASA
		else if (property.equals("tasa")) {
			criteria.addOrder(isAsc ? Order.asc("tasa") : Order.desc("tasa"));
		}
		// FRANQUICIA
		else if (property.equals("franquicia")) {
			criteria.addOrder(isAsc ? Order.asc("franquicia") : Order
					.desc("franquicia"));
		}
		// PRIMA MINIMA
		else if (property.equals("primaMinima")) {
			criteria.addOrder(isAsc ? Order.asc("primaMinima") : Order
					.desc("primaMinima"));
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