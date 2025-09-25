package com.rsi.agp.core.managers.impl.anexoMod.impresion;

import java.util.Date;

public class ImpresionIncidenciasModBean implements java.io.Serializable {
	
	private static final long serialVersionUID = -5086949454611799816L;
	
	private int anio;
	private int numeroInc;
	private String asunto;
	private String codAsunto;
	private String estado;
	private String descEstado;
	private Date fechaEstado;
	private String codigoDocAfectado;
	private String descDocAfectado;
	private String tipoPoliza;
	private String idEnvio;
	private String error;
	private String referencia;
	private int numDocumentos;	
	
	public String getCodAsunto() {
		return codAsunto;
	}
	public void setCodAsunto(String codAsunto) {
		this.codAsunto = codAsunto;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public int getNumDocumentos() {
		return numDocumentos;
	}
	public void setNumDocumentos(int numDocumentos) {
		this.numDocumentos = numDocumentos;
	}
	public int getAnio() {
		return anio;
	}
	public void setAnio(int anio) {
		this.anio = anio;
	}
	public int getNumeroInc() {
		return numeroInc;
	}
	public void setNumeroInc(int numeroInc) {
		this.numeroInc = numeroInc;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getDescEstado() {
		return descEstado;
	}
	public void setDescEstado(String descEstado) {
		this.descEstado = descEstado;
	}
	public Date getFechaEstado() {
		return fechaEstado;
	}
	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}
	public String getCodigoDocAfectado() {
		return codigoDocAfectado;
	}
	public void setCodigoDocAfectado(String codigoDocAfectado) {
		this.codigoDocAfectado = codigoDocAfectado;
	}
	public String getDescDocAfectado() {
		return descDocAfectado;
	}
	public void setDescDocAfectado(String descDocAfectado) {
		this.descDocAfectado = descDocAfectado;
	}
	public String getTipoPoliza() {
		return tipoPoliza;
	}
	public void setTipoPoliza(String tipoPoliza) {
		this.tipoPoliza = tipoPoliza;
	}
	public String getIdEnvio() {
		return idEnvio;
	}
	public void setIdEnvio(String idEnvio) {
		this.idEnvio = idEnvio;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	

}

