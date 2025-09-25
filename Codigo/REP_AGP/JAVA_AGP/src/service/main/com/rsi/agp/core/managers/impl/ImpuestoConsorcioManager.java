package com.rsi.agp.core.managers.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.IImpuestoConsorcioDao;

public class ImpuestoConsorcioManager implements IManager {
private static final Log logger = LogFactory.getLog(ImpuestoConsorcioManager.class);
	
	private IImpuestoConsorcioDao impuestoConsorcioDao;

	public IImpuestoConsorcioDao getImpuestoConsorcioDao() {
		return impuestoConsorcioDao;
	}

	public void setImpuestoConsorcioDao(IImpuestoConsorcioDao impuestoConsorcioDao) {
		this.impuestoConsorcioDao = impuestoConsorcioDao;
	}
}
