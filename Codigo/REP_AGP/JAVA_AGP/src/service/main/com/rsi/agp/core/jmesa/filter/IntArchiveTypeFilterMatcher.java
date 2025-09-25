package com.rsi.agp.core.jmesa.filter;

import org.jmesa.core.filter.FilterMatcher;

public class IntArchiveTypeFilterMatcher implements FilterMatcher {

	public boolean evaluate(Object itemValue, String filterValue) {
		Integer item = new Integer(String.valueOf(itemValue));
        Integer filter = new Integer(filterValue);
        if (filter.equals(new Integer(0)) && item.compareTo(filter) == 0) {
            return true;
        }
        else if (filter.equals(new Integer(1)) && item.compareTo(filter) >= 0){
        	return true;
        }

        return false;
	}

}
