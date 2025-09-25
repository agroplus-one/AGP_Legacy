package com.rsi.agp.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.agroseguro.acuseRecibo.Error;

public class ErrorAcuseDefMultiple {
	
	private int codigo = 0;
	private int tipo = 0;
	private String descripcion = "";
	private int hoja = 0;
	private int numero = 0;
	
	private Log logger = LogFactory.getLog(ErrorAcuseDefMultiple.class);
	
	public ErrorAcuseDefMultiple (Error e) {
		try {
			this.codigo = Integer.parseInt(e.getCodigo());
			this.tipo = e.getTipo();
			this.descripcion = e.getDescripcion();
			this.hoja = parseAttrXPath(e.getLocalizacion().getXpath(), "hoja");
			this.numero = parseAttrXPath(e.getLocalizacion().getXpath(), "numero");
		} catch (Exception e2) {
			logger.error("Error al crear el objeto ErrorAcuseDefMultiple", e2);			
		}
	}
	
	
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getHoja() {
		return hoja;
	}
	public void setHoja(int hoja) {
		this.hoja = hoja;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}

	private int parseAttrXPath (String xpath, String attr) {
		
		try {
			String[] resPre = xpath.split("@" + attr + "='");
			return Integer.parseInt(resPre[1].substring(0, resPre[1].indexOf("'")));
		} catch (Exception e) {
			logger.debug("Error al extraer el atributo '" + attr + "' de la cadena '" + xpath + "'", e);
		}
		
		return -1;
	}
}
