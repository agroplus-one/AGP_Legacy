package com.rsi.agp.dao.tables.poliza.dc2015;


import java.math.BigDecimal;

public class DistCosteExplotacionesGrupoNegocio implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6646376549711015799L;
	private Long id;
	private DistCosteExplotaciones distCosteExplotaciones;
	private Character gruponegocio;
	private BigDecimal costetomador;
	private BigDecimal primacomercial;
	private BigDecimal primacomercialneta;
	private BigDecimal recargoconsorcio;
	private BigDecimal reciboprima;

	public DistCosteExplotacionesGrupoNegocio() {
	}


	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Character getGruponegocio() {
		return this.gruponegocio;
	}

	public void setGruponegocio(Character gruponegocio) {
		this.gruponegocio = gruponegocio;
	}

	public BigDecimal getCostetomador() {
		return this.costetomador;
	}

	public void setCostetomador(BigDecimal costetomador) {
		this.costetomador = costetomador;
	}

	public BigDecimal getPrimacomercial() {
		return primacomercial;
	}

	public void setPrimacomercial(BigDecimal primacomercial) {
		this.primacomercial = primacomercial;
	}

	public BigDecimal getPrimacomercialneta() {
		return primacomercialneta;
	}

	public void setPrimacomercialneta(BigDecimal primacomercialneta) {
		this.primacomercialneta = primacomercialneta;
	}

	public BigDecimal getRecargoconsorcio() {
		return recargoconsorcio;
	}

	public void setRecargoconsorcio(BigDecimal recargoconsorcio) {
		this.recargoconsorcio = recargoconsorcio;
	}

	public BigDecimal getReciboprima() {
		return reciboprima;
	}

	public void setReciboprima(BigDecimal reciboprima) {
		this.reciboprima = reciboprima;
	}


	public DistCosteExplotaciones getDistCosteExplotaciones() {
		return distCosteExplotaciones;
	}


	public void setDistCosteExplotaciones(DistCosteExplotaciones distCosteExplotaciones) {
		this.distCosteExplotaciones = distCosteExplotaciones;
	}
}