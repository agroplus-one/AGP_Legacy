package com.rsi.agp.dao.tables.comisiones;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;

public class FormFicheroComisionesBean implements Serializable{
	
		private MultipartFile file = null;
		private String nombreFichero = null;
		private Date fechaCarga;
		private Date fechaAceptacion;
		private Date fechaCierre;
		private Date fechaEmision;
		private BigDecimal plan;
		private String fase;
		private String estado;
		private ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSituacion = new ReglamentoProduccionEmitidaSituacion();
		
		public MultipartFile getFile() {
			return file;
		}

		public void setFile(MultipartFile file) {
			this.file = file;
		}

		public String getNombreFichero() {
			return nombreFichero;
		}

		public void setNombreFichero(String nombreFichero) {
			this.nombreFichero = nombreFichero;
		}

		public Date getFechaCarga() {
			return fechaCarga;
		}

		public void setFechaCarga(Date fechaCarga) {
			this.fechaCarga = fechaCarga;
		}

		public Date getFechaAceptacion() {
			return fechaAceptacion;
		}

		public void setFechaAceptacion(Date fechaAceptacion) {
			this.fechaAceptacion = fechaAceptacion;
		}

		public Date getFechaCierre() {
			return fechaCierre;
		}

		public void setFechaCierre(Date fechaCierre) {
			this.fechaCierre = fechaCierre;
		}

		public Date getFechaEmision() {
			return fechaEmision;
		}

		public void setFechaEmision(Date fechaEmision) {
			this.fechaEmision = fechaEmision;
		}

		public ReglamentoProduccionEmitidaSituacion getReglamentoProduccionEmitidaSituacion() {
			return reglamentoProduccionEmitidaSituacion;
		}

		public void setReglamentoProduccionEmitidaSituacion(
				ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSituacion) {
			this.reglamentoProduccionEmitidaSituacion = reglamentoProduccionEmitidaSituacion;
		}

		public BigDecimal getPlan() {
			return plan;
		}

		public void setPlan(BigDecimal plan) {
			this.plan = plan;
		}

		public String getFase() {
			return fase;
		}

		public void setFase(String fase) {
			this.fase = fase;
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

}
