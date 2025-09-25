package com.rsi.agp.core.managers.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.config.CampoMascaraFiltro;
import com.rsi.agp.dao.models.config.ICampoMascaraDao;
import com.rsi.agp.dao.tables.masc.CampoMascara;

public class CampoMascaraManager implements IManager {

	private static final Log LOGGER = LogFactory.getLog(CampoMascaraManager.class);
	protected ICampoMascaraDao campoMascaraDao;
	
	public final List<CampoMascara> listCamposMascara(CampoMascara campoMascara) {
		
		CampoMascaraFiltro campoMascaraFiltro = new CampoMascaraFiltro();
		campoMascaraFiltro.setCampoMascara(campoMascara);
		
		return campoMascaraDao.getObjects(campoMascaraFiltro);
	}
	
	public final void saveCampoMascara(CampoMascara campoMascara) {
		try {
			campoMascaraDao.saveOrUpdate(campoMascara);
			campoMascaraDao.evict(campoMascara);
		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error al guardar el campo mascara",e);
		}
	}
	
	public final CampoMascara getCampoMascara(Long idCampoMascara) {
		
		return (CampoMascara)campoMascaraDao.getObject(CampoMascara.class, idCampoMascara);
	}
	
	public final void deleteCampoMascara(CampoMascara campoMascara) {
		
		campoMascaraDao.removeObject(CampoMascara.class, campoMascara.getId());
	}
	
	public final boolean existeCampoMascara(CampoMascara campoMascara) {
		
		return this.campoMascaraDao.existeCampoMascara(campoMascara);

	}
	
	public void setCampoMascaraDao(ICampoMascaraDao campoMascaraDao) {
		this.campoMascaraDao = campoMascaraDao;
	}
	
	
	

}
