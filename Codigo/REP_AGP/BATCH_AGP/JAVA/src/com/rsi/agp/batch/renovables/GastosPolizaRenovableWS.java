package com.rsi.agp.batch.renovables;

import java.io.File;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service;
import es.agroseguro.serviciosweb.contratacionrenovaciones.GastosPolizaRenovableResponse;
import es.agroseguro.tipos.Gastos;

public final class GastosPolizaRenovableWS {
	private static final Logger logger = Logger.getLogger(GastosPolizaRenovableWS.class);
	
	protected static ContratacionRenovaciones getObjetoWs()	throws Exception {
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
			throw new Exception("Imposible recuperar el WSDL de Gastos de una p�liza Renovable. Revise la Ruta: "+ url, ex);
		} catch (NullPointerException ex) {
			throw new Exception("Imposible obtener el WSDL de Gastos de una p�liza Renovable. Revise la Ruta: "+ wsdlLocation.toString(), ex);
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
		logger.debug("wsServicee: " + wsService.toString());
		
		try {
			ContratacionRenovaciones_Service srv = new ContratacionRenovaciones_Service(wsdlLocation, serviceName);
			contratRen = (ContratacionRenovaciones) srv.getPort(portName, ContratacionRenovaciones.class);
		}catch  (Exception ex) {
			logger.debug("# Error en WSn Exception: "+ex.getMessage().toString());
			throw new Exception(ex.getMessage().toString(), ex);
		}
		return contratRen;
	}
	
	/**
	 * 
	 * @param referencia
	 * @param plan
	 * @param gastoRenovacion
	 * @param session
	 * @param objWs
	 * @param reintentos
	 * @return
	 * @throws Exception
	 */
	protected static Boolean getGastosPolizaRenovable(final String referencia,final Long plan, final com.rsi.agp.dao.tables.renovables.GastosRenovacion gastoRenovacion, 
													  final Session session, final ContratacionRenovaciones objWs, Integer reintentos) throws Exception {
		Boolean res = false;		
		BigDecimal zero = new BigDecimal(0);
		int vuelta = 1;
		
		for (int ciclo=0;ciclo<reintentos;ciclo++) {			
			try {
				WSUtils.addSecurityHeader(objWs);
				ParametrosGastos wsReq = new ParametrosGastos();
				wsReq.setPlan(plan.intValue());
				wsReq.setReferencia(referencia);
				List<Gastos> lstGastos = new ArrayList<Gastos>();
				logger.debug("LLAMANDO AL WS CON LOS DATOS: PLAN: "+plan.toString() +" REF:"+referencia);
				
				if (gastoRenovacion != null) {
					Gastos gas = new Gastos();
					gas.setAdministracion ((null != gastoRenovacion.getAdministracion()) ? gastoRenovacion.getAdministracion() : zero);
					gas.setAdquisicion ((null != gastoRenovacion.getAdquisicion()) ? gastoRenovacion.getAdquisicion() : zero);
					gas.setComisionMediador ((null != gastoRenovacion.getComisionMediador()) ? gastoRenovacion.getComisionMediador() : zero);
					if (null != gastoRenovacion.getGrupoNegocio()) gas.setGrupoNegocio(gastoRenovacion.getGrupoNegocio().toString());
					lstGastos.add(gas);		
					logger.debug(GastosRenovables.getGastosWSLogs(gas));
					wsReq.setListaGastos(lstGastos);
				}
				logger.debug("# ciclo "+vuelta+"/"+reintentos);
				GastosPolizaRenovableResponse wsResp = objWs.gastosPolizaRenovable(wsReq); // Llamada al WS
				logger.debug("# ciclo "+vuelta+"/"+reintentos +" OK");
				res = wsResp.isResultado();
				break;
			} catch  (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException ex) {
				logger.error ("Error en WS en agrException: ", ex);
				List<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> lstErrores = ex.getFaultInfo().getError();
				String errores ="";
				for (es.agroseguro.serviciosweb.contratacionrenovaciones.Error error: lstErrores){
					errores = errores + error.getMensaje().toString()+".";
					logger.debug("Error en agrException: "+error.getMensaje().toString());
				}
				throw new Exception(errores, ex);	
			}catch  (Exception e) {
				if (e instanceof javax.xml.ws.WebServiceException) {
					Throwable cause = e; 
					if ((cause = cause.getCause()) != null){
				        if(cause instanceof ConnectException){
							logger.debug("# ConnectException: "+e.getMessage().toString());					
							if (vuelta != reintentos.intValue()) {
								vuelta++;
								logger.debug("# Reintentando conexion "+vuelta+"/"+reintentos+"...");
							}else {
								String errores = e.getMessage().toString();
								logger.debug("Guardamos error Timeout para la referencia: "+referencia);
								throw new Exception(errores, e);
							}
				        }else { 
				        	logger.debug("# otro tipo de Exception: "+e.getMessage().toString());
				        	throw new Exception(e.getMessage().toString(), e);
				        }
				    } else {
				    	logger.debug("# Error exc. tipo: "+e.getMessage().toString());
			        	throw new Exception(e.getMessage().toString(), e);
				    }
				}else {
					logger.debug("# Error exception tipo: "+e.getClass().toString());
					throw new Exception(e.getMessage().toString(), e);
				}
			}
		}

		return res;
	}
}
