package com.rsi.agp.dao.tables.poliza.explotaciones.Informes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

public class InformeAnexModExplotacion {
	
	private BigDecimal id; // Id de la explotacion
	private BigDecimal idpoliza;
	private BigDecimal idgruporaza;
	private String tipoModificacion;
	private BigDecimal numero;
	private String provincia;
	private String comarca;
	private String termino;
	private String rega;
	private String sigla;
	private String subexplotacion;
	private BigDecimal latitud;
	private BigDecimal longitud;
	private String especie;
	private String regimen;
	private String grupoRaza;
	private String tipoCapital;
	private String TipoAnimal;
	private String grupoListado;
	private BigDecimal numanimales;
	private BigDecimal precio;
	private BigDecimal tasaComercial;
	private BigDecimal costeTomador;
	private BigDecimal idM;
	private BigDecimal idpolizaM;
	private BigDecimal idgruporazaM;
	private String tipoModificacionM;
	private BigDecimal numeroM;
	private String provinciaM;
	private String comarcaM;
	private String terminoM;
	private String regaM;
	private String siglaM;
	private String subexplotacionM;
	private BigDecimal latitudM;
	private BigDecimal longitudM;
	private String especieM;
	private String regimenM;
	private String grupoRazaM;
	private String tipoCapitalM;
	private String tipoAnimalM;
	private String grupoListadoM;
	private BigDecimal numAnimalesM;
	private BigDecimal precioM;
	private BigDecimal tasaComercialM;
	private BigDecimal costeTomadorM;
	private List<InformeDatosVariables> datosVariables;
	private JRMapCollectionDataSource datosVariable2;

	public JRMapCollectionDataSource getDatosVariable2() {
		return datosVariable2;
	}

	public void setDatosVariable2(JRMapCollectionDataSource datosVariable2) {
		this.datosVariable2 = datosVariable2;
	}

	public List<InformeDatosVariables> getDatosVariables() {
		return datosVariables;
	}

	public void setDatosVariables(List<InformeDatosVariables> datosVariables) {
		this.datosVariables = datosVariables;
		setDatosVariable2();
	}
	
	private void setDatosVariable2() {
		List<Map<String, ?>> dvList = new ArrayList<Map<String, ?>>();
		Map<String, Object> dvMap;
		for (InformeDatosVariables iDv : datosVariables) {
			dvMap = new HashMap<String, Object>();
			dvMap.put("codConcepto", iDv.getCodConcepto());
			dvMap.put("codConceptoM", iDv.getCodConceptoM());
			dvMap.put("descripcion", iDv.getDescripcion());
			dvMap.put("descripcionM", iDv.getDescripcionM());
			dvMap.put("nombreConcepto", iDv.getNombreConcepto());
			dvMap.put("nombreConceptoM", iDv.getNombreConceptoM());
			dvMap.put("valor", iDv.getValor());
			dvMap.put("valorM", iDv.getValorM());
			dvList.add(dvMap);
		}
		datosVariable2 = new JRMapCollectionDataSource(dvList);
	}

