package com.rsi.agp.dao.tables.poliza;

import java.math.BigDecimal;

public class ComsPctCalculado implements java.io.Serializable {

	private static final long serialVersionUID = 7516416422826920522L;

	private ComsPctCalculadoId id;
	private BigDecimal pctCalculado;

	public ComsPctCalculado() {
		super();
		this.id = new ComsPctCalculadoId();
	}

	public ComsPctCalculado(final Long idComparativa, final Character idGrupo, final BigDecimal pctCalculado) {
		super();
		this.id = new ComsPctCalculadoId(idComparativa, idGrupo);
		this.pctCalculado = pctCalculado;
	}

	public ComsPctCalculadoId getId() {
		return this.id;
	}

	public void setId(final ComsPctCalculadoId id) {
		this.id = id;
	}

	public BigDecimal getPctCalculado() {
		return this.pctCalculado;
	}

	public void setPctCalculado(final BigDecimal pctCalculado) {
		this.pctCalculado = pctCalculado;
	}
}
