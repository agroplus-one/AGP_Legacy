package com.rsi.agp.dao.tables.doc;

import com.rsi.agp.dao.tables.commons.Perfil;

public class DocAgroseguroPerfiles implements java.io.Serializable {

	private static final long serialVersionUID = -2451007441199452520L;
	private Long id;
	private DocAgroseguro docAgroseguro;
	private Perfil perfil;

	public DocAgroseguroPerfiles() {
	}

	public DocAgroseguroPerfiles(Long id, DocAgroseguro docAgroseguro, Perfil perfil) {
		this.id = id;
		this.docAgroseguro = docAgroseguro;
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
	public DocAgroseguro getDocAgroseguro() {
		return docAgroseguro;
	}

	/**
	 * @param errorWsAccion
	 *            the errorWsAccion to set
	 */
	public void setDocAgroseguro(DocAgroseguro docAgroseguro) {
		this.docAgroseguro = docAgroseguro;
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
