package com.rsi.agp.core.managers;

import java.util.HashMap;

import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;

public interface ICuadroCoberturasGanadoManager {
	
	public JSONObject getCoberturasJSON (ModuloView modulo, String idtabla) throws JSONException;
	
	public ModuloView getCoberturasModulo(String codmodulo, Poliza poliza, boolean informes);
	public ModuloView getCoberturasModulo(String codmodulo, Long lineaseguroid, boolean informes);
	public boolean hayComparativasElegibles (String[] codModulos, Linea linea);
	public HashMap<String, Object> crearComparativas(Poliza poliza, String[] modulos);
}
