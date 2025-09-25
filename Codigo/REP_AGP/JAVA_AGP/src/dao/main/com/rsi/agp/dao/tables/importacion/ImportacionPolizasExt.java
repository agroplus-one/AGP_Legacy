package com.rsi.agp.dao.tables.importacion;

import java.io.Serializable;
import java.util.Date;

public class ImportacionPolizasExt implements Serializable {

	private static final long serialVersionUID = -8300420536643041359L;
	
	private Long id;
	private Integer plan;
	private Integer linea;
	private String codmodulo;
	private String referencia;
	private String tipoRef;
	private Integer estado;
	private Date fecImportacion;
	private String detalle;
	private Long idEnvio;

	public ImportacionPolizasExt() {
	}

	public ImportacionPolizasExt(final Long id) {
		this.id = id;
	}

	public ImportacionPolizasExt(final Long id, final Integer plan,
			final Integer linea, final String codmodulo,final String referencia, final String tipoRef,
			final Integer estado, final Date fecImportacion,
			final String detalle, final Long idEnvio) {
		this.id = id;
		this.plan = plan;
		this.linea = linea;
		this.codmodulo = codmodulo;
		this.referencia = referencia;
		this.tipoRef = tipoRef;
		this.estado = estado;
		this.fecImportacion = fecImportacion;
		this.detalle = detalle;
		this.idEnvio = idEnvio;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Integer getPlan() {
		return plan;
	}

	public void setPlan(final Integer plan) {
		this.plan = plan;
	}

	public Integer getLinea() {
		return linea;
	}

	public void setLinea(final Integer linea) {
		this.linea = linea;
	}
	
	public String getCodmodulo() {
		return codmodulo;
	}

	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}
	
	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(final String referencia) {
		this.referencia = referencia;
	}

	public String getTipoRef() {
		return tipoRef;
	}

	public void setTipoRef(final String tipoRef) {
		this.tipoRef = tipoRef;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(final Integer estado) {
		this.estado = estado;
	}

	public Date getFecImportacion() {
		return fecImportacion;
	}

	public void setFecImportacion(final Date fecImportacion) {
		this.fecImportacion = fecImportacion;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(final String detalle) {
		this.detalle = detalle;
	}

	public Long getIdEnvio() {
		return idEnvio;
	}

	public void setIdEnvio(final Long idEnvio) {
		this.idEnvio = idEnvio;
	}

}