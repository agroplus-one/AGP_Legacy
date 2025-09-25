package com.rsi.agp.dao.filters.cesp;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;

public class RiesgoCubiertoFiltro implements Filter {

	private Long idLinea;
	private String codModulo;

	public RiesgoCubiertoFiltro(final Long idLinea, final String codModulo) {
		this.idLinea = idLinea;
		this.codModulo = codModulo;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(RiesgoCubierto.class);

		if (FiltroUtils.noEstaVacio(idLinea)) {
			criteria.add(Restrictions.eq("id.lineaseguroid", idLinea));
		}
		if (FiltroUtils.noEstaVacio(codModulo)) {
			criteria.add(Restrictions.eq("id.codmodulo", codModulo));
		}

		return criteria;
	}

}
