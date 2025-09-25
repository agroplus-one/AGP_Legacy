package com.rsi.agp.batch.procesarPolizasRenovables;

import java.io.File;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.w3._2005._05.xmlmime.Base64Binary;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsi.agp.batch.bbdd.Conexion;
import com.rsi.agp.batch.procesarPolizasRenovables.ProcesarPolizasRenovables.PolizaRenBean;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.PolizasPctComisionesManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.comisiones.PolizasPctComisionesDao;
import com.rsi.agp.dao.models.param.ParametrizacionDao;
import com.rsi.agp.dao.models.poliza.PolizaDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.ClaseDetalleGanado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.ConfigAgp;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.LineaGrupoNegocio;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

import es.agroseguro.contratacion.ObjetosAsegurados;
import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.contratacion.costePoliza.Financiacion;
import es.agroseguro.contratacion.datosVariables.DatosVariables;
import es.agroseguro.contratacion.explotacion.Coordenadas;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument;
import es.agroseguro.estadoRenovacion.EstadoRenovacionDocument;
import es.agroseguro.estadoRenovacion.Renovacion;
import es.agroseguro.iTipos.Ambito;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ConsultaContratacionRenovableResponse;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ParametrosConsultaContratacionRenovable;

public final class ProcesarPolizasRenovablesWS {
	private static final Logger logger = Logger.getLogger(ProcesarPolizasRenovables.class);
	
