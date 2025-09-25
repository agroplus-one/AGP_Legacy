package com.rsi.agp.batch.incidenciasListAsuntos;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Session;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.listaAsuntos.ListaAsuntosDocument;
/* Pet. 57627 ** MODIF TAM (29.10.2019) */
import es.agroseguro.listaMotivosAnulacionRescision.ListaMotivosDocument;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ContratacionSCImpresionModificacion;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ContratacionSCImpresionModificacion_Service;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ListaAsuntosResponse;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ListaMotivosResponse;;


public final class IncidenciasListAsuntosWS {
	private static final Logger logger = Logger.getLogger(IncidenciasListAsuntosWS.class);
	
	protected static ContratacionSCImpresionModificacion getObjWSList()	throws Exception {
		
		URL wsdlLocation = null;
		String url = "";
		
		logger.debug("**@@** Dentro de obtener objeto del WS**@@**");
		
		ContratacionSCImpresionModificacion contratImp = null;
		
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();

		try {
			File ficherowsdl = new File(WSUtils.getBundleProp("impresionModificacionWS.wsdl"));
			url = ficherowsdl.getAbsolutePath();
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException ex) {
			throw new Exception("Imposible recuperar el WSDL de Lista de Incidencias Asuntos/Motivos. Revise la Ruta: "+ url, ex);
		} catch (NullPointerException ex) {
			throw new Exception("Imposible obtener el WSDL de Lista de Incidencias Asuntos/Motivos. Revise la Ruta: "+ wsdlLocation.toString(), ex);
		}	
		
		// PARAMETROS WS
		String wsLocation = WSUtils.getBundleProp("impresionModificacionWS.location");
		
		String wsPort 	  = WSUtils.getBundleProp("impresionModificacionWS.port");
		String wsService  = WSUtils.getBundleProp("impresionModificacionWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName    = new QName(wsLocation, wsPort);
		
		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsService: " + wsService.toString());
		
		try {
			
			ContratacionSCImpresionModificacion_Service srv = new ContratacionSCImpresionModificacion_Service(wsdlLocation, serviceName);;
			
			contratImp = (ContratacionSCImpresionModificacion) srv.getPort(portName, ContratacionSCImpresionModificacion.class);
			
		} catch  (Exception ex) {		
			
			logger.error("# Error global en WS Exception: "+ex.getMessage().toString(),ex);
			return contratImp;
		}
		
		return contratImp;
	}
	
	
	protected static ListaAsuntosDocument getListaAsuntos(final Session session, final ContratacionSCImpresionModificacion objWs) throws Exception {
		
		ListaAsuntosDocument wsListInc = null;
		ListaAsuntosResponse wsListResponse = null;
		
		try {
			WSUtils.addSecurityHeader(objWs);
			
			//for (java.lang.reflect.Method m : ContratacionSCImpresionModificacion.class.getMethods()){
			//	StringBuffer sb = new StringBuffer();
			//	for (Class<?> c : m.getParameterTypes()) {
			//		sb.append(c.getName());
			//		sb.append(", ");
			//	}
			//	logger.debug(m.getName() + " --> " + sb.toString());
			//}
			
			wsListResponse = objWs.listaAsuntos(null);
			wsListResponse.getListaAsuntos();
			wsListInc = getListaAsuntosFromResponse(wsListResponse);
			
			
		} catch  (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException ex) {
			logger.error ("Error en WS en agrException: ", ex);
			List<es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error> lstErrores = ex.getFaultInfo().getError();
			String errores ="";
			for (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error error: lstErrores){
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
						String errores = e.getMessage().toString();
						throw new Exception(errores, e);
						
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
		
		return wsListInc;
	}
	
	/**
	 * Método que rellena el objeto PolizaActualizadaResponse
	 * 
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws XmlException
	 * @throws IOException
	 */
	private static ListaAsuntosDocument getListaAsuntosFromResponse(	final ListaAsuntosResponse response) throws UnsupportedEncodingException,
			XmlException {
	
		org.w3._2005._05.xmlmime.Base64Binary listaIncAsuntos = response.getListaAsuntos();
		
		ListaAsuntosDocument lAsuntos = null;
		
		byte[] byteArrayLista = listaIncAsuntos.getValue();
		
		if (byteArrayLista != null && byteArrayLista.length > 0){
			String xmlData = new String (byteArrayLista, Constants.DEFAULT_ENCODING);
			
			lAsuntos = es.agroseguro.listaAsuntos.ListaAsuntosDocument.Factory.parse(xmlData);
		}
	
		return lAsuntos;
	}
	
	/*** Pet. 57627 ** MODIF TAM (29.10.2019) *** Inicio ***/
	/*** Incluimos las funciones para recuperar la lista de Motivos de las Anulaciones y Rescisiones de Pólizas */
	protected static ListaMotivosDocument getListaMotivos(final Session session, final ContratacionSCImpresionModificacion objWs) throws Exception {
		
		ListaMotivosDocument wsListMotivos = null;
		ListaMotivosResponse wsListResponse = null;
		
		try {
			WSUtils.addSecurityHeader(objWs);
			
			wsListResponse = objWs.listaMotivos(null);
			wsListResponse.getListaMotivos();
			wsListMotivos = getListaMotivosFromResponse(wsListResponse);
			
			
		} catch  (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException ex) {
			logger.error ("Error en WS en agrException: ", ex);
			List<es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error> lstErrores = ex.getFaultInfo().getError();
			String errores ="";
			for (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error error: lstErrores){
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
						String errores = e.getMessage().toString();
						throw new Exception(errores, e);
						
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
		
		return wsListMotivos;
	}
	
	/**
	 * Método que rellena el objeto ListaMotivosResponse
	 * 
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws XmlException
	 * @throws IOException
	 */
	private static ListaMotivosDocument getListaMotivosFromResponse(final ListaMotivosResponse response) throws UnsupportedEncodingException,
			XmlException {
	
		org.w3._2005._05.xmlmime.Base64Binary listaAsMotivos = response.getListaMotivos();
		
		ListaMotivosDocument lMotivos = null;
		
		byte[] byteArrayLista = listaAsMotivos.getValue();
		
		if (byteArrayLista != null && byteArrayLista.length > 0){
			String xmlData = new String (byteArrayLista, Constants.DEFAULT_ENCODING);
			
			lMotivos = es.agroseguro.listaMotivosAnulacionRescision.ListaMotivosDocument.Factory.parse(xmlData);
		}
			
		return lMotivos;

	}

	
	
}





