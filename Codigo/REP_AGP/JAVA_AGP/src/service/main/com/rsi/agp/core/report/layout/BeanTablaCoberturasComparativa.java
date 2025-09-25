package com.rsi.agp.core.report.layout;

import java.util.List;

public class BeanTablaCoberturasComparativa {
	
	private String codModulo;
	private String caractExplotacion;
	private List coberturas;
	private List titulos;
	
	public BeanTablaCoberturasComparativa() {
		super();
	}
	
	public String getCodModulo() {
		return codModulo;
	}
	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}
	public String getCaractExplotacion() {
		return caractExplotacion;
	}
	public void setCaractExplotacion(String caractExplotacion) {
		this.caractExplotacion = caractExplotacion;
	}
	public List getCoberturas() {
		return coberturas;
	}
	public void setCoberturas(List coberturas) {
		this.coberturas = coberturas;
	}
	public List getTitulos() {
		return titulos;
	}
	public void setTitulos(List titulos) {
		this.titulos = titulos;
	}
	
	

}
