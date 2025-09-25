package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado;

public class ModelTableDecoratorParcelasReduccionCapital extends TableDecorator {

	private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	public String getColumnaAlta(){
		
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();

		//fix local entidades externas no lista parcelas en tabla
		Character altaAnexoVal = new Character('N');
		if(capitalAsegurado.getAltaenanexo()==null) {
			capitalAsegurado.setAltaenanexo(altaAnexoVal);
		}
		//fix local entidades externas no lista parcelas en tabla
		
		return "<input type=\"checkbox\" onclick=\"numero_check_seleccionados(0);capitalAlta(this.id)\" id=\"alta_" + capitalAsegurado.getId() + 
			"\" name=\"alta_" + capitalAsegurado.getId() + "\"" + 
			("S".equals(capitalAsegurado.getAltaenanexo().toString()) ? "checked" : "" ) + 
			" class=\"compruebaAlta dato\"/>";
	}

	public String getColumnaN(){		
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		
		if(capitalAsegurado.getParcela().getHoja() == null || capitalAsegurado.getParcela().getNumero()==null ){
			return "";	
		}else{
			return capitalAsegurado.getParcela().getHoja() + " - " + capitalAsegurado.getParcela().getNumero();
		}
	}
	public String getColumnaPRV(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if(capitalAsegurado.getParcela().getCodprovincia()== null){
			return "";
		}else{
			return capitalAsegurado.getParcela().getCodprovincia().toString();
		}
	}
	public String getColumnaCMC(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if(capitalAsegurado.getParcela().getCodcomarca() == null){
			return "";
		}else{
			return capitalAsegurado.getParcela().getCodcomarca().toString();
		}
	}
	public String getColumnaTRM(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if(capitalAsegurado.getParcela().getCodtermino()== null){
			return "";
		}else{
			return capitalAsegurado.getParcela().getCodtermino().toString();
		}
	}
	public String getColumnaSBT(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if(capitalAsegurado.getParcela().getSubtermino() == null){
			return "";
		}else{
			return capitalAsegurado.getParcela().getSubtermino().toString();
		}
	}
	public String getColumnaCUL(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getCodcultivo() == null) {
			return "";
		} else {
			return capitalAsegurado.getParcela().getCodcultivo().toString();	
		}		
	}
	
	public String getColumnaVAR(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getParcela().getCodvariedad() == null) {
			return "";
		} else {
			return capitalAsegurado.getParcela().getCodvariedad().toString();
		}
	}
	
	public String getColumnaIdCat(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if(capitalAsegurado.getParcela().getPoligono() == null || capitalAsegurado.getParcela().getParcela_1() == null ){
			return "";
		}else{
			return capitalAsegurado.getParcela().getPoligono() + " - " + capitalAsegurado.getParcela().getParcela_1();
		}
	}
	
	public String getColumnaSIGPAC(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if(capitalAsegurado.getParcela().getCodprovsigpac() == null || capitalAsegurado.getParcela().getCodtermsigpac()  == null 
				|| capitalAsegurado.getParcela().getAgrsigpac()  == null || capitalAsegurado.getParcela().getZonasigpac()  == null || 
				capitalAsegurado.getParcela().getPoligonosigpac()  == null || capitalAsegurado.getParcela().getParcelasigpac()  == null || 
				capitalAsegurado.getParcela().getRecintosigpac()  == null){
			return "";
		}else{
			return capitalAsegurado.getParcela().getCodprovsigpac() + "-" + capitalAsegurado.getParcela().getCodtermsigpac() + "-" +
			capitalAsegurado.getParcela().getAgrsigpac() + "-" + capitalAsegurado.getParcela().getZonasigpac() + "-" + capitalAsegurado.getParcela().getPoligonosigpac() 
					+ "-" + capitalAsegurado.getParcela().getParcelasigpac() + "-" + capitalAsegurado.getParcela().getRecintosigpac();
		}
	}
	
	public String getIdCat() {
		CapitalAsegurado capAsegSiniestradoDV = (CapitalAsegurado) getCurrentRowObject();
		if (capAsegSiniestradoDV.getParcela().getPoligono() != null && capAsegSiniestradoDV.getParcela().getParcela_1()!= null) {
			return capAsegSiniestradoDV.getParcela().getPoligono() +" - "+capAsegSiniestradoDV.getParcela().getParcela_1();
		} else {
			return getSigPac(capAsegSiniestradoDV.getParcela());
		}
	}

	public String getSigPac(com.rsi.agp.dao.tables.reduccionCap.Parcela parcela) {
		String sigPac = "";
		if (parcela.getCodprovsigpac() != null)
			sigPac = parcela.getCodprovsigpac().toString();
		if (parcela.getCodtermsigpac() != null)
			sigPac += "-"+  parcela.getCodtermsigpac().toString();
		if (parcela.getAgrsigpac() != null)
			sigPac += "-"+ parcela.getAgrsigpac().toString();
		if (parcela.getZonasigpac() != null)
			sigPac += "-"+ parcela.getZonasigpac().toString();
		if (parcela.getPoligonosigpac() != null)
			sigPac += "-"+ parcela.getPoligonosigpac().toString();
		if (parcela.getParcelasigpac() != null)
			sigPac += "-"+ parcela.getParcelasigpac().toString();
		if (parcela.getRecintosigpac() != null)
			sigPac += "-"+ parcela.getRecintosigpac().toString();		
	
		return sigPac;
	}

	public String getColumnaTCap(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getCodtipocapital() == null) {
			return "";
		} else {
			return capitalAsegurado.getCodtipocapital().toString();
		}
	}

	public String getColumnaSuperf(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		if (capitalAsegurado.getSuperficie() == null) {
			return "";
		} else {
			return capitalAsegurado.getSuperficie().toString();
		}
	}
	
	public String getColumnaPrecio(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		
		if(capitalAsegurado.getPrecio() == null){
			return "";
		}else{
			return capitalAsegurado.getPrecio().toString();
		}
	}

	public String getColumnaProd(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		BigDecimal prod = capitalAsegurado.getProd();
		Long id = capitalAsegurado.getId();

		if(prod == null){
			return "";
		}else{
			return 	
				"<label id=\"prod_" + id + "\"" + " value=" + prod + " class=\"literal\">" + 
				prod + 
				"</label>";
		}
	}

	public String getColumnaProdPost(){
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) getCurrentRowObject();
		BigDecimal prodRed = capitalAsegurado.getProdred();
		Long id = capitalAsegurado.getId();
		StringBuffer campoProdPost = new StringBuffer();
		
		//if (prodRed == null) prodRed = BigDecimal.ZERO; 
		// DAA 26/07/2012
		campoProdPost.append(
				"<input type=\"text\" onchange=\"capitalProdPost(this.id,this.value)\" id=\"prodPost_" + id + "\"" + 
				"name=\"prodPost_" + id + "\"" + " value=" + (prodRed!= null ? prodRed :  "\"\"")  + " class=\"dato\"/>");
		
		return campoProdPost.toString();

	}
}
