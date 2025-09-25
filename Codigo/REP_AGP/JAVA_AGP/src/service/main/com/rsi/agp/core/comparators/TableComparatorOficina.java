package com.rsi.agp.core.comparators;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class TableComparatorOficina implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		Integer clase = 0;
		Integer clase2 = 0;
		if(o1 != "" && o2 != ""){
			clase = Integer.parseInt((String) o1.toString().trim());
			clase2 = Integer.parseInt((String) o2.toString().trim());
		}
		return clase.compareTo(clase2);
	}

}
