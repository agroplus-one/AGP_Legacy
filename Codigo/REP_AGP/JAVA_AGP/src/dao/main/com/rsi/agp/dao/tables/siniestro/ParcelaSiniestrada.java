package com.rsi.agp.dao.tables.siniestro;

import java.io.Serializable;
import java.util.Date;

import com.rsi.agp.dao.tables.poliza.Parcela;

public class ParcelaSiniestrada implements Serializable {

	private static final long serialVersionUID = 1L;
	private Parcela parcela;
	private boolean alta;
	private boolean frutos;
	private Date fechaRecoleccion;

	public ParcelaSiniestrada() {
		parcela = new Parcela();
		alta = false;
		frutos = false;
		
	}

	public Parcela getParcela() {
		return parcela;
	}

	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}

	public boolean isAlta() {
		return alta;
	}

	public void setAlta(boolean alta) {
		this.alta = alta;
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
