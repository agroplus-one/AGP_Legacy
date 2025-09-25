package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.AseguradoUtil;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.MetodoPagoController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.poliza.IPagoPolizaDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

public class PagoPolizaManager extends MetodoPagoController implements IManager {
	
	private static final Log LOGGER = LogFactory.getLog(PagoPolizaManager.class);
	
	protected IPagoPolizaDao pagoPolizaDao;
	private final static Character FRACCIONADO = 'F';
	private PolizaManager polizaManager; 
	
	public boolean lineaContratacion2021(BigDecimal codPlan_pol, BigDecimal codLinea_pol, boolean isLineaGan) throws Exception {
		return pagoPolizaDao.lineaContratacion2021(codPlan_pol, codLinea_pol, isLineaGan);
	}
	
	public final Poliza getPolizaById(final long idPoliza) {
		return (Poliza) pagoPolizaDao.getObject(Poliza.class, idPoliza);
	}
	
	public final PagoPoliza getPagoPolizaByPolizaId(final long idPoliza) {
		PagoPoliza pagoPoliza = null;
		Poliza poliza=null;
		poliza = getPolizaById(idPoliza);
		if(null!=poliza && null!= poliza.getPagoPolizas() && poliza.getPagoPolizas().size()>0){
			pagoPoliza = (PagoPoliza) poliza.getPagoPolizas().toArray()[0];
		}
		
		return pagoPoliza;
	}
	
	/**
	 * Metodo para cargar los datos de un pago a partir de los datos de una poliza.
	 * @param pagoPoliza
	 * @param poliza
	 */
	public final void loadPagoPoliza(PagoPoliza pagoPoliza, Poliza poliza){
		//Introducimos la cuenta junto con sus validaciones
		String ccc = AseguradoUtil.obtenerCcc(poliza, true);
		if (ccc.length()==24) {
			pagoPoliza.setIban(ccc.substring(0,4));
			pagoPoliza.setCccbanco(ccc.substring(4,24));
		}else{
			pagoPoliza.setIban("ES");
		}

		pagoPoliza.setFechasegundopago(getFechaSegundoPago(poliza));
		//Introducimos el porcentaje del primer y segundo pago
		pagoPoliza.setPctprimerpago(getPctPrimerPago(poliza));
		pagoPoliza.setPctsegundopago(getPctSegundoPago(poliza));

		pagoPoliza.setFecha(getFechaPrimerPago(poliza));
		
		//pagoPoliza.setImporte(poliza.getImporte());
		
		//si tiene ya un pagoPoliza previo, se mantiene la cuenta, la fecha de pago y el banco
		Set<PagoPoliza> pagoPolizas = poliza.getPagoPolizas();
		
		// si no tiene pagospoliza comprobamos si es Saeca y metemos de la distrib. de costes el importepagoFracc, si no es Saeca ponemos el importe de la póliza
		if (pagoPolizas == null || pagoPolizas.size()==0){
			Boolean esSaeca = polizaManager.esFinanciadaSaeca(poliza.getLinea().getLineaseguroid(),poliza.getColectivo().getSubentidadMediadora());
			if(esSaeca){
				DistribucionCoste2015 dcte = polizaManager.getDistCosteSaeca(poliza);
				if (dcte.getImportePagoFracc() != null)
					pagoPoliza.setImporte(dcte.getImportePagoFracc());
				else
					pagoPoliza.setImporte(poliza.getImporte());
			}else{
				pagoPoliza.setImporte(poliza.getImporte());
			}
		}
		for (PagoPoliza pag: pagoPolizas){
			if (null != pag.getImporte()){
				pagoPoliza.setImporte(pag.getImporte());
			}else{
				Boolean esSaeca = polizaManager.esFinanciadaSaeca(poliza.getLinea().getLineaseguroid(),poliza.getColectivo().getSubentidadMediadora());
				if(esSaeca){
					DistribucionCoste2015 dcte = polizaManager.getDistCosteSaeca(poliza);
					pagoPoliza.setImporte(dcte.getImportePagoFracc());
				}else{
					pagoPoliza.setImporte(poliza.getImporte());
				}
			}	
			if (null != pag.getBanco())
				pagoPoliza.setBanco(pag.getBanco());
			/*if (null != pag.getFecha())
				pagoPoliza.setFecha(pag.getFecha());
			else
				pagoPoliza.setFecha(getFechaPrimerPago(poliza));*/
			if (null != pag.getCccbanco()) {
				pagoPoliza.setCccbanco(pag.getCccbanco());
			}
			if (null != pag.getTipoPago()) {
				pagoPoliza.setTipoPago(pag.getTipoPago());
			}
			if (null != pag.getEnvioIbanAgro()) {
				pagoPoliza.setEnvioIbanAgro(pag.getEnvioIbanAgro());
			}
			if(null!=pag.getTitularCuenta()){
				pagoPoliza.setTitularCuenta(pag.getTitularCuenta());
			}
			if(null!=pag.getDestinatarioDomiciliacion()){
				pagoPoliza.setDestinatarioDomiciliacion(pag.getDestinatarioDomiciliacion());
			}
		}
		pagoPoliza.setPoliza(poliza);
	}
	
	
	/**
	 * Metodo para guardar los datos de un pago a partir de los  datos de la poliza.
	 * @param polizaBean
	 * @throws DAOException
	 */
	public final void savePagoPoliza(Long idpoliza,Usuario usuario) throws DAOException{
		PagoPoliza pagoPolizaBean = new PagoPoliza(); 
		Poliza polizaBean = this.getPolizaById(idpoliza);
		this.loadPagoPoliza(pagoPolizaBean, polizaBean);
		if (null == pagoPolizaBean.getTipoPago()){
			if (isPagoCCAllowed(usuario, polizaBean)) {
				pagoPolizaBean.setTipoPago(Constants.CARGO_EN_CUENTA); 
			} else {
				pagoPolizaBean.setTipoPago(Constants.PAGO_MANUAL); 
			}
		}
		// MPM - 24/06/2015 
		// La forma de pago es F sólo si la póliza se ha financiado con Agroseguro o con SAECA
		
		// Accede a la distribución de costes para comprobar si se ha financiado
		Set<DistribucionCoste2015> dc2015 = polizaBean.getDistribucionCoste2015s();
		
		if (dc2015 == null || dc2015.isEmpty()) {
			pagoPolizaBean.setFormapago('C');
		}
		else {
			for (DistribucionCoste2015 distribucionCoste2015 : dc2015) {
				if (distribucionCoste2015 != null && (distribucionCoste2015.getOpcionFracc() != null || distribucionCoste2015.getImportePagoFraccAgr() != null)) {
					pagoPolizaBean.setFormapago('F');
				}
				else {
					pagoPolizaBean.setFormapago('C');
				}
			}
		}
		
		/*if (pagoPolizaBean.getPctprimerpago().compareTo(new BigDecimal("100")) == 0)
			pagoPolizaBean.setFormapago('C');
		else
			pagoPolizaBean.setFormapago('F');*/
		
		polizaBean.getPagoPolizas().removeAll(polizaBean.getPagoPolizas());
		polizaBean.getPagoPolizas().add(pagoPolizaBean);
		pagoPolizaDao.saveOrUpdate(polizaBean);
	}
	
