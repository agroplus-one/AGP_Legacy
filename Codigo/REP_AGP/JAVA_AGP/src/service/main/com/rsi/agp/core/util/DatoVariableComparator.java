package com.rsi.agp.core.util;

import java.util.Comparator;

import es.agroseguro.modulosYCoberturas.DatoVariable;

public class DatoVariableComparator implements Comparator<DatoVariable> {
	public int compare(DatoVariable d1, DatoVariable d2) {
		Integer cc1 = null;
		Integer cc2 = null;
		cc1 = d1.getColumna();
		cc2 = d2.getColumna();

		return cc1.compareTo(cc2);
	}
}