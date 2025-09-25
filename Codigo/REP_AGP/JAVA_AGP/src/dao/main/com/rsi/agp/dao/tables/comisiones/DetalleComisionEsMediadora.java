package com.rsi.agp.dao.tables.comisiones;

import java.math.BigDecimal;

public class DetalleComisionEsMediadora {

	private BigDecimal codlinea;
	private BigDecimal codplan;
	private Character grupoNegocio;
	private String descripcionGN;
	private BigDecimal pctEntAux;
	private BigDecimal pctEsMedAux;
	private BigDecimal pctEntidad;
	private BigDecimal pctMediador;

	public DetalleComisionEsMediadora() {
	}
	
	public DetalleComisionEsMediadora(DetalleComisionEsMediadora obj) {
		this(obj.getCodlinea(), obj.getCodplan(), obj.getGrupoNegocio(), obj.getDescripcionGN(), obj.getPctEntAux(),
				obj.getPctEsMedAux(), obj.getPctEntidad(), obj.getPctMediador());
	}

	public DetalleComisionEsMediadora(BigDecimal codlinea, BigDecimal codplan, Character grupoNegocio,
			String descripcionGN, BigDecimal pctEntAux, BigDecimal pctEsMedAux, BigDecimal pctEntidad,
			BigDecimal pctMediador) {
		super();
		this.codlinea = codlinea;
		this.codplan = codplan;
		this.grupoNegocio = grupoNegocio;
		this.descripcionGN = descripcionGN;
		this.pctEntAux = pctEntAux;
		this.pctEsMedAux = pctEsMedAux;
		this.pctEntidad = pctEntidad;
		this.pctMediador = pctMediador;
	}

	public BigDecimal getCodlinea() {
		return codlinea;
	}

	public void setCodlinea(BigDecimal codlinea) {
		this.codlinea = codlinea;
	}

	public BigDecimal getCodplan() {
		return codplan;
	}

	public void setCodplan(BigDecimal codplan) {
		this.codplan = codplan;
	}

	public Character getGrupoNegocio() {
		return grupoNegocio;
	}

	public void setGrupoNegocio(Character grupoNegocio) {
		this.grupoNegocio = grupoNegocio;
	}

	public String getDescripcionGN() {
		return descripcionGN;
	}

	public void setDescripcionGN(String descripcionGN) {
		this.descripcionGN = descripcionGN;
	}

	public BigDecimal getPctEntAux() {
		return pctEntAux;
	}

	public void setPctEntAux(BigDecimal pctEntAux) {
		this.pctEntAux = pctEntAux;
	}

	public BigDecimal getPctEsMedAux() {
		return pctEsMedAux;
	}

	public void setPctEsMedAux(BigDecimal pctEsMedAux) {
		this.pctEsMedAux = pctEsMedAux;
	}

	public BigDecimal getPctEntidad() {
		return pctEntidad;
	}

	public void setPctEntidad(BigDecimal pctEntidad) {
		this.pctEntidad = pctEntidad;
	}

	public BigDecimal getPctMediador() {
		return pctMediador;
	}

	public void setPctMediador(BigDecimal pctMediador) {
		this.pctMediador = pctMediador;
	}
}
