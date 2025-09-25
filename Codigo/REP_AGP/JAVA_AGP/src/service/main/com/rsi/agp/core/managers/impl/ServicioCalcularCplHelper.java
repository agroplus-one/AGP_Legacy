package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.serviciosweb.contratacionscutilidades.AgrException;
import es.agroseguro.serviciosweb.contratacionscutilidades.CalcularRequest;
import es.agroseguro.serviciosweb.contratacionscutilidades.CalcularResponse;
import es.agroseguro.serviciosweb.contratacionscutilidades.ContratacionSCUtilidades;
import es.agroseguro.serviciosweb.contratacionscutilidades.ContratacionSCUtilidades_Service;
import es.agroseguro.serviciosweb.contratacionscutilidades.Error;
import es.agroseguro.serviciosweb.contratacionscutilidades.ObjectFactory;

/**
 * Helper para el Servicio de CÃ¡lculo
 * 
 * @author T-Systems
 *
 */
public class ServicioCalcularCplHelper {

	private static final Log logger = LogFactory.getLog(ServicioCalcularCplHelper.class);
	
	/**
	 * Configura y realiza la llamada al servicio Web de Cálculo
	 */
	public Map<String, Object> doWork(Long idEnvio, Long idPoliza, BigDecimal descuentoColectivo,String realPath, IPolizaDao polizaDao) throws CalculoServiceException {
		String xml = null;
		
		Poliza poliza = (Poliza) polizaDao.getObject(Poliza.class, idPoliza);
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

		// Añade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvCalculo);
		
		//Cogemos el XML de la BBDD		
		logger.debug("Obtenemos en un Clob el XML que ha generado el PL");
		xml = WSUtils.obtenXMLPolizaCpl(idEnvio, polizaDao);
		
		// No se recupero el XML...
		if (xml == null) throw new CalculoServiceException("No se ha podido obtener el XML de la Póliza");
		
		logger.debug("Llamando a Servicio de Contratacion: CALCULO>> " + xml);				

		// Se valida el XML antes de llamar al Servicio Web
		// Pet. 57626 ** MODIF TAM (29.05.2020) ** Inicio //
		/*WSUtils.getXMLCalculoCpl(xml);*/
		
		xml = xml.replaceAll("xmlns=\"http://www.agroseguro.es/Contratacion\"", "xmlns=\"http://www.agroseguro.es/PresupuestoContratacion\"");
		WSUtils.getXMLCalculoUnificado(xml);		
		
		// Se convierte el String a Base64, para enviarlo al WS
		byte[] array = null;
		Base64Binary base64Binary = new Base64Binary();
		base64Binary.setContentType("text/xml");
		try {
			array = xml.getBytes(Constants.DEFAULT_ENCODING);
			base64Binary.setValue(array);
		} catch (UnsupportedEncodingException e2) {
			logger.error("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
			throw new CalculoServiceException("Se esperaba un XML en formato " + Constants.DEFAULT_ENCODING, e2);
		}
		// Parametros de envio al Servicio Web
		CalcularRequest parameters = new CalcularRequest();
		parameters.setPoliza(base64Binary);		
		
		// Polizas de planes iguales o superiores a 2015 no se envia descuentoColectivo
		if (!poliza.isPlanMayorIgual2015()){
			ObjectFactory o = new ObjectFactory();		
			if (descuentoColectivo == null) descuentoColectivo = poliza.getColectivo().getPctdescuentocol();
			if (descuentoColectivo == null) descuentoColectivo = new BigDecimal(0);
			parameters.setDescuentoColectivo(o.createCalcularRequestDescuentoColectivo(descuentoColectivo)); 
		}
		
		CalcularResponse response = null;		
		try {
			response = srvCalculo.calcular(parameters);
			if (response != null)
			{
				Map<String, Object> retorno = new HashMap<String, Object>();
				// Se crea el String con el XML recibido y se envuelve en un Acuse de Recibo
				Base64Binary respuesta = response.getAcuseRecibo();
				byte[] byteArray = respuesta.getValue();
				String xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
				AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(xmlData));
				// Se comprueba si hay XML de calculo, y si lo hay , se crea el objeto y se devuelve
				if (response.getCalculo() != null && 
					response.getCalculo().getValue() != null && 
					response.getCalculo().getValue().length > 0) {
					respuesta = response.getCalculo();
					byteArray = respuesta.getValue();
					xmlData = new String (byteArray, Constants.DEFAULT_ENCODING);
					
					/* Pet. 57626 ** MODIF TAM (02.06.2020) **/
					/* Se quita la validación de Ganado/Agrícola, ya que ahora los 2 tipos van por formato Unificado */
					// Si la póliza es de Ganado se utiliza el formato unificado
					es.agroseguro.distribucionCostesSeguro.PolizaDocument polizaDoc = es.agroseguro.distribucionCostesSeguro.PolizaDocument.Factory.parse(new StringReader(xmlData));
					/* Pet. 57626 ** MODIF TAM (02.06.2020) Fin **/
					retorno.put("calculo", polizaDoc.getPoliza());					
				}
				retorno.put("acuse", acuseReciboDoc.getAcuseRecibo());
				return retorno;
			}
		} catch (AgrException e) {
			logger.error("Error inesperado devuelto por el servicio web de Calculo " , e);
			WSUtils.debugAgrException(e);
			String exceptionMsg = createCalculoServiceExceptionMessage(e);
			throw new CalculoServiceException(exceptionMsg);
		} catch (Exception e) {
			logger.error("Error inesperado al llamar al servicio web de Calculo" , e);
			throw new CalculoServiceException("Error inesperado al llamar al servicio web de Calculo" , e);
		}

		return null;
	}
	
	private String createCalculoServiceExceptionMessage(AgrException e){
		StringBuilder sb = new StringBuilder();
		for(Error error : e.getFaultInfo().getError()){
			sb.append(error.getCodigo()).append("####").append(error.getMensaje()).append("####");
		}
		return sb.toString();
	}
}
