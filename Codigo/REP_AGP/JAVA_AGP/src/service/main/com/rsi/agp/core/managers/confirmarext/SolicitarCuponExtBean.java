package com.rsi.agp.core.managers.confirmarext;

import java.io.Serializable;

import org.w3._2005._05.xmlmime.Base64Binary;

public class SolicitarCuponExtBean implements Serializable {

	private static final long serialVersionUID = -7230380760545673629L;

	private int codigo;
	private String mensaje;
	private AgrError[] agrErrors;
	private Base64Binary poliza;
	private Base64Binary polizaComp;
	private Base64Binary polizaRC;
	private Base64Binary polizaCompRC;
	private Base64Binary estadoContratacion;
	private Base64Binary cuponModificacion;

	public SolicitarCuponExtBean() {
		super();
		this.codigo = -1;
		this.agrErrors = null;
		this.poliza = null;
		this.polizaComp = null;
		this.polizaRC = null;
		this.polizaCompRC = null;
		this.estadoContratacion = null;
		this.cuponModificacion = null;
		this.mensaje = null;
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

	public Base64Binary getPoliza() {
		return poliza;
	}

	public void setPoliza(Base64Binary poliza) {
		this.poliza = poliza;
	}

	public Base64Binary getPolizaComp() {
		return polizaComp;
	}

	public void setPolizaComp(Base64Binary polizaComp) {
		this.polizaComp = polizaComp;
	}

	public Base64Binary getPolizaRC() {
		return polizaRC;
	}

	public void setPolizaRC(Base64Binary polizaRC) {
		this.polizaRC = polizaRC;
	}
	
	public Base64Binary getPolizaCompRC() {
		return polizaCompRC;
	}

	public void setPolizaCompRC(Base64Binary polizaCompRC) {
		this.polizaCompRC = polizaCompRC;
	}

	public Base64Binary getEstadoContratacion() {
		return estadoContratacion;
	}

	public void setEstadoContratacion(Base64Binary estadoContratacion) {
		this.estadoContratacion = estadoContratacion;
	}

	public Base64Binary getCuponModificacion() {
		return cuponModificacion;
	}

	public void setCuponModificacion(Base64Binary cuponModificacion) {
		this.cuponModificacion = cuponModificacion;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public static class AgrError implements Serializable {

		private static final long serialVersionUID = 6147514431256394399L;

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
