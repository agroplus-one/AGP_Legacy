package com.rsi.agp.dao.tables.importacion;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;

public class AuditoriaAnexosExt implements java.io.Serializable {

	private static final long serialVersionUID = -1513694695823737353L;

	public static final String SERV_SC = "SC"; // Solicitud cupon
	public static final String SERV_CA = "CA"; // Confirmacion anexo
	public static final String SERV_AC = "AC"; // Anulacion cupon

	private long id;
	private String codigoInterno;
	private Date horaLlamada;
	private String referencia;
	private BigDecimal plan;
	private String idCupon;
	private transient Clob acuseRecibo;
	private transient Clob poliza;
	private transient Clob polizaComp;
	private transient Clob estadoCont;
	private transient Clob cuponModificacion;
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

	public Clob getAcuseRecibo() {
		return acuseRecibo;
	}

	public void setAcuseRecibo(Clob acuseRecibo) {
		this.acuseRecibo = acuseRecibo;
	}

	public Clob getPoliza() {
		return poliza;
	}

	public void setPoliza(Clob poliza) {
		this.poliza = poliza;
	}

	public Clob getPolizaComp() {
		return polizaComp;
	}

	public void setPolizaComp(Clob polizaComp) {
		this.polizaComp = polizaComp;
	}

	public Clob getEstadoCont() {
		return estadoCont;
	}

	public void setEstadoCont(Clob estadoCont) {
		this.estadoCont = estadoCont;
	}

	public Clob getCuponModificacion() {
		return cuponModificacion;
	}

	public void setCuponModificacion(Clob cuponModificacion) {
		this.cuponModificacion = cuponModificacion;
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
}