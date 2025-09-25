package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.ICaracteristicaExplotacionDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;

/**
 * 
 * MANAGER
 * 
 * Clase para operar con la caracteristica de explotacion
 * 
 */
public class CaracteristicaExplotacionManager {
	
	
	/* CONSTANTS
	 ------------------------------------------------------------------------ */
	private static final Log logger = LogFactory.getLog(CaracteristicaExplotacionManager.class);
	
	/* VARIABLES
	 ------------------------------------------------------------------------ */
	private IPolizaDao polizaDao;
	private ICaracteristicaExplotacionDao caracteristicaExplotacionDao;
	private ServicioCaractExplotacionHelper servicioCaractExplotacionHelper = new ServicioCaractExplotacionHelper();
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;

	/* MÉTODOS PÚBLICOS
	 ------------------------------------------------------------------------ */

	/**
	 * Validar caracteristica explotacion
	 */
	public boolean validarCaractExplotacion(Poliza poliza) throws BusinessException {
		
		boolean result = true;
		
		try{
		
			result = caracteristicaExplotacionDao.validarCaractExplotacion(poliza);

		}catch(Exception ex){
			logger.error(ex);
		    throw new BusinessException("[CaracteristicaExplotacionManager] validarCaractExplotacion() - error ",ex);
		}
		
		return result;
	}
	
	/**
	 * Aplica y borra carac. de explotacion, devuelve si aplica o no.
	 */
	public boolean aplicaYBorraCaractExplocion(Poliza poliza) throws BusinessException {
		
		boolean result;
		
		try{
		
			result = caracteristicaExplotacionDao.aplicaYBorraCaractExplocion(poliza);
		
		}catch(Exception ex){
			logger.error(ex);
		    throw new BusinessException("[CaracteristicaExplotacionManager] aplicaYBorraCaractExplocion() - error ",ex);
		}
		
		return result;
	}

	/**
	 * MÃ©todo para comprobar si una poliza necesita la caracteristica de la
	 * explotacion en los datos variables de cobertura
	 * 
	 */
	public boolean aplicaCaractExplotacion(final Long lineaseguroid) throws BusinessException {
		
		boolean result;
		
		try{
			
			result = caracteristicaExplotacionDao.aplicaCaractExplotacion(lineaseguroid);
			
		}catch(Exception ex){
			logger.error(ex);
		    throw new BusinessException("[CaracteristicaExplotacionManager] aplicaCaractExplotacion() - error ",ex);
		}
		
		return result;
	}

	/**
	 * Borra carac. explotacion
	 */
	public void deleteCaractExplotacion(final Long idpoliza) throws BusinessException {
		
		try {
			
			caracteristicaExplotacionDao.deleteCaractExplotacion(idpoliza);
			
		}catch(Exception ex){
			logger.error(ex);
		    throw new BusinessException("[CaracteristicaExplotacionManager] deleteCaractExplotacion() - error ",ex);
		}	
	}

	public BigDecimal calcularCaractExplotacion(Poliza poliza, String realPath) throws BusinessException, ValidacionPolizaException {
		
		BigDecimal carExpl;
		ComparativaPoliza comparativaPolizaAux = new ComparativaPoliza();
		
		try{
			
			for(ComparativaPoliza comparativaPoliza : poliza.getComparativaPolizas()){
				comparativaPolizaAux = comparativaPoliza;
			}
	
			carExpl = this.calcularCaractExplotacion(poliza, realPath, comparativaPolizaAux, null);

		} catch(BusinessException ex){
			logger.error(ex);
		    throw ex;
		} catch(ValidacionPolizaException ex){
			logger.error(ex);
		    throw ex;
		}

		return carExpl;
	}
	
	/** DAA 20/03/2013
	 * 	Realiza el calculo de la Caracteristica de Explotacion
	 * @throws ValidacionPolizaException 
	 */
	public BigDecimal calcularCaractExplotacion(Poliza poliza, String realPath, ComparativaPoliza comparativa, Poliza polizaPpl) 
			throws BusinessException, ValidacionPolizaException {
		
		logger.debug ("**@@** Dentro de calcularCaractExplotacion");
		BigDecimal carExpl = new BigDecimal(0);
		List<BigDecimal> listaCPM = null;
		String xml;
		try {			
			// calcula CPM
			if(comparativa != null){
				logger.debug("(1) Se cargan los CPM permitidos para la poliza - idPoliza: " + poliza.getIdpoliza() + ", codModulo: " + comparativa.getId().getCodmodulo());
				listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null, poliza.getIdpoliza(), comparativa.getId().getCodmodulo());
			}
			else{
				logger.debug("(2) Se cargan los CPM permitidos para la poliza - idPoliza: " + poliza.getIdpoliza() + ", codModulo: " + polizaPpl.getCodmodulo());
				listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null, poliza.getIdpoliza(), polizaPpl.getCodmodulo());
			}			

			logger.debug ("**@@** Antes de obtener la lista de comparativas");
			if (!poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
				
			}
			
			BigDecimal filaComparativa = null;
			
			Long idEnvio;
			
