package com.rsi.agp.core.managers.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Hibernate;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.PolizasActas;
import com.rsi.agp.dao.tables.poliza.PolizasSWActas;

import es.agroseguro.serviciosweb.siniestrosscinformacion.AgrException;
import es.agroseguro.serviciosweb.siniestrosscinformacion.InfoBasicaActasResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfActaResponse;

public class ActasTasacionManager implements IManager{
	
	private static final Log logger = LogFactory.getLog(HojasCampoManager.class);

	private IPolizaDao polizaDao;
	
	public es.agroseguro.seguroAgrario.informacionActas.Actas getActasTasacionXsd(Integer codPlan, 
			String refPoliza, String nifSocio,String realPath, Usuario usuario) throws Exception{
		logger.info("########## - ActasTasacionManager.getActasTasacionXsd. ");
		es.agroseguro.seguroAgrario.informacionActas.Actas actasXsd=null;
		
		try {
			//Llamamos al servicio
			SWSiniestrosInformacionHelper servicio = new SWSiniestrosInformacionHelper();
			InfoBasicaActasResponse response= servicio.getActasTasacionSW(codPlan, refPoliza, nifSocio, realPath);
			if(null!=response){
				//Hojas campo convertidas al tipo del xsd
				String xmlData = com.rsi.agp.core.util.WSUtils.getStringResponse(response.getActas());
				actasXsd = getListaActasTasacionDocument(xmlData);
				PolizasSWActas polizaSWactasHbm = getPolizaSWActas(actasXsd, usuario, xmlData);
				this.polizaDao.saveOrUpdate(polizaSWactasHbm);				
			}
		}catch(SOAPFaultException e){
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;	
		} catch (Exception e) {
			logger.error("########## - Error en ActasTasacionManager.getActasTasacionXsd. ", e);
			throw e;
		}
		
		return actasXsd;
	}
	
	public PolizasSWActas getPolizaSWActas(es.agroseguro.seguroAgrario.informacionActas.Actas actasXsd, 
			Usuario usuario, String xmlData) throws Exception{
		logger.info("########## - ActasTasacionManager.getPolizaSWActas. ");
		PolizasSWActas poliza= new PolizasSWActas();
		if(null!=actasXsd.getNif())poliza.setNif(actasXsd.getNif());
		if(actasXsd.getPlan()!=0)poliza.setPlan(new Long(actasXsd.getPlan()));
		if(null!=actasXsd.getReferencia()){
			poliza.setReferencia(actasXsd.getReferencia());
			com.rsi.agp.dao.tables.poliza.Poliza polizaBD= polizaDao.getPolizaByReferencia
			(actasXsd.getReferencia(), Constants.MODULO_POLIZA_PRINCIPAL);
			poliza.setPoliza(polizaBD);
		}
		
		poliza.setFecha(new Date());
		poliza.setRespuesta(Hibernate.createClob(xmlData));
		poliza.setUsuario(usuario.getCodusuario());
	
		return poliza;
	}
	
	public PolizasActas getPolizaActasTasacionHbm(
			es.agroseguro.seguroAgrario.informacionActas.Actas actasXsd)throws Exception{
		
		PolizasActas polizaActa = new PolizasActas();
		logger.info("########## - ActasTasacionManager.getPolizaActasTasacionHbm. ");
		
		if(actasXsd.getPlan()!=0)polizaActa.setPlan(new Long(actasXsd.getPlan()));
		if(null!=actasXsd.getReferencia()){
			polizaActa.setReferencia(actasXsd.getReferencia());
			com.rsi.agp.dao.tables.poliza.Poliza polizaBD= polizaDao.getPolizaByReferencia
					(actasXsd.getReferencia(), Constants.MODULO_POLIZA_PRINCIPAL);
			polizaActa.setPoliza(polizaBD);
		}			
		if(null!=actasXsd.getNif())polizaActa.setNif(actasXsd.getNif());
		
		if(null!=actasXsd.getActaArray()&& actasXsd.getActaArray().length>0){
			for (int i = 0; i < actasXsd.getActaArray().length; i++) {
				es.agroseguro.seguroAgrario.informacionActas.InformacionActa infoActaXsd=actasXsd.getActaArray()[i];
				com.rsi.agp.dao.tables.poliza.PolizasInfoActas infoActaHbm=getInfoActa(infoActaXsd, polizaActa);
				polizaActa.getPolizasInfoActases().add(infoActaHbm);
			}
		}
		
		return polizaActa;
	}
	
