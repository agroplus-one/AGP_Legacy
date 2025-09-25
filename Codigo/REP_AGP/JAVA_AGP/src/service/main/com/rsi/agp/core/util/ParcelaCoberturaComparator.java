package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.dao.tables.poliza.ParcelasCoberturasNew;


public class ParcelaCoberturaComparator implements Comparator <ParcelasCoberturasNew> {
	
	public int compare(ParcelasCoberturasNew parc1, ParcelasCoberturasNew parc2) {
		
		return parc1.compareTo(parc2);		
	}
}