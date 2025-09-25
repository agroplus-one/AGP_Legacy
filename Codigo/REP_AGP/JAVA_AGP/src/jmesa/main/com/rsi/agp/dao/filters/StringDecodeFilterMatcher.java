package com.rsi.agp.dao.filters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.core.filter.FilterMatcher;

import com.rsi.agp.core.webapp.util.StringUtils;

public class StringDecodeFilterMatcher implements FilterMatcher {

	private final Log logger = LogFactory.getLog(StringDecodeFilterMatcher.class);
	
	public boolean evaluate(Object itemValue, String filterValue) {
		if (itemValue == null) {
            return false;
        }

		String item =String.valueOf(itemValue);
		String filter = "";
		try {
			filter = StringUtils.decodeString(filterValue.toString());
		
        if (StringUtils.decodeString(item).equals(filter) || (item).equals(filter)) {
            return true;
        }
		} catch (Exception e) {
			logger.error("Excepcion : StringDecodeFilterMatcher - evaluate", e);
		}
        return false;
	}
}


