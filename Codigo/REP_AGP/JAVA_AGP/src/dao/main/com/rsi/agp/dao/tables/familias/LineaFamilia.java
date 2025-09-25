package com.rsi.agp.dao.tables.familias;

import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.cgen.LineaCondicionado;

public class LineaFamilia  implements java.io.Serializable {

	private LineaFamiliaId id;
	
	private Familia familia;
	private GrupoFamilia grupoFamilia;
	private GruposNegocio gruposNegocio;
	private LineaCondicionado linea;
	
	public LineaFamilia() {
		this.id = new LineaFamiliaId();
		this.familia = new Familia();
		this.grupoFamilia = new GrupoFamilia();
		this.linea = new LineaCondicionado();
		this.gruposNegocio = new GruposNegocio();
	}
	
	public LineaFamiliaId getId() {
		return id;
	}

	public void setId(LineaFamiliaId id) {
		this.id = id;
	}



	public Familia getFamilia() {
		return familia;
	}

	public void setFamilia(Familia familia) {
		this.familia = familia;
	}

	public GrupoFamilia getGrupoFamilia() {
		return grupoFamilia;
	}

	public void setGrupoFamilia(GrupoFamilia grupoFamilia) {
		this.grupoFamilia = grupoFamilia;
	}

	public GruposNegocio getGruposNegocio() {
		return gruposNegocio;
	}

	public void setGruposNegocio(GruposNegocio gruposNegocio) {
		this.gruposNegocio = gruposNegocio;
	}

	public LineaCondicionado getLinea() {
		return linea;
	}

	public void setLinea(LineaCondicionado linea) {
		this.linea = linea;
	}



     

}


