package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.util.List;

import com.rsi.agp.dao.models.mtoinf.IMtoVistasDao;
import com.rsi.agp.dao.tables.mtoinf.RelVistaCampos;
import com.rsi.agp.dao.tables.mtoinf.Vista;

public class MtoVistasSingleton {
	
	private IMtoVistasDao mtoVistasDao;
	
	/**
	 * Devuelve el listado completo de vistas
	 * @return
	 */
	public List<Vista> getListadoVistas () {
		// Devuelve el listado de vistas
		return mtoVistasDao.getListadoVistas();
	}

	/**
	 * Devuelve el listado completo de RelVistaCampos
	 * @return
	 */
	public List<RelVistaCampos> getRelVistaCampos () {
		// Devuelve el listado de RelVistaCampos
		return mtoVistasDao.getRelVistaCampos();
	}

	public void setMtoVistasDao(IMtoVistasDao mtoVistasDao) {
		this.mtoVistasDao = mtoVistasDao;
	}
	
}
