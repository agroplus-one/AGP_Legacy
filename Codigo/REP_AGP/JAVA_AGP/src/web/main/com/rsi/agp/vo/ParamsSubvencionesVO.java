package com.rsi.agp.vo;

public class ParamsSubvencionesVO {

	private Long codPlan;
	private Long codLinea;
	private Long codCultivo;
	private Long codVariedad;
	private Long codProvincia;
	private Long codComarca;
	private Long codTermino;
	private String listCodigosModulos;
	private Long codPoliza;
	private Character subtermino;

	public ParamsSubvencionesVO() {
		super();
	}

	public Long getCodPlan() {
		return codPlan;
	}

	public void setCodPlan(Long codPlan) {
		this.codPlan = codPlan;
	}

	public Long getCodLinea() {
		return codLinea;
	}

	public void setCodLinea(Long codLinea) {
		this.codLinea = codLinea;
	}

	public Long getCodCultivo() {
		return codCultivo;
	}

	public void setCodCultivo(Long codCultivo) {
		this.codCultivo = codCultivo;
	}

	public Long getCodVariedad() {
		return codVariedad;
	}

	public void setCodVariedad(Long codVariedad) {
		this.codVariedad = codVariedad;
	}

	public Long getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(Long codProvincia) {
		this.codProvincia = codProvincia;
	}

	public Long getCodComarca() {
		return codComarca;
	}

	public void setCodComarca(Long codComarca) {
		this.codComarca = codComarca;
	}

	public Long getCodTermino() {
		return codTermino;
	}

	public void setCodTermino(Long codTermino) {
		this.codTermino = codTermino;
	}

	public Character getSubtermino() {
		return subtermino;
	}

	public void setSubtermino(Character subtermino) {
		this.subtermino = subtermino;
	}

	public String getListCodigosModulos() {
		return listCodigosModulos;
	}

	public void setListCodigosModulos(String listCodigosModulos) {
		this.listCodigosModulos = listCodigosModulos;
	}

	public Long getCodPoliza() {
		return codPoliza;
	}

	public void setCodPoliza(Long codPoliza) {
		this.codPoliza = codPoliza;
	}
}
