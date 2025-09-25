package com.rsi.agp.core.jmesa.sort;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class SobreprecioSbpSort implements CriteriaCommand{

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
    		// Plan
    		if (property.equals("linea.codplan")){
    			criteria.addOrder(Order.asc("lin.codplan"));
    		}
    		// Linea
    		else if (property.equals("linea.codlinea")){
    			criteria.addOrder(Order.asc("lin.codlinea"));
    		}
    		// Cod.provincia
    		else if (property.equals("provincia.codprovincia")){
    			criteria.addOrder(Order.asc("prov.codprovincia"));
    		}
    		// Nom. Provincia
    		else if (property.equals("provincia.nomprovincia")){
    			criteria.addOrder(Order.asc("prov.nomprovincia"));
    		}
    		// Cod.cultivo
    		else if (property.equals("cultivo.id.codcultivo")){
    			criteria.addOrder(Order.asc("cultivo.id.codcultivo"));
    		}
    		// Nom. cultivo
    		else if (property.equals("cultivo.descultivo")){
    			criteria.addOrder(Order.asc("cultivo.descultivo"));
    		}
    		// Cod. tipo capital
    		else if (property.equals("tipoCapital.codtipocapital")){
    			criteria.addOrder(Order.asc("tipoCapital.codtipocapital"));
    		}
    		// Desc tipo capital
    		else if (property.equals("tipoCapital.destipocapital")){
    			criteria.addOrder(Order.asc("tipoCapital.destipocapital"));
    		}
    		// precio Minimo
    		else if (property.equals("precioMinimo")){
    			criteria.addOrder(Order.asc("precioMinimo"));
    		}
    		// precio Maximo
    		else if (property.equals("precioMaximo")){
    			criteria.addOrder(Order.asc("precioMaximo"));
    		}
        } else if (order.equals(Sort.DESC)) {// ***** DESC *****
    		// Plan
    		if (property.equals("linea.codplan")){
    			criteria.addOrder(Order.desc("lin.codplan"));
    		}
    		// Linea
    		else if (property.equals("linea.codlinea")){
    			criteria.addOrder(Order.desc("lin.codlinea"));
    		}
    		// Cod.provincia
    		else if (property.equals("provincia.codprovincia")){
    			criteria.addOrder(Order.desc("prov.codprovincia"));
    		}
    		// Nom. Provincia
    		else if (property.equals("provincia.nomprovincia")){
    			criteria.addOrder(Order.desc("prov.nomprovincia"));
    		}
    		// Cod.cultivo
    		else if (property.equals("cultivo.id.codcultivo")){
    			criteria.addOrder(Order.desc("cultivo.id.codcultivo"));
    		}
    		// Nom. cultivo
    		else if (property.equals("cultivo.descultivo")){
    			criteria.addOrder(Order.desc("cultivo.descultivo"));
    		}
    		// Cod. tipo capital
    		else if (property.equals("tipoCapital.codtipocapital")){
    			criteria.addOrder(Order.desc("tipoCapital.codtipocapital"));
    		}
    		// Desc tipo capital
    		else if (property.equals("tipoCapital.destipocapital")){
    			criteria.addOrder(Order.desc("tipoCapital.destipocapital"));
    		}
    		// precio Minimo
    		else if (property.equals("precioMinimo")){
    			criteria.addOrder(Order.desc("precioMinimo"));
    		}
    		// precio Maximo
    		else if (property.equals("precioMaximo")){
    			criteria.addOrder(Order.desc("precioMaximo"));
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
