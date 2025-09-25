package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;

public class CapitalFiltro implements Filter {

	private Integer posicion; 
	private String filtro;
	private boolean soloNumero;

	public CapitalFiltro(final Integer posicion, final String filtro) {
		this.posicion = posicion;
		this.filtro = filtro;
	}

	public CapitalFiltro(final String filtro, final boolean soloNumero) {
		this.filtro = filtro;
		this.soloNumero = soloNumero;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		Criteria criteria = sesion.createCriteria(TipoCapital.class);

		criteria.addOrder(Order.asc("destipocapital"));		
		criteria.add(Restrictions.ne("codtipocapital", new BigDecimal(99)));
		if (!"".equals(filtro)) {
			criteria.add(Restrictions.ilike("destipocapital", "%" + filtro + "%"));
		}

		if (!soloNumero) {
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}

		return criteria;
	}
}