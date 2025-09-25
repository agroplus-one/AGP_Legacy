package com.rsi.agp.core.webapp.util;

import java.math.BigDecimal;
import java.util.List;

public class CoberturasUtils {
	
	/**
	 * Devuelve un boolean si para el código de CPM introducido aplica la comprobación contra la lista de CPM o no
	 * @param cpm Código de CPM
	 * @return Boolean
	 */
	private static boolean aplicaCPM (BigDecimal cpm) {
		
		// Control de cpm nulo
		if (cpm == null) return false;
		
		switch (cpm.intValue()){
			//% CAPITAL ASEGURADO
			case 362: return true;
			//% FRANQUICIA
			case 120: return true;
			//% MINIMO INDEMNIZABLE
			case 121: return true;
			//CALCULO INDEMNIZACION
			case 174: return true;
			//DAÑOS CUBIERTOS
			case 169: return true;
			//GARANTIZADO
			case 175: return true;
			//RIESGO CUBIERTO ELEGIDO - ELEGIDO
			case 363: return true;
			//RIESGO CUBIERTO ELEGIDO - NO ELEGIDO
			case 0: return true;
			//TIPO FRANQUICIA
			case 170: return true;
			//TIPO RENDIMIENTO
			case 502: return true;
			// Si no es ninguno de los anteriores no aplica la comprobacion con la lista de CPM
			default: return false;
		}
	}
	
	/**
	 * Devuelve un boolean dependiendo de si la lista de CPM es vacia o, para el codigo de CPM pasado como parametro, existe en la
	 * lista o el concepto no aplica la comprobacion
	 * @param cpm Codigo de CPM
	 * @param listaCPM Lista de CPM permitidos para una poliza en concreto
	 * @return Boolean que indica si el CPM se tendra en cuenta como dato variable o no
	 */
	public static boolean isCPMPermitido (BigDecimal cpm, BigDecimal codConcepto, List<BigDecimal> listaCPM) {		
		return ((listaCPM!=null) && (listaCPM.isEmpty() || listaCPM.contains(cpm) || !aplicaCPM (codConcepto))); 					
	}

}
