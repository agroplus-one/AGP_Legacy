package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.PolizasActas;
import com.rsi.agp.dao.tables.poliza.PolizasHojasCampo;
import com.rsi.agp.dao.tables.poliza.PolizasInfoActas;
import com.rsi.agp.dao.tables.poliza.PolizasInfoHojasCampo;
import com.rsi.agp.dao.tables.siniestro.Siniestro;

import es.agroseguro.seguroAgrario.informacionSiniestros.InformacionSiniestro;
import es.agroseguro.seguroAgrario.informacionSiniestros.ListaSiniestrosDocument;
import es.agroseguro.serviciosweb.siniestrosscinformacion.AgrException;
import es.agroseguro.serviciosweb.siniestrosscinformacion.InfoBasicaSiniestrosResponse;
import es.agroseguro.serviciosweb.siniestrosscinformacion.PdfParteSiniestroResponse;

@SuppressWarnings("rawtypes")
public class SiniestrosSCInformacionManager {
	
	private static final Log logger = LogFactory.getLog(SiniestrosSCInformacionManager.class);
	
	private HojasCampoManager hojasCampoManager;
	private ActasTasacionManager actasTasacionManager;
	private SiniestrosManager siniestrosManager;	
	private GenericDao baseDaoHibernate;
	
	// ****************************************** HOJAS DE CAMPO ****************************************
	public void procesoActualizacionHojasCampo(Integer codPlan, String refPoliza, String nifSocio, String realPath, Usuario usuario) throws Exception{
		logger.info("########## - SiniestrosSCInformacionManager.procesoActualizacionHojasCampo ");
		try {
			es.agroseguro.seguroAgrario.informacionHojasCampo.HojasCampo hojasCampoXsd=					
					hojasCampoManager.getHojasCampoXsd(codPlan, refPoliza, nifSocio, realPath, usuario);
			
			if(null!=hojasCampoXsd && hojasCampoXsd.sizeOfHojaCampoArray()>0){
				logger.info("########## - SiniestrosSCInformacionManager.procesoActualizacionHojasCampo Hojas de campo xsd no nulo");
				//convertimos respuesta en PolizasSWHojasCampo 
								
				//Convertimos PolizasSWHojasCampo en PolizasHojasCampo 
				PolizasHojasCampo polizaHojasCampoHbm = hojasCampoManager.getPolizaHojasCampoHbm(hojasCampoXsd);
				
				//Acciones en Base de datos
				deletePolizasHojasCampoHbm(polizaHojasCampoHbm.getReferencia());
				logger.info("########## - SiniestrosSCInformacionManager.procesoActualizacionHojasCampo -- polizaHojasCampoHbm.getPoliza().getIdpoliza():"
						+polizaHojasCampoHbm.getPoliza().getIdpoliza());
				savePolizasHojasCampoHbm(polizaHojasCampoHbm);
			}	
		}catch(SOAPFaultException e){
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en HojasCampoManager.getHojaCampo. ", e);
			throw e;	
		} catch (Exception e) {
			logger.error("########## - Error en SiniestrosSCInformacionManager.procesoActualizacionHojasCampo. ", e);
			throw e;
		}
	}
	
	private void deletePolizasHojasCampoHbm(String referencia) throws DAOException{
		logger.info("########## - SiniestrosSCInformacionManager.deletePolizasHojasCampo - " +
				"Borramos registros de PolizasHojasCampo en BBDD. Referencia " 
				+ referencia);
		PolizasHojasCampo registroABorrar=(PolizasHojasCampo)
				baseDaoHibernate.getObject(PolizasHojasCampo.class, "referencia",
						referencia);
		if(null!=registroABorrar){
			baseDaoHibernate.delete(registroABorrar);
			logger.info("########## - SiniestrosSCInformacionManager.deletePolizasHojasCampo - Registros borrados");
		}else{
			logger.info("########## - SiniestrosSCInformacionManager.deletePolizasHojasCampo - No existe el registro");
		}		
	}
	
	private void savePolizasHojasCampoHbm(PolizasHojasCampo polizaHojasCampoHbm) throws DAOException{
		logger.info("########## - SiniestrosSCInformacionManager.savePolizasHojasCampo - Guardamos PolizasHojasCampo en BBDD ");
		baseDaoHibernate.saveOrUpdate(polizaHojasCampoHbm);
		if(null!=polizaHojasCampoHbm.getId()&& null!=polizaHojasCampoHbm.getPolizasInfoHojasCampos()
				&& polizaHojasCampoHbm.getPolizasInfoHojasCampos().size()>0){
		
			for (Iterator<PolizasInfoHojasCampo> iter = polizaHojasCampoHbm.getPolizasInfoHojasCampos().iterator(); iter.hasNext();) {
				PolizasInfoHojasCampo infoHojasCampo= (PolizasInfoHojasCampo) iter.next();
				baseDaoHibernate.saveOrUpdate(infoHojasCampo);				
			}
		}
		
	}
	
