package com.rsi.agp.core.jmesa.sort;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.jmesa.limit.Limit;
import org.jmesa.limit.SortSet;

public class GenericoSort implements IGenericoSort {
	
	List<Sort> sorts = new ArrayList<Sort>();
	
	@Override
	public void addSort(String property, String order) {
		sorts.add(new Sort(property, order));
	}	
	
	public static class Sort {
		
        public final static String ASC = "asc";
        
        private final String property;
        private final String order;

        public Sort(String property, String order) {
            this.property = property;
            this.order = order;
        }

        public String getProperty() {
            return property;
        }

        public String getOrder() {
            return order;
        }
    }

	@Override
	public void getConsultaSort(Limit limit) {
		SortSet sortSet = limit.getSortSet();
		Collection<org.jmesa.limit.Sort> sorts = sortSet.getSorts();
		for (org.jmesa.limit.Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			this.addSort(property, order);
		}
	}

	public void clear() {
		if(null!=sorts) {
			sorts.clear();
		}		
	}

	@Override
	public Criteria execute(Criteria criteria) {
		return null;
	}
}