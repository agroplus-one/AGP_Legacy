package com.rsi.agp.dao.tables.poliza;

import java.util.HashSet;
import java.util.Set;

import com.rsi.agp.dao.tables.ged.GedDocPoliza;
import com.rsi.agp.dao.tables.ged.GedDocPolizaSbp;

/**
* P0073325 - RQ.04, RQ.05, RQ.06, RQ.10, RQ.11 y RQ.12
*/
public class CanalFirma implements java.io.Serializable {

	private static final long serialVersionUID = -6993990313458107035L;

	private Long idCanal;
	private String nombreCanal;
	
	/**
	* P0073325 -RQ.10, RQ.11 y RQ.12
	*/
	private Set<GedDocPoliza> gedDocPoliza = new HashSet<GedDocPoliza>(0);
	private Set<GedDocPolizaSbp> gedDocPolizaSbp = new HashSet<GedDocPolizaSbp>(0);
	
	public Long getIdCanal() {
		return idCanal;
	}
	public void setIdCanal(Long idCanal) {
		this.idCanal = idCanal;
	}
	public String getNombreCanal() {
		return nombreCanal;
	}
	public void setNombreCanal(String nombreCanal) {
		this.nombreCanal = nombreCanal;
	}
	
	/**
	* P0073325 -RQ.10, RQ.11 y RQ.12
	*/
	public Set<GedDocPolizaSbp> getGedDocPolizaSbp() {
		return gedDocPolizaSbp;
	}
	public void setGedDocPolizaSbp(Set<GedDocPolizaSbp> gedDocPolizaSbp) {
		this.gedDocPolizaSbp = gedDocPolizaSbp;
	}
	public Set<GedDocPoliza> getGedDocPoliza() {
		return gedDocPoliza;
	}
	public void setGedDocPoliza(Set<GedDocPoliza> gedDocPoliza) {
		this.gedDocPoliza = gedDocPoliza;
	}
}
