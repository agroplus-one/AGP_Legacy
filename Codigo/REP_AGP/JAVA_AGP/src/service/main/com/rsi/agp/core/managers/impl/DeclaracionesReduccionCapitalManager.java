package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion.ISolicitudReduccionCapManager;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.dao.models.anexo.IEstadoCuponDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.IReduccionCapitalDao;
import com.rsi.agp.dao.models.poliza.IRiesgosDao;
import com.rsi.agp.dao.models.rc.CuponDao;
import com.rsi.agp.dao.models.rc.ICuponDao;
import com.rsi.agp.dao.models.rc.IDeclaracionRCPolizaDao;
import com.rsi.agp.dao.models.rc.IEstadoCuponRCDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.Estado;
import com.rsi.agp.dao.tables.reduccionCap.EstadoCuponRC;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.Documento;


public class DeclaracionesReduccionCapitalManager implements IManager {

	private IReduccionCapitalDao reduccionCapitalDao;
	private IRiesgosDao riesgosDao;
	private IPolizaCopyDao polizaCopyDao;
	private IPolizaDao polizaDao;
	private IHistoricoEstadosManager historicoEstadosManager;
	private static final Log logger = LogFactory.getLog(DeclaracionesReduccionCapitalManager.class);
	private IDeclaracionRCPolizaDao declaracionRCPolizaDao;
	private IEstadoCuponRCDao estadoCuponRCDao;
	private ICuponDao cuponRCDao;
	//private ISolicitudReduccionCapManager solicitudRCManager;

	public void setReduccionCapitalDao(IReduccionCapitalDao reduccionCapitalDao) {
		this.reduccionCapitalDao = reduccionCapitalDao;
	}
	
	public List<ReduccionCapital> buscarReduccionesCapital (ReduccionCapital reduccionCapital) throws BusinessException {
		
		try{
			
			return reduccionCapitalDao.list(reduccionCapital);
			
		}catch(DAOException dao){
			
			throw new BusinessException ("Se ha producido un error al recuperar datos de una Reduccion de Capital", dao);
			
		}
	}
	/**
	 * Pasa a definitiva una Reduccion de Capital 
	 * @author U029769 28/06/2013
	 * @param idReduccionCapital
	 * @param codUsuario
	 * @throws BusinessException
	 */
	public void pasarDefinitiva(Long idReduccionCapital, Usuario usuario) throws BusinessException{
		try{
			ReduccionCapital reduccionCapital = (ReduccionCapital)reduccionCapitalDao.getObject(ReduccionCapital.class, idReduccionCapital);
			
			Estado estado = new Estado();
			estado.setIdestado(Constants.REDUCCION_CAPITAL_ESTADO_DEFINITIVO);
			
			this.guardarReduccionCapital(reduccionCapital, estado, usuario);
			
		}catch(Exception ex){
			throw new BusinessException("Se ha producido un error al pasar a definitiva",ex);
		}
	}
	
	public ReduccionCapital buscarReduccionCapital (Long idReduccionCapital) throws BusinessException {
		try {
			
			return reduccionCapitalDao.getReduccionCapital(idReduccionCapital);
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error buscando un siniestro", dao);
			
		}
		
	}
	
	public List<Estado> getEstadosReduccionCapital() throws BusinessException{
		try {
			
			return reduccionCapitalDao.getEstadosReduccionCapital();
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error al recuperar datos de una Reduccion de Capital", dao);
			
		}
	}

	

	public Poliza getPoliza(Long idPoliza) throws BusinessException{
		Poliza poliza = null;
		try {
			
			poliza = reduccionCapitalDao.getPoliza(idPoliza);
			return poliza;
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error al recuperar datos de una Reduccion de Capital", dao);
			
		}
	}

	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws BusinessException{
		Comunicaciones comunicaciones = null;
		try {
			
			comunicaciones = reduccionCapitalDao.getComunicaciones(idEnvio);
			return comunicaciones;
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error al recuperar datos de una Reduccion de Capital", dao);
			
		}
	}
	
