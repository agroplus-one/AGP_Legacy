package com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre;

import java.math.BigDecimal;
import java.sql.Date;

public class InformeComsFacturacion {

	private Long id;
	private Integer idcierre;
	private Date fechaEmisionRecibo;
	private String grupo;
	private String familia;
	private String codGrupoNegocio;
	private String grupoNegocio;
	private Integer linea;
	private Integer plan;
	private Integer CSB;
	private BigDecimal coste;
	
	public InformeComsFacturacion() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getIdcierre() {
		return idcierre;
	}

	public void setIdcierre(Integer idcierre) {
		this.idcierre = idcierre;
	}

	public Date getFechaEmisionRecibo() {
		return fechaEmisionRecibo;
	}

	public void setFechaEmisionRecibo(Date fechaEmisionRecibo) {
		this.fechaEmisionRecibo = fechaEmisionRecibo;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getFamilia() {
		return familia;
	}

	public void setFamilia(String familia) {
		this.familia = familia;
	}

	public Integer getLinea() {
		return linea;
	}

	public void setLinea(Integer linea) {
		this.linea = linea;
	}

	public Integer getPlan() {
		return plan;
	}

	public void setPlan(Integer plan) {
		this.plan = plan;
	}

	public Integer getCSB() {
		return CSB;
	}

	public void setCSB(Integer cSB) {
		CSB = cSB;
	}

	public BigDecimal getCoste() {
		return coste;
	}

	public void setCoste(BigDecimal coste) {
		this.coste = coste;
	}

	public String getCodGrupoNegocio() {
		return codGrupoNegocio;
	}

	public void setCodGrupoNegocio(String codGrupoNegocio) {
		this.codGrupoNegocio = codGrupoNegocio;
	}

	public String getGrupoNegocio() {
		return grupoNegocio;
	}

	public void setGrupoNegocio(String grupoNegocio) {
		this.grupoNegocio = grupoNegocio;
	}

}
