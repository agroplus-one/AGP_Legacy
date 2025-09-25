package com.rsi.agp.core.managers.confirmarext;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfirmarExtPolizaBean {

	private int linea;
	private int plan;
	private String referencia;
	private String codigoInternoEntidad;
	private Map<String, ConfirmarExtGCBean> comisiones;
	private Calendar fechaPago;
	private Calendar fechaFirma;

	public ConfirmarExtPolizaBean() {
		super();
		comisiones = new HashMap<String, ConfirmarExtGCBean>();
	}

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	public String getCodigoInternoEntidad() {
		return codigoInternoEntidad;
	}

	public void setCodigoInternoEntidad(String codigoInternoEntidad) {
		this.codigoInternoEntidad = codigoInternoEntidad;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public Calendar getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Calendar fechaPago) {
		this.fechaPago = fechaPago;
	}
	
	public Calendar getFechaFirma() {
		return fechaFirma;
	}

	public void setFechaFirma(Calendar fechaFirma) {
		this.fechaFirma = fechaFirma;
	}

	public int getPlan() {
		return plan;
	}

	public void setPlan(int plan) {
		this.plan = plan;
	}

	public Map<String, ConfirmarExtGCBean> getComisiones() {
		return comisiones;
	}

	public void setComisiones(Map<String, ConfirmarExtGCBean> comisiones) {
		this.comisiones = comisiones;
	}
}
