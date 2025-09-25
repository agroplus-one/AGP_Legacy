package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Parcela;

public class ModelTableDecoratorParcelasComplementario extends TableDecorator {

	public String getAdmActions() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		String acciones = "";
		/// mejora 112 Angel 01/02/2012 añadida la opción de ver la póliza sin opción a editarla también con estado grabación definitiva
		if(!capAseg.getParcela().getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA) &&
				!capAseg.getParcela().getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)){
			acciones += "<a href=\"javascript:abrirIncremento('"+capAseg.getIdcapitalasegurado()+"')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";
		}
		acciones+="<input type='hidden' name='idRow' value='"+ capAseg.getIdcapitalasegurado()+ "#" + capAseg.getSuperficie().toString() +"' />";
		
		return acciones;
	}

	public String getNombre() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getNomparcela());
				
	}
	
	public String getCodtermino() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		if (capAseg.getParcela().getTermino() != null && capAseg.getParcela().getTermino().getId() != null) {
			return StringUtils.nullToString(capAseg.getParcela().getTermino().getId().getCodtermino());
		} else {
			return "";
		}
	}

	public String getCodsubtermino() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		if (capAseg.getParcela().getTermino() != null && capAseg.getParcela().getTermino().getId() != null) {
			return StringUtils.nullToString(capAseg.getParcela().getTermino().getId().getSubtermino());
		} else {
			return "";
		}
	}
	
	public String getIdCat() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		if (capAseg.getParcela().getPoligono() != null && capAseg.getParcela().getParcela() != null) {
			return capAseg.getParcela().getPoligono() +" - "+capAseg.getParcela().getParcela();
		} else {
			return getSigPac(capAseg.getParcela());
		}
	}

	public String getSigPac(Parcela pa) {
		String sigPac = "";
		if(pa.getCapitalAsegurados().size() > 0) {
			CapitalAsegurado ca = (CapitalAsegurado) pa.getCapitalAsegurados().toArray()[0];
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
		}
		return sigPac;
	}
	public String getCodprovincia() {
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		if (capAseg.getParcela().getTermino() != null	&& capAseg.getParcela().getTermino().getId() != null) {
			return StringUtils.nullToString(capAseg.getParcela().getTermino().getId().getCodprovincia());
		} else {
			return "";
		}
	}
	public String getCodcomarca(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		if(capAseg.getParcela().getTermino() != null	&& capAseg.getParcela().getTermino().getComarca() .getId() != null){
			return capAseg.getParcela().getTermino().getComarca().getId().getCodcomarca().toString();
		}else{
			return "";
		}		
	}
	public String getTcapital(){
		String resultado="";
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		if (capAseg.getTipoCapital() != null){
			resultado = capAseg.getTipoCapital().getDestipocapital();
		}
		
		return resultado;
	}
	
	public String getPrecio(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		String res="";
		if(capAseg.getPrecio() != null){
			res = capAseg.getPrecio().toString();
		}
		return 	res;
	}
	public String getProduccion(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		String res = "";
		if(capAseg.getProduccion() != null){
			res = capAseg.getProduccion().toString();
		}
		return res;
	}
	public String getSuperf(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return capAseg.getSuperficie().toString();
	}
	
	public String getNomPar(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		String res = "";
		if(capAseg.getParcela().getNomparcela()!=null){
			res =  capAseg.getParcela().getNomparcela();
		}
		return res;
		
	}
	public String getAlta(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		/*String alta = "";
		if(capAseg.getAltaencomplementario() != null){
			alta = capAseg.getAltaencomplementario().toString();
		}*/
		
		return "<input type=\"checkbox\" value=\"" + capAseg.getIdcapitalasegurado()+"\"onclick=\"onClickInCheck2('"+capAseg.getIdcapitalasegurado()+"')\" id=\""+capAseg.getIdcapitalasegurado()+"\" name=\""+capAseg.getIdcapitalasegurado()+"\" class=\"compruebaAlta dato\"/>";
	}
	public String getIncremento(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		String res = null;
		if(capAseg.getIncrementoproduccion() != null){
			res = capAseg.getIncrementoproduccion().floatValue() + "";
		}
		return  res;
	}
	public String getNumero(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return StringUtils.nullToString(capAseg.getParcela().getHoja()) + "-"+ StringUtils.nullToString(capAseg.getParcela().getNumero());
	}
	public String getCodVariedad(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return capAseg.getParcela().getCodvariedad().toString();
	}
	public String getCodCultivo(){
		CapitalAsegurado capAseg = (CapitalAsegurado) getCurrentRowObject();
		return capAseg.getParcela().getCodcultivo().toString();
	}

}
