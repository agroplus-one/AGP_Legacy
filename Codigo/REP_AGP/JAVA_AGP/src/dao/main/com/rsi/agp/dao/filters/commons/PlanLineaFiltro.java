package com.rsi.agp.dao.filters.commons;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.poliza.Linea;

public class PlanLineaFiltro implements Filter {

	private Long lineaSeguroId;

	public Long getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(Long lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Linea.class);
        criteria.add(Restrictions.eq("lineaseguroid",this.lineaSeguroId));

		return criteria;
	}
}
