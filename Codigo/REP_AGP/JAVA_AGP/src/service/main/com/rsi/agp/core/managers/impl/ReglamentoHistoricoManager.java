package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.ComisionesConstantes;
import com.rsi.agp.dao.models.comisiones.IReglamentoHistoricoDao;
import com.rsi.agp.dao.tables.comisiones.Reglamento;
import com.rsi.agp.dao.tables.comisiones.ReglamentoHist;

public class ReglamentoHistoricoManager implements IManager {
	private static final Log logger = LogFactory.getLog(ReglamentoHistoricoManager.class);
	private IReglamentoHistoricoDao reglamentoHistoricoDao;
	
	/**
	 * Metodo que genera una referencia en el historico por cada elemento de mi replicacion
	 * @param listgeeSubentidadesBean
	 * @throws BusinessException 
	 */
	public void addResgitroHistReplicar(List<Reglamento> listreglamento) throws BusinessException {
		logger.debug("init - addResgitroHistReplicar");
		try {
			
			for(Reglamento reg:listreglamento){
				this.addResgitroHist(reg, ComisionesConstantes.AccionesHistComisionCte.ALTA);
			}
			
		} catch (BusinessException dao) {
			logger.error("Se ha producido un error al guardar el historico de la distribucion de gge de la replicacion: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar el historico de la distribucion de gge de la replicacion",dao);
		}
		logger.debug("end - addResgitroHistReplicar");
	}
	
	/**
	 * Metodo que guarda en la BBDD un registro de historico por cada operacion que se realice sobre los reglamentos
	 * @param reglamentoBean
	 * @param accion
	 * @throws BusinessException 
	 */
	public void addResgitroHist(Reglamento reglamentoBean, String accion) throws BusinessException {
		logger.debug("init - addResgitroHist");
		ReglamentoHist reglamentoHist = null;
		try {
			reglamentoHist = this.generarHistorico(reglamentoBean,accion);
			
			reglamentoHistoricoDao.saveOrUpdate(reglamentoHist);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar el historico de reglamentos: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar el historico de reglamentos",dao);
		}
		logger.debug("end - addResgitroHist");
	}
	
	/**
	 * Metodo que generar el objeto historico de reglamento
	 * @param reglamentoBean
	 * @param accion
	 * @return
	 */
	private ReglamentoHist generarHistorico(Reglamento reglamentoBean,String accion) {
		logger.debug("init - generarHistorico");
		ReglamentoHist historico = new ReglamentoHist();
		
		historico.setAccion(accion);
		historico.setEntidad(reglamentoBean.getEntidad());
		historico.setPctentidad(reglamentoBean.getPctentidad());
		historico.setPctrga(reglamentoBean.getPctrga());
		historico.setPlan(reglamentoBean.getPlan());
		historico.setReglamento(new BigDecimal(reglamentoBean.getId()));
		historico.setUsuario(reglamentoBean.getUsuario());
		historico.setFechamodificacion(new Date());
		
		logger.debug("init - generarHistorico");
		return historico;
	}

	public IReglamentoHistoricoDao getReglamentoHistoricoDao() {
		return reglamentoHistoricoDao;
	}

	public void setReglamentoHistoricoDao(IReglamentoHistoricoDao reglamentoHistoricoDao) {
		this.reglamentoHistoricoDao = reglamentoHistoricoDao;
	}

}
