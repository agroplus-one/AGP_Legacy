package com.rsi.agp.core.managers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.XMLValidationException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.ComisionesConstantes;
import com.rsi.agp.core.util.XmlComisionesValidationUtil;
import com.rsi.agp.core.util.ZipUtil;
import com.rsi.agp.core.webapp.action.ImportacionComisionesController;
import com.rsi.agp.dao.models.IDatabaseManager;
import com.rsi.agp.dao.models.comisiones.IImportacionFicherosComisionesDao;
import com.rsi.agp.dao.models.comisiones.IImportacionFicherosDeudaAplazadaDao;
import com.rsi.agp.dao.models.comisiones.IImportacionFicherosEmitidosDao;
import com.rsi.agp.dao.models.comisiones.IImportacionFicherosImpagadosDao;
import com.rsi.agp.dao.models.comisiones.IImportacionFicherosReglamentoDao;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroContenido;
import com.rsi.agp.dao.tables.comisiones.FicheroIncidencia;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultContenido;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultIncidencias;
import com.rsi.agp.dao.tables.commons.Usuario;

/**
 * @author U028893 t-systems
 * 
 * Encargada de las importaciones de fichero online via web.
 * Se apoya para el parseo, carga en bbdd y validación en clases comunes
 * para el proceso batch.
 * 
 * */

public class ImportacionComisionesManager implements IManager {
	
	// Constantes
	private static final String IDFICHERO2 = "    IDFICHERO: ";
	private static final String IDFICHERO = "IDFICHERO";
	private static final String END_IMPORTAR_FICHERO_EMITIDOS = "end - importarFicheroEmitidos";
	private static final String FAILED = "FAILED";
	private static final String PROGRESS = "progress";
	private static final String PROGRESS_STATUS = "progressStatus";
	private static final String UPLOADING = "UPLOADING";
	private static final String SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO2 = "Se ha producido un error al recuperar el fichero importado";
	private static final String SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO = "Se ha producido un error al recuperar el fichero importado :";

	private static final Log LOGGER = LogFactory.getLog(ImportacionComisionesManager.class);
	
	private IImportacionFicherosImpagadosDao importacionFicheroImpagadosDao;
	private IImportacionFicherosComisionesDao importacionFicheroComisionesDao;
	private IImportacionFicherosReglamentoDao importacionFicheroReglamentoDao;
	private IImportacionFicherosEmitidosDao importacionFicherosEmitidosDao;
	private IImportacionFicherosDeudaAplazadaDao importacionFicherosDeudaAplazadaDao;

	private IDatabaseManager databaseManager;

	public final static int FICHERO_COMISIONES=1;
	public final static int FICHERO_IMPAGADOS=2;
	public final static int FICHERO_REGLAMENTO=3;
	public final static int FICHERO_EMITIDOS=4;
	public final static int FICHERO_DEUDA=5;
	
	public static final String ESTADO_CORRECTO = "Correcto";
	public static final String ESTADO_AVISO = "Aviso";
	public static final String ESTADO_ERRONEO = "Erroneo";
	public static final String ESTADO_CARGADO = "Cargado";
	
	/**
	 * Metodo que devuelve el listado completo de ficheros importados
	 * @param ffcb
	 * @param estado 
	 * @return
	 * @throws BusinessException
	 */
	public List<Fichero> listImportaciones(FormFicheroComisionesBean ffcb, Character tipo, String estado) throws BusinessException {
		LOGGER.debug("init - listImportaciones");
		List<Fichero> list = null;
		List<Fichero> listRes = new ArrayList<Fichero>();
		int erroneo=0;
		int aviso =0;
		int correcto=0;
		String estadoFichero;
		try {		
			list = importacionFicheroComisionesDao.list(ffcb,tipo);
			
			//Se realiza el filtro por estado
			if (estado.equals("")){
				listRes = list;				
			} else {
				for (int i=0;i<list.size();i++){
					
					Set<FicheroIncidencia> fIncidencia = list.get(i).getFicheroIncidencias();
					Iterator<FicheroIncidencia> iter = fIncidencia.iterator();
					 while (iter.hasNext()) {
				        FicheroIncidencia incidencia = iter.next();
				          
						if (incidencia.getEstado().equals(ESTADO_ERRONEO)){
							erroneo++;
						} else if (incidencia.getEstado().equals(ESTADO_AVISO)){
							aviso++;
						}else if (incidencia.getEstado().equals(ESTADO_CORRECTO)){
							correcto++;
						}
					 }	
					 if (erroneo>0)
						 estadoFichero = ESTADO_ERRONEO;
					 else if (aviso>0)
						 estadoFichero = ESTADO_AVISO;
					 else if (correcto>0)
						 estadoFichero = ESTADO_CORRECTO;
					 else{
						 estadoFichero=ESTADO_CARGADO;
					 }
					 if (estadoFichero.equals(estado)){
						listRes.add(list.get(i));
					 }
					 erroneo=0;
					 aviso=0;
					 correcto=0;
				}		
			}
			LOGGER.debug("end - listImportaciones");
			return listRes;
			
		} catch (DAOException dao) {
			LOGGER.debug("Se ha producido un error al recuperar el listado de importaciones :" + dao.getMessage());
			throw new BusinessException("Se ha producido un error al recuperar el listado de importaciones", dao);
		}	
		
	}
	
