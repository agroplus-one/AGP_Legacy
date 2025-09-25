package com.rsi.agp.dao.tables.reduccionCap;

public class RedCapitalSubvCCAAId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long idanexo;
	private Integer tipoDc;
	private Character codOrganismo;
	private char grupoNegocio;

	public RedCapitalSubvCCAAId() {
	}

	public RedCapitalSubvCCAAId(Long idanexo, Integer tipoDc, Character codOrganismo, char grupoNegocio) {
		this.idanexo = idanexo;
		this.tipoDc = tipoDc;
		this.codOrganismo = codOrganismo;
		this.grupoNegocio = grupoNegocio;
	}

	public RedCapitalSubvCCAAId(RedCapitalDistribucionCostesId id, Character codOrganismo, char grupoNegocio) {
		this.idanexo = id.getIdanexo();
		this.tipoDc = id.getTipoDc();
		this.grupoNegocio = id.getGrupoNegocio();
		this.codOrganismo = codOrganismo;
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

	public Character getCodOrganismo() {
		return this.codOrganismo;
	}

	public void setCodOrganismo(Character codOrganismo) {
		this.codOrganismo = codOrganismo;
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
		if (!(other instanceof RedCapitalSubvCCAAId))
			return false;
		RedCapitalSubvCCAAId castOther = (RedCapitalSubvCCAAId) other;

		return ((this.getIdanexo() == castOther.getIdanexo()) || (this.getIdanexo() != null
				&& castOther.getIdanexo() != null && this.getIdanexo().equals(castOther.getIdanexo())))
				&& ((this.getTipoDc() == castOther.getTipoDc()) || (this.getTipoDc() != null
						&& castOther.getTipoDc() != null && this.getTipoDc().equals(castOther.getTipoDc())))
				&& ((this.getCodOrganismo() == castOther.getCodOrganismo())
						|| (this.getCodOrganismo() != null && castOther.getCodOrganismo() != null
								&& this.getCodOrganismo().equals(castOther.getCodOrganismo())))
				&& (this.getGrupoNegocio() == castOther.getGrupoNegocio());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getIdanexo() == null ? 0 : this.getIdanexo().hashCode());
		result = 37 * result + (getTipoDc() == null ? 0 : this.getTipoDc().hashCode());
		result = 37 * result + (getCodOrganismo() == null ? 0 : this.getCodOrganismo().hashCode());
		result = 37 * result + this.getGrupoNegocio();
		return result;
	}

}