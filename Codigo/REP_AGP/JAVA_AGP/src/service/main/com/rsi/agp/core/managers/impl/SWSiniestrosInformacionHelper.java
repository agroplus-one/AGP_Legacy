package com.rsi.agp.core.managers.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.SWValidacionSiniestroException;
import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.serviciosweb.siniestrosscinformacion.AgrException;
import es.agroseguro.serviciosweb.siniestrosscinformacion.InfoBasicaActasResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.InfoBasicaHojasCampoResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.InfoBasicaSiniestrosResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.ObjectFactory;
import es.agroseguro.serviciosweb.siniestrosscinformacion.ParametrosAvisoSiniestros;
import es.agroseguro.serviciosweb.siniestrosscinformacion.ParametrosEntrada;
import es.agroseguro.serviciosweb.siniestrosscinformacion.ParametrosEntradaSiniestros;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfActaRequest;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfActaResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfHojaCampoRequest;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfHojaCampoResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfParteSiniestroRequest;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfParteSiniestroResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.SiniestrosSCInformacion;
import es.agroseguro.serviciosweb.siniestrosscinformacion.SiniestrosSCInformacion_Service;

public class SWSiniestrosInformacionHelper {
	private static final Log logger = LogFactory.getLog(SWSiniestrosInformacionHelper.class);
	
	/**
	 * LLama al servicio de informacion de siniestros y devuelve la lista de Hojas de campo de la poliza
	 * @param codPlan
	 * @param refPoliza
	 * @param nifSocio
	 * @param realPath
	 * @return
	 * @throws Exception
	 */
	public InfoBasicaHojasCampoResponse getHojasCampoSW(Integer codPlan, String refPoliza, String nifSocio, 
			String realPath) throws  Exception {
		InfoBasicaHojasCampoResponse response= null;
		
		SiniestrosSCInformacion srvSr = getSrvSiniestroInformacion(realPath);
		//Recogemos los parametros de entrada a enviar al servicio
		ParametrosEntrada paramsIn =getParametrosEntradaSw(codPlan, refPoliza, nifSocio);
		// -----------------------------------------------------
		//Llamamos al servicio
		try{
			response= srvSr.infoBasicaHojasCampo(paramsIn);//
		}catch(SOAPFaultException e){
			logger.error("########## - Error en SWSiniestrosInformacionHelper.getHojasCampoSW. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en SWSiniestrosInformacionHelper.getHojasCampoSW. ", e);
			throw e;	
		} catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de informacion basica del siniestro -- getHojasCampoSW. " , e);
			throw new Exception("Error inesperado al llamar al servicio web de informacion basica del siniestro -- getHojasCampoSW. " , e);
		}

