package com.rsi.agp.core.jmesa.filter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class CargasFicherosFilter implements CriteriaCommand{
	
	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());
	
	
	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
	}
	 
	
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	       	       	
			try {
				//Fichero
	        	if (property.equals("fichero")){
	        		criteria.add(Restrictions.eq("fichero", new BigDecimal(value.toString())));
	        	}else if (property.equals("tipo")){
	        		criteria.add(Restrictions.eq("tipo", new BigDecimal(value.toString())));
	        	}else if (property.equals("plan")){
	        		criteria.add(Restrictions.eq("plan", new BigDecimal(value.toString())));
	        	}else if (property.equals("linea")){
	        		criteria.add(Restrictions.eq("linea", new BigDecimal(value.toString())));
	        	}
	        }catch (Exception e) {
				logger.error("CargasFicherosFilter - "+e.getMessage());
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
