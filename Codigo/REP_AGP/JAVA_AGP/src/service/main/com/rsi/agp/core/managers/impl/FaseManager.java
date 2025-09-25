package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.IFaseDao;

public class FaseManager implements IManager {

	private static final Log LOGGER = LogFactory.getLog(FaseManager.class);
			
	private IFaseDao faseDao;

	/**
	 * Devuelve el plan al que pertenece una fase
	 * @param idFichero Id del fichero
	 * @param fase Número de fase
	 * @return
	 * @throws DAOException 
	 */
	public BigDecimal obtenerPlanByIdFichero(Long idFichero) throws DAOException{
		
		BigDecimal plan = faseDao.obtenerPlanByIdFichero(idFichero);
		LOGGER.debug("El plan asociado al idFichero " + idFichero + " es " + plan);
		return plan;
	}
	
	public void setFaseDao(IFaseDao faseDao) {
		this.faseDao = faseDao;
	}
}