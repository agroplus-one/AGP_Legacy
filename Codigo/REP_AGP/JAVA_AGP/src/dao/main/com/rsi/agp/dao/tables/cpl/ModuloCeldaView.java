package com.rsi.agp.dao.tables.cpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ModuloCeldaView implements Serializable {

	private static final long serialVersionUID = -4801349966938298259L;

	private boolean elegible;
	private String observaciones;
	private BigDecimal codconcepto;
	private BigDecimal columna;
	private List<ModuloValorCeldaView> valores = new ArrayList<ModuloValorCeldaView>();

	public ModuloCeldaView() {
		this.elegible = false;
	}

	public ModuloCeldaView(String descripcion) {
		this.elegible = false;
		this.valores.add(new ModuloValorCeldaView(descripcion));
	}

	public boolean isElegible() {
		return elegible;
	}

	public void setElegible(boolean elegible) {
		this.elegible = elegible;
	}

	public List<ModuloValorCeldaView> getValores() {
		return valores;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public BigDecimal getCodconcepto() {
		return codconcepto;
	}

	public void setCodconcepto(BigDecimal codconcepto) {
		this.codconcepto = codconcepto;
	}

	public void setValores(List<ModuloValorCeldaView> valores) {
		this.valores = valores;
	}

	public BigDecimal getColumna() {
		return columna;
	}

	public void setColumna(BigDecimal columna) {
		this.columna = columna;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append("codconcepto: " + this.getCodconcepto());
		sb.append(", elegible: " + this.isElegible());
		sb.append(", valores: {");
		for (ModuloValorCeldaView valor : this.getValores()) {
			sb.append(valor.toString());
		}
		sb.append("}]");
		return sb.toString();
	}
}