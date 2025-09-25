package com.rsi.agp.core.managers.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.IIncidenciasComisionesDao;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroIncidencia;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultIncidencias;


public class IncidenciasComisionesManager implements IManager{
	
	private static final Log logger = LogFactory.getLog(IncidenciasComisionesManager.class);
	private IIncidenciasComisionesDao incidenciasComisionesDao;
	private ImportacionComisionesManager importacionComisionesManager;
		
	private static final String ESTADO_ERRONEO = "Erroneo";
	
	/**
	 * Obtiene una lista de ficheros de incidencias
	 * @param ficheroIncidenciaBean
	 * @return
	 * @throws BusinessException
	 */
	public List<FicheroIncidencia> getListFicherosIncidencias(FicheroIncidencia ficheroIncidenciaBean)throws BusinessException {
		logger.debug("init - getListFicherosIncidencias");
		List<FicheroIncidencia> list= null;
		try {
			
			list = incidenciasComisionesDao.getListFicherosIncidencias(ficheroIncidenciaBean);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de incidencias: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el listado de incidencias",dao);
		}
		logger.debug("end - getListFicherosIncidencias");
		return list;
	}
	
	/**
	 * Obtiene un fichero de incidencias a partir de un id
	 * @param idFichero
	 * @return
	 * @throws BusinessException
	 */
	public Fichero getFicheroIncidencias(String idFichero) throws BusinessException {
		logger.debug("init - getFichero");
		Fichero fichero= null;
		try {
			fichero = (Fichero) incidenciasComisionesDao.getObject(Fichero.class, Long.parseLong(idFichero));
		} catch (Exception e) {
			return null;			
		}		
		logger.debug("end - getFichero");
		return fichero;
	}
	
	/**
	 * Obtiene una lista de ficheros de incidencias
	 * @param ficheroMultIncidenciaBean
	 * @return
	 * @throws BusinessException
	 */
	public List<FicheroMultIncidencias> getListFicherosMultIncidencias(FicheroMultIncidencias ficheroMultIncidenciaBean)throws BusinessException {
		logger.debug("init - getListFicherosIncidencias");
		List<FicheroMultIncidencias> list= null;
		try {
			
			list = incidenciasComisionesDao.getListFicherosMultIncidencias(ficheroMultIncidenciaBean);
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de incidencias: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el listado de incidencias",dao);
		}
		logger.debug("end - getListFicherosIncidencias");
		return list;
	}
	
	/**
	 * Obtiene un ficheroMult de incidencias a partir de un id
	 * @param idFichero
	 * @return
	 * @throws BusinessException
	 */
	public FicheroMult getFicheroMultIncidencias(String idFichero) throws BusinessException {
		logger.debug("init - getFichero");
		FicheroMult fichero= null;
		try {
			fichero = (FicheroMult) incidenciasComisionesDao.getObject(FicheroMult.class, Long.parseLong(idFichero));
		} catch (Exception e) {
			return null;			
		}		
		logger.debug("end - getFichero");
		return fichero;
	}
	
	/**
	 * Añade parametros para filtrar por los campos correspondientes para redirigir a otras paginas
	 * @param ficheroIncidenciaBean
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> inicializarParametros(FicheroIncidencia ficheroIncidenciaBean) throws BusinessException {
		logger.debug("init - getFichero");		
		Map<String, Object> parametros = new HashMap<String, Object>();	
		String entidad = null;
		String subentidad = null;
		
		try {
			//Mejora boton gge 1-02-2011 Tamara
			//ASF - Sigpe 5969: Cogemos la E-S Med del colectivo.
			if (!ficheroIncidenciaBean.getEsMedColectivo().equals("")){
				String[] entidadSubentidad = ficheroIncidenciaBean.getEsMedColectivo().split("-"); 
				if (entidadSubentidad.length>0){
					entidad = entidadSubentidad[0];
					parametros.put("entidadmediadora",entidad);
				}
				
				if (entidadSubentidad.length>1){
					subentidad = entidadSubentidad[1];
					parametros.put("subentidad",subentidad);
				}
			}
		
			if ((ficheroIncidenciaBean.getFichero() != null)	&& (ficheroIncidenciaBean.getFichero().getFase() != null) &&
				(ficheroIncidenciaBean.getFichero().getFase().getPlan() != null)){
				parametros.put("planIncidencias", ficheroIncidenciaBean.getFichero().getFase().getPlan().longValue());
			}
			// FIN Mejora boton gge 1-02-2011 Tamara
			parametros.put("primeraConsulta", true);	
			parametros.put("tipoFichero", ficheroIncidenciaBean.getFichero().getTipofichero());	
			parametros.put("idFichero",ficheroIncidenciaBean.getFichero().getId() );
						
		} catch (Exception e) {
			logger.error("Se ha producido un error al redirigir a GCE: " + e.getMessage());
			throw new BusinessException("Se ha producido un error al redirigir a GCE", e);
		}		
		
		logger.debug("end - getFichero");
		return parametros;
	}
	
	/** 
	 * Carga un fichero con fecha de aceptacion actual siempre que no haya incidencias en estado erroneo
	 * @param ficheroIncidenciaBean
	 * @param fechaAceptacionFichero
	 * @return
	 * @throws BusinessException 
	 */
	public boolean cargarFichero(Fichero fichero, Date fechaAceptacionFichero) throws BusinessException {
		logger.debug("init - cargarFichero");
		FicheroIncidencia ficheroIncidencia = new FicheroIncidencia();		
		boolean resultado = false;
		List<FicheroIncidencia> list = null;
		
		try {
			logger.debug("Se obtienen las incidencias asociadas al fichero " + fichero + " con estado erroneo");
			ficheroIncidencia.getFichero().setId(fichero.getId());
			ficheroIncidencia.setEstado(ESTADO_ERRONEO);
			list = incidenciasComisionesDao.getListFicherosIncidencias(ficheroIncidencia);
			
			logger.debug("Si no hay ninguna incidencia en estado erroneo entonces se puede aceptar el fichero");
			if (list.size() == 0){
				fichero.setFechaaceptacion(fechaAceptacionFichero);
				incidenciasComisionesDao.saveOrUpdate(fichero);
				resultado = true;
			} 					
			
		} catch (Exception e) {
			logger.error("Se ha producido un error al aceptar el fichero " + fichero);
			throw new BusinessException("Se ha producido un error al aceptar el fichero " + fichero, e);			
		}		
		logger.debug("end - cargarFichero");
		return resultado;
	}
	
