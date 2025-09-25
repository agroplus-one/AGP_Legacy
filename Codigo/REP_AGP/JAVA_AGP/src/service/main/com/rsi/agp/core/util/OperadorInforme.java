package com.rsi.agp.core.util;

public class OperadorInforme {
	
	private String codigo;
	private String descripcion;
	private boolean tipoNumerico;
	
	public OperadorInforme (String codigo, String descripcion, boolean tipoNumerico) {
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.tipoNumerico = tipoNumerico;
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
	public boolean isTipoNumerico() {
		return tipoNumerico;
	}

	public void setTipoNumerico(boolean tipoNumerico) {
		this.tipoNumerico = tipoNumerico;
	}

}
