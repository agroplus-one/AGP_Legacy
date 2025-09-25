package com.rsi.agp.vo;

public class LocalCultVarVO {

	// Añadidos para facilitar la llamada y respuesta del SW de Zonificación SIGPAC
	private String mensajeError = null;
	private String plan = "";
	private String linea = "";

	// Provincia
	private String codProvincia = "";
	private String nomProvincia = "";

	// Comarca
	private String codComarca = "";
	private String nomComarca = "";

	// Termino
	private String codTermino = "";
	private String nomTermino = "";

	// Subtermino
	private String subTermino = "";
	private String codPostal = "";

	// Cultivo
	private String codCultivo = null;
	private String desCultivo = null;

	// Variedad
	private String codVariedad = "";
	private String desVariedad = "";

	public LocalCultVarVO() {
		super();
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getNomProvincia() {
		return nomProvincia;
	}

	public void setNomProvincia(String nomProvincia) {
		this.nomProvincia = nomProvincia;
	}

	public String getCodComarca() {
		return codComarca;
	}

	public void setCodComarca(String codComarca) {
		this.codComarca = codComarca;
	}

	public String getNomComarca() {
		return nomComarca;
	}

	public void setNomComarca(String nomComarca) {
		this.nomComarca = nomComarca;
	}

	public String getCodTermino() {
		return codTermino;
	}

	public void setCodTermino(String codTermino) {
		this.codTermino = codTermino;
	}

	public String getNomTermino() {
		return nomTermino;
	}

	public void setNomTermino(String nomTermino) {
		this.nomTermino = nomTermino;
	}

	public String getSubTermino() {
		return subTermino;
	}

	public void setSubTermino(String subTermino) {
		this.subTermino = subTermino;
	}

	public String getCodPostal() {
		return codPostal;
	}

	public void setCodPostal(String codPostal) {
		this.codPostal = codPostal;
	}

	public String getCodCultivo() {
		return codCultivo;
	}

	public void setCodCultivo(String codCultivo) {
		this.codCultivo = codCultivo;
	}

	public String getDesCultivo() {
		return desCultivo;
	}

	public void setDesCultivo(String desCultivo) {
		this.desCultivo = desCultivo;
	}

	public String getCodVariedad() {
		return codVariedad;
	}

	public void setCodVariedad(String codVariedad) {
		this.codVariedad = codVariedad;
	}

	public String getDesVariedad() {
		return desVariedad;
	}

	public void setDesVariedad(String desVariedad) {
		this.desVariedad = desVariedad;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}
}
