package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.core.webapp.util.VistaImportesPorGrupoNegocio;

public class VistaImportesGNComparator implements Comparator<VistaImportesPorGrupoNegocio> {

	@Override
	public int compare(VistaImportesPorGrupoNegocio vgn1, VistaImportesPorGrupoNegocio vgn2) {
		
		String gn1 = vgn1.getCodGrupoNeg();
		String gn2 = vgn2.getCodGrupoNeg();
		
		if (gn1.compareTo(gn2)==-1)
			return -1;
		else
			return 1;
		
	}

}
