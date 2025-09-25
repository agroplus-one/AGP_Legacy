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


public class ClaseMtoFilter implements CriteriaCommand{

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());	
	private boolean excluirId = false;
	
	public ClaseMtoFilter () {}
	
	public ClaseMtoFilter (boolean exId) {
		excluirId = exId;
	}
	
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
				Class c = Class.forName("com.rsi.agp.dao.tables.admin.Clase");
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
	        	
        		// Plan
        		if (property.equals("linea.codplan") ){
        			criteria.add(Restrictions.eq("lin.codplan", new BigDecimal(value.toString())));
        		}
        		// Linea
        		else if (property.equals("linea.codlinea")){
        			criteria.add(Restrictions.eq("lin.codlinea", new BigDecimal(value.toString())));
        		}
        		// Clase
        		else if (property.equals("clase") && value != null){
        			criteria.add(Restrictions.eq("clase", new BigDecimal(value.toString())));
        		}
        		// Descripcion
        		else if (property.equals("descripcion") ){
        			//criteria.add(Restrictions.ilike("descripcion", "%".concat(value.toString()).concat("%")));
        		}
        		// Maxpolizas
        		else if (property.equals("maxpolizas") && value != null){
        			criteria.add(Restrictions.eq("maxpolizas", new BigDecimal(value.toString())));
        		}
        		// Comprobar Aac
        		else if (property.equals("comprobarAac")){
        			criteria.add(Restrictions.eq("comprobarAac", value.toString()));
        		}
        		// Comprobar Rce (Pet. 63428) 
        		else if (property.equals("comprobarRce")){
        			
        			criteria.add(Restrictions.eq("comprobarRce", value.toString())); 
        		}
        		// Id
        		else if (property.equals("id")) {
        			criteria.add(excluirId ?  (Restrictions.ne("id", new Long (value.toString()))) :
        									  (Restrictions.eq("id", new Long (value.toString()))));	
        		}else if(property.equals("rdtoHistorico")){
        			 criteria.add(Restrictions.eq("rdtoHistorico", new Long (value.toString())));
        			
        		}
        		else if (tipo.equals(Long.class)){
	        		criteria.add(Restrictions.eq(property, new LongType().fromStringValue(value+"")));
	        	}
        		else if (tipo.equals(Boolean.class)){
	        		criteria.add(Restrictions.eq(property, new BooleanType().fromStringValue(value+"")));
	        	}
	        		
			}catch (Exception e) {
				logger.error("ClaseMtoFilter - "+e.getMessage());
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
