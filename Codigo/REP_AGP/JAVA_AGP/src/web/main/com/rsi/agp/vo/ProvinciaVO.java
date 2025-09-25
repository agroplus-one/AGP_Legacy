package com.rsi.agp.vo;

public class ProvinciaVO {
	private int codProvincia = 0;
	private String nomProvincia = "";

	public ProvinciaVO() {

	}

	public ProvinciaVO(int codProvincia, String nomProvincia) {
		super();
		this.codProvincia = codProvincia;
		this.nomProvincia = nomProvincia;
	}

	public int getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(int codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getNomProvincia() {
		return nomProvincia;
	}

	public void setNomProvincia(String nomProvincia) {
		this.nomProvincia = nomProvincia;
	}
}
