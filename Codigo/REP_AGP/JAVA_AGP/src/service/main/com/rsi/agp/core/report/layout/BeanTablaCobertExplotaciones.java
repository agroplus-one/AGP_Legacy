package com.rsi.agp.core.report.layout;

import java.util.ArrayList;
import java.util.List;

/* Pet. 57622 */
public class BeanTablaCobertExplotaciones {
	
	private String explotacion = "";
	private String riesgosCubiertos = "";

	private List<String> celdas = new ArrayList<String>();

	public String getExplotacion() {
		return this.explotacion;
	}

	public void setExplotacion(String explotacion) {
		this.explotacion = explotacion;
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
		sb.append("explotacion: " + explotacion);
		sb.append(", riesgosCubiertos: " + riesgosCubiertos);
		sb.append(", celdas: {");
		for (String celda : celdas) {
			sb.append("[" + celda + "]");
		}
		sb.append("}]");
		return sb.toString();
	}
}
