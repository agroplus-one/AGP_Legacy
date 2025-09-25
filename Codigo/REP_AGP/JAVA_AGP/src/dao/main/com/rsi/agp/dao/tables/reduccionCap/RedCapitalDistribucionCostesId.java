package com.rsi.agp.dao.tables.reduccionCap;

public class RedCapitalDistribucionCostesId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	// validar la clase cuando las nuevas tablas para la peticion esten creadas

	private Long idanexo;
	private Integer tipoDc;
	private Character grupoNegocio;

	public RedCapitalDistribucionCostesId() {
	}

	public RedCapitalDistribucionCostesId(Long idanexo, Integer tipoDc, Character grupoNegocio) {
		this.idanexo = idanexo;
		this.tipoDc = tipoDc;
		this.grupoNegocio = grupoNegocio;
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

	public Character getGrupoNegocio() {
		return this.grupoNegocio;
	}

	public void setGrupoNegocio(Character grupoNegocio) {
		this.grupoNegocio = grupoNegocio;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof RedCapitalDistribucionCostesId))
			return false;
		RedCapitalDistribucionCostesId castOther = (RedCapitalDistribucionCostesId) other;

		return ((this.getIdanexo() == castOther.getIdanexo()) || (this.getIdanexo() != null
				&& castOther.getIdanexo() != null && this.getIdanexo().equals(castOther.getIdanexo())))
				&& ((this.getTipoDc() == castOther.getTipoDc()) || (this.getTipoDc() != null
						&& castOther.getTipoDc() != null && this.getTipoDc().equals(castOther.getTipoDc())))
				&& ((this.getGrupoNegocio() == castOther.getGrupoNegocio())
						|| (this.getGrupoNegocio() != null && castOther.getGrupoNegocio() != null
								&& this.getGrupoNegocio().equals(castOther.getGrupoNegocio())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getIdanexo() == null ? 0 : this.getIdanexo().hashCode());
		result = 37 * result + (getTipoDc() == null ? 0 : this.getTipoDc().hashCode());
		result = 37 * result + (getGrupoNegocio() == null ? 0 : this.getGrupoNegocio().hashCode());
		return result;
	}

}
