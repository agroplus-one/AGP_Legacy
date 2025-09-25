package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.IGGESubEntidadesDao;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.GGEEntidades;
import com.rsi.agp.dao.tables.comisiones.GGESubentidades;
import com.rsi.agp.dao.tables.commons.Usuario;

public class GGESubEntidadesManager implements IManager {
	
	private static final Log logger = LogFactory.getLog(GGESubEntidadesManager.class);
	
	private IGGESubEntidadesDao GGESubEntidadesDao;
	private GGEEntidadesManager GGEentidadesManager;
	
	/**
	 * 
	 * @param ggeSubentidades
	 * @return
	 * @throws BusinessException
	 */
	public List<GGESubentidades> getListGGESubentidades(GGESubentidades ggeSubentidades) throws BusinessException{
		logger.debug("init - getListGGESubentidades. [PARAM] ggeSubentidades");
		List<GGESubentidades> listgeeSubentidades = null;
		try{
			
			listgeeSubentidades = GGESubEntidadesDao.getListGGESubentidades(ggeSubentidades);
			
		}catch(DAOException dao){
			logger.error("Se ha producido un error al recuperar el listado de distribucion de GGE: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar el listado de distribucion de GGE",dao);
		}
		logger.debug("end - getListGGESubentidades");
		return listgeeSubentidades;
	}
	
	/**
	 * 
	 * @param id
	 * @throws BusinessException
	 */
	public void baja(Long id) throws BusinessException{
		logger.debug("init - baja. [PARAM] id="+id);
		try{
			
			GGESubEntidadesDao.delete(GGESubentidades.class, id);
			  
		}catch(DAOException dao){
			logger.error("Se ha producido un error al dar de baja una distribucion de GGE: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al dar de baja una distribucion de GGE",dao);
		}
		logger.debug("end - baja");
	}
	
