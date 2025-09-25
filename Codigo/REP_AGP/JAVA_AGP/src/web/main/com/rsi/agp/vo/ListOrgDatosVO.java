package com.rsi.agp.vo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ListOrgDatosVO {

	private List cabeceras;
	private List nombreAtributo;
	private ArrayList<ItemVistaVO> datosVO = new ArrayList<ItemVistaVO>();
	private String nombreDescriptivo;
	private String nombreVO;

	public List getCabeceras() {
		return cabeceras;
	}

	public void setCabeceras(List cabeceras) {
		this.cabeceras = cabeceras;
	}

	public List getNombreAtributo() {
		return this.nombreAtributo;
	}

	public void setNombreAtributo(List nombreAtributo) {
		this.nombreAtributo = nombreAtributo;
	}

	public ArrayList<ItemVistaVO> getDatosVO() {
		return datosVO;
	}

	public void setDatosVO(ArrayList<ItemVistaVO> datosVO) {
		this.datosVO = datosVO;
	}

	public String getNombreDescriptivo() {
		return nombreDescriptivo;
	}

	public void setNombreDescriptivo(String nombreDescriptivo) {
		this.nombreDescriptivo = nombreDescriptivo;
	}

	public String getNombreVO() {
		return nombreVO;
	}

	public void setNombreVO(String nombreVO) {
		this.nombreVO = nombreVO;
	}
}
