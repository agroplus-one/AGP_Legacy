package com.rsi.agp.dao.filters.cesp;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.Cultivo;

public class CultivoFiltro implements Filter {

	private Long idLineaSeguro;

	public CultivoFiltro(final Long idLinea) {
		setIdLineaSeguro(idLinea);
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Cultivo.class);
		criteria.add(Restrictions.eq("id.lineaseguroid", idLineaSeguro));
		return criteria;
	}

	public void setIdLineaSeguro(final Long idLineaSeguro) {
		this.idLineaSeguro = idLineaSeguro;
	}

}
