package com.rsi.agp.core.util;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.agroseguro.contratacion.explotacion.ExplotacionDocument;

/* Pet. 70105. FIII ** MODIF TAM (03.03.2021) ** Inicio */

public class ExplotacionesComparator implements Comparator <ExplotacionDocument> {
	private static final Log logger = LogFactory.getLog(ExplotacionesComparator.class);
	public int compare(ExplotacionDocument ex1, ExplotacionDocument ex2) {
		Long e1 = null;
		Long e2 = null;
		e1 = this.getclaveComparacion(ex1.getExplotacion().getNumero());
		e2 = this.getclaveComparacion(ex2.getExplotacion().getNumero());			
		
		return e1.compareTo(e2);
		
		
	}
	
	
	/*
	 * Método que devuelve un Long formado por el número de la explotación
	 */
	public Long getclaveComparacion(int numero) {
		Long res = null;
		
		String numeroExpl = String.format("%03d", new Integer(numero));

		String clave1 = numeroExpl;
		res = new Long(clave1);
		
		return res;
	}	

}
