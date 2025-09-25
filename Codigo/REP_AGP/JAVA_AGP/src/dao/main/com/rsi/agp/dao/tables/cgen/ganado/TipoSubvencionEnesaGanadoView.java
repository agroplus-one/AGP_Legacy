package com.rsi.agp.dao.tables.cgen.ganado;

import java.math.BigDecimal;

import com.rsi.agp.dao.tables.cpl.gan.SubvencionEnesaGanado;

public class TipoSubvencionEnesaGanadoView 
{
	private SubvencionEnesaGanado subvEnesaGanado;
	private boolean marcada = false;
	private boolean noEdit = false;
	private BigDecimal codgruposubvencion;
	private String descgrupo;
	
	
	public SubvencionEnesaGanado getSubvEnesaGanado() {
		return subvEnesaGanado;
	}
	public void setSubvEnesaGanado(SubvencionEnesaGanado subvEnesaGanado) {
		this.subvEnesaGanado = subvEnesaGanado;
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
