package com.rsi.agp.core.report;

import java.math.BigDecimal;

import com.rsi.agp.core.webapp.util.StringUtils;

/**
 * A usar para los informes de listado de parcelas
 * @author U029823
 *
 */
public class BeanParcela {

	private String numero;//1-1
	private BigDecimal codProvincia;//99
	private BigDecimal codComarca;//999
	private BigDecimal codTermino;//999
	private String subtermino;//A
	private BigDecimal codCultivo;//
	private BigDecimal codVariedad;
	private String idCatSigpac;
	private String nombre;
	private String superm;
	private String precio;
	private String produccion;
	private String tipoCapital;
	private String fechaGarantia;
	private String numUnidades;
	
	private String poligono;
	private String parcela;
	private BigDecimal codprovsigpac;
	private BigDecimal codtermsigpac;
	private BigDecimal agrsigpac;
	private BigDecimal zonasigpac;
	private BigDecimal poligonosigpac;
	private BigDecimal parcelasigpac;
	private BigDecimal recintosigpac;		
	
	private String incrementoProduccion;
	private String sistemaCultivo;
	private String sistemaConduccion;
	

	
	
	
	//GETTERS AND SETTERS
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public BigDecimal getCodProvincia() {
		return codProvincia;
	}
	public void setCodProvincia(BigDecimal codProvincia) {
		this.codProvincia = codProvincia;
	}
	public BigDecimal getCodComarca() {
		return codComarca;
	}
	public void setCodComarca(BigDecimal codComarca) {
		this.codComarca = codComarca;
	}
	public BigDecimal getCodTermino() {
		return codTermino;
	}
	public void setCodTermino(BigDecimal codTermino) {
		this.codTermino = codTermino;
	}
	public String getSubtermino() {
		return subtermino;
	}
	public void setSubtermino(String subtermino) {
		this.subtermino = subtermino;
	}
	public BigDecimal getCodCultivo() {
		return codCultivo;
	}
	public void setCodCultivo(BigDecimal codCultivo) {
		this.codCultivo = codCultivo;
	}
	public BigDecimal getCodVariedad() {
		return codVariedad;
	}
	public void setCodVariedad(BigDecimal codVariedad) {
		this.codVariedad = codVariedad;
	}
	public String getIdCatSigpac() {
		return idCatSigpac;
	}
	public void setIdCatSigpac(String idCatSigpac) {
		this.idCatSigpac = idCatSigpac;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getSuperm() {
		return superm;
	}
	public void setSuperm(String superm) {
		this.superm = superm;
	}
	public String getPrecio() {
		return precio;
	}
	public void setPrecio(String precio) {
		this.precio = precio;
	}
	public String getProduccion() {
		return produccion;
	}
	public void setProduccion(String produccion) {
		this.produccion = produccion;
	}
	public String getTipoCapital() {
		return tipoCapital;
	}
	public void setTipoCapital(String tipoCapital) {
		this.tipoCapital = tipoCapital;
	}
	public String getFechaGarantia() {
		return fechaGarantia;
	}
	public void setFechaGarantia(String fechaGarantia) {
		this.fechaGarantia = fechaGarantia;
	}
	public String getNumUnidades() {
		return numUnidades;
	}
	public void setNumUnidades(String numUnidades) {
		this.numUnidades = numUnidades;
	}
	public String getPoligono() {
		return poligono;
	}
	public void setPoligono(String poligono) {
		this.poligono = poligono;
	}
	public String getParcela() {
		return parcela;
	}
	public void setParcela(String parcela) {
		this.parcela = parcela;
	}
	public BigDecimal getCodprovsigpac() {
		return codprovsigpac;
	}
	public void setCodprovsigpac(BigDecimal codprovsigpac) {
		this.codprovsigpac = codprovsigpac;
	}
	public BigDecimal getCodtermsigpac() {
		return codtermsigpac;
	}
	public void setCodtermsigpac(BigDecimal codtermsigpac) {
		this.codtermsigpac = codtermsigpac;
	}
	public BigDecimal getAgrsigpac() {
		return agrsigpac;
	}
	public void setAgrsigpac(BigDecimal agrsigpac) {
		this.agrsigpac = agrsigpac;
	}
	public BigDecimal getZonasigpac() {
		return zonasigpac;
	}
	public void setZonasigpac(BigDecimal zonasigpac) {
		this.zonasigpac = zonasigpac;
	}
	public BigDecimal getPoligonosigpac() {
		return poligonosigpac;
	}
	public void setPoligonosigpac(BigDecimal poligonosigpac) {
		this.poligonosigpac = poligonosigpac;
	}
	public BigDecimal getParcelasigpac() {
		return parcelasigpac;
	}
	public void setParcelasigpac(BigDecimal parcelasigpac) {
		this.parcelasigpac = parcelasigpac;
	}
	public BigDecimal getRecintosigpac() {
		return recintosigpac;
	}
	public void setRecintosigpac(BigDecimal recintosigpac) {
		this.recintosigpac = recintosigpac;
	}
	public String getIncrementoProduccion() {
		return incrementoProduccion;
	}
	public void setIncrementoProduccion(String incrementoProduccion) {
		this.incrementoProduccion = incrementoProduccion;
	}
	public String getSistemaCultivo() {
		return sistemaCultivo;
	}
	public void setSistemaCultivo(String sistemaCultivo) {
		this.sistemaCultivo = sistemaCultivo;
	}
	public String getSistemaConduccion() {
		return sistemaConduccion;
	}
	public void setSistemaConduccion(String sistemaConduccion) {
		this.sistemaConduccion = sistemaConduccion;
	}
	
	
}