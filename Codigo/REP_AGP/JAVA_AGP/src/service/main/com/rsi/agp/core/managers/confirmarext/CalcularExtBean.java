package com.rsi.agp.core.managers.confirmarext;

import java.io.Serializable;

import org.w3._2005._05.xmlmime.Base64Binary;

public class CalcularExtBean implements Serializable {

	private static final long serialVersionUID = -7230380760545673629L;

	private int codigo;
	private String mensaje;
	private AgrError[] agrErrors;
	private Base64Binary acuseRecibo;
	private Base64Binary calculo;

	public CalcularExtBean() {
		super();
		this.codigo = -1;
		this.agrErrors = null;
		this.acuseRecibo = null;
		this.mensaje = null;
		this.calculo = null;
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

	public void setAgrErrors(final AgrError[] agrErrors) {
		this.agrErrors = agrErrors;
	}

	public Base64Binary getAcuseRecibo() {
		return acuseRecibo;
	}

	public void setAcuseRecibo(final Base64Binary acuseRecibo) {
		this.acuseRecibo = acuseRecibo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	public Base64Binary getCalculo() {
		return calculo;
	}

	public void setCalculo(final Base64Binary calculo) {
		this.calculo = calculo;
	}


	public static class AgrError implements Serializable {

		private static final long serialVersionUID = -6416332314166329627L;
		
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
