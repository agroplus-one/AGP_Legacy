package com.rsi.agp.vo;

public class ItemVO {

	private String codigo = "";
	private String descripcion = "";
	private String valor = "";

	public ItemVO() {
		this.codigo = "";
		this.descripcion = "";
		this.valor = "";
	}

	public ItemVO(String codigo, String descripcion, String valor) {
		super();
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.valor = valor;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}
