package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.IDanhosFaunaManager;
import com.rsi.agp.vo.SigpacVO;

import es.agroseguro.seguroAgrario.infoReduccionParcelaFauna.InfoReduccionParcelaFauna;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.Error;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACFaunaRequest;
import es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.SIGPACFaunaResponse;

/**
 * @author U029769
 */
public class DanhosFaunaManager implements IDanhosFaunaManager {
	
	private Log logger = LogFactory.getLog(DanhosFaunaManager.class);
	public String xmlData;
	
	@Override
	public InfoReduccionParcelaFauna obtenerDanhosFauna(SigpacVO sigpac,String realPath) throws Exception {
			
		logger.debug("Estamos en el manager");
		es.agroseguro.seguroAgrario.infoReduccionParcelaFauna.InfoReduccionParcelaFauna danhosFaunaXsd=null;
		
		SIGPACFaunaRequest request = null;
		SIGPACFaunaResponse respuesta = null;
		String error = "";
		
		try {
			String rutaWebInfDecod = URLDecoder.decode(realPath, "UTF-8");
			request=SWZonificacionSIGPACHelper.obtenerSIGPACFaunaRequest(sigpac);
			respuesta = new SWZonificacionSIGPACHelper().infoReduccionParcelasFauna(request, rutaWebInfDecod);
			if(null!=respuesta){
				
				xmlData = com.rsi.agp.core.util.WSUtils.getStringResponse(respuesta.getInfoReduccionParcelasFauna());
				danhosFaunaXsd=getDatosDanhosFauna(xmlData);
				
				
			}else{
				xmlData=null;
			}
			
		} catch (AgrException e) {
			error = getMsgAgrException(e);
			logger.debug("obtenerDanhosFauna" + error);
			danhosFaunaXsd=null;
		} catch (javax.xml.ws.soap.SOAPFaultException e) {
			error = e.getMessage();
			logger.debug("obtenerDanhosFauna: " + error);
			throw new BusinessException(error);
		} catch (Exception e) {
			error = e.getMessage();
			logger.error("obtenerDanhosFauna: ", e);
			throw e;
		}
		return danhosFaunaXsd;
	}
	
	
	private InfoReduccionParcelaFauna getDatosDanhosFauna(String xmlData) throws XmlException, IOException {
		
		es.agroseguro.seguroAgrario.infoReduccionParcelaFauna.InfoReduccionParcelaFauna datosDanhosFauna=null;
		es.agroseguro.seguroAgrario.infoReduccionParcelaFauna.InfoReduccionParcelaFaunaDocument datosDocumentDanhosFauna=
				es.agroseguro.seguroAgrario.infoReduccionParcelaFauna.InfoReduccionParcelaFaunaDocument.Factory.parse(new StringReader(xmlData));
		
		if(datosDocumentDanhosFauna!=null) {
			datosDanhosFauna=datosDocumentDanhosFauna.getInfoReduccionParcelaFauna();
		}
		
		return datosDanhosFauna;
	}

	/**
	 * Obtiene el mensaje de error para un AgrException
	 * 
	 * @param exc
	 * @return
	 */
	private String getMsgAgrException(es.agroseguro.serviciosweb.contratacionscsigpaczonificacion.AgrException exc) {
		String msg = ""; 
		if (exc.getFaultInfo() != null && exc.getFaultInfo().getError() != null) {
			for (Error error : exc.getFaultInfo().getError()) {
				msg += error.getMensaje() + " ";
			}
		}
		return msg;
	}
	
	

}
