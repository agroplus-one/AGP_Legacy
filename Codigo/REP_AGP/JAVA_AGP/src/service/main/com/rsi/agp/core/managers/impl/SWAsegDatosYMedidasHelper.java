package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoDatosYMedidasDocument;
import es.agroseguro.seguroAgrario.aseguradoInfoSaldoENESA.AseguradoInfoSaldoENESADocument;
import es.agroseguro.serviciosweb.contratacionscasegurado.AgrException;
import es.agroseguro.serviciosweb.contratacionscasegurado.AseguradoDatosYMedidasRequest;
import es.agroseguro.serviciosweb.contratacionscasegurado.AseguradoDatosYMedidasResponse;
import es.agroseguro.serviciosweb.contratacionscasegurado.AseguradoInfoSaldoENESARequest;
import es.agroseguro.serviciosweb.contratacionscasegurado.AseguradoInfoSaldoENESAResponse;
import es.agroseguro.serviciosweb.contratacionscasegurado.ContratacionSCAsegurado;
import es.agroseguro.serviciosweb.contratacionscasegurado.ContratacionSCAsegurado_Service;
import es.agroseguro.serviciosweb.contratacionscasegurado.ObjectFactory;

public class SWAsegDatosYMedidasHelper {

	private static final Log LOGGER = LogFactory.getLog(SWAsegDatosYMedidasHelper.class);
	
	
	/**
	 * Configura y realiza la llamada para obtener los datos actualizados del asegurado
	 * @param nifCif nif del asegurado
	 * @param realPath
	 * @throws AgrException 
	 * @throws Exception 
	 */
	public WSResponse<AseguradoDatosYMedidasDocument> getSolicitudAseguradoActualizado(
			String nifCif, String codPlan, String codLinea, String realPath) throws AgrException { 
				
		WSResponse<AseguradoDatosYMedidasDocument> respuestaMetodo = null;
		ContratacionSCAsegurado srvContratacionAseg = getSrvContratacionAseg (realPath);
		// Parametros a enviar al SW
		ObjectFactory objectFactory = new ObjectFactory();

		try{
			JAXBElement<Integer> linea = objectFactory.createAseguradoDatosYMedidasRequestLinea(Integer.valueOf(codLinea));
			JAXBElement<Integer> plan = objectFactory.createAseguradoDatosYMedidasRequestPlan(Integer.valueOf(codPlan));
			AseguradoDatosYMedidasRequest params = objectFactory.createAseguradoDatosYMedidasRequest();
			params.setNif(nifCif);
			params.setLinea(linea);
			params.setPlan(plan);
			// Llamada al SW
			AseguradoDatosYMedidasResponse respuestaSW = srvContratacionAseg.aseguradoDatosYMedidas(params); 
		
			if (respuestaSW != null) {
				LOGGER.debug("respuesta: " + params.toString());
				respuestaMetodo = getSolicitudDatosAsegResponse(respuestaSW);
			}
		}  catch (XmlException e) {
			LOGGER.debug(e);
		} catch (IOException e) {
			LOGGER.debug(e);
		}
		return respuestaMetodo;
		
	} 
	
	/**
	 * Configura y realiza la llamada para obtener los datos actualizados de las proximas subvenciones asegurado
	 * @param nifCif
	 * @param fechaEstudio
	 * @param realPath
	 * @return
	 * @throws AgrException
	 */
	public WSResponse<AseguradoDatosYMedidasDocument> mostrarProximasSubvenciones(String nifCif, String fechaEstudio, String realPath) throws AgrException{
		WSResponse<AseguradoDatosYMedidasDocument> respuestaMetodo = null;
		ContratacionSCAsegurado srvContratacionAseg = getSrvContratacionAseg (realPath);
		ObjectFactory objectFactory = new ObjectFactory();
		try {
			AseguradoDatosYMedidasRequest parametros = objectFactory.createAseguradoDatosYMedidasRequest();
			if(!fechaEstudio.equals("")){
				XMLGregorianCalendar xmlGc = generarFechXmlCalendar(fechaEstudio);
				JAXBElement<XMLGregorianCalendar> fecha = objectFactory.createAseguradoDatosYMedidasRequestFechaEstudio(xmlGc);
				parametros.setFechaEstudio(fecha);
			}
			parametros.setNif(nifCif);
			parametros.setMostrarProximasSubvenciones(objectFactory.createAseguradoDatosYMedidasRequestMostrarProximasSubvenciones(true));
			
			AseguradoDatosYMedidasResponse respuestaSW = srvContratacionAseg.aseguradoDatosYMedidas(parametros);
			if(respuestaSW != null){
				LOGGER.debug("respuesta: " + parametros.toString());
				respuestaMetodo = getSolicitudDatosAsegResponse(respuestaSW);
			}
		} catch (XmlException e) {
			LOGGER.debug(e);
		} catch (IOException e) {
			LOGGER.debug(e);
		} catch (ParseException e) {
			LOGGER.debug(e);;
		} catch (DatatypeConfigurationException e) {
			LOGGER.debug(e);
		}
		return respuestaMetodo;
	}

