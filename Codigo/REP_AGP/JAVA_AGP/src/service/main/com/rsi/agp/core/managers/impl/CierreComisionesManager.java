package com.rsi.agp.core.managers.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.DatosConsultaComException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.comisiones.ICierreComisionesDao;
import com.rsi.agp.dao.models.comisiones.IInformesMediadoresDao;
import com.rsi.agp.dao.models.comisiones.IRgaComisionesDao;
import com.rsi.agp.dao.models.comisiones.IRgaComisionesPendientesDao;
import com.rsi.agp.dao.models.comisiones.IRgaUnifComisionesDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.EntidadesOperadoresInforme;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.InformeMediadores;
import com.rsi.agp.dao.tables.comisiones.ReportCierre;
import com.rsi.agp.dao.tables.comisiones.RgaComisiones;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeColaboradores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsFacturacion;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsFamLinEnt;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsImpagados2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsRGA2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeCorredores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeDetMediador2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeEntidades2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeEntidadesOperadores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeTotMediador2015;
import com.rsi.agp.dao.tables.commons.Usuario;


public class CierreComisionesManager implements IManager{
	private static final Log logger = LogFactory.getLog(CierreComisionesManager.class);
	private ICierreComisionesDao cierreComisionesDao;
	private IRgaComisionesDao rgaComisionesDao;
	private IInformesMediadoresDao informesMediadoresDao;
	private IRgaComisionesPendientesDao rgaComisionesPendientesDao;	
	private InformesExcelManager informesExcelManager;
	private IRgaUnifComisionesDao rgaUnifComisionesDao;
			
	public static final Character FICHERO_COMISIONES = 'C';
	public static final Character FICHERO_IMPAGADOS = 'I';
	public static final Character FICHERO_DEUDA = 'D';
	public static final Character FICHERO_UNIFICADO = 'U';
	