			//Genera xml de calculoExpl dependiendo de si es la poliza principal o la complementaria
			Usuario usuario = poliza.getUsuario();
			if (poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
				logger.debug ("**@@** Antes de generar el XML de la Póliza Principal");
				
				xml = WSUtils.generateXMLPoliza(poliza, comparativa, Constants.WS_CARACT_EXPLOTACION, polizaDao, listaCPM, usuario, false, null);
			
				if (poliza.getLinea().getEsLineaGanadoCount() > 0) {
					filaComparativa = new BigDecimal(comparativa.getId().getIdComparativa());
				} else {
					filaComparativa = comparativa.getId().getFilacomparativa();
				}
				
				String tipoEnvio ="CE";
				
				idEnvio = this.guardarXmlEnvio(poliza, xml, tipoEnvio, poliza.getCodmodulo(), filaComparativa);

			}else{
				/* Pet.57626 ** MODIF TAM (28.05.2020) ** Inicio */ 
				// comprobamos que la poliza principal tiene comparativas y si tiene cojo una 
				//para enviarla por parámetro
				List<ComparativaPoliza> listComparativasPoliza = poliza
						.getComparativaPolizas() != null ? Arrays.asList(poliza
						.getPolizaPpal().getComparativaPolizas()
						.toArray(new ComparativaPoliza[] {}))
								: new ArrayList<ComparativaPoliza>();
						ComparativaPoliza cp = new ComparativaPoliza();
			
				if(listComparativasPoliza.size()>0) {
					cp = listComparativasPoliza.get(0);
				}
				
				logger.debug ("**@@** Antes de generar el XML de la Póliza Complementaria");
				
				xml = WSUtils.generateXMLPolizaCpl(poliza, polizaPpl, cp, Constants.WS_CARACT_EXPLOTACION, polizaDao, listaCPM, usuario, null, false, null, null);

				if (poliza.getLinea().getEsLineaGanadoCount() > 0) {
					filaComparativa = new BigDecimal(cp.getId().getIdComparativa());
				} else {
					filaComparativa = cp.getId().getFilacomparativa();
				}
				
				String tipoEnvio ="CE";
				
				idEnvio = this.guardarXmlEnvio(poliza, xml, tipoEnvio, poliza.getCodmodulo(), null);

			}
			/** Pet. 57626 ** MODIF TAM (14/05/2020) ** Inicio **/
			/** Asignamos tipo de Envio CE -Características Explotación **/
			
			
			/** Pet. 57626 ** MODIF TAM (14/05/2020) ** Fin **/
			
			//calcula carExpl
			GregorianCalendar gcIni = new GregorianCalendar();
			
			/** Pet. 57626 ** MODIF TAM (18/05/2020) ** Inicio **/
			//carExpl = servicioCaractExplotacionHelper.doWork(xml, realPath, tipoWS.getValidacion());
			Map<String, Object> retorno = servicioCaractExplotacionHelper.doWork(xml, realPath, idEnvio, polizaDao);
			
			carExpl = (BigDecimal) retorno.get("carExpl");
			AcuseRecibo acuse = (AcuseRecibo) retorno.get("acuse");
			grabaAcuseRecibo(acuse, poliza, idEnvio);
			/** Pet. 57626 ** MODIF TAM (18/05/2020) ** Fin **/
			
			GregorianCalendar gcFin = new GregorianCalendar();
			
			Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
			logger.info("Tiempo de la llamada al servicio calculo de la caracteristica de la explotacion: " + tiempo + " milisegundos");
			
		}
		catch (ValidacionPolizaException ex){
			logger.error(ex);
		    throw ex;
		}
		catch(Exception ex){
			logger.error(ex);
		    throw new BusinessException("[CaracteristicaExplotacionManager] calcularCaractExplotacion() - error ",ex);
		}
		
		return carExpl;
	}
	
	
	/*** Pet. 57626 ** MODIF TAM (14.05.2020) ** Inicio ***/
	public Long guardarXmlEnvio(Poliza poliza, String envio, String tipoEnvio,
			String codmodulo, BigDecimal filacomparativa) throws DAOException {
		EnvioAgroseguro envioAgroseguro = new EnvioAgroseguro();
		envioAgroseguro.setPoliza(poliza);
		envioAgroseguro.setFechaEnvio((new GregorianCalendar()).getTime());

		envioAgroseguro.setXml(Hibernate.createClob(" "));
		// envioAgroseguro.setXml(envio);

		envioAgroseguro.setTipoenvio(tipoEnvio);
		envioAgroseguro.setCodmodulo(codmodulo);
		envioAgroseguro.setFilacomparativa(filacomparativa);

		EnvioAgroseguro newEnvio = (EnvioAgroseguro) this.polizaDao
				.saveEnvioAgroseguro(envioAgroseguro);
		// EnvioAgroseguro newEnvio = (EnvioAgroseguro)
		// this.polizaDao.saveEnvioAgroseguro(envioAgroseguro, envio, null);
		this.polizaDao.actualizaXmlEnvio(newEnvio.getId(), envio, null);
		this.polizaDao.evictEnvio(newEnvio);
		// poliza.getEnvioAgroseguros().add(newEnvio);
		// para actualizar el envio en la poliza vuelvo a traerla de base de
		// datos.
		poliza = this.polizaDao.getPolizaById(poliza.getIdpoliza());
		return newEnvio.getId();
	}
	
	public void grabaAcuseRecibo(AcuseRecibo acuse, Poliza poliza, Long idEnvio)
			throws DAOException {
		// ASF - 10/2/2014 - corrección para que grabe correctamente los acuses
		// en las llamadas a validación.

		this.polizaDao.actualizaXmlEnvio(idEnvio, null, acuse.toString());
	}
	/*** Pet. 57626 ** MODIF TAM (14.05.2020) ** Fin ***/
	
	/* SETTERS FOR SPRING IOC
	 ------------------------------------------------------------------------ */
	public void setCaracteristicaExplotacionDao(
			ICaracteristicaExplotacionDao caracteristicaExplotacionDao) {
		this.caracteristicaExplotacionDao = caracteristicaExplotacionDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}
}
