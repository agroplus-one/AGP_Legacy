package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.seguroAgrario.listaRecibos.ListaRecibosDocument;
import es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ListarRecibosRequest;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ListarRecibosResponse;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza_Service;
import es.agroseguro.tipos.PolizaReferenciaTipo;

public class ServicioListarRecibosPoliza {

	private static final Log logger = LogFactory.getLog(ServicioListarRecibosPoliza.class);

	public ListaRecibosDocument doWork(String tipoReferencia, BigDecimal codPlan, String refPoliza, String realPath) throws SeguimientoServiceException {
		
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
			
			// Añade la cabecera de seguridad
			WSUtils.addSecurityHeader(srvSeguimiento);
			
			ListarRecibosRequest parameters = new ListarRecibosRequest();
			
			parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(tipoReferencia));
			parameters.setCodplan(codPlan.intValue());
			parameters.setReferencia(refPoliza);
			
			logger.debug("Parametros para listar recibos:");
			logger.debug("Plan: " + parameters.getCodplan());
			logger.debug("Referencia: " + parameters.getReferencia());
			logger.debug("Tipo referencia: " + parameters.getTiporeferencia());
			
			ListarRecibosResponse response;
			
			response = srvSeguimiento.listarRecibos(parameters);
			String listaRecibosPoliza = new String (response.getListaRecibos().getValue(), "UTF-8");
			
			ListaRecibosDocument listaRecibos = ListaRecibosDocument.Factory.parse(new StringReader(listaRecibosPoliza));
			
			return listaRecibos;

		} catch (MalformedURLException e1) {
			logger.error("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: " + url, e1);
			throw new SeguimientoServiceException("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: " + url, e1);		
		} catch (AgrException e) {
			logger.error("Error inesperado devuelto por el servicio web de Seguimiento" , e);
			WSUtils.debugAgrException(e);
			throw new SeguimientoServiceException("Error inesperado devuelto por el servicio web de Seguimiento" , e);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Seguimiento" , e);
			throw new SeguimientoServiceException("Error inesperado al llamar al servicio web de Seguimiento" , e);
		}
		
	}
}
