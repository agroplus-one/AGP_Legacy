package com.rsi.agp.core.decorators;

import java.math.BigDecimal;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;

public class ModelTableDecoratorRevProduccionPrecios extends TableDecorator{

	public String getCapitalesAseguradosSelec () {
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String cadenaDev ="";
		
		String desTipoCapital = "";
		String sigPac = "";
		String superficie = "";
		if (ca.getTipoCapital().getDestipocapital() != null)
			desTipoCapital = ca.getTipoCapital().getDestipocapital().toString();
		
		if (ca.getParcela().getCodprovsigpac() != null)
			sigPac += ca.getParcela().getCodprovsigpac().toString();
		if (ca.getParcela().getCodtermsigpac() != null)
			sigPac += "-"+ ca.getParcela().getCodtermsigpac().toString();
		if (ca.getParcela().getAgrsigpac() != null)
			sigPac += "-"+ ca.getParcela().getAgrsigpac().toString();
		if (ca.getParcela().getZonasigpac() != null)
			sigPac += "-"+ ca.getParcela().getZonasigpac().toString();
		if (ca.getParcela().getPoligonosigpac() != null)
			sigPac += "-"+ ca.getParcela().getPoligonosigpac().toString();
		if (ca.getParcela().getParcelasigpac() != null)
			sigPac += "-"+ ca.getParcela().getParcelasigpac().toString();
		if (ca.getParcela().getRecintosigpac() != null)
			sigPac += "-"+ ca.getParcela().getRecintosigpac().toString();		
		
		if (ca.getSuperficie() != null)
			superficie = ca.getSuperficie().toString();
		
		cadenaDev += "<a href=\"javascript:modificar('"+desTipoCapital+"','"+sigPac+"','"+superficie+"');activacion.getLimitesProduccion_ajax('"+ca.getIdcapitalasegurado()+"');\"> ";
		cadenaDev += "<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>";
		cadenaDev += "<input type=\"hidden\" name=\"idCapitalAseguradoFila\" id=\"idCapitalAseguradoFila\" value=\""+ca.getIdcapitalasegurado()+"\"/>";
		cadenaDev += "<input type=\"hidden\" name=\"listaProduccionMod_" + ca.getIdcapitalasegurado()+ "\" id=\"listaProduccionMod_" + ca.getIdcapitalasegurado()+ "\" value=\"\"/>";
		cadenaDev += "<input type=\"hidden\" name=\"listaPrecioMod_" + ca.getIdcapitalasegurado()+ "\" id=\"listaPrecioMod_" + ca.getIdcapitalasegurado()+ "\" value=\"\"/>";

    	return cadenaDev;    	
	}
	
	public String getParcelaHoja () 
	{
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String parcelaHoja = "";
		String parcelaNumero = "";
		if (ca.getParcela().getHoja() != null)
			parcelaHoja = ca.getParcela().getHoja().toString();
		if (ca.getParcela().getNumero() != null)
			parcelaNumero = ca.getParcela().getNumero().toString();
		
		return parcelaHoja +" - "+parcelaNumero;
	}
	
	public String getCodProvSigpac() {
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String parcelaPoligono = "";
		String parcelaParcela = "";
		if (ca.getParcela().getPoligono() != null)
			parcelaPoligono = ca.getParcela().getPoligono().toString();
		if (ca.getParcela().getParcela() != null)
			parcelaParcela = ca.getParcela().getParcela().toString();		
		
		return parcelaPoligono + " - " +parcelaParcela;
	}
	
	public String getSigPac() {
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String sigPac = "";
		if (ca.getParcela().getCodprovsigpac() != null)
			sigPac = ca.getParcela().getCodprovsigpac().toString();
		if (ca.getParcela().getCodtermsigpac() != null)
			sigPac += "-"+  ca.getParcela().getCodtermsigpac().toString();
		if (ca.getParcela().getAgrsigpac() != null)
			sigPac += "-"+ ca.getParcela().getAgrsigpac().toString();
		if (ca.getParcela().getZonasigpac() != null)
			sigPac += "-"+ ca.getParcela().getZonasigpac().toString();
		if (ca.getParcela().getPoligonosigpac() != null)
			sigPac += "-"+ ca.getParcela().getPoligonosigpac().toString();
		if (ca.getParcela().getParcelasigpac() != null)
			sigPac += "-"+ ca.getParcela().getParcelasigpac().toString();
		if (ca.getParcela().getRecintosigpac() != null)
			sigPac += "-"+ ca.getParcela().getRecintosigpac().toString();		
		
		return sigPac;
	}
	
	public String getDesTipoCapital(){
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String desTipoCapital = "";
		if (ca.getTipoCapital().getDestipocapital() != null)
			desTipoCapital = ca.getTipoCapital().getDestipocapital().toString();
		
		return desTipoCapital;
	}
	
	public String getSuperficie () {
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String superficie = "";
		if (ca.getSuperficie() != null)
			superficie = ca.getSuperficie().toString();
		
		return superficie;
	}
	
	public String getProduccionInt (){
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String produccionInt = "";
		CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) ca
				.getCapAsegRelModulos().toArray()[0];
		if (capAsegRelMod.getProduccion() != null)
			produccionInt = capAsegRelMod.getProduccion().toString();
		
		return produccionInt;
	}
	
	public String getProduccionMod (){
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String produccionMod = "";
		CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) ca
				.getCapAsegRelModulos().toArray()[0];
		if (capAsegRelMod.getProduccionmodif() != null)
			produccionMod = capAsegRelMod.getProduccionmodif().toString();
		
		return produccionMod;
	}
	
	public String getPrecioInt (){
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String precioInt = "";
		CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) ca
				.getCapAsegRelModulos().toArray()[0];		
		if (capAsegRelMod.getPrecio() != null)
			precioInt = capAsegRelMod.getPrecio().toString();
		
		return precioInt;
	}	
	
	public String getPrecioMod (){
		CapitalAsegurado ca = (CapitalAsegurado)getCurrentRowObject();
		String precioMod = "";
		CapAsegRelModulo capAsegRelMod = (CapAsegRelModulo) ca
				.getCapAsegRelModulos().toArray()[0];
		if (capAsegRelMod.getPreciomodif() != null)
			precioMod = capAsegRelMod.getPreciomodif().toString();
		
		return precioMod;
	}
	
}
