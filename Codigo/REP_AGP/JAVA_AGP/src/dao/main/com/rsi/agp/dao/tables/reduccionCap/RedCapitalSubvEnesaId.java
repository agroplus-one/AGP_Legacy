package com.rsi.agp.dao.tables.reduccionCap;

public class RedCapitalSubvEnesaId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long idanexo;
	private Integer tipoDc;
	private Integer tipoSubvencion;
	private char grupoNegocio;

	public RedCapitalSubvEnesaId() {
	}

	public RedCapitalSubvEnesaId(Long idanexo, Integer tipoDc, Integer tipoSubvencion, char grupoNegocio) {
		this.idanexo = idanexo;
		this.tipoDc = tipoDc;
		this.tipoSubvencion = tipoSubvencion;
		this.grupoNegocio = grupoNegocio;
	}

	public RedCapitalSubvEnesaId(RedCapitalDistribucionCostesId id, Integer tipoSubvencion, char grupoNegocio) {
		this.idanexo = id.getIdanexo();
		this.tipoDc = id.getTipoDc();
		this.grupoNegocio = id.getGrupoNegocio();
		this.tipoSubvencion = tipoSubvencion;
	}

	public Long getIdanexo() {
		return this.idanexo;
	}

	public void setIdanexo(Long idanexo) {
		this.idanexo = idanexo;
	}

	public Integer getTipoDc() {
		return this.tipoDc;
	}

	public void setTipoDc(Integer tipoDc) {
		this.tipoDc = tipoDc;
	}

	public Integer getTipoSubvencion() {
		return this.tipoSubvencion;
	}

	public void setTipoSubvencion(Integer tipoSubvencion) {
		this.tipoSubvencion = tipoSubvencion;
	}

	public char getGrupoNegocio() {
		return this.grupoNegocio;
	}

	public void setGrupoNegocio(char grupoNegocio) {
		this.grupoNegocio = grupoNegocio;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof RedCapitalSubvEnesaId))
			return false;
		RedCapitalSubvEnesaId castOther = (RedCapitalSubvEnesaId) other;

		return ((this.getIdanexo() == castOther.getIdanexo()) || (this.getIdanexo() != null
				&& castOther.getIdanexo() != null && this.getIdanexo().equals(castOther.getIdanexo())))
				&& ((this.getTipoDc() == castOther.getTipoDc()) || (this.getTipoDc() != null
						&& castOther.getTipoDc() != null && this.getTipoDc().equals(castOther.getTipoDc())))
				&& ((this.getTipoSubvencion() == castOther.getTipoSubvencion())
						|| (this.getTipoSubvencion() != null && castOther.getTipoSubvencion() != null
								&& this.getTipoSubvencion().equals(castOther.getTipoSubvencion())))
				&& (this.getGrupoNegocio() == castOther.getGrupoNegocio());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getIdanexo() == null ? 0 : this.getIdanexo().hashCode());
		result = 37 * result + (getTipoDc() == null ? 0 : this.getTipoDc().hashCode());
		result = 37 * result + (getTipoSubvencion() == null ? 0 : this.getTipoSubvencion().hashCode());
		result = 37 * result + this.getGrupoNegocio();
		return result;
	}

}