	public List<FicheroMult> listImportacionesDeuda(FormFicheroComisionesBean ffcb, String estado) throws BusinessException {
		LOGGER.debug("init - listImportacionesDeuda");
		int erroneo=0;
		int aviso =0;
		int correcto=0;
		String estadoFichero;
		List<FicheroMult> list = null;
		List<FicheroMult> listRes = new ArrayList<FicheroMult>();
		try {
			list = importacionFicheroComisionesDao.listDeuda(ffcb);
			
			//Se realiza el filtro por estado
			if (estado.equals("")){
				listRes = list;				
			} else {
				for (int i=0;i<list.size();i++){
					
					Set<FicheroMultIncidencias> fIncidencia = list.get(i).getFicheroMultIncidenciases();
					Iterator<FicheroMultIncidencias> iter = fIncidencia.iterator();
					 while (iter.hasNext()) {
						 FicheroMultIncidencias incidencia = iter.next();
				          
						if (incidencia.getEstado().equals(ESTADO_ERRONEO)){
							erroneo++;
						} else if (incidencia.getEstado().equals(ESTADO_AVISO)){
							aviso++;
						}else if (incidencia.getEstado().equals(ESTADO_CORRECTO)){
							correcto++;
						}
					 }	
					 if (erroneo>0)
						 estadoFichero = ESTADO_ERRONEO;
					 else if (aviso>0)
						 estadoFichero = ESTADO_AVISO;
					 else if (correcto>0)
						 estadoFichero = ESTADO_CORRECTO;
					 else{
						 estadoFichero=ESTADO_CARGADO;
					 }
					 if (estadoFichero.equals(estado)){
						listRes.add(list.get(i));
					 }
					 erroneo=0;
					 aviso=0;
					 correcto=0;
				}		
			}
			LOGGER.debug("end - listImportacionesDeuda");
		} catch (DAOException e) {
			LOGGER.error("Excepcion : ImportacionComisionesManager - listImportacionesDeuda", e);
		}
		return listRes;
		
	}
	
