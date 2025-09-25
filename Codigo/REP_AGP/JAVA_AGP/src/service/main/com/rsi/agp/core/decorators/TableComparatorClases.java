package com.rsi.agp.core.decorators;


import java.util.Comparator;


@SuppressWarnings("rawtypes")
public class TableComparatorClases implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		Integer clase = Integer.parseInt((String) o1);
		Integer clase2 = Integer.parseInt((String) o2);
		
		return clase.compareTo(clase2);
	}
}
