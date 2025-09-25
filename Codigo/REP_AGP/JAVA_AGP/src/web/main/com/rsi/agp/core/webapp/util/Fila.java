/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  20/07/2010  Ernesto Laura     Clase para visualizar la pantalla del detalle de 
* 											coberturas
*
**************************************************************************************************/
package com.rsi.agp.core.webapp.util;

import java.math.BigDecimal;

public class Fila
{
	private int numFila;
	private String codConceptoPpal;
	private String descConceptoPpal;
	private String codRiesgoCbrto;
	private String descRiesgoCbrto;
	private String elegible;
	private String nvEleccion;
	private String descCapAseg;
	private String descCalcIndem;
	private String descMinIndem;
	private String descTipoFranq;
	private String descPctFranq;
	private String descTipoRendEleg;
	private String descGarantizadoEleg;
	private String tipoValor;
	private String concepto;
	private String valor;
	private BigDecimal codigoRCbrto;
	private String codModulo;
	private BigDecimal filaModulo;
	private String vinculado;	
	
	public int getNumFila() {
		return numFila;
	}
	public void setNumFila(int numFila) {
		this.numFila = numFila;
	}
	public String getCodConceptoPpal() {
		return codConceptoPpal;
	}
	public void setCodConceptoPpal(String codConceptoPpal) {
		this.codConceptoPpal = codConceptoPpal;
	}
	public String getDescConceptoPpal() {
		return descConceptoPpal;
	}
	public void setDescConceptoPpal(String descConceptoPpal) {
		this.descConceptoPpal = descConceptoPpal;
	}
	public String getCodRiesgoCbrto() {
		return codRiesgoCbrto;
	}
	public void setCodRiesgoCbrto(String codRiesgoCbrto) {
		this.codRiesgoCbrto = codRiesgoCbrto;
	}
	public String getDescRiesgoCbrto() {
		return descRiesgoCbrto;
	}
	public void setDescRiesgoCbrto(String descRiesgoCbrto) {
		this.descRiesgoCbrto = descRiesgoCbrto;
	}
	public String getElegible() {
		return elegible;
	}
	public void setElegible(String elegible) {
		this.elegible = elegible;
	}
	public String getNvEleccion() {
		return nvEleccion;
	}
	public void setNvEleccion(String nvEleccion) {
		this.nvEleccion = nvEleccion;
	}
	public String getDescCapAseg() {
		return descCapAseg;
	}
	public void setDescCapAseg(String descCapAseg) {
		this.descCapAseg = descCapAseg;
	}
	public String getDescCalcIndem() {
		return descCalcIndem;
	}
	public void setDescCalcIndem(String descCalcIndem) {
		this.descCalcIndem = descCalcIndem;
	}
	public String getDescMinIndem() {
		return descMinIndem;
	}
	public void setDescMinIndem(String descMinIndem) {
		this.descMinIndem = descMinIndem;
	}
	public String getDescTipoFranq() {
		return descTipoFranq;
	}
	public void setDescTipoFranq(String descTipoFranq) {
		this.descTipoFranq = descTipoFranq;
	}
	public String getDescPctFranq() {
		return descPctFranq;
	}
	public void setDescPctFranq(String descPctFranq) {
		this.descPctFranq = descPctFranq;
	}
	public String getDescTipoRendEleg() {
		return descTipoRendEleg;
	}
	public void setDescTipoRendEleg(String descTipoRendEleg) {
		this.descTipoRendEleg = descTipoRendEleg;
	}
	public String getDescGarantizadoEleg() {
		return descGarantizadoEleg;
	}
	public void setDescGarantizadoEleg(String descGarantizadoEleg) {
		this.descGarantizadoEleg = descGarantizadoEleg;
	}
	public String getTipoValor() {
		return tipoValor;
	}
	public void setTipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public BigDecimal getCodigoRCbrto() {
		return codigoRCbrto;
	}
	public void setCodigoRCbrto(BigDecimal codigoRCbrto) {
		this.codigoRCbrto = codigoRCbrto;
	}
	public String getCodModulo() {
		return codModulo;
	}
	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}
	public BigDecimal getFilaModulo() {
		return filaModulo;
	}
	public void setFilaModulo(BigDecimal filaModulo) {
		this.filaModulo = filaModulo;
	}
	public String getVinculado() {
		return vinculado;
	}
	public void setVinculado(String vinculado) {
		this.vinculado = vinculado;
	}
}