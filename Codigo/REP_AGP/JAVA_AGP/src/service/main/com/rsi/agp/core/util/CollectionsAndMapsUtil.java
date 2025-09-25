package com.rsi.agp.core.util;

import java.util.Collection;
import java.util.Map;

public class CollectionsAndMapsUtil {

	/**
	 * Comprueba si una colección es nula o está vacía 
	 * @param collection
	 * @return boolean
	 */
	public static boolean isEmpty(final Collection<?> collection){
		return (collection == null || collection.isEmpty());
	}
	
	/**
	 * Compprueba si un mapa es nulo o está vacío
	 * @param map
	 * @return boolean
	 */
	public static boolean isEmpty(final Map<?,?> map){
		return (map == null || map.isEmpty());
	}
	
	
	/**
	 * Devuelve la dimensión de la colección comprobando antes si la colección es nula o está vacía 
	 * @param collection
	 * @return int con el tamaño de la colección
	 */
	public static int size(final Collection<?> collection){
		int size = 0;
		if(!isEmpty(collection)){
			size = collection.size();
		}
		return size;
	}
}
