package com.rsi.agp.vo;

public class CaracteristicaRiesgoVO implements Comparable<CaracteristicaRiesgoVO> {

	private String tipo;
	private String observacion;
	private String valor;
	private Integer numColumna;

	public CaracteristicaRiesgoVO() {
		this.tipo = new String("");
		this.observacion = new String("");
		this.valor = new String("");
		this.numColumna = 9999;
	}

	public CaracteristicaRiesgoVO(String tipo, String observacion, String valor, Integer numColumna) {
		super();
		this.tipo = tipo;
		this.observacion = observacion;
		this.valor = valor;
		this.numColumna = numColumna;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public Integer getNumColumna() {
		return numColumna;
	}

	public void setNumColumna(Integer numColumna) {
		this.numColumna = numColumna;
	}

	public int compareTo(CaracteristicaRiesgoVO object) {
		CaracteristicaRiesgoVO otroUsuario = (CaracteristicaRiesgoVO) object;
		return numColumna.compareTo(otroUsuario.getNumColumna());
	}
}
