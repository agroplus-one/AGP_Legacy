
/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Clase serializable en formato JSON, para select en html
*
 **************************************************************************************************
*/
package com.rsi.agp.core.webapp.util.jsonJavaUtil;

public class GenericSelect  {
	private String value;
	private String textNode;

	public GenericSelect(String value, String textNode) {
		this.value = value;
		this.textNode = textNode;
	}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getTextNode() {
		return textNode;
	}
	
	public void setTextNode(String textNode) {
		this.textNode = textNode;
	}
}
