package com.rsi.agp.dao.tables.cgen;

import java.math.BigDecimal;

public class SubvencionesAseguradosView {
	
	private String tipoSubvencion; //'C' = CCAA, 'E'=Enesa
	private Object subvCCAA;
	private Object subvEnesa;
	private boolean marcada = false;
	private boolean noEdit = false;
	
	private BigDecimal codgruposubvencion;
	private String descgrupo;
	
	//Campo para poder ordenar las subvenciones
	private BigDecimal codtiposubvencion;
	
	private boolean lineaGanado = false;
	
	public String getTipoSubvencion() {
		return this.tipoSubvencion;
	}
	public void setTipoSubvencion(final String tipoSubvencion) {
		this.tipoSubvencion = tipoSubvencion;
	}
	public Object getSubvCCAA() {
		return this.subvCCAA;
	}
	public void setSubvCCAA(final Object subvCCAA) {
		this.subvCCAA = subvCCAA;
	}
	public Object getSubvEnesa() {
		return this.subvEnesa;
	}
	public void setSubvEnesa(final Object subvEnesa) {
		this.subvEnesa = subvEnesa;
	}
	public boolean isMarcada() {
		return this.marcada;
	}
	public void setMarcada(final boolean marcada) {
		this.marcada = marcada;
	}
	public boolean isNoEdit() {
		return this.noEdit;
	}
	public void setNoEdit(final boolean noEdit) {
		this.noEdit = noEdit;
	}
	public BigDecimal getCodtiposubvencion() {
		return this.codtiposubvencion;
	}
	public void setCodtiposubvencion(final BigDecimal codtiposubvencion) {
		this.codtiposubvencion = codtiposubvencion;
	}
	public BigDecimal getCodgruposubvencion() {
		return this.codgruposubvencion;
	}
	public void setCodgruposubvencion(final BigDecimal codgruposubvencion) {
		this.codgruposubvencion = codgruposubvencion;
	}
	public String getDescgrupo() {
		return this.descgrupo;
	}
	public void setDescgrupo(final String descgrupo) {
		this.descgrupo = descgrupo;
	}
	
	public boolean isLineaGanado() {
		return this.lineaGanado;
	}
	public void setLineaGanado(final boolean lineaGanado) {
		this.lineaGanado = lineaGanado;
	}	
}