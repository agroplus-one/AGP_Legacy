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
import com.rsi.agp.dao.models.comisiones.IGGESubEntidadesHistoricoDao;
import com.rsi.agp.dao.tables.comisiones.GGESubentidades;
import com.rsi.agp.dao.tables.comisiones.GGESubentidadesHistorico;

public class GGESubEntidadesHistoricoManager implements IManager {
private static final Log logger = LogFactory.getLog(GGESubEntidadesHistoricoManager.class);
	
	private IGGESubEntidadesHistoricoDao GGESubEntidadesHistoricoDao;
	
	/**
	 * Metodo que genera una referencia en el historico por cada elemento de mi replicacion
	 * @param listsubentidadesBean
	 * @throws BusinessException
	 */
	public void addResgitroHistReplicar(List<GGESubentidades> listsubentidadesBean) throws BusinessException {
		logger.debug("init - addResgitroHistReplicar");
		try {
			
			for(GGESubentidades gge:listsubentidadesBean){
				this.addResgitroHist(gge, ComisionesConstantes.AccionesHistComisionCte.ALTA);
			}
			
		} catch (BusinessException dao) {
			logger.error("Se ha producido un error al guardar el historico de la distribucion de gge de la replicacion: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar el historico de la distribucion de gge de la replicacion",dao);
		}
		logger.debug("end - addResgitroHistReplicar");
	}
	
	/**
	 * Metodo que guarda en la BBDD un registro de historico por cada operacion que se realice sobre las ggesubentidades
	 * @param gee
	 * @param accion
	 * @throws BusinessException
	 */
	public void addResgitroHist(GGESubentidades gee,String accion) throws BusinessException{
		logger.debug("init - addResgitroHist");
		GGESubentidadesHistorico gGESubentidadesHistorico = null;
		try {
			gGESubentidadesHistorico = this.generarHistorico(gee,accion);
			
			GGESubEntidadesHistoricoDao.saveOrUpdate(gGESubentidadesHistorico);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar el historico de la distribucion de gge: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar el historico de la distribucion de gge",dao);
		}
		logger.debug("end - addResgitroHist");
	}
	/**
	 * Metodo que generar el objeto historico de ggesubentidades
	 * @param gee
	 * @param accion
	 * @return
	 */
	public GGESubentidadesHistorico generarHistorico(GGESubentidades gee,String accion){
		logger.debug("init - generarHistorico");
		GGESubentidadesHistorico historico = new GGESubentidadesHistorico();
		
		historico.setCodusuario(gee.getUsuario().getCodusuario());
        historico.setEntidad(gee.getEntidad());
        historico.setFechamodificacion(new Date());
        historico.setGGESubentidades(new BigDecimal(gee.getId())); 
        historico.setPctentidad(gee.getPctentidad());
        historico.setPctmediador(gee.getPctmediador());
        historico.setPlan(gee.getPlan());
        historico.setSubentidadMediadora(gee.getSubentidadMediadora());
        historico.setAccion(accion);

        logger.debug("end - generarHistorico");
		return historico;
	}

	public IGGESubEntidadesHistoricoDao getGGESubEntidadesHistoricoDao() {
		return GGESubEntidadesHistoricoDao;
	}

	public void setGGESubEntidadesHistoricoDao(	IGGESubEntidadesHistoricoDao subEntidadesHistoricoDao) {
		GGESubEntidadesHistoricoDao = subEntidadesHistoricoDao;
	}

	
}
