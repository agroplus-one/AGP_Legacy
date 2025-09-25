package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public class ModelTableDecoratorPantallasConfigurables extends TableDecorator{
	public String getAdmActions() {
    	PantallaConfigurable  pc = (PantallaConfigurable)getCurrentRowObject();
    	return //"<a href=\"javascript:pantallasConfig.replicar('"+ pc.getLinea().getCodlinea() +"','"+ pc.getLinea().getCodplan() +"','"+ pc.getLinea().getLineaseguroid() +"')\"><img src=\"jsp/img/displaytag/replicar.png\" alt=\"Replicar\" style=\"margin-right:4px\" /></a>" +
    	       "<a href=\"javascript:editar('"+ pc.getIdpantallaconfigurable() +"')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a> " +
    		   "<a href=\"javascript:eliminar('"+ pc.getIdpantallaconfigurable() +"')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar\" title=\"Eliminar\"/></a> " +
    		   "<a href=\"javascript:pantallasConfig.tallerConfiguracion('"+ pc.getIdpantallaconfigurable() +"')\"><img src=\"jsp/img/displaytag/llave.png\" alt=\"Taller de configuraci&oacute;n\" title=\"Taller de configuraci&oacute;n\"/></a>" +
    		   	"<input type='hidden' name='idRow' value='"+ pc.getIdpantallaconfigurable() +"' />";
    }
	
    public String getAdmPlanes() {
    	PantallaConfigurable  pc = (PantallaConfigurable)getCurrentRowObject();
    	return pc.getLinea().getCodplan().toString();
    }
    
    public String getAdmLineas() {
    	PantallaConfigurable  pc = (PantallaConfigurable)getCurrentRowObject();
    	return StringUtils.nullToString(pc.getLinea().getCodlinea());
    }
    
    public String getAdmPantalla() {
    	PantallaConfigurable  pc = (PantallaConfigurable)getCurrentRowObject();
    	return pc.getPantalla().getDescpantalla();
    }  
}
