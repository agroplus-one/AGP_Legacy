package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;

public class ComparativaPolizaComparator implements Comparator<ComparativaPoliza> {
	
	public int compare(ComparativaPoliza cp1, ComparativaPoliza cp2) {
		return cp1.getId().compareTo(cp2.getId());
	}
}