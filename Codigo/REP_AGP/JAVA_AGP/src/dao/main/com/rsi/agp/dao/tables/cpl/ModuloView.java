package com.rsi.agp.dao.tables.cpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModuloView implements Serializable{

	private static final long serialVersionUID = -1295746797368561558L;
	
	private String codModulo;
	private String descripcionModulo;
	private Integer totcomplementarios;
	//CONDICIONES DE COBERTURAS
	private List<String> listaCabeceras = new ArrayList<String>();
	private List<ModuloFilaView> listaFilas = new ArrayList<ModuloFilaView>();
	private boolean isInFechaContratacion;
	private Integer renovable;
	private Integer tipoAsegGanado;
	private Integer numComparativa;
	private Long idModulo;

	public boolean isInFechaContratacion() {
		return isInFechaContratacion;
	}

	public void setInFechaContratacion(boolean isInFechaContratacion) {
		this.isInFechaContratacion = isInFechaContratacion;
	}

	public ModuloView(){
		super();
	}

	public String getDescripcionModulo() {
		return descripcionModulo;
	}

	public void setDescripcionModulo(String descripcionModulo) {
		this.descripcionModulo = descripcionModulo;
	}

	public List<ModuloFilaView> getListaFilas() {
		return listaFilas;
	}

	public List<String> getListaCabeceras() {
		return listaCabeceras;
	}

	public Integer getTotcomplementarios() {
		return totcomplementarios;
	}

	public void setTotcomplementarios(Integer totcomplementarios) {
		this.totcomplementarios = totcomplementarios;
	}

	public String getCodModulo() {
		return codModulo;
	}

	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}

	public void setListaCabeceras(List<String> listaCabeceras) {
		this.listaCabeceras = listaCabeceras;
	}

	public void setListaFilas(List<ModuloFilaView> listaFilas) {
		this.listaFilas = listaFilas;
	}

	public Integer getRenovable() {
		return renovable;
	}

	public void setRenovable(Integer renovable) {
		this.renovable = renovable;
	}

	public Integer getTipoAsegGanado() {
		return tipoAsegGanado;
	}

	public void setTipoAsegGanado(Integer tipoAsegGanado) {
		this.tipoAsegGanado = tipoAsegGanado;
	}

	public Integer getNumComparativa() {
		return numComparativa;
	}

	public void setNumComparativa(Integer numComparativa) {
		this.numComparativa = numComparativa;
	}

	public Long getIdModulo() {
		return idModulo;
	}

	public void setIdModulo(Long idModulo) {
		this.idModulo = idModulo;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append(", filas: {");
		for (ModuloFilaView fila : this.getListaFilas()) {
			sb.append(fila.toString());
		}
		sb.append("}]");
		return sb.toString();
	}
}
