package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.dao.tables.cpl.Modulo;

public class ModuloComparator implements Comparator<Modulo> {

	@Override
	public int compare(Modulo modulo1, Modulo modulo2) {
		
		String codModulo1 = modulo1.getId().getCodmodulo();
		String codModulo2 = modulo2.getId().getCodmodulo();
		String ppalcomplementario1 = modulo1.getPpalcomplementario().toString();
		String ppalcomplementario2 = modulo2.getPpalcomplementario().toString();
		
		if(codModulo1.equals("S") && codModulo2.equals("P")){
			return -1;
		}
		if(codModulo1.equals("P") && codModulo2.equals("S")){
			return 0;
		}
		
		if(ppalcomplementario1.compareTo(ppalcomplementario2)==-1 || ppalcomplementario1.equals(ppalcomplementario2)){
			return codModulo1.compareTo(codModulo2);
		}else{
			return ppalcomplementario2.compareTo(ppalcomplementario1);
		}
	}

}
