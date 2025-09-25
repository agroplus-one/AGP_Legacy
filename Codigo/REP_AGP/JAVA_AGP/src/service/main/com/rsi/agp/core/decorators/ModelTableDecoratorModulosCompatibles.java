package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.cesp.ModuloCompatibleCe;
import com.rsi.agp.dao.tables.masc.CampoMascara;

public class ModelTableDecoratorModulosCompatibles extends TableDecorator{

	public String getColumnasAcciones(){
		ModuloCompatibleCe moduloCompatible = (ModuloCompatibleCe)getCurrentRowObject();
		String acciones = "";
		
		acciones += "<a href=\"#\" onclick=\"javascript:editar("+moduloCompatible.getId()+")\"><img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>";
		acciones += "<a href=\"#\" onclick=\"javascript:eliminar("+moduloCompatible.getId()+")\"><img src='jsp/img/displaytag/delete.png' alt='Eliminar' title='Eliminar'/></a>";
		
		return acciones;
	}
	
	public String getColumnaPlan(){
		ModuloCompatibleCe moduloCompatible = (ModuloCompatibleCe)getCurrentRowObject();
		return moduloCompatible.getLinea().getCodplan().toString();
	}
	public String getColumnaLinea(){
		ModuloCompatibleCe moduloCompatible = (ModuloCompatibleCe)getCurrentRowObject();
		return moduloCompatible.getLinea().getCodlinea()+" - "+moduloCompatible.getLinea().getNomlinea();
	}
	public String getColumnaModP(){
		ModuloCompatibleCe moduloCompatible = (ModuloCompatibleCe)getCurrentRowObject();
		return  moduloCompatible.getModuloPrincipal().getId().getCodmodulo()+" - "+moduloCompatible.getModuloPrincipal().getDesmodulo();
	}
	public String getColumnaRiesgo(){
		ModuloCompatibleCe moduloCompatible = (ModuloCompatibleCe)getCurrentRowObject();
		return moduloCompatible.getRiesgoCubierto().getDesriesgocubierto();
	}
	public String getColumnaModC(){
		ModuloCompatibleCe moduloCompatible = (ModuloCompatibleCe)getCurrentRowObject();
		return moduloCompatible.getModuloComplementario().getId().getCodmodulo()+" - "+moduloCompatible.getModuloComplementario().getDesmodulo();
	}
}