	/** 
	 * Carga un fichero con fecha de aceptacion actual siempre que no haya incidencias en estado erroneo
	 * @param ficheroMultIncidenciaBean
	 * @param fechaAceptacionFichero
	 * @return
	 * @throws BusinessException 
	 */
	public boolean cargarFicheroMult(FicheroMult fichero, Date fechaAceptacionFichero) throws BusinessException {
		logger.debug("init - cargarFicheroMult");
		FicheroMultIncidencias ficheroMultIncidencia = new FicheroMultIncidencias();		
		boolean resultado = false;
		List<FicheroMultIncidencias> list = null;
		
		try {
			logger.debug("Se obtienen las incidencias asociadas al fichero " + fichero + " con estado erroneo");
			ficheroMultIncidencia.getFicheroMult().setId(fichero.getId());
			ficheroMultIncidencia.setEstado(ESTADO_ERRONEO);
			list = incidenciasComisionesDao.getListFicherosMultIncidencias(ficheroMultIncidencia);
			
			logger.debug("Si no hay ninguna incidencia en estado erroneo entonces se puede aceptar el fichero");
			if (list.size() == 0){
				fichero.setFechaAceptacion(fechaAceptacionFichero);
				incidenciasComisionesDao.saveOrUpdate(fichero);
				resultado = true;
			} 					
			
		} catch (Exception e) {
			logger.error("Se ha producido un error al aceptar el fichero " + fichero);
			throw new BusinessException("Se ha producido un error al aceptar el fichero " + fichero, e);			
		}		
		logger.debug("end - cargarFicheroMult");
		return resultado;
	}
	
	/**
	 * Obtiene la fecha de aceptacion de un fichero
	 * @param idFichero
	 * @return
	 * @throws BusinessException
	 */
	public Date getFechaAceptacion(Long idFichero) throws BusinessException {
		logger.debug("init - getFechaAceptacion");
		Fichero fichero = new Fichero();
		Date fechaAceptacion = null;
		
		try {
			fichero = this.getFicheroIncidencias(idFichero.toString());
			fechaAceptacion = fichero.getFechaaceptacion();
		} catch (Exception e) {
			logger.error("Se ha producido un error al obtener la fecha de aceptacion del fichero " + idFichero);
			throw new BusinessException("Se ha producido un error al aceptar el fichero " + idFichero, e);		
		}
		
		logger.debug("init - getFechaAceptacion");
		return fechaAceptacion;
	}
	
