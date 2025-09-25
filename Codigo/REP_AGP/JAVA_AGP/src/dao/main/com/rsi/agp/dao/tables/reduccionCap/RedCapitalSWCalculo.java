package com.rsi.agp.dao.tables.reduccionCap;

import java.sql.Clob;
import java.util.Date;

public class RedCapitalSWCalculo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private ReduccionCapital reduccionCapital; // Cambiado siguiendo ejemplo A.M.
	private String codusuario;
	private Date fecha;
	private String cupon;
	private Character tipoPoliza;
	private Integer calcularSitAct;
	private transient Clob modificacionPlz; //dudas, en principio no aplica
	private transient Clob calculoModificacion; //dudas, en principio no aplica
	private transient Clob calculoOriginal;
	private transient Clob diferenciasCoste;
	private String msgErrorSw;

	public RedCapitalSWCalculo() {
		super();
	}

	public RedCapitalSWCalculo(Long id, ReduccionCapital reduccionCapital, String codusuario, Date fecha, String cupon,
			Character tipoPoliza, Integer calcularSitAct) {
		this();
		this.id = id;
		this.reduccionCapital = reduccionCapital;
		this.codusuario = codusuario;
		this.fecha = fecha;
		this.cupon = cupon;
		this.tipoPoliza = tipoPoliza;
		this.calcularSitAct = calcularSitAct;
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

	public String getCodusuario() {
		return this.codusuario;
	}

	public void setCodusuario(String codusuario) {
		this.codusuario = codusuario;
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getCupon() {
		return this.cupon;
	}

	public void setCupon(String cupon) {
		this.cupon = cupon;
	}

	public Character getTipoPoliza() {
		return this.tipoPoliza;
	}

	public void setTipoPoliza(Character tipoPoliza) {
		this.tipoPoliza = tipoPoliza;
	}

	public Integer getCalcularSitAct() {
		return this.calcularSitAct;
	}

	public void setCalcularSitAct(Integer calcularSitAct) {
		this.calcularSitAct = calcularSitAct;
	}

	public Clob getModificacionPlz() {
		return this.modificacionPlz;
	}

	public void setModificacionPlz(Clob modificacionPlz) {
		this.modificacionPlz = modificacionPlz;
	}

	public Clob getCalculoModificacion() {
		return this.calculoModificacion;
	}

	public void setCalculoModificacion(Clob calculoModificacion) {
		this.calculoModificacion = calculoModificacion;
	}

	public Clob getCalculoOriginal() {
		return this.calculoOriginal;
	}

	public void setCalculoOriginal(Clob calculoOriginal) {
		this.calculoOriginal = calculoOriginal;
	}

	public Clob getDiferenciasCoste() {
		return this.diferenciasCoste;
	}

	public void setDiferenciasCoste(Clob diferenciasCoste) {
		this.diferenciasCoste = diferenciasCoste;
	}

	public String getMsgErrorSw() {
		return msgErrorSw;
	}

	public void setMsgErrorSw(String msgErrorSw) {
		this.msgErrorSw = msgErrorSw;
	}

}
