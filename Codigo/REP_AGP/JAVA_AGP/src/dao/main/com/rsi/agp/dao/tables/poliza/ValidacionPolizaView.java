package com.rsi.agp.dao.tables.poliza;

import java.math.BigDecimal;

public class ValidacionPolizaView 
{
	private BigDecimal tipoError;
	private String descError;
	private String ruta;
	public BigDecimal getTipoError() {
		return tipoError;
	}
	public void setTipoError(BigDecimal tipoError) {
		this.tipoError = tipoError;
	}
	public String getDescError() {
		return descError;
	}
	public void setDescError(String descError) {
		this.descError = descError;
	}
	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	
	
}
