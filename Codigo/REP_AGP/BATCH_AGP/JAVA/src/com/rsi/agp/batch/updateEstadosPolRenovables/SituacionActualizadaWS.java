package com.rsi.agp.batch.updateEstadosPolRenovables;


import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.renovables.GastosRenovacionAplicados;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.contratacion.costePoliza.Financiacion;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ConsultarContratacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ConsultarContratacionResponse;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion_Service;


public final class SituacionActualizadaWS {
	
	private static final Logger logger = Logger.getLogger(SituacionActualizadaWS.class);
	
	private SituacionActualizadaWS() {
	}
		// LLamada al ws de ContratacionSCModificacion para recoger la situacion actualizada de la poliza renovable
			public static es.agroseguro.contratacion.Poliza updatePolizasRenovables(Long plan, String referencia,ContratacionSCModificacion objSitActWs)	throws Exception {
				Integer reintentos = null;
				ResourceBundle bundle = ResourceBundle.getBundle("agp_procesar_plz_renovables");
				String strReintentos = bundle.getString("reintentos");
				if (!strReintentos.equals(""))
					reintentos = Integer.parseInt(strReintentos);
				WSUtils.addSecurityHeader(objSitActWs);		

				ConsultarContratacionRequest parameters = new ConsultarContratacionRequest();
				parameters.setPlan(plan.intValue());
				parameters.setReferencia(referencia);
				
				ConsultarContratacionResponse response = null;
				es.agroseguro.contratacion.Poliza polizaSit = null;
				int vuelta = 0;
				for (int ciclo=0;ciclo<reintentos;ciclo++){
					vuelta = ciclo+1;
					try {
						logger.debug("# ciclo "+vuelta+"/"+reintentos);
						 logger.debug(" # Llamando situacion actualizada referencia "+ referencia + " plan " + plan);
			  		    response = objSitActWs.consultarContratacion(parameters); // Llamada al WS
						logger.debug("# ciclo "+vuelta+"/"+reintentos +" OK");
						PolizaActualizadaResponse respuesta = getPolizaActualizadaFromResponse(response, true);
						if (respuesta != null && respuesta.getPolizaGanado() != null && respuesta.getPolizaGanado().getPoliza() != null){
							//&& respuesta.getPolizaGanado().getPoliza().getCostePoliza() != null){
							//costePoliza = respuesta.getPolizaGanado().getPoliza().getCostePoliza();
							polizaSit = respuesta.getPolizaGanado().getPoliza();
							
						}
						return polizaSit;
					}catch  (SWConsultaContratacionException ex) {
						logger.debug("# SWConsultaContratacionException: ", ex);					
					} catch (Exception e) {
						if (e instanceof javax.xml.ws.WebServiceException) {
							Throwable cause = e; 
							if  ((cause = cause.getCause()) != null){
						        if(cause instanceof ConnectException){
									logger.debug("# ConnectException: ",e);
									logger.debug("# Reintentando conexi�n "+vuelta+"/"+reintentos +"...");
						        }else {
									logger.error("# Error en WS Exception: ",e);
									break;
								}
						    }else {
						    	logger.error("# Error Exception: ",e);
								break;
						    }
						}else {
							logger.error("# Error exception tipo: ",e);
							break;
						}
					}
				}//FIN bucle ciclo
				return polizaSit;

			}
		
