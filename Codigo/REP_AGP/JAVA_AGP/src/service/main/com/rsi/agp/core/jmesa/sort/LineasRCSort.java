package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class LineasRCSort implements CriteriaCommand {

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
		// ESPECIE
		else if (property.equals("codespecie")) {
			criteria.addOrder(isAsc ? Order.asc("codespecie") : Order
					.desc("codespecie"));
		}
		else if (property.equals("descespecie")) {
			criteria.addOrder(isAsc ? Order.asc("descespecie") : Order
					.desc("descespecie"));
		}
		// REGIMEN
		else if (property.equals("codregimen")) {
			criteria.addOrder(isAsc ? Order.asc("codregimen") : Order
					.desc("codregimen"));
		}
		else if (property.equals("descregimen")) {
			criteria.addOrder(isAsc ? Order.asc("descregimen") : Order
					.desc("descregimen"));
		}
		// TIPO DE CAPITAL
		else if (property.equals("codtipocapital")) {
			criteria.addOrder(isAsc ? Order.asc("codtipocapital") : Order
					.desc("codtipocapital"));
		}
		else if (property.equals("desctipocapital")) {
			criteria.addOrder(isAsc ? Order.asc("desctipocapital") : Order
					.desc("desctipocapital"));
		}
		// ESPECIE PARA RC
		else if (property.equals("especiesRC.descripcion")) {
			criteria.addOrder(isAsc ? Order.asc("especiesRC.descripcion")
					: Order.desc("especiesRC.descripcion"));
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