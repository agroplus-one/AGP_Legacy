package com.rsi.agp.core.report;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.rsi.agp.core.report.layout.BeanTablaCoberturasComparativa;
import com.rsi.agp.core.report.layout.ListaBeanLiteralesComparativa;
import com.rsi.agp.core.report.layout.BeanDatosInformativosComparativa;
import com.rsi.agp.core.report.layout.BeanLiteralesComparativa;
import com.rsi.agp.dao.tables.poliza.ListaTipoCapitalComparativa;

public class BeanDistribucionComparativas {
	//Lista de Coberturas
	private List<BeanTablaCoberturasComparativa> tablaCoberturas;
	
	//Lista de Cabeceras de Modulos
	private ArrayList<BeanLiteralesComparativa> cabecera;
	
	//Lista de Importes de Dist Costes
	private ArrayList<ListaBeanLiteralesComparativa> literalesDistCostesGN1;
	private ArrayList<ListaBeanLiteralesComparativa> literalesDistCostesGN2;
	
	//Lista de Literales Dist Costes
	private ArrayList<ListaBeanLiteralesComparativa> listaImportesGN1;
	
	private ArrayList<ListaBeanLiteralesComparativa> listaImportesGN2;
	
	//Lista de  Tipos de Capital
	private LinkedHashSet<ListaTipoCapitalComparativa> tiposCapital;
	
	//Lista de Datos Informativos
	private  List<BeanDatosInformativosComparativa> datosInformativos;	
		
	public BeanDistribucionComparativas() {
		super();
	}

	public ArrayList<ListaBeanLiteralesComparativa> getListaImportesGN1() {
		return listaImportesGN1;
	}

	public void setListaImportesGN1(ArrayList<ListaBeanLiteralesComparativa> listaImportesGN1) {
		this.listaImportesGN1 = listaImportesGN1;
	}

	public ArrayList<ListaBeanLiteralesComparativa> getListaImportesGN2() {
		return listaImportesGN2;
	}

	public void setListaImportesGN2(ArrayList<ListaBeanLiteralesComparativa> listaImportesGN2) {
		this.listaImportesGN2 = listaImportesGN2;
	}

	public List<BeanTablaCoberturasComparativa> getTablaCoberturas() {
		return tablaCoberturas;
	}

	public void setTablaCoberturas(List<BeanTablaCoberturasComparativa> tablaCoberturas) {
		this.tablaCoberturas = tablaCoberturas;
	}

	public ArrayList<ListaBeanLiteralesComparativa> getLiteralesDistCostesGN1() {
		return literalesDistCostesGN1;
	}

	public void setLiteralesDistCostesGN1(ArrayList<ListaBeanLiteralesComparativa> literalesDistCostesGN1) {
		this.literalesDistCostesGN1 = literalesDistCostesGN1;
	}

	public ArrayList<ListaBeanLiteralesComparativa> getLiteralesDistCostesGN2() {
		return literalesDistCostesGN2;
	}

	public void setLiteralesDistCostesGN2(ArrayList<ListaBeanLiteralesComparativa> literalesDistCostesGN2) {
		this.literalesDistCostesGN2 = literalesDistCostesGN2;
	}

	public LinkedHashSet<ListaTipoCapitalComparativa> getTiposCapital() {
		return tiposCapital;
	}

	public void setTiposCapital(LinkedHashSet<ListaTipoCapitalComparativa> tiposCapital) {
		this.tiposCapital = tiposCapital;
	}

	public  List<BeanDatosInformativosComparativa> getDatosInformativos() {
		return datosInformativos;
	}

	public void setDatosInformativos( List<BeanDatosInformativosComparativa> datosInformativos) {
		this.datosInformativos = datosInformativos;
	}

	public ArrayList<BeanLiteralesComparativa> getCabecera() {
		return cabecera;
	}

	public void setCabecera(ArrayList<BeanLiteralesComparativa> cabecera) {
		this.cabecera = cabecera;
	}
}
