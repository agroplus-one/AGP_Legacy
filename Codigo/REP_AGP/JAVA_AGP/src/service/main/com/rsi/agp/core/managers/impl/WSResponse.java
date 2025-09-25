package com.rsi.agp.core.managers.impl;

/**
 * Clase genérica que encierra la respuesta de los servicios web de Asegurado Datos y Medidas
 * @author srojo
 *
 * @param <T>
 */
public class WSResponse<T> {

	private T data;
	
	public T getData(){
		return data;
	}
	
	public void setData(T data){
		this.data = data;
	}
}
