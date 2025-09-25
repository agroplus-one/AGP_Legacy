package com.rsi.agp.core.webapp.util;

import java.math.BigDecimal;

public class FilaCompVinculada implements java.io.Serializable{
	private Long lineaseguroid;
    private String codmodulo;
    private String desmodulo;
    private BigDecimal codconceptoppalmod;
    private String desconceptoppalmod;
    private BigDecimal codriesgocubierto;
    private String desriesgocubierto;
    private BigDecimal codconcepto;
    private String nomconcepto;
    private BigDecimal codvalor;
    private String desvalor;
    private Character datovinculado;
    private BigDecimal filamodulo;
    private BigDecimal columnamodulo;
    
    public FilaCompVinculada(){
    	super();
    }
    
	public Long getLineaseguroid() {
		return lineaseguroid;
	}
	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}
	public String getCodmodulo() {
		return codmodulo;
	}
	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}
	public String getDesmodulo() {
		return desmodulo;
	}
	public void setDesmodulo(String desmodulo) {
		this.desmodulo = desmodulo;
	}
	public BigDecimal getCodconceptoppalmod() {
		return codconceptoppalmod;
	}
	public void setCodconceptoppalmod(BigDecimal codconceptoppalmod) {
		this.codconceptoppalmod = codconceptoppalmod;
	}
	public String getDesconceptoppalmod() {
		return desconceptoppalmod;
	}
	public void setDesconceptoppalmod(String desconceptoppalmod) {
		this.desconceptoppalmod = desconceptoppalmod;
	}
	public BigDecimal getCodriesgocubierto() {
		return codriesgocubierto;
	}
	public void setCodriesgocubierto(BigDecimal codriesgocubierto) {
		this.codriesgocubierto = codriesgocubierto;
	}
	public String getDesriesgocubierto() {
		return desriesgocubierto;
	}
	public void setDesriesgocubierto(String desriesgocubierto) {
		this.desriesgocubierto = desriesgocubierto;
	}
	public BigDecimal getCodconcepto() {
		return codconcepto;
	}
	public void setCodconcepto(BigDecimal codconcepto) {
		this.codconcepto = codconcepto;
	}
	public String getNomconcepto() {
		return nomconcepto;
	}
	public void setNomconcepto(String nomconcepto) {
		this.nomconcepto = nomconcepto;
	}
	public BigDecimal getCodvalor() {
		return codvalor;
	}
	public void setCodvalor(BigDecimal codvalor) {
		this.codvalor = codvalor;
	}
	public String getDesvalor() {
		return desvalor;
	}
	public void setDesvalor(String desvalor) {
		this.desvalor = desvalor;
	}
	public Character getDatovinculado() {
		return datovinculado;
	}
	public void setDatovinculado(Character datovinculado) {
		this.datovinculado = datovinculado;
	}
	public BigDecimal getFilamodulo() {
		return filamodulo;
	}
	public void setFilamodulo(BigDecimal filamodulo) {
		this.filamodulo = filamodulo;
	}
	public BigDecimal getColumnamodulo() {
		return columnamodulo;
	}
	public void setColumnamodulo(BigDecimal columnamodulo) {
		this.columnamodulo = columnamodulo;
	}
	
}
