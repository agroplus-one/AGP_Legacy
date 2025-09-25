package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.cesp.PeriodoGarantiaCe;
import com.rsi.agp.dao.tables.masc.CampoMascara;

public class ModelTableDecoratorPeriodoGarantiaCe extends TableDecorator {

	public String getAcciones() {
		String acciones = "";
		PeriodoGarantiaCe periodoGarantiaCe = (PeriodoGarantiaCe)getCurrentRowObject();
		
		acciones += "<a href=\"#\" onclick=\"javascript:editar("+periodoGarantiaCe.getId()+")\"><img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>";
		acciones += "<a href=\"#\" onclick=\"javascript:eliminar("+periodoGarantiaCe.getId()+")\"><img src='jsp/img/displaytag/delete.png' alt='Eliminar' title='Eliminar'/></a>";
		
		return acciones;
    }
	public String getColumnaLinea() {
		PeriodoGarantiaCe periodoGarantiaCe = (PeriodoGarantiaCe)getCurrentRowObject();
		return periodoGarantiaCe.getLinea().getCodlinea()+" - "+periodoGarantiaCe.getLinea().getNomlinea();
	}
	
	public String getColumnaCultivo() {
		PeriodoGarantiaCe periodoGarantiaCe = (PeriodoGarantiaCe)getCurrentRowObject();
		return periodoGarantiaCe.getCultivo().getDescultivo();
	}

}
