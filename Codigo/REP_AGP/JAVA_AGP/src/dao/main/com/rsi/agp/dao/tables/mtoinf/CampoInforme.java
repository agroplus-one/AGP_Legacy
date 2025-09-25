package com.rsi.agp.dao.tables.mtoinf;

import java.math.BigDecimal;

@SuppressWarnings("rawtypes")
public class CampoInforme  implements Comparable { 

	
	
	private Long id;
	private BigDecimal permitidoOCalculado;
	private String campoCalculadoId;
	private String campoPermitidoId;
	private Long datoInformeId;
	private String nombre;
	private String  formato;
    private BigDecimal decimales;
    private Long orden;
    private Long informeId;
    private BigDecimal totaliza;
    private BigDecimal tipo;
    private String descTipo;
    private String nombreVista;
    private BigDecimal origen_datos;
    
	public BigDecimal getOrigen_datos() {
		return origen_datos;
	}
	public void setOrigen_datos(BigDecimal origen_datos) {
		this.origen_datos = origen_datos;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getFormato() {
		return formato;
	}
	public String getNombreVista() {
		return nombreVista;
	}
	public void setNombreVista(String nombreVista) {
		this.nombreVista = nombreVista;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	public BigDecimal getDecimales() {
		return decimales;
	}
	public BigDecimal getTipo() {
		return tipo;
	}
	public void setTipo(BigDecimal tipo) {
		this.tipo = tipo;
	}
	public void setDecimales(BigDecimal decimales) {
		this.decimales = decimales;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	

	public Long getOrden() {
		return orden;
	}
	public void setOrden(Long orden) {
		this.orden = orden;
	}
	public int compareTo(Object o) { 
		CampoInforme campoInforme = (CampoInforme)o; 
		// order by nombreVista,nombre
		int comparison = this.nombreVista.compareToIgnoreCase(campoInforme.nombreVista);
		   if (comparison != 0)
		      return comparison;
		   comparison = this.nombre.compareToIgnoreCase(campoInforme.nombre);
		   if (comparison != 0)
		      return comparison;
		return comparison;

    }
	
	
	public String getCampoCalculadoId() {
		return campoCalculadoId;
	}
	public void setCampoCalculadoId(String campoCalculadoId) {
		this.campoCalculadoId = campoCalculadoId;
	}
	public String getCampoPermitidoId() {
		return campoPermitidoId;
	}
	public void setCampoPermitidoId(String campoPermitidoId) {
		this.campoPermitidoId = campoPermitidoId;
	}
	public Long getInformeId() {
		return informeId;
	}
	public void setInformeId(Long informeId) {
		this.informeId = informeId;
	}
	public BigDecimal getTotaliza() {
		return totaliza;
	}
	public void setTotaliza(BigDecimal totaliza) {
		this.totaliza = totaliza;
	}
	public Long getDatoInformeId() {
		return datoInformeId;
	}
	public void setDatoInformeId(Long datoInformeId) {
		this.datoInformeId = datoInformeId;
	}
	public BigDecimal getPermitidoOCalculado() {
		return permitidoOCalculado;
	}
	public void setPermitidoOCalculado(BigDecimal permitidoOCalculado) {
		this.permitidoOCalculado = permitidoOCalculado;
	}
	public String getDescTipo() {
		return descTipo;
	}
	public void setDescTipo(String descTipo) {
		this.descTipo = descTipo;
	} 


    
    
    
}

