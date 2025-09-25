package com.rsi.agp.core.comparators;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class TableComparatorSigPacs implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		String cadena = (String) o1;
		String cadena2 = (String) o2;
		String x[] = cadena.split("-");
		String y[] = cadena2.split("-");
		
		if (cadena.contains("<div")){
			cadena = cadena.substring(0, cadena.indexOf("</div>"));
			cadena = cadena.substring(cadena.lastIndexOf(">")+1);
			x = cadena.split("-");
		}
		
		if (cadena2.contains("<div")){
			cadena2 = cadena2.substring(0, cadena2.indexOf("</div>"));
			cadena2 = cadena2.substring(cadena2.lastIndexOf(">")+1);
			y = cadena2.split("-");
		}
		
		if (x.length<1){
			x = new String [7];
			x[0]="0";
			x[1]="0";
			x[2]="0";
			x[3]="0";
			x[4]="0";
			x[5]="0";
			x[6]="0";
		}
		if (y.length<1){
			y = new String [7];
			y[0]="0";
			y[1]="0";
			y[2]="0";
			y[3]="0";
			y[4]="0";
			y[5]="0";
			y[6]="0";
		}
		
		for (int i = 0; i < x.length; i ++){
			int objeto1 = Integer.parseInt(x[i]);
			int objeto2 = Integer.parseInt(y[i]);
			if (objeto1 < objeto2){
				return -1;
			}
			else if (objeto1 > objeto2){
				return 1;
			}
		}
		return 0;
	}
}	

	
		
