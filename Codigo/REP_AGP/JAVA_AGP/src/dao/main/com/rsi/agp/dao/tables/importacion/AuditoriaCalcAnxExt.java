package com.rsi.agp.dao.tables.importacion;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;

public class AuditoriaCalcAnxExt implements java.io.Serializable {

	private static final long serialVersionUID = -327408788672174868L;

	public static String SERV_CL = "CL"; // Calcular anexo

	private long id;
	private String codigoInterno;
	private Date horaLlamada;
	private String referencia;
	private BigDecimal plan;
	private String tipoPoliza;
	private String idCupon;
	private BigDecimal calcularSituacionActual; 
	private transient Clob polizaModificacion;
	private transient Clob calculoModificacion;
	private transient Clob calculoOriginal;
	private transient Clob diferenciasCoste;
	private BigDecimal resultado;
	private String mensaje;
	private String servicio;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public Date getHoraLlamada() {
		return horaLlamada;
	}

	public void setHoraLlamada(Date horaLlamada) {
		this.horaLlamada = horaLlamada;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public BigDecimal getPlan() {
		return plan;
	}

	public void setPlan(BigDecimal plan) {
		this.plan = plan;
	}

	public String getIdCupon() {
		return idCupon;
	}

	public void setIdCupon(String idCupon) {
		this.idCupon = idCupon;
	}

	public BigDecimal getResultado() {
		return resultado;
	}

	public void setResultado(BigDecimal resultado) {
		this.resultado = resultado;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public String getTipoPoliza() {
		return tipoPoliza;
	}

	public void setTipoPoliza(String tipoPoliza) {
		this.tipoPoliza = tipoPoliza;
	}

	public BigDecimal getCalcularSituacionActual() {
		return calcularSituacionActual;
	}

	public void setCalcularSituacionActual(BigDecimal calcularSituacionActual) {
		this.calcularSituacionActual = calcularSituacionActual;
	}

	public Clob getPolizaModificacion() {
		return polizaModificacion;
	}

	public void setPolizaModificacion(Clob polizaModificacion) {
		this.polizaModificacion = polizaModificacion;
	}

	public Clob getCalculoModificacion() {
		return calculoModificacion;
	}

	public void setCalculoModificacion(Clob calculoModificacion) {
		this.calculoModificacion = calculoModificacion;
	}

	public Clob getCalculoOriginal() {
		return calculoOriginal;
	}

	public void setCalculoOriginal(Clob calculoOriginal) {
		this.calculoOriginal = calculoOriginal;
	}

	public Clob getDiferenciasCoste() {
		return diferenciasCoste;
	}

	public void setDiferenciasCoste(Clob diferenciasCoste) {
		this.diferenciasCoste = diferenciasCoste;
	}
}