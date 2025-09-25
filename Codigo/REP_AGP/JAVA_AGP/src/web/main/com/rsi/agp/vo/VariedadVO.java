package com.rsi.agp.vo;

public class VariedadVO {
	private int lineaSeguroId = 0;
	private int codCultivo = 0;
	private String desCultivo = "";
	private int codVariedad = 0;
	private String desVariedad = "";

	public VariedadVO() {

	}

	public VariedadVO(int lineaSeguroId, int codCultivo, int codVariedad, String desVariedad) {
		super();
		this.lineaSeguroId = lineaSeguroId;
		this.codCultivo = codCultivo;
		this.codVariedad = codVariedad;
		this.desVariedad = desVariedad;
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

	public int getCodVariedad() {
		return codVariedad;
	}

	public void setCodVariedad(int codVariedad) {
		this.codVariedad = codVariedad;
	}

	public String getDesVariedad() {
		return desVariedad;
	}

	public void setDesVariedad(String desVariedad) {
		this.desVariedad = desVariedad;
	}

	public String getDesCultivo() {
		return desCultivo;
	}

	public void setDesCultivo(String desCultivo) {
		this.desCultivo = desCultivo;
	}
}
