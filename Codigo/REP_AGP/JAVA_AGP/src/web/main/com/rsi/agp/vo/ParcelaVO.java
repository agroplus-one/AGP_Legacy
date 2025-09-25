package com.rsi.agp.vo;

import java.util.ArrayList;
import java.util.List;

public class ParcelaVO {

	private String codPoliza = "";
	private String codParcela = "";
	private String tipoParcela = "";
	
	private String tipoModificacion = "";
	private String idAnexoModificacion = "";
	
	// Ubicacion
	private String codProvincia = "";
	private String desProvincia = "";
	private String codComarca = "";
	private String desComarca = "";
	private String codTermino = "";
	private String desTermino = "";
	private String codSubTermino = "";

	// sigpac
	private String provinciaSigpac = "";
	private String terminoSigpac = "";
	private String agregadoSigpac = "";
	private String zonaSigpac = "";
	private String poligonoSigpac = "";
	private String parcelaSigpac = "";
	private String recintoSigpac = "";

	// Otros
	private String nombreParcela = "";
	private String cultivo = "";
	private String desCultivo = "";
	private String variedad = "";
	private String desVariedad = "";

	private CapitalAseguradoVO capitalAsegurado = new CapitalAseguradoVO();
	private List<CapitalAseguradoVO> capitalesAsegurados = new ArrayList<CapitalAseguradoVO>();
	
	private ArrayList<Integer> subvencionesENESA = new ArrayList<Integer>();
	private ArrayList<Integer> subvencionesCCAA = new ArrayList<Integer>();
	private List<RiesgoVO> riesgosSeleccionados = new ArrayList<RiesgoVO>();
	
	private String refIdParcela;
	private String idparcelaanxestructura;

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getCodComarca() {
		return codComarca;
	}

	public void setCodComarca(String codComarca) {
		this.codComarca = codComarca;
	}

	public String getCodTermino() {
		return codTermino;
	}

	public void setCodTermino(String codTermino) {
		this.codTermino = codTermino;
	}

	public String getCodSubTermino() {
		return codSubTermino;
	}

	public void setCodSubTermino(String codSubTermino) {
		this.codSubTermino = codSubTermino;
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

	public String getNombreParcela() {
		return nombreParcela;
	}

	public void setNombreParcela(String nombreParcela) {
		this.nombreParcela = nombreParcela;
	}

	public String getCultivo() {
		return cultivo;
	}

	public void setCultivo(String cultivo) {
		this.cultivo = cultivo;
	}

	public String getVariedad() {
		return variedad;
	}

	public void setVariedad(String variedad) {
		this.variedad = variedad;
	}

	public CapitalAseguradoVO getCapitalAsegurado() {
		return capitalAsegurado;
	}

	public void setCapitalAsegurado(CapitalAseguradoVO capitalAsegurado) {
		this.capitalAsegurado = capitalAsegurado;
	}
	
	public List<CapitalAseguradoVO> getCapitalesAsegurados() {
		return capitalesAsegurados;
	}

	public void setCapitalesAsegurados(List<CapitalAseguradoVO> capitalesAsegurados) {
		this.capitalesAsegurados = capitalesAsegurados;
	}

	public ArrayList<Integer> getSubvencionesENESA() {
		return subvencionesENESA;
	}

	public void setSubvencionesENESA(ArrayList<Integer> subvencionesENESA) {
		this.subvencionesENESA = subvencionesENESA;
	}

	public ArrayList<Integer> getSubvencionesCCAA() {
		return subvencionesCCAA;
	}

	public void setSubvencionesCCAA(ArrayList<Integer> subvencionesCCAA) {
		this.subvencionesCCAA = subvencionesCCAA;
	}

	public String getCodPoliza() {
		return codPoliza;
	}

	public void setCodPoliza(String codPoliza) {
		this.codPoliza = codPoliza;
	}

	public String getDesProvincia() {
		return desProvincia;
	}

	public void setDesProvincia(String desProvincia) {
		this.desProvincia = desProvincia;
	}

	public String getDesComarca() {
		return desComarca;
	}

	public void setDesComarca(String desComarca) {
		this.desComarca = desComarca;
	}

	public String getDesTermino() {
		return desTermino;
	}

	public void setDesTermino(String desTermino) {
		this.desTermino = desTermino;
	}

	public String getDesCultivo() {
		return desCultivo;
	}

	public void setDesCultivo(String desCultivo) {
		this.desCultivo = desCultivo;
	}

	public String getDesVariedad() {
		return desVariedad;
	}

	public void setDesVariedad(String desVariedad) {
		this.desVariedad = desVariedad;
	}

	public String getCodParcela() {
		return codParcela;
	}

	public void setCodParcela(String codParcela) {
		this.codParcela = codParcela;
	}
	
	public String getIdAnexoModificacion() {
		return idAnexoModificacion;
	}

	public void setIdAnexoModificacion(String idAnexoModificacion) {
		this.idAnexoModificacion = idAnexoModificacion;
	}

	public List<RiesgoVO> getRiesgosSeleccionados() {
		return riesgosSeleccionados;
	}

	public void setRiesgosSeleccionados(List<RiesgoVO> riesgosSeleccionados) {
		this.riesgosSeleccionados = riesgosSeleccionados;
	}

	public String getTipoModificacion() {
		return tipoModificacion;
	}

	public void setTipoModificacion(String tipoModificacion) {
		this.tipoModificacion = tipoModificacion;
	}
	
	public String getTipoParcela() {
		return tipoParcela;
	}
	
	public char getTipoParcelaChar() {
		return tipoParcela.charAt(0);
	}

	public void setTipoParcela(String tipoParcela) {
		this.tipoParcela = tipoParcela;
	}

	public String getRefIdParcela() {
		return refIdParcela;
	}

	public void setRefIdParcela(String refIdParcela) {
		this.refIdParcela = refIdParcela;
	}

	public String getIdparcelaanxestructura() {
		return idparcelaanxestructura;
	}

	public void setIdparcelaanxestructura(String idparcelaanxestructura) {
		this.idparcelaanxestructura = idparcelaanxestructura;
	}

	/**
	 * @return Todos los datos del SIGPAC separados por -
	 */
	public String getSIGPAC() {
		final char separador = '-';
		return provinciaSigpac + separador + terminoSigpac + separador + agregadoSigpac + separador + zonaSigpac
				+ separador + poligonoSigpac + separador + parcelaSigpac + separador + recintoSigpac;
	}
	
	public void resetTC() {
		this.capitalAsegurado = new CapitalAseguradoVO();
		this.capitalesAsegurados = new ArrayList<CapitalAseguradoVO>();		
		this.subvencionesENESA = new ArrayList<Integer>();
		this.subvencionesCCAA = new ArrayList<Integer>();
		this.riesgosSeleccionados = new ArrayList<RiesgoVO>();
	}
}