		// Objeto para la llamada al WS de Contrataci�n (para las p�lizas renovables)
		public static ContratacionSCModificacion getObjetoActRenWs()	throws Exception {
			ContratacionSCModificacion contratacionSC = null;	
			URL wsdlLocation = null;
			String url = "";
			if (!WSUtils.isProxyFixed())
				WSUtils.setProxy();
			try {
				File ficherowsdl = new File(WSUtils.getBundleProp("anexoModificacionWS.wsdl"));
				url = ficherowsdl.getAbsolutePath();
				wsdlLocation = new URL("file:" + url);
			} catch (MalformedURLException ex) {
				throw new Exception("Imposible recuperar el WSDL de consulta de contrataci�n. Revise la Ruta: "+ url, ex);
			} catch (NullPointerException ex) {
				throw new Exception("Imposible obtener el WSDL desde anexoModificacionUnificadoWS. Revise la Ruta: "+ wsdlLocation.toString(), ex);
			}	
			
			String wsLocation = WSUtils.getBundleProp("anexoModificacionWS.location");
			String wsPort 	  = WSUtils.getBundleProp("anexoModificacionWS.port");
			String wsService  = WSUtils.getBundleProp("anexoModificacionWS.service");
			QName serviceName = new QName(wsLocation, wsService);
			QName portName    = new QName(wsLocation, wsPort);
						
			// LOGS PARAMETROS WS
			logger.debug("wsdlLocation: " + wsdlLocation.toString());
			logger.debug("wsLocation: " + wsLocation.toString());
			logger.debug("wsPort: " + wsPort.toString());
			logger.debug("wsServicee: " + wsService.toString());
			
			try {
				ContratacionSCModificacion_Service srv = new ContratacionSCModificacion_Service(wsdlLocation, serviceName);
				contratacionSC = srv.getPort(portName,ContratacionSCModificacion.class);
				logger.debug(contratacionSC.toString());
			}catch  (Exception ex) {
				logger.debug("# Error en WSn Exception: "+ex.getMessage().toString());
				throw new Exception(ex.getMessage().toString(), ex);
			}
			return contratacionSC;
		}

		
		/**
		 * M�todo que rellena el objeto PolizaActualizadaResponse
		 * 
		 * @param response
		 * @return
		 * @throws UnsupportedEncodingException
		 * @throws XmlException
		 * @throws IOException
		 */
		private static PolizaActualizadaResponse getPolizaActualizadaFromResponse(
				final ConsultarContratacionResponse response,
				final boolean isPolizaGanado) throws UnsupportedEncodingException,
				XmlException, IOException {
			PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
			// xml de la p�liza principal
			Base64Binary ppal = response.getPolizaPrincipal();
			byte[] byteArrayPpal = ppal.getValue();
			String xmlDataPpal = new String(byteArrayPpal, WSUtils.DEFAULT_ENCODING);
			XmlObject poliza = XmlObject.Factory
					.parse(new StringReader(xmlDataPpal));
			if (isPolizaGanado) {
				respuesta.setPolizaGanado((es.agroseguro.contratacion.PolizaDocument) poliza);
			} else {
				respuesta.setPolizaPrincipal((es.agroseguro.seguroAgrario.contratacion.PolizaDocument) poliza);
				// xml de la p�liza complementaria
			}
			return respuesta;
		}
		
	public static void guardaDistribucionCoste(final Poliza polizaHbm, final PolizaRenovable polRenov,
			final CostePoliza costePoliza, final Session session) throws Exception {
		Transaction trans = null;
		Transaction trans2 = null;
		String referencia = polRenov.getReferencia();
		Long plan = polRenov.getPlan();
		
		// recogo la poliza de bbdd
		if (polizaHbm != null) {
			Set<DistribucionCoste2015> distribucionCoste2015s = new HashSet<DistribucionCoste2015>(0);
			DistribucionCoste2015 dc = new DistribucionCoste2015();

			try {
				Financiacion financiacion = null;

				if (costePoliza != null) {
					CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza.getCosteGrupoNegocioArray();

					if (costeGrupoNegocioArray != null) {
						/*
						 * ESC-8454 DNF 07/05/2020 no puedo tener dos transacciones iniciadas a la vez
						 * por ello desplazo la variable trans despues del trans2.commit()
						 */
						trans2 = session.beginTransaction();
						borrarDistCosteseByIdpoliza(polizaHbm.getIdpoliza(), session);
						trans2.commit();

						trans = session.beginTransaction();
						/* FIN ESC-8454 DNF 07/05/2020 */

						financiacion = costePoliza.getFinanciacion();

						for (CosteGrupoNegocio costeGrupoNeg : costeGrupoNegocioArray) {
							if (costeGrupoNeg != null) {
								logger.debug("# Actualizando Distribucion de costes de la poliza " + referencia
										+ " plan " + plan);
								// Rellena el objeto DistribucionCoste2015
								String codModulo = polizaHbm.getCodmodulo();
								BigDecimal filaComparativa = new BigDecimal(1);
								BigDecimal idComparativa = null;
								try {
									Set<ModuloPoliza> moduloPolizas = polizaHbm.getModuloPolizas();
									if (moduloPolizas != null && !moduloPolizas.isEmpty()) {
										for (ModuloPoliza mp : moduloPolizas) {
											idComparativa = BigDecimal.valueOf(mp.getId().getNumComparativa());
											break;
										}
									}
								} catch (Exception e) {
									logger.error("Error al obtener el idComparativa. " + e.getMessage());
								}
								dc = crearDC2015Unificada(polizaHbm, codModulo, filaComparativa, idComparativa,
										costePoliza, costeGrupoNeg, financiacion);
								// ESC-25609: asignamos las comisiones a la distribucion de costes de la poliza
								calcularComisiones(dc, polRenov);
								// Carga las subvenciones de CCAA y ENESA en el objeto de la distribucion de
								// costes
								dc = cargarSubvencionesDC(dc, costeGrupoNeg);
								// Bonificaciones y recargos
								dc = cargaBonifRecargUnificado(dc, costeGrupoNeg);
								distribucionCoste2015s.add(dc);
								session.saveOrUpdate(dc);
							}
						}
						trans.commit();
						logger.debug("# Distribucion de costes de la poliza " + referencia + " plan " + plan
								+ " actualizada OK");
					}
				}
			} catch (Exception ex) {
				logger.error("# Ha ocurrido algun error al guardar la distribucion de costes", ex);
			}
		} else {
			logger.debug("# La poliza con referencia " + referencia + " y plan " + plan + " no se encuentra en BBDD");
		}
	}
	
