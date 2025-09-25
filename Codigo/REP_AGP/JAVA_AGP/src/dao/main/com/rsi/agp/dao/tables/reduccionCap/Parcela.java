package com.rsi.agp.dao.tables.reduccionCap;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Parcela implements java.io.Serializable {

	private static final long serialVersionUID = 2712205892426898604L;
	
	private Long id;
	private ReduccionCapital reduccionCapital;
	private com.rsi.agp.dao.tables.poliza.Parcela parcela;
	private Character altaenanexo;
	private Long idparcelacopy;
	private BigDecimal codprovincia;
	private BigDecimal codcomarca;
	private BigDecimal codtermino;
	private Character subtermino;
	private String poligono;
	private String parcela_1;
	private BigDecimal codprovsigpac;
	private BigDecimal codtermsigpac;
	private BigDecimal agrsigpac;
	private BigDecimal zonasigpac;
	private BigDecimal poligonosigpac;
	private BigDecimal parcelasigpac;
	private BigDecimal recintosigpac;
	private String nomparcela;
	private BigDecimal hoja;
	private BigDecimal numero;
	private BigDecimal codcultivo;
	private BigDecimal codvariedad;
	private Set<CapitalAsegurado> capitalAsegurados = new HashSet<CapitalAsegurado>(0);

	public Parcela() {
		this.reduccionCapital = new ReduccionCapital();
		this.parcela = new com.rsi.agp.dao.tables.poliza.Parcela();
	}

	public Parcela(Long id, ReduccionCapital reduccionCapital, BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, BigDecimal hoja, BigDecimal numero, BigDecimal codcultivo, BigDecimal codvariedad) {
		this.id = id;
		this.reduccionCapital = reduccionCapital;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.hoja = hoja;
		this.numero = numero;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
	}

	public Parcela(Long id, ReduccionCapital reduccionCapital, com.rsi.agp.dao.tables.poliza.Parcela parcela,
			Character altaenanexo, Long idparcelacopy, BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, Character subtermino, String poligono, String parcela_1, BigDecimal codprovsigpac,
			BigDecimal codtermsigpac, BigDecimal agrsigpac, BigDecimal zonasigpac, BigDecimal poligonosigpac,
			BigDecimal parcelasigpac, BigDecimal recintosigpac, String nomparcela, BigDecimal hoja, BigDecimal numero,
			BigDecimal codcultivo, BigDecimal codvariedad, Set<CapitalAsegurado> capitalAsegurados) {
		this.id = id;
		this.reduccionCapital = reduccionCapital;
		this.parcela = parcela;
		this.altaenanexo = altaenanexo;
		this.idparcelacopy = idparcelacopy;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		this.poligono = poligono;
		this.parcela_1 = parcela_1;
		this.codprovsigpac = codprovsigpac;
		this.codtermsigpac = codtermsigpac;
		this.agrsigpac = agrsigpac;
		this.zonasigpac = zonasigpac;
		this.poligonosigpac = poligonosigpac;
		this.parcelasigpac = parcelasigpac;
		this.recintosigpac = recintosigpac;
		this.nomparcela = nomparcela;
		this.hoja = hoja;
		this.numero = numero;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.capitalAsegurados = capitalAsegurados;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReduccionCapital getReduccionCapital() {
		return this.reduccionCapital;
	}

	public void setReduccionCapital(ReduccionCapital reduccionCapital) {
		this.reduccionCapital = reduccionCapital;
	}

	public com.rsi.agp.dao.tables.poliza.Parcela getParcela() {
		return this.parcela;
	}

	public void setParcela(com.rsi.agp.dao.tables.poliza.Parcela parcela) {
		this.parcela = parcela;
	}

	public Character getAltaenanexo() {
		return this.altaenanexo;
	}

	public void setAltaenanexo(Character altaenanexo) {
		this.altaenanexo = altaenanexo;
	}

	public Long getIdparcelacopy() {
		return this.idparcelacopy;
	}

	public void setIdparcelacopy(Long idparcelacopy) {
		this.idparcelacopy = idparcelacopy;
	}

	public BigDecimal getCodprovincia() {
		return this.codprovincia;
	}

	public void setCodprovincia(BigDecimal codprovincia) {
		this.codprovincia = codprovincia;
	}

	public BigDecimal getCodcomarca() {
		return this.codcomarca;
	}

	public void setCodcomarca(BigDecimal codcomarca) {
		this.codcomarca = codcomarca;
	}

	public BigDecimal getCodtermino() {
		return this.codtermino;
	}

	public void setCodtermino(BigDecimal codtermino) {
		this.codtermino = codtermino;
	}

	public Character getSubtermino() {
		return this.subtermino;
	}

	public void setSubtermino(Character subtermino) {
		this.subtermino = subtermino;
	}

	public String getPoligono() {
		return this.poligono;
	}

	public void setPoligono(String poligono) {
		this.poligono = poligono;
	}

	public String getParcela_1() {
		return this.parcela_1;
	}

	public void setParcela_1(String parcela_1) {
		this.parcela_1 = parcela_1;
	}

	public BigDecimal getCodprovsigpac() {
		return this.codprovsigpac;
	}

	public void setCodprovsigpac(BigDecimal codprovsigpac) {
		this.codprovsigpac = codprovsigpac;
	}

	public BigDecimal getCodtermsigpac() {
		return this.codtermsigpac;
	}

	public void setCodtermsigpac(BigDecimal codtermsigpac) {
		this.codtermsigpac = codtermsigpac;
	}

	public BigDecimal getAgrsigpac() {
		return this.agrsigpac;
	}

	public void setAgrsigpac(BigDecimal agrsigpac) {
		this.agrsigpac = agrsigpac;
	}

	public BigDecimal getZonasigpac() {
		return this.zonasigpac;
	}

	public void setZonasigpac(BigDecimal zonasigpac) {
		this.zonasigpac = zonasigpac;
	}

	public BigDecimal getPoligonosigpac() {
		return this.poligonosigpac;
	}

	public void setPoligonosigpac(BigDecimal poligonosigpac) {
		this.poligonosigpac = poligonosigpac;
	}

	public BigDecimal getParcelasigpac() {
		return this.parcelasigpac;
	}

	public void setParcelasigpac(BigDecimal parcelasigpac) {
		this.parcelasigpac = parcelasigpac;
	}

	public BigDecimal getRecintosigpac() {
		return this.recintosigpac;
	}

	public void setRecintosigpac(BigDecimal recintosigpac) {
		this.recintosigpac = recintosigpac;
	}

	public String getNomparcela() {
		return this.nomparcela;
	}

	public void setNomparcela(String nomparcela) {
		this.nomparcela = nomparcela;
	}

	public BigDecimal getHoja() {
		return this.hoja;
	}

	public void setHoja(BigDecimal hoja) {
		this.hoja = hoja;
	}

	public BigDecimal getNumero() {
		return this.numero;
	}

	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}

	public BigDecimal getCodcultivo() {
		return this.codcultivo;
	}

	public void setCodcultivo(BigDecimal codcultivo) {
		this.codcultivo = codcultivo;
	}

	public BigDecimal getCodvariedad() {
		return this.codvariedad;
	}

	public void setCodvariedad(BigDecimal codvariedad) {
		this.codvariedad = codvariedad;
	}

	public Set<CapitalAsegurado> getCapitalAsegurados() {
		return this.capitalAsegurados;
	}

	public void setCapitalAsegurados(Set<CapitalAsegurado> capitalAsegurados) {
		this.capitalAsegurados = capitalAsegurados;
	}

}
