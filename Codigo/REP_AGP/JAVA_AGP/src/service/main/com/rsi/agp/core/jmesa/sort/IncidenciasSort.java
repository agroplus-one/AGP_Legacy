package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class IncidenciasSort implements CriteriaCommand {

	// Constantes
	private static final String ID_FECHA = "id.fecha";
	private static final String ESTADO = "estado";
	private static final String IDCUPON = "idcupon";
	private static final String ID_NUMERO = "id.numero";
	private static final String ID_ANHO = "id.anho";
	private static final String ID_NIFCIF = "id.nifcif";
	private static final String ID_TIPOREF = "id.tiporef";
	private static final String REFERENCIA = "referencia";
	private static final String ID_CODLINEA = "id.codlinea";
	private static final String ID_CODPLAN = "id.codplan";
	private static final String SUBENTMEDIADORA = "subentmediadora";
	private static final String ENTMEDIADORA = "entmediadora";
	private static final String OFICINA = "oficina";
	private static final String CODENTIDAD = "codentidad";
	
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
		
		// Modif TAM (13.06.2018) ** Insertamos el sort de los nuevos campos
		
		// Entidad
		if (property.equals(CODENTIDAD)) {
			criteria.addOrder(isAsc ? Order.asc(CODENTIDAD)
					: Order.desc(CODENTIDAD));
		/// Oficina
		}else if (property.equals(OFICINA)) {
			criteria.addOrder(isAsc ? Order.asc(OFICINA)
					: Order.desc(OFICINA));
		// E-S Mediadora
		}else if (property.equals(ENTMEDIADORA)) {
			criteria.addOrder(isAsc ? Order.asc(ENTMEDIADORA)
					: Order.desc(ENTMEDIADORA));
			criteria.addOrder(isAsc ? Order.asc(SUBENTMEDIADORA)
					: Order.desc(SUBENTMEDIADORA));
		// Plan
		}else if (property.equals(ID_CODPLAN)) {
			criteria.addOrder(isAsc ? Order.asc(ID_CODPLAN)
					: Order.desc(ID_CODPLAN));
		// Línea
		}else if (property.equals(ID_CODLINEA)) {
			criteria.addOrder(isAsc ? Order.asc(ID_CODLINEA)
					: Order.desc(ID_CODLINEA));
		// Póliza (referencia)
		}else if (property.equals(REFERENCIA)) {
			criteria.addOrder(isAsc ? Order.asc(REFERENCIA)
					: Order.desc(REFERENCIA));
		// Tipo Referencia			
		}else if (property.equals(ID_TIPOREF)) {
			criteria.addOrder(isAsc ? Order.asc(ID_TIPOREF)
					: Order.desc(ID_TIPOREF));
		// NIF/CIF
		}else if (property.equals(ID_NIFCIF)) {
			criteria.addOrder(isAsc ? Order.asc(ID_NIFCIF)
					: Order.desc(ID_NIFCIF));
		// Año			
		}else if (property.equals(ID_ANHO)) {
			criteria.addOrder(isAsc ? Order.asc(ID_ANHO)
					: Order.desc(ID_ANHO));
		// Nº de Incidencia			
		}else if (property.equals(ID_NUMERO)) {
			criteria.addOrder(isAsc ? Order.asc(ID_NUMERO)
					: Order.desc(ID_NUMERO));
		// Cupón
		}else if (property.equals(IDCUPON)) {
			criteria.addOrder(isAsc ? Order.asc(IDCUPON)
					: Order.desc(IDCUPON));
		// Estado Agroseguro
		}else if (property.equals(ESTADO)) {
			criteria.addOrder(isAsc ? Order.asc(ESTADO)
					: Order.desc(ESTADO));
		// Fecha Agroseguro
		}else if (property.equals(ID_FECHA)) {
			criteria.addOrder(isAsc ? Order.asc(ID_FECHA)
					: Order.desc(ID_FECHA));
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