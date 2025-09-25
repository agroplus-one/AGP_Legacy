package com.rsi.agp.core.report.rcganado.bean;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class Riesgo {
	private List<Explotacion> explotaciones = new ArrayList<Explotacion>();
	private int sumaAsegurada = 0;
	private int franquicia = 0;
	
	public List<Explotacion> getExplotaciones() {
		return explotaciones;
	}
	public void setExplotaciones(List<Explotacion> explotaciones) {
		this.explotaciones = explotaciones;
	}
	public int getSumaAsegurada() {
		return sumaAsegurada;
	}
	public void setSumaAsegurada(int sumaAsegurada) {
		this.sumaAsegurada = sumaAsegurada;
	}
	public int getFranquicia() {
		return franquicia;
	}
	public void setFranquicia(int franquicia) {
		this.franquicia = franquicia;
	}
	
	public void addExplotacion(Explotacion explotacion)   
    {       
        this.explotaciones.add(explotacion);   
    } 
	
}