	public void guardarReduccionCapital (ReduccionCapital reduccionCapital, Estado estado, Usuario usuario) throws BusinessException {
		
		try{
			Estado estadoOld = reduccionCapital.getEstado();
			if (estado != null){
				reduccionCapital.setEstado(estado);
			}
			if (reduccionCapital.getCodmotivoriesgo()!= null) {
				if (reduccionCapital.getCodmotivoriesgo().length()==1) {
					reduccionCapital.setCodmotivoriesgo("0"+reduccionCapital.getCodmotivoriesgo());
				}
			}
			reduccionCapitalDao.guardarReduccionCapital(reduccionCapital);
			
			if (estado != null && (estadoOld.getIdestado() == null || !estadoOld.getIdestado().equals(estado.getIdestado()))){
				historicoEstadosManager.insertaEstado(Tabla.ANEXO_RED, reduccionCapital.getId(),
					usuario.getCodusuario(),new BigDecimal(estado.getIdestado()));
			}
			reduccionCapitalDao.evict(reduccionCapital);
			
		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido un error guardando una reduccion de capital", dao);
		}
		
	}
	
	public CuponRC devuelveNuevoCupon(String idCupon) throws BusinessException {
		try {
			
			return cuponRCDao.obtenerCupon(idCupon);
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error creando el nuevo cupon", dao);
			
		}
	}
	
	public void eliminarReduccionCapital (ReduccionCapital rc) throws BusinessException {
		
		try {
			
			reduccionCapitalDao.eliminarReduccionCapital(rc);
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error borrando una reduccion de capital", dao);
			
		}
	}

	
	public List<Object[]> getRiesgos(Poliza poliza)throws BusinessException{
		
		List<Object[]> lista = null;
		
		try {
			lista= riesgosDao.getRiesgosReduccionCapital(poliza.getLinea().getLineaseguroid());
			return lista;
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error buscando los riesgos de una reduccion de capital", dao);
			
		}		
		
	}

	public boolean tieneEstado(Long idReduccionCapital,Short estado)throws BusinessException{
		
		try {
			return reduccionCapitalDao.tieneEstado(idReduccionCapital, estado);				
			
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error buscando el estado de una reduccion de capital", dao);
		}
	}
	
	public boolean tieneReduccionesCapital(Long idPoliza)throws BusinessException{
		
		try {
			return reduccionCapitalDao.tieneReduccionesCapital(idPoliza);				
			
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error averiguando si existen reducciones de capital", dao);
		}
	}
	
	public Long getPolizaCopyMasRecienteByReferencia(Character tipoReferencia, String referencia)throws BusinessException {
		try {
			com.rsi.agp.dao.tables.copy.Poliza poliza = polizaCopyDao.getPolizaCopyMasRecienteByReferencia(tipoReferencia, referencia);
			if (poliza !=null)
				return poliza.getId();
			else
				return null;
			
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error buscando la copy msa reciente", dao);
		}
	}
	
