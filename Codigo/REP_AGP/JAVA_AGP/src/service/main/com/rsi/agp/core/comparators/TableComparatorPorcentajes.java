package com.rsi.agp.core.comparators;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class TableComparatorPorcentajes implements Comparator{
	
	private final static String VACIO = "";
	
	public int compare(Object o1, Object o2) {
		
		String c1 = "";
		String c2 = "";
		if (o1 == null || VACIO.equals(o1))
			c1 = "0.00";
		else	
			c1 =((String) o1).substring(0, ((String)o1).length() -1 );
		if (o2 == null || VACIO.equals(o2)) 
			c2 = "0.00";
		else
			c2 =((String) o2).substring(0, ((String)o2).length() -1 );
		Float n1 = null ;
		Float n2 = null ;
		
		
		if (c1.contains("<span")){
			c1 = c1.substring(0, c1.indexOf("%"));
		}
		if (c2.contains("<span")){
			c2 = c2.substring(0, c2.indexOf("%"));
		}
			
		n1 = Float.valueOf(c1);
		n2 = Float.valueOf(c2);
		
		if (n1 > n2){
			return 1;
		}else if (n1 < n2){
			return -1;
		}else{
			return 0;
		}
	}
}