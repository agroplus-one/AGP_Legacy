package com.rsi.agp.dao.tables.familias;



public class LineaFamiliaId  implements java.io.Serializable {


     private Long codFamilia; 
     private Long codLinea;
     private Character grupoNegocio;
     private String codGrupoFamilia;
     
	public Long getCodFamilia() {
		return codFamilia;
	}
	public void setCodFamilia(Long codFamilia) {
		this.codFamilia = codFamilia;
	}
	public Character getGrupoNegocio() {
		return grupoNegocio;
	}
	public void setGrupoNegocio(Character grupoNegocio) {
		this.grupoNegocio = grupoNegocio;
	}

	public Long getCodLinea() {
		return codLinea;
	}
	public void setCodLinea(Long codLinea) {
		this.codLinea = codLinea;
	}
	public String getCodGrupoFamilia() {
		return codGrupoFamilia;
	}
	public void setCodGrupoFamilia(String codGrupoFamilia) {
		this.codGrupoFamilia = codGrupoFamilia;
	}


	

     

}


