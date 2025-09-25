package com.rsi.agp.core.managers.impl.anexoMod.impresion;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;

import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AnulacionPolizaResponse;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ContratacionSCImpresionModificacion;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ContratacionSCImpresionModificacion_Service;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.EnvioDocumentacionIncidenciaRequest;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.EnvioDocumentacionIncidenciaResponse;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.EnvioDocumentacionNuevaIncidenciaRequest;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.EnvioDocumentacionNuevaIncidenciaResponse;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ParametrosAnulacionRescision;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.PdfIncidenciaRequest;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.PdfIncidenciaResponse;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.PlanLineaNif;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.PlanReferencia;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.RelacionIncidenciasPolizaRequest;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.RelacionIncidenciasPolizaResponse;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.RelacionIncidenciasRequest;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.RelacionIncidenciasResponse;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.RescisionPropuestaResponse;

public class SWImpresionModificacionHelper {
	
	private static final Log logger = LogFactory.getLog(SWImpresionModificacionHelper.class);
	
	/**
	 * Configura y realiza la llamada para obtener la impresion del anexo por cupon
	 * @param referencia Referencia de la poliza sobre la cual se solicita la impresion
	 * @param plan Plan
	 * @param realPath
	 * @throws es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException 
	 * @throws AgrException Error en la llamada al SW
	 */
	public com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasPolizaResponse  
		getSolicitudImpresionModificacionPoliza(String referencia, BigDecimal plan, String realPath) throws AgrException,Exception {
		
		RelacionIncidenciasPolizaResponse respuesta;
		ContratacionSCImpresionModificacion srvImpresionAnexoMod = getSrvImpresionAnexoModificacion(realPath);
		
			// Parametros a enviar al SW
			RelacionIncidenciasPolizaRequest params = new RelacionIncidenciasPolizaRequest();
			params.setPlan(plan.intValue());
			params.setReferencia(referencia);
			
		try {	
			// Llamada al SW
			respuesta = srvImpresionAnexoMod.relacionIncidenciasPoliza(params); 
		
			if (respuesta != null) {
				logger.debug("respuesta: " + params.toString());
				return getSolicitudModificacionResponse(respuesta);
					
			}
		
		} catch (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException e) {
			//El servicio ha devuelto una excepcion => tratar el error e informar al usuario
			throw e;
		} catch (Exception ex ) {
			logger.error("Error inesperado al llamar al servicio web de impresion modificacion - getSolicitudImpresionModificacion" , ex);
			throw ex;
		}
		
		return null;
		
	}
	
	public com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasResponse getSolicitudImpresionModificacion(
			String referencia, BigDecimal plan, String realPath)
			throws AgrException, Exception {

		PlanReferencia planReferencia = new PlanReferencia();
		planReferencia.setPlan(plan.intValue());
		planReferencia.setReferencia(referencia);

		return getSolicitudImpresionModificacion(planReferencia, null, realPath);
	}

	public com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasResponse getSolicitudImpresionModificacion(
			String nifcif, BigDecimal plan, BigDecimal linea, String realPath)
			throws AgrException, Exception {

		PlanLineaNif planLineaNif = new PlanLineaNif();
		planLineaNif.setPlan(plan.intValue());
		planLineaNif.setLinea(linea.intValue());
		planLineaNif.setNif(nifcif);

		return getSolicitudImpresionModificacion(null, planLineaNif, realPath);
	}

