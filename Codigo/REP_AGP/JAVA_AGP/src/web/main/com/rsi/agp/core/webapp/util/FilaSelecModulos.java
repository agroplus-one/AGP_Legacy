package com.rsi.agp.core.webapp.util;

import java.util.ArrayList;
import java.util.HashSet;

public class FilaSelecModulos 
{
	private String identificador;
	private String codModulo;
	private int[] filamodulo;
	private String concepto;
	private String valor;
	private String codConceptoPpal;
	private String descConceptoPpal;
	private String codRiesgoCbrto;
	private String descRiesgoCbrto;
	private HashSet codigosRiesgosCubiertos = new HashSet();
	private String nvEleccion;
	
	public String getCodModulo() {
		return codModulo;
	}
	public void setCodModulo(String codModulo) {
		this.codModulo = codModulo;
	}
	public int[] getFilamodulo() {
		return filamodulo;
	}
	public void setFilamodulo(int[] filamodulo) {
		this.filamodulo = filamodulo;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getCodConceptoPpal() {
		return codConceptoPpal;
	}
	public void setCodConceptoPpal(String codConceptoPpal) {
		this.codConceptoPpal = codConceptoPpal;
	}
	public String getDescConceptoPpal() {
		return descConceptoPpal;
	}
	public void setDescConceptoPpal(String descConceptoPpal) {
		this.descConceptoPpal = descConceptoPpal;
	}
	public String getCodRiesgoCbrto() {
		return codRiesgoCbrto;
	}
	public void setCodRiesgoCbrto(String codRiesgoCbrto) {
		this.codRiesgoCbrto = codRiesgoCbrto;
	}
	public String getDescRiesgoCbrto() {
		return descRiesgoCbrto;
	}
	public void setDescRiesgoCbrto(String descRiesgoCbrto) {
		this.descRiesgoCbrto = descRiesgoCbrto;
	}
	
	public boolean existeFila (int numFila)
	{
		boolean retorno = false;
		for (int num : filamodulo)
		{
			if (num == numFila)
			{
				return true;
			}
		}
		return retorno;
	}
	
	public void addRiesgo (int codRiesgo)
	{
		codigosRiesgosCubiertos.add(codRiesgo);
	}
	
	public boolean existeRiesgoCubierto (int codRiesgo)
	{
		return codigosRiesgosCubiertos.contains(codRiesgo);
	}
	public String getNvEleccion() {
		return nvEleccion;
	}
	public void setNvEleccion(String nvEleccion) {
		this.nvEleccion = nvEleccion;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
}
