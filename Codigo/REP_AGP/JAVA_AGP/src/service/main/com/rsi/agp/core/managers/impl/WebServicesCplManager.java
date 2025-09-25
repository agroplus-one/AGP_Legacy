package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Hibernate;
import org.springframework.util.CollectionUtils;

import com.rsi.agp.core.exception.BusinessException;
//** Pet. 22208 ** MODIF TAM (22.03.2018) ** Inicio **/
import com.rsi.agp.core.exception.ConfirmacionServiceException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.DistribucionCostesException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.exception.ValidacionServiceException;
//** Pet. 22208 ** MODIF TAM (22.03.2018) ** Fin **/
import com.rsi.agp.core.jmesa.dao.IImportesFraccDao;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.FluxCondensatorObject;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IDistribucionCosteDAO;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.models.sbp.ISimulacionSbpDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.Organismo;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionCCAA;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.DistCosteSubvencion;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015BonifRec;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015GrupoNegocio;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015Subvencion;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument;

public class WebServicesCplManager implements IManager {
	private Log logger = LogFactory.getLog(WebServicesCplManager.class);
	private IPolizaDao polizaDao;
	private ServicioValidarCplHelper        servicioValidarCplHelper        = new ServicioValidarCplHelper();
	private ServicioCalcularCplHelper       servicioCalcularCplHelper       = new ServicioCalcularCplHelper();
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	private IDistribucionCosteDAO distribucionCosteDAO;
	private IImportesFraccDao importesFraccDao;
	private PolizaManager polizaManager;
	private PagoPolizaManager pagoPolizaManager;
	
	/** Pet. 22208 ** MODIF TAM (22.03.2018) ** Inicio **/
	private ServicioConfirmarCplHelper      servicioConfirmarCplHelper      = new ServicioConfirmarCplHelper();
	private ISeleccionPolizaDao seleccionPolizaDao;
	private IHistoricoEstadosManager historicoEstadosManager;
	/*PET-63699 DNF 14/05/2020*/ 
	private ISimulacionSbpDao simulacionSbpDao; 
	/*FIN PET-63699 DNF 14/05/2020*/ 
	
	/** Pet. 22208 ** MODIF TAM (22.03.2018) ** Fin **/
	
	
	/**
	 * Metodo que llama al SW de validación para una situación actualizada de la póliza complementaria
	 * @param poliza
	 * @param realPath
	 * @param tipoWS
	 * @return
	 * @throws BusinessException
	 */
	public AcuseRecibo validarSituacionActualizada (es.agroseguro.contratacion.Poliza poliza, 
			AnexoModificacion am, String realPath) throws BusinessException {
		
		GregorianCalendar gcIni = null;
		GregorianCalendar gcFin = null;
		AcuseRecibo acuse = null;
		try {
				gcIni =  new GregorianCalendar();
				
				acuse = servicioValidarCplHelper.doWorkValCplAnexo(poliza, am, realPath);
				
				gcFin = new GregorianCalendar();
				
				Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
				logger.debug("Tiempo de la llamada al servicio de validacion: " + tiempo + " milisegundos");
				
		} catch (ValidacionServiceException vse) {
			throw new BusinessException("Se ha producido un error en el servicio de validacion de una situación actualizada de la póliza", vse);
		}
		
		return acuse;
	}
	
