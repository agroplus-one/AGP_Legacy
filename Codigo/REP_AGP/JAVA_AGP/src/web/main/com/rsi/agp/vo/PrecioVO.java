package com.rsi.agp.vo;

public class PrecioVO {

	private String codModulo = "";
	private String desModulo = "";
	private String limMin = "";
	private String limMax = "";

	public PrecioVO() {
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

	public String getDesModulo() {
		return desModulo;
	}

	public void setDesModulo(String desModulo) {
		this.desModulo = desModulo;
	}
}
