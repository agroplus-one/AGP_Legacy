package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAA;

public class SubAseguradoCcaaFiltro implements Filter {

	private Asegurado asegurado;
	private Poliza poliza;
	private BigDecimal codSubvencion;

	public SubAseguradoCcaaFiltro() {
		super();
	}	

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(SubAseguradoCCAA.class);

		if (null != asegurado) 
		{
			criteria.add(Restrictions.eq("asegurado.id", asegurado.getId()));
		}
		if (null != poliza) 
		{
			criteria.add(Restrictions.eq("poliza.idpoliza", poliza.getIdpoliza()));
		}
		if (null != codSubvencion)
		{
			criteria.add(Restrictions.eq("subvencionCCAA.id.codtiposubvccaa", codSubvencion));
		}

		return criteria;
	}


	public Asegurado getAsegurado() {
		return asegurado;
	}


	public void setAsegurado(Asegurado asegurado) {
		this.asegurado = asegurado;
	}


	public Poliza getPoliza() {
		return poliza;
	}


	public void setPoliza(Poliza poliza) {
		this.poliza = poliza;
	}


	public BigDecimal getCodSubvencion() {
		return codSubvencion;
	}


	public void setCodSubvencion(BigDecimal codSubvencion) {
		this.codSubvencion = codSubvencion;
	}

}
