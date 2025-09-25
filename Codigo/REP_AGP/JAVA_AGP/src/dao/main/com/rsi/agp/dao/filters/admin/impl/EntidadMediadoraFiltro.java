package com.rsi.agp.dao.filters.admin.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.EntidadMediadora;

public class EntidadMediadoraFiltro implements Filter {

	private Integer posicion;
	private String filtro;

	public EntidadMediadoraFiltro(final Integer posicion, final String filtro) {
		this.posicion = posicion;
		this.filtro = filtro;
	}

	public EntidadMediadoraFiltro(final String filtro) {
		this.filtro = filtro;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(EntidadMediadora.class);

		if (FiltroUtils.noEstaVacio(filtro)) {
			criteria.add(Restrictions.ilike("nomentidad", "%" + filtro + "%"));
		}
		if (FiltroUtils.noEstaVacio(posicion)) {
			criteria.addOrder(Order.asc("nomentidad"));
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}

		return criteria;
	}

}
