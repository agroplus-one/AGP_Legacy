package com.rsi.agp.vo;

import java.util.ArrayList;

public class RiesgoVO {

	// Datos identificativos del riesgo
	private String codConceptoPpalMod;
	private String codRiesgoCubierto;
	private String codValor;
	private String codConcepto; // Para riesgos 363 --> elegido: -1, no elegido: -2, otros: valor
	private String codModulo;
	private String lineaSeguroId;

	private ArrayList<CaracteristicaRiesgoVO> caracteristicasRiesgo = new ArrayList<CaracteristicaRiesgoVO>();

	public RiesgoVO() {
		this.codConceptoPpalMod = new String("");
		this.codRiesgoCubierto = new String("");
		this.codValor = new String("");
		this.codConcepto = new String("");
		this.codModulo = new String("");
		this.lineaSeguroId = new String("");
	}

	public ArrayList<CaracteristicaRiesgoVO> getCaracteristicasRiesgo() {
		return caracteristicasRiesgo;
	}

	public void setCaracteristicasRiesgo(ArrayList<CaracteristicaRiesgoVO> caracteristicasRiesgo) {
		this.caracteristicasRiesgo = caracteristicasRiesgo;
	}

	public String getCodConceptoPpalMod() {
		return codConceptoPpalMod;
	}

	public void setCodConceptoPpalMod(String codConceptoPpalMod) {
		this.codConceptoPpalMod = codConceptoPpalMod;
	}

	public String getCodRiesgoCubierto() {
		return codRiesgoCubierto;
	}

	public void setCodRiesgoCubierto(String codRiesgoCubierto) {
		this.codRiesgoCubierto = codRiesgoCubierto;
	}

	public String getCodValor() {
		return codValor;
	}

	public void setCodValor(String codValor) {
		this.codValor = codValor;
	}

	public String getCodConcepto() {
		return codConcepto;
	}

	public void setCodConcepto(String codConcepto) {
		this.codConcepto = codConcepto;
	}

	public String getCodModulo() {
		return codModulo;
	}

	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}

	public String getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(String lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}
}
