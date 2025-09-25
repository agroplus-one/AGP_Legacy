package com.rsi.agp.vo;

public class SubterminoVO {
	// Provincia
	private int codProvincia = 0;
	private String nomProvincia = "";
	// Comarca
	private int codComarca = 0;
	private String nomComarca = "";
	// Comarca
	private int codTermino = 0;
	private String nomTermino = "";
	// Subtermino
	private String subTermino = "";
	private int codPostal = 0;

	public SubterminoVO() {
		super();
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

	public int getCodComarca() {
		return codComarca;
	}

	public void setCodComarca(int codComarca) {
		this.codComarca = codComarca;
	}

	public String getNomComarca() {
		return nomComarca;
	}

	public void setNomComarca(String nomComarca) {
		this.nomComarca = nomComarca;
	}

	public int getCodTermino() {
		return codTermino;
	}

	public void setCodTermino(int codTermino) {
		this.codTermino = codTermino;
	}

	public String getNomTermino() {
		return nomTermino;
	}

	public void setNomTermino(String nomTermino) {
		this.nomTermino = nomTermino;
	}

	public String getSubTermino() {
		return subTermino;
	}

	public void setSubTermino(String subTermino) {
		this.subTermino = subTermino;
	}

	public int getCodPostal() {
		return codPostal;
	}

	public void setCodPostal(int codPostal) {
		this.codPostal = codPostal;
	}
}
