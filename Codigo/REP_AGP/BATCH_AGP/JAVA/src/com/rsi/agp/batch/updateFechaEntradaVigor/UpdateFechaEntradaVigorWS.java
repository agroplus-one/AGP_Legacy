package com.rsi.agp.batch.updateFechaEntradaVigor;

import java.io.File;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.batch.updateFechaEntradaVigor.ParametrosListaPolizasRenovablesAgroplus;
import com.rsi.agp.batch.updateFechaEntradaVigor.UpdateFechaEntradaVigor.PolizaBean;
import com.rsi.agp.core.util.WSUtils;
import es.agroseguro.estadoRenovacion.Renovacion;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovables;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovablesDocument;
import es.agroseguro.seguroAgrario.recibos.Colectivo;
import es.agroseguro.seguroAgrario.recibos.Fase;
import es.agroseguro.seguroAgrario.recibos.FaseDocument;
import es.agroseguro.seguroAgrario.recibos.Individual;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ListaPolizasRenovablesResponse;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ObjectFactory;
import es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ConsultarEstadoRequest;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ConsultarEstadoResponse;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza_Service;
import es.agroseguro.tipos.PolizaReferenciaTipo;

public final class UpdateFechaEntradaVigorWS {
	private static final Logger logger = Logger.getLogger(UpdateFechaEntradaVigor.class);
	
