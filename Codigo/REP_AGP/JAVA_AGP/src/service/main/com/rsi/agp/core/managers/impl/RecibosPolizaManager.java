package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.IReciboDao;
import com.rsi.agp.dao.models.poliza.IReciboPolizaDao;
import com.rsi.agp.dao.tables.cgen.TipificacionRecibos;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.recibos.ReciboBonificacionRecargo;
import com.rsi.agp.dao.tables.recibos.ReciboDetCompensacion;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;
import com.rsi.agp.dao.tables.recibos.ReciboPolizaSubv;
import com.rsi.agp.dao.tables.recibos.ReciboSubv;

import es.agroseguro.iTipos.Direccion;
import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.recibos.emitidos.BonificacionRecargo;
import es.agroseguro.recibos.emitidos.Colectivo;
import es.agroseguro.recibos.emitidos.DatosEconomicos1;
import es.agroseguro.recibos.emitidos.DatosEconomicosAplicacion;
import es.agroseguro.recibos.emitidos.DatosEconomicosRecibo;
import es.agroseguro.recibos.emitidos.DetalleCompensac;
import es.agroseguro.recibos.emitidos.Individual;
import es.agroseguro.recibos.emitidos.IndividualAplicacion;
import es.agroseguro.seguroAgrario.listaRecibos.Fase;
import es.agroseguro.seguroAgrario.listaRecibos.ListaRecibosDocument;
import es.agroseguro.seguroAgrario.listaRecibos.Recibo;
import es.agroseguro.serviciosweb.seguimientoscpoliza.AgrException;

public class RecibosPolizaManager implements IManager {

	private static final Log logger = LogFactory.getLog(RecibosPolizaManager.class);
	private IReciboPolizaDao reciboPolizaDao;
	private WebServicesManager webServicesManager;	
	private IPolizaDao polizaDao;
	private IReciboDao reciboDao;
	
	public List<ReciboPoliza> buscarRecibos (ReciboPoliza reciboPoliza) throws BusinessException {
		
		try{
			
			return reciboPolizaDao.list(reciboPoliza);
			
		}catch(DAOException dao){
			
			throw new BusinessException ("Se ha producido un error buscando los recibos de una póliza", dao);
			
		}
	}


	public ReciboPoliza buscarRecibo (Long idReciboPoliza) throws BusinessException {
		try {
			
			return (ReciboPoliza)reciboPolizaDao.get(ReciboPoliza.class, idReciboPoliza);
			
		} catch (DAOException dao) {
			
			throw new BusinessException ("Se ha producido un error buscando un recibo", dao);
			
		}
		
	}

	public Base64Binary obtenerPDFPoliza (Long idReciboPoliza, String realPath,Usuario usuario) throws BusinessException {
		try {
			
			ReciboPoliza reciboPoliza = (ReciboPoliza)reciboPolizaDao.get(ReciboPoliza.class, idReciboPoliza);
			
			Base64Binary res = webServicesManager.consultarPolizaActual(reciboPoliza,realPath);
			
			//TMR FACTURACION
			reciboPolizaDao.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
			
			return res;
			
		} catch (Exception e) {
			
			throw new BusinessException ("Se ha producido un error obteniendo el PDF de la póliza", e);
			
		}
		
	}
	
	public Base64Binary obtenerPDFPolizaCopy (String CodPlan, String RefPoliza, String tipoRef, String realPath,Usuario usuario) throws BusinessException {
		try {
			
			Base64Binary res = webServicesManager.consultarPolizaActualCopy(CodPlan,RefPoliza,tipoRef, realPath);
			
			//TMR FACTURACION
			reciboPolizaDao.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
			return res;
			
		} catch (Exception e) {
			
			throw new BusinessException ("Se ha producido un error obteniendo el PDF de la póliza", e);
			
		}
		
	}
	
