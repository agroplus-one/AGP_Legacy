package com.rsi.agp.dao.tables.ged;

/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
import java.util.Date;
import com.rsi.agp.dao.tables.poliza.CanalFirma;
import com.rsi.agp.dao.tables.poliza.Poliza;
/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */

public class GedDocPoliza implements java.io.Serializable {

	private static final long serialVersionUID = 7920868641310913651L;
	
	/* P73325 - RQ.04, RQ.05 y RQ.06  */
	private Long idPoliza;
	private Poliza poliza;
	private String idDocumentum;
	private CanalFirma canalFirma;
	private Character docFirmada;
	private Date fechaFirma;
	private String codUsuario;
	private String codBarras;
	
	public GedDocPoliza() {
		super();
	}
	
	public Long getIdPoliza() {
		return idPoliza;
	}

	public void setIdPoliza(Long idpoliza) {
		this.idPoliza = idpoliza;
	}

	public Poliza getPoliza() {
		return poliza;
	}
	public void setPoliza(Poliza poliza) {
		this.poliza = poliza;
	}

	public String getIdDocumentum() {
		return idDocumentum;
	}

	public void setIdDocumentum(String idDocumentum) {
		this.idDocumentum = idDocumentum;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public CanalFirma getCanalFirma() {
		return canalFirma;
	}

	public void setCanalFirma(CanalFirma canalFirma) {
		this.canalFirma = canalFirma;
	}
	public String getCodBarras() {
		return codBarras;
	}
	public void setCodBarras(String codBarras) {
		this.codBarras = codBarras;
	}
}