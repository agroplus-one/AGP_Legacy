package com.rsi.agp.core.util;

import java.util.Comparator;

import com.rsi.agp.dao.tables.poliza.ModuloPoliza;

public class ModuloPolizaComparator implements Comparator<ModuloPoliza> {

	@Override
	public int compare(ModuloPoliza modulo1, ModuloPoliza modulo2) {

		int result = modulo1.getId().getCodmodulo().compareTo(modulo2.getId().getCodmodulo());
		if (result == 0) {
			result = modulo1.getId().getNumComparativa().compareTo(modulo2.getId().getNumComparativa());
		}
		return result;
	}
}