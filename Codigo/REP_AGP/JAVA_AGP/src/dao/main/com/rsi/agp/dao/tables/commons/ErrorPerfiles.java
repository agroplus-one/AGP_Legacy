package com.rsi.agp.dao.tables.commons;

public class ErrorPerfiles implements java.io.Serializable {

	private static final long serialVersionUID = -2451007441199452520L;
	private Long id;
	private ErrorWsAccion errorWsAccion;
	private Perfil perfil;

	public ErrorPerfiles() {
	}

	public ErrorPerfiles(Long id, ErrorWsAccion errorWsAccion, Perfil perfil) {
		this.id = id;
		this.errorWsAccion = errorWsAccion;
		this.perfil = perfil;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the errorWsAccion
	 */
	public ErrorWsAccion getErrorWsAccion() {
		return errorWsAccion;
	}

	/**
	 * @param errorWsAccion
	 *            the errorWsAccion to set
	 */
	public void setErrorWsAccion(ErrorWsAccion errorWsAccion) {
		this.errorWsAccion = errorWsAccion;
	}

	/**
	 * @return the perfil
	 */
	public Perfil getPerfil() {
		return perfil;
	}

	/**
	 * @param perfil
	 *            the perfil to set
	 */
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

}