	public es.agroseguro.acuseRecibo.Error[] getFicheroContenido(BigDecimal idEnvio, String refPoliza, BigDecimal linea, BigDecimal plan) throws BusinessException{
		AcuseReciboDocument acuseRecibo = null;
		es.agroseguro.acuseRecibo.Error[] errores = null;
		Comunicaciones comunicaciones= null;
		
		// Se monta la referencia que luego se comparara
		String referencia =  refPoliza.toString() + "" +  plan.toString()+ "" +linea.toString(); 
		
		try {
			comunicaciones = reduccionCapitalDao.getComunicaciones(idEnvio);
			if (comunicaciones == null){
				errores = new es.agroseguro.acuseRecibo.Error[0];
				
			} else {
				Clob fichero = comunicaciones.getFicheroContenido();
				if (fichero == null) {
					errores = new es.agroseguro.acuseRecibo.Error[0];
				} else {
					String xml = WSUtils.convertClob2String(fichero); //Recuperamos el Clob y lo convertimos en String
					// Se comprueba si existe cabecera, sino se inserta al principio
					if (xml.indexOf("<?xml version=\"1.0") == -1)
					{ String cabecera="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
					  xml = cabecera + xml;	
					}
					
					// Se Reempla 
					String namespace ="http://www.agroseguro.es/AcuseRecibo";
					String reemplazar1="<AcuseRecibo xmlns=\""+namespace+"\"";
					String reemplazar2="</AcuseRecibo>";
					
					if (xml.indexOf("<xml-fragment") == -1)
					{ if(xml.indexOf("http://www.agroseguro.es/AcuseRecibo") == -1)
						{
							//Buscamos Acuse Recibo
							xml = xml.replace("<AcuseRecibo", reemplazar1);
						}
					}
					else{
						
						xml = xml.replace("<xml-fragment", reemplazar1)
								.replace("</xml-fragment>", reemplazar2)
								.replace("xmlns:acus=\"http://www.agroseguro.es/AcuseRecibo\"", "")
								.replace("acus:", "");
					}
					
					
					try {
						acuseRecibo = AcuseReciboDocument.Factory.parse(new StringReader(xml)); // String parseado a AcuseReciboDocument 
					} catch (Exception e) {
						logger.error("Se ha producido un error al recuperar el XML de Acuse de Recibo", e);
						throw new BusinessException("Error al convertir el XML a XML Bean",e);
					}
				
					if (acuseRecibo != null) { 
						AcuseRecibo ac = acuseRecibo.getAcuseRecibo();
						ArrayList<es.agroseguro.acuseRecibo.Error> ArrayE  =  new ArrayList<es.agroseguro.acuseRecibo.Error>();
														
						// Recorremos Acuse de Recibo para hacer Array con errores
						for (int i = 0; i < ac.getDocumentoArray().length; i++) 
						{
							Documento documentoRecibido = ac.getDocumentoArray(i);
							int j = 0;
							
							// Si el documento del acuse de recibo tiene estado 2 (rechazado) y coincide  "idPoliza + linea + plan"
							if (documentoRecibido.getEstado()== Constants.ACUSE_RECIBO_ESTADO_RECHAZADO && documentoRecibido.getId().equals(referencia)) {
								// Formamos la lista de Errores
								while (j < documentoRecibido.getErrorArray().length){
								
									try {
										ArrayE.add((es.agroseguro.acuseRecibo.Error) documentoRecibido.getErrorArray(j));  
										j = j+1;
										
									} catch (Exception ex) {
										throw new BusinessException("Se ha producido un error al visualizar Acuse de Recibo ",ex);
									}
								}
							}
					}
					errores = new es.agroseguro.acuseRecibo.Error[ArrayE.size()];
					for (int i = 0; i < ArrayE.size(); i++) {
						errores[i] = ArrayE.get(i);
					}
				}else {
					errores = new es.agroseguro.acuseRecibo.Error[0];
				}
			}
		}		
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error al recuperar el fichero_contenido de una Reduccion de Capital", dao);
		}
		
		return errores;
	}
	
	//DAA 29/08/2013
	public ReduccionCapital editarReduccionCapital(ReduccionCapital reduccionCapital, Poliza poliza, Usuario usuario, boolean modoLectura) throws BusinessException {
		//Nueva logica P0079361
		Estado estado = null;
		if(reduccionCapital.getId() != null){
			// -- MODO EDICIoN o VISUALIZACION
			reduccionCapital = this.buscarReduccionCapital(reduccionCapital.getId());
			if(!modoLectura){
				// -- MODO EDICIoN
				estado = new Estado(Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR, "");
				this.guardarReduccionCapital(reduccionCapital, estado, usuario);
			}	
		}else{
			// -- MODO ALTA --
			reduccionCapital.setPoliza(poliza);//Introducir en el nuevo siniestro la poliza a la que esta referido
			reduccionCapital.setId(null);//El num de reduccion de capital se deja vacio porque se calculara cuando la reduccion se de de alta.
			estado = new Estado(Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR);
			this.guardarReduccionCapital(reduccionCapital, estado, usuario);
		}
		return reduccionCapital;
		//Nueva logica P0079361
	}
	
