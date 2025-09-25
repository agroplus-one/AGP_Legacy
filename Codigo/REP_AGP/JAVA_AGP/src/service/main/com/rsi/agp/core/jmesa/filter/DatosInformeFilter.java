package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class  DatosInformeFilter implements CriteriaCommand {

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
	        	if (property.equals("idcampo")) criteria.add(Restrictions.eq("idcampo", new BigDecimal(value.toString())));
	        	if (property.equals("abreviado")) criteria.add(Restrictions.eq("abreviado", value.toString()));
	        	if (property.equals("formato")) criteria.add(Restrictions.eq("formato", new BigDecimal(value.toString())));
	        	if (property.equals("decimales")) criteria.add(Restrictions.eq("decimales", new BigDecimal(value.toString())));
	        	if (property.equals("totaliza")) criteria.add(Restrictions.eq("totaliza", new BigDecimal(value.toString())));
	        	if (property.equals("total_por_grupo")) criteria.add(Restrictions.eq("total_por_grupo", new BigDecimal(value.toString())));
	        	if (property.equals("idinforme")) criteria.add(Restrictions.eq("idinforme", new BigDecimal(value.toString())));
        	
			}catch (Exception e) {
				logger.error("ConsultaPolizaSbpFilter - "+e.getMessage());
			}     	
        }
    }
	
	public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }
	
	/**
	 * Devuelve un boolean indicando si se ha insertado algún filtro al objeto o no
	 * @return
	 */
	public boolean tieneAlgunFiltro () {
		return (filters != null && filters.size()>0);
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
