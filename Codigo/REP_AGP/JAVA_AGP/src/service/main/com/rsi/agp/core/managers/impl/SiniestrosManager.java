package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SWConfirmacionSiniestroException;
import com.rsi.agp.core.exception.SWValidacionSiniestroException;
import com.rsi.agp.core.exception.ValidacionSiniestroException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.IRiesgosDao;
import com.rsi.agp.dao.models.poliza.ISiniestroDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.DatVarCapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.EstadoSiniestro;
import com.rsi.agp.dao.tables.siniestro.ParcelaSiniestro;
import com.rsi.agp.dao.tables.siniestro.Siniestro;
import com.rsi.agp.dao.tables.siniestro.SiniestroSWConfirmacion;
import com.rsi.agp.dao.tables.siniestro.SiniestroSWImpresion;
import com.rsi.agp.dao.tables.siniestro.SiniestroSWValidacion;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.DatosAsociados;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;

public class SiniestrosManager implements IManager {
	
	/*** SONAR Q ** MODIF TAM(26.11.2021) ***/
	/** - Se ha eliminado todo el código comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/

	private static final Log logger = LogFactory.getLog(SiniestrosManager.class);
	private ISiniestroDao siniestroDao;
	private IRiesgosDao riesgosDao;
	private IPolizaCopyDao polizaCopyDao;
	private IPolizaDao polizaDao;
	private IHistoricoEstadosManager historicoEstadosManager;
	private WebServicesManager webServicesManager;
	
	/** CONSTANTES SONAR Q ** MODIF TAM (26.11.2021) ** Inicio **/
	private final static String  ACUSE = "acuseRecibo";
	private final static String MOSTR_CONF = "mostrarConfirmar";
	/** CONSTANTES SONAR Q ** MODIF TAM (26.11.2021) ** Fin **/
	
	public List<EstadoSiniestro> getEstadosSiniestro() throws BusinessException{
		try {
			return siniestroDao.getEstadosSiniestro();
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error buscando los siniestros de una pÃ³liza", dao);
		}
	}
	/**
	 * Pasa a definitiva un siniestro.
	 * @author U029769 27/06/2013
	 * @param siniestro
	 * @param codUsuario
	 * @throws Exception
	 */
	public void pasarDefinitiva(Siniestro siniestro, Usuario usuario, Short estado) throws Exception{
		try{
			Asegurado ase = siniestro.getPoliza().getAsegurado();// getAsegurado(siniestro.getPoliza().getIdpoliza());
			
			String xml = XmlTransformerUtil.generateXMLSiniestro(siniestro, ase);
			siniestro.setXml(Hibernate.createClob(xml));
			
			EstadoSiniestro estadoSiniestro = new EstadoSiniestro();
			estadoSiniestro.setIdestado(estado);
			
			siniestro.setFecfirmasiniestro(new Date());
			this.guardarSiniestro(siniestro, estadoSiniestro, usuario, false);
			
		}catch(BusinessException ex){
			throw new BusinessException("Se ha producido un error al pasar a definitiva",ex);
		}catch (ValidacionSiniestroException e){
			logger.error("Error validando el xml de siniestros", e);
			throw new ValidacionSiniestroException (e.getMessage() );
		}catch (Exception ee){
			logger.error("Error generico al pasar a definitiva", ee);
			throw new Exception ("Error generico al pasar a definitiva");
		}
	}
	/**
	 * Al guardar un siniestro ya no obtenemos los datos de la copy
	 * 24/04/2014 U029769
	 * @param siniestro
	 * @param realPath
	 * @param usuario
	 * @param facturacion
	 * @throws BusinessException
	 */
	public void doGuardarSiniestro(Siniestro siniestro, String realPath, Usuario usuario, boolean facturacion) throws BusinessException {
		
		EstadoSiniestro estado = null;
		if (siniestro.getEstadoSiniestro().getIdestado() != null && (
			siniestro.getEstadoSiniestro().getIdestado().intValue() == Constants.SINIESTRO_ESTADO_PROVISIONAL.intValue() ||
			siniestro.getEstadoSiniestro().getIdestado().intValue() == Constants.SINIESTRO_ESTADO_ENVIADO_ERROR.intValue())) {
			
			
			//Si no viene usuario de alta ni fecha no estara editando por lo que 
			//guardamos usuario y fecha de alta 				
			//Llamada al PL para insertar el estado y usuario en el historico
			if(("").equals(siniestro.getUsuarioAlta()) && ("").equals(StringUtils.nullToString(siniestro.getFechaAlta()))){
				estado = new EstadoSiniestro(Constants.SINIESTRO_ESTADO_PROVISIONAL);
			}
			
			// Realizamos el alta del siniestro
			//Facturacion 
			this.guardarSiniestro(siniestro, estado, usuario, facturacion);
			
			//actualizamos la poliza poniendo el campo tieneSiniestros = 's'
			try {
				polizaDao.actualizaFlagTieneSiniestrosPoliza(siniestro.getPoliza().getIdpoliza(), Constants.CHARACTER_S);
			} catch (DAOException e) {
				logger.error("Excepcion : SiniestrosManager - doGuardarSiniestro", e);
			}

		}
	}


