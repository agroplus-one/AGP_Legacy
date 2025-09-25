package com.rsi.agp.core.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase utilidad para fechas.
 * 
 * Obtiene el Locale del usuario y a partir de ello obtiene el formato
 * que debe aplicar para mostrar las fechas y realizar la conversion
 * 
 * @author XES
 */
public class DateUtil extends Object {

	/**
	 * Constructor
	 */
	public DateUtil() {
		super();
	}

	/**
	 * Formato por defecto para las fechas en presentacion
	 */
	public static final String FORMAT_DATE_DEFAULT = "dd/MM/yyyy";
	
	/**
	 * Formato por defecto para fechas con horas y/o minutos y/o segundos,etc.
	 */
	public static final String FORMAT_TIME_DEFAULT = "dd/MM/yyyy HH:mm:ss";

	/**
	 * Formato por defecto para las fechas sql
	 */
	public static final String FORMAT_DATE_SQL_DEFAULT = "dd/MM/yyyy";
	

	/**
	 * Obtener el formato de fecha a partir del Locale
	 */
	public static String lookupDateFormat() {
		return lookupDateFormat(null);
	}

	/**
	 * Obtener el formato de fecha a partir del Locale pasado.
	 * 
	 * Se accede al fichero de recursos ApplicationResources para obtener el
	 * formato definido en org.apache.struts.taglib.bean.format.date
	 * <pre>
	 * Ejemplo: 
	 *      org.apache.struts.taglib.bean.format.date = dd/MM/yyyy
	 * </pre>
	 */
	public static String lookupDateFormat(Locale aLocale) {
		ResourceBundle bundle;

		if (aLocale != null)
			bundle = ResourceBundle.getBundle("ApplicationResources", aLocale);
		else
			bundle = ResourceBundle.getBundle("ApplicationResources");

		// Obtener el formato del fichero de recursos multiidioma        
		String format =
			bundle.getString("org.apache.struts.taglib.bean.format.date");

		if (format == null)
			format = FORMAT_DATE_DEFAULT;
		return format;
	}

	/**
	 * Obtener el formato de fecha con hora a partir del Locale
	 * 
	 * Se accede al fichero de recursos ApplicationResources para obtener el
	 * formato definido en org.apache.struts.taglib.bean.format.time
	 * <pre>
	 * Ejemplo: 
	 *      org.apache.struts.taglib.bean.format.time = dd/MM/yyyy HH:mm
	 * </pre>
	 * 
	 */
	public static String lookupTimeFormat(Locale aLocale) {

		ResourceBundle bundle =
			ResourceBundle.getBundle("ApplicationResources", aLocale);

		// Obtener el formato del fichero de recursos multiidioma        
		String format =
			bundle.getString("org.apache.struts.taglib.bean.format.time");

		if (format == null)
			format = FORMAT_TIME_DEFAULT;
		return format;
	}



	/**
	 * Obtener el formato de fecha en SQL
	 * Se accede al fichero de recursos ApplicationResources para obtener el
	 * formato definido en org.apache.struts.taglib.bean.format.sql.date
	 * <pre>
	 * Ejemplo: 
	 *      org.apache.struts.taglib.bean.format.sql.date  = dd/MM/yyyy
	 * </pre>
	 * 
	 */
	public static String lookupDateSQLFormat(Locale aLocale) {
		ResourceBundle bundle =
			ResourceBundle.getBundle("ApplicationResources", aLocale);

		// Obtener el formato del fichero de recursos multiidioma        
		String format =
			bundle.getString("org.apache.struts.taglib.bean.format.sql.date");

		if (format == null)
			format = FORMAT_DATE_SQL_DEFAULT;
		return format;
	}

