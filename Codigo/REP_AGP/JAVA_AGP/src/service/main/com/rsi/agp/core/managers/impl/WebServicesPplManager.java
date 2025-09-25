package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Hibernate;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.exception.ValidacionServiceException;
import com.rsi.agp.core.manager.impl.anexoRC.SWAnexoRCHelper;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.FluxCondensatorObject;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IDistribucionCosteDAO;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.Organismo;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionCCAA;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.DistCosteSubvencion;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.BonificacionAsegurado;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Consorcio;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.DatosCalculo;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.DistribucionCoste;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.RecargoAsegurado;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionCCAA;
import es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.SubvencionEnesa;

public class WebServicesPplManager implements IManager{
	
	/*** SONAR Q ** MODIF TAM(17.12.2021) ***/
	/** - Se ha eliminado todo el código comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/
	
	private static BigDecimal CONST_CARACT_EXPLOTACION = new BigDecimal(106);
	
	private Log logger = LogFactory.getLog(WebServicesPplManager.class);
	
	private IPolizaDao polizaDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	
	private ServicioValidarHelper servicioValidarHelper = new ServicioValidarHelper();
	private SWAnexoRCHelper swAnexoRCHelper = new SWAnexoRCHelper();
	private ServicioCalcularHelper servicioCalcularHelper = new ServicioCalcularHelper();
	private ServicioCaractExplotacionHelper servicioCaractExplotacionHelper = new ServicioCaractExplotacionHelper();
	
	private IDistribucionCosteDAO distribucionCosteDAO;
	
	/** CONSTANTES SONAR Q ** MODIF TAM (17.12.2021) ** Inicio **/
	private static final String MILISEG = " milisegundos";
	private static final String CALC = "calculo";
	private static final String ERROR = "Error al obtener la descripcion de la subvencion CCAA ";
	
	/**
	 * Metodo que borra la distribucion de costes de una poliza
	 * @param poliza
	 * @throws BusinessException
	 */
	public Poliza borrarDistribucionCosteAnterior(Poliza poliza) throws BusinessException {
		logger.debug("init - borrarDistribucionCosteAnterior");
		try {
			Set<com.rsi.agp.dao.tables.poliza.DistribucionCoste> distCostes = poliza.getDistribucionCostes();
			for (com.rsi.agp.dao.tables.poliza.DistribucionCoste distCoste : distCostes) {
				distribucionCosteDAO.deleteDistribucionCoste(distCoste.getPoliza().getIdpoliza(), distCoste.getCodmodulo(), distCoste.getFilacomparativa());
			}
			poliza.getDistribucionCostes().clear();
			logger.debug("end - borrarDistribucionCosteAnterior");
			return poliza;
		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error al borrar la distribucion de costes anterior",dao);
		}
	}
	
	/**
	 * Metodo que parsea y inserta en la BBDD la distribucion de costes resultado del xml de calculo
	 * @param fluxCondensatorObject
	 * @param idpoliza
	 * @param request
	 * @throws BusinessException
	 */
	public void guardarDistrubucionCostePpl(
			final FluxCondensatorObject fluxCondensatorObject,
			final Long idpoliza, final BigDecimal filacomparativa)
			throws BusinessException {

		logger.debug("init - guardarDistrubucionCosteCpl");
		com.rsi.agp.dao.tables.poliza.DistribucionCoste distribucionCoste = new com.rsi.agp.dao.tables.poliza.DistribucionCoste();
		try {
			// GUARDAMOS LA DISTRIBUCION DE COSTES
			generarDistribucionCoste(distribucionCoste, fluxCondensatorObject,
					filacomparativa);
			distribucionCosteDAO.saveDistribucionCoste(distribucionCoste,
					Long.valueOf(idpoliza));

			// ACTUALIZAMOS EL IMPORTE DE LA POLIZA
			Poliza poliza = polizaDao.getPolizaById(idpoliza);
			poliza.setImporte(new BigDecimal(fluxCondensatorObject
					.getImporteTomador()));
			polizaDao.saveOrUpdate(poliza);

		} catch (Exception ex) {
			throw new BusinessException(
					"Se ha producido un error al guardar la distribucion de costes",
					ex);
		}
		logger.debug("end - guardarDistrubucionCosteCpl");
	}
	
	/**
	 * Metodo que llama al SW de validacion  para poliza principal
	 * @param idPoliza
	 * @param codModulo
	 * @param realPath
	 * @param tipoWS
	 * @return
	 */
	public AcuseRecibo validarPpl(Long idEnvio, Long idPoliza, String realPath,Character tipoWS) throws BusinessException {
		logger.debug("init - validarPpl");
		GregorianCalendar gcIni = null;
		GregorianCalendar gcFin = null;
		AcuseRecibo acuse = null;
		try {
				gcIni =  new GregorianCalendar();
				
				acuse = servicioValidarHelper.doWork(idEnvio, idPoliza, realPath, polizaDao, tipoWS);
				
				gcFin = new GregorianCalendar();
				
				Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
				logger.debug("Tiempo de la llamada al servicio de validacion: " + tiempo + MILISEG);
				
		} catch (ValidacionServiceException vse) {
			throw new BusinessException("Se ha producido un error en el servicio de validacion de la poliza principal",vse);
		}
		logger.debug("end - validarPpl");
		return acuse;
	}
	
