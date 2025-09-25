package com.rsi.agp.dao.tables.poliza.explotaciones.Informes;

public class InformeDatosVariables {
	private Long codConcepto;	
	private String nombreConcepto;		
	private String valor;
	private String descripcion;
	
	private Long codConceptoM;	
	private String nombreConceptoM;		
	private String valorM;
	private String descripcionM;
	
	public Long getCodConcepto() {
		return codConcepto;
	}
	public void setCodConcepto(Long codConcepto) {
		this.codConcepto = codConcepto;
	}
	
	public String getNombreConcepto() {
		return nombreConcepto;
	}
	public void setNombreConcepto(String nombreConcepto) {
		this.nombreConcepto = nombreConcepto;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getCodConceptoM() {
		return codConceptoM;
	}
	public void setCodConceptoM(Long codConceptoM) {
		this.codConceptoM = codConceptoM;
	}
	public String getNombreConceptoM() {
		return nombreConceptoM;
	}
	public void setNombreConceptoM(String nombreConceptoM) {
		this.nombreConceptoM = nombreConceptoM;
	}
	public String getValorM() {
		return valorM;
	}
	public void setValorM(String valorM) {
		this.valorM = valorM;
	}
	public String getDescripcionM() {
		return descripcionM;
	}
	public void setDescripcionM(String descripcionM) {
		this.descripcionM = descripcionM;
	}
}
