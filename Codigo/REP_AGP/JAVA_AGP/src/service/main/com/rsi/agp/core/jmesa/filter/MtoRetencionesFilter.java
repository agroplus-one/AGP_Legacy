package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class MtoRetencionesFilter  implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log     logger = LogFactory.getLog(getClass());	
		
	
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
	
	public String getSqlWhere() {

		String sqlWhere= "WHERE 1=1";
		
		   try {
			for (Filter filter : filters) {
				String property = filter.getProperty(); 
				// ANYO
			    if (property.equals("anyo")){
			    	sqlWhere += " AND ANYO = '"+ filter.getValue()+"'";
					
				} 
				// RETENCION
				else if (property.equals("retencion")){
					sqlWhere += " AND RETENCION = '"+ filter.getValue()+"'";
				} 
			    
			 
			}
		}catch (Exception e) {
			logger.error("MtoRetencionessFilter - Error al recuperar la lista de todos los ids -"+e.getMessage());
		}  
		return sqlWhere;
	}

	
	/**
	 * Carga el objeto Criteria con las condiciones de busqueda referentes al filtro indicado por 'property,valor'
	 * U029769
	 * @param criteria
	 * @param property
	 * @param value
	 */
	private void buildCriteria(Criteria criteria, String property, Object value) {
		
		if (value != null) {
			try {			    
				
				// codentidad
			    if (property.equals("anyo") && value != null){
					criteria.add(Restrictions.eq("anyo", new Integer(value.toString())));
				} 
				// retencion
				else if (property.equals("retencion") && value != null){
					criteria.add(Restrictions.eq("retencion",  new BigDecimal(value.toString())));
				}         		        		
			}catch (Exception e) {
				logger.error("MtoRetencionesFilter - "+ e.getMessage());
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
