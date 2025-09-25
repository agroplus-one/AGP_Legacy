package com.rsi.agp.vo;

public class DatagridPopupVO {

	// Origen de datos
	private String codOrigenDeDatos = "";

	// --- INFORMACION GENERAL ---
	private String claseId = "";
	private String lineaSeguroId = "";
	private String codPoliza = "";
	private String codLinea = "";
	private String codPlan = "";
	private String codPantalla = "";
	private String codParcela = "";

	private String codModulo = "";

	// --- INFORMACION FORMULARIO ---
	// Ubicacion
	private String codProvincia = "";
	private String desProvincia = "";
	private String codComarca = "";
	private String desComarca = "";
	private String codTermino = "";
	private String desTermino = "";
	private String codSubTermino = "";

	// Id.catastral
	private String poligonoIdCatastral = "";
	private String codParcelaIdCatastral = "";

	// sigpac
	private String provinciaSigpac = "";
	private String terminoSigpac = "";
	private String agregadoSigpac = "";
	private String zonaSigpac = "";
	private String poligonoSigpac = "";
	private String parcelaSigpac = "";
	private String recintoSigpac = "";

	// Otros
	private String cultivo = "";
	private String desCultivo = "";
	private String variedad = "";
	private String desVariedad = "";

	// Tipo capital
	private String codTipoCapital = "";

	private String codsistemaproteccion = "";
	private String codPracticaCultural = "";
	private String fechaFinGarantias = "";
	private String sistemaCultivo = "";

	private String bbconcepto = "";
	private String textLabel = "";

	public DatagridPopupVO() {
		super();
	}

	public String getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(String lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}

	public String getCodTipoCapital() {
		return codTipoCapital;
	}

	public void setCodTipoCapital(String codTipoCapital) {
		this.codTipoCapital = codTipoCapital;
	}

	public String getCodPoliza() {
		return codPoliza;
	}

	public void setCodPoliza(String codPoliza) {
		this.codPoliza = codPoliza;
	}

	public String getCodLinea() {
		return codLinea;
	}

	public void setCodLinea(String codLinea) {
		this.codLinea = codLinea;
	}

	public String getCodPlan() {
		return codPlan;
	}

	public void setCodPlan(String codPlan) {
		this.codPlan = codPlan;
	}

	public String getCodPantalla() {
		return codPantalla;
	}

	public void setCodPantalla(String codPantalla) {
		this.codPantalla = codPantalla;
	}

	public String getCodParcela() {
		return codParcela;
	}

	public void setCodParcela(String codParcela) {
		this.codParcela = codParcela;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getDesProvincia() {
		return desProvincia;
	}

	public void setDesProvincia(String desProvincia) {
		this.desProvincia = desProvincia;
	}

	public String getCodComarca() {
		return codComarca;
	}

	public void setCodComarca(String codComarca) {
		this.codComarca = codComarca;
	}

	public String getDesComarca() {
		return desComarca;
	}

	public void setDesComarca(String desComarca) {
		this.desComarca = desComarca;
	}

	public String getCodTermino() {
		return codTermino;
	}

	public void setCodTermino(String codTermino) {
		this.codTermino = codTermino;
	}

	public String getDesTermino() {
		return desTermino;
	}

	public void setDesTermino(String desTermino) {
		this.desTermino = desTermino;
	}

	public String getCodSubTermino() {
		return codSubTermino;
	}

	public void setCodSubTermino(String codSubTermino) {
		this.codSubTermino = codSubTermino;
	}

	public String getPoligonoIdCatastral() {
		return poligonoIdCatastral;
	}

	public void setPoligonoIdCatastral(String poligonoIdCatastral) {
		this.poligonoIdCatastral = poligonoIdCatastral;
	}

	public String getCodParcelaIdCatastral() {
		return codParcelaIdCatastral;
	}

	public void setCodParcelaIdCatastral(String codParcelaIdCatastral) {
		this.codParcelaIdCatastral = codParcelaIdCatastral;
	}

	public String getProvinciaSigpac() {
		return provinciaSigpac;
	}

	public void setProvinciaSigpac(String provinciaSigpac) {
		this.provinciaSigpac = provinciaSigpac;
	}

	public String getTerminoSigpac() {
		return terminoSigpac;
	}

	public void setTerminoSigpac(String terminoSigpac) {
		this.terminoSigpac = terminoSigpac;
	}

	public String getAgregadoSigpac() {
		return agregadoSigpac;
	}

	public void setAgregadoSigpac(String agregadoSigpac) {
		this.agregadoSigpac = agregadoSigpac;
	}

	public String getZonaSigpac() {
		return zonaSigpac;
	}

	public void setZonaSigpac(String zonaSigpac) {
		this.zonaSigpac = zonaSigpac;
	}

	public String getPoligonoSigpac() {
		return poligonoSigpac;
	}

	public void setPoligonoSigpac(String poligonoSigpac) {
		this.poligonoSigpac = poligonoSigpac;
	}

	public String getParcelaSigpac() {
		return parcelaSigpac;
	}

	public void setParcelaSigpac(String parcelaSigpac) {
		this.parcelaSigpac = parcelaSigpac;
	}

	public String getRecintoSigpac() {
		return recintoSigpac;
	}

	public void setRecintoSigpac(String recintoSigpac) {
		this.recintoSigpac = recintoSigpac;
	}

	public String getCultivo() {
		return cultivo;
	}

	public void setCultivo(String cultivo) {
		this.cultivo = cultivo;
	}

	public String getDesCultivo() {
		return desCultivo;
	}

	public void setDesCultivo(String desCultivo) {
		this.desCultivo = desCultivo;
	}

	public String getVariedad() {
		return variedad;
	}

	public void setVariedad(String variedad) {
		this.variedad = variedad;
	}

	public String getDesVariedad() {
		return desVariedad;
	}

	public void setDesVariedad(String desVariedad) {
		this.desVariedad = desVariedad;
	}

	public String getCodOrigenDeDatos() {
		return codOrigenDeDatos;
	}

	public void setCodOrigenDeDatos(String codOrigenDeDatos) {
		this.codOrigenDeDatos = codOrigenDeDatos;
	}

	public String getCodModulo() {
		return codModulo;
	}

	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}

	public String getCodPracticaCultural() {
		return codPracticaCultural;
	}

	public void setCodPracticaCultural(String codPracticaCultural) {
		this.codPracticaCultural = codPracticaCultural;
	}

	public String getFechaFinGarantias() {
		return fechaFinGarantias;
	}

	public void setFechaFinGarantias(String fechaFinGarantias) {
		this.fechaFinGarantias = fechaFinGarantias;
	}

	public String getCodsistemaproteccion() {
		return codsistemaproteccion;
	}

	public void setCodsistemaproteccion(String codsistemaproteccion) {
		this.codsistemaproteccion = codsistemaproteccion;
	}

	public String getClaseId() {
		return claseId;
	}

	public void setClaseId(String claseId) {
		this.claseId = claseId;
	}

	public String getSistemaCultivo() {
		return sistemaCultivo;
	}

	public void setSistemaCultivo(String sistemaCultivo) {
		this.sistemaCultivo = sistemaCultivo;
	}

	public String getTextLabel() {
		return textLabel;
	}

	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	public String getBbconcepto() {
		return bbconcepto;
	}

	public void setBbconcepto(String bbconcepto) {
		this.bbconcepto = bbconcepto;
	}

}
