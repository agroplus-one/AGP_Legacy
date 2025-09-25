package com.rsi.agp.dao.tables.cgen;

import java.math.BigDecimal;

import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;

public class TipoSubvencionEnesaView 
{
	private SubvencionEnesa subvEnesa;
	private boolean marcada = false;
	private boolean noEdit = false;
	private BigDecimal codgruposubvencion;
	private String descgrupo;
	
	public SubvencionEnesa getSubvEnesa() {
		return subvEnesa;
	}
	public void setSubvEnesa(SubvencionEnesa subvEnesa) {
		this.subvEnesa = subvEnesa;
	}
	public boolean isMarcada() {
		return marcada;
	}
	public void setMarcada(boolean marcada) {
		this.marcada = marcada;
	}
	public boolean isNoEdit() {
		return noEdit;
	}
	public void setNoEdit(boolean noEdit) {
		this.noEdit = noEdit;
	}
	public BigDecimal getCodgruposubvencion() {
		return codgruposubvencion;
	}
	public void setCodgruposubvencion(BigDecimal codgruposubvencion) {
		this.codgruposubvencion = codgruposubvencion;
	}
	public String getDescgrupo() {
		return descgrupo;
	}
	public void setDescgrupo(String descgrupo) {
		this.descgrupo = descgrupo;
	}
	
}
