package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.util.ConstantsRC;

public class VistaPolizasRCSort implements CriteriaCommand {

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
		
		if(property.equals(ConstantsRC.ENTIDAD_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.ENTIDAD_VAL, isAsc));
		} else if(property.equals(ConstantsRC.OFICINA_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.OFICINA_VAL, isAsc));
		} else if(property.equals(ConstantsRC.USUARIO_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.USUARIO_VAL, isAsc));
		} else if(property.equals(ConstantsRC.PLAN_POLIZA_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.PLAN_POLIZA_VAL, isAsc));
		} else if(property.equals(ConstantsRC.LINEA_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.LINEA_VAL, isAsc));
		} else if(property.equals(ConstantsRC.REF_COL_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.REF_COL_VAL, isAsc));
		} else if(property.equals(ConstantsRC.FEC_ENVIO_RC_VAL)) {
			criteria.addOrder(fijarOrden(ConstantsRC.FEC_ENVIO_RC_VAL, isAsc));
		} else if(property.equals(ConstantsRC.REF_POLIZA_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.REF_POLIZA_VAL, isAsc));
		} else if(property.equals(ConstantsRC.NIFCIF_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.NIFCIF_VAL, isAsc));
		} else if(property.equals(ConstantsRC.CLASE_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.CLASE_VAL, isAsc));
		} else if(property.equals(ConstantsRC.ESTADO_POL_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.ESTADO_POL_VAL, isAsc));
		} else if(property.equals(ConstantsRC.DETALLE_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.DETALLE_VAL, isAsc));
		} else if(property.equals(ConstantsRC.REF_OMEGA_VAL)){
			criteria.addOrder(fijarOrden(ConstantsRC.REF_OMEGA_VAL, isAsc));
		} else if(property.equals(ConstantsRC.N_SOLICITUD_VAL)){
			criteria.addOrder(fijarOrden("idpoliza", isAsc));
		}
	}
	
	private static Order fijarOrden(String property, boolean isAsc){
		return isAsc ? Order.asc(property) : Order.desc(property);
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