	public List<Siniestro> buscarSiniestros (Siniestro siniestro) throws BusinessException {
		
		try{
			return siniestroDao.list(siniestro);
		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido un error buscando los siniestros de una poliza", dao);
		}
	}


	public Siniestro buscarSiniestro (Long idSiniestro) throws BusinessException {
		try {
			return siniestroDao.getSiniestro(idSiniestro);
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error buscando un siniestro", dao);
		}
	}

	public void eliminarSiniestro (Long idSiniestro) throws BusinessException {
	
		try {
			siniestroDao.eliminarSiniestro(idSiniestro);
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error borrando un siniestro", dao);
		}
	}
	
	/*** Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio ***/
	public void bajaSiniestro (Long idSiniestro) throws BusinessException {
		
		try {
			siniestroDao.bajaSiniestro(idSiniestro);
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error borrando un siniestro", dao);
		}
	}
	
	/**
	 * Método para guardar un siniestro
	 * @param siniestro Siniestro a guardar
	 * @param estadoSiniestro Estado al que pasa el siniestro
	 * @param usuario Usuario que solicita el guardado del siniestro
	 * @param facturacion true si hay que insertar registro en facturación
	 * @return Siniestro guardado
	 * @throws BusinessException
	 */
	public Siniestro guardarSiniestro (Siniestro siniestro, EstadoSiniestro estadoSiniestro, Usuario usuario, boolean facturacion) 
			throws BusinessException {
		try{
			EstadoSiniestro estadoOld = siniestro.getEstadoSiniestro();
			if (estadoSiniestro != null){
				siniestro.setEstadoSiniestro(estadoSiniestro);
			}

			//Facturacion
			siniestroDao.guardarSiniestro(siniestro,usuario, facturacion);		

			if (estadoSiniestro != null && (estadoOld.getIdestado() == null || !estadoOld.getIdestado().equals(estadoSiniestro.getIdestado()))){
				historicoEstadosManager.insertaEstado(Tabla.SINIESTRO, siniestro.getId(),
					usuario.getCodusuario(),new BigDecimal(estadoSiniestro.getIdestado()));
			}
			siniestroDao.evict(siniestro);
			
			return siniestro;
		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido un error guardando un siniestro", dao);
		}
	}
	
	public Map<String, Object> buscarDescripciones (Siniestro siniestro) throws BusinessException {
		try{
			return siniestroDao.buscarDescripciones(siniestro);		
		}catch(DAOException dao){
			throw new BusinessException ("Se ha producido buscando descripciones de via, provincia y localidad de un siniestro", dao);
		}
	}
	
	public List<Riesgo> getRiesgos(Poliza poliza)throws BusinessException{
		
		
		List<BigDecimal> listaRGMNoElegible = null;	
		List<BigDecimal> listaRiesgosElegiblesFiltrados = null;
		List<BigDecimal> listadoRiesgos = new ArrayList<BigDecimal>();
		List<Riesgo> lista = null;
		
		try {
			//Listados riesgos del modulo no elegibles y elegibles, que utilizamos para calcular los riesgos tarificables
			listaRGMNoElegible = riesgosDao.getRiesgosCubModulo(poliza.getLinea().getLineaseguroid(),poliza.getCodmodulo(),'N');
	
			//obtenemos los riesgos elegibles, eliminando los riesgos elegidos a nivel de cabecera en la poliza o detalle de parcela
			listaRiesgosElegiblesFiltrados = riesgosDao.getRiesgosElegidosFiltrados(poliza);
			
			//Union de las listas temporales de los riesgos elegibles y no elegibles
			listadoRiesgos.addAll(listaRiesgosElegiblesFiltrados);
			listadoRiesgos.addAll(listaRGMNoElegible);

			// Obtenemos un Listado de RIESGOS filtrando por riesgos tasables que a su vez estos estan filtrados
			// por los riesgos tarificables que a su vez estos se obtienen de la relacion de riesgos tarificables
			// que a su vez estos van filtrados por los riesgos elegidos (listadoRiesgos) 
			lista = riesgosDao.getRiesgos(listadoRiesgos, poliza.getCodmodulo(), poliza.getLinea().getLineaseguroid(),poliza.getLinea().getCodlinea());
			
			return lista;
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error buscando los riesgos de un siniestro", dao);
			
		}		
		
	}
	
