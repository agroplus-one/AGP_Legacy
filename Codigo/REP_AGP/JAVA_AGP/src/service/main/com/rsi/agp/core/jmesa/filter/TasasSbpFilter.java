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
import com.rsi.agp.dao.tables.sbp.TasasSbp;


public class TasasSbpFilter implements CriteriaCommand {

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
        		// Plan
        		if (TasasSbp.CAMPO_PLAN.equals(property)) {
        			criteria.add(Restrictions.eq(TasasSbp.CAMPO_PLAN, new BigDecimal(value.toString())));
        		}
        		// Linea
        		else if (TasasSbp.CAMPO_LINEA.equals(property)) {
        			criteria.add(Restrictions.eq(TasasSbp.CAMPO_LINEA, new BigDecimal(value.toString())));
        		}
        		// Provincia
        		else if (TasasSbp.CAMPO_CODPROVINCIA.equals(property)) {
        			criteria.add(Restrictions.eq(TasasSbp.CAMPO_CODPROVINCIA, new BigDecimal(value.toString())));
        		}
        		//Comarca
        		else if (TasasSbp.CAMPO_CODCOMARCA.equals(property)) {
        			criteria.add(Restrictions.eq(TasasSbp.CAMPO_CODCOMARCA, new BigDecimal(value.toString())));
        		}
        		//Cultivo
        		else if (TasasSbp.CAMPO_CODCULTIVO.equals(property)) {
        			criteria.add(Restrictions.eq(TasasSbp.CAMPO_CODCULTIVO, new BigDecimal(value.toString())));
        		}
        		// Tasa de incendio
        		else if (TasasSbp.CAMPO_TASA_INCENDIO.equals(property)) {
        			criteria.add(Restrictions.eq(TasasSbp.CAMPO_TASA_INCENDIO, new BigDecimal(value.toString())));
        		}
        		// Tasa de pedrisco
        		else if (TasasSbp.CAMPO_TASA_PEDRISCO.equals(property)) {
        			criteria.add(Restrictions.eq(TasasSbp.CAMPO_TASA_PEDRISCO, new BigDecimal(value.toString())));
        		}        		        		        		        		        		    			        			        			        			        		        
			}
			
			catch (Exception e) {
				logger.error("TasasSbpFilter - "+e.getMessage());
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
