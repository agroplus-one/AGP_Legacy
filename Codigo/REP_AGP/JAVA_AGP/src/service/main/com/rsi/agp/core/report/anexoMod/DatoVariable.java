package com.rsi.agp.core.report.anexoMod;

public class DatoVariable {
	
	private String nombreConcepto;
	private String valor;
	private String descripcion;
	
	public DatoVariable(){
		super();
	}

	public String getNombreConcepto() {
		return nombreConcepto;
	}

	public void setNombreConcepto(String nombreConcepto) {
		this.nombreConcepto = nombreConcepto;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