	/**
	 * Metodo que cierra las fases pendientes de cierre para una fecha
	 * @param fechaCierre
	 * @param usuario
	 * @throws DAOException,Exception 
	 */
	public HashMap<String,Object> cerrarPeriodo(Date fechaCierre,Usuario usuario) throws DAOException,Exception {
		logger.debug("CierreComisonesManager - INICIO cerrarPeriodo");
		Cierre cierre = null;
		HashMap<String,Object> params = new HashMap<String,Object>(); 
		boolean cerradoClasico = false;
		boolean cerradoUnificado = false;
		try {	
			
			cierre = new Cierre();
			cierre.setFechacierre(fechaCierre);
			cierre.setUsuario(usuario.getCodusuario());
			cierre.setPeriodo(new Date());
			
			cierre = (Cierre) cierreComisionesDao.saveOrUpdate(cierre);
			
			// generamos los datos de los ficheros con formato clasicos
			cerradoClasico = cerrarFasesFormatoClasico (cierre);
			
			// generamos los datos de los ficheros con Formato unificado
			cerradoUnificado = cerrarFasesFormatoUnificado (cierre);
			
			logger.debug("CierreComisonesManager - FIN cerrarPeriodo");
			
			params.put("cerradoClasico", cerradoClasico);
			params.put("cerradoUnificado", cerradoUnificado);
			params.put("cierre", cierre);
			
			return params;
		} catch (DatosConsultaComException Datos) {
			logger.error("Se ha producido un error al generar los datos para la consulta de Comisiones: " + Datos);
			throw new DatosConsultaComException ("Se ha producido un error al generar los datos para la consulta de Comisiones" , Datos);	
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al cerrar el periodo: " + dao);
			throw new Exception ("Se ha producido un error al cerrar el periodo" , dao);
		}catch (Exception e){
			logger.info("Error generico al cerrar el periodo: " + e);
			throw new Exception("Error generico al cerrar el periodo", e);
		}
		
	}
	/**
	 * 
	 * @param listFasesCierre
	 * @param cierre
	 * @throws DAOException
	 * @throws Exception
	 */
	private boolean cerrarFasesFormatoUnificado(final Cierre cierre) throws DAOException,Exception {
		try {	
			
			List<FicheroUnificado> listFasesUnifCierre = null;	
			listFasesUnifCierre = this.obtenerFasesUnificadasAceptadas(cierre.getFechacierre(), cierre.getId());
			logger.debug("Se han encontrado " + listFasesUnifCierre.size() + " sin cierre");
			
			if (listFasesUnifCierre.size()>0){
				logger.info("Se han encontrado " + listFasesUnifCierre.size() + " sin cierre de comisiones unificado");
				// primero generamos los datos, por si falla este proceso que no se guarde el cierre
				rgaUnifComisionesDao.generaDatosMediadores2015(listFasesUnifCierre);
			
				/* Formato unificado:  
				 * se recorren los ficheros de cada fase y se asigna la fecha cierre
				 * se asigna el cierre a todas las fases de cada fichero
				 */
				
				for(FicheroUnificado fi: listFasesUnifCierre){			
					fi.setFechaCierre(cierre.getFechacierre());			
					for (FaseUnificado f : fi.getFases()) {
						f.setCierre(cierre);
					}
				}
				logger.info("salvamos la listaFasesUnificadasCierre");
				
				for (FicheroUnificado faux : listFasesUnifCierre){
					faux.setFechaCierre(cierre.getFechacierre());
					cierreComisionesDao.saveOrUpdate(faux);
					cierreComisionesDao.evict(faux);
				}
				return true;
			}else{
				logger.info("No se han encontrado fases sin cierre de comisiones unificado");
				return false;
			}
			
		} catch (DatosConsultaComException Datos) {
			logger.error("Se ha producido un error al generar los datos Comisiones (FormatoUnificado): " + Datos);
			throw new DatosConsultaComException ("Se ha producido un error al generar los datos de Comisiones(FormatoUnificado)" , Datos);	
		}catch (Exception e){
			logger.info("Error generico (FormatoUnificado): " + e);
			throw new Exception("Error generico (FormatoUnificado)", e);
		}
	}
	/**
	 * Genera los datos de comisiones y los informes (FormatoClasico)
	 * @param listFasesCierre
	 * @param cierre
	 * @return 
	 * @throws DAOException
	 * @throws Exception
	 */
	private boolean cerrarFasesFormatoClasico(Cierre cierre)throws DAOException,Exception {
		
		List<Object> listFasesCierre = null;	
		List<Fase> listFasesCierreAux = new ArrayList<Fase>();
		try {	
			listFasesCierre = this.obtenerFasesAceptadas(cierre.getFechacierre());
			
			if (listFasesCierre.size()>0){
				
				for(Object obj: listFasesCierre){
					if (obj instanceof Fase){
						Fase f = (Fase)obj;
						f.setCierre(cierre);
						listFasesCierreAux.add(f);
					}else if (obj instanceof FaseUnificado){
						FaseUnificado fu = (FaseUnificado)obj;
						fu.setCierre(cierre);
						fu.getFichero().setFechaCierre(cierre.getFechacierre());
						cierreComisionesDao.saveOrUpdate(fu);
					}
				}
				
				
				logger.info("Se han encontrado " + listFasesCierre.size() + " sin cierre de comisiones Clasico");
				
				logger.info("salvamos la listaFasesCierre");
				cierreComisionesDao.saveOrUpdateList(listFasesCierreAux);
				
				logger.info("generamos los datosRGAComisiones");
				rgaComisionesDao.generarDatosRgaComisiones(listFasesCierreAux);
				
				logger.info("generamos los generarDatosRgaComisionesPendientes");
				rgaComisionesPendientesDao.generarDatosRgaComisionesPendientes(listFasesCierreAux);
				
				logger.info("generamos los datosInformesMediadores");
				informesMediadoresDao.generarDatosInformeMediadores(listFasesCierreAux);
				
				try {
					logger.debug("Actualizamos la tabla de Informes de Comisiones");
					cierreComisionesDao.actualizarInformesFicheroComisiones(cierre.getId());
					
				}catch (Exception e) {
					logger.error("Error al actualizar la Tabla Informes Comosiones",e);
					throw new DatosConsultaComException("Error al generar los datos para la consulta de Comisiones", e);
				} 
				return true;
			}else{
				logger.info("No se han encontrado fases sin cierre de comisiones Clasico");
				return false;
			}
		} catch (DatosConsultaComException Datos) {
			logger.error("Se ha producido un error al generar los datos Comisiones (FormatoClasico): " + Datos);
			throw new DatosConsultaComException ("Se ha producido un error al generar los datos de Comisiones(FormatoClasico)" , Datos);	
		}catch (Exception e){
			logger.info("Error generico (FormatoClasico): " + e);
			throw new Exception("Error generico (FormatoClasico)", e);
		}
		
	}