	/** Pet. 22208 ** MODIF TAM (22.03.2018) ** Inicio **/
	/**
	 * Metodo que llama al SW de Confirmacion, para finalizar la poliza a traves de la llamada al webservice
	 * @param poliza
	 * @param realPath
	 * @return
	 * @throws DAOException 
	 */
	public AcuseRecibo ConfirmarPolizaCpl(Long idEnvio, Long idPoliza, Poliza polizaDef, String realPath, 
			                                HttpServletRequest request)
		throws BusinessException, DAOException {
		
		AcuseRecibo acuse = servicioConfirmarCplHelper.doWork(idEnvio, idPoliza, realPath, polizaDao);
		GregorianCalendar gcIni = new GregorianCalendar();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		try{
		
		   GregorianCalendar gcFin = new GregorianCalendar();
		
   		   // Incluimos la validacion del acuse de recibo he insertamos registro de recepcion
		   if (acuse != null) {
			   logger.debug("Acuse de la llamada al servicio de SW Confirmacion recibido correctamente");
 
			   //guardamos el acuse de recibo
			   logger.debug("Obtenemos la poliza para actualizar el acuse en BD: " + idPoliza);
			   Poliza poliza = polizaDao.getPolizaById(idPoliza);
			
	  		   grabaAcuseRecibo(acuse, poliza, idEnvio);
		   } else {
			   logger.debug("El acuse de la llamada al servicio de SW Confirmacion es NULO");
		   }

		   Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
		   logger.debug("Tiempo de la llamada al servicio de confirmacion: " + tiempo + " milisegundos");
		
		
   		   //***** ACTUALIZAMOS EL ESTADO DE LA POLIZA *****//
		   EstadoPoliza estado;
	       //Poliza p = seleccionPolizaManager.getPolizaById(poliza.getIdpoliza());
	       Poliza p = (Poliza)seleccionPolizaDao.get(Poliza.class, polizaDef.getIdpoliza());
	       logger.debug("Id poliza complementaria: " + polizaDef.getIdpoliza());
	    
	       // ACEPTADA POR AGROSEGUROS
	       logger.debug("Acuse de recibo estado: " + acuse.getDocumentoArray(0).getEstado());
	       if (acuse.getDocumentoArray(0).getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO){
	    	   // Actualizamos el estado de la poliza a ESTADO_POLIZA_DEFINITIVA
		       estado = new EstadoPoliza(Constants.ESTADO_POLIZA_DEFINITIVA);
		       logger.debug("estado: " + estado.getIdestado());
		       
		   }else{
   		   // RECHAZADA POR AGROSEGUROS	
			   //Actualizamos el estado de la poliza a estado ESTADO_POLIZA_ENVIADA_ERRONEA
			   estado = new EstadoPoliza(Constants.ESTADO_POLIZA_ENVIADA_ERRONEA);
			   logger.debug("Rechazada por Agroseguro: " + estado.getIdestado());
		   }
		   p.setEstadoPoliza(estado); // set estado poliza
		
		   /// NO GRABA EL ESTADO DEFINITIVO... ESTO ESTA CASCANDO
		   //seleccionPolizaDao.evict(p);
		   seleccionPolizaDao.saveOrUpdate(p);
		   logger.debug("seleccionPolizaDao.saveOrUpdate: idpoliza: " + p.getIdpoliza() + " -- Estado: " + p.getEstadoPoliza());
		
		   polizaDef.setEstadoPoliza(estado);
		   
		   // INSERTAMOS NUEVO REGISTRO EN LA TABLA DE HISTORICO.
		   Set<PagoPoliza> pagos = p.getPagoPolizas();
		
  		   BigDecimal tipoPago =  null;
		   Date fechaPrimerPago = null;
		   Date fechaSegundoPago = null;
		   BigDecimal pctPrimerPago = null;
		   BigDecimal pctSegundoPago = null;
		   for (PagoPoliza pago:pagos){
			   if (pago.getTipoPago() != null)
				   tipoPago = pago.getTipoPago();
			   if (pago.getFecha() !=  null)
				   fechaPrimerPago = pago.getFecha();
			   if (pago.getPctprimerpago() != null)
				   pctPrimerPago = pago.getPctprimerpago();
			   if (pago.getFechasegundopago() != null)
				   fechaSegundoPago = pago.getFechasegundopago();			
			   if (pago.getPctsegundopago() != null)
				   pctSegundoPago = pago.getPctsegundopago();				
		   }
    
	       // ACEPTADA POR AGROSEGUROS
	       //if (acuseRecibo_swConfirmacion !=null){
		   if (acuse.getDocumentoArray(0).getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO){
		          historicoEstadosManager.insertaEstadoPoliza(p.getIdpoliza(),usuario.getCodusuario(),Constants.ESTADO_POLIZA_DEFINITIVA,
			                                               tipoPago,fechaPrimerPago,
				                                           pctPrimerPago,fechaSegundoPago,pctSegundoPago);
	       }else{
	              historicoEstadosManager.insertaEstadoPoliza(p.getIdpoliza(),usuario.getCodusuario(),Constants.ESTADO_POLIZA_ENVIADA_ERRONEA ,
	    		     									   tipoPago,fechaPrimerPago,
	    		    									   pctPrimerPago,fechaSegundoPago,pctSegundoPago);

	       }
		   
		   
		   /**DNF 13/11/2019**ESC-7560********/
		   if (acuse.getDocumentoArray(0).getEstado() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO){   
		       //ACTUALIZAMOS EL ESTADO A PAGADO Y LA FECHA DE PAGO A SYSDATE
		       logger.debug("Tipo pago: " + p.getPagoPolizas().iterator().next().getTipoPago());
		       if (p.getPagoPolizas().iterator().next().getTipoPago().compareTo(new BigDecimal(2)) == 0) {
		    	   	logger.debug("Llamando a actualizarPolizaPagada...");
		    	   	logger.debug("idpoliza: " + p.getIdpoliza());
		    	   	logger.debug("fecha de envio: " + p.getEnvioAgroseguros().iterator().next().getFechaEnvio());
		    	   	
					polizaDao.actualizarPolizaPagada(p.getIdpoliza(), p.getEnvioAgroseguros().iterator().next().getFechaEnvio());					
		       }
				/* PET-63699 DNF 14/05/2020 */
				Long idSbp = simulacionSbpDao.getPolizaSbpId(p.getPolizaPpal().getIdpoliza());
				if (idSbp != null) {
					// actualizaremos el indicador correspondiente para que en el proceso batch se
					// genera el suplemento correspondiente.
					// El indicador que hay que actualizar es marcar el campo GEN_SPL_CPL = S de la
					// tabla de Suplementos.
					simulacionSbpDao.updateGenSplCpl(idSbp);
				}
				/* FIN PET-63699 DNF 14/05/2020 */
		   }
		   /**FIN DNF 13/11/2019**ESC-7560****/
		   
		} catch (ConfirmacionServiceException cse) {
			throw new BusinessException("Se ha producido un error en el servicio de Confirmacion de la poliza complementaria",cse);
		}
		logger.debug("end - ConfirmarCpl");
		
		
		return acuse;
	}   

	 
	 /**
		 * 
		 * @param acuse
		 * @param idPoliza
		 */
	public void grabaAcuseRecibo(AcuseRecibo acuse, Poliza poliza, Long idEnvio)
	   	throws DAOException {
	    // ASF - 10/2/2014 - correccion para que grabe correctamente los acuses
		// en las llamadas a validacion.

		this.polizaDao.actualizaXmlEnvio(idEnvio, null, acuse.toString());
	}
	/** Pet. 22208 ** MODIF TAM (*/

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
	public void guardarDistribucionCosteCpl(final FluxCondensatorObject fluxCondensatorObject,
			DistribucionCoste2015 distribucionCoste2015, final Long idpoliza, final BigDecimal filacomparativa,
			BigDecimal codplan, FinanciacionDocument financiacion, Long idComparativa) throws BusinessException {

		logger.debug("init - guardarDistrubucionCosteCpl");
		
		try {
			com.rsi.agp.dao.tables.poliza.DistribucionCoste distribucionCoste = new com.rsi.agp.dao.tables.poliza.DistribucionCoste();
			// GUARDAMOS LA DISTRIBUCION DE COSTES
			Poliza poliza = polizaDao.getPolizaById(idpoliza);
			// Para polizas < 2015
			if (codplan.compareTo(Constants.PLAN_2015)== -1){
				logger.debug("Borramos distribucion de costes anterior");
				poliza = borrarDistribucionCosteAnterior(poliza);
				
				logger.debug("guardamos distribucion de costes ");
				
				generarDistribucionCoste(distribucionCoste, fluxCondensatorObject,
						filacomparativa);
				distribucionCosteDAO.saveDistribucionCoste(distribucionCoste,
						idpoliza);
			//para polizas => 2015
			}else {
				logger.debug("## ------------- 156 -- Poliza: " + idpoliza + " - Modulo: " + poliza.getCodmodulo() + " - FilaComparativa: " + filacomparativa);
				distribucionCosteDAO.deleteDistribucionCoste2015(idpoliza, poliza.getCodmodulo(), idComparativa);
				poliza.getDistribucionCoste2015s().clear();
				//com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015 distribucionCoste2015 = new com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015() ;
				
				BigDecimal idComparativaAux = null;
				if(idComparativa != null) {
					idComparativaAux = new BigDecimal(idComparativa);
				}
				
				generarDistribucionCoste2015(distribucionCoste2015, fluxCondensatorObject,	filacomparativa, idComparativaAux, financiacion);
				
				distribucionCosteDAO.saveDistribucionCoste2015(distribucionCoste2015,idpoliza);
				
			}
			if (financiacion != null && poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size()>0) {
				poliza.getPagoPolizas().iterator().next().setImporte(financiacion.getFinanciacion().getPeriodoArray(0).getPago().getImporte());
			}
			
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
	 * Metodo que transforma nuestro condensador de flujo en una distribucion de costes del 2015 para insertar en la BD
	 * @param distribucionCoste2015
	 * @param idpoliza
	 * @param fluxCondensatorObject
	 * @param request
	 * @throws Exception
	 */
	private void generarDistribucionCoste2015(
			DistribucionCoste2015 distribucionCoste2015,
			FluxCondensatorObject fluxCondensatorObject,
			BigDecimal filacomparativa,
			BigDecimal idComparativa,
			FinanciacionDocument  financiacion) {
		
		logger.info("init - generarDistribucionCoste2015 - FluxCondensatorObject = " + fluxCondensatorObject.toString());
		Set<DistCosteSubvencion2015> distCosteSubvencions = new HashSet<DistCosteSubvencion2015>();
		Set<BonificacionRecargo2015> boniRecargoss = new HashSet<BonificacionRecargo2015>();
		Set<DistCosteParcela2015> distCosteParcela2015s = new HashSet<DistCosteParcela2015>();
		
		DistCosteSubvencion2015 distCosteSubvencion = null;
		BigDecimal costeTomador = null;
		BigDecimal primaComercial= null;
		BigDecimal primaNeta= null; 
		BigDecimal recargoConsorcio= null;
		BigDecimal reciboPrima= null;
		BigDecimal totalCosteTomador = null;
		BigDecimal recargoAval = null;
		BigDecimal recargoFraccionamiento = null;
		BigDecimal comisionE = null;
		BigDecimal comisionES = null;
		
		
		Map<String, String> subCCA = null;
		Map<String, String> subEnesa = null;
		if(!fluxCondensatorObject.getCosteTomador().equals("N/D"))
			costeTomador = new BigDecimal(fluxCondensatorObject.getCosteTomador()).setScale(2);
		if(!fluxCondensatorObject.getPrimaComercial().equals("N/D"))
			primaComercial =new BigDecimal(fluxCondensatorObject.getPrimaComercial()).setScale(2);
		if(!fluxCondensatorObject.getPrimaNeta().equals("N/D"))
			primaNeta =new BigDecimal(fluxCondensatorObject.getPrimaNeta()).setScale(2);
		if(!fluxCondensatorObject.getRecargoConsorcio().equals("N/D"))
			recargoConsorcio =new BigDecimal(fluxCondensatorObject.getRecargoConsorcio()).setScale(2);

		if(!fluxCondensatorObject.getReciboPrima().equals("N/D"))
			reciboPrima =new BigDecimal(fluxCondensatorObject.getReciboPrima()).setScale(2);
		if(!fluxCondensatorObject.getTotalCosteTomador().equals("N/D"))
			totalCosteTomador =new BigDecimal(fluxCondensatorObject.getTotalCosteTomador()).setScale(2);
		if(!fluxCondensatorObject.getRecargoAval().equals("N/D"))
			recargoAval =new BigDecimal(fluxCondensatorObject.getRecargoAval()).setScale(2);
		if(!fluxCondensatorObject.getRecargoFraccionamiento().equals("N/D"))
			recargoFraccionamiento =new BigDecimal(fluxCondensatorObject.getRecargoFraccionamiento()).setScale(2);
		
		NumberFormat nf = NumberFormat.getInstance(new Locale("es", "ES"));		
		if(!fluxCondensatorObject.getComMediadorE().equals("N/D"))
			try {
				comisionE = new BigDecimal(nf.parse(fluxCondensatorObject.getComMediadorE()).toString());
			} catch (ParseException e) {
				logger.error("Error al convertir la comision entidad -> " + fluxCondensatorObject.getComMediadorE());
			}
		if(!fluxCondensatorObject.getComMediadorE_S().equals("N/D"))
			try {
				comisionES = new BigDecimal(nf.parse(fluxCondensatorObject.getComMediadorE_S()).toString());
			} catch (ParseException e) {
				logger.error("Error al convertir la comision entidad -> " + fluxCondensatorObject.getComMediadorE_S());
			}		
		
		distribucionCoste2015.setGrupoNegocio(new Character('1'));
		distribucionCoste2015.setCostetomador(costeTomador);
		distribucionCoste2015.setPrimacomercial(primaComercial);
		distribucionCoste2015.setPrimacomercialneta(primaNeta);
		distribucionCoste2015.setRecargoconsorcio(recargoConsorcio);
		if (comisionE != null)
			distribucionCoste2015.setImpComsEntidad(comisionE);
		if (comisionES != null)
			distribucionCoste2015.setImpComsESMed(comisionES);
		
		distribucionCoste2015.setReciboprima(reciboPrima);
		distribucionCoste2015.setTotalcostetomador(totalCosteTomador);
		if (recargoAval != null)
			distribucionCoste2015.setRecargoaval(recargoAval);
		if (recargoFraccionamiento != null)
			distribucionCoste2015.setRecargofraccionamiento(recargoFraccionamiento);
		
		
		distribucionCoste2015.setCodmodulo(fluxCondensatorObject.getIdModulo());
		distribucionCoste2015.setFilacomparativa(filacomparativa);
		distribucionCoste2015.setIdcomparativa(idComparativa);
		
		if (financiacion != null) {
			distribucionCoste2015.setImportePagoFracc(financiacion.getFinanciacion().getPeriodoArray(0).getPago().getImporte());
			distribucionCoste2015.setTotalcostetomador(financiacion.getFinanciacion().getPeriodoArray(0).getDistribucionCoste().getTotalCosteTomador());
			distribucionCoste2015.setCostetomador(financiacion.getFinanciacion().getCosteTomador());
			distribucionCoste2015.setPeriodoFracc(financiacion.getFinanciacion().getPeriodoFraccionamiento().intValue());
			distribucionCoste2015.setOpcionFracc(fluxCondensatorObject.getOpcionFracc());
			distribucionCoste2015.setValorOpcionFracc(fluxCondensatorObject.getValorOpcionFracc());
		}
		else{
			if (fluxCondensatorObject.getImportePagoFracc() != null)
				distribucionCoste2015.setImportePagoFracc(new BigDecimal(fluxCondensatorObject.getImportePagoFracc()));
			if (fluxCondensatorObject.getPeriodoFracc() != null)
				distribucionCoste2015.setPeriodoFracc(new Integer(fluxCondensatorObject.getPeriodoFracc()));
			if (fluxCondensatorObject.getOpcionFracc() != null)
				distribucionCoste2015.setOpcionFracc(fluxCondensatorObject.getOpcionFracc());
			if (fluxCondensatorObject.getValorOpcionFracc() != null)
				distribucionCoste2015.setValorOpcionFracc(fluxCondensatorObject.getValorOpcionFracc());
		}
		
		
		// guardamos la distribucion de coste de las subvenciones Enesa 2015
		subEnesa = fluxCondensatorObject.getSubvEnesa();
		
		String valorEnesa = null;
		String tipoEnesa = null;
		if(!subEnesa.isEmpty()){
			for(String key: subEnesa.keySet()){
				valorEnesa = subEnesa.get(key);
				tipoEnesa = key.split("-")[0];
				
				distCosteSubvencion = new DistCosteSubvencion2015();
				distCosteSubvencion.setCodorganismo(new Character('0'));
				distCosteSubvencion.setCodtiposubv(new BigDecimal(tipoEnesa));
				distCosteSubvencion.setImportesubv(new BigDecimal(valorEnesa));
				distCosteSubvencion.setDistribucionCoste2015(distribucionCoste2015);
				
				distCosteSubvencions.add(distCosteSubvencion);
			}
		}

		// guardamos la distribucion de coste de las subvenciones CCAA
		subCCA = fluxCondensatorObject.getSubvCCAA();
		String valorCCAA = null;
		String tipoCCAA = null;
		if(!subCCA.isEmpty()){
			for(String key: subCCA.keySet()){
				valorCCAA = subCCA.get(key);
				tipoCCAA = key.split("-")[0];
				
				distCosteSubvencion = new DistCosteSubvencion2015();
				distCosteSubvencion.setCodorganismo(new Character(tipoCCAA.charAt(0)));
				distCosteSubvencion.setCodtiposubv(null);
				distCosteSubvencion.setImportesubv(new BigDecimal(valorCCAA));
				distCosteSubvencion.setDistribucionCoste2015(distribucionCoste2015);
				
				distCosteSubvencions.add(distCosteSubvencion);
			}
		}
		distribucionCoste2015.setDistCosteSubvencion2015s(distCosteSubvencions);
		
		//BonificacionRecargo
		String codRec;
		Map<String, String> boniRecargo = fluxCondensatorObject.getBoniRecargo1();
		if (boniRecargo != null) {
			for(Object key: boniRecargo.keySet()){
				codRec = ((String) key).split("-")[0];
				BonificacionRecargo2015 bon = new BonificacionRecargo2015();
				bon.setDistribucionCoste2015(distribucionCoste2015);
				bon.setCodigo(new BigDecimal(codRec));
				bon.setImporte(new BigDecimal(boniRecargo.get(key)));
				boniRecargoss.add(bon);
			}
		}
		distribucionCoste2015.setBonificacionRecargo2015s(boniRecargoss);
		
		
		for (DistCosteParcela2015 parcela : fluxCondensatorObject.getDistCosteParcela2015s()) {
			
			DistCosteParcela2015 dCPar = new DistCosteParcela2015();
			
			dCPar.setDistribucionCoste2015(distribucionCoste2015);
			dCPar.setCapitalAsegurado( parcela.getCapitalAsegurado());
			dCPar.setHoja(parcela.getHoja());
			dCPar.setNumero(parcela.getNumero());
			dCPar.setPrecio(parcela.getPrecio());
			dCPar.setProduccion(parcela.getProduccion());
			dCPar.setTasacom(parcela.getTasacom());
			dCPar.setTasacombase(parcela.getTasacombase());
			dCPar.setTasacombaseneta(parcela.getTasacombaseneta());
			dCPar.setTipo(parcela.getTipo());
			
			dCPar.setCostetomador(parcela.getCostetomador());
			dCPar.setPrimacomercial(parcela.getPrimacomercial()) ;
			dCPar.setPrimacomercialneta(parcela.getPrimacomercialneta() );
			dCPar.setRecargoaval(parcela.getRecargoaval());
			dCPar.setRecargoconsorcio(parcela.getRecargoconsorcio());
			dCPar.setRecargofraccionamiento(parcela.getRecargofraccionamiento());
			dCPar.setReciboprima(parcela.getReciboprima() ) ;
			dCPar.setTotalcostetomador(parcela.getTotalcostetomador());
			
			if (!CollectionUtils.isEmpty(parcela.getDistCosteSubvs())) {
				
				Set<DistCosteParcela2015Subvencion> subvSet = parcela.getDistCosteSubvs();
				for (DistCosteParcela2015Subvencion subv : subvSet) {
					
					logger.debug("Preparando coste parcela por subvencion");
					DistCosteParcela2015Subvencion parcSubv = new DistCosteParcela2015Subvencion();
					parcSubv.setCodTipo(subv.getCodTipo());
					parcSubv.setCodOrganismo(subv.getCodOrganismo());
					parcSubv.setCodTipoSubv(subv.getCodTipoSubv());
					parcSubv.setImporte(subv.getImporte());
					parcSubv.setPctSubvencion(subv.getPctSubvencion());
					parcSubv.setValorUnitario(subv.getValorUnitario());
					parcSubv.setDistCosteParcela2015(dCPar);
					dCPar.getDistCosteSubvs().add(parcSubv);
				}
			}
			if (!CollectionUtils.isEmpty(parcela.getDistCosteBonifRecs())) {
				
				Set<DistCosteParcela2015BonifRec> brSet = parcela.getDistCosteBonifRecs();
				for (DistCosteParcela2015BonifRec bonRec : brSet) {
					
					logger.debug("Preparando coste parcela por bonificacion/recargo");
					DistCosteParcela2015BonifRec br = new DistCosteParcela2015BonifRec();
					br.setCodigo(bonRec.getCodigo());
					br.setImporte(bonRec.getImporte());
					br.setDistCosteParcela2015(dCPar);
					dCPar.getDistCosteBonifRecs().add(br);
				}
			}
			if (!CollectionUtils.isEmpty(parcela.getDistCosteGns())) {
				
				Set<DistCosteParcela2015GrupoNegocio> gnSet = parcela.getDistCosteGns();
				for (DistCosteParcela2015GrupoNegocio costeGN : gnSet) {
					
					logger.debug("Preparando coste parcela por grupo de negocio");
					DistCosteParcela2015GrupoNegocio parcGN = new DistCosteParcela2015GrupoNegocio();
					parcGN.setGruponegocio(costeGN.getGruponegocio());
					parcGN.setPrimacomercial(costeGN.getPrimacomercial());
					parcGN.setPrimacomercialneta(costeGN.getPrimacomercialneta());
					parcGN.setRecargoconsorcio(costeGN.getRecargoconsorcio());
					parcGN.setReciboprima(costeGN.getReciboprima());
					parcGN.setCostetomador(costeGN.getCostetomador());
					parcGN.setDistCosteParcela2015(dCPar);
					dCPar.getDistCosteGns().add(parcGN);
				}
			}
			
			distCosteParcela2015s.add(dCPar);
		}
		distribucionCoste2015.setDistCosteParcela2015s(distCosteParcela2015s);
		
		logger.debug("end - generarDistribucionCoste");
		
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
		
		logger.info("init - generarDistribucionCoste - FluxCondensatorObject = " + fluxCondensatorObject.toString());
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
				
		if(!fluxCondensatorObject.getBonifAsegurado().equals("N/D"))
			bonifasegurado = new BigDecimal(fluxCondensatorObject.getBonifAsegurado());
		if(!fluxCondensatorObject.getBonifMedidaPreventiva().equals("N/D"))
			bonifmedpreventivas =new BigDecimal(fluxCondensatorObject.getBonifMedidaPreventiva());
		if(!fluxCondensatorObject.getImporteTomador().equals("N/D"))
			cargotomador =new BigDecimal(fluxCondensatorObject.getImporteTomador());
		if(!fluxCondensatorObject.getCosteNeto().equals("N/D"))
			costeneto =new BigDecimal(fluxCondensatorObject.getCosteNeto());

		if(!fluxCondensatorObject.getDescuentoContColectiva().equals("N/D"))
			dtocolectivo =new BigDecimal(fluxCondensatorObject.getDescuentoContColectiva());
		if(!fluxCondensatorObject.getPctBonifAsegurado().equals("N/D"))
			pctbonifasegurado =new BigDecimal(fluxCondensatorObject.getPctBonifAsegurado());
		if(!fluxCondensatorObject.getPctRecargoAsegurado().equals("N/D"))
			pctrecargoasegurado =new BigDecimal(fluxCondensatorObject.getPctRecargoAsegurado());
		if(!fluxCondensatorObject.getPrimaComercial().equals("N/D"))
			primacomercial =new BigDecimal(fluxCondensatorObject.getPrimaComercial());
		if(!fluxCondensatorObject.getPrimaNeta().equals("N/D"))
			primaneta =new BigDecimal(fluxCondensatorObject.getPrimaNeta());
		if(!fluxCondensatorObject.getConsorcioReaseguro().equals("N/D"))
			reaseguro =new BigDecimal(fluxCondensatorObject.getConsorcioReaseguro());
		if(!fluxCondensatorObject.getConsorcioRecargo().equals("N/D"))
			recargo =new BigDecimal(fluxCondensatorObject.getConsorcioRecargo());
		if(!fluxCondensatorObject.getRecargoAsegurado().equals("N/D"))
			recargoasegurado =new BigDecimal(fluxCondensatorObject.getRecargoAsegurado());
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
		
		//DAA 17/01/2013 guardamos la distribucion de coste de las subvenciones Enesa
		subEnesa = fluxCondensatorObject.getSubvEnesa();
		
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

		//DAA 17/01/2013 guardamos la distribucion de coste de las subvenciones CCAA
		subCCA = fluxCondensatorObject.getSubvCCAA();
		String valorCCAA = null;
		String tipoCCAA = null;
		if(!subCCA.isEmpty()){
			for(String key: subCCA.keySet()){
				valorCCAA = subCCA.get(key);
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
	 * Metodo que llama al SW de validacion  para poliza complementaria
	 * @param idPoliza
	 * @param codModulo
	 * @param realPath
	 * @param tipoWS
	 * @return
	 * @throws DAOException 
	 */
	public AcuseRecibo validarCpl(Long idEnvio, Long idPoliza, String realPath,Character tipoWS) throws BusinessException, DAOException {
		logger.debug("init - validarCpl");
		GregorianCalendar gcIni = null;
		GregorianCalendar gcFin = null;
		AcuseRecibo acuse = null;
		try {
				gcIni =  new GregorianCalendar();
				acuse = servicioValidarCplHelper.doWork(idEnvio, idPoliza, realPath, polizaDao, tipoWS);
				gcFin = new GregorianCalendar();
				
				Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
				logger.debug("Tiempo de la llamada al servicio de validacion: " + tiempo + " milisegundos");
				
				//guardamos el acuse de recibo
				Poliza poliza = polizaDao.getPolizaById(idPoliza);
				guardarAcuseRecibo(acuse, poliza);
				
		} catch (ValidacionServiceException vse) {
			throw new BusinessException("Se ha producido un error en el servicio de validacion de la poliza complementaria",vse);
		}
		logger.debug("end - validarCpl");
		return acuse;
	}
	
	/**
	 * Metodo que llama al SW de calculo para poliza complementaria
	 * @param idPoliza
	 * @param codModulo
	 * @param descuentoColectivo
	 * @param realPath
	 * @param tipoWS
	 * @return
	 */
	public Map<String, Object> calcularCpl(Long idEnvio, Poliza poliza, BigDecimal descuentoColectivo,String realPath)throws BusinessException{
		logger.debug("init - calcularCpl");
		GregorianCalendar gcIni = null;
		GregorianCalendar gcFin = null;
		Map<String, Object> acuse = null;
		try {
			gcIni = new GregorianCalendar();
			
			acuse = servicioCalcularCplHelper.doWork(idEnvio, poliza.getIdpoliza(), descuentoColectivo, realPath, polizaDao);
			
			/* Pet. 57626 ** MODIF TAM (02.06.2020) ** Inicio */
			/* El calculo de polizas agricolas complementarias ya va con el Formato Unificado */
			this.grabaPolizaCalculo((es.agroseguro.distribucionCostesSeguro.impl.PolizaImpl) acuse.get("calculo"), poliza, idEnvio);
			/* Pet. 57626 ** MODIF TAM (02.06.2020) ** Fin */
			gcFin = new GregorianCalendar();
		
			Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
			logger.debug("Tiempo de la llamada al servicio de calculo: " + tiempo + " milisegundos");
			
		} catch (ValidacionServiceException vse) {
			throw new BusinessException("Se ha producido un error en el servicio de calculo de la poliza complementaria",vse);
		} catch (DAOException e) {
			throw new BusinessException("Se ha producido un error al guardar el resultado del calculo", e);
		}
		logger.debug("end - calcularCpl");
		return acuse;
	}
	
	public Long generateAndSaveXMLPolizaCpl(Poliza poliza, String webServiceToCall,
			Map<Character, ComsPctCalculado> comsPctCalculado, String realPath) 
			throws DAOException, ValidacionPolizaException, BusinessException {
		logger.debug("init - generateAndSaveXMLPolizaCpl");
		String tipoEnvio;
		if (webServiceToCall.equals(Constants.WS_VALIDACION)) {			
			tipoEnvio = "VL";
			if (poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size()>0){
				PagoPoliza pago=poliza.getPagoPolizas().iterator().next();
				if(null!=pago && pago.getFormapago().equals('F')){
					pago.setFormapago('C');
				}
			}
		} else if (webServiceToCall.equals(Constants.WS_PASAR_DEFINITIVA)) {
			tipoEnvio = "PD";
		} else if (webServiceToCall.equals(Constants.WS_CONFIRMACION)) {
			tipoEnvio = "CO";
		} else {
			tipoEnvio = "CL";
		}
		
		// Calculo de CPM permitidos			
		logger.debug("Se cargan los CPM permitidos para la poliza - idPoliza: " + poliza.getIdpoliza() + ", codModulo: " + poliza.getCodmodulo());
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null, poliza.getIdpoliza(), poliza.getCodmodulo());
		
		//Comprobamos el tipo de financiacion
		Boolean saeca = polizaManager.esFinanciadaSaeca(poliza.getLinea().getLineaseguroid(),poliza.getColectivo().getSubentidadMediadora());
		
		/* Pet. 57626 ** MODIF TAM (28.05.2020) ** Inicio */
		/* Se obtiene la comparativa de la poliza para pasarla por parametro */
		//comprobamos que la poliza principal tiene comparativas y si tiene cojo una 
		//para poder guardar la carct de explot.
		List<ComparativaPoliza> listComparativasPoliza = poliza
				.getComparativaPolizas() != null ? Arrays.asList(poliza
				.getPolizaPpal().getComparativaPolizas()
				.toArray(new ComparativaPoliza[] {}))
				: new ArrayList<ComparativaPoliza>();
		ComparativaPoliza cp = new ComparativaPoliza();
		if(listComparativasPoliza.size()>0)
			cp = listComparativasPoliza.get(0);		
		
		//Genero el xml
		Usuario usuario = poliza.getUsuario();
		String envio = WSUtils.generateXMLPolizaCpl(poliza, poliza.getPolizaPpal(), cp, webServiceToCall, polizaDao,
				listaCPM, usuario, saeca, true, comsPctCalculado, realPath);
		
		/* Pet. 57626 ** MODIF TAM (24/07/2020) ** Inicio */
		/* Antes de lanzar llamada al S.W de Calculo se cambia en el xml el nombre del esquema
		 * pero al grabar el envio se graba con el esquema incorrecto. (ServicioCalcularHelper.java (doWork)*/
		if (webServiceToCall.equals(Constants.WS_CALCULO)) {
			envio = envio.replace("xmlns=\"http://www.agroseguro.es/Contratacion\"", "xmlns=\"http://www.agroseguro.es/PresupuestoContratacion\"");
		}
		/* Pet. 57626 ** MODIF TAM (24/07/2020) ** Fin */
		
		//Insertar el fichero xml generado en la tabla de envios a agroseguro y devolver el identificador de la insercion
		Long id = guardarXmlEnvio(poliza, envio, tipoEnvio,poliza.getCodmodulo(), null);
		logger.debug("end - generateAndSaveXMLPolizaCpl");
		return id;			
	}
	
	/**
	 * Metodo para guardar el XML de envio a agroseguro
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
		this.polizaDao.evictPoliza(poliza);
		logger.debug("end - guardarXmlEnvio");
		return newEnvio.getId();
	}
		
	/**
	 * metodo que graba en un clob el xml de validacion para nuestra poliza complementaria
	 * @param resultadoCalculo
	 * @param poliza
	 * @throws XmlException 
	 */
	public void guardarAcuseRecibo(AcuseRecibo acuse ,Poliza poliza) throws BusinessException{
		logger.debug("init - guardarAcuseRecibo");
		
		if  (poliza == null) return;
		EnvioAgroseguro envioAgroseguro = null;
		
		for (EnvioAgroseguro auxEnvio : poliza.getEnvioAgroseguros()){
			if (auxEnvio.getCodmodulo().equals(poliza.getCodmodulo())){
				envioAgroseguro = auxEnvio;
				break;
			}
		}
		
		if (envioAgroseguro != null) {
			polizaDao.actualizaXmlEnvio(envioAgroseguro.getId(), null, acuse.toString());
			polizaDao.evictEnvio(envioAgroseguro);
		}
		 
		logger.debug("end - guardarAcuseRecibo");
	}
	
	/**
	 * Graba el XML de la poliza de calculo devuelta por AgroSeguro en el Clob CALCULO de la tabla TB_ENVIOS_AGROSEGURO del ultimo envio guardado
	 * 
	 * @param polizaCalculo
	 * @param poliza
	 * @throws DAOException 
	 */
	/* Pet. 57626 ** MODIF TAM (02.06.2020) ** Inicio */
	/* El calculo de Polizas Agricolas complementarias ahora va con el Formato Unificado */
	/*public void grabaPolizaCalculo(final es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.impl.PolizaImpl polizaCalculo, final Poliza poliza, Long idEnvio) throws DAOException {*/
	public void grabaPolizaCalculo(final es.agroseguro.distribucionCostesSeguro.impl.PolizaImpl  polizaCalculo, final Poliza poliza, Long idEnvio) throws DAOException {
	
		
		if (polizaCalculo == null || poliza == null) return;
		
		logger.debug("grabaPolizaCalculo");
		
		EnvioAgroseguro envioAgroseguro = null;
		logger.debug("grabaPolizaCalculo - idEnvio: " + idEnvio);
		envioAgroseguro = polizaDao.getEnvioAgroseguro(idEnvio);
		
		/*//Cojo el primero porque vienen ordenados por id descendente
		for (EnvioAgroseguro auxEnvio : poliza.getEnvioAgroseguros()){
			if (auxEnvio.getId().equals(idEnvio)){
				envioAgroseguro = auxEnvio;
				break;
			}
			else{
				envioAgroseguro = polizaDao.getEnvioAgroseguro(idEnvio);
				break;
			}
		}*/
		if (envioAgroseguro != null) {
			this.polizaDao.actualizaXmlEnvio(envioAgroseguro.getId(), null,	polizaCalculo.toString());
			this.polizaDao.evictEnvio(envioAgroseguro);
		}
	}
	
	public void limpiaErroresWs(AcuseRecibo acuseRecibo,String webServiceToCall, 
			Parametro parametro, BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntida, String servicio) {
		WSUtils.limpiaErroresWs(acuseRecibo, webServiceToCall, parametro, 
				polizaDao, codPlan, codLinea, codEntida, servicio);
	}
	
	/**
	 * Metodo que relleno el objeto condensadordeflujo para pintar en la jsp la distribucion de costes
	 * @param resultadoCalculo
	 * @return
	 */
	public FluxCondensatorObject generarCondensadorDeFlujo(	Map<String, Object> resultadoCalculo,Poliza poliza, FinanciacionDocument financiacion) throws Exception{
		
		logger.debug("init - generarCondensadorDeFlujo");
		FluxCondensatorObject fluxCondensatorObject  = new FluxCondensatorObject();
		
		
		/* Pet. 57626 ** MODIF TAM (03.06.2020) ** Inicio */
		/* Ahora la contratacion de complementarias tambien va con el formato Unificado */
		
		es.agroseguro.distribucionCostesSeguro.DatosCalculo	  datosCalculo			 = null;
		
		es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio distribCostes[] = null;
		
		es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio[] distribCostes1 = null;
		es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] boniRecargo1 = null;
		es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subCCAA1 = null;
		es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subEnesa1 = null;
		es.agroseguro.distribucionCostesSeguro.impl.PolizaImpl polizaCalculo = null;
		
		try {
			if(!resultadoCalculo.containsKey("calculo"))
				throw new NullPointerException("No se ha podido recuperar el resultado de la llamada al servicio Web de Calculo");
						
			/* Pet. 57626 ** MODIF TAM (03.06.2020) ** Inicio */
			/* La contratacion de complementarias ya va con Formato Unificado */
			polizaCalculo = (es.agroseguro.distribucionCostesSeguro.impl.PolizaImpl)resultadoCalculo.get("calculo");
			
			
			if (polizaCalculo != null)
	  			datosCalculo = polizaCalculo.getDatosCalculo(); 
			
			
		  	if (datosCalculo != null) {
		  		if (poliza.isPlanMayorIgual2015()){
		  			
		  			distribCostes1 = datosCalculo.getCostePoliza().getCosteGrupoNegocioArray();
		  		}else {
		  			
		  			distribCostes = datosCalculo.getCostePoliza().getCosteGrupoNegocioArray();
		  	  		fluxCondensatorObject.setPctDescContColectiva(StringUtils.formatPercent(poliza.getColectivo().getPctdescuentocol()));
		  		}
		  	}
	  		
		  	// ************** //
		  	// POLIZAS < 2015 //
 		    // ************** //
	  		if (distribCostes != null) {
	  			
	  			for (es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio costeGNActual:distribCostes) {
	  				
	  				fluxCondensatorObject.setPrimaComercial(costeGNActual.getPrimaComercial().toString());	
	  			}	  			
	  		}
	  		
		  	// *************** //
	  		// POLIZAS >= 2015 //
 		    // *************** //
	  		if (distribCostes1 != null) {
	  			
	  			for (es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio costeGNActual:distribCostes1) {
	  				
	  				fluxCondensatorObject.setPrimaComercial(costeGNActual.getPrimaComercial().toString());
	  				fluxCondensatorObject.setPrimaNeta(costeGNActual.getPrimaComercialNeta().toString());
	  				fluxCondensatorObject.setPrimaNetaB(costeGNActual.getPrimaComercialNeta());
	  				fluxCondensatorObject.setRecargoConsorcio(costeGNActual.getRecargoConsorcio().toString());
	  				fluxCondensatorObject.setReciboPrima(costeGNActual.getReciboPrima().toString());
	  				fluxCondensatorObject.setCosteTomador(costeGNActual.getCosteTomador().toString());
	  				fluxCondensatorObject.setImporteTomador(costeGNActual.getCosteTomador().toString());
	  				
		  			// Si no se ha llamado al SW de financiacion
		  			if (financiacion == null) {

		  				DistribucionCoste2015 dcte = polizaManager.getDistCosteSaeca(poliza);
			  			if(dcte!=null){
			  				
			  				if(dcte.getRecargoaval()!=null){

			  					fluxCondensatorObject.setRecargoAval(dcte.getRecargoaval().toString());
			  					
			  				}
			  				if(dcte.getRecargofraccionamiento()!=null) {
			  					
			  					fluxCondensatorObject.setRecargoFraccionamiento(dcte.getRecargofraccionamiento().toString());
			  				}
		  					fluxCondensatorObject.setTotalCosteTomador(dcte.getTotalcostetomador().toString());
			  				
			  				if(dcte.getImportePagoFracc()!=null) {
			  					
			  					fluxCondensatorObject.setImportePagoFracc(NumberUtils.formatear(dcte.getImportePagoFracc(),2));
			  				}
			  				if(dcte.getPeriodoFracc()!=null) {
			  					
			  					fluxCondensatorObject.setPeriodoFracc(dcte.getPeriodoFracc().toString());
			  				}
			  				fluxCondensatorObject.setOpcionFracc(dcte.getOpcionFracc());
			  				fluxCondensatorObject.setValorOpcionFracc(dcte.getValorOpcionFracc());
			  			}
		  			}else {
		  				
		  				// Si se ha llamado al SW de financiacion se actualizan los recargos	  			
		  				fluxCondensatorObject.setRecargoAval(financiacion.getFinanciacion().getPeriodoArray(0).getDistribucionCoste().getRecargoAval().toString());
		  				fluxCondensatorObject.setRecargoFraccionamiento(financiacion.getFinanciacion().getPeriodoArray(0).getDistribucionCoste().getRecargoFraccionamiento().toString());
		  				fluxCondensatorObject.setImporteTomador(financiacion.getFinanciacion().getPeriodoArray(0).getDistribucionCoste().getTotalCosteTomador().toString());
		  				fluxCondensatorObject.setTotalCosteTomador(financiacion.getFinanciacion().getPeriodoArray(0).getDistribucionCoste().getTotalCosteTomador().toString());
		  				fluxCondensatorObject.setPeriodoFracc(financiacion.getFinanciacion().getPeriodoFraccionamiento().toString());
		  				if(null!=financiacion.getFinanciacion().getPeriodoArray(0).getPago().getImporte())
		  					fluxCondensatorObject.setImportePagoFracc(NumberUtils.formatear(financiacion.getFinanciacion().getPeriodoArray(0).getPago().getImporte(),2));
		  			}	  			
	  			
		  			subCCAA1     = costeGNActual.getSubvencionCCAAArray();
		  			subEnesa1    = costeGNActual.getSubvencionEnesaArray();
		  			boniRecargo1   = costeGNActual.getBonificacionRecargoArray();
		  			
		  			if (boniRecargo1 != null) {
		  				for (int i =0; i<boniRecargo1.length;i++){
		  					fluxCondensatorObject.addBoniRecargo1(boniRecargo1[i].getCodigo() + "-" + 
		  							this.getDescBoniRecar(boniRecargo1[i].getCodigo()), 
		  							boniRecargo1[i].getImporte().toString());
		  				}
		  			}
	  			
		  			if (subCCAA1 != null && subCCAA1.length > 0) {
		  				Map<String,BigDecimal> ccaa = new HashMap<String, BigDecimal>();
		  				for (int i =0; i<subCCAA1.length;i++){
		  					ccaa.put(subCCAA1[i].getCodigoOrganismo(), new BigDecimal(0));
		  				}
		  				for (int i =0; i<subCCAA1.length;i++){
		  					ccaa.put(subCCAA1[i].getCodigoOrganismo(), 
		  							ccaa.get(subCCAA1[i].getCodigoOrganismo()).add(subCCAA1[i].getImporte()));  					
		  				}
		  				Iterator<Map.Entry<String, BigDecimal>> it = ccaa.entrySet().iterator();
		  				while (it.hasNext()) {
		  					Map.Entry<String, BigDecimal> e = (Map.Entry<String, BigDecimal>)it.next();
			  				String key = e.getKey();
			  				BigDecimal valor = e.getValue();
			  				
			  				fluxCondensatorObject.addSubCCAA(key.charAt(0)+"-" +this.getCCAA(key.charAt(0)),valor.toString());
		  				}
		  				
		  			}
	  			
		  			if (subEnesa1 != null && subEnesa1.length > 0) {
		  				for (int i =0; i<subEnesa1.length;i++){
		  					fluxCondensatorObject.addSubEnesa(subEnesa1[i].getTipo()+"-"+this.getDescripcionEnesa(new BigDecimal(subEnesa1[i].getTipo())), 
		  													subEnesa1[i].getImporte().toString());
		  					
		  				}
		  			}
	  			
		  			for (es.agroseguro.distribucionCostesSeguro.ObjetoAsegurado objetoAsegurado : polizaCalculo.getObjetoAseguradoArray()) {
		  				
						es.agroseguro.costePoliza.parcela.ParcelaDocument parcelaDoc = es.agroseguro.costePoliza.parcela.ParcelaDocument.Factory.parse(new StringReader(objetoAsegurado.xmlText()));
								
						es.agroseguro.costePoliza.parcela.ParcelaDocument.Parcela parcela = parcelaDoc.getParcela();	
						DistCosteParcela2015 dCPar = new DistCosteParcela2015();
						
						dCPar.setCapitalAsegurado(parcela.getCosteParcela().getCapitalAsegurado());
						dCPar.setHoja(new BigDecimal(parcela.getHoja()));
						dCPar.setNumero(new BigDecimal(parcela.getNumero()));
						dCPar.setPrecio(parcela.getPrecio());
						dCPar.setProduccion(new BigDecimal(parcela.getProduccion()));
						dCPar.setTasacom(parcela.getCosteParcela().getTasaComercial());
						dCPar.setTasacombase(parcela.getCosteParcela().getTasaComercialBase());
						dCPar.setTipo(new BigDecimal (parcela.getTipo()));
						
						dCPar.setCostetomador(parcela.getCosteParcela().getCosteGrupoNegocioArray(0).getCosteTomador());
						dCPar.setPrimacomercial(parcela.getCosteParcela().getCosteGrupoNegocioArray(0).getPrimaComercial()) ;
						dCPar.setPrimacomercialneta(parcela.getCosteParcela().getCosteGrupoNegocioArray(0).getPrimaComercialNeta() );
						dCPar.setRecargoconsorcio(parcela.getCosteParcela().getCosteGrupoNegocioArray(0).getRecargoConsorcio());
						dCPar.setReciboprima(parcela.getCosteParcela().getCosteGrupoNegocioArray(0).getReciboPrima());
						
						es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio[] costeGNArr = parcela.getCosteParcela().getCosteGrupoNegocioArray();
						if(!ArrayUtils.isEmpty(costeGNArr)) {
							
							for (es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio costeGN : costeGNArr) {
								
								logger.debug("Procesando XML coste parcela GN.................");
								DistCosteParcela2015GrupoNegocio parcGN = new DistCosteParcela2015GrupoNegocio();
								parcGN.setGruponegocio(costeGN.getGrupoNegocio().charAt(0));
								parcGN.setPrimacomercial(costeGN.getPrimaComercial());
								parcGN.setPrimacomercialneta(costeGN.getPrimaComercialNeta());
								parcGN.setRecargoconsorcio(costeGN.getRecargoConsorcio());
								parcGN.setReciboprima(costeGN.getReciboPrima());
								parcGN.setCostetomador(costeGN.getCosteTomador());
								parcGN.setDistCosteParcela2015(dCPar);
								dCPar.getDistCosteGns().add(parcGN);
								
								es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subvEnesaArr = costeGN.getSubvencionEnesaArray();
								if (!ArrayUtils.isEmpty(subvEnesaArr)) {
									
									for (es.agroseguro.contratacion.costePoliza.SubvencionEnesa subv : subvEnesaArr) {
										
										logger.debug("Procesando XML coste parcela Subv Enesa.................");
										DistCosteParcela2015Subvencion parcSubv = new DistCosteParcela2015Subvencion();
										parcSubv.setCodTipo('E');
										parcSubv.setCodOrganismo('0');
										parcSubv.setCodTipoSubv(BigDecimal.valueOf(subv.getTipo()));
										parcSubv.setImporte(subv.getImporte());
										parcSubv.setPctSubvencion(BigDecimal.ZERO);
										parcSubv.setValorUnitario(BigDecimal.ZERO);
										parcSubv.setDistCosteParcela2015(dCPar);
										dCPar.getDistCosteSubvs().add(parcSubv);
									}
								}
								es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subvCCAAArr = costeGN.getSubvencionCCAAArray();
								if (!ArrayUtils.isEmpty(subvCCAAArr)) {
									
									for (es.agroseguro.contratacion.costePoliza.SubvencionCCAA subv : subvCCAAArr) {
										
										logger.debug("Procesando XML coste parcela Subv CCAA.................");
										DistCosteParcela2015Subvencion parcsubv = new DistCosteParcela2015Subvencion();
										parcsubv.setCodTipo('C');
										parcsubv.setCodOrganismo(subv.getCodigoOrganismo().charAt(0));
										parcsubv.setCodTipoSubv(BigDecimal.ONE);
										parcsubv.setImporte(subv.getImporte());
										parcsubv.setPctSubvencion(BigDecimal.ZERO);
										parcsubv.setValorUnitario(BigDecimal.ZERO);
										parcsubv.setDistCosteParcela2015(dCPar);
										dCPar.getDistCosteSubvs().add(parcsubv);
									}
								}
								es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] bonRecArr = costeGN.getBonificacionRecargoArray();
								if (!ArrayUtils.isEmpty(bonRecArr)) {
									
									for (es.agroseguro.contratacion.costePoliza.BonificacionRecargo bonRec : bonRecArr) {
										
										logger.debug("Procesando XML coste parcela BR.................");
										DistCosteParcela2015BonifRec br = new DistCosteParcela2015BonifRec();
										br.setCodigo(BigDecimal.valueOf(bonRec.getCodigo()));
										br.setImporte(bonRec.getImporte());
										br.setDistCosteParcela2015(dCPar);
										dCPar.getDistCosteBonifRecs().add(br);
									}
								}
							}
						}
						
						es.agroseguro.costePoliza.costeObjetoAsegurado.SubvencionDesglose[] subvDesgloseArr = parcela.getCosteParcela().getSubvencionDesgloseArray();
						if (!ArrayUtils.isEmpty(subvDesgloseArr)) {
							
							for (es.agroseguro.costePoliza.costeObjetoAsegurado.SubvencionDesglose subv : subvDesgloseArr) {
								
								logger.debug("Procesando XML coste parcela Subv desglose.................");
								DistCosteParcela2015Subvencion parcSubv = new DistCosteParcela2015Subvencion();
								parcSubv.setCodTipo('D');
								parcSubv.setCodOrganismo(subv.getCodigoOrganismo().charAt(0));
								parcSubv.setCodTipoSubv(BigDecimal.valueOf(subv.getTipo()));
								parcSubv.setImporte(subv.getImporte());
								parcSubv.setPctSubvencion(subv.getPctSubvencion());
								parcSubv.setValorUnitario(subv.getValorUnitario());
								parcSubv.setDistCosteParcela2015(dCPar);
								dCPar.getDistCosteSubvs().add(parcSubv);
							}
						}
						
						fluxCondensatorObject.getDistCosteParcela2015s().add(dCPar);
					}
	  			
		  			// MPM - 06/05/2015
	  			
					// Si la poliza pertenece a un plan >= 2015 se comprueba si se muestra el boton de financiar
					if (poliza.isPlanMayorIgual2015()) { 
						//Asignamos si debe mostrar el boton de financiar 
						ImporteFraccionamiento impFrac = importesFraccDao.obtenerImporteFraccionamiento(poliza.getLinea().getLineaseguroid(),poliza.getColectivo().getSubentidadMediadora());
						// Si hay configurado un importe de fraccionamiento para el plan/linea de la poliza
						if (impFrac != null) {
							muestraFinanciar(fluxCondensatorObject, impFrac);
						}
					}
	  			}	
	  		}
	  		// TMR Formulacion 29-10-14 - FIN
	  		
	  		ModuloId moduloId = new ModuloId();
	  		moduloId.setCodmodulo(poliza.getCodmodulo());
	  		moduloId.setLineaseguroid(poliza.getLinea().getLineaseguroid());
	  		Modulo modulo = (Modulo)polizaDao.get(Modulo.class, moduloId);
	  		
	  		fluxCondensatorObject.setDescModulo(modulo.getDesmodulo());
	  		fluxCondensatorObject.setIdModulo(poliza.getCodmodulo());
	  		
	  		try {
				BigDecimal pctMinimoFinanciacion =getPctMinimoFinanciacion(poliza.getCodmodulo(), poliza);
				if(null!= pctMinimoFinanciacion){
					fluxCondensatorObject.setPctMinFinanSobreCosteTomador(pctMinimoFinanciacion.toString());
				}
			} catch (Exception e) {
				
				logger.error("Excepcion : WebServicesCplManager - generarCondensadorDeFlujo", e);
			}
	  		
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error al generar el condensador de flujo",ex);
			throw new Exception ("Se ha producido un error al generar el condensador de flujo",ex);
		}
		logger.debug("end - generarCondensadorDeFlujo");
		return fluxCondensatorObject;
		
		/* Pet. 57626 ** MODIF TAM (03.06.2020) ** Fin */
	}
	
	public FluxCondensatorObject generarCondensadorDeFlujoDistCoste(
			List<DistribucionCoste2015> listDc, Poliza poliza) throws Exception {
		
		
		logger.debug("init - generarCondensadorDeFlujo");
		FluxCondensatorObject fluxCondensatorObject  = new FluxCondensatorObject();
		
		try {
			if(null == listDc || listDc.size()== 0)
				throw new NullPointerException("No se ha podido recuperar la distribucion de costes de la poliza cpl");
						
			DistribucionCoste2015 dc = listDc.get(0);
			
	  		// POLIZAS < 2015
	  		if (dc != null) {
	  			fluxCondensatorObject.setPrimaComercial(dc.getPrimacomercial().toString());
	  			fluxCondensatorObject.setPrimaNeta(dc.getPrimacomercialneta().toString());
	  			//fluxCondensatorObject.setCosteNeto(dc.getCosteNeto().toString());
	  			
	  			fluxCondensatorObject.setPrimaNetaB(dc.getPrimacomercialneta());
	  			fluxCondensatorObject.setRecargoConsorcio(dc.getRecargoconsorcio().toString());
	  			fluxCondensatorObject.setReciboPrima(dc.getReciboprima().toString());
	  			fluxCondensatorObject.setCosteTomador(dc.getCostetomador().toString());
	  			fluxCondensatorObject.setImporteTomador(dc.getCostetomador().toString());
	  			//fluxCondensatorObject.setBonifMedidaPreventiva(dc.getBonificacionRecargo2015s()ificacionMedidasPreventivas().getBonifMedPreventivas().toString());
	  			//fluxCondensatorObject.setDescuentoContColectiva(dc.getDescuento().getDescuentoColectivo().toString());
	  			
	  			if(dc.getRecargoaval()!=null){
  					//fluxCondensatorObject.setRecargoAval(NumberUtils.formatear(dcte.getRecargoaval(),2));
  					fluxCondensatorObject.setRecargoAval(dc.getRecargoaval().toString());
  					
  				}
  				if(dc.getRecargofraccionamiento()!=null){
  					//fluxCondensatorObject.setRecargoFraccionamiento(NumberUtils.formatear(dcte.getRecargofraccionamiento(),2));
  					fluxCondensatorObject.setRecargoFraccionamiento(dc.getRecargofraccionamiento().toString());
  				}
				fluxCondensatorObject.setTotalCosteTomador(dc.getTotalcostetomador().toString());
	  			
				if(dc.getImportePagoFracc()!=null){
  					fluxCondensatorObject.setImportePagoFracc(NumberUtils.formatear(dc.getImportePagoFracc(),2));
  				}
  				if(dc.getPeriodoFracc()!=null){
  					fluxCondensatorObject.setPeriodoFracc(dc.getPeriodoFracc().toString());
  				}
  				fluxCondensatorObject.setOpcionFracc(dc.getOpcionFracc());
  				fluxCondensatorObject.setValorOpcionFracc(dc.getValorOpcionFracc());
	  			
  				
  				for(DistCosteSubvencion2015 distCosteSubvencion : dc.getDistCosteSubvencion2015s())
				{
				   if(distCosteSubvencion.getCodorganismo().equals('0')){
					   fluxCondensatorObject.addSubEnesa(this.getDescripcionEnesa(distCosteSubvencion.getCodtiposubv()),
							   NumberUtils.formatear(distCosteSubvencion.getImportesubv(),2));
							   
				   }else{
					   fluxCondensatorObject.addSubCCAA(this.getCCAA(distCosteSubvencion.getCodorganismo()),
							   NumberUtils.formatear(distCosteSubvencion.getImportesubv(),2));
					  
				   }
				}
				
  				Set<BonificacionRecargo2015> boniRecargo1   = dc.getBonificacionRecargo2015s();
	  			
	  			if (boniRecargo1 != null) {		  				
	  				for (BonificacionRecargo2015 b : boniRecargo1){
	  					fluxCondensatorObject.addBoniRecargo1(this.getDescBoniRecar(b.getCodigo().intValue()), 
								NumberUtils.formatear(b.getImporte(),2));
	  				}
	  			}
				
	  		
	  			fluxCondensatorObject.setImporteTomador(dc.getCostetomador().toString());
	  		
	  			// MPM - 06/05/2015
	  			
				// Si la poliza pertenece a un plan >= 2015 se comprueba si se muestra el boton de financiar
				if (poliza.isPlanMayorIgual2015()) { 
					//Asignamos si debe mostrar el boton de financiar 
					ImporteFraccionamiento impFrac = importesFraccDao.obtenerImporteFraccionamiento(poliza.getLinea().getLineaseguroid(),poliza.getColectivo().getSubentidadMediadora());
					// Si hay configurado un importe de fraccionamiento para el plan/linea de la poliza
					if (impFrac != null) {
						muestraFinanciar(fluxCondensatorObject,impFrac);
					}
				}
	  			
	  			// Comprobamos si la distribucion de costes se puede financiar
	  			//fluxCondensatorObject.setMuestraBotonFinanciar(esFinanciable(poliza.getLinea().getLineaseguroid(), fluxCondensatorObject.getImporteTomador()));
	  			
	  		}
	  		// TMR Formulacion 29-10-14 - FIN
	  		
	  		ModuloId moduloId = new ModuloId();
	  		moduloId.setCodmodulo(poliza.getCodmodulo());
	  		moduloId.setLineaseguroid(poliza.getLinea().getLineaseguroid());
	  		Modulo modulo = (Modulo)polizaDao.get(Modulo.class, moduloId);
	  		
	  		fluxCondensatorObject.setDescModulo(modulo.getDesmodulo());
	  		fluxCondensatorObject.setIdModulo(poliza.getCodmodulo());
	  		
	  		try {
				BigDecimal pctMinimoFinanciacion =getPctMinimoFinanciacion(poliza.getCodmodulo(), poliza);
				if(null!= pctMinimoFinanciacion){
					fluxCondensatorObject.setPctMinFinanSobreCosteTomador(pctMinimoFinanciacion.toString());
				}
			} catch (Exception e) {
				
				logger.error("Excepcion : WebServicesCplManager - generarCondensadorDeFlujoDistCoste", e);
			}
	  		
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error al generar el condensador de flujo",ex);
			throw new Exception ("Se ha producido un error al generar el condensador de flujo",ex);
		}
		logger.debug("end - generarCondensadorDeFlujo");
		return fluxCondensatorObject;
	}
	
	
	
	
	

	private BigDecimal getPctMinimoFinanciacion(String codModulo, Poliza poliza) throws Exception{
		BigDecimal resultado=null;
		BigDecimal codPlan=null;
		BigDecimal codLinea=null;
		
		if(null!=poliza && null!= poliza.getLinea() && 
				null!= poliza.getLinea().getCodplan() && null!= poliza.getLinea().getCodlinea()){
			codPlan=poliza.getLinea().getCodplan();
			codLinea=poliza.getLinea().getCodlinea();
			
			resultado =pagoPolizaManager.getPctMinimoFinanciacion(codPlan, codLinea, codModulo);
		}
		
		return resultado;
	}
	
	public void muestraFinanciar(FluxCondensatorObject fco, ImporteFraccionamiento impFrac) {

		if(fco.getImporteTomador() != null){
			String impTomStr = fco.getImporteTomador();
			BigDecimal impTom = new BigDecimal(impTomStr);			
			// Si el importe de la poliza supera el minimo para financiar
			if(impTom.compareTo(impFrac.getImporte())!=-1) {
				fco.setMuestraBotonFinanciar(true);
				fco.setEsFraccAgr(false);
				
			}else{
				fco.setMuestraBotonFinanciar(false);
			}
		}
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
			logger.error("Error al obtener la descripcion de la subvencion CCAA " + tipo,e);
			throw new Exception("Error al obtener la descripcion de la subvencion CCAA ",e);
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
			logger.error("Error al obtener la descripcion de la subvencion CCAA " + codOrganismo,e);
			throw new Exception("Error al obtener la descripcion del organismo ",e);
		}
		logger.debug("end - getCCAA");
		return desc;
	}
	/**
	 * Obtiene la descripcion de BonificacionRecargo
	 * 
	 * @param codigoOrganismo
	 * @return
	 */
	public String getDescBoniRecar (int i) {
		
		String desc = "";
		try {
			com.rsi.agp.dao.tables.cpl.BonificacionRecargo br = 
					(com.rsi.agp.dao.tables.cpl.BonificacionRecargo)polizaDao.getObject(
							com.rsi.agp.dao.tables.cpl.BonificacionRecargo.class,  Long.valueOf(i));
			if (br != null)
				desc = br.getDescripcion();
		}
		catch(Exception e) {
			logger.error("Error al obtener la descripcion de BonificacionRecargo", e);
		}
		return desc;
	}
	
	/**
	 * Actualiza la distribucion de costes guardada
	 * 
	 * @param poliza
	 */
	public es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument actualizaDistribCostes(
			Long idEnvio, Poliza poliza, String codModulo, BigDecimal filaComparativa,Long idComparativa)
			throws DistribucionCostesException, Exception {

		// Se recupera la comparativa de la poliza (en este punto, solo deberia
		// haber 1)
		String xml = WSUtils.obtenXMLCalculo(idEnvio, polizaDao);

		// No se recupero el XML...
		if (xml == null){
			throw new Exception("No se ha podido obtener el XML de Calculo");
		}else{
			xml = xml.replace("xml-fragment", "ns5:Poliza");
		}
		try {
			// Se valida el XML y se obtiene el document
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument polizaDocument = WSUtils
					.getXMLDistribCostes(xml);

			if (poliza.getLinea().getCodplan().compareTo(Constants.PLAN_2015)== -1){
				// SE ELIMINA EL CLEAR YA QUE AL SET PERSISTENT SET BORRA EN BBDD
				//poliza.getDistribucionCostes().clear();
				distribucionCosteDAO.deleteDistribucionCoste(poliza.getIdpoliza(), codModulo, filaComparativa);
				com.rsi.agp.dao.tables.poliza.DistribucionCoste distribCoste = distribucionCosteDAO
					.saveDistribucionCoste(polizaDocument.getPoliza(),
							poliza.getIdpoliza(), codModulo,
							filaComparativa);
				poliza.getDistribucionCostes().add(distribCoste);
				// actualizamos el importe de la poliza
				//poliza.setImporte(distribCoste.getCargotomador());
			}else {
				// SE ELIMINA EL CLEAR YA QUE AL SET PERSISTENT SET BORRA EN BBDD
				//poliza.getDistribucionCoste2015s().clear();
				logger.debug("## ------------- 1189 -- Poliza: " + poliza.getIdpoliza() + " - Modulo: " + codModulo + " - FilaComparativa: " + filaComparativa);
				distribucionCosteDAO.deleteDistribucionCoste2015(poliza.getIdpoliza(), codModulo, idComparativa);
				poliza.getDistribucionCoste2015s().clear();
				DistribucionCoste2015 distribCoste1 = distribucionCosteDAO
						.saveDistribucionCoste2015(polizaDocument.getPoliza(),
								poliza.getIdpoliza(), codModulo,
								filaComparativa, idComparativa);
					poliza.getDistribucionCoste2015s().add(distribCoste1);
					// actualizamos el importe de la poliza
					//poliza.setImporte(distribCoste1.getCostetomador());
			}

			// / Angel. Mejora 113 - 24/02/2012 Actualizacion del importe de la
			// poliza en pagos Poliza si existen.
			if (poliza.getPagoPolizas() != null
					&& poliza.getPagoPolizas().size() > 0) {
				Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
				if (it.hasNext()) {
					PagoPoliza pp = it.next();
					if (pp.getPctprimerpago() != null) {
						//pp.setImporte(distribCoste.getCargotomador());
						pp.setImporte(poliza.getImporte());
					}
				}
			}
			// / Fin mejora 113

			// guardamos la poliza para actualizar el importe
			//polizaDao.actualizaImporte (poliza); El importe se actualiza al pasar a provisional
			//polizaDao.saveOrUpdate(poliza);

			return polizaDocument;
		} catch (Exception e) {
			throw new DistribucionCostesException(
					"Ocurrio un error al guardar la distribucion de costes de la poliza",
					e);
		}

	}
	
	/** INICIO PET-63699 DNF 14/05/2020*/
	public void setSimulacionSbpDao(ISimulacionSbpDao simulacionSbpDao) { 
		this.simulacionSbpDao = simulacionSbpDao; 
	} 
	/** FIN PET-63699 DNF 14/05/2020*/ 
	
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}	
	
	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}
	
	public void setDistribucionCosteDAO(IDistribucionCosteDAO distribucionCosteDAO) {
		this.distribucionCosteDAO = distribucionCosteDAO;
	}

	public void setImportesFraccDao(IImportesFraccDao importesFraccDao) {
		this.importesFraccDao = importesFraccDao;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}
	
	// Pet. 22208 ** MODIF TAM (23.03.2018) ** Inicio //
	public void setSeleccionPolizaDao(ISeleccionPolizaDao seleccionPolizaDao) {
		this.seleccionPolizaDao = seleccionPolizaDao;
	}
	public void setHistoricoEstadosManager(IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}
	// Pet. 22208 ** MODIF TAM (23.03.2018) ** Fin //
}