	public Base64Binary obtenerPDFTradPolizaCopy (String CodPlan, String RefPoliza, String tipoRef, String realPath) throws BusinessException {
		try {
			
			Base64Binary res = webServicesManager.consultarPolizaTradActualCopy(CodPlan,RefPoliza,tipoRef, realPath);
			
			return res;
			
		} catch (Exception e) {
			
			throw new BusinessException ("Se ha producido un error obteniendo el PDF de la póliza", e);
			
		}
		
	}
	
	public Base64Binary obtenerPDFPolizaOrigen (String  idPoliza, String realPath,Usuario usuario) throws BusinessException {
		try {
			
			Poliza poliza = (Poliza)reciboPolizaDao.get(Poliza.class, Long.parseLong(idPoliza));
			
			Base64Binary res = webServicesManager.consultarPolizaOrigen(poliza,realPath);
			//TMR FACTURACION
			reciboPolizaDao.callFacturacion(usuario, Constants.FACTURA_IMPRESION);
			return res;
			
		} catch (Exception e) {
			
			throw new BusinessException ("Se ha producido un error obteniendo el PDF de la póliza", e);
			
		}
		
	}
		
	public List<Long> descargarRecibosPolizaWS(Character tipoReferencia,String refPoliza,String realPath) throws BusinessException{
		try{
			List<Long> idrecibos = new ArrayList<Long>();
			Poliza poliza = (Poliza)polizaDao.getPolizaByReferencia(refPoliza,tipoReferencia);
			
			logger.info("--Descargar Recibos Poliza--");
			
			if (poliza !=null){
				logger.info("tipoRef="+tipoReferencia+" codPlan="+poliza.getLinea().getCodplan()+" refPoliza="+refPoliza);
				return descargarRecibosPolizaWS(tipoReferencia.toString(),poliza.getLinea().getCodplan(), poliza.getLinea().getCodlinea(), 
						poliza.getColectivo().getIdcolectivo(), refPoliza, realPath);				
			}else{
				logger.info("Poliza no encontrada con los datos: tipoRef="+tipoReferencia+" refPoliza="+refPoliza);
				return idrecibos;
			}
		}catch(DAOException daoe){
			throw new BusinessException("Error durante el acceso a la base de datos",daoe); 
		}
	}

	public List<Long> descargarRecibosPolizaWS(Poliza poliza ,String realPath) throws BusinessException{
		try{
			List<Long> idrecibos = new ArrayList<Long>();
			logger.info("--Descargar Recibos Poliza--");
			
			logger.info("tipoRef="+poliza.getTipoReferencia().toString()+" codPlan="+poliza.getLinea().getCodplan()+" refPoliza="+poliza.getReferencia());
			return descargarRecibosPolizaWS(poliza.getTipoReferencia().toString(),poliza.getLinea().getCodplan(), poliza.getLinea().getCodlinea(), 
						poliza.getColectivo().getIdcolectivo(), poliza.getReferencia(), realPath);				
			
		}catch(Exception ex){
			throw new BusinessException("Error durante el acceso a la base de datos",ex); 
		}
	}
	
