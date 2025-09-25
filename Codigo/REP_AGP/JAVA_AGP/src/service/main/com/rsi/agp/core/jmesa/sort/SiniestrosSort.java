package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.service.impl.utilidades.SiniestrosUtilidadesService;

public class SiniestrosSort implements CriteriaCommand {

	List<Sort> sorts = new ArrayList<Sort>();

	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
			buildCriteria(criteria, sort.getProperty(), sort.getOrder());
		}
		return criteria;
	}

	private void buildCriteria(Criteria criteria, String property, String order) {

		// Variable que indica si la ordenacion es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);

		// Anhade el orden al Criteria dependiendo del campo y del sentido de la
		// ordenacion
		// Entidad
		if (SiniestrosUtilidadesService.CAMPO_ENTIDAD.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_ENTIDAD)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_ENTIDAD));
		}
		// Oficina
		else if (SiniestrosUtilidadesService.CAMPO_OFICINA.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_OFICINA)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_OFICINA));
		}
		// Plan
		else if (SiniestrosUtilidadesService.CAMPO_PLAN.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_PLAN)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_PLAN));
		}
		// Linea
		else if (SiniestrosUtilidadesService.CAMPO_LINEA.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_LINEA)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_LINEA));
		}
		// Poliza
		else if (SiniestrosUtilidadesService.CAMPO_POLIZA.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_POLIZA)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_POLIZA));
		}
		// NIF/CIF
		else if (SiniestrosUtilidadesService.CAMPO_NIF.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_NIF)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_NIF));
		}
		// Asegurado
		else if (SiniestrosUtilidadesService.CAMPO_NOMBRE.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_NOMBRE)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_NOMBRE));
		}
		// Fecha de envio de poliza
		else if (SiniestrosUtilidadesService.CAMPO_FEC_ENVIO_POLIZA.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_FEC_ENVIO_POLIZA)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_FEC_ENVIO_POLIZA));
		}
		// Orden
		else if (SiniestrosUtilidadesService.CAMPO_ORDEN.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_ORDEN)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_ORDEN));
		}
		// Riesgo siniestro
		else if (SiniestrosUtilidadesService.CAMPO_CODRIESGO.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_CODRIESGO)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_CODRIESGO));
		}
		// Fecha de ocurrencia
		else if (SiniestrosUtilidadesService.CAMPO_FEC_OCURRENCIA.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_FEC_OCURRENCIA)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_FEC_OCURRENCIA));
		}
		// Fecha de firma
		else if (SiniestrosUtilidadesService.CAMPO_FEC_FIRMA.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_FEC_FIRMA)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_FEC_FIRMA));
		}
		// Estado
		else if (SiniestrosUtilidadesService.CAMPO_ESTADO.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_ESTADO)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_ESTADO));
		}
		// Fecha de envio
		else if (SiniestrosUtilidadesService.CAMPO_FEC_ENVIO.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(SiniestrosUtilidadesService.CAMPO_FEC_ENVIO)
					: Order.desc(SiniestrosUtilidadesService.CAMPO_FEC_ENVIO));
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
