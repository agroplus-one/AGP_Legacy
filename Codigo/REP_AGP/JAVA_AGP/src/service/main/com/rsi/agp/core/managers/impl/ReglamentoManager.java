package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.IReglamentoDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.comisiones.Reglamento;

public class ReglamentoManager implements IManager {
	private static final Log logger = LogFactory.getLog(ReglamentoManager.class);
	private IReglamentoDao reglamentoDao;
	
	/**
	 * Metodo que devuelve el listado de reglamentos filtrados
	 * @param reglamentoBean
	 * @return
	 * @throws BusinessException
	 */
	public List<Reglamento> listReglamentos(Reglamento reglamentoBean) throws BusinessException {
		logger.debug("init - listReglamentos");
		 List<Reglamento> list = null;
		 
		try {
			
			list = reglamentoDao.listReglamentos(reglamentoBean);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de reglamentos: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar el listado de reglamentos",dao);
		}
		
		logger.debug("end - listReglamentos");
		return list;
	}
	
	/**
	 * Metodo que da de alta/edita en la BBDDun registro de reglamento, si las validaciones son correctas
	 * @param reglamentoBean
	 * @return
	 * @throws BusinessException
	 */
	public ArrayList<Integer> guardarReglamento(Reglamento reglamentoBean) throws BusinessException {
		logger.debug("init - guardarReglamento");
		ArrayList<Integer> errlist = null;
		
		try {
			
			errlist = this.validarReglamento(reglamentoBean);
			
			if(errlist.size() == 0){
				reglamentoBean.setFechamodificacion(new Date());
				reglamentoDao.saveOrUpdate(reglamentoBean);
			}
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar/editar el reglamento: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar/editar el reglamento",dao);
		}
		
		logger.debug("end - guardarReglamento");
		return errlist;
	}

	/**
	 * Metodo que valida un reglamento
	 * @param reglamentoBean
	 * @return
	 * @throws DAOException 
	 */
	private ArrayList<Integer> validarReglamento(Reglamento reglamentoBean) throws DAOException {
		logger.debug("init - validarReglamento");
		ArrayList<Integer> errList = new ArrayList<Integer>();
		
//		CALCULAMOS QUE EL PLAN INTRODUCIDO ES VALIDO
		if(!reglamentoDao.existePlan(reglamentoBean.getPlan().toString())){
			errList.add(1);
		}
		
//		CALCULAMOS LA ENTIDAD INTRODUCIDA
		Entidad entidad = reglamentoDao.getEntidad(reglamentoBean.getEntidad());
		if(entidad == null)
			errList.add(2);
			
		logger.debug("end - validarReglamento");
		return errList;
	}

	/**
	 * Metodo que devuelve un reglamento
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public Reglamento getReglamento(Long id) throws BusinessException{
		logger.debug("init - getReglamento");
		Reglamento reglamento = null;
		
		try {
			
			reglamento = (Reglamento) reglamentoDao.get(Reglamento.class, id);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el reglamento: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar el reglamento",dao);
		}
		
		logger.debug("end - getReglamento");
		return reglamento;
	}
	
	/**
	 * Metodo que elimina un reglamento de la bbdd
	 * @param reglamentoBean
	 * @throws BusinessException 
	 */
	public void borrarReglamento(Reglamento reglamentoBean) throws BusinessException {
		logger.debug("init - borrarReglamento");
		try {
			
			reglamentoDao.delete(Reglamento.class, reglamentoBean.getId());
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al borrar el reglamento: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al borrar el reglamento",dao);
		}
		logger.debug("end - borrarReglamento");
	}
	
	/**
	 * Metodo que comprueba si existe algun registro igual en la BBDD
	 * @param reglamentoBean
	 * @return
	 * @throws BusinessException
	 */
	public boolean existeRegistro(Reglamento reglamentoBean) throws BusinessException {
		logger.debug("init - existeRegistro");
		Integer count = null;
		boolean resultado = false;
		try {
			
			count = reglamentoDao.existeRegistro(reglamentoBean);
			
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
	 * Metod que replica para un plan origen un distribucion de reglamentos
	 * @param planOrigen
	 * @param planDestino
	 * @return
	 * @throws BusinessException 
	 */
	public ArrayList<Integer> replicarPlan(String planOrigen, String planDestino) throws BusinessException {
		logger.debug("init - replicarPlan");
		List<Reglamento> listOr = null;
		List<Reglamento> listDe = new ArrayList<Reglamento>();
		Reglamento reglamento = new Reglamento();
		Reglamento aux = null;
		ArrayList<Integer> errList = new ArrayList<Integer>();
		
		try {
			
			logger.debug("comprobamos que existe el plan destino");
			if(reglamentoDao.existePlan(planDestino)){
				logger.debug("comprobamos que existe el plan origen");
				if(reglamentoDao.existePlan(planOrigen)){
					reglamento.setPlan(new Long(planDestino));
					logger.debug("comprobamos que no se hayan insertado registros para el plan destino");
					if(!this.existeRegistro(reglamento)){
						reglamento.setPlan(new Long(planOrigen));
						listOr = reglamentoDao.listReglamentos(reglamento);
						
						for(Reglamento reg: listOr){
							aux = new Reglamento();
							aux.setEntidad(reg.getEntidad());
							aux.setFechamodificacion(new Date());
							aux.setPctentidad(reg.getPctentidad());
							aux.setPctrga(reg.getPctrga());
							aux.setPlan(new Long(planDestino));
							aux.setUsuario(reg.getUsuario());
							listDe.add(aux);
						}
						
						reglamentoDao.saveOrUpdateList(listDe);
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
	
	/**
	 * Metodo que devuelve el ultimo existente y el siguiente plan para dar de alta
	 * @param listReglamentos
	 * @return
	 */
	public Map<String, Object> getPlanesReplicar(List<Reglamento> listReglamentos) {
		logger.debug("init - getPlanesReplicar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		Long planOrigen = new Long(0);
		Long planDestino = null;
		
		for(Reglamento reg:listReglamentos){
			if(reg.getPlan().compareTo(planOrigen) > 0){
				planOrigen = reg.getPlan();
			}
		}
		
		planDestino = planOrigen + 1;
		
		parametros.put("planorigen", planOrigen);
		parametros.put("plannuevo", planDestino);

		logger.debug("end - getPlanesReplicar");
		return parametros;
	}


	public IReglamentoDao getReglamentoDao() {
		return reglamentoDao;
	}

	public void setReglamentoDao(IReglamentoDao reglamentoDao) {
		this.reglamentoDao = reglamentoDao;
	}

	

}
