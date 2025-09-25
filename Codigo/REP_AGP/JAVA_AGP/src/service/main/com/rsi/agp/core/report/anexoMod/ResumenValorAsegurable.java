package com.rsi.agp.core.report.anexoMod;

import java.math.BigDecimal;

/**
 * Objeto para mostrar el subinforme con el resumen del valor asegurable
 * @author u028783
 *
 */
public class ResumenValorAsegurable {
	
	private String descripcion;
	private Integer numParcelas;
	private BigDecimal valor;
	private String unidadMedida;
	private BigDecimal superficie;
	private BigDecimal valorAsegurable;
	private Boolean isNegrita;

	public ResumenValorAsegurable() {
		this.descripcion = "";
		this.numParcelas = 0;
		this.valor = new BigDecimal(0);
		this.unidadMedida = "";
		this.superficie = new BigDecimal(0);
		this.valorAsegurable = new BigDecimal(0);
		
	}
	
	public ResumenValorAsegurable(String descripcion) {
		this.descripcion = descripcion;
		this.numParcelas = 0;
		this.valor = new BigDecimal(0);
		this.unidadMedida = "";
		this.superficie = new BigDecimal(0);
		this.valorAsegurable = new BigDecimal(0);
	}


	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getNumParcelas() {
		return numParcelas;
	}

	public void setNumParcelas(Integer numParcelas) {
		this.numParcelas = numParcelas;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	public BigDecimal getSuperficie() {
		return superficie;
	}

	public void setSuperficie(BigDecimal superficie) {
		this.superficie = superficie;
	}

	public BigDecimal getValorAsegurable() {
		return valorAsegurable;
	}

	public void setValorAsegurable(BigDecimal valorAsegurable) {
		this.valorAsegurable = valorAsegurable;
	}

	public Boolean getIsNegrita() {
		return isNegrita;
	}

	public void setIsNegrita(Boolean isNegrita) {
		this.isNegrita = isNegrita;
	}

	

}
