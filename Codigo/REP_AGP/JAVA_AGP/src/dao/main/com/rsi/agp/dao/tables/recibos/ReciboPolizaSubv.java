package com.rsi.agp.dao.tables.recibos;

import java.math.BigDecimal;

import com.rsi.agp.dao.tables.cgen.Organismo;

public class ReciboPolizaSubv implements java.io.Serializable {

	private static final long serialVersionUID = 2257183808838301806L;

	private Long id;
	private ReciboPoliza reciboPoliza;
	private Character codorganismo;
	private BigDecimal subvccaa;
	private Organismo organismo;

	public ReciboPolizaSubv() {
		super();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReciboPoliza getReciboPoliza() {
		return this.reciboPoliza;
	}

	public void setReciboPoliza(ReciboPoliza reciboPoliza) {
		this.reciboPoliza = reciboPoliza;
	}

	public Character getCodorganismo() {
		return this.codorganismo;
	}

	public void setCodorganismo(Character codorganismo) {
		this.codorganismo = codorganismo;
	}

	public BigDecimal getSubvccaa() {
		return this.subvccaa;
	}

	public void setSubvccaa(BigDecimal subvccaa) {
		this.subvccaa = subvccaa;
	}

	public Organismo getOrganismo() {
		return organismo;
	}

	public void setOrganismo(Organismo organismo) {
		this.organismo = organismo;
	}

}
