package com.rsi.agp.dao.filters.admin.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.commons.Via;

public class ViaFiltro implements Filter {

	private Integer posicion;
	private String filtro;
	private boolean soloNumero;

	public ViaFiltro(final Integer posicion, final String filtro) {
		this.posicion = posicion;
		this.filtro = filtro;
	}

	public ViaFiltro(final String filtro, final boolean soloNumero) {
		this.filtro = filtro;
		this.soloNumero = soloNumero;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Via.class);

		criteria.addOrder(Order.asc("nombre"));
		if (!"".equals(filtro)) {
			criteria.add(Restrictions.ilike("nombre", "%" + filtro + "%"));
		}
		if (!soloNumero) {
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}

		return criteria;
	}

}
