package com.rsi.agp.core.decorators;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestradoDV;
import com.rsi.agp.dao.tables.siniestro.ParcelaSiniestro;

public class ModelTableDecoratorParcelasSiniestradas extends TableDecorator {

	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
	
	/**
	 * Método para obtener el valor de las columnas hoja y número de la parcela
	 * @return hoja - número de parcela
	 */
	public String getColumnaN(){		
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		if(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getHoja() == null || capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getNumero()==null ){
			return "";	
		}else{
			return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getHoja() + " - " + capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getNumero();
		}
	}

	/**
	 * Método para obtener la provincia de la parcela
	 * @return Código de provincia de la parcela
	 */
	public String getColumnaPRV(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		if(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermino()== null){
			return "";
		}else{
			return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodprovincia().toString();
		}
	}
	
	/**
	 * Método para obtener la comarca de la parcela
	 * @return Código de comarca de la parcela
	 */
	public String getColumnaCMC(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		if(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermino()== null){
			return "";
		}else{
			return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodcomarca().toString();
		}
	}
	
	/**
	 * Método para obtener el término de la parcela
	 * @return Código de término de la parcela
	 */
	public String getColumnaTRM(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		if(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermino()== null){
			return "";
		}else{
			return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermino().toString();
		}
	}
	
	/**
	 * Método para obtener el subtérmino de la parcela
	 * @return Subtérmino de la parcela
	 */
	public String getColumnaSBT(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		if(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodtermino()== null){
			return "";
		}else if(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSubtermino() == null){
			return "";
		}else{
			return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getSubtermino().toString();
		}
	}
	
	/**
	 * Método para obtener el cultivo de la parcela
	 * @return Código de cultivo de la parcela
	 */
	public String getColumnaCUL(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodcultivo().toString();
	}
	
	/**
	 * Método para obtener la variedad de la parcela
	 * @return Código de variedad de la parcela
	 */
	public String getColumnaVAR(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getCodvariedad().toString();
	}
	
	/**
	 * Método para obtener la identificación catastral o SIGPAC de la parcela
	 * @return Identificación Catastral o SIGPAC de la parcela
	 */
	public String getIdCat() {
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		if (capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getPoligono() != null && capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela_1()!= null) {
			return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getPoligono() +" - "+capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getParcela_1();
		} else {
			return getSigPac(capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro());
		}
	}
	
	/**
	 * Método para montar la cadena de texto que representa el SIGPAC de una parcela
	 * @param pa Parcela del siniestro.
	 * @return SIGPAC de la parcela.
	 */
	private String getSigPac(ParcelaSiniestro pa) {
		String sigPac = StringUtils.nullToString(pa.getCodprovsigpac()) + "-" + StringUtils.nullToString(pa.getCodtermsigpac()) + "-" + 
						StringUtils.nullToString(pa.getAgrsigpac()) + "-" + StringUtils.nullToString(pa.getZonasigpac()) + "-" + 
						StringUtils.nullToString(pa.getPoligonosigpac()) + "-" + StringUtils.nullToString(pa.getParcelasigpac()) + "-" + 
						StringUtils.nullToString(pa.getRecintosigpac());
		
		return sigPac;
	}

	/**
	 * Método para obtener el nombre de la parcela
	 * @return Nombre de la parcela
	 */
	public String getColumnaParaje(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		return capAsegSiniestradoDV.getCapAsegSiniestro().getParcelaSiniestro().getNomparcela();	
	}
	
	/**
	 * Método para obtener la superficie de la parcela
	 * @return Superficie de la parcela
	 */
	public String getColumnaSuperf(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		return capAsegSiniestradoDV.getCapAsegSiniestro().getSuperficie().toString();
	}
	
	/**
	 * Método para obtener el tipo de capital de la parcela
	 * @return Código de tipo de capital de la parcela
	 */
	public String getColumnaCapital(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		return capAsegSiniestradoDV.getCapAsegSiniestro().getCodtipocapital().toString();
	}
	
	/**
	 * Método para saber si un capital asegurado está dado de alta en el siniestro
	 * @return Si / No
	 */
	public String getColumnaAlta(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		String cadena = ""; 
		if (capAsegSiniestradoDV.getCapAsegSiniestro().getAltaensiniestro().toString().equals("S"))
			cadena = "<span style='color:red;'>Si</span>";
		else
			cadena += "No";
		return cadena;
	}

	/**
	 * Método para saber si en un capital asegurado está dado de alta el dato "Frutos caídos"
	 * @return Si / No
	 */
	public String getColumnaFrutos(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		if (capAsegSiniestradoDV.getCapAsegSiniestro().getAltaensiniestro().toString().equals("S")){
			return capAsegSiniestradoDV.isFrutos() ? "Si" : "No";
		}
		return "";
	}

	/**
	 * Método para obtener la fecha de recolección de la parcela
	 * @return Fecha de recolección de la parcela
	 */
	public String getColumnaFechaRecoleccion(){
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
		String fechaRecoleccion = "";
		
		try{
			fechaRecoleccion = df.format(capAsegSiniestradoDV.getFechaRecoleccion());
		
		}catch (Exception ignore){
		}

		return fechaRecoleccion;
	}
	
	/**
	 * Método para pintar la columna del check para los cambios masivos
	 * @return input tipo check.
	 */
	public String getCheckCambioMasivo(){
		String result;
		CapAsegSiniestradoDV capAsegSiniestradoDV = (CapAsegSiniestradoDV) getCurrentRowObject();
    	result =  "<input type=\"checkbox\" id=\"checkParcela_" + capAsegSiniestradoDV.getCapAsegSiniestro().getId() + "\"  name=\"checkParcela_" + capAsegSiniestradoDV.getCapAsegSiniestro().getId() + "\" onClick=\"onClickInCheck2('checkParcela_" + capAsegSiniestradoDV.getCapAsegSiniestro().getId() + "')\" class=\"dato\"/>";
		
		return result;
	}
	
}
