package com.rsi.agp.core.util;

import java.util.Collection;
import java.util.Map;

public class CollectionsAndMapsUtil {

	/**
	 * Comprueba si una colecci�n es nula o est� vac�a 
	 * @param collection
	 * @return boolean
	 */
	public static boolean isEmpty(final Collection<?> collection){
		return (collection == null || collection.isEmpty());
	}
	
	/**
	 * Compprueba si un mapa es nulo o est� vac�o
	 * @param map
	 * @return boolean
	 */
	public static boolean isEmpty(final Map<?,?> map){
		return (map == null || map.isEmpty());
	}
	
	
	/**
	 * Devuelve la dimensi�n de la colecci�n comprobando antes si la colecci�n es nula o est� vac�a 
	 * @param collection
	 * @return int con el tama�o de la colecci�n
	 */
	public static int size(final Collection<?> collection){
		int size = 0;
		if(!isEmpty(collection)){
			size = collection.size();
		}
		return size;
	}
}