	/**
	 * Obtener el formato de time en SQL
	 * Se accede al fichero de recursos ApplicationResources para obtener el
	 * formato definido en org.apache.struts.taglib.bean.format.sql.time
	 * <pre>
	 * Ejemplo: 
	 *      org.apache.struts.taglib.bean.format.sql.time  = dd/mm/yyyy hh24:mi:ss
	 * </pre>
	 */
	public static String lookupTimeSQLFormat(Locale aLocale) {
		ResourceBundle bundle =
			ResourceBundle.getBundle("ApplicationResources", aLocale);

		// Obtener el formato del fichero de recursos multiidioma        
		String format =
			bundle.getString("org.apache.struts.taglib.bean.format.sql.time");

		if (format == null)
			format = FORMAT_DATE_SQL_DEFAULT;
		return format;
	}

	/**
	 * Metodo que devuelve true si el formato de fecha pasado es del estilo
	 * yyyyMMdd, es decir, formato Americano
	 * @param aFormato Formato pasado
	 * @return true si el formato es americano
	 */
	public static boolean isWeightedYearPattern(String aFormato) {
		String[] separadores = { "/", "-", ".", "_" };

		for (int iCnt = 0; iCnt < separadores.length; iCnt++) {
			if (aFormato
				.equals(
					"yyyy"
						+ separadores[iCnt]
						+ "MM"
						+ separadores[iCnt]
						+ "dd"))
				return true;
		}

		return false;
	}


	/**
	 * Funcion de conversion de fecha a String segun el formato pasado
	 * @param aCal Fecha Date
	 * @param aFormat Formato de cadena de la fecha en String
	 * @return String con el valor de la fecha formateado segun el formato
	 * pasado
	 */
	public static String date2String(Date aCal, String aFormat) {
		if (aCal == null)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(aFormat);
		return sdf.format(aCal);
	}

	/**
	 * Funcion de conversion de fecha a String segun el Locale pasado
	 * @param aCal Fecha Date
	 * @param aLocale Locale para el que obtener el formato definido en
	 * el fichero de recursos
	 * @return String con el valor de la fecha formateado segun el formato
	 * definido segun el Locale
	 */
	public static String date2String(Date aCal, Locale aLocale) {
		// Obtener el formato a aplicar del Locale
		String tmpFormat = lookupDateFormat(aLocale);
		return date2String(aCal, tmpFormat);
	}

	/**
	 * Funcion de conversion de fecha completa a String segun el Locale pasado
	 * @param aCal Fecha Date
	 * @param aLocale Locale para el que obtener el formato definido en
	 * el fichero de recursos
	 * @return String con el valor de la fecha formateado segun el formato
	 * definido segun el Locale
	 */	
	public static String time2String(Date aCal, Locale aLocale) {
		// Obtener el formato a aplicar del Locale
		String tmpFormat = lookupTimeFormat(aLocale);
		return date2String(aCal, tmpFormat);
	}

	/**
	 * Convertir la cadena pasada fecha con formato indicado a Date
	 * @param aFormat Formato
	 * @param data String con la fecha a convertir
	 */
	public static Date string2Date(String data, String aFormat)
		throws Exception {
		if (data == null)
			return null;
		if (data.equals(""))
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(aFormat);
		return sdf.parse(data);
	}



	public static Date string2Date(String data, Locale aLocale)
		throws Exception {
		String tmpFormat = lookupDateFormat(aLocale);
		return string2Date(data, tmpFormat);
	}


	/**
	 * Convertir la cadena pasada fecha con formato indicado a Calendar
	 */
	public static Date string2Calendar(String data, String aFormat)
		throws Exception {
		if (data == null)
			return null;
		if (data.equals(""))
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(aFormat);
		return sdf.parse(data);
	}

	public static Date string2Calendar(String data, Locale aLocale)
		throws Exception {
		String tmpFormat = lookupTimeFormat(aLocale);
		return string2Calendar(data, tmpFormat);
	}

	public static Timestamp date2Timestamp(Date aDate) {
		Timestamp timestamp = new Timestamp(aDate.getTime());
		return timestamp;
	}