	/**
	 * Chequea de nuevo los registros de un fichero para comprobar si tiene incidencias
	 * @param fichero
	 * @throws BusinessException
	 */
	public void verificarTodos(Fichero fichero) throws BusinessException {
		logger.debug("init - verificarTodos");
		Character tipo;		
		
		try {
			logger.debug("se borran todos los registros correspondientes al fichero en la tabla de incidencias");
			this.borrarIncidencias(fichero.getId());
			
			logger.debug("se realizan de nuevo las validaciones en funcion del tipo de fichero");	
			tipo = fichero.getTipofichero();
					
			switch (tipo){
				case 'C':
					importacionComisionesManager.validarFicheroComisiones(fichero.getId());
					break;
				case 'I':
					importacionComisionesManager.validarFicheroImpagados(fichero.getId());
					break;
				case 'R':
					importacionComisionesManager.validarFicheroReglamento(fichero.getId());
					break;
				case 'G':
					importacionComisionesManager.validarFicheroEmitidos(fichero.getId());
					break;
				default:
					throw new BusinessException("Tipo incorrecto");
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error al verificar los registros del fichero " + fichero.getId());
			throw new BusinessException("Se ha producido un error al aceptar el fichero " + fichero.getId(), e);		
		}
		
		logger.debug("end - verificarTodos");
	}
	
	/**
	 * Chequea de nuevo los registros de un ficheroMult para comprobar si tiene incidencias
	 * @param ficheroMult
	 * @throws BusinessException
	 */
	public void verificarTodosMult(FicheroMult fichero) throws BusinessException {
		logger.debug("init - verificarTodosMult");
		Character tipo;		
		
		try {
			logger.debug("se borran todos los registros correspondientes al fichero en la tabla de incidencias");
			this.borrarIncidencias(fichero.getId());
			
			logger.debug("se realizan de nuevo las validaciones en funcion del tipo de fichero");	
			tipo = fichero.getTipoFichero();
					
			switch (tipo){
				case 'C':
					importacionComisionesManager.validarFicheroComisiones(fichero.getId());
					break;
				case 'I':
					importacionComisionesManager.validarFicheroImpagados(fichero.getId());
					break;
				case 'R':
					importacionComisionesManager.validarFicheroReglamento(fichero.getId());
					break;
				case 'G':
					importacionComisionesManager.validarFicheroEmitidos(fichero.getId());
					break;
				default:
					throw new BusinessException("Tipo incorrecto");
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error al verificar los registros del fichero " + fichero.getId());
			throw new BusinessException("Se ha producido un error al aceptar el fichero " + fichero.getId(), e);		
		}
		
		logger.debug("end - verificarTodosMult");
	}
	
	/**
	 * Borra todas las incidencias de un fichero
	 * @param idFichero
	 * @throws BusinessException
	 */
	private void borrarIncidencias(Long idFichero) throws BusinessException{
		
		try {
			incidenciasComisionesDao.deleteFichero(idFichero);
		} catch (Exception e) {
			logger.error("Se ha producido un error al borrar las incidencias del fichero " + idFichero);
			throw new BusinessException("Se ha producido un error al aceptar el fichero " + idFichero, e);		
		}
	}
	
	public String getEstadoFichero (Fichero fichero){
		String estado = ImportacionComisionesManager.ESTADO_CARGADO;
		int estadoAviso = 0;
		Set<FicheroIncidencia> fIncidencia = fichero.getFicheroIncidencias();
		Iterator<?> iter= fIncidencia.iterator();
		 while (iter.hasNext()) {
	        FicheroIncidencia incidencia =(FicheroIncidencia) iter.next();
	        estado = ImportacionComisionesManager.ESTADO_CORRECTO;
			if (incidencia.getEstado().equals(ImportacionComisionesManager.ESTADO_ERRONEO)){
				return ImportacionComisionesManager.ESTADO_ERRONEO;
			} else if (incidencia.getEstado().equals(ImportacionComisionesManager.ESTADO_AVISO)){
				estadoAviso ++;
			}
		}
		if (estadoAviso>0)
			return  ImportacionComisionesManager.ESTADO_AVISO;
		else
			return estado;
	}
	
	public String getEstadoFicheroMult (FicheroMult fichero){
		String estado = ImportacionComisionesManager.ESTADO_CARGADO;
		int estadoAviso = 0;
		Set<FicheroMultIncidencias> fIncidencia = fichero.getFicheroMultIncidenciases();
		Iterator<?> iter= fIncidencia.iterator();
		 while (iter.hasNext()) {
	        FicheroMultIncidencias incidencia =(FicheroMultIncidencias) iter.next();
	        estado = ImportacionComisionesManager.ESTADO_CORRECTO;
			if (incidencia.getEstado().equals(ImportacionComisionesManager.ESTADO_ERRONEO)){
				return ImportacionComisionesManager.ESTADO_ERRONEO;
			} else if (incidencia.getEstado().equals(ImportacionComisionesManager.ESTADO_AVISO)){
				estadoAviso ++;
			}
		}
		if (estadoAviso>0)
			return  ImportacionComisionesManager.ESTADO_AVISO;
		else
			return estado;
	}
	
	public void setIncidenciasComisionesDao(IIncidenciasComisionesDao incidenciasComisionesDao) {
		this.incidenciasComisionesDao = incidenciasComisionesDao;
	}

	public void setImportacionComisionesManager(ImportacionComisionesManager importacionComisionesManager) {
		this.importacionComisionesManager = importacionComisionesManager;
	}
	
}
