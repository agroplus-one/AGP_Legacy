package com.rsi.agp.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que ayuda a "castear" un lista de un tipo de objecto a otro.
 * ListCastingHelper<I,O>, donde I es la clase de origen y O es la clase destino
 * @author srojo
 *
 * @param <I>
 * @param <O>
 */
public class ListCastingHelper<I,O> {
	
	private List<I> inputList;
	
	/**
	 * I -> clase de origen.
	 * O -> clase destino
	 * @param list
	 */
	public ListCastingHelper(List<I> list) {
		super();
		this.inputList = list;
	}

	/**
	 * Método que ejecuta el "casteo"
	 * @return lista "casteada"
	 */
	@SuppressWarnings("unchecked")
	public List<O> cast(){
		List<O> outputList = new ArrayList<O>();
		for(I element : inputList){
			outputList.add((O) element);
		}
		return outputList;
	}
}
