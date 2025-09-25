package com.rsi.agp.dao.tables.cpl;

import java.io.Serializable;
import java.math.BigDecimal;

public class ModuloValorCeldaView implements Serializable {

	private static final long serialVersionUID = 3558251390085029861L;

	private String codigo;
	private String descripcion;
	private BigDecimal fila;
	private BigDecimal filaVinculada;
	private BigDecimal columna;
	private BigDecimal columnaVinculada;
	private boolean tachar;

	public ModuloValorCeldaView() {

	}

	public ModuloValorCeldaView(String codigo, String descripcion) {
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	public ModuloValorCeldaView(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getFila() {
		return fila;
	}

	public void setFila(BigDecimal fila) {
		this.fila = fila;
	}

	public BigDecimal getFilaVinculada() {
		return filaVinculada;
	}

	public void setFilaVinculada(BigDecimal filaVinculada) {
		this.filaVinculada = filaVinculada;
	}

	public BigDecimal getColumna() {
		return columna;
	}

	public void setColumna(BigDecimal columna) {
		this.columna = columna;
	}

	public BigDecimal getColumnaVinculada() {
		return columnaVinculada;
	}

	public void setColumnaVinculada(BigDecimal columnaVinculada) {
		this.columnaVinculada = columnaVinculada;
	}

	public boolean isTachar() {
		return tachar;
	}

	public void setTachar(boolean tachar) {
		this.tachar = tachar;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append("valor: (" + this.getCodigo());
		sb.append(") " + this.getDescripcion());
		sb.append("]");
		return sb.toString();
	}
}