	private com.rsi.agp.dao.tables.poliza.PolizasInfoActas getInfoActa(
			es.agroseguro.seguroAgrario.informacionActas.InformacionActa infoActaXsd,
			PolizasActas polizaActa)throws Exception{
		logger.info("########## - ActasTasacionManager.getInfoActa ");
		com.rsi.agp.dao.tables.poliza.PolizasInfoActas infoActaHbm =new com.rsi.agp.dao.tables.poliza.PolizasInfoActas();
		
		if(null!=infoActaXsd.getFechaActa() && null!=infoActaXsd.getFechaActa().getTime())
			infoActaHbm.setFechaacta(infoActaXsd.getFechaActa().getTime());
			
		if(null!=infoActaXsd.getFechaPago() && null!=infoActaXsd.getFechaPago().getTime())
			infoActaHbm.setFechapago(infoActaXsd.getFechaPago().getTime());
			
		if(null!=infoActaXsd.getImporte())
			infoActaHbm.setImporte(infoActaXsd.getImporte());
			
		if(null!=infoActaXsd.getImporteADevolverPorAsegurado())
			infoActaHbm.setImportedevaseg(infoActaXsd.getImporteADevolverPorAsegurado());
		
		infoActaHbm.setNumeroacta(new Long(infoActaXsd.getNumeroActa()));
		
		infoActaHbm.setSerie(new Long(infoActaXsd.getSerie()));
		
		if(null!=infoActaXsd.getSituacion()&& !infoActaXsd.getSituacion().trim().isEmpty())
			infoActaHbm.setSituacion(new Long(infoActaXsd.getSituacion()));
		
		if(null!=infoActaXsd.getSituacionDescriptivo())
			infoActaHbm.setSituaciondesc(infoActaXsd.getSituacionDescriptivo());
		
		return infoActaHbm;
	}
		
	private es.agroseguro.seguroAgrario.informacionActas.Actas getListaActasTasacionDocument(String xmlData) 
			throws XmlException, IOException{
		logger.info("########## - ActasTasacionManager.getListaActasTasacionDocument. " );
		
		es.agroseguro.seguroAgrario.informacionActas.Actas actas=null;
		es.agroseguro.seguroAgrario.informacionActas.ListaActasDocument lista= 
				es.agroseguro.seguroAgrario.informacionActas.ListaActasDocument.Factory.parse(new StringReader(xmlData));
		if(null!=lista)actas=lista.getListaActas();
		return actas;
	}	
	
	public byte[] getPdfActaTasacion(Long numero, Long serie, String realPath) throws Exception{
		byte[] byteArray=null;
		try {
			//Llamamos al servicio
			SWSiniestrosInformacionHelper servicio = new SWSiniestrosInformacionHelper();
			PdfActaResponse response= servicio.getPdfActaTasacionSW(numero, serie, realPath);
			
			if (response != null && null!=response.getDocumento()) {
				Base64Binary pdfBytes = response.getDocumento();
				byteArray = pdfBytes.getValue();
			}	
		}catch(SOAPFaultException e){
			logger.error(" -- getPdfActaTasacion. " , e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en ActasTasacionManager.getPdfActaTasacion. ", e);
			throw e;	
		} catch (Exception e) {
			logger.error("########## - Error en ActasTasacionManager.getPdfActaTasacion. ", e);
			throw e;
		}
		return byteArray;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
}