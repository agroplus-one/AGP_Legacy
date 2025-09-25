package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.core.report.anexoMod.BeanExplotacion.BeanRiesgoCubiertoElegido;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;

public class BeanRCbrtoElegidoComparator implements Comparator <BeanRiesgoCubiertoElegido> {
	public int compare(BeanRiesgoCubiertoElegido rce1, BeanRiesgoCubiertoElegido rce2) {
		Long cc1 = this.getclaveBeanRCElegido(rce1.getFila(),rce1.getColumna());
		Long cc2 = this.getclaveBeanRCElegido(rce2.getFila(),rce2.getColumna());	
		return cc1.compareTo(cc2);	
	}
	
	public Long getclaveBeanRCElegido(int fila, int columna){
    	Long res = null;
    	String fil = String.format("%02d", fila);	
		String col  = String.format("%02d", columna);

		String clave1=	fil + col;
		res = new Long(clave1);
    	return res;
    }

}
