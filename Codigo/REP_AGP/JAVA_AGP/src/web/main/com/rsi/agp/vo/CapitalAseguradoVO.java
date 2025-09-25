package com.rsi.agp.vo;

import java.util.ArrayList;
import java.util.List;

public class CapitalAseguradoVO {

	private String id = "";
	private String codtipoCapital = "";
	private String desTipoCapital = "";
	private String superficie = "";
	private String precio = "";
	private String produccion = "";
	private String metrosCuadrados = "";

	private String refIdParcela = "";

	private Boolean precioModif = false;

	private List<PrecioVO> listPrecios = new ArrayList<PrecioVO>();
	private List<ProduccionVO> listProducciones = new ArrayList<ProduccionVO>();
	private List<DatoVariableParcelaVO> datosVariablesParcela = new ArrayList<DatoVariableParcelaVO>();

	public String getCodtipoCapital() {
		return codtipoCapital;
	}

	public void setCodtipoCapital(String codtipoCapital) {
		this.codtipoCapital = codtipoCapital;
	}

	public String getSuperficie() {
		return superficie;
	}

	public void setSuperficie(String superficie) {
		this.superficie = superficie;
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

	public String getDesTipoCapital() {
		return desTipoCapital;
	}

	public void setDesTipoCapital(String desTipoCapital) {
		this.desTipoCapital = desTipoCapital;
	}

	public List<DatoVariableParcelaVO> getDatosVariablesParcela() {
		return datosVariablesParcela;
	}

	public void setDatosVariablesParcela(List<DatoVariableParcelaVO> datosVariablesParcela) {
		this.datosVariablesParcela = datosVariablesParcela;
	}

	public List<PrecioVO> getListPrecios() {
		return listPrecios;
	}

	public void setListPrecios(List<PrecioVO> listPrecios) {
		this.listPrecios = listPrecios;
	}

	public List<ProduccionVO> getListProducciones() {
		return listProducciones;
	}

	public void setListProducciones(List<ProduccionVO> listProducciones) {
		this.listProducciones = listProducciones;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefIdParcela() {
		return refIdParcela;
	}

	public void setRefIdParcela(String refIdParcela) {
		this.refIdParcela = refIdParcela;
	}

	public String getMetrosCuadrados() {
		return metrosCuadrados;
	}

	public void setMetrosCuadrados(String metrosCuadrados) {
		this.metrosCuadrados = metrosCuadrados;
	}

	public Boolean getPrecioModif() {
		return precioModif;
	}

	public void setPrecioModif(Boolean precioModif) {
		this.precioModif = precioModif;
	}
}
