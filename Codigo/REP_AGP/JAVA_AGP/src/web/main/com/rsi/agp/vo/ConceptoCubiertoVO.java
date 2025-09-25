package com.rsi.agp.vo;

public class ConceptoCubiertoVO implements Comparable<ConceptoCubiertoVO> {

	String id;
	String desConcepto;
	Integer numeroColumna;

	public ConceptoCubiertoVO() {
		id = new String();
		desConcepto = new String();
		numeroColumna = 9999;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesConcepto() {
		return desConcepto;
	}

	public void setDesConcepto(String desConcepto) {
		this.desConcepto = desConcepto;
	}

	public Integer getNumeroColumna() {
		return numeroColumna;
	}

	public void setNumeroColumna(Integer numeroColumna) {
		this.numeroColumna = numeroColumna;
	}

	public int compareTo(ConceptoCubiertoVO object) {
		ConceptoCubiertoVO otroUsuario = (ConceptoCubiertoVO) object;
		return numeroColumna.compareTo(otroUsuario.getNumeroColumna());
	}

}
