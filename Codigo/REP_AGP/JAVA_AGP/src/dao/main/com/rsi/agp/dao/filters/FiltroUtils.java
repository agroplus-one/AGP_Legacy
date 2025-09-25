package com.rsi.agp.dao.filters;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class FiltroUtils {

	public final static boolean noEstaVacio(final Object object) {
		boolean resultado = false;
		if (null != object) {
			if (object instanceof BigDecimal) {
				if (new BigDecimal(object.toString()).intValue() >= 0 ) {
					resultado = true;
				}
			} else if (object instanceof Character) {
				if(!object.toString().equals("-1")) {
					resultado = true;
				}
			} else if (object instanceof Date) {
				if (!new Date().equals((Date) object)) {
					resultado = true;
				}
			} else if (object instanceof Integer) {
				if (Integer.parseInt(object.toString()) >= 0 ) {
					resultado = true;
				}
			} else if (!"".equals(object.toString())) {
				resultado = true;
			}
			else if (object instanceof ArrayList) {
				ArrayList<?> l = (ArrayList<?>)object;
				if (l.size() > 0) {
					resultado = true;
				}
			}
		}
		return resultado;
	}
	
	/**
	 * Comprueba que la cadena no es nulo ni está vacía después de quitarle los espacios en blanco
	 * @param cadena
	 * @return
	 */
	public final static boolean noEstaVacioSinEspacios(final String cadena) {		
		return (cadena == null ? false : (noEstaVacio (cadena.trim())));
	}

	public final static int getMaxVisoresResults() {
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		return Integer.parseInt(bundle.getString("visores.numElements"));
	}
	
	// formato español
	public final static Date getDDMMYYYYDate(String date) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha;
		
		try {
			fecha = sdf.parse(date);
		} catch (ParseException e) {
			fecha = new Date();
		}
		
		return fecha;
	}
	
	// formato español = "dd/MM/yyyy"
	public final static Date getGenericDate(String format, String date) {
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date fecha;
		
		try {
			fecha = sdf.parse(date);
		} catch (ParseException e) {
			fecha = new Date();
		}
		
		return fecha;
	}
}
