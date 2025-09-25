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



public class MtoImpuestoSbpFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());	
		
	
	public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }

	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
	}
	
	/**
	 * Carga el objeto Criteria con las condiciones de búsqueda referentes al filtro indicado por 'property,valor'
	 * @param criteria
	 * @param property
	 * @param value
	 */
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	
			try {			    
        		// codplan
				if (property.equals("codplan") && value != null){
        			criteria.add(Restrictions.eq("codplan", new BigDecimal(value.toString())));
        		}
				// codimpuesto
        		else if (property.equals("impuestoSbp.codigo") && value != null){
        			criteria.add(Restrictions.eq("impuestoSbp.codigo", value.toString()));
        		}
				// nomimpuesto
        		else if (property.equals("impuestoSbp.descripcion") && value != null){
        			criteria.add(Restrictions.eq("impuestoSbp.descripcion", value.toString()));
        		} 
				// nombase
        		else if (property.equals("baseSbp.base") && value != null){
        			criteria.add(Restrictions.eq("baseSbp.base", value.toString()));
        		} 
				// valor
        		else if (property.equals("valor") && value != null){
        			criteria.add(Restrictions.eq("valor", new BigDecimal(value.toString())));
        		} 
			}
			
			catch (Exception e) {
				logger.error("MtoImpuestoSbpFilter - "+ e.getMessage());
			}
        }
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
