package com.rsi.agp.core.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.rsi.agp.core.exception.ValidacionServiceException;
import com.rsi.agp.core.security.SecurityHandler;
import com.rsi.agp.core.webapp.util.StringUtils;

/**
 * Utilidades para los servicios Web
 * 
 * @author T-Systems
 *
 */
public class WSUtils {
	
	// Fichero de Propiedades
	private static final ResourceBundle bundle = ResourceBundle.getBundle("webservices");;
	// Encoding
	public static final String DEFAULT_ENCODING = "UTF-8";
	// Logger
	private static final Log logger = LogFactory.getLog(WSUtils.class);
	// Indica si ya se establecio el proxy
	private static boolean proxyFixed = false;

	/**
	 * Método para añadir la cabecera de seguridad al "port" de acceso a los servicios web
	 * @param servicio Servicio para el que es necesaria la cabecera
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addSecurityHeader(Object servicio) {
		List securityHandlerChain = new ArrayList();
		securityHandlerChain.add(new SecurityHandler());
		((BindingProvider)servicio).getBinding().setHandlerChain(securityHandlerChain);
	}
	
	/**
	 * Método para establecer los valores de configuracion del proxy (si es necesario)
	 * Recupera los valores del fichero webservices.properties
	 */
	public static void setProxy() {
		
		if ("true".equals(bundle.getString("proxy.on"))) {
			System.setProperty("http.proxyHost", bundle.getString("proxy.host"));
			System.setProperty("http.proxyPort", bundle.getString("proxy.port"));
			if (!"".equals(StringUtils.nullToString(bundle.getString("proxy.user")))) {
				Authenticator.setDefault(new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(bundle
								.getString("proxy.user"), bundle.getString(
								"proxy.password").toCharArray());
					}
				});
			}
			WSUtils.setProxyFixed(true);
		}
	}
	
	/**
	 * Convierte un Clob a String
	 * 
	 * @param clobInData
	 * @return
	 */
	public static String convertClob2String(java.sql.Clob clobInData) {
		String stringClob = null;
		try {
			long i = 1;
			int clobLength = (int) clobInData.length();
			stringClob = clobInData.getSubString(i, clobLength);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error convirtiendo objeto Clob a String", e);
		}
		return stringClob;
	}
	
	/**
	 * Devuelve la propiedad indicada del fichero de propiedades webservices.properties
	 * 
	 * @param String La propiedad
	 */
	public static String getBundleProp(String theProp) {
		return bundle.getString(theProp);
	}

	/**
	 * Indica si el proxy está establecido
	 * 
	 * @return the proxyFixed
	 */
	public static boolean isProxyFixed() {
		return WSUtils.proxyFixed;
	}

	/**
	 * Establece si el proxy ya se fijo
	 * 
	 * @param proxyFixed the proxyFixed to set
	 */
	public static void setProxyFixed(boolean proxyFixed) {
		WSUtils.proxyFixed = proxyFixed;
	}
	
	/**
	 * Valida un String XML de Poliza
	 * 
	 * @param xml
	 * @return polizaDocument
	 * @throws ValidacionServiceException
	 */
	public static es.agroseguro.seguroAgrario.contratacion.PolizaDocument getXMLPoliza (String xml) throws Exception {
	
		// Se valida el XML antes de llamar al Servicio Web
		es.agroseguro.seguroAgrario.contratacion.PolizaDocument polizaDocument = null;
		ArrayList<String> validationErrors = new ArrayList<String>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);
		
		try {
			polizaDocument = es.agroseguro.seguroAgrario.contratacion.PolizaDocument.Factory.parse(xml);
		} catch (XmlException e1) {
			e1.printStackTrace();
			throw new Exception("Error al convertir el XML a XML Bean");
		}
		
		boolean bValidation = polizaDocument.validate(validationOptions);
		
		if (!bValidation) {
			logger.error("XML invalido, no cumple el esquema ContratacionSeguroAgrario.xsd");
		    Iterator<String> iter = validationErrors.iterator();
		    while (iter.hasNext())
		        logger.error(">> " + iter.next());
	
		    throw new Exception("XML invalido, no cumple el esquema ContratacionSeguroAgrario.xsd");
		}
		return polizaDocument;
	}
	
	public static String debugAgrException(Exception e) {
		String mensaje = "";
		es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException segException = null;
		if (e instanceof es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException) {
			segException = (es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException) e;
			mensaje += WSUtils.printException(segException);
		}
		return mensaje;
	}

	private static String printException(
			es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException exc) {

		String mensaje = "";
		List<es.agroseguro.serviciosweb.seguimientoscpoliza.Error> errores = null;

		es.agroseguro.serviciosweb.seguimientoscpoliza.AgrFallo fallo = exc
				.getFaultInfo();

		if (fallo != null)
			errores = fallo.getError();
		if (errores != null) {
			logger.error("Errores devueltos por el Servicio Web: ");
			logger.error("-------------------------------------- ");

			for (es.agroseguro.serviciosweb.seguimientoscpoliza.Error error : errores) {
				logger.error("Codigo: " + error.getCodigo() + " - Mensaje: "
						+ error.getMensaje());
				mensaje += "Codigo: " + error.getCodigo() + " - Mensaje: "
						+ error.getMensaje();
			}
		}

		return mensaje;
	}
}