package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;


public class DatoAseguradoFiltro implements Filter {

	private DatoAsegurado datoAsegurado;
	private boolean existeLine999 = false;

	public DatoAseguradoFiltro ()
	{}
	
	public DatoAseguradoFiltro(final DatoAsegurado datoAsegurado) {
		this.datoAsegurado = datoAsegurado;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(DatoAsegurado.class);
		criteria.createAlias("asegurado", "ase");
		criteria.createAlias("lineaCondicionado", "l");
		
		if (existeLine999)
		{
			Criterion crit = Restrictions.eq("ase.id", datoAsegurado.getAsegurado().getId());
			criteria.add(crit);
			crit = Restrictions.eq("l.codlinea", datoAsegurado.getLineaCondicionado().getCodlinea());
			criteria.add(crit);
		}
		else
		{
			if (FiltroUtils.noEstaVacio(datoAsegurado)) {
				final Long id = datoAsegurado.getId();
				if (FiltroUtils.noEstaVacio(id)) {
					criteria.add(Restrictions.eq("id", id));
				}
				final BigDecimal codlinea = datoAsegurado.getLineaCondicionado().getCodlinea();
				if (FiltroUtils.noEstaVacio(codlinea)) {
					criteria.add(Restrictions.eq("l.codlinea", codlinea));
				}
				final String ccc = datoAsegurado.getCcc();
				if (FiltroUtils.noEstaVacio(ccc)) {
					criteria.add(Restrictions.eq("ccc", ccc));
				}
				final String ccc2 = datoAsegurado.getCcc2();
				if (FiltroUtils.noEstaVacio(ccc2)) {
					criteria.add(Restrictions.eq("ccc2", ccc2));
				}
				final Long idAsegurado = datoAsegurado.getAsegurado().getId();
				if (FiltroUtils.noEstaVacio(idAsegurado)) {
					criteria.add(Restrictions.eq("ase.id", idAsegurado));
				}
				final String nifCif = datoAsegurado.getAsegurado().getNifcif();
				if (FiltroUtils.noEstaVacio(nifCif)) {
					criteria.add(Restrictions.eq("ase.nifcif", nifCif));
				}
				final String discriminante = datoAsegurado.getAsegurado().getDiscriminante();
				if (FiltroUtils.noEstaVacio(discriminante)) {
					criteria.add(Restrictions.eq("ase.discriminante", discriminante));
				}
				criteria.addOrder(Order.asc("l.codlinea"));
			}
		}

		return criteria;
	}

	public DatoAsegurado getDatoAsegurado() {
		return datoAsegurado;
	}

	public void setDatoAsegurado(DatoAsegurado datoAsegurado) {
		this.datoAsegurado = datoAsegurado;
	}

	public boolean isExisteLine999() {
		return existeLine999;
	}

	public void setExisteLine999(boolean existeLine999) {
		this.existeLine999 = existeLine999;
	}
}
