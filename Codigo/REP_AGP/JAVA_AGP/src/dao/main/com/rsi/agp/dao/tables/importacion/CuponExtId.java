package com.rsi.agp.dao.tables.importacion;

public class CuponExtId implements java.io.Serializable {

	private static final long serialVersionUID = -1513694695823737353L;

	private Long idpoliza;
	private String idcupon;

	public CuponExtId() {
	}

	public CuponExtId(Long idpoliza, String idcupon) {
		this.idpoliza = idpoliza;
		this.idcupon = idcupon;
	}

	public Long getIdpoliza() {
		return idpoliza;
	}

	public void setIdpoliza(Long idpoliza) {
		this.idpoliza = idpoliza;
	}

	public String getIdcupon() {
		return idcupon;
	}

	public void setIdcupon(String idcupon) {
		this.idcupon = idcupon;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof CuponExtId))
			return false;
		CuponExtId castOther = (CuponExtId) other;
		return ((this.getIdpoliza() == castOther.getIdpoliza()) || (this.getIdpoliza() != null
				&& castOther.getIdpoliza() != null && this.getIdpoliza().equals(castOther.getIdpoliza())))
				&& ((this.getIdcupon() == castOther.getIdcupon()) || (this.getIdcupon() != null
						&& castOther.getIdcupon() != null && this.getIdcupon().equals(castOther.getIdcupon())));
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + (getIdpoliza() == null ? 0 : this.getIdpoliza().hashCode());
		result = 37 * result + (getIdcupon() == null ? 0 : this.getIdcupon().hashCode());
		return result;
	}
}
