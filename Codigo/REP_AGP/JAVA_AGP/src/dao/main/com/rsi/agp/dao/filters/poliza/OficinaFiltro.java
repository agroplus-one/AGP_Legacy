package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.commons.Oficina;

public class OficinaFiltro implements Filter {

	private Integer posicion;
	private String filtro;
	private BigDecimal codEntidad;
	private BigDecimal codOficina;

	public OficinaFiltro(final String filtro, final BigDecimal codEntidad) {
		super();
		this.filtro = filtro;
		this.codEntidad = codEntidad;
	}

	public OficinaFiltro(final Integer posicion, final String filtro, final BigDecimal codEntidad) {
		super();
		this.posicion = posicion;
		this.filtro = filtro;
		this.codEntidad = codEntidad;
	}
	
	public OficinaFiltro(BigDecimal codEntidad, BigDecimal codOficina) {
		this.codEntidad = codEntidad;
		this.codOficina = codOficina;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Oficina.class);

		if (FiltroUtils.noEstaVacio(posicion)) {
			criteria.addOrder(Order.asc("nomoficina"));
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
		if (FiltroUtils.noEstaVacio(filtro)) {
			criteria.add(Restrictions.ilike("nomoficina", "%".concat(filtro).concat("%")));
		}
		if (FiltroUtils.noEstaVacio(codEntidad)) {
			criteria.add(Restrictions.eq("id.codentidad", codEntidad));
		}
		if (FiltroUtils.noEstaVacio(this.codOficina)) {
			criteria.add(Restrictions.eq("id.codoficina", codOficina));
		}

		return criteria;
	}

}
