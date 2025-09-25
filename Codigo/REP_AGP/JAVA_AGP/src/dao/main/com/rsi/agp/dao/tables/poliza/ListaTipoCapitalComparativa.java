package com.rsi.agp.dao.tables.poliza;

import java.util.ArrayList;

public class ListaTipoCapitalComparativa {
	
	private String tipoCapital;
	private String descripcion;
	private ArrayList<TipoCapitalComparativa> tiposCapital;
	
	public String getTipoCapital() {
		return tipoCapital;
	}
	public void setTipoCapital(String tipoCapital) {
		this.tipoCapital = tipoCapital;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public ArrayList<TipoCapitalComparativa> getTiposCapital() {
		return tiposCapital;
	}
	public void setTiposCapital(ArrayList<TipoCapitalComparativa> tiposCapital) {
		this.tiposCapital = tiposCapital;
	}
}