	// ******************************************FIN  HOJAS DE CAMPO ****************************************
	
	// ***************************************** ACTAS DE TASACIÓN  ****************************************
	
	public void procesoActualizacionActasTasacion(Integer codPlan, String refPoliza, String nifSocio, String realPath, Usuario usuario) throws Exception{
		logger.info("########## - SiniestrosSCInformacionManager.procesoActualizacionActasTasacion ");
		try {
			es.agroseguro.seguroAgrario.informacionActas.Actas actasXsd=
					actasTasacionManager.getActasTasacionXsd(codPlan, refPoliza, nifSocio, realPath, usuario);
			if(null!=actasXsd && actasXsd.sizeOfActaArray()>0){
				logger.info("########## - SiniestrosSCInformacionManager.procesoActualizacionActasTasacion -- Actas xsd no nulo");
				//convertimos respuesta en PolizasSWHojasCampo 
								
				//Convertimos PolizasSWHojasCampo en PolizasHojasCampo 
				PolizasActas polizaActaHbm = actasTasacionManager.getPolizaActasTasacionHbm(actasXsd);
				
				//Acciones en Base de datos
				logger.info("########## - SiniestrosSCInformacionManager.procesoActualizacionActasTasacion -- polizaActaHbm.getPoliza().getIdpoliza():"
						+polizaActaHbm.getPoliza().getIdpoliza());
				deletePolizaActaHbm(polizaActaHbm.getReferencia());
				savePolizasActaHbm(polizaActaHbm);
			}
		} catch (Exception e) {
			logger.error("########## - Error en SiniestrosSCInformacionManager.procesoActualizacionActasTasacion. ", e);
			throw e;
		}
	}
	
	private void deletePolizaActaHbm(String referencia ) throws DAOException{
		logger.info("########## - SiniestrosSCInformacionManager.deletePolizaActaHbm - " +
				"Borramos registros de PolizasHojasCampo en BBDD. Referencia " 
				+ referencia);
		PolizasActas registroABorrar=(PolizasActas)
				baseDaoHibernate.getObject(PolizasActas.class, "referencia", referencia);
		
		if(null!=registroABorrar){
			baseDaoHibernate.delete(registroABorrar);
			logger.info("########## - SiniestrosSCInformacionManager.deletePolizaActaHbm - Registros borrados");
		}else{
			logger.info("########## - SiniestrosSCInformacionManager.deletePolizaActaHbm - El registro no existe en base de datos");
		}
		
		
	}
	
	private void savePolizasActaHbm(PolizasActas polizaActaHbm) throws DAOException{
		logger.info("########## - SiniestrosSCInformacionManager.savePolizasActaHbm - Guardamos PolizasActa en BBDD ");
		baseDaoHibernate.saveOrUpdate(polizaActaHbm);
		
		if(null!=polizaActaHbm.getId()&& null!=polizaActaHbm.getPolizasInfoActases()
				&& polizaActaHbm.getPolizasInfoActases().size()>0){
		
			for (Iterator<PolizasInfoActas> iter = polizaActaHbm.getPolizasInfoActases().iterator(); iter.hasNext();) {
				PolizasInfoActas infoActas= (PolizasInfoActas) iter.next();
				infoActas.setPolizasActas(polizaActaHbm);
				baseDaoHibernate.saveOrUpdate(infoActas);				
			}
		}
	}

	
	// ***************************************** FIN ACTAS DE TASACIÓN  ****************************************
	
	
	
	public byte[] getPdfHojaCampo(Integer codPlan,String refPoliza,
			Long numeroHojaCampo,Long tipoHoja, String realPath ) throws Exception{
		
		byte[] pdf = (byte[]) hojasCampoManager.getPdfHojaCampo(codPlan, refPoliza, numeroHojaCampo, tipoHoja, realPath);
		return pdf;
	}
	
	public byte[] getPdfActaTasacion(Long numero, Long serie, String realPath ) throws Exception{
		
		byte[] pdf = (byte[]) actasTasacionManager.getPdfActaTasacion(numero, serie, realPath);
		return pdf;
	}
	public void guardarSiniestro(Siniestro sini, Usuario user, String xmlDatSini) throws DAOException{
		logger.debug("Guardamos la comunicacion con el servicio InformacionSiniestros");
		siniestrosManager.guardarInfoBasicaSiniestro(sini, user, xmlDatSini);
	}
	