	/**
	 * 
	 * @param geeSubEntidad
	 * @param usuario
	 * @throws BusinessException
	 */
	public ArrayList<Integer> saveUpdateGGE(GGESubentidades geeSubEntidad, Usuario usuario) throws BusinessException{
		 logger.debug("init - saveUpdateGGE. [PARAM] geeSubEntidad,usuario");
		 ArrayList<Integer> errList = new ArrayList<Integer>();
		  try{
			  errList = this.validacionDistribucionGGE(geeSubEntidad);
			  
			  if(errList.size() == 0){
				  geeSubEntidad.setFechamodificacion(Calendar.getInstance().getTime());
				  geeSubEntidad.setUsuario(usuario);
				  geeSubEntidad.setPctentidad(new BigDecimal(100).subtract(geeSubEntidad.getPctmediador()));
				  GGESubEntidadesDao.saveOrUpdate(geeSubEntidad);
			  }
			  
			  logger.debug("end - saveUpdateGGE");
			  return errList;
		      
		  }catch(DAOException dao){
			logger.error("Se ha producido un error al dar de alta una distribucion de GGE: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al dar de alta una distribucion de GGE",dao);
		  }
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public GGESubentidades getGge(Long id) throws BusinessException {
		logger.debug("init - getGge. [PARAM] id=" +id);
		GGESubentidades gge = null;
		try {
			
			gge = (GGESubentidades)GGESubEntidadesDao.get(GGESubentidades.class, id);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar una distribucion de GGE: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar una distribucion de GGE",dao);
		}
		logger.debug("init - getGge");
		return gge;
	}
	
	/**
	 * 
	 * @param geeSubentidadesBean
	 * @return
	 * @throws BusinessException 
	 */
	public boolean existeRegistro(GGESubentidades geeSubentidadesBean) throws BusinessException {
		logger.debug("init - existeRegistro");
		Integer count = null;
		boolean resultado = false;
		try {
			
			count = GGESubEntidadesDao.existeRegistro(geeSubentidadesBean);
			if(count == 0)
				resultado = false;
			else
				resultado = true;
		
		} catch (DAOException dao) {
			logger.error("Se ha producido un error comprobar si existe el registro en la BBDD: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error comprobar si existe el registro en la BBDD",dao);
		}
		logger.debug("end - existeRegistro");
		return resultado;
	}
	
	/**
	 * Metodo de validacion de los datos introducidos de linea y entidadsubentidad
	 * @param cultivosSubentidadesBean
	 * @return 
	 * @throws DAOException
	 */
	private ArrayList<Integer> validacionDistribucionGGE(GGESubentidades GGESubentidades) throws DAOException {
		logger.debug("init - validacionDistribucionMediadores");
		ArrayList<Integer> errList = new ArrayList<Integer>();
		
//		CALCULAMOS QUE EL PLAN INTRODUCIDO ES VALIDO
		if(!GGESubEntidadesDao.existePlan(GGESubentidades.getPlan().toString())){			
			errList.add(1);
		}
		
//		CALCULAMOS LA ENTIDAD/SUBENTIDAD INTRODUCIDA
		SubentidadMediadora subentidadMediadora = GGESubEntidadesDao.getSubentidadMediadora(GGESubentidades.getSubentidadMediadora());
		if(subentidadMediadora == null)
			errList.add(2);
		else{
			GGESubentidades.setSubentidadMediadora(subentidadMediadora);
			GGESubentidades.getEntidad().setCodentidad(subentidadMediadora.getId().getCodentidad());
		}
			
		logger.debug("end - validacionDistribucionMediadores");
		return errList;
	}
	
	public boolean existePlan(GGESubentidades geeSubentidadesBean) throws BusinessException {
		logger.debug("init - existePlan");
		boolean resultado = false;
		try {
			
			resultado = GGESubEntidadesDao.existePlan(geeSubentidadesBean.getPlan().toString());			
		
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar si está dado de alta el plan en la BBDD: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar si está dado de alta el plan en la BBDD",dao);
		}
		logger.debug("end - existePlan");
		return resultado;
	}
	
	/**
	 * Metodo que devuelve el ultimo existente y el siguiente plan para dar de alta
	 * @param listCultivosSubentidades
	 * @return
	 */
	public Map<String, Object> getPlanesReplicar(List<GGESubentidades> listGGESubentidades) {
		logger.debug("init - getPlanesReplicar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		Long planOrigen = new Long(0);
		Long planDestino = null;
		
		for(GGESubentidades gge:listGGESubentidades){
			if(gge.getPlan().compareTo(planOrigen) > 0){
				planOrigen = gge.getPlan();
			}
		}
		
		planDestino = planOrigen + 1;
		
		parametros.put("planorigen", planOrigen);
		parametros.put("plannuevo", planDestino);

		logger.debug("end - getPlanesReplicar");
		return parametros;
	}
	
	/**
	 * Metodo que replica la distribucion de comisiones del mediador de un plan origen a un plan destino
	 * @param planOrigen
	 * @param linea
	 * @param planDestino
	 * @param usuario 
	 * @return
	 * @throws BusinessException
	 */
	public ArrayList<Integer> replicarPlan(String planOrigen,String planDestino, Usuario usuario) throws BusinessException {
		logger.debug("init - replicarPlan");
		List<GGESubentidades> listOr = null;
		GGESubentidades GGESubentidades = new GGESubentidades();
		GGESubentidades auxSubEnt = null;
		GGEEntidades GGEEntidades = new GGEEntidades();
		GGEEntidades auxEnt = null;
		ArrayList<Integer> errList = new ArrayList<Integer>();
		
		try {
			
			logger.debug("comprobamos que es valido el plan destino");
			if((!GGESubEntidadesDao.existePlan(planDestino)) && (Long.parseLong(planDestino) > Long.parseLong(planOrigen))){
				logger.debug("comprobamos que existe el plan origen");
				if(GGESubEntidadesDao.existePlan(planOrigen)){
					GGESubentidades.setPlan(new Long(planDestino));
					logger.debug("comprobamos que no se hayan insertado registros para el plan destino");
					if(!this.existeRegistro(GGESubentidades)){						
						GGEEntidades = GGEentidadesManager.getLastGGEPlan(new Long(planOrigen));
						
						auxEnt = new GGEEntidades();
						auxEnt.setPlan(new Long(planDestino));
						auxEnt.setFecModificacion(new Date());						
						auxEnt.setPctentidades(GGEEntidades.getPctentidades());
						auxEnt.setPctrga(GGEEntidades.getPctrga());
						//auxEnt.setPctsectoragricola(GGEEntidades.getPctsectoragricola());
						auxEnt.setPctsectoragricola(GGESubEntidadesDao.getPctSectorAgricola(Long.parseLong(planDestino)));
						auxEnt.setUsuario(usuario);
						GGESubEntidadesDao.saveOrUpdate(auxEnt);
						
						GGESubentidades.setPlan(new Long(planOrigen));
						listOr = GGESubEntidadesDao.getListGGESubentidades(GGESubentidades);
						
						for(GGESubentidades gge: listOr){
							auxSubEnt = new GGESubentidades();
							auxSubEnt.setEntidad(gge.getEntidad());					
							auxSubEnt.setPctmediador(gge.getPctmediador());
							auxSubEnt.setPlan(new Long(planDestino));
							auxSubEnt.setSubentidadMediadora(gge.getSubentidadMediadora());
							auxSubEnt.setUsuario(gge.getUsuario());
							saveUpdateGGE(auxSubEnt, gge.getUsuario());
						}
						
					}else{
						errList.add(5);
					}
				}else{
					errList.add(3);
				}
			}else{
				errList.add(4);
			}
			
			logger.debug("end - replicarPlan");
			return errList;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al replicar: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al replicar",dao);
		}
	}


	public IGGESubEntidadesDao getGGESubEntidadesDao() {
		return GGESubEntidadesDao;
	}

	public void setGGESubEntidadesDao(IGGESubEntidadesDao subEntidadesDao) {
		GGESubEntidadesDao = subEntidadesDao;
	}

	public void setGGEentidadesManager(GGEEntidadesManager entidadesManager) {
		GGEentidadesManager = entidadesManager;
	}
	
	
}
