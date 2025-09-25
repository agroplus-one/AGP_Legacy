package com.rsi.agp.dao.filters;

import org.jmesa.core.filter.FilterMatcher;

public class StringLikeFilterMatcher implements FilterMatcher {
	public boolean evaluate(Object itemValue, String filterValue) {
		if (itemValue == null) {
            return false;
        }

		String item =String.valueOf(itemValue);
		String filter = filterValue.toString();
        if (item.equals(filter)|| item.startsWith(filter)) {
            return true;
        }

        return false;
	}
}

