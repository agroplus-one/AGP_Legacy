package com.rsi.agp.vo;

public class ComboDataVO {

	private String id = "";
	private String descripcion = "";
	private String codconcepto = "";
	private String nomconcepto = "";

	public String getNomconcepto() {
		return nomconcepto;
	}

	public void setNomconcepto(String nomconcepto) {
		this.nomconcepto = nomconcepto;
	}

	public String getCodconcepto() {
		return codconcepto;
	}

	public void setCodconcepto(String codconcepto) {
		this.codconcepto = codconcepto;
	}

	public ComboDataVO() {

	}

	public ComboDataVO(String id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
