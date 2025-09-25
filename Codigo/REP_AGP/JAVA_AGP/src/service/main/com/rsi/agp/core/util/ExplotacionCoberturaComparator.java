package com.rsi.agp.core.util;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;


public class ExplotacionCoberturaComparator implements Comparator <ExplotacionCobertura> {
	private static final Log logger = LogFactory.getLog(ExplotacionCoberturaComparator.class);
	public int compare(ExplotacionCobertura ex1, ExplotacionCobertura ex2) {
		Long e1 = null;
		Long e2 = null;
		e1 = ex1.getclaveComparacion();
		e2 = ex2.getclaveComparacion();			
		
		return e1.compareTo(e2);
		
		
	}

}
