package com.rsi.agp.core.jmesa.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class DeudaAplazadaApliFilter implements CriteriaCommand{
	
	private List<Filter> filters = new ArrayList<Filter>();
	private final Log logger = LogFactory.getLog(getClass());
	
	
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
				if (property.equals("fichero.id")){
	    			criteria.add(Restrictions.eq("fichero.id", new Long(value.toString())));
	    		}
				
	        }catch (Exception e) {
				logger.error("EmitidosApliFilter - "+e.getMessage());
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