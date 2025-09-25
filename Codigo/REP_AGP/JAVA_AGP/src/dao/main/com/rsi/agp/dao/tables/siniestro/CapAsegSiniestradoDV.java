package com.rsi.agp.dao.tables.siniestro;

import java.io.Serializable;
import java.util.Date;

public class CapAsegSiniestradoDV implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private CapAsegSiniestro capAsegSiniestro;
	private boolean frutos;
	private Date fechaRecoleccion;
	
	public CapAsegSiniestradoDV(CapAsegSiniestro capAsegSiniestro,boolean frutos, Date fechaRecoleccion) {		
		this.capAsegSiniestro = capAsegSiniestro;
		this.frutos = frutos;
		this.fechaRecoleccion = fechaRecoleccion;
	}

	public CapAsegSiniestradoDV() {	
		this.capAsegSiniestro = new CapAsegSiniestro();
	}

	public CapAsegSiniestro getCapAsegSiniestro() {
		return capAsegSiniestro;
	}

	public void setCapAsegSiniestro(CapAsegSiniestro capAsegSiniestro) {
		this.capAsegSiniestro = capAsegSiniestro;
	}

	public boolean isFrutos() {
		return frutos;
	}

	public void setFrutos(boolean frutos) {
		this.frutos = frutos;
	}

	public Date getFechaRecoleccion() {
		return fechaRecoleccion;
	}

	public void setFechaRecoleccion(Date fechaRecoleccion) {
		this.fechaRecoleccion = fechaRecoleccion;
	}

	
	
}