	public final void savePagoPoliza(PagoPoliza pagoPoliza) throws DAOException{
		pagoPolizaDao.saveOrUpdate(pagoPoliza);
	}

	/**
	 * Metodo para guardar los datos de un pago a partir de los datos de una poliza y los recibidos como parametros	
	 */
	public final void savePagoPoliza(Poliza polizaBean, String fechaPrimerPago, BigDecimal importe, String cccbanco,
			String cccbanco2, BigDecimal pctprimerpago, String fechaSegundoPago, BigDecimal pctsegundopago,
			String oficina, String iban, String iban2, BigDecimal tipoPago, boolean isPolizaPagada, BigDecimal banco,
			Character envioIbanAgro, Character destinatarioDomiciliacion, String titularCuenta)
			throws ParseException, DAOException {
		LOGGER.debug("savePagoPoliza");
		PagoPoliza pagoPolizaBean = new PagoPoliza();
		
		pagoPolizaBean.setImporte(importe);
		pagoPolizaBean.setCccbanco(cccbanco);
		pagoPolizaBean.setIban(iban);
		pagoPolizaBean.setCccbanco2(cccbanco2);
		pagoPolizaBean.setIban2(iban2);
		pagoPolizaBean.setPctprimerpago(pctprimerpago);
		pagoPolizaBean.setPctsegundopago(pctsegundopago);
		pagoPolizaBean.setTipoPago(tipoPago);
		if (envioIbanAgro != null)
			pagoPolizaBean.setEnvioIbanAgro(envioIbanAgro);
		
		if(destinatarioDomiciliacion!=null)
			pagoPolizaBean.setDestinatarioDomiciliacion(destinatarioDomiciliacion);
		
		if(titularCuenta!=null)
			pagoPolizaBean.setTitularCuenta(titularCuenta);
		
		try {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			if (!StringUtils.nullToString(fechaPrimerPago).equals("")) {
				pagoPolizaBean.setFecha(df.parse(fechaPrimerPago));
			}
			if (!StringUtils.nullToString(fechaSegundoPago).equals("")) {
				pagoPolizaBean.setFechasegundopago(df.parse(fechaSegundoPago));
			}
			// MPM - 24/06/2015 
			// La forma de pago es F sólo si la póliza se ha financiado con Agroseguro o con SAECA		
			// Por defecto se pone C
			pagoPolizaBean.setFormapago(Constants.FORMA_PAGO_ALCONTADO);
			
			// El sistema de hibernate obliga a pasar el objeto poliza (valida las foreign key).
			//antes de grabar,relleno la oficina con ceros a la izq. hasta completar el maximo permitido
			if (!StringUtils.nullToString(oficina).equals("")){
				Integer ofi = new Integer(StringUtils.nullToString(oficina));
				polizaBean.setOficina(String.format("%04d", ofi.intValue()));
			}
			
			Set<DistribucionCoste2015> distCostes = polizaBean.getDistribucionCoste2015s();
			if (null!=distCostes && distCostes.size()==1) {//ya hemos pasado o estamos en la grabación provisional y solo tenemos una dist. de costes
				if(null!=distCostes.iterator().next().getImportePagoFracc()) {//poliza financiada
					pagoPolizaBean.setFormapago(Constants.FORMA_PAGO_FINANCIADO);
					pagoPolizaBean.setImporte(distCostes.iterator().next().getImportePagoFracc());
				}
				else if (null!=distCostes.iterator().next().getImportePagoFraccAgr()) {
					pagoPolizaBean.setFormapago(Constants.FORMA_PAGO_FINANCIADO);
				}
			}
			
			//borramos los pagos anteriores que hubiera en la poliza
			polizaBean.getPagoPolizas().removeAll(polizaBean.getPagoPolizas());

			//guardamos el nuevo pago en la poliza.
			pagoPolizaBean.setPoliza(polizaBean);
			pagoPolizaBean.setId(null);
			
			if (isPolizaPagada) {
				polizaBean.setFechaPago(df.parse(fechaPrimerPago));
				EstadoPagoAgp e = new EstadoPagoAgp();
				e.setId(Constants.POLIZA_PAGADA);
				polizaBean.setEstadoPagoAgp(e);
				pagoPolizaBean.setBanco(banco);
			}
			if (banco!= null) {
				pagoPolizaBean.setBanco(banco);
			}
			LOGGER.debug("importe: "+pagoPolizaBean.getImporte());
			polizaBean.getPagoPolizas().add(pagoPolizaBean);
			pagoPolizaDao.saveOrUpdate(polizaBean);
			LOGGER.debug("pagoPoliza insertado: "+pagoPolizaBean.getId());
		} catch (DAOException dao) {
			LOGGER.error("Error al guardar los datos del pago", dao);
			throw dao;
		}
		
	}
	
