package com.rsi.agp.core.util;

import java.util.HashMap;

import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;

public class DatosExplotacionesUtil {

	@SuppressWarnings("serial")
	public static JSONObject respuestaErrorPrecio(final String mensaje){
		HashMap<String, String> errorMap = new HashMap<String, String>(){{
			put("errorPrecio", mensaje);
		}};
		return new JSONObject(errorMap);
	}
}
