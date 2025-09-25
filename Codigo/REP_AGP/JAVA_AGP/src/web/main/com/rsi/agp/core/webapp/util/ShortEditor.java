package com.rsi.agp.core.webapp.util;

import java.beans.PropertyEditorSupport;

public class ShortEditor extends PropertyEditorSupport{
		
	public void setAsText(String text) {
	    try {
	    	Short type = new Short(text);
	    	setValue(type);
		} catch (NumberFormatException ne) {
			setValue(null);
		}	  
	}
}
