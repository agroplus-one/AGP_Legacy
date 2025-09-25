package com.rsi.agp.core.managers.impl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;

import com.rsi.agp.core.util.WSUtils;


import es.agroseguro.estadoRenovacion.Renovacion;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovablesDocument;
import es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ObjectFactory;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ParametrosListaPolizasRenovables;

/**
 * Helper para el Servicio de anexos de modificación.
 * 
 * @author T-Systems
 * 
 */

public class SWListaPolizasRenovablesHelper {

	private static final Log logger = LogFactory.getLog(SWListaPolizasRenovablesHelper.class);

	public Renovacion getListaPolizaRenovables(final Long plan, final Long linea, final String referencia,
			final String realPath) throws Exception {
		URL wsdlLocation = null;
		
		Renovacion ren = null;

		// Establecemos proxy
		if (WSUtils.isProxyFixed())
			WSUtils.setProxy();
		
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("contratacionRenovacionesWS.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e) {
			logger.error("Imposible recuperar el WSDL de contratacionRenovaciones. Revise la Ruta: " + url, e);
			throw new WebServiceException("Imposible recuperar el WSDL de contratacionRenovaciones. Revise la Ruta: " + url, e);
		}

		// Recogemos de webservice.properties los valores para el ServiceName y Port
		String wsLocation = WSUtils.getBundleProp("contratacionRenovacionesWS.location");
		String wsPort = WSUtils.getBundleProp("contratacionRenovacionesWS.port");
		String wsService = WSUtils.getBundleProp("contratacionRenovacionesWS.service");

		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsService: " + wsService.toString());

		try {
			ContratacionRenovaciones_Service srv = new ContratacionRenovaciones_Service(wsdlLocation, serviceName);
			ContratacionRenovaciones contratRen = (ContratacionRenovaciones) srv.getPort(portName,
					ContratacionRenovaciones.class);
			WSUtils.addSecurityHeader(contratRen);
			ParametrosListaPolizasRenovables paramListPolReq = new ParametrosListaPolizasRenovables();

			// datos fijos
			paramListPolReq.setPlan(plan.intValue());
			paramListPolReq.setLinea(linea.intValue());

			ObjectFactory obj = new ObjectFactory();
			JAXBElement<String> ref = obj.createParametrosListaPolizasRenovablesReferencia(referencia);

			paramListPolReq.setReferencia(ref);

			logger.debug("## CALL WS - LISTA POL REN - PLAN: " + plan.toString() + " LINEA: " + linea.toString()
					+ " referencia: " + referencia + " ##");
			Renovacion[] renArr = null;

			renArr = getArrayRenovaciones(contratRen, paramListPolReq);

			if (renArr != null) {
				for (Renovacion renov : renArr) {
					ren = renov;
					break;
				}
			}

		} catch (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException ex) {
			logger.error("Error en WS en agrException: " + ex.getMessage().toString());
			List<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> lstErrores = ex.getFaultInfo().getError();
			String errores = "";
			for (es.agroseguro.serviciosweb.contratacionrenovaciones.Error error : lstErrores) {
				errores = errores + error.getMensaje().toString() + ".";
				logger.error("# Error en agrException: " + error.getMensaje().toString());
				break;
			}
		} catch (Exception e) {
			logger.error("# Error exception tipo: " + e.getClass().toString() + " " + e.getMessage().toString());
		}

		return ren;
	}

	/**
	 * Realiza la llamada al SW y devuelve el array de objetos Renovacion obtenido
	 * 
	 * @param contratRen
	 * @param paramListPolReq
	 * @return
	 * @throws AgrException
	 * @throws XmlException
	 * @throws UnsupportedEncodingException
	 */
	public static Renovacion[] getArrayRenovaciones(ContratacionRenovaciones contratRen,
			ParametrosListaPolizasRenovables paramListPolReq)
			throws AgrException, UnsupportedEncodingException, XmlException {

		return ListaPolizasRenovablesDocument.Factory
				.parse(new String(contratRen.listaPolizasRenovables(paramListPolReq).getPolizasRenovables().getValue(),
						"UTF-8"))
				.getListaPolizasRenovables().getRenovacionArray();
	}

	

}