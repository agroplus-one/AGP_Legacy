package com.rsi.agp.dao.tables.ged;

import java.util.Date;

import com.rsi.agp.dao.tables.poliza.CanalFirma;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

/**
* P0073325 - RQ.10, RQ.11 y RQ.12
*/
public class GedDocPolizaSbp implements java.io.Serializable {
	
	private static final long serialVersionUID = -1439105823391659754L;
	
	private Long idPolizaSbp;
	private PolizaSbp polizaSbp;
	private String idDocumentum;
	private CanalFirma canalFirma;
	private Character docFirmada;
	private Date fechaFirma;
	private String codUsuario;
	private String codBarras;
	
	public GedDocPolizaSbp() {
		super();
	}
	
	public PolizaSbp getPolizaSbp() {
		return polizaSbp;
	}
	public void setPolizaSbp(PolizaSbp polizaSbp) {
		this.polizaSbp = polizaSbp;
	}
	public String getIdDocumentum() {
		return idDocumentum;
	}
	public void setIdDocumentum(String idDocumentum) {
		this.idDocumentum = idDocumentum;
	}
	public CanalFirma getCanalFirma() {
		return canalFirma;
	}
	public void setCanalFirma(CanalFirma canalFirma) {
		this.canalFirma = canalFirma;
	}
	public Character getDocFirmada() {
		return docFirmada;
	}
	public void setDocFirmada(Character docFirmada) {
		this.docFirmada = docFirmada;
	}
	public Date getFechaFirma() {
		return fechaFirma;
	}
	public void setFechaFirma(Date fechaFirma) {
		this.fechaFirma = fechaFirma;
	}
	public String getCodUsuario() {
		return codUsuario;
	}
	public void setCodUsuario(String codUsuario) {
		this.codUsuario = codUsuario;
	}
	public String getCodBarras() {
		return codBarras;
	}
	public void setCodBarras(String codBarras) {
		this.codBarras = codBarras;
	}
	public Long getIdPolizaSbp() {
		return idPolizaSbp;
	}
	public void setIdPolizaSbp(Long idPolizaSbp) {
		this.idPolizaSbp = idPolizaSbp;
	}
}