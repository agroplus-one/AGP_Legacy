package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;

public class EntidadAccesoRestringidoFilter implements CriteriaCommand {
	
	private List<Filter> filters = new ArrayList<Filter >();
	private final Log  logger = LogFactory.getLog(getClass());

	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
	}
	
	public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }
	
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	       	       	
			try {
        		// Codentidad
        		if (EntidadAccesoRestringido.CAMPO_CODENTIDAD.equals(property)){
        			criteria.add(Restrictions.eq(EntidadAccesoRestringido.CAMPO_CODENTIDAD, new BigDecimal (value.toString())));
        		}
        		// Acceso al diseñador
        		else if (EntidadAccesoRestringido.CAMPO_ACCESO_DISENADOR.equals(property)){
        			criteria.add(Restrictions.eq("accesoDisenador", new BigDecimal (value.toString())));
        		}
        		// Acceso al generador
        		else if (EntidadAccesoRestringido.CAMPO_ACCESO_GENERADOR.equals(property)){
        			criteria.add(Restrictions.eq("accesoGenerador", new BigDecimal (value.toString())));
        		}
			}
			catch (Exception e) {
				logger.error("EntidadAccesoRestringidoFilter - Ocurrio un error al generar el filtro", e);
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
