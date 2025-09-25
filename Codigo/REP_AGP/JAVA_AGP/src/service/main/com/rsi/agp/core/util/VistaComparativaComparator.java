package com.rsi.agp.core.util;

import java.util.Comparator;


import com.rsi.agp.dao.tables.poliza.VistaComparativas;

public class VistaComparativaComparator implements Comparator <VistaComparativas> {

	@Override
	public int compare(VistaComparativas cp1, VistaComparativas cp2) {
		Double cc1 = cp1.getclaveComparacion();
		Double cc2 = cp2.getclaveComparacion();
		return cc1.compareTo(cc2);
	}
}
