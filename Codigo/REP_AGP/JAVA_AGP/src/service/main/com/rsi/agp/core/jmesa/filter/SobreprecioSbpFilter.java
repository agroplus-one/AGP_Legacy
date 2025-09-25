package com.rsi.agp.core.jmesa.filter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class SobreprecioSbpFilter implements CriteriaCommand{

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());	
	
	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
	}
	
	@SuppressWarnings("rawtypes")
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	       	       	
        	Class tipo;
        	
        	try {
				Class c = Class.forName("com.rsi.agp.dao.tables.sbp.Sobreprecio");
				Field field = c.getDeclaredField(property);
				tipo = field.getType();
			} catch (ClassNotFoundException e) {
				logger.debug ("No se encontró la clase");
				//Por defecto, pongo la clase String
				tipo = String.class;
			} catch (SecurityException e) {
				//Por defecto, pongo la clase String
				tipo = String.class;
			} catch (NoSuchFieldException e) {
				//El campo no existe. Se trata del filtro por actor (la propiedad está dentro de otro objeto)
				//En este caso, la clase será Long
				//tipo = Long.class;
				tipo = String.class;
			}
			try {
				if (tipo.equals(Boolean.class)){
	        		criteria.add(Restrictions.eq(property, new BooleanType().fromStringValue(value+"")));
	        	}
	        	else if (tipo.equals(Long.class)){
	        		criteria.add(Restrictions.eq(property, new LongType().fromStringValue(value+"")));
	        	}
	        	else{
	        		// Plan
	        		if (property.equals("linea.codplan")){
	        			criteria.add(Restrictions.eq("lin.codplan", new BigDecimal(value.toString())));
	        		}
	        		// Linea
	        		else if (property.equals("linea.codlinea")){
	        			criteria.add(Restrictions.eq("lin.codlinea", new BigDecimal(value.toString())));
	        		}
	        		// cod provincia
	        		else if (property.equals("provincia.codprovincia") && value != null){
	        			criteria.add(Restrictions.eq("prov.codprovincia", new BigDecimal(value.toString())));
	        		}
	        		// nom provincia
	        		else if (property.equals("provincia.nomprovincia") && value != null){
	        			criteria.add(Restrictions.eq("prov.nomprovincia", value.toString()));
	        		}
	        		// cod cultivo
	        		else if (property.equals("cultivo.id.codcultivo")){
	        			criteria.add(Restrictions.eq("cultivo.id.codcultivo", new BigDecimal(value.toString())));
	        		}
	        		// nom cultivo
	        		else if (property.equals("cultivo.descultivo")){
	        			criteria.add(Restrictions.eq("cultivo.descultivo",value.toString()));
	        		}
	        		// cod tipo capital
	        		else if (property.equals("tipoCapital.codtipocapital")){
	        			criteria.add(Restrictions.eq("tipoCapital.codtipocapital",new BigDecimal(value.toString())));
	        		}
	        		// desc tipo capital
	        		else if (property.equals("tipoCapital.destipocapital")){
	        			criteria.add(Restrictions.eq("tipoCapital.destipocapital",value.toString()));
	        		}
	        		//precio Minimo
	        		else if (property.equals("precioMinimo")){
	        			criteria.add(Restrictions.eq("precioMinimo", new BigDecimal(value.toString())));
	        		}
	        		//precio Maximo
	        		else if (property.equals("precioMaximo")){
	        			criteria.add(Restrictions.eq("precioMaximo", new BigDecimal(value.toString())));
	        		}
	        		
        	}
			}catch (Exception e) {
				logger.error("ConsultaSobreprecioFilter - "+e.getMessage());
			}     	
        }
    }
	
	
	public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }
	
	
	private static class Filter {
        private final String property;
        private final Object value;

        public Filter(String property, Object value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public Object getValue() {
            return value;
        }
    }
}
