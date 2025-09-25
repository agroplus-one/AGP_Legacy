/****************************************************************************
*
* CREACION
* --------
*
* REFERENCIA: P0000014796
* FECHA: 04/02/2010
* AUTOR: Oscar Navarro
* DESCRIPCION: Utilidades de seguridad para request
*
****************************************************************************/

package com.rsi.agp.core.util;
/**
* The Class SecureRequest.
* 
* @(#)SecureRequest.java This class is used to secure HTTP variables in a SQL and HTML manner
* @version
*/
public class SecureRequest {

	/**
	 * Secure string.
	 * 
	 * @param unsafeSQL
	 *            the unsafe SQL
	 * @param encodeXML
	 *            the encode XML
	 * @param trim
	 *            the trim
	 * @param maxLength
	 *            the max length
	 * 
	 * @return the string
	 */
	public final static String secureString(String unsafeSQL, boolean encodeXML, boolean trim, int maxLength) {
		if (unsafeSQL == null) {
			return "";
		}

		StringBuffer safe = new StringBuffer();
		String result = null;
		int length = unsafeSQL.length();
		char c;
		for (int n = 0; n < length; ++n) {
			c = unsafeSQL.charAt(n);
			if (c == '\'') {
				safe.append(c);
			}
			safe.append(c);
		}

		if (trim) {
			result = safe.toString().trim();
		} else {
			result = safe.toString();
		}

		if (maxLength != -1) {
			if (result.length() > maxLength) {
				result = result.substring(0, maxLength);
			}
		}
		return result;
	}

	/**
	 * Secure string values.
	 * 
	 * @param unsafeSQL
	 *            the unsafe SQL
	 * @param encodeXML
	 *            the encode XML
	 * @param trim
	 *            the trim
	 * 
	 * @return the string[]
	 */
	public final static String[] secureStringValues(String[] unsafeSQL, boolean encodeXML, boolean trim) {
		if (unsafeSQL == null) {
			return new String[0];
		}

		String[] result = new String[unsafeSQL.length];

		for (int i = 0; i < unsafeSQL.length; i++) {
			StringBuffer safe = new StringBuffer();
			int length = unsafeSQL[i].length();
			char c;
			for (int n = 0; n < length; ++n) {
				c = unsafeSQL[i].charAt(n);
				if (c == '\'') {
					safe.append(c);
				}
				safe.append(c);

				if (trim) {
					result[i] = safe.toString().trim();
				} else {
					result[i] = safe.toString();
				}
			}
		}
		return result;
	}
}
