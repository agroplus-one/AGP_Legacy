package com.rsi.agp.core.util;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaAnexo;


public class ExplotacionCoberturaAnexoComparator implements Comparator <ExplotacionCoberturaAnexo> {
	private static final Log logger = LogFactory.getLog(ExplotacionCoberturaAnexoComparator.class);
	public int compare(ExplotacionCoberturaAnexo ex1, ExplotacionCoberturaAnexo ex2) {
		Long e1 = null;
		Long e2 = null;
		e1 = ex1.getclaveComparacion();
		e2 = ex2.getclaveComparacion();			
		
		return e1.compareTo(e2);
		
		
	}

}
