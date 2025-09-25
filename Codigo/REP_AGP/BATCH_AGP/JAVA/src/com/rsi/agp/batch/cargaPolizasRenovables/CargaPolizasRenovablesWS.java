package com.rsi.agp.batch.cargaPolizasRenovables;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Session;

import com.rsi.agp.core.jmesa.service.impl.PolizaRenBean;
import com.rsi.agp.core.jmesa.service.impl.utilidades.AltaPolizaRenovableService;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.comisiones.PolizasPctComisionesDao;

import es.agroseguro.estadoRenovacion.Renovacion;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovablesDocument;
import es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ObjectFactory;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ParametrosListaPolizasRenovables;

public final class CargaPolizasRenovablesWS {
	private static final Logger logger = Logger.getLogger(CargaPolizasRenovables.class);

	private CargaPolizasRenovablesWS() {
	}

	/*
	 * ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S
	 * Mediadora * Inicio
	 */
	/*
	 * protected static List<PolizaRenBean> getListPolizasRenovables(final
	 * List<PolizaRenBean> lstRes,final Long plan, final List<BigDecimal>
	 * lstLineasSW, final Session session, final Map<String, BigDecimal[]>
	 * mapPctComs, final Integer reintentos, final PolizasPctComisionesDao
	 * polizasPctComisionesDao) throws Exception {
	 */
	public static List<PolizaRenBean> getListPolizasRenovables(final List<PolizaRenBean> lstRes, final Long plan,
			final List<BigDecimal> lstLineasSW, final Session session, final Integer reintentos,
			final PolizasPctComisionesDao polizasPctComisionesDao,
			final AltaPolizaRenovableService altaPolizaRenovableService) throws Exception {
		/*
		 * ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S
		 * Mediadora * Inicio
		 */
		
		logger.debug("CargaPolizasRenovablesWS - getListPolizasRenovables[INIT]" );

		URL wsdlLocation = null;
		String url = "";
		int cont = 1;
		int f;
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();

		try {
			File ficherowsdl = new File(WSUtils.getBundleProp("contratacionRenovacionesWS.wsdl"));
			url = ficherowsdl.getAbsolutePath();
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException ex) {
			throw new Exception("Imposible recuperar el WSDL de Gastos de una poliza Renovable. Revise la Ruta: " + url,
					ex);
		} catch (NullPointerException ex) {
			throw new Exception("Imposible obtener el WSDL de Gastos de una poliza Renovable. Revise la Ruta: "
					+ wsdlLocation.toString(), ex);
		}
		// PARAMETROS WS
		String wsLocation = WSUtils.getBundleProp("contratacionRenovacionesWS.location");
		String wsPort = WSUtils.getBundleProp("contratacionRenovacionesWS.port");
		String wsService = WSUtils.getBundleProp("contratacionRenovacionesWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsService: " + wsService.toString());
		
		try {
			ContratacionRenovaciones_Service srv = new ContratacionRenovaciones_Service(wsdlLocation, serviceName);
			ContratacionRenovaciones contratRen = (ContratacionRenovaciones) srv.getPort(portName,
					ContratacionRenovaciones.class);
			WSUtils.addSecurityHeader(contratRen);
			ParametrosListaPolizasRenovablesAgroplus paramListPolReq = new ParametrosListaPolizasRenovablesAgroplus();

			// datos fijos
			paramListPolReq.setPlan(plan.intValue());
			List<String> lstEstadosWs = BBDDCargaPolRenUtil.getEstadosPolRenovables(session);
			StringBuilder polKO = null;
			StringBuilder polOK = null;

			// List<JAXBElement<List<BigInteger>>> lstEstados = new
			// ArrayList<JAXBElement<List<BigInteger>>>();
			for (BigDecimal linea : lstLineasSW) { // recorremos la lista de líneas y llamamos por cada una de ellas al
													// SW de ListaPolizasRenovables
				paramListPolReq.setLinea(Integer.parseInt(linea.toString()));
				for (String estWs : lstEstadosWs) {
					List<JAXBElement<List<BigInteger>>> lstEstados = new ArrayList<JAXBElement<List<BigInteger>>>();
					List<BigInteger> lstBig = new ArrayList<BigInteger>();
					lstBig.add(new BigInteger(estWs));
					// Se añade el estado a los parametros de llamada al SW
					JAXBElement<List<BigInteger>> bb = new ObjectFactory()
							.createParametrosListaPolizasRenovablesListaEstados(lstBig);
					lstEstados.add(bb);
					// paramListPolReq.setEstado(bb);
					paramListPolReq.setListaEstados(lstEstados);

					for (f = 0; f < 3; f++) { // Se buscarán las pólizas con fecha de renovación desde hoy hasta dentro
												// de TRESE MESES, una llamada al SW por cada mes
						polKO = new StringBuilder();
						polOK = new StringBuilder();
						Calendar cDesde = Calendar.getInstance();
						Calendar cHasta = Calendar.getInstance();
						cDesde.add(Calendar.MONTH, f);

						// MPM - Se suma uno al mes ya que para Calendar los meses van de 0 a 11 y para
						// XMLGregorianCalendar van de 1 a 12
						JAXBElement<XMLGregorianCalendar> fecDesde = getFechaDesde(cDesde.get(Calendar.YEAR),
								cDesde.get(Calendar.MONTH) + 1, cDesde.get(Calendar.DAY_OF_MONTH));

						if (fecDesde != null) {
							// logger.debug("## Fecha renovacion Desde: " + fecDesde.getValue()+" ##");
							paramListPolReq.setFechaRenovacionDesde(fecDesde);
						}
						cHasta.add(Calendar.MONTH, f + 1);

						// MPM - Se suma uno al mes ya que para Calendar los meses van de 0 a 11 y para
						// XMLGregorianCalendar van de 1 a 12
						JAXBElement<XMLGregorianCalendar> fecHasta = getFechaHasta(cHasta.get(Calendar.YEAR),
								cHasta.get(Calendar.MONTH) + 1, cHasta.get(Calendar.DAY_OF_MONTH));
						if (fecHasta != null) {
							// logger.debug("## Fecha renovacion Hasta: " + fecHasta.getValue()+" ##");
							paramListPolReq.setFechaRenovacionHasta(fecHasta);
						}
						logger.debug("## CALL WS - LISTA POL REN - PLAN: " + plan.toString() + " LINEA: "
								+ linea.toString() + " fechaDesde: " + fecDesde.getValue() + " fechaHasta: "
								+ fecHasta.getValue() + " estado: " + estWs + " ##");
						Renovacion[] renArr = null;

						int vuelta = 1;
						for (int ciclo = 0; ciclo < reintentos; ciclo++) {
							try {
								logger.debug("# ciclo " + vuelta + "/" + reintentos);
								renArr = getArrayRenovaciones(contratRen, paramListPolReq);
								logger.debug("# ciclo " + vuelta + "/" + reintentos + " OK");
								logger.debug("## TOTAL POLIZAS A TRATAR: " + renArr.length + " PLAN: " + plan.toString()
										+ " LINEA: " + linea.toString() + " estado: " + estWs + " ##");
								break;
							} catch (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException ex) {
								logger.error("Error en WS en agrException: " + ex.getMessage().toString());
								List<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> lstErrores = ex
										.getFaultInfo().getError();
								String errores = "";
								for (es.agroseguro.serviciosweb.contratacionrenovaciones.Error error : lstErrores) {
									errores = errores + error.getMensaje().toString() + ".";
									logger.error("# Error en agrException: " + error.getMensaje().toString());
									break;
								}
								break;
							} catch (Exception e) {
								if (e instanceof javax.xml.ws.WebServiceException) {
									Throwable cause = e;
									if ((cause = cause.getCause()) != null) {
										if (cause instanceof ConnectException) {
											if (vuelta != reintentos.intValue()) {
												vuelta++;
												logger.debug(
														"# Reintentando conexion " + vuelta + "/" + reintentos + "...");
											} else {
												logger.debug(
														"# Fin reintentos por Timeout: " + e.getMessage().toString());
												break;
											}
										} else {
											logger.debug("# otro tipo de Exception: " + e.getMessage().toString());
											break;
										}
									} else {
										logger.error("# Error exc. tipo: " + e.getClass().toString() + " "
												+ e.getMessage().toString());
										break;
									}
								} else {
									logger.error("# Error exception tipo: " + e.getClass().toString() + " "
											+ e.getMessage().toString());
									break;
								}
							}
						} // fin bucle ciclo
						if (renArr != null) {
							for (Renovacion renov : renArr) {

								/* ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S
								 * Mediadora * Inicio */
								 
								/* BBDDCargaPolRenUtil.importaPolizaRen(lstRes,renov,
								 * session,mapPctComs,cont,polKO,polOK,polizasPctComisionesDao); */
								
								BBDDCargaPolRenUtil.importaPolizaRen(lstRes, renov, session, cont, polKO, polOK,
										polizasPctComisionesDao, plan, altaPolizaRenovableService);
								cont++;
								// }
							}
							cont = 1;
						}
						if (polKO.length() > 0) {
							logger.debug("#### Polizas renovables ya existentes: " + polKO.toString() + " #### ");
						}
						if (polOK.length() > 0) {
							logger.debug("#### Polizas renovables nuevas: " + polOK.toString() + " #### ");
						}
					} // fin bucle meses fechaHasta
				} // fin bucle lstEstados
			} // fin bucle lstLineasSW

		} catch (Exception ex) {
			logger.error("# Error global en WS Exception: " + ex.getMessage().toString(), ex);
			return lstRes;
		}
		
		logger.debug("CargaPolizasRenovablesWS - getListPolizasRenovables [END]" );

		return lstRes;
	}

