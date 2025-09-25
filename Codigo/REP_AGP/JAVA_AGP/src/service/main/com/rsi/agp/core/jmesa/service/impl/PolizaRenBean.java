package com.rsi.agp.core.jmesa.service.impl;


import java.io.Serializable;

public class PolizaRenBean implements Serializable{	

		private String plan;
		private String linea;
		private String referencia;
		private String modulo;
		private String idColectivo;
		private String nifAsegurado;
		private String descripcion;
		
		public PolizaRenBean(final String plan, final String linea,
				final String referencia, final String modulo, final String descripcion) {

			this.plan = plan;
			this.linea = linea;
			this.referencia = referencia;
			this.modulo = modulo;
			this.descripcion = descripcion;
		}

		
		public void setPlan(String plan) {
			this.plan = plan;
		}


		public void setLinea(String linea) {
			this.linea = linea;
		}


		public void setReferencia(String referencia) {
			this.referencia = referencia;
		}


		public void setModulo(String modulo) {
			this.modulo = modulo;
		}


		public PolizaRenBean() {
		}


		public String getPlan() {
			return plan;
		}

		public String getLinea() {
			return linea;
		}

		public String getReferencia() {
			return referencia;
		}

		public String getModulo() {
			return modulo;
		}
		
		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		
		public String getIdColectivo() {
			return idColectivo;
		}


		public void setIdColectivo(String idColectivo) {
			this.idColectivo = idColectivo;
		}


		public String getNifAsegurado() {
			return nifAsegurado;
		}


		public void setNifAsegurado(String nifAsegurado) {
			this.nifAsegurado = nifAsegurado;
		}


		@Override
		public String toString() {

			StringBuffer sb = new StringBuffer();
			sb.append(this.plan);
			sb.append(" ");
			sb.append(this.linea);
			sb.append(" ");
			sb.append(this.modulo.length() < 2 ? this.modulo + ' ': this.modulo);
			sb.append(" ");
			sb.append(this.referencia);
			sb.append(" ");
			sb.append(this.idColectivo);
			sb.append(" ");
			sb.append(this.nifAsegurado);
			sb.append(" ");
			sb.append(this.descripcion);
			return sb.toString();
		}
}

