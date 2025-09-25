package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
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
import com.rsi.agp.dao.models.comisiones.ICultivosEntidadesDao;
import com.rsi.agp.dao.models.comisiones.ICultivosSubEntidadesDao;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidadesHistorico;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class CultivosSubEntidadesManager implements IManager {
	private static final Log logger = LogFactory.getLog(CultivosSubEntidadesManager.class);
	private ICultivosSubEntidadesDao cultivosSubEntidadesDao;
	private ICultivosEntidadesDao cultivosEntidadesDao;
	private IPolizasPctComisionesDao polizasPctComisionesDao;

	/**
	 * Metodo que devuelve el listado de la distribucion de comisiones de las mediadoras
	 * @param cultivosSubentidadesBean
	 * @return
	 * @throws BusinessException
	 */
	public List<CultivosSubentidades> listCultivosSubentidades(CultivosSubentidades cultivosSubentidadesBean) throws BusinessException {
		logger.debug("init - listCultivosSubentidades");
		List<CultivosSubentidades> list = null;
		try {
			
			list = cultivosSubEntidadesDao.listCultivosSubentidades(cultivosSubentidadesBean, true);
			
			logger.debug("end - listCultivosSubentidades");
			return list;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de distribucion de mediadoras: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar el listado de distribucion de mediadoras",dao);
		}
	}
	
	/**
	 * Metodo que da de alta/update una distribucion de mediadores
	 * @param cultivosSubentidadesBean
	 * @throws BusinessException
	 * @throws Exception
	 */
	public ArrayList<Integer> guardarDistribucionMediadores(CultivosSubentidades cultivosSubentidadesBean) throws BusinessException, Exception {
		logger.debug("init - guardarDistribucionMediadores");
		ArrayList<Integer> errList = null;
		try {
//			VALIDAMOS LA INTEGRIDAD DE LOS DATOS
			errList = this.validacionDistribucionMediadores(cultivosSubentidadesBean);

//			SIN ERRORES DE VALIDACION GUARDAMOS EN LA BBDD
			if(errList.size() ==  0){
				cultivosSubentidadesBean.setFecModificacion(new Date());
				cultivosSubentidadesBean.setPctentidad(new BigDecimal(100).subtract(cultivosSubentidadesBean.getPctmediador()));
				cultivosSubEntidadesDao.saveOrUpdate(cultivosSubentidadesBean);
			}
			
			logger.debug("end - guardarDistribucionMediadores");
			return errList;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar la comision por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al guardar la comision por cultivo entidad",dao);
		}
	}
	
	/**
	 * Metodo de validacion de los datos introducidos de linea y entidad/subentidad
	 * @param cultivosSubentidadesBean
	 * @return 
	 * @throws DAOException
	 */
	private ArrayList<Integer> validacionDistribucionMediadores(CultivosSubentidades cultivosSubentidadesBean) throws DAOException {
		logger.debug("init - validacionDistribucionMediadores");
		ArrayList<Integer> errList = new ArrayList<Integer>();
//		CALCULAMOS LA LINEA INTRODUCIDA
		Linea linea = cultivosSubEntidadesDao.getLineaseguroId(cultivosSubentidadesBean.getLinea().getCodlinea(), cultivosSubentidadesBean.getLinea().getCodplan());
		if(linea == null)
			errList.add(1);
		else
			cultivosSubentidadesBean.setLinea(linea);
		
//		CALCULAMOS LA ENTIDAD/SUBENTIDAD INTRODUCIDA
		SubentidadMediadora subentidadMediadora = cultivosSubEntidadesDao.getSubentidadMediadora(cultivosSubentidadesBean.getSubentidadMediadora());
		if(subentidadMediadora == null)
			errList.add(2);
		else{
			cultivosSubentidadesBean.setSubentidadMediadora(subentidadMediadora);
			cultivosSubentidadesBean.getEntidad().setCodentidad(subentidadMediadora.getId().getCodentidad());
		}
			
		logger.debug("end - validacionDistribucionMediadores");
		return errList;
	}
	
	/**
	 * Metodo que devuelve una distrubucion de mediadoras a partir del id
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public CultivosSubentidades getCultivosSubEntidades(Long id) throws BusinessException {
		logger.debug("init - getCultivosSubEntidades");
		try {
			
			logger.debug("end - getCultivosSubEntidades");
			return (CultivosSubentidades) cultivosSubEntidadesDao.get(CultivosSubentidades.class, id);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar la comision por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar la comision por cultivo entidad",dao);
		}
	}
	
	/**
	 * Metodo que borra un registro 
	 * @param cultivosSubentidades
	 * @throws BusinessException
	 */
	public void borrarParametrosComisiones(CultivosSubentidades cultivosSubentidades) throws BusinessException {
		logger.debug("init - borrarParametrosComisiones");
		try {
			cultivosSubentidades.setFecBaja(new Date());
			//cultivosSubentidades.setFecEfecto(new Date());
			cultivosSubEntidadesDao.saveOrUpdate(cultivosSubentidades);
			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al borrar la comision por cultivo entidad: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al borrar la comision por cultivo entidad",dao);
		}
		
		logger.debug("end - borrarParametrosComisiones");
	}
	
	/**
	 * Metodo que comprueba si para el plan /linea/mediador introducidos existe algun registro
	 * @param cultivosSubentidadesBean
	 * @return
	 * @throws BusinessException
	 */
	public boolean existeRegistro(CultivosSubentidades cultivosSubentidadesBean) throws BusinessException {
		logger.debug("init - existeRegistro");
		Integer count = null;
		boolean resultado = false;
		try {
			
			count = cultivosSubEntidadesDao.existeRegistro(cultivosSubentidadesBean);
			
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
	 * Metodo que genera una distribucion de mediadores por defecto para la linea 999
	 * @return
	 * @throws DAOException
	 */
	public CultivosSubentidades generarDistMedLinea999() throws DAOException {
		logger.debug("init - generarDistMedLinea999");
		CultivosSubentidades cultivosSubentidades = new CultivosSubentidades();
		
		CultivosEntidades cultivosEntidades = cultivosEntidadesDao.getLastCultivosEntidades(true).get(0);
		Linea linea = (Linea) cultivosSubEntidadesDao.getLineaseguroId(new BigDecimal(999), cultivosEntidades.getLinea().getCodplan());
		if (linea != null) {
			cultivosSubentidades.setLinea(linea);
		}
		cultivosSubentidades.setPctmediador(new BigDecimal(100));
		
		
		logger.debug("end - generarDistMedLinea999");
		return cultivosSubentidades;
	}
	
	/**
	 * Metodo que comprueba si esta dada de alta, una distribucion de mediadores para la lÃ­nea 999
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	public boolean compruebaLinea999() throws DAOException {
		logger.debug("init - compruebaLinea999");
		boolean encontrado = false;		
		
		CultivosSubentidades cultivosSubentidadesBean = new CultivosSubentidades();
		List<CultivosSubentidades>listCultivosSubentidades = cultivosSubEntidadesDao.listCultivosSubentidades(cultivosSubentidadesBean, false);
		
		CultivosEntidades cultivosEntidades = cultivosEntidadesDao.getLastCultivosEntidades(true).get(0);
		
		Linea linea = (Linea) cultivosSubEntidadesDao.getLineaseguroId(new BigDecimal(999), cultivosEntidades.getLinea().getCodplan());
		
		for(CultivosSubentidades cs:listCultivosSubentidades){
			if(cs.getLinea().equals(linea)){
				encontrado = true;
				break;
			}
		}
		logger.debug("end - compruebaLinea999");
		return encontrado;
	}
	
	/**
	 * Metodo que devuelve la linea actual y la siguiente
	 * @param listCultivosSubentidades
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> getLineasReplicar(List<CultivosSubentidades> listCultivosSubentidades) throws BusinessException {
		logger.debug("init - getsLineasReplicar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		BigDecimal lineaOrigen = new BigDecimal(0);
		BigDecimal lineaDestino = null;
		
		try {		
			
			lineaOrigen = cultivosSubEntidadesDao.getLineaActual();
			lineaDestino = lineaOrigen.add(new BigDecimal(1));
			
			parametros.put("lineaorigen", lineaOrigen);
			parametros.put("lineanuevo", lineaDestino);			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al obtener las lineas para replicar: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al obtener las lineas para replicar",dao);
		}	

		logger.debug("end - getPlanesReplicar");
		return parametros;
	}
	
	/**
	 * Metodo que devuelve el ultimo existente y el siguiente plan para dar de alta
	 * @param listCultivosSubentidades
	 * @return
	 * @throws BusinessException 
	 */
	public Map<String, Object> getPlanesReplicar(List<CultivosSubentidades> listCultivosSubentidades) throws BusinessException {
		logger.debug("init - getPlanesReplicar");
		Map<String, Object> parametros = new HashMap<String, Object>();
		BigDecimal planOrigen = new BigDecimal(0);
		BigDecimal planDestino = null;
		
		try {		
			
			planOrigen = cultivosSubEntidadesDao.getPlanActual();
			planDestino = planOrigen.add(new BigDecimal(1));
			
			parametros.put("planorigen", planOrigen);
			parametros.put("plannuevo", planDestino);			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al obtener los planes para replicar: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al obtener los planes para replicar",dao);
		}	

		logger.debug("end - getPlanesReplicar");
		return parametros;
	}
	
	/**
	 * Metodo que replica la distribucion de comisiones del mediador de un plan origen a un plan destino
	 * @param planOrigen
	 * @param linea
	 * @return
	 * @throws BusinessException
	 */
	public ArrayList<Integer> replicarPlanLinea(String planOrigen, String planDestino, String lineaOrigen, String lineaDestino) throws BusinessException {
		logger.debug("init - replicarPlan");
		List<CultivosEntidades> listEntOr = null;
		List<CultivosEntidades> listEntDest = new ArrayList<CultivosEntidades>();
		List<CultivosSubentidades> listSubentOr = null;
		List<CultivosSubentidades> listSubentDest = new ArrayList<CultivosSubentidades>();
		Linea linDest = new Linea();
		Linea linBean = new Linea();
		CultivosSubentidades cultivosSubentidadesBean = new CultivosSubentidades();
		CultivosEntidades cultivosEntidadesBean = new CultivosEntidades();
		CultivosSubentidades auxSubent = null;
		CultivosEntidades auxEnt = null;
		ArrayList<Integer> errList = new ArrayList<Integer>();
		
		try {
			linDest = cultivosSubEntidadesDao.getLineaseguroId(new BigDecimal(lineaDestino), new BigDecimal(planDestino));			
			logger.info("replicarPlan: Se comprueba que existe plan/linea destino");
			if (linDest == null){
				logger.info("replicarPlanLinea: No existe ninguna linea del plan origen en el plan destino, por tanto no se puede replicar");
				errList.add(6);
			} else {				
				linBean.setCodplan(new BigDecimal(planOrigen));
				linBean.setCodlinea(new BigDecimal(lineaOrigen));
				cultivosEntidadesBean.setLinea(linBean);
				listEntOr = cultivosEntidadesDao.listComisionesCultivosEntidades(cultivosEntidadesBean);
				logger.info("replicarPlan: Se comprueba que el plan origen exista y tenga algun registro");
				if (listEntOr.size() > 0) {
					logger.info("replicarPlan: Se copian todos los datos de parametros generales");
					for(CultivosEntidades or: listEntOr) {									
						if (or.getFechaBaja() == null) {
							auxEnt = new CultivosEntidades();
							auxEnt.setFechamodificacion(new Date());
							auxEnt.setLinea(linDest);
							auxEnt.setPctgeneralentidad(or.getPctgeneralentidad());
							auxEnt.setPctrga(or.getPctrga());
							auxEnt.setUsuario(or.getUsuario());
							auxEnt.setFechaEfecto(or.getFechaEfecto());
							auxEnt.setPctadministracion(or.getPctadministracion());
							auxEnt.setPctadquisicion(or.getPctadquisicion());
							auxEnt.setGrupoNegocio(or.getGrupoNegocio());
							auxEnt.setSubentidadMediadora(or.getSubentidadMediadora());
							listEntDest.add(auxEnt);
						}
					}
					
					if (listEntDest.isEmpty()) {
						logger.info("Todos los registros origen estan dados de baja");
						errList.add(3);
					} else {
						
						/* ESC-17100 ** MODIF TAM (07.02.2022) ** Inicio */
						/* Antes de copiar los parametros generales, comprobamos si la replica se va a realizar */
						/*cultivosSubEntidadesDao.saveOrUpdateList(listEntDest);*/
						/* ESC-17100 ** MODIF TAM (07.02.2022) ** Fin */
						
						logger.info("replicarPlan: Se copian todos los datos de subentidades");
						cultivosSubentidadesBean.setLinea(linBean);
						listSubentOr = cultivosSubEntidadesDao.listCultivosSubentidades(cultivosSubentidadesBean, false);
						boolean canSave = true;
						for(CultivosSubentidades or: listSubentOr){
							cultivosSubentidadesBean.setLinea(linDest);
							
							List<CultivosSubentidades> cultivosSub = cultivosSubEntidadesDao.listCultivosSubentidades(cultivosSubentidadesBean, false);
							
							boolean fechaBajaDestNull = false;
							
							for (CultivosSubentidades c: cultivosSub ) {
								if (c.getFecBaja() == null) {
									fechaBajaDestNull = true;
									break;
								}
							}
							//if(!this.existeRegistro(cultivosSubentidadesBean)){
							if (!fechaBajaDestNull) { // Si todos los registros de destino tienen la fecha de baja
								if (or.getFecBaja() == null ) {// si no esta dado de baja le replicamos
									
									auxSubent = new CultivosSubentidades();
									auxSubent.setLinea(linDest);
									auxSubent.setEntidad(or.getEntidad());
									auxSubent.setPctentidad(or.getPctentidad());
									auxSubent.setPctmediador(or.getPctmediador());
									auxSubent.setSubentidadMediadora(or.getSubentidadMediadora());
									auxSubent.setUsuario(or.getUsuario());
									/* ESC-17100 ** MODIF TAM (16/02/2022) ** Inicio */
									/* Al realizar una replica del plan/linea, los registros  de los replicados 
									 * deberían tener la fecha de cuando hacemos la replica */
									/*auxSubent.setFecEfecto(or.getFecEfecto());*/
									auxSubent.setFecEfecto(new Date());
									/* ESC-17100 ** MODIF TAM (16/02/2022) ** Fin */
									auxSubent.setFecModificacion(new Date());
									listSubentDest.add(auxSubent);
								}
							} else {
								errList.add(5);
								canSave = false;
								break;
							}							
						}
						if (canSave && !listSubentDest.isEmpty()) {
							/* ESC-17100 ** MODIF TAM (07.02.2022) ** Inicio */
							/* Si se replica la línea, RGA ha solicitado NO copiar los datos de parametros generales */
							/*cultivosSubEntidadesDao.saveOrUpdateList(listEntDest);*/
							/* ESC-17100 ** MODIF TAM (07.02.2022) ** Fin  */
							cultivosSubEntidadesDao.saveOrUpdateList(listSubentDest);	
						}
					}
				} else {
					errList.add(3);
				}
			}		
			
			return errList;			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al replicar: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al replicar",dao);
		}		
	}
	
	public ArrayList<CultivosSubentidadesHistorico> consultaHistorico(
			 Long id) throws BusinessException {
		
		try {
			return cultivosSubEntidadesDao.consultaHistorico (id);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al consultar el historico: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al consultar el historico",dao);
		}
	}
	
	/**
	 * Obtiene los porcentajes de comisión de entidad y subentidad mediadora
	 * @param lineaSeguroId
	 * @param codEntidadMed
	 * @param codSubentidadMed
	 * @return
	 * @throws BusinessException
	 */
	public List<String> obtenerPorcentajesEntMedYSubEntMed(Poliza p) throws BusinessException {
		List<String> lstParamsGen = new ArrayList<String>();
		try {
			/* recogemos los datos del mto de comisiones por E-S Mediadora */
			Object[] paramsGen = polizasPctComisionesDao.getComisionesESMed (p.getLinea().getLineaseguroid(),
					p.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
					p.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(),
					p.getLinea().getCodlinea(),p.getLinea().getCodplan(),null);
			
			if (paramsGen != null && paramsGen.length >0){
				lstParamsGen.add((String) paramsGen[0].toString());
				lstParamsGen.add((String) paramsGen[1].toString());
			}
			return lstParamsGen;

		} catch (DAOException dao) {
			logger.error("Se ha producido un error en obtenerPorcentajesEntMedYSubEntMed: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error en obtenerPorcentajesEntMedYSubEntMed",dao);
		} catch (Exception ex) {
			logger.error("Error en obtenerPorcentajesEntMedYSubEntMed: " + ex.getMessage());
			throw new BusinessException ("Error en obtenerPorcentajesEntMedYSubEntMed",ex);
		}	
	}

	public ICultivosSubEntidadesDao getCultivosSubEntidadesDao() {
		return cultivosSubEntidadesDao;
	}

	public void setCultivosSubEntidadesDao(	ICultivosSubEntidadesDao cultivosSubEntidadesDao) {
		this.cultivosSubEntidadesDao = cultivosSubEntidadesDao;
	}

	public ICultivosEntidadesDao getCultivosEntidadesDao() {
		return cultivosEntidadesDao;
	}

	public void setCultivosEntidadesDao(ICultivosEntidadesDao cultivosEntidadesDao) {
		this.cultivosEntidadesDao = cultivosEntidadesDao;
	}

	public void setPolizasPctComisionesDao(
			IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}	

}
