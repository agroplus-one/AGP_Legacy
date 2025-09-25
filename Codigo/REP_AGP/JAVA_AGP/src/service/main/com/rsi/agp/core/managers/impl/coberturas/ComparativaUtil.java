package com.rsi.agp.core.managers.impl.coberturas;

import com.rsi.agp.dao.tables.poliza.ComparativaFija;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;
import com.rsi.agp.dao.tables.poliza.VistaComparativasId;

public class ComparativaUtil {
	
	public static VistaComparativas generateVistaComparativas(ComparativaFija comparativaFija){
		VistaComparativas vista = new VistaComparativas();
		VistaComparativasId vistaId = new VistaComparativasId();
		
		vistaId.setCodconcepto(comparativaFija.getCodconcepto());
		vistaId.setCodconceptoppalmod(comparativaFija.getCodconceptoppalmod());
		vistaId.setCodmodulo(comparativaFija.getCodmodulo());
		vistaId.setCodriesgocubierto(comparativaFija.getCodriesgocubierto());
		vistaId.setCodvalor(comparativaFija.getCodvalor());
		vistaId.setColumnamodulo(comparativaFija.getColumnamodulo());
		vistaId.setColumnamodulovinc(comparativaFija.getColumnamodulovinc());
		vistaId.setDatovinculado(comparativaFija.getDatovinculado());
		vistaId.setDesconceptoppalmod(comparativaFija.getDesconceptoppalmod());
		vistaId.setDesmodulo(comparativaFija.getDesmodulo());
		vistaId.setDesriesgocubierto(comparativaFija.getDesriesgocubierto());
		vistaId.setDesvalor(comparativaFija.getDesvalor());
		vistaId.setElegible(comparativaFija.getElegible());
		vistaId.setFilamodulo(comparativaFija.getFilamodulo());
		vistaId.setFilamodulovinc(comparativaFija.getFilamodulovinc());
		vistaId.setLineaseguroid(comparativaFija.getLineaseguroid().longValue());
		vistaId.setNomconcepto(comparativaFija.getNomconcepto());
		
		vista.setId(vistaId);
		
		return vista;
	}

}
