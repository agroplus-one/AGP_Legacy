package com.rsi.agp.dao.tables.reduccionCap;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class RedCapitalDistribucionCostes implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private RedCapitalDistribucionCostesId id;
	private BigDecimal primaComercial;
	private BigDecimal primaComercialNeta;
	private BigDecimal recargoConsorcio;
	private BigDecimal reciboPrima;
	private BigDecimal costeTomador;
	private BigDecimal totalCosteTomador;
	private BigDecimal recargoAval;
	private BigDecimal recargoFraccionamiento;
	private Set<RedCapitalBonifRecargos> redCapitalBonifRecargos = new HashSet<RedCapitalBonifRecargos>(0);
	private Set<RedCapitalSubvCCAA> redCapitalSubvCCAAs = new HashSet<RedCapitalSubvCCAA>(0);
	private Set<RedCapitalSubvEnesa> redCapitalSubvEnesas = new HashSet<RedCapitalSubvEnesa>(0);
	private Character grupoNegocio;
	private String descGrupoNegocio;

	public RedCapitalDistribucionCostes() {
	}

	public RedCapitalDistribucionCostes(RedCapitalDistribucionCostesId id, BigDecimal primaComercial,
			BigDecimal primaComercialNeta, BigDecimal recargoConsorcio, BigDecimal reciboPrima, BigDecimal costeTomador,
			BigDecimal totalCosteTomador) {
		this.id = id;
		this.primaComercial = primaComercial;
		this.primaComercialNeta = primaComercialNeta;
		this.recargoConsorcio = recargoConsorcio;
		this.reciboPrima = reciboPrima;
		this.costeTomador = costeTomador;
		this.totalCosteTomador = totalCosteTomador;
	}

	public RedCapitalDistribucionCostes(RedCapitalDistribucionCostesId id, BigDecimal primaComercial,
			BigDecimal primaComercialNeta, BigDecimal recargoConsorcio, BigDecimal reciboPrima, BigDecimal costeTomador,
			BigDecimal totalCosteTomador, BigDecimal recargoAval, BigDecimal recargoFraccionamiento,
			Set<RedCapitalSubvCCAA> redCapitalSubvCCAAs, Set<RedCapitalSubvEnesa> redCapitalSubvEnesas,
			Set<RedCapitalBonifRecargos> redCapitalBonifRecargos) {
		this.id = id;
		this.primaComercial = primaComercial;
		this.primaComercialNeta = primaComercialNeta;
		this.recargoConsorcio = recargoConsorcio;
		this.reciboPrima = reciboPrima;
		this.costeTomador = costeTomador;
		this.totalCosteTomador = totalCosteTomador;
		this.recargoAval = recargoAval;
		this.recargoFraccionamiento = recargoFraccionamiento;
		/*this.redCapitalSubvCCAAs = redCapitalSubvCCAAs;
		this.redCapitalSubvEnesas = redCapitalSubvEnesas;
		this.redCapitalBonifRecargos = redCapitalBonifRecargos;*/
	}

	public RedCapitalDistribucionCostesId getId() {
		return this.id;
	}

	public void setId(RedCapitalDistribucionCostesId id) {
		this.id = id;
	}

	public BigDecimal getPrimaComercial() {
		return this.primaComercial;
	}

	public void setPrimaComercial(BigDecimal primaComercial) {
		this.primaComercial = primaComercial;
	}

	public BigDecimal getPrimaComercialNeta() {
		return this.primaComercialNeta;
	}

	public void setPrimaComercialNeta(BigDecimal primaComercialNeta) {
		this.primaComercialNeta = primaComercialNeta;
	}

	public BigDecimal getRecargoConsorcio() {
		return this.recargoConsorcio;
	}

	public void setRecargoConsorcio(BigDecimal recargoConsorcio) {
		this.recargoConsorcio = recargoConsorcio;
	}

	public BigDecimal getReciboPrima() {
		return this.reciboPrima;
	}

	public void setReciboPrima(BigDecimal reciboPrima) {
		this.reciboPrima = reciboPrima;
	}

	public BigDecimal getCosteTomador() {
		return this.costeTomador;
	}

	public void setCosteTomador(BigDecimal costeTomador) {
		this.costeTomador = costeTomador;
	}

	public BigDecimal getTotalCosteTomador() {
		return this.totalCosteTomador;
	}

	public void setTotalCosteTomador(BigDecimal totalCosteTomador) {
		this.totalCosteTomador = totalCosteTomador;
	}

	public BigDecimal getRecargoAval() {
		return this.recargoAval;
	}

	public void setRecargoAval(BigDecimal recargoAval) {
		this.recargoAval = recargoAval;
	}

	public BigDecimal getRecargoFraccionamiento() {
		return this.recargoFraccionamiento;
	}

	public void setRecargoFraccionamiento(BigDecimal recargoFraccionamiento) {
		this.recargoFraccionamiento = recargoFraccionamiento;
	}

	public Set<RedCapitalBonifRecargos> getRedCapitalBonifRecargos() {
		return this.redCapitalBonifRecargos;
	}

	public void setRedCapitalBonifRecargos(Set<RedCapitalBonifRecargos> redCapitalBonifRecargos) {
		this.redCapitalBonifRecargos = redCapitalBonifRecargos;
	}	
	
	public Set<RedCapitalSubvCCAA> getRedCapitalSubvCCAAs() {
		return redCapitalSubvCCAAs;
	}

	public void setRedCapitalSubvCCAAs(Set<RedCapitalSubvCCAA> redCapitalSubvCCAAs) {
		this.redCapitalSubvCCAAs = redCapitalSubvCCAAs;
	}

	public Set<RedCapitalSubvEnesa> getRedCapitalSubvEnesas() {
		return redCapitalSubvEnesas;
	}

	public void setRedCapitalSubvEnesas(Set<RedCapitalSubvEnesa> redCapitalSubvEnesas) {
		this.redCapitalSubvEnesas = redCapitalSubvEnesas;
	}

	public Character getGrupoNegocio() {
		return grupoNegocio;
	}

	public void setGrupoNegocio(Character grupoNegocio) {
		this.grupoNegocio = grupoNegocio;
	}

	public String getDescGrupoNegocio() {
		return descGrupoNegocio;
	}

	public void setDescGrupoNegocio(String descGrupoNegocio) {
		this.descGrupoNegocio = descGrupoNegocio;
	}

}