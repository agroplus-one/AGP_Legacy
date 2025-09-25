package com.rsi.agp.vo;

public class SigpacVO {

	private String prov = "";
	private String term = "";
	private String agr = "";
	private String zona = "";
	private String pol = "";
	private String parc = "";
	private String lineaseguroid = "";
	private String codLinea = "";
	private String codCultivo = "";
	private String codPlan = "";
	private String recinto = "";

	public SigpacVO() {
		super();
	}

	public String getProv() {
		return prov;
	}

	public String getCodLinea() {
		return codLinea;
	}

	public void setCodLinea(String codLinea) {
		this.codLinea = codLinea;
	}

	public String getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(String lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getAgr() {
		return agr;
	}

	public void setAgr(String agr) {
		this.agr = agr;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public String getPol() {
		return pol;
	}

	public void setPol(String pol) {
		this.pol = pol;
	}

	public String getParc() {
		return parc;
	}

	public void setParc(String parc) {
		this.parc = parc;
	}

	public String getCodCultivo() {
		return codCultivo;
	}

	public void setCodCultivo(String codCultivo) {
		this.codCultivo = codCultivo;
	}

	public String getCodPlan() {
		return codPlan;
	}

	public void setCodPlan(String codPlan) {
		this.codPlan = codPlan;
	}
	
	public String getRecinto() {
		return recinto;
	}

	public void setRecinto(String recinto) {
		this.recinto = recinto;
	}
}
