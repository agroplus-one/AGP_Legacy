package com.rsi.agp.vo;

import java.util.ArrayList;
import java.util.List;

public class ModulosVO {

	// array of conceptos(coberturas/caracte's/conceptos de los modulos)
	// cabecera de los modulos. Todos los modulos las mismas.
	List<ConceptoCubiertoVO> listConceptosCubiertos = null;

	// array of ModuloVO(modulos)
	List<ModuloVO> modulos = null;

	public ModulosVO() {
		this.listConceptosCubiertos = new ArrayList<ConceptoCubiertoVO>();
		this.modulos = new ArrayList<ModuloVO>();
	}

	public List<ModuloVO> getModulos() {
		return modulos;
	}

	public void setModulos(List<ModuloVO> modulos) {
		this.modulos = modulos;
	}

	public List<ConceptoCubiertoVO> getListConceptosCubiertos() {
		return listConceptosCubiertos;
	}

	public void setListConceptosCubiertos(List<ConceptoCubiertoVO> listConceptosCubiertos) {
		this.listConceptosCubiertos = listConceptosCubiertos;
	}
}
