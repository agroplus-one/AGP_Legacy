package com.rsi.agp.batch.importacion;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rsi.agp.batch.procesarPolizasRenovables.PPRConstants;
import com.rsi.agp.batch.procesarPolizasRenovables.ProcesarPolizasRenovablesWS;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.contratacion.costePoliza.CostePoliza;

public class BBDDPolizaUtilGanado {
	private static final Logger logger = Logger.getLogger(BBDDPolizaUtil.class);
	

	/**
	 * Para polizas de ganado
	 * @param polizaHbm
	 * @param poliza
	 * @param idEnvio
	 * @param session
	 * @throws Exception
	 */
	protected static void populateAndValidatePolizaPpal(final Poliza polizaHbm,
			final es.agroseguro.contratacion.Poliza poliza,
			final Long idEnvio, final Session session) throws Exception {
		logger.info("populateAndValidatePolizaPpal");
		Transaction trans   = session.beginTransaction();
		//Linea		
		Linea linea=BBDDPolizaUtil.getLineaSeguroBBDD(new BigDecimal(poliza.getLinea()),
				new BigDecimal(poliza.getPlan()), session);
		logger.info("Asigna datos de la linea");
		polizaHbm.setLinea(linea);
		
		// Colectivo
		com.rsi.agp.dao.tables.admin.Colectivo col = BBDDPolizaUtil.getColectivoBBDD(poliza.getColectivo().getReferencia(), poliza.getColectivo().getDigitoControl(), session);
		polizaHbm.setColectivo(col);
		logger.info("... del colectivo");
		// Asegurado
		com.rsi.agp.dao.tables.admin.Asegurado aseg = BBDDPolizaUtil.getAseguradoBBDD(poliza.getAsegurado().getNif(),
				col.getTomador().getEntidad().getCodentidad(), session);
		polizaHbm.setAsegurado(aseg);
		polizaHbm.setDiscriminante(aseg.getDiscriminante());
		logger.info("...del asegurado");
		// Usuario
		polizaHbm.setUsuario(BBDDPolizaUtil.getUsuarioPolizaBBDD(session));
		logger.info("...del usuario");

		// importe
		if (poliza.getCostePoliza() != null){
			CostePoliza costePoliza = poliza.getCostePoliza();
			if (costePoliza.getTotalCosteTomador() != null)
				polizaHbm.setImporte(costePoliza.getTotalCosteTomador());
		}
		logger.info("...del importe");
		// Estado Poliza
		polizaHbm.setEstadoPoliza(BBDDEstadosUtil.getEstadoPolizaBBDD(session));
		logger.info("...del estado");
		// estado de pago ????
//		polizaHbm.setEstadoPagoAgp(getEstadoPagoBBDD(session)); //no pagada

		// Datos fijos o de importacion directa desde la situacion actual
		polizaHbm.setExterna(PPRConstants.POLIZA_EXTERNA);
		polizaHbm.setTipoReferencia(Constants.MODULO_POLIZA_PRINCIPAL);
		polizaHbm.setTienesiniestros(Constants.CHARACTER_N);
		polizaHbm.setTieneanexomp(Constants.CHARACTER_N);
		polizaHbm.setTieneanexorc(Constants.CHARACTER_N);
		polizaHbm.setPacCargada(Constants.CHARACTER_N);
		polizaHbm.setReferencia(poliza.getReferencia());
		polizaHbm.setDc(BigDecimal.valueOf(poliza.getDigitoControl()));
		polizaHbm.setCodmodulo(poliza.getCobertura().getModulo().trim());
		polizaHbm.setOficina(polizaHbm.getUsuario().getOficina().getId().getCodoficina().toString());
		logger.info("...datos fijos");
		// Datos de pago
		populatePago(polizaHbm, poliza.getPago(), poliza.getCuentaCobroSiniestros());
		
		
		polizaHbm.setEstadoPagoAgp(BBDDEstadosUtil.getEstadoPagoBBDD(session));
		logger.info("...datos de pago");
		
		session.saveOrUpdate(polizaHbm);
		trans.commit();
		
		
		//Modulo
		populateModulo(poliza,session, polizaHbm, linea.getLineaseguroid());			
		logger.info("...del modulo");
		//Distribucion de costes
		ProcesarPolizasRenovablesWS.guardaDistribucionCoste(polizaHbm,poliza,session);
		logger.info("...distribucion de costes");
		// Explotaciones
		ProcesarPolizasRenovablesWS.populateExplotaciones(polizaHbm,poliza, session,linea.getLineaseguroid());
		logger.info("...explotaciones");
		// actualizamos el historico de estados de la poliza
		ProcesarPolizasRenovablesWS.actualizarHistoricoPoliza(polizaHbm,session);
		logger.info("...Historico de estados");
		
		
		
	}
	
