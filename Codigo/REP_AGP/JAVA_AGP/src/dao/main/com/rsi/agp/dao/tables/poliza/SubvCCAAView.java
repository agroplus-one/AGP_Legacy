package com.rsi.agp.dao.tables.poliza;

import java.math.BigDecimal;

public class SubvCCAAView 
{
	private String codOrganismo;
	private String descOrganismo;
	private BigDecimal importe;
	
	public String getCodOrganismo() {
		return codOrganismo;
	}
	public void setCodOrganismo(String codOrganismo) {
		this.codOrganismo = codOrganismo;
	}
	public String getDescOrganismo() {
		return descOrganismo;
	}
	public void setDescOrganismo(String descOrganismo) {
		this.descOrganismo = descOrganismo;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}	
}