	private com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasResponse getSolicitudImpresionModificacion(
			PlanReferencia planReferencia, PlanLineaNif planLineaNif,
			String realPath) throws AgrException, Exception {

		ContratacionSCImpresionModificacion srvImpresionAnexoMod = this
				.getSrvImpresionAnexoModificacion(realPath);

		RelacionIncidenciasRequest request = new RelacionIncidenciasRequest();
		if (planReferencia != null)
			request.setPlanReferencia(planReferencia);
		if (planLineaNif != null)
			request.setPlanLineaNif(planLineaNif);

		try {
			// Llamada al SW
			RelacionIncidenciasResponse response = srvImpresionAnexoMod
					.relacionIncidencias(request);

			if (response != null) {
				return this.getSolicitudModificacionResponse(response);
			}

		} catch (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException e) {
			// El servicio ha devuelto una excepcion => tratar el error e
			// informar al usuario
			throw e;
		} catch (Exception ex) {
			logger.error(
					"Error inesperado al llamar al servicio web de impresion modificacion - getSolicitudImpresionModificacion",
					ex);
			throw ex;
		}

		return null;

	}
	
	/**
	 * Configura y realiza la llamada para el pdf de incidencias de anexos
	 * @param idCupon
	 * @param realPath
	 * @param numero 
	 * @param anio 
	 * @throws es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException 
	 * @throws AgrException Error en la llamada al SW
	 */
	public byte[] getImprimirPdfIncidencia(String idCupon, String realPath, String anio, String numero) 
			throws AgrException,Exception {
		
		PdfIncidenciaResponse respuesta;
		ContratacionSCImpresionModificacion srvImpresionAnexoMod = getSrvImpresionAnexoModificacion(realPath);
		
		// Parametros a enviar al SW
		PdfIncidenciaRequest params = new PdfIncidenciaRequest();
		//params.set

		es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ObjectFactory o = 
				new es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ObjectFactory();
		
		if (StringUtils.nullToString(idCupon).startsWith("SW")) {
			JAXBElement<String> idCuponJ = o.createPdfIncidenciaRequestCuponModificacion(idCupon);
			params.setCuponModificacion(idCuponJ);
			
		}else {
			JAXBElement<Integer> anioJ = o.createPdfIncidenciaRequestAnio(Integer.valueOf(anio));
			JAXBElement<BigInteger> numeroJ = o.createPdfIncidenciaRequestNumero(new BigInteger(numero));
			params.setAnio(anioJ);
			params.setNumero(numeroJ);
		}
		
		try {
			// Llamada al SW
			respuesta = srvImpresionAnexoMod.pdfIncidencia(params);
		
		} catch (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException e) {
			//El servicio ha devuelto una excepcion => tratar el error e informar al usuario
			throw e;
		} catch (Exception ex ) {
			logger.error("Error inesperado al llamar al servicio web de impresion modificacion - getImprimirPdfIncidencia" , ex);
			throw ex;
		}
		
		if (respuesta != null) {
			List<Base64Binary> listBytes = respuesta.getDocumento();
			
			Base64Binary b = listBytes.get(0);
			byte[] byteArray = b.getValue();
				
			if (byteArray != null && byteArray.length > 0){
					return byteArray;
			}
		}
		return null;
	}
	
	/**
	 * Rellena el objeto que encapsula la respuesta del SW de Solicitud de impresion
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws XmlException 
	 */
	private com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasResponse getSolicitudModificacionResponse(
			RelacionIncidenciasResponse response) throws XmlException,
			IOException {

		com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasResponse respuesta = new com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasResponse();

		Base64Binary relacionIncidencias = response.getRelacionIncidencias();
		byte[] byteArray = relacionIncidencias.getValue();
		if (byteArray != null && byteArray.length > 0) {
			String xmlData = new String(byteArray, Constants.DEFAULT_ENCODING);
			es.agroseguro.relacionIncidencias.RelacionIncidenciasDocument ri = es.agroseguro.relacionIncidencias.RelacionIncidenciasDocument.Factory
					.parse(new StringReader(xmlData));

			respuesta.setRelacionIncidencias(ri);

		}
		return respuesta;
	}
	
