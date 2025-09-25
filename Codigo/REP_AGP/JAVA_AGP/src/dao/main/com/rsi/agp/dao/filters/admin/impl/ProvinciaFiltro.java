package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.commons.Provincia;

public class ProvinciaFiltro implements Filter {

	private Integer posicion;
	private String filtro;
	private boolean soloNumero;

	public ProvinciaFiltro(final Integer posicion, final String filtro) {
		this.posicion = posicion;
		this.filtro = filtro;
	}

	public ProvinciaFiltro(final String filtro, final boolean soloNumero) {
		this.filtro = filtro;
		this.soloNumero = soloNumero;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		Criteria criteria = sesion.createCriteria(Provincia.class);

		criteria.addOrder(Order.asc("nomprovincia"));		
		criteria.add(Restrictions.ne("codprovincia", new BigDecimal(99)));
		if (!"".equals(filtro)) {
			criteria.add(Restrictions.ilike("nomprovincia", "%" + filtro + "%"));
		}

		if (!soloNumero) {
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}

		return criteria;
	}

}