	/**
	 * Metodo que llama al SW de validación para una situación actualizada de la póliza
	 * @param poliza
	 * @param realPath
	 * @param tipoWS
	 * @return
	 * @throws BusinessException
	 */
	public AcuseRecibo validarPplSituacionAct(final XmlObject poliza, AnexoModificacion am,
			final String realPath) throws BusinessException {
		logger.debug("init - validarPpl");
		GregorianCalendar gcIni = null;
		GregorianCalendar gcFin = null;
		AcuseRecibo acuse = null;
		try {
			gcIni = new GregorianCalendar();
			/*acuse = servicioValidarHelper.doWork(poliza, realPath);*/
			acuse = servicioValidarHelper.doWorkValAnexo(poliza,  am, realPath);
			
			gcFin = new GregorianCalendar();
			Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
			logger.debug("Tiempo de la llamada al servicio de validacion: "
					+ tiempo + MILISEG);
		} catch (ValidacionServiceException vse) {
			throw new BusinessException(
					"Se ha producido un error en el servicio de validacion de una situación actualizada de la póliza",
					vse);
		}
		logger.debug("end - validarPpl");
		return acuse;
	}
	/**
	 * Metodo que llama al SW de validación para una situación actualizada de la póliza
	 * @param poliza
	 * @param realPath
	 * @param tipoWS
	 * @return
	 * @throws BusinessException
	 */
	public AcuseRecibo validarSituacionActRC(final XmlObject poliza, ReduccionCapital redCap,
			final String realPath) throws BusinessException {
		logger.debug("init - validarPpl");
		GregorianCalendar gcIni = null;
		GregorianCalendar gcFin = null;
		AcuseRecibo acuse = null;
		try {
			gcIni = new GregorianCalendar();
			/*acuse = servicioValidarHelper.doWork(poliza, realPath);*/
			try {
				acuse = swAnexoRCHelper.validacionModificacionRC(poliza.xmlText(), redCap.getCupon().getIdcupon(), realPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			gcFin = new GregorianCalendar();
			Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
			logger.debug("Tiempo de la llamada al servicio de validacion: "
					+ tiempo + MILISEG);
		} catch (ValidacionServiceException vse) {
			throw new BusinessException(
					"Se ha producido un error en el servicio de validacion de una situación actualizada de la póliza",
					vse);
		}
		logger.debug("end - validarPpl");
		return acuse;
	}
	
	
	/**
	 * Metodo que llama al SW de calculo para poliza principal
	 * @param idPoliza
	 * @param codModulo
	 * @param descuentoColectivo
	 * @param realPath
	 * @param tipoWS
	 * @return
	 */
	public Map<String, Object> calcularPpl(Long idEnvio, Long idPoliza, BigDecimal descuentoColectivo,String realPath)throws BusinessException{
		logger.debug("init - calcularPpl");
		GregorianCalendar gcIni = null;
		GregorianCalendar gcFin = null;
		Map<String, Object> resultado = null;
		try {
				gcIni = new GregorianCalendar();
				
				resultado = servicioCalcularHelper.doWork(idEnvio, idPoliza, descuentoColectivo, realPath, polizaDao);
			
				gcFin = new GregorianCalendar();
			
				Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
				logger.debug("Tiempo de la llamada al servicio de calculo: " + tiempo + MILISEG);
			
		} catch (ValidacionServiceException vse) {
			throw new BusinessException("Se ha producido un error en el servicio de calculo de la poliza complementaria",vse);
		}
		logger.debug("end - calcularPpl");
		return resultado;
	}
	
	public Long generateAndSaveXMLPoliza(final Poliza poliza, final ComparativaPoliza cp, final String webServiceToCall,
			final boolean aplicaDtoRec, final Map<Character, ComsPctCalculado> comsPctCalculado)
			throws BusinessException, ValidacionPolizaException {
		logger.debug("init - generateAndSaveXMLPoliza");
		String tipoEnvio;
		Long id = null;
		try {
			if (webServiceToCall.equals(Constants.WS_VALIDACION)){
				tipoEnvio = "VL";
			}
			else{
				tipoEnvio = "CL";
			}
			// Calculo de CPM permitidos			
			logger.debug("Se cargan los CPM permitidos para la poliza - idPoliza: " + poliza.getIdpoliza() + ", codModulo: " + cp.getId().getCodmodulo());
			List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null, poliza.getIdpoliza(), cp.getId().getCodmodulo());
			
			Usuario usuario = poliza.getUsuario();
			String envio = WSUtils.generateXMLPoliza(poliza, cp, webServiceToCall, polizaDao, listaCPM, usuario,
					aplicaDtoRec, comsPctCalculado);
			
			id = this.guardarXmlEnvio(poliza, envio, tipoEnvio, cp.getId().getCodmodulo(), cp.getId().getFilacomparativa());
			
		} catch (DAOException dao) {
			logger.debug("Se ha producido un error al  guardar el xml de la poliza principal: " + dao.getMessage() );
			throw new BusinessException ("Se ha producido un error al guardar el xml de la poliza principal",dao);
		} catch (ValidacionPolizaException e) {
			throw e;
		} catch (Exception ex) {
			logger.debug("Se ha producido un error al  generar el xml de la poliza principal: " + ex.getMessage() );
			throw new BusinessException ("Se ha producido un error al generar el xml de la poliza principal",ex);
		}
		logger.debug("end - generateAndSaveXMLPoliza");
		return id;
	}
	
	/**
	 * metodo que graba en un clob el xml de calculo para nuestra poliza complementaria
	 * @param resultadoCalculo
	 * @param poliza
	 * @throws XmlException 
	 */
	public void guardarXmlCalculo(Map<String, Object> resultadoCalculo,Poliza poliza) throws BusinessException, XmlException{
		logger.debug("init - guardarXmlCalculo");
		try {
			if (resultadoCalculo.containsKey(CALC) || poliza == null) return;

			EnvioAgroseguro envioAgroseguro = null;
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo = null;
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument polizaDocument = null;
			
			polizaCalculo = (es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza)resultadoCalculo.get(CALC);
			polizaDocument = es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument.Factory.parse(polizaCalculo.getDomNode());
			
			for (EnvioAgroseguro auxEnvio : poliza.getEnvioAgroseguros()){
				if (auxEnvio.getCodmodulo().equals(poliza.getCodmodulo())){
					envioAgroseguro = auxEnvio;
					break;
				}
			}
			
			if (envioAgroseguro != null) {
				polizaDao.saveEnvioAgroseguro(envioAgroseguro);
				polizaDao.actualizaXmlEnvio(envioAgroseguro.getId(), null,	polizaDocument.toString());
				polizaDao.evictEnvio(envioAgroseguro);
			}
			
		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error al grabar el resultado de calculo.", dao);
		} catch (XmlException xmle) {
			throw new XmlException("Se ha producido un error en el parseo del XML de calculo",xmle);
		}
		logger.debug("end - guardarXmlCalculo");
	}
	
	/**
	 * MÃ©todo para guardar el XML de envio a agroseguro
	 * @param poliza Identificador de la poliza asociada
	 * @param envio XML de envio a Agroseguro
	 * @param webServiceToCall Servicio web a llamar
	 * @param codmodulo Modulo asociado al envio
	 * @param filacomparativa Comparativa asociada al envio
	 * @throws DAOException Error al guardar el envio en la base de datos
	 */
	private Long guardarXmlEnvio(Poliza poliza, String envio,String tipoEnvio, String codmodulo,BigDecimal filacomparativa) throws DAOException {
		logger.debug("init - guardarXmlEnvio");
		EnvioAgroseguro envioAgroseguro = new EnvioAgroseguro();
		envioAgroseguro.setPoliza(poliza);
		envioAgroseguro.setFechaEnvio((new GregorianCalendar()).getTime());
		
		envioAgroseguro.setXml(Hibernate.createClob(" "));
		
		envioAgroseguro.setTipoenvio(tipoEnvio);
		envioAgroseguro.setCodmodulo(codmodulo);
		envioAgroseguro.setFilacomparativa(filacomparativa);
		
		EnvioAgroseguro newEnvio = (EnvioAgroseguro) this.polizaDao.saveEnvioAgroseguro(envioAgroseguro);
		this.polizaDao.actualizaXmlEnvio(newEnvio.getId(), envio, null);
		this.polizaDao.evictEnvio(newEnvio);
		logger.debug("end - guardarXmlEnvio");
		return newEnvio.getId();
	}
	
	/**
	 * 
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	public Poliza borrarCaracExplotacion(Poliza poliza) throws BusinessException{
		logger.debug("init - borrarCaracExplotacion");
		try {
			
			for (ComparativaPoliza cp : poliza.getComparativaPolizas()){
				if (cp.getId().getCodconcepto().equals(CONST_CARACT_EXPLOTACION)){
					this.polizaDao.deleteCaractExplotacion(poliza.getIdpoliza());
					poliza.getComparativaPolizas().remove(cp);
					break;
				}				
			}
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al borrar la caracteristica de explotacion: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al borrar la caracteristica de explotacion", dao);
		}
		logger.debug("init - borrarCaracExplotacion");
		return poliza;
	}	
	
	public Poliza calcularNuevaCaractExpl(Poliza poliza, ComparativaPoliza cp, String realPath) 
			throws BusinessException, ValidacionPolizaException {
		logger.debug("init - calcularNuevaCaractExpl");
		
		try {
			// Calculo de CPM permitidos			
			logger.debug("Se cargan los CPM permitidos para la poliza - idPoliza: " + poliza.getIdpoliza() + ", codModulo: " + cp.getId().getCodmodulo());
			List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null, poliza.getIdpoliza(), cp.getId().getCodmodulo());
			
			//2. Genero el XML para enviar al calculo de la caracteristica de la explotacion
			Usuario usuario = poliza.getUsuario();
			String xml = WSUtils.generateXMLPoliza(poliza, cp, Constants.WS_CARACT_EXPLOTACION, polizaDao, listaCPM, usuario, false, null);
			
			//3. Calculo la nueva caracteristica de la explotacion. Utilizamos la misma configuracion que para llamar
			//al servicio de validacion en local o en remoto
			BigDecimal caractExlp = this.calcularCaractExplotacion(xml, realPath, poliza);
			
			poliza = this.guardarCaractExpl(poliza,cp,caractExlp);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al guardar la caracterï¿½stica de explotaciï¿½n: " + be.getMessage());
			throw be;
		} catch (ValidacionPolizaException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Se ha producido un error al generar el XML de la poliza: " + e.getMessage());
			throw new BusinessException("Se ha producido un error al generar el XML de la poliza", e);
		}
		
		logger.debug("end - calcularNuevaCaractExpl");
		
		return poliza;
		
	}	
	
	private Poliza guardarCaractExpl(Poliza poliza, ComparativaPoliza cp,BigDecimal caractExlp) throws BusinessException {
		logger.debug("init - guardarCaractExpl");
		
		ComparativaPoliza compP = new ComparativaPoliza();
		ComparativaPolizaId compID = new ComparativaPolizaId();
		
		try {			
			
			compP.setConceptoPpalModulo(cp.getConceptoPpalModulo());
			compP.setDescvalor(cp.getDescvalor());
			compP.setPoliza(poliza);
			compP.setRiesgoCubierto(cp.getRiesgoCubierto());
			compP.setRiesgoCubiertoModulo(cp.getRiesgoCubiertoModulo());
			compP.setDescvalor("Caracteristica de Explotacion");			
			
			compID.setCodconcepto(CONST_CARACT_EXPLOTACION);
			compID.setCodconceptoppalmod(cp.getId().getCodconceptoppalmod());
			compID.setCodmodulo(cp.getId().getCodmodulo());
			compID.setCodriesgocubierto(cp.getId().getCodriesgocubierto());
			compID.setCodvalor(caractExlp);
			compID.setFilacomparativa(cp.getId().getFilacomparativa());
			compID.setFilamodulo(cp.getId().getFilamodulo());
			compID.setIdpoliza(cp.getId().getIdpoliza());
			compID.setLineaseguroid(cp.getId().getLineaseguroid());
			
			// asignamos a la comparativa la caracteristica de la explotacion
			compP.setId(compID);

			//AÃ±ado la comparativa a la poliza
			poliza.getComparativaPolizas().add(compP);
			
			// guardamos la caracteristica de la explotacion
			polizaDao.saveOrUpdate(compP);			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al guardar la caracteristica de explotacion: " + dao.getMessage());
			throw new BusinessException("Se ha producido un error al guardar la caracteristica de explotacion", dao);
		}
		logger.debug("end - guardarCaractExpl");	
		return poliza;
	}

	/**
	 * 
	 * @param xml
	 * @param realPath
	 * @param tipoWS
	 * @return
	 * @throws BusinessException 
	 */
	private BigDecimal calcularCaractExplotacion(String xml, String realPath, Poliza poliza ) throws BusinessException{
		logger.debug("init - calcularCaractExplotacion");	
		GregorianCalendar gcIni = new GregorianCalendar();
		
		BigDecimal carExpl = new BigDecimal(0);
		
		/** Pet. 57626 ** MODIF TAM (18/05/2020) ** Inicio **/
		try{
		
			ComparativaPoliza comparativa = new ComparativaPoliza();
		
			for(ComparativaPoliza comparativaPoliza : poliza.getComparativaPolizas()){
				comparativa = comparativaPoliza;
			}

			String tipoEnvio ="CC";
		
			BigDecimal filaComparativa = null;
			if (poliza.getLinea().getEsLineaGanadoCount() > 0) {
				filaComparativa = new BigDecimal(comparativa.getId().getIdComparativa());
			} else {
				filaComparativa = comparativa.getId().getFilacomparativa();
			}
			
			Long idEnvio = this.guardarXmlEnvio(poliza, xml, tipoEnvio, comparativa.getId()	.getCodmodulo(), filaComparativa);
			
			
			/** Pet. 57626 ** MODIF TAM (14/05/2020) ** Fin **/
			
			//BigDecimal carExpl = servicioCaractExplotacionHelper.doWork(xml, realPath, tipoWS);
			
			Map<String, Object> retorno = servicioCaractExplotacionHelper.doWork(xml, realPath, idEnvio, polizaDao);
			
			carExpl = (BigDecimal) retorno.get("carExpl");
			/** Pet. 57626 ** MODIF TAM (18/05/2020) ** Fin **/
			
			GregorianCalendar gcFin = new GregorianCalendar();
			
			Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
			logger.debug("Tiempo de la llamada al servicio calculo de la caracteristica de la explotacion: " + tiempo + MILISEG);
			
			logger.debug("end - calcularCaractExplotacion");	


		}catch(Exception ex){
			logger.error(ex);
		    throw new BusinessException("[CaracteristicaExplotacionManager] deleteCaractExplotacion() - error ",ex);
		}
		
		
		return carExpl;
	}
	
	/* P0078691 ** MODIF TAM (14.12.2021) ** Inicio */
	/**
	 * 
	 * @param xml
	 * @param realPath
	 * @param tipoWS
	 * @return
	 * @throws BusinessException 
	 */
	public BigDecimal calcularCaractExplotacionAnx(String xml, String realPath, Poliza poliza ) throws BusinessException{
		
		logger.debug("WebServicesPplManager - calcularCaractExplotacionAnx [INIT]");	
		GregorianCalendar gcIni = new GregorianCalendar();
		
		BigDecimal carExpl = new BigDecimal(0);
		
		try{
		
			logger.debug("Valor de xml:"+xml);
			Map<String, Object> retorno = servicioCaractExplotacionHelper.doWorkAnx(xml, realPath, polizaDao);
			
			carExpl = (BigDecimal) retorno.get("carExpl");
			
			GregorianCalendar gcFin = new GregorianCalendar();
			
			Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
			logger.debug("Tiempo de la llamada al servicio calculo de la caracteristica de la explotacion: " + tiempo + MILISEG);
			
			logger.debug("end - calcularCaractExplotacion");	


		}catch(Exception ex){
			logger.error(ex);
		    throw new BusinessException("[CaracteristicaExplotacionManager] deleteCaractExplotacion() - error ",ex);
		}
		
		
		
		
		return carExpl;
	}
	
	/**
	 * Metodo que relleno el objeto condensadordeflujo para pintar en la jsp la distribucion de costes
	 * @param resultadoCalculo
	 * @return
	 */
	public FluxCondensatorObject generarCondensadorDeFlujo(	Map<String, Object> resultadoCalculo,Poliza poliza) throws Exception{
		logger.debug("init - generarCondensadorDeFlujo");
		FluxCondensatorObject fluxCondensatorObject  = new FluxCondensatorObject();
		DatosCalculo		  datosCalculo			 = null;
		DistribucionCoste     distribCostes			 = null;
		BonificacionAsegurado bonifAseg              = null;
		RecargoAsegurado	  recargoAseg			 = null;
		Consorcio			  consorcio			 	 = null;
		SubvencionCCAA[]      subCCAA				 = null;
		SubvencionEnesa[]     subEnesa				 = null;
		
		es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo = null;
		try {
			if(!resultadoCalculo.containsKey(CALC))
				throw new NullPointerException("No se ha podido recuperar el resultado de la llamada al servicio Web de Calculo");
						
			polizaCalculo = (es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza)resultadoCalculo.get(CALC);
			
			/* SONAR Q */
			datosCalculo = obtenerDatosCalculo(datosCalculo, polizaCalculo);
			/* SONAR Q FIN */
			
	  		if (datosCalculo != null) {
	  			distribCostes = datosCalculo.getDistribucionCoste();
	  			fluxCondensatorObject.setPctDescContColectiva(StringUtils.formatPercent(datosCalculo.getPctDescuentoColectivo()));
	  		}
	  		
	  		if (distribCostes != null) {
	  			fluxCondensatorObject.setPrimaComercial(distribCostes.getPrimaComercial().toString());
	  			fluxCondensatorObject.setPrimaNeta(distribCostes.getPrimaNeta().toString());
	  			fluxCondensatorObject.setCosteNeto(distribCostes.getCosteNeto().toString());
	  			fluxCondensatorObject.setBonifMedidaPreventiva(distribCostes.getBonificacionMedidasPreventivas().getBonifMedPreventivas().toString());
	  			fluxCondensatorObject.setDescuentoContColectiva(distribCostes.getDescuento().getDescuentoColectivo().toString());
	  			
	  			bonifAseg   = distribCostes.getBonificacionAsegurado();
	  			recargoAseg = distribCostes.getRecargoAsegurado();
	  			consorcio   = distribCostes.getConsorcio();
	  			subCCAA     = distribCostes.getSubvencionCCAAArray();
	  			subEnesa    = distribCostes.getSubvencionEnesaArray();
	  			/* SONAR Q */
	  			fluxCondensatorObject = obtenerfluxCondensatorObject(fluxCondensatorObject, bonifAseg, recargoAseg, consorcio);
	  			/* SONAR Q FIN */
	  			if (subCCAA != null && subCCAA.length > 0) {
	  				Map<String,BigDecimal> ccaa = new HashMap<String, BigDecimal>();
	  				/* SONAR Q */
	  				ccaa = obtenerCcaa(subCCAA, ccaa);
	  				/* SONAR Q FIN*/
	  				
	  				Iterator<Map.Entry<String, BigDecimal>> it = ccaa.entrySet().iterator();
	  				while (it.hasNext()) {
	  					Map.Entry<String, BigDecimal> e = (Map.Entry<String, BigDecimal>)it.next();
		  				String key = e.getKey();
		  				BigDecimal valor = e.getValue();
		  				
		  				fluxCondensatorObject.addSubCCAA(key.charAt(0)+"-"+this.getCCAA(key.charAt(0)),valor.toString());
	  				}
	  				
	  			}
	  			/* SONAR Q */
	  			fluxCondensatorObject = informarSubEnexafluxCond(fluxCondensatorObject, subEnesa);
	  			/* SONAR Q */
	  			fluxCondensatorObject.setImporteTomador(distribCostes.getCargoTomador().toString());
	  		}
	  		
	  		ModuloId moduloId = new ModuloId();
	  		moduloId.setCodmodulo(poliza.getCodmodulo());
	  		moduloId.setLineaseguroid(poliza.getLinea().getLineaseguroid());
	  		Modulo modulo = (Modulo)polizaDao.get(Modulo.class, moduloId);
	  		
	  		fluxCondensatorObject.setDescModulo(modulo.getDesmodulo());
	  		fluxCondensatorObject.setIdModulo(poliza.getCodmodulo());
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error al generar el condensador de flujo",ex);
			throw new Exception ("Se ha producido un error al generar el condensador de flujo",ex);
		}
		logger.debug("end - generarCondensadorDeFlujo");
		return fluxCondensatorObject;
	}
	
	/**
	 * Obtiene la descripcion del Codigo de Subvencion Enesa indicado
	 * @param tipo
	 * @return
	 */
	public String getDescripcionEnesa (BigDecimal tipo) throws Exception{
		logger.debug("init - getDescripcionEnesa");
		String dev ="";
		try {
			TipoSubvencionEnesa tipoEnesa = (TipoSubvencionEnesa)polizaDao.getObject(TipoSubvencionEnesa.class, tipo);
			if (tipoEnesa != null)
				dev = tipoEnesa.getDestiposubvenesa();
		}
		catch(Exception e) {
			logger.error("Error al obtener la descripcion de la subvencion ENESA " + tipo,e);
			throw new Exception("Error al obtener la descripcion de la subvencion ENESA ",e);
		}
		logger.debug("end - getDescripcionEnesa");
		return dev;
	}
	
	/**
	 * Obtiene la descripcion del Codigo de Subvencion CCAA indicado
	 * @param tipo
	 * @return
	 */
	public String getDescripcionCCAA(BigDecimal tipo)throws Exception{
		logger.debug("init - getDescripcionCCAA");
		String dev ="";
		try {
			TipoSubvencionCCAA tipoEnesa = (TipoSubvencionCCAA)polizaDao.getObject(TipoSubvencionCCAA.class, tipo);
			if (tipoEnesa != null)
				dev = tipoEnesa.getDestiposubvccaa();
		}
		catch(Exception e) {
			logger.error(ERROR + tipo,e);
			throw new Exception(ERROR,e);
		}
		logger.debug("end - getDescripcionCCAA");
		return dev;
	}
	
	/**
	 * Obtiene la descripcion del organismo
	 * @param codOrganismo
	 * @return
	 * @throws Exception
	 */
	public String getCCAA(Character codOrganismo)throws Exception {
		logger.debug("init - getCCAA");
		String desc ="";
		try {
			
			Organismo organismo = (Organismo) polizaDao.getObject(Organismo.class,codOrganismo);
			if(organismo != null){
				desc = organismo.getDesorganismo();
			}
		} catch (Exception e) {
			logger.error(ERROR + codOrganismo,e);
			throw new Exception("Error al obtener la descripcion del organismo ",e);
		}
		logger.debug("end - getCCAA");
		return desc;
	}
	
	/**
	 * Metodo que transforma nuestro condensador de flujo en una distribucion de costes para insertar en la BD
	 * @param distribucionCoste
	 * @param idpoliza
	 * @param fluxCondensatorObject
	 * @param request
	 * @throws Exception
	 */
	private void generarDistribucionCoste(
			final com.rsi.agp.dao.tables.poliza.DistribucionCoste distribucionCoste,
			final FluxCondensatorObject fluxCondensatorObject,
			final BigDecimal filacomparativa) throws Exception {
		
		logger.debug("init - generarDistribucionCoste");
		Set<DistCosteSubvencion> distCosteSubvencions = new HashSet<DistCosteSubvencion>();
		DistCosteSubvencion distCosteSubvencion = null;
		BigDecimal bonifasegurado = null;
		BigDecimal bonifmedpreventivas= null;
		BigDecimal cargotomador= null; 
		BigDecimal dtocolectivo= null;
		BigDecimal pctbonifasegurado= null;
		BigDecimal pctrecargoasegurado = null;
		BigDecimal primacomercial = null;
		BigDecimal primaneta = null;
		BigDecimal reaseguro = null;
		BigDecimal recargo = null;
		BigDecimal recargoasegurado= null; 
		BigDecimal ventanilla = null;
		BigDecimal costeneto= null;
		
		Map<String, String> subCCA = null;
		Map<String, String> subEnesa = null;
		
		/* SONAR Q */
		Map<String,BigDecimal> datos = obtenerdatosVarios(fluxCondensatorObject);
		bonifasegurado = datos.get("bonifasegurado");
		bonifmedpreventivas = datos.get("");
		cargotomador = datos.get("cargotomador");
		dtocolectivo = datos.get("dtocolectivo");
		pctbonifasegurado = datos.get("pctbonifasegurado");
		pctrecargoasegurado = datos.get("pctrecargoasegurado");
		primacomercial = datos.get("primacomercial");
		primaneta = datos.get("primaneta");
		reaseguro = datos.get("reaseguro");
		recargo = datos.get("recargo");
		recargoasegurado = datos.get("recargoasegurado");
		costeneto = datos.get("costeneto");
		/* SONAR Q FIN */
		
		ventanilla =new BigDecimal(0);
		
		distribucionCoste.setBonifasegurado(bonifasegurado);
		distribucionCoste.setBonifmedpreventivas(bonifmedpreventivas);
		distribucionCoste.setCargotomador(cargotomador);
		distribucionCoste.setCosteneto(costeneto);
		
		distribucionCoste.setDtocolectivo(dtocolectivo);
		distribucionCoste.setPctbonifasegurado(pctbonifasegurado);
		distribucionCoste.setPctrecargoasegurado(pctrecargoasegurado);
		distribucionCoste.setPrimacomercial(primacomercial);
		distribucionCoste.setPrimaneta(primaneta);
		distribucionCoste.setReaseguro(reaseguro);
		distribucionCoste.setRecargo(recargo);
		distribucionCoste.setRecargoasegurado(recargoasegurado);
		distribucionCoste.setVentanilla(ventanilla);
		
		distribucionCoste.setCodmodulo(fluxCondensatorObject.getIdModulo());
		distribucionCoste.setFilacomparativa(filacomparativa);
		
		subEnesa = fluxCondensatorObject.getSubvEnesa();
		
		/* SONAR Q */
		distCosteSubvencions =obtenerDistCosteSubvencion(distCosteSubvencions, subEnesa, distCosteSubvencion, distribucionCoste);
		/* SONAR Q */
		
		subCCA = fluxCondensatorObject.getSubvCCAA();
		String valorCCAA = null;
		String tipoCCAA = null;
		if(!subCCA.isEmpty()){
			for(String key: subEnesa.keySet()){
				valorCCAA = subEnesa.get(key);
				tipoCCAA = key.split("-")[0];
				
				distCosteSubvencion = new DistCosteSubvencion();
				distCosteSubvencion.setCodorganismo(new Character(tipoCCAA.charAt(0)));
				distCosteSubvencion.setCodtiposubv(null);
				distCosteSubvencion.setImportesubv(new BigDecimal(valorCCAA));
				distCosteSubvencion.setDistribucionCoste(distribucionCoste);
				
				distCosteSubvencions.add(distCosteSubvencion);
			}
		}
		
		distribucionCoste.setDistCosteSubvencions(distCosteSubvencions);
		logger.debug("end - generarDistribucionCoste");
	}
	
	/**
	 * Obtiene una representacion en String de la clave de la comparativa
	 * @param cpId
	 * @return
	 */
	public String getCPId(ComparativaPolizaId cpId) {
		return "" +  cpId.getIdpoliza() +
					 cpId.getLineaseguroid() +
					 cpId.getCodmodulo() + 
					 cpId.getFilacomparativa();
	}
	
	
	
	/** SONAR Q ** MODIF TAM (17.12.2021) ** Inicio **/
	private static FluxCondensatorObject obtenerfluxCondensatorObject(FluxCondensatorObject fluxCondensatorObject, BonificacionAsegurado bonifAseg, 
			RecargoAsegurado recargoAseg, Consorcio consorcio) {
		
		if (bonifAseg != null) {
			fluxCondensatorObject.setBonifAsegurado(bonifAseg.getBonifAsegurado().toString());
			fluxCondensatorObject.setPctBonifAsegurado(bonifAseg.getPctBonifAsegurado().toString());	  			
		}
		if (recargoAseg != null) {
			fluxCondensatorObject.setRecargoAsegurado(recargoAseg.getRecargoAsegurado().toString());
			fluxCondensatorObject.setPctRecargoAsegurado(recargoAseg.getPctRecargoAsegurado().toString()); 
		}
		if (consorcio != null) {
			fluxCondensatorObject.setConsorcioReaseguro(consorcio.getReaseguro().toString());
			fluxCondensatorObject.setConsorcioRecargo(consorcio.getRecargo().toString());
		}
		return fluxCondensatorObject;
	}
	
	private static Map<String,BigDecimal> obtenerCcaa(SubvencionCCAA[] subCCAA, Map<String,BigDecimal> ccaa) {
		
		for (int i =0; i<subCCAA.length;i++){
			ccaa.put(subCCAA[i].getCodigoOrganismo(), new BigDecimal(0));
		}
		for (int i =0; i<subCCAA.length;i++){
			ccaa.put(subCCAA[i].getCodigoOrganismo(), 
					ccaa.get(subCCAA[i].getCodigoOrganismo()).add(subCCAA[i].getSubvencionCA()));  					
		}
		return ccaa;
	}
	
	private FluxCondensatorObject informarSubEnexafluxCond(FluxCondensatorObject fluxCondensatorObject, SubvencionEnesa[] subEnesa) throws Exception {
		if (subEnesa != null && subEnesa.length > 0) {
			for (int i =0; i<subEnesa.length;i++){
				fluxCondensatorObject.addSubEnesa(subEnesa[i].getTipo()+ "-" + this.getDescripcionEnesa(new BigDecimal(subEnesa[i].getTipo())), 
						 					subEnesa[i].getSubvencionEnesa().toString());
				
			}
		}
		return fluxCondensatorObject;
	}
	
	private static DatosCalculo obtenerDatosCalculo(DatosCalculo datosCalculo, es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaCalculo) {
		if (polizaCalculo != null)
			datosCalculo = polizaCalculo.getDatosCalculo();
		return datosCalculo;
	}
	
	/* SONAR Q */
	private static Set<DistCosteSubvencion> obtenerDistCosteSubvencion(Set<DistCosteSubvencion> distCosteSubvencions , Map<String, String> subEnesa,
																		DistCosteSubvencion distCosteSubvencion,
																		final com.rsi.agp.dao.tables.poliza.DistribucionCoste distribucionCoste){
		String valorEnesa = null;
		String tipoEnesa = null;
		if(!subEnesa.isEmpty()){
			for(String key: subEnesa.keySet()){
				valorEnesa = subEnesa.get(key);
				tipoEnesa = key.split("-")[0];
				
				distCosteSubvencion = new DistCosteSubvencion();
				distCosteSubvencion.setCodorganismo(new Character('0'));
				distCosteSubvencion.setCodtiposubv(new BigDecimal(tipoEnesa));
				distCosteSubvencion.setImportesubv(new BigDecimal(valorEnesa));
				distCosteSubvencion.setDistribucionCoste(distribucionCoste);
				
				distCosteSubvencions.add(distCosteSubvencion);
			}
		}
		return distCosteSubvencions;
	}
	
	/* SONAR Q */
	private static Map<String,BigDecimal> obtenerdatosVarios(FluxCondensatorObject fluxCondensatorObject){
		
		Map<String,BigDecimal> datos = new HashMap<String, BigDecimal>(); 
		
		BigDecimal bonifasegurado = null;
		BigDecimal bonifmedpreventivas= null;
		BigDecimal cargotomador= null; 
		BigDecimal dtocolectivo= null;
		BigDecimal pctbonifasegurado= null;
		BigDecimal pctrecargoasegurado = null;
		BigDecimal primacomercial = null;
		BigDecimal primaneta = null;
		BigDecimal reaseguro = null;
		BigDecimal recargo = null;
		BigDecimal recargoasegurado= null; 
		BigDecimal costeneto= null;
	
		if(!fluxCondensatorObject.getBonifAsegurado().equals("N/D")) {
			bonifasegurado = new BigDecimal(fluxCondensatorObject.getBonifAsegurado());
			datos.put("bonifasegurado", bonifasegurado); 
		}
		
		if(!fluxCondensatorObject.getBonifMedidaPreventiva().equals("N/D")) {
			bonifmedpreventivas = new BigDecimal(fluxCondensatorObject.getBonifMedidaPreventiva());
			datos.put("bonifmedpreventivas", bonifmedpreventivas);
		}
		
		if(!fluxCondensatorObject.getImporteTomador().equals("N/D")) {
			cargotomador = new BigDecimal(fluxCondensatorObject.getImporteTomador());
			datos.put("cargotomador", cargotomador);
		}
		
		if(!fluxCondensatorObject.getDescuentoContColectiva().equals("N/D")) {
			dtocolectivo = new BigDecimal(fluxCondensatorObject.getDescuentoContColectiva());
			datos.put("dtocolectivo", dtocolectivo);
		}
		
		if(!fluxCondensatorObject.getPctBonifAsegurado().equals("N/D")) {
			pctbonifasegurado =  new BigDecimal(fluxCondensatorObject.getPctBonifAsegurado());
			datos.put("pctbonifasegurado", pctbonifasegurado);
		}
		
		if(!fluxCondensatorObject.getBonifAsegurado().equals("N/D")) {
			pctrecargoasegurado =new BigDecimal(fluxCondensatorObject.getBonifAsegurado());
			datos.put("pctrecargoasegurado",pctrecargoasegurado);
		}
		
		if(!fluxCondensatorObject.getPrimaComercial().equals("N/D")) {
			primacomercial = new BigDecimal(fluxCondensatorObject.getPrimaComercial());
			datos.put("primacomercial", primacomercial);
		}
		if(!fluxCondensatorObject.getPrimaNeta().equals("N/D")) {
			primaneta =new BigDecimal(fluxCondensatorObject.getPrimaNeta());
			datos.put("primaneta", primaneta);
		}
		if(!fluxCondensatorObject.getConsorcioReaseguro().equals("N/D")) {
			reaseguro = new BigDecimal(fluxCondensatorObject.getConsorcioReaseguro());
			datos.put("reaseguro", reaseguro);
		}
		
		if(!fluxCondensatorObject.getConsorcioRecargo().equals("N/D")) {
			recargo = new BigDecimal(fluxCondensatorObject.getConsorcioRecargo());
			datos.put("recargo", recargo);
		}
		
		if(!fluxCondensatorObject.getRecargoAsegurado().equals("N/D")) {
			recargoasegurado = new BigDecimal(fluxCondensatorObject.getRecargoAsegurado());
			datos.put("recargoasegurado", recargoasegurado);
		}
		if(!fluxCondensatorObject.getCosteNeto().equals("N/D")) {
			costeneto = new BigDecimal(fluxCondensatorObject.getCosteNeto());
			datos.put("costeneto", costeneto);
		}
		return datos;
	}
			
	/** SONAR Q ** MODIF TAM (27.10.2021) ** Fin **/

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setSwAnexoRCHelper(SWAnexoRCHelper swAnexoRCHelper) {
		this.swAnexoRCHelper = swAnexoRCHelper;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}	
	
	public void setDistribucionCosteDAO(IDistribucionCosteDAO distribucionCosteDAO) {
		this.distribucionCosteDAO = distribucionCosteDAO;
	}
}
