package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class CPMTipoCapitalSort implements CriteriaCommand{

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
		
		// Anade el orden al Criteria dependiendo del campo y del sentido de la ordenacion
		// Plan
		if ("cultivo.linea.codplan".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("lin.codplan") : Order.desc("lin.codplan"));
		}
		// Linea
		else if ("cultivo.linea.codlinea".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("lin.codlinea") : Order.desc("lin.codlinea"));
		}
		// Modulo
		else if ("modulo".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("modulo") : Order.desc("modulo"));
		}
		// CPM
		else if ("conceptoPpalModulo.codconceptoppalmod".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("conceptoPpalModulo.codconceptoppalmod") : Order.desc("conceptoPpalModulo.codconceptoppalmod"));
		}
		// Tipo capital
		else if ("tipoCapital.codtipocapital".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("tipoCapital.codtipocapital") : Order.desc("tipoCapital.codtipocapital"));
		}
		// Cultivo
		else if ("cultivo.id.codcultivo".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("cultivo.id.codcultivo") : Order.desc("cultivo.id.codcultivo"));
		}
		// Sistema de cultivo
		else if ("sistemaCultivo.codsistemacultivo".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("sistemaCultivo.codsistemacultivo") : Order.desc("sistemaCultivo.codsistemacultivo"));
		}
		// Fecha fin de garant�as
		else if ("fechafingarantia".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("fechafingarantia") : Order.desc("fechafingarantia"));
		}
		// Sistema de cultivo
		else if ("cicloCultivo.codciclocultivo".equals(property)){
			criteria.addOrder(isAsc ? Order.asc("cicloCultivo.codciclocultivo") : Order.desc("cicloCultivo.codciclocultivo"));
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