	public List<Long> descargarRecibosPolizaWS(String tipoReferencia, BigDecimal codPlan, BigDecimal codLinea, String refColectivo, 
			String refPoliza, String realPath) throws BusinessException{

		ListaRecibosDocument listaRecibosPoliza = null;
		boolean existeRecibo = false;
		boolean existeReciboPoliza = false;
		com.rsi.agp.dao.tables.recibos.Recibo reciboEncontrado = null;
		List<Long> idNuevosRecibos = new ArrayList<Long>();
		List<com.rsi.agp.dao.tables.recibos.Recibo> listaRecibos = new ArrayList<com.rsi.agp.dao.tables.recibos.Recibo>();
		
		try{		
			listaRecibosPoliza = webServicesManager.listarRecibosEmitidos(tipoReferencia, codPlan, refPoliza, realPath);
			
			if (listaRecibosPoliza != null && listaRecibosPoliza.getListaRecibos() != null){
				logger.debug("---------LISTARECIBOS-----------");
				logger.debug(listaRecibosPoliza.toString());
				logger.info("resultado de la llamada a listarRecibosEmitidos: " + listaRecibosPoliza.getListaRecibos().getFaseArray().length);			
				if (listaRecibosPoliza.getListaRecibos().getFaseArray() != null){
					for(Fase fase : listaRecibosPoliza.getListaRecibos().getFaseArray()){
						if (fase.getRecibo() !=null){
							Recibo reciboLista = fase.getRecibo();
							//Comprobamos que existe un recibo en BBDD a partir del codRecibo.
							reciboEncontrado = reciboDao.getRecibo(reciboLista.getRecibo(), codPlan + "", codLinea.intValue(), refColectivo);
							
							// MPM - 22/06/2012 - Hay que actualizar el valor de 'existeRecibo' en cada iteración del bucle
							existeRecibo = (reciboEncontrado != null);
							
							if(!existeRecibo && reciboLista.getRecibo()!=0){
								logger.info("Llamamos al servicio de consulta de recibos");
								es.agroseguro.recibos.emitidos.FaseDocument faseDocument = webServicesManager.consultaReciboEmitido(tipoReferencia, codPlan, refPoliza, new BigDecimal(reciboLista.getRecibo()), fase.getFechaEmisionRecibo().getTime() , realPath);
								logger.info("llamada exitosa");
								logger.debug("---------FASEDOCUMENT-----------");
								logger.debug(faseDocument.toString());
								com.rsi.agp.dao.tables.recibos.Recibo recibo = generarRecibo(fase, faseDocument);
								logger.info("recibo generado");
								if (recibo.getCodplan().equals(codPlan.toString()) && recibo.getCodlinea().intValue() == codLinea.intValue()){
									//sólo añado el recibo a la lista si se corresponde con el plan y la línea que estoy buscando
									listaRecibos.add(recibo);
								}
							}else if (reciboLista.getRecibo()!=0){
								//Comprobamos que existe un reciboPoliza en BBDD a partir del codRecibo,tipoRef y refPoliza
								//si no existe se inserta en BBDD
								existeReciboPoliza = reciboDao.existeReciboPoliza(reciboLista.getRecibo(),tipoReferencia.charAt(0),refPoliza);
			 					if (!existeReciboPoliza){
									logger.info("Llamamos al servicio de consulta de recibos");
									es.agroseguro.recibos.emitidos.FaseDocument faseDocument = webServicesManager.consultaReciboEmitido(tipoReferencia, codPlan, refPoliza, new BigDecimal(reciboLista.getRecibo()), fase.getFechaEmisionRecibo().getTime() , realPath);
									logger.info("llamada exitosa");
									logger.debug("---------FASEDOCUMENT-----------");
									logger.debug(faseDocument.toString());
									com.rsi.agp.dao.tables.recibos.Recibo recibo = addReciboPoliza(reciboEncontrado,fase, faseDocument);
									logger.info("recibo generado con nuevos RecibosPoliza ");
									if (recibo.getCodplan().equals(codPlan.toString()) && recibo.getCodlinea().intValue() == codLinea.intValue()){
										//sólo añado el recibo a la lista si se corresponde con el plan y la línea que estoy buscando
										listaRecibos.add(recibo);
									}
								}
							}
						}
					}
				}
				
				if(!listaRecibos.isEmpty()){
					logger.info("número de recibos: " + listaRecibos.size());
					//insertamos en BBDD la lista de recibos
					reciboPolizaDao.saveOrUpdateList(listaRecibos);
					
					for(com.rsi.agp.dao.tables.recibos.Recibo recibo : listaRecibos)
						idNuevosRecibos.add(recibo.getId());
					
				}
			}
			else{
				logger.error("resultado de la llamada a listarRecibosEmitidos nulo ");
			}
			
				
		
		}catch(DAOException daoe){
			throw new BusinessException("Error durante el acceso a la base de datos", daoe);
		}catch(SeguimientoServiceException sse){
			logger.error("Error del servicio de seguimiento", sse);
				try {
					AgrException e = (AgrException) sse.getCause();
					if(e.getFaultInfo().getError().get(0).getCodigo() == -1){
						logger.error("El WebService de Seguimiento no ha devuelto el listado de recibos de la póliza. Esta excepción no es necesariamente un error",sse);
					}else{
						throw new BusinessException("Error inesperado al devolver los recibos de la poliza", e);
					}
				} catch (ClassCastException e) {
					throw new BusinessException("Se ha producido un error al obtener los recibos de la poliza", e);
				}				
		}catch(Exception e){
			logger.error("Error indefinido al obtener los recibos", e);
			throw new BusinessException("Error indefinido al obtener los recibos", e);
		}
		
		return idNuevosRecibos;
	}
	
