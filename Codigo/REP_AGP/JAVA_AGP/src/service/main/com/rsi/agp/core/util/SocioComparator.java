package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.cpl.Modulo;

public class SocioComparator implements Comparator<Socio> {

	@Override
	public int compare(Socio socio, Socio socio2) {
		
		int orden1 = socio.getOrden();
		int orden2 = socio2.getOrden();

		if (orden1>orden2)
			return 1;
		else if (orden1<orden2)
			return -1;
		else return 0;
	}

}
