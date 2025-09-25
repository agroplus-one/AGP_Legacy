package com.rsi.agp.core.comparators;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class TableComparatorEntSubMed implements Comparator{

	
	public int compare(Object o1, Object o2) {
		
		int result = 0;
		
		final String[] c1 = ((String) o1).split("-");
		final String[] c2 = ((String) o2).split("-");
		
		final Integer ent1 = Integer.valueOf(c1[0].trim());
		final Integer ent2 = Integer.valueOf(c2[0].trim());
		
		result = ent1.compareTo(ent2);
		
		if (result == 0) {
			
			final Integer subent1 = Integer.valueOf(c1[1].trim());
			final Integer subent2 = Integer.valueOf(c2[1].trim());
			
			result = subent1.compareTo(subent2); 
		}
		
		return result;
	}

}
