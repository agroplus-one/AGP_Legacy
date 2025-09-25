package com.rsi.agp.dao.tables.importacion;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;

public class AuditoriaConfirmacionExt implements java.io.Serializable {

	private static final long serialVersionUID = -1513694695823737353L;

	private long id;
	private String codigoInterno;
	private Date horaLlamada;
	private transient Clob entrada;
	private transient Clob salida;
	private BigDecimal resultado;
	private String mensaje;

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

	public Clob getEntrada() {
		return entrada;
	}

	public void setEntrada(Clob entrada) {
		this.entrada = entrada;
	}

	public Clob getSalida() {
		return salida;
	}

	public void setSalida(Clob salida) {
		this.salida = salida;
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
}