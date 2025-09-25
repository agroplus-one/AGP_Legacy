package com.rsi.agp.core.util;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.agroseguro.contratacion.parcela.ParcelaDocument;

/* Pet. 70105. FIII ** MODIF TAM (03.03.2021) ** Inicio */

public class ParcelasComparator implements Comparator <ParcelaDocument> {
	private static final Log logger = LogFactory.getLog(ParcelasComparator.class);
	
	public int compare(ParcelaDocument parc1, ParcelaDocument parc2) {
		Long p1 = null;
		Long p2 = null;
		p1 = this.getClaveComparacion(parc1.getParcela().getHoja(), parc1.getParcela().getNumero());
		p2 = this.getClaveComparacion(parc2.getParcela().getHoja(), parc2.getParcela().getNumero());			
		
		return p1.compareTo(p2);
		
	}
	
	
	/*
	 * Método que devuelve un Long formado por la fila, Codconceptoppalmod y
	 * Codriesgocubierto
	 */
	public Long getClaveComparacion(int hoja, int numero) {
		Long res = null;
		
		String hojaParc = String.format("%03d", new Integer(hoja));
		String numeroParc = String.format("%03d", new Integer(numero));

		String clave1 = hojaParc + numeroParc;
		res = new Long(clave1);
		return res;
	}

}
