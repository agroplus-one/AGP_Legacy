package com.rsi.agp.batch.updateEstadosPolRenovables;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.batch.cargaPolizasRenovables.PopulatePolizaRenovable;
import com.rsi.agp.batch.procesarPolizasRenovables.ProcesarPolizasRenovablesWS;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.PolizasPctComisionesManager;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.comisiones.PolizasPctComisionesDao;
import com.rsi.agp.dao.models.poliza.LineaDao;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.GastosRenovacion;

import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.estadoRenovacion.Renovacion;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovables;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovablesDocument;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ListaPolizasRenovablesResponse;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ObjectFactory;
import es.agroseguro.serviciosweb.contratacionscmodificacion.ContratacionSCModificacion;

public class UpdateEstadosPolRenovables {

	private static final Logger logger = Logger.getLogger(UpdateEstadosPolRenovables.class);
	private static PolizasPctComisionesManager pManager;
	private static PolizasPctComisionesDao pDao;
	
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.debug(" ");
			logger.debug("#---------------------------------------------------------#");
			logger.info ("INICIO Batch ACTUALIZACI�N ESTADOS P�LIZAS RENOVABLES V.02");
			logger.debug("#---------------------------------------------------------#");
			doWork();
			logger.info("FIN Batch Actualizaci�n estados polizas renovables");
			logger.info("Salimos con ejecuci�n CORRECTA");
			System.exit(0);
		} catch (Exception e) {
			logger.error("Error en la Actualizaci�n estados polizas renovables", e);
			System.exit(1);
		} 
	}

	@SuppressWarnings("unchecked")
	private static void doWork() throws BusinessException {
		List<com.rsi.agp.dao.tables.cgen.LineaCondicionado> lstLineasC;
		List<com.rsi.agp.dao.tables.comisiones.CultivosEntidades> lstParam;
		List<BigDecimal> lstLineas = new ArrayList<BigDecimal>();
		List<BigDecimal> lstLineasSW = new ArrayList<BigDecimal>();
		StringBuilder polAtualizadas = new StringBuilder();
		List<com.rsi.agp.dao.tables.renovables.PolizaRenovable> lstPolActualizadas = new ArrayList<com.rsi.agp.dao.tables.renovables.PolizaRenovable>();
		SessionFactory factory;
		Session session     = null;
		List<Long> lstEstAgPlus   = new ArrayList<Long>();
		List<Long> lstEstAgSeguro = new ArrayList<Long>();
		int estCallWs   = 0;
		int estAgSeguro = 0;
		int estAgPlus   = 0;
		
		boolean primera = true;
		ContratacionRenovaciones objWs = null;
		ContratacionSCModificacion objSitActWs = null;
		try {
			factory = getSessionFactory();
			session = factory.openSession();
			
			// 1 - Recogemos las l�neas con grupo seguro "G01"	
			String gSeguro = UPRConstants.GRUPOSEGURO_G01;

			Criteria crit = session.createCriteria(com.rsi.agp.dao.tables.cgen.LineaCondicionado.class);		
			crit.createAlias("grupoSeguro","grupoSeguro");
			crit.add(Restrictions.eq("grupoSeguro.codgruposeguro",gSeguro));
			lstLineasC = (List<com.rsi.agp.dao.tables.cgen.LineaCondicionado>) crit.list();
			for (com.rsi.agp.dao.tables.cgen.LineaCondicionado linC: lstLineasC) {
				if (linC.getCodlinea() != null)
					lstLineas.add(linC.getCodlinea());
			}
			logger.info("## l�neas con G01: "+lstLineas.toString() + " ##");

			// 2 - Buscamos el plan actual
			Calendar c2 = new GregorianCalendar();
			int planActual = c2.get(Calendar.YEAR); // = 2016;

			for(int anio=planActual;anio>planActual-2;anio--) {
				lstLineasSW.clear();

				if (lstLineas != null && !lstLineas.isEmpty()) {	
					// 3 - Recogemos aquellas l�neas que se ha definido el parametro general de comisiones		
					Criteria crit2 = session.createCriteria(com.rsi.agp.dao.tables.comisiones.CultivosEntidades.class);
					crit2.createAlias("linea","linea");
					crit2.add(Restrictions.eq("linea.codplan",new BigDecimal(anio)));
					crit2.add(Restrictions.in("linea.codlinea",lstLineas));
					crit2.add(Restrictions.isNotNull("pctadministracion"));
					crit2.add(Restrictions.isNotNull("pctadquisicion"));
					lstParam = (List<com.rsi.agp.dao.tables.comisiones.CultivosEntidades>) crit2.list();
		
					for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param: lstParam) {
						if (param.getLinea() != null && param.getLinea().getCodlinea() != null && !lstLineasSW.contains(param.getLinea().getCodlinea())) {
							lstLineasSW.add(param.getLinea().getCodlinea());
						}
					}
					logger.info("## L�neas del plan: " + anio  + " con pctComisiones: "+lstLineasSW.toString() + " ##");
							
					/*		 
					// Estados Renovaci�n AGROPLUS	
					PEND_ASIGNAR_GASTOS --> 1;
					GASTOS_ASIGNADOS 	--> 2;
					ENVIADA_PENDIENTE_DE_CONFIRMAR --> 3;
					ENVIADA_CORRECTA 	--> 4;
					ENVIADA_ERRONEA 	--> 5;
					
					// Estados Renovaci�n AGROSEGURO			
					BORRADOR_PRECARTERA 	--> 1;
					PRIMERA_COMUNICACION 	--> 2;
					COMUNICACION_DEFINITIVA --> 3;
					EMITIDA 				--> 4;
					RESCINDIDA 				--> 5;
					ANULADA 				--> 6;
					PRECARTERA_PRECALCULADA --> 8;
					PRECARTERA_GENERADA 	--> 9;
					*/
					
					// ## CASU�STICAS ##
					/*
					// 2(PRIMERA_COMUNICACION)  cambian a ese estado en bbdd las que est�n en: AGSEGURO 1(BORRADOR_PRECARTERA),8(PRECARTERA_PRECALCULADA) o 9(PRECARTERA_GENERADA)	
					// 3(COMUNICACION_DEFINITIVA)  cambian a ese estado en bbdd las que est�n en: AGSEGURO 2(PRIMERA_COMUNICACION)
					// 4(EMITIDA)  cambian a ese estado en bbdd las que est�n en: AGSEGURO 3(COMUNICACION_DEFINITIVA) y fecha renovaci�n Ws sea mayor o igual que la fecha renovaci�n bbdd					
					// 5 (RESCINDICA) se procesar� �ste estado si en el sistema disponemos de polizas en cualquier estado de Agroseguro que est�n en vigor.
					// 6 (ANULADA) se procesar� �ste estado si en el sistema disponemos de polizas en estado de Agroseguro �4 � Emitida� o �3 � Comunicaci�n definitiva� que est�n en vigor.
					// 8(PRECARTERA_PRECALCULADA) cambian a ese estado en bbdd las que est�n en: AGSEGURO 9(PRECARTERA_GENERADA)
					*/
					 	
					// CALLWS: ESTADO DE AGROSEGURO CON EN EL QUE SE VA A LLAMAR AL WS. SI LA POLIZA SE ACTUALIZA SERA EL ESTADO QUE SE INSERTE
					//         EN TB_POLIZAS_RENOVABLES

					if (lstLineasSW != null && !lstLineasSW.isEmpty()){
						// 4 - Inicializamos el objeto WS
						if (primera) {
							
							objWs = UpdateEstadosPolRenovablesWS.getObjetoWs();
							logger.debug("**@@** Valor de objWs :"+objWs.toString());

							// recogemos el objeto para la llamada al servicio web de poliza actualizada
							objSitActWs = SituacionActualizadaWS.getObjetoActRenWs(); // cargamos el objeto de la llamada al Ws
							logger.debug("**@@** Valor de objSitActWs :"+objSitActWs.toString());
							primera = false;
						}						
						
						// CALLWS 2 AGSEGURO 1,8 y 9 AGPLUS 2 y 4
						estCallWs   = UPRConstants.ES_AGSEGURO_PRIMERA_COMUNICACION; //2
						lstEstAgPlus.clear();
						Integer estadoAgPlus = UPRConstants.ES_AGPLUS_GASTOS_ASIGNADOS; //2
						lstEstAgPlus.add(estadoAgPlus.longValue());
						estadoAgPlus = UPRConstants.ES_AGPLUS_ENVIADA_CORRECTA; //4
						lstEstAgPlus.add(estadoAgPlus.longValue());
						lstEstAgSeguro.clear();
						Integer estadoAgSeguro = UPRConstants.ES_AGSEGURO_BORRADOR_PRECARTERA; //1
						lstEstAgSeguro.add(estadoAgSeguro.longValue());
						estadoAgSeguro = UPRConstants.ES_AGSEGURO_PRECARTERA_PRECALCULADA; //8
						lstEstAgSeguro.add(estadoAgSeguro.longValue());
						estadoAgSeguro = UPRConstants.ES_AGSEGURO_PRECARTERA_GENERADA; //9
						lstEstAgSeguro.add(estadoAgSeguro.longValue());
						actualizar(session,1,1,lstLineasSW,lstPolActualizadas,estCallWs,false,anio,objWs,null, lstEstAgSeguro, lstEstAgPlus,false,false,pManager,pDao);
							
 						// CALLWS 3 AGSEGURO 2 AGPLUS 4
						estCallWs   = UPRConstants.ES_AGSEGURO_COMUNICACION_DEFINITIVA; //3
						estAgSeguro = UPRConstants.ES_AGSEGURO_PRIMERA_COMUNICACION; //2
						estAgPlus   = UPRConstants.ES_AGPLUS_ENVIADA_CORRECTA; //4
						actualizar(session,estAgSeguro,estAgPlus,lstLineasSW,lstPolActualizadas,estCallWs,false,anio,objWs,null,null,null,false,false,pManager,pDao);
						
						// CALLWS 4 AGSEGURO 3 AGPLUS 4    -- CON FECHAS --
						estCallWs   = UPRConstants.ES_AGSEGURO_EMITIDA; //4
						estAgSeguro = UPRConstants.ES_AGSEGURO_COMUNICACION_DEFINITIVA; //3
						estAgPlus   = UPRConstants.ES_AGPLUS_ENVIADA_CORRECTA; //4
						actualizar(session,estAgSeguro,estAgPlus,lstLineasSW,lstPolActualizadas,estCallWs,true,anio,objWs,objSitActWs,null,null,false,false,pManager,pDao);

 						// CALLWS 5 AGSEGURO 4
						estCallWs   = UPRConstants.ES_AGSEGURO_RESCINDIDA; //5
						actualizar(session,0,0,lstLineasSW,lstPolActualizadas,estCallWs,false,anio,objWs,null,null,null,false,true,pManager,pDao);
						
 						// CALLWS 6 AGSEGURO 5
						estCallWs   = UPRConstants.ES_AGSEGURO_ANULADA; //6
						lstEstAgSeguro.clear();
						estadoAgSeguro = UPRConstants.ES_AGSEGURO_EMITIDA; //4
						lstEstAgSeguro.add(estadoAgSeguro.longValue());
						estadoAgSeguro = UPRConstants.ES_AGSEGURO_COMUNICACION_DEFINITIVA; //3
						lstEstAgSeguro.add(estadoAgSeguro.longValue());
						actualizar(session,estAgSeguro,0,lstLineasSW,lstPolActualizadas,estCallWs,false,anio,objWs,null,lstEstAgSeguro,null,false,true,pManager,pDao);
						
 						// CALLWS 8 AGSEGURO 9
						estCallWs   = UPRConstants.ES_AGSEGURO_PRECARTERA_PRECALCULADA; //8
						estAgSeguro = UPRConstants.ES_AGSEGURO_PRECARTERA_GENERADA; //9
						estAgPlus   = UPRConstants.ES_AGPLUS_ENVIADA_CORRECTA; //4
						actualizar(session,estAgSeguro,estAgPlus,lstLineasSW,lstPolActualizadas,estCallWs,false,anio,objWs,null,null,null,true,false,pManager,pDao);
						
					}
				}
			}
					
			logger.info("#### TOTAL P�LIZAS ACTUALIZADAS: "+ lstPolActualizadas.size()+" ####");
			if (!lstPolActualizadas.isEmpty()) {
				for (com.rsi.agp.dao.tables.renovables.PolizaRenovable polAct: lstPolActualizadas) {
					polAtualizadas.append(polAct.getReferencia()+ ",");
				}
				logger.debug("#### Referencias actualizadas: "+polAtualizadas.toString()+" ####");
			}		
			
			/*try {
			 	// ESC-29998: polizas sin gstos
				logger.debug("INICIO Polizas Sin Gastos");
				polizasSinPagos(session, objSitActWs);
				logger.debug("FIN Polizas Sin Gastos");
			} catch (Exception ex) {
				logger.debug("ERROR >> Polizas Sin Gastos", ex);
			}
			try {
			 	// ESC-29998: polizas sin gstos
				logger.debug(">>>INICIO Polizas Renovables no emitidas");
				comisionesPolRenov(session, objWs);
				logger.debug(">>>FIN Polizas Renovables no emitidas");
			} catch (Exception ex) {
				logger.debug("ERROR >> Polizas Renovables no emitidas", ex);
			}*/
		} catch (Exception ex) {
			logger.error("# Error inesperado en la ejecuci�n de la actualizaci�n de estados de P�lizas Renovables",ex);
			throw new BusinessException(ex);
		} finally {
			if (session != null) {
				logger.debug("Cerrando session en Finally..");
				session.close();
				logger.debug("Session cerrada en Finally");
			}
		}
	}

	private static SessionFactory getSessionFactory() {
		SessionFactory sessionFactory;
		try {
			Configuration cfg = new Configuration();
			cfg.configure();

			sessionFactory = cfg.buildSessionFactory();
			
			//Creamos los managers y DAOS necesarios
			pManager = new PolizasPctComisionesManager();
			pDao = new PolizasPctComisionesDao();
			LineaDao lineaDao = new LineaDao();
			lineaDao.setSessionFactory(sessionFactory);
			//abrimos la sesion
	    	sessionFactory.getCurrentSession().beginTransaction();
	    	pManager.setPolizasPctComisionesDao(pDao);
	    	pDao.setLineaDao(lineaDao);	    	
	    	pDao.setSessionFactory(sessionFactory);

		} catch (Exception ex) {
			logger.error("# Error al crear el objeto SessionFactory. ", ex);
			throw new ExceptionInInitializerError(ex);
		}
		return sessionFactory;
	}
	
	
	protected static void actualizar(final Session session, final int estAgSeguro, final int estAgPlus,
			final List<BigDecimal> lstLineasSW, List<com.rsi.agp.dao.tables.renovables.PolizaRenovable> lstPolActualizadas,
			final int estCallWS, final boolean checkFecha, int planActual, ContratacionRenovaciones contratRen, ContratacionSCModificacion objSitActWs,
			final List<Long> lstEstAgSeguro,final List<Long> lstEstadosAgPlus, final Boolean modificarDias,boolean actFechaModifPlz, final PolizasPctComisionesManager pManager, final PolizasPctComisionesDao pDao ){
		
	List<com.rsi.agp.dao.tables.renovables.PolizaRenovable> lstPolRenBBDD = new ArrayList<com.rsi.agp.dao.tables.renovables.PolizaRenovable>();
	List<Renovacion> lstRenWS = new ArrayList<Renovacion>();
	boolean cumple = false;
	StringBuilder msg = new StringBuilder();
	int estadoAgPlusPOl = 0;
	int actualizadasTemp = 0;
	List<Character> lstGruposN = new ArrayList<Character>();
	Map<Character,Long> mapGruposN = new HashMap<Character,Long>();
		try {
			logger.error("Estado agroseguro: " + estAgSeguro);
			logger.error("Estado agroplus: " + estAgPlus);
			logger.error("Estado llamada al WS: " + estCallWS);
			if (estAgPlus !=0){ // para casos de agroseguro distintos a Anulada o Rescindida
				try{
					lstPolRenBBDD = BBDDUpdateEstadosPolRenUtil.getPolizasRenovablesEstados(session, estAgSeguro,
							lstEstAgSeguro, lstEstadosAgPlus, planActual);
				} catch (Exception ex) {
					logger.error("# Se ha producido un error al recoger los datos por WS, ",ex);
				}		
				if (lstPolRenBBDD.size() >0) {
					try{
						lstRenWS = UpdateEstadosPolRenovablesWS.getListPolizasRenovablesWS(Long.valueOf(planActual),
							lstLineasSW,session,Integer.toString(estCallWS),contratRen,true,modificarDias);
					} catch (Exception ex) {
						logger.error("# Se ha producido un error al recoger los datos por WS: ",ex);
					}	
				
					if (!lstRenWS.isEmpty()) {
						for (com.rsi.agp.dao.tables.renovables.PolizaRenovable polBBDD: lstPolRenBBDD) {
							for (Renovacion polWs: lstRenWS) {								
								if (polBBDD.getReferencia().equals(polWs.getReferencia()) && 
									polBBDD.getPlan().toString().equals(Integer.toString(polWs.getPlan())) &&
									polBBDD.getLinea().toString().equals(Integer.toString(polWs.getLinea()))){
										cumple = false;
										lstGruposN.clear();
										mapGruposN.clear();
										if (checkFecha) {  // comprobamos que la fecha de la polizaWS es mayor o igual que la de la BBDD
											Calendar calendarWs = polWs.getFechaRenovacion();
											Date dateWs = calendarWs.getTime();
											Date dateBB = polBBDD.getFechaRenovacion();
											
											if((dateWs.compareTo(dateBB)>0) || (dateWs.compareTo(dateBB)==0)){
												cumple = true;
											}
										}else {
											cumple =true;
										}
										if (cumple){
											try{
												logger.debug("------------------------");
												
												//recogemos previamente los grupos de negocio de los gastos
												Set<GastosRenovacion> gastosRenovacionSet = polBBDD.getGastosRenovacions();
												for (GastosRenovacion gasRen: gastosRenovacionSet){
													lstGruposN.add(gasRen.getGrupoNegocio());
													mapGruposN.put(gasRen.getGrupoNegocio(), gasRen.getEstadoRenovacionAgroplus().getCodigo());
												}
												
												Long linId = getLineaseguroIdfromPlanLinea(session,Long.valueOf(planActual),polBBDD.getLinea());
												actualizarDatosPoliza(polWs,polBBDD,session,objSitActWs,pManager,pDao,linId);
											} catch (Exception ex) {
												logger.error("# Error al actualizar los datos de la poliza ",ex);
											}	
											if (!lstPolActualizadas.contains(polBBDD)){
												lstPolActualizadas.add(polBBDD);	
												actualizadasTemp++;
											}
											try{
												Transaction trans   = null;											
												trans = session.beginTransaction();
												logger.info("### Guardamos la polizaBBDD ");
												session.saveOrUpdate(polBBDD);
								            	trans.commit();								            	
											} catch (Exception ex) {
												logger.error("# Error al guardar los datos de la poliza ",ex);
											}	
							            	estadoAgPlusPOl = 10 + estCallWS;
							            	try{
								            	logger.info("### actualizamos el estado Agroseguro en la poliza renovable ");
								            	// actualizamos el estado agroseguro de la poliza renovable
								            	BBDDUpdateEstadosPolRenUtil.actualizaEstadoPolRenById(polBBDD.getId(), estCallWS, null, session);
											} catch (Exception ex) {
												logger.error("# Error al actualizar el estado en la poliza renovable",ex);
											}
							            	try{
								            	logger.info("### actualizamos datos de la poliza ");
								            	// actualizamos el estado AGROPLUS la poliza
								            	BBDDUpdateEstadosPolRenUtil.actualizaDatosPolizaById(polBBDD, estadoAgPlusPOl, null, session,planActual,actFechaModifPlz);
											} catch (Exception ex) {
												logger.error("# Error al actualizar el estado en la poliza ",ex);
											}
							            	
							            	try{
												// actualizamos el coste tomador de la poliza
												logger.info("### actualizamos el estado en la poliza  ");
												BBDDUpdateEstadosPolRenUtil.actualizaCosteTomadorPoliza(polBBDD, session);
											} catch (Exception ex) {
												logger.error("# Error al actualizar el coste tomador de la poliza ",ex);
											}	
							            	
							            	try{
								            	logger.info("### actualiza el hist�rico de las polizas renovables");
								            	// Realizamos por cada gasto que tenga la poliza una inserci�n en el historico de estados
								            	for (Character grN:lstGruposN){
													BBDDUpdateEstadosPolRenUtil.actualizarHistorico(polBBDD.getId(), estCallWS, 
															mapGruposN.get(grN),polBBDD.getCosteTotalTomador(),polBBDD.getImporteDomiciliar(),session,grN);
												}
											} catch (Exception ex) {
												logger.error("# Error al actualizar el historico de las polizas renovables ",ex);
											}
										}
									}
							}
						}
						logger.info("### P�LIZAS ACTUALIZADAS: "+ actualizadasTemp+" ###");
					}else {
						logger.debug("## El SW no devuelve ninguna poliza con el estado Agroseguro: "+estCallWS + " ##");
					}
				}else {
					msg.append("## No hay polizas renovables en BBDD para el estado Agroseguro: "+estAgSeguro+" plan:"+planActual);
					msg.append(" ##");
					logger.debug(msg.toString());
				}
			}else{// para casos de estado Agroseguro Anulada o Rescindida. Sin fechas en la llamada al Ws
				
				try{
					lstRenWS = UpdateEstadosPolRenovablesWS.getListPolizasRenovablesWS(Long.parseLong(Integer.toString(planActual)),
						lstLineasSW,session,Integer.toString(estCallWS),contratRen,false,false);
				} catch (Exception ex) {
					logger.error("# Se ha producido un error al recoger los datos por WS ",ex);
				}						
				if (lstRenWS.size() >0) {
						com.rsi.agp.dao.tables.renovables.PolizaRenovable polBBDD = null;						
						for (Renovacion polWs: lstRenWS) {							
							try{
								polBBDD = BBDDUpdateEstadosPolRenUtil.getPolizaRenovable(session,polWs.getReferencia(),estCallWS,lstEstAgSeguro,planActual);
							
							} catch (Exception ex) {
								logger.error("# Se ha producido un error al obtener la poliza renovable, ",ex);
							}
							if (polBBDD != null) {
								lstGruposN.clear();
								mapGruposN.clear();
								try{
									logger.debug("------------------------");
									
									//recogemos previamente los grupos de negocio de los gastos
									Set<GastosRenovacion> gastosRenovacionSet = polBBDD.getGastosRenovacions();
									for (GastosRenovacion gasRen : gastosRenovacionSet){
										lstGruposN.add(gasRen.getGrupoNegocio());
										mapGruposN.put(gasRen.getGrupoNegocio(), gasRen.getEstadoRenovacionAgroplus().getCodigo());
									}
									Long linId = getLineaseguroIdfromPlanLinea(session,Long.valueOf(planActual),polBBDD.getLinea());
									actualizarDatosPoliza(polWs,polBBDD,session,objSitActWs,pManager,pDao,linId);
								
								} catch (Exception ex) {
									logger.error("# Error al actualizar los datos de la poliza ",ex);
								}	
								if (!lstPolActualizadas.contains(polBBDD)){
									lstPolActualizadas.add(polBBDD);	
									actualizadasTemp++;
								}
								try{
									Transaction trans   = null;											
									trans = session.beginTransaction();
									session.saveOrUpdate(polBBDD);
									trans.commit();
								} catch (Exception ex) {
									logger.error("# Error al guardar los datos de la poliza ",ex);
								}	
								estadoAgPlusPOl = 10 + estCallWS;
								try{
									// actualizamos el estado agroseguro de la poliza renovable
									logger.info("### actualizamos el estado en la poliza  renovable");
									BBDDUpdateEstadosPolRenUtil.actualizaEstadoPolRenById(polBBDD.getId(), estCallWS, null, session);
								} catch (Exception ex) {
									logger.error("# Error al actualizar el estado en la poliza renovable ",ex);
								}
								try{
									// actualizamos el estado AGROPLUS la poliza
									logger.info("### actualizamos el estado en la poliza");
									BBDDUpdateEstadosPolRenUtil.actualizaDatosPolizaById(polBBDD, estadoAgPlusPOl, null, session,planActual,actFechaModifPlz);
								} catch (Exception ex) {
									logger.error("# Error al actualizar el estado en la poliza ",ex);
								}
								
								try{
									// actualizamos el coste tomador de la poliza
									logger.info("### actualizamos el estado en la poliza ");
									BBDDUpdateEstadosPolRenUtil.actualizaCosteTomadorPoliza(polBBDD, session);
								} catch (Exception ex) {
									logger.error("# Error al actualizar el coste tomador de la poliza ",ex);
								}						
								
								try{
									// Realizamos por cada gasto que tenga la poliza una inserci�n en el historico de estados
									logger.info("### actualiza el hist�rico de las polizas renovables");
									for (Character grN:lstGruposN){
										BBDDUpdateEstadosPolRenUtil.actualizarHistorico(polBBDD.getId(), estCallWS, 
												mapGruposN.get(grN),polBBDD.getCosteTotalTomador(),polBBDD.getImporteDomiciliar(),session,grN);
									}
								} catch (Exception ex) {
									logger.error("# Error al actualizar el historico de las polizas renovables ",ex);
								}
								
							}
						}
						logger.info("### P�LIZAS ACTUALIZADAS: "+ actualizadasTemp+" ###");
				}else {
					logger.debug("## El SW no devuelve ninguna poliza con el estado Agroseguro: "+estCallWS + " ##");
				}
			}
		} catch (Exception ex) {
			logger.error("# Error Generico ",ex);
		}	
		
	}
	
	// M�todo que atualiza los datos de la poliza que existe en BBDD con la que nos devuelve el servicio web
	public static void actualizarDatosPoliza(final Renovacion polWs,
			final com.rsi.agp.dao.tables.renovables.PolizaRenovable polBBDD, Session session,
			ContratacionSCModificacion objSitActWs, final PolizasPctComisionesManager pManager,
			final PolizasPctComisionesDao pDao, final Long linId) throws BusinessException {
		try {
			// Colectivo
			Transaction transCol;
			ColectivosRenovacion colHbm = null;			
			
			if (polWs.getColectivo() != null && !polWs.getColectivo().equals("")) {
				logger.info("### actualizarDatosPoliza(): Cargamos colectivo: " + polWs.getColectivo()+ " idpoliza: " + polWs.getReferencia());
				Criteria critCol = session.createCriteria(ColectivosRenovacion.class);
				critCol.add(Restrictions.eq("referencia",polWs.getColectivo()));
				colHbm = (ColectivosRenovacion) critCol.uniqueResult();
			}
			if (colHbm == null && polWs.getColectivo() != null && !polWs.getColectivo().equals("")){
				logger.info("### actualizarDatosPoliza(): Colectivo null: " + polWs.getColectivo() + " idpoliza:  " + polWs.getReferencia());
				transCol = session.beginTransaction();
				ColectivosRenovacion colNew= new ColectivosRenovacion();
				colNew.setReferencia(polWs.getColectivo());
				colNew.setCodentidad(Long.valueOf(9999));
				colNew.setCodentidadmed(Long.valueOf(9999));
				colNew.setCodsubentmed(Long.valueOf(0));
				colNew.setDc('1');
				colNew.setCodlinea(Long.parseLong(Integer.toString(polWs.getLinea())));
				logger.info("### actualizarDatosPoliza(): Guardamos colectivo nuevo: " + polWs.getColectivo()+ " idpoliza: " + polWs.getReferencia());
				session.saveOrUpdate(colNew);
				transCol.commit();
				session.flush();
				polBBDD.setColectivoRenovacion(colNew);
			}else { // ya existe3
				if (polWs.getColectivo() != null && !polWs.getColectivo().equals("")){
					logger.info("### actualizarDatosPoliza(): Colectivo ya existe: " + polWs.getColectivo()+ " idpoliza:  " + polWs.getReferencia());
					polBBDD.setColectivoRenovacion(colHbm);
				}else{
					logger.info("### actualizarDatosPoliza(): Colectivo vacio en el WS - idpoliza:" + polWs.getReferencia());
				}
			}	
			
			// L�nea
			polBBDD.setLinea(Long.parseLong(Integer.toString(polWs.getLinea())));
			
			// Plan
			polBBDD.setPlan(Long.parseLong(Integer.toString(polWs.getPlan())));
			
			// Referencia
			if (!polWs.getReferencia().isEmpty())
				polBBDD.setReferencia(polWs.getReferencia());
			char dc = Integer.toString(polWs.getDigitoControl()).charAt(0);
			polBBDD.setDc(dc);
			
			// Asegurado
			if (!polWs.getNifAsegurado().isEmpty())
				polBBDD.setNifAsegurado(polWs.getNifAsegurado());
			
			/*
			// Fecha Carga no se actualiza
			*/
			
			// Tomador
			if (polWs.getNifTomador() != null && !polWs.getNifTomador().isEmpty())
				polBBDD.setNifTomador(polWs.getNifTomador());
			
			// nombre Tomador - no existe en polBBDD

			// Medidador
			if (polWs.getMediador() != null && !polWs.getMediador().isEmpty())
				polBBDD.setMediador(polWs.getMediador());

			// Fecha renovaci�n
			if (polWs.getFechaRenovacion() != null)
				polBBDD.setFechaRenovacion(polWs.getFechaRenovacion().getTime());
		
			// Fecha comunicaci�n
			if (polWs.getFechaComunicacion() != null)
				polBBDD.setFechaComunicacion(polWs.getFechaComunicacion().getTime());
			
			// Coste Total Tomador
			if (polWs.getCosteTotalTomador() != null)
				polBBDD.setCosteTotalTomador(polWs.getCosteTotalTomador());
			
			// fase no existe en polBBDD
			
			// numeroRecibo
			if( polWs.getNumeroRecibo() != 0)
				polBBDD.setNumRecibo(new BigDecimal(polWs.getNumeroRecibo())); 
				
			// Fecha domiciliaci�n
			if (polWs.getFechaDomiciliacion() != null)
				polBBDD.setFechaDomiciliacion(polWs.getFechaDomiciliacion().getTime());
			
			// importeDomiciliar
			if (polWs.getImporteDomiciliar() != null)
				polBBDD.setImporteDomiciliar(polWs.getImporteDomiciliar());
						
			// IBAN
			if (polWs.getIBAN() != null)
				polBBDD.setIban(polWs.getIBAN());
			
			// MPM - Destinatario de la domiciliaci�n 
			if (polWs.getDestinatarioDomiciliacion() != null && polWs.getDestinatarioDomiciliacion().length()>=1)
				polBBDD.setDestinoDomiciliacion(polWs.getDestinatarioDomiciliacion().charAt(0));

			// Forma de pago
			if (polWs.getForma() != null && polWs.getForma().length()>=1)
				polBBDD.setForma(polWs.getForma().charAt(0));
			
			// Domiciliado
			if (polWs.getDomiciliado() != null && polWs.getDomiciliado().length()>=1)		
				polBBDD.setDomiciliado(polWs.getDomiciliado().charAt(0));
			
			//P21660 CosteTotalTomadorAnterior
			if (polWs.getCosteTotalTomadorAnterior() != null)
				polBBDD.setCosteTotalTomadorAnterior(polWs.getCosteTotalTomadorAnterior());
			
			
			// recogo la poliza de bbdd
			Poliza polizaHbm = SituacionActualizadaWS.getPolizaBBDD(session,polBBDD.getReferencia(),polBBDD.getPlan());
			
			
			// Actualizacion solo en el cambio de estado a 'Emitida'
			if (objSitActWs != null){ // emitida
				if (polizaHbm != null){
					logger.debug("## Cambio de estado a EMITIDA. Realizamos actualizaciones en poliza ref: "+ polizaHbm.getReferencia() + " idPoliza: "+polizaHbm.getIdpoliza());				
					// Borrado previo de pagos
					
					Transaction transDeletePagos = session.beginTransaction();
					PopulatePolizaRenovable.borrarPagosByIdPoliza(polizaHbm.getIdpoliza(),session);				
					transDeletePagos.commit();	
					
					//P21660 Actualizaci�n pagos
					try {
						
						if (polWs != null) {
							logger.debug("# Actualizamos pagos..");
							Transaction transPagos = session.beginTransaction();
							PagoPoliza pago = new PagoPoliza();
							pago.setPoliza(polizaHbm);
							pago.setFormapago(polWs.getForma() != null ? polWs.getForma().charAt(0) : 'C');
							pago.setFecha(polWs.getFechaDomiciliacion() != null ? polWs.getFechaDomiciliacion().getTime() : null);
							pago.setImporte(polWs.getCosteTotalTomador());						
							if (polWs.getIBAN() != null) {// Si vienen informados los datos del IBAN 
								pago.setIban(polWs.getIBAN().substring(0,4));
								pago.setCccbanco(polWs.getIBAN().substring(4));
							}else {// Si no vienen informados se inserta el IBAN a 'ES' ya que es un dato obligatorio en la BBDD
								pago.setIban("ES");
							}
							pago.setDomiciliado(polWs.getDomiciliado() != null ? polWs.getDomiciliado().charAt(0) : null);
							
							
							pago.setTipoPago(new BigDecimal(0));
							pago.setImportePago(polWs.getImporteDomiciliar());
							PopulatePolizaRenovable.guardaPagoPoliza(pago,session);
							transPagos.commit();
							logger.debug("#  pagos actualizados # ");				
						}
						
					} catch (Exception ex) {
						logger.error("## Error al actualizar los pagos de la poliza: "+polizaHbm.getReferencia() +" L�NEA:  " + polizaHbm.getLinea() + " PLAN: "+polBBDD.getPlan() +" ## ",ex);
					}
					
					// Borrado previo de comisiones
					Transaction transDeletePctComisiones = session.beginTransaction();
					PopulatePolizaRenovable.borrarPctComisionesByIdPoliza(polizaHbm.getIdpoliza(),session);
					transDeletePctComisiones.commit();					
					
					//P21660 Actualizamos pctComisiones;
					logger.debug("# Actualizamos pctComisiones..");
					Transaction transPctComisiones   = session.beginTransaction();
					String resultado = ProcesarPolizasRenovablesWS.getPctComisiones(polizaHbm, polWs, session, pManager, pDao);
					if (resultado.equals("OK")) {
						session.saveOrUpdate(polizaHbm);
						logger.debug("#  pctComisiones actualizadas # ");
					}else{
						logger.debug(" ## ERROR en comisiones: "+ resultado);
					}
					transPctComisiones.commit();					
					
					// recogemos la situaci�n actualizada de la poliza
					logger.debug("**@@**UpdateEstadosPolRenovables - Antes de obtener la situaci�n Actualizada de la poliza");
					es.agroseguro.contratacion.Poliza polizaSit = SituacionActualizadaWS.updatePolizasRenovables(polBBDD.getPlan(),polBBDD.getReferencia(),objSitActWs);
					
					if (polizaSit != null){
						logger.debug(" ## Situaci�n actualizda recogida OK ");
						//21660 Actualizaci�n del importe de la poliza
						// importe
						if (polizaSit.getCostePoliza() != null){
							CostePoliza costePoliza = polizaSit.getCostePoliza();
							if (costePoliza.getTotalCosteTomador() != null){
								polizaHbm.setImporte(costePoliza.getTotalCosteTomador());
								session.saveOrUpdate(polizaHbm);
							}
						}
						
						/* Pet. 540460** MODIF TAM (14.09.2018) ** Inicio */
						/* Una vez recuperada la situaci�n actualizada de la poliza a trav�s de la llamada al WS 
						 * consultarContratacion, actualizamos los datos de Pago (Titular Cuenta, Destinatario, envio Iban...
						 */
						logger.debug("**@@**UpdateEstadosPolRenovables - actualizarDatosPoliza");
						logger.debug("**@@** Valor de Pago.Domiciliado:"+polizaSit.getPago().getDomiciliado());

						if (polizaSit.getPago() != null){
							if ((polizaSit.getPago().getDomiciliado().equals("S") || polizaSit.getPago().getDomiciliado().equals("T"))
									&& polizaSit.getPago().getCuenta() != null){
								logger.debug("# Actualizamos pagos (Domiciliado)..");
								Transaction transPagosDomi = session.beginTransaction();
								PagoPoliza pagoDomi = new PagoPoliza();
								pagoDomi.setPoliza(polizaHbm);
							
								// Si vienen informados los datos del IBAN los actualizamos en tablas
								if (polizaSit.getPago().getCuenta().getIban() != null) {
									pagoDomi.setIban(polizaSit.getPago().getCuenta().getIban().substring(0,4));
									pagoDomi.setCccbanco(polizaSit.getPago().getCuenta().getIban().substring(4));
								}else {// Si no vienen informados se inserta el IBAN a 'ES' ya que es un dato obligatorio en la BBDD
									pagoDomi.setIban("ES");
								}
								
								/* Si el pago est� domiciliado y la cuenta informada, quiere decir que se ha enviado el Iban a Agrsoseguro*/
								if (polizaSit.getPago().getCuenta()!= null){
									pagoDomi.setEnvioIbanAgro('S');
								}

								logger.debug("**@@** Valor del titular: " + polizaSit.getPago().getCuenta().getTitular());
								if (polizaSit.getPago().getCuenta().getTitular() != null && polizaSit.getPago().getCuenta().getTitular() != "null"){
									pagoDomi.setTitularCuenta(polizaSit.getPago().getCuenta().getTitular());	
								}else{
									pagoDomi.setTitularCuenta("");
								}
							
								pagoDomi.setDestinatarioDomiciliacion(polizaSit.getPago().getCuenta().getDestinatario()!= null ? polizaSit.getPago().getCuenta().getDestinatario().charAt(0) : null);

								PopulatePolizaRenovable.actualizaPagosPoliza(pagoDomi,session);
								transPagosDomi.commit();
								logger.debug("#  pagos actualizados (Domiciliado)# ");
							}
						}
						/* Pet. 540460** MODIF TAM (14.09.2018) ** Inicio */
						
						//P21660 Borrado previo de explotaciones					
						Transaction transDeleteExp = session.beginTransaction();
						PopulatePolizaRenovable.borrarExplotacionesByIdPoliza(polizaHbm.getIdpoliza(),session);
						transDeleteExp.commit();				
						
						//P21660 actualizaci�n Explotaciones
						try {
							logger.debug("# Actualizamos Explotaciones..");
							ProcesarPolizasRenovablesWS.populateExplotaciones(polizaHbm,polizaSit, session,linId);
							logger.debug("# explotaciones actualizadas # ");
						} catch (Exception ex) {
							logger.error("## Error al crear las explotaciones de la poliza: "+polizaHbm.getReferencia() +" L�NEA: " + polizaHbm.getLinea() + " PLAN:  "+polBBDD.getPlan() +" ## ",ex);
						}
						
						try {
						// Actualizacion de la distribucion de costes de la poliza, solo en el cambio de estado a 'Emitida'							
							if (polizaSit != null && polizaSit.getCostePoliza() != null) {
								logger.debug("# Actualizamos distribucion de costes..");
								//SituacionActualizadaWS.guardaDistribucionCoste(polizaHbm,polBBDD.getReferencia(),polBBDD.getPlan(),polizaSit.getCostePoliza(), session);
								SituacionActualizadaWS.guardaDistribucionCoste(polizaHbm, polBBDD, polizaSit.getCostePoliza(), session);
							}
						} catch (Exception ex) {
							logger.error("## Error al actualizar la distribucion de costes de la poliza: "+polizaHbm.getReferencia() +" L�NEA: " + polizaHbm.getLinea() + " PLAN: "+polBBDD.getPlan() +" ## ",ex);
						}	
					}
				}else{
					logger.debug("# No se encuentra la poliza en BBDD con plan: "+polBBDD.getPlan() +" y referencia: "+polBBDD.getReferencia());
				}
			}
			
			// Borrado de datos previos	en poliza renovable
			Transaction transDeleteDatos = session.beginTransaction();
			logger.debug(" borrarDatosPolizaRenovable  idPoliza: "+polBBDD.getId());
			try{
				PopulatePolizaRenovable.borrarDatosPolizaRenovable(polBBDD.getId(),session);
				transDeleteDatos.commit();
			} catch (Exception ex) {
				logger.error("# Error en borrado dedatos Polizarenovable ",ex);
				throw ex;
				
			}
			
			// Gastos Aplicados - pol renovable
			PopulatePolizaRenovable.populateGastosAplicados(polBBDD,polWs,pDao,linId);
			// CosteGrupoNegocio - pol renovable
			PopulatePolizaRenovable.populateCosteGrupoNegocio(polBBDD,polWs);
			// Fraccionamiento - pol renovable
			PopulatePolizaRenovable.populateFraccionamiento(polBBDD,polWs);

		} catch (Exception ex) {
			logger.error("# actualizarDatosPoliza() : Se ha producido un error al actualizar los datos de la poliza renovable ",ex);
			throw new BusinessException(ex);			
		}			
	}
	
	public static Long  getLineaseguroIdfromPlanLinea(final Session session,final Long codPlan, final Long codLinea) {
		String str = "select lineaseguroid from o02agpe0.tb_lineas lin where lin.codlinea =   "+codLinea+" and lin.codplan = "+codPlan;

		BigDecimal linId =(BigDecimal) session.createSQLQuery(str).uniqueResult();
		
		logger.debug("## plan: " + codPlan + " linea: " + codLinea + " lineaseguroid: " + linId+" ##");
		
		return linId.longValue();
	}		
	
	public static void polizasSinPagos(final Session session, ContratacionSCModificacion objSitActWs) {
		List<?> polizas = null;
		es.agroseguro.contratacion.Poliza polSitAct = null;
		
		logger.debug("1. Recuperamos las polizas que no tienen asignados gastos");
		polizas = PopulatePolizaRenovable.polizasSinPagos(session);
		
		if (polizas != null && polizas.size() > 0) {
			logger.debug("2. Recorremos las polizas  obtenidas");
			for (int i = 0; i < polizas.size(); i++) {
				Object[] registro = (Object[]) polizas.get(i);
				BigDecimal idPoliza = (BigDecimal) registro[0];
				String referencia = (String) registro[1];
				BigDecimal codPlan = (BigDecimal) registro[2];
				//BigDecimal codLinea = (BigDecimal) registro[3];

				try {
					logger.debug("3. Recuperamos la sitaucion actualizada de la poliza " + referencia + " y plan " + codPlan);
					polSitAct = SituacionActualizadaWS.updatePolizasRenovables(codPlan.longValue(), referencia, objSitActWs);
					
					if (polSitAct != null && polSitAct.getPago() != null) {
						logger.debug("4. Asignamos el pago a la poliza");
						PagoPoliza pagoDomi = new PagoPoliza();
						Poliza poliza = new Poliza();
						
						poliza.setIdpoliza(idPoliza.longValue());
						pagoDomi.setPoliza(poliza);
						
						pagoDomi.setFormapago(polSitAct.getPago().getForma() != null ? polSitAct.getPago().getForma().charAt(0) : 'C');
						
						if (polSitAct.getPago().getImporte() != null) {
							logger.debug("# Importe Pago - Importe");
							pagoDomi.setImporte(polSitAct.getPago().getImporte());
							pagoDomi.setImportePago(polSitAct.getPago().getImporte());
						} else {
							logger.debug("# Importe Pago - TotalCosteTomador");
							pagoDomi.setImporte(polSitAct.getCostePoliza().getTotalCosteTomador());
							pagoDomi.setImportePago(polSitAct.getCostePoliza().getTotalCosteTomador());
						}

						pagoDomi.setDomiciliado(polSitAct.getPago().getDomiciliado().charAt(0));

						if (polSitAct.getPago().getCuenta().getIban() != null) {
							pagoDomi.setIban(polSitAct.getPago().getCuenta().getIban().substring(0, 4));
							pagoDomi.setCccbanco(polSitAct.getPago().getCuenta().getIban().substring(4));
						} else {
							// Si no vienen informados se inserta el IBAN a 'ES' ya que es un dato obligatorio en la BBDD
							pagoDomi.setIban("ES");
						}

						pagoDomi.setTipoPago(new BigDecimal(0));

						logger.debug("5. Insertamos el pago a la poliza");
						PopulatePolizaRenovable.guardaPagoPoliza(pagoDomi, session);

						if (polSitAct.getPago().getCuenta() != null) {
							logger.debug("# Pago Domiciliado");
							if (polSitAct.getPago().getDomiciliado().equals("S") || polSitAct.getPago().getDomiciliado().equals("T")) {	
								// Si el pago esta domiciliado y la cuenta informada, quiere decir que se ha enviado el Iban a Agrsoseguro
								if (polSitAct.getPago().getCuenta() != null) {
									pagoDomi.setEnvioIbanAgro('S');
								}
	
								if (polSitAct.getPago().getCuenta().getTitular() != null
										&& polSitAct.getPago().getCuenta().getTitular() != "null") {
									logger.debug("**@@** Valor del titular: " + polSitAct.getPago().getCuenta().getTitular());
									pagoDomi.setTitularCuenta(polSitAct.getPago().getCuenta().getTitular());
								} else {
									logger.debug("**@@** Valor del titular: null");
									pagoDomi.setTitularCuenta("");
								}
	
								if (polSitAct.getPago().getCuenta().getDestinatario() != null) {
									logger.debug("# Destinatario:"+polSitAct.getPago().getCuenta().getDestinatario().charAt(0));
									pagoDomi.setDestinatarioDomiciliacion(polSitAct.getPago().getCuenta().getDestinatario().charAt(0));
								} else {
									logger.debug("# Destinatario a null");
									pagoDomi.setDestinatarioDomiciliacion(null);
								}
	
								logger.debug("6. Actualizadmos el pago a la poliza");
								PopulatePolizaRenovable.actualizaPagosPoliza(pagoDomi, session);
								logger.debug("#  Pago actualizado (Domiciliado)# ");
							} else {
								logger.debug("# Cuenta a null");
							}
						} else {
							logger.debug("# Pago Domiciliado:"+polSitAct.getPago().getDomiciliado());
						}
					} else {
						if (polSitAct == null) {
							logger.debug("# Situacion Actual a null");
						} else if (polSitAct.getPago() == null) {
							logger.debug("# Pago a null");	
						}
					}
				} catch (Exception e) {
					logger.error("# ERROR: polizasSinPagos", e);
				}				
			}
		} else {
			logger.debug("No se han obtenido polizas sin gastos");
		}
		logger.debug("7. FIN polizasSinPagos");
	}
	
	public static void comisionesPolRenov(final Session session, ContratacionRenovaciones objListRenovWs) {
		List<?> polizas = null;
		List<String> polError = new ArrayList<String>(0);
		List<String> polErrorPrima = new ArrayList<String>(0);
		List<String> polIgualPrima = new ArrayList<String>(0);
		Renovacion polizaRen = null;
		// Datos polizas
		BigDecimal idPoliza;
		BigDecimal idPolizaRenovable;
		String refPoliza = "";
		BigDecimal codLinea;
		BigDecimal codPlan;
		BigDecimal primaComercialNeta;
		BigDecimal pctComEnt;
		BigDecimal pctComEsMed;
		String grupoNeg = "";
		BigDecimal impComMed;
		BigDecimal impComES;

		// Datos Renovacion
		String renvCgGrupoNeg = "";
		String renvGaGrupoNeg = "";
		BigDecimal renvPrimaComercialNeta;
		BigDecimal renvPctCom;
		BigDecimal renvIimpCom;
		BigDecimal cien = new BigDecimal(100);
		
		StringBuilder refPolError = new StringBuilder();
		StringBuilder refPolErrorPrima = new StringBuilder();
		StringBuilder refPolIgualPrima = new StringBuilder();
		
		polizas = PopulatePolizaRenovable.polizasRenovables(session);

		if (polizas != null && polizas.size() > 0) {
			logger.debug(">>> Polizas encontradas " + polizas.size());
			
			for (int i = 0; i < polizas.size(); i++) {
				Object[] registro = (Object[]) polizas.get(i);

				idPoliza = (BigDecimal) registro[0];
				refPoliza = (String) registro[1];
				codLinea = (BigDecimal) registro[2];
				codPlan = (BigDecimal) registro[3];
				primaComercialNeta = (BigDecimal) registro[4];
				pctComEnt = (BigDecimal) registro[5];
				pctComEsMed = (BigDecimal) registro[6];
				grupoNeg = (String) registro[7];
				idPolizaRenovable = (BigDecimal) registro[8];

				renvCgGrupoNeg = "";
				renvPrimaComercialNeta = new BigDecimal(0);
				renvGaGrupoNeg = "";
				renvPctCom = new BigDecimal(0);
				renvIimpCom = new BigDecimal(0);
				impComMed = new BigDecimal(0);
				impComES = new BigDecimal(0);

				try {
					logger.debug(">>> Recuperamos los datos de la renovacion de la poliza " + idPoliza + " referencia:"
							+ refPoliza + " linea:" + codLinea.toString() + " plan:" + codPlan.toString()
							+ " pctComEnt:" + pctComEnt.toString() + " pctComEsMed:" + pctComEsMed.toString()
							+ " grupoNeg:" +grupoNeg + " idPolizaRenovable:" + idPolizaRenovable);
					
					polizaRen = getPolizaRenovableWS(refPoliza, codPlan.longValue(), codLinea.toString(),
							objListRenovWs);

					if (polizaRen != null && polizaRen.getCosteGrupoNegocioArray() != null
							&& polizaRen.getGastosArray() != null) {
						//logger.debug(">>> Recorremos los costes del grupo de negocio");
						for (int j = 0; j < polizaRen.getCosteGrupoNegocioArray().length; j++) {
							if (polizaRen.getCosteGrupoNegocioArray(j).getGrupoNegocio() != null && !polizaRen
									.getCosteGrupoNegocioArray(j).getGrupoNegocio().toString().equals("")) {
								renvCgGrupoNeg = polizaRen.getCosteGrupoNegocioArray(j).getGrupoNegocio();
							}
							
							if (grupoNeg.equals(renvCgGrupoNeg)) {
								if (polizaRen.getCosteGrupoNegocioArray(j).getPrimaComercialNeta() != null) {
									renvPrimaComercialNeta = polizaRen.getCosteGrupoNegocioArray(j).getPrimaComercialNeta();
								}
								break;
							} else {
								logger.debug(">>>## COSTE: Grupo de Negocio distinto grupoNeg=" + grupoNeg
										+ ", renvCgGrupoNeg=" + renvCgGrupoNeg);
							}
						}

						if (grupoNeg.equals(renvCgGrupoNeg)) {
							logger.debug(">>> Grupo de Negocio=" + renvCgGrupoNeg + ", Prima Comercial Neta="
									+ renvPrimaComercialNeta);
							if (primaComercialNeta.compareTo(renvPrimaComercialNeta) == 0) {
								logger.debug(">>>## Prima igual: primaComercialNeta=" + primaComercialNeta + ", renvPrimaComercialNeta=" + renvPrimaComercialNeta);
								polIgualPrima.add(refPoliza);
							} else {
								logger.debug(">>>## Prima distinta: primaComercialNeta=" + primaComercialNeta + ", renvPrimaComercialNeta=" + renvPrimaComercialNeta);
								primaComercialNeta = renvPrimaComercialNeta;
								polErrorPrima.add(refPoliza);
							}
							
							//logger.debug(">>> Recorremos los gastos para calcular la comision");
							for (int k = 0; k < polizaRen.getGastosAplicadosArray().length; k++) {
								if (polizaRen.getGastosAplicadosArray(k).getGrupoNegocio() != null && !polizaRen
										.getGastosAplicadosArray(k).getGrupoNegocio().toString().equals("")) {
									renvGaGrupoNeg = polizaRen.getGastosAplicadosArray(k).getGrupoNegocio();
								}

								if (grupoNeg.equals(renvGaGrupoNeg)) {
									if (polizaRen.getGastosAplicadosArray(k).getComisionMediador() != null) {
										renvPctCom = polizaRen.getGastosAplicadosArray(k).getComisionMediador();
									}
									if (polizaRen.getGastosAplicadosArray(k).getImporteComisionMediador() != null) {
										renvIimpCom = polizaRen.getGastosAplicadosArray(k).getImporteComisionMediador();
									}
									break;
								} else {
									logger.debug(">>>## GASTOS: Grupo de Negocio distinto grupoNeg=" + grupoNeg
											+ ", renvCgGrupoNeg=" + renvGaGrupoNeg + ", renvIimpCom=" + renvIimpCom);
								}
							}

							if (grupoNeg.equals(renvGaGrupoNeg)) {
								if (renvPctCom.compareTo(pctComEsMed)!=0) {
									logger.debug(">>> Porcentaje distintos renvPctCom=" + renvPctCom + " pctComEsMed=" + pctComEsMed);
									pctComEsMed = renvPctCom;
								}
								
								impComMed = renvPrimaComercialNeta.multiply(pctComEnt.divide(cien));
								impComES = renvPrimaComercialNeta.multiply(pctComEsMed.divide(cien));

								logger.debug(">>> Obtenemos las comisiones: impComMed=" + impComMed + " impComES="
										+ impComES);

								PopulatePolizaRenovable.updateComisionPolRenov(idPoliza, grupoNeg,
										renvPrimaComercialNeta, impComMed, impComES, session);
							} else {
								logger.debug(">>> Distinto grupo de negocio: grupoNeg=" + grupoNeg + ", renvGaGrupoNeg="
										+ renvGaGrupoNeg);
							}
						} else {
							logger.debug(">>> Distinto grupo de negocio: grupoNeg=" + grupoNeg + ", renvCgGrupoNeg="
									+ renvCgGrupoNeg);
						}

						logger.debug(">>> Asignamos el pago a la poliza");

					} else {
						logger.debug(">>> No devuelve Renovacion");
					}
				} catch (Exception e) {
					logger.error(">>> ERROR: polizasSinPagos, referencia=" + refPoliza, e);
					polError.add(refPoliza);
				}
			}
		} else {
			logger.debug(">>> No se han obtenido polizas sin gastos");
		}
		
		if (!polIgualPrima.isEmpty()) {
			logger.debug(">>> Polizas con primas iguales = " + polIgualPrima.size());
			for (String ref : polIgualPrima) {
				refPolIgualPrima.append(ref).append(",");
			}
			logger.debug(">>> Referencias: " + refPolIgualPrima.toString());
		}
		
		if (!polErrorPrima.isEmpty()) {
			logger.debug(">>> Polizas con primas erroneas = " + polErrorPrima.size());
			for (String ref : polErrorPrima) {
				refPolErrorPrima.append(ref).append(",");
			}
			logger.debug(">>> Referencias: " + refPolErrorPrima.toString());
		}
		
		if (!polError.isEmpty()) {
			logger.debug(">>> Polizas que ha devuelto ERROR AGROSEGURO = " + polError.size());
			for (String ref : polError) {
				refPolError.append(ref).append(",");
			}
			logger.debug(">>> Referencias: " + refPolError.toString());
		}
		logger.debug(">>> FIN polizasSinPagos");
	}
	

	protected static Renovacion getPolizaRenovableWS(String refPoliza, Long codPlan, String codLinea, final ContratacionRenovaciones objWs) throws Exception {
		Renovacion polREn = null;// es.agroseguro.estadoRenovacion.Renovacion;
		ListaPolizasRenovablesResponse wsResp = null;
		Base64Binary res = null;
		try {
			logger.debug(">>> Llamamos al servicio web, REFERENCIA:" + refPoliza + " PLAN:" + codPlan.toString() + " LINEA:" + codLinea);
			WSUtils.addSecurityHeader(objWs);
			
			ParametrosListaPolizasRenovablesAgroplus paramListPolReq = new ParametrosListaPolizasRenovablesAgroplus();
			JAXBElement<String> referencia = new ObjectFactory().createParametrosListaPolizasRenovablesReferencia(refPoliza);
			paramListPolReq.setReferencia(referencia);
			paramListPolReq.setPlan(codPlan.intValue());
			paramListPolReq.setLinea(Integer.parseInt(codLinea));

			//logger.debug(">>> Aniadimos los Parametros de entrada.......");
			wsResp = objWs.listaPolizasRenovables(paramListPolReq);
			//logger.debug(">>> Llamada al WS.......");
			res = wsResp.getPolizasRenovables();
			byte[] arrayAcuse = res.getValue();
			String acuse = new String(arrayAcuse, "UTF-8");
			ListaPolizasRenovablesDocument pp = ListaPolizasRenovablesDocument.Factory.parse(acuse);
			ListaPolizasRenovables ppRen = pp.getListaPolizasRenovables();
			Renovacion[] renArr = ppRen.getRenovacionArray();
			for (Renovacion renov : renArr) {
				polREn = renov;
			}
		} catch (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException ex) {
			for (es.agroseguro.serviciosweb.contratacionrenovaciones.Error error : ex.getFaultInfo().getError()) {
				logger.debug(">>> AgrException: " + error.getMensaje().toString());
			}
			throw new Exception(ex.getMessage().toString(), ex);
		} catch (Exception e) {
			logger.debug(">>> Exception: " + e.getMessage().toString());
			if (e instanceof javax.xml.ws.WebServiceException) {
				Throwable cause = e;
				if ((cause = cause.getCause()) != null) {
					if (cause instanceof ConnectException) {
						logger.debug(">>> ConnectException: " + e.getMessage().toString());
					} else {
						logger.debug(">>> Otro tipo de Exception: " + e.getMessage().toString());
					}
				} else {
					logger.debug(">>> Error exc. tipo: " + e.getMessage().toString());
				}
			} else {
				logger.debug(">>> Error exception tipo: " + e.getClass().toString());
			}
			throw new Exception(e.getMessage().toString(), e);
		}

		return polREn;
	}
}