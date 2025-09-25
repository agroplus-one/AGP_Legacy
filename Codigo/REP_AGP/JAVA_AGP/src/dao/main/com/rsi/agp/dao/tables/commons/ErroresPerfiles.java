package com.rsi.agp.dao.tables.commons;

public class ErroresPerfiles  implements java.io.Serializable {

     private Long id;     
     private ErrorWsAccion errorWsAccion;
     private Perfil perfil;

    private ErroresPerfiles() {
    }

    private ErroresPerfiles(Long id, ErrorWsAccion errorWsAccion, Perfil perfil) {
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
	 * @param id the id to set
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
	 * @param errorWsAccion the errorWsAccion to set
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
	 * @param perfil the perfil to set
	 */
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
        
}


