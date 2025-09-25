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
import com.rsi.agp.dao.tables.poliza.PolizasHojasCampo;
import com.rsi.agp.dao.tables.poliza.PolizasSWHojasCampo;

import es.agroseguro.serviciosweb.siniestrosscinformacion.AgrException;
import es.agroseguro.serviciosweb.siniestrosscinformacion.InfoBasicaHojasCampoResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfHojaCampoResponse;

public class HojasCampoManager implements IManager{
	
	private static final Log logger = LogFactory.getLog(HojasCampoManager.class);

	private IPolizaDao polizaDao;
	
	/**
	 * Devuelve la lista de hojas de campo de tipo del xsd
	 * @param codPlan
	 * @param refPoliza
	 * @param nifSocio
	 * @param realPath
	 * @param usuario
	 * @param xmlData 
	 * @return
	 * @throws Exception
	 */
	public es.agroseguro.seguroAgrario.informacionHojasCampo.HojasCampo 
			getHojasCampoXsd(Integer codPlan, String refPoliza, String nifSocio, 
				String realPath, Usuario usuario) throws Exception{
		logger.info("########## - HojasCampoManager.getHojasCampoXsd. ");
		es.agroseguro.seguroAgrario.informacionHojasCampo.HojasCampo hojasCampoXsd=null;
		
		try {
			//Llamamos al servicio
			SWSiniestrosInformacionHelper servicio = new SWSiniestrosInformacionHelper();
			InfoBasicaHojasCampoResponse response= servicio.getHojasCampoSW(codPlan, refPoliza, nifSocio, realPath);
			if(null!=response){
				//Hojas campo convertidas al tipo del xsd
				String xmlData = com.rsi.agp.core.util.WSUtils.getStringResponse(response.getHojasCampo());
				hojasCampoXsd = getListaHojasCampoDocument(xmlData);
				PolizasSWHojasCampo polizaSWhojasCampoHbm = getPolizaSWHojasCampo(hojasCampoXsd, usuario, xmlData);
				this.polizaDao.saveOrUpdate(polizaSWhojasCampoHbm);				
			}
		}catch(SOAPFaultException e){
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;	
		} catch (Exception e) {
			logger.error("########## - Error en HojasCampoManager.getHojasCampoXsd. ", e);
			throw e;
		}
		return hojasCampoXsd;
	}
	
	
	
	/**
	 * Método que devuelve el objeto mapeado de hibernate de nuestra tabla TB_POLIZAS_SW_HOJAS_CAMPO
	 * @param hojasCampoXsd
	 * @param usuario
	 * @param xmlData
	 * @return
	 * @throws Exception
	 */
	public PolizasSWHojasCampo getPolizaSWHojasCampo(es.agroseguro.seguroAgrario.informacionHojasCampo.HojasCampo hojasCampoXsd, 
			Usuario usuario, String xmlData) throws Exception{
		logger.info("########## - HojasCampoManager.getPolizaSWHojasCampo. ");
		
		PolizasSWHojasCampo poliza = new PolizasSWHojasCampo();
		
		if(null!=hojasCampoXsd.getNif())poliza.setNif(hojasCampoXsd.getNif());
		if(hojasCampoXsd.getPlan()!=0)poliza.setPlan(new Long(hojasCampoXsd.getPlan()));
		if(null!=hojasCampoXsd.getReferencia()){
			poliza.setReferencia(hojasCampoXsd.getReferencia());
			com.rsi.agp.dao.tables.poliza.Poliza polizaBD= polizaDao.getPolizaByReferencia
					(hojasCampoXsd.getReferencia(), Constants.MODULO_POLIZA_PRINCIPAL);
			poliza.setPoliza(polizaBD);
		}
		poliza.setFecha(new Date());
		poliza.setRespuesta(Hibernate.createClob(xmlData));
		poliza.setUsuario(usuario.getCodusuario());
		
		return poliza;
	} 
	
