package com.rsi.agp.dao.tables.cpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ModuloFilaView implements Serializable {

	private static final long serialVersionUID = 8525898723759702142L;

	private String conceptoPrincipalModulo;
	private String riesgoCubierto;
	private BigDecimal codConceptoPrincipalModulo;
	private BigDecimal codRiesgoCubierto;
	private boolean rcElegible = false;
	private boolean basica = false;
	private BigDecimal codCptoRCE;
	private BigDecimal filaComparativa;
	// CONDICIONES DE COBERTURAS
	private List<ModuloCeldaView> celdas;
	private BigDecimal filamodulo;
	// Lista de vinculaciones correspondientes al riesgo cubierto de esta fila
	private List<ModuloFilaVinculacionView> listVinculaciones;

	public ModuloFilaView() {
		super();
	}

	public String getConceptoPrincipalModulo() {
		return conceptoPrincipalModulo;
	}

	public void setConceptoPrincipalModulo(String conceptoPrincipalModulo) {
		this.conceptoPrincipalModulo = conceptoPrincipalModulo;
	}

	public String getRiesgoCubierto() {
		return riesgoCubierto;
	}

	public void setRiesgoCubierto(String riesgoCubierto) {
		this.riesgoCubierto = riesgoCubierto;
	}

	public BigDecimal getCodConceptoPrincipalModulo() {
		return codConceptoPrincipalModulo;
	}

	public void setCodConceptoPrincipalModulo(BigDecimal codConceptoPrincipalModulo) {
		this.codConceptoPrincipalModulo = codConceptoPrincipalModulo;
	}

	public BigDecimal getCodRiesgoCubierto() {
		return codRiesgoCubierto;
	}

	public void setCodRiesgoCubierto(BigDecimal codRiesgoCubierto) {
		this.codRiesgoCubierto = codRiesgoCubierto;
	}

	public BigDecimal getFilamodulo() {
		return this.filamodulo;
	}

	public void setFilamodulo(BigDecimal filamodulo) {
		this.filamodulo = filamodulo;
	}

	public List<ModuloCeldaView> getCeldas() {
		return celdas;
	}

	public void setCeldas(List<ModuloCeldaView> celdas) {
		this.celdas = celdas;
	}

	public boolean isRcElegible() {
		return rcElegible;
	}

	public void setRcElegible(boolean rcElegible) {
		this.rcElegible = rcElegible;
	}

	public List<ModuloFilaVinculacionView> getListVinculaciones() {
		return listVinculaciones;
	}

	public void setListVinculaciones(List<ModuloFilaVinculacionView> listVinculaciones) {
		this.listVinculaciones = listVinculaciones;
	}

	public BigDecimal getCodCptoRCE() {
		return codCptoRCE;
	}

	public void setCodCptoRCE(BigDecimal codCptoRCE) {
		this.codCptoRCE = codCptoRCE;
	}

	public BigDecimal getFilaComparativa() {
		return filaComparativa;
	}

	public void setFilaComparativa(BigDecimal filaComparativa) {
		this.filaComparativa = filaComparativa;
	}

	public boolean isBasica() {
		return basica;
	}

	public void setBasica(boolean basica) {
		this.basica = basica;
	}

	/*
	 * Metodo que devuelve un Long formado por el
	 * codmodulo,Codconceptoppalmod,Codriesgocubierto y Filamodulo
	 */
	public Long getclaveComparacion() {
		Long res = null;
		if (this.getCodConceptoPrincipalModulo() != null && this.getCodRiesgoCubierto() != null
				&& this.getFilamodulo() != null) {
			String cpm = String.format("%03d", new Integer(this.getCodConceptoPrincipalModulo().toString()));
			String rc = String.format("%03d", new Integer(this.getCodRiesgoCubierto().toString()));
			String fm = String.format("%03d", new Integer(this.getFilamodulo().toString()));

			String clave1 = cpm + rc + fm;
			res = new Long(clave1);
		}
		return res;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append("cpm: " + this.getCodConceptoPrincipalModulo());
		sb.append(", rc: " + this.getCodRiesgoCubierto());
		sb.append(", celdas: {");
		for (ModuloCeldaView celda : this.getCeldas()) {
			sb.append(celda.toString());
		}
		sb.append("}]");
		return sb.toString();
	}
}