	private com.rsi.agp.dao.tables.recibos.Recibo generarRecibo(Fase fase, es.agroseguro.recibos.emitidos.FaseDocument faseDocument){
		
		es.agroseguro.recibos.emitidos.Recibo reciboWS = faseDocument.getFase().getReciboArray(0);
				
		
		
		Colectivo colectivoWS = reciboWS.getColectivo();
		Individual individualWS = reciboWS.getIndividual();
		NombreApellidos nombreApellidosWS = reciboWS.getNombreApellidos();
		RazonSocial razonSocialWS = reciboWS.getRazonSocial();
		Direccion direccionWS = reciboWS.getDireccion();		
		DatosEconomicosRecibo datosEconomicosReciboWS = reciboWS.getDatosEconomicos();	
		
		DetalleCompensac[] detalleCompensacWS = reciboWS.getDetalleCompensacArray(); // ???
		es.agroseguro.recibos.emitidos.SubvencionCCAA[] subvencionesCCAAsWS = reciboWS.getSubvencionCCAAArray();
		IndividualAplicacion[] individualAplicacionesWS = reciboWS.getPolizasAplicaciones().getIndividualAplicacionArray();
		
		com.rsi.agp.dao.tables.recibos.Recibo recibo = new com.rsi.agp.dao.tables.recibos.Recibo();

		recibo.setCodfase(((Integer)fase.getFase()).toString());
		recibo.setFecemisionrecibo(fase.getFechaEmisionRecibo().getTime());
		recibo.setCodigoentidadaseg(reciboWS.getCodigo());
		recibo.setMoroso(reciboWS.getDeudor().toString());
		recibo.setCodplan(((Integer)fase.getPlan()).toString());
		recibo.setCodlinea(reciboWS.getLinea());
		recibo.setCodrecibo(reciboWS.getRecibo());
		recibo.setDeudor(reciboWS.getDeudor().toString());
		
		if (Integer.valueOf(reciboWS.getTipo()) != 0) {
			TipificacionRecibos t = new TipificacionRecibos();
			t.setTipificacionRecibo(new BigDecimal(reciboWS.getTipo()));
			recibo.setTipificacionRecibos(t);
		}else {
			recibo.setTipificacionRecibos(null);
		}
		
		if(nombreApellidosWS != null){

			recibo.setApell1tomador(nombreApellidosWS.getApellido1());
			recibo.setApell2tomador(nombreApellidosWS.getApellido2());
			recibo.setNombretomador(nombreApellidosWS.getNombre());
			
		}
		
		if(razonSocialWS != null){
			
			recibo.setRazonsocialtomador(razonSocialWS.getRazonSocial());
			
		}
		
		if(direccionWS != null){
			
			recibo.setBloquetomador(direccionWS.getBloque());
			recibo.setViatomador(direccionWS.getVia());
			recibo.setPisotomador(direccionWS.getPiso());
			recibo.setEscaleratomador(direccionWS.getEscalera());
			recibo.setProvinciatomador(direccionWS.getProvincia());
			recibo.setNumeroviatomador(direccionWS.getNumero());
			recibo.setLocalidadtomador(direccionWS.getLocalidad());
			recibo.setCptomador(direccionWS.getCp());
			
		}
		
		if(colectivoWS != null){
		
			recibo.setDccolectivo(colectivoWS.getDigitoControl());
			recibo.setRefcolectivo(colectivoWS.getReferencia());
			
		}
		if(individualWS != null){
			//?????????
		}
		
		Set<ReciboDetCompensacion> reciboDetCompensacions = new HashSet<ReciboDetCompensacion>();
		if(null!=detalleCompensacWS) {
			for(DetalleCompensac dc : detalleCompensacWS){
				ReciboDetCompensacion rdc = new ReciboDetCompensacion();
				rdc.setCobrodeudor(dc.getCobro());
				rdc.setLineadeudor(new BigDecimal(dc.getLinea()));
				//rdc.setNumrecibodeudor(dc.get) ???
				rdc.setPlandeudor(new BigDecimal(dc.getPlan()));
				rdc.setRecibo(recibo);
				
				reciboDetCompensacions.add(rdc);
			}
			recibo.setReciboDetCompensacions(reciboDetCompensacions);
		}
		
		
		if(datosEconomicosReciboWS != null){
			
			recibo.setBonificacion(datosEconomicosReciboWS.getBonificacion());
			recibo.setBonifsistproteccion(datosEconomicosReciboWS.getBonificacionSistProteccion());
			recibo.setClea(datosEconomicosReciboWS.getClea());
			recibo.setCompensacionimpagados(datosEconomicosReciboWS.getCompensacionRecibosImpagados());
			recibo.setCompensaciontomador(datosEconomicosReciboWS.getCompensacionSaldoTomador());
			recibo.setConsorcio(datosEconomicosReciboWS.getConsorcio());
			recibo.setCosteneto(datosEconomicosReciboWS.getCosteNeto());
			recibo.setCostetomador(datosEconomicosReciboWS.getCosteTomador());
			recibo.setDctocolectivo(datosEconomicosReciboWS.getDescuentoColectivo());
			recibo.setDctoventanilla(datosEconomicosReciboWS.getDescuentoVentanilla());
			recibo.setLiquido(datosEconomicosReciboWS.getLiquido());
			recibo.setPagos(datosEconomicosReciboWS.getPagos());
			recibo.setPrimacomercial(datosEconomicosReciboWS.getPrimaComercial());
			recibo.setPrimaneta(datosEconomicosReciboWS.getPrimaNeta());
			recibo.setRecargo(datosEconomicosReciboWS.getRecargo());
			recibo.setSubvenesa(datosEconomicosReciboWS.getSubvencionEnesa());
			
		}
		
		Set<ReciboSubv> recibosSubvenciones = new HashSet<ReciboSubv>();
		
		if(null!= subvencionesCCAAsWS) {
			for(es.agroseguro.recibos.emitidos.SubvencionCCAA subvencionCCAA : subvencionesCCAAsWS){
				
				ReciboSubv reciboSubv = new ReciboSubv();
				reciboSubv.setCodorganismo(subvencionCCAA.getCodigo().charAt(0));
				reciboSubv.setSubvccaa(subvencionCCAA.getSubvencionComunidades());
				reciboSubv.setRecibo(recibo);
				
				recibosSubvenciones.add(reciboSubv);
				
			}
			recibo.setReciboSubvs(recibosSubvenciones);
		}
		
		
		
		Set<ReciboPoliza> recibosPolizas = new HashSet<ReciboPoliza>();
		for(IndividualAplicacion individualAplicacion : individualAplicacionesWS){
			
			ReciboPoliza reciboPoliza = new ReciboPoliza();
			
			DatosEconomicosAplicacion datosEconomicosAplicacionWS = individualAplicacion.getDatosEconomicos();
			DatosEconomicos1 datosEconomicos1 = individualAplicacion.getDatosEconomicos1();
			
			NombreApellidos nombreApellidosAplicacionWS = individualAplicacion.getNombreApellidos();
			RazonSocial razonSocialAplicacionWS = individualAplicacion.getRazonSocial();
			
			if(nombreApellidosAplicacionWS != null){
				
				reciboPoliza.setNombreaseg(nombreApellidosAplicacionWS.getNombre());
				reciboPoliza.setApell1aseg(nombreApellidosAplicacionWS.getApellido1());
				reciboPoliza.setApell2aseg(nombreApellidosAplicacionWS.getApellido2());
				
			}
			
			if(razonSocialAplicacionWS != null){
				
				reciboPoliza.setRazonsocialaseg(razonSocialAplicacionWS.getRazonSocial());
				
			}
			
			reciboPoliza.setDcpoliza(individualAplicacion.getDigitoControl());
			reciboPoliza.setNifaseg(individualAplicacion.getNif());
			
			reciboPoliza.setRefpoliza(individualAplicacion.getReferencia());
			reciboPoliza.setTiporecibo(individualAplicacion.getTipoRecibo().toString().charAt(0));
			reciboPoliza.setTiporef(individualAplicacion.getTipoReferencia().toString().charAt(0));
			
			reciboPoliza.setRecibo(recibo);
			
			es.agroseguro.recibos.emitidos.SubvencionCCAA[] subvencionesCCAAsAplicacionWS = individualAplicacion.getSubvencionCCAAArray();
			
			
			Set<ReciboPolizaSubv> recibosSubvencionesAplicacion = new HashSet<ReciboPolizaSubv>();
			
			if(null!= subvencionesCCAAsAplicacionWS) {
				for(es.agroseguro.recibos.emitidos.SubvencionCCAA subvencionCCAAAplicacion : subvencionesCCAAsAplicacionWS){
					
					ReciboPolizaSubv reciboPolizaSubv = new ReciboPolizaSubv();
					reciboPolizaSubv.setCodorganismo(subvencionCCAAAplicacion.getCodigo().charAt(0));
					reciboPolizaSubv.setSubvccaa(subvencionCCAAAplicacion.getSubvencionComunidades());
					reciboPolizaSubv.setReciboPoliza(reciboPoliza);
					
					recibosSubvencionesAplicacion.add(reciboPolizaSubv);
					
				}
				reciboPoliza.setReciboPolizaSubvs(recibosSubvencionesAplicacion);
			}
			
			
			if(datosEconomicosAplicacionWS != null){
				
				reciboPoliza.setBonificacion(datosEconomicosAplicacionWS.getBonificacion());
				reciboPoliza.setBonifsistproteccion(datosEconomicosAplicacionWS.getBonificacionSistProteccion());
				reciboPoliza.setClea(datosEconomicosAplicacionWS.getClea());
				reciboPoliza.setConsorcio(datosEconomicosAplicacionWS.getConsorcio());
				reciboPoliza.setCosteneto(datosEconomicosAplicacionWS.getCosteNeto());
				reciboPoliza.setCostetomador(datosEconomicosAplicacionWS.getCosteTomador());
				reciboPoliza.setDctocolectivo(datosEconomicosAplicacionWS.getDescuentoColectivo());
				reciboPoliza.setDctoventanilla(datosEconomicosAplicacionWS.getDescuentoVentanilla());
				reciboPoliza.setPagos(datosEconomicosAplicacionWS.getPagos());
				reciboPoliza.setPrimacomercial(datosEconomicosAplicacionWS.getPrimaComercial());
				reciboPoliza.setPrimaneta(datosEconomicosAplicacionWS.getPrimaNeta());
				reciboPoliza.setRecargo(datosEconomicosAplicacionWS.getRecargo());
				reciboPoliza.setSaldopoliza(datosEconomicosAplicacionWS.getSaldoPoliza());
				reciboPoliza.setSubvenesa(datosEconomicosAplicacionWS.getSubvencionEnesa());
				
			}
			if (datosEconomicos1 != null) {
				
				reciboPoliza.setCostetomador(datosEconomicos1.getCosteTomador());
				reciboPoliza.setDiferencia(datosEconomicos1.getDiferencia());
				reciboPoliza.setPagos(datosEconomicos1.getPagos());
				reciboPoliza.setPrimacomercial(datosEconomicos1.getPrimaComercial());
				reciboPoliza.setPrimaneta(datosEconomicos1.getPrimaComercialNeta());
				reciboPoliza.setRecargoAval(datosEconomicos1.getRecargoAval());
				reciboPoliza.setRecargo(datosEconomicos1.getRecargoConsorcio());
				reciboPoliza.setRecargoFracc(datosEconomicos1.getRecargoFraccionamiento());
				reciboPoliza.setReciboPrima(datosEconomicos1.getReciboPrima());
				reciboPoliza.setTotalCosteTomador(datosEconomicos1.getTotalCosteTomador());
				// bonificaciones-recargos
				
				if(null!=datosEconomicos1.getBonificacionesRecargos()) {
					Set<ReciboBonificacionRecargo> boniRecargosList = new HashSet<ReciboBonificacionRecargo>();
					for(BonificacionRecargo boniRec : datosEconomicos1.getBonificacionesRecargos().getBonificacionRecargoArray()) {
						ReciboBonificacionRecargo br = new ReciboBonificacionRecargo();
						br.getId().setCodigo(new BigDecimal(boniRec.getCodigo()));
						br.setImporte(boniRec.getImporte());
						br.setReciboPoliza(reciboPoliza);
						boniRecargosList.add(br);
					}
					reciboPoliza.setReciboBonificacionRecargos(boniRecargosList);
				}
				//subv enesa
				reciboPoliza.setSubvenesa(datosEconomicos1.getSubvencionEnesa());
				
				
			}
			recibosPolizas.add(reciboPoliza);
			
		}
		recibo.setReciboPolizas(recibosPolizas);
		
		
		return recibo;
	}
	
private com.rsi.agp.dao.tables.recibos.Recibo addReciboPoliza(com.rsi.agp.dao.tables.recibos.Recibo reciboEncontrado,Fase fase, es.agroseguro.recibos.emitidos.FaseDocument faseDocument){
		
		es.agroseguro.recibos.emitidos.Recibo reciboWS = faseDocument.getFase().getReciboArray(0);
				
		
		IndividualAplicacion[] individualAplicacionesWS = reciboWS.getPolizasAplicaciones().getIndividualAplicacionArray();
		
		Set<ReciboPoliza> recibosPolizas = new HashSet<ReciboPoliza>();
		for(IndividualAplicacion individualAplicacion : individualAplicacionesWS){
			
			ReciboPoliza reciboPoliza = new ReciboPoliza();
			
			DatosEconomicosAplicacion datosEconomicosAplicacionWS = individualAplicacion.getDatosEconomicos();
			NombreApellidos nombreApellidosAplicacionWS = individualAplicacion.getNombreApellidos();
			RazonSocial razonSocialAplicacionWS = individualAplicacion.getRazonSocial();
			
			if(nombreApellidosAplicacionWS != null){
				
				reciboPoliza.setNombreaseg(nombreApellidosAplicacionWS.getNombre());
				reciboPoliza.setApell1aseg(nombreApellidosAplicacionWS.getApellido1());
				reciboPoliza.setApell2aseg(nombreApellidosAplicacionWS.getApellido2());
				
			}
			
			if(razonSocialAplicacionWS != null){
				reciboPoliza.setRazonsocialaseg(razonSocialAplicacionWS.getRazonSocial());
			}
			
			if(datosEconomicosAplicacionWS != null){
				
				reciboPoliza.setBonificacion(datosEconomicosAplicacionWS.getBonificacion());
				reciboPoliza.setBonifsistproteccion(datosEconomicosAplicacionWS.getBonificacionSistProteccion());
				reciboPoliza.setClea(datosEconomicosAplicacionWS.getClea());
				reciboPoliza.setConsorcio(datosEconomicosAplicacionWS.getConsorcio());
				reciboPoliza.setCosteneto(datosEconomicosAplicacionWS.getCosteNeto());
				reciboPoliza.setCostetomador(datosEconomicosAplicacionWS.getCosteTomador());
				reciboPoliza.setDctocolectivo(datosEconomicosAplicacionWS.getDescuentoColectivo());
				reciboPoliza.setDctoventanilla(datosEconomicosAplicacionWS.getDescuentoVentanilla());
				reciboPoliza.setPagos(datosEconomicosAplicacionWS.getPagos());
				reciboPoliza.setPrimacomercial(datosEconomicosAplicacionWS.getPrimaComercial());
				reciboPoliza.setPrimaneta(datosEconomicosAplicacionWS.getPrimaNeta());
				reciboPoliza.setRecargo(datosEconomicosAplicacionWS.getRecargo());
				reciboPoliza.setSaldopoliza(datosEconomicosAplicacionWS.getSaldoPoliza());
				reciboPoliza.setSubvenesa(datosEconomicosAplicacionWS.getSubvencionEnesa());
				
			}
			
			reciboPoliza.setDcpoliza(individualAplicacion.getDigitoControl());
			reciboPoliza.setNifaseg(individualAplicacion.getNif());
			
			reciboPoliza.setRefpoliza(individualAplicacion.getReferencia());
			reciboPoliza.setTiporecibo(individualAplicacion.getTipoRecibo().toString().charAt(0));
			reciboPoliza.setTiporef(individualAplicacion.getTipoReferencia().toString().charAt(0));
			
			reciboPoliza.setRecibo(reciboEncontrado);
			
			es.agroseguro.recibos.emitidos.SubvencionCCAA[] subvencionesCCAAsAplicacionWS = individualAplicacion.getSubvencionCCAAArray();
			
			
			Set<ReciboPolizaSubv> recibosSubvencionesAplicacion = new HashSet<ReciboPolizaSubv>();
			
			for(es.agroseguro.recibos.emitidos.SubvencionCCAA subvencionCCAAAplicacion : subvencionesCCAAsAplicacionWS){
				
				ReciboPolizaSubv reciboPolizaSubv = new ReciboPolizaSubv();
				reciboPolizaSubv.setCodorganismo(subvencionCCAAAplicacion.getCodigo().charAt(0));
				reciboPolizaSubv.setSubvccaa(subvencionCCAAAplicacion.getSubvencionComunidades());
				reciboPolizaSubv.setReciboPoliza(reciboPoliza);
				
				recibosSubvencionesAplicacion.add(reciboPolizaSubv);
				
			}
			
			reciboPoliza.setReciboPolizaSubvs(recibosSubvencionesAplicacion);
			
			recibosPolizas.add(reciboPoliza);
			
			
		}
		reciboEncontrado.setReciboPolizas(recibosPolizas);
		//((com.rsi.agp.dao.tables.recibos.Recibo) reciboEncontrado).setReciboPolizas(recibosPolizas);
		
		return (com.rsi.agp.dao.tables.recibos.Recibo) reciboEncontrado;
	}
	public List<TipificacionRecibos> getListTipificacionRecibos() throws Exception{
	
		try {
			return reciboPolizaDao.getListTipificacionRecibos();
		}catch(Exception e) {
			logger.error("Error al obtener la lista de tipificacion de recibos", e);
			throw e;
		}
	}
	
	public void setReciboPolizaDao(IReciboPolizaDao reciboPolizaDao) {
		this.reciboPolizaDao = reciboPolizaDao;
	}


	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}


	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setReciboDao(IReciboDao reciboDao) {
		this.reciboDao = reciboDao;
	}


	
	
	
}
