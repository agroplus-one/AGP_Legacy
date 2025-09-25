package com.rsi.agp.dao.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

/**
 * Creates a command to wrap the Hibernate criteria API to filter.
 * 
 * @author T-Systems
 */
public class TableDataFilter implements CriteriaCommand {
    List<Filter> filters = new ArrayList<Filter>();
    HashMap<String, Object> filtros = new HashMap<String, Object>();

    public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }
 
   public Criteria execute(Criteria criteria) {
        for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
    }
   private void buildCriteria(Criteria criteria, String property, Object value) {
       if (value != null) {
           criteria.add(Restrictions.like(property, "%" + value + "%").ignoreCase());
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

	public HashMap<String, Object> getFiltros() {
		return filtros;
	}

	public void setFiltros(HashMap<String, Object> filtros) {
		this.filtros = filtros;
	}

    
}
