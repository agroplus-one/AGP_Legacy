package com.rsi.agp.dao.filters.admin.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cgen.LineaCondicionado;


public class LineasCondicionadoFiltro implements Filter {

	private Integer posicion;
	private String filtro;

	public LineasCondicionadoFiltro(final Integer posicion, final String filtro) {
		this.posicion = posicion;
		this.filtro = filtro;
	}

	public LineasCondicionadoFiltro(final String filtro) {
		this.filtro = filtro;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(LineaCondicionado.class);

		if (FiltroUtils.noEstaVacio(posicion)) {
			criteria.addOrder(Order.asc("deslinea"));
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
		if (FiltroUtils.noEstaVacio(filtro)) {
			criteria.add(Restrictions.ilike("deslinea", "%" + filtro + "%"));
		}

		return criteria;
	}

}