	public PolizasHojasCampo getPolizaHojasCampoHbm(
			es.agroseguro.seguroAgrario.informacionHojasCampo.HojasCampo hojasCampoXsd)throws Exception{
		
		PolizasHojasCampo polizaHojasCampo = new PolizasHojasCampo();
		logger.info("########## - HojasCampoManager.getPolizaHojasCampoHbm. ");
		if(hojasCampoXsd.getPlan()!=0)polizaHojasCampo.setPlan(new Long(hojasCampoXsd.getPlan()));
		if(null!=hojasCampoXsd.getReferencia()){
			polizaHojasCampo.setReferencia(hojasCampoXsd.getReferencia());
			com.rsi.agp.dao.tables.poliza.Poliza polizaBD= polizaDao.getPolizaByReferencia
					(hojasCampoXsd.getReferencia(), Constants.MODULO_POLIZA_PRINCIPAL);
			polizaHojasCampo.setPoliza(polizaBD);
		}
		
		if(null!=hojasCampoXsd.getHojaCampoArray() && hojasCampoXsd.getHojaCampoArray().length>0){
			for (int i = 0; i < hojasCampoXsd.getHojaCampoArray().length; i++) {
				es.agroseguro.seguroAgrario.informacionHojasCampo.InformacionHojaCampo infHojaCampoXsd = 
						hojasCampoXsd.getHojaCampoArray()[i];
				com.rsi.agp.dao.tables.poliza.PolizasInfoHojasCampo infoHojaCampo=getInfoHojaCampo(infHojaCampoXsd,polizaHojasCampo);
				polizaHojasCampo.getPolizasInfoHojasCampos().add(infoHojaCampo);				
			}
		} 
		
		return polizaHojasCampo;
	}
	
	/**
	 * Método que devuelve una lista de HojaCampo del tipo del servicio 
	 * @param xmlData
	 * @return
	 * @throws XmlException
	 * @throws IOException
	 */
	private es.agroseguro.seguroAgrario.informacionHojasCampo.HojasCampo getListaHojasCampoDocument(String xmlData) 
			throws XmlException, IOException{
		logger.info("########## - HojasCampoManager.getListaHojasCampoDocument. " );
		
		es.agroseguro.seguroAgrario.informacionHojasCampo.HojasCampo hojasCampo=null;
		es.agroseguro.seguroAgrario.informacionHojasCampo.ListaHojasCampoDocument lista=
				es.agroseguro.seguroAgrario.informacionHojasCampo.ListaHojasCampoDocument.Factory.parse(new StringReader(xmlData) );
		if(null!=lista)hojasCampo=lista.getListaHojasCampo();
		
		return hojasCampo;
	}	
	
	private com.rsi.agp.dao.tables.poliza.PolizasInfoHojasCampo getInfoHojaCampo(
			es.agroseguro.seguroAgrario.informacionHojasCampo.InformacionHojaCampo infHojaCampoXsd,
			PolizasHojasCampo polizaHojasCampo)throws Exception{
		
		logger.info("########## - HojasCampoManager.getInfoHojaCampo ");
		
		com.rsi.agp.dao.tables.poliza.PolizasInfoHojasCampo infoHojaCampoHbm= new com.rsi.agp.dao.tables.poliza.PolizasInfoHojasCampo();
		
		if(null!=infHojaCampoXsd.getFechaTasacion())infoHojaCampoHbm.setFechatasacion(infHojaCampoXsd.getFechaTasacion().getTime());
		infoHojaCampoHbm.setNumerohojacampo(new Long(infHojaCampoXsd.getNumeroHojaCampo()));
		if(null!=infHojaCampoXsd.getSituacion() && !infHojaCampoXsd.getSituacion().trim().isEmpty())
			infoHojaCampoHbm.setSituacion(new Long(infHojaCampoXsd.getSituacion()));
		if(null!=infHojaCampoXsd.getSituacionDescriptivo())infoHojaCampoHbm.setSituaciondesc(infHojaCampoXsd.getSituacionDescriptivo());
		if(null!=infHojaCampoXsd.getTipoHoja()&& !infHojaCampoXsd.getTipoHoja().trim().isEmpty())
			infoHojaCampoHbm.setTipohoja(new Long(infHojaCampoXsd.getTipoHoja()));
		if(null!=infHojaCampoXsd.getTipoHojaDescriptivo())infoHojaCampoHbm.setTipohojadesc(infHojaCampoXsd.getTipoHojaDescriptivo());
			
		infoHojaCampoHbm.setPolizasHojasCampo(polizaHojasCampo);
		
		return infoHojaCampoHbm;
	}
	
	public byte[] getPdfHojaCampo(Integer codPlan,String refPoliza,Long numeroHojaCampo,Long tipoHoja,String realPath) throws Exception{
		byte[] byteArray=null;
		try {
			//Llamamos al servicio
			SWSiniestrosInformacionHelper servicio = new SWSiniestrosInformacionHelper();
			PdfHojaCampoResponse response= servicio.getPdfHojaCampoSW(codPlan, refPoliza, numeroHojaCampo, tipoHoja, realPath);
			
			if (response != null && null!=response.getDocumento()) {
				Base64Binary pdfBytes = response.getDocumento();
				byteArray = pdfBytes.getValue();
			}	
		
		}catch(SOAPFaultException e){
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;	
		} catch (Exception e) {
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;
		}
		return byteArray;				
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
}