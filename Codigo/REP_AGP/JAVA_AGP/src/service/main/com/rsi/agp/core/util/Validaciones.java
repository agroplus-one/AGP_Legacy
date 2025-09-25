package com.rsi.agp.core.util;

import com.rsi.agp.core.webapp.util.StringUtils;

public class Validaciones {
	
	public static Boolean isValidObligatorio(String valorCampo){
		Boolean res=true;
		if(null==valorCampo || valorCampo.length()<1)
			res=false;
		return res;
	}
	
	public static Boolean isNumeric(String valorCampo){
		Boolean res=true;
		if(!StringUtils.isNumeric(valorCampo))
			res=false;
		return res;
	}
	
	public static Boolean isValidLongitud(String valorCampo, int longitud){
		Boolean res=true;
		if(valorCampo.length()!=longitud)
			res=false;
		return res;
	}
	
	public static Boolean isValidLongitudMax(String valorCampo, String nombreCampo, int longitudMaxima){
		Boolean res=true;
		if(valorCampo.length()>longitudMaxima)
			res=false;
		return res;
	}
	
	/**
	 * 
	 * @param valorCampo
	 * @param nombreCampo
	 * @param valoresFijos Valores separados por ;
	 * @return
	 */
	public static Boolean isValidValorFijo(String valorCampo, String nombreCampo, String valoresFijos){
		Boolean res=true;
		String valores[]=valoresFijos.split(";");
		Boolean esValido=false;
		for (int i = 0; i < valores.length; i++) {
			if(valorCampo.compareTo(valores[i])==0){
				esValido=true;
				break;
			}
		}
		if(!esValido)
			res=false;
		return res;
	}
}
