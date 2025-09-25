package com.rsi.agp.dao.filters.orgDat;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.MaterialCubierta;


public class VistaMaterialCubiertaInstFiltro implements Filter {
	
	
	public VistaMaterialCubiertaInstFiltro() {
		super();
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(MaterialCubierta.class);
		return criteria;
	}
}