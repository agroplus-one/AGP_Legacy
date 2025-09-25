package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.util.WSUtils;


import es.agroseguro.seguroAgrario.recibos.FaseDocument;
import es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ConsultarEstadoRequest;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ConsultarEstadoResponse;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ObjectFactory;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza_Service;
import es.agroseguro.tipos.PolizaReferenciaTipo;

public class ServicioConsultaEstado {

	private static final Log logger = LogFactory.getLog(ServicioConsultaEstado.class);

	public FaseDocument doWork(String tipoReferencia, BigDecimal codPlan, String refPoliza, BigDecimal codRecibo, Date fechaEmisionRecibo, String realPath) throws SeguimientoServiceException {
		
		// Establece el Proxy
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("seguimiento.wsdl");
        try {
		
			wsdlLocation = new URL("file:" + url);
		
	//		Recogemos de webservice.properties los valores para el ServiceName y Port
			String wsLocation = WSUtils.getBundleProp("seguimiento.location");
			String wsPort = WSUtils.getBundleProp("seguimiento.port");
			String wsService = WSUtils.getBundleProp("seguimiento.service");
			
			QName serviceName = new QName(wsLocation,wsService);
			QName portName = new QName(wsLocation,wsPort);
			
	//		Envoltorio para la llamda al servicio web de seguimiento
			SeguimientoSCPoliza_Service srv = new SeguimientoSCPoliza_Service(wsdlLocation, serviceName);
			SeguimientoSCPoliza srvSeguimiento = srv.getPort(portName, SeguimientoSCPoliza.class);
			
			// AÃ±ade la cabecera de seguridad
			WSUtils.addSecurityHeader(srvSeguimiento);
			
			ConsultarEstadoRequest parameters = new ConsultarEstadoRequest();
			
			parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(tipoReferencia));
			parameters.setCodplan(codPlan.intValue());
			parameters.setReferencia(refPoliza);
			
			if(codRecibo != null) {
				// Cambios en el set de parámetros por modificaciones en los descriptores del servicio de seguimiento
				parameters.setRecibo(new ObjectFactory().createConsultarEstadoRequestRecibo((codRecibo.intValue())));
			}
			
			if(fechaEmisionRecibo != null){
			
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.setTime(fechaEmisionRecibo);
				XMLGregorianCalendar fecha = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
				parameters.setFecha(new ObjectFactory().createConsultarEstadoRequestFecha(fecha));
	
			}
	
			ConsultarEstadoResponse response;
			
			response = srvSeguimiento.consultarEstado(parameters);
			
			
			Base64Binary documento = response.getDocumento();
			FaseDocument estadoActual = null;
			if (documento != null ) {
				String estadoPoliza = new String (documento.getValue(), "UTF-8");
				estadoActual = FaseDocument.Factory.parse(new StringReader(estadoPoliza));
			}
			
			return estadoActual;

		} catch (MalformedURLException e1) {
			logger.error("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: " + url, e1);
			throw new SeguimientoServiceException("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: " + url, e1);		
		} catch (AgrException e) {
			//logger.error("Error inesperado devuelto por el servicio web de Seguimiento" , e);
			WSUtils.debugAgrException(e);
			throw new SeguimientoServiceException("Error inesperado devuelto por el servicio web de Seguimiento" , e);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Seguimiento" , e);
			throw new SeguimientoServiceException("Error inesperado al llamar al servicio web de Seguimiento" , e);
		}
		
	}
}
