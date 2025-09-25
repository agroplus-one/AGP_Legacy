package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.pac.PacCargas;

public class ModelTableDecoratorCargaPAC extends TableDecorator {

	public String getAcciones() {
		String acciones = "";
		PacCargas cargaPac = (PacCargas)getCurrentRowObject();
		
		acciones += "<input type=\"checkbox\" id=\"checkCarga_" + cargaPac.getId() + "\"  name=\"checkCarga_" + cargaPac.getId() 
					+ "\" onClick=\"onClickInCheck(this, " + cargaPac.getId() + ");\" value=\""+cargaPac.getId().toString()+ "\"/>";
		
		acciones += "<a href=\"#\" onclick=\"javascript:verContenido("+cargaPac.getId()+ ",'" + cargaPac.getNombreFichero() + "');\">" +
				"<img src=\"jsp/img/displaytag/download.png\" alt=\"Descargar Archivo\" title=\"Descargar Archivo\"/></a>";
		
		acciones += "&nbsp;<a href=\"#\" onclick=\"eliminar("+cargaPac.getId()+")\">" +
				"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar Archivo\" title=\"Eliminar Archivo\"/></a>";
		
		return acciones;
	}
	
	/**
	 * Devuelve la entidad mediadora y la subentidad mediadora separados por '-'
	 * del registro correspondiente de carga de PAC
	 * @return
	 */
	public String getEntSubMediadora() {
		PacCargas cargaPac = (PacCargas)getCurrentRowObject();
		return cargaPac.getEntMed().toString() + "-" + cargaPac.getSubentMed().toString();
	}
}
