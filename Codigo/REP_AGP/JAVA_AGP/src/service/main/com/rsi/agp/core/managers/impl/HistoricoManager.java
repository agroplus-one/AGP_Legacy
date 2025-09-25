package com.rsi.agp.core.managers.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.log.IHistoricoCambioIBANDao;
import com.rsi.agp.dao.models.log.IHistoricoCambioTitularDao;
import com.rsi.agp.dao.tables.log.HistoricoCambioIBAN;
import com.rsi.agp.dao.tables.log.HistoricoCambioTitular;

public class HistoricoManager implements IManager {
	
	private IHistoricoCambioIBANDao historicoCambioIBANDao;
	private IHistoricoCambioTitularDao historicoCambioTitularDao;
	
	private static final Log LOGGER = LogFactory.getLog(HistoricoManager.class);
	
	/**
	 * Registra en el histórico un cambio de IBAN
	 * @param idPoliza
	 * @param usuario
	 * @param ibanAnterior
	 * @param ibanNuevo
	 * @return
	 */
	public boolean grabarHistoricoCambioIBAN(Long idPoliza, String usuario, String ibanAnterior, String ibanNuevo){
		LOGGER.debug("INICIO - grabarHistoricoCambioIBAN");
		
		boolean isOperacionRealizada = false;
		HistoricoCambioIBAN registro = new HistoricoCambioIBAN();
		registro.setFecha(new Date());
		registro.setUsuario(usuario);
		registro.setIdpoliza(idPoliza);
		registro.setIbanAnterior(ibanAnterior);
		registro.setIbanNuevo(ibanNuevo);
		
		try {
			historicoCambioIBANDao.saveOrUpdate(registro);
			isOperacionRealizada = true;
		} catch (DAOException e) {
			LOGGER.error("Error al intentar registrar en el histórico el cambio de IBAN");
		}
		
		LOGGER.debug("FIN - grabarHistoricoCambioIBAN");
		return isOperacionRealizada;
	}

	/**
	 * Registra en el histórico un cambio de titular
	 * @param idPoliza
	 * @param usuario
	 * @param titularAnterior
	 * @param titularNuevo
	 * @return
	 */
	public boolean grabarHistoricoCambioTitular(Long idPoliza, String usuario, String titularAnterior, String titularNuevo){
		LOGGER.debug("INICIO - grabarHistoricoCambioTitular");
		
		boolean isOperacionRealizada = false;
		HistoricoCambioTitular registro = new HistoricoCambioTitular();
		
		registro.setFecha(new Date());
		registro.setUsuario(usuario);
		registro.setIdpoliza(idPoliza);
		registro.setTitularAnterior(titularAnterior);
		registro.setTitularNuevo(titularNuevo);
		
		try {
			historicoCambioTitularDao.saveOrUpdate(registro);
			isOperacionRealizada = true;
		} catch (DAOException e) {
			LOGGER.error("Error al intentar registrar en el histórico el cambio de titular");
		}
		LOGGER.debug("FIN - grabarHistoricoCambioTitular");
		return isOperacionRealizada;
	}	
	
	
	// GETTERS AND SETTERS
	public IHistoricoCambioIBANDao getHistoricoCambioIBANDao() {
		return historicoCambioIBANDao;
	}
	public void setHistoricoCambioIBANDao(
			IHistoricoCambioIBANDao historicoCambioIBANDao) {
		this.historicoCambioIBANDao = historicoCambioIBANDao;
	}
	public IHistoricoCambioTitularDao getHistoricoCambioTitularDao() {
		return historicoCambioTitularDao;
	}
	public void setHistoricoCambioTitularDao(
			IHistoricoCambioTitularDao historicoCambioTitularDao) {
		this.historicoCambioTitularDao = historicoCambioTitularDao;
	}
}