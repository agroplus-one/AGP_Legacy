package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class ListadoPolizaSbpSort implements CriteriaCommand {
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
		if (order.equals(Sort.ASC)) {

			if (property.equals("polizaPpal.colectivo.tomador.id.codentidad")) {
				criteria.addOrder(Order.asc("tom.id.codentidad"));
			} else if (property.equals("polizaPpal.oficina")) {
				criteria.addOrder(Order.asc("polPpal.oficina"));
			} else if (property.equals("usuarioProvisional")) {
				criteria.addOrder(Order.asc("usuarioProvisional"));
			} else if (property.equals("polizaPpal.linea.codplan")) {
				criteria.addOrder(Order.asc("lin.codplan"));
			} else if (property.equals("polizaPpal.linea.codlinea")) {
				criteria.addOrder(Order.asc("lin.codlinea"));
			} else if (property.equals("polizaPpal.clase")) {
				criteria.addOrder(Order.asc("polPpal.clase"));
			} else if (property.equals("polizaPpal.colectivo.idcolectivo")) {
				criteria.addOrder(Order.asc("col.idcolectivo"));
			} else if (property.equals("polizaPpal.colectivo.dccolectivo")) {
				criteria.addOrder(Order.asc("col.dccolectivo"));
			} else if (property.equals("referencia")) {
				criteria.addOrder(Order.asc("referencia"));
			} else if (property.equals("estadoPlzSbp.idestado")) {
				criteria.addOrder(Order.asc("estadoSbp.idestado"));
			} else if (property.equals("polizaPpal.codmodulo")) {
				criteria.addOrder(Order.asc("polPpal.codmodulo"));
			} else if (property.equals("polizaPpal.estadoPoliza.idestado")) {

			} else if (property.equals("polizaCpl.estadoPoliza.idestado")) {

			} else if (property.equals("incSbpComp")) {
				criteria.addOrder(Order.asc("incSbpComp"));
			} else if (property.equals("polizaPpal.asegurado.nifcif")) {
				criteria.addOrder(Order.asc("aseg.nifcif"));
			} else if (property.equals("detalle")) {
			} else if (property.equals("importe")) {
				criteria.addOrder(Order.asc("importe"));
			} else if (property.equals("refPlzOmega")) {
				criteria.addOrder(Order.asc("refPlzOmega"));
			} else if (property.equals("nSolicitud")) {
				criteria.addOrder(Order.asc("id"));
				/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Inicio */
			} else if (property.equals("polizaPpal.colectivo.subentidadMediadora.id.codentidad")) {
				criteria.addOrder(Order.asc("esMed.id.codentidad"));
			} else if (property.equals("polizaPpal.colectivo.subentidadMediadora.id.codsubentidad")) {
				criteria.addOrder(Order.asc("esMed.id.codsubentidad"));
				/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Fin */
			}else {
				criteria.addOrder(Order.asc(property));
			}
		} else if (order.equals(Sort.DESC)) {
			if (property.equals("polizaPpal.colectivo.tomador.id.codentidad")) {
				criteria.addOrder(Order.desc("tom.id.codentidad"));
			} else if (property.equals("polizaPpal.oficina")) {
				criteria.addOrder(Order.desc("polPpal.oficina"));
			} else if (property.equals("usuarioProvisional")) {
				criteria.addOrder(Order.desc("usuarioProvisional"));
			} else if (property.equals("polizaPpal.linea.codplan")) {
				criteria.addOrder(Order.desc("lin.codplan"));
			} else if (property.equals("polizaPpal.linea.codlinea")) {
				criteria.addOrder(Order.desc("lin.codlinea"));
			} else if (property.equals("polizaPpal.clase")) {
				criteria.addOrder(Order.desc("polPpal.clase"));
			} else if (property.equals("polizaPpal.colectivo.idcolectivo")) {
				criteria.addOrder(Order.desc("col.idcolectivo"));
			} else if (property.equals("polizaPpal.colectivo.dccolectivo")) {
				criteria.addOrder(Order.desc("col.dccolectivo"));
			} else if (property.equals("referencia")) {
				criteria.addOrder(Order.desc("referencia"));
			} else if (property.equals("estadoPlzSbp.idestado")) {
				criteria.addOrder(Order.desc("estadoSbp.idestado"));
			} else if (property.equals("polizaPpal.codmodulo")) {
				criteria.addOrder(Order.desc("polPpal.codmodulo"));
			} else if (property.equals("polizaPpal.estadoPoliza.idestado")) {

			} else if (property.equals("polizaCpl.estadoPoliza.idestado")) {
				criteria.addOrder(Order.desc("estadoCpl.idestado"));
			} else if (property.equals("incSbpComp")) {
				criteria.addOrder(Order.desc("incSbpComp"));
			} else if (property.equals("polizaPpal.asegurado.nifcif")) {
				criteria.addOrder(Order.desc("aseg.nifcif"));
			} else if (property.equals("detalle")) {
			} else if (property.equals("importe")) {
				criteria.addOrder(Order.desc("importe"));
			} else if (property.equals("refPlzOmega")) {
				criteria.addOrder(Order.desc("refPlzOmega"));
			} else if (property.equals("nSolicitud")) {
				criteria.addOrder(Order.desc("id"));
				/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Inicio */
			} else if (property.equals("polizaPpal.colectivo.subentidadMediadora.id.codentidad")) {
				criteria.addOrder(Order.desc("esMed.id.codentidad"));
			} else if (property.equals("polizaPpal.colectivo.subentidadMediadora.id.codsubentidad")) {
				criteria.addOrder(Order.desc("esMed.id.codsubentidad"));
				/* Pet. 79014 ** MODIF TAM (17.03.2022) ** Fin */
			} else {
				criteria.addOrder(Order.desc(property));
			}
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
