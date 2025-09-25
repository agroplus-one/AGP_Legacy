package com.rsi.agp.core.decorators;

import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;

public class ModelTableDecoratorRecibosPoliza extends TableDecorator {
	int i=0;
	public String getAcciones(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		
		String acciones = "";
		acciones +="<input type=\"hidden\" name=\"tipoRef"+i+"\" id=\"tipoRef"+i+"\" value= "+reciboPoliza.getTiporef()+"></input>";
		acciones +="<input type=\"hidden\" name=\"idReciboPoliza"+i+"\" id=\"idReciboPoliza"+i+"\" value= "+reciboPoliza.getId()+"></input>";
		acciones += "<a href=\"#\" onclick=\"javascript:verDetalle("+reciboPoliza.getId()+")\"><img src='jsp/img/magnifier.png' alt='Ver detalle' title='Ver detalle'/></a>";
		acciones += "<a href=\"#\" onclick=\"javascript:imprimir("+reciboPoliza.getId()+")\"><img src='jsp/img/displaytag/imprimir.png' alt='Imprimir' title='Imprimir' /></a>";
		i++;
		return acciones;
	}
	public String getFase(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if(reciboPoliza.getRecibo().getCodfase()!=null && !"".equals(reciboPoliza.getRecibo().getCodfase())){
			return reciboPoliza.getRecibo().getCodfase();
		}else{
			return "";
		}		
	}
	public Date getFechaEmsion(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();		
		return reciboPoliza.getRecibo().getFecemisionrecibo();		
	}
	public String getRecibo(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if(reciboPoliza.getRecibo().getCodrecibo()!=null){
			return reciboPoliza.getRecibo().getCodrecibo().toString();
		}else{
			return "";
		}		
	}
	public String getPlan(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if(reciboPoliza.getRecibo().getCodplan()!=null && !"".equals(reciboPoliza.getRecibo().getCodplan())){
			return reciboPoliza.getRecibo().getCodplan();
		}else{
			return "";
		}		
	}
	public String getLinea(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if(reciboPoliza.getRecibo().getCodlinea()!=null){
			return reciboPoliza.getRecibo().getCodlinea().toString();
		}else{
			return "";
		}		
	}
	public String getColectivo(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if(reciboPoliza.getRecibo().getRefcolectivo()!=null && !"".equals(reciboPoliza.getRecibo().getRefcolectivo())){
			return reciboPoliza.getRecibo().getRefcolectivo() + "-" + reciboPoliza.getRecibo().getDccolectivo();
		}else{
			return "";
		}		
	}
	public String getPoliza(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if(reciboPoliza.getRefpoliza() != null && !"".equals(reciboPoliza.getRefpoliza())){
			return reciboPoliza.getRefpoliza();
		}else{
			return "";
		}		
	}
	public String getCIFNIF(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if(reciboPoliza.getNifaseg()!=null && !"".equals(reciboPoliza.getNifaseg())){
			return reciboPoliza.getNifaseg();
		}else{
			return "";
		}		
	}
	public String getTipificacionRecibo(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if (reciboPoliza.getRecibo().getTipificacionRecibos() != null) {
			if(FiltroUtils.noEstaVacio(reciboPoliza.getRecibo().getTipificacionRecibos().getTipificacionRecibo())){
				return reciboPoliza.getRecibo().getTipificacionRecibos().getTipificacionRecibo()+
					"-"+ reciboPoliza.getRecibo().getTipificacionRecibos().getDescripcion();
			}
		}
		return "";
	}
	public String getNombre(){
		ReciboPoliza reciboPoliza = (ReciboPoliza)getCurrentRowObject();
		if(reciboPoliza.getNombreaseg()!= null && !"".equals(reciboPoliza.getNombreaseg())){
			if(reciboPoliza.getApell1aseg()!=null && !"".equals(reciboPoliza.getApell1aseg())){
				if(reciboPoliza.getApell2aseg()!=null && !"".equals(reciboPoliza.getApell2aseg())){
					return reciboPoliza.getNombreaseg() + " " + reciboPoliza.getApell1aseg() + " " + reciboPoliza.getApell2aseg();
				}else{
					return reciboPoliza.getNombreaseg() + " " + reciboPoliza.getApell1aseg();
				}
			}else{
				return reciboPoliza.getNombreaseg();
			}			
		}else{
			if(reciboPoliza.getRazonsocialaseg()!= null && !"".equals(reciboPoliza.getRazonsocialaseg())){
				return reciboPoliza.getRazonsocialaseg();
			}else{
				return "";
			}			
		}		
	}
	
}
