package com.rsi.agp.dao.filters;

import org.jmesa.core.filter.FilterMatcher;

public class StringFilterMatcher implements FilterMatcher {

	public boolean evaluate(Object itemValue, String filterValue) {
		if (itemValue == null) {
            return false;
        }

		String item =String.valueOf(itemValue);
		String filter = filterValue.toString();
        if (item.equals(filter)) {
            return true;
        }

        return false;
	}
}