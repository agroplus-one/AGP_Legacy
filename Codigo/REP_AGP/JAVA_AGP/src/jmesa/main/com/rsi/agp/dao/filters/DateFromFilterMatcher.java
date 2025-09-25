package com.rsi.agp.dao.filters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.core.filter.DateFilterMatcher;

public class DateFromFilterMatcher extends DateFilterMatcher {
	
	private static final Log logger = LogFactory.getLog(DateFromFilterMatcher.class);
	
	public DateFromFilterMatcher(String pattern) {
        super(pattern);
    }

	@Override
	public boolean evaluate(Object itemValue, String filterValue) {
		if (itemValue == null) {
            return false;
        }

        String pattern = getPattern();
        if (pattern == null) {
            return false;
        }
        DateFormat df = new SimpleDateFormat(pattern);
        try {
			//Date itemDate = df.parse(itemValue.toString());
			Date filterDate = df.parse(filterValue.toString());
			if (((Date)itemValue).compareTo(filterDate) >= 0)
				return true;
		} catch (ParseException e) {
			logger.error("Excepcion : DateFromFilterMatcher - evaluate", e);
		}
        return false;
	}

}
