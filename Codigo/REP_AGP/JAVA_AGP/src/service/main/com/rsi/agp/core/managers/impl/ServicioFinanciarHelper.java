package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.CalculoServiceException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.contratacionscutilidades.AgrException;
import es.agroseguro.serviciosweb.contratacionscutilidades.ContratacionSCUtilidades;
import es.agroseguro.serviciosweb.contratacionscutilidades.ContratacionSCUtilidades_Service;
import es.agroseguro.serviciosweb.contratacionscutilidades.FinanciarRequest;
import es.agroseguro.serviciosweb.contratacionscutilidades.FinanciarResponse;

public class ServicioFinanciarHelper {
	private static final Log logger = LogFactory.getLog(ServicioFinanciarHelper.class);
	
	public Map<String, Object> doWork(String realPath, FinanciarRequest fr ) throws CalculoServiceException {
		Map<String, Object> retorno = null;		
		//Poliza poliza = (Poliza) polizaDao.getObject(Poliza.class, idPoliza);
		// Establece el Proxy
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
        String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("contratacion.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			logger.error("Imposible recuperar el WSDL de Calculo. Revise la Ruta: " + url, e1);
			throw new CalculoServiceException("Imposible recuperar el WSDL de Calculo. Revise la Ruta: " + url, e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("contratacion.location");
		String wsPort     = WSUtils.getBundleProp("contratacion.port");
		String wsService  = WSUtils.getBundleProp("contratacion.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de contratacion
		ContratacionSCUtilidades_Service srv = new ContratacionSCUtilidades_Service(wsdlLocation, serviceName);
		ContratacionSCUtilidades srvCalculo = srv.getPort(portName, ContratacionSCUtilidades.class);

		// AÃ±ade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvCalculo);
		
		FinanciarResponse respFinanciar = null;
		
		try {
		respFinanciar=srvCalculo.financiar(fr);
		
		
		if (respFinanciar != null)
		{
			retorno = new HashMap<String, Object>();	
			// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
			Base64Binary respuesta = respFinanciar.getFinanciacion(); 
			byte[] byteArray = respuesta.getValue();
			String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			
			//es.agroseguro.seguroAgrario.financiacion.Financiacion financiacion= es.agroseguro.seguroAgrario.financiacion.Financiacion.Factory.parse(new StringReader(xmlData));
			
			// Se comprueba si hay XML de calculo, y si lo hay , se crea el objeto y se devuelve
			if (respFinanciar.getFinanciacion() != null && 
				respFinanciar.getFinanciacion().getValue() != null && 
				respFinanciar.getFinanciacion().getValue().length > 0) {
					respuesta = respFinanciar.getFinanciacion();
					byteArray = respuesta.getValue();
					xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
					es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument financiacion= es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument.Factory.parse(new StringReader(xmlData));
					
					retorno.put("financiacion", financiacion);
					//retorno.put("financiacionPeriodo", financiacion.getPeriodoArray(0));
			}
			//retorno.put("acuse", acuseReciboDoc.getAcuseRecibo());
			return retorno;
		}
		
		
		} catch (AgrException e) {
			logger.error("Error inesperado devuelto por el servicio web de Calculo para la financiación " , e);
			retorno = new HashMap<String, Object>();	
			String mensaje = WSUtils.debugAgrException(e);
			retorno.put("alerta", mensaje);			
			//throw new CalculoServiceException("Error inesperado devuelto por el servicio web de Calculo para la financiación " , e);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Calculopara la financiación" , e);
			throw new CalculoServiceException("Error inesperado al llamar al servicio web de Calculo para la financiación" , e);
		}
		
		return retorno;
		
	}
	
	
}
