package com.rsi.agp.dao.tables.poliza;

public class ComsPctCalculadoId implements java.io.Serializable {

	private static final long serialVersionUID = 186586759625645188L;

	private Long idComparativa;
	private Character idGrupo;

	public ComsPctCalculadoId() {
		super();
	}

	public ComsPctCalculadoId(final Long idComparativa, final Character idGrupo) {
		this();
		this.idComparativa = idComparativa;
		this.idGrupo = idGrupo;
	}

	public Long getIdComparativa() {
		return this.idComparativa;
	}

	public void setIdComparativa(final Long idComparativa) {
		this.idComparativa = idComparativa;
	}

	public Character getIdGrupo() {
		return this.idGrupo;
	}

	public void setIdGrupo(final Character idGrupo) {
		this.idGrupo = idGrupo;
	}
}
