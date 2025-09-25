package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.vo.SigpacVO;

import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.ContratacionSCSIGPACZonificacion;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.ContratacionSCSIGPACZonificacion_Service;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACFaunaRequest;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACFaunaResponse;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACRequest;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACResponse;

public class SWZonificacionSIGPACHelper {

	private static final Log logger = LogFactory.getLog(SWZonificacionSIGPACHelper.class);

	/**
	 * Configura y realiza la llamada para obtener los datos de ubicación dado un
	 * SIGPAC
	 * 
	 * @param sigpacVO
	 * @param claseId
	 * @param realPath
	 *            Ruta del Web-Inf
	 * @param codUsuario
	 *            Código de usuario
	 * @return
	 * @throws AgrException
	 * @throws Exception
	 *             Error en la llamada al SW
	 */
	public com.rsi.agp.core.managers.impl.SIGPACResponse getDatosUbicacionSIGPAC(SIGPACRequest params, String realPath,
			String codUsuario) throws AgrException, Exception {

		SIGPACResponse respuesta = null;
		ContratacionSCSIGPACZonificacion srvSIGPACZonificacion = getSrvUbicacionSIGPAC(realPath);
		com.rsi.agp.core.managers.impl.SIGPACResponse respuestaTransformada = null;

		try {
			// Llamada al SW
			respuesta = srvSIGPACZonificacion.equivalenciaSIGPACAgroseguro(params);

			if (respuesta != null) {
				logger.debug("respuesta: " + params.toString());
				respuestaTransformada = transformarRespuesta(respuesta);
			}

		} catch (AgrException e) {
			// El servicio ha devuelto una excepcion => tratar el error e informar al
			// usuario
			throw e;

		} catch (javax.xml.ws.soap.SOAPFaultException e) {
			throw e;

		} catch (Exception ex) {
			logger.error("Error inesperado al llamar al servicio web de SIGPAC zonificación - getDatosUbicacionSIGPAC",
					ex);
			throw ex;
		}

		return respuestaTransformada;
	}
	
	/**
	 * Configura y realiza la llamada para obtener los datos de ubicación dado un
	 * SIGPAC
	 * 
	 * @param sigpacVO
	 * @param claseId
	 * @param realPath
	 *            Ruta del Web-Inf
	 * @param codUsuario
	 *            Código de usuario
	 * @return
	 * @throws AgrException
	 * @throws Exception
	 *             Error en la llamada al SW
	 */
	public es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACFaunaResponse infoReduccionParcelasFauna(SIGPACFaunaRequest params, String realPath) throws AgrException, Exception {

		SIGPACFaunaResponse respuesta = null;
		ContratacionSCSIGPACZonificacion srvSIGPACZonificacion = getSrvUbicacionSIGPAC(realPath);

		try {
			// Llamada al SW
			respuesta = srvSIGPACZonificacion.infoReduccionParcelasFauna(params);

				logger.debug("respuesta: " + params.toString());

		} catch (AgrException e) {
		
			logger.error("Error al llamar al SW de infoReduccionParcelasFauna", e);
			respuesta=null;
			throw e;

		} catch (SOAPFaultException e) {
			throw e;

		} catch (Exception ex) {
			logger.error("Error inesperado al llamar al servicio web de SIGPAC zonificación - getDatosUbicacionSIGPAC",
					ex);
			throw ex;
		}

		return respuesta;
	}

	/**
	 * Metodo para crear los objetos necesarios para las llamadas al servicio web.
	 * 
	 * @param realPath
	 *            Ruta real de los ficheros "wsdl"
	 * @return Manejador para las llamadas al servicio de SIGPAC Zonificación.
	 */
	private ContratacionSCSIGPACZonificacion getSrvUbicacionSIGPAC(String realPath) {
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();

		URL wsdlLocation = null;
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("sigpacZonificacionWS.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new SWConsultaContratacionException(
					"Imposible recuperar el WSDL de SIGPAC zonificación. Revise la Ruta: " + url, e1);
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("sigpacZonificacionWS.location");
		String wsPort = WSUtils.getBundleProp("sigpacZonificacionWS.port");
		String wsService = WSUtils.getBundleProp("sigpacZonificacionWS.service");

		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		// Crea el envoltorio para la llamada al servicio web de impresion de anexos

		ContratacionSCSIGPACZonificacion_Service srv = new ContratacionSCSIGPACZonificacion_Service(wsdlLocation,
				serviceName);

		ContratacionSCSIGPACZonificacion srvSIGPACZonificacion = srv.getPort(portName,
				ContratacionSCSIGPACZonificacion.class);
		logger.debug(srvSIGPACZonificacion.toString());

		// Anade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvSIGPACZonificacion);

		return srvSIGPACZonificacion;
	}

