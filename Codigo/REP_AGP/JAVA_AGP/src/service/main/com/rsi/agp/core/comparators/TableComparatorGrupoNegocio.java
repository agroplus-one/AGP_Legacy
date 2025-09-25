package com.rsi.agp.core.comparators;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class TableComparatorGrupoNegocio implements Comparator{

	

	@Override
	public int compare(Object o1, Object o2) {
		String  g1 = (String)o1;
		String  g2 = (String)o2;

		if(g1.compareTo(g2)==-1 || g1.equals(g2)){
			return g1.compareTo(g2);
		}else{
			return g2.compareTo(g1);
		}
	}
}