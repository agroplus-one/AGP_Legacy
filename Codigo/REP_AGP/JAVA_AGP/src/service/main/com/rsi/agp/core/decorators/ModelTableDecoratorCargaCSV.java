package com.rsi.agp.core.decorators;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.cvs.CvsCarga;

public class ModelTableDecoratorCargaCSV extends TableDecorator {

	public String getAcciones() {
		String acciones = "";
		CvsCarga cargaCsv = (CvsCarga)getCurrentRowObject();
		
		acciones += "<input type=\"checkbox\" id=\"checkCarga_" + cargaCsv.getId() + "\"  name=\"checkCarga_" + cargaCsv.getId() 
					+ "\" onClick=\"onClickInCheck(this, " + cargaCsv.getId() + ");\" value=\""+cargaCsv.getId().toString()+ "\"/>";
		
		acciones += "<a href=\"#\" onclick=\"javascript:verContenido("+cargaCsv.getId()+ ",'" + cargaCsv.getNombreFichero() + "');\">" +
				"<img src=\"jsp/img/displaytag/download.png\" alt=\"Descargar Archivo\" title=\"Descargar Archivo\"/></a>";
		
		acciones += "&nbsp;<a href=\"#\" onclick=\"eliminar("+cargaCsv.getId()+")\">" +
				"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar Archivo\" title=\"Eliminar Archivo\"/></a>";
		
		return acciones;
	}
	
	/**
	 * Devuelve la entidad mediadora y la subentidad mediadora separados por '-'
	 * del registro correspondiente de carga de CSV
	 * @return
	 */
	public String getEntSubMediadora() {
		CvsCarga cargaCsv = (CvsCarga)getCurrentRowObject();
		return cargaCsv.getEntMed().toString() + "-" + cargaCsv.getSubentMed().toString();
	}
}
