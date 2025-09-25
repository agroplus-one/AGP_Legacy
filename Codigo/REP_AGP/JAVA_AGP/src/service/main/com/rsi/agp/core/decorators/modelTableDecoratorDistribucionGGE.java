package com.rsi.agp.core.decorators;

import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.comisiones.GGESubentidades;

public class modelTableDecoratorDistribucionGGE extends TableDecorator {
	
	public String getAdmActions() {
		GGESubentidades geeSubentidades = (GGESubentidades) getCurrentRowObject();
		String acciones = "";
            
			acciones +=  "<a href=\"javascript:modificar('" + 
			geeSubentidades.getId() + "','" +
			geeSubentidades.getEntidad().getCodentidad() + "','" +
			geeSubentidades.getPlan() + "','" +
			geeSubentidades.getSubentidadMediadora().getId().getCodsubentidad() + "','" +
			geeSubentidades.getPctmediador() + "','" +
			geeSubentidades.getEntidad().getNomentidad() + "','" + 
			geeSubentidades.getSubentidadMediadora().getNomSubentidadCompleto();
			acciones += "')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>";
			
			
			acciones += "<a href=\"javascript:deleteGeeSubentidad('";
			acciones += geeSubentidades.getId();
			acciones += "')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar\" title=\"Eliminar\"/></a>";
			
		
		return acciones;
	}

	public String getEntidad(){
		GGESubentidades geeSubentidades = (GGESubentidades) getCurrentRowObject();

		if(geeSubentidades.getEntidad()!= null && geeSubentidades.getEntidad().getCodentidad() != null)
		    return geeSubentidades.getEntidad().getCodentidad().toString();	
		else 
		    return "";
	}
	public String getSubentidad(){
		GGESubentidades geeSubentidades = (GGESubentidades) getCurrentRowObject();

		if(geeSubentidades.getSubentidadMediadora()!=null &&  geeSubentidades.getSubentidadMediadora().getId()!= null)
		    return geeSubentidades.getSubentidadMediadora().getId().getCodsubentidad().toString();
		else 
			return "";
	}
	
	public String getPlan(){
		GGESubentidades geeSubentidades = (GGESubentidades) getCurrentRowObject();

		return geeSubentidades.getPlan().toString();
	}
	public String getPorcentajeentidad(){
		GGESubentidades geeSubentidades = (GGESubentidades) getCurrentRowObject();

		if(geeSubentidades.getPctentidad() != null)
		    return geeSubentidades.getPctentidad().toString() + "%";
		else 
			return "";
	}
	public String getPorcentajemediador(){
		GGESubentidades geeSubentidades = (GGESubentidades) getCurrentRowObject();	
		
		if(geeSubentidades.getPctmediador() != null)
		    return geeSubentidades.getPctmediador().toString() + "%";
		else
			return  "";
	}
	
	public Date getFechamodificacion(){
		GGESubentidades geeSubentidades = (GGESubentidades) getCurrentRowObject();
	
		return geeSubentidades.getFechamodificacion();
	}	
}