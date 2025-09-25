package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;

public class SubAseguradoEnesaGanadoFiltro implements Filter {

	private Asegurado asegurado;
	private Poliza poliza;
	private BigDecimal codSubvencion;

	public SubAseguradoEnesaGanadoFiltro() {
		super();
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion
				.createCriteria(SubAseguradoENESAGanado.class);
		if (null != asegurado) {
			criteria.add(Restrictions.eq("asegurado.id", asegurado.getId()));
		}
		if (null != poliza) {
			criteria.add(Restrictions.eq("poliza.idpoliza",
					poliza.getIdpoliza()));
		}
		if (null != codSubvencion) {
			criteria.add(Restrictions.eq(
					"subvencionEnesaGanado.id.codtiposubvenesa", codSubvencion));
		}
		return criteria;
	}

	public Asegurado getAsegurado() {
		return this.asegurado;
	}

	public void setAsegurado(final Asegurado asegurado) {
		this.asegurado = asegurado;
	}

	public Poliza getPoliza() {
		return this.poliza;
	}

	public void setPoliza(final Poliza poliza) {
		this.poliza = poliza;
	}

	public BigDecimal getCodSubvencion() {
		return this.codSubvencion;
	}

	public void setCodSubvencion(final BigDecimal codSubvencion) {
		this.codSubvencion = codSubvencion;
	}
}