	public static Timestamp calendar2Timestamp(Calendar aCal) {
		Timestamp timestamp = new Timestamp((aCal.getTime()).getTime());
		return timestamp;
	}


	/**
	 * Paso de Date a Calendar
	 * @param aDate Date
	 * @return Calendar
	 */
	public static Calendar date2Calendar(Date aDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(aDate);
		return cal;
	}

	/**
	 * Funcion que nos dice si la cadena pasada es una fecha
	 * correcta con el locale indicado
	 */
	public static boolean isDate(String aData, Locale aLocale) {
		String tmpFormat = lookupDateFormat(aLocale);
		return isDate(aData, tmpFormat);
	}

	/**
	 * Funcion que devuelve true si la fecha cumple el formato y es
	 * ademas una fecha correcta
	 */
	public static boolean isDate(String aDate, String aFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(aFormat);
		try {
			sdf.setLenient(false);
			sdf.parse(aDate);
			return true;
		} catch (ParseException pExc) {
			return false;
		}
	}

	/**
	 * Funcion que nos dice si la cadena pasada es una fecha
	 * correcta con el locale indicado.	 
	 * @see DateUtil#lookupTimeFormat(java.util.Locale aLocale)
	 * @param aDate Fecha
	 * @param aLocale Locale
	 */
	public static boolean isTime(String aDate, Locale aLocale) {
		String tmpFormat = lookupTimeFormat(aLocale);
		return isTime(aDate, tmpFormat);
	}

	/**
	 * Funcion que devuelve true si la fecha cumple el formato y es
	 * ademas una fecha correcta
	 * @param aDate Fecha
	 * @param aFormat Formato
	 */
	public static boolean isTime(String aDate, String aFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(aFormat);
		try {
			sdf.setLenient(false);
			sdf.parse(aDate);
			return true;
		} catch (ParseException pExc) {
			return false;
		}
	}

	/**
	 * Funcion que devuelve la fecha actual
	 */
	public static Date getFechaActual() {

		Calendar fechaActual = Calendar.getInstance();
		// Nos guardamos el dia,mes,anyo
		int year = fechaActual.get(Calendar.YEAR);
		int month = fechaActual.get(Calendar.MONTH);
		int date = fechaActual.get(Calendar.DATE);
		// Hacemos clear para quitarle las horas a la fecha								
		fechaActual.clear();
		fechaActual.set(year, month, date);

		return fechaActual.getTime();
	}
	
	public static Date getFechaActualConHora() {

		Calendar fechaActual = Calendar.getInstance();
		// Nos guardamos el dia,mes,anyo
		int year = fechaActual.get(Calendar.YEAR);
		int month = fechaActual.get(Calendar.MONTH);
		int date = fechaActual.get(Calendar.DATE);
		fechaActual.set(year, month, date);

		return fechaActual.getTime();
	}


	/**
 	 * Funcion que devuelve la fecha sin horas dado un calendar
 	 * @param aCalendar Calendar 
 	 * @return Calendar sin las horas del objeto inicial
	 */
	public static Calendar getFecha(Calendar aCalendar) {

		Calendar fecha = Calendar.getInstance();
		// Nos guardamos el dia,mes,anyo
		int year = aCalendar.get(Calendar.YEAR);
		int month = aCalendar.get(Calendar.MONTH);
		int date = aCalendar.get(Calendar.DATE);
		// Hacemos clear para quitarle las horas a la fecha								
		fecha.clear();
		fecha.set(year, month, date);

		return fecha;
	}

	/**
	 * Comprueba que la fecha recibida sea menor (24 horas ) que la actual.
	 * @param Date fecha1
	 * @return boolean
	 */
	public static boolean isFechaMenorActual(Date fecha1){
		
		Calendar fecha1Aux = new GregorianCalendar();
		fecha1Aux.setTime(fecha1);
		
		 
		Calendar c1 = getFecha(fecha1Aux);
		Calendar c2 = getFecha(new GregorianCalendar());
		
		if (c1.compareTo(c2)<0){
			return true;
		}
		return false;
	}
	
