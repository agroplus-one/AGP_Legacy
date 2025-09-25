package com.rsi.agp.core.jmesa.filter;

import org.jmesa.core.filter.FilterMatcher;

public class LongFilterMatcher implements FilterMatcher {

	public boolean evaluate(Object itemValue, String filterValue) {
		Long item = new Long(String.valueOf(itemValue));
        Long filter = new Long(filterValue);
        if (item.compareTo(filter) == 0) {
            return true;
        }

        return false;
	}

}
