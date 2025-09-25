package com.rsi.agp.core.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ParamUtils {
	
	/**
	 * Recupera una serie de parámetros de request y los almacena en un mapa
	 * @param request Objeto request
	 * @param parametros Mapa donde se almacenarán
	 * @param arrayNombreParametros Array con los nombres de los parámetros que se quieren extraer
	 */
	public static void recuperarParametros(HttpServletRequest request, Map<String, Object> parametros, String[] arrayNombreParametros){
		
		if(parametros!=null && arrayNombreParametros!=null){
			for (int i = 0; i < arrayNombreParametros.length; i++) {
				String nombreParametro = arrayNombreParametros[i];
				if(request.getParameter(nombreParametro)!=null){
					parametros.put(nombreParametro, request.getParameter(nombreParametro));
				}
			}
		}
	}
	
	
	/**
	 * Recupera una serie de atributos de request y los almacena en un mapa
	 * @param request Objeto request
	 * @param parametros Mapa donde se almacenarán
	 * @param arrayNombreParametros Array con los nombres de los atributos que se quieren extraer
	 */
	public static void recuperarRequest(HttpServletRequest request, Map<String, Object> parametros, String[] arrayNombreParametros){

		if(parametros!=null && arrayNombreParametros!=null){
			for (int i = 0; i < arrayNombreParametros.length; i++) {
				String nombreParametro = arrayNombreParametros[i];
				if(request.getAttribute(nombreParametro)!=null){
					parametros.put(nombreParametro, request.getAttribute(nombreParametro));
				}
			}
		}
	}
}