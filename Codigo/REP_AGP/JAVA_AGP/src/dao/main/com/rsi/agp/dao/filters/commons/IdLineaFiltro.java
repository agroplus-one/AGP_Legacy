package com.rsi.agp.dao.filters.commons;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public class IdLineaFiltro implements Filter {

	private Long idLinea;

	public Long getIdLinea() {
		return idLinea;
	}

	public void setIdLinea(final Long idLinea) {
		this.idLinea = idLinea;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		Criteria criteria = sesion.createCriteria(PantallaConfigurable.class);
        criteria.add(Restrictions.eq("linea.lineaseguroid",this.idLinea));

		return criteria;
	}
}



