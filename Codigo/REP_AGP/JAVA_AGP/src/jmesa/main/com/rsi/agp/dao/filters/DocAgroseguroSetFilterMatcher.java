package com.rsi.agp.dao.filters;

import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.jmesa.core.filter.FilterMatcher;

import com.rsi.agp.dao.tables.commons.ErrorPerfiles;
import com.rsi.agp.dao.tables.doc.DocAgroseguroPerfiles;

public class DocAgroseguroSetFilterMatcher  implements FilterMatcher {
	
	@SuppressWarnings("unchecked")
	public boolean evaluate(Object itemValue, String filterValue) {
		
		if (itemValue == null) {
            return false;
        }
		String[] filter = filterValue.replace("[","").replace("]", "").replace(" ", "").split(",");
		Set<DocAgroseguroPerfiles> item = (Set<DocAgroseguroPerfiles>) itemValue;
		
		for (DocAgroseguroPerfiles val : item) {
			if (ArrayUtils.contains(filter, val.getPerfil().getId().toString())) {
				return true;
			}				
		}

        return false;
	}
}