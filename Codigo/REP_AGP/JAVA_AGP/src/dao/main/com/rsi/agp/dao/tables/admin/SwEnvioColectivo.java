package com.rsi.agp.dao.tables.admin;

import java.sql.Clob;
import java.util.Date;

public class SwEnvioColectivo implements java.io.Serializable {

	private static final long serialVersionUID = -3898637439084249374L;

	private long id;
	private long idColectivo;
	private Date fecha;
	private String codUsuario;
	private transient Clob xmlEnvio;
	private transient Clob xmlRespuesta;

	public SwEnvioColectivo() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdColectivo() {
		return idColectivo;
	}

	public void setIdColectivo(long idColectivo) {
		this.idColectivo = idColectivo;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getCodUsuario() {
		return codUsuario;
	}

	public void setCodUsuario(String codUsuario) {
		this.codUsuario = codUsuario;
	}

	public Clob getXmlEnvio() {
		return xmlEnvio;
	}

	public void setXmlEnvio(Clob xmlEnvio) {
		this.xmlEnvio = xmlEnvio;
	}

	public Clob getXmlRespuesta() {
		return xmlRespuesta;
	}

	public void setXmlRespuesta(Clob xmlRespuesta) {
		this.xmlRespuesta = xmlRespuesta;
	}
}