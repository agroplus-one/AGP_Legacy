package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.cesp.GrupoAseguradoCe;

public class ModelTableDecoratorGrupoAsegurado extends TableDecorator {

	public String getColumnaAcciones(){
		String acciones = "";
		GrupoAseguradoCe ls = (GrupoAseguradoCe)getCurrentRowObject();
		
		acciones += "<a href=\"#\" onclick=\"javascript:editar('"+ls.getCodgrupoaseg()+"','"+ls.getBonifrecprimas()+"','"+ls.getBonifrecrdtomax()+"')\"><img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>";
		acciones += "<a href=\"#\" onclick=\"javascript:eliminar('"+ls.getCodgrupoaseg()+"')\"><img src='jsp/img/displaytag/delete.png' alt='Eliminar' title='Eliminar'/></a>";

		return acciones;
	}
	
	
	public String getColumnaGrupo(){
		GrupoAseguradoCe ls = (GrupoAseguradoCe)getCurrentRowObject();
		return ls.getCodgrupoaseg();
	}

	public String getColumnaPrimas(){
		String valor = "";
		GrupoAseguradoCe ls = (GrupoAseguradoCe)getCurrentRowObject();	//si no entra en el if lo retorna vacio, pero si entra lo retorna con valor.
		
		if(ls.getBonifrecprimas()!=null){
			valor= ls.getBonifrecprimas().toString()+" %";
		}
		return valor;
	}
	
	
	public String getColumnaRendimiento(){
		String valor = "";
		GrupoAseguradoCe ls = (GrupoAseguradoCe)getCurrentRowObject();
		
		if(ls.getBonifrecrdtomax()!=null){
			valor= ls.getBonifrecrdtomax().toString()+" %";
		}
		return valor;
	}
}
