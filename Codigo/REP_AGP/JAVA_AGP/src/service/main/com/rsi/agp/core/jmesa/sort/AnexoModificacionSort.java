package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.service.impl.utilidades.AnexoModificacionUtilidadesService;


public class AnexoModificacionSort implements CriteriaCommand {
	
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
		
		// Añade el orden al Criteria dependiendo del campo y del sentido de la ordenacion
		// Entidad
		if (AnexoModificacionUtilidadesService.CAMPO_ENTIDAD.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("tom.id.codentidad") : Order.desc("tom.id.codentidad"));
		}
		// Oficina
		else if (AnexoModificacionUtilidadesService.CAMPO_OFICINA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("pol.oficina") : Order.desc("pol.oficina"));
		}
		// Plan
		else if (AnexoModificacionUtilidadesService.CAMPO_PLAN.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("lin.codplan") : Order.desc("lin.codplan"));
		}
		// Linea
		else if (AnexoModificacionUtilidadesService.CAMPO_LINEA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("lin.codlinea") : Order.desc("lin.codlinea"));
		}
		// Poliza
		else if (AnexoModificacionUtilidadesService.CAMPO_POLIZA.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("pol.referencia") : Order.desc("pol.referencia"));
		}
		// Tipo Referencia
		else if (AnexoModificacionUtilidadesService.CAMPO_TIPOREF.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("pol.tipoReferencia") : Order.desc("pol.tipoReferencia"));
		}
		// NIF/CIF
		else if (AnexoModificacionUtilidadesService.CAMPO_NIF.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("aseg.nifcif") : Order.desc("aseg.nifcif"));
		}
		// Asegurado
		else if (AnexoModificacionUtilidadesService.CAMPO_FULLNAME.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("aseg.nombre") : Order.desc("aseg.nombre"));
		} 
		// Fecha de envio del anexo
		else if (AnexoModificacionUtilidadesService.CAMPO_FEC_ENVIO_ANEXO.equals(property)){
			criteria.addOrder(isAsc ? Order.asc("fechaEnvioAnexo") : Order.desc("fechaEnvioAnexo"));
		}
		// Asunto
		else if (AnexoModificacionUtilidadesService.CAMPO_ASUNTO.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(AnexoModificacionUtilidadesService.CAMPO_ASUNTO) : Order.desc(AnexoModificacionUtilidadesService.CAMPO_ASUNTO));
		}		
		// Estado
		else if (AnexoModificacionUtilidadesService.CAMPO_ESTADO.equals(property)){
			criteria.addOrder(isAsc ? Order.asc(AnexoModificacionUtilidadesService.CAMPO_ESTADO) : Order.desc(AnexoModificacionUtilidadesService.CAMPO_ESTADO));
		}
		// Estado Agroseguro
		else if (AnexoModificacionUtilidadesService.CAMPO_ESTADO_AGROSEGURO.equals(property)) {
			criteria.addOrder(isAsc ? Order.asc(AnexoModificacionUtilidadesService.CAMPO_ESTADO_AGROSEGURO) : Order.desc(AnexoModificacionUtilidadesService.CAMPO_ESTADO_AGROSEGURO));
		}
	
    }
	
	public void addSort(String property, String order) {
		sorts.add(new Sort(property, order));
		
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
