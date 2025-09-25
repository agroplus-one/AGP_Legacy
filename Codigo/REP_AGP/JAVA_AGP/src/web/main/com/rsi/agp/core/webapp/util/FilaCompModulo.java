package com.rsi.agp.core.webapp.util;

import java.math.BigDecimal;
import java.util.ArrayList;

public class FilaCompModulo {

	BigDecimal filaComparativa;
	ArrayList<FilaComparativa> filasCompMod = new ArrayList<FilaComparativa>();

	public ArrayList<FilaComparativa> getFilasCompMod() {
		return filasCompMod;
	}

	public void setFilasCompMod(ArrayList<FilaComparativa> filasCompMod) {
		this.filasCompMod = filasCompMod;
	}

	public BigDecimal getFilaComparativa() {
		return filaComparativa;
	}

	public void setFilaComparativa(BigDecimal filaComparativa) {
		this.filaComparativa = filaComparativa;
	}

}