	private static DistribucionCoste2015 cargaBonifRecargUnificado(DistribucionCoste2015 dc,CosteGrupoNegocio costeGrupoNegocio) {
		es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] boniRecargo = costeGrupoNegocio.getBonificacionRecargoArray();
		if (boniRecargo != null) {
			// Bucle para a�adir las distribuciones de coste de las Bonificaciones Recargo
			for (int i = 0; i < boniRecargo.length; i++) {
				BonificacionRecargo2015 bon = new BonificacionRecargo2015();
				bon.setDistribucionCoste2015(dc);
				bon.setCodigo(new BigDecimal(boniRecargo[i].getCodigo()));
				bon.setImporte(boniRecargo[i].getImporte());
				dc.getBonificacionRecargo2015s().add(bon);
			}
		}
		return dc;
	}
	
	private static DistribucionCoste2015 crearDC2015Unificada(Poliza polHbm, String codModulo,
			BigDecimal filaComparativa, BigDecimal idComparativa, CostePoliza costePoliza,
			CosteGrupoNegocio costeGrupoNegocio, Financiacion financiacion) throws DAOException {

		DistribucionCoste2015 dc;
		dc = new DistribucionCoste2015();
		dc.setPoliza(polHbm);
		dc.setCodmodulo(polHbm.getCodmodulo());
		dc.setFilacomparativa(filaComparativa);
		dc.setIdcomparativa(idComparativa);
		dc.setCostetomador(costeGrupoNegocio.getCosteTomador());
		dc.setPrimacomercial(costeGrupoNegocio.getPrimaComercial());
		dc.setPrimacomercialneta(costeGrupoNegocio.getPrimaComercialNeta());
		dc.setRecargoconsorcio(costeGrupoNegocio.getRecargoConsorcio());
		dc.setReciboprima(costeGrupoNegocio.getReciboPrima());
		dc.setTotalcostetomador(costePoliza.getTotalCosteTomador());
		dc.setGrupoNegocio(costeGrupoNegocio.getGrupoNegocio().charAt(0));
		if (financiacion != null && financiacion.getRecargoAval()!= null) {
			dc.setRecargoaval(financiacion.getRecargoAval());
		}
		if (financiacion != null && financiacion.getRecargoFraccionamiento()!= null) {
			dc.setRecargofraccionamiento(financiacion.getRecargoFraccionamiento());
		}
		
		if (polHbm.getModuloPolizas() != null && !polHbm.getModuloPolizas().isEmpty()) {
			ModuloPoliza moduloPoliza = (ModuloPoliza) polHbm.getModuloPolizas().iterator().next();
			
			if (moduloPoliza.getId().getNumComparativa() != null) {
				dc.setIdcomparativa(new BigDecimal(moduloPoliza.getId().getNumComparativa()));
			}
		}
		
		return dc;
	}	

	private static void calcularComisiones(DistribucionCoste2015 distCoste, final PolizaRenovable polRenov) {
		logger.debug("## INICIO calculo de las comisiones");
		try {
			if (distCoste != null && distCoste.getPoliza() != null && polRenov.getGastosRenovacionAplicados() != null) {
				logger.debug("## Poliza renovable:" + polRenov.getId() + ", referencia:" + polRenov.getReferencia());
				logger.debug("## Poliza:" + distCoste.getPoliza().getIdpoliza() + ", referencia:"
						+ distCoste.getPoliza().getReferencia());

				for (GastosRenovacionAplicados gastoApl : polRenov.getGastosRenovacionAplicados()) {
					logger.debug("## GN distribucion de costes:" + distCoste.getGrupoNegocio() + ", GN gastos:"
							+ gastoApl.getGrupoNegocio());

					if (Character.compare(distCoste.getGrupoNegocio(), gastoApl.getGrupoNegocio()) == 0) {
						if (null == gastoApl.getComisionAplEntidad() || null == gastoApl.getComisionAplEsMed()
								|| null == distCoste.getPrimacomercialneta()) {
							logger.debug("## NULL >> ComisionAplEntidad():" + gastoApl.getComisionAplEntidad()
									+ " ComisionAplEsMed" + gastoApl.getComisionAplEsMed() + " PrimaComercialNeta"
									+ distCoste.getPrimacomercialneta());
							break;
						}

						BigDecimal cien = new BigDecimal(100);
						BigDecimal impComTotalE = new BigDecimal(0);
						BigDecimal impComTotalE_S = new BigDecimal(0);
						BigDecimal comEntidad = gastoApl.getComisionAplEntidad();
						BigDecimal comESMed = gastoApl.getComisionAplEsMed();
						BigDecimal primaNeta = distCoste.getPrimacomercialneta();

						logger.debug("## PrimaComercialNeta:" + primaNeta + ", comEntidad:" + comEntidad + ", comESMed:"
								+ comESMed + ", grupoNegocio: " + distCoste.getGrupoNegocio());

						// Calculo Entidad - Aqui no se aplican ni recargos ni descuentos
						impComTotalE = primaNeta.multiply(comEntidad.divide(cien));

						// Calculo Subentidad Mediadora - Aqui no se aplican ni recargos ni descuentos
						impComTotalE_S = primaNeta.multiply(comESMed.divide(cien));

						logger.debug("## Comisiones calculadas ## impComTotalE:" + impComTotalE + ", impComTotalE_S:"
								+ impComTotalE_S);

						distCoste.setImpComsEntidad(impComTotalE);
						distCoste.setImpComsESMed(impComTotalE_S);
					}
				}
			} else {
				if (distCoste == null)
					logger.debug("## La distribucion de costes es NULL");
				if (distCoste.getPoliza() == null)
					logger.debug("## La distribucion de costes no tiene asociada una poliza");
				if (null == polRenov.getGastosRenovacionAplicados())
					logger.debug("## La lista de GastosRenovacionAplicados está a NULL");
			}
		} catch (Exception e) {
			logger.error("## ERROR en el calculo de las comisiones ## ", e);
		}
		logger.debug("## FIN calculo de las comisiones");
	}
	
	private static DistribucionCoste2015 cargarSubvencionesDC(final DistribucionCoste2015 dc,final CosteGrupoNegocio costeGrupoNegocio) {
		es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subCCAA = costeGrupoNegocio.getSubvencionCCAAArray();
		es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subEnesa = costeGrupoNegocio.getSubvencionEnesaArray();
		// Subvenciones CCAA
		if (subCCAA != null) {
			for (int i = 0; i < subCCAA.length; i++) {
				// Bucle para a�adir las distribuciones de coste de las subvenciones CCAA
				DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
				subv.setDistribucionCoste2015(dc);
				subv.setCodorganismo(subCCAA[i].getCodigoOrganismo().charAt(0));
				subv.setImportesubv(subCCAA[i].getImporte());
				dc.getDistCosteSubvencion2015s().add(subv);
			}
		}
		// Subvenciones ENESA
		if (subEnesa != null) {
			
			for (int i = 0; i < subEnesa.length; i++) {
				DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
				subv.setDistribucionCoste2015(dc);
				subv.setCodorganismo('0');
				subv.setCodtiposubv(new BigDecimal(subEnesa[i].getTipo()));
				subv.setImportesubv(subEnesa[i].getImporte());
				dc.getDistCosteSubvencion2015s().add(subv);
			}
		}
		return dc;
	}
	
	// Recupera la p�liza a partir de la referencia y el plan
		protected static Poliza getPolizaBBDD(final Session session,
				final String referencia,final Long plan) {
			
			Poliza poliza = null;
			if (!referencia.equals("") && plan != null){
				try{
					Criteria crit = session.createCriteria(Poliza.class);
					crit.createAlias("linea", "linea");
					crit.add(Restrictions.eq("referencia",referencia));
					crit.add(Restrictions.eq("linea.codplan",new BigDecimal(plan.toString())));
					poliza = (Poliza) crit.uniqueResult();
				
				}catch(Exception e ){
					logger.error("## ERROR en getPolizaBBDD ##  ",e);
				}
			}
			return poliza;
		}
		
		protected static void borrarDistCosteseByIdpoliza(Long idPoliza, Session session){
			try{
				String str = " delete from o02agpe0.tb_distribucion_costes_2015 where idpoliza="+idPoliza;	
				//logger.debug(str);
				Query query = session.createSQLQuery(str);
				query.executeUpdate();
				
			}catch(Exception e ){
				logger.error("## ERROR en borrado de la distribuci�n coste de la poliza con idpoliza:"+idPoliza+"##  ",e);
			}
		}
}