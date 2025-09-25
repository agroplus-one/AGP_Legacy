package com.rsi.agp.dao.tables.poliza;

import java.math.BigDecimal;

public class SubvEnesaView 
{
	private BigDecimal tipoSubvEnesa;
	private String descSubvencion;
	private BigDecimal importe;
	
	public BigDecimal getTipoSubvEnesa() {
		return tipoSubvEnesa;
	}
	public void setTipoSubvEnesa(BigDecimal tipoSubvEnesa) {
		this.tipoSubvEnesa = tipoSubvEnesa;
	}
	public String getDescSubvencion() {
		return descSubvencion;
	}
	public void setDescSubvencion(String descSubvencion) {
		this.descSubvencion = descSubvencion;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}		
}
