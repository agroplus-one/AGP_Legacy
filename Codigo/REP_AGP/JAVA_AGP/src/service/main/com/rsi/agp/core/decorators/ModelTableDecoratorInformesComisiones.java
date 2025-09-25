package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.ReportCierre;

public class ModelTableDecoratorInformesComisiones extends TableDecorator{
	
	public String getAcciones(){
		String acciones = "";
		ReportCierre fich = (ReportCierre) getCurrentRowObject();
		
		acciones += "<a href=\"cierre.html?method=doVerContenidoArchivo&idInforme="+fich.getId()+"\"><img src='jsp/img/displaytag/download.png' alt='Descargar Archivo' title='Descargar Archivo'/></a>";
		
		return acciones;
	}	
	public String getNombreFichero(){
		ReportCierre fich = (ReportCierre) getCurrentRowObject();
		return StringUtils.nullToString(fich.getNombrefichero());
	}	
}
