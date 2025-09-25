package com.rsi.agp.core.managers.confirmarext;

import java.io.Serializable;

import org.w3._2005._05.xmlmime.Base64Binary;

public class CalcularAnexoExtBean implements Serializable {

	private static final long serialVersionUID = -7230380760545673629L;

	private int codigo;
	private String mensaje;
	private transient AgrError[] agrErrors;
	private Base64Binary acuseRecibo;
	private Base64Binary calculoModificacion;
	private Base64Binary calculoOriginal;
	private Base64Binary diferenciasCoste;	

	public CalcularAnexoExtBean() {
		super();
		this.codigo = -1;
		this.agrErrors = null;
		this.acuseRecibo = null;
		this.mensaje = null;
		this.calculoModificacion = null;
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
	
	public Base64Binary getCalculoModificacion() {
		return calculoModificacion;
	}

	public void setCalculoModificacion(final Base64Binary calculoModificacion) {
		this.calculoModificacion = calculoModificacion;
	}


	public Base64Binary getCalculoOriginal() {
		return calculoOriginal;
	}

	public void setCalculoOriginal(Base64Binary calculoOriginal) {
		this.calculoOriginal = calculoOriginal;
	}


	public Base64Binary getDiferenciasCoste() {
		return diferenciasCoste;
	}

	public void setDiferenciasCoste(Base64Binary diferenciasCoste) {
		this.diferenciasCoste = diferenciasCoste;
	}


	public static class AgrError {

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
