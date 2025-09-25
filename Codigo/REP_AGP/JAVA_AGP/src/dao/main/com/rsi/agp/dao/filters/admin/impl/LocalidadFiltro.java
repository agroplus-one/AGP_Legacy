package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.commons.Localidad;

public class LocalidadFiltro implements Filter {

	private Integer posicion;
	private String filtro;
	private BigDecimal codProvincia;
	private boolean soloNumero;

	public LocalidadFiltro(final Integer posicion, final String filtro, final BigDecimal codProvincia) {
		this.posicion = posicion;
		this.filtro = filtro;
		this.codProvincia = codProvincia;
	}

	public LocalidadFiltro(String filtro, BigDecimal codProvincia, boolean soloNumero) {
		this.filtro = filtro;
		this.codProvincia = codProvincia;
		this.soloNumero = soloNumero;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Localidad.class);

		criteria.addOrder(Order.asc("nomlocalidad"));
		if (!"".equals(filtro)) {
			criteria.add(Restrictions.ilike("nomlocalidad", "%" + filtro + "%"));
		}
		if (null != codProvincia && !codProvincia.equals(new BigDecimal("0"))) {
			criteria.add(Restrictions.eq("id.codprovincia", codProvincia));
		}
		if (!soloNumero) {
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}

		return criteria;
	}

}
