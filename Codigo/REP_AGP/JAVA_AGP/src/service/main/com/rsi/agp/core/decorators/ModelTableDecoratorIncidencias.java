package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;

import com.rsi.agp.dao.tables.comisiones.FicheroIncidencia;

public class ModelTableDecoratorIncidencias extends TableDecorator{
	
	public String getAdmActions() {
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
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
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		String resultado = "";
		
		if(fi.getLinea() != null)
			resultado =  StringUtils.nullToString(fi.getLinea().getCodplan());
		
		return resultado;
	}
	
	public String getLinea(){
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		String resultado = "";
		
		if(fi.getLinea() != null)
			resultado =  StringUtils.nullToString(fi.getLinea().getCodlinea());
		
		return resultado;
	}
	
	public String getColectivo(){
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		String resultado = "";		
		
		if(fi.getIdcolectivo()!=null)
			resultado = StringUtils.nullToString(fi.getIdcolectivo());
		
		return resultado;
	}
	
	public String getEsMed(){
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		return StringUtils.nullToString(fi.getSubentidad());
	}
	
	public String getOficina(){
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		return StringUtils.nullToString(fi.getOficina());
				
	}
	
	public String getRefPoliza(){
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		String refPoliza = "";
		if (fi.getRefpoliza() != null)
			refPoliza = StringUtils.nullToString(fi.getRefpoliza());
			
		return refPoliza;
	}

	public String getEstado(){
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		return StringUtils.nullToString(fi.getEstado());
	}
	
	public String getMensaje(){
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		String mensaje = "";
		if (fi.getMensaje()!=null)
			return StringUtils.nullToString(fi.getMensaje().substring(0, 30));
		else
			return mensaje;
	}
	public String getEsCol(){
		FicheroIncidencia fi = (FicheroIncidencia) getCurrentRowObject();
		return StringUtils.nullToString(fi.getEsMedColectivo());
	}
}
	
	