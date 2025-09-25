package com.rsi.agp.vo;

public class ProduccionVO {

	private String codModulo = "";
	private String desModulo = "";
	private String limMin = "";
	private String limMax = "";
	private String rdtoMin = "";
	private String rdtoMax = "";
	private String produccion = "";

	public ProduccionVO() {
		super();
	}

	public String getLimMin() {
		return limMin;
	}

	public void setLimMin(String limMin) {
		this.limMin = limMin;
	}

	public String getLimMax() {
		return limMax;
	}

	public void setLimMax(String limMax) {
		this.limMax = limMax;
	}

	public String getCodModulo() {
		return codModulo;
	}

	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}

	public String getRdtoMin() {
		return rdtoMin;
	}

	public void setRdtoMin(String rdtoMin) {
		this.rdtoMin = rdtoMin;
	}

	public String getRdtoMax() {
		return rdtoMax;
	}

	public void setRdtoMax(String rdtoMax) {
		this.rdtoMax = rdtoMax;
	}

	public String getDesModulo() {
		return desModulo;
	}

	public void setDesModulo(String desModulo) {
		this.desModulo = desModulo;
	}

	public String getProduccion() {
		return produccion;
	}

	public void setProduccion(String produccion) {
		this.produccion = produccion;
	}
}