package com.rsi.agp.core.jmesa.filter;

import static com.rsi.agp.core.jmesa.service.impl.mtoinf.MtoOperadorCamposGenericosService.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class OperadorCampoGenericoFilter {
	private List<Filter> filters = new ArrayList<Filter >();
	private final Log  logger = LogFactory.getLog(getClass());	
	
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
	}
	
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	       	       	
			try {
				// Tabla origen
	    		if (property.equals(CAMPO_IDVISTA)){
	    			criteria.add(Restrictions.eq(CAMPO_IDVISTA, new BigDecimal(value.toString())));
	    		}
	    		// Campo
	    		else if (property.equals(CAMPO_CAMPO)){
	    			criteria.add(Restrictions.eq(CAMPO_CAMPO, value.toString()));
	    		}
	            // Operador
	    		else if (property.equals(CAMPO_OPERADOR)){
	    			criteria.add(Restrictions.eq(CAMPO_OPERADOR, new BigDecimal(value.toString())));
	    		}
			}catch (Exception e) {
				logger.error("CamposPermitidosFilter - "+e.getMessage());
			}     	
        }
    }
	
	/**
	 * Devuelve la sql que obtiene el número de operadores que se ajustan al filtro
	 * @return
	 */
	public String getSqlCount(){
		
		StringBuilder sql = new StringBuilder ("");
		sql.append(" select count (*) from vw_mtoinf_operadores op WHERE 1 = 1");
		
		// Recorre los filtros introducidos y crea el where
		for (Filter filter : filters) {	
			if (filter.getValue() != null) {
				// Tabla origen
				if (filter.getProperty().equals(CAMPO_IDVISTA)){
					sql.append (" AND op.idvista = ").append(filter.getValue());
				}
				// Campo
				else if (filter.getProperty().equals(CAMPO_CAMPO)) {
					sql.append (" AND op.nombrecampo = '").append(filter.getValue()).append("'");
				}
				// Operador
				else if (filter.getProperty().equals(CAMPO_OPERADOR)) {
					sql.append (" AND op.operador = ").append(filter.getValue());
				}
			}
		}
		
		return sql.toString();
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
