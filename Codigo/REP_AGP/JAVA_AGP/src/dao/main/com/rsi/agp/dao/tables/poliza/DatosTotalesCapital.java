package com.rsi.agp.dao.tables.poliza;

import java.math.BigDecimal;

public class DatosTotalesCapital {
	
	private String descripcion;
	private BigDecimal hoja;
	private BigDecimal totalProduccion;
	private BigDecimal totalValorAsegurable;
	private BigDecimal totalSuperficie;
	
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public BigDecimal getHoja() {
		return hoja;
	}
	public void setHoja(BigDecimal hoja) {
		this.hoja = hoja;
	}
	public BigDecimal getTotalProduccion() {
		return totalProduccion;
	}
	public void setTotalProduccion(BigDecimal totalProduccion) {
		this.totalProduccion = totalProduccion;
	}
	public BigDecimal getTotalValorAsegurable() {
		return totalValorAsegurable;
	}
	public void setTotalValorAsegurable(BigDecimal totalValorAsegurable) {
		this.totalValorAsegurable = totalValorAsegurable;
	}
	public BigDecimal getTotalSuperficie() {
		return totalSuperficie;
	}
	public void setTotalSuperficie(BigDecimal totalSuperficie) {
		this.totalSuperficie = totalSuperficie;
	}
	

}
