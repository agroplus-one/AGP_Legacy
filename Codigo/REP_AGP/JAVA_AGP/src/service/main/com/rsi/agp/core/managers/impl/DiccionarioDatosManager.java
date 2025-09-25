package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.config.DiccionarioDatosFiltro;
import com.rsi.agp.dao.filters.config.DiccionarioDatosTipoCampoLimiteFiltro;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.tables.cpl.MascaraGrupoTasas;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;

public class DiccionarioDatosManager implements IManager {

	private static final Log LOGGER = LogFactory.getLog(DiccionarioDatosManager.class);
	protected IDiccionarioDatosDao diccionarioDatosDao;

	@SuppressWarnings("unchecked")
	public final List<DiccionarioDatos> listAll() {
		DiccionarioDatosFiltro diccionarioDatosFiltro = new DiccionarioDatosFiltro();
		
		return diccionarioDatosDao.getObjects(diccionarioDatosFiltro);
	}
	
	@SuppressWarnings("unchecked")
	public final List<DiccionarioDatos> listByTipoCampoLimite(BigDecimal idTipoCampoLimite) {
		
		DiccionarioDatosTipoCampoLimiteFiltro diccionarioDatosTipoCampoLimiteFiltro = new DiccionarioDatosTipoCampoLimiteFiltro();
		diccionarioDatosTipoCampoLimiteFiltro.setIdTipoCampoLimite(idTipoCampoLimite);
		
		return diccionarioDatosDao.getObjects(diccionarioDatosTipoCampoLimiteFiltro);
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}
	

}
