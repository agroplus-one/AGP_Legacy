package com.rsi.agp.dao.filters.param;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.param.Parametro;

public class ParametrizacionFiltro implements Filter {

	@Override
	public final Criteria getCriteria(final Session sesion) {
		return sesion.createCriteria(Parametro.class);
	}

}
