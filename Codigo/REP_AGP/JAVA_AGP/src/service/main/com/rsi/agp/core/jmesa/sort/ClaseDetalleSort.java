package com.rsi.agp.core.jmesa.sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class ClaseDetalleSort implements CriteriaCommand{

	List<Sort> sorts = new ArrayList<Sort>();
	@Override
	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
            buildCriteria(criteria, sort.getProperty(), sort.getOrder());
        }
		return criteria;
	}
	
	
	private void buildCriteria(Criteria criteria, String property, String order) {
        if (order.equals(Sort.ASC)) {// ***** ASC *****
    		// Modulo
    		if (property.equals("modulo.codmoduloasoc")){
    			criteria.addOrder(Order.asc("modulo.codmoduloasoc"));
    		}
    		// Ciclo Cultivo
    		else if (property.equals("cicloCultivo.codciclocultivo")){
    			criteria.addOrder(Order.asc("ciclo.codciclocultivo"));
    		}
    		// Sistema Cultivo
    		else if (property.equals("sistemaCultivo.codsistemacultivo")){
    			criteria.addOrder(Order.asc("sist.codsistemacultivo"));
    		}
    		// Cultivo
    		else if (property.equals("cultivo.id.codcultivo") ){
    			criteria.addOrder(Order.asc("cultivo.id.codcultivo"));
    		}
    		// Variedad
    		else if (property.equals("variedad.id.codvariedad")){
    			criteria.addOrder(Order.asc("variedad.id.codvariedad"));
    		}
    		// Provincia
    		else if (property.equals("codprovincia")){
    			criteria.addOrder(Order.asc("codprovincia"));
    		}
    		// Comarca
    		else if (property.equals("codcomarca")){
    			criteria.addOrder(Order.asc("codcomarca"));
    		}
    		// Termino
    		else if (property.equals("codtermino")){
    			criteria.addOrder(Order.asc("codtermino"));
    		}
    		// SubTermino
    		else if (property.equals("subtermino")){
    			criteria.addOrder(Order.asc("subtermino"));
    		}
    		// Tipo Capital
    		else if (property.equals("tipoCapital.codtipocapital")){
    			criteria.addOrder(Order.asc("tCap.codtipocapital"));
    		}
    		// Tipo Plantación
    		else if (property.equals("tipoPlantacion.codtipoplantacion")){
    			criteria.addOrder(Order.asc("tPlant.codtipoplantacion"));
    		}
        } else if (order.equals(Sort.DESC)) {// ***** DESC *****
        	if (property.equals("modulo.codmoduloasoc")){
    			criteria.addOrder(Order.desc("modulo.codmoduloasoc"));
    		}
    		// Ciclo Cultivo
    		else if (property.equals("cicloCultivo.codciclocultivo")){
    			criteria.addOrder(Order.desc("ciclo.codciclocultivo"));
    		}
    		// Sistema Cultivo
    		else if (property.equals("sistemaCultivo.codsistemacultivo")){
    			criteria.addOrder(Order.desc("sist.codsistemacultivo"));
    		}
    		// Cultivo
    		else if (property.equals("cultivo.id.codcultivo") ){
    			criteria.addOrder(Order.desc("cultivo.id.codcultivo"));
    		}
    		// Variedad
    		else if (property.equals("variedad.id.codvariedad")){
    			criteria.addOrder(Order.desc("variedad.id.codvariedad"));
    		}
    		// Provincia
    		else if (property.equals("codprovincia")){
    			criteria.addOrder(Order.desc("codprovincia"));
    		}
    		// Comarca
    		else if (property.equals("codcomarca")){
    			criteria.addOrder(Order.desc("codcomarca"));
    		}
    		// Termino
    		else if (property.equals("codtermino")){
    			criteria.addOrder(Order.desc("codtermino"));
    		}
    		// SubTermino
    		else if (property.equals("subtermino")){
    			criteria.addOrder(Order.desc("subtermino"));
    		}
        	// Tipo Capital
    		else if (property.equals("tipoCapital.codtipocapital")){
    			criteria.addOrder(Order.desc("tCap.codtipocapital"));
    		}
    		// Tipo Plantación
    		else if (property.equals("tipoPlantacion.codtipoplantacion")){
    			criteria.addOrder(Order.desc("tPlant.codtipoplantacion"));
    		}
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
