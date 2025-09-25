package com.rsi.agp.core.managers.confirmarext;

import java.io.Serializable;

import org.w3._2005._05.xmlmime.Base64Binary;

public class AnularCuponExtBean implements Serializable {

	private static final long serialVersionUID = -7230380760545673629L;

	private int codigo;
	private String mensaje;
	private AgrError[] agrErrors;
	private Base64Binary respuesta;

	public AnularCuponExtBean() {
		super();
		this.codigo = -1;
		this.agrErrors = null;
		this.mensaje = null;
		this.respuesta = null;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(final int codigo) {
		this.codigo = codigo;
	}

	public AgrError[] getAgrErrors() {
		return agrErrors;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Base64Binary getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(Base64Binary respuesta) {
		this.respuesta = respuesta;
	}

	public static class AgrError implements Serializable {

		private static final long serialVersionUID = 7181726352678607876L;

		private int codigo;
		private String mensaje;

		public AgrError() {
			super();
			this.codigo = -1;
			this.mensaje = "";
		}

		public int getCodigo() {
			return codigo;
		}

		public void setCodigo(final int codigo) {
			this.codigo = codigo;
		}

		public String getMensaje() {
			return mensaje;
		}

		public void setMensaje(final String mensaje) {
			this.mensaje = mensaje;
		}
	}

}
