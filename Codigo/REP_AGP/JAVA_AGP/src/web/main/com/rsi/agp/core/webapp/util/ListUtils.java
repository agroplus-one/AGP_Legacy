package com.rsi.agp.core.webapp.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.rsi.agp.core.util.Constants;

public class ListUtils {

	/**
	 * Parte la lista recibida como parametro en listas de como maximo 1000
	 * elementos para poder ser utilizados con el operador IN
	 * 
	 * @param <T>
	 * @param lista
	 * @return
	 */
	public static <T> List<List<T>> getListasParaIN(List<T> lista) {
		List<List<T>> listaIdsPartida = new ArrayList<List<T>>();

		// Numero de listas que se van a generar
		int numListas = (int) Math.ceil(new Double(lista.size()) / new Double(Constants.MAX_NUM_ELEM_OPERATOR_IN));

		for (int i = 0; i < numListas; i++) {
			int ini = (i * Constants.MAX_NUM_ELEM_OPERATOR_IN);
			int fin = ((i + 1) * Constants.MAX_NUM_ELEM_OPERATOR_IN);
			if (fin > lista.size()) {
				fin = lista.size();
			}
			listaIdsPartida.add(new ArrayList<T>(lista.subList(ini, fin)));
		}

		return listaIdsPartida;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object[] getListaParaIn(String listaIn, Class tipo)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException, InvocationTargetException {
		// quitamos los parentesis
		String listaInSinParentesis = listaIn.replaceAll("[()]", "");
		String[] listaStr = listaInSinParentesis.split(",");
		List<Object> resultado = new ArrayList<Object>();
		for (String cod : listaStr) {
			Object valor;
			Constructor cons = tipo.getConstructor(String.class);
			valor = cons.newInstance(cod);
			resultado.add(valor);
		}
		return resultado.toArray();
	}

}