		return response;

	}
	
	/**
	 * LLama al servicio de informacion de siniestros y devuelve la lista de actas de tasacion de la poliza
	 * @param codPlan
	 * @param refPoliza
	 * @param nifSocio
	 * @param realPath
	 * @return
	 * @throws Exception
	 */
	public InfoBasicaActasResponse getActasTasacionSW(Integer codPlan, String refPoliza, String nifSocio, 
			String realPath) throws  Exception {
		InfoBasicaActasResponse response=null;
		SiniestrosSCInformacion srvSr = getSrvSiniestroInformacion(realPath);
		//Recogemos los parametros de entrada a enviar al servicio
		ParametrosEntrada paramsIn =getParametrosEntradaSw(codPlan, refPoliza, nifSocio);
		// -----------------------------------------------------
		//Llamamos al servicio
		try{
			response= srvSr.infoBasicaActas(paramsIn);//
		}catch(SOAPFaultException e){
			logger.error("########## - Error en SWSiniestrosInformacionHelper.getActasTasacionSW. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en SWSiniestrosInformacionHelper.getActasTasacionSW. ", e);
			throw e;	
		} catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de informacion basica  del siniestro -- getActasTasacionSW. " , e);
			throw new Exception("Error inesperado al llamar al servicio web de informacion basica  del siniestro -- getActasTasacionSW. " , e);
		}
		return response;
	}
	
	
	private ParametrosEntrada getParametrosEntradaSw(Integer codPlan, 
			String refPoliza, String nifSocio)throws  Exception {
		ParametrosEntrada paramsIn = new ParametrosEntrada();
		if(null!=codPlan)paramsIn.setPlan(codPlan);
		if(null!=refPoliza)paramsIn.setReferencia(refPoliza);
		
		ObjectFactory obj = new ObjectFactory();
		JAXBElement<String> nifSocioJax = obj.createParametrosEntradaNif(nifSocio);
		if(null!=nifSocio)paramsIn.setNif(nifSocioJax);
		return paramsIn;
	}
	
	
	private SiniestrosSCInformacion getSrvSiniestroInformacion(String realPath) {
		if (!WSUtils.isProxyFixed()) WSUtils.setProxy();
		
        URL wsdlLocation = null;
        String url = realPath + System.getProperty("file.separator") + WSUtils.getBundleProp("siniestrosInformacionWS.wsdl");
		try {
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException e1) {
			throw new SWValidacionSiniestroException("Imposible recuperar el WSDL del servicion de informacion del siniestro. Revise la Ruta: " + url, e1);		
		}
		// Se recoge de webservice.properties los valores para el serviceName y Port
		String wsLocation = WSUtils.getBundleProp("siniestrosInformacionWS.location");
		String wsPort     = WSUtils.getBundleProp("siniestrosInformacionWS.port");
		String wsService  = WSUtils.getBundleProp("siniestrosInformacionWS.service");
		
		QName serviceName = new QName(wsLocation, wsService);
		QName portName = new QName(wsLocation, wsPort);
		
		// Crea el envoltorio para la llamada al servicio web de validacion del siniestro
		SiniestrosSCInformacion_Service srv =new SiniestrosSCInformacion_Service(wsdlLocation, serviceName);		
		
		SiniestrosSCInformacion srvSr = srv.getPort(portName, SiniestrosSCInformacion.class);
		logger.debug(srvSr.toString());
		
		// Anhade la cabecera de seguridad
		WSUtils.addSecurityHeader(srvSr);
				
		return srvSr;
	}
	
	/**
	 * 
	 * @param codPlan - obligatorio
	 * @param refPoliza - obligatorio
	 * @param numeroHojaCampo - obligatorio
	 * @param tipoHoja - obligatorio (Solo puede tomar los valores 1 para inmediatas y 2 para definitivas)
	 * @return
	 */
	public PdfHojaCampoResponse getPdfHojaCampoSW(Integer codPlan, String refPoliza, 
			Long numeroHojaCampo,Long tipoHoja, String realPath)throws  Exception {
		
		PdfHojaCampoResponse response= null;
		PdfHojaCampoRequest parametrosIn=null;
		try{
			SiniestrosSCInformacion srvSr = getSrvSiniestroInformacion(realPath);
			parametrosIn=new PdfHojaCampoRequest();
			//parametrosIn.setNumero(codPlan);
			parametrosIn.setReferencia(refPoliza);
			parametrosIn.setNumero(numeroHojaCampo.intValue());
			parametrosIn.setTipo(tipoHoja.toString());
			parametrosIn.setPlan(codPlan);
			
			response= srvSr.pdfHojaCampo(parametrosIn);//
		}catch(SOAPFaultException e){
			logger.error(" -- getPdfHojaCampoSW. " , e);
			throw e;
		}catch(AgrException e){
			logger.error("Error inesperado al llamar al servicio web de informacion basica  del siniestro -- getPdfHojaCampoSW. " , e);
			throw e;
		} catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de informacion basica  del siniestro -- getPdfHojaCampoSW. " , e);
			throw new Exception("Error inesperado al llamar al servicio web de informacion basica  del siniestro -- getPdfHojaCampoSW. " , e);
		}

		return response;
	}
	
	public PdfActaResponse getPdfActaTasacionSW(Long numero,Long serie, String realPath)throws  Exception {
		
		PdfActaResponse response= null;
		PdfActaRequest parametrosIn=null;
		try{
			SiniestrosSCInformacion srvSr = getSrvSiniestroInformacion(realPath);
			parametrosIn=new PdfActaRequest();
			parametrosIn.setNumero(numero.intValue());
			parametrosIn.setSerie(serie.intValue());
			
			response= srvSr.pdfActa(parametrosIn);//
		}catch(SOAPFaultException e){
			logger.error(" -- getPdfActaTasacionSW. " , e);
			throw e;
		}catch(AgrException e){
			logger.error("Error inesperado al llamar al servicio web de informacion basica  del siniestro -- getPdfActaTasacionSW. " , e);
			throw e;
		}catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de informacion basica  del siniestro -- getPdfActaTasacionSW. " , e);
			throw new Exception("Error inesperado al llamar al servicio web de informacion basica  del siniestro -- getPdfActaTasacionSW. " , e);
		}

		return response;
	}
	
	//Descarga del Parte
	public PdfParteSiniestroResponse getPdfParteSiniestroSW(Integer serieSiniestro, Integer numeroSiniestro, String realPath) throws  Exception {
		PdfParteSiniestroResponse response= null;
		
		SiniestrosSCInformacion srvSr = getSrvSiniestroInformacion(realPath);
		
		//Recogemos los parametros de entrada a enviar al servicio
		PdfParteSiniestroRequest parametrosIn= new PdfParteSiniestroRequest();
		parametrosIn.setNumero(numeroSiniestro);
		parametrosIn.setSerie(serieSiniestro);
		
		
		ParametrosAvisoSiniestros pAvisoSiniestros = new ParametrosAvisoSiniestros();
		pAvisoSiniestros.setNumeroEnvioSiniestro(numeroSiniestro);
		pAvisoSiniestros.setSerieEnvioSiniestro(serieSiniestro);
		
		
		
		// -----------------------------------------------------
		//Llamamos al servicio
		try{	
			
			response = srvSr.pdfParte(parametrosIn);//
		}catch(SOAPFaultException e){
			logger.error("########## - Error en SWSiniestrosInformacionHelper.getParteSiniestroSW. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en SWSiniestrosInformacionHelper.getParteSiniestroSW. ", e);
			throw e;	
		} catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de informacion basica del siniestro -- getParteSiniestroSW. " , e);
			throw new Exception("Error inesperado al llamar al servicio web de informacion basica del siniestro -- getParteSiniestroSW. " , e);
		}

		return response;
	}

	public InfoBasicaSiniestrosResponse getInfoBasicaSiniestros(Integer serieSiniestro, Integer numeroSiniestro,
			String realPath) throws Exception {
		InfoBasicaSiniestrosResponse response= null;
		
		SiniestrosSCInformacion srvSr = getSrvSiniestroInformacion(realPath);
		
		//Recogemos los parametros de entrada a enviar al servicio
		
		logger.info("llamamos al servicio infoBasicaSiniestros con numeroSiniestro:" + numeroSiniestro );
		logger.info("llamamos al servicio infoBasicaSiniestros con seriwe:" + serieSiniestro);
		
		ParametrosEntradaSiniestros pes = new ParametrosEntradaSiniestros();
		ParametrosAvisoSiniestros parametrosAvisoSiniestros = new ParametrosAvisoSiniestros();
		parametrosAvisoSiniestros.setNumeroEnvioSiniestro(numeroSiniestro);
		parametrosAvisoSiniestros.setSerieEnvioSiniestro(serieSiniestro);
		pes.setParametrosAvisoSiniestros(parametrosAvisoSiniestros);
		
		
		// -----------------------------------------------------
		//Llamamos al servicio
		try{	
			
			response = srvSr.infoBasicaSiniestros(pes);
		}catch(SOAPFaultException e){
			logger.error("########## - Error en SWSiniestrosInformacionHelper.infoBasicaSiniestros. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en SWSiniestrosInformacionHelper.infoBasicaSiniestros. ", e);
			throw e;	
		} catch (Exception e) {
			//Error inesperado
			logger.error("Error inesperado al llamar al servicio web de informacion basica del siniestro -- getInfoBasicaSiniestros. " , e);
			throw new Exception("Error inesperado al llamar al servicio web de informacion basica del siniestro -- getInfoBasicaSiniestros. " , e);
		}

		return response;
	}
		
	
	
}
