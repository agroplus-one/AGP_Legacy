package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.util.List;

import com.rsi.agp.core.jmesa.service.mtoinf.IMtoVistasService;
import com.rsi.agp.dao.tables.mtoinf.RelVistaCampos;
import com.rsi.agp.dao.tables.mtoinf.Vista;

public class MtoVistasService implements IMtoVistasService{
	
	// Carga el objeto singleton que devuelve el listado de vistas
	private MtoVistasSingleton mtoVistasSingleton;

	@Override
	public List<Vista> getListadoVistas() {
		return mtoVistasSingleton.getListadoVistas();
	}

	public void setMtoVistasSingleton(MtoVistasSingleton mtoVistasSingleton) {
		this.mtoVistasSingleton = mtoVistasSingleton;
	}

	public MtoVistasSingleton getMtoVistasSingleton() {
		return mtoVistasSingleton;
	}

	@Override
	public List<RelVistaCampos> getRelVistaCampos() {
		return mtoVistasSingleton.getRelVistaCampos();
	}
}
