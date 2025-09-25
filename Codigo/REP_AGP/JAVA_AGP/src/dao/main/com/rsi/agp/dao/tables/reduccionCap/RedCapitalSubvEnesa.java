package com.rsi.agp.dao.tables.reduccionCap;

import java.math.BigDecimal;

public class RedCapitalSubvEnesa implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private RedCapitalSubvEnesaId id;
	private RedCapitalDistribucionCostes redCapitalDistribucionCostes;
	private BigDecimal importe;
	private String descripcion;

	public RedCapitalSubvEnesa() {
	}

	public RedCapitalSubvEnesa(RedCapitalSubvEnesaId id, RedCapitalDistribucionCostes redCapitalDistribucionCostes,
			BigDecimal importe) {
		this.id = id;
		this.redCapitalDistribucionCostes = redCapitalDistribucionCostes;
		this.importe = importe;
	}

	public RedCapitalSubvEnesaId getId() {
		return this.id;
	}

	public void setId(RedCapitalSubvEnesaId id) {
		this.id = id;
	}

	public RedCapitalDistribucionCostes getRedCapitalDistribucionCostes() {
		return this.redCapitalDistribucionCostes;
	}

	public void setRedCapitalDistribucionCostes(RedCapitalDistribucionCostes redCapitalDistribucionCostes) {
		this.redCapitalDistribucionCostes = redCapitalDistribucionCostes;
	}

	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}