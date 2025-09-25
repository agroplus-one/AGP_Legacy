package com.rsi.agp.core.decorators;

import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Reglamento;

public class ModelTableDecoratorReglamentos extends TableDecorator{
	
	public String getAdmActions() {
		Reglamento reg = (Reglamento) getCurrentRowObject();
		return "<a href=\"javascript:modificar('"+reg.getId()+"','"+reg.getPlan()+"','"+ reg.getEntidad().getCodentidad()+"','"+reg.getPctentidad()+"','"+reg.getPctrga() + "','"+reg.getUsuario().getCodusuario()+"','"+reg.getEntidad().getNomentidad()+"')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;"+
				"<a href=\"javascript:borrar('"+reg.getId()+"')\" /><img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>&nbsp;";
	}
	
	public String getPlan(){
		Reglamento reg = (Reglamento) getCurrentRowObject();
		return StringUtils.nullToString(reg.getPlan());
	}
	
	public String getPctent(){
		Reglamento reg = (Reglamento) getCurrentRowObject();
		String resultado = "";
		if(reg.getPctentidad() != null)
			resultado = StringUtils.nullToString(reg.getPctentidad()) + "%";
		else
			resultado = "0%";
		return resultado;
	}
	public String getPctrga(){
		Reglamento reg = (Reglamento) getCurrentRowObject();
		String resultado = "";
		if(reg.getPctrga() != null)
			resultado = StringUtils.nullToString(reg.getPctrga()) + "%";
		
		return resultado;
	}
	public Date getFecha(){
		Reglamento reg = (Reglamento) getCurrentRowObject();
		return reg.getFechamodificacion();
	}
	public String getEntidad(){
		Reglamento reg = (Reglamento) getCurrentRowObject();
		return StringUtils.nullToString(reg.getEntidad().getCodentidad());
	}
}
