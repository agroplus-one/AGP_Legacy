package com.rsi.agp.core.webapp.util;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

public class LongEditor extends PropertyEditorSupport{
		
	public void setAsText(String text) {
		    try {
		    		Long type = new Long(text);
		    		setValue(type);
			} catch (NumberFormatException ne) {
				setValue(null);
			}	  
	}
}
