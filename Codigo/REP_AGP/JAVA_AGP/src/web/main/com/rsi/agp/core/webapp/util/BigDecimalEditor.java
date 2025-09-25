package com.rsi.agp.core.webapp.util;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

public class BigDecimalEditor extends PropertyEditorSupport{
		
	public void setAsText(String text) {
		    try {
		    		BigDecimal type = new BigDecimal(text);
		    		setValue(type);
			} catch (NumberFormatException ne) {
				setValue(null);
			}	  
	}
}
