package com.rsi.agp.core.managers.confirmarext;

import java.math.BigDecimal;

public class ConfirmarExtGCBean {

	private BigDecimal comisionMediador;
	private BigDecimal gastosAdmon;
	private BigDecimal gastosAdquisicion;

	public ConfirmarExtGCBean() {
		super();
		comisionMediador = BigDecimal.ZERO;
		gastosAdmon = BigDecimal.ZERO;
		gastosAdquisicion = BigDecimal.ZERO;
	}

	public BigDecimal getComisionMediador() {
		return comisionMediador;
	}

	public void setComisionMediador(BigDecimal comisionMediador) {
		this.comisionMediador = comisionMediador;
	}

	public BigDecimal getGastosAdmon() {
		return gastosAdmon;
	}

	public void setGastosAdmon(BigDecimal gastosAdmon) {
		this.gastosAdmon = gastosAdmon;
	}

	public BigDecimal getGastosAdquisicion() {
		return gastosAdquisicion;
	}

	public void setGastosAdquisicion(BigDecimal gastosAdquisicion) {
		this.gastosAdquisicion = gastosAdquisicion;
	}
}