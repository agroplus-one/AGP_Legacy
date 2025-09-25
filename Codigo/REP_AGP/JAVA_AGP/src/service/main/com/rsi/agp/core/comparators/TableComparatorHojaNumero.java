package com.rsi.agp.core.comparators;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class TableComparatorHojaNumero implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		String cadena = (String) o1;
		String cadena2 = (String) o2;
		int pos = 0;
		int tam = 0;
		String x[] = cadena.split("-");
		String y[] = cadena2.split("-");
		
		if (cadena.contains("<div")){
			x = cadena.split("");
			tam = x.length;
			for (int i=(tam-6);i>1;i--){
				if (x[i].equals(">")){
					pos=i;
					break;
				}
			}
			cadena = cadena.substring((cadena.indexOf("")+(tam-(tam-pos))), (cadena.indexOf("</div>")));
			x = cadena.split("-");
		}
		
		if (cadena2.contains("<div")){
			y = cadena2.split("");
			tam = y.length;
			for (int i=(tam-6);i>1;i--){
				if (y[i].equals(">")){
					pos=i;
					break;
				}
			}
			cadena2 = cadena2.substring((cadena2.indexOf("")+(tam-(tam-pos))), (cadena2.indexOf("</div>")));
			y = cadena2.split("-");
		}
		
		if (x.length<1){
			x = new String [2];
			x[0]="0";
			x[1]="0";
		}
		if (y.length<1){
			y = new String [2];
			y[0]="0";
			y[1]="0";
		}

		Integer hoja = new Integer (x[0].trim());
		Integer hoja2 = new Integer (y[0].trim());
		Integer numero = new Integer (x[1].trim());
		Integer numero2 = new Integer (y[1].trim());
		
		if (hoja < hoja2) {
			return -1;
		}
		else if (hoja > hoja2) {
			return 1;
		}
		else {
			if (numero > numero2){
				return 1;
			}else if (numero < numero2){
				return -1;
			}else{
				return 0;
			}
		}
	}
}	

	
		
