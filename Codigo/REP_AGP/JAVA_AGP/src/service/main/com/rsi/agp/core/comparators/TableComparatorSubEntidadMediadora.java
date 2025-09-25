package com.rsi.agp.core.comparators;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class TableComparatorSubEntidadMediadora implements Comparator{

	
	public int compare(Object o1, Object o2) {
		
		String c1 = (String) o1;
		String c2 = (String) o2;
		Integer n1 = new Integer (c1);
		Integer n2 = new Integer (c2);
		
		if (n1 > n2){
			return 1;
		}else if (n1 < n2){
			return -1;
		}else{
			return 0;
		}
	}

}
