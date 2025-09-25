package com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre;

import java.math.BigDecimal;
import java.sql.Date;

public class InformeComsFamLinEnt {

	public InformeComsFamLinEnt() {
	}

	private Long id;
	private Integer idCierre;
	private Date fechaEmisionRecibo;
	private String grupo;
	private String familia;
	private String codGrupoNegocio;
	private String grupoNegocio;
	private Integer linea;
	private Integer plan;
	private Integer CSB;
	private Integer entidad;
	private Integer subentidad;
	private BigDecimal total;
	private BigDecimal reglamento;
	private BigDecimal comision;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getEntidad() {
		return entidad;
	}

	public void setEntidad(Integer entidad) {
		this.entidad = entidad;
	}

	public Integer getSubentidad() {
		return subentidad;
	}

	public void setSubentidad(Integer subentidad) {
		this.subentidad = subentidad;
	}
	

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getReglamento() {
		return reglamento;
	}

	public void setReglamento(BigDecimal reglamento) {
		this.reglamento = reglamento;
	}

	public BigDecimal getComision() {
		return comision;
	}

	public void setComision(BigDecimal comision) {
		this.comision = comision;
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

	public Integer getIdCierre() {
		return idCierre;
	}

	public void setIdCierre(Integer idCierre) {
		this.idCierre = idCierre;
	}

}