	/**
	 * Metodo que devuelve cuantas fases estan sin cerrar
	 * @param fechaCierre 
	 * @return
	 * @throws BusinessException
	 */
	public int getFasesSinCierre(Date fechaCierre) throws BusinessException{
		logger.debug("init - getFasesSinCierre");
		List<Fase> fases = null;
		List<FaseUnificado> fasesUnif = null;
		int resultado = 0;
		HashMap< String, Object> aux = new HashMap<String, Object>();
		try {
			
			fases = cierreComisionesDao.listFasesSinCerrar(fechaCierre);
			fasesUnif = cierreComisionesDao.listFasesUnifSinCerrar(fechaCierre,null);
			
			for (Fase f:fases){
				aux.put(f.getFase()+"-"+f.getPlan(),f );
			}
			// al anadir las fases unificadas si hay alguna con la misma 
			// clave, la sobreescribe y asi no habria repetidas al devolver el size
			for (FaseUnificado fu:fasesUnif){
				aux.put(fu.getFase()+"-"+fu.getPlan(),fu );
			}
			
			resultado = aux.size();
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar las fases que no estan cerradas:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar las fases que no estan cerradas" , dao);
		}
		logger.debug("end - getFasesSinCierre");
		return resultado;
	}
	
	/**
	 * Metodo que comrpueba si el periodo de cierre actual, fue cerrado previamente
	 * @param fechacierre
	 * @return
	 * @throws BusinessException
	 */
	public boolean periodoCerrado(Date fechacierre) throws BusinessException {
		logger.debug("init - periodoCerrado");
		Integer res = null;
		try {
			
			res = cierreComisionesDao.periodoCerrado(fechacierre);
			
			logger.debug("end - periodoCerrado");
			if(res == 0)
				return false;
			else
				return true;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar si el periodo de cierre fue cerrado previamente:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar si el periodo de cierre fue cerrado previamente" , dao);
		}
	}
	
	/**
	 * metodo que comprueba si por cada fase del fichero fueron cargados comisiones
	 * @param fechaCierre 
	 * @return
	 * @throws BusinessException 
	 */
	public boolean comisionesReglamentosEmitidosCargados(StringBuffer ficheros, Date fechaCierre) throws BusinessException {
		logger.debug("init - comisionesReglamentosEmitidosCargados");		
		
		List<Fase> listFases = null;
		boolean res = true;
		try {			
			listFases = cierreComisionesDao.listFasesSinCerrar(fechaCierre);
			ficheros.delete(0, ficheros.length());
			for(Fase fase:listFases){
				Set<Fichero> setFicheros = fase.getFicheros();
				boolean comisiones = false;
			
				if (setFicheros != null && setFicheros.size() > 0){
					for(Fichero fichero:setFicheros){ 
						if(fichero.getTipofichero().equals(FICHERO_COMISIONES) || fichero.getTipofichero().equals(FICHERO_IMPAGADOS)){
							comisiones = true;
						// Si no encuentra el de comisiones nos vamos a fase_unif a ver si esta ahi
						}else{
							FaseUnificado faseUnif = cierreComisionesDao.existeComisiomesUnif(fichero.getFase().getFase(), fichero.getFase().getPlan(), false);
							if (faseUnif!= null){
								comisiones = true;
							}
						}
					}
				} 
				if(!comisiones ){
					res = false;
					ficheros.append("<span style='color:black'>&#09;La fase " + fase.getPlan()+"-"+fase.getFase() + " contiene errores.</span><br>");
				}					
			}
			logger.debug("end - comisionesReglamentosEmitidosCargados");
			return res;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar si fueron cargados comisiones y reglamentos:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar si fueron cargados comisiones y reglamentos" , dao);
		}
	}
	
