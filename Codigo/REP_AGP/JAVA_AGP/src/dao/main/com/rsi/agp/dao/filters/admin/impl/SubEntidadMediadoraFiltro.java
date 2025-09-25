package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;

public class SubEntidadMediadoraFiltro implements Filter {

	private Integer posicion;
	private String filtro;
	private BigDecimal codEntidadMediadora;

	public SubEntidadMediadoraFiltro(final Integer posicion, final String filtro, final BigDecimal codEntidadMediadora) {
		this.posicion = posicion;
		this.filtro = filtro;
		this.codEntidadMediadora = codEntidadMediadora;
	}

	public SubEntidadMediadoraFiltro(final String filtro, final BigDecimal codEntidadMediadora) {
		this.filtro = filtro;
		this.codEntidadMediadora = codEntidadMediadora;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);

		if (FiltroUtils.noEstaVacio(filtro)) {
			criteria.add(Restrictions.ilike("nomsubentidad", "%" + filtro + "%"));
		}
		if (FiltroUtils.noEstaVacio(posicion)) {
			criteria.addOrder(Order.asc("nomsubentidad"));
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
		if (FiltroUtils.noEstaVacio(codEntidadMediadora)) {
			criteria.add(Restrictions.eq("id.codentidad", codEntidadMediadora));
		}
	
		return criteria;
	}

}