	/**
	 * Rellena el objeto que encapsula la respuesta del SW de Solicitud de impresion
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws XmlException 
	 */
	private com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasPolizaResponse  getSolicitudModificacionResponse 
			(RelacionIncidenciasPolizaResponse response) throws XmlException, IOException {
		
		com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasPolizaResponse respuesta = 
				new com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasPolizaResponse();
		
		Base64Binary relacionIncidencias = response.getRelacionIncidenciasPoliza();
		byte[] byteArray = relacionIncidencias.getValue();		
		if (byteArray != null && byteArray.length > 0){
			String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
			es.agroseguro.seguroAgrario.relacionIncidenciasPoliza.RelacionIncidenciasPolizaDocument riPol =
					es.agroseguro.seguroAgrario.relacionIncidenciasPoliza.RelacionIncidenciasPolizaDocument.Factory.
					parse(new StringReader(xmlData));
			
			respuesta.setRelacionIncidenciaPoliza(riPol);
			
		}
		return respuesta;
	}
	
	
	/**
	 * Metodo para crear los objetos necesarios para las llamadas al servicio web.
	 * @param realPath Ruta real de los ficheros "wsdl"
	 * @return Manejador para las llamadas al servicio de impresion de anexos.
	 */
	private ContratacionSCImpresionModificacion getSrvImpresionAnexoModificacion(String realPath) {
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
		try {
			wsdlLocation = new URL("file:" + realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("impresionModificacionWS.wsdl"));
		} catch (MalformedURLException e1) {
			throw new SWConsultaContratacionException("Imposible recuperar el WSDL de impresion de anexos. Revise la Ruta: " + 
					((wsdlLocation != null) ? wsdlLocation.toString() : ""), e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("impresionModificacionWS.location");
		String wsPort     = WSUtils.getBundleProp("impresionModificacionWS.port");
		String wsService  = WSUtils.getBundleProp("impresionModificacionWS.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de impresion de anexos		
		
		ContratacionSCImpresionModificacion_Service srv = new ContratacionSCImpresionModificacion_Service(wsdlLocation, serviceName);
		
		ContratacionSCImpresionModificacion srvImpresionAnexoMod = srv.getPort(portName, ContratacionSCImpresionModificacion.class);
		logger.debug(srvImpresionAnexoMod.toString());
		
		// Anade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvImpresionAnexoMod);
				
		return srvImpresionAnexoMod;
	}

	/* MÉTODOS PARA EL ENVIO DE DOCUMENTACIÓN SOBRE INCIDENCIAS */
	public AcuseReciboDocument envioDocumentacionNuevaIncidencia(String realPath, EnvioDocumentacionNuevaIncidenciaRequest req) throws AgrException{
		AcuseReciboDocument acuseRecio = null;
		ContratacionSCImpresionModificacion srv = this.getSrvImpresionAnexoModificacion(realPath);
		EnvioDocumentacionNuevaIncidenciaResponse res = srv.envioDocumentacionNuevaIncidencia(req);
		if(res != null){
			try {
				acuseRecio = (AcuseReciboDocument) this.procesarAcuseReciboDocumentos(res);
			} catch(Exception e) {
				logger.error("No se pudo obtener el documento de acuse de recibo", e);
			}
		}
		return acuseRecio;
	}
	
	public AcuseReciboDocument envioDocumentacionIncidencia(String realPath, EnvioDocumentacionIncidenciaRequest wsReq) throws AgrException{
		AcuseReciboDocument acuseRecio = null;
		ContratacionSCImpresionModificacion srv = this.getSrvImpresionAnexoModificacion(realPath);	
		EnvioDocumentacionIncidenciaResponse wsRes = srv.envioDocumentacionIncidencia(wsReq);
		if(wsRes != null){
			try {
				acuseRecio = (AcuseReciboDocument) this.procesarAcuseReciboDocumentos(wsRes);
			} catch(Exception e) {
				logger.error("No se pudo obtener el documento de acuse de recibo", e);
			}
		}
		return acuseRecio;
	}
	
	private AcuseReciboDocument procesarAcuseReciboDocumentos(Object wsRes) throws XmlException, IOException{
		AcuseReciboDocument acuseReciboDocument = null;
		byte[] acuseByteArray = null;
		if(wsRes instanceof EnvioDocumentacionNuevaIncidenciaResponse){
			acuseByteArray = ((EnvioDocumentacionNuevaIncidenciaResponse) wsRes).getAcuseRecibo().getValue();
		} else {
			acuseByteArray = ((EnvioDocumentacionIncidenciaResponse) wsRes).getAcuseRecibo().getValue();
		}
		if(!ArrayUtils.isEmpty(acuseByteArray)){
			String xmlData = new String(acuseByteArray, Constants.DEFAULT_ENCODING);
			acuseReciboDocument = AcuseReciboDocument.Factory.parse(xmlData);
		}
		return acuseReciboDocument;
	}

	
	/* Pet. 57627 ** MODIF TAM (15.10.2019) ** Inicio **/
	/* MÉTODOS PARA EL ENVIO DE ANULACION / RESCISIÓN DE POLIZAS */
	public AcuseReciboDocument envioAnulacionPol(String realPath, ParametrosAnulacionRescision req) throws AgrException{
		
		AcuseReciboDocument acuseRecibo = null;
		ContratacionSCImpresionModificacion srv = this.getSrvImpresionAnexoModificacion(realPath);
		AnulacionPolizaResponse res = srv.anulacionPoliza(req);
		if(res != null){
			try {
				acuseRecibo = (AcuseReciboDocument) this.procesarAcuseReciboAnulacion(res);
			} catch(Exception e) {
				logger.error("No se pudo obtener el documento de acuse de recibo de la Anulación de Póliza", e);
			}
		}
		return acuseRecibo;
	}
	
	public AcuseReciboDocument envioRescisionPol(String realPath, ParametrosAnulacionRescision req) throws AgrException{
		AcuseReciboDocument acuseRecibo = null;
		ContratacionSCImpresionModificacion srv = this.getSrvImpresionAnexoModificacion(realPath);
		RescisionPropuestaResponse res = srv.rescisionPropuesta(req);
		                                     
		if(res != null){
			try {
				acuseRecibo = (AcuseReciboDocument) this.procesarAcuseReciboRescision(res);
			} catch(Exception e) {
				logger.error("No se pudo obtener el documento de acuse de recibo de la Rescisión de Póliza", e);
			}
		}
		return acuseRecibo;
	}
	
	
	private AcuseReciboDocument procesarAcuseReciboAnulacion(Object wsRes) throws XmlException, IOException{
		AcuseReciboDocument acuseReciboAnulacion = null;
		byte[] acuseByteArray = null;
		if(wsRes instanceof AnulacionPolizaResponse){
			acuseByteArray = ((AnulacionPolizaResponse) wsRes).getAcuseRecibo().getValue();
		} else {
			acuseByteArray = ((AnulacionPolizaResponse) wsRes).getAcuseRecibo().getValue();
		}
		if(!ArrayUtils.isEmpty(acuseByteArray)){
			String xmlData = new String(acuseByteArray, Constants.DEFAULT_ENCODING);
			acuseReciboAnulacion = AcuseReciboDocument.Factory.parse(xmlData);
		}
		return acuseReciboAnulacion;
	}
	
	private AcuseReciboDocument procesarAcuseReciboRescision(Object wsRes) throws XmlException, IOException{
		AcuseReciboDocument acuseReciboRescision = null;
		byte[] acuseByteArray = null;
		if(wsRes instanceof RescisionPropuestaResponse){
			acuseByteArray = ((RescisionPropuestaResponse) wsRes).getAcuseRecibo().getValue();
		} else {
			acuseByteArray = ((RescisionPropuestaResponse) wsRes).getAcuseRecibo().getValue();
		}
		if(!ArrayUtils.isEmpty(acuseByteArray)){
			String xmlData = new String(acuseByteArray, Constants.DEFAULT_ENCODING);
			acuseReciboRescision = AcuseReciboDocument.Factory.parse(xmlData);
		}
		return acuseReciboRescision;
	}
	/* Pet. 57627 ** MODIF TAM (15.10.2019) ** Fin **/
	
	
}