	public byte[] getPdfParte(Integer serie, Integer numSiniestro, String realPath, Usuario usuario, Long idSiniestro, Integer numeroSiniestro) throws Exception{
		byte[] pdf = null;//(byte[]) actasTasacionManager.getPdfActaTasacion(numero, serie, realPath);
		try {
			Integer serieSiniestro = null;
			//Integer numeroSiniestro = null;
			SWSiniestrosInformacionHelper servicio = new SWSiniestrosInformacionHelper();
			// llamamos al servicio infoBasicaSiniestros
			InfoBasicaSiniestrosResponse response1 = servicio.getInfoBasicaSiniestros(serie, numSiniestro, realPath);
			// guardamos la llamada en bbdd
			Siniestro siniestro = new Siniestro();
			siniestro.setId(idSiniestro);
			
			byte[] byteArraySiniestro =response1.getInformacionSiniestros().getValue();
			String xmlDataSiniestro = new String (byteArraySiniestro, Constants.DEFAULT_ENCODING);
			logger.debug("xml de respuesta getInfoBasicaSiniestros: " + xmlDataSiniestro);
			
			logger.debug("Guardamos la comunicacion con el servicio InformacionSiniestros");
			siniestrosManager.guardarInfoBasicaSiniestro(siniestro, usuario,xmlDataSiniestro);
			
			
			ListaSiniestrosDocument listSiniestros = ListaSiniestrosDocument.Factory.parse(new StringReader(xmlDataSiniestro));
			InformacionSiniestro[] infoSini = listSiniestros.getListaSiniestros().getSiniestroArray();
			
			/*DNF 28/09/2018 Si viene de listadoSiniestro.jsp el valor será cero, si viene de declaracionSiniestro traera 
			  el codigo que necesitamos para pintar el popupInformacionSiniestros*/
			if(numeroSiniestro == 0){
				if (infoSini[0] != null){
					numeroSiniestro = infoSini[0].getNumeroSiniestro();
					serieSiniestro = infoSini[0].getSerie();
				}else{
					logger.error("Error al recuperar la InfoBasica del siniestro");
					throw new Exception ("Error al recuperar la InfoBasica del siniestro");
				}
			}else{
				if (infoSini != null){
					
					for(int x = 0 ; x < infoSini.length ; x++){
						if (infoSini[x] != null && infoSini[x].getNumeroSiniestro() == numeroSiniestro){
							serieSiniestro = infoSini[x].getSerie();
						}else{
							if(infoSini[x] == null){
								logger.error("Error al recuperar la InfoBasica del siniestro");
								throw new Exception ("Error al recuperar la InfoBasica del siniestro");
							}
						}
					}
				}	
			}
			
			
			
			
			
			logger.debug("llamamos al servicio getPdfParteSiniestroSW con numeroSiniestro:" +numeroSiniestro );
			logger.debug("llamamos al servicio getPdfParteSiniestroSW con seriwe:" +serieSiniestro);
			
			//Llamamos al servicio getPdfParteSiniestroSW
			PdfParteSiniestroResponse response= servicio.getPdfParteSiniestroSW(serieSiniestro, numeroSiniestro, realPath);
			
			if (response != null && null!=response.getDocumento()) {
				Base64Binary pdfBytes = response.getDocumento();
				pdf = pdfBytes.getValue();
			}	
		}catch(SOAPFaultException e){
			logger.error(" -- getPdfActaTasacion. " , e);
			throw e;
		}catch(AgrException e){
			logger.error("########## - Error en SiniestrosSCInformacionManager.getPdfParte. ", e);
			logger.error("[ESC-25843] Usuario llamante: " + usuario.getCodusuario());
			logger.error("[ESC-25843] Datos de entrada: serie --> " + serie + ", numSiniestro --> " + numSiniestro);
			throw e;	
		} catch (Exception e) {
			logger.error("########## - Error en SiniestrosSCInformacionManager.getPdfParte. ", e);
			throw e;
		}
		return pdf;
	}
	
	public void setHojasCampoManager(HojasCampoManager hojasCampoManager) {
		this.hojasCampoManager = hojasCampoManager;
	}

	public void setActasTasacionManager(ActasTasacionManager actasTasacionManager) {
		this.actasTasacionManager = actasTasacionManager;
	}

	public void setBaseDaoHibernate(GenericDao baseDaoHibernate) {
		this.baseDaoHibernate = baseDaoHibernate;
	}

	public void setSiniestrosManager(SiniestrosManager siniestrosManager) {
		this.siniestrosManager = siniestrosManager;
	}

	public SiniestrosManager getSiniestrosManager(){
		return siniestrosManager;
	}
}