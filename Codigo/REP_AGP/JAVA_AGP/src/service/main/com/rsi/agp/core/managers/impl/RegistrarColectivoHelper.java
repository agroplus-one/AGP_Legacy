package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.RegistrarColectivoException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.serviciosweb.contratacioncolectivos.ContratacionColectivos;
import es.agroseguro.serviciosweb.contratacioncolectivos.ContratacionColectivos_Service;
import es.agroseguro.serviciosweb.contratacioncolectivos.ParametrosRegistrarColectivo;
import es.agroseguro.serviciosweb.contratacioncolectivos.RegistrarColectivoResponse;

public class RegistrarColectivoHelper {

	private static final Log logger = LogFactory.getLog(RegistrarColectivoHelper.class);

	public static AcuseRecibo registrarColectivo(final String realPath, final String xmlEnvio)
			throws Exception {
		
		AcuseRecibo acuseRecibo = null;
		logger.debug("RegistrarColectivoHelper - registrarColectivo - init");

		// Crea el objeto para llamar al SW
		ContratacionColectivos srvContratacionColectivos = getSrvContratacionColectivos(realPath);

		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xmlEnvio.getBytes(Constants.DEFAULT_ENCODING);
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e) {
			logger.error("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e);
			throw new RegistrarColectivoException("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e);
		}

		// Crea el objeto que encapsula los parametros para la llamada al metodo de
		// calculo de modificacion con cupon activo
		ParametrosRegistrarColectivo entrada = new ParametrosRegistrarColectivo();
		entrada.setColectivo(base64Binary);

		// Llama al metodo de calculo de modificacion con cupon activo

		RegistrarColectivoResponse salida = null;

		try {
			salida = srvContratacionColectivos.registrarColectivo(entrada);
			if (salida != null) {
				
				byte[] byteArrayColectivo = salida.getAcuse().getValue();
				String xmlDataColectivo = new String(byteArrayColectivo, Constants.DEFAULT_ENCODING);

				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(xmlDataColectivo));
				
				acuseRecibo = acuseReciboDoc.getAcuseRecibo();
				
				logger.debug("ESTADO COLECTIVO: " + acuseRecibo.getDocumentoArray(0).getEstado());
			}
		} catch (es.agroseguro.serviciosweb.contratacioncolectivos.AgrException e) {
			logger.error(e);
		}
		
		logger.debug("RegistrarColectivoHelper - registrarColectivo - end");

		return acuseRecibo;
	}

	/**
	 * Genera el objeto para llamar al SW de Contratacion Colectivos
	 * 
	 * @param realPath
	 * @return
	 * @throws ContratacionColectivosException
	 */
	private static ContratacionColectivos getSrvContratacionColectivos(final String realPath) {
		
		logger.debug("RegistrarColectivoHelper - getSrvContratacionColectivos - init");

		// Establece el proxy si no se ha hecho anteriormente
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();

		// Obtiene la ubicacion del .wsdl de calculo de modificacion
		URL wsdlLocation = getWsdlLocation(realPath);

		// Obtiene del fichero de propiedades los valores necesarios para generar el
		// objeto para llamar al SW de calculo de modificacion
		ContratacionColectivos srv = null;

		try {
			String wsLocation = WSUtils.getBundleProp("contratacion_colectivos.location");
			String wsPort = WSUtils.getBundleProp("contratacion_colectivos.port");
			String wsService = WSUtils.getBundleProp("contratacion_colectivos.service");
			QName serviceName = new QName(wsLocation, wsService);
			QName portName = new QName(wsLocation, wsPort);

			ContratacionColectivos_Service srvCalculoAm = new ContratacionColectivos_Service(wsdlLocation, serviceName);
			srv = srvCalculoAm.getPort(portName, ContratacionColectivos.class);

			logger.debug(srv.toString());

			// Añade la cabecera de seguridad
			WSUtils.addSecurityHeader(srv);
		} catch (Exception e) {
			throw new ContratacionColectivosException(
					"Error al generar el objeto para llamar al SW de contratacion de colectivos", e);
		}
		
		logger.debug("RegistrarColectivoHelper - getSrvContratacionColectivos - end");

		return srv;
	}

	/**
	 * Devuelve la ubicacion del .wsdl de contratacion de colectivos
	 * 
	 * @param realPath
	 * @return
	 * @throws ContratacionColectivosException
	 */
	private static URL getWsdlLocation(final String realPath) throws ContratacionColectivosException {
		
		logger.debug("RegistrarColectivoHelper - getWsdlLocation - init");

		URL wsdlLocation = null;
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("contratacion_colectivos.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new ContratacionColectivosException(
					"Imposible recuperar el WSDL de contratacion de colectivos. Revise la Ruta: " + url, e1);
		} catch (Exception e2) {
			throw new ContratacionColectivosException(
					"Error inesperado al recuperar el WSDL de contratacion de colectivo", e2);
		}
		
		logger.debug("RegistrarColectivoHelper - getWsdlLocation - end");

		return wsdlLocation;
	}

}
