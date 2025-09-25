package com.rsi.agp.core.report.layout;

import java.util.ArrayList;
import java.util.List;

public class BeanTablaCoberturas {
	
	private String garantia = "";
	private String riesgosCubiertos = "";
	
	private List<String> celdas = new ArrayList<String>();

	public String getGarantia(){
		return this.garantia;
	}
	
	public void setGarantia(String garantia){
		this.garantia = garantia;
	}
	
	public String getRiesgosCubiertos(){
		return this.riesgosCubiertos;
	}
	
	public void setRiesgosCubiertos(String riesgosCubiertos){
		this.riesgosCubiertos = riesgosCubiertos;
	}

	public List<String> getCeldas() {
		return celdas;
	}

	public void setCeldas(List<String> celdas) {
		this.celdas = celdas;
	}
}