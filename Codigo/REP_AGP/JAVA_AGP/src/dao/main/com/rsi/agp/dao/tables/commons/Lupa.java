package com.rsi.agp.dao.tables.commons;

public class Lupa {
	String clase;
	String[] propiedadTabla;
	String propiedadFiltroTabla;
	String valorFiltroTabla;
	String[] propiedadFiltroFormulario;
	String[] valorFiltroFormulario;
	String[] propiedadFormulario;
	String[] camposOrden;
	String[] valoresOrden;
	String[] camposNoFiltrados;
	String[] valoresNoFiltrados;
	
	public Lupa() {
	}

	public Lupa(String clase, String[] propiedadTabla,
			String propiedadFiltroTabla, String valorFiltroTabla,
			String[] propiedadFiltroFormulario, String[] valorFiltroFormulario,
			String[] propiedadFormulario, String[] camposOrden,
			String[] valoresOrden, String[] camposNoFiltrados,
			String[] valoresNoFiltrados) {
		this.clase = clase;
		this.propiedadTabla = propiedadTabla;
		this.propiedadFiltroTabla = propiedadFiltroTabla;
		this.valorFiltroTabla = valorFiltroTabla;
		this.propiedadFiltroFormulario = propiedadFiltroFormulario;
		this.valorFiltroFormulario = valorFiltroFormulario;
		this.propiedadFormulario = propiedadFormulario;
		this.camposOrden = camposOrden;
		this.valoresOrden = valoresOrden;
		this.camposNoFiltrados = camposNoFiltrados;
		this.valoresNoFiltrados = valoresNoFiltrados;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String[] getPropiedadTabla() {
		return propiedadTabla;
	}

	public void setPropiedadTabla(String[] propiedadTabla) {
		this.propiedadTabla = propiedadTabla;
	}

	public String getPropiedadFiltroTabla() {
		return propiedadFiltroTabla;
	}

	public void setPropiedadFiltroTabla(String propiedadFiltroTabla) {
		this.propiedadFiltroTabla = propiedadFiltroTabla;
	}

	public String getValorFiltroTabla() {
		return valorFiltroTabla;
	}

	public void setValorFiltroTabla(String valorFiltroTabla) {
		this.valorFiltroTabla = valorFiltroTabla;
	}

	public String[] getPropiedadFiltroFormulario() {
		return propiedadFiltroFormulario;
	}

	public void setPropiedadFiltroFormulario(String[] propiedadFiltroFormulario) {
		this.propiedadFiltroFormulario = propiedadFiltroFormulario;
	}

	public String[] getValorFiltroFormulario() {
		return valorFiltroFormulario;
	}

	public void setValorFiltroFormulario(String[] valorFiltroFormulario) {
		this.valorFiltroFormulario = valorFiltroFormulario;
	}

	public String[] getPropiedadFormulario() {
		return propiedadFormulario;
	}

	public void setPropiedadFormulario(String[] propiedadFormulario) {
		this.propiedadFormulario = propiedadFormulario;
	}

	public String[] getCamposOrden() {
		return camposOrden;
	}

	public void setCamposOrden(String[] camposOrden) {
		this.camposOrden = camposOrden;
	}

	public String[] getValoresOrden() {
		return valoresOrden;
	}

	public void setValoresOrden(String[] valoresOrden) {
		this.valoresOrden = valoresOrden;
	}

	public String[] getCamposNoFiltrados() {
		return camposNoFiltrados;
	}

	public void setCamposNoFiltrados(String[] camposNoFiltrados) {
		this.camposNoFiltrados = camposNoFiltrados;
	}

	public String[] getValoresNoFiltrados() {
		return valoresNoFiltrados;
	}

	public void setValoresNoFiltrados(String[] valoresNoFiltrados) {
		this.valoresNoFiltrados = valoresNoFiltrados;
	}

}
