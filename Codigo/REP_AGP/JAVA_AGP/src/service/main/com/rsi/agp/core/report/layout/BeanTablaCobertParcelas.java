/*****************************************************************/
/** CREATE: 29/09/2020, T-SYSTEMS                               **/
/** Fuente nuevo para las impresiones de coberturas de Parcelas **/
/** PETICIÓN: 63485 - FASE II                                   **/
/*****************************************************************/

package com.rsi.agp.core.report.layout;

import java.util.ArrayList;
import java.util.List;

public class BeanTablaCobertParcelas {

	private String parcela = "";
	private String riesgosCubiertos = "";

	private List<String> celdas = new ArrayList<String>();

	public String getParcela() {
		return this.parcela;
	}

	public void setParcela(String parcela) {
		this.parcela = parcela;
	}

	public String getRiesgosCubiertos() {
		return this.riesgosCubiertos;
	}

	public void setRiesgosCubiertos(String riesgosCubiertos) {
		this.riesgosCubiertos = riesgosCubiertos;
	}

	public List<String> getCeldas() {
		return celdas;
	}

	public void setCeldas(List<String> celdas) {
		this.celdas = celdas;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append("parcela: " + parcela);
		sb.append(", riesgosCubiertos: " + riesgosCubiertos);
		sb.append(", celdas: {");
		for (String celda : celdas) {
			sb.append("[" + celda + "]");
		}
		sb.append("}]");
		return sb.toString();
	}
}