	public InformeAnexModExplotacion() {
		datosVariables = new ArrayList<InformeDatosVariables>();
		setDatosVariable2();
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public BigDecimal getIdpoliza() {
		return idpoliza;
	}

	public void setIdpoliza(BigDecimal idpoliza) {
		this.idpoliza = idpoliza;
	}

	public String getTipoModificacion() {
		return tipoModificacion;
	}

	public void setTipoModificacion(String tipoModificacion) {
		this.tipoModificacion = tipoModificacion;
	}

	public BigDecimal getNumero() {
		return numero;
	}

	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getComarca() {
		return comarca;
	}

	public void setComarca(String comarca) {
		this.comarca = comarca;
	}

	public String getTermino() {
		return termino;
	}

	public void setTermino(String termino) {
		this.termino = termino;
	}

	public String getRega() {
		return rega;
	}

	public void setRega(String rega) {
		this.rega = rega;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getSubexplotacion() {
		return subexplotacion;
	}

	public void setSubexplotacion(String subexplotacion) {
		this.subexplotacion = subexplotacion;
	}

	public BigDecimal getLatitud() {
		return latitud;
	}

	public void setLatitud(BigDecimal latitud) {
		this.latitud = latitud;
	}

	public BigDecimal getLongitud() {
		return longitud;
	}

	public void setLongitud(BigDecimal longitud) {
		this.longitud = longitud;
	}

	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public String getRegimen() {
		return regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public String getGrupoRaza() {
		return grupoRaza;
	}

	public void setGrupoRaza(String grupoRaza) {
		this.grupoRaza = grupoRaza;
	}

	public String getTipoCapital() {
		return tipoCapital;
	}

	public void setTipoCapital(String tipoCapital) {
		this.tipoCapital = tipoCapital;
	}

	public String getTipoAnimal() {
		return TipoAnimal;
	}

	public void setTipoAnimal(String tipoAnimal) {
		TipoAnimal = tipoAnimal;
	}

	public String getGrupoListado() {
		return grupoListado;
	}

	public void setGrupoListado(String grupoListado) {
		this.grupoListado = grupoListado;
	}

	public BigDecimal getNumanimales() {
		return numanimales;
	}

	public void setNumanimales(BigDecimal numanimales) {
		this.numanimales = numanimales;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public BigDecimal getTasaComercial() {
		return tasaComercial;
	}

	public void setTasaComercial(BigDecimal tasaComercial) {
		this.tasaComercial = tasaComercial;
	}

	public BigDecimal getCosteTomador() {
		return costeTomador;
	}

	public void setCosteTomador(BigDecimal costeTomador) {
		this.costeTomador = costeTomador;
	}

	public BigDecimal getIdM() {
		return idM;
	}

	public void setIdM(BigDecimal idM) {
		this.idM = idM;
	}

	public BigDecimal getIdpolizaM() {
		return idpolizaM;
	}

	public void setIdpolizaM(BigDecimal idpolizaM) {
		this.idpolizaM = idpolizaM;
	}

	public String getTipoModificacionM() {
		return tipoModificacionM;
	}

	public void setTipoModificacionM(String tipoModificacionM) {
		this.tipoModificacionM = tipoModificacionM;
	}

	public BigDecimal getNumeroM() {
		return numeroM;
	}

	public void setNumeroM(BigDecimal numeroM) {
		this.numeroM = numeroM;
	}

	public String getProvinciaM() {
		return provinciaM;
	}

	public void setProvinciaM(String provinciaM) {
		this.provinciaM = provinciaM;
	}

	public String getComarcaM() {
		return comarcaM;
	}

	public void setComarcaM(String comarcaM) {
		this.comarcaM = comarcaM;
	}

	public String getTerminoM() {
		return terminoM;
	}

	public void setTerminoM(String terminoM) {
		this.terminoM = terminoM;
	}

	public String getRegaM() {
		return regaM;
	}

	public void setRegaM(String regaM) {
		this.regaM = regaM;
	}

	public String getSiglaM() {
		return siglaM;
	}

	public void setSiglaM(String siglaM) {
		this.siglaM = siglaM;
	}

	public String getSubexplotacionM() {
		return subexplotacionM;
	}

	public void setSubexplotacionM(String subexplotacionM) {
		this.subexplotacionM = subexplotacionM;
	}

	public BigDecimal getLatitudM() {
		return latitudM;
	}

	public void setLatitudM(BigDecimal latitudM) {
		this.latitudM = latitudM;
	}

	public BigDecimal getLongitudM() {
		return longitudM;
	}

	public void setLongitudM(BigDecimal longitudM) {
		this.longitudM = longitudM;
	}

	public String getEspecieM() {
		return especieM;
	}

	public void setEspecieM(String especieM) {
		this.especieM = especieM;
	}

	public String getRegimenM() {
		return regimenM;
	}

	public void setRegimenM(String regimenM) {
		this.regimenM = regimenM;
	}

	public String getGrupoRazaM() {
		return grupoRazaM;
	}

	public void setGrupoRazaM(String grupoRazaM) {
		this.grupoRazaM = grupoRazaM;
	}

	public String getTipoCapitalM() {
		return tipoCapitalM;
	}

	public void setTipoCapitalM(String tipoCapitalM) {
		this.tipoCapitalM = tipoCapitalM;
	}

	public String getTipoAnimalM() {
		return tipoAnimalM;
	}

	public void setTipoAnimalM(String tipoAnimalM) {
		this.tipoAnimalM = tipoAnimalM;
	}

	public String getGrupoListadoM() {
		return grupoListadoM;
	}

	public void setGrupoListadoM(String grupoListadoM) {
		this.grupoListadoM = grupoListadoM;
	}

	public BigDecimal getNumAnimalesM() {
		return numAnimalesM;
	}

	public void setNumAnimalesM(BigDecimal numAnimalesM) {
		this.numAnimalesM = numAnimalesM;
	}

	public BigDecimal getPrecioM() {
		return precioM;
	}

	public void setPrecioM(BigDecimal precioM) {
		this.precioM = precioM;
	}

	public BigDecimal getTasaComercialM() {
		return tasaComercialM;
	}

	public void setTasaComercialM(BigDecimal tasaComercialM) {
		this.tasaComercialM = tasaComercialM;
	}

	public BigDecimal getCosteTomadorM() {
		return costeTomadorM;
	}

	public void setCosteTomadorM(BigDecimal costeTomadorM) {
		this.costeTomadorM = costeTomadorM;
	}

	public BigDecimal getIdGrupoRaza() {
		return idgruporaza;
	}

	public void setIdGrupoRaza(BigDecimal idgruporaza) {
		this.idgruporaza = idgruporaza;
	}

	public BigDecimal getIdGrupoRazaM() {
		return idgruporazaM;
	}

	public void setIdGrupoRazaM(BigDecimal idgruporazaM) {
		this.idgruporazaM = idgruporazaM;
	}

}