	/**
	 * metodo que comprueba si por cada fase de ficheros UNIFICADOS se ha cargado el de comisiones
	 * @param fechaCierre 
	 * @return boolean
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean unificadosComisionesCargados  (
			StringBuffer ficheros, Date fechaCierre) throws BusinessException {
		
		logger.debug("init - unificadosComisionesCargados");		
		
		List<FaseUnificado> listFases = null;
		boolean res = true;
		HashMap<String, List<FicheroUnificado>> aux = new HashMap<String, List<FicheroUnificado>>();
		List<FicheroUnificado> listFicheros = null;
		List<FicheroUnificado> listFicherosAux = null;
		boolean comisiones = false;
		try {			
			listFases = cierreComisionesDao.listFasesUnifSinCerrar(fechaCierre,null);
			
			ficheros.delete(0, ficheros.length());
			
			for(FaseUnificado fase:listFases){
				String key = fase.getPlan()+"-"+fase.getFase();
				
				if (aux.containsKey(key)){
					listFicherosAux = (List<FicheroUnificado>) aux.get(key);
					listFicherosAux.add(fase.getFichero());
					aux.put(key, listFicherosAux);
					
				}else{
					listFicheros = new  ArrayList<FicheroUnificado>();
					listFicheros.add(fase.getFichero());
					aux.put(fase.getPlan()+"-"+fase.getFase(), listFicheros);
				}
			}	
			Iterator<Entry<String, List<FicheroUnificado>>> it = aux.entrySet().iterator();
	    	while(it.hasNext()) {
	    		Map.Entry ent = (Map.Entry)it.next();
	    		List<FicheroUnificado> listF = (List<FicheroUnificado>)ent.getValue();
	    		comisiones = false;
    			
	    		for (FicheroUnificado f :listF){
    				if (f.getTipoFichero().equals(FICHERO_UNIFICADO) || f.getTipoFichero().equals(FICHERO_COMISIONES) || f.getTipoFichero().equals(FICHERO_IMPAGADOS) 
	    					|| f.getTipoFichero().equals(FICHERO_DEUDA)){
    					comisiones = true;
    				}
    			}
    			// si no ha encontrado el de comisiones
    			if(!comisiones ){
    				res = false;
    				ficheros.append("<span style='color:black'>&#09;La fase " + ent.getKey() + " contiene errores.</span><br>");
    			}	
	    	}
			
			logger.debug("end - unificadosComisionesCargados");
			return res;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar si fueron cargados comisiones y reglamentos:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar si fueron cargados comisiones y reglamentos" , dao);
		}
	}
	
	/**
	 * Metodo que comprueba si todos los ficheros por fase tienen fecha de aceptacion
	 * @param fecha
	 * @return
	 * @throws BusinessException
	 */
	public boolean ficherosNoAceptados(Date fecha,StringBuffer ficheros) throws BusinessException {
		logger.debug("init - ficherosAceptados");
		boolean Noaceptados = true;
		List<Fichero> listficheros = null;
		try {
			
			listficheros = cierreComisionesDao.ficherosNoAceptados(fecha);
			
			ficheros.delete(0, ficheros.length());
			for(Fichero f:listficheros){
				ficheros.append("<span style='color:black'>&#09;El fichero " + f.getNombrefichero() + " de la fase " +
						f.getFase().getFase() + " contiene errores.</span><br>");
			}
			
			if(listficheros.size() == 0)
				Noaceptados = false;
			else
				Noaceptados = true;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar que todos los ficheros estan no aceptados" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar que todos los ficheros estan no aceptados" , dao);
		}
		logger.debug("end - ficherosAceptados");
		return Noaceptados;
	}	
	/**
	 * Metodo que comprueba si todos los ficheros unificados tienen fecha de aceptacion
	 * @param fecha
	 * @return
	 * @throws BusinessException
	 */
	public boolean ficherosUnificadosNoAceptados(Date fecha,StringBuffer ficheros) throws BusinessException {
		logger.debug("init - ficherosUnificadosNoAceptados");
		boolean Noaceptados = true;
		List<FaseUnificado> listfases = null;
		try {
			
			listfases = cierreComisionesDao.ficherosUnificadosNoAceptados(fecha);
			
			ficheros.delete(0, ficheros.length());
			for(FaseUnificado f:listfases){
				
				ficheros.append("<span style='color:black'>&#09;El fichero " + f.getFichero().getNombreFichero() + " de la fase " +
						f.getFase() + " contiene errores.</span><br>");
			}
			
			if(listfases.size() == 0)
				Noaceptados = false;
			else
				Noaceptados = true;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar que todos los ficheros unificados estan no aceptados" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar que todos los ficheros unificados estan no aceptados" , dao);
		}
		logger.debug("end - ficherosUnificadosNoAceptados");
		return Noaceptados;
	}	
	
	
	public List<Object> obtenerFasesAceptadas(Date fecha) throws BusinessException {
		return obtenerFasesAceptadas(fecha, new HashMap<String, String>()); 
	}
	
	public List<Object> obtenerFasesAceptadas(Date fecha, Map<String,String> faseaux) throws BusinessException {
		logger.debug("init - ficherosAceptados");		
		List<Fase> listFases = null;
		List<Object> listFasesAceptadas = new ArrayList<Object>();
		
		try {
					
			listFases = cierreComisionesDao.listFasesSinCerrar(fecha);
			//ficheros.delete(0, ficheros.length());
			for(Fase fase:listFases){	
				logger.info("Id Fase: " + fase.getId());
				Set<Fichero> setFicheros = fase.getFicheros();
				boolean correcto = true;
				
				boolean comisiones = false;
				
				
				logger.info("Se va recorriendo cada fichero, y se comprueba si existe alguno de cada tipo y si estÃ¡n todos aceptados");
				for(Fichero fichero:setFicheros){ 					
					logger.info("Id Fichero: " + fichero.getId());
					 if(fichero.getTipofichero().equals(FICHERO_UNIFICADO) || fichero.getTipofichero().equals(FICHERO_COMISIONES) || fichero.getTipofichero().equals(FICHERO_IMPAGADOS)){
						comisiones = true;
					 // Si no encuentra el de comisiones nos vamos a fase_unif a ver si esta ahi y con fecha de aceptacion
					 }else{
						 FaseUnificado faseUnif = cierreComisionesDao.existeComisiomesUnif(fichero.getFase().getFase(), fichero.getFase().getPlan(), true);
						 // IGT: 20190516 SOLO COJEMOS LA FASE SI NO TIENE CIERRE YA ASOCIADO
						if (faseUnif != null && (faseUnif.getCierre() == null
								|| (faseUnif.getCierre() != null && faseUnif.getCierre().getId() == null))) {
							 comisiones = true;
							 listFasesAceptadas.add(faseUnif);
						 }
					 }
					if (fichero.getFechaaceptacion() != null){
						correcto = correcto && true;
					} else {
						correcto = false;
					}
				}
				
				if(comisiones && correcto){
					faseaux.put(fase.getPlan()+"-"+fase.getFase(), "<span style='color:black'>&#09;La fase " + fase.getPlan()+"-"+fase.getFase() + " se puede cerrar.</span><br>");
					listFasesAceptadas.add(fase);
				}
			}	
				
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar que todos los ficheros estan aceptados" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar que todos los ficheros estan aceptados" , dao);
		}
		return listFasesAceptadas;
	}
	
	public List<FicheroUnificado> obtenerFasesUnificadasAceptadas(Date fecha,Long idcierre) throws BusinessException {
		return obtenerFasesUnificadasAceptadas(fecha, idcierre,new HashMap<String, String>()); 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<FicheroUnificado> obtenerFasesUnificadasAceptadas(Date fecha,
			Long idcierre,Map<String,String> faseaux) throws BusinessException {
		logger.debug("init - ficherosAceptados");		
		List<FaseUnificado> listFases = null;
		List<FicheroUnificado> listFasesUnifAceptadas = new ArrayList<FicheroUnificado>();
		List<FicheroUnificado> listFicherosAux = null;
		HashMap<String, List<FicheroUnificado>> aux = new HashMap<String, List<FicheroUnificado>>();
		List<FicheroUnificado> listAux = null;
		List<FicheroUnificado> listFicheros = null;
		boolean comisiones = false;
		boolean correcto = false;
		FicheroUnificado fich = null;
		try {
					
			listFases = cierreComisionesDao.listFasesUnifSinCerrar(fecha,idcierre);
			//ficheros.delete(0, ficheros.length());
			
			logger.info("Se va recorriendo cada fichero, y se comprueba si el de comisiones y si esta n todos aceptados");
			for(FaseUnificado fase:listFases){
				String key = fase.getPlan()+"-"+fase.getFase();
				logger.debug("Fase: " + key);
				if (aux.containsKey(key)){
					listFicherosAux = new  ArrayList<FicheroUnificado>();
					listFicherosAux = (List<FicheroUnificado>) aux.get(key);
					listFicherosAux.add(fase.getFichero());
					aux.put(key, listFicherosAux);
					
				}else{
					listFicheros = new  ArrayList<FicheroUnificado>();
					listFicheros.add(fase.getFichero());
					aux.put(fase.getPlan()+"-"+fase.getFase(), listFicheros);
					
				}
			}	
			Iterator it = aux.entrySet().iterator();
	    	while(it.hasNext()) {
	    		
	    		Map.Entry ent = (Map.Entry)it.next();
	    		
	    		List<FicheroUnificado> listF = (List<FicheroUnificado>)ent.getValue();
	    		comisiones = false;
	    		correcto = true;
	    		listAux = new ArrayList<FicheroUnificado>();
	    		for (FicheroUnificado f :listF){
	    			fich = new FicheroUnificado();
	    			fich = f;
	    			if (f.getTipoFichero().equals(FICHERO_UNIFICADO)
	    					|| f.getTipoFichero().equals(FICHERO_COMISIONES) 
	    					|| f.getTipoFichero().equals(FICHERO_IMPAGADOS) 
	    					|| f.getTipoFichero().equals(FICHERO_DEUDA)){
    					comisiones = true;
    				}
    				if (f.getFechaAceptacion() != null){
    					correcto = correcto && true;
					} else {
						correcto = false;
					}
    				
					listAux.add (fich);
    			}
	    		if(comisiones && correcto){
	    			
	    			if (!faseaux.containsKey(ent.getKey().toString())){
	    				faseaux.put(ent.getKey().toString(), "<span style='color:black'>&#09;La fase " + ent.getKey() + " se puede cerrar.</span><br>");
	    			}
	    			for (FicheroUnificado faux :listAux) {
						listFasesUnifAceptadas.add(faux);
	    			}
				}	    		
    		}
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al comprobar que todos los ficheros unificados estan aceptados" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al comprobar que todos los ficheros unificados estan aceptados" , dao);
		}
		return listFasesUnifAceptadas;
	}
	
	public List<Fichero> getListaFicherosSinCierre() throws BusinessException {
		logger.debug("init - getFasesSinCierre");
		List<Fichero> ficheros = null;		
		try {
			
			ficheros = cierreComisionesDao.getFasesSinCierre();			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar las fases que no estan cerradas:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar las fases que no estan cerradas" , dao);
		}
		logger.debug("end - getFasesSinCierre");
		return ficheros;
	}	
	
	public List<ReportCierre> getListaInformesGenerados(final Long idCierre) throws BusinessException {
		logger.debug("init - getListaInformesGenerados");
		List<ReportCierre> ficheros = null;		
		try {
			
			ficheros = cierreComisionesDao.getListaInformesGenerados(idCierre);		
			logger.debug("Se han obtenido" + ficheros.size() + " informes");
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar la lista de informes de comisiones:" + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al recuperar la lista de informes de comisiones" , dao);
		}
		logger.debug("end - getListaInformesGenerados");
		return ficheros;
	}
	
	public boolean generarInformeExcelClasico(final Cierre cierre, final Usuario usuario) throws BusinessException{
		logger.info("CierreComisionesManager - Inicio generarInformeExcel");
		List<RgaComisiones> listTotalesMediador = new ArrayList<RgaComisiones>();
		List<RgaComisiones> listDetalleMediador = new ArrayList<RgaComisiones>();
		List<RgaComisiones> listInformeComisionesEntidades = new ArrayList<RgaComisiones>();
		List<ReciboImpagado> listInformeImpagados = new ArrayList<ReciboImpagado>();
		List<Colectivo> listcolectivos = new ArrayList<Colectivo>();
		List<InformeMediadores> listInformeMediadores = new ArrayList<InformeMediadores>();
		
		List<EntidadesOperadoresInforme> listEntidadesOperadores = new ArrayList<EntidadesOperadoresInforme>();
		
		HashMap<String,String> subEntMed = new HashMap<String,String>();
		HashMap<String,String> entMed = new HashMap<String,String>();
		HashMap<String,String> lineas = new HashMap<String,String>();
		try {		
			listDetalleMediador = informesExcelManager.getListaInformesMediador(cierre.getId());
			listInformeComisionesEntidades = informesExcelManager.getListaComisionesEntidades(cierre.getId());
			listInformeImpagados = informesExcelManager.getListaComisionesImpagados(cierre);
			listInformeMediadores = informesExcelManager.getListaMediadores(cierre.getFechacierre());
			listTotalesMediador = informesExcelManager.getListaTotalesMediador(cierre.getId());
			listEntidadesOperadores = informesExcelManager.getListaEntidadesOperadoras(cierre.getId());
			
			listcolectivos = informesExcelManager.getColectivos();
			subEntMed =informesExcelManager.getSubentidadesMediadoras();
			lineas = informesExcelManager.getLineas();
			entMed = informesExcelManager.getEntidadesMediadoras();
			
			informesExcelManager.generarInformeDetalleMediador(listDetalleMediador, cierre.getId(), cierre.getFechacierre(), subEntMed, lineas, usuario);
			informesExcelManager.generarInformeComisionesEntidades(listInformeComisionesEntidades, cierre.getId(), cierre.getFechacierre(), subEntMed,
					lineas, usuario);
			informesExcelManager.generarInformeImpagados(listInformeImpagados, cierre.getId(), cierre.getFechacierre(), entMed, listcolectivos, usuario);
			informesExcelManager.generarEntidadesOperadores(listEntidadesOperadores, entMed, cierre.getId(), cierre.getFechacierre(), usuario);
			informesExcelManager.generarInformeMediadores(listInformeMediadores, cierre.getId(), cierre.getFechacierre(), usuario);
			informesExcelManager.generarTotalesMediador(listTotalesMediador, lineas, subEntMed, cierre.getId(), cierre.getFechacierre(), usuario);
			logger.info("CierreComisionesManager - Fin generarInformeExcel");
			
			return true;
			
		} catch (BusinessException e){
			logger.error("Se ha producido un error al guardar el fichero excel generado:" + e.getMessage());
			return false;
		}		
		
	}
	
	/*
	 * Generación de informes 2015+
	 */
	public boolean generarInformeExcel2015(final Long idCierre, final Date fechaCierre, final Date periodo, final Usuario usuario)
			throws BusinessException {
		logger.info("CierreComisionesManager - Inicio generarInformeExcel2015");
		
		List<InformeTotMediador2015> listTotalesMediador2015 = new ArrayList<InformeTotMediador2015>();
		List<InformeDetMediador2015> listDetalleMediador2015 = new ArrayList<InformeDetMediador2015>();
		List<InformeEntidades2015> listInformeEntidades2015 = new ArrayList<InformeEntidades2015>();
		List<InformeComsImpagados2015> listInformeImpagados2015 = new ArrayList<InformeComsImpagados2015>();
		List<InformeComsRGA2015> listInformeComsRGA2015 = new ArrayList<InformeComsRGA2015>();
		List<InformeEntidadesOperadores2015> listEntidadesOperadores2015 = new ArrayList<InformeEntidadesOperadores2015>();
		
		List<InformeColaboradores2015> listInformeColaboradores2015 = new ArrayList<InformeColaboradores2015>();
		List<InformeCorredores2015> listInformeCorredores2015 = new ArrayList<InformeCorredores2015>();
		List<InformeComsFamLinEnt> listInformeComsFamLinEnt = new ArrayList<InformeComsFamLinEnt>();
		List<InformeComsFacturacion> listInformeComsFacturacion = new ArrayList<InformeComsFacturacion>();

		HashMap<String, String> lineas = new HashMap<String, String>();

		try {
			listInformeEntidades2015 = informesExcelManager.getListaComisionesEntidades2015();
			listDetalleMediador2015 = informesExcelManager.getListaInformesMediador2015();
			listEntidadesOperadores2015 = informesExcelManager.getListaEntidadesOperadores2015();
			listTotalesMediador2015 = informesExcelManager.getListaTotalesMediador2015();
			listInformeImpagados2015 = informesExcelManager.getListaComisionesImpagados2015(idCierre);
			listInformeComsRGA2015 = informesExcelManager.getListaComsRGA2015();
			listInformeComsFamLinEnt = informesExcelManager.getListaComsFamLinEnt();
			listInformeComsFacturacion = informesExcelManager.getListaComsFacturacion();
			listInformeColaboradores2015 = informesExcelManager.getListaColaboradores2015();
			listInformeCorredores2015 = informesExcelManager.getListaCorredores2015();
			
			lineas = informesExcelManager.getLineas();

			informesExcelManager.generarEntidadesOperadores2015(listEntidadesOperadores2015, idCierre, fechaCierre,usuario);
			informesExcelManager.generarInformeComisionesEntidades2015(listInformeEntidades2015, idCierre, fechaCierre,lineas, usuario);
			informesExcelManager.generarInformeDetalleMediador2015(listDetalleMediador2015, idCierre, fechaCierre,lineas, usuario); 
			informesExcelManager.generarTotalesMediador2015(listTotalesMediador2015, lineas, idCierre, fechaCierre, usuario);
			informesExcelManager.generarInformeImpagados2015(listInformeImpagados2015, idCierre, fechaCierre, usuario);
			informesExcelManager.generarInformeComsRGA2015(listInformeComsRGA2015, idCierre, fechaCierre, lineas,usuario);
			informesExcelManager.generarInformeComsFamLinEnt(listInformeComsFamLinEnt, idCierre, fechaCierre, usuario);
			informesExcelManager.generarInformeComsFacturacion(listInformeComsFacturacion, idCierre, fechaCierre, usuario);	
			informesExcelManager.generarInformeColaboradores2015(listInformeColaboradores2015, idCierre, fechaCierre, usuario);
			informesExcelManager.generarInformeCorredores2015(listInformeCorredores2015, idCierre, fechaCierre, usuario);
			
		
			
			logger.info("CierreComisionesManager - Fin generarInformeExcel2015");
			return true;

		} catch (BusinessException e) {
			logger.error("Se ha producido un error al guardar el fichero excel2015 generado:" + e.getMessage());
			return false;
		}
	}
	
	public ReportCierre getInformeById(Long idInforme) throws BusinessException{
		try{
			
			return cierreComisionesDao.getInformeById(idInforme);
		
		}catch (DAOException daoe){
			throw new BusinessException ("Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}	
	
	public ReportCierre getContenidoInforme(Long idInforme) throws BusinessException{
		try{
			return cierreComisionesDao.getContenidoInforme(idInforme);	
		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error durante la lectura del informe", daoe);
		}		
	}	
	
	public Cierre getCierreByFecha(Date fechaCierre) throws BusinessException {
		try{
			return cierreComisionesDao.getCierreByFecha(fechaCierre);	
		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error al obtener el cierre de un fichero", daoe);
		}	
	}
	
	public void borrarInformesCierre(final Long idCierre) throws BusinessException{
		List<ReportCierre> listaInformes = null;
		try{
			logger.debug("Procedemos a borrar los informes de cierre anteriores...");
			logger.debug("IDCIERRE: " + idCierre);
			listaInformes = this.getListaInformesGenerados(idCierre);
			for (ReportCierre rc : listaInformes) {
				logger.debug("Borramos: " + rc.getId());
				cierreComisionesDao.delete(rc);
			}
			cierreComisionesDao.deleteAll(listaInformes);	

		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error al borrar los informes", daoe);
		}	
		
	}
	
	/**
	 * Obtiene el id del cierre más reciente
	 * @return
	 * @throws BusinessException
	 */
	public Long obtenerIdCierreMasReciente() throws BusinessException{
		
		Long idCierre = null;
		try{
			idCierre = cierreComisionesDao.obtenerIdCierreMasReciente();
		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error al obtener el cierre más reciente", daoe);
		}
		return idCierre;
	}
	
	
	/**
	 * Borra un cierre dado su id
	 * @param idCierre
	 * @return borradoInformesOk
	 * @throws BusinessException
	 */
	public boolean borrarCierrePorId(Long idCierre) throws BusinessException{

		boolean borradoInformesOk = false;
		try{
			Cierre cierre = (Cierre) cierreComisionesDao.getObject(Cierre.class, idCierre);
			borradoInformesOk = cierreComisionesDao.borrarInformesComisionesByIdCierre(idCierre);	
		    cierreComisionesDao.borrarRgaUnifMediadores(cierre.getFechacierre());
			cierreComisionesDao.borrarCierrePorId(cierre);		
		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error al borrar el cierre", daoe);
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error al borrar el cierre", e);
		}
		return borradoInformesOk;
		
	}
	/*public void updateInformesGenerados(Cierre cierre, boolean informesGenerados) throws BusinessException{
		
		try{
			cierreComisionesDao.updateInformesGenerados(cierre,informesGenerados);	
		
		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error al borrar los informes", daoe);
		}	
	}

	public String isInformesCerrados(Long idCierre) throws BusinessException{
		try{
			
			return cierreComisionesDao.isInformesCerrados(idCierre);	
		
		}catch (DAOException daoe){
			throw new BusinessException("Se ha producido un error al borrar los informes", daoe);
		}	
		
	}
*/
	public void setCierreComisionesDao(ICierreComisionesDao cierreComisionesDao) {
		this.cierreComisionesDao = cierreComisionesDao;
	}

	public void setRgaComisionesDao(IRgaComisionesDao rgaComisionesDao) {
		this.rgaComisionesDao = rgaComisionesDao;
	}



	public void setInformesExcelManager(InformesExcelManager informesExcelManager) {
		this.informesExcelManager = informesExcelManager;
	}

	public void setRgaComisionesPendientesDao(IRgaComisionesPendientesDao rgaComisionesPendientesDao) {
		this.rgaComisionesPendientesDao = rgaComisionesPendientesDao;
	}

	public void setInformesMediadoresDao(IInformesMediadoresDao informesMediadoresDao) {
		this.informesMediadoresDao = informesMediadoresDao;
	}
	public void setRgaUnifComisionesDao(IRgaUnifComisionesDao rgaUnifComisionesDao) {
		this.rgaUnifComisionesDao = rgaUnifComisionesDao;
	}
	

	
}