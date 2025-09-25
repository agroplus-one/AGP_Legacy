package com.rsi.agp.core.report.rcganado;

public class ExplotacionInforme {
	private String rega;
	private String especie;
	private String regimen;
	private String numAnimales;
	
	public ExplotacionInforme(String rega, String especie, String regimen,
			String numAnimales) {
		super();
		this.rega = rega;
		this.especie = especie;
		this.regimen = regimen;
		this.numAnimales = numAnimales;
	}

	public String getRega() {
		return rega;
	}

	public void setRega(String rega) {
		this.rega = rega;
	}

	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public String getRegimen() {
		return regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public String getNumAnimales() {
		return numAnimales;
	}

	public void setNumAnimales(String numAnimales) {
		this.numAnimales = numAnimales;
	}
}
