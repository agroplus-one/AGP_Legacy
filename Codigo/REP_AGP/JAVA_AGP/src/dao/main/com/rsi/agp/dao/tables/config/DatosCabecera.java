package com.rsi.agp.dao.tables.config;

import java.io.Serializable;

public class DatosCabecera implements Serializable{
	
	private String entidad;
	private String usuario;
	private String asegurado;
	private String nifCif;
	private String clase;
	private String colectivo;
	private String planLinea;
	private String interCoefRdto;
	
	
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getAsegurado() {
		return asegurado;
	}
	public void setAsegurado(String asegurado) {
		this.asegurado = asegurado;
	}
	public String getNifCif() {
		return nifCif;
	}
	public void setNifCif(String nifCif) {
		this.nifCif = nifCif;
	}
	public String getClase() {
		return clase;
	}
	public void setClase(String clase) {
		this.clase = clase;
	}
	public String getColectivo() {
		return colectivo;
	}
	public void setColectivo(String colectivo) {
		this.colectivo = colectivo;
	}
	public String getPlanLinea() {
		return planLinea;
	}
	public void setPlanLinea(String planLinea) {
		this.planLinea = planLinea;
	}
	public String getInterCoefRdto() {
		return interCoefRdto;
	}
	public void setInterCoefRdto(String interCoefRdto) {
		this.interCoefRdto = interCoefRdto;
	}
	
}