	/**
	 * Fecha en la que se realiza el primer pago de la poliza. Se asgna igual a la fecha
	 * de primer pago del colectivo; si la fecha actual es mayor que la de primer pago del 
	 * colectivo, se asigna la fecha actual.
	 * @param pagoPoliza
	 */
	public Date getFechaPrimerPago(Poliza poliza){
		LOGGER.debug("init - getFechaPrimerPago");
		Date fecha = null;
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getFecha() != null)
					fecha = pp.getFecha();
			}
		}
		else{
			Date fechaDefecto = new Date();
			Date fechaPrimerPagoColectivo = poliza.getColectivo().getFechaprimerpago();
			
			//Si la fecha actual es mayor que la de primer pago del colectivo, se asigna la fecha actual.	
			if (fechaDefecto.after(fechaPrimerPagoColectivo))
				fecha =  fechaDefecto;
			else
				fecha = fechaPrimerPagoColectivo;
		}
		LOGGER.debug("end - getFechaPrimerPago");
		return fecha;
	}
	
	/**
	 * Fecha en la que se realiza el segundo pago de la poliza. Se asigna igual a la fecha
	 * de segundo pago del colectivo; si la fecha actual es mayor que la de segundo pago del 
	 * colectivo, se asigna el dÃ­a siguiente a la fecha actual.
	 * @param pagoPoliza
	 */
	private Date getFechaSegundoPago(Poliza poliza){
		LOGGER.debug("init - getFechaSegundoPago");
		long miliDia = 86400000;  // un dÃ­a en milisegundos 
		Date fechaToday = new Date();
		Date fechaTomorrow = new Date(fechaToday.getTime() + miliDia);
		Date fecha = null;
		
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getFechasegundopago() != null)
					fecha = pp.getFechasegundopago();
			}
		}
		else{
			Date fechaSegundoPagoColectivo = poliza.getColectivo().getFechasegundopago();
			//Si la fecha actual es mayor que la de segundo pago del colectivo, se asigna la fecha de maÃ±ana.	
			if ((fechaSegundoPagoColectivo != null && fechaTomorrow.after(fechaSegundoPagoColectivo)))
				fecha =  fechaTomorrow;
			else if (fechaSegundoPagoColectivo != null)
				fecha =  fechaSegundoPagoColectivo;
			else
				fecha = null;
		}
		LOGGER.debug("end - getFechaSegundoPago");
		return fecha;
	}	
	
	/**
	 * Devuelve el porcentaje correspondiente al primer pago del colectivo.
	 * @param poliza Poliza de la cual sacamos todos los porcentajes
	 * @return
	 */
	private BigDecimal getPctPrimerPago(Poliza poliza){
		LOGGER.debug("init - getPctPrimerPago");
		BigDecimal res = null;
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getPctprimerpago() != null)
					res = pp.getPctprimerpago();
			}
		}
		else{
			res = poliza.getColectivo().getPctprimerpago();
		}
		LOGGER.debug("end - getPctPrimerPago");
		return res;
	}
	
	/**
	 * Devuelve el porcentaje correspondiente al segundo pago del colectivo.
	 * @param poliza Poliza de la cual sacamos todos los porcentajes
	 * @return
	 */
	private BigDecimal getPctSegundoPago(Poliza poliza){
		LOGGER.debug("init - getPctSegundoPago");
		BigDecimal res = null;
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getPctsegundopago() != null)
					res = pp.getPctsegundopago();
			}
		}
		else{
			res = poliza.getColectivo().getPctsegundopago();
		}
		LOGGER.debug("end - getPctSegundoPago");
		return res;
	}	

	public boolean compruebaPagoFraccionado(Poliza poliza) {
		boolean esFinanciada;
		boolean res= false;
		if(poliza.getPagoPolizas() != null && poliza.getPagoPolizas().size() > 0){
			Iterator<PagoPoliza> it = poliza.getPagoPolizas().iterator();
			if (it.hasNext()){
				PagoPoliza pp = it.next();
				if (pp.getFormapago() != null){
					if (pp.getFormapago().equals(FRACCIONADO)){
						res= true;
					}
				}
			}
		}		
		try {
			esFinanciada = pagoPolizaDao.polizaEsFinanciada(poliza.getIdpoliza());
			if (esFinanciada)
				res=true;
		} catch (Exception e) {
			LOGGER.error("Ocurrio un error al comprobar si la poliza está financiadao",e);
		}
		
//		if(null != poliza.getEsFinanciada() && poliza.getEsFinanciada().equals('S'))
//			res=true;
		
		return res;
	}
	
	public final void setPagoPolizaDao(final IPagoPolizaDao pagoPolizaDao) {
		this.pagoPolizaDao = pagoPolizaDao;
	}
	
	public final PagoPoliza getPagoPolizaDao(final long idPoliza) {
		return (PagoPoliza) pagoPolizaDao.getObject(PagoPoliza.class, idPoliza);
	}

	public boolean existeBancoDestino(String bancoDestino) throws Exception {
		
		try {
			return pagoPolizaDao.existeBancoDestino(bancoDestino);
	
		}catch(Exception e) {
			LOGGER.error("Ocurrio un error inesperado",e);
			throw e;
			
		}
	}

	public JSONObject  validaFormaPago(String idpoliza) throws Exception {
		try {
			return pagoPolizaDao.validaFormaPago(idpoliza);
	
		}catch(Exception e) {
			LOGGER.error("Ocurrio un error inesperado",e);
			throw e;
			
		}
	}

	public boolean guardaDatosCuenta(Long idpoliza,Character envioIBANAgr) throws Exception {
		try {
			return pagoPolizaDao.guardaDatosCuenta(idpoliza,envioIBANAgr);
	
		}catch(Exception e) {
			LOGGER.error("Ocurrio un error inesperado",e);
			throw e;
			
		}
		
	}

	public BigDecimal getPctMinimoFinanciacion(BigDecimal codPlan,
			BigDecimal codLinea, String codModulo) throws Exception {
		BigDecimal resultado=pagoPolizaDao.getPctMinimoFinanciacion(codPlan, codLinea, codModulo);
		
		return resultado;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	
	public JSONObject  validaEntidadPermitida(String idpoliza,String entidad) throws Exception {
		try {
			return pagoPolizaDao.validaEntidadPermitida(idpoliza, entidad);
	
		}catch(Exception e) {
			LOGGER.error("Ocurrio un error inesperado",e);
			throw e;
			
		}
	}



}
