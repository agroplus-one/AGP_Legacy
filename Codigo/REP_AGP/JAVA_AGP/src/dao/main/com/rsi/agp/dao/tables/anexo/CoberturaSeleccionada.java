package com.rsi.agp.dao.tables.anexo;

import java.math.BigDecimal;

public class CoberturaSeleccionada {

	private BigDecimal codconceptoppalmod;
	private BigDecimal codriesgocubierto;
	private BigDecimal codconcepto;
	private String codvalor;

	public CoberturaSeleccionada() {
	}

	public CoberturaSeleccionada(Long id, AnexoModificacion anexoModificacion, BigDecimal codconcepto,
			String codvalor) {

		this.codconcepto = codconcepto;
		this.codvalor = codvalor;
	}

	public CoberturaSeleccionada(Long id, AnexoModificacion anexoModificacion, BigDecimal codconceptoppalmod,
			BigDecimal codriesgocubierto, BigDecimal codconcepto, String codvalor, Character tipomodificacion) {

		this.codconceptoppalmod = codconceptoppalmod;
		this.codriesgocubierto = codriesgocubierto;
		this.codconcepto = codconcepto;
		this.codvalor = codvalor;

	}

	public BigDecimal getCodconceptoppalmod() {
		return this.codconceptoppalmod;
	}

	public void setCodconceptoppalmod(BigDecimal codconceptoppalmod) {
		this.codconceptoppalmod = codconceptoppalmod;
	}

	public BigDecimal getCodriesgocubierto() {
		return this.codriesgocubierto;
	}

	public void setCodriesgocubierto(BigDecimal codriesgocubierto) {
		this.codriesgocubierto = codriesgocubierto;
	}

	public BigDecimal getCodconcepto() {
		return this.codconcepto;
	}

	public void setCodconcepto(BigDecimal codconcepto) {
		this.codconcepto = codconcepto;
	}

	public String getCodvalor() {
		return this.codvalor;
	}

	public void setCodvalor(String codvalor) {
		this.codvalor = codvalor;
	}

}
