package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.cpl.ICaracteristicasModuloDao;
import com.rsi.agp.dao.models.cpl.IFechContratacionAgricDao;
import com.rsi.agp.dao.models.cpl.IModulosDao;
import com.rsi.agp.dao.models.cpl.ITablaBonusDao;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;

public class EleccionModulosManager implements IManager {

	protected IFechContratacionAgricDao fechContratacionAgricDao;
	protected IModulosDao modulosDao;
	protected ITablaBonusDao tablaBonusDao;
	protected ICaracteristicasModuloDao caracteristicasModuloDao;

	public HashMap<String, Object> consultaComparativas(String[] modulos) {
		// Consultar lass opciones de contratacion para los modulos seleccionados
		HashMap<String, Object> detalleCoberturas = new HashMap<String, Object>();
		List<String> codModuloFechas = new ArrayList<>();

		for (String mod : modulos) {
			codModuloFechas.add(mod);
		}

		return detalleCoberturas;
	}

	public HashMap<String, Object> trataSeleccion(String seleccionComp) {
		HashMap<String, Object> selectComp = new HashMap<String, Object>();
		String[] selecciones = null;
		if (seleccionComp != null && !seleccionComp.equalsIgnoreCase("")) {
			selecciones = seleccionComp.split(";");
			selectComp.put("seleccionados", selecciones);
		} else {
			selectComp.put("selecccionados", "No ha seleccionado nada");
		}
		return selectComp;
	}

	public void actualizaPrecioyProd(Parcela parcela, List<ComparativaPoliza> comparativa) throws Exception {
		// EMPTY METHOD
	}

	public void setFechContratacionAgricDao(IFechContratacionAgricDao fechContratacionAgricDao) {
		this.fechContratacionAgricDao = fechContratacionAgricDao;
	}

	public void setModulosDao(IModulosDao modulosDao) {
		this.modulosDao = modulosDao;
	}

	public void setTablaBonusDao(ITablaBonusDao tablaBonusDao) {
		this.tablaBonusDao = tablaBonusDao;
	}

	public void setCaracteristicasModuloDao(ICaracteristicasModuloDao caracteristicasModuloDao) {
		this.caracteristicasModuloDao = caracteristicasModuloDao;
	}
}