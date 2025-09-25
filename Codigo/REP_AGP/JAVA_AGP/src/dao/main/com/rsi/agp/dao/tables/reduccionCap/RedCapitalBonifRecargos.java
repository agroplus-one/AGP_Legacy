package com.rsi.agp.dao.tables.reduccionCap;

import java.math.BigDecimal;

public class RedCapitalBonifRecargos implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private RedCapitalBonifRecargosId id;
	private RedCapitalDistribucionCostes redCapitalDistribucionCostes;
	private BigDecimal importe;
	private String descripcion;

	public RedCapitalBonifRecargos() {
	}

	public RedCapitalBonifRecargos(RedCapitalBonifRecargosId id,
			RedCapitalDistribucionCostes redCapitalDistribucionCostes, BigDecimal importe) {
		this.id = id;
		this.redCapitalDistribucionCostes = redCapitalDistribucionCostes;
		this.importe = importe;
	}

	public RedCapitalBonifRecargosId getId() {
		return this.id;
	}

	public void setId(RedCapitalBonifRecargosId id) {
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
