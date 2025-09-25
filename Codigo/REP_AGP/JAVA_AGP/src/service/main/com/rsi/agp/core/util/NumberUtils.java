package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class NumberUtils {
	
	private static final int NUMERO_CERO = 0;
	private static final Locale LOCALE_SPAIN = new Locale("es", "ES");
	
	/**
	 * Calcula el valor de un porcentaje l
	 * @param porcentajeBase
	 * @param porcentajeGeneral
	 * @return
	 */
	public static BigDecimal calcularPorcentajes (BigDecimal porcentajeBase, BigDecimal porcentajeGeneral){		
		return ((porcentajeBase.multiply(porcentajeGeneral).divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP));
	}
	
	/**
	 * formatea un número con el punto para los miles y coma para los decimales
	 * 
	 * @param numero
	 * @param decimales
	 * @return
	 */
	public static String formatear(BigDecimal numero, int decimales){
		try{
			NumberFormat nf = NumberFormat.getInstance(Locale.ITALY);
			nf.setMinimumFractionDigits(decimales);
			if (numero != null)
				return nf.format(numero);
			else
				return "";
		}
		catch (Exception e){
			return "";
		}
	}
	
	/**
	* formatea un n�mero con dos decimales y el s�mbolo del euro
	 * @param numero
	 * @return
	 */
	public static String formatearMoneda(BigDecimal numero){
		NumberFormat df = NumberFormat.getCurrencyInstance(LOCALE_SPAIN);
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		return numero != null ? df.format(numero) : df.format(NUMERO_CERO); 
	}
	
	/**
	 * Formatea un String en BigDecimal con decimales
	 * @param cadena
	 * @param decimales
	 * @return
	 */
	public static BigDecimal formatToNumber(String cadena, int decimales){
		DecimalFormat nf = (DecimalFormat)NumberFormat.getInstance(Locale.ITALY);
        nf.setParseBigDecimal(true);
        nf.setMinimumFractionDigits(decimales);
        
		try {
			return (BigDecimal) nf.parse(cadena, new ParsePosition(0));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Formatea un String en BigDecimal sin decimales
	 * @param cadena
	 * @return
	 */
	public static BigDecimal formatToNumber(String cadena){		
        return formatToNumber (cadena, 0);
	}
	
	/**
	 * Trunca el número a los decimals indicados
	 * @param numero
	 * @param decimales
	 * @return
	 */
	public static double redondear( BigDecimal numero, int decimales ) {
	    return Math.round(numero.doubleValue()*Math.pow(10,decimales))/Math.pow(10,decimales);
	}  
	
	/**
	 * Convierta a Long la cadena recibida como parámetro
	 * @param number
	 * @return Long o null en caso de que ocurra algún error
	 */
	public static Long parseLong (String number) {
		try {
			return Long.parseLong(number);
		} catch (Exception e) {
			return null;
		}
	}
	
}