	public Poliza getPoliza(Long idPoliza) throws BusinessException{
		Poliza poliza = null;
		try {
			
			poliza = siniestroDao.getPoliza(idPoliza);
			return poliza;
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error buscando los riesgos de un siniestro", dao);
			
		}
	}
	
	public boolean tieneEstado(Long idSiniestro,Short estado)throws BusinessException{
		
		try {
			return siniestroDao.tieneEstado(idSiniestro, estado);				
			
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error buscando el estado de un siniestro", dao);
		}
	}
	
	public Long getPolizaCopyMasRecienteByReferencia(Character tipoReferencia, String referencia)throws BusinessException {
		try {
			com.rsi.agp.dao.tables.copy.Poliza poliza = polizaCopyDao.getPolizaCopyMasRecienteByReferencia(tipoReferencia, referencia);
			if (poliza != null)				
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
			comunicaciones = siniestroDao.getComunicaciones(idEnvio);
			
			if (comunicaciones == null){
				errores = new es.agroseguro.acuseRecibo.Error[0];
			}else{
			
				Clob fichero = comunicaciones.getFicheroContenido();
				if (fichero == null) {
					errores = new es.agroseguro.acuseRecibo.Error[0];
				} else {
					
					/* SONAR Q */
					String xml = obtenerXml(fichero);
					/* FIN SONAR Q */
					
					try {
						acuseRecibo = AcuseReciboDocument.Factory.parse(new StringReader(xml)); // String parseado a AcuseReciboDocument 
					} catch (Exception e) {
						logger.error("Se ha producido un error al recuperar el XML de Acuse de Recibo", e);
						throw new BusinessException("Error al convertir el XML a XML Bean",e);
					}
			
					/* SONAR Q */
					errores = obtenerErrores(acuseRecibo, errores, referencia);
					/* FIN SONAR Q */
				}
			}	
			
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error al recuperar el fichero_contenido de una ReducciÃ³n de Capital", dao);
		}
		
		return errores;
	}
	
	
	public void updateSiniestrosPoliza(Poliza poliza) throws BusinessException {
		
		try {
			polizaDao.saveOrUpdate(poliza);
			
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error actualizando el campo tienesiniestros de la poliza", e);
		}
		
		
	}
	
