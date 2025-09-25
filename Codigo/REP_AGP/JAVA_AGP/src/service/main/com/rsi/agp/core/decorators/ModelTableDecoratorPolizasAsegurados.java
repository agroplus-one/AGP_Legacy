package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.poliza.Poliza;

public class ModelTableDecoratorPolizasAsegurados extends TableDecorator {
	
	
	
	public String getNifAseg (){
		Poliza p = (Poliza)getCurrentRowObject();
		return p.getAsegurado().getNifcif();
	}
	
	public String getColectivo (){
		Poliza p = (Poliza)getCurrentRowObject();
		return p.getColectivo().getIdcolectivo();
	}
	
	public String getLinea (){
		Poliza p = (Poliza)getCurrentRowObject();
		return p.getLinea().getCodlinea().toString();
	}
	
	public String getPlan (){
		Poliza p = (Poliza)getCurrentRowObject();
		return p.getLinea().getCodplan().toString();
	}

}
