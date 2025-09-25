package com.rsi.agp.batch.importacion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.hibernate.Session;

import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.contratacionscmodificacion.ConsultarContratacionRequest;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ConsultarContratacionResponse;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion_Service;

public final class ImportacionPolizasExtWS {

	private ImportacionPolizasExtWS() {
	}

	protected static es.agroseguro.contratacion.Poliza getSituacionActualizada(final String referencia,
			final Integer plan, final Integer linea, final String tipoRef, final Session session, final boolean esGanado)
			throws Exception {
		es.agroseguro.contratacion.Poliza poliza;
		InputStream is;
		URL wsdlLocation = null;
		String url = "";

		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();

		try {
			File ficherowsdl = new File(WSUtils.getBundleProp("anexoModificacionWS.wsdl"));
			
			url = ficherowsdl.getAbsolutePath();
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException ex) {
			throw new Exception(
					"Imposible recuperar el WSDL de Situacion Actual. Revise la Ruta: "
							+ url, ex);
		} catch (NullPointerException ex) {
			throw new Exception(
					"Imposible obtener el WSDL de Situacion Actual. Revise la Ruta: "
							+ wsdlLocation.toString(), ex);
		}

		String wsLocation = WSUtils.getBundleProp("anexoModificacionWS.location");
		String wsPort = WSUtils.getBundleProp("anexoModificacionWS.port");
		String wsService = WSUtils.getBundleProp("anexoModificacionWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);

		try {

			ContratacionSCModificacion_Service srv = new ContratacionSCModificacion_Service(
					wsdlLocation, serviceName);
			ContratacionSCModificacion srvModificacion = (ContratacionSCModificacion) srv
					.getPort(portName, ContratacionSCModificacion.class);

			WSUtils.addSecurityHeader(srvModificacion);

			ConsultarContratacionRequest wsReq = new ConsultarContratacionRequest();
			wsReq.setPlan(plan);
			wsReq.setReferencia(referencia);

			ConsultarContratacionResponse wsResp = srvModificacion
					.consultarContratacion(wsReq);
			
			if (null != tipoRef) {
				if(tipoRef.trim().compareTo(new String("P"))==0){//principal
					is = new ByteArrayInputStream(wsResp.getPolizaPrincipal().getValue());
					es.agroseguro.contratacion.PolizaDocument polizadoc = es.agroseguro.contratacion.PolizaDocument.Factory.parse(is);
					poliza = polizadoc.getPoliza();				
				}else{// complementaria
					boolean vieneComp = wsResp.getPolizaComplementaria().getValue().length != 0;
					if (vieneComp) {
						// Presuponemos que es complementaria
						is = new ByteArrayInputStream(wsResp.getPolizaComplementaria().getValue());
						es.agroseguro.contratacion.PolizaDocument polizaCompdoc = es.agroseguro.contratacion.PolizaDocument.Factory.parse(is);
						poliza = polizaCompdoc.getPoliza();
					}else{
						throw new Exception("Error. La situacion actualizada de la poliza no recoge la poliza complementaria. Referencia:  " + referencia);	
					}						
				}
			} else {
				throw new Exception("Error. Se desconoce el tipo de referencia de la poliza " + referencia);
			}
			
		} catch (es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException ex) {
			throw new Exception(
					"Error en la llamada al WS de situacion actual.", ex);
		} catch (XmlException ex) {
			throw new Exception(
					"Error en el parseo del XML de situacion actual. Codigo error: "
							+ ex.getError().getErrorCode(), ex);
		} catch (IOException ex) {
			throw new Exception(
					"Error de I/O en la obtencion del XML de situacion actual: "
							+ ex.getMessage(), ex);
		}

		return poliza;
	}
}