	/**
	 * Comprueba si existen duplicados en el siniestro
	 * @param Siniestro
	 * @return boolean
	 */
	public Boolean  verificarDuplicadosSiniestro(Siniestro siniestro){
		Boolean tieneDuplicados = false;
		List<String> lstDatVar= new ArrayList<String>();
		for (ParcelaSiniestro lstParSiniestros : siniestro.getParcelasSiniestros()){
			for (CapAsegSiniestro lstCapSiniestros : lstParSiniestros.getCapAsegSiniestros()){
				lstDatVar.clear();
				for (DatVarCapAsegSiniestro datVar : lstCapSiniestros.getDatVarCapAsegSiniestros()){					
					if (!lstDatVar.contains(datVar.getCodconcepto().toString())){
						lstDatVar.add(datVar.getCodconcepto().toString());
					}else{
						return true;
					}
				}
			}
		}
		return tieneDuplicados;
	}
	public boolean isSiniestroConParcelas (Long idSiniestro) throws BusinessException {
		
		try {	
			return siniestroDao.isSiniestroConParcelas(idSiniestro);
			
		}catch(DAOException d ) {
			logger.error("error al recuperar las parcelas del siniestro");
			throw new BusinessException();
		}
	}
	
	
	
	
	public Map<String, Object> validarSiniestro(String idPoliza,Siniestro sin,String realPath, Usuario usuario, 
			Long lineaSeguroId) throws SWValidacionSiniestroException, AgrException, Exception{
		Map<String, Object> parametros = new HashMap<String, Object>();
		SWSiniestroHelper helper = new SWSiniestroHelper();
	
		try {
		
			Asegurado asegurado = sin.getPoliza().getAsegurado();//getAsegurado(sin.getPoliza().getIdpoliza());
	
			parametros = helper.validarSiniestro(sin, realPath, usuario, asegurado);	
			AcuseRecibo acuseRecibo = (AcuseRecibo)parametros.get(ACUSE);
			String xmlEnvio = (String)parametros.get("xmlEnvio");
			String xmlRecepcion = (String)parametros.get("xmlRecepcion");
			// llamada a guardar clobs.
			this.guardarXmlValidacion(sin,null,usuario,xmlEnvio,xmlRecepcion);
	
			if (acuseRecibo != null) {
				logger.debug("Acuse validación recibido: " + acuseRecibo.toString());
			}
	
			//Recorremos el objeto acusePolizaHolder para eliminar los errores que no queremos que se muestren
			Poliza poliza = sin.getPoliza();
			BigDecimal codPlan = poliza.getLinea().getCodplan();
			BigDecimal codLinea = poliza.getLinea().getCodlinea();
			BigDecimal codEntidad = poliza.getColectivo().getSubentidadMediadora().getEntidad().getCodentidad();
			
			WSUtils.limpiaErroresWs(acuseRecibo, Constants.WS_VALIDACION_SN, null, this.polizaDao, codPlan, codLinea, codEntidad, Constants.SINIESTRO);
			
			// Si tiene errores
			parametros.put(ACUSE, acuseRecibo);
			if (acuseRecibo.getDocumentoArray().length > 0 && acuseRecibo.getDocumentoArray(0).getErrorArray().length>0) {								
				// Se indica en el objeto de errores que ha habido error en la validacion del acuse de recibo para que el 
				// controller realice la redireccion correspondiente 
				
				if (usuario.getPerfil().equals(
						Constants.PERFIL_USUARIO_ADMINISTRADOR)) {

					parametros.put(MOSTR_CONF, Boolean.TRUE);
					parametros
							.put("mensaje",
									"Los datos enviados contienen errores, corrija o contin&uacute;e:");
				} else {
					BigDecimal tipoUsuario = usuario.getTipousuario();
					tipoUsuario = usuario.getExterno().equals(
							Constants.USUARIO_EXTERNO) ? tipoUsuario
							.add(Constants.NUMERO_DIEZ) : tipoUsuario;
					List<ErrorWsAccion> errorWsAccionList = this.webServicesManager
							.getErroresWsAccion(codPlan, codLinea, codEntidad,
									tipoUsuario, Constants.SINIESTRO);
					boolean continuar = WSUtils.comprobarErroresPorPerfil(
							acuseRecibo, errorWsAccionList);
					parametros.put(MOSTR_CONF, continuar);
					parametros
							.put("mensaje",
									continuar ? "Los datos enviados contienen errores, corrija o contin&uacute;e:"
											: "Los datos enviados contienen errores. Corríjalo para poder continuar.");
				}

				parametros.put("mostrarCorregir", Boolean.TRUE);
				parametros.put("tieneErrores", "true");

				parametros.put("errores", Arrays.asList(acuseRecibo
						.getDocumentoArray(0).getErrorArray()));
				parametros.put("errLength", acuseRecibo.getDocumentoArray(0)
						.getErrorArray().length);
			} else {
				
				parametros.put(MOSTR_CONF, Boolean.TRUE);
				parametros.put("mostrarCorregir", Boolean.FALSE);
				parametros.put("tieneErrores", "false");
			}
			
			parametros.put("mostrarForzarConfirmar", Boolean.FALSE);
				
		} catch (BusinessException e) {
			logger.error("Excepcion : SiniestrosManager - validarSiniestro", e);
		}
		
		return parametros;
	}
	
	/**
	 * Metodo para guardar los XML de validación del ws de siniestro 
	 * @param siniestro identificador
	 * @param xmlEnvio XML de envio a Agroseguro
	 * @param xmlRecepcion XML de recepción a Agroseguro
	 * @throws DAOException Error al guardar el envio en la base de datos
	 */
	private Long guardarXmlValidacion(Siniestro siniestro, String tipoEnvio, Usuario usuario, String xmlEnvio, String xmlRecepcion) throws DAOException {
		logger.debug("init - guardarXmlValidacion");
		
		SiniestroSWValidacion sinSwValidacion = new SiniestroSWValidacion();
		sinSwValidacion.setSiniestro(siniestro);
		sinSwValidacion.setUsuario(usuario.getCodusuario());
		sinSwValidacion.setFecha(new Date());
		
		sinSwValidacion.setXmlEnvio((Hibernate.createClob(xmlEnvio)));
		sinSwValidacion.setXmlRespuesta((Hibernate.createClob(xmlRecepcion)));
		
		this.siniestroDao.saveOrUpdate(sinSwValidacion);
		logger.debug("end - guardarXmlValidacion");
		return null;
	}
	
