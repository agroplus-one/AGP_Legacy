package com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion;

import com.rsi.agp.core.webapp.util.StringUtils;

public class SolicitudReduccionCapBean {

	private Long id;
	private String idCupon;
	private int idEstadoPpal;
	private String estadoPpal;
	private int idEstadoCpl;
	private String estadoCpl;
	private String modifPpalCupon;
	private String modifCplCupon;
	private String modifPpalIdEstado;
	private String modifPpalEstado;
	private String modifCplIdEstado;
	private String modifCplEstado;
	private String error = "";

	public String getEstadoPpal() {
		return StringUtils.nullToString(estadoPpal);
	}

	public void setEstadoPpal(String estadoPpal) {
		this.estadoPpal = estadoPpal;
	}

	public String getEstadoCpl() {
		return StringUtils.nullToString(estadoCpl);
	}

	public void setEstadoCpl(String estadoCpl) {
		this.estadoCpl = estadoCpl;
	}

	public String getModifPpalCupon() {
		return StringUtils.nullToString(modifPpalCupon);
	}

	public void setModifPpalCupon(String modifPpalCupon) {
		this.modifPpalCupon = modifPpalCupon;
	}

	public String getModifCplCupon() {
		return StringUtils.nullToString(modifCplCupon);
	}

	public void setModifCplCupon(String modifCplCupon) {
		this.modifCplCupon = modifCplCupon;
	}

	public String getModifPpalEstado() {
		return StringUtils.nullToString(modifPpalEstado);
	}

	public void setModifPpalEstado(String modifPpalEstado) {
		this.modifPpalEstado = modifPpalEstado;
	}

	public String getModifCplEstado() {
		return StringUtils.nullToString(modifCplEstado);
	}

	public void setModifCplEstado(String modifCplEstado) {
		this.modifCplEstado = modifCplEstado;
	}

	public String getIdCupon() {
		return StringUtils.nullToString(idCupon);
	}

	public void setIdCupon(String idCupon) {
		this.idCupon = idCupon;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getModifPpalIdEstado() {
		return StringUtils.nullToString(modifPpalIdEstado);
	}

	public void setModifPpalIdEstado(String modifPpalIdEstado) {
		this.modifPpalIdEstado = modifPpalIdEstado;
	}

	public String getModifCplIdEstado() {
		return StringUtils.nullToString(modifCplIdEstado);
	}

	public void setModifCplIdEstado(String modifCplIdEstado) {
		this.modifCplIdEstado = modifCplIdEstado;
	}

	public int getIdEstadoPpal() {
		return idEstadoPpal;
	}

	public void setIdEstadoPpal(int idEstadoPpal) {
		this.idEstadoPpal = idEstadoPpal;
	}

	public int getIdEstadoCpl() {
		return idEstadoCpl;
	}

	public void setIdEstadoCpl(int idEstadoCpl) {
		this.idEstadoCpl = idEstadoCpl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
