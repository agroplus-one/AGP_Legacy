package com.rsi.agp.dao.tables.poliza.dc2015;


import java.math.BigDecimal;

public class DistCosteExplotacionesBonifRec implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8575770025560134593L;
	private Long id;
	private DistCosteExplotaciones distCosteExplotaciones;
	private BigDecimal codigo;
	private BigDecimal importe;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public DistCosteExplotaciones getDistCosteExplotaciones() {
		return distCosteExplotaciones;
	}
	public void setDistCosteExplotaciones(DistCosteExplotaciones distCosteExplotaciones) {
		this.distCosteExplotaciones = distCosteExplotaciones;
	}
	public BigDecimal getCodigo() {
		return codigo;
	}
	public void setCodigo(BigDecimal codigo) {
		this.codigo = codigo;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

}