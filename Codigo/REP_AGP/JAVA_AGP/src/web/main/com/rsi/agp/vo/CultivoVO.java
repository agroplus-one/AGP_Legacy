package com.rsi.agp.vo;

public class CultivoVO {

	private int lineaSeguroId = 0;
	private int codCultivo = 0;
	private String desCultivo = "";

	public CultivoVO() {

	}

	public CultivoVO(int lineaSeguroId, int codCultivo, String desCultivo) {
		super();
		this.lineaSeguroId = lineaSeguroId;
		this.codCultivo = codCultivo;
		this.desCultivo = desCultivo;
	}

	public int getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(int lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}

	public int getCodCultivo() {
		return codCultivo;
	}

	public void setCodCultivo(int codCultivo) {
		this.codCultivo = codCultivo;
	}

	public String getDesCultivo() {
		return desCultivo;
	}

	public void setDesCultivo(String desCultivo) {
		this.desCultivo = desCultivo;
	}
}