	private static void populatePago(final Poliza polizaHbm, final es.agroseguro.contratacion.Pago pagoAct,
			final es.agroseguro.contratacion.CuentaCobroSiniestros ccsAct) {
		
			PagoPoliza pago = new PagoPoliza();
			pago.setPoliza(polizaHbm);
			pago.setFormapago(pagoAct.getForma() != null ? pagoAct.getForma().charAt(0) : 'C');
			pago.setFecha(pagoAct.getFecha() != null ? pagoAct.getFecha().getTime() : null);
			pago.setImporte(pagoAct.getImporte());
			
			// Si vienen informados los datos del IBAN para el pago de prima
			if (pagoAct.getCuenta() != null && !StringUtils.isNullOrEmpty(pagoAct.getCuenta().getIban())) {
				pago.setIban(pagoAct.getCuenta().getIban().substring(0,4));
				pago.setCccbanco(pagoAct.getCuenta().getIban().substring(4));
			}
			// Si no vienen informados se inserta el IBAN a 'ES' ya que es un dato obligatorio en la BBDD
			else {
				pago.setIban("ES");
			}
			
			if (ccsAct != null && !StringUtils.isNullOrEmpty(ccsAct.getIban())) {
				pago.setIban2(ccsAct.getIban().substring(0,4));
				pago.setCccbanco2(ccsAct.getIban().substring(4));
			}
			
			pago.setDomiciliado(pagoAct.getDomiciliado() != null ? pagoAct.getDomiciliado().charAt(0) : null);
			pago.setTipoPago(new BigDecimal(0));
			
			Set<PagoPoliza> pagosPoliza = new HashSet<PagoPoliza>();
			pagosPoliza.add(pago);
			polizaHbm.setPagoPolizas(pagosPoliza);
			
			logger.debug("MPM - Informacion del pago insertada");
		
	}
	
	private static void populateModulo(final es.agroseguro.contratacion.Poliza poliza,final Session session, 
			final Poliza polizaHbm, Long lineaSeguroId){
		try {
			
			Transaction trans2   = session.beginTransaction();

			Set<ModuloPoliza> modulosPolHbm;
			ModuloPoliza moduloPolHbm;			
			modulosPolHbm = new HashSet<ModuloPoliza>();
			ModuloPolizaId modId = new ModuloPolizaId();
			modId.setCodmodulo(poliza.getCobertura().getModulo().trim());
			modId.setLineaseguroid(lineaSeguroId);
			modId.setIdpoliza(polizaHbm.getIdpoliza());
			Long secuencia = BBDDCoberturasUtil.getSecuenciaComparativa(session);
			modId.setNumComparativa(secuencia.longValue());
			moduloPolHbm = new ModuloPoliza(modId,polizaHbm,null,1);

			modulosPolHbm.add(moduloPolHbm);
			session.saveOrUpdate(moduloPolHbm);
			
			trans2.commit();
			
		} catch (Exception ex) {
			logger.error("## Error al crear el modulo de la poliza : "+poliza.getReferencia() +" LINEA: " + poliza.getLinea() + " PLAN: "+poliza.getPlan() +" ## ",ex);
			//session.delete(polizaHbm);
		}
	}
	
}
