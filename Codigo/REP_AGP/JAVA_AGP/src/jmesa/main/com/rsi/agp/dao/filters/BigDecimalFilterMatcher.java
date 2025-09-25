package com.rsi.agp.dao.filters;

import java.math.BigDecimal;

import org.jmesa.core.filter.FilterMatcher;

public class BigDecimalFilterMatcher implements FilterMatcher {

	public boolean evaluate(Object itemValue, String filterValue) {
		BigDecimal item = new BigDecimal(String.valueOf(itemValue));
		BigDecimal filter = new BigDecimal(filterValue);
        if (item.compareTo(filter) == 0) {
            return true;
        }

        return false;
	}

}
