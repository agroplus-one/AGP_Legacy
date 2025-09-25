package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.IGGEEntidadesDao;
import com.rsi.agp.dao.tables.comisiones.GGEEntidades;
import com.rsi.agp.dao.tables.commons.Usuario;

public class GGEEntidadesManager implements IManager {
	private static final Log logger = LogFactory.getLog(GGEEntidadesManager.class);
	private IGGEEntidadesDao GGEEntidadesDao;
	
	/**
	 * 
	 * @param psa
	 * @param pe
	 * @param pRGA
	 * @param plan
	 * @param usuario
	 * @return
	 * @throws BusinessException
	 */
	public GGEEntidades Modificar(String psa, String pe, String pRGA, String plan, Usuario usuario,GGEEntidades ggEntidades) throws BusinessException{
		logger.debug("init - Modificar");
		try{ 
		
	         if(!psa.equals("") && !pe.equals("") && !pRGA.equals("") && !plan.equals("")){  
	        	 ggEntidades.setPctsectoragricola(new BigDecimal(psa));
	        	 ggEntidades.setPctrga(new BigDecimal(pRGA));
	        	 ggEntidades.setPctentidades(new BigDecimal(pe));
	        	 ggEntidades.setPlan(new Long(plan));
	        	 ggEntidades.setUsuario(usuario);
	        	 ggEntidades.setFecModificacion(new Date());
	        	 
				GGEEntidadesDao.saveOrUpdate(ggEntidades);
	         }
	     	logger.debug("end - Modificar");
	        return ggEntidades;
	         
		}catch(DAOException dao){
			logger.error("Se ha producido un error al modificar los porcentajes: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al modificar los porcentajes:",dao);
		}
	}
	
	/**
	 * Metodo que devuelve los ultimos porcentajes de entidades
	 * @param year 
	 * @return
	 * @throws BusinessException
	 */
	public GGEEntidades getLastGGEPlan() throws BusinessException {
		logger.debug("init - getLastGGEPlan");
		GGEEntidades entidades = null;
		try {
			
			entidades = GGEEntidadesDao.getLastGGEPlan(); 
			
			logger.debug("end - getLastGGEPlan");
			return entidades;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar los ultimos porcentajes: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar los ultimos porcentajes",dao);
		}		
	}
	
	/**
	 * Metodo que devuelve los porcentajes de entidades del anio que se le pasa por parametro
	 * @param year 
	 * @return
	 * @throws BusinessException
	 */
	public GGEEntidades getLastGGEPlan(Long year) throws BusinessException {
		logger.debug("init - getLastGGEPlan");
		GGEEntidades entidades = null;
		try {
			
			entidades = GGEEntidadesDao.getLastGGEPlan(year);
			
			logger.debug("end - getLastGGEPlan");
			return entidades;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar los ultimos porcentajes: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar los ultimos porcentajes",dao);
		}
	}
	
	/**
	 * Metodo que devuelve el ultimo existente y el siguiente plan para dar de alta
	 * @param listCultivosSubentidades
	 * @return
	 */
	public Map<String, Object> getPlanesReplicar(GGEEntidades ggeEntidade) {
		logger.debug("init - getPlanesReplicar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		Long planOrigen = new Long(0);
		Long planDestino = null;
		
		planOrigen = 
		
		planDestino = planOrigen + 1;
		
		parametros.put("planorigen", planOrigen);
		parametros.put("plannuevo", planDestino);

		logger.debug("end - getPlanesReplicar");
		return parametros;	
	}
	

	public GGEEntidades getEntidadByIdPlan(Long idplan) throws BusinessException {
		logger.debug("init - getEntidadByIdPlan");
		GGEEntidades entidades = null;
		try {
			
			entidades = GGEEntidadesDao.getEntidadByIdPlan(idplan);
			
			logger.debug("end - getEntidadByIdPlan");
			return entidades;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el plan: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error el plan",dao);
		}
		
	}	
	

	public List<GGEEntidades> getAll() throws BusinessException{
		logger.debug("init - getAll");	
		List<GGEEntidades> list;
		try {			
			list = GGEEntidadesDao.getAll();			
			logger.debug("end - getAll");
			return list;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar todos los planes: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar todos los planes",dao);
		}
		
		
	}
	public IGGEEntidadesDao getGGEEntidadesDao() {
		return GGEEntidadesDao;
	}

	public void setGGEEntidadesDao(IGGEEntidadesDao entidadesDao) {
		GGEEntidadesDao = entidadesDao;
	}


}
