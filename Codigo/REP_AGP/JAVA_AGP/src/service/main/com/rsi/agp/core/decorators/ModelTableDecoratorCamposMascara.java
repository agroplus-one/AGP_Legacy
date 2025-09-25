package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.dao.tables.masc.CampoMascara;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class ModelTableDecoratorCamposMascara extends TableDecorator {

	public String getAcciones() {
		String acciones = "";
		CampoMascara campoMascara = (CampoMascara)getCurrentRowObject();
		
		acciones += "<a href=\"#\" onclick=\"javascript:editar("+campoMascara.getId()+")\"><img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>";
		acciones += "<a href=\"#\" onclick=\"javascript:eliminar("+campoMascara.getId()+")\"><img src='jsp/img/displaytag/delete.png' alt='Eliminar' title='Eliminar'/></a>";
		
		return acciones;
    }
	public String getDescMascara() {
		CampoMascara campoMascara = (CampoMascara)getCurrentRowObject();
		return campoMascara.getTablaCondicionado().getCodtablacondicionado()+" - "+campoMascara.getTablaCondicionado().getDestablacondicionado();
	}
	
	public String getCampo() {
		CampoMascara campoMascara = (CampoMascara)getCurrentRowObject();
		return campoMascara.getDiccionarioDatosByCodconceptomasc().getCodconcepto()+" - "+campoMascara.getDiccionarioDatosByCodconceptomasc().getNomconcepto();
	}
	
	public String getCampoE() {
		CampoMascara campoMascara = (CampoMascara)getCurrentRowObject();
		return campoMascara.getDiccionarioDatosByCodconceptoasoc().getCodconcepto()+" - "+campoMascara.getDiccionarioDatosByCodconceptoasoc().getNomconcepto();
	}
}