	private XMLGregorianCalendar generarFechXmlCalendar(String fechaEstudio)
			throws ParseException, DatatypeConfigurationException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		GregorianCalendar gc = new GregorianCalendar();
		Date parsedDate = sdf.parse(fechaEstudio);
		gc.setTime(parsedDate);
		XMLGregorianCalendar xmlGc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		return xmlGc;
	}
	
	/**
	 * Configura y realiza la llamada para obtener los datos actualizados de las proximas subvenciones asegurado
	 * @param nifCif
	 * @param codPlan
	 * @param codLinea
	 * @param realPath
	 * @return
	 * @throws AgrException
	 */
	public WSResponse<AseguradoDatosYMedidasDocument> mostrarProximasSubvenciones(
			String nifCif, String codPlan, String codLinea, String realPath) throws AgrException {
		WSResponse<AseguradoDatosYMedidasDocument> respuestaMetodo = null;
		ContratacionSCAsegurado srvContratacionAseg = getSrvContratacionAseg (realPath);
		ObjectFactory objectFactory = new ObjectFactory();
		try {
			JAXBElement<Integer> linea = objectFactory.createAseguradoDatosYMedidasRequestLinea(Integer.valueOf(codLinea));
			JAXBElement<Integer> plan = objectFactory.createAseguradoDatosYMedidasRequestPlan(Integer.valueOf(codPlan));
			JAXBElement<Boolean> mostrarProximasSubvenciones = objectFactory.createAseguradoDatosYMedidasRequestMostrarProximasSubvenciones(true);
			AseguradoDatosYMedidasRequest params = objectFactory.createAseguradoDatosYMedidasRequest();
			params.setNif(nifCif);
			params.setLinea(linea);
			params.setPlan(plan);
			params.setMostrarProximasSubvenciones(mostrarProximasSubvenciones);
			AseguradoDatosYMedidasResponse respuestaSW = srvContratacionAseg.aseguradoDatosYMedidas(params);
			
			byte[]resp = respuestaSW.getAseguradoDatosYMedidas().getValue();
			String strRespuestaSW = new String (resp, "UTF-8");
			LOGGER.debug("RESPUESTA SW: " + strRespuestaSW);
			
			if(respuestaSW != null){
				LOGGER.debug("Respuesta ");
				respuestaMetodo = this.getSolicitudDatosAsegResponse(respuestaSW);
			}
		} catch (XmlException e) {
			LOGGER.debug(e);
		} catch (IOException e) {
			LOGGER.debug(e);
		}
		LOGGER.debug("RESPUESTA METODO: " + respuestaMetodo.getData().toString());
		return respuestaMetodo;
	}
	
	/**
	 * Configura y realiza la llamada para obtener los datos actualizados del saldo ENESA del asegurado
	 * @param nifCif
	 * @param codPlan
	 * @param realPath
	 * @return
	 * @throws AgrException
	 */
	public WSResponse<AseguradoInfoSaldoENESADocument> mostrarSaldoENESA(
			String nifCif, String codPlan, String realPath) throws AgrException {
		WSResponse<AseguradoInfoSaldoENESADocument> respuestaMetodo = null;
		ContratacionSCAsegurado srvContratacionAseg = getSrvContratacionAseg (realPath);
		ObjectFactory factoria = new ObjectFactory();
		try {
			AseguradoInfoSaldoENESARequest params = factoria.createAseguradoInfoSaldoENESARequest();
			params.setNif(nifCif);
			params.setPlan(Integer.valueOf(codPlan));
			AseguradoInfoSaldoENESAResponse respuestaSW = srvContratacionAseg.aseguradoInfoSaldoENESA(params);
			if(respuestaSW != null){
				LOGGER.debug("Respuesta ");
				respuestaMetodo = this.getSolicitudDatosAsegResponse(respuestaSW);
			}
		} catch (XmlException e) {
			LOGGER.debug(e);
		} catch (IOException e) {
			LOGGER.debug(e);
		}
		
		
		return respuestaMetodo;
	}
	
	/**
	 * Rellena el objeto que encapsula la respuesta del SW de aseguradoDatosYMedidas
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws XmlException 
	 */
	private WSResponse<AseguradoInfoSaldoENESADocument>  getSolicitudDatosAsegResponse(
				AseguradoInfoSaldoENESAResponse response) throws XmlException, IOException {
		WSResponse<AseguradoInfoSaldoENESADocument> respuesta = new WSResponse<AseguradoInfoSaldoENESADocument>();
		Base64Binary datosAct = response.getAseguradoInfoSaldoENESA();
		byte[] byteArray = datosAct.getValue();		
		if (!ArrayUtils.isEmpty(byteArray)){
			String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			AseguradoInfoSaldoENESADocument documento = AseguradoInfoSaldoENESADocument.Factory.parse(new StringReader(xmlData));
			respuesta.setData(documento);
		}
		if (respuesta.getData() != null) 
			LOGGER.debug(respuesta.getData().toString());
		return respuesta;
	}
	
	/**
	 * Rellena el objeto que encapsula la respuesta del SW de aseguradoDatosYMedidas
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws XmlException 
	 */
	private WSResponse<AseguradoDatosYMedidasDocument>  getSolicitudDatosAsegResponse(
				AseguradoDatosYMedidasResponse response) throws XmlException, IOException {
		WSResponse<AseguradoDatosYMedidasDocument> respuesta = new WSResponse<AseguradoDatosYMedidasDocument>();
		Base64Binary datosAct = response.getAseguradoDatosYMedidas();
		byte[] byteArray = datosAct.getValue();
		if (!ArrayUtils.isEmpty(byteArray)){
			String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			AseguradoDatosYMedidasDocument adPol = AseguradoDatosYMedidasDocument.Factory.parse(new StringReader(xmlData));
			respuesta.setData(adPol);
		}
		if (respuesta.getData() != null) 
			LOGGER.debug(respuesta.getData().toString());
		return respuesta;
	}
	/**
	 * Metodo para crear los objetos necesarios para las llamadas al servicio web.
	 * @param realPath Ruta real de los ficheros "wsdl"
	 * @return Manejador para las llamadas al servicio de aseuradoDatosYMedidas.
	 */
	private ContratacionSCAsegurado getSrvContratacionAseg(String realPath) {
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
        String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("aseguradosDatosYMedidasWS.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new SWConsultaContratacionException("Imposible recuperar el WSDL de impresion de aseguradosDatosYMedidas. Revise la Ruta: " + url, e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("aseguradosDatosYMedidasWS.location");
		String wsPort     = WSUtils.getBundleProp("aseguradosDatosYMedidasWS.port");
		String wsService  = WSUtils.getBundleProp("aseguradosDatosYMedidasWS.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de aseuradoDatosYMedidas
		ContratacionSCAsegurado_Service srv = new ContratacionSCAsegurado_Service(wsdlLocation, serviceName);
		
		
		ContratacionSCAsegurado srvAseguradoDatos = srv.getPort(portName, ContratacionSCAsegurado.class);
		
		LOGGER.debug(srvAseguradoDatos.toString());
		
		// Anade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvAseguradoDatos);
				
		return srvAseguradoDatos;
	}
}
