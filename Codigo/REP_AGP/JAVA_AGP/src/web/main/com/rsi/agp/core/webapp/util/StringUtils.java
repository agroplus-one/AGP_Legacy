package com.rsi.agp.core.webapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	public static String nullToString(Object cad){
        try {
			if (cad == null || cad.equals("null"))
			    cad = "";
			return cad.toString();
		} catch (Exception e) {
			return "";
		}
        
    }
	
	public static boolean isNullOrEmpty(String value){
		if(value == null || value.equals("")){
			return true;
		}else{
			return false;
		}
	}
	
	public static String stack2string(Exception excepcion){
		try{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			excepcion.printStackTrace(pw);
			return "---- \r\n" + sw.toString() + " ---- \r\n";
		} catch (Exception e){
			return "No se pudo convertir la excepción a cadena de texto";
		}
	}
	
	public final static boolean validaNifCif (final String codigo)
	{
		boolean correcto = false;
		//Comprobamos si es CIF
		correcto = validaNifCif("CIF", codigo);
		//Si no valida CIF, comprobamos con NIF
		if (!correcto)
			correcto = validaNifCif("NIF", codigo);
		
		return correcto;
	}
	
	public final static boolean validaNifCif (final String tipo, final String codigo) {
		boolean resultado = false;
		if ("CIF".equalsIgnoreCase(tipo)) {
			try {
				final Pattern cifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
				final String CONTROL_SOLO_NUMEROS = "ABEH"; // Sólo admiten números como caracter de control
				final String CONTROL_SOLO_LETRAS = "KPQS"; // Sólo admiten letras como caracter de control
				final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI"; // Conversión de dígito a letra de control.
				if (!cifPattern.matcher(codigo).matches()) {
					// No cumple el patrón
					resultado = false;
				}

				int parA = 0;
				for (int i = 2; i < 8; i += 2) {
					final int digito = Character.digit(codigo.charAt(i), 10);
					if (digito < 0) {
						resultado = false;
					}
					parA += digito;
				}

				int nonB = 0;
				for (int i = 1; i < 9; i += 2) {
					final int digito = Character.digit(codigo.charAt(i), 10);
					if (digito < 0) {
						resultado = false;
					}
					int nn = 2 * digito;
					if (nn > 9) {
						nn = 1 + (nn - 10);
					}
					nonB += nn;
				}

				final int parcialC = parA + nonB;
				final int digitoE = parcialC % 10;
				final int digitoD = (digitoE > 0) ? (10 - digitoE) : 0;
				final char letraIni = codigo.charAt(0);
				final char caracterFin = codigo.charAt(8);

				final boolean esControlValido =
					(CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0	&& CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin)
					||(CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
				resultado = esControlValido;

			} catch (final Exception e) {
				resultado = false;
			}
		} else if ("NIF".equalsIgnoreCase(tipo)) {
			{
				final Pattern nifEspPattern = Pattern.compile("[KLM][0-9]{7}[0-9A-J]"); 
				if (nifEspPattern.matcher(codigo).matches()) {   // Nif Especiales  K L M Se calcula igual que CIF
					try {
						
						final String CONTROL_SOLO_NUMEROS = "ABEH"; // Sólo admiten números como caracter de control
						final String CONTROL_SOLO_LETRAS = "KPQS"; // Sólo admiten letras como caracter de control
						final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI"; // Conversión de dígito a letra de control.
						if (!nifEspPattern.matcher(codigo).matches()) {
							// No cumple el patrón
							resultado = false;
						}

						int parA = 0;
						for (int i = 2; i < 8; i += 2) {
							final int digito = Character.digit(codigo.charAt(i), 10);
							if (digito < 0) {
								resultado = false;
							}
							parA += digito;
						}

						int nonB = 0;
						for (int i = 1; i < 9; i += 2) {
							final int digito = Character.digit(codigo.charAt(i), 10);
							if (digito < 0) {
								resultado = false;
							}
							int nn = 2 * digito;
							if (nn > 9) {
								nn = 1 + (nn - 10);
							}
							nonB += nn;
						}

						final int parcialC = parA + nonB;
						final int digitoE = parcialC % 10;
						final int digitoD = (digitoE > 0) ? (10 - digitoE) : 0;
						final char letraIni = codigo.charAt(0);
						final char caracterFin = codigo.charAt(8);

						final boolean esControlValido =
							(CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0	&& CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin)
							||(CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
						resultado = esControlValido;

					} catch (final Exception e) {
						resultado = false;
					}
					
				} else {
					
					String dni = codigo.substring(0, codigo.length()-1);
					char letc = codigo.charAt(codigo.length()-1);
					Character letca = new Character(letc);
					String let = letca.toString();

					String regla = "[0-9]";

					if (let.matches(regla)) {
						resultado=false;
					} else {
						String cadena = "TRWAGMYFPDXBNJZSQVHLCKET" ;
						Integer dnii = new Integer(dni);
						int dniint = dnii.intValue();
						int posicion = dniint % 23 ;
						String letra = cadena.substring(posicion,posicion+1) ;
						if (letra.equals(let.toUpperCase())) {
							resultado=true;
						} else {
							resultado=false;
						}
					}

				}
				
			}
		}else if("NIE".equalsIgnoreCase(tipo)){
			String texto = codigo.toUpperCase();    
			String patron = "[XYZ]";
			String cadena = "TRWAGMYFPDXBNJZSQVHLCKET" ;
			int pos = 0;
			if (Pattern.matches(patron, texto.substring(0, 1))){
				  if(texto.substring(0,1).equals("X")){	         				
					  texto = texto.replaceFirst("X","0");
	      		  }else if(texto.substring(0,1).equals("Y")){
	      			texto = texto.replaceFirst("Y","1");
	      		  }else if(texto.substring(0,1).equals("Z")){
	      			texto = texto.replaceFirst("Z","2");
	      		  }
			
				pos = Integer.parseInt(texto.substring(0, 8)) % 23; 	     
	            if (texto.substring(8,9).equals(cadena.substring(pos, pos + 1))){
	                    resultado = true;
	             }else{
	                    resultado = false; 
	             }
	         }
		}
			
			
				
		return resultado;
	}

	public final static String normalizaRS(final String razonSocial) {
		String resultado = razonSocial;

		if (null == razonSocial) return null;

		Pattern pat;
		Matcher mat;

		pat = Pattern.compile("Comunidad de bienes", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(resultado);
		if (mat.find()) {
			resultado = resultado.replaceAll(mat.group(), " ");
			return normalizaEspacios(resultado.concat(" CB"));
		}

		pat = Pattern.compile("CB", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(resultado);
		if (mat.find()) {
			resultado = resultado.replaceAll(mat.group(), " ");
			return normalizaEspacios(resultado.concat(" CB"));
		}

		pat = Pattern.compile("Sociedad Anónima", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(resultado);
		if (mat.find()) {
			resultado = resultado.replaceAll(mat.group(), " ");
			return normalizaEspacios(resultado.concat(" SA"));
		}

		pat = Pattern.compile("Sociedad Anonima", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(resultado);
		if (mat.find()) {
			resultado = resultado.replaceAll(mat.group(), " ");
			return normalizaEspacios(resultado.concat(" SA"));
		}

		pat = Pattern.compile("Ayuntamiento", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(resultado);
		if (mat.find()) {
			resultado = resultado.replaceAll(mat.group(), " ");
			return normalizaEspacios("AYTO ".concat(resultado));
		}

		pat = Pattern.compile("AYTO", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(resultado);
		if (mat.find()) {
			resultado = resultado.replaceAll(mat.group(), " ");
			return normalizaEspacios("AYTO ".concat(resultado));
		}

		pat = Pattern.compile("Sociedad Cooperativa", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(resultado);
		if (mat.find()) {
			resultado = resultado.replaceAll(mat.group(), " ");
			return normalizaEspacios("SCOOP ".concat(resultado));
		}

		pat = Pattern.compile("SCOOP", Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(resultado);
		if (mat.find()) {
			resultado = resultado.replaceAll(mat.group(), " ");
			return normalizaEspacios("SCOOP ".concat(resultado));
		}

		return resultado;
	}

	public final static String normalizaEspacios(final String cadena) {
		String resultado = cadena;
		while (resultado.contains("  ")) {
			resultado = resultado.replace("  ", " ");
		}
		resultado = resultado.trim();
		return resultado;
	}

	/** 
	 * Función para calcular el digito de control de la Ref. de colectivo.
	 * @param  Referencia del colectivo.
	 * @return Digito de control calculado.
	 * DAA 09/04/12
	 */
	public final static String getDigitoControl(Integer numero) {
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		//si referencia.modulo es clave del properties 
		if (bundle.containsKey("referencia.modulo")){
			//cargamos el valor referencia.modulo del agp.properties
			final Integer valorDivisionDc = Integer.parseInt(bundle.getString("referencia.modulo"));
			return Integer.toString(numero % valorDivisionDc);
		}
		else{
			return "-1";
		}
	}

	public final static boolean cuentaValida(final String cuenta) {
		boolean resultado = false;
		final String banco = cuenta.substring(0,4);
		final String sucursal = cuenta.substring(4,8);
		final String dc = cuenta.substring(8,10);
		final String ccc = cuenta.substring(10,20);
		if ((obtenerDigito("00" + banco + sucursal).equals(new Integer(dc.substring(0,1))))
				&& (obtenerDigito(ccc).equals(new Integer(dc.substring(1,2))))) {
			resultado = true;
		}
		return resultado;
	}

	public final static Integer obtenerDigito(final String codigo){
		final Integer[] valores = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};
		Integer control = 0;
		for (int i = 0; i <= 9; i++) {
			control += codigo.charAt(i) * valores[i];
		}
		control = 11 - (control % 11);
		if (control == 11) {
			control = 0;
		} else if (control == 10) {
			control = 1;
		}
		return control;
	}

	public final static String normalizaCadena(String cadena) {
		byte letras[] = null;
		try {
			letras = cadena.getBytes("UTF-8");
		} catch (final UnsupportedEncodingException e) {
		}		
		return (letras != null) ? new String(letras) : cadena;
	}
	
	/**
	 * Función para quitar retornos de carro y tabulaciones de cadenas de texto
	 * @param cadena Cadena original.
	 * @return Cadena sin retornos de carro ni tabulaciones
	 */
	public final static String eliminaRetornos(String cadena){
		String result = cadena.replace("\\n", " ").replace("\\t", " ");
		return result;
	}
	
	
	/**
	 * Convierte el BigDecimal a cadena con simbolo de porcentaje
	 * @param percent
	 * @return
	 */
	public static String formatPercent(BigDecimal percent) {
		try {
			DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getPercentInstance(new Locale("es", "ES"));
			percent = percent.setScale(2, BigDecimal.ROUND_UP);
			percent = percent.divide(new BigDecimal(100));
			return decimalFormat.format(percent);			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "0%";
	}
	
	
	/**
	 * Escapa los caracteres problemáticos del HTML
	 * 
	 * @param cadena a formatear
	 * @return cadena formateada
	 * 
	 * From: class EscapeChars
	 * Autor: http://www.javapractices.com/topic/TopicAction.do?Id=96
	 */
	public static String forHTML(String aText){
		
	     final StringBuilder           result    = new StringBuilder();
	     final StringCharacterIterator iterator  = new StringCharacterIterator(aText);
	     char                          character =  iterator.current();
	     
	     while (character != CharacterIterator.DONE ){
	       if (character == '<') {
	         result.append("&lt;");
	       }
	       else if (character == '>') {
	         result.append("&gt;");
	       }
	       else if (character == '&') {
	         result.append("&amp;");
	       }
	       else if (character == '\"') {
	         result.append("&quot;");
	       }
	       else if (character == '\'') {
	    	   result.append("\\'");
	       }
	       else {
	         //the char is not a special one
	         //add it to the result as is
	         result.append(character);
	       }
	       character = iterator.next();
	     }
	     return result.toString();
	  }
	
	public static String convertStreamToString(InputStream is){
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
			}
			is.close();
			return sb.toString();
		} catch (IOException e) {
			return "No se pudo obtener la cadena asociada al input stream";
		}
		
	}
	
	/**
	 * Transforma la lista de objetos en una cadena con los todos los objetos separados por comas y entre paréntesis.
	 * p.e [obj1, obj2, ..., objn] --> (obj1, obj2, ..., objn) --> ('obj1', 'obj2', ..., 'objn') si incComillas es true
	 * @param lista Lista de objetos
	 * @param incComillas Indica si cada valor de la lista a devolver irá entre comillas simples
	 * @return
	 */
	
	public static String toValoresSeparadosXComas (List<? extends Object> lista, boolean incComillas) {
		return (lista == null) ? "" : ((incComillas) ? (lista.toString().replace(" ", "").replace("[", "('").replace("]", "')").replace(",", "','")) : (lista.toString().replace(" ", "").replace("[", "(").replace("]", ")")));
	}
	
	/**
	 * Transforma la lista de objetos en una cadena con los todos los objetos separados por comas y entre paréntesis si el boolean 
	 * 'incParent' lo indica
	 * @param lista
	 * @param incComillas
	 * @param incParent
	 * @return
	 */
	public static String toValoresSeparadosXComas (List<? extends Object> lista, boolean incComillas, boolean incParent) {		
		
		if (incParent) return toValoresSeparadosXComas (lista, incComillas);
		else return toValoresSeparadosXComas (lista, incComillas).replace("(", "").replace(")", "");
	}
	
	public static String toValoresSeparadosXComas (String[] lista, boolean incComillas) {
		return toValoresSeparadosXComas(Arrays.asList(lista), incComillas);
	}
	
	/** Comprueba si un campo bigdecimal es null, si lo es devuelve cero
	 * 
	 * @param valor
	 * @return
	 */
	public static BigDecimal nullToZero (BigDecimal valor){
		if (valor == null)
			return new BigDecimal(0);
		else
			return valor;
	}
	
	/**
	 * Parte la cadena por el caracter separador y devuelve el resultado como una lista de BigDecimal
	 * @param cadenaCodigosLupas
	 * @param chrSeparador
	 * @return
	 */
	public static List<BigDecimal> asListBigDecimal(String cadenaCodigosLupas, String chrSeparador) {
		List<BigDecimal> lstCodEntidades = new ArrayList<BigDecimal>();
		
		try (Scanner scan = new Scanner (cadenaCodigosLupas)) {
			scan.useDelimiter(chrSeparador);
			lstCodEntidades = new ArrayList<BigDecimal>();
			
			while (scan.hasNextBigDecimal()) {
				lstCodEntidades.add(scan.nextBigDecimal());
			}
		}
		
		return lstCodEntidades;
	}
	/**
	 * Metodo que convierte los caracteres especiales de una cadena en caracteres ISO-8859-1 
	 * @param cadena
	 * @return 
	 * @throws Exception
	 */
	public static String decodeString (String cadena) throws Exception{
		
		byte[] arrByte = cadena.getBytes("ISO-8859-1");
		return  new String(arrByte, "UTF-8");
	}
	/**
	 * Comprueba si una string es un numero
	 * @param s
	 * @return bolean 
	 */
	public static boolean isNumeric(String s) { 
	    return s.matches("[-+]?\\d*\\.?\\d+"); 
	}
	
	public static String cortarCadena (String cad, int maximo) {
		
		if (cad != null && cad.length() > maximo) {
			return cad.substring(0, maximo-3).concat("...");
		}
		
		return "";
	}
	
	/**
	 * 
	 * @param BigDecimal a eliminar los ceros a la derecha
	 * @param MaximumFractionDigits
	 * @param MinimumFractionDigits
	 * @return
	 */
	public static BigDecimal eliminarCerosDerBigDecimal(BigDecimal num, int x, int y) {
		num = num.setScale(2, BigDecimal.ROUND_DOWN);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(x);
		df.setMinimumFractionDigits(y);
		df.setGroupingUsed(false);
		String result = df.format(num);
		result = result.replace(",", ".");	
		return new BigDecimal(result);
	}
	
	public static Long convertirModuloParaOrdenacion(String str){
		Long res = null;
		String resS="";
		if (str != null && !str.trim().isEmpty()){
			for (int i=0;i<str.length();i++){
				resS=resS + str.codePointAt(i);
			}
			res = new Long (String.format(org.apache.commons.lang.StringUtils.rightPad(resS, 6, "0")));
		}
		return res;
	}
	
	/**
	 * Elimina los espacios anteriores y posteriores a una cadena de texto
	 * @param cadena
	 * @return
	 */
	public static String eliminarEspacios(String cadena){
		String resultado = "";
		if(cadena != null){
			resultado = resultado.trim();
		}
		return resultado;
	}
}
