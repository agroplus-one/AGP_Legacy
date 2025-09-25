package com.rsi.agp.vo;

public class DatosModulosRiesgosVO {

	private String modulos;
	private String lineaSeguroId;
	private ParcelaVO parcelaVO;

	public DatosModulosRiesgosVO() {
		this.modulos = new String("");
		this.lineaSeguroId = new String("");
	}

	public String getModulos() {
		return modulos;
	}

	public void setModulos(String modulos) {
		this.modulos = modulos;
	}

	public String getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(String lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}

	public ParcelaVO getParcelaVO() {
		return parcelaVO;
	}

	public void setParcelaVO(ParcelaVO parcelaVO) {
		this.parcelaVO = parcelaVO;
	}
}
