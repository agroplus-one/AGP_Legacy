package com.rsi.agp.core.managers.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.ComisionesConstantes;
import com.rsi.agp.dao.models.comisiones.ICultivosSubEntidadesHistoricoDao;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidadesHistorico;
import com.rsi.agp.dao.tables.commons.Usuario;

public class CultivosSubEntidadesHistoricoManager implements IManager {
	private static final Log logger = LogFactory.getLog(CultivosSubEntidadesHistoricoManager.class);
	private ICultivosSubEntidadesHistoricoDao cultivosSubEntidadesHistoricoDao;
	
	/**
	 * Metodo que guarda en la BBDD eol historico de la replicacion
	 * @param listCultivosSubentidades
	 * @throws BusinessException
	 */
	public void addResgitroHistReplicar(List<CultivosSubentidades> listCultivosSubentidades,Usuario usuario) throws BusinessException {
		logger.debug("init - addResgitroHistReplicar");
		try {
			
			for(CultivosSubentidades cs:listCultivosSubentidades){
				this.addResgitroHist(cs, ComisionesConstantes.AccionesHistComisionCte.ALTA,usuario);
			}
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al guardar el historico de la distribucion de mediadores replicado: " + be.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar el historico de la distribucion de mediadores replicado",be);
		}
		logger.debug("init - addResgitroHistReplicar");
	}
	
	/**
	 * Metodo que guarda en la BBDD un registro de historico por cada operacion que se realice sobre los parametros de comisiones por cultivo
	 * @param cultivosSubentidadesBean
	 * @throws BusinessException
	 */ 
	public void addResgitroHist(CultivosSubentidades cultivosSubentidadesBean,String accion,Usuario usuario) throws BusinessException {
		logger.debug("init - addResgitroHist");
		CultivosSubentidadesHistorico cultivosSubentidadesHistorico = null;
		try {
			cultivosSubentidadesHistorico = this.generarHistorico(cultivosSubentidadesBean,accion,usuario);
			
			cultivosSubEntidadesHistoricoDao.saveOrUpdate(cultivosSubentidadesHistorico);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar el historico de la distribucion de mediadores: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar el historico de la distribucion de mediadores",dao);
		}
		logger.debug("end - addResgitroHist");
	}

	/**
	 * Metodo que generar el objeto historico del cultivo entidad
	 * @param cultivosSubentidadesBean
	 * @return
	 */
	private CultivosSubentidadesHistorico generarHistorico(CultivosSubentidades cultivosSubentidadesBean,String accion,Usuario usuario) {
		logger.debug("init - generarHistorico");	
		CultivosSubentidadesHistorico cultivosSubentidadesHistorico = new CultivosSubentidadesHistorico();
			
		cultivosSubentidadesHistorico.setFechamodificacion(new Date());
		cultivosSubentidadesHistorico.setUsuario(usuario);
		cultivosSubentidadesHistorico.setCultivosSubentidades(cultivosSubentidadesBean); 
		cultivosSubentidadesHistorico.setCodentidadSM(cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodentidad());
		cultivosSubentidadesHistorico.setLinea(cultivosSubentidadesBean.getLinea());
		cultivosSubentidadesHistorico.setPctentidad(cultivosSubentidadesBean.getPctentidad());
		cultivosSubentidadesHistorico.setPctmediador(cultivosSubentidadesBean.getPctmediador());
		cultivosSubentidadesHistorico.setCodsubentidadSM(cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad());
		cultivosSubentidadesHistorico.setAccion(accion);
		/* ESC-17100 ** MODIF TAM (09/02/2022) ** Inicio */
		/* Al realizar una replica del plan/linea, los registros del histórico de los replicados 
		 * deberían tener la fecha de cuando hacemos la replica */
		/*cultivosSubentidadesHistorico.setFecEfecto(cultivosSubentidadesBean.getFecEfecto());*/
		cultivosSubentidadesHistorico.setFecEfecto(new Date());
		/* ESC-17100 ** MODIF TAM (09/02/2022) ** Fin */
		cultivosSubentidadesHistorico.setCultivosSubentidades(cultivosSubentidadesBean);
		logger.debug("end - generarHistorico");
		return cultivosSubentidadesHistorico;
	}

	public ICultivosSubEntidadesHistoricoDao getCultivosSubEntidadesHistoricoDao() {
		return cultivosSubEntidadesHistoricoDao;
	}

	public void setCultivosSubEntidadesHistoricoDao(ICultivosSubEntidadesHistoricoDao cultivosSubEntidadesHistoricoDao) {
		this.cultivosSubEntidadesHistoricoDao = cultivosSubEntidadesHistoricoDao;
	}

	
}
