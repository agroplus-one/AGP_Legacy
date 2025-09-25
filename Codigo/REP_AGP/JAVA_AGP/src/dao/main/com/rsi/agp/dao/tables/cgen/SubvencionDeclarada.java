package com.rsi.agp.dao.tables.cgen;

import java.math.BigDecimal;

public class SubvencionDeclarada {
	
	private BigDecimal codSubvencion;
	private String desSubvencion;

	public SubvencionDeclarada() {
		
	}

	public SubvencionDeclarada(BigDecimal codSubvencion, String desSubvencion) {
		this.codSubvencion = codSubvencion;
		this.desSubvencion = desSubvencion;
	}

	public BigDecimal getCodSubvencion() {
		return codSubvencion;
	}

	public void setCodSubvencion(BigDecimal codSubvencion) {
		this.codSubvencion = codSubvencion;
	}

	public String getDesSubvencion() {
		return desSubvencion;
	}

	public void setDesSubvencion(String desSubvencion) {
		this.desSubvencion = desSubvencion;
	}

}
