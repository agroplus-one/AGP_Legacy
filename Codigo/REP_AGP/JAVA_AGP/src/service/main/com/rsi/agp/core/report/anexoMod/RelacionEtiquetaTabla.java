package com.rsi.agp.core.report.anexoMod;

public class RelacionEtiquetaTabla {
	
	private String etiqueta;
	private String tabla;
	private String nombreConcepto;
	
	public RelacionEtiquetaTabla() {
	}
	
	public RelacionEtiquetaTabla(String etiqueta, String tabla, String nombreConcepto) {
		this.etiqueta = etiqueta;
		this.tabla = tabla;
		this.nombreConcepto = nombreConcepto;
	}
	
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}

	public String getNombreConcepto() {
		return nombreConcepto;
	}

	public void setNombreConcepto(String nombreConcepto) {
		this.nombreConcepto = nombreConcepto;
	}
	
	

}