	/**
	 * Metodo para guardar los XML de recepción del ws de siniestro 
	 * @param siniestro identificador
	 * @param xmlEnvio XML de envio a Agroseguro
	 * @param xmlRecepcion XML de recepción a Agroseguro
	 * @throws DAOException Error al guardar el envio en la base de datos
	 */
	private Long guardarXmlConfirmacion(Siniestro siniestro, String tipoEnvio, Usuario usuario, String xmlEnvio, String xmlRecepcion) throws DAOException {
		logger.debug("init - guardarXmlConfirmacion");
		
		SiniestroSWConfirmacion sinSwConfirm = new SiniestroSWConfirmacion();
		sinSwConfirm.setSiniestro(siniestro);
		sinSwConfirm.setUsuario(usuario.getCodusuario());
		sinSwConfirm.setFecha(new Date());
		
		sinSwConfirm.setXmlEnvio((Hibernate.createClob(xmlEnvio)));
		sinSwConfirm.setXmlRespuesta((Hibernate.createClob(xmlRecepcion)));
		
		this.siniestroDao.saveOrUpdate(sinSwConfirm);
		logger.debug("end - guardarXmlConfirmacion");
		return null;
	}
	
	
	public Map<String, Object> confirmarSiniestro(String idPoliza,Siniestro sin,String realPath, Usuario usuario) throws SWConfirmacionSiniestroException, AgrException, Exception{
		Map<String, Object> parametros = new HashMap<String, Object>();
		SWSiniestroHelper helper = new SWSiniestroHelper();
		// cargar siniestro.
		
		try {
		Asegurado asegurado = sin.getPoliza().getAsegurado();

		parametros = helper.confirmarSiniestro(sin, realPath, usuario, asegurado);	
		AcuseRecibo acuseRecibo = (AcuseRecibo)parametros.get(ACUSE);
		String xmlEnvio = (String)parametros.get("xmlEnvio");
		String xmlRecepcion = (String)parametros.get("xmlRecepcion");
		if(acuseRecibo.getDocumentoArray(0).getEstado()==1){
			parametros.put("esRechazado", "false");
		}else{
			parametros.put("esRechazado", "true");
		}
		// llamada a guardar clobs.
		this.guardarXmlConfirmacion(sin,null,usuario,xmlEnvio,xmlRecepcion);
		

		if (acuseRecibo != null) {
			logger.debug("Acuse confirmación recibido: " + acuseRecibo.toString());
		}
		parametros.put(ACUSE, acuseRecibo);
				
		} catch (BusinessException e) {
			logger.error("Excepcion : SiniestrosManager - confirmarSiniestro", e);
		}
		return parametros;
	}
	
	public void asignaDatosAsociados(AcuseRecibo acRe, Siniestro sn){
		if (acRe.getDocumentosRecibidos()>0){//Sólo podemos recibir 1
			if(acRe.getDocumentoArray(0).getEstado()==1){
				
				if(null!=acRe.getDocumentoArray(0).getDatosAsociados()){
					DatosAsociados ds=(DatosAsociados) acRe.getDocumentoArray(0).getDatosAsociados();
					if (ds.getDomNode()!=null && ds.getDomNode().getAttributes()!=null){
						String numSiniestro= ds.getDomNode().getAttributes().getNamedItem("numeroSiniestro").getNodeValue().toString();
						sn.setNumerosiniestro(new BigDecimal(numSiniestro));
						String serieSiniestro=ds.getDomNode().getAttributes().getNamedItem("serieSiniestro").getNodeValue().toString();
						sn.setNumeroSerie(new Integer(serieSiniestro));
					}
				}
			}
		}
	}
	
	public void guardarInfoBasicaSiniestro(Siniestro siniestro, Usuario usuario, String xmlRecepcion) throws DAOException {
		logger.debug("init - guardarInfoBasicaSiniestro");
		
		SiniestroSWImpresion sinSwImpr = new SiniestroSWImpresion();
		sinSwImpr.setSiniestro(siniestro);
		sinSwImpr.setUsuario(usuario.getCodusuario());
		sinSwImpr.setFecha(new Date());
		
		sinSwImpr.setXmlRespuesta((Hibernate.createClob(xmlRecepcion)));
		
		this.siniestroDao.saveOrUpdate(sinSwImpr);
		logger.debug("end - guardarInfoBasicaSiniestro");
		
	}
	
