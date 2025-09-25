package com.rsi.agp.core.report.anexoMod;

import java.math.BigDecimal;

public class ResumenCosechaAsegurada {
	
	private Integer codprovincia;
	private Integer codcomarca;
	private Integer codtermino;
	private Integer codcultivo;
	private Integer codvariedad;
	private BigDecimal superficie;
	private BigDecimal produccion;

	public ResumenCosechaAsegurada() {
		
	}

	public ResumenCosechaAsegurada(Integer codprovincia, Integer codcomarca,
			Integer codtermino, Integer codcultivo, Integer codvariedad,
			BigDecimal superficie, BigDecimal produccion) {
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.superficie = superficie;
		this.produccion = produccion;
	}

	public Integer getCodprovincia() {
		return codprovincia;
	}

	public void setCodprovincia(Integer codprovincia) {
		this.codprovincia = codprovincia;
	}

	public Integer getCodcomarca() {
		return codcomarca;
	}

	public void setCodcomarca(Integer codcomarca) {
		this.codcomarca = codcomarca;
	}

	public Integer getCodtermino() {
		return codtermino;
	}

	public void setCodtermino(Integer codtermino) {
		this.codtermino = codtermino;
	}

	public Integer getCodcultivo() {
		return codcultivo;
	}

	public void setCodcultivo(Integer codcultivo) {
		this.codcultivo = codcultivo;
	}

	public Integer getCodvariedad() {
		return codvariedad;
	}

	public void setCodvariedad(Integer codvariedad) {
		this.codvariedad = codvariedad;
	}

	public BigDecimal getSuperficie() {
		return superficie;
	}

	public void setSuperficie(BigDecimal superficie) {
		this.superficie = superficie;
	}

	public BigDecimal getProduccion() {
		return produccion;
	}

	public void setProduccion(BigDecimal produccion) {
		this.produccion = produccion;
	}
	
}
