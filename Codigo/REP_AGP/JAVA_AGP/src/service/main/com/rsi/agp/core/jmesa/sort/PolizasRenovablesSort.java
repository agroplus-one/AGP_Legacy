package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

/**
 * @author U029769
 *
 */
public class PolizasRenovablesSort implements CriteriaCommand {

List<Sort> sorts = new ArrayList<Sort>();
	
	@Override
	public Criteria execute(Criteria criteria) {
		//addSort("id","desc");
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
	 * @param order Sentido de la ordenacion
	 */
	private void buildCriteria(Criteria criteria, String property, String order) {
		
		// Variable que indica si la ordenacion es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);
				
		// Anade el orden al Criteria dependiendo del campo y del sentido de la ordenacion
		// entidad
		if (property.equals("codentidad")){
			criteria.addOrder(isAsc ? Order.asc("codentidad") : Order.desc("codentidad"));
		}
		// entidad mediadora
		if (property.equals("codentidadmed")){
			criteria.addOrder(isAsc ? Order.asc("codentidadmed") : Order.desc("codentidadmed"));
		}
		// subentidad mediadora
		if (property.equals("codsubentmed")){
			criteria.addOrder(isAsc ? Order.asc("codsubentmed") : Order.desc("codsubentmed"));
		}
		// tomador
		if (property.equals("tomador")){
			criteria.addOrder(isAsc ? Order.asc("nifTomador") : Order.desc("nifTomador"));
		}
		// plan
		else if (property.equals("plan")){
			criteria.addOrder(isAsc ? Order.asc("plan") : Order.desc("plan"));
		}
		// linea
		else if (property.equals("linea")){
			criteria.addOrder(isAsc ? Order.asc("linea") : Order.desc("linea"));
		} 
		// referencia
		else if (property.equals("referencia")){
			criteria.addOrder(isAsc ? Order.asc("referencia") : Order.desc("referencia"));
		}
		// colectivo
		else if (property.equals("refcol")){
			criteria.addOrder(isAsc ? Order.asc("refcol") : Order.desc("refcol"));
		}
		// asegurado
		else if (property.equals("nifAsegurado")){
			criteria.addOrder(isAsc ? Order.asc("nifAsegurado") : Order.desc("nifAsegurado"));
		}
		// estado Agroplus
		else if (property.equals("estagroplus")){
			criteria.addOrder(isAsc ? Order.asc("estagroplus") : Order.desc("estagroplus"));
		}
		// estadps Agroseguro
		else if (property.equals("estagroseguro")){
			criteria.addOrder(isAsc ? Order.asc("estagroseguro") : Order.desc("estagroseguro"));
		}
		// estado envío IBAN
		else if (property.equals("estadoIban")){
			criteria.addOrder(isAsc ? Order.asc("estadoIban") : Order.desc("estadoIban"));
		}
		//fecha Carga
		else if (property.equals("fechaCarga")){
			criteria.addOrder(isAsc ? Order.asc("fechaCarga") : Order.desc("fechaCarga"));
		} 
		//fecha Renovación
		else if (property.equals("fechaRenovacion")){
			criteria.addOrder(isAsc ? Order.asc("fechaRenovacion") : Order.desc("fechaRenovacion"));
		}
		//fecha Carga
		else if (property.equals("fechaEnvioIbanAgro")){
			criteria.addOrder(isAsc ? Order.asc("fechaEnvioIbanAgro") : Order.desc("fechaEnvioIbanAgro"));
		} 
		// comisiones
		else if (property.equals("pctComision")){
			criteria.addOrder(isAsc ? Order.asc("pctComision") : Order.desc("pctComision"));
		}
		// comision Entidad
		else if (property.equals("pctEntidad")){
			criteria.addOrder(isAsc ? Order.asc("pctEntidad") : Order.desc("pctEntidad"));
		}
		// comision E-S Mediadora
		else if (property.equals("pctESMed")){
			criteria.addOrder(isAsc ? Order.asc("pctESMed") : Order.desc("pctESMed"));
		}
		// Coste total tomador
		else if (property.equals("costeTotalTomador")){
			criteria.addOrder(isAsc ? Order.asc("costeTotalTomador") : Order.desc("costeTotalTomador"));
		}
		// % Comisión aplicado
		else if (property.equals("comisionApl")){
			criteria.addOrder(isAsc ? Order.asc("comisionApl") : Order.desc("comisionApl"));
		}
		// % Entidad mediadora aplicado
		else if (property.equals("entidadApl")){
			criteria.addOrder(isAsc ? Order.asc("entidadApl") : Order.desc("entidadApl"));
		}
		// % E-S mediadora aplicado
		else if (property.equals("esMedApl")){
			criteria.addOrder(isAsc ? Order.asc("esMedApl") : Order.desc("esMedApl"));
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