	/**
	 * Metodo que devuelve un objeto FicheroContenido
	 * @param idfichero
	 * @return
	 * @throws BusinessException
	 */
	public FicheroContenido getFicheroContenido(Long idfichero) throws BusinessException{
		LOGGER.debug("init - getFicheroContenido");
		FicheroContenido fichero = null;
		try {
			fichero = (FicheroContenido) importacionFicheroComisionesDao.getFicheroContenido(idfichero);
			
		} catch (DAOException dao) {
			LOGGER.debug(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO + dao.getMessage());
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO2, dao);
		}
		LOGGER.debug("end - getFicheroContenido");
		return fichero;
	}
	
	/**
	 * Metodo que devuelve un objeto FicheroMultContenido
	 * @param idfichero
	 * @return
	 * @throws BusinessException
	 */
	public FicheroMultContenido getFicheroMultContenido(Long idfichero) throws BusinessException{
		LOGGER.debug("init - getFicheroMult");
		FicheroMultContenido fichero = null;
		try {
			fichero = (FicheroMultContenido) importacionFicheroComisionesDao.getFicheroMultContenido(idfichero);
			
		} catch (DAOException dao) {
			LOGGER.debug(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO + dao.getMessage());
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO2, dao);
		}
		LOGGER.debug("end - getFicheroMult");
		return fichero;
	}
	/**
	 * Metodo que devuelve un objeto Fichero
	 * @param idfichero
	 * @return
	 * @throws BusinessException
	 */
	public Fichero getFichero(Long idfichero) throws BusinessException{
		LOGGER.debug("init - getFichero");
		Fichero fichero = null;
		try {
			fichero = (Fichero) this.importacionFicheroComisionesDao.getObject(Fichero.class, idfichero);
			
		} catch (Exception e) {
			LOGGER.debug(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO + e.getMessage());
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO2, e);
		}
		LOGGER.debug("end - getFichero");
		return fichero;
	}
	
	/**
	 * Metodo que elimina de la BBDD un fichero
	 * @param fichero
	 * @throws BusinessException
	 */
	public void borrarFichero(Fichero fichero,Usuario usuario) throws BusinessException {
		LOGGER.debug("init - borrarFichero");
		try {
			
			Fase fase = fichero.getFase(); //ASF - 19/9/2013 importacionFicheroComisionesDao.getFaseFichero(fichero); 
			int countFase = importacionFicheroComisionesDao.obtenerFicherosFase(fichero.getFase().getId());
			//TMR 30-05-2012 Facturacion
			importacionFicheroComisionesDao.deleteFacturacion(fichero, usuario);
			
			//DAA 27/09/2013 Si se borra el fichero debemos borrar los datos en la tabla de informes
			//TIPO_G es el de recibidos
			LOGGER.debug("Tipo fichero" + fichero.getTipofichero());
			if(ComisionesConstantes.TiposFichero.TIPO_G.equals(fichero.getTipofichero().toString())){
				importacionFicheroComisionesDao.deleteFromTablaInformesRecibos(fichero.getId(), fase.getPlan());
			}else{
				LOGGER.debug("No es tipo recibo");
			}
			
			if (countFase <= 1){				
				importacionFicheroComisionesDao.delete(fase);
			}
			
		} catch (DAOException dao) {
			LOGGER.debug(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO + dao.getMessage());
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_EL_FICHERO_IMPORTADO2, dao);
		}
		LOGGER.debug("end - borrarFichero");
	}
	
	/**
	 * Metodo que determina si un fichero tiene fecha de cierre
	 * @param fichero
	 * @return
	 */
	public boolean ficheroCierre(Fichero fichero) {
		LOGGER.debug("init - ficheroSinCierre");
		boolean tieneCierre = false;
		Fase fase = fichero.getFase(); //ASF - 19/9/2013 importacionFicheroComisionesDao.getFaseFichero(fichero);
		if(fase.getCierre() != null && fase.getCierre().getId() != null)
			tieneCierre = true;
		else 
			tieneCierre = false;

		LOGGER.debug("end - ficheroSinCierre");
		return tieneCierre;
	}
	
	/**
	 * Metodo que comprueba si un fichero fue importado previamente
	 * @param ffcb
	 * @return
	 * @throws BusinessException
	 */
	public boolean ficheroImportado(FormFicheroComisionesBean ffcb) throws BusinessException {
		try {
			
			return importacionFicheroComisionesDao.ficheroImportado(ffcb.getFile().getOriginalFilename()) > 0;
			
		} catch (DAOException dao) {
			LOGGER.debug("Se ha producido un error al importar, fichero duplicado:" + dao.getMessage());
			throw new BusinessException("Se ha producido un error al importar, fichero duplicado", dao);
		}
	}
	
	/**
	 * Metodo que gestiona la creacion y llamada a las importaciones de los distintos tipos de ficheros
	 * @param ffcb
	 * @param tipoFichero
	 * @param usuario
	 * @return 
	 * @throws BusinessException
	 */
	public Map<String,Long> crearFicheroImportado(FormFicheroComisionesBean ffcb,Character tipoFichero,Usuario usuario, HttpServletRequest request) throws BusinessException{
		LOGGER.debug("init - crearFicheroImportado");
		File newfile = null;
		Long id = null;
		Map<String,Long> mapaIdAndError = new HashMap<String,Long>();
		
		try {
			LOGGER.debug("creamos un FILE temporal");
			newfile = File.createTempFile("xml", "temp");
			
			LOGGER.debug("transformamos nuestro multipartfile a file");
			ffcb.getFile().transferTo(newfile);
			
			if (request != null) {
				request.getSession().setAttribute(PROGRESS_STATUS, UPLOADING);
				request.getSession().setAttribute(PROGRESS,10);
			}
			
			if(tipoFichero.equals(new Character('C'))){
				
				LOGGER.debug("comienzo de la importacion del fichero de comisiones");
				id = this.importarFicheroComisiones(newfile,usuario,tipoFichero,ffcb.getFile().getOriginalFilename(),request);
				mapaIdAndError.put(ImportacionComisionesController.CLAVE_ID_FICHERO, id);
				
			}else if (tipoFichero.equals(new Character('I'))) {
				
				LOGGER.debug("comienzo de la importacion del fichero de impagos");
				id = this.importarFicheroImpagados(newfile,usuario,tipoFichero,ffcb.getFile().getOriginalFilename(),request);
				mapaIdAndError.put(ImportacionComisionesController.CLAVE_ID_FICHERO, id);

			}else if (tipoFichero.equals(new Character('R'))) {
				
				LOGGER.debug("comienzo de la importacion del fichero de reglamentos");
				id = this.importarFicheroReglamento(newfile,usuario,tipoFichero,ffcb.getFile().getOriginalFilename(),request);
				mapaIdAndError.put(ImportacionComisionesController.CLAVE_ID_FICHERO, id);
				
			}else if (tipoFichero.equals(new Character('G'))) {
				
				if (request != null) {
					request.getSession().setAttribute(PROGRESS_STATUS, UPLOADING);
					request.getSession().setAttribute(PROGRESS,60);
				}
				
				LOGGER.debug("comienzo de la importacion del fichero de recibos emitidos");
				mapaIdAndError = this.importarFicheroEmitidos(newfile,usuario,tipoFichero,ffcb.getFile().getOriginalFilename(),request);
			
				if (request != null) {
					request.getSession().setAttribute(PROGRESS_STATUS, UPLOADING);
					request.getSession().setAttribute(PROGRESS,85);
				}
				
			}else if (tipoFichero.equals(new Character('D'))) {
				
				LOGGER.debug("comienzo de la importacion del fichero de deuda aplazada");
				mapaIdAndError = this.importarFicheroDeudaAplazada(newfile,usuario,tipoFichero,ffcb.getFile().getOriginalFilename(),request);
			
			} 
			
			
		} catch (IOException ex) {
			if (request != null) request.getSession().setAttribute(PROGRESS_STATUS, FAILED);
			LOGGER.debug("Se ha producido un error al crear el fichero temporal :" + ex.getMessage());
			throw new BusinessException("Se ha producido un error al crear el fichero temporal", ex);
		} catch (DAOException dao) {
			if (request != null) request.getSession().setAttribute(PROGRESS_STATUS, FAILED);
			LOGGER.debug("Se ha producido un error al importar el fichero :" + dao.getMessage());
			throw new BusinessException("Se ha producido un error al importar el fichero", dao);
		} catch (BusinessException be) {
			if (request != null) request.getSession().setAttribute(PROGRESS_STATUS, FAILED);
			LOGGER.debug("Se ha producido un error al importar el fichero :" + be.getMessage());
			throw new BusinessException("Se ha producido un error al importar el fichero", be);
		}
		LOGGER.debug("end - crearFicheroImportado");
		return mapaIdAndError;
	}
	
	private Map<String,Long> importarFicheroDeudaAplazada(File ficheroImportacion, Usuario usuario,Character tipo, String nombre, HttpServletRequest request) throws DAOException, BusinessException{
		LOGGER.debug("init - importarFicheroDeudaAplazada");

		Map<String,Long> mapaIdAndError = new HashMap<String,Long>();
		
		es.agroseguro.recibos.comisionesCobroDeudaAplazada.FasesDocument fase = null;
		fase = (es.agroseguro.recibos.comisionesCobroDeudaAplazada.FasesDocument)realizarValidacion(ficheroImportacion, FICHERO_DEUDA);
		
		request.getSession().setAttribute(PROGRESS_STATUS, UPLOADING);
		request.getSession().setAttribute(PROGRESS,55);
		
		Long id = importacionFicherosDeudaAplazadaDao.importarYValidarFicheroDeudaAplazada(fase, usuario, tipo, nombre, request);
		mapaIdAndError.put(ImportacionComisionesController.CLAVE_ID_FICHERO, id);
		
		try{
			// LLamada a un PL para hacer la validacion
			this.validarFicheroDeudaAplazada(id);
		}catch (Exception e) {
			LOGGER.error("Error al validar fichero deuda aplazada",e);
			mapaIdAndError.put(ImportacionComisionesController.CLAVE_ID_ERROR, ImportacionComisionesController.WARN_ID_VALIDACION);
			LOGGER.debug(END_IMPORTAR_FICHERO_EMITIDOS);
			return mapaIdAndError;
		}

		LOGGER.debug("end - importarFicheroDeudaAplazada");
		return mapaIdAndError;
	}
	

	private Map<String,Long> importarFicheroEmitidos(File ficheroImportacion, Usuario usuario,Character tipo, String nombre, HttpServletRequest request) throws DAOException, BusinessException{
		LOGGER.debug("init - importarFicheroEmitidos");

		Map<String,Long> mapaIdAndError = new HashMap<String,Long>();
		
		es.agroseguro.recibos.emitidos.FaseDocument fase=null;
		fase = (es.agroseguro.recibos.emitidos.FaseDocument)realizarValidacion(ficheroImportacion, FICHERO_EMITIDOS);
		
		if (request != null) {
			request.getSession().setAttribute(PROGRESS_STATUS, UPLOADING);
			request.getSession().setAttribute(PROGRESS,55);
		}

		Long id = importacionFicherosEmitidosDao.importarYValidarFicheroEmitidos(fase, usuario, tipo, nombre, Boolean.TRUE);
		mapaIdAndError.put(ImportacionComisionesController.CLAVE_ID_FICHERO, id);
		
		try{
			// LLamada a un PL para hacer la validacion
			this.validarFicheroEmitidos(id);
		}catch (Exception e) {
			LOGGER.error("Error al validar fichero emitido",e);
			mapaIdAndError.put(ImportacionComisionesController.CLAVE_ID_ERROR, ImportacionComisionesController.WARN_ID_VALIDACION);
			LOGGER.debug(END_IMPORTAR_FICHERO_EMITIDOS);
			return mapaIdAndError;
		}
		
		//DAA 24/09/2013	Actualizamos la tabla de Informes
		try {
			int plan = fase.getFase().getPlan();
			importacionFicherosEmitidosDao.actualizarInformesFicheroEmitidos(id, plan);
			
		}catch (Exception e) {
			LOGGER.error("Error al actualizar la Tabla Informes Recibos",e);
			mapaIdAndError.put(ImportacionComisionesController.CLAVE_ID_ERROR, ImportacionComisionesController.WARN_ID_TABLA_INFORMES);
			//throw new DAOException("Error al actualizar la Tabla Informes Recibos", e);
			LOGGER.debug(END_IMPORTAR_FICHERO_EMITIDOS);
			return mapaIdAndError;
		} 

		LOGGER.debug(END_IMPORTAR_FICHERO_EMITIDOS);
		return mapaIdAndError;
	}

	/**
	 * Metodo que se encargar de hacer la validacion previa y la importacion de los recibos de comisiones
	 * Actualizamos la variable de session progressStatus, progress para saber el % del proceso completado
	 * @param ficheroImportacion
	 * @param usuario
	 * @param tipo
	 * @param nombre
	 * @param request
	 * @return 
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private Long importarFicheroComisiones(File ficheroImportacion,Usuario usuario,Character tipo,String nombre,HttpServletRequest request) throws DAOException, BusinessException{
		LOGGER.debug("init - importarFicheroComisiones");
		es.agroseguro.recibos.comisiones.FaseDocument fase=null;
		
		fase= (es.agroseguro.recibos.comisiones.FaseDocument)realizarValidacion(ficheroImportacion, FICHERO_COMISIONES);
		
		
		request.getSession().setAttribute(PROGRESS_STATUS, UPLOADING);
		request.getSession().setAttribute(PROGRESS,55);
		
		
		Long id = importacionFicheroComisionesDao.importarYValidarFicheroComisiones(fase,usuario,tipo,nombre,request);
		
		
		// LLamada a un PL para hacer la validacion
		this.validarFicheroComisiones(id);
		
		LOGGER.debug("end - importarFicheroComisiones");
		return id;
	}
	
	/**
	 * Metodo que se encargar de hacer la validacion previa y la importacion de los recibos de impagos
	 * Actualizamos la variable de session progressStatus, progress para saber el % del proceso completado
	 * @param ficheroImportacion
	 * @param usuario
	 * @param tipo
	 * @param nombre
	 * @param request
	 * @return
	 * @throws BusinessException
	 * @throws DAOException
	 */
	private Long importarFicheroImpagados(File ficheroImportacion,Usuario usuario,Character tipo,String nombre,HttpServletRequest request) throws BusinessException, DAOException{
		LOGGER.debug("init - importarFicheroImpagados");
		es.agroseguro.recibos.gastos.FaseDocument fase=null;
		
		fase=(es.agroseguro.recibos.gastos.FaseDocument) realizarValidacion(ficheroImportacion, FICHERO_IMPAGADOS);
		
		request.getSession().setAttribute(PROGRESS_STATUS, UPLOADING);
		request.getSession().setAttribute(PROGRESS,55);
		
		Long id = importacionFicheroImpagadosDao.importarYValidarFicheroImpagados(fase,usuario,tipo,nombre,request);
		
		// LLamada a un PL para hacer la validacion
		this.validarFicheroImpagados(id);
		
		LOGGER.debug("end - importarFicheroImpagados");
		return id;
	}
	
	/**
	 * Metodo que se encargar de hacer la validacion previa y la importacion de los ficheros de reglamentos
	 * Actualizamos la variable de session progressStatus, progress para saber el % del proceso completado
	 * @param ficheroImportacion
	 * @param usuario
	 * @param tipo
	 * @param nombre
	 * @param request
	 * @return
	 * @throws BusinessException
	 * @throws DAOException 
	 * @throws DAOException
	 */
	private Long importarFicheroReglamento(File ficheroImportacion,Usuario usuario,Character tipo,String nombre,HttpServletRequest request) throws BusinessException, DAOException{
		LOGGER.debug("init - importarFicheroReglamento");
		es.agroseguro.recibos.reglamentoProduccionEmitida.FaseDocument fase=null;
		
		fase=(es.agroseguro.recibos.reglamentoProduccionEmitida.FaseDocument) realizarValidacion(ficheroImportacion, FICHERO_REGLAMENTO);
		
		request.getSession().setAttribute(PROGRESS_STATUS, UPLOADING);
		request.getSession().setAttribute(PROGRESS,55);
		
		Long id = importacionFicheroReglamentoDao.importarYValidarFicheroReglamento(fase,usuario,tipo,nombre,request);
		
		// LLamada a un PL para hacer la validacion
		this.validarFicheroReglamento(id);
		
		LOGGER.debug("end - importarFicheroReglamento");
		return id;
	}
	

	public void validarFicheroDeudaAplazada(Long id) throws BusinessException{
		LOGGER.debug("init - validarFicheroDeudaAplazada");
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String procedure = "PQ_VALIDAR_DEUDA_APLAZADA.doValidarFicheroDeudaAplazada(IDFICHERO IN NUMBER)";
		parametros.put(IDFICHERO, id);
		
		LOGGER.debug("Llamada al procedimiento PQ_VALIDAR_DEUDA_APLAZADA.doValidarFicheroDeudaAplazada con los siguientes parametros: ");
		LOGGER.debug(IDFICHERO2 + id);
		
		databaseManager.executeStoreProc(procedure, parametros);
		
		LOGGER.debug("end - validarFicheroDeudaAplazada");
	}
	
	/**
	 * Metodo que llama al PL de validacion del fichero recibos emitidos
	 * @param id
	 * @throws BusinessException
	 */
	public void validarFicheroEmitidos(Long id) throws BusinessException{
		LOGGER.debug("init - validarFicheroComisiones");
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String procedure = "PQ_VALIDAR_RECIBOS_EMITIDOS.doValidarFicherosEmitidos(IDFICHERO IN NUMBER)";
		parametros.put(IDFICHERO, id);
		
		LOGGER.debug("Llamada al procedimiento pq_validar_recibos_emitidos.doValidarFicherosEmitidos con los siguientes parámetros: ");
		LOGGER.debug(IDFICHERO2 + id);
		
		databaseManager.executeStoreProc(procedure, parametros);
		
		LOGGER.debug("end - validarFicherosEmitidos");
	}
	
	/**
	 * Metodo que llama al PL de validacion del fichero de Impagados
	 * @param id
	 * @throws BusinessException
	 */
	public void validarFicheroImpagados(Long id)throws BusinessException {
		LOGGER.debug("init - validarFicheroComisiones");
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String procedure = "PQ_VALIDAR_IMPAGADOS.doValidarFicherosImpagados(IDFICHERO IN NUMBER)";
		parametros.put(IDFICHERO, id);
		
		LOGGER.debug("Llamada al procedimiento pq_validar_impagados.doValidarFicherosImpagados con los siguientes parámetros: ");
		LOGGER.debug(IDFICHERO2 + id);
		
		databaseManager.executeStoreProc(procedure, parametros);
		
		LOGGER.debug("end - validarFicheroComisiones");
		
	}
	
	/**
	 * Metodo que llama al PL de validacion del fichero de Reglamentos
	 * @param id
	 * @throws BusinessException
	 */
	public void validarFicheroReglamento(Long id)throws BusinessException {
		LOGGER.debug("init - validarFicheroReglamento");
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String procedure = "PQ_VALIDAR_IMPORT_REGL.doValidarFicherosReglamentos(IDFICHERO IN NUMBER)";
		parametros.put(IDFICHERO, id);
		
		LOGGER.debug("Llamada al procedimiento pq_validar_import_regl.doValidarFicherosReglamentos con los siguientes parámetros: ");
		LOGGER.debug(IDFICHERO2 + id);
		
		databaseManager.executeStoreProc(procedure, parametros);
		
		LOGGER.debug("end - validarFicheroReglamento");
		
	}
	
	/**
	 * Metodo que llama al PL de validacion del fichero de Comisiones
	 * @param id
	 * @throws BusinessException
	 */
	public void validarFicheroComisiones(Long id) throws BusinessException{
		LOGGER.debug("init - validarFicheroComisiones");
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String procedure = "PQ_VALIDAR_IMPORT_FICH.doValidarFicherosComisiones(IDFICHERO IN NUMBER)";
		parametros.put(IDFICHERO, id);
		
		LOGGER.debug("Llamada al procedimiento PQ_VALIDAR_IMPORT_FICH.doValidarFicherosComisiones con los siguientes parámetros: ");
		LOGGER.debug(IDFICHERO2 + id);
		
		databaseManager.executeStoreProc(procedure, parametros);
		
		LOGGER.debug("end - validarFicheroComisiones");
	}

	/**
	 * Metodo que trata el fichero zip y valida su contenido
	 * @param ficheroImportacion
	 * @param fase
	 * @param tipoImportacion
	 * @return
	 * @throws BusinessException
	 */
	private XmlObject realizarValidacion(File ficheroImportacion, int tipoImportacion) throws BusinessException{
		LOGGER.debug("init - realizarValidacion");
		XmlObject  xmlobj;
		try {
			
			File temp=ZipUtil.getFirstFileInZip(ficheroImportacion);		
			
			xmlobj = XmlComisionesValidationUtil.getXMLBeanValidado(temp, tipoImportacion);

		} catch (XMLValidationException xmle) {
			LOGGER.error("Se ha producido un error en validacion del fichero: " + xmle.getMessage());
			throw new BusinessException("Se ha producido un error en validacion del fichero",xmle);
		} catch (Exception be) {
			LOGGER.error("Se ha producido un error al tratar el zip : " + be.getMessage());
			throw new BusinessException("Se ha producido un error al tratar el zip",be);
		}
		
		LOGGER.debug("init - realizarValidacion");
		return xmlobj;
	}
	
	public void setImportacionFicheroImpagadosDao(IImportacionFicherosImpagadosDao importacionFicheroImpagadosDao) {
		this.importacionFicheroImpagadosDao = importacionFicheroImpagadosDao;
	}

	public void setImportacionFicheroComisionesDao(IImportacionFicherosComisionesDao importacionFicheroComisionesDao) {
		this.importacionFicheroComisionesDao = importacionFicheroComisionesDao;
	}

	public void setImportacionFicheroReglamentoDao(IImportacionFicherosReglamentoDao importacionFicheroReglamentoDao) {
		this.importacionFicheroReglamentoDao = importacionFicheroReglamentoDao;
	}
	
	public void setImportacionFicherosEmitidosDao(IImportacionFicherosEmitidosDao importacionFicherosEmitidosDao) {
		this.importacionFicherosEmitidosDao = importacionFicherosEmitidosDao;
	}

	public void setDatabaseManager(IDatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
	
	public void setImportacionFicherosDeudaAplazadaDao(IImportacionFicherosDeudaAplazadaDao importacionFicherosDeudaAplazadaDao) {
		this.importacionFicherosDeudaAplazadaDao = importacionFicherosDeudaAplazadaDao;
	}
}