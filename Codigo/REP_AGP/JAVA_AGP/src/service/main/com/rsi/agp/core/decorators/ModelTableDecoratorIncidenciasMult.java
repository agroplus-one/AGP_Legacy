package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;

import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultIncidencias;

public class ModelTableDecoratorIncidenciasMult extends TableDecorator{
	
	public String getAdmActions() {
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		String acciones = "";
		acciones += "<a href=\"#\" onclick=\"javascript:modificar('"+fi.getIdcolectivo()+"', '"+fi.getLinea().getCodlinea()+"','"+
				fi.getLinea().getCodplan()+"','"+getEsMed()+"',"+"'"+StringUtils.nullToString(fi.getOficina())+"','"+
				StringUtils.nullToString(fi.getRefpoliza())+"','"+fi.getEstado()+"','"+this.getEsCol()+"')\">" +
				"<img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/></a>&nbsp;";
		
		if (!fi.getEstado().equals("Correcto"))
			acciones += "<a href=\"javascript:showMensajeError('"+fi.getMensaje()+"');\"><img src=\"jsp/img/folderopen.gif\" alt=\"Mensajes\" title=\"Mensajes\"/></a>&nbsp;";
		
		return acciones;
	}
	
	public String getPlan(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		String resultado = "";
		
		if(fi.getLinea() != null)
			resultado =  StringUtils.nullToString(fi.getLinea().getCodplan());
		
		return resultado;
	}
	
	public String getLinea(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		String resultado = "";
		
		if(fi.getLinea() != null)
			resultado =  StringUtils.nullToString(fi.getLinea().getCodlinea());
		
		return resultado;
	}
	
	public String getColectivo(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		String resultado = "";		
		
		if(fi.getIdcolectivo()!=null)
			resultado = StringUtils.nullToString(fi.getIdcolectivo());
		
		return resultado;
	}
	
	public String getEsMed(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		return StringUtils.nullToString(fi.getSubentidad());
	}
	
	public String getOficina(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		return StringUtils.nullToString(fi.getOficina());
				
	}
	
	public String getRefPoliza(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		String refPoliza = "";
		if (fi.getRefpoliza() != null)
			refPoliza = StringUtils.nullToString(fi.getRefpoliza());
			
		return refPoliza;
	}

	public String getEstado(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		return StringUtils.nullToString(fi.getEstado());
	}
	
	public String getMensaje(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		String mensaje = "";
		if (fi.getMensaje()!=null)
			return StringUtils.nullToString(fi.getMensaje().substring(0, 30));
		else
			return mensaje;
	}
	public String getEsCol(){
		FicheroMultIncidencias fi = (FicheroMultIncidencias) getCurrentRowObject();
		return StringUtils.nullToString(fi.getEsMedColectivo());
	}
}
	
	