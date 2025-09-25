package com.rsi.agp.dao.tables.reduccionCap;

public class RedCapitalBonifRecargosId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long idanexo;
	private Integer tipoDc;
	private Integer codigo;
	private Character grupoNegocio;

	public RedCapitalBonifRecargosId() {
	}

	public RedCapitalBonifRecargosId(Long idanexo, Integer tipoDc, Integer codigo, Character grupoNegocio) {
		this.idanexo = idanexo;
		this.tipoDc = tipoDc;
		this.codigo = codigo;
		this.grupoNegocio = grupoNegocio;
	}

	public RedCapitalBonifRecargosId(RedCapitalDistribucionCostesId id, Integer codigo, Character grupoNegocio) {
		this.idanexo = id.getIdanexo();
		this.tipoDc = id.getTipoDc();
		this.codigo = codigo;
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

	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
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
		if (!(other instanceof RedCapitalBonifRecargosId))
			return false;
		RedCapitalBonifRecargosId castOther = (RedCapitalBonifRecargosId) other;

		return ((this.getIdanexo() == castOther.getIdanexo()) || (this.getIdanexo() != null
				&& castOther.getIdanexo() != null && this.getIdanexo().equals(castOther.getIdanexo())))
				&& ((this.getTipoDc() == castOther.getTipoDc()) || (this.getTipoDc() != null
						&& castOther.getTipoDc() != null && this.getTipoDc().equals(castOther.getTipoDc())))
				&& ((this.getCodigo() == castOther.getCodigo()) || (this.getCodigo() != null
						&& castOther.getCodigo() != null && this.getCodigo().equals(castOther.getCodigo())))
				&& ((this.getGrupoNegocio() == castOther.getGrupoNegocio())
						|| (this.getGrupoNegocio() != null && castOther.getGrupoNegocio() != null
								&& this.getGrupoNegocio().equals(castOther.getGrupoNegocio())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getIdanexo() == null ? 0 : this.getIdanexo().hashCode());
		result = 37 * result + (getTipoDc() == null ? 0 : this.getTipoDc().hashCode());
		result = 37 * result + (getCodigo() == null ? 0 : this.getCodigo().hashCode());
		result = 37 * result + (getGrupoNegocio() == null ? 0 : this.getGrupoNegocio().hashCode());
		return result;
	}

}