package com.rsi.agp.vo;

public class ComarcaVO {

	private int codComarca = 0;
	private int codProvincia = 0;
	private String nomComarca = "";

	public ComarcaVO() {

	}

	public ComarcaVO(int codComarca, int codProvincia, String nomComarca) {
		super();
		this.codComarca = codComarca;
		this.codProvincia = codProvincia;
		this.nomComarca = nomComarca;
	}

	public ComarcaVO(int codComarca, String nomComarca) {
		super();
		this.codComarca = codComarca;
		this.nomComarca = nomComarca;
	}

	public int getCodComarca() {
		return codComarca;
	}

	public void setCodComarca(int codComarca) {
		this.codComarca = codComarca;
	}

	public int getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(int codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getNomComarca() {
		return nomComarca;
	}

	public void setNomComarca(String nomComarca) {
		this.nomComarca = nomComarca;
	}

}
