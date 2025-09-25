package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.IGGEEntidadesHistoricoDao;
import com.rsi.agp.dao.tables.comisiones.GGEEntidades;
import com.rsi.agp.dao.tables.comisiones.GGEEntidadesHistorico;

public class GGEEntidadesHistoricoManager implements IManager {
	private static final Log logger = LogFactory.getLog(GGEEntidadesHistoricoManager.class);
	private IGGEEntidadesHistoricoDao GGEEntidadesHistoricoDao;

	public void addResgitroHist(GGEEntidades gge, String accion) throws BusinessException {
		logger.debug("init - addResgitroHist");
		GGEEntidadesHistorico ggeHist = null;
		try {
			ggeHist = this.generarHistorico(gge,accion);
			
			GGEEntidadesHistoricoDao.saveOrUpdate(ggeHist);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar el historico de la modificacion de porcentajes gge: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar el historico de la modificacion de porcentajes gge",dao);
		}
		logger.debug("end - addResgitroHist");
		
	}
	
	private GGEEntidadesHistorico generarHistorico(GGEEntidades gge,String accion) {
		GGEEntidadesHistorico historico = new GGEEntidadesHistorico();
		
		historico.setAccion(accion);
		historico.setFechamodificacion(new Date());
		historico.setGGEEntidades(new BigDecimal(gge.getId()));
		historico.setPctentidades(gge.getPctentidades());
		historico.setPctrag(gge.getPctrga());
		historico.setPctsectoragricola(gge.getPctsectoragricola());
		historico.setPlan(gge.getPlan());
		historico.setUsuario(gge.getUsuario());
		
		return historico;
	}

	public IGGEEntidadesHistoricoDao getGGESubEntidadesHistoricoDao() {
		return GGEEntidadesHistoricoDao;
	}

	public void setGGEEntidadesHistoricoDao(IGGEEntidadesHistoricoDao entidadesHistoricoDao) {
		GGEEntidadesHistoricoDao = entidadesHistoricoDao;
	}

	
}
