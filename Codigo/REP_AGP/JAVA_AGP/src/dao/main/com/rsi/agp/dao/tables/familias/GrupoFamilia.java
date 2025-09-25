package com.rsi.agp.dao.tables.familias;

import java.util.HashSet;
import java.util.Set;

import com.rsi.agp.dao.tables.admin.SubentidadMediadora;

public class GrupoFamilia  implements java.io.Serializable {


    private String codGrupo;
    private String nomGrupo;
	private Set<Familia> familias = new HashSet<Familia>(0);

    
    public GrupoFamilia() {}
    
    public GrupoFamilia(String codGrupo) {
    	this.codGrupo = codGrupo;
    }
    
    public GrupoFamilia(String codGrupo, String nomGrupo) {
    	this.codGrupo = codGrupo;
    	this.nomGrupo = nomGrupo;
    }
     
     
	public String getCodGrupo() {
		return codGrupo;
	}
	public void setCodGrupo(String codGrupo) {
		this.codGrupo = codGrupo;
	}
	public String getNomGrupo() {
		return nomGrupo;
	}
	public void setNomGrupo(String nomGrupo) {
		this.nomGrupo = nomGrupo;
	}
	public Set<Familia> getFamilias() {
		return familias;
	}
	public void setFamilias(Set<Familia> familias) {
		this.familias = familias;
	}



}


