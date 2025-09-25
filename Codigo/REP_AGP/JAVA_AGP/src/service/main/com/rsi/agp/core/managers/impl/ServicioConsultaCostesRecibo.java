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

import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ConsultarCostesReciboRequest;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ConsultarCostesReciboResponse;
import es.agroseguro.serviciosweb.seguimientoscpoliza.ObjectFactory;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza;
import es.agroseguro.serviciosweb.seguimientoscpoliza.SeguimientoSCPoliza_Service;
import es.agroseguro.tipos.PolizaReferenciaTipo;

public class ServicioConsultaCostesRecibo {

	private static final Log logger = LogFactory.getLog(ServicioConsultaCostesRecibo.class);

	public es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza doWork(String tipoReferencia,
			BigDecimal codPlan, String refPoliza, BigDecimal codRecibo, Date fechaEmisionRecibo, String realPath)
			throws SeguimientoServiceException {

		logger.debug("InformesExcelManager - ServicioConsultaCostesRecibo # INIT");
		logger.debug("codPlan: " + codPlan);
		logger.debug("refPoliza: " + refPoliza);
		logger.debug("tipoReferencia: " + tipoReferencia);
		
		// Establece el Proxy
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();

		URL wsdlLocation = null;
		String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("seguimiento.wsdl");
		try {

			wsdlLocation = new URL("file:" + url);
			
			logger.debug("URL SW XML: " + wsdlLocation.toString());

			// Recogemos de webservice.properties los valores para el ServiceName y Port
			String wsLocation = WSUtils.getBundleProp("seguimiento.location");
			String wsPort = WSUtils.getBundleProp("seguimiento.port");
			String wsService = WSUtils.getBundleProp("seguimiento.service");

			QName serviceName = new QName(wsLocation, wsService);
			QName portName = new QName(wsLocation, wsPort);

			// Envoltorio para la llamda al servicio web de seguimiento
			SeguimientoSCPoliza_Service srv = new SeguimientoSCPoliza_Service(wsdlLocation, serviceName);
			SeguimientoSCPoliza srvSeguimiento = srv.getPort(portName, SeguimientoSCPoliza.class);

			// AÃ±ade la cabecera de seguridad
			WSUtils.addSecurityHeader(srvSeguimiento);

			ConsultarCostesReciboRequest parameters = new ConsultarCostesReciboRequest();

			parameters.setTiporeferencia(PolizaReferenciaTipo.fromValue(tipoReferencia));
			parameters.setCodplan(codPlan.intValue());
			parameters.setReferencia(refPoliza);

			if (codRecibo != null) {
				// Cambios en el set de parámetros por modificaciones en los descriptores del
				// servicio de seguimiento
				parameters
						.setRecibo(new ObjectFactory().createConsultarCostesReciboRequestRecibo(codRecibo.intValue()));
			}

			if (fechaEmisionRecibo != null) {
				// Cambios en el set de parámetros por modificaciones en los descriptores del
				// servicio de seguimiento
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.setTime(fechaEmisionRecibo);
				XMLGregorianCalendar fecha = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
				parameters.setFecha(new ObjectFactory().createConsultarCostesReciboRequestFecha(fecha));

			}

			ConsultarCostesReciboResponse response = srvSeguimiento.consultarCostesRecibo(parameters);
			String recibo = new String(response.getCostesRecibo().getValue(), "UTF-8");

			logger.debug("Respuesta: " + recibo);
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza costesRecibo = es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.PolizaDocument.Factory
					.parse(new StringReader(recibo)).getPoliza();

			logger.debug("InformesExcelManager - ServicioConsultaCostesRecibo # END");
			return costesRecibo;

		} catch (MalformedURLException e1) {
			logger.error("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: " + url, e1);
			throw new SeguimientoServiceException("Imposible recuperar el WSDL de Seguimiento. Revise la Ruta: " + url, e1);
		} catch (AgrException e) {
			String msgError = WSUtils.debugAgrException(e);
			logger.error(msgError, e);
			throw new SeguimientoServiceException(msgError, e);
			//throw new SeguimientoServiceException("Error inesperado devuelto por el servicio web de Seguimiento", e);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Seguimiento", e);
			throw new SeguimientoServiceException("Error inesperado al llamar al servicio web de Seguimiento", e);
		}
	}
}
