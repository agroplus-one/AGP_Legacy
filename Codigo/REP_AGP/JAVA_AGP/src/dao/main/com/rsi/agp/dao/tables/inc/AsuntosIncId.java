package com.rsi.agp.dao.tables.inc;

import java.io.Serializable;

public class AsuntosIncId implements Serializable{

	private static final long serialVersionUID = -1619324838049171127L;
	
	private String codasunto;
	private Character catalogo;
	
	public AsuntosIncId() {
	}
	
	public AsuntosIncId(String codasunto, Character catalogo) {
		this.codasunto = codasunto;
		this.catalogo = catalogo;
	}

	public String getCodasunto() {
		return this.codasunto;
	}

	public void setCodasunto(String codasunto) {
		this.codasunto = codasunto;
	}

	public Character getCatalogo() {
		return this.catalogo;
	}

	public void setCatalogo(Character catalogo) {
		this.catalogo = catalogo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catalogo == null) ? 0 : catalogo.hashCode());
		result = prime * result + ((codasunto == null) ? 0 : codasunto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AsuntosIncId other = (AsuntosIncId) obj;
		if (catalogo == null) {
			if (other.catalogo != null)
				return false;
		} else if (!catalogo.equals(other.catalogo))
			return false;
		if (codasunto == null) {
			if (other.codasunto != null)
				return false;
		} else if (!codasunto.equals(other.codasunto))
			return false;
		return true;
	}
	
}
