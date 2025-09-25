package com.rsi.agp.vo;

public class DatoVariableParcelaVO {

	private Integer codconcepto;
	private String valor;
	private Long idDatoVariable;

	public DatoVariableParcelaVO(final Integer codconcepto, final String valor, final Long idDatoVariable) {
		super();
		this.codconcepto = codconcepto;
		this.valor = valor;
		this.idDatoVariable = idDatoVariable;
	}

	public Integer getCodconcepto() {
		return codconcepto;
	}

	public void setCodconcepto(Integer codconcepto) {
		this.codconcepto = codconcepto;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public Long getIdDatoVariable() {
		return idDatoVariable;
	}

	public void setIdDatoVariable(Long idDatoVariable) {
		this.idDatoVariable = idDatoVariable;
	}
}