package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.poliza.Linea;

public class LineaManager implements IManager {
	private static final Log logger = LogFactory.getLog(LineaManager.class);
	
	private ILineaDao lineaDao;

	public Linea obtenerLinea(BigDecimal codLinea, BigDecimal codPlan) throws BusinessException {
		
		logger.debug("init - ");
		Linea linea = null;
		
		try{
			linea = lineaDao.getLinea(codLinea, codPlan);
			
		}catch (DAOException dao){
			logger.error("Línea no encontrada: " + dao.getMessage());
			throw new BusinessException("Línea no encontrada.", dao);
		}
		logger.debug("end - ");
		return linea;
	}

	
	//Sets de los DAO
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
}