	/**
	 * Devuelve la fechaDesde recibida como parámetro formateada para la llamada al
	 * SW
	 * 
	 * @param fecha
	 * @return
	 */
	public static JAXBElement<XMLGregorianCalendar> getFechaDesde(int ano, int mes, int dia) {
		JAXBElement<XMLGregorianCalendar> fecFormateada = null;
		try {
			XMLGregorianCalendar fechaHCal = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(ano, mes, dia,
					DatatypeConstants.FIELD_UNDEFINED);
			fecFormateada = new ObjectFactory().createParametrosListaPolizasRenovablesFechaRenovacionDesde(fechaHCal);
		} catch (Exception e) {
			logger.debug("Error al obtener la fechaDesde para la llamada al SW", e);
		}
		return fecFormateada;
	}

	/**
	 * Devuelve la fechaHasta recibida como parámetro formateada para la llamada al
	 * SW
	 * 
	 * @param fecha
	 * @return
	 */
	public static JAXBElement<XMLGregorianCalendar> getFechaHasta(int ano, int mes, int dia) {
		JAXBElement<XMLGregorianCalendar> fecFormateada = null;
		try {
			XMLGregorianCalendar fechaHCal = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(ano, mes, dia,
					DatatypeConstants.FIELD_UNDEFINED);
			fecFormateada = new ObjectFactory().createParametrosListaPolizasRenovablesFechaRenovacionHasta(fechaHCal);
		} catch (Exception e) {
			logger.debug("Error al obtener la fechaHasta para la llamada al SW", e);
		}
		return fecFormateada;
	}

	/**
	 * Realiza la llamada al SW y devuelve el array de objetos Renovacion obtenido
	 * 
	 * @param contratRen
	 * @param paramListPolReq
	 * @return
	 * @throws AgrException
	 * @throws XmlException
	 * @throws UnsupportedEncodingException
	 */
	public static Renovacion[] getArrayRenovaciones(ContratacionRenovaciones contratRen,
			ParametrosListaPolizasRenovables paramListPolReq)
			throws AgrException, UnsupportedEncodingException, XmlException {
		return ListaPolizasRenovablesDocument.Factory
				.parse(new String(contratRen.listaPolizasRenovables(paramListPolReq).getPolizasRenovables().getValue(),
						"UTF-8"))
				.getListaPolizasRenovables().getRenovacionArray();
	}

}
