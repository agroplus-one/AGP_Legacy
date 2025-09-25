package com.rsi.agp.dao.tables.mtoinf;

import java.math.BigDecimal;

public class FormatoCampoGenerico {
	private BigDecimal idFormato;
	private String nombreFormato;
	
	public BigDecimal getIdFormato() {
		return idFormato;
	}
	public void setIdFormato(BigDecimal idFormato) {
		this.idFormato = idFormato;
	}
	public String getNombreFormato() {
		return nombreFormato;
	}
	public void setNombreFormato(String nombreFormato) {
		this.nombreFormato = nombreFormato;
	}
}
