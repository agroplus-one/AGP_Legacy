package com.rsi.agp.core.decorators;

import java.math.BigDecimal;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.log.ImportacionTabla;

public class ModelTableDecoratorDetHistorico extends TableDecorator
{
	public String getImpSelec() {		 
    	ImportacionTabla it = (ImportacionTabla)getCurrentRowObject();
    	
    	String cadena = "";
    	
    	if (it.getTablaCondicionado().getCodtablacondicionado().equals((new BigDecimal(8))))  {
    		cadena += "<a href=\"javascript:detallehistorico.consultaTabla("+it.getTablaCondicionado().getCodtablacondicionado().toString()+")\"><img width=\"16\" alt=\"Detalle Grupo Tasas\" src=\"jsp/img/magnifier.png\"/></a>&nbsp;";
    		cadena += "<a href=\"javascript:detallehistorico.consultaTabla('"+it.getTablaCondicionado().getCodtablacondicionado().toString()+ "R"+"')\"><img width=\"16\" alt=\"Detalle Grupo Tasas Riesgo\" src=\"jsp/img/magnifier.png\"/></a>";    		
    	}else if(it.getTablaCondicionado().getCodtablacondicionado().equals((new BigDecimal(406))))  {
    		cadena += "<a href=\"javascript:detallehistorico.consultaTabla("+it.getTablaCondicionado().getCodtablacondicionado().toString()+")\"><img width=\"16\" alt=\"Detalle Grupo Tasas Ganado\" src=\"jsp/img/magnifier.png\"/></a>&nbsp;";
    		cadena += "<a href=\"javascript:detallehistorico.consultaTabla('"+it.getTablaCondicionado().getCodtablacondicionado().toString()+ "R"+"')\"><img width=\"16\" alt=\"Detalle Grupo Tasas Riesgo Ganado\" src=\"jsp/img/magnifier.png\"/></a>";    		
    	}else {
    		cadena += "<a href=\"javascript:detallehistorico.consultaTabla("+it.getTablaCondicionado().getCodtablacondicionado().toString()+")\"><img width=\"16\" alt=\"Detalle\" src=\"jsp/img/magnifier.png\"/></a>";
    	}
    	
    	return cadena;    	
    }
	
	public String getImpTabla() {
		ImportacionTabla it = (ImportacionTabla)getCurrentRowObject();
		return it.getTablaCondicionado().getDestablacondicionado();
	}
	
	public String getImpEstado() {
		ImportacionTabla it = (ImportacionTabla)getCurrentRowObject();
		return it.getEstado();
	}
	
	public String getImpDetalle() {
		ImportacionTabla it = (ImportacionTabla)getCurrentRowObject();
		return it.getDescestado();
	}
	
	public String getImpFichero() {
		ImportacionTabla it = (ImportacionTabla)getCurrentRowObject();
		if (it.getFicherozip()==null)
			return "";
		return it.getFicherozip();
	}
}