	// LLamada al ws de Seguimiento para actualizar la fecha vigor de las pólizas agrícolas
	protected static int updatePolizasAgricolas(final List<PolizaBean> lstPolAgr, final Integer reintentos, int contAgr, Integer maxPolizas)	throws Exception {
		SeguimientoSCPoliza seguimientoSC = getObjetoAgrWs(); // cargamos el objeto de la llamada al Ws
		WSUtils.addSecurityHeader(seguimientoSC);
		es.agroseguro.seguroAgrario.recibos.Poliza polizaWS = null;
		Individual individualWS = null;
		ConsultarEstadoRequest parameters = new ConsultarEstadoRequest();
		ConsultarEstadoResponse response;
		
		int ban= 1;
		for (PolizaBean polAgr:lstPolAgr) {
			logger.debug("# ----------------------------- #");
			if (contAgr>=maxPolizas){
				break;
			}
			for (int ciclo=0;ciclo<reintentos;ciclo++){
				int vuelta = ciclo+1;
				try {
					parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(polAgr.getTipoRef()));
					parameters.setCodplan(Integer.parseInt(polAgr.getPlan()));
					parameters.setReferencia(polAgr.getReferencia());
					logger.debug(" #A#"+ban+"/"+lstPolAgr.size()+"# CALL WS: plan: "+polAgr.getPlan()+ " referencia: "+polAgr.getReferencia()+" tipoRef: "+polAgr.getTipoRef());
					response = seguimientoSC.consultarEstado(parameters); // Llamada al WS
					logger.debug("# ciclo "+vuelta+"/"+reintentos +" OK");
					String doc = new String (response.getDocumento().getValue(), "UTF-8");					
					FaseDocument situacionActualizadaPoliza = FaseDocument.Factory.parse(new StringReader(doc));
					Fase fase = situacionActualizadaPoliza.getFase();
					Colectivo colectivoWS = null;
										
					// DAA 24/01/2013 hay que tener en cuenta que no venga el colectivo en el XML
					if(fase.getColectivoArray().length != 0){
						colectivoWS = fase.getColectivoArray()[0];
						polizaWS = colectivoWS.getPolizaArray()[0];
						
					}else {
						individualWS = fase.getIndividualArray()[0];
						polizaWS = individualWS.getPoliza();
					}
					String date = "";
					SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
					if (polAgr.getTipoRef().equals("P")){ 
						date = sdf.format(polizaWS.getFechaEntradaVigorPrincipal().getTime());
					}else {
						date = sdf.format(polizaWS.getFechaEntradaVigorComplementario().getTime());
					}
					BBDDUpdateFechaEntradaVigor.actualizaFechaVigor(date,polAgr.getReferencia(), polAgr.getPlan(),polAgr.getLinea(),polAgr.getTipoRef());
					contAgr = contAgr+1;
					
					break;
			
				} catch (AgrException e) {
					String errores = debugAgrException(e);
					logger.debug(errores);
					break;
				} catch (Exception e) {
					if (e instanceof javax.xml.ws.WebServiceException) {
						Throwable cause = e; 
						if  ((cause = cause.getCause()) != null){
					        if(cause instanceof ConnectException){
								logger.debug("# ConnectException: ",e);
								logger.debug("# Reintentando conexión "+vuelta+"/"+reintentos +"...");
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
			ban = ban+1;
		}
		return contAgr;
	}	
	
	// LLamada al ws de Seguimiento para actualizar la fecha vigor de las pólizas ganado no renovables
	protected static int updatePolizasGanado(final List<PolizaBean> lstPolGan, final Integer reintentos, int contGan, Integer maxPolizas)	throws Exception {
		SeguimientoSCPoliza seguimientoSC = getObjetoAgrWs(); // cargamos el objeto de la llamada al Ws
		WSUtils.addSecurityHeader(seguimientoSC);
		ConsultarEstadoRequest parameters = new ConsultarEstadoRequest();
		ConsultarEstadoResponse response;
		int ban= 1;
		for (PolizaBean polGan:lstPolGan) {
			logger.debug("# ----------------------------- #");
			if (contGan>=maxPolizas){
				break;
			}
			for (int ciclo=0;ciclo<reintentos;ciclo++){
				int vuelta = ciclo+1;
				try {
					parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(polGan.getTipoRef()));
					parameters.setCodplan(Integer.parseInt(polGan.getPlan()));
					parameters.setReferencia(polGan.getReferencia());
						
					logger.debug(" #G#"+ban+"/"+lstPolGan.size()+"# CALL WS: plan: "+polGan.getPlan()+ " referencia: "+polGan.getReferencia()+" tipoRef: "+polGan.getTipoRef());
					response = seguimientoSC.consultarEstado(parameters); // Llamada al WS
					logger.debug("# ciclo "+vuelta+"/"+reintentos +" OK");
					String estadoPoliza = new String (response.getEstado().get(0).getValue(), "UTF-8");
					es.agroseguro.seguroAgrario.estadoContratacion.EstadoContratacionDocument estadoActual = es.agroseguro.seguroAgrario.estadoContratacion.EstadoContratacionDocument.Factory.parse(new StringReader(estadoPoliza));
					estadoActual.getEstadoContratacion().getSeguroPrincipal().getEstadoPoliza().getFechaEntradaVigor();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
					if (estadoActual.getEstadoContratacion() != null && estadoActual.getEstadoContratacion().getSeguroPrincipal() != null && 
						estadoActual.getEstadoContratacion().getSeguroPrincipal().getEstadoPoliza() != null) {
						String date = sdf.format(estadoActual.getEstadoContratacion().getSeguroPrincipal().getEstadoPoliza().getFechaEntradaVigor().getTime());
						BBDDUpdateFechaEntradaVigor.actualizaFechaVigor(date,polGan.getReferencia(), polGan.getPlan(),polGan.getLinea(),polGan.getTipoRef());
						contGan = contGan+1;
					}else {
						logger.debug(" sin FechaEntradaVigor ");
					}
					break;
					
				} catch (AgrException e) {
					String errores = debugAgrException(e);
					logger.debug(errores);
					break;
				} catch (Exception e) {
					if (e instanceof javax.xml.ws.WebServiceException) {
						Throwable cause = e; 
						if  ((cause = cause.getCause()) != null){
					        if(cause instanceof ConnectException){
								logger.debug("# ConnectException: ",e);
								logger.debug("# Reintentando conexión "+vuelta+"/"+reintentos +"...");
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
			ban = ban+1;
		}
		return contGan;
	}
	
	// LLamada al ws de ContrataciónRenovables para actualizar la fecha vigor de las pólizas renovables
		protected static int updatePolizasRenovables(final List<PolizaBean> lstPolRen, final Integer reintentos, int contRen, Integer maxPolizas)	throws Exception {
			ContratacionRenovaciones objWs = getObjetoRenWs(); // cargamos el objeto de la llamada al Ws
			WSUtils.addSecurityHeader(objWs);		
			Base64Binary res = null;
			int ban= 1;
			ListaPolizasRenovablesResponse wsResp = null;
			for (PolizaBean polRen:lstPolRen) {
				logger.debug("# ----------------------------- #");
				if (contRen>=maxPolizas){
					break;
				}
				int vuelta = 0;
				for (int ciclo=0;ciclo<reintentos;ciclo++){
					vuelta = ciclo+1;
					try {
						logger.debug("# ciclo "+vuelta+"/"+reintentos);
						ParametrosListaPolizasRenovablesAgroplus paramListPolReq = new ParametrosListaPolizasRenovablesAgroplus();

						paramListPolReq.setPlan(Integer.parseInt(polRen.getPlan()));
						paramListPolReq.setLinea(Integer.parseInt(polRen.getLinea()));
						ObjectFactory obj = new ObjectFactory();
						JAXBElement<String> ref = obj.createParametrosListaPolizasRenovablesReferencia(polRen.getReferencia());		
			  		    paramListPolReq.setReferencia(ref);
			  		    
			  		    wsResp = objWs.listaPolizasRenovables(paramListPolReq);
			  		    logger.debug(" #R#"+ban+"/"+lstPolRen.size()+"# CALL WS: plan: "+polRen.getPlan()+ " referencia: "+ref.getValue());
						res = wsResp.getPolizasRenovables(); // Llamada al WS
						logger.debug("# ciclo "+vuelta+"/"+reintentos +" OK");
						byte[]arrayAcuse = res.getValue();
						String acuse = new String (arrayAcuse, "UTF-8");
						ListaPolizasRenovablesDocument pp = ListaPolizasRenovablesDocument.Factory.parse(acuse);
					    ListaPolizasRenovables ppRen = pp.getListaPolizasRenovables();
						Renovacion[] renArr = ppRen.getRenovacionArray();
						for (Renovacion renov : renArr) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
							String date = sdf.format(renov.getFechaRenovacion().getTime());
							BBDDUpdateFechaEntradaVigor.actualizaFechaVigor(date.toString(),polRen.getReferencia(), polRen.getPlan(),polRen.getLinea(),polRen.getTipoRef());
						}
						contRen = contRen+1;
						break;
					}catch  (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException ex) {
						List<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> lstErrores = ex.getFaultInfo().getError();
						String errores ="";
						for (es.agroseguro.serviciosweb.contratacionrenovaciones.Error error: lstErrores){
							errores = errores + error.getMensaje().toString()+".";
							logger.debug("# AgrException: "+error.getMensaje().toString());
						}
						break;
					} catch (Exception e) {
						if (e instanceof javax.xml.ws.WebServiceException) {
							Throwable cause = e; 
							if  ((cause = cause.getCause()) != null){
						        if(cause instanceof ConnectException){
									logger.debug("# ConnectException: ",e);
									logger.debug("# Reintentando conexión "+vuelta+"/"+reintentos +"...");
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
				ban = ban+1;
			}
			return contRen;
		}
	
	/**
	 * Realiza un Debug de la exception devuelta por el servicio Web
	 * @param e
	 */
	public static String debugAgrException(Exception e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException segException = null;		
		if (e instanceof es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException) {
			segException = (es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException) e;
			mensaje += printException(segException);
		}			
		return mensaje;
	}
	
	/**
	 * Imprime los errores de una exception del servicio de seguimientos de poliza
	 * @param e
	 */
	private static String printException(
			es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.seguimientoscpoliza.AgrFallo fallo = ((es.agroseguro.serviciosweb.seguimientoscpoliza.AgrFallo) e
				.getFaultInfo());
		List<es.agroseguro.serviciosweb.seguimientoscpoliza.Error> errores = null;
		es.agroseguro.serviciosweb.seguimientoscpoliza.Error error = null;
		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			//logger.error("Errores devueltos por el Servicio Web: ");
			//logger.error("-------------------------------------- ");
			for (Iterator<es.agroseguro.serviciosweb.seguimientoscpoliza.Error> it = errores.iterator(); it.hasNext();) {
				error = (es.agroseguro.serviciosweb.seguimientoscpoliza.Error) it.next();
				//logger.error("Codigo: " + error.getCodigo() + " - Mensaje: " + error.getMensaje());
				mensaje += " Codigo: "  + error.getCodigo() + " - Mensaje: " + error.getMensaje();
			}
		}
		return mensaje;
	}
	
	// Objeto para la llamada al WS de Seguimiento (para las pólizas agrícolas y de ganado)
	protected static SeguimientoSCPoliza getObjetoAgrWs()	throws Exception {
		SeguimientoSCPoliza seguimientoSC = null;
		URL wsdlLocation = null;
		String url = "";
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();
		try {
			File ficherowsdl = new File(WSUtils.getBundleProp("seguimiento.wsdl"));
			url = ficherowsdl.getAbsolutePath();
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException ex) {
			throw new Exception("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: "+ url, ex);
		} catch (NullPointerException ex) {
			throw new Exception("Imposible obtener el WSDL de Seguimiento. Revise la Ruta: "+ wsdlLocation.toString(), ex);
		}	
		
		String wsLocation = WSUtils.getBundleProp("seguimiento.location");
		String wsPort 	  = WSUtils.getBundleProp("seguimiento.port");
		String wsService  = WSUtils.getBundleProp("seguimiento.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName    = new QName(wsLocation, wsPort);
		
		// LOGS PARAMETROS WS
		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsService: " + wsService.toString());
		
		try {
			SeguimientoSCPoliza_Service srv = new SeguimientoSCPoliza_Service(wsdlLocation, serviceName);
			seguimientoSC = (SeguimientoSCPoliza) srv.getPort(portName, SeguimientoSCPoliza.class);
		}catch  (Exception ex) {
			logger.debug("# Error en WSn Exception: "+ex.getMessage().toString());
			throw new Exception(ex.getMessage().toString(), ex);
		}
		return seguimientoSC;
	}
	
	// Objeto para la llamada al WS de Contratación (para las pólizas renovables)
	protected static ContratacionRenovaciones getObjetoRenWs()	throws Exception {
		ContratacionRenovaciones contratRen = null;	
		URL wsdlLocation = null;
		String url = "";
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();
		try {
			File ficherowsdl = new File(WSUtils.getBundleProp("contratacionRenovacionesWS.wsdl"));
			url = ficherowsdl.getAbsolutePath();
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException ex) {
			throw new Exception("Imposible recuperar el WSDL de una póliza Renovable. Revise la Ruta: "+ url, ex);
		} catch (NullPointerException ex) {
			throw new Exception("Imposible obtener el WSDL de una póliza Renovable. Revise la Ruta: "+ wsdlLocation.toString(), ex);
		}	
		
		String wsLocation = WSUtils.getBundleProp("contratacionRenovacionesWS.location");
		String wsPort 	  = WSUtils.getBundleProp("contratacionRenovacionesWS.port");
		String wsService  = WSUtils.getBundleProp("contratacionRenovacionesWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName    = new QName(wsLocation, wsPort);
		
		// LOGS PARAMETROS WS
		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsService: " + wsService.toString());
		
		try {
			ContratacionRenovaciones_Service srv = new ContratacionRenovaciones_Service(wsdlLocation, serviceName);
			contratRen = (ContratacionRenovaciones) srv.getPort(portName, ContratacionRenovaciones.class);
		}catch  (Exception ex) {
			logger.debug("# Error en WSn Exception: "+ex.getMessage().toString());
			throw new Exception(ex.getMessage().toString(), ex);
		}
		return contratRen;
	}
	
}