	public ReduccionCapital altaAnexoReduccionCap(ReduccionCapital reduccionCapital, String realPath, Usuario usuario,
			Estado estado, boolean esAlta) throws BusinessException, DAOException/*, ValidacionAnexoModificacionException,
			CargaPolizaActualizadaDelCuponException, CargaPolizaFromCopyOrPolizaException*/ {

		logger.debug("*** DeclaracionesReduccionPolizaManager - altaAnexoReduccionCap");
		Long idPoliza = reduccionCapital.getPoliza().getIdpoliza();
		Poliza poliza = this.polizaDao.getPolizaById(idPoliza);
		reduccionCapital.setPoliza(poliza);
		
		guardarReduccionCapital(reduccionCapital, estado, usuario);
		
		//actualizaXmlRC(reduccionCapital);

		//this.updateAnexoModificacionPoliza(anexo.getPoliza());

		return reduccionCapital;
	}
	/**
	 * Metodo para actualizar el xml de una Reduccion de Capital
	 * 
	 * @param redCap
	 * ReduccionCapital a actualizar
	 * @throws BusinessException
	 * @throws ValidacionRCException,Exception
	 */
	public void actualizaXmlRC(ReduccionCapital redCap)throws BusinessException {
		try {
			XmlTransformerUtil.updateXMLRedCap(redCap,reduccionCapitalDao);

		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Error validando el xml de de Anexos de Modificacion" + e.getMessage());
		} catch (Exception ee) {
			logger.error("Error generico al actualizar el xml de Anexos de Modificacion" + ee.getMessage());
			throw new BusinessException("Error generico al pasar a definitiva");
		}
	}
	
	/**
	 * Llama al metodo que borra todos los cupones cuyo idcupon coincida con el
	 * parámetro
	 * 
	 * @param idCupon
	 */
	public void borrarCupon(String idCupon) {
		try {
			cuponRCDao.borrarCupon(idCupon);
		} catch (Exception e) {
			logger.error("Error al borrar el cupon", e);
		}
	}
	
	public boolean isAnexoCaducado(ReduccionCapital rc) throws ParseException {
		boolean caducado = false;
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date fechaCupon = rc.getCupon().getFecha();
		sdf2.format(fechaCupon);

		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.HOUR_OF_DAY, 23);
		cal1.set(Calendar.MINUTE, 59);
		cal1.set(Calendar.SECOND, 59);
		cal1.set(Calendar.MILLISECOND, 59);
		cal1.add(Calendar.DATE, -1);

		String aux = sdf2.format(cal1.getTime());

		Date fecha2 = sdf2.parse(aux);
		if (fechaCupon.compareTo(fecha2) > 0) {
			caducado = false;
		} else if (fechaCupon.compareTo(fecha2) < 0) {
			caducado = true;
		} else if (fechaCupon.compareTo(fecha2) == 0) {
			caducado = true;
		}
		return caducado;
	}
	
	public EstadoCuponRC getEstadoCupon() throws DAOException {

		return estadoCuponRCDao.getEstadoCupon(Constants.AM_CUPON_ESTADO_CADUCADO);

	}
	
	/**
	 * Llama al metodo que comprueba si el AM por cupon caducado correspondiente al
	 * id indicado es editable solicitando un nuevo cupon o no
	 * 
	 * @param idPoliza
	 * @param idAnexo
	 * @return
	 */
	public BigDecimal isEditableRCCuponCaducado(Long idPoliza, Long idAnexo) {
		try {
			return declaracionRCPolizaDao.isEditableAMCuponCaducado(idAnexo, idPoliza);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al comprobar si es editable el anexo con id " + idAnexo, e);
			return new BigDecimal(-1);
		}
	}
	
	/**
	 * Metodo para cargar los datos basicos del anexo de RC a partir de la
	 * situacion actualizada obtenida durante la peticion del cupon para el anexo.
	 * 
	 * @param anexo
	 *            Anexo de Reduccion de Capital
	 * @throws Exception
	 */