	public void setSiniestroDao(ISiniestroDao siniestroDao) {
		this.siniestroDao = siniestroDao;
	}
	public void setRiesgosDao(IRiesgosDao riesgosDao) {
		this.riesgosDao = riesgosDao;
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
	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}
	
	/**
	 * Contamos el número total de siniestros.
	 * @author U029114 21/06/2017
	 * @param idPoliza
	 * @return int
	 * @throws DAOException
	 */
	public BigDecimal getNumTotalSiniestros(Long idPoliza) throws DAOException {

		try {
			BigDecimal totalSiniestros = this.siniestroDao.getNumTotalSiniestros(idPoliza);
			return totalSiniestros;

		}catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
	/**
	 * Actualiza el flag de tener siniestros o no de la póliza
	 * @author U029114 21/06/2017
	 * @param idPoliza
	 * @param caracter
	 * @throws DAOException
	 */
	public void actualizaFlagTieneSiniestrosPoliza(Long idpoliza, Character caracter) throws DAOException {
		
		try {
			polizaDao.actualizaFlagTieneSiniestrosPoliza(idpoliza, caracter);
		}catch (Exception ex) {
			logger.info("Se ha producido un error actualizando el campo tienesiniestros de la poliza: " + ex.getMessage());
			throw new DAOException("Se ha producido un error actualizando el campo tienesiniestros de la poliza", ex);
		}
		
	}
	
	/**
	 * Obtenemos un nuevo numsiniestro del siniestro.
	 * @author U029114 27/06/2017
	 * @param idPoliza
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal getNuevoNumSiniestro(Long idPoliza) throws DAOException {
		
		try {
			BigDecimal nuevoNumSini = this.siniestroDao.getNuevoNumSiniestro(idPoliza);
			return nuevoNumSini;
		
		}catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
	/* MODIF TAM (23.11.2021) ** SONAR Q ** Inicio */
	/* Creamos nuevas funciones, para descargar las funciones principales de ifs/fors */
	
	/* SONAR Q */
	private static String obtenerXml(Clob fichero) {
		String xml = WSUtils.convertClob2String(fichero); //Recuperamos el Clob y lo convertimos en String
		
		//Se comprueba si existe cabecera, sino se inserta al principio
		if (xml.indexOf("<?xml version=\"1.0") == -1){ 
		  String cabecera="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		  xml = cabecera + xml;	
		}
		
		// Se Reempla 
		String namespace ="http://www.agroseguro.es/AcuseRecibo";
		String reemplazar1="<AcuseRecibo xmlns=\""+namespace+"\"";
		String reemplazar2="</AcuseRecibo>";
		
		if (xml.indexOf("<xml-fragment") == -1) { 
			if(xml.indexOf("http://www.agroseguro.es/AcuseRecibo") == -1){
				//Buscamos Acuse Recibo
				xml = xml.replace("<AcuseRecibo", reemplazar1);
			}
		}else{
			
			xml = xml.replace("<xml-fragment", reemplazar1)
					.replace("</xml-fragment>", reemplazar2)
					.replace("xmlns:acus=\"http://www.agroseguro.es/AcuseRecibo\"", "")
					.replace("acus:", "");
		}
		return xml;
	}
	
	private static es.agroseguro.acuseRecibo.Error[] obtenerErrores(AcuseReciboDocument acuseRecibo, 
															        es.agroseguro.acuseRecibo.Error[] errores,
															        String referencia) throws BusinessException{
	
		if (acuseRecibo != null) { 
			AcuseRecibo ac = acuseRecibo.getAcuseRecibo();
			ArrayList<es.agroseguro.acuseRecibo.Error> ArrayE  =  new ArrayList<es.agroseguro.acuseRecibo.Error>();
						
			// Recorremos Acuse de Recibo para hacer Array con errores
			for (int i = 0; i < ac.getDocumentoArray().length; i++) 
			{	
				Documento documentoRecibido = ac.getDocumentoArray(i);
				int j = 0;
				
				// Si el documento del acuse de recibo tiene estado 2 (rechazado) y coincide  "idPoliza + linea + plan"
				if (documentoRecibido.getEstado()== Constants.ACUSE_RECIBO_ESTADO_RECHAZADO  && documentoRecibido.getId().equals(referencia)) {
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
		return errores;
	}
	
	/* MODIF TAM (23.11.2021) ** SONAR Q ** Fin */
}
