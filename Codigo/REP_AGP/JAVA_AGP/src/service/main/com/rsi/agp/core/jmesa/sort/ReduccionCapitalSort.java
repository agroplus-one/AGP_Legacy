package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.service.impl.utilidades.ReduccionCapitalUtilidadesService;

public class ReduccionCapitalSort implements CriteriaCommand {
	
	List<Sort> sorts = new ArrayList<Sort>();
	
	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
            buildCriteria(criteria, sort.getProperty(), sort.getOrder());
        }
		return criteria;
	}
	
	
	private void buildCriteria(Criteria criteria, String property, String order) {
		
		// Variable que indica si la ordenación es ascendente o descendente
		boolean isAsc = Sort.ASC.equals(order);
		
		// Añade el orden al Criteria dependiendo del campo y del sentido de la ordenación
		// Entidad
		if (ReduccionCapitalUtilidadesService.CAMPO_ENTIDAD.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_ENTIDAD) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_ENTIDAD));
		}
		// Oficina
		else if (ReduccionCapitalUtilidadesService.CAMPO_OFICINA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_OFICINA) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_OFICINA));
		}
		// Plan
		else if (ReduccionCapitalUtilidadesService.CAMPO_PLAN.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_PLAN) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_PLAN));
		}
		// Linea
		else if (ReduccionCapitalUtilidadesService.CAMPO_LINEA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_LINEA) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_LINEA));
		}
		// Poliza
		else if (ReduccionCapitalUtilidadesService.CAMPO_POLIZA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_POLIZA) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_POLIZA));
		}
		// NIF/CIF
		else if (ReduccionCapitalUtilidadesService.CAMPO_NIF.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_NIF) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_NIF));
		}
		// Asegurado
		else if (ReduccionCapitalUtilidadesService.CAMPO_NOMBRE.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_NOMBRE) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_NOMBRE));
		} 
		// Fecha de envío de póliza
		else if (ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO_POLIZA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO_POLIZA) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO_POLIZA));
		}
		// Orden
		else if (ReduccionCapitalUtilidadesService.CAMPO_ORDEN.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_ORDEN) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_ORDEN));
		}
		// Riesgo siniestro
		else if (ReduccionCapitalUtilidadesService.CAMPO_CODRIESGO.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_CODRIESGO) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_CODRIESGO));
		}		
		// Fecha de ocurrencia
		else if (ReduccionCapitalUtilidadesService.CAMPO_FEC_DANIOS.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_FEC_DANIOS) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_FEC_DANIOS));
		}
		// Estado
		else if (ReduccionCapitalUtilidadesService.CAMPO_ESTADO.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_ESTADO) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_ESTADO));
		}
		// Fecha de envío
		else if (ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO) : Order.desc(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO));
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
