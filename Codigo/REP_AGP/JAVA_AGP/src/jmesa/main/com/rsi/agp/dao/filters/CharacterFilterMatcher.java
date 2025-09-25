package com.rsi.agp.dao.filters;


import org.jmesa.core.filter.FilterMatcher;

public class CharacterFilterMatcher implements FilterMatcher {

	public boolean evaluate(Object itemValue, String filterValue) {
		if (itemValue == null) {
            return false;
        }

		char item =String.valueOf(itemValue).charAt(0);
		char filter = filterValue.charAt(0);
        if (item == filter) {
            return true;
        }

        return false;
	}
}
