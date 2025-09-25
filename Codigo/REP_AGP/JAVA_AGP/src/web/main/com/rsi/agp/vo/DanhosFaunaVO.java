package com.rsi.agp.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DanhosFaunaVO {

	private String fechaVigor;
	private String enVigor;
	private BigInteger tipoReduccion;
	private BigDecimal reduccionProducion;
	private String descripcion;

	public DanhosFaunaVO() {
		super();
	}

	public String getFechaVigor() {
		return fechaVigor;
	}

	public void setFechaVigor(String fechaVigor) {
		this.fechaVigor = fechaVigor;
	}

	public String getEnVigor() {
		return enVigor;
	}

	public void setEnVigor(String enVigor) {
		this.enVigor = enVigor;
	}

	public BigInteger getTipoReduccion() {
		return tipoReduccion;
	}

	public void setTipoReduccion(BigInteger tipoReduccion) {
		this.tipoReduccion = tipoReduccion;
	}

	public BigDecimal getReduccionProducion() {
		return reduccionProducion;
	}

	public void setReduccionProducion(BigDecimal reduccionProducion) {
		this.reduccionProducion = reduccionProducion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
