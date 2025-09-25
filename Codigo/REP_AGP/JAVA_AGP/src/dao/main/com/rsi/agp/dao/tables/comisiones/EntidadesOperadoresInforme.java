package com.rsi.agp.dao.tables.comisiones;

import java.math.BigDecimal;

public class EntidadesOperadoresInforme {

	private BigDecimal fases;
	private BigDecimal cooperativas;
	private BigDecimal impagados;
	private BigDecimal codEntMed;
	private BigDecimal codSubMed;
	private String pagoDirecto;
	public BigDecimal getFases() {
		return fases;
	}
	public void setFases(BigDecimal fases) {
		this.fases = fases;
	}
	public BigDecimal getCooperativas() {
		return cooperativas;
	}
	public void setCooperativas(BigDecimal cooperativas) {
		this.cooperativas = cooperativas;
	}
	public BigDecimal getImpagados() {
		return impagados;
	}
	public void setImpagados(BigDecimal impagados) {
		this.impagados = impagados;
	}
	public BigDecimal getCodEntMed() {
		return codEntMed;
	}
	public void setCodEntMed(BigDecimal codEntMed) {
		this.codEntMed = codEntMed;
	}
	public BigDecimal getCodSubMed() {
		return codSubMed;
	}
	public void setCodSubMed(BigDecimal codSubMed) {
		this.codSubMed = codSubMed;
	}
	public String getPagoDirecto() {
		return pagoDirecto;
	}
	public void setPagoDirecto(String pagoDirecto) {
		this.pagoDirecto = pagoDirecto;
	}
}
