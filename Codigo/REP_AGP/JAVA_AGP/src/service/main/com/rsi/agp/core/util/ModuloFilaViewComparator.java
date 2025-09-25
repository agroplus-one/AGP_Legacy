package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.dao.tables.cpl.ModuloFilaView;

public class ModuloFilaViewComparator implements Comparator <ModuloFilaView> {

	public int compare(ModuloFilaView comparativaPoliza1, ModuloFilaView comparativaPoliza2) {
		String clave1 = null;
		String clave2 = null;
		Integer cl1   = null;
		Integer cl2   = null;
		
		clave1=comparativaPoliza1.getFilamodulo().toString() + comparativaPoliza1.getFilaComparativa().toString();
		clave2=comparativaPoliza2.getFilamodulo().toString() + comparativaPoliza2.getFilaComparativa().toString();
		cl1 = new Integer(clave1);
		cl2 = new Integer(clave2);
		
		return cl1.compareTo(cl2);
	}

}
