package com.rsi.agp.vo;

import java.util.ArrayList;
import java.util.List;

public class PreciosProduccionesVO {

	private List<PrecioVO> listPrecios = new ArrayList<PrecioVO>();
	private List<ProduccionVO> listProducciones = new ArrayList<ProduccionVO>();
	private String mensajeError = null; //Para recogerlo en la pantalla de parcelas
	private boolean rdtosLibres = false;

	public boolean isRdtosLibres() {
		return rdtosLibres;
	}

	public void setRdtosLibres(boolean rdtosLibres) {
		this.rdtosLibres = rdtosLibres;
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

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}
}
