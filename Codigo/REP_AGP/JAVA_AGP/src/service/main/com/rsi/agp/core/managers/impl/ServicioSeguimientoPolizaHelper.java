package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.seguimientoContratacion.SeguimientoContratacionDocument;
import es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ObjectFactory;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoEstadoContratacionRequest;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoEstadoContratacionResponse;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza_Service;


public class ServicioSeguimientoPolizaHelper {

	private static final Log logger = LogFactory.getLog(ServicioSeguimientoPolizaHelper.class);

	public Map<String, Object> doWork(String refPoliza, BigDecimal codPlan,  Date fechaCambioEstDesde, Date fechaCambioEstHasta, String realPath, String metodo) throws SeguimientoServiceException {
		
		
		logger.debug ("SeguimientoPolizaHelper - doWork [INIT]");
		// Establece el Proxy
		if (!WSUtils.isProxyFixed()) {
			WSUtils.setProxy();
		}
		
        URL wsdlLocation = null;
        Map<String, Object> retorno = new HashMap<String, Object>();
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("seguimiento.wsdl");
        try {
		
			wsdlLocation = new URL("file:" + url);
		
			// Recogemos de webservice.properties los valores para el ServiceName y Port
			String wsLocation = WSUtils.getBundleProp("seguimiento.location");
			String wsPort = WSUtils.getBundleProp("seguimiento.port");
			String wsService = WSUtils.getBundleProp("seguimiento.service");
			
			QName serviceName = new QName(wsLocation,wsService);
			QName portName = new QName(wsLocation,wsPort);
			
			// Envoltorio para la llamda al servicio web de seguimiento
			SeguimientoSCPoliza_Service srv = new SeguimientoSCPoliza_Service(wsdlLocation, serviceName);
			SeguimientoSCPoliza srvSeguimiento = srv.getPort(portName, SeguimientoSCPoliza.class);
			
			// Añade la cabecera de seguridad
			WSUtils.addSecurityHeader(srvSeguimiento);
			
			SeguimientoEstadoContratacionRequest parameters = new SeguimientoEstadoContratacionRequest();
			
			ObjectFactory objectFactory = new ObjectFactory();
			
			//Si le llamo desde el metodo getInfoPoliza de SeguimientoPolizaManager
			if(metodo.equals("getInfoPoliza")){
				JAXBElement<Integer> plan = objectFactory.createSeguimientoEstadoContratacionRequestPlan(codPlan.intValue());
				JAXBElement<String> referencia = objectFactory.createSeguimientoEstadoContratacionRequestReferencia(refPoliza);
				parameters.setPlan(plan);
				parameters.setReferencia(referencia);
			}
			//Si le llamo desde el metodo getPolizasSeguimiento de SeguimientoPolizaManager
			else if (metodo.equals("getPolizasSeguimiento")){
				XMLGregorianCalendar xmlGcDesde = generarFechXmlCalendar(fechaCambioEstDesde);
				XMLGregorianCalendar xmlGcHasta = generarFechXmlCalendar(fechaCambioEstHasta);
				
				JAXBElement<XMLGregorianCalendar> fCambioEstDesde = objectFactory.createSeguimientoEstadoContratacionRequestCambioEstadoDesde(xmlGcDesde);
				JAXBElement<XMLGregorianCalendar> fCambioEstHasta = objectFactory.createSeguimientoEstadoContratacionRequestCambioEstadoHasta(xmlGcHasta);
				parameters.setCambioEstadoDesde(fCambioEstDesde);
				parameters.setCambioEstadoHasta(fCambioEstHasta);
			}
			
			parameters.setSoloPrincipales(objectFactory.createSeguimientoEstadoContratacionRequestSoloPrincipales(Boolean.FALSE));
			parameters.setIncluirRenovaciones(objectFactory.createSeguimientoEstadoContratacionRequestIncluirRenovaciones(Boolean.TRUE));
			parameters.setIncluirRecibos(objectFactory.createSeguimientoEstadoContratacionRequestIncluirRecibos(Boolean.TRUE));
			parameters.setIncluirIncidencias(objectFactory.createSeguimientoEstadoContratacionRequestIncluirIncidencias(Boolean.TRUE));
			parameters.setIncluirInfoAdicional(objectFactory.createSeguimientoEstadoContratacionRequestIncluirInfoAdicional(Boolean.TRUE));
			parameters.setHistoriaPoliza(objectFactory.createSeguimientoEstadoContratacionRequestHistoriaPoliza(Boolean.TRUE));
			
			
			SeguimientoEstadoContratacionResponse response = srvSeguimiento.seguimientoEstadoContratacion(parameters);
			
			
			//Map<String, Object> retorno = new HashMap<String, Object>();
			// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
			Base64Binary respuesta = response.getEstadoContratacion();
			byte[] byteArray = respuesta.getValue();
			String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			//AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(xmlData));
			SeguimientoContratacionDocument seguiContDoc = SeguimientoContratacionDocument.Factory.parse(new StringReader(xmlData));
			//retorno.put("acuse", acuseReciboDoc.getAcuseRecibo());
			retorno.put("acuse", seguiContDoc.getSeguimientoContratacion());
			
			
			

		} catch (MalformedURLException e1) {
			logger.error("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: " + url, e1);
			throw new SeguimientoServiceException("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: " + url, e1);		
		} catch (AgrException e) {
			retorno.put("errorAgro", WSUtils.debugAgrException(e));
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Seguimiento" , e);
			throw new SeguimientoServiceException("Error inesperado al llamar al servicio web de Seguimiento" , e);
		}
        logger.debug ("SeguimientoPolizaHelper - doWork [END]");
        return retorno;
		
	}
	private XMLGregorianCalendar generarFechXmlCalendar(Date fecha)
			throws ParseException, DatatypeConfigurationException {
		/*
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		GregorianCalendar gc = new GregorianCalendar();
		Date parsedDate = sdf.parse(fecha);
		gc.setTime(parsedDate);
		XMLGregorianCalendar xmlGc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		return xmlGc;
		*/
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(fecha);
		XMLGregorianCalendar xmlGc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		return xmlGc;
		
	}
}




