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
import com.rsi.agp.dao.models.comisiones.ICultivosEntidadesHistoricoDao;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;

public class CultivosEntidadesHistoricoManager implements IManager {
private static final Log logger = LogFactory.getLog(CultivosEntidadesHistoricoManager.class);
	
	private ICultivosEntidadesHistoricoDao cultivosEntidadesHistoricoDao;

	/**
	 * Metodo que guarda en la BBDD un registro de historico por cada operacion que se realice sobre los parametros de comisiones por cultivo
	 * @param cultivosEntidadesBean objeto sobre el que se genera el historico
	 * @throws BusinessException
	 */
	public void addResgitroHist(CultivosEntidades cultivosEntidadesBean,String accion) throws BusinessException {
		logger.debug("init - addResgitroHist");
		CultivosEntidadesHistorico cultivosEntidadesHist = null;
		try {
			
			logger.debug("init - Generamos el objeto cultivo entidades historico");
			cultivosEntidadesHist = this.generarHistorico(cultivosEntidadesBean,accion);
			logger.debug("end - Generamos el objeto cultivo entidades historico");
			
			cultivosEntidadesHistoricoDao.saveOrUpdate(cultivosEntidadesHist);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardarel historico sobre el parametro de comisiones por cultivo: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardarel historico sobre el parametro de comisiones por cultivo",dao);
		}
		logger.debug("end - addResgitroHist");
	}
	
	public void addResgitroHistReplicar(List<CultivosEntidades> listCultivosEntidades) throws BusinessException{
		logger.debug("init - addResgitroHistReplicar");
		try {
			
			for(CultivosEntidades cs:listCultivosEntidades){
				this.addResgitroHist(cs, ComisionesConstantes.AccionesHistComisionCte.ALTA);
			}
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al guardar el historico de la distribucion de mediadores replicado: " + be.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar el historico de la distribucion de mediadores replicado",be);
		}
		logger.debug("init - addResgitroHistReplicar");
	}
	
	/**
	 * Metodo que generar el objeto historico del cultivo entidad
	 * @param cultivosEntidadesBean objeto sobre el que se genera el historico
	 * @return
	 */
	private CultivosEntidadesHistorico generarHistorico(CultivosEntidades cultivosEntidadesBean,String accion) {
		logger.debug("init - generarHistorico");
		CultivosEntidadesHistorico cultivosEntidadesHist = new CultivosEntidadesHistorico();
		
		cultivosEntidadesHist.setCultivosEntidades(new BigDecimal(cultivosEntidadesBean.getId()));
		cultivosEntidadesHist.setFechamodificacion(new Date());
		cultivosEntidadesHist.setLinea(cultivosEntidadesBean.getLinea());
		cultivosEntidadesHist.setPctgeneralentidad(cultivosEntidadesBean.getPctgeneralentidad());
		cultivosEntidadesHist.setPctadministracion(cultivosEntidadesBean.getPctadministracion());
		cultivosEntidadesHist.setPctadquisicion(cultivosEntidadesBean.getPctadquisicion());
		/* ESC-17100 ** MODIF TAM (09/02/2022) ** Inicio */
		/* Al realizar una replica del plan/linea, los registros del histórico de los replicados 
		 * deberían tener la fecha de cuando hacemos la replica */
		/*cultivosEntidadesHist.setFechaEfecto(cultivosEntidadesBean.getFechaEfecto());*/
		cultivosEntidadesHist.setFechaEfecto(new Date());
		/* ESC-17100 ** MODIF TAM (09/02/2022) ** Fin */
		
		cultivosEntidadesHist.setUsuario(cultivosEntidadesBean.getUsuario());
		cultivosEntidadesHist.setGrupoNegocio(cultivosEntidadesBean.getGrupoNegocio());
		cultivosEntidadesHist.setSubentidadMediadora(cultivosEntidadesBean.getSubentidadMediadora());
		cultivosEntidadesHist.setAccion(accion);
		
		logger.debug("end - generarHistorico");
		return cultivosEntidadesHist;
	}
	
	public ICultivosEntidadesHistoricoDao getCultivosEntidadesHistoricoDao() {
		return cultivosEntidadesHistoricoDao;
	}

	public void setCultivosEntidadesHistoricoDao(ICultivosEntidadesHistoricoDao cultivosEntidadesHistoricoDao) {
		this.cultivosEntidadesHistoricoDao = cultivosEntidadesHistoricoDao;
	}

}