	/**
	 * Funcion que devuelve el nombre al que corresponde un numero de mes
	 * @param mes
	 * @return String
	 */ 	
	public static String getNombreMes(int mes) {
		String resultado = "";				
	
		switch (mes){
			case 1:
				resultado = "Enero";
				break;
			case 2:
				resultado = "Febrero";
				break;
			case 3:
				resultado = "Marzo";
				break;
			case 4:
				resultado = "Abril";
				break;
			case 5:
				resultado = "Mayo";
				break;
			case 6:
				resultado = "Junio";
				break;
			case 7:
				resultado = "Julio";
				break;
			case 8:
				resultado = "Agosto";
				break;
			case 9:
				resultado = "Septiembre";
				break;
			case 10:
				resultado = "Octubre";
				break;
			case 11:
				resultado = "Noviembre";
				break;
			case 12:
				resultado = "Diciembre";
				break;
			default:
				break;
		}
		return resultado;
	}
	
	/**
	 * Devuelve la fecha actual con la hora y minutos indicados por parámetro
	 * @param hora
	 * @param minuto
	 * @return
	 */
	public static Calendar getFechaActualConHora (int hora, int minuto) {
		
		// Obtiene la fecha actual real
		Calendar cal = Calendar.getInstance();
						
		// Establece la hora y minutos indicados por parámetro y devuelve
		cal.set(Calendar.HOUR_OF_DAY, hora);
		cal.set(Calendar.MINUTE, minuto);
		
		return cal;
	}
	
	/**
	 * Devuelve true si la hora actual es mayor que la hora que se indica por parámetro o false en caso contrario
	 * @param hora
	 * @return
	 */
	public static boolean horaActualMayor (int hora, int minuto) {
		
		// Si la hora y minuto son válidas
		if (hora >= 0 && hora <= 23 && minuto >= 0 && minuto <= 59) {
			// Devuelve si la fecha actual con la hora y minuto indicados es mayor que la actual
			return Calendar.getInstance().after(getFechaActualConHora(hora, minuto));
		}
		
		// Si la hora y minuto de parámetro no son válidas, se devuelve false
		return false;
		
	}
	/**
	 * Dada una fecha, calcula la diferencia entre en a�os entre esa fecha
	 * y la fecha actual
	 * 16/12/2014 U029769
	 * @param fecha
	 * @return
	 */
	public static int getNumAnosEntreFechas(Date fecha) {
		Calendar cal = Calendar.getInstance(); // current date
		int currYear = cal.get(Calendar.YEAR);
		int currMonth = cal.get(Calendar.MONTH);
		int currDay = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(fecha); 
		int years = currYear - cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		if (month == currMonth) { 
		    return cal.get(Calendar.DAY_OF_MONTH) <= currDay ? years : years - 1;
		} else {
		    return  month > month ? years - 1 : years;
		}
	}
	/**
	 * Dadas 2 fechas te devuelve el numero de dias que han pasado
	 * @param fechainicial
	 * @param fechafinal
	 * @return dias
	 */
	public static int obtener_dis_entre_2_fechas(Date fechainicial, Date fechafinal) {

		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String fechainiciostring = df.format(fechainicial);
		try {
		fechainicial = df.parse(fechainiciostring);
		}
		catch (ParseException ex) {
		}

		String fechafinalstring = df.format(fechafinal);
		try {
		fechafinal = df.parse(fechafinalstring);
		}
		catch (ParseException ex) {
		}

		long fechainicialms = fechainicial.getTime();
		long fechafinalms = fechafinal.getTime();
		long diferencia = fechafinalms - fechainicialms;
		double dias = Math.floor(diferencia / 86400000L);// 3600*24*1000 
		return ( (int) dias);
	}

	
	
}
