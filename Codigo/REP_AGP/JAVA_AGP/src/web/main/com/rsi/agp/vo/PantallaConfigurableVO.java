package com.rsi.agp.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PantallaConfigurableVO {

	private int idPantallaConfigurable;
	private int idPantalla;

	private List<CampoPantallaConfigurableVO> listCampos = new ArrayList<CampoPantallaConfigurableVO>();
	private List<BigDecimal> listCodConceptosMascaras = new ArrayList<BigDecimal>();

	public PantallaConfigurableVO() {

	}

	public PantallaConfigurableVO(int idPantallaConfigurable, int idPantalla,
			List<CampoPantallaConfigurableVO> listCampos) {
		this.idPantallaConfigurable = idPantallaConfigurable;
		this.idPantalla = idPantalla;
		this.listCampos = listCampos;
	}

	public int getIdPantallaConfigurable() {
		return idPantallaConfigurable;
	}

	public void setIdPantallaConfigurable(int idPantallaConfigurable) {
		this.idPantallaConfigurable = idPantallaConfigurable;
	}

	public int getIdPantalla() {
		return idPantalla;
	}

	public void setIdPantalla(int idPantalla) {
		this.idPantalla = idPantalla;
	}

	public List<CampoPantallaConfigurableVO> getListCampos() {
		return listCampos;
	}

	public void setListCampos(List<CampoPantallaConfigurableVO> listCampos) {
		this.listCampos = listCampos;
	}

	public List<BigDecimal> getListCodConceptosMascaras() {
		return listCodConceptosMascaras;
	}

	public void setListCodConceptosMascaras(List<BigDecimal> listCodConceptosMascaras) {
		this.listCodConceptosMascaras = listCodConceptosMascaras;
	}
}
