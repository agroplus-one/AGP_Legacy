package com.rsi.agp.vo;

public class ConceptoLongTipoVO {

	private int codConcepto;
	private int longitud;
	private String tipo;
	private int numDecimales;

	public ConceptoLongTipoVO(int codConcepto, int longitud, String tipo, int numDecimales) {
		this.codConcepto = codConcepto;
		this.longitud = longitud;
		this.tipo = tipo;
		this.numDecimales = numDecimales;
	}

	public ConceptoLongTipoVO() {

	}

	public int getCodConcepto() {
		return codConcepto;
	}

	public void setCodConcepto(int codConcepto) {
		this.codConcepto = codConcepto;
	}

	public int getLongitud() {
		return longitud;
	}

	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getNumDecimales() {
		return numDecimales;
	}

	public void setNumDecimales(int numDecimales) {
		this.numDecimales = numDecimales;
	}

}
