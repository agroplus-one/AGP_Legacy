package com.rsi.agp.dao.tables.poliza.dc2015;

import java.math.BigDecimal;

public class DistCosteExplotacionesSubvencion implements java.io.Serializable {

	private static final long serialVersionUID = -1976675925761912726L;

	private Long id;
	private DistCosteExplotaciones distCosteExplotaciones;
	private Character codTipo;
	private Character codOrganismo;
	private BigDecimal codTipoSubv;
	private BigDecimal importe;
	private BigDecimal pctSubvencion;
	private BigDecimal valorUnitario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DistCosteExplotaciones getDistCosteExplotaciones() {
		return distCosteExplotaciones;
	}

	public void setDistCosteExplotaciones(DistCosteExplotaciones distCosteExplotaciones) {
		this.distCosteExplotaciones = distCosteExplotaciones;
	}

	public Character getCodTipo() {
		return codTipo;
	}

	public void setCodTipo(Character codTipo) {
		this.codTipo = codTipo;
	}

	public Character getCodOrganismo() {
		return codOrganismo;
	}

	public void setCodOrganismo(Character codOrganismo) {
		this.codOrganismo = codOrganismo;
	}

	public BigDecimal getCodTipoSubv() {
		return codTipoSubv;
	}

	public void setCodTipoSubv(BigDecimal codTipoSubv) {
		this.codTipoSubv = codTipoSubv;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getPctSubvencion() {
		return pctSubvencion;
	}

	public void setPctSubvencion(BigDecimal pctSubvencion) {
		this.pctSubvencion = pctSubvencion;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
}