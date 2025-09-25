package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;

public class PolizaPctComisionesComparator implements Comparator<PolizaPctComisiones> {

	@Override
	public int compare(PolizaPctComisiones pp1, PolizaPctComisiones pp2) {
		
		String p1 = pp1.getGrupoNegocio().toString();
		String p2 = pp2.getGrupoNegocio().toString();
		
		if (p1.compareTo(p2)==-1)
			return -1;
		else
			return 1;
		
	}

}