//	private void setDatosRCFromPolizaActualizada(final ReduccionCapital reduccionCap) throws Exception {
//		
//		es.agroseguro.contratacion.Poliza poliza  = null;
//		es.agroseguro.contratacion.Poliza polizaComp = null;
//		
//		poliza = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudRCManager.getPolizaActualizadaFromCupon(reduccionCap.getCupon().getIdcupon())).getPoliza();
//	
//		
//
//	}
	
	/**
	 * Guarda un Anexo de Modificacion y inserta en el historico 14/08/2013 U029769
	 * 
	 * @param anexo
	 * @throws BusinessException
	 */
//	public ReduccionCapital saveAnexoReduccion(ReduccionCapital reduccionCap, String codUsuario, Estado estado,
//			boolean esAlta) throws BusinessException {
//
//		try {
//
//			boolean insertarHistorico = false;
//			if (estado != null && !estado.getIdestado().equals(reduccionCap.getEstado().getIdestado()))
//				insertarHistorico = true;
//
//			if (estado != null) {
//				reduccionCap.setEstado(estado);
//			}
//			/*
//			 * Si es alta:Comprobamos si el objeto "comunicaciones" tiene id, porque si lo
//			 * tiene hay que quitarlo para que no de error al guardar
//			 */
//			if (esAlta) {
//				if (reduccionCap.getComunicaciones() != null && anexo.getComunicaciones().getIdenvio() == null) {
//					reduccionCap.setComunicaciones(null);
//				}
//
//				if (reduccionCap.getEstadoAgroseguro() != null && reduccionCap.getEstadoAgroseguro().getCodestado() == null) {
//					reduccionCap.setEstadoAgroseguro(null);
//				}
//			}
//
//			declaracionReduccionPolizaDao.saveOrUpdate(anexo);
//
//			// Si hay un cambio de estado se inserta en el historico
//			if (insertarHistorico)
//				historicoEstadosManager.insertaEstado(Tabla.ANEXO_MOD, anexo.getId(), codUsuario, estado.getIdestado());
//
//			declaracionModificacionPolizaDao.evict(anexo);
//
//			return anexo;
//
//		} catch (Exception ex) {
//			throw new BusinessException("Se ha producido un error al guardar el anexo de modificacion", ex);
//		}
//	}
	
	
	public void updateTieneReduccionCapPoliza(Poliza poliza) throws BusinessException {
		try {
			polizaDao.saveOrUpdate(poliza);
			
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error actualizando el campo tieneanexomp de la poliza", e);
		}
	}
	public List<ReduccionCapital> buscarReduccionCapitalPoliza (ReduccionCapital reduccionCapital) throws BusinessException {
		try{
		
			return reduccionCapitalDao.list(reduccionCapital);
		
		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido un error buscando los siniestros de una poliza", dao);
		}
	}	
	public boolean isRCconParcelas(Long id) throws BusinessException {
		try{
			return reduccionCapitalDao.isRCconParcelas(id);
		
		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido un error buscando las parcelas de Reduccion de capital", dao);
		}
	}
	
	public void setRiesgosDao(IRiesgosDao riesgosDao) {
		this.riesgosDao = riesgosDao;
	}

	public IReduccionCapitalDao getReduccionCapitalDao() {
		return reduccionCapitalDao;
	}

	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) {
		this.polizaCopyDao = polizaCopyDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}
	

	// P0079361

	public void setDeclaracionRCPolizaDao(IDeclaracionRCPolizaDao declaracionRCPolizaDao) {
		this.declaracionRCPolizaDao = declaracionRCPolizaDao;
	}

	public void setEstadoCuponRCDao(IEstadoCuponRCDao estadoCuponRCDao) {
		this.estadoCuponRCDao = estadoCuponRCDao;
	}

	public void setCuponRCDao(ICuponDao cuponRCDao) {
		this.cuponRCDao = cuponRCDao;
	}
	
	

//	public void setSolicitudRCManager(ISolicitudReduccionCapManager solicitudRCManager) {
//		this.solicitudRCManager = solicitudRCManager;
//	}

	
	

	
}