	/**
	 * Rellena el objeto que encapsula la respuesta del SW de SIGPAC Zonificación
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws XmlException
	 */
	private com.rsi.agp.core.managers.impl.SIGPACResponse transformarRespuesta(SIGPACResponse response)
			throws XmlException, IOException {

		com.rsi.agp.core.managers.impl.SIGPACResponse respuesta = new com.rsi.agp.core.managers.impl.SIGPACResponse();

		Base64Binary relacionIncidencias = response.getEquivalenciaSIGPACAgroseguro();
		byte[] byteArray = relacionIncidencias.getValue();
		if (byteArray != null && byteArray.length > 0) {
			String xmlData = new String(byteArray, Constants.DEFAULT_ENCODING);
			es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.EquivalenciaSIGPACAgroseguroDocument eqSigpac = es.agroseguro.seguroAgrario.equivalenciaSIGPACAgroseguro.EquivalenciaSIGPACAgroseguroDocument.Factory
					.parse(new StringReader(xmlData));

			respuesta.setEquivalenciaSIGPACAgroseguroDocument(eqSigpac);

		}
		return respuesta;
	}

	public static SIGPACRequest obtenerSIGPACRequest(SigpacVO sigpacVO) {

		// Parametros a enviar al SW
		SIGPACRequest params = new SIGPACRequest();

		// Obligatorios
		params.setProvinciaSIGPAC(Integer.parseInt(sigpacVO.getProv()));
		params.setTerminoSIGPAC(Integer.parseInt(sigpacVO.getTerm()));
		params.setAgregadoSIGPAC(Integer.parseInt(sigpacVO.getAgr()));
		params.setZonaSIGPAC(Integer.parseInt(sigpacVO.getZona()));
		params.setPoligonoSIGPAC(Integer.parseInt(sigpacVO.getPol()));
		params.setParcelaSIGPAC(Integer.parseInt(sigpacVO.getParc()));

		// Opcionales

		es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.ObjectFactory o = new es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.ObjectFactory();

		JAXBElement<Integer> planJaxb = o.createSIGPACRequestPlan(Integer.parseInt(sigpacVO.getCodPlan()));
		params.setPlan(planJaxb);

		JAXBElement<Integer> codLineaJaxb = o.createSIGPACRequestLinea(Integer.parseInt(sigpacVO.getCodLinea()));
		params.setLinea(codLineaJaxb);

		if (!StringUtils.isNullOrEmpty(sigpacVO.getCodCultivo())) {
			JAXBElement<Integer> cultivoJaxb = o.createSIGPACRequestCultivo(Integer.parseInt(sigpacVO.getCodCultivo()));
			params.setCultivo(cultivoJaxb);
		}

		return params;
	}

	public static SIGPACFaunaRequest obtenerSIGPACFaunaRequest(SigpacVO sigpac) {
		// Parametros a enviar al SW
		SIGPACFaunaRequest params = new SIGPACFaunaRequest();

		// Obligatorios
		params.setPlan(Integer.parseInt(sigpac.getCodPlan()));
		params.setLinea(Integer.parseInt(sigpac.getCodLinea()));
		params.setProvinciaSIGPAC(Integer.parseInt(sigpac.getProv()));
		params.setTerminoSIGPAC(Integer.parseInt(sigpac.getTerm()));
		params.setAgregadoSIGPAC(Integer.parseInt(sigpac.getAgr()));
		params.setZonaSIGPAC(Integer.parseInt(sigpac.getZona()));
		params.setPoligonoSIGPAC(Integer.parseInt(sigpac.getPol()));
		params.setParcelaSIGPAC(Integer.parseInt(sigpac.getParc()));

		// Opcionales
		es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.ObjectFactory o = new es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.ObjectFactory();
		if (!StringUtils.isNullOrEmpty(sigpac.getRecinto())) {
			JAXBElement<Integer> recintoJaxb = o
					.createSIGPACFaunaRequestRecintoSIGPAC(Integer.parseInt(sigpac.getRecinto()));
			params.setRecintoSIGPAC(recintoJaxb);
		}

		return params;
	}
}