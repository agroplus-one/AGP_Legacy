package com.rsi.agp.core.webapp.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FilaComparativa {

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
    private FilaCompVinculada filavinculada;
    private List<FilaCompVinculada> filasVinculadas = new ArrayList<FilaCompVinculada>();
    private String elegible;
    private FilaCompElegible filaelegible;
    
    public FilaComparativa(){
    	super();
    	filavinculada = new FilaCompVinculada();
    	elegible = "N";
    	filaelegible = new FilaCompElegible();
    }
    
    public FilaComparativa(FilaComparativa fila){
    	this.lineaseguroid = fila.getLineaseguroid();
    	this.codmodulo = fila.getCodmodulo();
    	this.desmodulo = fila.getDesmodulo();
    	this.codconceptoppalmod = fila.getCodconceptoppalmod();
    	this.desconceptoppalmod = fila.getDesconceptoppalmod();
    	this.codriesgocubierto = fila.getCodriesgocubierto();
    	this.desriesgocubierto = fila.getDesriesgocubierto();
    	this.codconcepto = fila.getCodconcepto();
    	this.nomconcepto = fila.getNomconcepto();
        this.codvalor = fila.getCodvalor();
        this.desvalor = fila.getDesvalor();
        this.datovinculado = fila.getDatovinculado();
        this.filamodulo = fila.getFilamodulo();
        this.columnamodulo = fila.getColumnamodulo();
        this.filavinculada = fila.getFilavinculada();
        this.elegible = fila.getElegible();
        this.filaelegible = fila.getFilaelegible();
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
	public FilaCompVinculada getFilavinculada() {
		return filavinculada;
	}
	public void setFilavinculada(FilaCompVinculada filavinculada) {
		this.filavinculada = filavinculada;
	}

	public String getElegible() {
		return elegible;
	}

	public void setElegible(String elegible) {
		this.elegible = elegible;
	}

	public FilaCompElegible getFilaelegible() {
		return filaelegible;
	}

	public void setFilaelegible(FilaCompElegible filaelegible) {
		this.filaelegible = filaelegible;
	}

	public List<FilaCompVinculada> getFilasVinculadas() {
		return filasVinculadas;
	}

	public void setFilasVinculadas(List<FilaCompVinculada> filasVinculadas) {
		this.filasVinculadas = filasVinculadas;
	}
	
	
}
