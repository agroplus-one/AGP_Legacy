package com.rsi.agp.core.managers.impl.ganado;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ICargaExplotacionesManager;
import com.rsi.agp.core.managers.impl.ContratacionRenovacionesHelper;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.managers.impl.poliza.util.PolizaUtils;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.poliza.ganado.ICargaExplotacionesDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;
import com.rsi.agp.dao.tables.poliza.explotaciones.SWCargaExpPlzTradicional;

import es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException;
import es.agroseguro.serviciosweb.contratacionrenovaciones.RecuperacionExplotacionesResponse;


public class CargaExplotacionesManager implements ICargaExplotacionesManager {
	private static final Log logger = LogFactory.getLog(CargaExplotacionesManager.class);
	ICargaExplotacionesDao cargaExplotacionesDao;
	ILineaDao lineaDao;
	IDiccionarioDatosDao diccionarioDatosDao;
	
	
	/**
	 * actualiza el campo ID_CARGA_EXPLOTACIONES del registro correspondiente de TB_POLIZAS
	 * @throws Exception 
	 */
	public void actualizarIdCargaExplotaciones(Long idpoliza, Integer idCargaExplotaciones) throws Exception{
		try {
			cargaExplotacionesDao.actualizarIdCargaExplotaciones(idpoliza, idCargaExplotaciones);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 *Realiza una llamada a ‘lineaDao.existeLinea’ para que lance una consulta sobre la tabla de 
	 *líneas con la línea indicada y el plan indicado menos uno para ver si existe registro 	
	 */
	public Boolean isPolizaAnteriorSistemaTradicional(BigDecimal plan, BigDecimal linea){

		BigDecimal planAnterior=plan.subtract(new BigDecimal(1));
		
		if ( !lineaDao.existeLinea(planAnterior, linea) ){ // si no existe la linea return true
			return true;
		}
		return false;
	}
	
	/**
	 * Realiza una llamada a ‘CargaExplotacionesDao’ (indicando ‘tipoFiltro=0’) para que lance una consulta 
	 * sobre la tabla de pólizas para comprobar si existen registros para el asegurado, plan anterior y línea, 
	 * además de los estados ‘Enviada correcta’ o ‘Emitida’
	 * @throws Exception 
	 */
	public IsRes isSituacionActualizadaAgroseguro(Long idAsegurado, BigDecimal plan,
			BigDecimal linea,Long idpoliza) throws Exception{
		IsRes res=null;
		try {
			List<BigDecimal>listaPlanes= new ArrayList<BigDecimal>();
			listaPlanes.add(plan.subtract(new BigDecimal(1)));
			res = isTipoPolizaCargaExplotaciones(0, idAsegurado, listaPlanes, linea,idpoliza);			
		} catch (Exception e) {
			throw e;
		}
		return res;
						
	}
	
	/**
	 *Realiza una llamada a ‘CargaExplotacionesDao’ (indicando ‘tipoFiltro=1’) para que lance una consulta 
	 *sobre la tabla de pólizas para comprobar si existen registros para el asegurado, plan anterior y línea, 
	 *además de los estados ‘Enviada correcta’ o ‘Emitida’ 
	 * @throws Exception 
	 */
	public  IsRes isPolizaOriginalUltimosPlanes(Long idAsegurado, BigDecimal plan,
			BigDecimal linea, Long idpoliza) throws Exception{
		IsRes res=null;
		
		try {
			List<BigDecimal>listaPlanes= new ArrayList<BigDecimal>();
			listaPlanes.add(plan.subtract(new BigDecimal(1)));
			listaPlanes.add(plan.subtract(new BigDecimal(2)));
			listaPlanes.add(plan.subtract(new BigDecimal(3)));
			res = isTipoPolizaCargaExplotaciones(1, idAsegurado, listaPlanes, linea,idpoliza);
			
		} catch (Exception e) {
			throw e;
		}
		return res;				
	}

	/**
	 *Realiza una llamada a CargaExplotacionesDao�� (indicando ��tipoFiltro=2) para que lance una consulta 
	 *sobre la tabla de polizas para comprobar si existen registros para el asegurado, plan ACTUAL y linea, 
	 *ademas de los estados Enviada correcta o ��Emitida
	 * @throws Exception 
	 */
	public IsRes isPolizaPlanActual(Long idAsegurado, BigDecimal plan,
			BigDecimal linea,Long idpoliza) throws  Exception{		
		IsRes res=null;
		try {
			List<BigDecimal>listaPlanes= new ArrayList<BigDecimal>();
			listaPlanes.add(plan);
			res = isTipoPolizaCargaExplotaciones(2, idAsegurado, listaPlanes, linea,idpoliza);
		} catch (Exception e) {
			throw e;
		}
		return res;
		
	}
	

	private IsRes isTipoPolizaCargaExplotaciones(int tipoFiltro, Long idAsegurado,
			List<BigDecimal>listaPlanes, BigDecimal linea, Long idpoliza) throws Exception{
		Boolean isSitAct =false;
		Long idPoliza=null;
		IsRes res = null;
		List<BigDecimal> idsPolizas=null;
		try {
			idsPolizas= cargaExplotacionesDao.getIdsPolizas(tipoFiltro, idAsegurado, listaPlanes, linea, idpoliza);
			if(idsPolizas!=null && idsPolizas.size()>0){
				isSitAct =true;
				if(idsPolizas.size()==1){
					idPoliza=(Long)idsPolizas.get(0).longValue();
					logger.debug(idPoliza.toString());
				}
			}						
		} catch (DAOException e) {
			logger.error("CargaExplotacionesManager.isTipoPolizaCargaExplotaciones - ", e);
			throw e;
		} catch (Exception e) {
			logger.error("CargaExplotacionesManager.isTipoPolizaCargaExplotaciones - ", e);
			throw e;
		}finally{
			res = new IsRes(isSitAct, idPoliza);			
		}		
		return res;
	}
	
	
	
	public String cargaPolizaSistemaTradicional(BigDecimal plan, String referencia, String realPath,
			Long idPoliza, Usuario usuario, Long lineaseguroid, Poliza poliza) throws SOAPFaultException,AgrException, Exception{
		logger.info("########## - CargaExplotacionesManager.cargaPolizaSistemaTradicional. ");
		String xmlData=null;
		String mensajeError=null;
		es.agroseguro.contratacion.PolizaDocument polizaDocument=null;
		String moduloSW=null;
		try {
			ContratacionRenovacionesHelper servicio= new ContratacionRenovacionesHelper();
			RecuperacionExplotacionesResponse  response= servicio.recuperarExplotaciones(plan, referencia, realPath);
			if(null!=response){
				
				xmlData = com.rsi.agp.core.util.WSUtils.getStringResponse(response.getDocumento());
				polizaDocument= getPolizaDocument(xmlData);
				if(!validaRefColectivo(polizaDocument, poliza)){
					logger.error("########## - Error en CargaExplotacionesManager.cargaPolizaSistemaTradicional. Referencia del colectivo no encontrada.");
					throw new Exception();
				}
			}

			List<Explotacion> listaExplotaciones= this.getListaExplotaciones(lineaseguroid, idPoliza, 
					polizaDocument.getPoliza(), poliza.getModuloPolizas());
			if(null!=polizaDocument.getPoliza().getCobertura())
				moduloSW=polizaDocument.getPoliza().getCobertura().getModulo();
			this.asociarExplotacionesPoliza(moduloSW, listaExplotaciones, poliza);		
			
		}catch(SOAPFaultException e){
			logger.error("########## - Error en CargaExplotacionesManager.cargaPolizaSistemaTradicional. ", e);
			mensajeError= WSUtils.debugAgrException(e);	
		}catch(AgrException e){
			logger.error("########## - Error en HojasCampoManager.cargaPolizaSistemaTradicional. ", e);
			mensajeError= WSUtils.debugAgrException(e);	
		} catch (Exception e) {
			logger.error("########## - Error en CargaExplotacionesManager.cargaPolizaSistemaTradicional. ", e);
			throw e;
		}finally{
			//Creamos el objeto que registra la llamada al servicio y su respuesta y lo guardamos en base de datos
			saveSWCargaExpPlzTradicional(polizaDocument.getPoliza(), usuario, xmlData, idPoliza, plan, referencia);			
		}
		return mensajeError;
	}
	
	
	private Boolean validaRefColectivo(es.agroseguro.contratacion.PolizaDocument polizaDocument, Poliza poliza){
		Boolean res=true;
		try {
			String ref =polizaDocument.getPoliza().getColectivo().getReferencia().trim();
			Integer dc=polizaDocument.getPoliza().getColectivo().getDigitoControl();
			Integer referenciaDc= new Integer(ref + dc);
			
			Object[] campos= cargaExplotacionesDao.getMediadoraColectivoGanadoweb(referenciaDc);
			if(campos!=null){
				BigDecimal ent =((BigDecimal) campos[0]);
				BigDecimal subEnt=((BigDecimal) campos[1]);
				
				BigDecimal codEntMed = poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad();
				BigDecimal codSubEntMed = poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad();
				res=(ent.compareTo(codEntMed)==0 && subEnt.compareTo(codSubEntMed)==0);
			
			}else{
				res=false;
			}			
			
		} catch (NumberFormatException nfe){
			res=false;//Valor no numérico
		} catch (Exception e) {
			res=false;
		}
		return res;
	}
	
	private es.agroseguro.contratacion.PolizaDocument getPolizaDocument(String xmlData) 
			throws XmlException, IOException{
		logger.info("########## - ActasTasacionManager.getListaActasTasacionDocument. " );
		es.agroseguro.contratacion.PolizaDocument polizaDocument= 
				es.agroseguro.contratacion.PolizaDocument.Factory.parse(new StringReader((xmlData)));
		return polizaDocument;
	}	
	
	private void saveSWCargaExpPlzTradicional(es.agroseguro.contratacion.Poliza polizaXml, 
			Usuario usuario, String xmlData, Long idPoliza, BigDecimal plan, String referencia) throws Exception{
		try {
			SWCargaExpPlzTradicional swCargaExpPlzTradicional=
					getSWCargaExpPlzTradicional(polizaXml, usuario, xmlData, idPoliza, plan, referencia);
			logger.info("########## -  CargaExplotacionesManager..saveSWCargaExpPlzTradicional - Guardamos SWCargaExpPlzTradicional en BBDD ");
			cargaExplotacionesDao.saveOrUpdate(swCargaExpPlzTradicional);
		} catch (Exception e) {
			logger.error("########## -  CargaExplotacionesManager..saveSWCargaExpPlzTradicional - ", e);
			throw new Exception("Error guardando el resultado de la llamada al servicio de renovación de explotaciones.", e);
		}
		
	}
	
	private SWCargaExpPlzTradicional getSWCargaExpPlzTradicional(es.agroseguro.contratacion.Poliza polizaXml, 
			Usuario usuario, String xmlData, Long idPoliza, BigDecimal plan, String referencia) throws Exception{
		logger.info("########## - CargaExplotacionesManager.getSWCargaExpPlzTradicional. ");
		
		SWCargaExpPlzTradicional doc = new SWCargaExpPlzTradicional();		
		doc.setFecha(new Date());
		doc.setIdpoliza(idPoliza);	
		doc.setPlan(plan);
		doc.setReferencia(referencia);
		doc.setRespuesta(Hibernate.createClob(xmlData));
		doc.setUsuario(usuario.getCodusuario());	
		
		return doc;
	} 
	
	private List<Explotacion>getListaExplotaciones(Long lineaSeguroId, Long idPoliza, 
			es.agroseguro.contratacion.Poliza polizaSW, Set<ModuloPoliza> modulos) throws DAOException{
		Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta = 
				diccionarioDatosDao.getCodConceptoEtiquetaTablaExplotaciones(lineaSeguroId);		
		
		List<Explotacion> listaExplotaciones= 
				PolizaUtils.getExplotacionesPolizaFromPolizaActualizada(polizaSW, idPoliza, dvCodConceptoEtiqueta);		
		
		asignaValoresExplotacionesCoberturas(listaExplotaciones,lineaSeguroId, modulos);
		
		return listaExplotaciones;
	}
	
	private void asignaValoresExplotacionesCoberturas(final List<Explotacion> listaExplotaciones, 
			Long lineaSeguroId, Set<ModuloPoliza> modulos) throws DAOException{
		for (Explotacion explotacion : listaExplotaciones) {
			Set<ExplotacionCobertura> nuevasCoberturas= new HashSet<ExplotacionCobertura>();;
			
			if(explotacion.getExplotacionCoberturas().size()>0){
				for (ExplotacionCobertura cobertura : explotacion.getExplotacionCoberturas()) {
					if(cobertura.getCpm()!=0 || cobertura.getRiesgoCubierto()!=0 || cobertura.getElegida()!=null){
						
						Iterator<ModuloPoliza> iterator = modulos.iterator(); 
					    int i=0;
					       while (iterator.hasNext()){
							   String modulo=iterator.next().getId().getCodmodulo();
							   
								if(i==0){
									asignaValorCobertura(cobertura, modulo,lineaSeguroId, explotacion);
								}else{
									ExplotacionCobertura cob=new ExplotacionCobertura(cobertura);
									cob.setId(null);
									cob.setCodmodulo(modulo);
									nuevasCoberturas.add(cob);
								}
								i+=1;
						   }					
					}
				}
				if (nuevasCoberturas.size()>0){
					explotacion.getExplotacionCoberturas().addAll(nuevasCoberturas);
				}
			}
		}
	}
	
	private void asignaValorCobertura(final ExplotacionCobertura cobertura, String modulo,
			Long lineaSeguroId, final Explotacion explotacion) throws DAOException{
		
		cobertura.setCodmodulo(modulo);
		cobertura.setElegible(new Character('S'));
		short fila = cargaExplotacionesDao.getFilaExplotacionCobertura(
				lineaSeguroId, modulo, cobertura.getCpm(), cobertura.getRiesgoCubierto());
		String cpmDescripcion=cargaExplotacionesDao.getDescripcionConceptoPpalMod(cobertura.getCpm());
		String rcDescripcion=cargaExplotacionesDao.getDescripcionRiesgoCubierto(lineaSeguroId, 
				modulo, cobertura.getRiesgoCubierto());
		cobertura.setFila(fila);
		if(null==cpmDescripcion)cpmDescripcion=new String("");
		cobertura.setCpmDescripcion(cpmDescripcion);
		
		if(null==rcDescripcion)rcDescripcion=new String("");
		cobertura.setRcDescripcion(rcDescripcion);
		cobertura.setExplotacion(explotacion);
	}
	
	private void asociarExplotacionesPoliza(String modulo, List<Explotacion>listaExplotaciones, 
			Poliza poliza ) throws Exception{
		if(modulo!=null)
			poliza.setCodmodulo(modulo.trim());
		for (Explotacion explotacion : listaExplotaciones) {
			explotacion.setPoliza(poliza);
			cargaExplotacionesDao.saveOrUpdate(explotacion);
			poliza.getExplotacions().add(explotacion);
		}	
	}
	
	@Override
	public void cargaSituacionActualizada(Long idPolizaAnterior, String realPath, final Poliza poliza) throws Exception {

		Poliza polizaAnterior=null;	
		String moduloSW=null;
		try {
			polizaAnterior=(Poliza) cargaExplotacionesDao.get(Poliza.class, idPolizaAnterior);		
			SWAnexoModificacionHelper servicio = new SWAnexoModificacionHelper();
			PolizaActualizadaResponse polActResponse =servicio.getPolizaActualizadaUnificado(polizaAnterior.getReferencia(), 
					polizaAnterior.getLinea().getCodplan(), realPath, true);
			es.agroseguro.contratacion.PolizaDocument polizaDocument =  polActResponse.getPolizaGanado();			
			es.agroseguro.contratacion.Poliza polizaAct=polizaDocument.getPoliza();			
			
			List<Explotacion> listaExplotaciones= this.getListaExplotaciones(polizaAnterior.getLinea().getLineaseguroid(),
					idPolizaAnterior, polizaAct, poliza.getModuloPolizas());
			
			if(null!=polizaAct.getCobertura())
				moduloSW=polizaAct.getCobertura().getModulo();
			
			this.asociarExplotacionesPoliza(moduloSW, listaExplotaciones, poliza);	
			
		}catch(SOAPFaultException e){
			logger.error("########## - Error en CargaExplotacionesManager.cargaSituacionActualizada. ", e);			
			throw e;			
		}catch(AgrException e){
			logger.error("########## - Error en HojasCampoManager.cargaSituacionActualizada. ", e);					
			throw e;		
		} catch (Exception e) {
			logger.error("########## - Error en CargaExplotacionesManager.cargaSituacionActualizada. ", e);
			throw e;
		}
	}
		

	public List<Poliza> listaPlzSituacionActualizada(Long idAsegurado, BigDecimal plan, BigDecimal linea,
			Long idPoliza) throws DAOException{
		//int tipoFiltro, Long idAsegurado, List<BigDecimal> listCodplan, BigDecimal codlinea
		List<Poliza>polizas=this.getPolizas(0, idAsegurado, plan, linea, false,idPoliza);
		return polizas;		
	}
		
	public List<Poliza> listaPolizaOriginalUltimosPlanes(Long idAsegurado, BigDecimal plan, BigDecimal linea,
			Long idPoliza) throws DAOException{
		List<Poliza>polizas=this.getPolizas(1, idAsegurado, plan, linea,false,idPoliza );
		return polizas;		
	}
	
	public List<Poliza> listaPolizaPlanActual(Long idAsegurado, BigDecimal plan, BigDecimal linea
			,Long idPoliza) throws DAOException{
		List<Poliza>polizas=this.getPolizas(2, idAsegurado, plan, linea, true,idPoliza);
		return polizas;		
	}
	
	
	private List<Poliza> getPolizas(int tipoFiltro, Long idAsegurado, BigDecimal plan, 
			BigDecimal linea, boolean planActual,Long idpoliza) throws DAOException{
		List<Poliza>polizas=null;
		List<BigDecimal>listaPlanes= new ArrayList<BigDecimal>();
		try {
			BigDecimal planAnterior=plan.subtract(new BigDecimal(1));
			if (!planActual){
				listaPlanes.add(planAnterior);
			}else{
				listaPlanes.add(plan);
			}
			if(tipoFiltro==1){
				listaPlanes.add(planAnterior.subtract(new BigDecimal(1)));
				listaPlanes.add(planAnterior.subtract(new BigDecimal(2)));
			}
			polizas= cargaExplotacionesDao.getPolizas(tipoFiltro, idAsegurado, listaPlanes, linea,idpoliza);
									
		} catch (DAOException e) {
			logger.error("CargaExplotacionesManager.isTipoPolizaCargaExplotaciones - ", e);
			throw e;
		}
		return polizas;
		
	}
	
	public void cargaExplotacionesPolizaExistente(Long idPolizaSeleccionada, final Poliza poliza) throws Exception{
//		Cargará de BBDD el listado de explotaciones de la póliza correspondiente al id pasado como parámetro.
//
//		Implementará un proceso que genere una copia de este listado de explotaciones 
//		(y de todos sus objetos hijos) y lo asociará a la póliza que se ha dado de alta para almacenarlo en BBDD. 
		//Este proceso utilizará los constructores copia de los objetos implicados:
//
//		Explotacion.java
//		GrupoRaza.java
//		DatosVariable.java
//		PrecioAnimalesModulo		
		
		List<Explotacion>explotacionesNuevas=null;
		String moduloPolAnt=null;
		try {
			Poliza polizaSeleccionada=(Poliza) cargaExplotacionesDao.getObject(Poliza.class, idPolizaSeleccionada);		
			explotacionesNuevas= new ArrayList<Explotacion>();
			
			for (Explotacion  explotacion: polizaSeleccionada.getExplotacions()) {
				Explotacion nuevaExplotacion=new Explotacion(explotacion);
				for(GrupoRaza gr: nuevaExplotacion.getGrupoRazas()){
					gr.setExplotacion(nuevaExplotacion);
					for(DatosVariable dv:gr.getDatosVariables()){
						dv.setGrupoRaza(gr);
					}
					for(PrecioAnimalesModulo am: gr.getPrecioAnimalesModulos()){
						am.setGrupoRaza(gr);
					}
				}			
				
				nuevaExplotacion.setPoliza(poliza);	
				explotacionesNuevas.add(nuevaExplotacion);				
			}			
			moduloPolAnt=polizaSeleccionada.getCodmodulo();	
			//asignaValoresExplotacionesCoberturas(explotacionesNuevas,poliza.getLinea().getLineaseguroid(), polizaSeleccionada.getModuloPolizas());
			
			this.asociarExplotacionesPoliza(moduloPolAnt,explotacionesNuevas, poliza);	
		} catch (Exception e) {
			logger.error("CargaExplotacionesManager.cargaExplotacionesPolizaExistente - ", e);
			throw e;
		}
		
	}
	
	
	
	public void setCargaExplotacionesDao(
			ICargaExplotacionesDao cargaExplotacionesDao) {
		this.cargaExplotacionesDao = cargaExplotacionesDao;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}


	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	


}