	protected static ContratacionRenovaciones getObjetoWs()	throws Exception {
		ContratacionRenovaciones contratRen = null;
		
		URL wsdlLocation = null;
		String url = "";

		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();

		try {
			File ficherowsdl = new File(WSUtils.getBundleProp("contratacionRenovacionesWS.wsdl"));
			url = ficherowsdl.getAbsolutePath();
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException ex) {
			throw new Exception("Imposible recuperar el WSDL de Gastos de una poliza Renovable. Revise la Ruta: "+ url, ex);
		} catch (NullPointerException ex) {
			throw new Exception("Imposible obtener el WSDL de Gastos de una poliza Renovable. Revise la Ruta: "+ wsdlLocation.toString(), ex);
		}	
		
		String wsLocation = WSUtils.getBundleProp("contratacionRenovacionesWS.location");
		String wsPort 	  = WSUtils.getBundleProp("contratacionRenovacionesWS.port");
		String wsService  = WSUtils.getBundleProp("contratacionRenovacionesWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName    = new QName(wsLocation, wsPort);
		
		// LOGS PARAMETROS WS
		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsService: " + wsService.toString());
		
		try {
			ContratacionRenovaciones_Service srv = new ContratacionRenovaciones_Service(wsdlLocation, serviceName);
			contratRen = (ContratacionRenovaciones) srv.getPort(portName, ContratacionRenovaciones.class);
		}catch  (Exception ex) {
			logger.debug("# Error en WSn Exception: "+ex.getMessage().toString());
			throw new Exception(ex.getMessage().toString(), ex);
		}
		return contratRen;
	}
	
	private static String generateBarCode(Session session, ParametrizacionDao paramDao) {
		String barCode = "";
		logger.debug("generateBarCode - INIT");
		try {
			DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
			String year = df.format(Calendar.getInstance().getTime());
			Criteria criteria = session.createCriteria(ConfigAgp.class);
			criteria.add(Restrictions.eq("agpNemo", "SEQ_BARCODE_A"));
			ConfigAgp configAgp = (ConfigAgp) paramDao.getObject(ConfigAgp.class, "agpNemo", "SEQ_BARCODE_A");
			
			barCode = configAgp.getAgpValor().substring(0, 3);
			barCode += String.format("%0" + 6 + "d", Integer.valueOf(configAgp.getAgpValor().substring(3)) + 1);
			//barCode += org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(Integer.valueOf(configAgp.getAgpValor().substring(3)) + 1), 6, '0');
			
			configAgp.setAgpValor(barCode);
			paramDao.saveOrUpdate(configAgp);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("generateBarCode - END");
		return barCode;
	}
	
	protected static List<PolizaRenBean> getListPolizasRenovables(final List<PolizaRenBean> lstRes, final Long planActual, 
			final List<BigDecimal> lstLineasSW, final String estados,
			final Session session, final PolizasPctComisionesManager polizasPctComisionesManager, final PolizasPctComisionesDao pDao, final PolizaDao polDao, final ParametrizacionDao paramDao)	throws Exception {
	    
		List<String> lstReferencias = new ArrayList<String>(); 
		List<String> lstRefFinal = new ArrayList<String>();
		PolizaRenovable polizaRen = null;
		Base64Binary res = null;
		Base64Binary resEstadoRenovacion = null;
		int polizasRev = 1;
		int polInsertadas = 0;
		Boolean noPoliza = true;
		Integer reintentos = null;
		ResourceBundle bundle = ResourceBundle.getBundle("agp_procesar_plz_renovables");
		
		String strReintentos = bundle.getString("reintentos");
		if (!strReintentos.equals(""))
			reintentos = Integer.parseInt(strReintentos);
		logger.debug("# Reintentos por timeOut: "+reintentos);
		try {
			ContratacionRenovaciones contratRen = getObjetoWs(); // cargamos el objeto de la llamada al Ws
			
			WSUtils.addSecurityHeader(contratRen);
			lstReferencias = BBDDProcesarPolRenUtil.getReferencias(session,planActual,estados);
			int limPolizas = BBDDProcesarPolRenUtil.getContadorPolizas(session);
			for (@SuppressWarnings("unused") BigDecimal linea: lstLineasSW) {			
				for (String ref: lstReferencias) {
					if (polizasRev<=limPolizas) {
						if (!lstRefFinal.contains(ref))
							lstRefFinal.add(ref);
					}
					polizasRev++;
				}
			} // fin bucle lstLineasSW
			logger.debug("## polizas renovables a tratar: "+ lstRefFinal.size()+" ##");
			// una vez que tengamos la lista de referencias vamos llamando al SW de ConsultaContratacionRenovable

			// Recogemos de BBDD el limite de  polizas a insertar 
			polizasRev = 1;
			
			for (String ref: lstRefFinal) {	
				BigDecimal clase   = null;
				String codOficina = null;
				String codUsuario = "";
				logger.debug(" ### POL. A PROCESAR "+ ref+ " # "+ polizasRev+" ### ");
					try {
						Criteria crit = session.createCriteria(PolizaRenovable.class);
						//crit.add(Restrictions.eq("referencia","119147P"));
						crit.add(Restrictions.eq("referencia",ref));
						crit.add(Restrictions.eq("plan",planActual));
						polizaRen = (PolizaRenovable) crit.uniqueResult();
									
					} catch (Exception ex) {
						logger.error(" Error al recoger en BBDD la poliza renovable : "+ ref , ex);
					}
					
					logger.debug("ENTRA LINEA 204 procesar");
					if (polizaRen != null) {
						logger.debug("ENTRA LINEA 206 procesar");
						PolizaRenBean polR = new PolizaRenBean();
						polR.setReferencia(polizaRen.getReferencia());
						polR.setLinea(polizaRen.getLinea().toString());
						polR.setPlan(polizaRen.getPlan().toString());
						polR.setIdColectivo(polizaRen.getColectivoRenovacion().getReferencia());
						polR.setNifAsegurado(polizaRen.getNifAsegurado());
						polR.setEstado(polizaRen.getEstadoRenovacionAgroseguro().getCodigo().toString());
						
						lstRes.add(polR);
						
						noPoliza = true;
						Colectivo colHbm = new Colectivo();
						// Comprobamos si el colectivo existe
						//BORRAR!!
						//colHbm = BBDDProcesarPolRenUtil.comprobarColectivo(polR,new BigDecimal(9998),
							//   new BigDecimal(9998),new BigDecimal(0),session);
						
						colHbm = BBDDProcesarPolRenUtil.comprobarColectivo(polR,new BigDecimal(polizaRen.getColectivoRenovacion().getCodentidad()),
							new BigDecimal(polizaRen.getColectivoRenovacion().getCodentidadmed()),new BigDecimal(polizaRen.getColectivoRenovacion().getCodsubentmed()),session);
						if (colHbm != null) { // SI existe el colectivo, comprobamos asegurado
							//BORRAR!!
							Asegurado aseguradoHbm = getAseguradoBBDD(polR.getNifAsegurado(),colHbm.getTomador().getEntidad().getCodentidad(),
								colHbm.getSubentidadMediadora().getId().getCodentidad(),colHbm.getSubentidadMediadora().getId().getCodsubentidad(),session);
							//Asegurado aseguradoHbm = getAseguradoBBDD("70579106H",new BigDecimal(3190),new BigDecimal(3190),new BigDecimal(0), session);
							if (aseguradoHbm != null) { // SI existe el asegurado, llamar al servicio web y replicar
								ParametrosConsultaContratacionRenovable paramConsultaReq = new ParametrosConsultaContratacionRenovable();
								// ## Parametros de llamada al WS ##
								paramConsultaReq.setPlan(planActual.intValue());
								paramConsultaReq.setReferencia(polR.getReferencia());
								//paramConsultaReq.setReferencia("H944258");
								
								String acuse = "";
								String acuseEstadoRenovacion = "";
								ConsultaContratacionRenovableResponse wsResp = null;
								for (int ciclo=0;ciclo<reintentos;ciclo++){
									int vuelta = ciclo+1;
									try {		
										//logger.debug("## CALL WS - CONSULTA POL. RENOVABLE - PLAN: "+planActual.toString() +" LINEA: " + polR.getLinea() + " REF: "+polR.getReferencia() +" ##");
										logger.debug("# ciclo "+vuelta+"/"+reintentos);
										wsResp = contratRen.consultaContratacionRenovable(paramConsultaReq);	
										
										res = wsResp.getPolizaRenovable(); // Llamada al WS
										resEstadoRenovacion = wsResp.getEstadoRenovacion();
										acuse = new String (res.getValue(), "UTF-8");
										acuseEstadoRenovacion = new String (resEstadoRenovacion.getValue(), "UTF-8");
										logger.debug("# ciclo "+vuelta+"/"+reintentos +" OK");
										break;
									} catch  (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException ex) {
										//logger.error("Error en WS en agrException: "+ex.getMessage().toString());
										List<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> lstErrores = ex.getFaultInfo().getError();
										String errores ="";
										for (es.agroseguro.serviciosweb.contratacionrenovaciones.Error error: lstErrores){
											errores = errores + error.getMensaje().toString()+".";
											logger.error("# Error en AgrException: "+error.getMensaje().toString()+" POL. RENOVABLE - PLAN: "+planActual.toString() +" LINEA: " + polR.getLinea() + " REF: "+polR.getReferencia() +" ##");
										}
										polR.setDescripcion(polR.getReferencia() +": "+errores);
									
										noPoliza = false;
										break;
									}catch  (Exception e) {
										if (e instanceof javax.xml.ws.WebServiceException) {
											Throwable cause = e; 
											if  ((cause = cause.getCause()) != null){
										        if(cause instanceof ConnectException){
													logger.debug("#  ConnectException: "+e.getMessage().toString());
													logger.debug("# Reintentando conexion "+vuelta+"/"+reintentos +"...");
													noPoliza = false;
										        }else {
													logger.error("# Error en WS Exception: "+e.getMessage().toString()+e);
													polR.setDescripcion(polR.getReferencia() +": Error al llamar al WS");
													noPoliza = false;
													break;
												}
										    }else {
										    	logger.error("# Error en WS Exception  : "+e.getMessage().toString()+e);
												polR.setDescripcion(polR.getReferencia() +": Error al llamar al WS");
												noPoliza = false;
												break;
										    }
										}else {
											logger.debug("# Error exception tipo: "+e.getClass().toString());
											logger.error("# Error en WS Exception..: "+e.getMessage().toString()+e);
											polR.setDescripcion(polR.getReferencia() +": Error al llamar al WS");
											noPoliza = false;
											break;
										}
									}
								} // fin bucle ciclo
								// BORRAR!!
								//logger.debug("## Alta  poliza: "+polR.getReferencia() +" LINEA: " + polR.getLinea() + " PLAN: "+planActual.toString() +" ## ");
								
								logger.debug("ENTRA LINEA 298 procesar");
								if (noPoliza) {
									logger.debug("ENTRA LINEA 300 procesar");
									// Convierte la respuesta del servicio en un objeto poliza
									es.agroseguro.contratacion.PolizaDocument polDoc= es.agroseguro.contratacion.PolizaDocument.Factory.parse(acuse);
									es.agroseguro.contratacion.Poliza pp = polDoc.getPoliza();			
									
									// Convierte la respuesta del servicio en un objeto renovacion
									Renovacion renovacion = EstadoRenovacionDocument.Factory.parse(acuseEstadoRenovacion).getEstadoRenovacion().getRenovacion();
									
									
									// ALTA POLIZA
									Long linId = BBDDProcesarPolRenUtil.getlineaseguroid(session, pp.getPlan(), pp.getLinea());
									Poliza polizaHbm = null;
									try {
										logger.debug("ENTRA LINEA 313 procesar");
										polizaHbm = altaPoliza(session,pp,colHbm,aseguradoHbm,linId,polR,polizaRen, renovacion,polizasPctComisionesManager,pDao);
										Poliza polGuardada = polDao.getPolizaByReferenciaPlan(pp.getReferencia(), 'P', BigDecimal.valueOf(pp.getPlan()));
										
										logger.debug("Insertando en tabla GED poliza " + polGuardada.getReferencia() + "-" + polGuardada.getIdpoliza());
										DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
										Date today = Calendar.getInstance().getTime();
										String fechaFirma = df.format(today);
										
										String codBarras = generateBarCode(session, paramDao);
										
										logger.debug("Codigo de barras generado: " + codBarras);
										
										
										String strGed = "insert into O02AGPE0.tb_ged_doc_poliza values (" + polGuardada.getIdpoliza() + ", 'N/A', 1, 'N', SYSDATE, '@BATCH', '" + codBarras + "')";
										logger.debug(strGed);
										session.createSQLQuery(strGed).executeUpdate();
										
										
									} catch (Exception ex) {
										logger.error("## Error al crear la poliza: "+polR.getReferencia() +" LINEA: " + polR.getLinea() + " PLAN: "+planActual.toString() +" ## ", ex);
										if (polizaHbm != null) {
											session.delete(polizaHbm);
											polizaHbm = null;
										}
									}
									if (polizaHbm != null) {
										try {
											
											Transaction trans2   = session.beginTransaction();
		
											Set<ModuloPoliza> modulosPolHbm;
											ModuloPoliza moduloPolHbm;
		
											modulosPolHbm = new HashSet<ModuloPoliza>();
											ModuloPolizaId modId = new ModuloPolizaId();
											BigDecimal secuencia = BBDDProcesarPolRenUtil.getSecuenciaComparativa(session);
											modId.setNumComparativa(secuencia.longValue());
											modId.setCodmodulo(pp.getCobertura().getModulo().trim());
											modId.setLineaseguroid(linId);
											modId.setIdpoliza(polizaHbm.getIdpoliza());
											moduloPolHbm = new ModuloPoliza(modId,polizaHbm,null,1);
		
											modulosPolHbm.add(moduloPolHbm);
											session.saveOrUpdate(moduloPolHbm);
											
											trans2.commit();
											
										} catch (Exception ex) {
											logger.error("## Error al crear el modulo de la poliza : "+polR.getReferencia() +" LINEA: " + polR.getLinea() + " PLAN: "+planActual.toString() +" ## ",ex);
										}
										try {					
											//IGT - DISTRIBUCION DE COSTES 2015
											if (renovacion.getCosteGrupoNegocioArray() != null) {
												
												Transaction trans3   = session.beginTransaction();
												
												List<DistribucionCoste2015> dt2015List = new ArrayList<DistribucionCoste2015>(renovacion.getCosteGrupoNegocioArray().length);
												
												for (es.agroseguro.estadoRenovacion.GrupoNegocioPrimas gnp : renovacion.getCosteGrupoNegocioArray()) {
													
													DistribucionCoste2015 dt2015 = new DistribucionCoste2015();
													
													dt2015.setCodmodulo(polizaHbm.getCodmodulo());
													dt2015.setFilacomparativa(BigDecimal.ZERO);
													dt2015.setPrimacomercial(gnp.getPrimaComercial());
													dt2015.setPrimacomercialneta(gnp.getPrimaComercialBaseNeta());
													dt2015.setRecargoconsorcio(BigDecimal.ZERO);
													dt2015.setReciboprima(BigDecimal.ZERO);
													dt2015.setCostetomador(BigDecimal.ZERO);
													dt2015.setTotalcostetomador(renovacion.getCosteTotalTomador());
													dt2015.setGrupoNegocio(gnp.getGrupoNegocio().trim().charAt(0));
													dt2015.setRecargoaval(BigDecimal.ZERO);
													dt2015.setRecargofraccionamiento(BigDecimal.ZERO);
													
													dt2015.setPoliza(polizaHbm);
													
													session.saveOrUpdate(dt2015);
													
													logger.debug("Creada Distribución de Coste 2015.");
													
													//IGT - BONIFICACION RECARGO 2015
													if (gnp.getBonificacionRecargoArray() != null) {
														
														List<BonificacionRecargo2015> bf2015List = new ArrayList<BonificacionRecargo2015>(gnp.getBonificacionRecargoArray().length);

														for (es.agroseguro.estadoRenovacion.BonificacionRecargo br : gnp.getBonificacionRecargoArray()) {
															BonificacionRecargo2015 bf2015 = new BonificacionRecargo2015();
															
															bf2015.setCodigo(BigDecimal.valueOf(br.getCodigo()));
															bf2015.setImporte(br.getImporte());
															
															bf2015.setDistribucionCoste2015(dt2015);
															
															session.saveOrUpdate(bf2015);
															
															logger.debug("Creado registro de Bonificación Recargo 2015.");
															
															bf2015List.add(bf2015);
														}
														
														dt2015.getBonificacionRecargo2015s().addAll(bf2015List);
													} else {
														logger.debug("Distribución de coste sin BonificacionRecargo en SW para la póliza " + polR.getReferencia());
													}													
													
													dt2015List.add(dt2015);
												}
												
												polizaHbm.getDistribucionCoste2015s().addAll(dt2015List);
												
												trans3.commit();
											} else {
												logger.debug("Renovación sin CosteGrupoNegocio en SW para la póliza " + polR.getReferencia());
											}
										} catch (Exception ex) {
											logger.error("## Error al guardar la distrubución de costes: "+polR.getReferencia() +" LINEA: " + polR.getLinea() + " PLAN: "+planActual.toString() +" ## ");
											logger.error("###########################");
											logger.error(ex);
											logger.error("###########################");
										}
										
										try {
											// Clase
											Object[] campos= BBDDProcesarPolRenUtil.checkClasePolizaAnterior(pp.getReferencia(),pp.getPlan()-1,session);
											if (campos != null){
												clase   = ((BigDecimal) campos[0]);
												codUsuario = ((String) campos[1]);
												codOficina = ((String) campos[2]);
											}
											
											if (clase != null){
												polizaHbm.setClase(clase);
											}
											Usuario usuario = null;
											if (!codUsuario.equals("")){
												usuario = BBDDProcesarPolRenUtil.getUsuario(codUsuario, session);
												polizaHbm.setUsuario(usuario);
											}
											if (codOficina != null)
												polizaHbm.setOficina(codOficina);
										} catch (Exception ex) {
											logger.error("## Error al guardar la clase de la poliza",ex);
										}
										
										try {
											// Explotaciones
											Explotacion expInicial = populateExplotaciones(polizaHbm,pp, session,linId);
											if (expInicial != null && clase == null){
												clase = getClasePoliza( expInicial,session);
												if (clase != null){
													polizaHbm.setClase(clase);
													Transaction transFinal   = session.beginTransaction();
													session.saveOrUpdate(polizaHbm);
													transFinal.commit();
												}
											}
											polR.setDescripcion("OK");
											polInsertadas++;
											logger.debug("# poliza "+polizaHbm.getReferencia() + " INSERTADA # Total: "+polInsertadas+" # ");
										} catch (Exception ex) {
											logger.error("## Error al crear las explotaciones de la poliza: "+polR.getReferencia() +" LINEA: " + polR.getLinea() + " PLAN: "+planActual.toString() +" ## ",ex);
											session.delete(polizaHbm);
										}
										
										try {	
											// actualizamos el historico de estados de la poliza
											actualizarHistoricoPoliza(polizaHbm,session);
										} catch (Exception ex) {
											logger.error("## Error al guardar en el historico la poliza: "+polizaHbm.getReferencia(),ex);
										}
									} // FIN polizaHbm != null
								}
								
							}else {// asegurado NO existe
								//logger.debug("# No se encontro el Asegurado "+ polR.getNifAsegurado()+" - PLAN: "+planActual.toString() +" LINEA: " + polR.getLinea() + " REF: "+polR.getReferencia() +" #");
								
								polR.setDescripcion(polR.getReferencia() +": No se encontro el asegurado");
							}
						}else {// colectivo NO existe
							//logger.debug("# No se encontro el Colectivo "+ polR.getIdColectivo()+" - PLAN: "+planActual.toString() +" LINEA: " + polR.getLinea() + " REF: "+polR.getReferencia() +" #");
							
							polR.setDescripcion(polR.getReferencia() +": No se encontro el colectivo");
						}
						polizasRev++;
					}else {
						logger.debug(" ### Ha ocurrido un error al recoger la poliza "+ ref+ " en BBDD #");
					}
			}						
		logger.debug("## TOTAL POLIZAS INSERTADAS: "+polInsertadas+ " ## ");
		} catch  (Exception ex) {
			logger.debug("# Error general en WS Exception: "+ex.getMessage().toString(),ex);
			return lstRes;
		}
		
		return lstRes;
	}
	
	/**
	 * Metodoque realiza el alta de la poliza a partir de los datos de WS
	 * @param session
	 * @param pp datos devueltos por el WS de la poliza a clonar
	 * @param colHbm colectivo a insertar en la nueva poliza
	 * @param aseguradoHbm asegurado a insertar en la nueva poliza
	 * @return
	 * @throws Exception
	 */
	private static Poliza altaPoliza(final Session session, final es.agroseguro.contratacion.Poliza pp,
			final Colectivo colHbm, Asegurado aseguradoHbm,final Long linId, final PolizaRenBean polR, final PolizaRenovable polizaRen,
			final Renovacion r, final PolizasPctComisionesManager polizasPctComisionesManager, final PolizasPctComisionesDao pDao) throws Exception {
		
		// ALTA POLIZA
		Transaction trans   = session.beginTransaction();
		Poliza polizaHbm = new Poliza();
		
		// LINEA de Seguro
		polizaHbm.setLinea(getLineaSeguroBBDD(
				BigDecimal.valueOf(pp.getLinea()),
				BigDecimal.valueOf(pp.getPlan()), session));

		// Colectivo
		polizaHbm.setColectivo(colHbm);
		
		// Asegurado
		polizaHbm.setAsegurado(aseguradoHbm);
		polizaHbm.setDiscriminante(aseguradoHbm.getDiscriminante());
		
		// Usuario
		polizaHbm.setUsuario(aseguradoHbm.getUsuario());
		
		// importe
		if (pp.getCostePoliza() != null){
			CostePoliza costePoliza = pp.getCostePoliza();
			if (costePoliza.getTotalCosteTomador() != null)
				polizaHbm.setImporte(costePoliza.getTotalCosteTomador());
		}

		polizaHbm.setFechaenvio(polizaRen.getFechaRenovacion());
		
		// Estado poliza
		int estado = Integer.parseInt(polR.getEstado());
		estado = estado +10;
		polizaHbm.setEstadoPoliza(getEstadoPolizaBBDD(session,estado));

		// estado de pago
		polizaHbm.setEstadoPagoAgp(getEstadoPagoBBDD(session)); //no pagada
		
		// Datos fijos o de importacion directa desde la situacion actual
		polizaHbm.setExterna(PPRConstants.POLIZA_EXTERNA);
		polizaHbm.setTipoReferencia(Constants.MODULO_POLIZA_PRINCIPAL);
		polizaHbm.setTienesiniestros(Constants.CHARACTER_N);
		polizaHbm.setTieneanexomp(Constants.CHARACTER_N);
		polizaHbm.setTieneanexorc(Constants.CHARACTER_N);
		polizaHbm.setPacCargada(Constants.CHARACTER_N);
		polizaHbm.setReferencia(pp.getReferencia());
		polizaHbm.setDc(BigDecimal.valueOf(pp.getDigitoControl()));
		polizaHbm.setCodmodulo(pp.getCobertura().getModulo().trim());
		polizaHbm.setOficina(polizaHbm.getUsuario().getOficina().getId()
				.getCodoficina().toString());
		
		
		// MPM - Insertar los datos del pago presentes en el estado de renovacion de la poliza
		if (r != null) {
			PagoPoliza pago = new PagoPoliza();
			pago.setPoliza(polizaHbm);
			pago.setFormapago(r.getForma() != null ? r.getForma().charAt(0) : 'C');
			pago.setFecha(r.getFechaDomiciliacion() != null ? r.getFechaDomiciliacion().getTime() : null);
			pago.setImporte(r.getImporteDomiciliar());
			// Si vienen informados los datos del IBAN 
			if (r.getIBAN() != null) {
				pago.setIban(r.getIBAN().substring(0,4));
				pago.setCccbanco(r.getIBAN().substring(4));
			}
			// Si no vienen informados se inserta el IBAN a 'ES' ya que es un dato obligatorio en la BBDD
			else {
				pago.setIban("ES");
			}			
			
			pago.setDomiciliado(r.getDomiciliado() != null ? r.getDomiciliado().charAt(0) : null);
			pago.setTipoPago(new BigDecimal(0));
			
			/* Pet. 540460** MODIF TAM (14.09.2018) ** Inicio */
			if (pp.getPago() != null) {
				if (pp.getPago().getDomiciliado().equals("S") && pp.getPago().getCuenta()  != null){
					logger.debug("# Actualizamos pagos (Domiciliado)..");
					// Si vienen informados los datos del IBAN los actualizamos en tablas
					if (pp.getPago().getCuenta().getIban() != null) {
						pago.setIban(pp.getPago().getCuenta().getIban().substring(0,4));
						pago.setCccbanco(pp.getPago().getCuenta().getIban().substring(4));
					}else {// Si no vienen informados se inserta el IBAN a 'ES' ya que es un dato obligatorio en la BBDD
						pago.setIban("ES");
					}
					
					/* Si el pago está domiciliado y la cuenta informada, quiere decir que se ha enviado el Iban a Agrsoseguro*/
					if (pp.getPago().getCuenta()!= null){
						pago.setEnvioIbanAgro('S');
					}

					logger.debug("**@@** Valor del titular: " + pp.getPago().getCuenta().getTitular());
					if (pp.getPago().getCuenta().getTitular() != null && pp.getPago().getCuenta().getTitular() != "null"){
						pago.setTitularCuenta(pp.getPago().getCuenta().getTitular());	
					}else{
						pago.setTitularCuenta("");
					}
				
					pago.setDestinatarioDomiciliacion(pp.getPago().getCuenta().getDestinatario()!= null ? pp.getPago().getCuenta().getDestinatario().charAt(0) : null);

				}
			}
			/* Pet. 540460** MODIF TAM (14.09.2018) ** Inicio */
			
			if (pp.getCuentaCobroSiniestros() != null
					&& !StringUtils.isNullOrEmpty(pp.getCuentaCobroSiniestros().getIban())) {
				pago.setIban2(pp.getCuentaCobroSiniestros().getIban().substring(0, 4));
				pago.setCccbanco2(pp.getCuentaCobroSiniestros().getIban().substring(4));
			}
			
			Set<PagoPoliza> pagosPoliza = new HashSet<PagoPoliza>();
			pagosPoliza.add(pago);
			polizaHbm.setPagoPolizas(pagosPoliza);
		}
		
		session.saveOrUpdate(polizaHbm);
		trans.commit();
		
		//Insertamos los porcentajes de comisiones;
		Transaction trans2   = session.beginTransaction();
		String  resultado = getPctComisiones(polizaHbm, r,session,polizasPctComisionesManager,pDao);
		if (resultado.equals("OK")) {
			session.saveOrUpdate(polizaHbm);
			trans2.commit();
			return polizaHbm;
		}else {
			session.delete(polizaHbm);
			trans2.commit();
			logger.debug("## borrado poliza  "+ polizaHbm.getReferencia()+ " plan: "+ polizaHbm.getLinea().getCodplan() + " ## ");
			polR.setDescripcion(resultado);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String getPctComisiones(final Poliza polizaHbm,
			 Renovacion r,final Session session, final PolizasPctComisionesManager polizasPctComisionesManager, final PolizasPctComisionesDao pDao) throws Exception{
		int plan=polizaHbm.getLinea().getCodplan().intValue();
		int linea=polizaHbm.getLinea().getCodlinea().intValue();
		List <String> gruposN = new ArrayList<String>();
		List <String> gruposNFinal = new ArrayList<String>();
		if (r.getGastosArray() != null && r.getGastosArray().length>0){
			boolean gastosCero = false;
			for (int i=0; i<r.getGastosArray().length;i++) {
				BigDecimal gastosAdmin=r.getGastosArray(i).getAdministracion();
				BigDecimal gastosAdq=r.getGastosArray(i).getAdquisicion();
				BigDecimal gastosComisionMed=r.getGastosArray(i).getComisionMediador();
				
				if (gastosAdmin.compareTo(new BigDecimal(0)) ==0 && gastosAdq.compareTo(new BigDecimal(0)) ==0  && gastosComisionMed.compareTo(new BigDecimal(0)) ==0){
					logger.debug("## Gastos adq, adm y comisionMed a 0 en la llamada al WS, asignamos gastos desde el mantenimiento de parametros generales");
					gastosCero = true;
					break;
				}else{
					Character grupoNegocio=r.getGastosArray(i).getGrupoNegocio().charAt(0);	
					//if (!r.getGastosArray(i).getGrupoNegocio().toString().equals("2")){
						Calendar fechaEfecto=r.getFechaRenovacion();
						logger.debug("## getPctComisiones - plan "+ plan+ " ## "); 
						logger.debug("## getPctComisiones - linea "+ linea + " ## ");
						logger.debug("## getPctComisiones - gastosAdmin "+ gastosAdmin.toString()+ " ## ");
						logger.debug("## getPctComisiones - gastosAdq "+ gastosAdq.toString()+ " ## ");
						logger.debug("## getPctComisiones - gastosComisionMed "+ gastosComisionMed.toString()+ " ## ");
						logger.debug("## getPctComisiones - grupoNegocio "+ grupoNegocio.toString()+ " ## ");
						SubentidadMediadora sm= polizaHbm.getColectivo().getSubentidadMediadora();
						logger.debug("## getPctComisiones - llamamos a populateComisionesProcesar ## ");
						try {
							populateComisionesProcesar(polizaHbm, plan, linea, fechaEfecto,gastosAdmin, gastosAdq, gastosComisionMed, sm, session, grupoNegocio);			
	
						logger.debug("## getPctComisiones - gastosAdmin despues "+ polizaHbm.getPolizaPctComisiones().getPctadministracion().toString()+ " ## ");
						logger.debug("## getPctComisiones - gastosAdq  despues "+ polizaHbm.getPolizaPctComisiones().getPctadquisicion().toString()+ " ## ");
						logger.debug("## getPctComisiones - gastosComisionMed  despues "+ polizaHbm.getPolizaPctComisiones().getPctesmediadora().toString()+ " ## ");
						gruposN.add(grupoNegocio.toString());
						} catch (Exception e) {
							logger.error("Exception: ",e);
							return e.getMessage();
						}
					//}
				}
				
			}
			List<LineaGrupoNegocio> gruposNeg = new ArrayList <LineaGrupoNegocio>();
			//gruposN.clear();
			//gruposN.add("1");
			if (gruposN.size()>0){ // recojo los grupos de negocio informados
				gruposNeg = ((List<LineaGrupoNegocio>) pDao.getObjects(LineaGrupoNegocio.class, "id.lineaseguroid", polizaHbm.getLinea().getLineaseguroid()));
				for (LineaGrupoNegocio gN:gruposNeg){
					if (!gruposN.contains(gN.getId().getGrupoNegocio().toString())){
						gruposNFinal.add(gN.getId().getGrupoNegocio().toString());
						logger.debug("Gn a anhadir: "+gN.getId().getGrupoNegocio().toString());
					}
				}
			}	
			if 	(gruposNFinal.size()>0){
				asignarGastos(polizaHbm,polizasPctComisionesManager,session,gruposNFinal);
			}
				
			
			if (gastosCero){
				asignarGastos(polizaHbm,polizasPctComisionesManager,session,null);
			}
		}else{
			logger.debug("## Poliza renovable sin gastos en la llamada al WS ");
			logger.debug("## Asignamos gastos desde el mantenimiento de parametros generales");
			asignarGastos(polizaHbm,polizasPctComisionesManager,session,null);
		}
		return "OK";
	}
		
	
	@SuppressWarnings("unchecked")
	private static void asignarGastos(final Poliza polizaHbm, final PolizasPctComisionesManager polizasPctComisionesManager,
			final Session session, List <String> gruposN){
		Map<String, Object> paramsComs = new HashMap<String, Object>();
		try {
			paramsComs = polizasPctComisionesManager.validaComisiones (polizaHbm,null);
			if (paramsComs.get("alerta") != null) {
				logger.debug(" ERROR EN COMISIONES: "+paramsComs.get("alerta"));
			}			
			if (paramsComs.get("polizaPctComisiones")!= null) {
				List<PolizaPctComisiones> listappc = (List<PolizaPctComisiones>) paramsComs.get("polizaPctComisiones");
				for (PolizaPctComisiones polizaPctComisiones : listappc) {
					if (gruposN == null || gruposN.contains(polizaPctComisiones.getGrupoNegocio().toString())){
						logger.debug(" Guardamos pctComisiones");
						logger.debug(" pctComisiones GN: "+polizaPctComisiones.getGrupoNegocio().toString());
						logger.debug(" pctComisiones Adm: "+polizaPctComisiones.getPctadministracion());
						logger.debug(" pctComisiones Adq: "+polizaPctComisiones.getPctadquisicion());
						logger.debug(" pctComisiones Pctcommmax: "+polizaPctComisiones.getPctcommax());
						logger.debug(" pctComisiones Pctdescmax: "+polizaPctComisiones.getPctdescmax());
						logger.debug(" pctComisiones PctEntidad: "+polizaPctComisiones.getPctentidad());
						logger.debug(" pctComisiones PctEsMed: "+polizaPctComisiones.getPctesmediadora());
						polizaPctComisiones.setPoliza(polizaHbm);
						session.saveOrUpdate(polizaPctComisiones);
						logger.debug(" FIN Guardamos pctComisiones");
					}
				}	
			}
			
		} catch (Exception e) {
			logger.error("Error al asignar gastos: ",e);
			
		}
	}
	
	
	// Metodo que valida que la LINEA de seguro recibida del WS
	// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importacion
	private static Linea getLineaSeguroBBDD(final BigDecimal codlinea,
			final BigDecimal codplan, final Session session) throws Exception {
		Linea lineaHbm;
		Criteria crit = session.createCriteria(Linea.class)
				.add(Restrictions.eq("codlinea", codlinea))
				.add(Restrictions.eq("codplan", codplan));

		lineaHbm = (Linea) crit.uniqueResult();
		if (lineaHbm == null) {throw new Exception("# No se encuentra la LINEA de seguro. Revise los datos: codlinea "
							+ codlinea + ", codplan " + codplan);
		}

		return lineaHbm;
	}
	
	// Metodo que valida que el asegurado recibido en la situacion actual
		// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
		// necesario para las referencias externas de la poliza en la importacion
		private static Asegurado getAseguradoBBDD(final String nifAsegurado,
				final BigDecimal codentidad, final BigDecimal entMed, final BigDecimal entSubMed,final Session session)
				throws Exception {
			Asegurado aseguradoHbm;
			try {
				Criteria crit = session.createCriteria(Asegurado.class)
						.createAlias("entidad", "entidad")
						.createAlias("usuario", "usuario")
						.add(Restrictions.eq("nifcif", nifAsegurado))
						.add(Restrictions.eq("entidad.codentidad", codentidad))
						.add(Restrictions.eq("usuario.subentidadMediadora.id.codentidad", entMed))
						.add(Restrictions.eq("usuario.subentidadMediadora.id.codsubentidad", entSubMed));
				aseguradoHbm = (Asegurado) crit.uniqueResult();				
			} catch (Exception ex) {
				logger.error(" Error al comprobar el asegurado: "+ nifAsegurado+" , codent: " + codentidad +" entMed: "+entMed+" subMed: "+entSubMed, ex);
				return null;
			}
			/*
			if (aseguradoHbm == null) {
				logger.debug("# El asegurado: "+ nifAsegurado + ", codent: " + codentidad +" entMed: "+entMed+" subMed: "+entSubMed+ " NO EXISTE #");
			}
			*/
			return aseguradoHbm;
		}
		
		// Metodo que valida que el estado de la poliza asociado a la misma por
		// configuracion
		// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
		// necesario para las referencias externas de la poliza en la importacion
		protected static EstadoPoliza getEstadoPolizaBBDD(final Session session,final int estado)
				throws Exception {

			EstadoPoliza estadoHbm;

			estadoHbm = (EstadoPoliza) session.get(EstadoPoliza.class,new BigDecimal(estado));

			if (estadoHbm == null) {
				throw new Exception("# No se encuentra el estado de la poliza. Revise los datos: idEstado "
								+ Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
			}

			return estadoHbm;
		}
		
		// Metodoque valida que el estado del pago asociado a la poliza por
		// configuracion
		// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
		protected static EstadoPagoAgp getEstadoPagoBBDD(final Session session)
				throws Exception {

			EstadoPagoAgp estadoPagoHbm;

			estadoPagoHbm = (EstadoPagoAgp) session.get(EstadoPagoAgp.class,Constants.POLIZA_NO_PAGADA);

			if (estadoPagoHbm == null) {
				throw new Exception("# No se encuentra el estado del pago. Revise los datos: idPagoAgp "+ Constants.POLIZA_NO_PAGADA);
			}

			return estadoPagoHbm;
		}
	
		// Metodoque transforma las explotaciones de la llamada a WS
		// principal en el formato esperado por el modelo de datos de Agroplus y
		// puebla el objeto Hibernate encargado de la importacion
		public static void guardaDistribucionCoste(final Poliza polizaHbm,final es.agroseguro.contratacion.Poliza pp,
					final Session session) throws Exception {
			Transaction trans   = null;
			trans = session.beginTransaction();
			Set<DistribucionCoste2015> distribucionCoste2015s = new HashSet<DistribucionCoste2015>(0);
			DistribucionCoste2015 dc = new DistribucionCoste2015();
			try {
				
				es.agroseguro.contratacion.costePoliza.Financiacion financiacion = null;
				if (pp.getCostePoliza() !=null) {
					CostePoliza costePoliza = pp.getCostePoliza();
					if (costePoliza != null) {
						CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza.getCosteGrupoNegocioArray();
						if (costeGrupoNegocioArray != null) {
							financiacion = costePoliza.getFinanciacion();
							for (CosteGrupoNegocio costeGrupoNeg: costeGrupoNegocioArray) {
								if ( costeGrupoNeg != null) {
									// Rellena el objeto DistribucionCoste2015
									dc = crearDC2015Unificada(polizaHbm,null,null,costePoliza, costeGrupoNeg,financiacion);
									// Carga las subvenciones de CCAA y ENESA en el objeto de la distribucion de costes
									dc = cargarSubvencionesDC(dc,costeGrupoNeg);
									// Bonificaciones y recargos
									dc = cargaBonifRecargUnificado(dc,costeGrupoNeg);
									distribucionCoste2015s.add(dc);
									session.saveOrUpdate(dc);
								}
							}
							trans.commit();
						}	
						
					}
				}
				
			}catch (Exception ex) {
				logger.error("# Ha ocurrido algun error al guardar la distribucion de costes", ex);
			}	
		}	
	
		private static DistribucionCoste2015 cargaBonifRecargUnificado(DistribucionCoste2015 dc,CosteGrupoNegocio costeGrupoNegocio) {
			es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] boniRecargo = costeGrupoNegocio.getBonificacionRecargoArray();
			if (boniRecargo != null) {
				// Bucle para anhadir las distribuciones de coste de las
				// Bonificaciones Recargo
				for (int i = 0; i < boniRecargo.length; i++) {
					BonificacionRecargo2015 bon = new BonificacionRecargo2015();
					bon.setDistribucionCoste2015(dc);
					bon.setCodigo(new BigDecimal(boniRecargo[i].getCodigo()));
					bon.setImporte(boniRecargo[i].getImporte());
					dc.getBonificacionRecargo2015s().add(bon);
				}
			}
			return dc;
		}
		
		
		private static DistribucionCoste2015 crearDC2015Unificada(Poliza polHbm,
				   String codModulo, BigDecimal filaComparativa,
				   CostePoliza costePoliza, CosteGrupoNegocio costeGrupoNegocio,
				   Financiacion financiacion) throws DAOException {

			DistribucionCoste2015 dc;
			dc = new DistribucionCoste2015();
			dc.setPoliza(polHbm);
			dc.setCodmodulo(codModulo);
			dc.setFilacomparativa(filaComparativa);
			dc.setCostetomador(costeGrupoNegocio.getCosteTomador());
			dc.setPrimacomercial(costeGrupoNegocio.getPrimaComercial());
			dc.setPrimacomercialneta(costeGrupoNegocio.getPrimaComercialNeta());
			dc.setRecargoconsorcio(costeGrupoNegocio.getRecargoConsorcio());
			dc.setReciboprima(costeGrupoNegocio.getReciboPrima());
			dc.setTotalcostetomador(costePoliza.getTotalCosteTomador());
			dc.setGrupoNegocio(costeGrupoNegocio.getGrupoNegocio().charAt(0));
			if (financiacion != null && financiacion.getRecargoAval()!= null) {
				dc.setRecargoaval(financiacion.getRecargoAval());
			}
			if (financiacion != null && financiacion.getRecargoFraccionamiento()!= null) {
				dc.setRecargofraccionamiento(financiacion.getRecargoFraccionamiento());
			}
		
		return dc;
		}	
	
		
		private static DistribucionCoste2015 cargarSubvencionesDC(final DistribucionCoste2015 dc,final CosteGrupoNegocio costeGrupoNegocio) {
			es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subCCAA = costeGrupoNegocio.getSubvencionCCAAArray();
			es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subEnesa = costeGrupoNegocio.getSubvencionEnesaArray();
			// Subvenciones CCAA
			if (subCCAA != null) {
				for (int i = 0; i < subCCAA.length; i++) {
					// Bucle para anhadir las distribuciones de coste de las
					// subvenciones CCAA
					DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
					subv.setDistribucionCoste2015(dc);
					subv.setCodorganismo(subCCAA[i].getCodigoOrganismo().charAt(0));
					subv.setImportesubv(subCCAA[i].getImporte());
					dc.getDistCosteSubvencion2015s().add(subv);
				}
			}
			// Subvenciones ENESA
			if (subEnesa != null) {
				
				for (int i = 0; i < subEnesa.length; i++) {
					DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
					subv.setDistribucionCoste2015(dc);
					subv.setCodorganismo('0');
					subv.setCodtiposubv(new BigDecimal(subEnesa[i].getTipo()));
					subv.setImportesubv(subEnesa[i].getImporte());
					dc.getDistCosteSubvencion2015s().add(subv);
				}
			}
			return dc;
		}	
		
	// Metodoque transforma las explotaciones de la llamada a WS
	// principal en el formato esperado por el modelo de datos de Agroplus y
	// puebla el objeto Hibernate encargado de la importacion
	public static Explotacion populateExplotaciones(final Poliza polizaHbm,final es.agroseguro.contratacion.Poliza poliza,
				final Session session, final Long lineaId) throws Exception {
		Transaction trans   = null;
		boolean primera = true;
		Map<String,Explotacion> mapaExplotacion = new HashMap <String,Explotacion>();
		try {
			ObjetosAsegurados objectosAsegurados  = poliza.getObjetosAsegurados();
			Map<String, RelacionEtiquetaTabla> auxEtiquetaTabla = getCodConceptoEtiquetaTablaExplotaciones(lineaId,session);
			Node node = objectosAsegurados.getDomNode().getFirstChild();
			
			while (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) { 
					trans = session.beginTransaction();
		            es.agroseguro.contratacion.explotacion.ExplotacionDocument explotacionDocumento = null;
		            try { 
		            	explotacionDocumento = es.agroseguro.contratacion.explotacion.ExplotacionDocument.Factory.parse(node);
		            } catch (XmlException e) { 
		                logger.error("Error al parsear una explotacion.", e); 
		                
		            }
		            if (explotacionDocumento != null){
			            es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion = explotacionDocumento.getExplotacion();
			            Explotacion explotacionBean = new Explotacion();
			          
			         	if(explotacion.getNumero() != 0) {
			            	explotacionBean.setNumero(explotacion.getNumero());
			         	}
			            if(explotacion.getRega() != null){
			            	explotacionBean.setRega(explotacion.getRega());
			            }
			            if(explotacion.getSigla() != null){
			            	explotacionBean.setSigla(explotacion.getSigla());
			            } else {
			            	explotacionBean.setSigla("");
			            }
			            if(explotacion.getSubexplotacion() != 0){
			            	explotacionBean.setSubexplotacion(explotacion.getSubexplotacion());
			            }
			            explotacionBean.setEspecie(Long.parseLong(Integer.toString(explotacion.getEspecie())));
			            explotacionBean.setRegimen(Long.parseLong(Integer.toString(explotacion.getRegimen())));
			
			            // Coordenadas
			            agregarCoordenadas(explotacion, explotacionBean);
			            
			            //ambito (Termino)
			            Termino termino = obtenerTermino(session, explotacion);
			            	
		            	if (termino != null) {
		            		logger.debug(new StringBuilder("#").append(termino.getId().getSubtermino()).append("#"));
			            	explotacionBean.setTermino(termino);
			            	
			            	Set<GrupoRaza> grupoRazaSet = new HashSet<GrupoRaza>(0);
			            	
			            	DatosVariables datosVariablesExplotacion = explotacion.getDatosVariables();
			            	String codigoModulo = poliza.getCobertura().getModulo().trim();
			            	
			            	es.agroseguro.contratacion.explotacion.GrupoRaza[] grupoRazaArray = explotacion.getGrupoRazaArray();
			            	for (es.agroseguro.contratacion.explotacion.GrupoRaza grupoRaza : grupoRazaArray) {
		            			
			            		DatosVariables datosVariablesRaza = grupoRaza.getDatosVariables();
			            		Long codigoGrupoRaza = new Long(grupoRaza.getGrupoRaza());
			            		es.agroseguro.contratacion.explotacion.CapitalAsegurado[] capitalAseguradoArray = grupoRaza.getCapitalAseguradoArray();
			            		
		            			for (es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalAsegurado : capitalAseguradoArray) {
			            			
			            			DatosVariables datosVariablesCapitalAsegurado = capitalAsegurado.getDatosVariables();
			            			BigDecimal codigoTipoCapitalAsegurado = new BigDecimal(capitalAsegurado.getTipo());
			            			es.agroseguro.contratacion.explotacion.Animales[] animalesArray = capitalAsegurado.getAnimalesArray();
			            			
			            			for (es.agroseguro.contratacion.explotacion.Animales animal: animalesArray) {
			            				// Declaro la variables necesarias 
			            				GrupoRaza grupoRazaBean = new GrupoRaza();
			            				PrecioAnimalesModulo precioAnimalesModulo = new PrecioAnimalesModulo();
			            				Set<PrecioAnimalesModulo> precioAnimalesSet = new HashSet<PrecioAnimalesModulo>();
			            				Set<DatosVariable> datosVariablesSet = new HashSet<DatosVariable>();
			            				
			            				// Recupero los datos variables del animal
			            				DatosVariables datosVariablesAnimal = animal.getDatosVariables();
			            				
			            				// Establezco el grupo de raza, precio y codigo de modulo de los animales
			            				precioAnimalesModulo.setGrupoRaza(grupoRazaBean);
			            				precioAnimalesModulo.setPrecio(animal.getPrecio());
			            				precioAnimalesModulo.setCodmodulo(codigoModulo);
			            				precioAnimalesSet.add(precioAnimalesModulo);
			            				
				            			// AÃ±ado todos los datos variables
				            			datosVariablesSet = addDatVar(datosVariablesSet, datosVariablesAnimal, grupoRazaBean, session, auxEtiquetaTabla);
				            			datosVariablesSet = addDatVar(datosVariablesSet, datosVariablesCapitalAsegurado, grupoRazaBean, session, auxEtiquetaTabla);
				            			datosVariablesSet = addDatVar(datosVariablesSet, datosVariablesRaza, grupoRazaBean, session, auxEtiquetaTabla);
				            			datosVariablesSet = addDatVar(datosVariablesSet, datosVariablesExplotacion, grupoRazaBean, session, auxEtiquetaTabla);
				            			
				            			// Relleno todos los campos de grupoRazaBean
				            			grupoRazaBean.setNumanimales(new Long(animal.getNumero()));
			            				grupoRazaBean.setCodtipoanimal(new Long(animal.getTipo()));
			            				grupoRazaBean.setPrecioAnimalesModulos(precioAnimalesSet);
				            			grupoRazaBean.setExplotacion(explotacionBean);
				            			grupoRazaBean.setCodtipocapital(codigoTipoCapitalAsegurado);
				            			grupoRazaBean.setCodgruporaza(codigoGrupoRaza);
				            			grupoRazaBean.setDatosVariables(datosVariablesSet);
				            			
					            		// Finalmente aÃ±ado el animal al conjunto de animales
					            		grupoRazaSet.add(grupoRazaBean);
			            			}
			            		}
			            	}
			            	
			            	/** ESC-16132 ** MODIF TAM (09.12.2021) ** Guardar las coberturas de las explotaciones en caso de tenerlas contratadas ** INICIO **/

							Set<ExplotacionCobertura> explotacionCobertura = populateExplotacionesCoberturas(polizaHbm,
									poliza, session, lineaId, explotacionBean);

							if (explotacionCobertura != null) {
								explotacionBean.setExplotacionCoberturas(explotacionCobertura);
							}
							
			            	/* ESC-16132 ** MODIF TAM (09.12.2021) ** Guardar las coberturas de las explotaciones en caso de tenerlas contratadas * FIN*/
			            	
			            	explotacionBean.setGrupoRazas(grupoRazaSet);
			            	explotacionBean.setPoliza(polizaHbm);
		
			            	session.saveOrUpdate(explotacionBean);
			            	trans.commit();
			            	if(primera) {
			            		primera = false;
			            		mapaExplotacion.put("inicial", explotacionBean);
			            	} else {
			            		Explotacion expTemp = (Explotacion)mapaExplotacion.get("inicial");
			            		if (expTemp != null && expTemp.getNumero() != null && explotacionBean.getNumero() != null){
			            			if (explotacionBean.getNumero().compareTo(expTemp.getNumero()) == -1){
			            				mapaExplotacion.put("inicial", explotacionBean);
			            			}
			            		}
			            	}		
			            		
		            	} else { // no existe termino
		            		logger.debug("# Termino no encontrado #");
		            	}
		        	} // fin Node.ELEMENT_NODE 	
				} 	// fin cc != null
				node = node.getNextSibling();
			}	// FIN explotacion
			
			//return !primera ? (Explotacion) mapaExplotacion.get("inicial") : null;
			if (!primera){
				return (Explotacion) mapaExplotacion.get("inicial");
			} else {
				return null;
			}
		}catch (Exception ex) {
			if (trans != null) {
				logger.debug("# ProcesarPolizasRenovablesWs - Ha ocurrido algun error. Rollback de la transaccion",ex);
				
				trans.rollback();				
			}
			return null;
			//logger.error("# ProcesarPolizasRenovablesWs - Ha ocurrido algun error", ex);
		}
			
	}
	
	
	/** ESC-16132 ** MODIF TAM (09.12.2021) ** Guardar las coberturas de las explotaciones en caso de tenerlas contratadas ** INICIO **/
	public static Set<ExplotacionCobertura> populateExplotacionesCoberturas(final Poliza polizaHbm,
			final es.agroseguro.contratacion.Poliza poliza, final Session session, final Long lineaId,
			final Explotacion explotacionBean) throws Exception {

		logger.debug("**@@** ProcesarPolizasRenovablesWS - populateExplotacionesCoberturas [INIT]");
		Long idPoliza = polizaHbm.getIdpoliza();

		Poliza polizaAgr = new Poliza(idPoliza);
		
		if (poliza.getCobertura() != null) {
			polizaAgr.setCodmodulo(poliza.getCobertura().getModulo());
		}

		Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();

		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				ExplotacionDocument explotacion = null;
				try {
					explotacion = ExplotacionDocument.Factory.parse(currNode);
					logger.debug("**@@** Valor de explotacion:"+explotacion.toString());
					
					es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explot = explotacion
							.getExplotacion();

					Set<ExplotacionCobertura> coberturas = explot.getDatosVariables() != null
							? getExplotacionesCoberturas(explot.getDatosVariables().getRiesgCbtoElegArray(),
									polizaHbm, lineaId, session)
							: null;

					if (coberturas != null) {
						for (ExplotacionCobertura exp : coberturas) {
							exp.setExplotacion(explotacionBean);
						}
					}
					return coberturas;

				} catch (XmlException e) {
					logger.debug(
							"# ImportacionPolizasService (pupulateExplotacionesCoberturas) - Error al parsear una explotación.",
							e);
				}
			}
		}
		return null;

	}

	public static Set<ExplotacionCobertura> getExplotacionesCoberturas(
			es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido riesgos[], final Poliza polizaHbm,
			final Long lineaId, final Session session) throws DAOException {
		Set<ExplotacionCobertura> coberturas = null;

		String modulo = polizaHbm.getCodmodulo();

		if (null != riesgos && riesgos.length > 0) {
			coberturas = new HashSet<ExplotacionCobertura>(riesgos.length - 1);
			for (int i = 0; i < riesgos.length; i++) {
				ExplotacionCobertura cob = new ExplotacionCobertura();
				cob.setRiesgoCubierto((short) riesgos[i].getCodRCub());
				cob.setCpm((short) riesgos[i].getCPMod());
				cob.setElegida(riesgos[i].getValor().toCharArray()[0]);
				cob.setElegible('S');
				/* obtenemos el valor de fila */
				short fila = BBDDProcesarPolRenUtil.getFilaExplotacionCobertura(lineaId, modulo,
						riesgos[i].getCPMod(), riesgos[i].getCodRCub(), session);

				/* Obtenemos la descripción de cpm */
				String cpmDescripcion = BBDDProcesarPolRenUtil.getDescripcionConceptoPpalMod(riesgos[i].getCPMod(), session);

				/* Obtenemos la descripción de RC */
				String rcDescripcion = BBDDProcesarPolRenUtil.getDescripcionRiesgoCubierto(lineaId, modulo,
						riesgos[i].getCodRCub(), session);
				cob.setFila(fila);

				if (null == cpmDescripcion)
					cpmDescripcion = new String("");
				cob.setCpmDescripcion(cpmDescripcion);

				if (null == rcDescripcion)
					rcDescripcion = new String("");
				cob.setRcDescripcion(rcDescripcion);

				cob.setCodmodulo(modulo);

				coberturas.add(cob);
			}
		}
		return coberturas;
	}

	/** ESC-16132 ** MODIF TAM (09.12.2021) ** Guardar las coberturas de las explotaciones en caso de tenerlas contratadas ** FIN **/

	private static void agregarCoordenadas(
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion,
			Explotacion explotacionBean) {
		Coordenadas coord = explotacion.getCoordenadas();
		if (coord != null) {
			if  (coord.getLatitud() != 0) {
				explotacionBean.setLatitud(coord.getLatitud());
			}
			if  (coord.getLongitud() != 0) {
				explotacionBean.setLongitud(coord.getLongitud());
			}
		}
	}


	private static Termino obtenerTermino(
			final Session session,
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion) {
		Termino terminoHbm =  new Termino();
		Criteria criteria = session.createCriteria(Termino.class);
		criteria.add(Restrictions.eq("id.codprovincia",new BigDecimal(explotacion.getUbicacion().getProvincia())));
		criteria.add(Restrictions.eq("id.codcomarca"  ,new BigDecimal(explotacion.getUbicacion().getComarca())));
		criteria.add(Restrictions.eq("id.codtermino"  ,new BigDecimal(explotacion.getUbicacion().getTermino())));
		if (explotacion.getUbicacion().getSubtermino() != null && !explotacion.getUbicacion().getSubtermino().trim().equals("")){
			criteria.add(Restrictions.eq("id.subtermino",new Character(explotacion.getUbicacion().getSubtermino().charAt(0))));
		}else if (explotacion.getUbicacion().getSubtermino() != null){
			criteria.add(Restrictions.eq("id.subtermino",' '));
		}
		Ambito ubicacion = explotacion.getUbicacion();
		logger.debug(new StringBuilder("Provincia Explotacion: ").append(ubicacion.getProvincia())
				.append(" | Comarca Explotacion: ").append(ubicacion.getComarca())
				.append(" | Termito Explotacion: ").append(ubicacion.getTermino())
				.append(" | Subtermino Explotacion: ").append(ubicacion.getSubtermino()));
		terminoHbm = (Termino)criteria.uniqueResult();
		return terminoHbm;
	}
	
	/**
	 * 
	 * @param datosVariables
	 * @param datt
	 * @param grupRBean
	 * @param session
	 * @return
	 */
	public static Set<DatosVariable> addDatVar(final Set<DatosVariable> datosVariables, final DatosVariables datt,
			final GrupoRaza grupRBean,Session session,Map<String, RelacionEtiquetaTabla> auxEtiquetaTabla ) {
		NodeList childList2 = null;
		try{
			if (datt != null){
				childList2 = datt.getDomNode().getChildNodes();
				for (int j = 0; j < childList2.getLength(); j++) {  				
		            Node node2 = childList2.item(j);
		            String datNombre = node2.getLocalName();
		            String strCodConcepto = BBDDProcesarPolRenUtil.getCodconcepto(datNombre,auxEtiquetaTabla);	            
		            if (strCodConcepto != null && !strCodConcepto.equals("")) {
			            Integer codConcepto = Integer.parseInt(strCodConcepto);
			            if (codConcepto != null && codConcepto.compareTo(0) !=0) {
				            DatosVariable datBean = new DatosVariable();
							datBean.setCodconcepto(codConcepto);
							String val = node2.getAttributes().getNamedItem("valor").getNodeValue();
							datBean.setValor(val);
							datBean.setGrupoRaza(grupRBean);
							if (!datosVariables.contains(datBean))
								datosVariables.add(datBean);
			            }
					}
				}
			}
		}catch (Exception ex) {
				logger.error("# Error al procesar el dato variable:", ex);
				return datosVariables;
		}
		return datosVariables;
	}
	
	
	
	/**
	 * Metodoque obtiene un mapa cuya clave es el codigo de concepto de los
	 * datos variables de explotaciones y el valor es un objeto que contiene la
	 * etiqueta y la tabla asociadas al concepto.
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/LINEA
	 * @return Mapa con la informacion asociada a cada codigo de concepto de los
	 *         datos variables de explotaciones
	 */
	public static Map<String, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaExplotaciones(
			final Long lineaseguroid,Session session) {
		String sql = "select distinct o.codconcepto, dd.nomconcepto, dd.etiquetaxml, dd.numtabla "
				+ "from o02agpe0.tb_sc_oi_org_info o, o02agpe0.tb_sc_dd_dic_datos dd "
				+ "where o.codconcepto = dd.codconcepto and "
				+ "o.codubicacion in ("
				+ PPRConstants.UBICACION_EXPLOTACION
				+ ", "
				+ PPRConstants.UBICACION_GRUPO_RAZA
				+ ", "
				+ PPRConstants.UBICACION_CAP_ASEG
				+ ", "
				+ PPRConstants.UBICACION_ANIMALES
				+ ") and o.coduso = "
				+ PPRConstants.USO_POLIZA
				+ "  and o.lineaseguroid = " + lineaseguroid;
		@SuppressWarnings("unchecked")
		List<Object> busqueda = (List<Object>) session.createSQLQuery(sql).list();
		// Recorro la lista y voy rellenando el mapa
		Map<String, RelacionEtiquetaTabla> resultado = new HashMap<String, RelacionEtiquetaTabla>();
		for (Object elem : busqueda) {
			Object[] elemento = (Object[]) elem;
			RelacionEtiquetaTabla ret = new RelacionEtiquetaTabla(
					nullToString(elemento[1]),  //etiqueta
					nullToString(elemento[3]),  //tabla
					nullToString(elemento[0])); //codConcepto
			resultado.put(nullToString(elemento[2]), ret);
		}
		return resultado;
	}
	
	public static String nullToString(Object cad){
        try {
			if (cad == null || cad.equals("null"))
			    cad = "";
			return cad.toString();
		} catch (Exception e) {
			return "";
		}
        
    }
	
	public static class RelacionEtiquetaTabla {
		
		private String etiqueta;
		private String tabla;
		private String codConcepto;
		
		public RelacionEtiquetaTabla() {
		}
		
		public RelacionEtiquetaTabla(String etiqueta, String tabla, String codConcepto) {
			this.etiqueta = etiqueta;
			this.tabla = tabla;
			this.codConcepto = codConcepto;
		}
		
		public String getEtiqueta() {
			return etiqueta;
		}
		public void setEtiqueta(String etiqueta) {
			this.etiqueta = etiqueta;
		}
		public String getTabla() {
			return tabla;
		}
		public void setTabla(String tabla) {
			this.tabla = tabla;
		}

		public String getcodConcepto() {
			return codConcepto;
		}

		public void setCodConcepto(String codConcepto) {
			this.codConcepto = codConcepto;
		}

	}
	
	// Metodoque actualiza el historico de las polizas
	public static void actualizarHistoricoPoliza(final Poliza polizaHbm, final Session session) {
		String strHis = "insert into o02agpe0.tb_POLIZAS_HISTORICO_ESTADOS values"+
		"(o02agpe0.sq_POLIZAS_HISTORICO_ESTADOS.nextval,"+polizaHbm.getIdpoliza()+",'BATCH',sysdate,"+polizaHbm.getEstadoPoliza().getIdestado()+",null,null,null,null,null)";	
		Query qHis = session.createSQLQuery(strHis);
		qHis.executeUpdate();
		//logger.debug("ACTUALIZACION HISTORICO: " + strHis );
	}
	
	// ***************COMISIONES ***************************************************

		public static PolizaPctComisiones populateComisionesProcesar(final Poliza polizaHbm,
				int plan, int linea, Calendar fechaEfecto, BigDecimal gastosAdmin, 
				BigDecimal gastosAdq, BigDecimal gastosComisionMed, 
				SubentidadMediadora sm, final Session session, Character grupoNegocio) throws Exception{ 
			
			logger.debug("### ProcesarPolizasRenovablesWS.populateComisionesProcesar - ENTRA");
			// Si no se han obtenido los datos de la ES Mediadora no se pueden buscar las comisiones
			if (sm == null) {
				logger.debug("### ProcesarPolizasRenovablesWS.populateComisionesProcesar - SALE 1");
				throw new Exception("No se encuentran las comisiones ya que no se ha obtenido la ES Mediadora de la poliza");
			}
			
			CultivosSubentidades coms=getComisionesSubentidadesProcesar(plan, linea, fechaEfecto, sm.getId().getCodentidad(), sm.getId().getCodsubentidad());
			
			PolizaPctComisiones res=null;
			if(null!=coms){
				res=new PolizaPctComisiones();
				//res.setIdpoliza(polizaHbm.getIdpoliza());
				res.setPoliza(polizaHbm);
				//OJO llenar
				res.setPctadministracion(gastosAdmin);
				res.setPctadquisicion(gastosAdq);
				res.setPctcommax(gastosComisionMed);
				res.setPctentidad(coms.getPctentidad());
				res.setPctesmediadora(coms.getPctmediador());
				res.setGrupoNegocio(grupoNegocio);			
				session.saveOrUpdate(res);
				
				//polizaHbm.getSetPolizaPctComisiones().add(res);
				polizaHbm.setPolizaPctComisiones(res);
				logger.debug(" # pctComisiones de la poliza "+polizaHbm.getReferencia()+" GN: "+grupoNegocio+ " insertados #");
				logger.debug("### ProcesarPolizasRenovablesWS.populateComisionesProcesar - SALE 2");
				//}
			}else{
				logger.debug("### ProcesarPolizasRenovablesWS.populateComisionesProcesar - SALE 3");
				throw new Exception(
						polizaHbm.getReferencia() +": No se encuentran las comisiones del plan " + plan + ", LINEA " + linea + ", fecha de efecto " + fechaEfecto.toString()
						+ ", entMed " + sm.getId().getCodentidad() + ", subEntMed " + sm.getId().getCodsubentidad());
			}
			
			return res;
		} 
		
		private static CultivosSubentidades getComisionesSubentidadesProcesar( int plan, int linea, Calendar fechaEfecto, BigDecimal entMed, BigDecimal subEntMed) throws Exception{
			logger.debug("Seleccionamos las comisiones en la tabla de comisiones de subentidades por fecha de efecto. ");		
			CultivosSubentidades res=null;
			Conexion c = new Conexion();		
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String fecEfecto= df.format(fechaEfecto.getTime());
			try {
				logger.debug("### ProcesarPolizasRenovablesWS.getComisionesSubentidadesProcesar - ENTRA");
				
				String sql ="select coms.PCTENTIDAD, coms.PCTMEDIADOR " +     
						"from o02agpe0.TB_COMS_CULTIVOS_SUBENTIDADES coms " +
						"inner join O02AGPE0.TB_COMS_CULTIVOS_SUBS_HIST sh ON coms.id=sh.IDCOMISIONESSUBENT " +
						"inner join O02AGPE0.TB_LINEAS l on coms.LINEASEGUROID=l.LINEASEGUROID " + 
						"where l.CODPLAN=" + plan + " and l.CODLINEA=" + linea + 
						" and coms.CODENTIDAD=" + entMed + " and coms.CODSUBENTIDAD=" + subEntMed +
						" and TO_DATE(to_char(sh.FEC_EFECTO, 'DD/MM/YYYY'), 'DD/MM/YYYY') <= TO_DATE ('" + fecEfecto + "','DD/MM/YYYY') "+ 
						"and ((coms.fec_baja is null or to_date('" + fecEfecto + "','dd/mm/yyyy') < to_date(coms.fec_baja,'dd/mm/yyyy'))) " +
						"order by sh.fec_efecto desc ,sh.fechamodificacion desc";
							
				logger.debug ("plan: " + plan + " - linea: " + linea + " - fecEfecto: " + fecEfecto + " - entMed: " + entMed + " - subEntMed: " + subEntMed);
				logger.debug (sql);
				
				List<Object> resultado = c.ejecutaQuery(sql, 2);
				if(resultado!=null && resultado.size()>0){
					BigDecimal pctent = (BigDecimal) ((Object[])resultado.get(0))[0];
					BigDecimal pctmed = (BigDecimal) ((Object[])resultado.get(0))[1] ;
					
					res = new CultivosSubentidades();
					res.setPctentidad(pctent);
					res.setPctmediador(pctmed);
				}else{
					String sql2 =" select coms.PCTENTIDAD, coms.PCTMEDIADOR " +     
							"from o02agpe0.TB_COMS_CULTIVOS_SUBENTIDADES coms " +
							"inner join O02AGPE0.TB_COMS_CULTIVOS_SUBS_HIST sh ON coms.id=sh.IDCOMISIONESSUBENT " +
							"inner join O02AGPE0.TB_LINEAS l on coms.LINEASEGUROID=l.LINEASEGUROID " + 
							"where l.CODPLAN=" + plan + " and l.CODLINEA=999" + 
							" and coms.CODENTIDAD=" + entMed + " and coms.CODSUBENTIDAD=" + subEntMed +
							" and TO_DATE(to_char(sh.FEC_EFECTO, 'DD/MM/YYYY'), 'DD/MM/YYYY') <= TO_DATE ('" + fecEfecto + "','DD/MM/YYYY') "+ 
							"and ((coms.fec_baja is null or to_date('" + fecEfecto + "','dd/mm/yyyy') < to_date(coms.fec_baja,'dd/mm/yyyy'))) " +
							"order by sh.fec_efecto desc ,sh.fechamodificacion desc";
					logger.debug ("plan: " + plan + " - linea: 999 - fecEfecto: " + fecEfecto + " - entMed: " + entMed + " - subEntMed: " + subEntMed);
					logger.debug ("busqueda por LINEA generica. - " + sql2);
					List<Object> resultado2 = c.ejecutaQuery(sql2, 2);
					if(resultado2!=null && resultado2.size()>0){
						BigDecimal pctent = (BigDecimal) ((Object[])resultado2.get(0))[0];
						BigDecimal pctmed = (BigDecimal) ((Object[])resultado2.get(0))[1] ;
						
						res = new CultivosSubentidades();
						res.setPctentidad(pctent);
						res.setPctmediador(pctmed);
					}
				}
				
					
			} catch (Exception e) { 
				logger.error("Error seleccionando las comisiones de subentidades por fecha de efecto." ,e);
				throw(e);
			}finally{
				logger.debug("### ProcesarPolizasRenovablesWS.getComisionesSubentidadesProcesar - SALE");
			}
			return res;
		}
		
		
		/**
		 * Metodo que devuelve la clas pasando como parametro una explotacion.
		 * Filtra por los datos genericos. Segun el siguiente orden: 
		 * 1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99; 4. Provincia = 99 5: modulo;
		 * ORDEN: subtermino 9, termino 999, comarca 99, provincia 99, tipo de animal 999, tipo de capital, grupo de raza, regimen, especie y modulo
		 */
		@SuppressWarnings("unchecked")
		public static BigDecimal getClasePoliza(Explotacion exp,Session sn){		
			List <Integer> lstGruposRaza  = new ArrayList<Integer>();
			List <Integer> lstTipoAnimal  = new ArrayList<Integer>();
			List <Integer> lstTipoCapital = new ArrayList<Integer>();
			List <String> lstModulos = new ArrayList<String>();
			List <Character> lstSubTerminos = new ArrayList<Character>();
			List <Integer> lstTerminos = new ArrayList<Integer>();
			List <Integer> lstComarcas = new ArrayList<Integer>();
			List <Integer> lstProvincias = new ArrayList<Integer>();
			List <Integer> lstRegimenes = new ArrayList<Integer>();
			List <Integer> lstEspecies = new ArrayList<Integer>();
				
			try{
				logger.debug("getClasePoliza()");
				Set<GrupoRaza> grupoRazasSet = (Set<GrupoRaza>) exp.getGrupoRazas();
				for (GrupoRaza gr : grupoRazasSet){
					lstGruposRaza.add(gr.getCodgruporaza().intValue());
					lstTipoAnimal.add(gr.getCodtipoanimal().intValue());
					lstTipoCapital.add(gr.getCodtipocapital().intValue());
				}
				
				lstGruposRaza.add(999);
				lstTipoAnimal.add(999);
				lstTipoCapital.add(999);
				
				lstModulos.add(exp.getPoliza().getCodmodulo());
				lstModulos.add("99999");
				if (exp.getTermino().getId().getSubtermino() != null && !exp.getTermino().getId().getSubtermino().toString().trim().equals("")){
			
					lstSubTerminos.add(exp.getTermino().getId().getSubtermino().toString().trim().charAt(0));
				}
				lstSubTerminos.add('9');
				lstTerminos.add(exp.getTermino().getId().getCodtermino().intValue());
				lstTerminos.add(999);
				lstComarcas.add( exp.getTermino().getId().getCodcomarca().intValue());
				lstComarcas.add(99);
				lstProvincias.add( exp.getTermino().getId().getCodprovincia().intValue());
				lstProvincias.add(99);
				lstRegimenes.add(exp.getRegimen().intValue());
				lstRegimenes.add(999);
				lstEspecies.add(exp.getEspecie().intValue());
				lstEspecies.add(999);
								
				Criteria criteria = getCriteria(exp,sn,lstGruposRaza,lstTipoAnimal,lstTipoCapital,lstModulos,lstSubTerminos,lstTerminos,lstComarcas,lstProvincias,lstRegimenes,lstEspecies );
				logger.debug(" DEBUG CONSULTA1: " +criteria.toString());
				
				List<ClaseDetalleGanado> f = (List<ClaseDetalleGanado>)criteria.list();

				if (f != null && f.size()>0){
					return f.get(0).getClase().getClase();
				}else{
					logger.debug("Clase no encontrada");
					return null;
				}		
			} catch (Exception ex) {
				logger.error("getClasePoliza - Se ha producido un error en la BBDD: ",ex);
				return null;
			}	
			
		}

		private static Criteria getCriteria(Explotacion exp,Session sn,
				List <Integer> lstGruposRaza,List <Integer> lstTipoAnimal,List <Integer> lstTipoCapital, List<String> lstModulos, List<Character> lstSubTerminos,
				List <Integer> lstTerminos, List <Integer> lstComarcas,List <Integer> lstProvincias,List <Integer> lstRegimenes,List <Integer> lstEspecies ){
			
			// ORDEN: subtermino 9, termino 999, comarca 99, provincia 99, tipo de animal 999, tipo de capital, grupo de raza, regimen, especie y modulo
			Criteria criteria = sn.createCriteria(ClaseDetalleGanado.class);
			criteria.add(Restrictions.eq("lineaseguroid",exp.getPoliza().getLinea().getLineaseguroid().intValue()));		
			criteria.add(Restrictions.in("subtermino",  lstSubTerminos));
			criteria.add(Restrictions.in("codtermino",  lstTerminos));
			criteria.add(Restrictions.in("codcomarca",  lstComarcas));
			criteria.add(Restrictions.in("codprovincia", lstProvincias));
			criteria.add(Restrictions.in("codtipoanimal", lstTipoAnimal));
			criteria.add(Restrictions.in("codtipocapital",lstTipoCapital));
			criteria.add(Restrictions.in("codgruporaza",lstGruposRaza));
			criteria.add(Restrictions.in("codregimen", lstRegimenes));
			criteria.add(Restrictions.in("codespecie", lstEspecies));
			criteria.add(Restrictions.in("codmodulo", lstModulos));
			
			return criteria;
		}
	
}