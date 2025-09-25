package com.rsi.agp.dao.tables.mtoinf;

import java.math.BigDecimal;
import java.util.Comparator;

public class CondicionInformeGenericos {
	 private Long id;
	 private BigDecimal informeId;
	 private Long datoInformesId;
     private String campoPermitidoId;
     private String campoCalculadoId;
     private Long idOperadorCondicion;
     private String abreviado;
     private String condicion;
     private BigDecimal idOperador;
     private Boolean esCampoCalculado;
	
	public String getAbreviado() {
		return abreviado;
	}
	public void setAbreviado(String abreviado) {
		this.abreviado = abreviado;
	}
	public String getCondicion() {
		return condicion;
	}
	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	
	public Boolean getEsCampoCalculado() {
		return esCampoCalculado;
	}
	public void setEsCampoCalculado(Boolean esCampoCalculado) {
		this.esCampoCalculado = esCampoCalculado;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getIdOperadorCondicion() {
		return idOperadorCondicion;
	}
	public void setIdOperadorCondicion(Long idOperadorCondicion) {
		this.idOperadorCondicion = idOperadorCondicion;
	}
	public BigDecimal getIdOperador() {
		return idOperador;
	}
	public void setIdOperador(BigDecimal idOperador) {
		this.idOperador = idOperador;
	}
	public String getCampoPermitidoId() {
		return campoPermitidoId;
	}
	public void setCampoPermitidoId(String campoPermitidoId) {
		this.campoPermitidoId = campoPermitidoId;
	}
	public String getCampoCalculadoId() {
		return campoCalculadoId;
	}
	public void setCampoCalculadoId(String campoCalculadoId) {
		this.campoCalculadoId = campoCalculadoId;
	}
	public BigDecimal getInformeId() {
		return informeId;
	}
	public void setInformeId(BigDecimal informeId) {
		this.informeId = informeId;
	}
	public Long getDatoInformesId() {
		return datoInformesId;
	}
	public void setDatoInformesId(Long datoInformesId) {
		this.datoInformesId = datoInformesId;
	}

}
