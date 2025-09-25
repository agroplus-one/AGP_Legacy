package com.rsi.agp.batch.updateFechaEntradaVigor;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.JAXBElement;

import es.agroseguro.serviciosweb.contratacionrenovaciones.ParametrosListaPolizasRenovables;


public class ParametrosListaPolizasRenovablesAgroplus extends ParametrosListaPolizasRenovables{
	
	public void setListaEstados(List<JAXBElement<List<BigInteger>>> lstEstados) {
		this.listaEstados = lstEstados;
	}
	
}
