package com.rsi.agp.core.report.rcganado.bean;

public class Explotacion {
	private String rega = null;
	private String especie = null;
	private String regimen = null;
	private int numAnimales = 0;
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
	public int getNumAnimales() {
		return numAnimales;
	}
	public void setNumAnimales(int numAnimales) {
		this.numAnimales = numAnimales;
	}
	public Explotacion(String rega, String especie, String regimen,
			int numAnimales) {
		this.rega = rega;
		this.especie = especie;
		this.regimen = regimen;
		this.numAnimales = numAnimales;
	}
	
}
