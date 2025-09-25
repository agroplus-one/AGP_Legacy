package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class DocAgroseguroSort implements CriteriaCommand {

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

	private void buildCriteria(final Criteria criteria, final String property, final String order) {

		// Variable que indica si la ordenacion es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);

		// Anade el orden al Criteria dependiendo del campo y del sentido de la
		// ordenacion
		// PLAN
		if (property.equals("codplan")) {
			criteria.addOrder(isAsc ? Order.asc("codplan") : Order.desc("codplan"));
		}
		// LINEA
		else if (property.equals("codlinea")) {
			criteria.addOrder(isAsc ? Order.asc("codlinea") : Order.desc("codlinea"));
		}
		// CODENTIDAD
		else if (property.equals("codentidad")) {
			criteria.addOrder(isAsc ? Order.asc("codentidad") : Order.desc("codentidad"));
		}
		// TIPO DOCUMENTO
		else if (property.equals("docAgroseguroTipo.descripcion")) {
			criteria.addOrder(isAsc ? Order.asc("tipo.descripcion") : Order.desc("tipo.descripcion"));
		}
		// DESCRIPCION
		else if (property.equals("descripcion")) {
			criteria.addOrder(isAsc ? Order.asc("descripcion") : Order.desc("descripcion"));
		}
		// FICHERO
		else if (property.equals("nombre")) {
			criteria.addOrder(isAsc ? Order.asc("nombre") : Order.desc("nombre"));
		}
		// P0079014 ** MODIF TAM (28/04/2022) ** Defecto Nº25 ** Inicio //
		// FECHA VALIDEZ
		if (property.equals("fechavalidez")) {
			criteria.addOrder(isAsc ? Order.asc("fechavalidez") : Order.desc("fechavalidez"));
		}
		// P0079014 ** MODIF TAM (05/05/2022) ** Defecto Nº25 ** Inicio //
		// FECHA VALIDEZ
		if (property.equals("fecha")) {
			criteria.addOrder(isAsc ? Order.asc("fecha") : Order.desc("fecha"));
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
